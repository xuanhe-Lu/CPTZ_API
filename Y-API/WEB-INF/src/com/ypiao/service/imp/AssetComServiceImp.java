package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ypiao.bean.Company;
import com.ypiao.data.JPrepare;
import com.ypiao.service.AssetComService;

public class AssetComServiceImp extends AConfig implements AssetComService {

	private static final String TBL_ASSET_COMS = "asset_coms";

	protected void checkSQL() {
	}

	private void save(Connection conn, Company c) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE" + TBL_ASSET_COMS + " SET Tid=?,CA=?,CB=?,CC=?,CD=?,CE=?,CF=?,CG=?,CH=?,CK=?,CM=?,CN=?,State=?,Time=? WHERE Cid=?");
		try {
			ps.setInt(1, c.getTid());
			ps.setString(2, c.getCa());
			ps.setString(3, c.getCb());
			ps.setString(4, c.getCc());
			ps.setString(5, c.getCd());
			ps.setString(6, c.getCe());
			ps.setString(7, c.getCf());
			ps.setString(8, c.getCg());
			ps.setString(9, c.getCh());
			ps.setInt(10, c.getCk());
			ps.setBigDecimal(11, c.getCm());
			ps.setBigDecimal(12, c.getCn());
			ps.setInt(13, c.getState());
			ps.setLong(14, c.getTime());
			ps.setInt(15, c.getCid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_ASSET_COMS + " (Cid,Tid,CA,CB,CC,CD,CE,CF,CG,CH,CK,CM,CN,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, c.getCid());
				ps.setInt(2, c.getTid());
				ps.setString(3, c.getCa());
				ps.setString(4, c.getCb());
				ps.setString(5, c.getCc());
				ps.setString(6, c.getCd());
				ps.setString(7, c.getCe());
				ps.setString(8, c.getCf());
				ps.setString(9, c.getCg());
				ps.setString(10, c.getCh());
				ps.setInt(11, c.getCk());
				ps.setBigDecimal(12, c.getCm());
				ps.setBigDecimal(13, c.getCn());
				ps.setInt(14, c.getTotal());
				ps.setInt(15, c.getState());
				ps.setLong(16, c.getTime());
				ps.executeUpdate();
			} // 更新统计数据
			if (c.getBank() == c.getCk()) {
				// Ignored
			} else {
				if (c.getBank() > 0) {
					ps.close();
					ps = conn.prepareStatement("UPDATE comm_bank SET Total=(Total-1) WHERE Bid=? AND Bid>=?");
					ps.setInt(1, c.getBank());
					ps.setInt(2, 1);
					ps.executeUpdate();
				}
				ps.close();
				ps = conn.prepareStatement("UPDATE comm_bank SET Total=(Total+1) WHERE Bid=?");
				ps.setInt(1, c.getCk());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int update(Connection conn, int cid, int row) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_ASSET_COMS + " SET Total=? WHERE Cid=?");
		try {
			ps.setInt(1, row);
			ps.setInt(2, cid);
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public void save(Company c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, c); // 保存数据
		} finally {
			JPrepare.close(conn);
		}
	}

	public void remove(int cid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM " + TBL_ASSET_COMS + " WHERE Cid=? AND State=? AND Total<=?");
			ps.setInt(1, cid);
			ps.setInt(2, STATE_NORMAL);
			ps.setInt(3, 0);
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
