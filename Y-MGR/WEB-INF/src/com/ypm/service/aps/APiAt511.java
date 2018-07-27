package com.ypm.service.aps;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.*;
import com.ypm.service.CouponInfoService;

public class APiAt511 extends Abstract {

	private CouponInfoService couponInfoService;

	public CouponInfoService getCouponInfoService() {
		return couponInfoService;
	}

	public void setCouponInfoService(CouponInfoService couponInfoService) {
		this.couponInfoService = couponInfoService;
	}

	public void save(Manager mgr) {
		try {
			CouponInfo info = mgr.getObject(CouponInfo.class);
			this.getCouponInfoService().save(info);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void rule(Manager mgr) {
		try {
			CouponRule rule = mgr.getObject(CouponRule.class);
			this.getCouponInfoService().save(rule);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void sok(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			long time = mgr.getLong("time");
			this.getCouponInfoService().saveSok(ids, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		} finally {
			ids = null;
		}
	}

	public void addUser(Manager mgr) {
		try {
			List<CouponUser> ls = mgr.getList(CouponUser.class);
			this.getCouponInfoService().save(ls);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void delUser(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			long rid = mgr.getLong("rid");
			this.getCouponInfoService().delete(rid, ids);
		} catch (SQLException e) {
			mgr.addError(DATA_DELETE_FAILED);
		} finally {
			ids = null;
		}
	}

	public void refuse(Manager mgr) {
		try {
			long rid = mgr.getLong("rid");
			long time = mgr.getLong("time");
			this.getCouponInfoService().update(rid, STATE_CHECK, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}

	public void sendQM(Manager mgr) {
		try {
			CouponRule rule = mgr.getObject(CouponRule.class);
			this.getCouponInfoService().saveQM(rule);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}

}
