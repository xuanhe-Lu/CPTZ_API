package com.ypiao.json;

import java.math.BigDecimal;
import com.ypiao.bean.*;
import com.ypiao.fuiou.*;
import com.ypiao.service.*;
import com.ypiao.sign.Fuiou;
import com.ypiao.util.APState;
import com.ypiao.util.VeRule;
import com.ypiao.util.VeStr;

/**
 * 用户充值接口. 
 */
public class OnRecharge extends Action {

	private static final long serialVersionUID = -5437640323954068644L;

	private SysConfig sysConfig;

	private PayInfoService payInfoService;

	private BankInfoService bankInfoService;

	private UserAuthService userAuthService;

	private UserBankService userBankService;

	private UserChargeService userChargeService;

	public OnRecharge() {
		super(false);
	}

	public SysConfig getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig) {
		this.sysConfig = sysConfig;
	}

	public PayInfoService getPayInfoService() {
		return payInfoService;
	}

	public void setPayInfoService(PayInfoService payInfoService) {
		this.payInfoService = payInfoService;
	}

	public BankInfoService getBankInfoService() {
		return bankInfoService;
	}

	public void setBankInfoService(BankInfoService bankInfoService) {
		this.bankInfoService = bankInfoService;
	}

	public UserAuthService getUserAuthService() {
		return userAuthService;
	}

	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	public UserBankService getUserBankService() {
		return userBankService;
	}

	public void setUserBankService(UserBankService userBankService) {
		this.userBankService = userBankService;
	}

	public UserChargeService getUserChargeService() {
		return userChargeService;
	}

	public void setUserChargeService(UserChargeService userChargeService) {
		this.userChargeService = userChargeService;
	}

	/**
	 * @return string
	 * 
	 * 充值提交 
	 */
	public String commit() {
		AjaxInfo json = this.getAjaxInfo();
		String code = this.getString("vcode");
		try {
			long sid = this.getLong("sid");
			LogCharge c = this.getUserChargeService().findChargeBySid(sid);
			if (c == null) {
				json.addError(this.getText("system.error.none"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 构建付款信息
			c.setVercd(code); // 验证码
			UserSession us = this.getUserSession();
			UserProto p = this.getUserChargeService().findProtoByUid(us.getUid());
			synchronized (doLock(us.getUid())) {
				PayResponse res = null;
				PayInfo pay = this.getPayInfoService().getInfoByFuiou();
				if (p == null) {
					PayRequest req = new PayRequest();
					req.setMchntcd(pay.getSellid());
					req.setMchntorderid(String.valueOf(c.getSid()));
					req.setUserId(c.getUid());
					req.setOrderId(c.getOrderId());
					req.setBankcard(c.getBankCard());
					req.setMobile(c.getMobile().replace("+86-", ""));
					req.setVercd(code);
					req.setSignpay(c.getSignpay());
					req.setRem1(String.valueOf(c.getUid()));
					req.setUserIP(c.getHSIP());
					res = Fuiou.toPay(req, pay.getSecret());
				} else {
					ProtoPayRequest req = new ProtoPayRequest();
					req.setMchntcd(pay.getSellid());
					req.setMchntorderid(String.valueOf(c.getSid()));
					req.setUserId(p.getUserId());
					req.setOrderId(c.getOrderId());
					req.setProtocolno(p.getSNo());
					req.setVercd(code);
					req.setSignpay(c.getSignpay());
					req.setRem1(String.valueOf(c.getUid()));
					req.setUserIP(c.getHSIP());
					res = Fuiou.toPay(req, pay.getSecret());
				} // 执行付款操作
				boolean addM = false;
				if (res.getResponsecode().equals("0000")) {
					c.setState(APState.ORDER_PAY_NO_BACK);
					addM = true;
				} else {
					c.setState(APState.ORDER_USER_PAY_FAIL);
				} // 返回充值结果
				c.setRes_code(res.getResponsecode());
				c.setRes_msg(res.getResponsemsg());
				this.getUserChargeService().saveLog(c);
				if (addM) {
					UserRmbs r = this.getUserChargeService().recharge(c);
					if (r == null) {
						json.addError(this.getText("返回结果异常，请联系客服！"));
					} else {
						us.setMoney(r.getTotal()); // 账户余额
						json.addObject();
						json.append("uid", us.getUid());
						json.append("rmb", DF2.format(r.getTotal()));
					}
				} else {
					json.addError(res.getResponsemsg());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText("system.error.info"));
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 充值页面
	 */
	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		BigDecimal rmb = this.getBigDecimal("rmb");
		try {
			UserSession us = this.getUserSession();
			System.out.println(us.getBinds() + "\t" + us.getReals() + "==" + rmb);
			if (us.getBinds() != STATE_DISABLE) {
				json.addError(this.getText("user.error.070"));
				System.out.println("json:"+json.toString());
 return JSON;
			}
			if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
				json.addError(this.getText("user.error.071", new String[] { "2" }));
				System.out.println("json:"+json.toString());
 return JSON;
			}
			UserBank b = this.getUserBankService().findBankByUid(us.getUid());
			if (b == null) {
				json.addError(this.getText("user.error.070"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 获取银行限额
			BankInfo info = this.getBankInfoService().getBankByBid(b.getBid());
			if (info != null && rmb.compareTo(info.getToall()) >= 1) {
				json.addError(this.getText("user.error.072", new String[] { DF2.format(info.getToall()) }));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 执行充值操作
			LogCharge c = new LogCharge();
			c.setSid(VeStr.getUSid()); // 订单编号
			c.setUid(us.getUid());
			c.setAmount(rmb); // 交易金额
			c.setIdCard(b.getIdCard());
			c.setMobile(b.getMobile());
			c.setName(b.getName());
			c.setBankName(b.getBankName());
			c.setBankCard(b.getCardNo());
			c.setSigntp("MD5");
			c.setHSIP(VeStr.getRemoteAddr(request));
			int amt = VeRule.toPer(rmb).intValue(); // 金额处理
			PayInfo pay = this.getPayInfoService().getInfoByFuiou();
			c.setBackUrl(pay.getNotifyUrl());
			UserProto p = this.getUserChargeService().findProtoByUid(us.getUid());
			/*synchronized (doLock(us.getUid())) {
				OrderResponse res = null;
				if (p == null) {
					OrderRequest req = new OrderRequest();
					req.setMchntcd(pay.getSellid());
					req.setMchntorderid(String.valueOf(c.getSid()));
					req.setUserId(c.getUid());
					req.setAmt(amt); // 支付金额
					req.setBankcard(c.getBankCard());
					req.setName(c.getName());
					req.setIdno(c.getIdCard());
					req.setMobile(c.getMobile().replace("+86-", ""));
					req.setBackurl(c.getBackUrl());
					req.setRem1(c.getHSIP());
					req.setUserIP(c.getHSIP());
					res = Fuiou.order(req, pay.getSecret()); // 首次充值
				} else if (p.getCNo().equalsIgnoreCase(b.getCardNo())) {
					ProtoRequest req = new ProtoRequest();
					req.setMchntcd(pay.getSellid());
					req.setMchntorderid(String.valueOf(c.getSid()));
					req.setUserId(p.getUserId());
					req.setAmt(amt); // 支付金额
					req.setProtocolno(p.getSNo()); // 协议号
					req.setBackurl(c.getBackUrl());
					req.setRem1(c.getHSIP());
					req.setUserIP(c.getHSIP());
					res = Fuiou.order(req, pay.getSecret()); // 协议支付
				} else {
					Fuiou.unBindCard(pay.getSellid(), pay.getSecret(), p.getUserId(), p.getSNo());
					p.setState(STATE_CHECK); // 更新状态信息
					this.getUserChargeService().unBindProto(p);
					OrderRequest req = new OrderRequest();
					req.setMchntcd(pay.getSellid());
					req.setMchntorderid(String.valueOf(c.getSid()));
					req.setUserId(c.getUid());
					req.setAmt(amt); // 支付金额
					req.setBankcard(c.getBankCard());
					req.setName(c.getName());
					req.setIdno(c.getIdCard());
					req.setMobile(c.getMobile().replace("+86-", ""));
					req.setBackurl(c.getBackUrl());
					req.setRem1(c.getHSIP());
					req.setUserIP(c.getHSIP());
					res = Fuiou.order(req, pay.getSecret()); // 新卡首次充值
				} // 记录充值信息
				c.setSignpay(res.getSignpay());
				if (res.getResponsecode().equals("0000")) {
					c.setOrderId(res.getOrderId());
					c.setState(APState.ORDER_USER_NO_VERCD);
					json.addObject();
					json.append("sid", c.getSid());
					json.append("orderid", c.getOrderId());
				} else {
					c.setState(APState.ORDER_USER_ACTION_FAIL);
					json.addError(res.getResponsemsg());
				}
				c.setRes_code(res.getResponsecode());
				c.setRes_msg(res.getResponsemsg());
			}*/
			this.getUserChargeService().saveLog(c);
		} catch (Throwable e) {
			e.printStackTrace();
			json.addError(this.getText("system.error.info"));
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}
}
