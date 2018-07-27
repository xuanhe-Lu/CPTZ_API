package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class AutoRaws implements Serializable {

	private static final long serialVersionUID = 1458557745489081569L;

	private long pid = 0;

	private long sid = 0;

	private long rid = 0;

	private int cid = 0;

	private BigDecimal ma = BigDecimal.ZERO;

	private BigDecimal mb = BigDecimal.ZERO;

	private BigDecimal mc = BigDecimal.ZERO;

	private BigDecimal md = BigDecimal.ZERO;

	private BigDecimal me = BigDecimal.ZERO;

	private BigDecimal mf = BigDecimal.ZERO;

	private BigDecimal mg = BigDecimal.ZERO;

	private BigDecimal yma = BigDecimal.ZERO;

	private int state = 0;

	private long time = 0;

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public long getRid() {
		return rid;
	}

	public void setRid(long rid) {
		this.rid = rid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public BigDecimal getMa() {
		return ma;
	}

	public void setMa(BigDecimal ma) {
		this.ma = ma;
	}

	public BigDecimal getMb() {
		return mb;
	}

	public void setMb(BigDecimal mb) {
		this.mb = mb;
	}

	public BigDecimal getMc() {
		return mc;
	}

	public void setMc(BigDecimal mc) {
		this.mc = mc;
	}

	public BigDecimal getMd() {
		return md;
	}

	public void setMd(BigDecimal md) {
		this.md = md;
	}

	public BigDecimal getMe() {
		return me;
	}

	public void setMe(BigDecimal me) {
		this.me = me;
	}

	public BigDecimal getMf() {
		return mf;
	}

	public void setMf(BigDecimal mf) {
		this.mf = mf;
	}

	public BigDecimal getMg() {
		return mg;
	}

	public void setMg(BigDecimal mg) {
		this.mg = mg;
	}

	public BigDecimal getYma() {
		return yma;
	}

	public void setYma(BigDecimal yma) {
		this.yma = yma;
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
