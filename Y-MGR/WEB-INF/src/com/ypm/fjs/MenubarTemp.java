package com.ypm.fjs;

import java.sql.SQLException;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.DictInfo;
import com.ypm.service.AdminMenuService;

public class MenubarTemp extends Action {

	private static final long serialVersionUID = 8709243295379917026L;

	private AdminMenuService adminMenuService;

	public AdminMenuService getAdminMenuService() {
		return adminMenuService;
	}

	public void setAdminMenuService(AdminMenuService adminMenuService) {
		this.adminMenuService = adminMenuService;
	}

	public String getChildrens() {
		String tid = this.getString("tid");
		this.setAjaxInfo(this.getAdminMenuService().getMenuListByTid(tid));
		return JSON;
	}

	public String getTemplate() {
		AjaxInfo json = this.getAjaxInfo();
		String tid = this.getString("tid");
		try {
			DictInfo info = this.getAdminMenuService().getTemplateById(tid);
			if (info == null) {
				json.addError(this.getText("system.error.pars"));
			} else {
				json.data();
				json.append("TID", info.getId());
				json.append("TNAME", info.getName());
				json.append("TDESC", info.getRemark());
			}
		} finally {
			tid = null;
		}
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String name = this.getString("TNAME");
		if (name == null) {
			json.addError(this.getText("menu.error.007"));
			return JSON;
		}
		try {
			DictInfo info = null;
			String tid = this.getString("MTID");
			if (tid != null && !tid.equals("-1")) {
				info = this.getAdminMenuService().getTemplateById(tid);
				if (info == null) {
					json.addError(this.getText("menu.error.009"));
					return JSON;
				} else if (!info.getName().equalsIgnoreCase(name) && this.getAdminMenuService().isTemplateByName(name)) {
					json.addError(this.getText("menu.error.008", new String[]{ name }));
					return JSON;
				}
			} else if (this.getAdminMenuService().isTemplateByName(name)) {
				json.addError(this.getText("menu.error.008", new String[]{ name }));
				return JSON;
			} else {
				info = new DictInfo();
			}
			info.setName(name);
			info.setValue(getString("MIDS"));
			info.setRemark(getString("TDESC"));
			this.getAdminMenuService().saveTemplate(info);
			json.addMessage(this.getText("data.save.succeed"));
		} catch (Exception e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			name = null;
		}
		return JSON;
	}

	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		String tid = this.getString("tid");
		try {
			if (tid == null) {
				json.addError(this.getText("system.error.pars"));
			} else {
				this.getAdminMenuService().removeTemplate(tid);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.delete.failed"));
		}
		return JSON;
	}

}
