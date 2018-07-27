package com.ypm.bean;

import java.io.Serializable;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -7358182054419516255L;

	private String sid;

	private long uid, ups;

	private int VIP = 0;

	private int iOS = 0;

	private String account, username, password;

	private String fta, ftb;

	private int facer = 0;

	private int gender = 0;

	private String gider, nicer;

	private int binds, reals;

	private long rtime = 0;

	private int state = 0;

	private long login = 0;

	private long time = 0;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
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
		return VIP;
	}

	public void setVIP(int vIP) {
		VIP = vIP;
	}

	public int getIOS() {
		return iOS;
	}

	public void setIOS(int iOS) {
		this.iOS = iOS;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFta() {
		return fta;
	}

	public void setFta(String fta) {
		this.fta = fta;
	}

	public String getFtb() {
		return ftb;
	}

	public void setFtb(String ftb) {
		this.ftb = ftb;
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

	public long getRtime() {
		return rtime;
	}

	public void setRtime(long rtime) {
		this.rtime = rtime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getLogin() {
		return login;
	}

	public void setLogin(long login) {
		this.login = login;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
