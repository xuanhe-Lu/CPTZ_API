package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.ypm.bean.UserCoupon;
import com.ypm.data.JPrepare;
import com.ypm.service.UserCouponService;
import com.ypm.service.UserInfoService;
import com.ypm.util.Table;

public class UserCouponServiceImp extends AConfig implements UserCouponService {

	private UserInfoService userInfoService;

	protected void checkSQL() {
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	private void compute(Connection conn, long uid, long time) throws SQLException {
		int coupon = 0; // 券码数量
		PreparedStatement ps = conn.prepareStatement("SELECT COUNT(1) FROM " + Table.TBL_USER_COUPON + " WHERE Uid=? AND State=? AND Eday>=?");
		try {
			ps.setLong(1, uid);
			ps.setInt(2, STATE_NORMAL);
			ps.setLong(3, time);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				coupon = rs.getInt(1);
			}
			rs.close();
		} finally {
			ps.close();
		} // 更新用户数据
		this.getUserInfoService().update(conn, uid, coupon, time);
	}

	public void compute(long uid, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.compute(conn, uid, time);
		} finally {
			JPrepare.close(conn);
		}
	}

	private void save(Connection conn, UserCoupon uc) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_USER_COUPON + " SET Uid=?,Cid=?,Way=?,Name=?,Type=?,TMA=?,TMB=?,Toall=?,Today=?,Sday=?,Eday=?,Remark=?,GmtA=?,GmtB=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setLong(1, uc.getUid());
			ps.setLong(2, uc.getCid());
			ps.setString(3, uc.getWay());
			ps.setString(4, uc.getName());
			ps.setInt(5, uc.getType());
			ps.setBigDecimal(6, uc.getTma());
			ps.setBigDecimal(7, uc.getTmb());
			ps.setBigDecimal(8, uc.getToall());
			ps.setInt(9, uc.getToday());
			ps.setLong(10, uc.getSday());
			ps.setLong(11, uc.getEday());
			ps.setString(12, uc.getRemark());
			ps.setLong(13, uc.getGmtA());
			ps.setLong(14, uc.getGmtB());
			ps.setInt(15, uc.getState());
			ps.setLong(16, uc.getTime());
			ps.setLong(17, uc.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // Add Coupon
				ps = conn.prepareStatement("INSERT INTO " + Table.TBL_USER_COUPON + "(Sid,Uid,Cid,Way,Name,Type,TMA,TMB,Toall,Today,Sday,Eday,Remark,GmtA,GmtB,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, uc.getSid());
				ps.setLong(2, uc.getUid());
				ps.setLong(3, uc.getCid());
				ps.setString(4, uc.getWay());
				ps.setString(5, uc.getName());
				ps.setInt(6, uc.getType());
				ps.setBigDecimal(7, uc.getTma());
				ps.setBigDecimal(8, uc.getTmb());
				ps.setBigDecimal(9, uc.getToall());
				ps.setInt(10, uc.getToday());
				ps.setLong(11, uc.getSday());
				ps.setLong(12, uc.getEday());
				ps.setString(13, uc.getRemark());
				ps.setLong(14, uc.getGmtA());
				ps.setLong(15, uc.getGmtB());
				ps.setInt(16, uc.getState());
				ps.setLong(17, uc.getTime());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	public void save(List<UserCoupon> fs, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			Set<Long> set = new HashSet<Long>();
			for (UserCoupon uc : fs) {
				set.add(uc.getUid());
				this.save(conn, uc);
			} // 计算券码数量
			for (Long uid : set) {
				this.compute(conn, uid.longValue(), time);
			}
		} finally {
			JPrepare.close(conn);
		}
	}

	public int update(Connection conn, long sid, long uid, long ordId, long time) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_USER_COUPON + " SET OrdId=?,GmtB=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setLong(1, ordId);
			ps.setLong(2, time);
			ps.setInt(3, STATE_READER);
			ps.setLong(4, time);
			ps.setLong(5, sid);
			int num = ps.executeUpdate();
			if (num >= 1) {
				this.execute(() -> {
					try {
						Thread.sleep(1000);
						this.compute(uid, time);
					} catch (SQLException | InterruptedException e) {
						e.printStackTrace();
					}
				});
			}
			return num;
		} finally {
			ps.close();
		}
	}
}
