package com.ypm.service.imp;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import org.commons.collect.MapMaker;
import org.commons.lang.StringUtils;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.SiteAreaService;
import com.ypm.util.GMTime;

public class SiteAreaServiceImp extends AConfig implements SiteAreaService {

	private static final String KEY_PROS = "Province";

	private static final String TBL_COMM_REGION = "comm_region";

	private Map<String, Map<String, SiteCity>> cache;

	public SiteAreaServiceImp() {
		this.cache = new MapMaker().concurrencyLevel(32).expiration(600000).makeMap(); // 10 分钟缓存时长
	}

	protected void checkSQL() {
	}

	public void saveRegion(RegionInfo info) throws Exception {
		info.setState(STATE_ENABLE);
		info.setTime(System.currentTimeMillis());
		SyncMap.getAll().send(SYS_A990, "sync", info).successfully();
		String code = info.getCode();
		int len = code.length(); // 编码长度
		int end = (len - 2), tj = (len / 2);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_REGION + " SET Code=?,CNA=?,CNB=?,ENA=?,ENB=?,ZipCode=?,TelCode=?,TelNum=?,State=?,Time=? WHERE Code=?");
			ps.setString(1, code);
			ps.setString(2, info.getCna());
			ps.setString(3, info.getCnb());
			ps.setString(4, info.getEna());
			ps.setString(5, info.getEnb());
			ps.setString(6, info.getZipCode());
			ps.setString(7, info.getTelCode());
			ps.setInt(8, info.getTelNum());
			ps.setInt(9, info.getState());
			ps.setLong(10, info.getTime());
			if (info.getSid() == null) {
				ps.setString(11, info.getCode());
				if (ps.executeUpdate() <= 0) {
					ps.close(); // add new
					ps = conn.prepareStatement("INSERT INTO " + TBL_COMM_REGION + " (Code,CNA,CNB,ENA,ENB,ZipCode,TelCode,TelNum,Tj,Leaf,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
					ps.setString(1, info.getCode());
					ps.setString(2, info.getCna());
					ps.setString(3, info.getCnb());
					ps.setString(4, info.getEna());
					ps.setString(5, info.getEnb());
					ps.setString(6, info.getZipCode());
					ps.setString(7, info.getTelCode());
					ps.setInt(8, info.getTelNum());
					ps.setInt(9, tj); // 层次
					ps.setInt(10, 1); // 页
					ps.setInt(11, info.getState());
					ps.setLong(12, info.getTime());
					ps.executeUpdate();
				}
				if (tj > 1) {
					ps.close(); // 更新节点状态
					ps = conn.prepareStatement("UPDATE " + TBL_COMM_REGION + " SET Leaf=?,Time=? WHERE Code=?");
					ps.setInt(1, 0);
					ps.setLong(2, info.getTime());
					ps.setString(3, code.substring(0, end));
					ps.executeUpdate();
				}
			} else {
				ps.setString(11, info.getSid());
				if (ps.executeUpdate() <= 0) {
					return;
				} else if (info.getSid().equalsIgnoreCase(code)) {
					// Ignored
				} else {
					ps.close(); // 修改子编号
					StringBuilder sb = new StringBuilder();
					ps = JPrepare.prepareStatement(conn, "SELECT Code,Time FROM " + TBL_COMM_REGION + " WHERE Code LIKE ? AND Tj>?");
					ps.setString(1, info.getSid() + '%');
					ps.setInt(2, tj);
					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						sb.setLength(0);
						sb.append(code).append(rs.getString(1).substring(len));
						rs.updateString(1, sb.toString());
						rs.updateLong(2, info.getTime());
						rs.updateRow();
					}
					rs.close();
					ps.close(); // 修改新状态
					ps = conn.prepareStatement("UPDATE " + TBL_COMM_REGION + " SET Leaf=?,Time=? WHERE Code=?");
					ps.setInt(1, 0);
					ps.setLong(2, info.getTime());
					ps.setString(3, code.substring(0, end));
					ps.executeUpdate();
				}
			} // 提交事务
			conn.commit();
			this.cache.clear();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isRegionByCode(String code) {
		return JPrepare.isExists("SELECT Code FROM " + TBL_COMM_REGION + " WHERE Code=? AND State=?", code, STATE_ENABLE);
	}

	public boolean isRegionByName(String code, String name) {
		int len = (code == null) ? 0 : code.length();
		int tj = len / 2; // 层级
		if (tj <= 1) {
			return JPrepare.isExists("SELECT Code FROM " + TBL_COMM_REGION + " WHERE Tj=? AND State=? AND CNA=?", STATE_ENABLE, 1, name);
		} else {
			String str = code.substring(0, len - 2) + '%';
			return JPrepare.isExists("SELECT Code FROM " + TBL_COMM_REGION + " WHERE Tj=? AND State=? AND Code LIKE ? AND CNA=?", STATE_ENABLE, tj, str, name);
		}
	}

	public AjaxInfo findRegionChildrens(String code) {
		int tj = 1 + StringUtils.length(code) / 2;
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			if (tj <= 1) {
				ps = conn.prepareStatement("SELECT Code,CNB,Leaf FROM " + TBL_COMM_REGION + " WHERE Tj=? AND State=? ORDER BY Code ASC");
				ps.setInt(1, 1);
				ps.setInt(2, STATE_ENABLE);
			} else {
				ps = conn.prepareStatement("SELECT Code,CNB,Leaf FROM " + TBL_COMM_REGION + " WHERE Tj=? AND State=? AND Code LIKE ? ORDER BY Code ASC");
				ps.setInt(1, tj);
				ps.setInt(2, STATE_ENABLE);
				ps.setString(3, code + '%');
			} // 构建数组
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
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findRegionChildrens(String code, int lv) throws SQLException {
		int tj = 1 + StringUtils.length(code) / 2;
		AjaxInfo json = AjaxInfo.getArray();
		if (lv <= 0) {
			lv = tj + 1;
		} else if (lv < tj) {
			return json;
		}
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (tj <= 1) {
				ps = conn.prepareStatement("SELECT Code,CNB,Leaf FROM " + TBL_COMM_REGION + " WHERE Tj=? AND State=? ORDER BY Code ASC");
				ps.setInt(1, 1);
				ps.setInt(2, STATE_ENABLE);
			} else {
				ps = conn.prepareStatement("SELECT Code,CNB,Leaf FROM " + TBL_COMM_REGION + " WHERE Tj=? AND State=? AND Code LIKE ? ORDER BY Code ASC");
				ps.setInt(1, tj);
				ps.setInt(2, STATE_ENABLE);
				ps.setString(3, code + '%');
			} // 构建数组
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getInt(1));
				json.append("text", rs.getString(2));
				if (lv == tj || rs.getInt(3) != 0) {
					json.append("leaf", true);
				} else {
					json.append("leaf", false);
				}
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public RegionInfo findRegionInfo(String code) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			RegionInfo info = null;
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Code,CNA,CNB,ENA,ENB,ZipCode,TelCode,TelNum,Tj,Leaf,State,Time FROM " + TBL_COMM_REGION + " WHERE Code=?");
			ps.setString(1, code);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				info = new RegionInfo();
				info.setSid(rs.getString(1));
				info.setCode(rs.getString(1));
				info.setCna(rs.getString(2));
				info.setCnb(rs.getString(3));
				info.setEna(rs.getString(4));
				info.setEnb(rs.getString(5));
				info.setZipCode(rs.getString(6));
				info.setTelCode(rs.getString(7));
				info.setTelNum(rs.getInt(8));
				info.setState(rs.getInt(9));
				info.setTime(rs.getLong(10));
			}
			rs.close();
			return info;
		} catch (SQLException e) {
			return null;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void removeRegion(String code) throws Exception {
		int len = (code == null) ? 0 : code.length();
		if (len < 2)
			return; // Ignored
		long time = System.currentTimeMillis();
		SyncMap.getAll().add("code", code).send(SYS_A990, "remove").successfully();
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_REGION + " SET State=?,Time=? WHERE Code LIKE ? AND State=?");
			ps.setInt(1, STATE_DISABLE);
			ps.setLong(2, time);
			ps.setString(3, code + '%');
			ps.setInt(4, STATE_ENABLE);
			if (ps.executeUpdate() >= 1 && len > 2) {
				String key = code.substring(0, len - 2);
				ps.close(); // 检测
				ps = conn.prepareStatement("SELECT COUNT(1) FROM " + TBL_COMM_REGION + " WHERE Code LIKE ? AND Tj=? AND State=?");
				ps.setString(1, key + '%');
				ps.setInt(2, (len / 2));
				ps.setInt(3, STATE_ENABLE);
				ResultSet rs = ps.executeQuery();
				long num = 0;
				if (rs.next()) {
					num = rs.getInt(1);
				}
				rs.close();
				if (num <= 0) {
					ps.close();
					ps = conn.prepareStatement("UPDATE " + TBL_COMM_REGION + " SET Leaf=?,Time=? WHERE Code=?");
					ps.setInt(1, 1);
					ps.setLong(2, time);
					ps.setString(3, key);
					ps.executeUpdate();
				}
			}
			this.cache.clear();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public Map<String, SiteCity> getProvince() {
		Map<String, SiteCity> map = this.cache.get(KEY_PROS);
		if (map == null) {
			map = new LinkedHashMap<String, SiteCity>();
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = JPrepare.getConnection();
				ps = conn.prepareStatement("SELECT Code,CNA,CNB FROM " + TBL_COMM_REGION + " WHERE Tj=? AND State=?");
				ps.setInt(1, 1); // 省份加载
				ps.setInt(2, STATE_ENABLE);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					SiteCity sc = new SiteCity();
					sc.setCode(rs.getString(1));
					sc.setName(rs.getString(2));
					sc.setNick(rs.getString(3));
					sc.executeCount(); // 构建正则
					map.put(sc.getCode(), sc);
				}
				rs.close();
				this.cache.put(KEY_PROS, map);
			} catch (Exception e) {
				// Ignored
			} finally {
				JPrepare.close(ps, conn);
			}
		}
		return map;
	}

	public Map<String, SiteCity> getSiteCity(String code) {
		StringBuilder sb = new StringBuilder();
		Map<String, SiteCity> map = this.cache.get(code);
		if (map == null) {
			map = new LinkedHashMap<String, SiteCity>();
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				sb.setLength(0);
				conn = JPrepare.getConnection();
				sb.append("SELECT Code,CNA,CNB FROM " + TBL_COMM_REGION + " WHERE ");
				if (code.length() == 3) {
					ps = conn.prepareStatement(sb.append("Tj>? AND State=? AND Code LIKE ? ORDER BY Code ASC").toString());
					ps.setInt(1, 2);
					ps.setInt(2, STATE_ENABLE);
					ps.setString(3, code);
				} else {
					ps = conn.prepareStatement(sb.append("Tj=? AND State=? AND Code LIKE ? ORDER BY Code ASC").toString());
					ps.setInt(1, (code.length() / 2) + 1);
					ps.setInt(2, STATE_ENABLE);
					ps.setString(3, code + '%');
				}
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					SiteCity sc = new SiteCity();
					sc.setCode(rs.getString(1));
					sc.setName(rs.getString(2));
					sc.setNick(rs.getString(3));
					sc.executeCount(); // 构建正则
					map.put(sc.getCode(), sc);
				}
				rs.close();
				this.cache.put(code, map);
			} catch (Exception e) {
				// Ignored
			} finally {
				JPrepare.close(ps, conn);
			}
		}
		return map;
	}

	public SiteCity getSiteCityByCode(String code) {
		int len = (code == null) ? 0 : code.length();
		if (len <= 1) {
			return null;
		} else {
			Map<String, SiteCity> m = null;
			if (len == 2) {
				m = this.getProvince();
			} else {
				m = this.getSiteCity(code.substring(0, (len - 2)));
			}
			return m.get(code);
		}
	}

	public String getCityByAll(String code) {
		int len = (code == null) ? 0 : code.length();
		if (len <= 1) {
			return "";
		} else if (len == 2) {
			SiteCity sc = this.getProvince().get(code);
			if (sc == null) {
				return "";
			} else {
				return sc.getNick();
			}
		} else {
			SiteCity sc = null;
			Map<String, SiteCity> m = this.getProvince();
			StringBuilder sb = new StringBuilder();
			for (int i = 2; i < len; i += 2) {
				String key = code.substring(0, i);
				sc = m.get(key);
				if (sc == null) {
					return sb.toString();
				} else {
					m = this.getSiteCity(key);
					sb.append(sc.getNick());
				}
			} // add last
			sc = m.get(code);
			if (sc != null) {
				sb.append(sc.getNick());
			}
			return sb.toString();
		}
	}
}
