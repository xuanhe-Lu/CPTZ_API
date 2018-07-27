package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ypm.bean.FeedInfo;
import com.ypm.data.JPrepare;
import com.ypm.service.FeedBackService;

public class FeedBackServiceImp extends AConfig implements FeedBackService {

	private static final String TBL_COMM_FEED = "comm_feed";

	protected void checkSQL() {
	}

	public void save(FeedInfo f) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_FEED + " SET Uid=?,Mobile=?,Content=?,State=?,Time=? WHERE Sid=?");
			ps.setLong(1, f.getUid());
			ps.setString(2, f.getMobile());
			ps.setString(3, f.getContent());
			ps.setInt(4, f.getState());
			ps.setLong(5, f.getTime());
			ps.setString(6, f.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_COMM_FEED + " (Sid,Uid,Mobile,Content,State,Time) VALUES (?,?,?,?,?,?)");
				ps.setString(1, f.getSid());
				ps.setLong(2, f.getUid());
				ps.setString(3, f.getMobile());
				ps.setString(4, f.getContent());
				ps.setInt(5, f.getState());
				ps.setLong(6, f.getTime());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

}
