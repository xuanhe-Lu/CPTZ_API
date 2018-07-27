package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ypiao.bean.FileInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.service.SiteFileService;

public class SiteFileServiceImp implements SiteFileService {

	private static final String TBL_COMM_IMGS = "comm_imgs";

	public void save(FileInfo f) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_IMGS + " SET Sid=?,Tid=?,Sortid=?,Source=?,Dist=?,Name=?,Info=?,Pdw=?,Pdh=?,Size=?,State=?,Time=? WHERE Pid=?");
			ps.setString(1, f.getSid());
			ps.setInt(2, f.getTid());
			ps.setInt(3, f.getSortid());
			ps.setString(4, f.getSource());
			ps.setString(5, f.getDist());
			ps.setString(6, f.getName());
			ps.setString(7, f.getInfo());
			ps.setInt(8, f.getPdw());
			ps.setInt(9, f.getPdh());
			ps.setLong(10, f.getSize());
			ps.setInt(11, f.getState());
			ps.setLong(12, f.getTime());
			ps.setString(13, f.getPid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_COMM_IMGS + " (Pid,Sid,Tid,Sortid,Source,Dist,Name,Info,Pdw,Pdh,Size,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, f.getPid());
				ps.setString(2, f.getSid());
				ps.setInt(3, f.getTid());
				ps.setInt(4, f.getSortid());
				ps.setString(5, f.getSource());
				ps.setString(6, f.getDist());
				ps.setString(7, f.getName());
				ps.setString(8, f.getInfo());
				ps.setInt(9, f.getPdw());
				ps.setInt(10, f.getPdh());
				ps.setLong(11, f.getSize());
				ps.setInt(12, f.getState());
				ps.setLong(13, f.getTime());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
