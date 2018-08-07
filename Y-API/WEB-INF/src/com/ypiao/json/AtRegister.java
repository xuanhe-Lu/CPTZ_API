package com.ypiao.json;

import java.sql.SQLException;
import com.sunsw.http.protocol.HTTP;
import com.ypiao.bean.*;
import com.ypiao.service.*;
import com.ypiao.util.VeRule;
import com.ypiao.util.VeStr;

public class AtRegister extends Action {

	private static final long serialVersionUID = 1270519822817372651L;

	private SendInfoService sendInfoService;

	private UserInfoService userInfoService;

	private UserLogerService userLogerService;

	public AtRegister() {
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

	
	public UserLogerService getUserLogerService() {
		return userLogerService;
	}

	
	public void setUserLogerService(UserLogerService userLogerService) {
		this.userLogerService = userLogerService;
	}

	/**
	 * @return String
	 * 
	 * 邀请好友注册接口
	 */
	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		String fix = getString("fix");
		String Pwd = getString("pwd");
		String code = getString("vcode");
		// TODO 验证码写死 测试用
		code = String.valueOf(123456);
		String mobile = getString("mobile");
		try {
			String sm = VeStr.getMobile(fix, mobile);
			if (!this.getSendInfoService().isCode(sm, code)) {
				json.addError(this.getText("user.error.008"));
				System.out.println("json:"+json.toString());
 return JSON;
			} // 获取用户信息
			UserInfo info = this.getUserInfoService().findUserInfoBySM(sm);
			if (info == null) {
				long ups = this.getLong("ups");
				if (USER_UID_BEG >= ups) {
					ups = this.getLong("cid");
				} // 邀请人信息
				UserStatus s = this.getUserInfoService().findUserStatusByUid(ups);
				UserReger reg = new UserReger();
				reg.setAccount(sm); // 手机号
				reg.setGider(sm);
				reg.setNicer(sm);
				if (s == null) {
					reg.setUPS(0);
				} else {
					reg.setUPS(s.getUid());
					reg.setChannel(s.getMobile());
					reg.setRemark(s.getMobile());
				}
				String agent = request.getHeader(HTTP.USER_AGENT);
				if (VeRule.iPhone(agent)) {
					reg.setIOS(1);
				} else {
					reg.setIOS(0);
				} // Login Password
				reg.setPassword(VeStr.toMD5(Pwd));
				reg.setDevice(getString("device"));
				reg.setModel(getString("model"));
				reg.setRelease(getString("release"));
				reg.setSdk(getInt("sdk"));
				reg.setToken(getString("token"));
				reg.setRemote(VeStr.getRemoteAddr(request));
				this.getUserLogerService().register(reg);
				json.addMessage("恭喜你注册成功！");
			} else {
				json.addError(this.getText("您已注册过！"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		} finally {
			fix = code = mobile = null;
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}
}
