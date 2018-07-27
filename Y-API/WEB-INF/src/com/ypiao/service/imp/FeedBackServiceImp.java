package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ypiao.bean.FeedInfo;
import com.ypiao.bean.SyncMap;
import com.ypiao.data.JPrepare;
import com.ypiao.service.FeedBackService;
import com.ypiao.util.GMTime;

/**
 * 系统反馈接口实现类. 
 */
public class FeedBackServiceImp extends AConfig implements FeedBackService {

	// 系统反馈表名
	private static final String TBL_COMM_FEED = "comm_feed";

	protected void checkSQL() {
	}

	/**
	 * 保存反馈数据.
	 * 
	 * @param f FeedInfo
	 * @throws SQLException
	 */
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

	/**
	 * 保存反馈数据.
	 * 
	 * @param f FeedInfo
	 * @throws SQLException
	 */
	public void saveFeed(FeedInfo f) throws SQLException {
		f.setTime(GMTime.currentTimeMillis());
		// 保存反馈信息
		this.save(f); 
		// 同步数据
		SyncMap.getAll().sender( SYS_A995, "save", f );
	}
}
