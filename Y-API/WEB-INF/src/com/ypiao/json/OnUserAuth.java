package com.ypiao.json;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.commons.lang.RandomUtils;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.IDModel;
import com.ypiao.bean.PayInfo;
import com.ypiao.bean.UserAuth;
import com.ypiao.bean.UserBank;
import com.ypiao.bean.UserSession;
import com.ypiao.fuiou.BankResponse;
import com.ypiao.fuiou.CardBinResponse;
import com.ypiao.service.BankInfoService;
import com.ypiao.service.PayInfoService;
import com.ypiao.service.SenderService;
import com.ypiao.service.UserAuthService;
import com.ypiao.service.UserBankService;
import com.ypiao.service.UserLogerService;
import com.ypiao.sign.AuthUtils;
import com.ypiao.sign.Checker;
import com.ypiao.sign.Fuiou;
import com.ypiao.sign.JSON;
import com.ypiao.util.AUtils;
import com.ypiao.util.GMTime;
import com.ypiao.util.VeRule;
import com.ypiao.util.VeStr;

/**
 * 用户实名认证接口. 
 */
public class OnUserAuth extends Action {

	private static final long serialVersionUID = -3173217311634297839L;

	private SenderService senderService;

	private PayInfoService payInfoService;

	private BankInfoService bankInfoService;

	private UserAuthService userAuthService;

	private UserBankService userBankService;

	private UserLogerService userLogerService;
	
	private static Logger LOGGER = Logger.getLogger(OnUserAuth.class);

	public OnUserAuth() {
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
	 * 实名认证提交处理. 
	 */
	public String commit() {
		AjaxInfo json = this.getAjaxInfo();
		String name = this.getString("name");
		String mobile = this.getString("mobile");
		String code = this.getString("vcode");
		// TODO 验证码写死 测试用
		code = String.valueOf(123456);
		String cardNo = this.getString("cardno");
		String idCard = this.getString("idcard");
		try {
			UserSession us = this.getUserSession();
			if (us.getReals() >= 1 && us.getBinds() >= 1) {
				json.addError(this.getText("user.error.051"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测录入信息
			if (name == null || name.length() <= 1) {
				json.addError(this.getText("user.error.052"));
				System.out.println("json:"+json.toString());
 return JSON;
			}
		/*	if (!VeRule.isIDCard(idCard)) {
				json.addError(this.getText("user.error.053"));
				System.out.println("json:"+json.toString());
 return JSON;
			} */// 检测银行卡号
			/*if (!VeRule.isBank(cardNo)) {
				json.addError(this.getText("user.error.061"));
				System.out.println("json:"+json.toString());
 return JSON;
			} */// 检测预留手机号
			String sm = VeStr.getMobile(mobile);
			if (sm == null) {
				json.addError(this.getText("user.error.062"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 实名信息加载
			UserAuth a = this.getUserAuthService().findAuthByUid(us.getUid());
			if (a == null) {
				if (this.getUserAuthService().isAuthByIdCard(idCard)) {
					json.addError(this.getText("user.error.054"));
					System.out.println("json:"+json.toString());
 return JSON;
				}
				a = new UserAuth();
				a.setUid(us.getUid());
				a.setState(STATE_NORMAL);
			} else if (idCard.equalsIgnoreCase(a.getIdCard())) {
				// Ignored
			} else if (this.getUserAuthService().isAuthByIdCard(idCard)) {
				json.addError(this.getText("user.error.054"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测是否实名认证
			if (name.equalsIgnoreCase(a.getName())) {
				// Ignroed
			} else {
				IDModel m = AuthUtils.realName(name, idCard);
				if (m.isFlag()) {
					a.setAddr(m.getAddress());
					a.setName(name);
					a.setIdCard(idCard);
					a.setGender(VeRule.toGender(m.getSex()));
					a.setRtime(GMTime.currentTimeMillis());
				} else {
					json.addError(this.getText("user.error.055"));
					System.out.println("json:"+json.toString());
 return JSON;
				}
			} // 根据卡号获取
			UserBank b = this.getUserBankService().findBankByCNo(cardNo);
			if (b == null) {
				b = new UserBank();
				b.setUid(us.getUid());
			} else if (b.getUid() != us.getUid()) {
				this.getUserAuthService().save(a);
				this.addActionError(this.getText("user.error.066"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 检测验证码发送
			if (code == null || code.length() <= 4) {
				this.getUserAuthService().save(a);
				long time = GMTime.currentTimeMillis();
				/*if (!sm.equalsIgnoreCase(b.getMobile())) {
					PayInfo pay = this.getPayInfoService().getInfoByFuiou();
					CardBinResponse res = Fuiou.cardBinQry(pay.getSellid(), pay.getSecret(), cardNo);
					if (!res.getRcd().equals("0000")) {
						json.addError(this.getText(res.getRdesc()));
						System.out.println("json:"+json.toString());
 return JSON;
					} // 调用京东万象数据接口
					BankResponse r = Checker.bankcard4(a.getName(), a.getIdCard(), cardNo, sm);
					if (!r.isFlag()) {
						LOGGER.info( "实名认证，调用京东万象数据接口失败，京东万象数据接口返回信息：" );
						LOGGER.info( r.toString() );
						LOGGER.info( r.getResult().toString() );
						json.addError(this.getText("user.error.065"));
						System.out.println("json:"+json.toString());
 return JSON;
					} // 将卡号信息存入数据库
					BankResponse.Data d = r.getResult().getData();
					if (d.getBankCardBin() == null) {
						b.setBankId("0");
						b.setBankName(res.getCnm());
						b.setBinId(AUtils.toInt(cardNo.substring(0, 6)));
						b.setBinStat(1);
						//b.setCardName("");
						b.setCardTy("D");
					} else {
						JSON j = new JSON(d.getBankCardBin());
						b.setBankId(j.get("bankid"));
						String bk = j.getString("bankname");
						if (bk == null) {
							b.setBankName(res.getCnm());
						} else {
							b.setBankName(bk.replaceAll("(\\(\\d+\\))", ""));
						}
						b.setBinId(j.getInt("id"));
						b.setBinStat(j.getInt("binstat"));
						b.setCardName(j.get("cardname"));
						b.setCardTy(j.get("cardty"));
					}
					b.setBid(getBankInfoService().findBankByCNo(cardNo));
					b.setCardNo(cardNo);
					b.setChannel(d.getChannel());
					b.setMobile(sm);
					b.setName(a.getName());
					b.setGmtA(time);
					b.setState(STATE_NORMAL);
				}*/ // 检测是否重新生成验证码
				long out = (time - b.getTime());
				if (out > USER_TIMEOUT) {
//					b.setCode(String.valueOf(RandomUtils.randomNumeric(100000, 999999)));
					//TODO 测试用，暂时写死 123456
					b.setCode(String.valueOf(123456));
				} // 发送绑卡验证码


				// TODO 测试用
				b = new UserBank();
				b.setUid(us.getUid());
				b.setBankId("0");
				b.setBankName("招商银行");
				b.setBinId(AUtils.toInt(cardNo.substring(0, 6)));
				b.setBinStat(1);
				//b.setCardName("");
				b.setCardTy("D");
				b.setBid(102);
				b.setCardNo(cardNo);
				b.setChannel("CUPS");
				b.setMobile(sm);
				b.setName(a.getName());
				b.setGmtA(time);
				b.setState(STATE_NORMAL);



				this.getUserBankService().saveBank(b);
				if (out > GMTime.MILLIS_PER_MIU) {
					boolean sendOK = this.getSenderService().sendByBank(b.getMobile(), b.getCode());
					LOGGER.info(sendOK);
					if (!sendOK) {
						LOGGER.info( "实名认证，发送绑卡验证码失败，请检查后台配置。" );
					}
				}
				json.success(API_OK);
			} else if (code.equalsIgnoreCase(b.getCode())) {
				this.getUserBankService().saveBind(b);
				us.setBinds(STATE_CHECK); // 已绑卡
				us.setReals(STATE_CHECK); // 已实名
				this.getUserLogerService().update(us);
				json.success(API_OK);
			} else {
				json.addError(this.getText("user.error.008"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText("system.error.info"));
		} finally {
			code = name = mobile = cardNo = idCard = null;
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

	/**
	 * 实名认证页面.
	 */
	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		String name = this.getString("name");
		String idCard = this.getString("idcard");
		try {
			UserSession us = this.getUserSession();
			if (us.getReals() >= 1) {
				json.addError(this.getText("user.error.051"));
			} else if (name == null || name.length() <= 1) {
				json.addError(this.getText("user.error.052"));
			} else if (!VeRule.isIDCard(idCard)) {
				json.addError(this.getText("user.error.053"));
			} else if (this.getUserAuthService().isAuthByIdCard(idCard)) {
				json.addError(this.getText("user.error.054"));
			} else {
				IDModel m = AuthUtils.realName(name, idCard);
				if (m.isFlag()) {
					UserAuth a = this.getUserAuthService().findAuthByUid(us.getUid());
					if (a == null) {
						a = new UserAuth();
						a.setUid(us.getUid());
						a.setState(STATE_NORMAL);
					} // 实名认证成功
					a.setAddr(m.getAddress());
					a.setName(name);
					a.setIdCard(idCard);
					a.setGender(VeRule.toGender(m.getSex()));
					a.setRtime(GMTime.currentTimeMillis());
					this.getUserAuthService().saveAuth(a);
					us.setGender(a.getGender());
					us.setReals(STATE_CHECK);
					us.setTime(a.getBtime());
					this.getUserLogerService().update(us);
					json.addObject();
					json.append("uid", us.getUid());
					json.append("vip", us.getVIP());
					json.append("gender", us.getGender());
					json.append("name", a.getName());
					json.append("nicer", us.getNicer());
					json.append("mobile", us.getMobile());
					json.append("idcard", VeRule.toStar(idCard, 6, 3, 3, "-"));
					json.append("binds", us.getBinds());
					json.append("reals", us.getReals());
				} else {
					json.addError(this.getText("user.error.055"));
				}
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		} finally {
			idCard = name = null;
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}
}
