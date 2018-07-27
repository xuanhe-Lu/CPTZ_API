package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ypiao.bean.InfoVIPS;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserRoleService;

public class UserRoleServiceImp implements UserRoleService {

	private static final String TBl_USER_VIPS = "user_vips";

	private void save(Connection conn, InfoVIPS v) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBl_USER_VIPS + " SET Name=?,Rate=?,Rats=?,Sale=?,SNum=?,SRmb=?,STxt=?,Sok=?,GMa=?,GMb=?,GNa=?,GNb=?,Rday=?,Remark=?,State=?,Time=? WHERE VIP=?");
		try {
			ps.setString(1, v.getName());
			ps.setBigDecimal(2, v.getRate());
			ps.setBigDecimal(3, v.getRats());
			ps.setBigDecimal(4, v.getSale());
			ps.setInt(5, v.getSNum());
			ps.setBigDecimal(6, v.getSRmb());
			ps.setString(7, v.getSTxt());
			ps.setInt(8, v.getSok());
			ps.setBigDecimal(9, v.getGma());
			ps.setBigDecimal(10, v.getGmb());
			ps.setBigDecimal(11, v.getGna());
			ps.setBigDecimal(12, v.getGnb());
			ps.setInt(13, v.getRday());
			ps.setString(14, v.getRemark());
			ps.setInt(15, v.getState());
			ps.setLong(16, v.getTime());
			ps.setInt(17, v.getVip());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBl_USER_VIPS + " (VIP,Name,Rate,Rats,Sale,SNum,SRmb,STxt,Sok,GMa,GMb,GNa,GNb,Rday,Remark,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, v.getVip());
				ps.setString(2, v.getName());
				ps.setBigDecimal(3, v.getRate());
				ps.setBigDecimal(4, v.getRats());
				ps.setBigDecimal(5, v.getSale());
				ps.setInt(6, v.getSNum());
				ps.setBigDecimal(7, v.getSRmb());
				ps.setString(8, v.getSTxt());
				ps.setInt(9, v.getSok());
				ps.setBigDecimal(10, v.getGma());
				ps.setBigDecimal(11, v.getGmb());
				ps.setBigDecimal(12, v.getGna());
				ps.setBigDecimal(13, v.getGnb());
				ps.setInt(14, v.getRday());
				ps.setString(15, v.getRemark());
				ps.setInt(16, v.getState());
				ps.setLong(17, v.getTime());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	public void save(InfoVIPS v) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			if (v.getVip() >= 0) {
				this.save(conn, v);
			}
		} finally {
			JPrepare.close(conn);
		}
	}
}
