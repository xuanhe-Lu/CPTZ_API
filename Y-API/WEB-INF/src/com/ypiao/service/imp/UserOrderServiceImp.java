package com.ypiao.service.imp;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.*;
import com.ypiao.util.*;

public class UserOrderServiceImp extends AConfig implements UserOrderService {

	private static String SQL_ORDER_BY_PID;

	private UserInfoService userInfoService;

	private UserMoneyService userMoneyService;

	protected void checkSQL() {
		SQL_ORDER_BY_PID = JPrepare.getQuery("SELECT Sid,Uid,TMA,GmtA FROM " + Table.TBL_LOG_ORDER + " WHERE Pid=?", -1, "inner", "SELECT Uid,Mobile FROM user_status", "A.Uid=B.Uid ORDER BY A.Sid DESC");
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public UserMoneyService getUserMoneyService() {
		return userMoneyService;
	}

	public void setUserMoneyService(UserMoneyService userMoneyService) {
		this.userMoneyService = userMoneyService;
	}

	public int adds(Connection conn, LogOrder log) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_LOG_ORDER + " SET Pid=?,Cid=?,Name=?,GmtA=?,GmtB=?,GmtC=?,GmtD=?,GURL=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setLong(1, log.getPid());
			ps.setLong(2, log.getCid());
			ps.setString(3, log.getName());
			ps.setLong(4, log.getGmtA());
			ps.setLong(5, log.getGmtB());
			ps.setLong(6, log.getGmtC());
			ps.setLong(7, log.getGmtD());
			ps.setString(8, log.getUrl());
			ps.setInt(9, log.getState());
			ps.setLong(10, log.getTime());
			ps.setLong(11, log.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // Add OK
				ps = conn.prepareStatement("INSERT INTO " + Table.TBL_LOG_ORDER + " (Sid,Uid,Pid,Cid,Tid,Name,Rate,Rday,Any,Day,Way,Ads,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,GmtD,GURL,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, log.getSid());
				ps.setLong(2, log.getUid());
				ps.setLong(3, log.getPid());
				ps.setLong(4, log.getCid());
				ps.setInt(5, log.getTid());
				ps.setString(6, log.getName());
				ps.setBigDecimal(7, log.getRate());
				ps.setInt(8, log.getRday());
				ps.setInt(9, log.getAny());
				ps.setInt(10, log.getDay());
				ps.setString(11, log.getWay());
				ps.setBigDecimal(12, log.getAds());
				ps.setBigDecimal(13, log.getTma());
				ps.setBigDecimal(14, log.getTmb());
				ps.setBigDecimal(15, log.getTmc());
				ps.setBigDecimal(16, log.getTmd());
				ps.setBigDecimal(17, log.getTme());
				ps.setBigDecimal(18, log.getTmf());
				ps.setBigDecimal(19, log.getTmg());
				ps.setBigDecimal(20, log.getYma());
				ps.setBigDecimal(21, log.getYmb());
				ps.setLong(22, log.getGmtA());
				ps.setLong(23, log.getGmtB());
				ps.setLong(24, log.getGmtC());
				ps.setLong(25, log.getGmtD());
				ps.setString(26, log.getUrl());
				ps.setInt(27, log.getState());
				ps.setLong(28, log.getTime());
				return ps.executeUpdate();
			} // Not Add
			return 0;
		} finally {
			ps.close();
		}
	}

	public void saveAuto(LogOrder log) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			conn.setAutoCommit(false);
			UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, log.getUid());
			r.setSid(log.getSid() + 100); // 回款
			r.setFid(0);
			r.setTid(APState.TRADE_ADD5);
			r.setWay(APState.EVENT_P015);
			r.setEvent("回款：" + log.getName());
			r.add(log.getYma());
			r.setTime(log.getTime());
			if (this.getUserMoneyService().update(conn, r) >= 1) {
				// Ignored
			} else if (this.getUserMoneyService().insert(conn, r) >= 1) {
				BigDecimal sy = log.getYma().subtract(log.getTmb());
				this.getUserInfoService().updateAddTZ(conn, r.getUid(), log.getYma(), sy, log.getTime());
			}
			this.adds(conn, log);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(conn);
		}
	}

	public void updateAny() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long time = (GState.USER_TODAX - 1);
			ps = JPrepare.prepareStatement(conn, "SELECT Sid,Uid,Any,Day,GmtC,GmtD,State,Time FROM " + Table.TBL_LOG_ORDER + " WHERE State<=? AND GmtD<=?");
			ps.setInt(1, SALE_A3); // 回款中
			ps.setLong(2, time);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				long gmtC = rs.getLong(5);
				if (GState.USER_TODAX >= gmtC) {
					rs.updateInt(3, 0); // 今日回款
					rs.updateLong(6, gmtC);
					rs.updateInt(7, SALE_A3);
				} else {
					rs.updateInt(3, VeRule.toAny(gmtC));
					rs.updateLong(6, GState.USER_TODAX);
					rs.updateInt(7, SALE_A2);
				}
				rs.updateRow();
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public List<ProdOrder> findOrderByPid(long Pid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			List<ProdOrder> ls = new ArrayList<ProdOrder>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			ps = conn.prepareStatement(SQL_ORDER_BY_PID);
			ps.setLong(1, Pid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ProdOrder o = new ProdOrder();
				o.setSid(rs.getLong(1));
				o.setMoney(DF2.format(rs.getFloat(3)));
				o.setTime(GMTime.format(rs.getLong(4), GMTime.CHINA, sdf));
				o.setName(VeRule.toStar(rs.getString(6), 4, -1, 3, 4, 4, "-"));
				ls.add(o); // 数据
			}
			rs.close();
			return ls;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isNewByUid(long uid) throws SQLException {
		return JPrepare.isExists("SELECT Sid FROM " + Table.TBL_LOG_ORDER + " WHERE Uid=? AND Tid=?", uid, 1);
	}

}
