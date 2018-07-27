package com.ypiao.json;

import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import com.sunsw.struts.interceptor.ServletRequestAware;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.service.UserCouponService;

public class OnUserCoupon extends Action implements ServletRequestAware {

	private static final long serialVersionUID = -5032222376485710996L;
	
	// 注入HttpServletRequest
	private HttpServletRequest request;
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	private UserCouponService userCouponService;

	public OnUserCoupon() {
		super(true);
	}

	public UserCouponService getUserCouponService() {
		return userCouponService;
	}

	public void setUserCouponService(UserCouponService userCouponService) {
		this.userCouponService = userCouponService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int state = this.getInt("state");
			if (STATE_DISABLE != state) {
				state = STATE_ENABLE;
			}
			UserSession us = this.getUserSession();
			this.getUserCouponService().sendByUid(json, us.getUid(), state);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return string 
	 * 
	 * 优惠券弹窗
	 */
	public String alert() {
		AjaxInfo json = this.getAjaxInfo();
		
		int state = this.getInt( "state" );
		if (STATE_DISABLE != state) {
			state = STATE_ENABLE;
		}
		
		// 获取请求头用户uid属性值
		String userId = this.request.getHeader( "uid" );
		long uid = userId == null ? 0 : Long.parseLong(userId);
		
		try {
			this.getUserCouponService().alertByUid( json, uid, state );
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}
		
		return JSON;
	}
}
