package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class CouponRule implements Serializable {

	private static final long serialVersionUID = 1629313349465534284L;

	private long rid = 0;

	private int type = 0;

	private String name;

	private BigDecimal tma = BigDecimal.ZERO;

	private BigDecimal tmb = BigDecimal.ZERO;

	private BigDecimal toall = BigDecimal.ZERO;

	private int today = 0;

	private int total = 0;

	private int stid = 0;

	private int tday = 0;

	private long sday, eday;

	private String remark;

	private int state = 0;

	private long time = 0;

	private long adM = 0;

	private String adN;

	public long getRid() {
		return rid;
	}

	public void setRid(long rid) {
		this.rid = rid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public BigDecimal getToall() {
		return toall;
	}

	public void setToall(BigDecimal toall) {
		this.toall = toall;
	}

	public int getToday() {
		return today;
	}

	public void setToday(int today) {
		this.today = today;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getStid() {
		return stid;
	}

	public void setStid(int stid) {
		this.stid = stid;
	}

	public int getTday() {
		return tday;
	}

	public void setTday(int tday) {
		this.tday = tday;
	}

	public long getSday() {
		return sday;
	}

	public void setSday(long sday) {
		this.sday = sday;
	}

	public long getEday() {
		return eday;
	}

	public void setEday(long eday) {
		this.eday = eday;
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
