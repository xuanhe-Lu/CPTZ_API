package com.ypm.bean;

import java.io.Serializable;

public class SiteCity implements Serializable {

	private static final long serialVersionUID = 5904653453079339985L;

	private String code;

	private String name;

	private String nick;

	private String info;

	public SiteCity() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void executeCount() {
		String str = this.getNick();
		if (str.length() > 2) {
			this.setInfo(str.replaceAll("(省|市|区|州|特別行政區)$", ""));
		} else {
			this.setInfo(str);
		}
	}

}
