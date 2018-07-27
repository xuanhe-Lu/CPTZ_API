package com.ypm.view;

import com.ypm.service.DictInfoService;

public class Dictionary extends Action {

	private static final long serialVersionUID = -7404121736891950101L;

	private DictInfoService dictInfoService;

	private String sid;

	public DictInfoService getDictInfoService() {
		return dictInfoService;
	}

	public void setDictInfoService(DictInfoService dictInfoService) {
		this.dictInfoService = dictInfoService;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getDictList() {
		this.setAjaxInfo(this.getDictInfoService().getDictInfsBySid(this.getSid()));
		return JSON;
	}

	public String index() {
		this.setAjaxInfo(this.getDictInfoService().getDictInfoBySid(this.getSid()));
		return JSON;
	}

}
