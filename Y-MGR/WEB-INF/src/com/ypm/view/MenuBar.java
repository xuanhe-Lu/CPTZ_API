package com.ypm.view;

import com.ypm.bean.UserSession;
import com.ypm.service.AdminMenuService;

public class MenuBar extends Action {

	private static final long serialVersionUID = -959080881719644085L;

	public AdminMenuService adminMenuService;

	public AdminMenuService getAdminMenuService() {
		return adminMenuService;
	}

	public void setAdminMenuService(AdminMenuService adminMenuService) {
		this.adminMenuService = adminMenuService;
	}

	@Override
	public String index() {
		UserSession us = this.getUserSession();
		this.setAjaxInfo(this.getAdminMenuService().getMenuListByUid(us.getMenu()));
 return JSON;
	}

}
