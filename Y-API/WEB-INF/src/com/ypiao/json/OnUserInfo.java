package com.ypiao.json;

import org.apache.log4j.Logger;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.bean.UserStatus;
import com.ypiao.service.SysConfig;
import com.ypiao.service.UserInfoService;
import com.ypiao.util.VeRule;
import com.ypiao.util.VeStr;

/**
 * Update by xk on 2018-07-13.
 * 
 * 用户信息接口.
 */
public class OnUserInfo extends Action {

	private static final long serialVersionUID = -2958295044815327393L;
	
	private static final Logger LOGGER = Logger.getLogger(OnUserInfo.class);

	private SysConfig sysConfig;

	private UserInfoService userInfoService;

	public OnUserInfo() {
		super(true);
	}

	public SysConfig getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig) {
		this.sysConfig = sysConfig;
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	/**
	 * @return string
	 * 
	 * 用户设置密码
	 */
	public String addPwd() {
		AjaxInfo json = this.getAjaxInfo();
		String Pwd = this.getString( "pwd" );
		
		try {
			if (Pwd == null || Pwd.length() < 6) {
				json.addError(this.getText( "user.error.016" ));
			}
			UserSession us = this.getUserSession();
			UserInfo info = this.getUserInfoService().findUserInfoByUid(us.getUid());
			LOGGER.info("info:"+info+"uid"+us.getUid());
//			if (info == null) {
//				json.addError(this.getText( "system.error.login" ));
//			} else if (info.getPassword() == null || info.getPassword().equalsIgnoreCase( "=auto=" )) {
				if (Pwd.length() == 32) {
					this.getUserInfoService().updatePwd( us.getUid(), Pwd.toUpperCase() );
				} else {
					this.getUserInfoService().updatePwd( us.getUid(), VeStr.MD5(Pwd) );
				} // 设置密码
				json.success(API_OK);
//			} else {
//				json.addError(this.getText( "user.error.018" ));
//			}
		} catch (Exception e) {
			LOGGER.info( "用户设置密码失败，异常信息：" + e.getMessage() );
			json.addError(this.getText( "system.error.info" ));
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}

	/**
	 * @return string
	 * 
	 * 用户风险测评分数
	 */
	public String score() {
		AjaxInfo json = this.getAjaxInfo();

		try {
			int score = this.getInt( "nc" );
			long uid = this.getUserSession().getUid();
			if(uid <100000){
				LOGGER.info("session 中没有获取到uid，");
				uid = this.getLong("uid");
			}
			LOGGER.info( "score:" +score+"uid:"+uid );
			int nc = this.getUserInfoService().updateNC( uid, score );
			json.addObject();
			json.append( "nc", nc );
		} catch (Exception e) {
			LOGGER.info( "更新用户风险测评分数失败，异常信息：" + e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
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
				json.addError(this.getText( "system.error.none" ));
			} else {
				int fee = this.getSysConfig().getSYSCashByMonth();
				json.addObject();
				json.append( "uid", s.getUid() );
				json.append( "pay", s.getPay() );
				json.append( "vip", s.getVIP() +"");
				json.append( "facer", us.getFacer() );
				json.append( "gender", us.getGender() );
				json.append( "name", s.getName() );
				json.append( "nicer", us.getNicer() );
				json.append( "mobile", s.getMobile() );
				json.append( "idcard", s.getIdCard() );
				json.append( "bkinfo", VeRule.toStar(s.getBkInfo(), 4, 4, 3, "-") );
				json.append( "binds", s.getBinds() );
				json.append( "reals", s.getReals() );
				json.append( "ma", s.getMa() );
				json.append( "mb", s.getMb() );
				json.append( "mc", s.getMc() );
				json.append( "md", s.getMd() );
				json.append( "mg", s.getMg() );
				json.append( "na", s.getNa() );
				int m = fee - s.getNm();
				if (m >= 0) {
					json.append( "nb", m ); // 免费提现次数
				} else {
					json.append( "nb", 0 ); // 免费提现次数
				}
				json.append( "nc", s.getNc() ); // 风险评测分数
				json.append( "shareurl", this.getSysConfig().getShareUrl() );
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}
}
