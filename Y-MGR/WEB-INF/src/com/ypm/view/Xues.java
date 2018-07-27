package com.ypm.view;

import com.ypm.bean.AjaxInfo;
import com.ypm.service.XuesService;

/**
 * Create by xk on 2018-05-07.
 * 
 * 票友学堂接口数据.
 */
public class Xues extends Action {

	private static final long serialVersionUID = 1L;
	
	// 注入XuesService
	private XuesService xuesService;
	
	public XuesService getXuesService() {
		return xuesService;
	}

	public void setXuesService(XuesService xuesService) {
		this.xuesService = xuesService;
	}

	@Override
	public String index() {
		StringBuilder sql = new StringBuilder();
		sql.append( "SELECT * FROM xue_info WHERE State = 1 " );
		sql.append( "ORDER BY Time DESC " );
		
		// 加载数据信息
		AjaxInfo ajaxInfo = this.getXuesService().findXuesByAll(sql);
		this.setAjaxInfo(ajaxInfo);
		
		return JSON;
	}
}
