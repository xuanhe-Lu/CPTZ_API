package com.ypiao.json;

import com.ypiao.bean.AjaxInfo;

public class OnUserProfile extends Action {

	private static final long serialVersionUID = 9177259006837719166L;

	public OnUserProfile() {
		super(true);
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		json.addError("错误信息哈！");
		return JSON;
	}
}
