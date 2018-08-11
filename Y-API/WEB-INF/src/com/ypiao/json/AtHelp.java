package com.ypiao.json;

import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import com.sunsw.struts.ServletActionContext;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.HelpInfoService;

/**
 * Created by xk on 2018-06-22.
 * 
 * 常见问题数据接口. 
 */
public class AtHelp extends Action {

	private static final long serialVersionUID = 8598185592101882657L;

	private HelpInfoService helpInfoService;
	
	// 接收type=?
	private int type;
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public AtHelp() {
		super(true);
	}

	public HelpInfoService getHelpInfoService() {
		return helpInfoService;
	}

	public void setHelpInfoService(HelpInfoService helpInfoService) {
		this.helpInfoService = helpInfoService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			this.getHelpInfoService().sendByAll(json);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		logger.info("json:"+json.toString());
 return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 官网常见问题数据列表
	 */
	public String list () {
		// 解决接口调用跨域问题
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader( "Access-Control-Allow-Origin", "*" );
		
		AjaxInfo json = this.getAjaxInfo();
		try {
			this.getHelpInfoService().sendByList(json);
		} catch (SQLException e) {
			json.addError(this.getText( "system.error.get" ));
		}
		
		logger.info("json:"+json.toString());
 return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * app常见问题分类获取
	 */
	public String listByType () {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			this.getHelpInfoService().listByType( json, type );
		} catch (SQLException e) {
			json.addError(this.getText( "system.error.get" ));
		}
		
		logger.info("json:"+json.toString());
 return JSON;
	}
}
