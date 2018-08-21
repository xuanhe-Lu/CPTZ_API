package com.ypiao.json;

import java.sql.SQLException;
import org.commons.lang.RandomUtils;
import com.ypiao.bean.*;
import com.ypiao.service.*;
import com.ypiao.util.GMTime;
import com.ypiao.util.VeStr;

public class AtLogin extends Action {

	private static final long serialVersionUID = 8116994544536533622L;

	private static final int TOLOGIN = -10;

	private SysConfig sysConfig;

	private SendInfoService sendInfoService;

	private UserInfoService userInfoService;

	private UserLogerService userLogerService;

	public AtLogin() {
		super(true);
	}

	public SysConfig getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig) {
		this.sysConfig = sysConfig;
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

	public String index() {
		return login();
	}

	public String login() {
		AjaxInfo json = this.getAjaxInfo();
		String fix = getString("fix");
		String acc = getString("account");
		String Pwd = getParameter("password");
		try {
			if (acc == null || acc.length() < 5) {
				json.addError(this.getText("user.error.021"));
			} else if (Pwd == null || Pwd.length() < 6) {
				json.addError(this.getText("user.error.023"));
			} else {
				UserInfo info = this.getUserLogerService().getUserInfoByAcc(fix, acc);
				if (info == null) {
					json.addError(this.getText("user.error.022"));
				} else {
					if (Pwd.length() != 32) {
						Pwd = VeStr.MD5(Pwd);
					} // check Password
					if (Pwd.equalsIgnoreCase(info.getPassword())) {
						this.setLogin(json, info);
					} else {
						json.addError(this.getText("user.error.024"));
					}
				}
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		} finally {
			Pwd = acc = fix = null;
		}
		return JSON;
	}

	/** 短信验证登录 */
	public String logsms() {
		AjaxInfo json = this.getAjaxInfo();
		String fix = getString("fix");
		String code = getString("vcode");
		// TODO 验证码写死 测试用
		code = String.valueOf(123456);
		String mobile = getString("mobile");
		try {
			if (code == null) {
				json.addError(this.getText("user.error.007"));
				return JSON;
			} // 检测手机号码
			String sm = VeStr.getMobile(fix, mobile);
			if (sm == null) {
				json.addError(this.getText("user.error.010"));
			} else if (this.getSendInfoService().isCode(sm, code)) {
				UserInfo info = this.getUserInfoService().findUserInfoBySM(sm);
				if (info == null) {
					String type = getString("type");
					UserReger reg = new UserReger();
					if ("ios".equalsIgnoreCase(type)) {
						reg.setIOS(1);
					} else {
						reg.setIOS(0);
					}
					reg.setAccount(sm); // 手机号
					reg.setPassword("=auto=");
					reg.setGider(sm);
					reg.setNicer(sm);
					reg.setChannel(getString("channel"));
					reg.setDevice(getString("device"));
					reg.setModel(getString("model"));
					reg.setRelease(getString("release"));
					reg.setSdk(getInt("sdk"));
					reg.setToken(getString("token"));
					reg.setRemote(VeStr.getRemoteAddr(request));
					this.getUserLogerService().register(reg);
					return this.setAuth(json, reg);
				} else {
					return this.setLogin(json, info);
				}
			} else {
				json.addError(this.getText("user.error.008"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		} finally {
			fix = code = mobile = null;
		}
		return JSON;
	}

	/** 三方账号登录 */
	public String open() {
		this.getAjaxInfo().addError("==" + this.cache);
		return JSON;
	}

	/** 自动登录 */
	public String relog() {
		AjaxInfo json = this.getAjaxInfo();
		String Pwd = getParameter("pwd");
		String token = getString("token");
		try {
			long uid = this.getLong("uid");
			UserInfo info = this.getUserInfoService().findUserInfoByUid(uid);
			if (info == null || Pwd == null) {
				json.addError(TOLOGIN, this.getText("user.error.022"));
			} else if (token == null || !token.equals(info.getSid())) {
				json.addError(TOLOGIN, this.getText("user.error.037"));
			} else if (Pwd.equalsIgnoreCase("=auto=")) {
				this.setLogin(json, info);
			} else {
				if (Pwd.length() != 32) {
					Pwd = VeStr.MD5(Pwd);
				} // check Password
				if (Pwd.equalsIgnoreCase(info.getPassword())) {
					this.setLogin(json, info);
				} else {
					json.addError(TOLOGIN, this.getText("user.error.024"));
				}
			}
		} catch (SQLException e) {
			json.addError(TOLOGIN, this.getText("system.error.info"));
		} finally {
			Pwd = token = null;
		}
		return JSON;
	}

	/** 用户退出操作 */
	public String logout() {
		AjaxInfo json = this.getAjaxInfo();
		UserSession us = this.getUserSession();
		try {
			this.getUserLogerService().logout(us);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			us.setUid(0);
		}
		json.success(API_OK);
		return JSON;
	}

	/** IOS切换后台通知 */
	public String reset() {
		AjaxInfo json = this.getAjaxInfo();
		UserSession us = this.getUserSession();
		if (getInt("state") == 0) {
			us.setIPhone(true);
			us.setLocked(false);
		} else {
			us.setIPhone(true);
			us.setLocked(true);
		}
		json.success(API_OK);
		return JSON;
	}

	private String setAuth(AjaxInfo json, UserReger reg) throws SQLException {
		UserSession us = this.getUserSession();
		String token = reg.getToken();
		try {
			if (token == null) {
				if (reg.getDevice() == null) {
					reg.setDevice(RandomUtils.randomAlphanumeric(32));
				}
				token = reg.getDevice();
				reg.setToken(token);
			} // 下发详情数据
			us.setIPhone(reg.getIOS() == 1);
			us.setSid(reg.getToken());
			us.setUid(reg.getUid());
			us.setUPS(reg.getUPS());
			us.setVIP(reg.getVIP());
			us.setMobile(reg.getAccount());
			us.setFacer(reg.getFacer());
			us.setGender(reg.getGender());
			us.setGider(reg.getGider());
			us.setNicer(reg.getNicer());
			us.setBinds(reg.getBinds());
			us.setReals(reg.getReals());
			us.setTime(System.currentTimeMillis());
			this.getUserLogerService().login(us, reg.getDevice(), reg.getModel());
			int fee = this.getSysConfig().getSYSCashByMonth(); // 免费提现
			UserStatus s = this.getUserInfoService().findUserStatusByUid(us.getUid());
			json.addObject();
			json.append("uid", us.getUid());
			json.append("vip", us.getVIP());
			json.append("token", reg.getToken());
			json.append("account", us.getMobile());
			json.append("facer", reg.getFacer());
			json.append("gender", us.getGender());
			json.append("gider", reg.getGider());
			json.append("nicer", reg.getNicer());
			json.append("binds", reg.getBinds());
			json.append("reals", reg.getReals());
			json.append("state", reg.getState());
			if (s == null) {
				json.append("pay", 0);
				json.append("na", 0);
				json.append("nb", fee);
				json.append("nc", 0);
			} else {
				int m = fee - s.getNm();
				json.append("pay", s.getPay());
				json.append("na", s.getNa());
				if (m >= 0) {
					json.append("nb", m);
				} else {
					json.append("nb", 0);
				}
				json.append("nc", s.getNc());
			}
			json.append("session_key", us.getKey());
			return JSON;
		} finally {
			token = null;
		}
	}

	private String setLogin(AjaxInfo json, UserInfo info) throws SQLException {
		switch (info.getState()) {
		case STATE_NORMAL:
			String type = getString("type");
			String dev = getString("device");
			String model = getString("model");
			String token = getString("token");
			UserSession us = this.getUserSession();
			try {
				boolean iPhone = false;
				if (type == null) {
					// 安卓
				} else {
					iPhone = type.equalsIgnoreCase("ios");
				}
				if (token == null) {
					if (dev == null) {
						dev = RandomUtils.randomAlphanumeric(32);
					}
					token = dev;
				} // 下发详情数据
				us.setIPhone(iPhone);
				us.setSid(token);
				us.setUid(info.getUid());
				us.setUPS(info.getUPS());
				us.setVIP(info.getVIP());
				us.setMobile(info.getAccount());
				us.setFacer(info.getFacer());
				us.setGender(info.getGender());
				us.setGider(info.getGider());
				us.setNicer(info.getNicer());
				us.setBinds(info.getBinds());
				us.setReals(info.getReals());
				us.setTime(System.currentTimeMillis());
				this.getUserLogerService().login(us, dev, model);
				int fee = this.getSysConfig().getSYSCashByMonth(); // 免费提现
				UserStatus s = this.getUserInfoService().findUserStatusByUid(us.getUid());
				json.addObject();
				json.append("uid", us.getUid());
				json.append("vip", us.getVIP());
				json.append("token", token);
				json.append("account", us.getMobile());
				json.append("facer", info.getFacer());
				json.append("gender", us.getGender());
				json.append("gider", info.getGider());
				json.append("nicer", info.getNicer());
				json.append("binds", info.getBinds());
				json.append("reals", info.getReals());
				json.append("state", info.getState());
				if (s == null) {
					json.append("pay", 0);
					json.append("na", 0);
					json.append("nb", fee);
					json.append("nc", 0);
				} else {
					int m = fee - s.getNm();
					json.append("pay", s.getPay());
					json.append("na", s.getNa());
					if (m >= 0) {
						json.append("nb", m);
					} else {
						json.append("nb", 0);
					}
					json.append("nc", s.getNc());
				}
				json.append("session_key", us.getKey());
			} finally {
				type = dev = model = token = null;
			}
			return JSON;
		case 2: // 锁定
			json.addError(this.getText("user.error.032"));
			return JSON;
		case 4: // 过期
			json.addError(this.getText("user.error.034"));
			return JSON;
		case 5: // 禁止
			json.addError(this.getText("user.error.035"));
		default: // 异常
			json.addError(this.getText("user.error.031"));
			return JSON;
		}
	}
}
