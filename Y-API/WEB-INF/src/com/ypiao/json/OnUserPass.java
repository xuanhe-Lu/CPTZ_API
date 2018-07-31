package com.ypiao.json;

import org.apache.log4j.Logger;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserAuth;
import com.ypiao.bean.UserInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.service.SenderService;
import com.ypiao.service.UserAuthService;
import com.ypiao.service.UserInfoService;
import com.ypiao.util.VeStr;

/**
 * Update by xk on 2018-07-13.
 * 
 * 用户密码处理接口. 
 */
public class OnUserPass extends Action {

	private static final long serialVersionUID = 5598817972349457172L;
	
	private static final Logger LOGGER = Logger.getLogger(OnUserPass.class);

	private SenderService senderService;

	private UserAuthService userAuthService;

	private UserInfoService userInfoService;

	public OnUserPass() {
		super(true);
	}

	public SenderService getSenderService() {
		return senderService;
	}

	public void setSenderService(SenderService senderService) {
		this.senderService = senderService;
	}

	public UserAuthService getUserAuthService() {
		return userAuthService;
	}

	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		String Pass = this.getParameter( "pass" );
		
		try {
			if (Pass == null || Pass.length() < 6) {
				json.addError(this.getText( "user.error.023" ));
			} else {
				if (Pass.length() != 32) {
					Pass = VeStr.MD5(Pass);
				} 
				// 获取数据
				UserSession us = this.getUserSession();
				UserInfo info = this.getUserInfoService().findUserInfoByUid(us.getUid());
				if (info == null) {
					json.addError(this.getText( "user.error.024" ));
				} else if (Pass.equalsIgnoreCase(info.getPassword())) {
					json.success(API_OK);
				} else {
					json.addError(this.getText( "user.error.025" ));
				}
			}
		} catch (Exception e) {
			json.addError(this.getText( "system.error.none" ));
		} finally {
			Pass = null;
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}

	/**
	 * @return string
	 * 
	 * 修改支付密码
	 */
	public String modPay() {
		AjaxInfo json = this.getAjaxInfo();
		String code = this.getParameter( "vcode" );
		String Pwd = this.getParameter( "pwd" );
		
		try {
			if (code == null) {
				json.addError(this.getText( "user.error.007" ));
			} else if (Pwd == null || Pwd.length() < 6) {
				json.addError(this.getText( "user.error.026" ));
			} else {
				UserSession us = this.getUserSession();
				//if (this.getSenderService().isCode(us.getMobile(), code)) {
				if (code.length() >= 4) {
					UserAuth a = this.getUserAuthService().findAuthByUid(us.getUid());
					if (a == null) {
						a = new UserAuth();
						a.setUid(us.getUid());
						a.setGender(us.getGender());
					} 
					// 支付密码信息
					if (Pwd.length() == 32) {
						a.setPays(Pwd.toUpperCase());
					} else {
						a.setPays(VeStr.MD5(Pwd));
					}
					this.getUserAuthService().saveAuth(a);
					json.success(API_OK);
				} else {
					json.addError(this.getText( "user.error.008" ));
				}
			}
		} catch (Exception e) {
			LOGGER.info( "用户修改支付密码失败，异常信息：" + e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "data.save.failed" ));
		} finally {
			Pwd = code = null;
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 修改登录密码
	 */
	public String modPwd() {
		AjaxInfo json = this.getAjaxInfo();
		String Pwd = this.getParameter( "pwd" );
		String Pass = this.getParameter( "pass" );
		
		try {
			if (Pwd == null || Pwd.length() < 6) {
				json.addError(this.getText( "user.error.016" ));
			} else if (Pass == null || Pass.length() < 6) {
				json.addError(this.getText( "user.error.023" ));
			} else {
				if (Pass.length() != 32) {
					Pass = VeStr.MD5(Pass);
				} 
				// 获取数据
				UserSession us = this.getUserSession();
				UserInfo info = this.getUserInfoService().findUserInfoByUid(us.getUid());
				if (info == null) {
					json.addError(this.getText( "user.error.024" ));
				} else if (Pass.equalsIgnoreCase(info.getPassword())) {
					if (Pwd.length() == 32) {
						this.getUserInfoService().updatePwd(us.getUid(), Pwd.toUpperCase());
					} else {
						this.getUserInfoService().updatePwd(us.getUid(), VeStr.MD5(Pwd));
					}
					json.success(API_OK);
				} else {
					json.addError(this.getText( "user.error.025" ));
				}
			}
		} catch (Exception e) {
			LOGGER.info( "用户修改登录密码失败，异常信息：" + e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "system.error.none" ));
		} finally {
			Pass = Pwd = null;
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}
}
