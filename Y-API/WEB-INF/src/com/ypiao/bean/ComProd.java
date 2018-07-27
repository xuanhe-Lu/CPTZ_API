package com.ypiao.bean;

import java.io.Serializable;

public class ComProd implements Serializable {

	private static final long serialVersionUID = 6898313077020739223L;

	private long pid = 0;

	private long rid = 0;

	private String name;

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public long getRid() {
		return rid;
	}

	public void setRid(long rid) {
		this.rid = rid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
