package com.ypm.bean;

import java.io.Serializable;

public class UserIder implements Serializable {

	private static final long serialVersionUID = 7978477016896355691L;

	private long uid = 0;

	private String mobile;

	private int state = 0;

	private long time = 0;

	public UserIder() {
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
