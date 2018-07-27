package com.ypm.view;

import com.ypm.bean.AjaxInfo;
import com.ypm.service.AderNoteService;

/**
 * Create by xk on 2018-05-07.
 * 
 * 通知管理接口数据.
 */
public class AderNote extends Action {

	private static final long serialVersionUID = 1L;
	
	// 注入AderNoteService
	private AderNoteService aderNoteService;
	
	public AderNoteService getAderNoteService() {
		return aderNoteService;
	}

	public void setAderNoteService(AderNoteService aderNoteService) {
		this.aderNoteService = aderNoteService;
	}

	@Override
	public String index() {
		StringBuilder sql = new StringBuilder();
		sql.append( "SELECT * FROM ader_note WHERE State = 1 " );
		sql.append( "ORDER BY Time DESC " );
		
		// 加载数据信息
		AjaxInfo ajaxInfo = this.getAderNoteService().findAderNoteByAll(sql);
		this.setAjaxInfo(ajaxInfo);
		
		return JSON;
	}
}
