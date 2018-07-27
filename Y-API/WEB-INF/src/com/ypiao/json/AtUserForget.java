package com.ypiao.json;

import org.apache.log4j.Logger;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.SendInfoService;
import com.ypiao.service.UserInfoService;
import com.ypiao.util.VeStr;

/**
 * Update by xk on 2018-07-13.
 * 
 * 用户忘记密码接口.
 */
public class AtUserForget extends Action {

	private static final long serialVersionUID = 3967793525225527949L;
	
	private static final Logger LOGGER = Logger.getLogger(AtUserForget.class);

	private SendInfoService sendInfoService;

	private UserInfoService userInfoService;

	public AtUserForget() {
		super(true);
	}

	public SendInfoService getSendInfoService() {
		return sendInfoService;
	}

	public void setSendInfoService(SendInfoService sendInfoService) {
		this.sendInfoService = sendInfoService;
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		String fix = "+86";
		String Pwd = this.getString( "pwd" );
		String code = this.getString( "vcode" );
		String mobile = this.getString( "mobile" );
		
		try {
			if (code == null || mobile == null) {
				json.addError(this.getText( "user.error.007" ));
			} else if (Pwd == null || Pwd.length() < 6) {
				json.addError(this.getText( "user.error.016" ));
			} else {
				String sm = VeStr.getMobile( fix, mobile );
				if (sm == null) {
					json.addError(this.getText( "user.error.007" ));
				} else if (this.getSendInfoService().isCode( sm, code )) {
					if (Pwd.length() == 32) {
						this.getUserInfoService().updatePwd( sm, Pwd.toUpperCase() );
					} else {
						this.getUserInfoService().updatePwd( sm, VeStr.MD5(Pwd) );
					}
					json.success(API_OK);
				} else {
					json.addError(this.getText( "user.error.008" ));
				}
			}
		} catch (Exception e) {
			LOGGER.info( "用户忘记密码操作失败，异常信息：" + e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		} finally {
			fix = Pwd = code = mobile = null;
		}
		
		return JSON;
	}
}
