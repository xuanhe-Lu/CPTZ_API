package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ypm.bean.LogOrder;
import com.ypm.bean.SysOrder;
import com.ypm.bean.UserRmbs;
import com.ypm.data.JPrepare;
import com.ypm.service.*;
import com.ypm.util.APState;
import com.ypm.util.Table;

public class TradeInfoServiceImp extends AConfig implements TradeInfoService {

	private ProdInfoService prodInfoService;

	private UserCouponService userCouponService;

	private UserInfoService userInfoService;

	private UserMoneyService userMoneyService;

	private UserOrderService userOrderService;

	protected void checkSQL() {
	}

	public ProdInfoService getProdInfoService() {
		return prodInfoService;
	}

	public void setProdInfoService(ProdInfoService prodInfoService) {
		this.prodInfoService = prodInfoService;
	}

	public UserCouponService getUserCouponService() {
		return userCouponService;
	}

	public void setUserCouponService(UserCouponService userCouponService) {
		this.userCouponService = userCouponService;
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

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
	}

	public void save(LogOrder log) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, log.getUid());
			if (this.getUserOrderService().adds(conn, log) >= 1) {
				this.getProdInfoService().update(conn, log.getPid(), log.getTma());
				this.getUserInfoService().updateSubTZ(conn, log.getUid(), log.getTmb(), log.getYma(), log.getTime());
				r.setSid(log.getSid());
				r.setTid(APState.TRADE_OUT4); // 投资
				r.setWay("理财消费");
				r.setEvent("购买" + log.getName());
				r.sub(log.getTmb());
				r.setTime(log.getTime());
				this.getUserMoneyService().save(conn, r);
			}
			if (log.getCid() >= USER_UID_MAX) {
				this.getUserCouponService().update(conn, log.getCid(), log.getUid(), log.getSid(), log.getTime());
			}
		} finally {
			JPrepare.close(conn);
		}
	}

	public void save(SysOrder s) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + Table.TBL_SYS_ORDER + " SET State=?,Stext=?,Time=? WHERE Sid=?");
			ps.setInt(1, s.getState());
			ps.setString(2, s.getStext());
			ps.setLong(3, s.getTime());
			ps.setLong(4, s.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + Table.TBL_SYS_ORDER + " (Sid,Uid,Pid,Cid,Tid,Name,Rate,Rday,Day,Way,Ads,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,GmtD,Mobile,Nicer,State,Stext,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, s.getSid());
				ps.setLong(2, s.getUid());
				ps.setLong(3, s.getPid());
				ps.setLong(4, s.getCid());
				ps.setInt(5, s.getTid());
				ps.setString(6, s.getName());
				ps.setBigDecimal(7, s.getRate());
				ps.setInt(8, s.getRday());
				ps.setInt(9, s.getDay());
				ps.setString(10, s.getWay());
				ps.setBigDecimal(11, s.getAds());
				ps.setBigDecimal(12, s.getTma());
				ps.setBigDecimal(13, s.getTmb());
				ps.setBigDecimal(14, s.getTmc());
				ps.setBigDecimal(15, s.getTmd());
				ps.setBigDecimal(16, s.getTme());
				ps.setBigDecimal(17, s.getTmf());
				ps.setBigDecimal(18, s.getTmg());
				ps.setBigDecimal(19, s.getYma());
				ps.setBigDecimal(20, s.getYmb());
				ps.setLong(21, s.getGmtA());
				ps.setLong(22, s.getGmtB());
				ps.setLong(23, s.getGmtC());
				ps.setLong(24, s.getGmtD());
				ps.setString(25, s.getMobile());
				ps.setString(26, s.getNicer());
				ps.setInt(27, s.getState());
				ps.setString(28, s.getStext());
				ps.setLong(29, s.getTime());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
