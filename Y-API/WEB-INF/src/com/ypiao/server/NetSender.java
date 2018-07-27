package com.ypiao.server;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.ypiao.bean.Messager;
import com.ypiao.bean.SyncInfo;
import com.ypiao.data.JPrepare;

public class NetSender implements Runnable {

	private static final int SIZE = 20;

	private String SQL_BY_DELETE, SQL_BY_INSERT, SQL_BY_TIME;

	private String SQL_TRUNCATE = null;

	private NetConnect netService;

	private boolean sended = false;

	private boolean status = false;

	public NetSender(NetConnect service, int t) {
		this.netService = service;
		String tbl = null;
		if (t == 0) {
			tbl = "sync_info";
		} else {
			tbl = "sync_inf" + t;
		} // SQL_INFO
		StringBuilder sb = new StringBuilder();
		SQL_BY_DELETE = sb.append("DELETE FROM ").append(tbl).append(" WHERE Sid=?").toString();
		sb.setLength(0);
		SQL_BY_INSERT = sb.append("INSERT INTO ").append(tbl).append(" (Cls,Rev,Content,FPath,Time) VALUES (?,?,?,?,?)").toString();
		sb.setLength(0);
		SQL_BY_TIME = JPrepare.getQuery(sb.append("SELECT Sid,Cls,Rev,Content,FPath,Time FROM ").append(tbl).append(" ORDER BY Sid ASC"), SIZE);
		SQL_TRUNCATE = "TRUNCATE TABLE " + tbl;
		JPrepare.checkSyncs("sync", tbl);
	}

	public NetConnect getNetService() {
		return netService;
	}

	private void destroy(Messager mgr) {
		if (mgr == null) {
			// Ignored
		} else {
			mgr.destroy();
		}
	}

	protected void wakeup() {
		synchronized (this) {
			this.notifyAll();
		}
	}

	protected void waiting() {
		try {
			synchronized (this) {
				this.wait(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected boolean shutdown() {
		return status;
	}

	@Override
	public void run() {
		try {
			int index = 0;
			this.status = false;
			List<SyncInfo> ls = new ArrayList<SyncInfo>(SIZE);
			do {
				index += 1;
				if (sended) {
					if (this.sendA(false)) {
						index = 0;
					}
				} else if (this.sendB(ls)) {
					if (this.sendA(false)) {
						index = 0;
					}
				} else {
					ls.clear();
					this.adds();
				}
				this.waiting();
			} while (index < 30);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.status = true;
		}
	}

	private void adds() throws SQLException {
		SyncInfo info = this.getNetService().getInfo();
		if (info == null) {
			// Ignored
		} else {
			this.adds(info);
		}
	}

	private void adds(SyncInfo info) throws SQLException {
		this.sended = false; // 发送状态
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			long time = System.currentTimeMillis();
			ps = conn.prepareStatement(SQL_BY_INSERT);
			do {
				ps.setInt(1, info.getCls());
				ps.setInt(2, info.getRev());
				ps.setString(3, info.getContent());
				ps.setString(4, info.getFPath());
				ps.setLong(5, time++);
				ps.addBatch();
				info = this.getNetService().getInfo();
			} while (info != null);
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private boolean sendA(boolean result) throws SQLException {
		SyncInfo info = this.getNetService().getInfo();
		if (info == null) {
			return result;
		}
		Messager mgr = null;
		try {
			mgr = this.getNetService().send(info);
			if (mgr == null || mgr.failure()) {
				this.adds(info);
				return false;
			}
		} catch (IOException e) {
			this.adds(info);
			return false;
		} finally {
			this.destroy(mgr);
		}
		return this.sendA(true);
	}

	private boolean sendB(List<SyncInfo> ls) throws SQLException {
		Connection conn = JPrepare.getConnection();
		Statement stmt = null;
		try {
			do {
				while (ls.size() > 0) {
					SyncInfo info = ls.remove(0);
					Messager mgr = this.getNetService().send(info);
					if (mgr == null) {
						return false;
					} else if (mgr.failure()) {
						mgr.destroy();
					} else {
						this.delInfo(conn, info);
						mgr.destroy();
					}
				} // 加载数据信息
				this.setInfo(conn, ls);
			} while (ls.size() > 0);
			stmt = conn.createStatement();
			stmt.execute(SQL_TRUNCATE);
			return (this.sended = true);
		} catch (Exception e) {
			return false;
		} finally {
			JPrepare.close(stmt, conn);
		}
	}

	private void delInfo(Connection conn, SyncInfo info) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_BY_DELETE);
		try {
			ps.setLong(1, info.getSid());
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	private void setInfo(Connection conn, List<SyncInfo> ls) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_BY_TIME);
		try {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				SyncInfo info = new SyncInfo();
				info.setSid(rs.getLong(1));
				info.setCls(rs.getInt(2));
				info.setRev(rs.getInt(3));
				info.setContent(rs.getString(4));
				String fP = rs.getString(5);
				if (fP == null) {
					info.setFile(null);
				} else {
					info.setFile(new File(fP));
				}
				info.setTime(rs.getLong(6));
				ls.add(info);
			}
			rs.close();
		} finally {
			ps.close();
		}
	}
}
