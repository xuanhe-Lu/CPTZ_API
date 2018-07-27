package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ypm.bean.LogCharge;
import com.ypm.bean.SyncMap;
import com.ypm.bean.UserRmbs;
import com.ypm.data.JPrepare;
import com.ypm.service.UserMoneyService;
import com.ypm.util.Table;

public class UserMoneyServiceImp extends AConfig implements UserMoneyService {

	private static final String TBL_LOG_CHARGE = "log_charge", TBL_USER_RMBS = "user_rmbs";

	private static String SQL_BY_RMB;
	private static String SQL_BY_RMB1;

	protected void checkSQL() {
		SQL_BY_RMB = JPrepare.getQuery("SELECT Sid,Tid,Uid,Fid,Way,Event,Cost,Adds,Total,State,Time FROM " + TBL_USER_RMBS + " WHERE Uid=? ORDER BY Time DESC", 1);
		SQL_BY_RMB1 = JPrepare.getQuery("SELECT Sid,Tid,Uid,Fid,Way,Event,Cost,Adds,Total,State,Time FROM ypiao.user_rmbs WHERE Uid=? ORDER BY Time DESC", 1);
	}

	private void save(Connection conn, LogCharge c) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_LOG_CHARGE + " SET Uid=?,Name=?,Mobile=?,IdCard=?,BankName=?,BankCard=?,BackUrl=?,Amount=?,Signtp=?,Signpay=?,OrderId=?,ResCode=?,ResMsg=?,Vercd=?,HSIP=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setLong(1, c.getUid());
			ps.setString(2, c.getName());
			ps.setString(3, c.getMobile());
			ps.setString(4, c.getIdCard());
			ps.setString(5, c.getBankName());
			ps.setString(6, c.getBankCard());
			ps.setString(7, c.getBackUrl());
			ps.setBigDecimal(8, c.getAmount());
			ps.setString(9, c.getSigntp());
			ps.setString(10, c.getSignpay());
			ps.setString(11, c.getOrderId());
			ps.setString(12, c.getRes_code());
			ps.setString(13, c.getRes_msg());
			ps.setString(14, c.getVercd());
			ps.setString(15, c.getHSIP());
			ps.setInt(16, c.getState());
			ps.setLong(17, c.getTime());
			ps.setLong(18, c.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // 新增记录
				ps = conn.prepareStatement("INSERT INTO " + TBL_LOG_CHARGE + " (Sid,Uid,Name,Mobile,IdCard,BankName,BankCard,BackUrl,Amount,Signtp,Signpay,OrderId,ResCode,ResMsg,HSIP,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, c.getSid());
				ps.setLong(2, c.getUid());
				ps.setString(3, c.getName());
				ps.setString(4, c.getMobile());
				ps.setString(5, c.getIdCard());
				ps.setString(6, c.getBankName());
				ps.setString(7, c.getBankCard());
				ps.setString(8, c.getBackUrl());
				ps.setBigDecimal(9, c.getAmount());
				ps.setString(10, c.getSigntp());
				ps.setString(11, c.getSignpay());
				ps.setString(12, c.getOrderId());
				ps.setString(13, c.getRes_code());
				ps.setString(14, c.getRes_msg());
				ps.setString(15, c.getHSIP());
				ps.setInt(16, c.getState());
				ps.setLong(17, c.getTime());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	public int insert(Connection conn, UserRmbs r) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("INSERT INTO " + TBL_USER_RMBS + " (Sid,Tid,Uid,Fid,Way,Event,Cost,Adds,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
		try {
			ps.setLong(1, r.getSid());
			ps.setInt(2, r.getTid());
			ps.setLong(3, r.getUid());
			ps.setLong(4, r.getFid());
			ps.setString(5, r.getWay());
			ps.setString(6, r.getEvent());
			ps.setBigDecimal(7, r.getCost());
			ps.setBigDecimal(8, r.getAdds());
			ps.setBigDecimal(9, r.getTotal());
			ps.setInt(10, r.getState());
			ps.setLong(11, r.getTime());
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	public int insertYpiao(Connection conn, UserRmbs r) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("INSERT INTO ypiao.user_rmbs (Sid,Tid,Uid,Fid,Way,Event,Cost,Adds,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
		try {
			ps.setLong(1, r.getSid());
			ps.setInt(2, r.getTid());
			ps.setLong(3, r.getUid());
			ps.setLong(4, r.getFid());
			ps.setString(5, r.getWay());
			ps.setString(6, r.getEvent());
			ps.setBigDecimal(7, r.getCost());
			ps.setBigDecimal(8, r.getAdds());
			ps.setBigDecimal(9, r.getTotal());
			ps.setInt(10, r.getState());
			ps.setLong(11, r.getTime());
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public int update(Connection conn, UserRmbs r) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_RMBS + " SET Tid=?,Uid=?,Fid=?,Way=?,Event=?,Cost=?,Adds=?,Total=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setInt(1, r.getTid());
			ps.setLong(2, r.getUid());
			ps.setLong(3, r.getFid());
			ps.setString(4, r.getWay());
			ps.setString(5, r.getEvent());
			ps.setBigDecimal(6, r.getCost());
			ps.setBigDecimal(7, r.getAdds());
			ps.setBigDecimal(8, r.getTotal());
			ps.setInt(9, r.getState());
			ps.setLong(10, r.getTime());
			ps.setLong(11, r.getSid());
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	public int updateYpiao(Connection conn, UserRmbs r) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE ypiao.user_rmbs SET Tid=?,Uid=?,Fid=?,Way=?,Event=?,Cost=?,Adds=?,Total=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setInt(1, r.getTid());
			ps.setLong(2, r.getUid());
			ps.setLong(3, r.getFid());
			ps.setString(4, r.getWay());
			ps.setString(5, r.getEvent());
			ps.setBigDecimal(6, r.getCost());
			ps.setBigDecimal(7, r.getAdds());
			ps.setBigDecimal(8, r.getTotal());
			ps.setInt(9, r.getState());
			ps.setLong(10, r.getTime());
			ps.setLong(11, r.getSid());
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public void save(Connection conn, UserRmbs r) throws SQLException {
		if (this.update(conn, r) >= 1) {
			// Ignroed
		} else if (this.insert(conn, r) >= 1) {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_USER_STATUS + " SET MA=(MC+?),MB=?,Time=? WHERE Uid=?");
			try {
				ps.setBigDecimal(1, r.getTotal());
				ps.setBigDecimal(2, r.getTotal());
				ps.setLong(3, r.getTime());
				ps.setLong(4, r.getUid());
				ps.executeUpdate();
				SyncMap.getAll().sender(SYS_A128, "save", r);



			} finally {
				ps.close();
			}
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

	public void save(UserRmbs rmb) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, rmb);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void share(Connection conn, UserRmbs r) throws SQLException {
		if (this.update(conn, r) >= 1) {
			// Ignroed
		} else if (this.insert(conn, r) >= 1) {
			PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_USER_STATUS + " SET MA=(MC+?),MB=?,MF=(MF+?),Time=? WHERE Uid=?");
			try {
				ps.setBigDecimal(1, r.getTotal());
				ps.setBigDecimal(2, r.getTotal());
				ps.setBigDecimal(3, r.getAdds());
				ps.setLong(4, r.getTime());
				ps.setLong(5, r.getUid());
				ps.executeUpdate();
				SyncMap.getAll().sender(SYS_A128, "share", r);
			} finally {
				ps.close();
			}
		}
	}

	public void share(UserRmbs rmb) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.share(conn, rmb);
		} finally {
			JPrepare.close(conn);
		}
	}

	/** 获取当前余额 */
	public UserRmbs findMoneyByUid(Connection conn, long uid) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_BY_RMB);
		try {
			ps.setLong(1, uid);
			UserRmbs r = new UserRmbs(uid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				r.setSid(rs.getLong(1));
				r.setTid(rs.getInt(2));
				r.setFid(rs.getLong(4));
				r.setWay(rs.getString(5));
				r.setEvent(rs.getString(6));
				r.setCost(rs.getBigDecimal(7));
				r.setAdds(rs.getBigDecimal(8));
				r.setTotal(rs.getBigDecimal(9));
				r.setState(rs.getInt(10));
				r.setTime(rs.getLong(11));
			}
			rs.close();
			return r;
		} finally {
			ps.close();
		}
	}


	/** 获取YPiao当前余额 */
	public UserRmbs findYpiaoMoneyByUid(Connection conn, long uid) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_BY_RMB1);
		try {
			ps.setLong(1, uid);
			UserRmbs r = new UserRmbs(uid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				r.setSid(rs.getLong(1));
				r.setTid(rs.getInt(2));
				r.setFid(rs.getLong(4));
				r.setWay(rs.getString(5));
				r.setEvent(rs.getString(6));
				r.setCost(rs.getBigDecimal(7));
				r.setAdds(rs.getBigDecimal(8));
				r.setTotal(rs.getBigDecimal(9));
				r.setState(rs.getInt(10));
				r.setTime(rs.getLong(11));
			}
			rs.close();
			return r;
		} finally {
			ps.close();
		}
	}
}
