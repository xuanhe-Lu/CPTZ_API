package com.ypm.bean;

import java.io.Serializable;

public class UserBker implements Serializable {

	private static final long serialVersionUID = -3960444073082467916L;

	private long sid = 0;

	private long uid = 0;

	private int bid;

	private String cno;

	private String code, mobile, name;

	private String ba, bb, bc, bd;

	private int state = 0;

	private long time;

	private long adM = 0;

	private String adN;

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public String getCNo() {
		return cno;
	}

	public void setCNo(String cno) {
		this.cno = cno;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBa() {
		return ba;
	}

	public void setBa(String ba) {
		this.ba = ba;
	}

	public String getBb() {
		return bb;
	}

	public void setBb(String bb) {
		this.bb = bb;
	}

	public String getBc() {
		return bc;
	}

	public void setBc(String bc) {
		this.bc = bc;
	}

	public String getBd() {
		return bd;
	}

	public void setBd(String bd) {
		this.bd = bd;
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

	public long getAdM() {
		return adM;
	}

	public void setAdM(long adM) {
		this.adM = adM;
	}

	public String getAdN() {
		return adN;
	}

	public void setAdN(String adN) {
		this.adN = adN;
	}
}
