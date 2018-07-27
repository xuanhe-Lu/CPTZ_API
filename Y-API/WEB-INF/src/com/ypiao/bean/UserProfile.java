package com.ypiao.bean;

import java.io.Serializable;

public class UserProfile implements Serializable {

	private static final long serialVersionUID = 544711892884891105L;

	private long uid = 0;

	private String sid;

	private long cid = 0;

	private int iOS = 0;

	private String device;

	private String mobile;

	private String model;

	private String release;

	private int sdk = 0;

	private String token;

	private String raddr;

	private long rtime;

	private long time = 0;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public int getIOS() {
		return iOS;
	}

	public void setIOS(int iOS) {
		this.iOS = iOS;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public int getSdk() {
		return sdk;
	}

	public void setSdk(int sdk) {
		this.sdk = sdk;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRaddr() {
		return raddr;
	}

	public void setRaddr(String raddr) {
		this.raddr = raddr;
	}

	public long getRtime() {
		return rtime;
	}

	public void setRtime(long rtime) {
		this.rtime = rtime;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
