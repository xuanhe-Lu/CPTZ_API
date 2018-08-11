package com.ypiao.json;

import org.apache.log4j.Logger;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.service.UserMoneyService;

/**
 * Update by xk on 2018-07-13.
 * 
 * 用户交易信息接口.
 */
public class OnUserTrade extends Action {

	private static final long serialVersionUID = 6948070445959558777L;
	
	private static final Logger LOGGER = Logger.getLogger(OnUserTrade.class);

	private UserMoneyService userMoneyService;

	public OnUserTrade() {
		super(true);
	}

	public UserMoneyService getUserMoneyService() {
		return userMoneyService;
	}

	public void setUserMoneyService(UserMoneyService userMoneyService) {
		this.userMoneyService = userMoneyService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			UserSession us = this.getUserSession();
			this.getUserMoneyService().sendMoneyByUid( json, us.getUid() );
		} catch (Exception e) {
			LOGGER.info( "获取用户交易信息失败，异常信息：" + e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}
		
		logger.info("json:"+json.toString());
 return JSON;
	}
}
