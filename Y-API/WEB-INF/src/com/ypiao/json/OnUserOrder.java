package com.ypiao.json;

import java.math.BigDecimal;
import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.LogOrder;
import com.ypiao.bean.UserSession;
import com.ypiao.service.TradeInfoService;
import com.ypiao.util.GMTime;

public class OnUserOrder extends Action {

	private static final long serialVersionUID = 2819203680803348816L;

	private TradeInfoService tradeInfoService;

	public OnUserOrder() {
		super(true);
	}

	public TradeInfoService getTradeInfoService() {
		return tradeInfoService;
	}

	public void setTradeInfoService(TradeInfoService tradeInfoService) {
		this.tradeInfoService = tradeInfoService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int state = this.getInt("state");
			if (state != SALE_A4) {
				state = SALE_A3;
			}
			UserSession us = this.getUserSession();
			this.getTradeInfoService().sendTreadByUid(json, us.getUid(), state);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		logger.info("json:"+json.toString());
 return JSON;
	}

	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long sid = this.getLong("sid");
			LogOrder log = this.getTradeInfoService().findLogOrderBySid(sid);
			if (log == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.addObject();
				json.append("sid", log.getSid());
				json.append("pid", log.getPid());
				json.append("name", log.getName());
				json.append("state", log.getState());
				json.append("tma", log.getTma());
				json.append("tms", DF2.format(log.getTmg()));
				StringBuilder sb = new StringBuilder();
				if (log.getTmc().compareTo(BigDecimal.ZERO) >= 1) {
					sb.append('，').append("抵扣：").append(DF2.format(log.getTmc()));
				}
				if (log.getTme().compareTo(BigDecimal.ZERO) >= 1) {
					sb.append('，').append("加息：").append(DF2.format(log.getTme())).append("%");
				} else if (sb.length() <= 0) {
					sb.append('，').append("无");
				} // 信息
				json.append("txt", sb.substring(1));
				json.append("total", log.getYma());
				json.append("gmta", GMTime.format(log.getGmtA(), GMTime.CHINA, GMTime.OUT_SHORT));
				json.append("gmtc", GMTime.format(log.getGmtC(), GMTime.CHINA, GMTime.OUT_SHORT));
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		logger.info("json:"+json.toString());
 return JSON;
	}
}