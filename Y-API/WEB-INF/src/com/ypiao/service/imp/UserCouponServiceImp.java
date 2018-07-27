package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserCoupon;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserCouponService;
import com.ypiao.service.UserInfoService;
import com.ypiao.util.GMTime;

public class UserCouponServiceImp extends AConfig implements UserCouponService {

	private static final String TBL_USER_COUPON = "user_coupon";

	private static String SQL_BY_EXPIRED;

	private UserInfoService userInfoService;

	protected void checkSQL() {
		SQL_BY_EXPIRED = JPrepare.getQuery("SELECT Uid FROM " + TBL_USER_COUPON + " WHERE State=? AND Eday<=?", MAX_100);
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	private void compute(Connection conn, long uid, long time) throws SQLException {
		int coupon = 0; // 券码数量
		PreparedStatement ps = conn.prepareStatement("SELECT COUNT(1) FROM " + TBL_USER_COUPON + " WHERE Uid=? AND State=? AND Eday>=?");
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

	public void expired() throws SQLException {
		Set<Long> set = new HashSet<Long>();
		while (this.loadByAll(set) >= 1) {
			long time = GMTime.currentTimeMillis();
			for (Long uid : set) {
				this.expired(uid.longValue(), time);
			}
		}
	}

	private void expired(long uid, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_USER_COUPON + " SET State=? WHERE Uid=? AND State=? AND Eday<=?");
			ps.setInt(1, STATE_CHECK); // 过期
			ps.setLong(2, uid);
			ps.setInt(3, STATE_NORMAL);
			ps.setLong(4, time);
			if (ps.executeUpdate() >= 1) {
				ps.close();
				ps = null; // set Null
				this.compute(conn, uid, time);
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private void save(Connection conn, UserCoupon uc) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_COUPON + " SET Uid=?,Cid=?,Way=?,Name=?,Type=?,TMA=?,TMB=?,Toall=?,Today=?,Sday=?,Eday=?,Remark=?,GmtA=?,GmtB=?,State=?,Time=? WHERE Sid=?");
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
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_COUPON + "(Sid,Uid,Cid,Way,Name,Type,TMA,TMB,Toall,Today,Sday,Eday,Remark,GmtA,GmtB,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_COUPON + " SET OrdId=?,GmtB=?,State=?,Time=? WHERE Sid=?");
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
					} catch (Throwable e) {
						e.printStackTrace();
					}
				});
			}
			return num;
		} finally {
			ps.close();
		}
	}

	public UserCoupon findCouponBySid(long sid) throws SQLException {
		if (USER_UID_MAX >= sid) {
			return null;
		} // 加载优惠券
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			UserCoupon uc = null;
			ps = conn.prepareStatement("SELECT Sid,Uid,Cid,Way,Name,Type,TMA,TMB,Toall,Today,Sday,Eday,OrdId,Remark,GmtA,GmtB,State,Time FROM " + TBL_USER_COUPON + " WHERE Sid=?");
			ps.setLong(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				uc = new UserCoupon();
				uc.setSid(rs.getLong(1));
				uc.setUid(rs.getLong(2));
				uc.setCid(rs.getLong(3));
				uc.setWay(rs.getString(4));
				uc.setName(rs.getString(5));
				uc.setType(rs.getInt(6));
				uc.setTma(rs.getBigDecimal(7));
				uc.setTmb(rs.getBigDecimal(8));
				uc.setToall(rs.getBigDecimal(9));
				uc.setToday(rs.getInt(10));
				uc.setSday(rs.getLong(11));
				uc.setEday(rs.getLong(12));
				uc.setOrdId(rs.getLong(13));
				uc.setRemark(rs.getString(14));
				uc.setGmtA(rs.getLong(15));
				uc.setGmtB(rs.getLong(16));
				uc.setState(rs.getInt(17));
				uc.setTime(rs.getLong(18));
			}
			rs.close();
			return uc;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private int loadByAll(Set<Long> set) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			set.clear();
			ps = conn.prepareStatement(SQL_BY_EXPIRED);
			ps.setInt(1, STATE_NORMAL);
			ps.setLong(2, GMTime.currentTimeMillis());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				set.add(rs.getLong(1));
			}
			rs.close();
			return set.size();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int loadByUid(AjaxInfo json, long uid, int day) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int na = 0;
			json.datas(API_OK);
			long time = GMTime.currentTimeMillis();
			ps = conn.prepareStatement("SELECT Sid,Name,Type,TMA,TMB,Toall,Today,Eday,State,Time FROM " + TBL_USER_COUPON + " WHERE Uid=? AND State=? AND Eday>=? AND Sday<=? AND Today<=? ORDER BY Eday ASC");
			ps.setLong(1, uid);
			ps.setInt(2, STATE_NORMAL);
			ps.setLong(3, time);
			ps.setLong(4, time);
			ps.setInt(5, day);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("sid", rs.getLong(1));
				json.append("name", rs.getString(2));
				json.append("type", rs.getInt(3));
				json.append("fees", DF2.format(rs.getFloat(4)));
				json.append("rate", DF2.format(rs.getFloat(5)));
				json.append("toall", DF2.format(rs.getFloat(6)));
				json.append("today", rs.getInt(7));
				json.append("eday", GMTime.format(rs.getLong(8), GMTime.CHINA));
				json.append("state", rs.getInt(9));
				na += 1;
			}
			rs.close();
			return na;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo sendByUid(AjaxInfo json, long uid, int state) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long time = GMTime.currentTimeMillis();
			if (STATE_ENABLE == state) {
				ps = conn.prepareStatement("SELECT Sid,Name,Type,TMA,TMB,Toall,Today,Eday,State,Time FROM " + TBL_USER_COUPON + " WHERE Uid=? AND State=? AND Eday>=? ORDER BY Eday ASC");
				ps.setLong(1, uid);
				ps.setInt(2, STATE_NORMAL);
				ps.setLong(3, (time + 1));
			} else {
				ps = conn.prepareStatement("SELECT Sid,Name,Type,TMA,TMB,Toall,Today,Eday,State,Time FROM " + TBL_USER_COUPON + " WHERE Uid=? AND (Eday<=? OR State>=?) ORDER BY Eday DESC");
				ps.setLong(1, uid);
				ps.setLong(2, time);
				ps.setInt(3, STATE_CHECK);
			}
			json.datas(API_OK);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("sid", rs.getLong(1));
				json.append("name", rs.getString(2));
				json.append("type", rs.getInt(3));
				json.append("fees", DF2.format(rs.getFloat(4)));
				json.append("rate", DF2.format(rs.getFloat(5)));
				json.append("toall", DF2.format(rs.getFloat(6)));
				json.append("today", rs.getInt(7));
				json.append("eday", GMTime.format(rs.getLong(8), GMTime.CHINA));
				json.append("state", rs.getInt(9));
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @param uid long
	 * @param state int
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 优惠券弹窗
	 */
	public AjaxInfo alertByUid(AjaxInfo json, long uid, int state) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		try {
			long curTime = GMTime.currentTimeMillis();
			
			if (uid == 0) {
				json.datas(API_OK);
				return json;
			} else {
				if (STATE_ENABLE == state) {
					ps = conn.prepareStatement( "SELECT Sid, Name, Type, TMA, TMB, Toall, Today FROM " + TBL_USER_COUPON + " WHERE Uid = ? AND State = ? AND Eday >= ? ORDER BY TMA DESC LIMIT 0, 3" );
					ps.setLong( 1, uid );
					ps.setInt( 2, STATE_NORMAL );
					ps.setLong( 3, (curTime + 1) );
				} else {
					ps = conn.prepareStatement( "SELECT Sid, Name, Type, TMA, TMB, Toall, Today FROM " + TBL_USER_COUPON + " WHERE Uid = ? AND (Eday <= ? OR State >= ?) ORDER BY TMA DESC LIMIT 0, 3" );
					ps.setLong( 1, uid );
					ps.setLong( 2, curTime );
					ps.setInt( 3, STATE_CHECK );
				}
			}
			json.datas(API_OK);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				json.formater();
				json.append( "sid", rs.getLong(1) );
				json.append( "name", rs.getString(2) );
				json.append( "type", rs.getInt(3) );
				json.append( "fees", DF2.format(rs.getFloat(4)) );
				json.append( "rate", DF2.format(rs.getFloat(5)) );
				json.append( "toall", DF2.format(rs.getFloat(6)) );
				json.append( "today", rs.getInt(7) );
			}
			
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
