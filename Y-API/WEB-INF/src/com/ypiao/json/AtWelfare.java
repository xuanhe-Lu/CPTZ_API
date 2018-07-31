package com.ypiao.json;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.WelfareService;

/**
 * Created by xk on 2018-06-12.
 * 
 * 福利专区数据接口. 
 */
public class AtWelfare extends Action {

	private static final long serialVersionUID = 8598185592101882657L;
	
	// Constructor
	public AtWelfare() {
		super(true);
	}
	
	// 注入 WelfareService
	private WelfareService welfareService;
	
	public WelfareService getWelfareService() {
		return welfareService;
	}

	public void setWelfareService(WelfareService welfareService) {
		this.welfareService = welfareService;
	}

	/**
	 * @return String
	 * 
	 * 福利专区数据列表，包含 "精选活动" 和 "近期活动"
	 */
	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			this.getWelfareService().sendByAll(json);
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}
	
}
