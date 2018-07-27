package com.ypm.fjs.um;

import com.ypm.Action;
import com.ypm.bean.AdminDept;
import com.ypm.bean.AjaxInfo;
import com.ypm.service.AdminDeptService;

public class DeptManage extends Action {

	private static final long serialVersionUID = -5517426596730186890L;

	private AdminDeptService adminDeptService;

	public AdminDeptService getAdminDeptService() {
		return adminDeptService;
	}

	public void setAdminDeptService(AdminDeptService adminDeptService) {
		this.adminDeptService = adminDeptService;
	}

	public String list() {
		this.setAjaxInfo(this.getAdminDeptService().findDeptByAll(getStart(), getLimit()));
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String id = this.getString("ID");
		String name = this.getString("Name");
		try {
			if (name == null) {
				json.addError(this.getText("dept.error.001"));
			} else if (id == null || id.length() < 2) {
				if (this.getAdminDeptService().isDeptByName(name)) {
					json.addError(this.getText("dept.error.002", new String[] { name }));
				} else {
					AdminDept d = new AdminDept();
					d.setName(name);
					d.setRemark(this.getString("REMARK"));
					d.setHeader(this.getString("HEADER"));
					d.setViewer(this.getString("VIEWER"));
					this.getAdminDeptService().saveDept(d);
					json.addMessage(this.getText("data.save.succeed"));
				}
			} else {
				AdminDept d = this.getAdminDeptService().findDeptById(id);
				if (d == null) {
					json.addError(this.getText("dept.error.005"));
				} else if (!d.getName().equalsIgnoreCase(name) && this.getAdminDeptService().isDeptByName(name)) {
					json.addError(this.getText("dept.error.002", new String[] { name }));
				} else {
					d.setName(name);
					d.setRemark(this.getString("REMARK"));
					d.setHeader(this.getString("HEADER"));
					d.setViewer(this.getString("VIEWER"));
					this.getAdminDeptService().saveDept(d);
					json.addMessage(this.getText("data.update.succeed"));
				}
			}
		} catch (Exception e) {
			if (id == null) {
				json.addError(this.getText("data.update.failed"));
			} else {
				json.addError(this.getText("data.save.failed"));
			}
		} finally {
			id = name = null;
		}
		return JSON;
	}

	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (ids == null || ids.length() < 5) {
				json.addError(this.getText("system.error.pars"));
			} else {
				this.getAdminDeptService().removeDept(ids);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.delete.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}
}
