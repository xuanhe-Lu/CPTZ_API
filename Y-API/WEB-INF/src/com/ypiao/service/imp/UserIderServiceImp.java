package com.ypiao.service.imp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

	protected void checkSQL() {
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
		Messager mgr = null;
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
}
