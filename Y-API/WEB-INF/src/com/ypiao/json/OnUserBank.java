package com.ypiao.json;

import java.io.IOException;
import java.sql.SQLException;
import org.commons.lang.RandomUtils;
import com.ypiao.bean.*;
import com.ypiao.fuiou.BankResponse;
import com.ypiao.fuiou.CardBinResponse;
import com.ypiao.service.*;
import com.ypiao.sign.Checker;
import com.ypiao.sign.Fuiou;
import com.ypiao.sign.JSON;
import com.ypiao.util.GMTime;
import com.ypiao.util.VeRule;
import com.ypiao.util.VeStr;

/**
 * 用户银行卡相关处理接口. 
 */
public class OnUserBank extends Action {

	private static final long serialVersionUID = 1655575881114989785L;

	private SenderService senderService;

	private PayInfoService payInfoService;

	private BankInfoService bankInfoService;

	private UserAuthService userAuthService;

	private UserBankService userBankService;

	private UserLogerService userLogerService;

	public OnUserBank() {
		super(true);
	}

	public SenderService getSenderService() {
		return senderService;
	}

	public void setSenderService(SenderService senderService) {
		this.senderService = senderService;
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

	public UserLogerService getUserLogerService() {
		return userLogerService;
	}

	public void setUserLogerService(UserLogerService userLogerService) {
		this.userLogerService = userLogerService;
	}

	/**
	 * 绑定银行卡.
	 */
	public String bind() {
		AjaxInfo json = this.getAjaxInfo();
		String code = this.getString("vcode");
		String cardNo = this.getString("cardno");
		try {
			if (code == null || code.length() < 4) {
				json.addError(this.getText("user.error.007"));
				System.out.println("json:"+json.toString());
 return JSON;
			} else if (!VeRule.isBank(cardNo)) {
				json.addError(this.getText("user.error.065"));
				System.out.println("json:"+json.toString());
 return JSON;
			}
			UserSession us = this.getUserSession();
			UserBank b = this.getUserBankService().findBankByCNo(cardNo);
			if (b == null) {
				json.addError(this.getText("user.error.063"));
			} else if (b.getUid() != us.getUid()) {
				json.addError(this.getText("user.error.065"));
			} else if (code.equalsIgnoreCase(b.getCode())) {
				this.getUserBankService().saveBind(b);
				us.setBinds(STATE_CHECK); // 已绑卡
				this.getUserLogerService().update(us);
				json.success(API_OK);
			} else {
				json.addError(this.getText("user.error.008"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			json.addError(this.getText("system.error.info"));
		} finally {
			cardNo = code = null;
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

	/**
	 * 银行卡换卡. 
	 */
	public String change() {
		AjaxInfo json = this.getAjaxInfo();
		String code = this.getString("vcode");
		String cardNo = this.getString("cardno");
		String mobile = this.getString("mobile");
		try {
			if (!VeRule.isBank(cardNo)) {
				json.addError(this.getText("user.error.061"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测预留手机号
			String sm = VeStr.getMobile(mobile);
			if (sm == null) {
				json.addError(this.getText("user.error.062"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测是否实名
			UserSession us = this.getUserSession();
			UserAuth a = this.getUserAuthService().findAuthByUid(us.getUid());
			if (a == null || a.getRtime() <= USER_TIME) {
				json.addError(this.getText("user.error.063"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 卡号已绑定
			boolean checked = true;
			UserBank b = this.getUserBankService().findBankByCNo(cardNo);
			if (b == null) {
				// Ignroed
			} else if (b.getState() == STATE_READER) {
				json.addError(this.getText("user.error.067"));
				System.out.println("json:"+json.toString());
 return JSON;
			} else if (b.getUid() == us.getUid()) {
				checked = false;
			} // 检测数据
			if (checked) {
				PayInfo pay = this.getPayInfoService().getInfoByFuiou();
				CardBinResponse res = Fuiou.cardBinQry(pay.getSellid(), pay.getSecret(), cardNo);
				if (!res.getRcd().equals("0000")) {
					json.addError(this.getText(res.getRdesc()));
					System.out.println("json:"+json.toString());
 return JSON;
				} // 调用京东万象数据接口
				BankResponse r = Checker.bankcard4(a.getName(), a.getIdCard(), cardNo, sm);
				if (!r.isFlag()) {
					json.addError(this.getText("user.error.065"));
					System.out.println("json:"+json.toString());
 return JSON;
				} // 将卡号信息存入数据库
				BankResponse.Data d = r.getResult().getData();
				JSON j = new JSON(d.getBankCardBin());
				b = new UserBank();
				b.setUid(us.getUid());
				b.setBid(getBankInfoService().findBankByCNo(cardNo));
				b.setCardNo(cardNo);
				b.setBankId(j.get("bankid"));
				String bk = j.getString("bankname");
				if (bk == null) {
					b.setBankName("农业银行");
				} else {
					b.setBankName(bk.replaceAll("(\\(\\d+\\))", ""));
				}
				b.setBinId(j.getInt("id"));
				b.setBinStat(j.getInt("binstat"));
				b.setCardName(j.get("cardname"));
				b.setCardTy(j.get("cardty"));
				b.setChannel(d.getChannel());
				b.setMobile(sm);
				b.setName(a.getName());
				b.setGmtA(GMTime.currentTimeMillis());
				b.setState(STATE_NORMAL);
			} // 检测是否发送验证码
			long time = GMTime.currentTimeMillis();
			if (code == null || code.length() < 4) {
				if ((time - b.getTime()) > USER_TIMEOUT) {
					b.setCode(String.valueOf(RandomUtils.randomNumeric(100000, 999999)));
				}
				this.getUserBankService().saveBank(b);
				this.getSenderService().sendByBank(b.getMobile(), b.getCode());
				json.success(API_OK);
			} else if (code.equalsIgnoreCase(b.getCode())) {
				UserBker bk = this.getUserBankService().findBkerByUid(us.getUid());
				bk.setBid(b.getBid());
				bk.setCNo(cardNo);
				bk.setMobile(mobile);
				bk.setName(a.getName());
				if (bk.getBa() == null || bk.getBa().length() < 10) {
					json.addError(this.getText("user.error.081"));
				} else if (bk.getBb() == null || bk.getBb().length() < 10) {
					json.addError(this.getText("user.error.082"));
				} else if (bk.getBc() == null || bk.getBc().length() < 10) {
					json.addError(this.getText("user.error.083"));
				} else if (bk.getBd() == null || bk.getBd().length() < 10) {
					json.addError(this.getText("user.error.084"));
				} else {
					b.setState(STATE_CHECK);
					bk.setState(STATE_CHECK);
					this.getUserBankService().saveBank(b);
					this.getUserBankService().saveBker(bk);
					json.success(API_OK);
				}
			} else {
				json.addError(this.getText("user.error.007"));
			}
		} catch (SQLException | IOException e) {
			json.addError(this.getText("user.error.065"));
		} finally {
			mobile = cardNo = code = null;
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		String cardNo = this.getString("cardno");
		String mobile = this.getString("mobile");
		try {
			if (!VeRule.isBank(cardNo)) {
				json.addError(this.getText("user.error.061"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测预留手机号
			String sm = VeStr.getMobile(mobile);
			if (sm == null) {
				json.addError(this.getText("user.error.062"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测是否实名
			UserSession us = this.getUserSession();
			UserAuth a = this.getUserAuthService().findAuthByUid(us.getUid());
			if (a == null || a.getRtime() <= USER_TIME) {
				json.addError(this.getText("user.error.063"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 卡号已绑定
			if (a.getBtime() > USER_TIME) {
				json.addError(this.getText("user.error.064"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 根据卡号获取记录
			boolean checked = true;
			UserBank b = this.getUserBankService().findBankByCNo(cardNo);
			if (b == null) {
				// Ignroed
			} else if (b.getState() == STATE_READER) {
				json.addError(this.getText("user.error.064"));
				System.out.println("json:"+json.toString());
 return JSON;
			} else if (b.getUid() == us.getUid()) {
				checked = false;
			} // 检测数据
			if (checked) {
				PayInfo pay = this.getPayInfoService().getInfoByFuiou();
				CardBinResponse res = Fuiou.cardBinQry(pay.getSellid(), pay.getSecret(), cardNo);
				if (!res.getRcd().equals("0000")) {
					json.addError(this.getText(res.getRdesc()));
					System.out.println("json:"+json.toString());
 return JSON;
				} // 调用京东万象数据接口
				BankResponse r = Checker.bankcard4(a.getName(), a.getIdCard(), cardNo, sm);
				if (!r.isFlag()) {
					json.addError(this.getText("user.error.065"));
					System.out.println("json:"+json.toString());
 return JSON;
				} // 将卡号信息存入数据库
				BankResponse.Data d = r.getResult().getData();
				JSON j = new JSON(d.getBankCardBin());
				b = new UserBank();
				b.setUid(us.getUid());
				b.setBid(getBankInfoService().findBankByCNo(cardNo));
				b.setCardNo(cardNo);
				b.setBankId(j.get("bankid"));
				String bk = j.getString("bankname");
				if (bk == null) {
					b.setBankName("农业银行");
				} else {
					b.setBankName(bk.replaceAll("(\\(\\d+\\))", ""));
				}
				b.setBinId(j.getInt("id"));
				b.setBinStat(j.getInt("binstat"));
				b.setCardName(j.get("cardname"));
				b.setCardTy(j.get("cardty"));
				b.setChannel(d.getChannel());
				b.setMobile(sm);
				b.setName(a.getName());
				b.setGmtA(GMTime.currentTimeMillis());
				b.setState(STATE_NORMAL);
			} // 检测是否更新验证码
			long time = GMTime.currentTimeMillis();
			if ((time - b.getTime()) > USER_TIMEOUT) {
				b.setCode(String.valueOf(RandomUtils.randomNumeric(100000, 999999)));
			} // 发送绑卡验证码
			this.getUserBankService().saveBank(b);
			this.getSenderService().sendByBank(b.getMobile(), b.getCode());
			json.success(API_OK);
		} catch (SQLException | IOException e) {
			json.addError(this.getText("user.error.065"));
		} finally {
			mobile = cardNo = null;
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			UserSession us = this.getUserSession();
			this.getUserBankService().sendByUid(json, us.getUid());
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

	public String chker() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			UserSession us = this.getUserSession();
			this.getUserBankService().sendByNew(json, us.getUid());
		} catch (SQLException e) {
			e.printStackTrace();
			json.addError(this.getText("system.error.get"));
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

}
