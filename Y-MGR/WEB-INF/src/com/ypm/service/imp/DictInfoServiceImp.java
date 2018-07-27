package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.DictInfoService;
import com.ypm.util.AUtils;

public class DictInfoServiceImp extends AConfig implements DictInfoService {

	private static final String TBL_DICT_MENU = "dict_menu";

	private static final String TBL_DICT_DEFS = "dict_defs";

	private static final String TBL_DICT_INFO = "dict_info";

	private static final String TBL_DICT_USER = "dict_user";

	private static String getDicTable(int type) {
		if (type <= 0) {
			return TBL_DICT_DEFS;
		} else if (type == 1) {
			return TBL_DICT_INFO;
		} else {
			return TBL_DICT_USER;
		}
	}

	protected void checkSQL() {
	}

	public String findDictInfoByDef(String sid, String id) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String str = null;
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT B.Value FROM dict_menu AS A INNER JOIN " + TBL_DICT_DEFS + " AS B ON A.Sid=B.Sid WHERE A.SNo=? AND B.Id=?");
			ps.setString(1, sid);
			ps.setString(2, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				str = rs.getString(1);
			}
			rs.close();
			return str;
		} catch (Exception e) {
			return null;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public DictMenu findDictMenuBySid(int sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			DictMenu m = null;
			ps = conn.prepareStatement("SELECT Sid,Tid,Sortid,SNo,Name,Type,Leaf,Losk FROM " + TBL_DICT_MENU + " WHERE Sid=?");
			ps.setInt(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				m = new DictMenu();
				m.setSid(rs.getInt(1));
				m.setTid(rs.getInt(2));
				m.setSortid(rs.getInt(3));
				m.setSNo(rs.getString(4));
				m.setName(rs.getString(5));
				m.setType(rs.getInt(6));
				m.setLosk(rs.getInt(8));
			}
			rs.close();
			return m;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isDictMenuBySid(String sno) {
		return JPrepare.isExists("SELECT Sid FROM " + TBL_DICT_MENU + " WHERE SNo=?", sno);
	}

	public boolean isDictMenuByTid(int tid) {
		return JPrepare.isExists("SELECT Sid FROM " + TBL_DICT_MENU + " WHERE Tid=?", tid);
	}

	public boolean isDictInfoByName(String sid, String name) {
		DictMenu menu = this.getDictMenuBySid(sid);
		if (menu == null) {
			return true;
		} else if (DICT_DEFS == menu.getType()) {
			return JPrepare.isExists("SELECT Sid FROM " + TBL_DICT_DEFS + " WHERE Sid=? AND Name=?", sid, name);
		} else if (DICT_INFO == menu.getType()) {
			return JPrepare.isExists("SELECT Sid FROM " + TBL_DICT_INFO + " WHERE Sid=? AND Name=?", sid, name);
		} else {
			return JPrepare.isExists("SELECT Sid FROM " + TBL_DICT_USER + " WHERE Sid=? AND Name=?", sid, name);
		}
	}

	/** Update Info */
	private void update(Connection conn, int sid, int leaf) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_DICT_MENU + " SET Leaf=? WHERE Sid=?");
			ps.setInt(1, leaf);
			ps.setInt(2, sid);
			ps.executeUpdate();
		} finally {
			if (ps != null)
				ps.close();
		}
	}

	public void saveDictMenu(DictMenu menu) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			if (menu.getSid() <= 0) {
				menu.setSid(this.getId(conn, TBL_DICT_MENU, "Sid"));
				ps = conn.prepareStatement("SELECT COUNT(*) FROM " + TBL_DICT_MENU + " WHERE Tid=? AND Type=?");
				ps.setInt(1, menu.getTid());
				ps.setInt(2, menu.getType());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					menu.setSortid(rs.getInt(1) + 1);
				} else {
					menu.setSortid(1);
				}
				rs.close();
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_DICT_MENU + " (Sid,Tid,Sortid,SNo,Name,Type,Leaf,Losk) VALUES (?,?,?,?,?,?,?,0)");
				ps.setInt(1, menu.getSid());
				ps.setInt(2, menu.getTid());
				ps.setInt(3, menu.getSortid());
				ps.setString(4, menu.getSNo());
				ps.setString(5, menu.getName());
				ps.setInt(6, menu.getType());
				ps.setInt(7, 1);
				if (ps.executeUpdate() > 0) {
					this.update(conn, menu.getTid(), 0);
				}
			} else {
				ps = JPrepare.prepareStatement(conn, "SELECT Sid,SNo,Name,Losk FROM " + TBL_DICT_MENU + " WHERE Sid=?");
				ps.setInt(1, menu.getSid());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					String sno = rs.getString(2);
					this.deletes(sno, KEY_DICT);
					if (rs.getInt(4) == 1) {
						menu.setSNo(sno);
					} else {
						rs.updateString(2, menu.getSNo());
					}
					rs.updateString(3, menu.getName());
					rs.updateRow();
				}
				rs.close();
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void orderDictMenu(String ids) throws SQLException {
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_DICT_MENU + " SET Sortid=? WHERE Sid=?");
			for (Long sid : set) {
				ps.setInt(1, index++);
				ps.setInt(2, sid.intValue());
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

	public void removeDictMenu(DictMenu m) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM " + TBL_DICT_MENU + " WHERE Sid=? AND Losk=?");
			ps.setInt(1, m.getSid());
			ps.setInt(2, 0);
			if (ps.executeUpdate() > 0) {
				ps.close();
				if (DICT_INFO == m.getType()) {
					ps = conn.prepareStatement("DELETE FROM " + TBL_DICT_INFO + " WHERE Sid=?");
				} else {
					ps = conn.prepareStatement("DELETE FROM " + TBL_DICT_USER + " WHERE Sid=?");
				}
				ps.setInt(1, m.getSid());
				ps.executeUpdate();
				ps.close();
				ps = conn.prepareStatement("UPDATE " + TBL_DICT_MENU + " SET Sortid=Sortid-1 WHERE Type=? AND Tid=? AND Sortid>=?");
				ps.setInt(1, m.getType());
				ps.setInt(2, m.getTid());
				ps.setInt(3, m.getSortid());
				if (ps.executeUpdate() <= 0 && m.getSortid() <= 1) {
					this.update(conn, m.getTid(), 1);
				} // 删除缓存文件
				this.deletes(m.getSNo(), KEY_DICT);
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findDictChildrens(int type, int sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			AjaxInfo json = AjaxInfo.getArray();
			ps = conn.prepareStatement("SELECT Sid,Name,Leaf FROM " + TBL_DICT_MENU + " WHERE Type=? AND Tid=? ORDER BY Sortid ASC");
			ps.setInt(1, type);
			ps.setInt(2, sid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getInt(1));
				json.append("text", rs.getString(2));
				if (rs.getInt(3) == 0) {
					json.append("leaf", false);
				} else {
					json.append("leaf", true);
				}
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findDictInfsBySid(int type, int sid) {
		AjaxInfo json = AjaxInfo.getArray();
		if (sid <= 0) {
			return json;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			Map<String, String> map = this.getDefault();
			conn = JPrepare.getConnection();
			if (DICT_INFO == type) {
				ps = conn.prepareStatement("SELECT Id,Sid,Sortid,Name,Value,Remark FROM " + TBL_DICT_INFO + " WHERE Sid=? ORDER BY Sortid ASC");
			} else {
				ps = conn.prepareStatement("SELECT Id,Sid,Sortid,Name,Value,Remark FROM " + TBL_DICT_USER + " WHERE Sid=? ORDER BY Sortid ASC");
			}
			ps.setInt(1, sid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("DICTID", rs.getLong(1));
				json.append("DICTSID", rs.getInt(2));
				json.append("SORTID", rs.getInt(3));
				json.append("REFID", rs.getString(4));
				json.append("REFVALUE", rs.getString(5));
				json.append("DEFCHECK", map.get(rs.getString(6)));
			}
			rs.close();
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findDictOrderBySid(int type, int sid) {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			if (DICT_INFO == type) {
				ps = conn.prepareStatement("SELECT Id,Value FROM " + TBL_DICT_INFO + " WHERE Sid=? ORDER BY Sortid ASC");
			} else {
				ps = conn.prepareStatement("SELECT Id,Value FROM " + TBL_DICT_USER + " WHERE Sid=? ORDER BY Sortid ASC");
			}
			ps.setInt(1, sid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getInt(1));
				json.append("text", rs.getString(2));
				json.append("leaf", false);
			}
			rs.close();
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo getDictInfoBySid(String sid) {
		DictMenu dm = this.getDictMenuBySid(sid);
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			if (DICT_INFO == dm.getType()) {
				ps = conn.prepareStatement("SELECT Id,Name,Value FROM " + TBL_DICT_INFO + " WHERE Sid=? ORDER BY Sortid ASC");
			} else {
				ps = conn.prepareStatement("SELECT Id,Name,Value FROM " + TBL_DICT_USER + " WHERE Sid=? ORDER BY Sortid ASC");
			}
			ps.setInt(1, dm.getSid());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(2));
				json.append("text", rs.getString(3));
				json.append("leaf", true);
			}
			rs.close();
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo getDictInfsBySid(String sid) {
		AjaxInfo json = AjaxInfo.getArray();
		AjaxFile f = AjaxFile.get(KEY_DICT, sid.toLowerCase());
		try {
			if (f.isFailed()) {
				this.setDictInfo(f, json, sid);
			} else if (f.isExpired()) {
				json.setBody(f.getBody());
				this.execute(() -> setDictInfo(f, sid));
			} else {
				json.setBody(f.getBody());
			}
		} catch (Exception e) {
			// Ignored
		}
		return json;
	}

	/** Add DictInfo for json */
	private void setDictInfo(AjaxFile file, String sid) {
		try {
			this.setDictInfo(file, null, sid);
		} catch (Exception e) {
			// Ignored
		}
	}

	/** Add DictInfo for json */
	private void setDictInfo(AjaxFile file, AjaxInfo json, String sid) throws SQLException {
		DictMenu m = this.getDictMenuBySid(sid);
		if (m == null) {
			return;
		}
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			if (json == null) {
				json = AjaxInfo.getArray();
			}
			if (m.getType() <= DICT_DEFS) {
				ps = conn.prepareStatement("SELECT Id,Name FROM " + TBL_DICT_DEFS + " WHERE Sid=? ORDER BY Sortid ASC");
				ps.setInt(1, m.getSid());
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					json.formater();
					json.append("REFID", rs.getString(1));
					json.append("REFVALUE", rs.getString(2));
				}
				rs.close();
			} else if (m.getType() == DICT_INFO) {
				ps = conn.prepareStatement("SELECT Id,Name,Value,Remark FROM " + TBL_DICT_INFO + " WHERE Sid=? ORDER BY Sortid ASC");
				ps.setInt(1, m.getSid());
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					json.formater();
					json.append("ID", rs.getInt(1));
					json.append("SNO", m.getSNo());
					json.append("SORTID", index++);
					json.append("REFID", rs.getString(2));
					json.append("REFVALUE", rs.getString(3));
					json.append("DEFCHECK", rs.getString(4));
				}
				rs.close();
			} else {
				ps = conn.prepareStatement("SELECT Id,Name,Value,Remark  FROM " + TBL_DICT_USER + " WHERE Sid=? ORDER BY Sortid ASC");
				ps.setInt(1, m.getSid());
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					json.formater();
					json.append("ID", rs.getInt(1));
					json.append("SNO", m.getSNo());
					json.append("SORTID", index++);
					json.append("REFID", rs.getString(2));
					json.append("REFVALUE", rs.getString(3));
					json.append("DEFCHECK", rs.getString(4));
				}
				rs.close();
			} // 写入缓存
			file.write(json);
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public DictInfo findDictInfoBySid(int type, long id) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			DictInfo info = null;
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT A.Sid,A.SNo,B.Id,B.Sortid,B.Name,B.Value,B.Remark FROM " + TBL_DICT_MENU + " AS A INNER JOIN ");
			if (DICT_INFO == type) {
				sql.append(TBL_DICT_INFO);
			} else {
				sql.append(TBL_DICT_USER);
				type = DICT_USER;
			}
			ps = conn.prepareStatement(sql.append(" AS B ON A.Sid=B.Sid WHERE B.Id=?").toString());
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				info = new DictInfo();
				info.setType(type);
				info.setSid(rs.getInt(1));
				info.setSNo(rs.getString(2));
				info.setId(rs.getString(3));
				info.setSortid(rs.getInt(4));
				info.setName(rs.getString(5));
				info.setValue(rs.getString(6));
				info.setRemark(rs.getString(7));
			}
			rs.close();
			return info;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public Map<String, String> getDictBySSid(String sid) {
		return this.getDictInfoBySSid(sid);
	}

	public boolean isDictInfoByName(int type, int sid, String name) {
		if (DICT_INFO == type) {
			return JPrepare.isExists("SELECT Sid FROM " + TBL_DICT_INFO + " WHERE Sid=? AND Name=?", sid, name);
		} else {
			return JPrepare.isExists("SELECT Sid FROM " + TBL_DICT_USER + " WHERE Sid=? AND Name=?", sid, name);
		}
	}

	private void saveDictByDefs(DictInfo info, String fix, int s) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (info.getSortid() <= 0) {
				info.setSortid(this.getSortid(conn, "SELECT COUNT(Id) FROM " + TBL_DICT_DEFS + " WHERE Sid=?", info.getSid()));
			} // select type save
			if (info.getId() == null) {
				info.setId(this.getSid(conn, fix, s, "SELECT MAX(Id) FROM " + TBL_DICT_DEFS + " WHERE Sid=?", info.getSid()));
			} else {
				ps = conn.prepareStatement("UPDATE " + TBL_DICT_DEFS + " SET Name=?,Value=?,Remark=? WHERE Sid=? AND Id=?");
				ps.setString(1, info.getName());
				ps.setString(2, info.getValue());
				ps.setString(3, info.getRemark());
				ps.setInt(4, info.getSid());
				ps.setString(5, info.getId());
				if (ps.executeUpdate() >= 1) {
					return;
				}
				ps.close();
			}
			ps = conn.prepareStatement("INSERT INTO " + TBL_DICT_DEFS + " (Id,Sid,Sortid,Name,Value,Remark) VALUES (?,?,?,?,?,?)");
			ps.setString(1, info.getId());
			ps.setInt(2, info.getSid());
			ps.setInt(3, info.getSortid());
			ps.setString(4, info.getName());
			ps.setString(5, info.getValue());
			ps.setString(6, info.getRemark());
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private void saveDictByInfo(DictInfo info, String fix, int s) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (info.getSortid() <= 0) {
				info.setSortid(this.getSortid(conn, "SELECT COUNT(Id) FROM " + TBL_DICT_INFO + " WHERE Sid=?", info.getSid()));
			} // select type save
			if (info.getId() == null) {
				int id = this.getId(conn, TBL_DICT_INFO, "Id");
				ps = conn.prepareStatement("INSERT INTO " + TBL_DICT_INFO + " (Id,Sid,Sortid,Name,Value,Remark) VALUES (?,?,?,?,?,?)");
				ps.setInt(1, id);
				ps.setInt(2, info.getSid());
				ps.setInt(3, info.getSortid());
				ps.setString(4, info.getName());
				ps.setString(5, info.getValue());
				ps.setString(6, info.getRemark());
			} else {
				ps = conn.prepareStatement("UPDATE " + TBL_DICT_INFO + " SET Name=?,Value=?,Remark=? WHERE Id=?");
				ps.setString(1, info.getName());
				ps.setString(2, info.getValue());
				ps.setString(3, info.getRemark());
				ps.setInt(4, AUtils.toInt(info.getId()));
			}
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private void saveDictByUser(DictInfo info, String fix, int s) throws SQLException {
	}

	public void saveDictInfo(DictInfo info) throws SQLException {
		this.saveDictInfo(info, "1", 5);
	}

	public void saveDictInfo(DictInfo info, String fix) throws SQLException {
		this.saveDictInfo(info, fix, 5);
	}

	public void saveDictInfo(DictInfo info, String fix, int s) throws SQLException {
		if (info.getSid() <= 0) {
			DictMenu m = this.getDictMenuBySid(info.getSNo());
			info.setSid(m.getSid());
			info.setType(m.getType());
			info.setSNo(m.getSNo());
		} // save to data
		switch (info.getType()) {
		case DICT_DEFS:
			this.saveDictByDefs(info, fix, s);
			break;
		case DICT_INFO:
			this.saveDictByInfo(info, fix, s);
			break;
		default:
			this.saveDictByUser(info, fix, s);
		}
		this.deletes(info.getSNo(), KEY_DICT);
	}

	public void orderDictInfo(int sid, String ids) throws SQLException {
		DictMenu m = this.findDictMenuBySid(sid);
		if (m == null || ids == null)
			return; // 未找到菜单
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			ps = conn.prepareStatement(sql.append("UPDATE ").append(getDicTable(m.getType())).append(" SET Sortid=? WHERE Sid=? AND Id=?").toString());
			for (Long id : set) {
				ps.setInt(1, index++);
				ps.setInt(2, m.getSid());
				ps.setLong(3, id.longValue());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			this.deletes(m.getSNo(), KEY_DICT);
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	/** 删除系统字典条词 */
	public void removeDictInfo(DictMenu m, String id) throws SQLException {
		this.removeDictInfo(m.getType(), m.getSid(), m.getSNo(), id);
	}

	public void removeDictInfo(int type, String ids) throws SQLException {
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			if (DICT_INFO == type) {
				ps = conn.prepareStatement("DELETE FROM " + TBL_DICT_INFO + " WHERE Id=?");
			} else {
				ps = conn.prepareStatement("DELETE FROM " + TBL_DICT_USER + " WHERE Id=?");
			} // delete id
			for (Long id : set) {
				ps.setLong(1, id.longValue());
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

	public void removeDictInfo(int type, int sid, String sno, String id) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int sortid = -1;
			String table = getDicTable(type);
			StringBuilder sb = new StringBuilder();
			ps = JPrepare.prepareStatement(conn, sb.append("SELECT Id,Sid,Sortid FROM ").append(table).append(" WHERE Sid=? AND Id=?").toString());
			ps.setInt(1, sid);
			if (type <= 0) {
				ps.setString(2, id);
			} else {
				ps.setInt(2, Integer.parseInt(id));
			}
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				sortid = rs.getInt(3);
				rs.deleteRow();
			}
			rs.close();
			if (sortid > -1) {
				ps.close();
				sb.setLength(0);
				ps = conn.prepareStatement(sb.append("UPDATE ").append(table).append(" SET Sortid=(Sortid-1) WHERE Sid=? AND Sortid>=?").toString());
				ps.setInt(1, sid);
				ps.setInt(2, sortid);
				ps.executeUpdate();
				this.deletes(sno, KEY_DICT);
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
