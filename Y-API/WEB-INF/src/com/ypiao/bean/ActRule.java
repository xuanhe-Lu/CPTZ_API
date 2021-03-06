package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class ActRule implements Serializable {

	private static final long serialVersionUID = 3870879527834031489L;

	private long sid;

	private int adj = 0;

	private int sortid = 0;

	private BigDecimal tma, tmb;

	private BigDecimal ymc = BigDecimal.ZERO;

	private int state = 0;

	private long time = 0;

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public int getAdj() {
		return adj;
	}

	public void setAdj(int adj) {
		this.adj = adj;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
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

	public BigDecimal getYmc() {
		return ymc;
	}

	public void setYmc(BigDecimal ymc) {
		this.ymc = ymc;
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
