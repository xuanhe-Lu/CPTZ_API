package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ypm.bean.UserAuth;
import com.ypm.data.JPrepare;
import com.ypm.service.UserAuthService;
import com.ypm.service.UserInfoService;

public class UserAuthServiceImp extends AConfig implements UserAuthService {

	private static final String TBL_USER_AUTH = "user_auth";

	private UserInfoService userInfoService;

	protected void checkSQL() {
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public void save(Connection conn, UserAuth a) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_AUTH + " SET Pays=?,Addr=?,Name=?,IdCard=?,Gender=?,Btime=?,Rtime=?,State=?,Time=? WHERE Uid=?");
		try {
			ps.setString(1, a.getPays());
			ps.setString(2, a.getAddr());
			ps.setString(3, a.getName());
			ps.setString(4, a.getIdCard());
			ps.setInt(5, a.getGender());
			ps.setLong(6, a.getBtime());
			ps.setLong(7, a.getRtime());
			ps.setInt(8, a.getState());
			ps.setLong(9, a.getTime());
			ps.setLong(10, a.getUid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_AUTH + " (Uid,Pays,Addr,Name,IdCard,Gender,Btime,Rtime,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, a.getUid());
				ps.setString(2, a.getPays());
				ps.setString(3, a.getAddr());
				ps.setString(4, a.getName());
				ps.setString(5, a.getIdCard());
				ps.setInt(6, a.getGender());
				ps.setLong(7, a.getBtime());
				ps.setLong(8, a.getRtime());
				ps.setInt(9, a.getState());
				ps.setLong(10, a.getTime());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		} // 更新用户主表
		this.getUserInfoService().update(conn, a);
	}

	public void save(UserAuth a) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, a);
		} finally {
			JPrepare.close(conn);
		}
	}

	public UserAuth findAuthByUid(Connection conn, long uid) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT Uid,Pays,Addr,Name,IdCard,Gender,Btime,Rtime,State,Time FROM " + TBL_USER_AUTH + " WHERE Uid=?");
		try {
			UserAuth a = null;
			ps.setLong(1, uid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				a = new UserAuth();
				a.setUid(rs.getLong(1));
				a.setPays(rs.getString(2));
				a.setAddr(rs.getString(3));
				a.setName(rs.getString(4));
				a.setIdCard(rs.getString(5));
				a.setGender(rs.getInt(6));
				a.setBtime(rs.getLong(7));
				a.setRtime(rs.getLong(8));
				a.setState(rs.getInt(9));
				a.setTime(rs.getLong(10));
			}
			rs.close();
			return a;
		} finally {
			ps.close();
		}
	}

	public UserAuth findAuthByUid(long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			return findAuthByUid(conn, uid);
		} finally {
			JPrepare.close(conn);
		}
	}
}
