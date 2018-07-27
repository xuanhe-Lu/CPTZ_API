package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.ConfigService;
import com.ypiao.service.SysConfig;
import com.ypiao.util.VeRule;

public class ConfigServiceImp extends AConfig implements ConfigService {

	private static final String TBL_COMM_CFG = "comm_config", TBL_COMM_VER = "comm_ver";

	private static String SQL_CFG_BY_VER;

	private SysConfig sysConfig;

	protected void checkSQL() {
		SQL_CFG_BY_VER = JPrepare.getQuery("SELECT Sid,Tid,Code,Codever,Filename,Size,Content,Tday,State,Time FROM " + TBL_COMM_VER + " WHERE State=? AND Tid=? AND Code>=? ORDER BY Sid DESC", 1);
	}

	public SysConfig getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig) {
		this.sysConfig = sysConfig;
	}

	public void saveClient(SetClient sc) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_VER + " SET Tid=?,Codever=?,Filename=?,Size=?,Content=?,Tday=?,State=?,Time=? WHERE Sid=?");
			ps.setInt(1, sc.getTid());
			ps.setString(2, sc.getCodever());
			ps.setString(3, sc.getFilename());
			ps.setDouble(4, sc.getSize());
			ps.setString(5, sc.getContent());
			ps.setString(6, sc.getTday());
			ps.setInt(7, sc.getState());
			ps.setLong(8, sc.getTime());
			ps.setInt(9, sc.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_COMM_VER + " (Sid,Tid,Code,Codever,Filename,Size,Content,Tday,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, sc.getSid());
				ps.setInt(2, sc.getTid());
				ps.setInt(3, sc.getCode());
				ps.setString(4, sc.getCodever());
				ps.setString(5, sc.getFilename());
				ps.setDouble(6, sc.getSize());
				ps.setString(7, sc.getContent());
				ps.setString(8, sc.getTday());
				ps.setInt(9, sc.getState());
				ps.setLong(10, sc.getTime());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void updateClient(String ids, int state, long time) throws SQLException {
		Set<Integer> set = this.toInt(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_VER + " SET State=?,Time=? WHERE Sid=?");
			for (Integer s : set) {
				ps.setInt(1, state);
				ps.setLong(2, time);
				ps.setInt(3, s.intValue());
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

	public void removeClient(String ids) throws SQLException {
		Set<Integer> set = this.toInt(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM " + TBL_COMM_VER + " WHERE Sid=?");
			for (Integer s : set) {
				ps.setInt(1, s.intValue());
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

	public void saveConfig(Config cfg) throws SQLException {
		this.getSysConfig().saveConfig(cfg); // 保存数据
		if (VeRule.isYes("(USE_REWRITE|USE_DEBUG)", cfg.getId())) {
			this.getSysConfig().resetSystem();
		}
	}

	public void orderConfig(String ids) throws SQLException {
		String ts[] = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_CFG + " SET Sortid=? WHERE Id=?");
			for (String id : ts) {
				ps.setInt(1, index++);
				ps.setString(2, id);
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

	public void removeConfig(String ids) throws SQLException {
		String ts[] = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM " + TBL_COMM_CFG + " WHERE Id=?");
			for (String id : ts) {
				ps.setString(1, id);
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

	private SetClient findClient(int tid, int ver) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			SetClient sc = null;
			ps = conn.prepareStatement(SQL_CFG_BY_VER);
			ps.setInt(1, STATE_ENABLE);
			ps.setInt(2, tid);
			ps.setInt(3, ver);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				sc = new SetClient();
				sc.setSid(rs.getInt(1));
				sc.setTid(rs.getInt(2));
				sc.setCode(rs.getInt(3));
				sc.setCodever(rs.getString(4));
				sc.setContent(rs.getString(5));
				sc.setTday(rs.getString(6));
				sc.setState(rs.getInt(7));
				sc.setTime(rs.getLong(8));
			}
			rs.close();
			return sc;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public SetClient findClientByAndroid(int ver) throws SQLException {
		return findClient(1, ver);
	}

	public SetClient findClientByIOS(int ver) throws SQLException {
		return findClient(2, ver);
	}

	public void sendVer(Manager mgr, int tid) throws Exception {
		int code = mgr.getInt("code"); // 当前版本号
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			Message msg = mgr.getMessage();
			ps = conn.prepareStatement("SELECT Sid,Tid,Code,Codever,Filename,Size,Content,Tday FROM " + TBL_COMM_VER + " WHERE State=? AND Tid=? AND Code>=? ORDER BY Sid DESC");
			ps.setInt(1, STATE_ENABLE);
			ps.setInt(2, tid);
			ps.setInt(3, (code + 1));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				msg.addObject(); // 对象值
				msg.append("code", rs.getInt(3));
				msg.append("codever", rs.getString(4));
				msg.append("content", rs.getString(7));
				if ((tid % 2) == 1) {
					//msg.append("filename", "yinpiao.apk");
					msg.append( "filename", "/download/" + rs.getString(5) );
					msg.append( "size", rs.getDouble(6) + "M" );
				} // update date
				msg.append("tday", rs.getString(6));
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
			mgr.sendReply(-1);
		}
	}
}
