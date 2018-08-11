package com.ypiao.json;

import javax.servlet.http.HttpServletResponse;

import com.sunsw.struts.ServletActionContext;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.AderNoteService;

/**
 * Create by xk on 2018-05-15.
 * 
 * 通知管理数据接口.
 */
public class AtAderNote extends Action {

	private static final long serialVersionUID = 8598185592101882657L;
	
	// Constructor
	public AtAderNote() {
		super(true);
	}
	
	// 注入 AderNoteService
	private AderNoteService aderNoteService;

	public AderNoteService getAderNoteService() {
		return aderNoteService;
	}

	public void setAderNoteService(AderNoteService aderNoteService) {
		this.aderNoteService = aderNoteService;
	}

	/**
	 * @return String
	 * 
	 * 数据列表
	 */
	public String index() {
		// 解决接口调用跨域问题
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader( "Access-Control-Allow-Origin", "*" );
		
		AjaxInfo json = this.getAjaxInfo();
		try {
			this.getAderNoteService().sendByAll(json);
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}
		
		logger.info("json:"+json.toString());
 return JSON;
	}
}
