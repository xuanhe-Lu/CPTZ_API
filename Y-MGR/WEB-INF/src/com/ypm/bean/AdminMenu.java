package com.ypm.bean;

import java.io.Serializable;

public class AdminMenu implements Serializable {

	private static final long serialVersionUID = 6829727562568361229L;

	private int id = 0;

	private int tid = 0;

	private int Sortid = 0;

	private String name;

	private String module;

	private String remark;

	private String icon;

	private int leaf = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getSortid() {
		return Sortid;
	}

	public void setSortid(int sortid) {
		Sortid = sortid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getLeaf() {
		return leaf;
	}

	public void setLeaf(int leaf) {
		this.leaf = leaf;
	}

}
