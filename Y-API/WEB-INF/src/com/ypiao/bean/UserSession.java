package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import com.ypiao.util.AState;

public class UserSession implements AState, Serializable {

	private static final long serialVersionUID = -534996057455913914L;

	private NetService netService;

	private boolean iPhone = false;

	private boolean locked = true;

	private String channel, key;

	private String sid, gzh, xch;

	private long uid = 0;

	private long ups = 0;

	private int vip = 0;

	private int facer = 0;

	private int gender = 0;

	private String gider;

	private String mobile;

	private String nicer;

	private int binds = 0, reals = 0;

	private long time = 0;

	private BigDecimal money = BigDecimal.ZERO;

	public UserSession(long uid) {
		this.uid = uid;
	}

	public UserSession(NetService service) {
		this.netService = service;
	}

	/** 清除登录信息 */
	public void clear() {
		this.uid = 0;
	}

	/** 销毁缓存 */
	public void destroy() {
		this.netService = null;
	}

	public boolean iPhone() {
		return iPhone;
	}

	public void setIPhone(boolean iPhone) {
		this.iPhone = iPhone;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isLogin() {
		return (uid >= USER_UID_BEG);
	}

	public NetService getNetService() {
		return netService;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		if (sid == null) {
			this.sid = "A";
		} else {
			this.sid = sid;
		}
	}

	public String getGzh() {
		return gzh;
	}

	public void setGzh(String gzh) {
		this.gzh = gzh;
	}

	public String getXch() {
		return xch;
	}

	public void setXch(String xch) {
		this.xch = xch;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getUPS() {
		return ups;
	}

	public void setUPS(long ups) {
		this.ups = ups;
	}

	public int getVIP() {
		return vip;
	}

	public void setVIP(int vip) {
		this.vip = vip;
	}

	public int getFacer() {
		return facer;
	}

	public void setFacer(int facer) {
		this.facer = facer;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getGider() {
		return gider;
	}

	public void setGider(String gider) {
		this.gider = gider;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNicer() {
		return nicer;
	}

	public void setNicer(String nicer) {
		this.nicer = nicer;
	}

	public int getBinds() {
		return binds;
	}

	public void setBinds(int binds) {
		this.binds = binds;
	}

	public int getReals() {
		return reals;
	}

	public void setReals(int reals) {
		this.reals = reals;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}
}
