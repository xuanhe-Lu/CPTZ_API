package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserCoupon implements Serializable {

	private static final long serialVersionUID = -4761746776162560089L;

	private long sid = 0;

	private long uid = 0;

	private long cid = 0;

	private String way, name;

	private int type = 0;

	private BigDecimal tma = BigDecimal.ZERO;

	private BigDecimal tmb = BigDecimal.ZERO;

	private BigDecimal toall = BigDecimal.ZERO;

	private int today = 0;

	private long sday = 0;

	private long eday = 0;

	private String remark;

	private long ordId;

	private long gmtA, gmtB;

	private int state = 0;

	private long time = 0;

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

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public long getOrdId() {
		return ordId;
	}

	public void setOrdId(long ordId) {
		this.ordId = ordId;
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
}
