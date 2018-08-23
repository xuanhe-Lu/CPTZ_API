package com.ypm.service.imp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.ypm.bean.Message;
import com.ypm.bean.ServerInfo;
import com.ypm.data.JPrepare;
import com.ypm.service.NodeInfoService;
import com.ypm.util.GMTime;

public class NodeInfoServiceImp extends AConfig implements NodeInfoService {

	private static final String TBL_COMM_SERVER = "comm_server";

	protected void checkSQL() {
	}

	public List<ServerInfo> findByAll() throws SQLException {
		List<ServerInfo> ls = new ArrayList<ServerInfo>();
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Sid,Mfk,Mfs,Name,IPer,Port,State,Stime,Time FROM " + TBL_COMM_SERVER + " WHERE State=? ORDER BY Sid ASC");
			ps.setInt(1, STATE_NORMAL);
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

	public void saveInfo(ServerInfo info) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			info.setTime(System.currentTimeMillis());
			info.setState(STATE_NORMAL); // 正常服务
			ps = conn.prepareStatement("SELECT Sid,Mfk,Mfs,Name,IPer,Port,State,Time FROM " + TBL_COMM_SERVER + " WHERE Mfk=? AND Mfs=?");
			ps.setString(1, info.getMfk());
			ps.setInt(2, info.getMfs());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				info.setSid(rs.getInt(1));
				info.setName(rs.getString(4));
				if (rs.getString(5).equalsIgnoreCase(info.getIper())) {

				} else {
					// 启动通知操作
				}
			}
			rs.close();
			if (info.getSid() <= 0) {
				ps.close();
				ps = conn.prepareStatement("SELECT MAX(Sid) FROM " + TBL_COMM_SERVER);
				rs = ps.executeQuery();
				if (rs.next()) {
					info.setSid(rs.getInt(1) + 1);
				} else {
					info.setSid(1);
				}
				rs.close();
			}
			ps.close();
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_SERVER + " SET IPer=?,Port=?,Time=? WHERE Sid=?");
			ps.setString(1, info.getIper());
			ps.setInt(2, info.getPort());
			ps.setLong(3, info.getTime());
			ps.setInt(4, info.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_COMM_SERVER + " (Sid,Mfk,Mfs,Name,IPer,Port,State,Stime,Time) VALUES (?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, info.getSid());
				ps.setString(2, info.getMfk());
				ps.setInt(3, info.getMfs());
				ps.setString(4, info.getName());
				ps.setString(5, info.getIper());
				ps.setInt(6, info.getPort());
				ps.setInt(7, info.getState());
				ps.setLong(8, info.getStime());
				ps.setLong(9, info.getTime());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void sendByAll(Message msg) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			msg.addList("Sid,Mfk,Mfs,Name,IPer,Port,State,Time");
			ps = conn.prepareStatement("SELECT Sid,Mfk,Mfs,Name,IPer,Port,State,Time FROM " + TBL_COMM_SERVER + " WHERE State=? ORDER BY Sid ASC");
			ps.setInt(1, STATE_NORMAL);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				msg.formater();
				msg.append(rs.getInt(1));
				msg.append(rs.getString(2));
				msg.append(rs.getInt(3));
				msg.append(rs.getString(4));
				msg.append(rs.getString(5));
				msg.append(rs.getInt(6));
				msg.append(rs.getInt(7));
				msg.append(rs.getLong(8));
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

}
