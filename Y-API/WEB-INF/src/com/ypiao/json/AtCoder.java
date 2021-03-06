package com.ypiao.json;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.SendInfoService;
import com.ypiao.util.VeStr;
import org.apache.log4j.Logger;

public class AtCoder extends Action {

	private static final long serialVersionUID = 8958922066521328713L;

	private static Logger logger = Logger.getLogger(AtCoder.class);

	private static final int ALL = 0, NOT = 1, YES = 2;

	private SendInfoService sendInfoService;

	public AtCoder() {
		super(true);
	}

	public SendInfoService getSendInfoService() {
		return sendInfoService;
	}

	public void setSendInfoService(SendInfoService sendInfoService) {
		this.sendInfoService = sendInfoService;
	}

	public String check() {
		AjaxInfo json = this.getAjaxInfo();
		String code = this.getString("vcode");

		// TODO 验证码写死 测试用
		code = String.valueOf(123456);
		String mobile = this.getString("mobile");
		try {
			if (code == null || mobile == null) {
				json.addError(this.getText("user.error.007"));
			} else {
				String sm = VeStr.getMobile(mobile);
				if (sm == null) {
					json.addError(this.getText("user.error.007"));
				} else if (this.getSendInfoService().isCode(sm, code)) {
					json.addMessage("OK!");
				} else {
					json.addError(this.getText("user.error.008"));
				}
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		} finally {
			code = mobile = null;
		}
		logger.info("json:"+json.toString());
 return JSON;
	}

	/** 账号存在=(取回密码) */
	public String get() {
		return send(100, YES);
	}

	/** 账号不存在=(更改类型) */
	public String index() {
		return send(100, NOT);
	}

	/** 存在/不存在=(登录/注册) */
	public String login() {
		return send(100, ALL);
	}

	private String send(int tid, int exist) {
		AjaxInfo json = this.getAjaxInfo();
		String fix = "+86-";
		String mobile = this.getString("mobile");
		try {
			logger.info(cache);
			if (mobile == null) {
				json.addError(this.getText("user.error.010"));
				logger.info("json:"+json.toString());
 return JSON;
			} // 转换手机号码
			String sm = VeStr.getMobile(fix, mobile);
			if (sm == null) {
				json.addError(this.getText("user.error.010"));
				logger.info("json:"+json.toString());
 return JSON;
			} else if (ALL == exist) {
				// Ignored
			} else if (this.getSendInfoService().isUser(sm)) {
				if (NOT == exist) {
					json.addError(this.getText("user.error.011"));
					logger.info("json:"+json.toString());
 return JSON;
				}
			} else if (YES == exist) {
				json.addError(this.getText("user.error.013"));
				logger.info("json:"+json.toString());
 return JSON;
			} // 检测发送状态
			String addr = VeStr.getRemoteAddr(request);
			switch (this.getSendInfoService().detect(addr, sm)) {
			case SendInfoService.STA:
				json.success(0);
				break;
			case SendInfoService.STC:
				json.addError(this.getText("user.error.001"));
				break;
			case SendInfoService.STD:
				json.addError(this.getText("user.error.004"));
				break;
			case SendInfoService.STE:
				json.addError(this.getText("user.error.005"));
				break;
			default:
				if (this.getSendInfoService().sendCode(addr, fix, sm, tid)) {
					json.success(0);
				} else {
					json.addError(this.getText("user.error.006"));
				}
			}
		} catch (SQLException e) {
			json.addError(this.getText("user.error.006"));
		} finally {
			fix = mobile = null;
		}
		logger.info("json:"+json.toString());
 return JSON;
	}
}
