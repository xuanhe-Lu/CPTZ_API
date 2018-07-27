package com.ypm.bean;

import java.io.Serializable;

public class AdminDept implements Serializable {

	private static final long serialVersionUID = -7426709448409247972L;

	private String id;

	private String name;

	private String remark;

	private String header;

	private String viewer;

	public AdminDept() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getViewer() {
		return viewer;
	}

	public void setViewer(String viewer) {
		this.viewer = viewer;
	}

}
