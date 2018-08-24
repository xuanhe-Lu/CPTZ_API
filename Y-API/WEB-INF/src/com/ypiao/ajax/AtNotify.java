package com.ypiao.ajax;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.LogCharge;
import com.ypiao.bean.PayInfo;
import com.ypiao.service.PayInfoService;
import com.ypiao.service.UserChargeService;
import com.ypiao.sign.Fuiou;
import com.ypiao.util.APState;
import com.ypiao.util.AUtils;
import com.ypiao.util.VeRule;
import org.apache.log4j.Logger;

public class AtNotify extends Action {

	private static final long serialVersionUID = -5674538058218981491L;

	private PayInfoService payInfoService;

	private UserChargeService userChargeService;

	public AtNotify() {
		super(true);
	}

	public PayInfoService getPayInfoService() {
		return payInfoService;
	}

	public void setPayInfoService(PayInfoService payInfoService) {
		this.payInfoService = payInfoService;
	}

	public UserChargeService getUserChargeService() {
		return userChargeService;
	}

	public void setUserChargeService(UserChargeService userChargeService) {
		this.userChargeService = userChargeService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		json.addFailure();
		logger.info("json:"+json.toString());
 return JSON;
	}

	public String ok() {
		AjaxInfo json = this.getAjaxInfo();
		json.addMessage("50");
		logger.info("json:"+json.toString());
 return JSON;
	}

	public String fuiou() {
		AjaxInfo json = this.getAjaxInfo();
		Map<String, String> map = new HashMap<String, String>();
		try {
			this.addParameter(map);
			logger.info(this.getRequest().getRemoteAddr() + "\t" + map);
			long sid = AUtils.toLong(map.get("MCHNTORDERID"));
			LogCharge c = this.getUserChargeService().findChargeBySid(sid);
			if (c == null) {
				json.addFailure();
				logger.info("json:"+json.toString());
 return JSON;
			} // 验证数据信息
			String res_code = map.get("RESPONSECODE");
			String res_msg = map.get("RESPONSEMSG");
			PayInfo pay = this.getPayInfoService().getInfoByFuiou();
			if (Fuiou.verify(map, pay.getSecret())) {
				c.setRes_code(res_code);
				c.setRes_msg(res_msg);
				BigDecimal amt = AUtils.toDeciml(map.get("AMT"));
				if ("0000".equalsIgnoreCase(res_code)) {
					c.setState(APState.ORDER_SUCCESS);
					c.setAmount(VeRule.toRMB(amt, 2));
					this.getUserChargeService().saveLog(c);
					String SNo = map.get("PROTOCOLNO");
					if (SNo != null) {
						this.getUserChargeService().bindProto(c, SNo);
					}
				} else {
					this.getUserChargeService().saveLog(c);
				}
			} else if (APState.ORDER_SUCCESS == c.getState()) {
				// Ignored
			} else {
				c.setRes_msg("返回结果验签失败");
				c.setState(APState.ORDER_SIGN_FAIL);
				this.getUserChargeService().saveLog(c);
			}
			json.addSuccess();
		} catch (Throwable e) {
			json.addFailure();
		} finally {
			map.clear();
		}
		logger.info("json:"+json.toString());
 return JSON;
	}
}
