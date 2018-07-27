package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserCash implements Serializable {

	private static final long serialVersionUID = -3950140087108980188L;

	private long sid = 0;

	private long uid = 0;

	private String name, mobile;

	private int bkId;

	private String bkName, bkInfo;

	private BigDecimal tma = BigDecimal.ZERO;

	private BigDecimal tmb = BigDecimal.ZERO;

	private BigDecimal tmc = BigDecimal.ZERO;

	private int rcv = 0;

	private long gmtA, gmtB;

	private int state = 0;

	private long time;

	private long adm = 0;

	private String adn;
	
	private long lsDh;//流水单号，用户提现生成流水单号
	
	public long getLsDh() {
		return lsDh;
	}

	public void setLsDh(long lsDh) {
		this.lsDh = lsDh;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getBkId() {
		return bkId;
	}

	public void setBkId(int bkId) {
		this.bkId = bkId;
	}

	public String getBkName() {
		return bkName;
	}

	public void setBkName(String bkName) {
		this.bkName = bkName;
	}

	public String getBkInfo() {
		return bkInfo;
	}

	public void setBkInfo(String bkInfo) {
		this.bkInfo = bkInfo;
	}

	public BigDecimal getTma() {
		return tma;
	}

	public void setTma(BigDecimal tma) {
		this.tma = tma;
	}

	public BigDecimal getTmb() {
		return tmb;
	}

	public void setTmb(BigDecimal tmb) {
		this.tmb = tmb;
	}

	public BigDecimal getTmc() {
		return tmc;
	}

	public void setTmc(BigDecimal tmc) {
		this.tmc = tmc;
	}

	public int getRcv() {
		return rcv;
	}

	public void setRcv(int rcv) {
		this.rcv = rcv;
	}

	public long getGmtA() {
		return gmtA;
	}

	public void setGmtA(long gmtA) {
		this.gmtA = gmtA;
	}

	public long getGmtB() {
		return gmtB;
	}

	public void setGmtB(long gmtB) {
		this.gmtB = gmtB;
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

	public long getAdm() {
		return adm;
	}

	public void setAdm(long adm) {
		this.adm = adm;
	}

	public String getAdn() {
		return adn;
	}

	public void setAdn(String adn) {
		this.adn = adn;
	}
}
