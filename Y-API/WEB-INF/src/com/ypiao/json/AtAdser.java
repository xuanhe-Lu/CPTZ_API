package com.ypiao.json;

import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sunsw.struts.ServletActionContext;
import com.sunsw.struts.interceptor.ServletRequestAware;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.AderInfoService;
import com.ypiao.service.UserOrderService;

public class AtAdser extends Action implements ServletRequestAware {

	private static final long serialVersionUID = 7317446137411862209L;
	
	// 注入HttpServletRequest
	private HttpServletRequest request;
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	private AderInfoService aderInfoService;

	private UserOrderService userOrderService;

	public AtAdser() {
		super(true);
	}

	public AderInfoService getAderInfoService() {
		return aderInfoService;
	}

	public void setAderInfoService(AderInfoService aderInfoService) {
		this.aderInfoService = aderInfoService;
	}

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int tid = getInt("tid", 0);
			this.getAderInfoService().sendByTid(json, tid);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		System.out.println("json:"+json.toString());
 		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 网站首页中部广告数据列表获取
	 */
	public String indexCenter () {
		// 解决接口调用跨域问题
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader( "Access-Control-Allow-Origin", "*" );
		
		AjaxInfo json = this.getAjaxInfo();
		try {
			int tid = 13;
			this.getAderInfoService().sendByTid( json, tid );
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 网站首页顶部轮播广告数据列表获取
	 */
	public String indexTop () {
		// 解决接口调用跨域问题
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader( "Access-Control-Allow-Origin", "*" );
		
		AjaxInfo json = this.getAjaxInfo();
		try {
			int tid = 12;
			this.getAderInfoService().sendByTid( json, tid );
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * app首页中部四个icon + 其下方点击注册图片
	 */
	public String appCenterIcon () {
		AjaxInfo json = this.getAjaxInfo();
		// 获取请求头用户uid属性值
		String userId = this.request.getHeader( "uid" );
		long uid = userId == null ? 0 : Long.parseLong(userId);
	
		try {
			json.success(API_OK);
			// 已登录
			if (uid >= USER_UID_BEG) {
				// 购买过新手标
				if (userOrderService.isNewByUid(uid)) {
					this.getAderInfoService().sendByTid( json, 14, 1 );
				} else {
					this.getAderInfoService().sendByTid( json, 15, 1 );
				}
			} else {// 未登录
				this.getAderInfoService().sendByTid( json, 15, 1 );
				this.getAderInfoService().sendByTid( json, 17, 2 );
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addText( "error", e.getMessage() );
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * app首页底部了解盈喵金服
	 */
	public String appKnow () {
		AjaxInfo json = this.getAjaxInfo();
		int tid = 16;
		
		try {
			this.getAderInfoService().sendByTid( json, tid );
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		}
		
		System.out.println("json:"+json.toString());
 return JSON;
	}

}
