package com.ypm.bean;

import java.io.Serializable;

public class DictMenu implements Serializable {

	private static final long serialVersionUID = 3484879967200040507L;

	private int sid = 0;

	private int tid = 0;

	private int sortid = 0;

	private String SNo;

	private String name;

	private int type = 0;

	private int losk = 0;

	public DictMenu() {
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
	}

	public String getSNo() {
		return SNo;
	}

	public void setSNo(String sNo) {
		SNo = sNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLosk() {
		return losk;
	}

	public void setLosk(int losk) {
		this.losk = losk;
	}

}
