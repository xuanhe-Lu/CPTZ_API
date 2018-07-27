package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.commons.lang.LRUMap;
import org.commons.lang.RandomUtils;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.*;
import com.ypiao.util.AUtils;
import com.ypiao.util.GMTime;
import com.ypiao.util.VeStr;

public class UserLogerServiceImp extends AConfig implements UserLogerService {

	private static final String TBL_USER_TOKEN = "user_token";

	private Map<Long, UserSession> cache = new LRUMap<Long, UserSession>(64);

	private UserIderService userIderService;

	private UserInfoService userInfoService;

	protected void checkSQL() {
	}

	public UserIderService getUserIderService() {
		return userIderService;
	}

	public void setUserIderService(UserIderService userIderService) {
		this.userIderService = userIderService;
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	private UserSession getSession(long uid) {
		UserSession us = cache.get(uid);
		if (us == null) {
			us = new UserSession(0);
			if (uid >= USER_UID_BEG) {
				this.cache.put(uid, us);
			}
		}
		return us;
	}

	public int update(UserSession us) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_USER_TOKEN + " SET UPS=?,VIP=?,Channel=?,Facer=?,Gender=?,Mobile=?,Nicer=?,Binds=?,Reals=? WHERE Uid=?");
			ps.setLong(1, us.getUPS());
			ps.setInt(2, us.getVIP());
			ps.setString(3, us.getChannel());
			ps.setInt(4, us.getFacer());
			ps.setInt(5, us.getGender());
			ps.setString(6, us.getMobile());
			ps.setString(7, us.getNicer());
			ps.setInt(8, us.getBinds());
			ps.setInt(9, us.getReals());
			ps.setLong(10, us.getUid());
			return ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void register(UserReger reg) throws SQLException {
		if (USER_UID_BEG > reg.getUid()) {
			UserIder u = this.getUserIderService().getUserIder(reg.getAccount());
			reg.setUid(u.getUid());
		}
		reg.setRtime(GMTime.currentTimeMillis());
		reg.setTime(reg.getRtime()); // 注册时间
		this.getUserInfoService().register(reg);
	}

	private void saveToken(Connection conn, UserSession us) throws SQLException {
		long time = System.currentTimeMillis();
		PreparedStatement ps = JPrepare.prepareStatement(conn, "SELECT Uid,UPS,VIP,Stime,Token,Channel,Facer,Gender,Mobile,Nicer,Binds,Reals,USid,UGzh,UXch,Time FROM " + TBL_USER_TOKEN + " WHERE Uid=?");
		try {
			ps.setLong(1, us.getUid());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				long out = rs.getLong(16) + 30000;
				rs.updateInt(3, us.getVIP());
				rs.updateLong(4, 0);
				if (time >= out) {
					us.setKey(RandomUtils.randomAlphanumeric(16));
					rs.updateString(5, us.getKey());
				} else {
					us.setKey(rs.getString(5));
				}
				rs.updateString(6, us.getChannel());
				rs.updateInt(7, us.getFacer());
				rs.updateInt(8, us.getGender());
				rs.updateString(9, us.getMobile());
				rs.updateString(10, us.getNicer());
				rs.updateInt(11, us.getBinds());
				rs.updateInt(12, us.getReals());
				rs.updateString(13, us.getSid());
				if (us.getGzh() == null) {
					us.setGzh(rs.getString(14));
				} else {
					rs.updateString(14, us.getGzh());
				}
				if (us.getXch() == null) {
					us.setXch(rs.getString(15));
				} else {
					rs.updateString(15, us.getXch());
				}
				rs.updateLong(16, time);
				rs.updateRow();
			} else {
				us.setKey(RandomUtils.randomAlphanumeric(16));
				rs.moveToInsertRow();
				rs.updateLong(1, us.getUid());
				rs.updateLong(2, us.getUPS());
				rs.updateInt(3, us.getVIP());
				rs.updateLong(4, 0);
				rs.updateString(5, us.getKey());
				rs.updateString(6, us.getChannel());
				rs.updateInt(7, us.getFacer());
				rs.updateInt(8, us.getGender());
				rs.updateString(9, us.getMobile());
				rs.updateString(10, us.getNicer());
				rs.updateInt(11, us.getBinds());
				rs.updateInt(12, us.getReals());
				rs.updateString(13, us.getSid());
				rs.updateString(14, us.getGzh());
				rs.updateString(15, us.getXch());
				rs.updateLong(16, time);
				rs.insertRow();
			}
			rs.close();
			this.cache.put(us.getUid(), us);
		} finally {
			ps.close();
		}
	}

	public UserInfo getUserInfoByAcc(String fix, String acc) throws SQLException {
		String sm = VeStr.getMobile(fix, acc);
		if (sm == null) {
			long uid = AUtils.toLong(acc);
			return this.getUserInfoService().findUserInfoByUid(uid);
		} else {
			return this.getUserInfoService().findUserInfoBySM(sm);
		}
	}

	public UserSession getAccessByUid(long uid) throws SQLException {
		UserSession us = this.getSession(uid);
		if (uid > us.getUid()) {
			Connection conn = JPrepare.getConnection();
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("SELECT Uid,UPS,VIP,Stime,Token,Channel,Facer,Gender,Mobile,Nicer,Binds,Reals,USid,UGzh,UXch,Time FROM " + TBL_USER_TOKEN + " WHERE Uid=?");
				ps.setLong(1, uid);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					us.setUid(uid);
					us.setUPS(rs.getLong(2));
					us.setVIP(rs.getInt(3));
					us.setChannel(rs.getString(6));
					us.setFacer(rs.getInt(7));
					us.setGender(rs.getInt(8));
					us.setMobile(rs.getString(9));
					us.setNicer(rs.getString(10));
					us.setBinds(rs.getInt(11));
					us.setReals(rs.getInt(12));
					us.setSid(rs.getString(13));
					us.setGzh(rs.getString(14));
					us.setXch(rs.getString(15));
					this.cache.put(uid, us);
				}
				rs.close();
			} finally {
				JPrepare.close(ps, conn);
			}
		}
		return us;
	}

	public UserSession getAccessToken(long uid, long stime, String token) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			UserSession us = null;
			conn = JPrepare.getConnection();
			ps = JPrepare.prepareStatement(conn, "SELECT Uid,UPS,VIP,Stime,Token,Channel,Facer,Gender,Mobile,Nicer,Binds,Reals,USid,UGzh,UXch,Time FROM " + TBL_USER_TOKEN + " WHERE Uid=?");
			ps.setLong(1, uid);
			ResultSet rs = ps.executeQuery();
			if (rs.next() && stime >= rs.getLong(4)) {
				String md5 = VeStr.MD5(rs.getString(5) + stime);
				if (md5.equalsIgnoreCase(token)) {
					long time = System.currentTimeMillis();
					us = this.getSession(uid);
					us.setUid(uid);
					us.setUPS(rs.getLong(2));
					us.setVIP(rs.getInt(3));
					us.setChannel(rs.getString(6));
					us.setFacer(rs.getInt(7));
					us.setGender(rs.getInt(8));
					us.setMobile(rs.getString(9));
					us.setNicer(rs.getString(10));
					us.setBinds(rs.getInt(11));
					us.setReals(rs.getInt(12));
					us.setSid(rs.getString(13));
					us.setGzh(rs.getString(14));
					us.setXch(rs.getString(15));
					rs.updateLong(4, stime);
					rs.updateLong(16, time);
					rs.updateRow();
				}
			}
			rs.close();
			return us;
		} catch (SQLException e) {
			return null;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void login(UserSession us, String dev, String model) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			this.saveToken(conn, us);
			ps = conn.prepareStatement("UPDATE user_info SET Sid=?,Login=? WHERE Uid=?");
			ps.setString(1, us.getSid());
			ps.setLong(2, us.getTime());
			ps.setLong(3, us.getUid());
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void logout(UserSession us) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM " + TBL_USER_TOKEN + " WHERE Uid=?");
			ps.setLong(1, us.getUid());
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
