package com.ypiao.service.imp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.ypiao.bean.ServerInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.service.NodeInfoService;

public class NodeInfoServiceImp implements NodeInfoService {

	private static final String TBL_COMM_SERVER = "comm_server";

	public void saveAll(List<ServerInfo> ls) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("TRUNCATE TABLE " + TBL_COMM_SERVER);
			ps.executeUpdate();
			ps.close(); // 批量更新
			ps = conn.prepareStatement("INSERT INTO " + TBL_COMM_SERVER + " (Sid,Mfk,Mfs,Name,IPer,Port,State,Stime,Time) VALUES (?,?,?,?,?,?,?,?,?)");
			for (ServerInfo s : ls) {
				ps.setInt(1, s.getSid());
				ps.setString(2, s.getMfk());
				ps.setInt(3, s.getMfs());
				ps.setString(4, s.getName());
				ps.setString(5, s.getIper());
				ps.setInt(6, s.getPort());
				ps.setInt(7, s.getState());
				ps.setLong(8, s.getStime());
				ps.setLong(9, s.getTime());
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

	public List<ServerInfo> findByAll() throws SQLException {
		List<ServerInfo> ls = new ArrayList<ServerInfo>();
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Sid,Mfk,Mfs,Name,IPer,Port,State,Stime,Time FROM " + TBL_COMM_SERVER + " ORDER BY Sid ASC");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ServerInfo s = new ServerInfo();
				s.setSid(rs.getInt(1));
				s.setMfk(rs.getString(2));
				s.setMfs(rs.getInt(3));
				s.setName(rs.getString(4));
				s.setIper(rs.getString(5));
				s.setPort(rs.getInt(6));
				s.setState(rs.getInt(7));
				s.setTime(rs.getLong(8));
				ls.add(s);
			}
			rs.close();
			return ls;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

}
