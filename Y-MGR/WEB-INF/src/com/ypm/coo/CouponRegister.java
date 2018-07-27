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
import com.ypm.util.VeStr;

public class CouponRegister extends Action {

	private static final long serialVersionUID = 8020883363637830174L;

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
		int state = this.getInt("State", -1);
		if (state >= 0) {
			sql.append(" AND State=?");
			fs.add(state);
		}
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

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String name = this.getString("Name");
		try {
			UserSession us = this.getUserSession();
			long sday = GMTime.valueOf(getString("Sday"));
			long eday = GMTime.valueOf(getString("Eday"));
			if (name == null) {
				json.addError(this.getText("coo.error.021"));
			} else if (sday >= eday) {
				json.addError(this.getText("coo.error.022"));
			} else {
				CouponRule r = null;
				long rid = this.getLong("Rid");
				if (rid >= 1) {
					r = this.getCouponInfoService().findCouponByRid(rid);
					if (r == null) {
						json.addError(this.getText("system.error.none"));
						return JSON;
					} else if (r.getState() >= STATE_READER) {
						json.addError(this.getText("coo.error.031"));
						return JSON;
					}
				} else {
					r = new CouponRule();
					r.setRid(VeStr.getUSid());
					r.setTotal(0);
				}
				r.setType(getInt("Type"));
				r.setName(name);
				r.setTma(getBigDecimal("TMA"));
				r.setTmb(getBigDecimal("TMB"));
				r.setToall(getBigDecimal("Toall"));
				r.setToday(getInt("Today"));
				r.setStid(getInt("Stid"));
				r.setTday(getInt("Tday"));
				r.setSday(sday);
				r.setEday(eday);
				r.setRemark(getString("Remark"));
				r.setState(STATE_NORMAL);
				r.setAdM(us.getUserId());
				r.setAdN(us.getUserName());
				this.getCouponInfoService().saveInfo(r);
				json.addMessage(this.getText("data.save.succeed"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			name = null;
		}
		return JSON;
	}

	/** 待发放状态 */
	public String state() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 15)) {
				this.getCouponInfoService().saveSok(ids);
				json.addMessage(this.getText("data.update.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			ids = null;
		}
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

	/** 添加优惠券发放对象 */
	public String adds() {
		AjaxInfo json = this.getAjaxInfo();
		String user = this.getString("Info");
		try {
			long rid = this.getLong("Rid");
			CouponRule r = this.getCouponInfoService().findCouponByRid(rid);
			if (r == null) {
				json.addError(this.getText("system.error.none"));
			} else if (r.getState() >= STATE_READER) {
				json.addError(this.getText("coo.error.031"));
			} else {
				this.getCouponInfoService().addUser(rid, user);
				json.addMessage(this.getText("data.save.succeed"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			user = null;
		}
		return JSON;
	}

	public String delUser() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			long rid = this.getLong("Rid");
			CouponRule r = this.getCouponInfoService().findCouponByRid(rid);
			if (r == null) {
				json.addError(this.getText("system.error.none"));
			} else if (r.getState() >= STATE_READER) {
				json.addError(this.getText("coo.error.031"));
			} else {
				this.getCouponInfoService().delUser(rid, ids);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.delete.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	public String delete() {
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
