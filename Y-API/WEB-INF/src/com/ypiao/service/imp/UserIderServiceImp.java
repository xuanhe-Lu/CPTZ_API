package com.ypiao.service.imp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.commons.lang.LRUMap;
import com.ypiao.bean.Messager;
import com.ypiao.bean.SyncMap;
import com.ypiao.bean.UserIder;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserIderService;
import com.ypiao.util.GMTime;

public class UserIderServiceImp extends AConfig implements UserIderService {

	private Map<String, UserIder> cache = new LRUMap<String, UserIder>(12);

	private static final String TBL_USER_IDER = "user_ider";
	private static String SQL_BY_UUID = null;
	protected void checkSQL() {
		SQL_BY_UUID = JPrepare.getQuery("SELECT Uid,Sid,State,Time FROM " + TBL_USER_IDER + " WHERE State=? AND Time<=? ORDER BY Time ASC,Uid ASC", 1);
	}

	public int update(Connection conn, long uid, String sm, long time) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_IDER + " SET Mobile=?,State=?,Time=? WHERE Uid=?");
		try {
			ps.setString(1, sm);
			ps.setInt(2, STATE_READER);
			ps.setLong(3, time);
			ps.setLong(4, uid);
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	private void saveIder(UserIder u) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_USER_IDER + " SET Mobile=?,State=?,Time=? WHERE Uid=?");
			ps.setString(1, u.getMobile());
			ps.setInt(2, u.getState());
			ps.setLong(3, u.getTime());
			ps.setLong(4, u.getUid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // add new
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_IDER + " (Uid,Sid,Mobile,State,Time) VALUES (?,?,?,?,?)");
				ps.setLong(1, u.getUid());
				ps.setString(2, null);
				ps.setString(3, u.getMobile());
				ps.setInt(4, u.getState());
				ps.setLong(5, u.getTime());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public UserIder findUserIder(String mobile) throws SQLException {
		/*Messager mgr = null;
		try {
			UserIder u = null;
			mgr = SyncMap.getAdm().add("mobile", mobile).send(SYS_A121, "getUid");
			if (mgr.isObject()) {
				u = mgr.getObject(UserIder.class);
				u.setTime(GMTime.currentTimeMillis());
				this.cache.put(mobile, u);
				this.saveIder(u);
			}
			return u;
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			mgr.destroy();
		}*/
		UserIder ider = null;
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = JPrepare.prepareStatement(conn, "SELECT Uid,Sid,Mobile,State,Time FROM " + TBL_USER_IDER + " WHERE Mobile=?");
			ps.setString(1, mobile);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ider = new UserIder();
				ider.setUid(rs.getLong(1));
				ider.setMobile(rs.getString(3));
				ider.setState(rs.getInt(4));
				ider.setTime(rs.getLong(5));
				if (ider.getState() == STATE_READER) {
					ider.setState(STATE_NORMAL);
					ider.setTime(GMTime.currentTimeMillis());
					rs.updateLong(5, ider.getTime());
					rs.updateRow();
				}
			}
			rs.close();
			if (ider == null) {
				ps.close();
				ps = conn.prepareStatement("SELECT Uid,Account,State,Time FROM user_info WHERE Account=?");
				ps.setString(1, mobile);
				rs = ps.executeQuery();
				if (rs.next()) {
					ider = new UserIder();
					ider.setUid(rs.getLong(1));
					ider.setMobile(rs.getString(2));
					ider.setState(STATE_READER);
					ider.setTime(rs.getLong(4));
				}
				rs.close();
				if (ider == null) {
					ps.close();
					ider = new UserIder();
					ider.setState(STATE_NORMAL);
					ider.setTime(GMTime.currentTimeMillis());
					ider.setUid(findUserId(conn, ider.getTime()));
					ider.setMobile(mobile);
					ps = conn.prepareStatement("UPDATE " + TBL_USER_IDER + " SET Mobile=?,State=? WHERE Uid=?");
					ps.setString(1, ider.getMobile());
					ps.setInt(2, STATE_READER);
					ps.setLong(3, ider.getUid());
					ps.executeUpdate();
				}
			}
			this.cache.put(mobile, ider);
			return ider;
		} finally {
			JPrepare.close(ps, conn);
		}


	}

	public UserIder getUserIder(String mobile) throws SQLException {
		UserIder u = this.cache.get(mobile);
		if (u == null) {
			return this.findUserIder(mobile);
		} else if (u.getState() == STATE_NORMAL && (GMTime.currentTimeMillis() - u.getTime()) > 10000) {
			return this.findUserIder(mobile);
		}
		return u;
	}


	/** 重新生成索引编号 */
	public void compute() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long sid = USER_UID_BEG;
			ps = conn.prepareStatement("SELECT Uid,Sid,State,Time FROM " + TBL_USER_IDER + " WHERE Uid=?");
			ps.setLong(1, 1);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				sid = rs.getLong(4);
			} else {
				sid += 1;
			}
			rs.close();
			try {
				ps.close();
				long beg = sid; // begin user id
				long time = GMTime.currentTimeMillis();
				conn.setAutoCommit(false);
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_IDER + " (Uid,State,Time) VALUES (?,?,?)");
				for (int i = 200; i > 10; i--) {
					ps.setLong(1, sid++);
					ps.setInt(2, STATE_NORMAL);
					ps.setLong(3, time++);
					ps.addBatch();
				} // 加载预处理
				for (int i = 10; i > 0; i--) {
					ps.setLong(1, sid++);
					ps.setInt(2, STATE_CHECK);
					ps.setLong(3, time++);
					ps.addBatch();
				} // 提交处理
				ps.executeBatch();
				ps.close(); // 更新指针
				ps = conn.prepareStatement("UPDATE " + TBL_USER_IDER + " SET Time=? WHERE Uid=?");
				ps.setLong(1, sid);
				ps.setLong(2, 1);
				if (ps.executeUpdate() <= 0) {
					ps.close(); // 新增编号
					ps = conn.prepareStatement("INSERT INTO " + TBL_USER_IDER + " (Uid,Sid,State,Time) VALUES (?,?,?,?)");
					ps.setLong(1, 1);
					ps.setString(2, "SYS");
					ps.setInt(3, STATE_READER);
					ps.setLong(4, sid);
					ps.executeUpdate();
				}
				ps.close();
				ps = conn.prepareStatement("UPDATE " + TBL_USER_IDER + " SET State=? WHERE Uid<=? AND State=?");
				ps.setInt(1, STATE_NORMAL);
				ps.setLong(2, beg);
				ps.setInt(3, STATE_CHECK);
				ps.executeUpdate();
				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private long findUserId(Connection conn, long time) throws SQLException {
		long uid = this.findUserId(conn, STATE_NORMAL, time);
		if (USER_UID_BEG > uid) {
			this.execute(() -> {
				try {
					this.compute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
			uid = this.findUserId(conn, STATE_CHECK, time);
			if (USER_UID_BEG > uid) {
				uid = USER_UID_BEG;
			}
		}
		return uid;
	}

	private long findUserId(Connection conn, int state, long time) throws SQLException {
		PreparedStatement ps = JPrepare.prepareStatement(conn, SQL_BY_UUID);
		try {
			long uid = 0;
			ps.setInt(1, state);
			ps.setLong(2, (time - 5000));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				uid = rs.getLong(1);
				rs.updateLong(4, time);
				rs.updateRow();
			}
			rs.close();
			return uid;
		} finally {
			ps.close();
		}
	}

}
