package com.ypm.service.imp;

import java.awt.Toolkit;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;

import com.ypm.bean.AjaxInfo;
import com.ypm.bean.ExportInfo;
import com.ypm.bean.SyncMap;
import com.ypm.bean.UserCash;
import com.ypm.bean.UserRmbs;
import com.ypm.bean.UserSession;
import com.ypm.data.JPrepare;
import com.ypm.office.ExcelSheet;
import com.ypm.office.ExcelWriter;
import com.ypm.service.UserCashService;
import com.ypm.service.UserInfoService;
import com.ypm.service.UserMoneyService;
import com.ypm.util.APState;
import com.ypm.util.GMTime;
import com.ypm.util.GState;
import com.ypm.util.MD5Util;
import com.ypm.util.VeRule;

public class UserCashServiceImp extends AConfig implements UserCashService {
	private static final String ENCODEING="UTF-8";
	private static final String TBL_USER_CASH = "user_cash";
	private static final String TBL_COMM_BANK = "comm_bank";
	/** 富友返回状态*/
	public static int STATE_MAX = 5;
	private UserInfoService userInfoService;
	private UserMoneyService userMoneyService;
	protected void checkSQL() {
	}
	public UserInfoService getUserInfoService() {
		return userInfoService;
	}
	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}
	public UserMoneyService getUserMoneyService() {
		return userMoneyService;
	}
	public void setUserMoneyService(UserMoneyService userMoneyService) {
		this.userMoneyService = userMoneyService;
	}
	private int insert(Connection conn, UserCash c) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("INSERT INTO " + TBL_USER_CASH + " (Sid,Uid,Name,Mobile,BkId,BkInfo,BkName,TMA,TMB,TMC,RCV,GmtA,GmtB,State,Time,adM,lsDh,adN) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		try {
			ps.setLong(1, c.getSid());
			ps.setLong(2, c.getUid());
			ps.setString(3, c.getName());
			ps.setString(4, c.getMobile());
			ps.setInt(5, c.getBkId());
			ps.setString(6, c.getBkInfo());
			ps.setString(7, c.getBkName());
			ps.setBigDecimal(8, c.getTma());
			ps.setBigDecimal(9, c.getTmb());
			ps.setBigDecimal(10, c.getTmc());
			ps.setInt(11, c.getRcv());
			ps.setLong(12, c.getGmtA());
			ps.setLong(13, c.getGmtB());
			ps.setInt(14, c.getState());
			ps.setLong(15, c.getTime());
			ps.setLong(16, c.getAdm());
			ps.setLong(17, GMTime.currentTimeMillis());
//			ps.setLong(17,c.getLsDh());
			ps.setString(18, c.getAdn());
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	private int update(Connection conn, UserCash c) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_CASH + " SET Uid=?,Name=?,Mobile=?,RCV=?,GmtB=?,State=?,Time=?,adM=?,adN=? WHERE Sid=?");
		try {
			ps.setLong(1, c.getUid());
			ps.setString(2, c.getName());
			ps.setString(3, c.getMobile());
			ps.setInt(4, c.getRcv());
			ps.setLong(5, c.getGmtB());
			ps.setInt(6, c.getState());
			ps.setLong(7, c.getTime());
			ps.setLong(8, c.getAdm());
			ps.setString(9, c.getAdn());
			ps.setLong(10, c.getSid());
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	public void save(UserCash c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			if (this.update(conn, c) >= 1) {
				// Ignored
			} else if (this.insert(conn, c) >= 1) {
				this.getUserInfoService().updateSubTX(conn, c.getUid(), c.getTma(), c.getTime());
			}
		} finally {
			JPrepare.close(conn);
		}
	}
	public void update(UserCash c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.update(conn, c);
		} finally {
			JPrepare.close(conn);
		}
	}
	public void update(List<UserCash> ls) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_USER_CASH + " SET Uid=?,Name=?,Mobile=?,RCV=?,GmtB=?,State=?,Time=?,adM=?,adN=? WHERE Sid=?");
			for (UserCash c : ls) {
				ps.setLong(1, c.getUid());
				ps.setString(2, c.getName());
				ps.setString(3, c.getMobile());
				ps.setInt(4, c.getRcv());
				ps.setLong(5, c.getGmtB());
				ps.setInt(6, c.getState());
				ps.setLong(7, c.getTime());
				ps.setLong(8, c.getAdm());
				ps.setString(9, c.getAdn());
				ps.setLong(10, c.getSid());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	public AjaxInfo findCashByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_USER_CASH).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_USER_CASH, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			}
			float f = 0f;
			long adm = 0, rcv = 0; // 管理UID/RCV
			Map<String, String> ms = this.getDictInfoBySSid(CFO_CASH_STATE);
			sql.insert(0, "SELECT Sid,Uid,Name,Mobile,BkId,BkName,BkInfo,TMA,TMB,TMC,RCV,GmtA,GmtB,State,Time,adM,adN ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql.toString(), offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("SID", rs.getLong(1));
				json.append("UID", rs.getLong(2));
				json.append("NAME", rs.getString(3));
				json.append("MOBILE", rs.getString(4));
				json.append("BKID", rs.getInt(5));
				json.append("BKNAME", rs.getString(6));
				json.append("BKINFO", rs.getString(7));
				json.append("TMA", DF3.format(rs.getFloat(8)));
				if ((f = rs.getFloat(9)) > 0) {
					json.append("TMB", DF3.format(f));
				} else {
					json.appends("TMB", "-");
				}
				json.append("TMC", DF3.format(rs.getFloat(10)));
				if ((rcv = rs.getInt(11)) >= 1) {
					json.append("RCV", rcv);
				} else {
					json.append("RCV", "-");
				}
				json.append("GMTA", GMTime.formater(rs.getLong(12), GMTime.CHINA));
				json.append("GMTB", GMTime.formater(rs.getLong(13), GMTime.CHINA));
				json.append("STATE", ms.get(rs.getString(14)));
				json.append("TIME", GMTime.format(rs.getLong(15), GMTime.CHINA));
				if ((adm = rs.getLong(16)) >= USER_IDS) {
					json.append("ADM", adm);
					json.append("ADN", rs.getString(17));
				} else {
					json.append("ADM", "");
					json.appends("ADN", "-");
				}
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}
	public AjaxInfo loadCashBySum(AjaxInfo json, StringBuilder sql, List<Object> fs) throws SQLException {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			sql.insert(0, "SELECT SUM(TMA),SUM(TMB),SUM(TMC) FROM " + TBL_USER_CASH);
			ps = conn.prepareStatement(sql.toString());
			for (Object obj : fs) {
				ps.setObject(index++, obj);
			}
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				json.append("TMA", DF3.format(rs.getDouble(1)), "元");
				json.append("TMB", DF3.format(rs.getDouble(2)), "元");
				json.append("TMC", DF3.format(rs.getDouble(3)), "元");
			} else {
				json.append("TMA", "-");
				json.append("TMB", "-");
				json.append("TMC", "-");
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	public UserCash findCashBySid(long sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			UserCash c = null;
			ps = conn.prepareStatement("SELECT Sid,Uid,Name,Mobile,BkId,BkName,BkInfo,TMA,TMB,TMC,RCV,GmtA,GmtB,State,Time,adM,adN FROM " + TBL_USER_CASH + " WHERE Sid=?");
			ps.setLong(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				c = new UserCash();
				c.setSid(rs.getLong(1));
				c.setUid(rs.getLong(2));
				c.setName(rs.getString(3));
				c.setMobile(rs.getString(4));
				c.setBkId(rs.getInt(5));
				c.setBkName(rs.getString(6));
				c.setBkInfo(rs.getString(7));
				c.setTma(rs.getBigDecimal(8));
				c.setTmb(rs.getBigDecimal(9));
				c.setTmc(rs.getBigDecimal(10));
				c.setRcv(rs.getInt(11));
				c.setGmtA(rs.getLong(12));
				c.setGmtB(rs.getLong(13));
				c.setState(rs.getInt(14));
				c.setTime(rs.getLong(15));
				c.setAdm(rs.getLong(16));
				c.setAdn(rs.getString(17));
			}
			rs.close();
			return c;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	//用户提现
	public synchronized void agree(UserSession us, String ids) throws SQLException {
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int rcv = 0; // 编号获取
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("SELECT MAX(RCV) FROM " + TBL_USER_CASH + " WHERE State>=?");
			ps.setInt(1, STATE_CHECK);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rcv = rs.getInt(1);
			}
			rs.close();
			long time = GMTime.currentTimeMillis(); // 当前时间
			List<UserCash> fs = new ArrayList<UserCash>(set.size());
			for (Long sid : set) {
				ps.close();
				ps = JPrepare.prepareStatement(conn, "SELECT Sid,Uid,Name,Mobile,BkId,BkName,BkInfo,TMA,TMB,TMC,RCV,GmtA,GmtB,State,Time,adM,lsDh,adN FROM " + TBL_USER_CASH + " WHERE Sid=?");
				ps.setLong(1, sid.longValue());
				rs = ps.executeQuery();
				if (rs.next()) {
					int state = rs.getInt(14);
					if (state >= 2) {
						if (state == 6) {
							UserCash c = new UserCash();
							c.setSid(rs.getLong(1));
							c.setUid(rs.getLong(2));
							c.setName(rs.getString(3));
							c.setMobile(rs.getString(4));
							c.setBkId(rs.getInt(5));
							c.setBkName(rs.getString(6));
							c.setBkInfo(rs.getString(7));
							c.setTma(rs.getBigDecimal(8));
							c.setTmb(rs.getBigDecimal(9));
							c.setTmc(rs.getBigDecimal(10));
							c.setRcv(1);
							c.setGmtA(rs.getLong(12));
							c.setGmtB(time);
							c.setAdm(us.getUserId());
							c.setLsDh(rs.getLong(17));
							c.setAdn(us.getUserName());
							payForReq(c);//调用富友系统付款的接口
							c.setState(5);//发起提现后，状态需要更变，   默认5   发送中
							c.setTime(time);
							rs.updateInt(11, c.getRcv());
							rs.updateLong(13, c.getGmtB());
							rs.updateInt(14, c.getState());
							rs.updateLong(15, time);
							rs.updateLong(16, c.getAdm());
							rs.updateLong(17, c.getLsDh());
							rs.updateString(18, c.getAdn());
							rs.updateRow();
							fs.add(c);
						}
					} else {
						UserCash c = new UserCash();
						c.setSid(rs.getLong(1));
						c.setUid(rs.getLong(2));
						c.setName(rs.getString(3));
						c.setMobile(rs.getString(4));
						c.setBkId(rs.getInt(5));
						c.setBkName(rs.getString(6));
						c.setBkInfo(rs.getString(7));
						c.setTma(rs.getBigDecimal(8));
						c.setTmb(rs.getBigDecimal(9));
						c.setTmc(rs.getBigDecimal(10));
						c.setRcv(1);
						c.setGmtA(rs.getLong(12));
						c.setGmtB(time);
						c.setAdm(us.getUserId());
						c.setLsDh(rs.getLong(17));
						c.setAdn(us.getUserName());
						payForReq(c);//调用富友系统付款的接口
						c.setState(STATE_MAX);//发起提现后，状态需要更变，   默认5   发送中   6  异常
						c.setTime(time);
						rs.updateInt(11, c.getRcv());
						rs.updateLong(13, c.getGmtB());
						rs.updateInt(14, c.getState());
						rs.updateLong(15, time);
						rs.updateLong(16, c.getAdm());
						rs.updateLong(17, c.getLsDh());
						rs.updateString(18, c.getAdn());
						rs.updateRow();
						fs.add(c);
					}
				}
				rs.close();
			}
			conn.commit();
			if (fs.size() >= 1) {
				SyncMap.getAll().sender(SYS_A880, "agree", fs);
			}
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	Timer timer;
	Toolkit toolkit = null;
	//项目启动后定时去执行
	public UserCashServiceImp() {
		toolkit = Toolkit.getDefaultToolkit();
        timer = new Timer();
//        timer.schedule(new RemindTask(), seconds*1000);
        timer.schedule(new RemindTask(),
                0,        //initial delay
                600*1000);  //subsequent rate
    }
	class RemindTask extends TimerTask {
		private static final int SYS_A880 = 880;
		int numWarningBeeps = 3;
		long time = GMTime.currentTimeMillis(); // 当前时间
        public void run() {
//            System.out.println("Time's up!");
//			toolkit.beep();
////            timer.cancel(); //Terminate the timer thread
//            System.exit(0);
			if (numWarningBeeps > 0) {
				toolkit.beep();
				System.out.println("开始执行!");
				Connection conn = null;
				PreparedStatement ps = null;
				//              numWarningBeeps--;
				try {
					conn = JPrepare.getConnection();
					conn.setAutoCommit(false);
					ps = conn.prepareStatement("SELECT u.Sid,u.Uid,u.`Name`,u.Mobile,u.BkId,u.BkName,u.BkInfo,u.TMA,u.TMB,u.TMC,u.RCV,u.GmtA,u.GmtB,u.State,u.Time,u.adM,u.adN,u.lsDh FROM user_cash u WHERE u.State = 5 AND date_sub(now(), interval 30 MINUTE)");
					ResultSet rs = ps.executeQuery();
//					String ids = "1807091007522014";
					Set<Long> set = new HashSet<>();
					while(rs.next()) {
						set.add(rs.getLong(1));
					}
					Iterator<Long> st = set.iterator();
					while(st.hasNext()) {
						System.out.println(st.next().longValue());
					}
					rs.close();
					long time = GMTime.currentTimeMillis(); // 当前时间
					List<UserCash> fs = new ArrayList<UserCash>(set.size());
					for (Long sid : set) {
						ps.close();
						ps = JPrepare.prepareStatement(conn, "SELECT Sid,Uid,Name,Mobile,BkId,BkName,BkInfo,TMA,TMB,TMC,RCV,GmtA,GmtB,State,Time,adM,lsDh,adN FROM " + TBL_USER_CASH + " WHERE Sid=?");
						ps.setLong(1, sid.longValue());
						rs = ps.executeQuery();
						if (rs.next()) {
							int state = rs.getInt(14);
								// Ignored
								if (state == 5) {
									UserCash c = new UserCash();
									c.setSid(rs.getLong(1));
									c.setUid(rs.getLong(2));
									c.setName(rs.getString(3));
									c.setMobile(rs.getString(4));
									c.setBkId(rs.getInt(5));
									c.setBkName(rs.getString(6));
									c.setBkInfo(rs.getString(7));
									c.setTma(rs.getBigDecimal(8));
									c.setTmb(rs.getBigDecimal(9));
									c.setTmc(rs.getBigDecimal(10));
									c.setRcv(2);//VCR 字段去掉
									c.setGmtA(rs.getLong(12));
									c.setGmtB(time);
									c.setAdm(rs.getLong(16));
									c.setLsDh(rs.getLong(17));
									c.setAdn(rs.getString(18));
									qryTrans(c);//调用富友系统查询的接口
									c.setState(4);//发起提现后，状态需要更变，   默认5   发送中
									c.setTime(time);
									rs.updateInt(11, c.getRcv());
									rs.updateLong(13, c.getGmtB());
									rs.updateInt(14, c.getState());
									rs.updateLong(15, time);
									rs.updateLong(16, c.getAdm());
									rs.updateLong(17, c.getLsDh());
									rs.updateString(18, c.getAdn());
									rs.updateRow();
									fs.add(c);
								}
						}
						rs.close();
					}
					conn.commit();
					if (fs.size() >= 1) {
						SyncMap.getAll().sender(SYS_A880, "agree", fs);
					}
				} catch (SQLException e) {
					try {
						conn.rollback();
						throw e;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} finally {
					JPrepare.close(ps, conn);
				}
			} else {
				toolkit.beep(); 
				System.out.println("Time's up!");
				//timer.cancel(); //Not necessary because we call System.exit
				System.exit(0);   //Stops the AWT thread (and everything else)
			}
       }
	}
	public static void main(String[] args){
//		String jieguo = str.substring(str.indexOf("")+3,str.indexOf(""));
//		System.out.println(jieguo);
    }
	public static void requestPost(String url,List<NameValuePair> params,UserCash c) throws ClientProtocolException, IOException {
	    CloseableHttpClient httpclient = HttpClientBuilder.create().build();
	    HttpPost httppost = new HttpPost(url);
	    httppost.setEntity(new UrlEncodedFormEntity(params,ENCODEING));
	    CloseableHttpResponse response = httpclient.execute(httppost);
	    System.out.println(response.toString());
	    HttpEntity entity = response.getEntity();
	    String jsonStr = EntityUtils.toString(entity, "utf-8");
	    System.out.println(jsonStr);
	    String jieguo = jsonStr.substring(jsonStr.indexOf("<memo>")+6,jsonStr.indexOf("</memo>"));
//	    System.out.println(jieguo);
	    if (jieguo.equals("总行代码小于4位") || jieguo.equals("城市代码不等于4位")) {
	    	System.out.println("交易处理中...");
	    	STATE_MAX = 6;//交易已发送且失败     交易处理失败
		}else if(jieguo.equals("成功")){
			STATE_MAX = 5;//交易发送中    交易处理中
		}else{
			STATE_MAX = 6;//交易已发送且失败     交易处理失败
		}
	    System.out.println("状态："+STATE_MAX);
	    httppost.releaseConnection();
	}
	public static String getDate(){
		SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
		Date date=new Date();
		String date1=sf.format(date);
		return date1;		
	}
	//付款  - 富友接口需要的参数
	public static void payForReq(UserCash uc) {
		Connection conn;
		PreparedStatement ps = null;
		
		try {
			conn = JPrepare.getConnection();
			if (uc == null) {
				uc = new UserCash();
			}else {
				ps = JPrepare.prepareStatement(conn, "SELECT BankId,name FROM " + TBL_COMM_BANK + " WHERE BId = ?");
				ps.setInt(1, uc.getBkId());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					uc.setBkId(rs.getInt(1));
					uc.setBkName(rs.getString(2));
					if (uc.getBkId() == 0) {
						if (uc.getBkName().equals("中国建设银行") || uc.getBkName().equals("建设银行")) {
							uc.setBkId(104);
						}else if (uc.getBkName().equals("农业银行") || uc.getBkName().equals("中国农业银行")) {
							uc.setBkId(102);
						}else if (uc.getBkName().equals("邮储银行") || uc.getBkName().equals("邮政储蓄银行")) {
							uc.setBkId(106);
						}
					}
				}
				rs.close();
				BigDecimal bdTma = new BigDecimal(100);
				BigDecimal tmc = uc.getTmc().multiply(bdTma);
//				BigDecimal tma = uc.getTma().multiply(bdTma);
				String mobile = uc.getMobile().replace("+86-", "");
				String bkname = uc.getBkName();//银行名称
				int cityno = 1000;
				//以下15家银行，对私，城市代码可以写默认 默认值：北京市代码：1000 ，支行可以不填 ！
				if (bkname.equals("中国工商银行") || bkname.equals("中国银行") || bkname.equals("中国农业银行") || bkname.equals("中国建设银行") 
						|| bkname.equals("交通银行") || bkname.equals("中国邮政储蓄银行") || bkname.equals("招商银行") || bkname.equals("平安银行") 
						|| bkname.equals("中信银行") || bkname.equals("光大银行") || bkname.equals("兴业银行") || bkname.equals("民生银行") 
						|| bkname.equals("广发银行") || bkname.equals("华夏银行") || bkname.equals("上海浦东发展银行")) {
					cityno = 1000;
				}else {
					//对公和其他银行（非以上15家大行）均需要城市代码和支行，且支行需要传准确。
					//对公
					cityno = 1000;
				}
				System.out.println("流水单号："+uc.getLsDh());
				String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>"+
		    			"<payforreq>"+
		    			"<ver>1.00</ver>"+
		    			"<merdt>"+UserCashServiceImp.getDate()+"</merdt>"+
		    			"<orderno>"+uc.getLsDh()+"</orderno>"+
//		    			"<bankno>0308</bankno>"+//总行代码  
		    			"<bankno>"+"0"+uc.getBkId()+"</bankno>"+
		    			"<cityno>"+cityno+"</cityno>"+//城市代码
		    			//"<branchnm>中国银行股份有限公司北京西单支行</branchnm>"+可不填，如果填写，一定要填对，否则影响交易；但对公户、城商行、农商行、信用社必须填支行，且需正确的支行信息
		    			"<accntno>"+uc.getBkInfo()+"</accntno>"+//默认测试账号
		    			"<accntnm>"+uc.getName()+"</accntnm>"+
//		    			"<amt>"+uc.getTma().multiply(bdTma)+"</amt>"+//单笔最少三元  单位分
						"<amt>"+tmc.longValue()+"</amt>"+
		    			"<mobile>"+mobile+"</mobile>"+
		    			"<addDesc>1</addDesc>"+
		    			"</payforreq>";
		    	String macSource = "0003310F1078099|a2djik32kyx9v7azi8pzhotksvcsmw4v|"+"payforreq"+"|"+xml;
		    	System.out.println("xml="+xml);
		    	String mac = MD5Util.encode(macSource, "UTF-8").toUpperCase();
		    	String loginUrl = "https://fht.fuiou.com/req.do";//生产环境
		        List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(new BasicNameValuePair("merid", "0003310F1078099"));
		        params.add(new BasicNameValuePair("reqtype", "payforreq"));
		        params.add(new BasicNameValuePair("xml", xml));
		        params.add(new BasicNameValuePair("mac", mac));
		        requestPost(loginUrl,params,uc);
			}
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	}
	
	//查询  - 富友接口
	public static void qryTrans(UserCash uc) {
		try {
			System.out.println("流水单号："+uc.getLsDh());
			String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>"+
	    			"<qrytransreq>"+
	    			"<ver>1.1</ver>"+
	    			"<busicd>AP01</busicd>"+    //AP01:代付  AC01：代收  TP01：退票
	    			"<orderno>"+uc.getLsDh()+"</orderno>"+      //查询多个流水，流水中间用英文,间隔，一次最多50个
//	    			"<orderno>1531213102537</orderno>" +
	    			"<startdt>"+UserCashServiceImp.getDate(2)+"</startdt>"+  
	    			"<enddt>"+UserCashServiceImp.getDate(1)+"</enddt>"+
//	    			"<transst>1</transst>"+
	    			"</qrytransreq>";
//	    	String macSource = "0002900F0345178|123456|"+"qrytransreq"+"|"+xml;//测试商户号
			String macSource = "0003310F1078099|a2djik32kyx9v7azi8pzhotksvcsmw4v|"+"qrytransreq"+"|"+xml;
			System.out.println(xml);
	    	String mac = MD5Util.encode(macSource, "UTF-8").toUpperCase();
//	        String loginUrl = "https://fht-test.fuiou.com/fuMer/req.do";//测试地址
	    	String loginUrl = "https://fht.fuiou.com/req.do";//生产环境
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("merid", "0003310F1078099"));
//	        params.add(new BasicNameValuePair("merid", "0002900F0345178"));
	        params.add(new BasicNameValuePair("reqtype", "qrytransreq"));
	        params.add(new BasicNameValuePair("xml", xml));
	        params.add(new BasicNameValuePair("mac", mac));
	        requestPosts(loginUrl,params);
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public static void requestPosts(String url,List<NameValuePair> params) throws ClientProtocolException, IOException {
	    CloseableHttpClient httpclient = HttpClientBuilder.create().build();
	    HttpPost httppost = new HttpPost(url);
	    httppost.setEntity(new UrlEncodedFormEntity(params,ENCODEING));
	    CloseableHttpResponse response = httpclient.execute(httppost);
	    System.out.println("查询："+response.toString());
	    HttpEntity entity = response.getEntity();
	    String jsonStr = EntityUtils.toString(entity, "utf-8");
	    System.out.println("jsonStr="+jsonStr);
	    String js = jsonStr.substring(jsonStr.indexOf("<memo>")+6,jsonStr.indexOf("</memo>"));
	    if (js.equals("未查询到符合条件的信息")) {
	    	System.out.println("交易处理中...");
		}else if(js.equals("请求过于频繁,稍后再试")){
			System.out.println("请求过于频繁,稍后再试...");
		}else{
			String jieguo = jsonStr.substring(jsonStr.indexOf("<result>")+8,jsonStr.indexOf("</result>"));
	    	System.out.println("jiegou"+jieguo);
	    	if (jieguo.equals("") || jieguo == null) {
	    		STATE_MAX = 6;//交易已发送且失败     交易处理失败
	    	}else {
	    		if (jieguo.equals("渠道资金到账已复核,交易已发送")) {
	    			STATE_MAX = 4;//交易已发送      已完成
	    		}else if(jieguo.equals("渠道资金到账已复核,交易未发送") || jieguo.equals("渠道资金到账已复核,交易发送中") ) {
	    			STATE_MAX = 5;//交易发送中    交易处理中
	    		}else {
	    			STATE_MAX = 6;//交易已发送且失败     交易处理失败
	    		}
	    	}
		}
	    System.out.println("状态："+STATE_MAX);
//	    String str = toJsonStr(jsonStr);
//	    System.out.println("str:"+str);
	    System.out.println("查询："+jsonStr);
	    httppost.releaseConnection();
	}
	public static String getDate(int i){
		SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
		 Calendar calendar = Calendar.getInstance(); 
		 calendar.setTime(new Date());
		 if(i==1){
			 String str = sf.format(calendar.getTime());
			 System.out.println("str1="+str);
			 return str;
		 }else{
			 calendar.add(Calendar.DAY_OF_YEAR, -1);
			 String str = sf.format(calendar.getTime());
			 System.out.println("str2="+str);
			 return str;	 
		 }	
	}
	public static String toJsonStr(String jsonStr) {
		String jieguo = jsonStr.substring(jsonStr.indexOf("<result>")+9,jsonStr.indexOf("</result>"));
		System.out.println("jieguo="+jieguo);
		if (jieguo.equals("") || jieguo == null) {
			STATE_MAX = 6;//交易已发送且失败
		}else {
			if (jieguo.equals("渠道资金到账已复核,交易已发送")) {
				STATE_MAX = 5;//交易发送中
			}else {
				STATE_MAX = 4;//交易已发送且成功
			}
		}
		return jieguo;
	}

	public void refuse(UserSession us, UserCash c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long time = GMTime.currentTimeMillis();
			c.setState(STATE_ERRORS);
			c.setGmtB(time);
			c.setTime(time);
			c.setAdm(us.getUserId());
			c.setAdn(us.getUserName());
			ps = conn.prepareStatement("UPDATE " + TBL_USER_CASH + " SET GmtB=?,State=?,Time=?,adM=?,adN=? WHERE Sid=? AND State<=?");
			ps.setLong(1, c.getGmtB());
			ps.setInt(2, c.getState());
			ps.setLong(3, c.getTime());
			ps.setLong(4, c.getAdm());
			ps.setString(5, c.getAdn());
			ps.setLong(6, c.getSid());
			ps.setInt(7, STATE_CHECK);
			if (ps.executeUpdate() >= 1) {
				UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, c.getUid());
				r.setSid(c.getSid() + 100);
				r.setTid(APState.TRADE_ADD3);
				r.setWay("系统退回");
				r.setEvent("提现退回");
				r.add(c.getTma()); // 提现总额
				r.setTime(time);
				this.getUserMoneyService().save(r);
				SyncMap.getAll().sender(SYS_A880, "refuse", c);
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean exportByOut(ExportInfo info) throws SQLException, IOException {
		info.setSid("out".hashCode());
		if (!info.isExpired()) {
			return true;
		} // 构建新数据
		ExcelWriter e = info.createExcel();
		ExcelSheet s = e.getSheet(0);
		CellStyle c = s.getStyleByC();
		e.setTitle(GState.TODAY +"提现");
		s.setSheetName(GState.TODAY);
		s.addHeader(c, "业务参考号", 1, 144);
		s.addHeader(c, "协议号", 1, 90);
		s.addHeader(c, "付款账号", 1, 135);
		s.addHeader(c, "付款账号银行号", 1, 118);
		s.addHeader(c, "收方编号", 1, 90);
		s.addHeader(c, "收款人帐号", 1, 170);
		s.addHeader(c, "收款人户名", 1, 85);
		s.addHeader(c, "收款行行号", 1, 108);
		s.addHeader(c, "金额", 1, 115);
		s.addHeader(c, "币种", 1, 70);
		s.addHeader(c, "业务类型", 1, 70);
		s.addHeader(c, "业务种类", 1, 96);
		s.addHeader(c, "收方电子邮件", 1, 102);
		s.addHeader(c, "收方移动电话", 1, 102);
		s.addHeader(c, "附言", 1, 150);
		DataFormat df = s.getDataFormat("money");
		CellStyle left = s.getCellStyle("left");
		CellStyle money = s.getCellStyle("money");
		money.setDataFormat(df.getFormat("#,##0.00"));
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int rcv = 0;
			StringBuilder sb = new StringBuilder();
			ps = conn.prepareStatement("SELECT Sid,Uid,Name,Mobile,BkId,BkName,BkInfo,TMA,TMB,TMC,RCV,GmtA,GmtB,State,Time FROM " + TBL_USER_CASH + " WHERE State=? ORDER BY RCV ASC");
			ps.setInt(1, STATE_CHECK);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				s.addNext();
				rcv = rs.getInt(11);
				sb.append("RCV");
				if (rcv < 10) {
					sb.append("000000");
				} else if (rcv < 100) {
					sb.append("00000");
				} else if (rcv < 1000) {
					sb.append("0000");
				} else if (rcv < 10000) {
					sb.append("000");
				} else if (rcv < 100000) {
					sb.append("00");
				} else if (rcv < 1000000) {
					sb.append("0");
				}
				s.addCell(c, rs.getString(1));
				s.addCell(c, "0000002537");
				s.addCell(c, "571911676610801");
				s.addCell(left, "CB");
				s.addCell(c, sb.append(rcv).toString());
				s.addCell(left, rs.getString(7));
				s.addCell(left, rs.getString(3));
				s.addCell(c, VeRule.toBankId(rs.getString(6)));
				s.addCell(money, rs.getDouble(10));
				s.addCell(left, "人民币");
				s.addCell(left, "C200");
				s.addCell(left, "02001:汇兑");
				s.addCell(2);
				s.addCell(left, "盈喵金服提现");
				sb.setLength(0);
			}
			rs.close();
			return info.output();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	
	
}
