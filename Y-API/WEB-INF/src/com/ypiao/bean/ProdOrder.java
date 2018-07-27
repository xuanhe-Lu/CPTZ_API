package com.ypiao.bean;

import java.io.Serializable;

public class ProdOrder implements Serializable {

	private static final long serialVersionUID = 6239136378441239226L;

	private long sid;

	private String name;

	private String money;

	private String time;

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
