package com.ypiao.bean;

import java.io.Serializable;

public class SMSConfig implements Serializable {

	private static final long serialVersionUID = 6109688229574361045L;

	private int tid = 0;

	private String name;

	private String content;

	private String sign;

	private int state = 0;

	private long time = 0;

	public SMSConfig() {
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
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
