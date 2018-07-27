package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.ProdModel;
import com.ypm.bean.SyncMap;
import com.ypm.data.JPrepare;
import com.ypm.service.ProdModelService;
import com.ypm.util.GMTime;

public class ProdModelServiceImp extends AConfig implements ProdModelService {

	private static final String TBL_PROD_MODEL = "prod_model";

	private Map<Integer, String> _PMs;

	public ProdModelServiceImp() {
		this._PMs = new HashMap<Integer, String>();
	}

	protected void checkSQL() {
	}

	private void save(Connection conn, ProdModel m) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_PROD_MODEL + " SET Name=?,Total=?,Toall=?,Tofee=?,MA=?,MB=?,MC=?,Remark=?,State=?,Time=? WHERE Tid=?");
		try {
			ps.setString(1, m.getName());
			ps.setInt(2, m.getTotal());
			ps.setInt(3, m.getToall());
			ps.setInt(4, m.getTofee());
			ps.setBigDecimal(5, m.getMa());
			ps.setBigDecimal(6, m.getMb());
			ps.setBigDecimal(7, m.getMc());
			ps.setString(8, m.getRemark());
			ps.setInt(9, m.getState());
			ps.setLong(10, m.getTime());
			ps.setInt(11, m.getTid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_PROD_MODEL + " (Tid,Name,Total,Toall,Tofee,MA,MB,MC,Remark,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, m.getTid());
				ps.setString(2, m.getName());
				ps.setInt(3, m.getTotal());
				ps.setInt(4, m.getToall());
				ps.setInt(5, m.getTofee());
				ps.setBigDecimal(6, m.getMa());
				ps.setBigDecimal(7, m.getMb());
				ps.setBigDecimal(8, m.getMc());
				ps.setString(9, m.getRemark());
				ps.setInt(10, m.getState());
				ps.setLong(11, m.getTime());
				ps.executeUpdate();
			} // 更新数据
		} finally {
			ps.close();
		}
	}

	public int update(String ids, int state, long time) throws SQLException {
		Set<Integer> set = this.toInt(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_PROD_MODEL + " SET State=?,Time=? WHERE Tid=?");
			for (Integer t : set) {
				ps.setInt(1, state);
				ps.setLong(2, time);
				ps.setInt(3, t.intValue());
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

	public AjaxInfo findModelByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_PROD_MODEL).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_PROD_MODEL, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			int v = 0;
			double m = Double.NaN;
			Map<String, String> def = this.getDefault();
			Map<String, String> ms = this.getInfoState();
			sql.insert(0, "SELECT Tid,Name,Total,Toall,Tofee,MA,MB,MC,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("TID", rs.getInt(1));
				json.append("NAME", rs.getString(2));
				if ((v = rs.getInt(3)) >= 1) {
					json.append("TOTAL", v);
				} else {
					json.append("TOTAL", "-");
				}
				if ((v = rs.getInt(4)) >= 1) {
					json.append("TOALL", v);
				} else {
					json.append("TOALL", "-");
				}
				json.append("TOFEE", def.get(rs.getString(5)));
				if ((m = rs.getDouble(6)) >= 1) {
					json.append("MA", DF3.format(m));
				} else {
					json.append("MA", "-");
				} // 单笔限额
				if ((m = rs.getDouble(7)) >= 1) {
					json.append("MB", DF3.format(m));
				} else {
					json.append("MB", "-");
				} // 起投金额
				if ((m = rs.getDouble(8)) >= 1) {
					json.append("MC", DF3.format(m));
				} else {
					json.append("MC", "-");
				}
				json.append("STATE", ms.get(rs.getString(9)));
				json.append("TIME", GMTime.format(rs.getLong(10), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public ProdModel findModelByTid(int tid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ProdModel m = null;
			ps = conn.prepareStatement("SELECT Tid,Name,Total,Toall,Tofee,MA,MB,MC,Remark,State,Time FROM " + TBL_PROD_MODEL + " WHERE Tid=?");
			ps.setInt(1, tid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				m = new ProdModel();
				m.setTid(rs.getInt(1));
				m.setName(rs.getString(2));
				m.setTotal(rs.getInt(3));
				m.setToall(rs.getInt(4));
				m.setTofee(rs.getInt(5));
				m.setMa(rs.getBigDecimal(6));
				m.setMb(rs.getBigDecimal(7));
				m.setMc(rs.getBigDecimal(8));
				m.setRemark(rs.getString(9));
				m.setState(rs.getInt(10));
				m.setTime(rs.getLong(11));
			}
			rs.close();
			return m;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findModelByAll(int state) {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			double amt = 0;
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Tid,Name,Total,Tofee,MA,MB,MC FROM " + TBL_PROD_MODEL + " WHERE State=? ORDER BY Tid ASC");
			ps.setInt(1, state);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
				json.append("tofee", rs.getInt(4));
				if ((amt = rs.getDouble(6)) > 0) {
					json.append("MB", DF2.format(amt), "元");
				} else {
					json.append("MB", "不限");
				}
				if ((amt = rs.getDouble(7)) > 0) {
					json.append("MC", DF2.format(amt), "元");
				} else {
					json.append("MC", "不限");
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

	public Map<Integer, String> findModelByAll() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Tid,Name FROM " + TBL_PROD_MODEL);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				_PMs.put(rs.getInt(1), rs.getString(2));
			}
			rs.close();
			return _PMs;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public Map<Integer, String> getModelByAll() {
		if (_PMs.size() <= 0) {
			try {
				return this.findModelByAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return _PMs;
	}

	public boolean isModelByName(String name) {
		return JPrepare.isExists("SELECT Tid FROM " + TBL_PROD_MODEL + " WHERE Name=?", name);
	}

	public void saveModel(ProdModel m) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			if (m.getTid() <= 0) {
				m.setTid(this.getId(conn, TBL_PROD_MODEL, "Tid"));
			}
			m.setTime(GMTime.currentTimeMillis());
			save(conn, m); // 保存数据
			_PMs.put(m.getTid(), m.getName());
		} finally {
			JPrepare.close(conn);
		} // 同步数据信息
		SyncMap.getAll().sender(SYS_A211, "save", m);
	}

	public void saveState(String ids, int state) throws SQLException {
		long time = GMTime.currentTimeMillis();
		if (this.update(ids, state, time) >= 1) {
			SyncMap.getAll().add("ids", ids).add("state", state).add("time", time).sender(SYS_A211, "state");
		}
	}
}
