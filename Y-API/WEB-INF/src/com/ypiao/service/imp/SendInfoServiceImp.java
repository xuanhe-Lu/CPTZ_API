package com.ypiao.service.imp;

import java.sql.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.commons.lang.LRUMap;
import org.commons.lang.RandomUtils;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.SendInfoService;
import com.ypiao.service.SenderService;
import com.ypiao.service.SysConfig;
import com.ypiao.util.Constant;
import com.ypiao.util.GMTime;
import com.ypiao.util.GState;
import com.ypiao.util.VeStr;

public class SendInfoServiceImp extends AConfig implements SendInfoService {

	private static final int ANUM_MAX = 5, BNUM_MAX = 5;

	// 发送频率
	private static final int INTERVAL = 50000; 

	private static final int TIMEOUT = 30 * 60 * 1000;

	private static final String TBL_SMS_INFO = "sms_info";

	public Map<String, SMSInfo> stmA = new LRUMap<String, SMSInfo>(12);

	private SysConfig sysConfig;

	private SenderService senderService;

	protected void checkSQL() {
	}

	public SysConfig getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig) {
		this.sysConfig = sysConfig;
	}

	public SenderService getSenderService() {
		return senderService;
	}

	public void setSenderService(SenderService senderService) {
		this.senderService = senderService;
	}
private static Logger logger =Logger.getLogger(SendInfoServiceImp.class);
	private SMSConfig findSMSConfig(String key, int tid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			SMSConfig c = null;
			ps = conn.prepareStatement("SELECT Tid,Name,Content,Sign,State,Time FROM sms_config WHERE Tid=?");
			ps.setInt(1, tid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				c = new SMSConfig();
				c.setTid(rs.getInt(1));
				c.setName(rs.getString(2));
				c.setContent(rs.getString(3));
				c.setSign(rs.getString(4));
				c.setState(rs.getInt(5));
				c.setTime(rs.getLong(6));
				this.setObject(key, c);
			}
			rs.close();
			return c;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private SMSFilter findFilterBySid(Connection conn, String sid) throws SQLException {
		PreparedStatement ps = JPrepare.prepareStatement(conn, "SELECT Sid,Num,State,Time FROM sms_filter WHERE Sid=?");
		try {
			SMSFilter f = null;
			ps.setString(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				f = new SMSFilter();
				f.setSid(rs.getString(1));
				f.setNum(rs.getInt(2) + 1);
				f.setState(rs.getInt(3));
				f.setTime(rs.getLong(4));
				rs.updateInt(1, f.getNum());
				rs.updateRow();
			}
			rs.close();
			return f;
		} finally {
			ps.close();
		}
	}

	private SMSInfo findInfoBySid(Connection conn, String sid) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT Sid,Tid,Cid,ANum,BNum,CNum,Nums,Code,Sday,State,Time FROM " + TBL_SMS_INFO + " WHERE Sid=?");
		try {
			SMSInfo s = null;
			ps.setString(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				s = new SMSInfo();
				s.setSid(rs.getString(1));
				s.setTid(rs.getInt(2));
				s.setCid(rs.getInt(3));
				s.setAnum(rs.getInt(4));
				s.setBnum(rs.getInt(5));
				s.setCnum(rs.getInt(6));
				s.setNums(rs.getInt(7));
				s.setCode(rs.getString(8));
				s.setSday(rs.getLong(9));
				s.setState(rs.getInt(10));
				s.setTime(rs.getLong(11));
				stmA.put(sid, s);
			}
			rs.close();
			return s;
		} finally {
			ps.close();
		}
	}

	private String getCode() {
		return /*String.valueOf(RandomUtils.randomNumeric(1000, 9999))*/ String.valueOf(123456);
	}

	private SMSInfo getInfoBySid(String sid) throws SQLException {
		SMSInfo info = stmA.get(sid);
		if (info == null) {
			Connection conn = JPrepare.getConnection();
			try {
				this.findFilterBySid(conn, sid);
			} finally {
				JPrepare.close(conn);
			}
		}
		return info;
	}

	public SMSConfig getSMSConfig(int tid) throws SQLException {
		String key = "SMS_Config=" + tid;
		Object obj = this.getObject(key);
		if (obj == null) {
			return this.findSMSConfig(key, tid);
		} else {
			return (SMSConfig) obj;
		}
	}

	public int detect(String addr, String sm) throws SQLException {
		Matcher m = Pattern.compile("(\\d)\\1{6}").matcher(sm);
		if (m.find()) {
			return STA;
		} // 检测同一来源是否频繁请求
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			SMSFilter f = this.findFilterBySid(conn, sm);
			if (f == null) {
				// Ignored
			} else {
				return (STATE_ENABLE == f.getState()) ? STB : STC;
			} // 检测发送信息
			SMSInfo s = this.findInfoBySid(conn, sm);
			if (s == null) {
				// Ignored
			} else if (GState.USER_TODAX > s.getSday()) {
				// 全新一天，重新统计
			} else {
				long out = (System.currentTimeMillis() - s.getTime());
				if (INTERVAL >= out) {
					return STD;
				} else if (ANUM_MAX > s.getAnum()) {
					// A 通道有剩余
				} else if (BNUM_MAX > s.getBnum()) {
					// B 通道有剩余
				} else {
					return STE;
				}
			}
			return SOK;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isCode(String sm, String code) throws SQLException {
		logger.info(Constant.USE_DEBUG + "==" + sm + "====" + code);
		/*if (sm == null || code == null) {
			return false;
		} else if (Constant.USE_DEBUG) {
			return code.equals("2018");
		} // 查询验证码
		Connection conn = JPrepare.getConnection();
		try {
			SMSInfo s = this.findInfoBySid(conn, sm);
			if (s == null) {
				return false;
			} else if (code.equals(s.getCode())) {
				if (STATE_NORMAL == s.getState()) {
					long out = System.currentTimeMillis() - s.getTime();
					return (TIMEOUT >= out);
				}
			}
			return false;
		} finally {
			JPrepare.close(conn);
		}*/
		return true;
	}

	public boolean isUser(String sm) {
		return JPrepare.isExists("SELECT Uid FROM user_info WHERE Account=?", sm);
	}

	public boolean sendCode(String addr, String fix, String sm, int tid) throws SQLException {
		/*SMSConfig sc = this.getSMSConfig(tid);
		if (sc == null) {
			return false;
		} else if (STATE_ENABLE != sc.getState()) {
			return true;
		} // 检测发送条件
		SMSInfo s = this.getInfoBySid(sm);
		long time = System.currentTimeMillis();
		if (s == null) {
			s = new SMSInfo();
			s.setSid(sm); // 主键信息
			s.setSday(time);
			s.setCode(getCode());
			s.setCid(1); // A通道
			s.setAnum(1);
			s.setBnum(0);
			s.setNums(1);
			s.setTime(time);
		} else if (INTERVAL > (time - s.getTime())) {
			return true; // 频率太快
		} else {
			int cid = s.getCid();
			if (STATE_NORMAL == s.getState()) {
				cid = (s.getCid() + 1) % 2;
			} // 检测优先数组
			if (GState.USER_TODAX > s.getSday()) {
				if (cid == 0) {
					s.addA(0, 1);
					s.setBnum(0);
				} else {
					s.setAnum(0);
					s.addB(1, 1);
				}
				s.setSday(time);
			} else if (ANUM_MAX > s.getAnum()) {
				if (cid == 0 || BNUM_MAX <= s.getBnum()) {
					s.addA(0, 1);
				} else {
					s.addB(1, 1);
				}
			} else if (BNUM_MAX > s.getBnum()) {
				s.addB(1, 1);
			} else {
				return true;
			} // 重新生成验证码
			long out = time - (TIMEOUT >> 1);
			if (out >= s.getTime()) {
				s.setCode(getCode());
				s.setTime(time);
			} // 汇总+1
			s.setNums(s.getNums() + 1);
		}
		s.setTid(tid);
		s.setCnum(s.getAnum() + s.getBnum());
		s.setState(STATE_NORMAL);
		SMSLogs log = new SMSLogs();
		log.setSid(VeStr.getUSid());
		log.setCid(s.getCid());
		log.setMobile(sm);
		log.setContent(sc.getContent().replace("${code}", s.getCode()));
		log.setState(STATE_NORMAL);
		log.setTime(time);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_SMS_INFO + " SET Tid=?,Cid=?,ANum=?,BNum=?,CNum=?,Nums=?,Code=?,Sday=?,State=?,Time=? WHERE Sid=?");
			ps.setInt(1, s.getTid());
			ps.setInt(2, s.getCid());
			ps.setInt(3, s.getAnum());
			ps.setInt(4, s.getBnum());
			ps.setInt(5, s.getCnum());
			ps.setInt(6, s.getNums());
			ps.setString(7, s.getCode());
			ps.setLong(8, s.getSday());
			ps.setInt(9, s.getState());
			ps.setLong(10, s.getTime());
			ps.setString(11, s.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_SMS_INFO + " (Sid,Tid,Cid,ANum,BNum,CNum,Nums,Code,Sday,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, s.getSid());
				ps.setInt(2, s.getTid());
				ps.setInt(3, s.getCid());
				ps.setInt(4, s.getAnum());
				ps.setInt(5, s.getBnum());
				ps.setInt(6, s.getCnum());
				ps.setInt(7, s.getNums());
				ps.setString(8, s.getCode());
				ps.setLong(9, s.getSday());
				ps.setInt(10, s.getState());
				ps.setLong(11, s.getTime());
				ps.executeUpdate();
			} // 发送记录
			ps.close();
			ps = conn.prepareStatement("INSERT INTO sms_logs (Sid,Cid,Mobile,Content,State,Time) VALUES (?,?,?,?,?,?)");
			ps.setLong(1, log.getSid());
			ps.setInt(2, log.getCid());
			ps.setString(3, log.getMobile());
			ps.setString(4, log.getContent());
			ps.setInt(5, log.getState());
			ps.setLong(6, log.getTime());
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		} // 发送验证码
		if (this.getSysConfig().sendSMS()) {
			return truethis.getSenderService().sendByCode(log);
		} // 直接返回成功*/
		return true;
	}
}
