package com.ypiao.json;

import java.math.BigDecimal;
import java.sql.SQLException;
import com.ypiao.bean.*;
import com.ypiao.service.*;
import com.ypiao.util.VeStr;

public class OnUserCash extends Action {

	private static final long serialVersionUID = 1944616117768811955L;

	private SysConfig sysConfig;

	private UserAuthService userAuthService;

	private UserCashService userCashService;

	private UserInfoService userInfoService;

	public OnUserCash() {
		super(true);
	}

	public SysConfig getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig) {
		this.sysConfig = sysConfig;
	}

	public UserAuthService getUserAuthService() {
		return userAuthService;
	}

	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	public UserCashService getUserCashService() {
		return userCashService;
	}

	public void setUserCashService(UserCashService userCashService) {
		this.userCashService = userCashService;
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public String commit() {
		AjaxInfo json = this.getAjaxInfo();
		String Pwd = this.getParameter("pwd");
		BigDecimal rmb = this.getBigDecimal("rmb");
		try {
			SysConfig sys = this.getSysConfig();
			BigDecimal min = sys.getSYSCashByMin();
			if (min.compareTo(BigDecimal.ZERO) <= 0) {
				min = BigDecimal.ONE;
			} // 检测提现金额
			if (min.compareTo(rmb) >= 1) {
				json.addError(this.getText("user.error.881", new String[] { DF2.format(min) }));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测支付密码
			if (Pwd == null || Pwd.length() < 6) {
				json.addError(this.getText("user.error.027"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测认证信息
			UserSession us = this.getUserSession();
			UserAuth a = this.getUserAuthService().findAuthByUid(us.getUid());
			if (a == null) {
				json.addError(this.getText("user.error.050"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 校对支付密码
			Pwd = VeStr.toMD5(Pwd); // 格式化
			if (!Pwd.equalsIgnoreCase(a.getPays())) {
				json.addError(this.getText("user.error.028"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测账户信息
			synchronized (doLock(us.getUid())) {
				UserStatus s = this.getUserInfoService().findUserStatusByUid(us.getUid());
				if (s == null) {
					json.addError(this.getText("user.error.888"));
				} else if (rmb.compareTo(s.getMb()) >= 1) {
					json.addError(this.getText("user.error.884"));
				} else {
					int m = (sys.getSYSCashByMonth() - s.getNm());
					UserCash c = new UserCash();
					c.setSid(VeStr.getUSid());
					c.setUid(us.getUid());
					c.setName(a.getName());
					c.setMobile(s.getMobile());
					c.setBkId(s.getBkId());
					c.setBkName(s.getBkName());
					c.setBkInfo(s.getBkInfo());
					c.setTma(rmb);
					if (m >= 1) {
						c.setTmb(BigDecimal.ZERO);
					} else {
						BigDecimal fee = sys.getSYSCashByFee();
						if (fee.compareTo(BigDecimal.ZERO) <= 0) {
							// Ignored
						} else if (fee.compareTo(rmb) >= 0) {
							json.addError(this.getText("user.error.880", new String[] { DF2.format(fee) }));
							System.out.println("json:"+json.toString());
 return JSON;
						} else {
							c.setTmb(fee);
						}
					} // 到账金额
					c.setTmc(rmb.subtract(c.getTmb()));
					c.setState(0); // 已申请
					if (this.getUserCashService().commit(c)) {
						json.success(API_OK);
					} else {
						json.addError(this.getText("user.error.884"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			json.addError(this.getText("user.error.885"));
		} finally {
			Pwd = null;
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			UserSession us = this.getUserSession();
			UserStatus s = this.getUserInfoService().findUserStatusByUid(us.getUid());
			if (s == null) {
				json.addError(this.getText("system.error.user"));
			} else {
				SysConfig sys = this.getSysConfig();
				int m = (sys.getSYSCashByMonth() - s.getNm());
				json.addObject();
				json.append("uid", us.getUid());
				json.append("mb", DF2.format(s.getMb()));
				if (m >= 1) {
					json.append("nb", m);
					json.append("fee", 0);
				} else {
					BigDecimal fee = sys.getSYSCashByFee();
					json.append("nb", 0);
					json.append("fee", DF2.format(fee));
				}
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

	public String list() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			UserSession us = this.getUserSession();
			this.getUserCashService().sendByUid(json, us.getUid());
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}
}
