package com.ypm.coo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.CouponInfo;
import com.ypm.service.CouponInfoService;
import com.ypm.util.GMTime;

public class CouponManage extends Action {

	private static final long serialVersionUID = 4743581984437179142L;

	private CouponInfoService couponInfoService;

	public CouponInfoService getCouponInfoService() {
		return couponInfoService;
	}

	public void setCouponInfoService(CouponInfoService couponInfoService) {
		this.couponInfoService = couponInfoService;
	}

	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int cid = this.getInt("Cid");
			CouponInfo c = this.getCouponInfoService().findCouponByCid(cid);
			if (c == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("CID", c.getCid());
				json.append("TID", c.getTid());
				json.append("TYPE", c.getType());
				json.append("NAME", c.getName());
				json.append("TMA", c.getTma());
				json.append("TMB", c.getTmb());
				json.append("TOALL", c.getToall());
				json.append("TODAY", c.getToday());
				json.append("TOTAL", c.getTotal());
				json.append("COUNT", c.getCount());
				json.append("STID", c.getStid());
				json.append("TDAY", c.getTday());
				json.append("SDAY", (c.getSday() + GMTime.CHINA));
				json.append("EDAY", (c.getEday() + GMTime.CHINA));
				json.append("REMARK", c.getRemark());
				json.append("STATE", c.getState());
				json.append("TIME", c.getTime());
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		int tid = this.getInt("Tid", -1);
		if (tid >= 0) {
			sql.append(" AND Tid=?");
			fs.add(tid);
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
		if (sort == null || sort.equalsIgnoreCase("Cid")) {
			sb.append("Cid");
			sort = "Cid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Cid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Cid DESC");
		} else {
			sb.append(" ASC,Cid ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getCouponInfoService().findCouponByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String name = this.getString("Name");
		try {
			long sday = GMTime.valueOf(getString("Sday"));
			long eday = GMTime.valueOf(getString("Eday"));
			if (name == null) {
				json.addError(this.getText("coo.error.021"));
			} else if (sday >= eday) {
				json.addError(this.getText("coo.error.022"));
			} else {
				CouponInfo c = null;
				int cid = this.getInt("Cid");
				if (cid >= 1) {
					c = this.getCouponInfoService().findCouponByCid(cid);
					if (c == null) {
						json.addError(this.getText("system.error.none"));
					}
				} else {
					c = new CouponInfo();
				}
				if (c != null) {
					c.setTid(getInt("Tid"));
					c.setType(getInt("Type"));
					c.setName(name);
					c.setTma(getBigDecimal("TMA"));
					c.setTmb(getBigDecimal("TMB"));
					c.setToall(getBigDecimal("Toall"));
					c.setToday(getInt("Today"));
					c.setTotal(getBigDecimal("Total"));
					c.setCount(getInt("Count"));
					c.setStid(getInt("Stid"));
					c.setTday(getInt("Tday"));
					c.setSday(sday);
					c.setEday(eday);
					c.setRemark(getString("Remark"));
					c.setState(getInt("State"));
					if (c.getState() != STATE_ENABLE) {
						c.setState(STATE_DISABLE);
					}
					this.getCouponInfoService().saveInfo(c);
					json.addMessage(this.getText("data.save.succeed"));
				}
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			name = null;
		}
		return JSON;
	}

	public String state() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 1)) {
				int state = this.getInt("State");
				if (state != STATE_DISABLE) {
					state = STATE_ENABLE;
				}
				this.getCouponInfoService().saveState(ids, state);
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

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
