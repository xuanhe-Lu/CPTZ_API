package com.ypiao.json;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.service.*;

public class AtIndex extends Action {

	private static final long serialVersionUID = -9223083031123736290L;

	private AderInfoService aderInfoService;

	private AderNoteService aderNoteService;

	private ProdInfoService prodInfoService;

	private XuerInfoService xuerInfoService;

	public AtIndex() {
		super(true);
	}

	public AderInfoService getAderInfoService() {
		return aderInfoService;
	}

	public void setAderInfoService(AderInfoService aderInfoService) {
		this.aderInfoService = aderInfoService;
	}

	public AderNoteService getAderNoteService() {
		return aderNoteService;
	}

	public void setAderNoteService(AderNoteService aderNoteService) {
		this.aderNoteService = aderNoteService;
	}

	public ProdInfoService getProdInfoService() {
		return prodInfoService;
	}

	public void setProdInfoService(ProdInfoService prodInfoService) {
		this.prodInfoService = prodInfoService;
	}

	public XuerInfoService getXuerInfoService() {
		return xuerInfoService;
	}

	public void setXuerInfoService(XuerInfoService xuerInfoService) {
		this.xuerInfoService = xuerInfoService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			json.success(API_OK); // 成功状态
			UserSession us = this.getUserSession();
			this.getAderInfoService().sendIndex(json, us.getUid());
			this.getAderNoteService().sendIndex(json);
			this.getProdInfoService().sendIndex(json);
			this.getXuerInfoService().sendIndex(json);
		} catch (SQLException e) {
			json.addText("error", e.getMessage());
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}
}
