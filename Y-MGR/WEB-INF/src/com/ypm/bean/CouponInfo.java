package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class CouponInfo implements Serializable {

	private static final long serialVersionUID = -762725603303962828L;

	private int cid, tid, type;

	private String name;

	private BigDecimal tma = BigDecimal.ZERO;

	private BigDecimal tmb = BigDecimal.ZERO;

	private BigDecimal toall = BigDecimal.ZERO;

	private int today = 0;

	private BigDecimal total = BigDecimal.ZERO;

	private int count = 0;

	private int stid = 0;

	private int tday = 0;

	private long sday, eday;

	private String remark;

	private int state = 0;

	private long time = 0;

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
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

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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
}
