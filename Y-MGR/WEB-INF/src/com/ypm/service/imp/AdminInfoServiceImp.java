package com.ypm.service.imp;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.ypm.bean.AdminInfo;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.UserSession;
import com.ypm.data.JPrepare;
import com.ypm.service.*;
import com.ypm.util.GMTime;

public class AdminInfoServiceImp extends AConfig implements AdminInfoService {

	private AdminDeptService adminDeptService;

	private AdminRoleService adminRoleService;

	protected void checkSQL() {
	}

	public AdminDeptService getAdminDeptService() {
		return adminDeptService;
	}

	public void setAdminDeptService(AdminDeptService adminDeptService) {
		this.adminDeptService = adminDeptService;
	}

	public AdminRoleService getAdminRoleService() {
		return adminRoleService;
	}

	public void setAdminRoleService(AdminRoleService adminRoleService) {
		this.adminRoleService = adminRoleService;
	}

	private long getUserId(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		try {
			long uid = 1; // default
			ResultSet rs = stmt.executeQuery("SELECT MAX(UserId) FROM admin_info");
			if (rs.next()) {
				uid += rs.getLong(1);
			} else {
				uid += 100000;
			}
			rs.close(); return uid;
		} finally {
			stmt.close();
		}
	}

	public void saveInfo(AdminInfo a) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			a.setTime(System.currentTimeMillis());
			if (a.getUserId() > 10) {
				ps = conn.prepareStatement("UPDATE admin_info SET UserNo=?,UserName=?,RealName=?,Menu=?,Dept=?,Job=?,Org=?,Time=? WHERE UserId=?");
				ps.setString(1, a.getUserNo());
				ps.setString(2, a.getUserName());
				ps.setString(3, a.getRealName());
				ps.setString(4, a.getMenu());
				ps.setString(5, a.getDept());
				ps.setString(6, a.getJob());
				ps.setString(7, a.getOrg());
				ps.setLong(8, a.getTime());
				ps.setLong(9, a.getUserId());
			} else {
				a.setUserId(getUserId(conn));
				if (a.getUserNo() == null) a.setUserNo(String.valueOf(a.getUserId()));
				ps = conn.prepareStatement("INSERT INTO admin_info (UserId,UserNo,UserName,PassWord,RealName,Menu,Dept,Job,Org,State,Soger,Super,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, a.getUserId());
				ps.setString(2, a.getUserNo());
				ps.setString(3, a.getUserName());
				ps.setString(4, a.getPassWord());
				ps.setString(5, a.getRealName());
				ps.setString(6, a.getMenu());
				ps.setString(7, a.getDept());
				ps.setString(8, a.getJob());
				ps.setString(9, a.getOrg());
				ps.setInt(10, a.getState());
				ps.setInt(11, 2); // 未建档
				ps.setInt(12, a.getAdmin());
				ps.setLong(13, a.getTime());
			}
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void update(long uid, int state) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE admin_info SET State=?,Time=? WHERE UserId=?");
			ps.setInt(1, state);
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, uid);
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	public void updateFailCt(long uid, int failCount) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE admin_info SET FailCt=?,Time=? WHERE UserId=?");
			ps.setInt(1, failCount);
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, uid);
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	public void updatePwd(long uid, String pwd) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE admin_info SET PassWord=?,Time=? WHERE UserId=?");
			ps.setString(1, pwd);
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, uid);
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void delete(long uid, String ids) throws SQLException {
		long time = System.currentTimeMillis();
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE admin_info SET State=?,Time=? WHERE UserId=?");
			for (Long u : set) {
				if (u.longValue() == uid) continue;
				ps.setInt(1, STATE_DELETE);
				ps.setLong(2, time++);
				ps.setLong(3, u.longValue());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isAdminByUserNo(String uno) {
		return JPrepare.isExists("SELECT UserId FROM admin_info WHERE UserNo=?", uno);
	}

	public boolean isAdminByUserName(String name) {
		return JPrepare.isExists("SELECT UserId FROM admin_info WHERE UserName=?", name);
	}

	public AjaxInfo findAdminInfo(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM admin_info").toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			Map<String, String> menu = this.getDictInfoByDSid(MENUBAR_TEMPLATE);
			Map<String, String> usss = this.getInfoState();
			Map<String, String> usts = this.getDictInfoBySSid(SYSTEM_USER_TYPE);
			Map<String, String> dept = this.getAdminDeptService().getAdminDetpAll();
			Map<String, String> jobs = this.getAdminRoleService().getAdminJobAll();
			Map<String, String> orgs = this.getAdminRoleService().getAdminOrgAll();
			conn = JPrepare.getConnection(); // 连接信息
			long total = this.getTotal(conn, "admin_info", fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			}
			sql.insert(0, "SELECT UserId,UserNo,UserName,PassWord,RealName,Menu,Dept,Job,Org,State,Soger,Super ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql.toString(), offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				json.formater();
				json.append("UID", rs.getLong(1));
				json.append("USNO", rs.getString(2));
				json.append("LOGINNAME", rs.getString(3));
				json.append("USERNAME", rs.getString(5));
				json.append("MENUID", menu.get(rs.getString(6)));
				json.append("DEPTID", dept.get(rs.getString(7)));
				json.append("JOBID", jobs.get(rs.getString(8)));
				json.append("ORGID", orgs.get(rs.getString(9)));
				json.append("STATE", rs.getString(10));
				json.append("STATUS", usss.get(rs.getString(10)));
				json.append("ISSUPER", usts.get(rs.getString(12)));
			}
			rs.close();
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	private AdminInfo findAdminInfo(String colName, Object obj) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			AdminInfo a = null;
			conn = JPrepare.getConnection();
			StringBuilder sql = new StringBuilder();
			ps = conn.prepareStatement(sql.append("SELECT UserId,UserNo,UserName,PassWord,RealName,Menu,Dept,Job,Org,State,Super,FailCt,Time FROM admin_info WHERE ").append(colName).append("=?").toString());
			ps.setObject(1, obj);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				a = new AdminInfo();
				a.setUserId(rs.getLong(1));
				a.setUserNo(rs.getString(2));
				a.setUserName(rs.getString(3));
				a.setPassWord(rs.getString(4));
				a.setRealName(rs.getString(5));
				a.setMenu(rs.getString(6));
				a.setDept(rs.getString(7));
				a.setJob(rs.getString(8));
				a.setOrg(rs.getString(9));
				a.setState(rs.getInt(10));
				a.setAdmin(rs.getInt(11));
				a.setFailCt(rs.getInt(12));
				a.setTime(rs.getLong(13));
			}
			rs.close(); return a;
		} catch (SQLException e) {
			return null;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AdminInfo findAdminInfoByName(String name) {
		return this.findAdminInfo("UserName", name);
	}

	public AdminInfo findAdminInfoByUid(long uid) {
		if (uid > 0) {
			return this.findAdminInfo("UserId", uid);
		} else {
			return null;
		}
	}

	public UserSession getUserSession(AdminInfo a) {
		UserSession us = new UserSession();
		us.setUserId(a.getUserId());
		us.setUserName(a.getUserName());
		us.setPassWord(a.getPassWord());
		us.setMenu(a.getMenu());
		return us;
	}

}
