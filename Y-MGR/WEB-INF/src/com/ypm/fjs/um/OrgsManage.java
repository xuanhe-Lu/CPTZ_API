package com.ypm.fjs.um;

import java.sql.SQLException;
import com.ypm.Action;
import com.ypm.bean.AdminOrgs;
import com.ypm.bean.AjaxInfo;
import com.ypm.service.AdminRoleService;

public class OrgsManage extends Action {

	private static final long serialVersionUID = 3403148650304325432L;

	private AdminRoleService adminRoleService;

	public AdminRoleService getAdminRoleService() {
		return adminRoleService;
	}

	public void setAdminRoleService(AdminRoleService adminRoleService) {
		this.adminRoleService = adminRoleService;
	}

	public String getChildrens() {
		int code = this.getNode("Code");
		this.setAjaxInfo(this.getAdminRoleService().findOrgChildrens(code));
		return JSON;
	}

	public String getTree() {
		int code = this.getNode("Code");
		this.setAjaxInfo(this.getAdminRoleService().getOrgChildrens(code));
		return JSON;
	}

	public String tree() {
		return this.getTree();
	}

	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int code = this.getInt("Code");
			AdminOrgs org = this.getAdminRoleService().getAdminOrgById(code);
			if (org == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("TID", org.getType());
				json.append("CODE", org.getCode());
				json.append("NAME", org.getName());
				json.append("REMARK", org.getRemark());
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String name = this.getString("Name");
		if (name == null) {
			json.addError(this.getText("org.error.001"));
			return JSON;
		}
		try {
			AdminOrgs org = null;
			int code = this.getInt("Code");
			if (code <= 0) {
				if (this.getAdminRoleService().isAdminOrgByName(name)) {
					json.addError(this.getText("org.error.002", new String[]{name}));
					return JSON;
				}
				org = new AdminOrgs();
				AdminOrgs a = this.getAdminRoleService().getAdminOrgById(getInt("Tid"));
				if (a == null) {
					org.setType(0);
				} else {
					org.setType(a.getCode());
				}
			} else {
				org = this.getAdminRoleService().getAdminOrgById(code);
				if (org == null) {
					json.addError(this.getText("org.error.005"));
					return JSON;
				} else if (!org.getName().equalsIgnoreCase(name) && this.getAdminRoleService().isAdminOrgByName(name)) {
					json.addError(this.getText("org.error.002", new String[]{name}));
					return JSON;
				}
			}
			org.setName(name);
			org.setRemark(getString("Remark"));
			this.getAdminRoleService().saveOrg(org);
			json.addMessage(this.getText("data.save.succeed"));
		} catch (Exception e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			name = null;
		}
		return JSON;
	}

	public String move() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int code = this.getInt("Code");
			AdminOrgs org = this.getAdminRoleService().getAdminOrgById(code);
			if (org == null) {
				json.addError(this.getText("org.error.005"));
			} else {
				int type = this.getInt("Type");
				int sortid = (this.getInt("Sortid") + 1);
				if (sortid <= 0) sortid = 1;
				if (type == org.getType()) {
					if (org.getSortid() != sortid) {
						this.getAdminRoleService().moveOrg(org, type, sortid);
					}
					json.addMessage(this.getText("data.order.succeed"));
				} else if (type == 0 || this.getAdminRoleService().isAdminOrgByCode(type)) {
					this.getAdminRoleService().moveOrg(org, type, sortid);
					json.addMessage(this.getText("data.order.succeed"));
				} else {
					json.addError(this.getText("org.error.003"));
				}
			}
		} catch (Exception e) {
			json.addError(this.getText("data.order.failed"));
		}
		return JSON;
	}

	public String order() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			int code = this.getInt("Code");
			this.getAdminRoleService().orderOrg(ids, code);
			json.addMessage(this.getText("data.order.succeed"));
		} catch (Exception e) {
			json.addError(this.getText("data.order.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int code = this.getInt("Code");
			AdminOrgs org = this.getAdminRoleService().getAdminOrgById(code);
			if (org == null) {
				json.addError(this.getText("org.error.005"));
			} else if (this.getAdminRoleService().isAdminOrgByType(org.getCode())) {
				json.addError(this.getText("org.error.006"));
			} else {
				this.getAdminRoleService().removeOrg(org);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.delete.failed"));
		}
		return JSON;
	}

}
