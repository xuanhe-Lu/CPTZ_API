package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.LogOrder;
import com.ypm.bean.Manager;
import com.ypm.bean.SysOrder;
import com.ypm.service.TradeInfoService;

public class APiAt850 extends Abstract {

	private TradeInfoService tradeInfoService;

	public TradeInfoService getTradeInfoService() {
		return tradeInfoService;
	}

	public void setTradeInfoService(TradeInfoService tradeInfoService) {
		this.tradeInfoService = tradeInfoService;
	}

	public void order(Manager mgr) {
		try {
			SysOrder order = mgr.getObject(SysOrder.class);
			this.getTradeInfoService().save(order);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void save(Manager mgr) {
		try {
			LogOrder order = mgr.getObject(LogOrder.class);
			this.getTradeInfoService().save(order);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
