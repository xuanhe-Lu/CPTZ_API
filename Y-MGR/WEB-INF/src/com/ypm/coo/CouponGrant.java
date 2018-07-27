package com.ypm.coo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.CouponRule;
import com.ypm.bean.UserSession;
import com.ypm.service.CouponInfoService;
import com.ypm.util.GMTime;

public class CouponGrant extends Action {

	private static final long serialVersionUID = 2819946922118457954L;

	private CouponInfoService couponInfoService;

	public CouponInfoService getCouponInfoService() {
		return couponInfoService;
	}

	public void setCouponInfoService(CouponInfoService couponInfoService) {
		this.couponInfoService = couponInfoService;
	}

	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long rid = this.getLong("Rid");
			CouponRule r = this.getCouponInfoService().findCouponByRid(rid);
			if (r == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("RId", r.getRid());
				json.append("TYPE", r.getType());
				json.append("NAME", r.getName());
				json.append("TMA", r.getTma());
				json.append("TMB", r.getTmb());
				json.append("TOALL", r.getToall());
				json.append("TODAY", r.getToday());
				json.append("TOTAL", r.getTotal());
				json.append("STID", r.getStid());
				json.append("TDAY", r.getTday());
				json.append("SDAY", (r.getSday() + GMTime.CHINA));
				json.append("EDAY", (r.getEday() + GMTime.CHINA));
				json.append("REMARK", r.getRemark());
				json.append("STATE", r.getState());
				json.append("TIME", r.getTime());
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		sql.append(" AND State>=?");
		fs.add(STATE_READER);
		String key = this.getString("key");
		if (key == null) {
			// Ignored
		} else {
			sql.append(" AND NAME LIKE ?");
			fs.add('%' + key + '%');
		} // 排序信息
		StringBuilder sb = new StringBuilder();
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Rid")) {
			sb.append("Rid");
			sort = "Rid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Rid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Rid DESC");
		} else {
			sb.append(" ASC,Rid ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getCouponInfoService().findCouponByRid(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	/** 发放对象列表 */
	public String user() {
		long rid = this.getLong("Rid");
		StringBuilder sb = new StringBuilder();
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Rid")) {
			sb.append("Rid");
			sort = "Rid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Rid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Rid DESC");
		} else {
			sb.append(" ASC,Rid ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getCouponInfoService().findCouponByRid(rid, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String state() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long rid = this.getLong("Rid");
			int state = this.getInt("state");
			CouponRule r = this.getCouponInfoService().findCouponByRid(rid);
			if (r == null) {
				json.addError(this.getText("system.error.none"));
			} else if (state == r.getState()) {
				json.addMessage(this.getText("data.update.succeed"));
			} else if (STATE_READER == r.getState()) {
				if (STATE_CHECK == state) {
					this.getCouponInfoService().refuse(r);
				} else {
					UserSession us = this.getUserSession();
					r.setAdM(us.getUserId());
					r.setAdN(us.getUserName());
					this.getCouponInfoService().sendQM(r);
				}
				json.addMessage(this.getText("data.update.succeed"));
			} else {
				json.addError(this.getText("coo.error.030"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.update.failed"));
		}
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
