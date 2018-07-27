package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class ActInfo implements Serializable {

	private static final long serialVersionUID = 3266416522484267726L;

	private int adj = 0;

	private String name;

	private BigDecimal rate = BigDecimal.ZERO;

	private String remark;

	private long sday, eday;

	private int state;

	private long time;

	public int getAdj() {
		return adj;
	}

	public void setAdj(int adj) {
		this.adj = adj;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
