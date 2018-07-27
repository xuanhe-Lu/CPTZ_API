package com.ypm.bean;

import java.io.Serializable;

public class DictInfo implements Serializable {

	private static final long serialVersionUID = 168740549002141746L;

	private String id;

	private int sid = 0;

	private int type = 0;

	private int sortid = 0;

	private String sno;

	private String name;

	private String value;

	private String remark;

	public DictInfo() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
	}

	public String getSNo() {
		return sno;
	}

	public void setSNo(String sno) {
		this.sno = sno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
