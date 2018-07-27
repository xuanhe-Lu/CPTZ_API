package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import com.ypm.bean.AdminDept;
import com.ypm.bean.AjaxFile;
import com.ypm.bean.AjaxInfo;
import com.ypm.data.JPrepare;
import com.ypm.service.AdminDeptService;

public class AdminDeptServiceImp extends AConfig implements AdminDeptService {

	private static final String KEY_DEPT = "dept";

	protected void checkSQL() {
	}

	public AdminDept findDeptById(String id) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			AdminDept d = null;
			ps = conn.prepareStatement("SELECT Id,Name,Remark,Header,Viewer,Heades,Viewes FROM admin_dept WHERE Id=?");
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				d = new AdminDept();
				d.setId(rs.getString(1));
				d.setName(rs.getString(2));
				d.setRemark(rs.getString(3));
				d.setHeader(rs.getString(4));
				d.setViewer(rs.getString(5));
			}
			rs.close(); return d;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findDeptByAll(int offset, int max) {
		long total = JPrepare.getTotal("admin_dept");
		AjaxInfo json = AjaxInfo.getBean();
		json.setTotal(total);
		if (total <= offset) {
			return json.close();
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement(JPrepare.getQuery("SELECT Id,Name,Remark,Header,Viewer,Heades,Viewes FROM admin_dept ORDER BY Id ASC", offset, max));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("ID", rs.getString(1));
				json.append("NAME", rs.getString(2));
				json.append("REMARK", rs.getString(3));
				json.append("HEADER", rs.getString(4));
				json.append("VIEWER", rs.getString(5));
				json.append("HEADES", rs.getString(6));
				json.append("VIEWES", rs.getString(7));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo getDetpChildrens() {
		AjaxInfo json = AjaxInfo.getArray();
		AjaxFile file = AjaxFile.get(KEY_DEPT);
		try {
			if (file.isFailed()) {
				this.setAdminDetps(file, json);
			} else {
				json.setBody(file.getBody());
				if (file.isExpired()) {
					this.execute(() -> setAdminDetps(file));
				}
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			file.close();
		}
		return json;
	}

	private void setAdminDetps(AjaxFile file) {
		try {
			this.setAdminDetps(file, null);
		} catch (Exception e) {
			// Ignored
		}
	}

	private void setAdminDetps(AjaxFile file, AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (json == null) json = AjaxInfo.getArray();
			ps = conn.prepareStatement("SELECT Id,Name FROM admin_dept ORDER BY Id ASC");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
			}
			rs.close();
			file.write(json);
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public Map<String, String> getAdminDetpAll() {
		return this.getMapStr(KEY_DEPT, "SELECT Id,Name FROM admin_dept ORDER BY Id ASC");
	}

	public boolean isDeptByName(String name) {
		return JPrepare.isExists("SELECT Id FROM admin_dept WHERE Name=?", name);
	}

	public void saveDept(AdminDept d) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (d.getId() == null) {
				d.setId(this.getSid(conn, "DPT", 3, "SELECT MAX(Id) FROM admin_dept"));
				ps = conn.prepareStatement("INSERT INTO admin_dept (Name,Remark,Header,Heades,Viewer,Viewes,Id) VALUES (?,?,?,?,?,?,?)");
			} else {
				ps = conn.prepareStatement("UPDATE admin_dept SET Name=?,Remark=?,Header=?,Heades=?,Viewer=?,Viewes=? WHERE Id=?");
			} // set Parameter
			ps.setString(1, d.getName());
			ps.setString(2, d.getRemark());
			ps.setString(3, d.getHeader());
			ps.setString(4, toUser(d.getHeader()));
			ps.setString(5, d.getViewer());
			ps.setString(6, toUser(d.getViewer()));
			ps.setString(7, d.getId());
			if (ps.executeUpdate() >= 1) {
				this.deletes(KEY_DEPT);
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void removeDept(String ids) throws SQLException {
		String[] ts = toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM admin_dept WHERE Id=?");
			for (String id : ts) {
				ps.setString(1, id);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			this.deletes(KEY_DEPT);
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

}
