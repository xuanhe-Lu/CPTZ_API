package com.ypm.bean;

import java.io.Serializable;

public class FieldMenu implements Serializable {

	private static final long serialVersionUID = -6743800170194644245L;

	private int sid = 0;

	private int tid = 0;

	private int sortid = 0;

	private String sno;

	private String name;

	private String title;

	private String value;

	private int losk = 0;

	public FieldMenu() {
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

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getLosk() {
		return losk;
	}

	public boolean isLosk() {
		return (losk == 1);
	}

	public void setLosk(int losk) {
		this.losk = losk;
	}

}
