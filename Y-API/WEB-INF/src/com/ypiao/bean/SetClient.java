package com.ypiao.bean;

import java.io.Serializable;

public class SetClient implements Serializable {

	private static final long serialVersionUID = 2976942426974277970L;

	private int sid = 0;

	private int tid = 0;

	private int code = 0;

	private String codever;
	
	/************** xk 新增apk文件名、文件大小字段 Start *************/
	private String filename;
	
	private double size = 0;
	/************** xk 新增apk文件名、文件大小字段 End ***************/

	private String content;

	private int force = 0;

	private String tday;

	private int state = 0;

	private long time = 0;

	public SetClient() {
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

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCodever() {
		return codever;
	}

	public void setCodever(String codever) {
		this.codever = codever;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getForce() {
		return force;
	}

	public void setForce(int force) {
		this.force = force;
	}

	public String getTday() {
		return tday;
	}

	public void setTday(String tday) {
		this.tday = tday;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "SetClient [sid=" + sid + ", tid=" + tid + ", code=" + code + ", codever=" + codever + ", filename="
				+ filename + ", size=" + size + ", content=" + content + ", tday=" + tday + ", state=" + state
				+ ", time=" + time + "]";
	}
	
}
