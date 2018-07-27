package com.ypm.service.imp;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.AderInfoService;
import com.ypm.util.GMTime;
import com.ypm.util.VeStr;

public class AderInfoServiceImp extends AConfig implements AderInfoService {

	private static final String TBL_ADER_INFO = "ader_info";

	protected void checkSQL() {
	}

	private void save(Connection conn, AdsInfo a) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_ADER_INFO + " SET Tid=?,Sortid=?,Name=?,Tags=?,Type=?,Dist=?,URL=?,Ver=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setInt(1, a.getTid());
			ps.setInt(2, a.getSortid());
			ps.setString(3, a.getName());
			ps.setString(4, a.getTags());
			ps.setInt(5, a.getType());
			ps.setString(6, a.getDist());
			ps.setString(7, a.getUrl());
			ps.setInt(8, a.getVer());
			ps.setInt(9, a.getState());
			ps.setLong(10, a.getTime());
			ps.setString(11, a.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_ADER_INFO + " (Sid,Tid,Sortid,Name,Tags,Type,Dist,URL,Ver,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, a.getSid());
				ps.setInt(2, a.getTid());
				ps.setInt(3, a.getSortid());
				ps.setString(4, a.getName());
				ps.setString(5, a.getTags());
				ps.setInt(6, a.getType());
				ps.setString(7, a.getDist());
				ps.setString(8, a.getUrl());
				ps.setInt(9, a.getVer());
				ps.setInt(10, a.getState());
				ps.setLong(11, a.getTime());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	public AdsInfo findAderBySid(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			AdsInfo a = null;
			ps = conn.prepareStatement("SELECT Sid,Tid,Sortid,Name,Tags,Type,Dist,URL,Ver,State,Time FROM " + TBL_ADER_INFO + " WHERE Sid=?");
			ps.setString(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				a = new AdsInfo();
				a.setSid(rs.getString(1));
				a.setTid(rs.getInt(2));
				a.setSortid(rs.getInt(3));
				a.setName(rs.getString(4));
				a.setTags(rs.getString(5));
				a.setType(rs.getInt(6));
				a.setDist(rs.getString(7));
				a.setUrl(rs.getString(8));
				a.setVer(rs.getInt(9));
				a.setState(rs.getInt(10));
				a.setTime(rs.getLong(11));
			}
			ps.close();
			return a;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findAderByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_ADER_INFO).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_ADER_INFO, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			Map<String, String> ma = this.getInfoState();
			Map<String, String> mb = this.getDictInfoBySSid(COO_ADS_TIDS);
			Map<String, String> mc = this.getDictInfoBySSid(COO_ADS_TYPE);
			sql.insert(0, "SELECT Sid,Tid,Sortid,Name,Tags,Type,URL,Ver,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("SID", rs.getString(1));
				json.append("TID", mb.get(rs.getString(2)));
				json.append("SORTID", rs.getInt(3));
				json.append("NAME", rs.getString(4));
				json.append("TAGS", rs.getString(5));
				json.append("TYPE", mc.get(rs.getString(6)));
				json.append("URL", rs.getString(7));
				json.append("STATE", ma.get(rs.getString(9)));
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

	public AjaxInfo findTreeByInfo(int tid) {
		AjaxInfo json = AjaxInfo.getBean(true, true);
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement(JPrepare.getQuery("SELECT Sid,Name FROM " + TBL_ADER_INFO + " WHERE Tid=? AND State=? ORDER BY Sortid ASC", 20));
			ps.setInt(1, tid);
			ps.setInt(2, STATE_NORMAL);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
			}
			rs.close();
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public void saveAds(List<AdsInfo> ads) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long time = GMTime.currentTimeMillis();
			for (AdsInfo a : ads) {
				if (ps != null) {
					ps.close();
				} // check AdsInfo
				ps = JPrepare.prepareStatement(conn, "SELECT Sid,Tid,Sortid,Name,Tags,Type,Dist,URL,Ver,State,Time FROM " + TBL_ADER_INFO + " WHERE URL=?");
				ps.setString(1, a.getUrl());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					// Ignored
				} else {
					a.setSid(VeStr.getUSid(true));
					a.setTime(time);
					rs.moveToInsertRow();
					rs.updateString(1, a.getSid());
					rs.updateInt(2, a.getTid());
					rs.updateInt(3, a.getState());
					rs.updateString(4, a.getName());
					rs.updateString(5, a.getTags());
					rs.updateInt(6, a.getType());
					rs.updateString(7, a.getDist());
					rs.updateString(8, a.getUrl());
					rs.updateInt(9, a.getVer());
					rs.updateInt(10, a.getState());
					rs.updateLong(11, a.getTime());
					rs.insertRow();
				}
				rs.close();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveAder(AdsInfo a, FileInfo f) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			if (f.getDist() == null) {
				// Ignroed
			} else {
				a.setDist(f.getDist());
			}
			a.setTime(GMTime.currentTimeMillis());
			this.save(conn, a); // 保存数据
		} finally {
			JPrepare.close(conn);
		} // 同步数据信息
		f.setTime(a.getSid(), a.getTime());
		this.saveFile(f);
		SyncMap.getAll().sender(SYS_A501, "save", a);
	}

	private void order(String[] ids, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_ADER_INFO + " SET Sortid=?,Time=? WHERE Sid=?");
			for (String sid : ids) {
				ps.setInt(1, index++);
				ps.setLong(2, time);
				ps.setString(3, sid);
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

	public void orderInfo(String ids) throws SQLException {
		String[] ts = this.toSplit(ids);
		long time = GMTime.currentTimeMillis();
		this.order(ts, time);
		SyncMap.getAll().add("ids", ids).add("time", time).sender(SYS_A501, "order");
	}

	private void state(String[] ids, int state, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_ADER_INFO + " SET State=?,Time=? WHERE Sid=?");
			for (String sid : ids) {
				ps.setInt(1, state);
				ps.setLong(2, time);
				ps.setString(3, sid);
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

	public void stateInfo(String ids, int state) throws SQLException {
		String[] ts = this.toSplit(ids);
		if (state != STATE_ENABLE) {
			state = STATE_DISABLE;
		} // sync to API
		long time = GMTime.currentTimeMillis();
		this.state(ts, state, time);
		SyncMap.getAll().add("ids", ids).add("state", state).add("time", time).sender(SYS_A501, "state");
	}

	private void remove(String[] ids) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM " + TBL_ADER_INFO + " WHERE Sid=?");
			for (String sid : ids) {
				ps.setString(1, sid);
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

	public void removeInfo(String ids) throws SQLException {
		String[] ts = this.toSplit(ids);
		this.remove(ts); // delete
		SyncMap.getAll().add("ids", ids).sender(SYS_A501, "remove");
	}
}
