package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.BankInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.data.sql.ReadUtils;
import com.ypiao.service.BankInfoService;

public class BankInfoServiceImp extends AConfig implements BankInfoService {

	private static final String TBL_COMM_BANK = "comm_bank";

	private static Map<String, Integer> _BKID;

	private static Map<Integer, BankInfo> _BKS;

	protected void checkSQL() {
		_BKID = new HashMap<String, Integer>();
		_BKS = new HashMap<Integer, BankInfo>();
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
			} // 缓存信息
			_BKS.put(b.getBid(), b);
		} finally {
			ps.close();
		}
	}

	public void save(BankInfo info) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, info);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void order(String ids, long time) throws SQLException {
		Set<Integer> set = this.toInt(ids);
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
		}
	}

	public void state(String ids, int state, long time) throws SQLException {
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
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int findBankByCNo(String cardNo) {
		if (_BKID.isEmpty()) {
			String str = ReadUtils.readFile("bank.txt");
			if (str == null) {
				_BKID.put("0000", 0);
			} else {
				String[] ts = str.split("(\r\n|\r|\n)");
				for (String txt : ts) {
					String[] s = txt.split("=>");
					_BKID.put(s[0].trim(), Integer.parseInt(s[1].trim()));
				}
			}
		} // 匹配编号信息
		String key = null;
		for (int i = 8; i >= 4; i--) {
			key = cardNo.substring(0, i);
			Integer bid = _BKID.get(key);
			if (bid != null) {
				return bid.intValue();
			}
		} // 未匹配成功
		return 0;
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
	
	public BankInfo getBankByBid(int bid) {
		BankInfo b = _BKS.get(bid);
		if (b == null) {
			try {
				return this.findBankByBid(bid);
			} catch (SQLException e) {
				return null;
			}
		}
		return b;
	}

	public void sendByAll(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			json.datas(API_OK);
			ps = conn.prepareStatement("SELECT Bid,Sortid,Type,Name,Nice,Month,Toall,Today,Total,State,Time FROM " + TBL_COMM_BANK + " WHERE State=? ORDER BY Sortid ASC");
			ps.setInt(1, STATE_NORMAL);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("bid", rs.getInt(1));
				json.append("name", rs.getString(4));
				json.append("nice", rs.getString(5));
				json.append("month", DF2.format(rs.getDouble(6)));
				json.append("toall", DF2.format(rs.getDouble(7)));
				json.append("today", DF2.format(rs.getDouble(8)));
				json.append("time", rs.getLong(11));
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
