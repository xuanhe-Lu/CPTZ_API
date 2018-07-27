package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class InfoVIPS implements Serializable {

	private static final long serialVersionUID = -5799621761525219177L;

	private int vip;

	private String name;

	private BigDecimal rate = BigDecimal.ZERO;

	private BigDecimal rats = BigDecimal.ZERO;

	private BigDecimal sale = BigDecimal.ZERO;

	private int sNum = 0, sok = 0;

	private BigDecimal sRmb = BigDecimal.ZERO;

	private String sTxt;

	private BigDecimal gma = BigDecimal.ZERO;

	private BigDecimal gmb = BigDecimal.ZERO;

	private BigDecimal gna = BigDecimal.ZERO;

	private BigDecimal gnb = BigDecimal.ZERO;

	private int rday = 0;

	private String remark;

	private int state = 0;

	private long time = 0;

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getRats() {
		return rats;
	}

	public void setRats(BigDecimal rats) {
		this.rats = rats;
	}

	public BigDecimal getSale() {
		return sale;
	}

	public void setSale(BigDecimal sale) {
		this.sale = sale;
	}

	public int getSNum() {
		return sNum;
	}

	public void setSNum(int sNum) {
		this.sNum = sNum;
	}

	public int getSok() {
		return sok;
	}

	public void setSok(int sok) {
		this.sok = sok;
	}

	public BigDecimal getSRmb() {
		return sRmb;
	}

	public void setSRmb(BigDecimal sRmb) {
		this.sRmb = sRmb;
	}

	public String getSTxt() {
		return sTxt;
	}

	public void setSTxt(String sTxt) {
		this.sTxt = sTxt;
	}

	public BigDecimal getGma() {
		return gma;
	}

	public void setGma(BigDecimal gma) {
		this.gma = gma;
	}

	public BigDecimal getGmb() {
		return gmb;
	}

	public void setGmb(BigDecimal gmb) {
		this.gmb = gmb;
	}

	public BigDecimal getGna() {
		return gna;
	}

	public void setGna(BigDecimal gna) {
		this.gna = gna;
	}

	public BigDecimal getGnb() {
		return gnb;
	}

	public void setGnb(BigDecimal gnb) {
		this.gnb = gnb;
	}

	public int getRday() {
		return rday;
	}

	public void setRday(int rday) {
		this.rday = rday;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
