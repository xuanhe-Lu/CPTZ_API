package com.ypiao.service.imp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.ypiao.bean.*;
import com.ypiao.service.*;
import com.ypiao.util.GState;
import com.ypiao.util.VeStr;

public class TriggerServiceImp extends AConfig implements TriggerService {

	private CouponInfoService couponInfoService;

	private UserCouponService userCouponService;

	protected void checkSQL() {
	}

	public CouponInfoService getCouponInfoService() {
		return couponInfoService;
	}

	public void setCouponInfoService(CouponInfoService couponInfoService) {
		this.couponInfoService = couponInfoService;
	}

	public UserCouponService getUserCouponService() {
		return userCouponService;
	}

	public void setUserCouponService(UserCouponService userCouponService) {
		this.userCouponService = userCouponService;
	}

	/** 计算转换后的券码 */
	private void compute(int tid, long uid, long time) throws SQLException {
		List<CouponInfo> ls = this.getCouponInfoService().findCouponByTid(tid, time);
		int size = (ls == null) ? 0 : ls.size();
		List<UserCoupon> fs = new ArrayList<UserCoupon>(size);
		if (size >= 1) {
			Calendar t = Calendar.getInstance();
			for (CouponInfo c : ls) {
				UserCoupon uc = new UserCoupon();
				uc.setSid(VeStr.getUSid());
				uc.setUid(uid);
				uc.setCid(c.getCid());
				uc.setWay(WAY_AUTO);
				uc.setName(c.getName());
				uc.setType(c.getType());
				uc.setTma(c.getTma());
				uc.setTmb(c.getTmb());
				uc.setToall(c.getToall());
				uc.setToday(c.getToday());
				uc.setSday(c.getSday());
				long eday = c.getEday();
				if (c.getStid() == 0) {
					// Ignroed
				} else if (c.getTday() >= 1) {
					t.setTimeInMillis(GState.USER_TODAX);
					t.add(Calendar.DATE, c.getTday());
					eday = t.getTimeInMillis();
				} else if (c.getEday() >= GState.USER_TODAY) {
					eday = GState.USER_TODAY;
				}
				uc.setEday(eday - 1000);
				uc.setRemark(c.getRemark());
				uc.setGmtA(time);
				uc.setGmtB(0); // 未使用
				uc.setState(STATE_NORMAL);
				uc.setTime(time);
				fs.add(uc);
			}
			this.getUserCouponService().save(fs, time);
			SyncMap.getAll().add("time", time).sender(SYS_A512, "save", fs);
		}
	}

	public void register(UserInfo info) throws SQLException {
		this.compute(1, info.getUid(), info.getTime());
		//新增邀请好友送邀请人1%加息券
		if(info.getUPS() !=0 && info.getUPS()>=100000) {
			this.compute(7, info.getUPS(), info.getTime());
		}
	}

	public void bindCard(long uid, long time) throws SQLException {
		this.compute(3, uid, time);
	}

	public void invite(long uid, long time) throws SQLException {
		this.compute(5, uid, time);
	}
}
