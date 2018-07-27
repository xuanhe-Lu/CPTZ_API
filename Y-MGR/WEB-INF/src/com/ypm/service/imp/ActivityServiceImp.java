package com.ypm.service.imp;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.commons.lang.LRUMap;
import com.ypm.bean.ActInfo;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.SyncMap;
import com.ypm.data.JPrepare;
import com.ypm.service.ActivityService;
import com.ypm.util.GMTime;

public class ActivityServiceImp extends AConfig implements ActivityService {

	private static final String TBL_ACT_INFO = "act_info";

	private Map<Integer, ActInfo> _cache = new LRUMap<Integer, ActInfo>();

	protected void checkSQL() {
	}

	private void save(Connection conn, ActInfo a) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_ACT_INFO + " SET Name=?,Rate=?,Remark=?,Sday=?,Eday=?,State=?,Time=? WHERE Adj=?");
		try {
			ps.setString(1, a.getName());
			ps.setBigDecimal(2, a.getRate());
			ps.setString(3, a.getRemark());
			ps.setLong(4, a.getSday());
			ps.setLong(5, a.getEday());
			ps.setInt(6, a.getState());
			ps.setLong(7, a.getTime());
			ps.setInt(8, a.getAdj());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // add model
				ps = conn.prepareStatement("INSERT INTO " + TBL_ACT_INFO + " (Adj,Name,Rate,Remark,Sday,Eday,State,Time) VALUES (?,?,?,?,?,?,?,?)");
				ps.setInt(1, a.getAdj());
				ps.setString(2, a.getName());
				ps.setBigDecimal(3, a.getRate());
				ps.setString(4, a.getRemark());
				ps.setLong(5, a.getSday());
				ps.setLong(6, a.getEday());
				ps.setInt(7, a.getState());
				ps.setLong(8, a.getTime());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	public void save(ActInfo info) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, info);
		} finally {
			JPrepare.close(conn);
		}
	}

	public int update(String ids, int state, long time) throws SQLException {
		Set<Integer> set = this.toInt(ids);
		if (set.size() <= 0) {
			return 0;
		} // update state
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_ACT_INFO + " SET State=?,Time=? WHERE Adj=?");
			for (Integer adj : set) {
				ps.setInt(1, state);
				ps.setLong(2, time);
				ps.setInt(3, adj.intValue());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			return set.size();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findActByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_ACT_INFO).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_ACT_INFO, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			float r = 0f;
			Map<String, String> ms = this.getInfoState();
			sql.insert(0, "SELECT Adj,Name,Rate,Remark,Sday,Eday,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("ADJ", rs.getInt(1));
				json.append("NAME", rs.getString(2));
				if ((r = rs.getFloat(3)) > 0) {
					json.append("RATE", DF2.format(r), "%");
				} else {
					json.append("RATE", "-");
				}
				json.append("REMARK", rs.getString(4));
				json.append("SDAY", GMTime.format(rs.getLong(5), GMTime.CHINA));
				json.append("EDAY", GMTime.format(rs.getLong(6), GMTime.CHINA));
				json.append("STATE", ms.get(rs.getString(7)));
				json.append("TIME", GMTime.format(rs.getLong(8), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findActByAll() {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			float amt = 0;
			json.append("id", 0);
			json.append("text", "默认（无）");
			json.append("adk", "-");
			conn = JPrepare.getConnection();
			long time = GMTime.currentTimeMillis();
			ps = conn.prepareStatement("SELECT Adj,Name,Rate FROM " + TBL_ACT_INFO + " WHERE State=? AND Eday>=? ORDER BY Adj DESC");
			ps.setInt(1, STATE_ENABLE);
			ps.setLong(2, time);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getInt(1));
				json.append("text", rs.getString(2));
				if ((amt = rs.getFloat(3)) > 0) {
					json.append("adk", "+" + DF2.format(amt), "%");
				} else {
					json.append("adk", "-");
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

	public ActInfo findActByAdj(int adj) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ActInfo a = null;
			ps = conn.prepareStatement("SELECT Adj,Name,Rate,Remark,Sday,Eday,State,Time FROM " + TBL_ACT_INFO + " WHERE Adj=?");
			ps.setInt(1, adj);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				a = new ActInfo();
				a.setAdj(rs.getInt(1));
				a.setName(rs.getString(2));
				a.setRate(rs.getBigDecimal(3));
				a.setRemark(rs.getString(4));
				a.setSday(rs.getLong(5));
				a.setEday(rs.getLong(6));
				a.setState(rs.getInt(7));
				a.setTime(rs.getLong(8));
				_cache.put(adj, a);
			}
			rs.close();
			return a;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public ActInfo getActByAdj(int adj) {
		ActInfo act = _cache.get(adj);
		if (act == null) {
			try {
				return this.findActByAdj(adj);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return act;
	}

	public void saveInfo(ActInfo info) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			conn.setAutoCommit(false);
			if (info.getAdj() <= 0) {
				info.setAdj(this.getId(conn, TBL_ACT_INFO, "Adj"));
			}
			info.setTime(GMTime.currentTimeMillis());
			this.save(conn, info);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(conn);
		}
		SyncMap.getAll().sender(SYS_A513, "saveInfo", info);
	}

	public void saveState(String ids, int state) throws SQLException {
		long time = GMTime.currentTimeMillis();
		if (this.update(ids, state, time) >= 1) {
			SyncMap.getAll().add("ids", ids).add("state", state).add("time", time).sender(SYS_A513, "saveState");
		}
	}
}
