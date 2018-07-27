package com.ypiao.bean;

import java.io.Serializable;

public class FileBase implements Serializable {

	private static final long serialVersionUID = 7644965684641673755L;

	private String Pid;

	private String source;

	private String dist;

	private String name;

	private String info;

	private int Pdw, Pdh;

	private long size;

	public FileBase() {
	}

	public String getPid() {
		return Pid;
	}

	public void setPid(String pid) {
		Pid = pid;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDist() {
		return dist;
	}

	public void setDist(String dist) {
		this.dist = dist;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getPdw() {
		return Pdw;
	}

	public void setPdw(int pdw) {
		Pdw = pdw;
	}

	public int getPdh() {
		return Pdh;
	}

	public void setPdh(int pdh) {
		Pdh = pdh;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
