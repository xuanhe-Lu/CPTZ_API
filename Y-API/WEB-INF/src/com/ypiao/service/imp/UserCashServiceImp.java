package com.ypiao.service.imp;

import java.sql.*;
import java.util.List;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.*;
import com.ypiao.util.APState;
import com.ypiao.util.GMTime;

public class UserCashServiceImp extends AConfig implements UserCashService {

	private static final String TBL_USER_CASH = "user_cash";

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

	public boolean commit(UserCash c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			boolean result = false;
			conn.setAutoCommit(false);
			long time = GMTime.currentTimeMillis();
			UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, c.getUid());
			if (c.getTma().compareTo(r.getTotal()) >= 1) {
				// 余额不足
			} else if (this.getUserInfoService().updateSubTX(conn, c.getUid(), c.getTma(), time) >= 1) {
				c.setGmtA(time);
				c.setTime(time);
				r.setSid(c.getSid());
				r.setFid(0);
				r.setTid(APState.TRADE_OUT2);
				r.setWay(APState.EVENT_P000);
				r.setEvent("提现");
				r.sub(c.getTma());
				r.setTime(time);
				this.getUserMoneyService().insert(conn, r);
				this.insert(conn, c); // 保存提现
				result = true;
			}
			conn.commit();
			if (result) {
				SyncMap.getAll().sender(SYS_A880, "save", c);
				SyncMap.getAll().sender(SYS_A128, "save", r);
			}
			return result;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(conn);
		}
	}

	public AjaxInfo sendByUid(AjaxInfo json, long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			String card = null;
			json.datas(API_OK);
			ps = conn.prepareStatement(JPrepare.getQuery("SELECT Sid,Uid,Name,BkId,BkInfo,BkName,TMA,TMB,GmtA,State,Time FROM " + TBL_USER_CASH + " WHERE Uid=? ORDER BY Time DESC", 50));
			ps.setLong(1, uid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("sid", rs.getLong(1));
				json.append("bid", rs.getInt(4));
				card = rs.getString(5);
				int j = card.length() - 4;
				json.getBuilder("info").append("\"").append(rs.getString(6)).append("(尾号").append(card.substring(j)).append(")\"");
				json.append("total", DF2.format(rs.getDouble(7)));
				json.append("fee", DF2.format(rs.getDouble(8)));
				json.append("state", rs.getInt(10));
				json.append("time", GMTime.format(rs.getLong(9), GMTime.CHINA));
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
