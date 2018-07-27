package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.LogOrder;
import com.ypiao.bean.Manager;
import com.ypiao.bean.SysOrder;
import com.ypiao.service.TradeInfoService;

public class APIAt850 extends Abstract {

	private TradeInfoService tradeInfoService;

	public TradeInfoService getTradeInfoService() {
		return tradeInfoService;
	}

	public void setTradeInfoService(TradeInfoService tradeInfoService) {
		this.tradeInfoService = tradeInfoService;
	}

	public void order(Manager mgr) {
		try {
			SysOrder sys = mgr.getObject(SysOrder.class);
			this.getTradeInfoService().save(sys);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void save(Manager mgr) {
		try {
			LogOrder log = mgr.getObject(LogOrder.class);
			this.getTradeInfoService().save(log);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
