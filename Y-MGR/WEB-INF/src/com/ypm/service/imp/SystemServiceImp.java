package com.ypm.service.imp;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.commons.collect.MapMaker;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.SystemService;
import com.ypm.util.*;

public class SystemServiceImp extends AConfig implements SystemService {

	private Map<String, String> cache;

	public SystemServiceImp() {
		this.cache = new MapMaker().concurrencyLevel(32).expiration(600000).makeMap();
	}

	protected void checkSQL() {
	}

	/** 提取字段信息 */
	private String getFields(String fid) {
		String str = this.cache.get(fid);
		if (str != null) {
			return str;
		}
		List<FieldInfo> ls = this.getFieldInfoById(fid);
		int len = (ls == null) ? 0 : ls.size();
		if (len <= 0) {
			return ""; // None
		}
		boolean isNotPK = true;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			FieldInfo info = ls.get(i);
			if (info.isPkid()) {
				isNotPK = false;
				sb.insert(0, "'").insert(0, info.getKey()).insert(0, "'");
			} else if (info.getShow() == 1) {
				sb.append(",'").append(info.getKey()).append("'");
			}
		} // 检测主键加载
		if (isNotPK)
			sb.insert(0, "'ID'");
		str = sb.toString();
		this.cache.put(fid, str);
		return str;
	}

	/** 显示字段信息 */
	private String getheader(String fid) {
		StringBuilder sb = new StringBuilder();
		String key = sb.append("At").append(fid).toString();
		String str = this.cache.get(key);
		if (str != null) {
			return str;
		}
		List<FieldInfo> ls = this.getFieldInfoById(fid);
		int len = (ls == null) ? 0 : ls.size();
		if (len <= 0) {
			return ""; // None
		}
		sb.setLength(0);
		Map<String, String> ms = new HashMap<String, String>();
		for (int i = 0; i < len; i++) {
			FieldInfo info = ls.get(i);
			if (info.getShow() != 1)
				continue;
			sb.append(",{header:\"").append(info.getNice()).append("\"");
			sb.append(",dataIndex:\"").append(info.getKey()).append("\"");
			if (info.getType() == 2) {
				sb.append(",align:\"center\"");
			} else if (info.getType() == 3) {
				sb.append(",align:\"right\"");
			} // 宽度信息
			if (info.getWidth() > 25) {
				sb.append(",width:").append(info.getWidth());
			} else {
				sb.append(",id:\"").append(info.getKey()).append("\"");
			}
			if (info.getSortab() == 1) {
				sb.append(",sortable:true,menuDisabled:false");
			} else {
				sb.append(",menuDisabled:true");
			}
			str = info.getFormat();
			if (str != null) {
				Matcher m = Pattern.compile("([\\{,])([^:]+)\\:(\\w+)").matcher(str);
				while (m.find()) {
					ms.put(m.group(2), m.group(3));
				}
			}
			if (ms.size() >= 1) {
				boolean isTow = false;
				sb.append(",renderer:function(v,m,r,row,col,store){");
				Iterator<Entry<String, String>> it = ms.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> e = it.next();
					if (isTow) {
						sb.append("else if(v==\"").append(e.getKey()).append("\"){");
					} else {
						sb.append("if(v==\"").append(e.getKey()).append("\"){");
						isTow = true;
					}
					sb.append("m.style+=\"color:#").append(e.getValue()).append("\";");
					sb.append("}");
				}
				sb.append("return v;}");
				ms.clear();
			}
			sb.append(",fixed:false}");
		}
		str = sb.toString();
		this.cache.put(key, str);
		return str;
	}

	public String getFields(String body, String key) {
		String fid = null, type = null;
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("<s:(\\w+)((\\s+)id=\"([\\w.]+)([^>]+))?>").matcher(body);
		while (m.find()) {
			type = m.group(1);
			if (m.group(4) == null) {
				fid = key;
			} else {
				fid = m.group(4).trim();
			} // 关联编号
			if (type.equalsIgnoreCase("fields")) {
				m.appendReplacement(sb, this.getFields(fid));
			} else if (type.equalsIgnoreCase("header")) {
				m.appendReplacement(sb, this.getheader(fid));
			} else {
				m.appendReplacement(sb, "");
			}
		} // 输出构建后信息
		return m.appendTail(sb).toString();
	}

	public void saveFieldMenu(FieldMenu menu) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			if (menu.getSid() <= 0) {
				menu.setSid(this.getId(conn, "field_menu", "Sid"));
				menu.setSortid(this.getSortid(conn, "SELECT COUNT(1) FROM field_menu WHERE Tid=?", menu.getTid()));
				ps = conn.prepareStatement("INSERT INTO field_menu (Sid,Tid,Sortid,SNo,Name,Title,Value,Leaf,Losk) VALUES (?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, menu.getSid());
				ps.setInt(2, menu.getTid());
				ps.setInt(3, menu.getSortid());
				ps.setString(4, menu.getSno());
				ps.setString(5, menu.getName());
				ps.setString(6, menu.getTitle());
				ps.setString(7, menu.getValue());
				ps.setInt(8, 1); // 词条
				ps.setInt(9, 0); // 开放
				ps.executeUpdate();
				if (menu.getTid() > 0) {
					ps.close();
					ps = conn.prepareStatement("UPDATE field_menu SET Leaf=? WHERE Sid=?");
					ps.setInt(1, 0); // 目录
					ps.setInt(2, menu.getTid());
					ps.executeUpdate();
				}
			} else {
				ps = conn.prepareStatement("UPDATE field_menu SET SNo=?,Name=?,Title=?,Value=? WHERE Sid=?");
				ps.setString(1, menu.getSno());
				ps.setString(2, menu.getName());
				ps.setString(3, menu.getTitle());
				ps.setString(4, menu.getValue());
				ps.setInt(5, menu.getSid());
				ps.executeUpdate();
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveFieldInfo(FieldMenu menu, String dbsrc) throws SQLException {
		String[] ts = VeStr.toSplit(dbsrc);
		int len = ts.length; // 可用数组
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE field_menu SET Name=?,Title=?,Value=? WHERE Sid=?");
			ps.setString(1, menu.getName());
			ps.setString(2, menu.getTitle());
			ps.setString(3, menu.getValue());
			ps.setInt(4, menu.getSid());
			ps.executeUpdate();
			if (len > 1 || ts[0].length() > 4) {
				ps.close();
				ps = conn.prepareStatement("UPDATE field_info SET Nice=?,Width=?,Display=?,Sortab=?,Export=?,Type=?,Format=? WHERE Sid=? AND Id=?");
				for (int i = 0; i < len; i++) {
					String[] s = ts[i].split(Constant.REG_EX, -1);
					if (s.length < 10)
						continue;
					ps.setString(1, s[2]);
					ps.setInt(2, AUtils.toInt(s[3]));
					ps.setInt(3, VeRule.getBoolean(s[5], 0));
					ps.setInt(4, VeRule.getBoolean(s[6], 0));
					ps.setInt(5, VeRule.getBoolean(s[7], 0));
					ps.setInt(6, AUtils.toInt(s[8]));
					ps.setString(7, VeStr.toStr(s[10]));
					ps.setInt(8, menu.getSid());
					ps.setInt(9, Integer.parseInt(s[0]));
					ps.addBatch();
				}
				ps.executeBatch();
			}
			conn.commit();
			this.cache.clear();
			this.clearField();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void findFieldInfoById(AjaxInfo json, int sid, String[] arr) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Sid,SNo,Name,Title,Value,Losk FROM field_menu WHERE Sid=?");
			ps.setInt(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				json.data(); // 加载基本数据
				json.append("SID", rs.getInt(1));
				json.append("SNO", rs.getString(2));
				json.append("NAME", rs.getString(3));
				json.append("TITLE", rs.getString(4));
				json.append("FNAME", rs.getString(5));
			} else {
				throw new SQLException("The dictionary classification was not found or has been deleted!");
			} // 加载列表数据
			rs.close();
			ps.close();
			json.close().adds("src");
			int t = 0, s = arr.length;
			ps = conn.prepareStatement("SELECT Id,Name,Nice,Width,Pkey,Display,Sortab,Export,Type,Format FROM field_info WHERE Sid=? ORDER BY Sortid ASC");
			ps.setInt(1, sid);
			rs = ps.executeQuery();
			while (rs.next()) {
				json.formates();
				json.append(rs.getInt(1));
				json.append(rs.getString(2));
				json.append(rs.getString(3));
				json.append(rs.getInt(4));
				if (rs.getBoolean(5)) {
					json.append("PKID");
				} else {
					json.append("--");
				}
				json.append(rs.getBoolean(6));
				json.append(rs.getBoolean(7));
				json.append(rs.getBoolean(8));
				json.append(t = rs.getInt(9));
				if (t > s || t < 0) {
					t = 0;
				}
				json.append(arr[t]);
				json.append(rs.getString(10));
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findMenuChildrens(int tid) {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			if (tid <= 0) {
				tid = 0;
			}
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Sid,Name,Leaf FROM field_menu WHERE Tid=? ORDER BY Sortid ASC");
			ps.setInt(1, tid);
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
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findInfoChildrens(int sid) {
		AjaxInfo json = AjaxInfo.getArray();
		if (sid <= 0) {
			return json;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Id,Nice FROM field_info WHERE Sid=? ORDER BY Sortid ASC");
			ps.setInt(1, sid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
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

	public FieldMenu findFieldMenuById(int sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			FieldMenu menu = null;
			ps = conn.prepareStatement("SELECT Sid,Tid,Sortid,SNo,Name,Title,Value,Losk FROM field_menu WHERE Sid=?");
			ps.setInt(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				menu = new FieldMenu();
				menu.setSid(rs.getInt(1));
				menu.setTid(rs.getInt(2));
				menu.setSortid(rs.getInt(3));
				menu.setSno(rs.getString(4));
				menu.setName(rs.getString(5));
				menu.setTitle(rs.getString(6));
				menu.setValue(rs.getString(7));
				menu.setLosk(rs.getInt(8));
			}
			rs.close();
			return menu;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isFieldMenuBySid(String sid) {
		return JPrepare.isExists("SELECT Sid FROM field_menu WHERE SNo=?", sid);
	}

	public boolean isFieldMenuByTid(int tid) {
		return JPrepare.isExists("SELECT Sid FROM field_menu WHERE Tid=?", tid);
	}

	public void moveFieldMenu(int sid, int tid, int sortid) throws SQLException {
		FieldMenu m = this.findFieldMenuById(sid);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			if (m.getTid() == tid) {
				if (m.getSortid() > sortid) {
					ps = conn.prepareStatement("UPDATE field_menu SET Sortid=(Sortid+1) WHERE Tid=? AND Sortid<? AND Sortid>=?");
				} else {
					ps = conn.prepareStatement("UPDATE field_menu SET Sortid=(Sortid-1) WHERE Tid=? AND Sortid>? AND Sortid<=?");
				}
				ps.setInt(1, m.getTid());
				ps.setInt(2, m.getSortid());
				ps.setInt(3, sortid);
				ps.executeUpdate();
			} else {
				ps = conn.prepareStatement("UPDATE field_menu SET Sortid=(Sortid-1) WHERE Tid=? AND Sortid>=?");
				ps.setInt(1, m.getTid());
				ps.setInt(2, m.getSortid());
				if (ps.executeUpdate() <= 0 && m.getSortid() <= 1) {
					ps.close();
					ps = conn.prepareStatement("UPDATE field_menu SET Leaf=? WHERE Sid=?");
					ps.setInt(1, 1);
					ps.setInt(2, m.getTid());
					ps.executeUpdate();
				}
				ps.close();
				ps = conn.prepareStatement("UPDATE field_menu SET Sortid=(Sortid+1) WHERE Tid=? AND Sortid>=?");
				ps.setInt(1, tid);
				ps.setInt(2, sortid);
				ps.executeUpdate();
			}
			ps.close(); // update
			ps = conn.prepareStatement("UPDATE field_menu SET Tid=?,Sortid=? WHERE Sid=?");
			ps.setInt(1, tid);
			ps.setInt(2, sortid);
			ps.setInt(3, m.getSid());
			ps.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void orderFieldMenu(String ids) throws SQLException {
		Set<Integer> set = BUtils.toInt(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE field_menu SET Sortid=? WHERE Sid=?");
			for (Integer id : set) {
				ps.setInt(1, index++);
				ps.setInt(2, id.intValue());
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

	public void orderFieldInfo(int sid, String ids) throws SQLException {
		Set<Integer> set = BUtils.toInt(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE field_info SET Sortid=? WHERE Id=? AND Sid=?");
			for (Integer id : set) {
				ps.setInt(1, index++);
				ps.setInt(2, id.intValue());
				ps.setInt(3, sid);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			this.cache.clear();
			this.clearField();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void removeFieldMenu(FieldMenu menu) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM field_info WHERE Sid=?");
			ps.setInt(1, menu.getSid());
			ps.executeUpdate();
			ps.close(); // 删除目录
			ps = conn.prepareStatement("DELETE FROM field_menu WHERE Sid=?");
			ps.setInt(1, menu.getSid());
			ps.executeUpdate();
			ps.close(); // 重建编号
			ps = conn.prepareStatement("UPDATE field_menu SET Sortid=(Sortid-1) WHERE Tid=? AND Sortid>?");
			ps.setInt(1, menu.getTid());
			ps.setInt(2, menu.getSortid());
			if (ps.executeUpdate() == 0 && menu.getSortid() <= 1) {
				ps.close(); // 改变状态
				ps = conn.prepareStatement("UPDATE field_menu SET Leaf=? WHERE Sid=?");
				ps.setInt(1, 1); // 词条
				ps.setInt(2, menu.getTid());
				ps.executeUpdate();
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
}
