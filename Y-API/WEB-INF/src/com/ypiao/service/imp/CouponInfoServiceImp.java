package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.CouponInfoService;
import com.ypiao.util.Table;

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

	public void state(String ids, int state, long time) throws SQLException {
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
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public List<CouponInfo> findCouponByTid(int tid, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			List<CouponInfo> ls = new ArrayList<CouponInfo>();
			ps = conn.prepareStatement("SELECT Cid,Tid,Type,Name,TMA,TMB,Toall,Today,Total,Count,Stid,Tday,Sday,Eday,Remark,State,Time FROM " + Table.TBL_COUPON_INFO + " WHERE Tid=? AND State=? AND Sday<=? AND Eday>=? ORDER BY Cid ASC");
			ps.setInt(1, tid);
			ps.setInt(2, STATE_ENABLE);
			ps.setLong(3, time);
			ps.setLong(4, time);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				CouponInfo c = new CouponInfo();
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
				ls.add(c);
			}
			rs.close();
			return ls;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void sendByUid(AjaxInfo json, long uid) {
		// json.formater();
		// json.append("sid", "1");
		// json.append("type", "1");
		// json.append("name", "测试优惠券");
		// json.append("fees", "10.00");
		// json.append("rate", "0");
		// json.append("toall", 100);
		// json.append("today", 30);
		// json.append("eday", "2018-05-25");
		// json.append("info", "单笔投资金额>=100元");
	}
}
