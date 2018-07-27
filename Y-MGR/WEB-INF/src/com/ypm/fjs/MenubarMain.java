package com.ypm.fjs;

import com.ypm.Action;
import com.ypm.bean.AdminMenu;
import com.ypm.bean.AjaxInfo;
import com.ypm.service.AdminMenuService;
import com.ypm.util.AUtils;

public class MenubarMain extends Action {

	private static final long serialVersionUID = 6491641449849964077L;

	private AdminMenuService adminMenuService;

	public AdminMenuService getAdminMenuService() {
		return adminMenuService;
	}

	public void setAdminMenuService(AdminMenuService adminMenuService) {
		this.adminMenuService = adminMenuService;
	}

	public String getIcons() {
		this.setAjaxInfo(this.getAdminMenuService().getIcons());
		return JSON;
	}

	public String getChildrens() {
		String id = this.getString("code");
		this.setAjaxInfo(this.getAdminMenuService().findMenuListById(id));
		return JSON;
	}

	public String getMenuInfo() {
		AjaxInfo json = this.getAjaxInfo();
		AdminMenu menu = this.getAdminMenuService().findAdminMenuById(getString("code"));
		if (menu == null) {
			json.addError(this.getText("system.error.none"));
		} else {
			json.data();
			json.append("ID", menu.getId());
			json.append("CODE", menu.getId());
			json.append("NAME", menu.getName());
			json.append("ORDER", menu.getSortid());
			json.append("MODULEID", menu.getModule());
			json.append("ICONNAME", menu.getIcon());
			json.append("REMARK", menu.getRemark());
		}
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String name = this.getString("name");
		if (name == null) {
			json.addError(this.getText("menu.error.001"));
			return JSON;
		} // 构建参数信息
		String id = this.getString("code");
		try {
			AdminMenu menu = new AdminMenu();
			if (id == null) {
				menu.setId(-1);
				menu.setTid(this.getInt("tid"));
			} else {
				menu.setId(AUtils.toInt(id));
			}
			menu.setName(name);
			menu.setModule(getString("ModuleId"));
			menu.setRemark(getString("Remark"));
			menu.setIcon(getString("ICONNAME"));
			this.getAdminMenuService().saveMenu(menu);
			if (id == null) {
				json.addMessage(this.getText("data.save.succeed"));
			} else {
				json.addMessage(this.getText("data.update.succeed"));
			}
		} catch (Exception e) {
			if (id == null) {
				json.addMessage(this.getText("data.save.failed"));
			} else {
				json.addMessage(this.getText("data.update.failed"));
			}
		} finally {
			id = name = null;
		}
		return JSON;
	}

	public String move() {
		AjaxInfo json = this.getAjaxInfo();
		String id = this.getString("code");
		try {
			int tid = this.getInt("tid");
			int index = (getInt("sid") + 1);
			this.getAdminMenuService().moveMenu(id, tid, index);
			json.addMessage(this.getText("data.update.succeed"));
		} catch (Exception e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			id = null;
		}
		return JSON;
	}

	public String order() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int tid = this.getInt("code");
			this.getAdminMenuService().orderMenu(tid, getString("Ids"));
			json.addMessage(this.getText("data.order.succeed"));
		} catch (Exception e) {
			json.addError(this.getText("data.order.failed"));
		}
		return JSON;
	}

	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int id = this.getInt("code");
			if (id <= 0) {
				json.addError(this.getText("system.error.pars"));
			} else if (this.getAdminMenuService().isMenuByTid(id)) {
				json.addError(this.getText("menu.error.006"));
			} else {
				this.getAdminMenuService().removeMenu(id);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.delete.failed"));
		}
		return JSON;
	}

}
