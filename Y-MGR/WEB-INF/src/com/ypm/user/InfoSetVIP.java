package com.ypm.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.InfoVIPS;
import com.ypm.service.UserRoleService;

public class InfoSetVIP extends Action {

	private static final long serialVersionUID = 29417952189505494L;

	private UserRoleService userRoleService;

	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int vip = this.getInt("VIP");
			InfoVIPS v = this.getUserRoleService().findInfoByVIP(vip);
			if (v == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("VIP", v.getVip());
				json.append("NAME", v.getName());
				json.append("RATE", v.getRate());
				json.append("RATS", v.getRats());
				json.append("SALE", v.getSale());
				json.append("SNUM", v.getSNum());
				json.append("SRMB", v.getSRmb());
				json.append("STXT", v.getSTxt());
				json.append("SOK", v.getSok());
				json.append("GMA", v.getGma());
				json.append("GMB", v.getGmb());
				json.append("GNA", v.getGna());
				json.append("GNB", v.getGnb());
				json.append("RDAY", v.getRday());
				json.append("REMARK", v.getRemark());
				json.append("STATE", v.getState());
				json.append("TIME", v.getTime());
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		String key = this.getString("key");
		if (key == null) {
			// Ignored
		} else {
			sql.append(" AND Name LIKE ?");
			fs.add('%' + key + '%');
		} // 排序信息
		StringBuilder sb = new StringBuilder();
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("vip")) {
			sb.append("VIP");
			sort = "VIP";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("vip")) {
			if (dir == null || dir.equalsIgnoreCase("ASC")) {
				sb.append(" ASC");
			} else {
				sb.append(" DESC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,VIP DESC");
		} else {
			sb.append(" ASC,VIP DESC");
		} // 加载数据信息
		this.setAjaxInfo(this.getUserRoleService().findVIPByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		int vip = this.getInt("VIP", -1);
		String name = this.getString("Name");
		try {
			InfoVIPS v = null;
			if (name == null) {
				json.addError(this.getText("user.error.051"));
			} else if (vip >= 0) {
				v = this.getUserRoleService().findInfoByVIP(vip);
				if (v == null) {
					json.addError(this.getText("system.error.none"));
				}
			} else {
				v = new InfoVIPS();
				v.setVip(-1);
			}
			if (v != null) {
				v.setName(name);
				v.setRate(getBigDecimal("Rate"));
				v.setRats(getBigDecimal("Rats"));
				v.setSale(getBigDecimal("Sale"));
				v.setSNum(getInt("SNum"));
				v.setSRmb(getBigDecimal("SRmb"));
				v.setSTxt(getString("STxt"));
				v.setSok(getInt("Sok"));
				v.setGma(getBigDecimal("GMa"));
				v.setGmb(getBigDecimal("GMb"));
				v.setGna(getBigDecimal("GNa"));
				v.setGnb(getBigDecimal("GNb"));
				v.setRday(getInt("Rday"));
				v.setState(getInt("State"));
				v.setRemark(getString("Remark"));
				this.getUserRoleService().saveVIP(v);
				if (vip < 0) {
					json.addMessage(this.getText("data.save.succeed"));
				} else {
					json.addMessage(this.getText("data.update.succeed"));
				}
			}
		} catch (SQLException e) {
			if (vip < 0) {
				json.addError(this.getText("data.save.failed"));
			} else {
				json.addError(this.getText("data.update.failed"));
			}
		} finally {
			name = null;
		}
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
