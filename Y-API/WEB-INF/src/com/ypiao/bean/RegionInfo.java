package com.ypiao.bean;

import java.io.Serializable;

public class RegionInfo implements Serializable {

	private static final long serialVersionUID = 6929662072515096870L;

	private String sid;

	private String code;

	private String cna, cnb;

	private String ena, enb;

	private String zipCode;

	private String telCode;

	private int telNum = 0;

	private int state = 0;

	private long time = 0;

	public RegionInfo() {
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCna() {
		return cna;
	}

	public void setCna(String cna) {
		this.cna = cna;
	}

	public String getCnb() {
		return cnb;
	}

	public void setCnb(String cnb) {
		this.cnb = cnb;
	}

	public String getEna() {
		return ena;
	}

	public void setEna(String ena) {
		this.ena = ena;
	}

	public String getEnb() {
		return enb;
	}

	public void setEnb(String enb) {
		this.enb = enb;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getTelCode() {
		return telCode;
	}

	public void setTelCode(String telCode) {
		this.telCode = telCode;
	}

	public int getTelNum() {
		return telNum;
	}

	public void setTelNum(int telNum) {
		this.telNum = telNum;
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
