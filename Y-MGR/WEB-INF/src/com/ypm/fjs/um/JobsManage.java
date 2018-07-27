package com.ypm.fjs.um;

import java.sql.SQLException;
import com.ypm.Action;
import com.ypm.bean.AdminJobs;
import com.ypm.bean.AjaxInfo;
import com.ypm.service.AdminRoleService;

public class JobsManage extends Action {

	private static final long serialVersionUID = -1720045670121588137L;

	private AdminRoleService adminRoleService;

	public AdminRoleService getAdminRoleService() {
		return adminRoleService;
	}

	public void setAdminRoleService(AdminRoleService adminRoleService) {
		this.adminRoleService = adminRoleService;
	}

	public String getChildrens() {
		int code = this.getNode("Code");
		this.setAjaxInfo(this.getAdminRoleService().findJobChildrens(code));
		return JSON;
	}

	public String getTree() {
		int code = this.getNode("Code");
		this.setAjaxInfo(this.getAdminRoleService().getJobChildrens(code));
		return JSON;
	}

	public String tree() {
		return this.getTree();
	}

	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int code = this.getInt("Code");
			AdminJobs job = this.getAdminRoleService().getAdminJobById(code);
			if (job == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("TID", job.getType());
				json.append("CODE", job.getCode());
				json.append("NAME", job.getName());
				json.append("REMARK", job.getRemark());
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
			json.addError(this.getText("job.error.001"));
			return JSON;
		}
		try {
			AdminJobs job = null;
			int code = this.getInt("Code");
			if (code <= 0) {
				if (this.getAdminRoleService().isAdminJobByName(name)) {
					json.addError(this.getText("job.error.002", new String[]{name}));
					return JSON;
				}
				job = new AdminJobs();
				AdminJobs a = this.getAdminRoleService().getAdminJobById(getInt("Tid"));
				if (a == null) {
					job.setType(0);
				} else {
					job.setType(a.getCode());
				}
			} else {
				job = this.getAdminRoleService().getAdminJobById(code);
				if (job == null) {
					json.addError(this.getText("job.error.005"));
					return JSON;
				} else if (!job.getName().equalsIgnoreCase(name) && this.getAdminRoleService().isAdminJobByName(name)) {
					json.addError(this.getText("job.error.002", new String[]{name}));
					return JSON;
				}
			}
			job.setName(name);
			job.setRemark(getString("Remark"));
			this.getAdminRoleService().saveJob(job);
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
			AdminJobs job = this.getAdminRoleService().getAdminJobById(code);
			if (job == null) {
				json.addError(this.getText("job.error.005"));
			} else {
				int type = this.getInt("Type");
				int sortid = (this.getInt("Sortid") + 1);
				if (sortid <= 0) sortid = 1;
				if (type == job.getType()) {
					if (job.getSortid() != sortid) {
						this.getAdminRoleService().moveJob(job, type, sortid);
					}
					json.addMessage(this.getText("data.order.succeed"));
				} else if (type == 0 || this.getAdminRoleService().isAdminJobByCode(type)) {
					this.getAdminRoleService().moveJob(job, type, sortid);
					json.addMessage(this.getText("data.order.succeed"));
				} else {
					json.addError(this.getText("job.error.003"));
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
			this.getAdminRoleService().orderJob(ids, code);
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
			AdminJobs job = this.getAdminRoleService().getAdminJobById(code);
			if (job == null) {
				json.addError(this.getText("job.error.005"));
			} else if (this.getAdminRoleService().isAdminJobByType(job.getCode())) {
				json.addError(this.getText("job.error.006"));
			} else {
				this.getAdminRoleService().removeJob(job);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.delete.failed"));
		}
		return JSON;
	}

}
