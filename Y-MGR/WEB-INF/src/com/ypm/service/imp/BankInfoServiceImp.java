package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.BankInfo;
import com.ypm.bean.SyncMap;
import com.ypm.data.JPrepare;
import com.ypm.service.BankInfoService;
import com.ypm.util.GMTime;

public class BankInfoServiceImp extends AConfig implements BankInfoService {

	private static final String TBL_COMM_BANK = "comm_bank";

	private Map<Integer, String> _cache = new HashMap<Integer, String>();

	private boolean isLoad = true;

	protected void checkSQL() {
	}

	private void save(Connection conn, BankInfo b) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_COMM_BANK + " SET BankId=?,Sortid=?,Type=?,Name=?,Nice=?,Month=?,Toall=?,Today=?,Total=?,State=?,Time=? WHERE Bid=?");
		try {
			ps.setInt(1, b.getBankId());
			ps.setInt(2, b.getSortid());
			ps.setInt(3, b.getType());
			ps.setString(4, b.getName());
			ps.setString(5, b.getNice());
			ps.setBigDecimal(6, b.getMonth());
			ps.setBigDecimal(7, b.getToall());
			ps.setBigDecimal(8, b.getToday());
			ps.setInt(9, b.getTotal());
			ps.setInt(10, b.getState());
			ps.setLong(11, b.getTime());
			ps.setInt(12, b.getBid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_COMM_BANK + " (Bid,BankId,Sortid,Type,Name,Nice,Month,Toall,Today,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, b.getBid());
				ps.setInt(2, b.getBankId());
				ps.setInt(3, b.getSortid());
				ps.setInt(4, b.getType());
				ps.setString(5, b.getName());
				ps.setString(6, b.getNice());
				ps.setBigDecimal(7, b.getMonth());
				ps.setBigDecimal(8, b.getToall());
				ps.setBigDecimal(9, b.getToday());
				ps.setInt(10, b.getTotal());
				ps.setInt(11, b.getState());
				ps.setLong(12, b.getTime());
				ps.executeUpdate();
			} // 缓存更新
			_cache.put(b.getBid(), b.getNice());
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
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_BANK + " SET State=?,Time=? WHERE Bid=?");
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

	public AjaxInfo findBankByAll() {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Bid,Nice FROM " + TBL_COMM_BANK + " ORDER BY Sortid ASC");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findBankByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_COMM_BANK).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_COMM_BANK, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			double m = Double.NaN;
			Map<String, String> ms = this.getInfoState();
			Map<String, String> mt = this.getDictInfoBySSid(SYSTEM_BANK_TYPE);
			sql.insert(0, "SELECT Bid,Sortid,Type,Name,Nice,Month,Toall,Today,Total,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("BID", rs.getInt(1));
				json.append("SORTID", rs.getInt(2));
				json.append("TYPE", mt.get(rs.getString(3)));
				json.append("NAME", rs.getString(4));
				json.append("NICE", rs.getString(5));
				if ((m = rs.getDouble(6)) >= 1) {
					json.append("MONTH", DF3.format(m));
				} else {
					json.append("MONTH", "-");
				} // 单笔限额
				if ((m = rs.getDouble(7)) >= 1) {
					json.append("TOALL", DF3.format(m));
				} else {
					json.append("TOALL", "-");
				} // 起投金额
				if ((m = rs.getDouble(8)) >= 1) {
					json.append("TODAY", DF3.format(m));
				} else {
					json.append("TODAY", "-");
				}
				json.append("TOTAL", rs.getInt(9));
				json.append("STATE", ms.get(rs.getString(10)));
				json.append("TIME", GMTime.format(rs.getLong(11), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public BankInfo findBankByBid(int bid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			BankInfo b = null;
			ps = conn.prepareStatement("SELECT Bid,BankId,Sortid,Type,Name,Nice,Month,Toall,Today,Total,State,Time FROM " + TBL_COMM_BANK + " WHERE Bid=?");
			ps.setInt(1, bid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				b = new BankInfo();
				b.setBid(rs.getInt(1));
				b.setBankId(rs.getInt(2));
				b.setSortid(rs.getInt(3));
				b.setType(rs.getInt(4));
				b.setName(rs.getString(5));
				b.setNice(rs.getString(6));
				b.setMonth(rs.getBigDecimal(7));
				b.setToall(rs.getBigDecimal(8));
				b.setToday(rs.getBigDecimal(9));
				b.setTotal(rs.getInt(10));
				b.setState(rs.getInt(11));
				b.setTime(rs.getLong(12));
			}
			rs.close();
			return b;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	/**
	 * @return Map<Integer, String>
	 * 
	 * 查询所有支持银行的编号、银行名称、简称信息集合
	 */
	public Map<Integer, String> getBankByAll() {
		if (isLoad) {
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = JPrepare.getConnection();
				ps = conn.prepareStatement( "SELECT Bid, Name, Nice FROM " + TBL_COMM_BANK + " ORDER BY Bid ASC" );
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					_cache.put( rs.getInt(1), rs.getString(3) );
				}
				rs.close();
				isLoad = false;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JPrepare.close(ps, conn);
			}
		}
		
		return _cache;
	}

	public void saveInfo(BankInfo b) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			if (b.getBid() <= 0) {
				b.setBid(this.getId(conn, TBL_COMM_BANK, "Bid"));
			}
			b.setTime(GMTime.currentTimeMillis());
			save(conn, b); // 保存数据
		} finally {
			JPrepare.close(conn);
		} // 同步数据信息
		SyncMap.getAll().sender(SYS_A991, "save", b);
	}

	public void saveOrder(String ids) throws SQLException {
		Set<Integer> set = this.toInt(ids);
		long time = GMTime.currentTimeMillis();
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_BANK + " SET Sortid=?,Time=? WHERE Bid=?");
			for (Integer b : set) {
				ps.setInt(1, index++);
				ps.setLong(2, time);
				ps.setInt(3, b.intValue());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		} // 同步更新操作
		SyncMap.getAll().add("ids", ids).add("time", time).sender(SYS_A991, "order");
	}

	public void saveState(String ids, int state) throws SQLException {
		long time = GMTime.currentTimeMillis();
		if (this.update(ids, state, time) >= 1) {
			SyncMap.getAll().add("ids", ids).add("state", state).add("time", time).sender(SYS_A991, "state");
		}
	}
}
