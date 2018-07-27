package com.ypm.bean;

import java.io.Serializable;

public class AdminOrgs implements Serializable {

	private static final long serialVersionUID = 3877034757019480062L;

	private int code = 0;

	private int type = 0;

	private int sortid = 0;

	private String name;

	private String remark;

	private int leaf = 0;

	public AdminOrgs() {
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getLeaf() {
		return leaf;
	}

	public void setLeaf(int leaf) {
		this.leaf = leaf;
	}

}
