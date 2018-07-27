package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserChargeService;
import com.ypiao.service.UserMoneyService;
import com.ypiao.util.APState;
import com.ypiao.util.GMTime;

public class UserChargeServiceImp extends AConfig implements APState, UserChargeService {

	private static final String TBL_LOG_CHARGE = "log_charge", TBL_USER_PROTO = "user_proto";

	private UserMoneyService userMoneyService;

	private Map<Long, Object> _cache = new HashMap<Long, Object>();

	protected void checkSQL() {
	}

	public UserMoneyService getUserMoneyService() {
		return userMoneyService;
	}

	public void setUserMoneyService(UserMoneyService userMoneyService) {
		this.userMoneyService = userMoneyService;
	}

	private void save(Connection conn, LogCharge c) throws SQLException {
		PreparedStatement ps = JPrepare.prepareStatement(conn, "SELECT Sid,Uid,Name,Mobile,IdCard,BankName,BankCard,BackUrl,Amount,Signtp,Signpay,OrderId,ResCode,ResMsg,Vercd,HSIP,State,Time FROM " + TBL_LOG_CHARGE + " WHERE Sid=?");
		try {
			ps.setLong(1, c.getSid());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (c.getVercd() == null) {
					c.setVercd(rs.getString(15));
				} // 根据状态处理
				if (ORDER_SUCCESS == rs.getInt(17)) {
					rs.updateString(15, c.getVercd());
				} else {
					rs.updateLong(2, c.getUid());
					rs.updateString(3, c.getName());
					rs.updateString(4, c.getMobile());
					rs.updateString(5, c.getIdCard());
					rs.updateString(6, c.getBankName());
					rs.updateString(7, c.getBankCard());
					rs.updateString(8, c.getBackUrl());
					rs.updateBigDecimal(9, c.getAmount());
					rs.updateString(10, c.getSigntp());
					rs.updateString(11, c.getSignpay());
					rs.updateString(12, c.getOrderId());
					rs.updateString(13, c.getRes_code());
					rs.updateString(14, c.getRes_msg());
					rs.updateString(15, c.getVercd());
					rs.updateString(16, c.getHSIP());
					rs.updateInt(17, c.getState());
					rs.updateLong(18, c.getTime());
				}
				rs.updateRow();
			} else {
				rs.moveToInsertRow();
				rs.updateLong(1, c.getSid());
				rs.updateLong(2, c.getUid());
				rs.updateString(3, c.getName());
				rs.updateString(4, c.getMobile());
				rs.updateString(5, c.getIdCard());
				rs.updateString(6, c.getBankName());
				rs.updateString(7, c.getBankCard());
				rs.updateString(8, c.getBackUrl());
				rs.updateBigDecimal(9, c.getAmount());
				rs.updateString(10, c.getSigntp());
				rs.updateString(11, c.getSignpay());
				rs.updateString(12, c.getOrderId());
				rs.updateString(13, c.getRes_code());
				rs.updateString(14, c.getRes_msg());
				rs.updateString(15, c.getVercd());
				rs.updateString(16, c.getHSIP());
				rs.updateInt(17, c.getState());
				rs.updateLong(18, c.getTime());
				rs.insertRow();
			}
			rs.close();
		} finally {
			ps.close();
		}
	}

	public void save(LogCharge c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, c);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void save(UserProto p) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_USER_PROTO + " SET Uid=?,SNO=?,UserId=?,OrderId=?,Merorder=?,Total=?,State=?,Time=? WHERE CNo=?");
			ps.setLong(1, p.getUid());
			ps.setString(2, p.getSNo());
			ps.setString(3, p.getUserId());
			ps.setString(4, p.getOrderId());
			ps.setString(5, p.getMerorder());
			ps.setInt(6, p.getTotal());
			ps.setInt(7, p.getState());
			ps.setLong(8, p.getTime());
			ps.setString(9, p.getCNo());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_PROTO + " (Uid,CNo,SNO,UserId,OrderId,Merorder,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, p.getUid());
				ps.setString(2, p.getCNo());
				ps.setString(3, p.getSNo());
				ps.setString(4, p.getUserId());
				ps.setString(5, p.getOrderId());
				ps.setString(6, p.getMerorder());
				ps.setInt(7, p.getTotal());
				ps.setInt(8, p.getState());
				ps.setLong(9, p.getTime());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int update(String CNo, int state, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_USER_PROTO + " SET State=?,Time=? WHERE CNo=?");
			ps.setInt(1, state);
			ps.setLong(2, time);
			ps.setString(3, CNo);
			return ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public LogCharge findChargeBySid(long sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			LogCharge c = null;
			ps = conn.prepareStatement("SELECT Sid,Uid,Name,Mobile,IdCard,BankName,BankCard,BackUrl,Amount,Signtp,Signpay,OrderId,ResCode,ResMsg,Vercd,HSIP,State,Time FROM " + TBL_LOG_CHARGE + " WHERE Sid=?");
			ps.setLong(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				c = new LogCharge();
				c.setSid(rs.getLong(1));
				c.setUid(rs.getLong(2));
				c.setName(rs.getString(3));
				c.setMobile(rs.getString(4));
				c.setIdCard(rs.getString(5));
				c.setBankName(rs.getString(6));
				c.setBankCard(rs.getString(7));
				c.setBackUrl(rs.getString(8));
				c.setAmount(rs.getBigDecimal(9));
				c.setSigntp(rs.getString(10));
				c.setSignpay(rs.getString(11));
				c.setOrderId(rs.getString(12));
				c.setRes_code(rs.getString(13));
				c.setRes_msg(rs.getString(14));
				c.setVercd(rs.getString(15));
				c.setHSIP(rs.getString(16));
				c.setState(rs.getInt(17));
				c.setTime(rs.getLong(18));
			}
			rs.close();
			return c;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public UserProto findProtoByUid(long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			UserProto p = null;
			ps = conn.prepareStatement("SELECT Uid,CNo,SNO,UserId,OrderId,Merorder,Total,State,Time FROM " + TBL_USER_PROTO + " WHERE Uid=? AND State=?");
			ps.setLong(1, uid);
			ps.setInt(2, STATE_NORMAL);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p = new UserProto();
				p.setUid(rs.getLong(1));
				p.setCNo(rs.getString(2));
				p.setSNo(rs.getString(3));
				p.setUserId(rs.getString(4));
				p.setOrderId(rs.getString(5));
				p.setMerorder(rs.getString(6));
				p.setTotal(rs.getInt(7));
				p.setState(rs.getInt(8));
				p.setTime(rs.getLong(9));
			}
			rs.close();
			return p;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveLog(LogCharge c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			c.setTime(GMTime.currentTimeMillis());
			this.save(conn, c); // 保存操作
			if (ORDER_SUCCESS == c.getState()) {
				UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, c.getUid());
				r.setSid(c.getSid());
				r.setFid(0);
				r.setTid(APState.TRADE_ADD1);
				r.setWay(APState.EVENT_P003);
				r.setEvent("充值");
				r.add(c.getAmount());
				r.setTime(c.getTime());
				this.getUserMoneyService().save(conn, r);
				Object obj = _cache.get(c.getSid());
				if (obj != null) {
					synchronized (obj) {
						obj.notifyAll();
					}
				}
			} // 同步充值结果
			SyncMap.getAll().sender(SYS_A129, "charge", c);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void bindProto(LogCharge c, String SNo) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			UserProto p = new UserProto();
			ps = JPrepare.prepareStatement(conn, "SELECT Uid,CNo,SNo,UserId,OrderId,Merorder,Total,State,Time FROM " + TBL_USER_PROTO + " WHERE CNo=?");
			ps.setString(1, c.getBankCard());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p.setUid(rs.getLong(1));
				p.setCNo(rs.getString(2));
				p.setSNo(rs.getString(3));
				p.setUserId(rs.getString(4));
				p.setOrderId(rs.getString(5));
				p.setMerorder(rs.getString(6));
				p.setTotal(rs.getInt(7) + 1);
				p.setState(STATE_NORMAL);
				p.setTime(c.getTime());
				if (USER_UID_BEG > p.getUid()) {
					p.setUid(c.getUid());
					rs.updateLong(1, p.getUid());
				}
				rs.updateInt(7, p.getTotal());
				rs.updateInt(8, p.getState());
				rs.updateLong(9, p.getTime());
				rs.updateRow();
			} else {
				p.setUid(c.getUid());
				p.setCNo(c.getBankCard());
				p.setSNo(SNo); // 协议号
				p.setUserId(String.valueOf(c.getUid()));
				p.setOrderId(c.getOrderId());
				p.setMerorder(String.valueOf(c.getSid()));
				p.setTotal(1);
				p.setState(STATE_NORMAL);
				p.setTime(c.getTime());
				rs.moveToInsertRow();
				rs.updateLong(1, p.getUid());
				rs.updateString(2, p.getCNo());
				rs.updateString(3, p.getSNo());
				rs.updateString(4, p.getUserId());
				rs.updateString(5, p.getOrderId());
				rs.updateString(6, p.getMerorder());
				rs.updateInt(7, p.getTotal());
				rs.updateInt(8, p.getState());
				rs.updateLong(9, p.getTime());
				rs.insertRow();
			}
			rs.close();
			SyncMap.getAll().sender(SYS_A129, "proto", p);
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void unBindProto(UserProto p) throws SQLException {
		long time = GMTime.currentTimeMillis();
		if (this.update(p.getCNo(), p.getState(), time) >= 1) {
			p.setTime(time);
			SyncMap.getAll().add("cno", p.getCNo()).add("state", p.getState()).add("time", time).sender(SYS_A129, "unBind");
		}
	}

	public UserRmbs recharge(LogCharge c) throws SQLException {
		UserRmbs r = null; // 10秒内返回结果
		Object obj = new Object();
		this._cache.put(c.getSid(), obj);
		try {
			synchronized (obj) {
				for (int i = 0; i < 20; i++) {
					r = this.getUserMoneyService().findMoneyBySid(c.getSid(), c.getUid());
					if (r == null) {
						obj.wait(1000); // 等待1秒
					} else {
						obj.wait(500); // 等待0.5秒
						break;
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this._cache.remove(c.getSid());
		}
		return r;
	}
}
