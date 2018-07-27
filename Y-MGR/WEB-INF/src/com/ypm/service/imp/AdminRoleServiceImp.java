package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.AdminRoleService;

public class AdminRoleServiceImp extends AConfig implements AdminRoleService {

	private static final String KEY_JOBS = "jobs=";

	private static final String KEY_ORGS = "orgs=";

	protected void checkSQL() {
	}
	// ==================== 职位信息设置 ====================
	private void updateJob(Connection conn, int code, int leaf) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE admin_jobs SET Leaf=? WHERE Code=?");
			ps.setInt(1, leaf);
			ps.setInt(2, code);
			ps.executeUpdate();
		} finally {
			if (ps != null) ps.close();
		}
	}

	public void saveJob(AdminJobs job) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			if (job.getCode() <= 0) {
				job.setCode(this.getId(conn, "admin_jobs", "Code"));
				job.setSortid(this.getSortid(conn, "SELECT COUNT(*) FROM admin_jobs WHERE Type=?", job.getType()));
				ps = conn.prepareStatement("INSERT INTO admin_jobs (Code,Type,Sortid,Name,Remark,Leaf) VALUES (?,?,?,?,?,?)");
				ps.setInt(1, job.getCode());
				ps.setInt(2, job.getType());
				ps.setInt(3, job.getSortid());
				ps.setString(4, job.getName());
				ps.setString(5, job.getRemark());
				ps.setInt(6, 1);
				if (ps.executeUpdate() > 0) {
					this.updateJob(conn, job.getType(), 0);
				}
			} else {
				ps = conn.prepareStatement("UPDATE admin_jobs SET Name=?,Remark=? WHERE Code=?");
				ps.setString(1, job.getName());
				ps.setString(2, job.getRemark());
				ps.setInt(3, job.getCode());
				ps.executeUpdate();
			}
			conn.commit();
			this.removeS(KEY_JOBS);
			this.removeS(KEY_JOBS + job.getType());
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isAdminJobByName(String name) {
		return JPrepare.isExists("SELECT Code FROM admin_jobs WHERE Name=?", name);
	}

	public boolean isAdminJobByCode(int code) {
		return JPrepare.isExists("SELECT Code FROM admin_jobs WHERE Code=?", code);
	}

	public boolean isAdminJobByType(int type) {
		return JPrepare.isExists("SELECT Code FROM admin_jobs WHERE Type=?", type);
	}

	public AjaxInfo findJobChildrens(int code) {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Code,Name,Leaf FROM admin_jobs WHERE Type=? ORDER BY Sortid ASC");
			ps.setInt(1, code);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
				if (rs.getInt(3) == 0) {
					json.append("leaf", false);
					json.append("nodeType", "async");
					json.append("iconCls", "mnu-folder");
				} else {
					json.append("leaf", true);
					json.append("nodeType", "async");
					json.append("iconCls", "mnu-leaf");
				}
			}
			rs.close();
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	private String findJobChildrens(AjaxInfo json, int code) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Code,Name,Leaf FROM admin_jobs WHERE Type=? ORDER BY Sortid ASC");
			ps.setInt(1, code);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
				if (rs.getInt(3) == 0) {
					json.append("leaf", false);
				} else {
					json.append("leaf", true);
				}
			}
			rs.close();
			return json.toString();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo getJobChildrens(int code) {
		String key = KEY_JOBS + code;
		AjaxInfo json = AjaxInfo.getArray();
		try {
			Object obj = this.getS(key);
			if (obj == null) {
				this.putS(key, this.findJobChildrens(json, code));
			} else {
				json.setBody((String) obj);
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			key = null;
		}
		return json;
	}

	public Map<String, String> getAdminJobAll() {
		return this.getMapStr(KEY_JOBS, "SELECT Code,Name FROM admin_jobs ORDER BY Code ASC");
	}

	public AdminJobs getAdminJobById(int code) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			AdminJobs job = null;
			ps = conn.prepareStatement("SELECT Code,Type,Sortid,Name,Remark,Leaf FROM admin_jobs WHERE Code=?");
			ps.setInt(1, code);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				job = new AdminJobs();
				job.setCode(rs.getInt(1));
				job.setType(rs.getInt(2));
				job.setSortid(rs.getInt(3));
				job.setName(rs.getString(4));
				job.setRemark(rs.getString(5));
				job.setLeaf(rs.getInt(6));
			}
			rs.close(); return job;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void moveJob(AdminJobs job, int type, int sortid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			this.removeS(KEY_JOBS + job.getType());
			if (job.getType() == type) {
				if (job.getSortid() > sortid) {
					ps = conn.prepareStatement("UPDATE admin_jobs SET Sortid=(Sortid+1) WHERE Type=? AND Sortid<? AND Sortid>=?");
				} else {
					ps = conn.prepareStatement("UPDATE admin_jobs SET Sortid=(Sortid-1) WHERE Type=? AND Sortid>? AND Sortid<=?");
				}
				ps.setInt(1, type);
				ps.setInt(2, job.getSortid());
				ps.setInt(3, sortid);
				ps.executeUpdate();
			} else {
				this.removeS(KEY_JOBS + type);
				ps = conn.prepareStatement("UPDATE admin_jobs SET Sortid=(Sortid-1) WHERE Type=? AND Sortid>?");
				ps.setInt(1, job.getType());
				ps.setInt(2, job.getSortid());
				int a = ps.executeUpdate();
				ps.close(); // new type
				if (a <= 0 && job.getSortid() <= 1) {
					this.updateJob(conn, job.getType(), 1);
				}
				ps = conn.prepareStatement("UPDATE admin_jobs SET Sortid=(Sortid+1) WHERE Type=? AND Sortid>=?");
				ps.setInt(1, type);
				ps.setInt(2, sortid);
				ps.executeUpdate();
			} // jobs info
			if (ps != null) ps.close();
			ps = conn.prepareStatement("UPDATE admin_jobs SET Type=?,Sortid=? WHERE Code=?");
			ps.setInt(1, type);
			ps.setInt(2, sortid);
			ps.setInt(3, job.getCode());
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void orderJob(String ids, int code) throws SQLException {
		String[] ts = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE admin_jobs SET Sortid=? WHERE Code=?");
			for (String id : ts) {
				ps.setInt(1, index++);
				ps.setInt(2, Integer.parseInt(id));
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			this.removeS(KEY_JOBS + code);
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void removeJob(AdminJobs job) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM admin_jobs WHERE Code=?");
			ps.setInt(1, job.getCode());
			if (ps.executeUpdate() >= 1) {
				ps.close(); // close
				ps = conn.prepareStatement("UPDATE admin_jobs SET Sortid=(Sortid-1) WHERE Type=? AND Sortid>?");
				ps.setInt(1, job.getType());
				ps.setInt(2, job.getSortid());
				if (job.getType() <= 0) {
					ps.executeUpdate();
				} else if (ps.executeUpdate() <= 0 && job.getSortid() <= 1) {
					ps.close();
					this.updateJob(conn, job.getType(), 1);
				}
			}
			conn.commit();
			this.removeS(KEY_JOBS + job.getCode());
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	// ==================== 组织机构设置 ====================
	private void updateOrg(Connection conn, int code, int leaf) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE admin_orgs SET Leaf=? WHERE Code=?");
			ps.setInt(1, leaf);
			ps.setInt(2, code);
			ps.executeUpdate();
		} finally {
			if (ps != null) ps.close();
		}
	}

	public void saveOrg(AdminOrgs org) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			if (org.getCode() <= 0) {
				org.setCode(this.getId(conn, "admin_orgs", "Code"));
				org.setSortid(this.getSortid(conn, "SELECT COUNT(*) FROM admin_orgs WHERE Type=?", org.getType()));
				ps = conn.prepareStatement("INSERT INTO admin_orgs (Code,Type,Sortid,Name,Remark,Leaf) VALUES (?,?,?,?,?,?)");
				ps.setInt(1, org.getCode());
				ps.setInt(2, org.getType());
				ps.setInt(3, org.getSortid());
				ps.setString(4, org.getName());
				ps.setString(5, org.getRemark());
				ps.setInt(6, 1);
				if (ps.executeUpdate() > 0) {
					this.updateOrg(conn, org.getType(), 0);
				}
			} else {
				ps = conn.prepareStatement("UPDATE admin_orgs SET Name=?,Remark=? WHERE Code=?");
				ps.setString(1, org.getName());
				ps.setString(2, org.getRemark());
				ps.setInt(3, org.getCode());
				ps.executeUpdate();
			}
			conn.commit();
			this.removeS(KEY_ORGS);
			this.removeS(KEY_ORGS + org.getType());
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isAdminOrgByName(String name) {
		return JPrepare.isExists("SELECT Code FROM admin_orgs WHERE Name=?", name);
	}

	public boolean isAdminOrgByCode(int code) {
		return JPrepare.isExists("SELECT Code FROM admin_orgs WHERE Code=?", code);
	}

	public boolean isAdminOrgByType(int type) {
		return JPrepare.isExists("SELECT Code FROM admin_orgs WHERE Type=?", type);
	}

	public AjaxInfo findOrgChildrens(int code) {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Code,Name,Leaf FROM admin_orgs WHERE Type=? ORDER BY Sortid ASC");
			ps.setInt(1, code);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
				if (rs.getInt(3) == 0) {
					json.append("leaf", false);
					json.append("nodeType", "async");
					json.append("iconCls", "mnu-folder");
				} else {
					json.append("leaf", true);
					json.append("nodeType", "async");
					json.append("iconCls", "mnu-leaf");
				}
			}
			rs.close();
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	private String findOrgChildrens(AjaxInfo json, int code) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Code,Name,Leaf FROM admin_orgs WHERE Type=? ORDER BY Sortid ASC");
			ps.setInt(1, code);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
				if (rs.getInt(3) == 0) {
					json.append("leaf", false);
				} else {
					json.append("leaf", true);
				}
			}
			rs.close();
			return json.toString();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo getOrgChildrens(int code) {
		String key = KEY_ORGS + code;
		AjaxInfo json = AjaxInfo.getArray();
		try {
			Object obj = this.getS(key);
			if (obj == null) {
				this.putS(key, this.findOrgChildrens(json, code));
			} else {
				json.setBody((String) obj);
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			key = null;
		}
		return json;
	}

	public Map<String, String> getAdminOrgAll() {
		return this.getMapStr(KEY_ORGS, "SELECT Code,Name FROM admin_orgs ORDER BY Code ASC");
	}

	public AdminOrgs getAdminOrgById(int code) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			AdminOrgs org = null;
			ps = conn.prepareStatement("SELECT Code,Type,Sortid,Name,Remark,Leaf FROM admin_orgs WHERE Code=?");
			ps.setInt(1, code);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				org = new AdminOrgs();
				org.setCode(rs.getInt(1));
				org.setType(rs.getInt(2));
				org.setSortid(rs.getInt(3));
				org.setName(rs.getString(4));
				org.setRemark(rs.getString(5));
				org.setLeaf(rs.getInt(6));
			}
			rs.close(); return org;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void moveOrg(AdminOrgs org, int type, int sortid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			this.removeS(KEY_ORGS + org.getType());
			if (org.getType() == type) {
				if (org.getSortid() > sortid) {
					ps = conn.prepareStatement("UPDATE admin_orgs SET Sortid=(Sortid+1) WHERE Type=? AND Sortid<? AND Sortid>=?");
				} else {
					ps = conn.prepareStatement("UPDATE admin_orgs SET Sortid=(Sortid-1) WHERE Type=? AND Sortid>? AND Sortid<=?");
				}
				ps.setInt(1, type);
				ps.setInt(2, org.getSortid());
				ps.setInt(3, sortid);
				ps.executeUpdate();
			} else {
				this.removeS(KEY_ORGS + type);
				ps = conn.prepareStatement("UPDATE admin_orgs SET Sortid=(Sortid-1) WHERE Type=? AND Sortid>?");
				ps.setInt(1, org.getType());
				ps.setInt(2, org.getSortid());
				int a = ps.executeUpdate();
				ps.close(); // new type
				if (a <= 0 && org.getSortid() <= 1) {
					this.updateOrg(conn, org.getType(), 1);
				}
				ps = conn.prepareStatement("UPDATE admin_orgs SET Sortid=(Sortid+1) WHERE Type=? AND Sortid>=?");
				ps.setInt(1, type);
				ps.setInt(2, sortid);
				ps.executeUpdate();
			}
			if (ps != null) ps.close();
			ps = conn.prepareStatement("UPDATE admin_orgs SET Type=?,Sortid=? WHERE Code=?");
			ps.setInt(1, type);
			ps.setInt(2, sortid);
			ps.setInt(3, org.getCode());
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void orderOrg(String ids, int code) throws SQLException {
		String[] ts = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE admin_orgs SET Sortid=? WHERE Code=?");
			for (String id : ts) {
				ps.setInt(1, index++);
				ps.setInt(2, Integer.parseInt(id));
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			this.removeS(KEY_ORGS + code);
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void removeOrg(AdminOrgs org) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM admin_orgs WHERE Code=?");
			ps.setInt(1, org.getCode());
			if (ps.executeUpdate() >= 1) {
				ps.close(); // close
				ps = conn.prepareStatement("UPDATE admin_orgs SET Sortid=(Sortid-1) WHERE Type=? AND Sortid>?");
				ps.setInt(1, org.getType());
				ps.setInt(2, org.getSortid());
				if (org.getType() <= 0) {
					ps.executeUpdate();
				} else if (ps.executeUpdate() <= 0 && org.getSortid() <= 1) {
					ps.close();
					this.updateOrg(conn, org.getType(), 1);
				}
			}
			conn.commit();
			this.removeS(KEY_ORGS + org.getCode());
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

}
