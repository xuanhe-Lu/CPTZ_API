package com.ypiao.json;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.ProdInfoService;

public class AtActInfo extends Action {

	private static final long serialVersionUID = 1841352334196324844L;

	private ProdInfoService prodInfoService;

	public AtActInfo() {
		super(true);
	}

	public ProdInfoService getProdInfoService() {
		return prodInfoService;
	}

	public void setProdInfoService(ProdInfoService prodInfoService) {
		this.prodInfoService = prodInfoService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int adj = this.getInt("adj");
			int max = this.getInt("max");
			if (max <= 1) {
				max = 1;
			} // 下发数据信息
			this.getProdInfoService().sendByAdj(json, adj, max);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		logger.info("json:"+json.toString());
 return JSON;
	}
}
