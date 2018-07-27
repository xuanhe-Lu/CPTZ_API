package com.ypiao.json;

import org.apache.log4j.Logger;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.ActInfoService;

/**
 * Update by xk on 2018-07-13.
 * 
 * 618活动信息接口.
 */
public class At20180618 extends Action {

	private static final long serialVersionUID = 4252641579940351664L;
	
	private static final Logger LOGGER = Logger.getLogger(At20180618.class);

	private ActInfoService actInfoService;

	public At20180618() {
		super(true);
	}

	public ActInfoService getActInfoService() {
		return actInfoService;
	}

	public void setActInfoService(ActInfoService actInfoService) {
		this.actInfoService = actInfoService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			long uid = this.getLong("uid");
			this.getActInfoService().findInfoByUid( json, uid );
		} catch (Exception e) {
			LOGGER.info( e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}
		
		return JSON;
	}

	public String list() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			long uid = this.getLong( "uid" );
			this.getActInfoService().findListByUid( json, uid );
		} catch (Exception e) {
			LOGGER.info( e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}
		
		return JSON;
	}
}
