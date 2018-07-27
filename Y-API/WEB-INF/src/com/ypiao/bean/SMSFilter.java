package com.ypiao.bean;

import java.io.Serializable;

public class SMSFilter implements Serializable {

	private static final long serialVersionUID = -3983549989976158849L;

	private String sid;

	private int num = 0;

	private int state = 0;

	private long time = 0;

	public SMSFilter() {
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
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
