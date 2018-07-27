package com.ypm.service.imp;

import java.sql.*;
import java.util.*;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.AdminMenuService;
import com.ypm.service.DictInfoService;
import com.ypm.util.AUtils;

public class AdminMenuServiceImp extends AConfig implements AdminMenuService {

	private static final String MENU_DIR = "menu";

	private static final String MENU_FIX[] = {"S", "U"};

	private DictInfoService dictInfoService;

	protected void checkSQL() {
	}

	public DictInfoService getDictInfoService() {
		return dictInfoService;
	}

	public void setDictInfoService(DictInfoService dictInfoService) {
		this.dictInfoService = dictInfoService;
	}

	public AjaxInfo getIcons() {
		AjaxInfo json = AjaxInfo.getArray();
		AjaxFile file = AjaxFile.get("icons");
		try {
			if (file.isFailed()) {
				this.setIcons(file, json);
			} else if (file.isExpired()) {
				json.setBody(file.getBody());
				this.execute(() -> setIcons(file));
			} else {
				json.setBody(file.getBody());
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			file.close();
		}
		return json;
	}

	private void setIcons(AjaxFile file) {
		try {
			this.setIcons(file, null);
		} catch (Exception e) {
			// Ignored
		}
	}
	/** 从数据库中加载 */
	private void setIcons(AjaxFile file, AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			if (json == null) json = AjaxInfo.getArray();
			ResultSet rs = stmt.executeQuery("SELECT Sid FROM comm_icon ORDER BY Sortid ASC");
			while (rs.next()) {
				json.formater();
				json.append("name", rs.getString(1));
			}
			rs.close();
			file.write(json);
		} finally {
			JPrepare.close(stmt, conn);
		}
	}

	private Map<String, List<AdminMenu>> findMenuListByAll() throws SQLException {
		Map<String, List<AdminMenu>> map = new HashMap<String, List<AdminMenu>>();
		Connection conn = JPrepare.getConnection();
		Statement stmt = null;
		try {
			int tid = -1; // 类别信息
			ArrayList<AdminMenu> ls = null;
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Id,Tid,Sortid,Name,Module,Remark,Icon,Leaf FROM comm_menu ORDER BY Tid ASC,Sortid ASC");
			while (rs.next()) {
				if (tid == rs.getInt(2)) {
					// Ignored
				} else {
					if (ls != null) {
						ls.trimToSize();
						map.put(String.valueOf(tid), ls);
					} // New Array
					tid = rs.getInt(2);
					ls = new ArrayList<AdminMenu>();
				} // Add Info
				AdminMenu m = new AdminMenu();
				m.setId(rs.getInt(1));
				m.setTid(rs.getInt(2));
				m.setSortid(rs.getInt(3));
				m.setName(rs.getString(4));
				m.setModule(rs.getString(5));
				m.setRemark(rs.getString(6));
				m.setIcon(rs.getString(7));
				m.setLeaf(rs.getInt(8));
				ls.add(m);
			}
			rs.close();
			if (ls != null) {
				ls.trimToSize();
				map.put(String.valueOf(tid), ls);
			}
			return map;
		} finally {
			JPrepare.close(stmt, conn);
		}
	}

	public AjaxInfo findMenuListById(String id) {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Id,Name,Icon,Leaf FROM comm_menu WHERE Tid=? ORDER BY Sortid ASC");
			ps.setInt(1, AUtils.toInt(id));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getInt(1));
				json.append("text", rs.getString(2));
				if (rs.getString(3) == null) {
					json.append("iconCls", "");
				} else {
					json.append("iconCls", "mnu-", rs.getString(3));
				}
				if (rs.getInt(4) == 1) {
					json.append("leaf", true);
				} else {
					json.append("leaf", false);
				}
				json.append("nodeType", "async");
			}
			rs.close();
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}
	/** 菜单选项信息 */
	private Set<String> getMenuListBySet(String id) {
		Set<String> set = new HashSet<String>();
		if (id == null || id.equals("all")) return set;
		String info = this.getDictInfoService().findDictInfoByDef(MENUBAR_TEMPLATE, id);
		if (info == null) return set;
		String[] ts = this.toSplit(info);
		for (String str : ts) {
			set.add(str);
		}
		return set;
	}

	public AjaxInfo getMenuListByTid(String id) {
		AjaxInfo json = AjaxInfo.getArray();
		if (id == null || id.length() < 2) id = "All";
		AjaxFile file = AjaxFile.get(MENU_DIR, MENU_FIX[0], id);
		try {
			if (file.isFailed()) {
				this.setMenuBySys(file, json, id);
			} else {
				json.setBody(file.getBody());
				if (file.isExpired()) {
					final String s = id;
					this.execute(() -> setMenuBySys(file, s));
				}
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			file.close();
		}
		return json;
	}

	public AjaxInfo getMenuListByUid(String id) {
		AjaxInfo json = AjaxInfo.getArray();
		AjaxFile file = AjaxFile.get(MENU_DIR, MENU_FIX[1], id);
		try {
			if (file.isFailed()) {
				this.setMenuByUid(file, json, id);
			} else {
				json.setBody(file.getBody());
				if (file.isExpired()) {
					this.execute(() -> setMenuByUid(file, id));
				}
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			file.close();
		}
		return json;
	}

	private void setMenuBySys(AjaxFile file, String id) {
		try {
			this.setMenuBySys(file, null, id);
		} catch (Exception e) {
			// Ignored
		}
	}

	private void setMenuBySys(AjaxFile file, AjaxInfo json, String id) throws SQLException {
		Map<String, List<AdminMenu>> map = this.findMenuListByAll();
		try {
			Set<String> set = this.getMenuListBySet(id);
			List<AdminMenu> ls = map.remove("0");
			if (json == null) json = AjaxInfo.getArray();
			this.setMenuBySys(json, map, set, ls);
			file.write(json);
		} finally {
			map.clear();
		}
	}
	/** for add */
	private void setMenuBySys(AjaxInfo json, Map<String, List<AdminMenu>> map, Set<String> set, List<AdminMenu> ls) {
		String key = null; // Temporary
		for (int i = 0, j = ls.size(); i < j; i++) {
			AdminMenu m = ls.get(i);
			key = String.valueOf(m.getId());
			json.formater();
			json.append("id", m.getId());
			json.append("text", m.getName());
			List<AdminMenu> fs = map.remove(key);
			if (fs == null) {
				json.append("leaf", true);
			} else {
				json.adds("children");
				this.setMenuBySys(json, map, set, fs);
				json.close();
			}
			if (set.remove(key)) {
				json.append("checked", true);
			} else {
				json.append("checked", false);
			}
			json.append("expanded", true);
			json.append("iconCls", m.getIcon());
		}
	}

	private void setMenuByUid(AjaxFile file, String id) {
		try {
			this.setMenuByUid(file, null, id);
		} catch (Exception e) {
			// Ignored
		}
	}

	private void setMenuByUid(AjaxFile file, AjaxInfo json, String id) throws SQLException {
		Map<String, List<AdminMenu>> map = this.findMenuListByAll();
		try {
			Set<String> set = this.getMenuListBySet(id);
			List<AdminMenu> ls = map.remove("0");
			if (json == null) json = AjaxInfo.getArray();
			this.setMenuByUid(json, map, set, ls);
			file.write(json); // 处理缓存
		} finally {
			map.clear();
		}
	}
	/** for add */
	private void setMenuByUid(AjaxInfo json, Map<String, List<AdminMenu>> map, Set<String> set, List<AdminMenu> ls) {
		String key = null; // Temporary
		for (AdminMenu m : ls) {
			key = String.valueOf(m.getId());
			if (set.remove(key)) {
				json.formater();
				json.append("id", m.getId());
				json.append("module", m.getModule());
				json.append("text", m.getName());
				List<AdminMenu> fs = map.remove(key);
				if (fs == null) {
					json.append("leaf", true);
				} else {
					json.adds("children");
					this.setMenuByUid(json, map, set, fs);
					json.close();
				}
				json.append("iconCls", "mnu-", m.getIcon());
			} else {
				// Ignored
			}
		}
	}

	public AdminMenu findAdminMenuById(String id) {
		if (id == null) return null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			AdminMenu m = null;
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Id,Tid,Sortid,Name,Module,Remark,Icon,Leaf FROM comm_menu WHERE Id=?");
			ps.setInt(1, Integer.parseInt(id));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				m = new AdminMenu();
				m.setId(rs.getInt(1));
				m.setTid(rs.getInt(2));
				m.setSortid(rs.getInt(3));
				m.setName(rs.getString(4));
				m.setModule(rs.getString(5));
				m.setRemark(rs.getString(6));
				m.setIcon(rs.getString(7));
				m.setLeaf(rs.getInt(8));
			}
			rs.close(); return m;
		} catch (Exception e) {
			return null;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isMenuByTid(int tid) {
		return JPrepare.isExists("SELECT Id FROM comm_menu WHERE Tid=?", tid);
	}

	public void saveMenu(AdminMenu m) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (m.getId() <= 0) {
				if (m.getTid() > 0) {
					ps = JPrepare.prepareStatement(conn, "SELECT Id,Tid,Leaf FROM comm_menu WHERE Id=?");
					ps.setInt(1, m.getTid());
					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						rs.updateInt(3, 0);
						rs.updateRow();
					} else {
						throw new SQLException("Parent menu is not found!");
					}
					rs.close(); ps.close();
				} else {
					m.setTid(0);
				} // 获取排序编号
				ps = conn.prepareStatement("SELECT COUNT(Id) FROM comm_menu WHERE Tid=?");
				ps.setInt(1, m.getTid());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					m.setSortid(rs.getInt(1) + 1);
				} else {
					m.setSortid(1);
				} // 获取菜单编号
				rs.close(); ps.close();
				ps = conn.prepareStatement("SELECT MAX(Id) FROM comm_menu");
				rs = ps.executeQuery();
				if (rs.next()) {
					m.setId(rs.getInt(1) + 1);
				} else {
					m.setId(1);
				} // 插入新数据
				rs.close(); ps.close();
				ps = conn.prepareStatement("INSERT INTO comm_menu (Id,Tid,Sortid,Name,Module,Remark,Icon,Leaf) VALUES (?,?,?,?,?,?,?,?)");
				ps.setInt(1, m.getId());
				ps.setInt(2, m.getTid());
				ps.setInt(3, m.getSortid());
				ps.setString(4, m.getName());
				ps.setString(5, m.getModule());
				ps.setString(6, m.getRemark());
				ps.setString(7, m.getIcon());
				ps.setInt(8, 1);
				ps.executeUpdate();
			} else {
				ps = JPrepare.prepareStatement(conn, "SELECT Id,Tid,Sortid,Name,Module,Remark,Icon,Leaf FROM comm_menu WHERE Id=?");
				ps.setInt(1, m.getId());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					m.setTid(rs.getInt(2));
					rs.updateString(4, m.getName());
					rs.updateString(5, m.getModule());
					rs.updateString(6, m.getRemark());
					rs.updateString(7, m.getIcon());
					rs.updateRow();
				} else {
					throw new SQLException("The menu is not found!");
				}
				rs.close();
			} // 删除缓存信息
			this.deletes(MENU_DIR);
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void moveMenu(String id, int tid, int sortid) throws SQLException {
		AdminMenu m = this.findAdminMenuById(id);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			if (m.getTid() == tid) {
				if (m.getSortid() > sortid) {
					ps = conn.prepareStatement("UPDATE comm_menu SET Sortid=(Sortid+1) WHERE Tid=? AND Sortid<? AND Sortid>=?");
				} else {
					ps = conn.prepareStatement("UPDATE comm_menu SET Sortid=(Sortid-1) WHERE Tid=? AND Sortid>? AND Sortid<=?");
				}
				ps.setInt(1, m.getTid());
				ps.setInt(2, m.getSortid());
				ps.setInt(3, sortid);
				ps.executeUpdate();
			} else {
				ps = conn.prepareStatement("UPDATE comm_menu SET Sortid=(Sortid-1) WHERE Tid=? AND Sortid>=?");
				ps.setInt(1, m.getTid());
				ps.setInt(2, m.getSortid());
				if (ps.executeUpdate() <= 0 && m.getSortid() <= 1) {
					ps.close();
					ps = conn.prepareStatement("UPDATE comm_menu SET Leaf=? WHERE Id=?");
					ps.setInt(1, 1);
					ps.setInt(2, m.getTid());
					ps.executeUpdate();
				}
				ps.close();
				ps = conn.prepareStatement("UPDATE comm_menu SET Sortid=(Sortid+1) WHERE Tid=? AND Sortid>=?");
				ps.setInt(1, tid);
				ps.setInt(2, sortid);
				ps.executeUpdate();
			}
			ps.close(); // update
			ps = conn.prepareStatement("UPDATE comm_menu SET Tid=?,Sortid=? WHERE Id=?");
			ps.setInt(1, tid);
			ps.setInt(2, sortid);
			ps.setInt(3, m.getId());
			ps.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void orderMenu(int tid, String ids) throws SQLException {
		String[] ts = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE comm_menu SET Sortid=? WHERE Id=?");
			for (String id : ts) {
				ps.setInt(1, index++);
				ps.setInt(2, AUtils.toInt(id));
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			this.deletes(MENU_DIR);
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void removeMenu(int id) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int tid = 0, sortid = 0;
			ps = JPrepare.prepareStatement(conn, "SELECT Id,Tid,Sortid FROM comm_menu WHERE Id=?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				tid = rs.getInt(2);
				sortid = rs.getInt(3);
				rs.deleteRow();
			}
			rs.close();
			if (tid > 0) {
				ps.close();
				ps = conn.prepareStatement("UPDATE comm_menu SET Sortid=(Sortid-1) WHERE Tid=? AND Sortid>=?");
				ps.setInt(1, tid);
				ps.setInt(2, sortid);
				ps.executeUpdate();
				this.deletes(MENU_DIR);
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public DictInfo getTemplateById(String id) {
		return this.findDictInfoBySid(MENUBAR_TEMPLATE, id);
	}

	public boolean isTemplateByName(String name) {
		return this.getDictInfoService().isDictInfoByName(MENUBAR_TEMPLATE, name);
	}

	public void saveTemplate(DictInfo info) throws SQLException {
		info.setSNo(MENUBAR_TEMPLATE);
		info.setType(DICT_DEFS); // for default
		this.getDictInfoService().saveDictInfo(info, null, 3);
		this.deletes(MENU_DIR, MENU_FIX, info.getId());
	}

	public void removeTemplate(String id) throws SQLException {
		DictMenu menu = this.getDictMenuBySid(MENUBAR_TEMPLATE);
		if (menu == null) {
			// Ignored
		} else {
			this.getDictInfoService().removeDictInfo(menu, id);
			this.deletes(MENU_DIR, MENU_FIX, id);
		}
	}

}
