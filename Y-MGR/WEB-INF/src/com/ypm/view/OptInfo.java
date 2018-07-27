package com.ypm.view;

public class OptInfo extends Action {

	private static final long serialVersionUID = -8779547853142352808L;

	@Override
	public String index() {
		this.getAjaxInfo().setBody("");
		return JSON;
	}

}
