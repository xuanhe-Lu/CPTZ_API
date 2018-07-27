package com.ypiao.bean;

import java.io.Serializable;

public class SMSInfo implements Serializable {

	private static final long serialVersionUID = 2114267566802865420L;

	private String sid;

	private int tid;

	private int cid;

	private int anum, bnum, cnum, nums;

	private String code;

	private long sday = 0;

	private int state = 0;

	private long time = 0;

	public void addA(int cid, int num) {
		this.cid = cid;
		this.anum += num;
	}

	public void addB(int cid, int num) {
		this.cid = cid;
		this.bnum += num;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getAnum() {
		return anum;
	}

	public void setAnum(int anum) {
		this.anum = anum;
	}

	public int getBnum() {
		return bnum;
	}

	public void setBnum(int bnum) {
		this.bnum = bnum;
	}

	public int getCnum() {
		return cnum;
	}

	public void setCnum(int cnum) {
		this.cnum = cnum;
	}

	public int getNums() {
		return nums;
	}

	public void setNums(int nums) {
		this.nums = nums;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getSday() {
		return sday;
	}

	public void setSday(long sday) {
		this.sday = sday;
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
}
