package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.CouponInfoService;
import com.ypm.util.*;

public class CouponInfoServiceImp extends AConfig implements CouponInfoService {

	protected void checkSQL() {
	}

	private int findTotal(Connection conn, long rid) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT COUNT(1) FROM " + Table.TBL_COUPON_USER + " WHERE Rid=?");
		try {
			int row = 0;
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				row = rs.getInt(1);
			}
			rs.close();
			return row;
		} finally {
			ps.close();
		}
	}

	private void compute(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int total = this.findTotal(conn, rid);
			ps = conn.prepareStatement("UPDATE " + Table.TBL_COUPON_RULE + " SET Total=? WHERE Rid=?");
			ps.setInt(1, total);
			ps.setLong(2, rid);
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private void insert(List<CouponUser> fs) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("INSERT INTO " + Table.TBL_COUPON_USER + " (Sid,Uid,Rid,Mobile,Name,State,Time) VALUES (?,?,?,?,?,?,?)");
			for (CouponUser u : fs) {
				ps.setLong(1, u.getSid());
				ps.setLong(2, u.getUid());
				ps.setLong(3, u.getRid());
				ps.setString(4, u.getMobile());
				ps.setString(5, u.getName());
				ps.setInt(6, u.getState());
				ps.setLong(7, u.getTime());
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

	private void save(Connection conn, CouponInfo c) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_COUPON_INFO + " SET Tid=?,Type=?,Name=?,TMA=?,TMB=?,Toall=?,Today=?,Total=?,Count=?,Stid=?,Tday=?,Sday=?,Eday=?,Remark=?,State=?,Time=? WHERE Cid=?");
		try {
			ps.setInt(1, c.getTid());
			ps.setInt(2, c.getType());
			ps.setString(3, c.getName());
			ps.setBigDecimal(4, c.getTma());
			ps.setBigDecimal(5, c.getTmb());
			ps.setBigDecimal(6, c.getToall());
			ps.setInt(7, c.getToday());
			ps.setBigDecimal(8, c.getTotal());
			ps.setInt(9, c.getCount());
			ps.setInt(10, c.getStid());
			ps.setInt(11, c.getTday());
			ps.setLong(12, c.getSday());
			ps.setLong(13, c.getEday());
			ps.setString(14, c.getRemark());
			ps.setInt(15, c.getState());
			ps.setLong(16, c.getTime());
			ps.setInt(17, c.getCid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + Table.TBL_COUPON_INFO + " (Cid,Tid,Type,Name,TMA,TMB,Toall,Today,Total,Count,Stid,Tday,Sday,Eday,Remark,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, c.getCid());
				ps.setInt(2, c.getTid());
				ps.setInt(3, c.getType());
				ps.setString(4, c.getName());
				ps.setBigDecimal(5, c.getTma());
				ps.setBigDecimal(6, c.getTmb());
				ps.setBigDecimal(7, c.getToall());
				ps.setInt(8, c.getToday());
				ps.setBigDecimal(9, c.getTotal());
				ps.setInt(10, c.getCount());
				ps.setInt(11, c.getStid());
				ps.setInt(12, c.getTday());
				ps.setLong(13, c.getSday());
				ps.setLong(14, c.getEday());
				ps.setString(15, c.getRemark());
				ps.setInt(16, c.getState());
				ps.setLong(17, c.getTime());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	private void save(Connection conn, CouponRule r) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_COUPON_RULE + " SET Type=?,Name=?,TMA=?,TMB=?,Toall=?,Today=?,Total=?,Stid=?,Tday=?,Sday=?,Eday=?,Remark=?,State=?,Time=?,adM=?,adN=? WHERE Rid=?");
		try {
			ps.setInt(1, r.getType());
			ps.setString(2, r.getName());
			ps.setBigDecimal(3, r.getTma());
			ps.setBigDecimal(4, r.getTmb());
			ps.setBigDecimal(5, r.getToall());
			ps.setInt(6, r.getToday());
			ps.setInt(7, r.getTotal());
			ps.setInt(8, r.getStid());
			ps.setInt(9, r.getTday());
			ps.setLong(10, r.getSday());
			ps.setLong(11, r.getEday());
			ps.setString(12, r.getRemark());
			ps.setInt(13, r.getState());
			ps.setLong(14, r.getTime());
			ps.setLong(15, r.getAdM());
			ps.setString(16, r.getAdN());
			ps.setLong(17, r.getRid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + Table.TBL_COUPON_RULE + " (Rid,Type,Name,TMA,TMB,Toall,Today,Total,Stid,Tday,Sday,Eday,Remark,State,Time,adM,adN) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, r.getRid());
				ps.setInt(2, r.getType());
				ps.setString(3, r.getName());
				ps.setBigDecimal(4, r.getTma());
				ps.setBigDecimal(5, r.getTmb());
				ps.setBigDecimal(6, r.getToall());
				ps.setInt(7, r.getToday());
				ps.setInt(8, r.getTotal());
				ps.setInt(9, r.getStid());
				ps.setInt(10, r.getTday());
				ps.setLong(11, r.getSday());
				ps.setLong(12, r.getEday());
				ps.setString(13, r.getRemark());
				ps.setInt(14, r.getState());
				ps.setLong(15, r.getTime());
				ps.setLong(16, r.getAdM());
				ps.setString(17, r.getAdN());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	public void save(CouponInfo info) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, info);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void save(CouponRule rule) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, rule);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void save(List<CouponUser> ls) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int size = ls.size();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + Table.TBL_COUPON_USER + " SET Uid=?,Time=? WHERE Sid=?");
			for (CouponUser u : ls) {
				ps.setLong(1, u.getUid());
				ps.setLong(2, u.getTime());
				ps.setLong(3, u.getSid());
				ps.addBatch();
			}
			int[] is = ps.executeBatch();
			ps.close(); // 写入数据
			ps = conn.prepareStatement("INSERT INTO " + Table.TBL_COUPON_USER + " (Sid,Uid,Rid,Mobile,Name,State,Time) VALUES (?,?,?,?,?,?,?)");
			for (int i = 0; i < size; i++) {
				if (is[i] >= 1) {
					// Ignored
				} else {
					CouponUser u = ls.get(i);
					ps.setLong(1, u.getSid());
					ps.setLong(2, u.getUid());
					ps.setLong(3, u.getRid());
					ps.setString(4, u.getMobile());
					ps.setString(5, u.getName());
					ps.setInt(6, u.getState());
					ps.setLong(7, u.getTime());
					ps.addBatch();
				}
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		} // 汇总数据
		this.compute(ls.get(0).getRid());
	}

	public void saveSok(String ids, long time) throws SQLException {
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + Table.TBL_COUPON_RULE + " SET State=?,Time=? WHERE Rid=? AND State<=?");
			for (Long r : set) {
				ps.setInt(1, STATE_READER);
				ps.setLong(2, time);
				ps.setLong(3, r.longValue());
				ps.setInt(4, STATE_CHECK);
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

	public int saveQM(CouponRule r) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + Table.TBL_COUPON_RULE + " SET State=?,Time=?,adM=?,adN=? WHERE Rid=? AND State=?");
			ps.setInt(1, r.getState());
			ps.setLong(2, r.getTime());
			ps.setLong(3, r.getAdM());
			ps.setString(4, r.getAdN());
			ps.setLong(5, r.getRid());
			ps.setInt(6, STATE_READER);
			int num = ps.executeUpdate();
			if (num >= 1) {
				ps.close();
				ps = conn.prepareStatement("UPDATE " + Table.TBL_COUPON_USER + " SET State=?,Time=? WHERE Rid=? AND State=?");
				ps.setInt(1, STATE_READER);
				ps.setLong(2, r.getTime());
				ps.setLong(3, r.getRid());
				ps.setInt(4, STATE_NORMAL);
				if (ps.executeUpdate() >= 1) {
					ps.close(); // Add Coupon
					ps = conn.prepareStatement("INSERT INTO " + Table.TBL_USER_COUPON + " (Sid,Uid,Cid,Way,Name,Type,TMA,TMB,Toall,Today,Sday,Eday,Remark,OrdId,GmtA,GmtB,State,Time) SELECT Sid,Uid,Rid,?,?,?,?,?,?,?,?,?,?,?,?,?,?,Time FROM " + Table.TBL_COUPON_USER + " WHERE Rid=?");
					ps.setString(1, WAY_USER);
					ps.setString(2, r.getName());
					ps.setInt(3, r.getType());
					ps.setBigDecimal(4, r.getTma());
					ps.setBigDecimal(5, r.getTmb());
					ps.setBigDecimal(6, r.getToall());
					ps.setInt(7, r.getToday());
					ps.setLong(8, r.getSday());
					ps.setLong(9, r.getEday());
					ps.setString(10, r.getRemark());
					ps.setLong(11, 0); // OrdId
					ps.setLong(12, r.getTime());
					ps.setLong(13, 0); // GmtB
					ps.setInt(14, STATE_NORMAL);
					ps.setLong(15, r.getRid());
					if (ps.executeUpdate() >= 1) {
						ps.close();
						ps = conn.prepareStatement("UPDATE " + Table.TBL_USER_STATUS + " A,(SELECT Uid,COUNT(1) AS C FROM " + Table.TBL_USER_COUPON + " WHERE Uid IN (SELECT Uid FROM " + Table.TBL_COUPON_USER + " WHERE Rid=?) AND State=? AND Eday>=? GROUP BY Uid) B SET A.NA=B.C,A.Time=? WHERE A.Uid=B.Uid");
						ps.setLong(1, r.getRid());
						ps.setInt(2, STATE_NORMAL);
						ps.setLong(3, r.getTime());
						ps.setLong(4, r.getTime());
						ps.executeUpdate();
					}
				}
			}
			conn.commit();
			return num;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int update(long rid, int state, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + Table.TBL_COUPON_RULE + " SET State=?,Time=? WHERE Rid=? AND State<=?");
			ps.setInt(1, state);
			ps.setLong(2, time);
			ps.setLong(3, rid);
			ps.setInt(4, STATE_READER);
			return ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private void delete(long rid, Set<Long> set) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM " + Table.TBL_COUPON_USER + " WHERE Rid=? AND Sid=?");
			for (Long sid : set) {
				ps.setLong(1, rid);
				ps.setLong(2, sid.longValue());
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

	public void delete(long rid, String ids) throws SQLException {
		Set<Long> set = this.toLong(ids);
		if (set.size() >= 1) {
			this.delete(rid, set);
			this.compute(rid);
		}
	}

	public int update(String ids, int state, long time) throws SQLException {
		Set<Integer> set = this.toInt(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + Table.TBL_COUPON_INFO + " SET State=?,Time=? WHERE Cid=?");
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

	public AjaxInfo findCouponByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + Table.TBL_COUPON_INFO).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, Table.TBL_COUPON_INFO, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			Map<String, String> ms = this.getInfoState();
			Map<String, String> tids = this.getDictInfoBySSid(COO_COUPON_TID);
			Map<String, String> type = this.getDictInfoBySSid(COO_COUPON_TYPE);
			Map<String, String> stid = this.getDictInfoBySSid(COO_COUPON_STID);
			sql.insert(0, "SELECT Cid,Tid,Type,Name,TMA,TMB,Toall,Today,Total,Count,Stid,Tday,Sday,Eday,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("CID", rs.getInt(1));
				json.append("TID", tids.get(rs.getString(2)));
				json.append("TYPE", type.get(rs.getString(3)));
				json.append("NAME", rs.getString(4));
				json.append("TMA", DF3.format(rs.getDouble(5)));
				json.append("TMB", DF2.format(rs.getFloat(6)), "%");
				json.append("TOALL", DF3.format(rs.getDouble(7)));
				json.append("TODAY", rs.getInt(8), "天");
				json.append("TOTAL", DF3.format(rs.getDouble(9)));
				json.append("COUNT", rs.getInt(10));
				json.append("STID", stid.get(rs.getString(11)));
				if (rs.getInt(11) == 1) {
					json.append("TDAY", rs.getInt(12), "天");
				} else {
					json.append("TDAY", "截止时间");
				}
				json.append("SDAY", GMTime.format(rs.getLong(13), GMTime.CHINA));
				json.append("EDAY", GMTime.format(rs.getLong(14), GMTime.CHINA));
				json.append("STATE", ms.get(rs.getString(15)));
				json.append("TIME", GMTime.format(rs.getLong(16), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public CouponInfo findCouponByCid(int cid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			CouponInfo c = null;
			ps = conn.prepareStatement("SELECT Cid,Tid,Type,Name,TMA,TMB,Toall,Today,Total,Count,Stid,Tday,Sday,Eday,Remark,State,Time FROM " + Table.TBL_COUPON_INFO + " WHERE Cid=?");
			ps.setInt(1, cid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				c = new CouponInfo();
				c.setCid(rs.getInt(1));
				c.setTid(rs.getInt(2));
				c.setType(rs.getInt(3));
				c.setName(rs.getString(4));
				c.setTma(rs.getBigDecimal(5));
				c.setTmb(rs.getBigDecimal(6));
				c.setToall(rs.getBigDecimal(7));
				c.setToday(rs.getInt(8));
				c.setTotal(rs.getBigDecimal(9));
				c.setCount(rs.getInt(10));
				c.setStid(rs.getInt(11));
				c.setTday(rs.getInt(12));
				c.setSday(rs.getLong(13));
				c.setEday(rs.getLong(14));
				c.setRemark(rs.getString(15));
				c.setState(rs.getInt(16));
				c.setTime(rs.getLong(17));
			}
			rs.close();
			return c;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveInfo(CouponInfo info) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			if (info.getCid() <= 0) {
				info.setCid(this.getId(conn, Table.TBL_COUPON_INFO, "Cid"));
			}
			info.setTime(GMTime.currentTimeMillis());
			this.save(conn, info);
		} finally {
			JPrepare.close(conn);
		}
		SyncMap.getAll().sender(SYS_A511, "save", info);
	}

	public void saveState(String ids, int state) throws SQLException {
		long time = GMTime.currentTimeMillis();
		if (this.update(ids, state, time) >= 1) {
			SyncMap.getAll().add("ids", ids).add("state", state).add("time", time).sender(SYS_A511, "state");
		}
	}

	public AjaxInfo findCouponByRid(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + Table.TBL_COUPON_RULE).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, Table.TBL_COUPON_RULE, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			Map<String, String> type = this.getDictInfoBySSid(COO_COUPON_TYPE);
			Map<String, String> stid = this.getDictInfoBySSid(COO_COUPON_STID);
			Map<String, String> ms = this.getDictInfoBySSid(COO_COUPON_STATE);
			sql.insert(0, "SELECT Rid,Type,Name,TMA,TMB,Toall,Today,Total,Stid,Tday,Sday,Eday,State,Time,adM,adN ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("RID", rs.getLong(1));
				json.append("TYPE", type.get(rs.getString(2)));
				json.append("NAME", rs.getString(3));
				json.append("TMA", DF3.format(rs.getDouble(4)));
				json.append("TMB", DF2.format(rs.getFloat(5)), "%");
				json.append("TOALL", DF3.format(rs.getDouble(6)));
				json.append("TODAY", rs.getInt(7), "天");
				json.append("TOTAL", rs.getInt(8));
				json.append("STID", stid.get(rs.getString(9)));
				if (rs.getInt(9) == 1) {
					json.append("TDAY", rs.getInt(10), "天");
				} else {
					json.append("TDAY", "截止时间");
				}
				json.append("SDAY", GMTime.format(rs.getLong(11), GMTime.CHINA));
				json.append("EDAY", GMTime.format(rs.getLong(12), GMTime.CHINA));
				json.append("STATE", ms.get(rs.getString(13)));
				json.append("TIME", GMTime.format(rs.getLong(14), GMTime.CHINA));
				json.append("ADM", rs.getLong(15));
				json.append("ADN", rs.getString(16));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findCouponByRid(long rid, String order, int offset, int max) {
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.findTotal(conn, rid);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			Map<String, String> ms = this.getInfoStates();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT Sid,Uid,Rid,Mobile,Name,State,Time FROM ").append(Table.TBL_COUPON_USER).append(" WHERE Rid=? ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("SID", rs.getLong(1));
				json.append("UID", rs.getLong(2));
				json.append("MOBILE", rs.getString(4));
				json.append("NAME", rs.getString(5));
				json.append("STATE", ms.get(rs.getString(6)));
				json.append("TIME", GMTime.format(rs.getLong(7), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public CouponRule findCouponByRid(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			CouponRule r = null;
			ps = conn.prepareStatement("SELECT Rid,Type,Name,TMA,TMB,Toall,Today,Total,Stid,Tday,Sday,Eday,Remark,State,Time,adM,adN FROM " + Table.TBL_COUPON_RULE + " WHERE Rid=?");
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				r = new CouponRule();
				r.setRid(rs.getLong(1));
				r.setType(rs.getInt(2));
				r.setName(rs.getString(3));
				r.setTma(rs.getBigDecimal(4));
				r.setTmb(rs.getBigDecimal(5));
				r.setToall(rs.getBigDecimal(6));
				r.setToday(rs.getInt(7));
				r.setTotal(rs.getInt(8));
				r.setStid(rs.getInt(9));
				r.setTday(rs.getInt(10));
				r.setSday(rs.getLong(11));
				r.setEday(rs.getLong(12));
				r.setRemark(rs.getString(13));
				r.setState(rs.getInt(14));
				r.setTime(rs.getLong(15));
				r.setAdM(rs.getLong(16));
				r.setAdN(rs.getString(17));
			}
			rs.close();
			return r;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveInfo(CouponRule rule) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			rule.setTime(GMTime.currentTimeMillis());
			this.save(conn, rule);
		} finally {
			JPrepare.close(conn);
		}
		SyncMap.getAll().sender(SYS_A511, "rule", rule);
	}

	public void saveSok(String ids) throws SQLException {
		long time = GMTime.currentTimeMillis();
		this.saveSok(ids, time);
		SyncMap.getAll().add("ids", ids).add("time", time).sender(SYS_A511, "sok");
	}

	public void addUser(long rid, String user) throws SQLException {
		String[] ts = this.toString(user);
		Set<Long> ids = new HashSet<Long>(ts.length);
		Set<String> set = new HashSet<String>(ts.length);
		for (String str : ts) {
			if (str.indexOf('+') >= 0) {
				String sm = VeStr.getMobile(str);
				if (sm != null) {
					set.add(sm); // 手机号码
				}
			} else {
				int pos = str.indexOf('-');
				if (pos == -1) {
					String sm = VeStr.getMobile(str);
					if (sm == null) {
						int len = str.length();
						if (len <= 9 && len >= 6) {
							long uid = AUtils.toLong(str);
							if (USER_UID_BEG <= uid) {
								ids.add(uid);
							}
						}
					} else {
						set.add(sm);
					}
				} else {
					long beg = AUtils.toLong(str.substring(0, pos).trim());
					long end = AUtils.toLong(str.substring(pos + 1).trim());
					if (USER_UID_BEG > beg) {
						if (USER_UID_BEG > end) {
							// Ignroed
						} else {
							beg = (end - 1000);
							if (USER_UID_BEG >= beg) {
								beg = USER_UID_BEG;
							} // 加载相关数据
							for (long i = beg; i <= end; i++) {
								ids.add(i);
							}
						}
					} else if (beg >= end) {
						ids.add(beg);
						if (USER_UID_BEG <= end) {
							ids.add(end);
						}
					} else {
						long j = (beg + 1000);
						if (j >= end) {
							j = end;
						}
						for (long i = beg; i <= j; i++) {
							ids.add(i);
						}
					}
				}
			}
		} // 解析并转换成对象
		int index = 1, size = 500, a = set.size();
		List<List<CouponUser>> ls = new ArrayList<List<CouponUser>>();
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			if (a >= 1) {
				sql.append("SELECT Uid FROM ").append(Table.TBL_USER_INFO);
				if (a >= 2) {
					sql.append(" WHERE Account IN (?");
					for (int i = 1; i < a; i++) {
						sql.append(",?");
					}
					sql.append(')');
				} else {
					sql.append(" WHERE Account=?");
				}
				ps = conn.prepareStatement(sql.toString());
				for (String sm : set) {
					ps.setString(index++, sm);
				}
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					ids.add(rs.getLong(1));
				}
				rs.close();
			} // 过滤已存在对象
			a = ids.size();
			if (a >= 1) {
				if (ps != null) {
					ps.close();
				}
				sql.setLength(0);
				sql.append("SELECT Sid,Uid,Rid,Mobile,Name,State,Time FROM ").append(Table.TBL_COUPON_USER).append(" WHERE Rid=? AND Uid IN (?");
				for (int i = 1; i < a; i++) {
					sql.append(",?");
				}
				index = 2;
				List<CouponUser> fs = null;
				ps = conn.prepareStatement(sql.append(')').toString());
				ps.setLong(1, rid);
				for (Long uid : ids) {
					ps.setLong(index++, uid.longValue());
				}
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					CouponUser c = new CouponUser();
					c.setSid(rs.getLong(1));
					c.setUid(rs.getLong(2));
					c.setRid(rs.getLong(3));
					c.setMobile(rs.getString(4));
					c.setName(rs.getString(5));
					c.setState(rs.getInt(6));
					c.setTime(rs.getLong(7));
					ids.remove(c.getUid());
					if (fs == null || fs.size() >= size) {
						fs = new ArrayList<CouponUser>(size);
						ls.add(fs); // 加入缓存
					}
					fs.add(c);
				}
				rs.close();
				for (List<CouponUser> us : ls) {
					if (us.size() >= 1) {
						SyncMap.getAll().sender(SYS_A511, "addUser", fs);
					}
				}
				ls.clear();
			}
			a = ids.size();
			if (a >= 1) {
				if (ps != null) {
					ps.close();
				}
				sql.setLength(0);
				long time = GMTime.currentTimeMillis();
				sql.append("SELECT Uid,Mobile,Name FROM ").append(Table.TBL_USER_STATUS).append(" WHERE Uid IN (?");
				for (int i = 1; i < a; i++) {
					sql.append(",?");
				}
				index = 1;
				List<CouponUser> fs = null;
				ps = conn.prepareStatement(sql.append(')').toString());
				for (Long uid : ids) {
					ps.setLong(index++, uid.longValue());
				}
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					CouponUser c = new CouponUser();
					c.setSid(VeStr.getUSid());
					c.setUid(rs.getLong(1));
					c.setRid(rid);
					c.setMobile(rs.getString(2));
					c.setName(rs.getString(3));
					c.setState(STATE_NORMAL);
					c.setTime(time);
					ids.remove(c.getUid());
					if (fs == null || fs.size() >= size) {
						fs = new ArrayList<CouponUser>(size);
						ls.add(fs); // 加入缓存
					}
					fs.add(c);
				}
				rs.close();
			}
		} finally {
			JPrepare.close(ps, conn);
		} // 保存数据信息
		for (List<CouponUser> fs : ls) {
			if (fs.size() >= 1) {
				this.insert(fs);
				SyncMap.getAll().sender(SYS_A511, "addUser", fs);
			}
		}
		this.compute(rid);
	}

	public void delUser(long rid, String ids) throws SQLException {
		Set<Long> set = this.toLong(ids);
		if (set.size() >= 1) {
			this.delete(rid, set);
			this.compute(rid);
			SyncMap.getAll().add("rid", rid).add("ids", ids).sender(SYS_A511, "delUser");
		}
	}

	public void refuse(CouponRule r) throws SQLException {
		r.setState(STATE_CHECK);
		r.setTime(GMTime.currentTimeMillis());
		if (this.update(r.getRid(), r.getState(), r.getTime()) >= 1) {
			SyncMap.getAll().add("rid", r.getRid()).add("time", r.getTime()).sender(SYS_A511, "refuse");
		}
	}

	public void sendQM(CouponRule rule) throws SQLException {
		rule.setState(STATE_ERRORS);
		rule.setTime(GMTime.currentTimeMillis());
		long eday = rule.getEday();
		if (rule.getStid() == 0) {
			// Ignroed
		} else if (rule.getTday() >= 1) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(GState.USER_TODAX);
			c.add(Calendar.DATE, rule.getTday());
			eday = c.getTimeInMillis();
		} else if (rule.getEday() >= GState.USER_TODAY) {
			eday = GState.USER_TODAY;
		}
		rule.setSday(rule.getTime());
		rule.setEday(eday - 1000);
		if (this.saveQM(rule) >= 1) {
			SyncMap.getAll().sender(SYS_A511, "sendQM", rule);
		}
	}
}
