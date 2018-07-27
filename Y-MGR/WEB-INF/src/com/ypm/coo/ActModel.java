package com.ypm.coo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.ActInfo;
import com.ypm.bean.AjaxInfo;
import com.ypm.service.ActivityService;
import com.ypm.util.GMTime;

public class ActModel extends Action {

	private static final long serialVersionUID = -2863790647900894222L;

	private ActivityService activityService;

	public ActivityService getActivityService() {
		return activityService;
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int adj = this.getInt("adj");
			ActInfo a = this.getActivityService().findActByAdj(adj);
			if (a == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("ADJ", a.getAdj());
				json.append("NAME", a.getName());
				json.append("RATE", a.getRate());
				json.append("REMARK", a.getRemark());
				json.append("SDAY", (a.getSday() + GMTime.CHINA));
				json.append("EDAY", (a.getEday() + GMTime.CHINA));
				json.append("STATE", a.getState());
				json.append("TIME", a.getTime());
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
		if (sort == null || sort.equalsIgnoreCase("Adj")) {
			sb.append("Adj");
			sort = "Adj";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Adj")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Adj DESC");
		} else {
			sb.append(" ASC,Adj ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getActivityService().findActByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String rule() {
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String name = this.getString("Name");
		try {
			long sday = GMTime.valueOf(getString("Sday"));
			long eday = GMTime.valueOf(getString("Eday"));
			if (name == null) {
				json.addError(this.getText("coo.error.041"));
			} else if (sday >= eday) {
				json.addError(this.getText("coo.error.042"));
			} else {
				ActInfo a = null;
				int adj = this.getInt("Adj");
				if (adj >= 1) {
					a = this.getActivityService().findActByAdj(adj);
					if (a == null) {
						json.addError(this.getText("system.error.none"));
						return JSON;
					}
				} else {
					a = new ActInfo();
				}
				a.setName(name);
				a.setRate(getBigDecimal("Rate"));
				a.setSday(sday);
				a.setEday(eday);
				a.setRemark(getString("Remark"));
				a.setState(STATE_NORMAL);
				// a.setAdM(us.getUserId());
				// a.setAdN(us.getUserName());
				this.getActivityService().saveInfo(a);
				json.addMessage(this.getText("data.save.succeed"));
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
				} // 状态处理
				this.getActivityService().saveState(ids, state);
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
