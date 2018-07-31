package com.ypm.view;

import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;
import com.sunsw.struts.interceptor.SessionAware;
import com.ypm.bean.AdminInfo;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.UserSession;
import com.ypm.service.AdminInfoService;
import com.ypm.service.SysConfig;
import com.ypm.util.Constant;
import com.ypm.util.VeStr;

public class Login extends Action implements SessionAware {

	private static final long serialVersionUID = -8766572631305799591L;

	private Map<String, Object> session;

	private SysConfig sysConfig;

	private AdminInfoService adminInfoService;

	private String name;

	private String pass;
	
	// 接收账户工号,即userId
	private String num;

	private String tels;

	public Login() {
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public SysConfig getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig) {
		this.sysConfig = sysConfig;
	}

	public AdminInfoService getAdminInfoService() {
		return adminInfoService;
	}

	public void setAdminInfoService(AdminInfoService adminInfoService) {
		this.adminInfoService = adminInfoService;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getTels() {
		return tels;
	}

	public void setTels(String tels) {
		this.tels = tels;
	}

	/**
	 * 用户登录执行方法
	 */
	public String index() {
		UserSession us = this.getUserSession();
		if (us != null && us.getUserId() > 0) {
			return this.success();
		} else {
			this.setTels(this.getSysConfig().getSiteTel());
		} // 检测用户登录
		String name = VeStr.toStr(this.getName());
		if (name == null) {
			return INPUT;
		} // 登录密码
		AjaxInfo json = this.getAjaxInfo();
		String pass = VeStr.toStr(this.getPass());
		if (pass == null) {
			pass = "none"; // 为空时调用
		} else if (pass.length() < 3) {
			json.addError(this.getText("user.error.014"));
			System.out.println("json:"+json.toString());
 return JSON;
		}
		// 验证工号
		String num = this.getNum();
		if (num.length() != 6 || !Pattern.compile( "([0-9]{6})" ).matcher(num).matches()) {
			json.addError(this.getText( "user.error.016" ));
			System.out.println("json:"+json.toString());
 return JSON;
		}
		// 查询数据
		AdminInfo info = this.getAdminInfoService().findAdminInfoByName(name);
		try {
			if (info == null || !info.getPassWord().equalsIgnoreCase(VeStr.MD5(pass))) {
				if (info.getFailCt() >= 3) {
					json.addError(this.getText("user.error.055"));//登录失败！密码错误超过三次，请在20分钟后重新登录
				}else {
					int failCount = info.getFailCt() + 1;
					adminInfoService.updateFailCt(info.getUserId(), failCount);
					json.addError(this.getText("user.error.014"));
				}
			} else if(info.getUserId() != Long.valueOf(num)) {
				json.addError(this.getText( "user.error.016" ));
			} else if (info.getState() == 0) {
				if (info.getFailCt() >= 3) {
					Date stTime = new Date(info.getTime());
					long nowTime = System.currentTimeMillis();  //获取当前时间的毫秒数
					long sj = (nowTime-stTime.getTime())/1000;
					if (sj >= 1200) {
						adminInfoService.updateFailCt(info.getUserId(), 0);
						us = this.getAdminInfoService().getUserSession(info);
						us.setLoginIP(VeStr.getRemoteAddr(getRequest()));
						this.getSession().put(Constant.USE_SESSION_KEY, us);
						json.addMessage("OK!");
					}else {
						json.addError(this.getText("user.error.055"));//登录失败！密码错误超过三次，请在20分钟后重新登录！						
					}
				}else {
					us = this.getAdminInfoService().getUserSession(info);
					us.setLoginIP(VeStr.getRemoteAddr(getRequest()));
					this.getSession().put(Constant.USE_SESSION_KEY, us);
					json.addMessage("OK!");					
				}
			} else {
				json.addError(this.getText("user.error.03" + info.getState()));
			}
		} catch (Exception e) {
			json.addError(this.getText("system.error.info"));
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}

	private String success() {
		if (Constant.USE_REWRITE) {
			this.setToUrl("index.html");
		} else {
			this.setToUrl("index.jsp");
		} // Return Result
		return NONE;
	}

}
