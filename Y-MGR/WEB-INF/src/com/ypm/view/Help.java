package com.ypm.view;

import com.ypm.bean.AjaxInfo;
import com.ypm.service.HelpService;

/**
 * Create by xk on 2018-05-10.
 * 
 * 常见问题接口数据.
 */
public class Help extends Action {

	private static final long serialVersionUID = 1L;
	
	// 注入HelpService
	private HelpService HelpService;
	
	public HelpService getHelpService() {
		return HelpService;
	}

	public void setHelpService(HelpService helpService) {
		HelpService = helpService;
	}

	@Override
	public String index() {
		StringBuilder sql = new StringBuilder();
		sql.append( "SELECT * FROM comm_help WHERE State = 0 " );
		sql.append( "ORDER BY Time DESC " );
		
		// 加载数据信息
		AjaxInfo ajaxInfo = this.getHelpService().findHelpByAll(sql);
		this.setAjaxInfo(ajaxInfo);
		
		return JSON;
	}
}
