package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class RawProd implements Serializable {

	private static final long serialVersionUID = -392376066879118190L;

	private long rid = 0;

	private int cid = 0;

	private String ba, bg;

	private int bh, bi, bj;

	private BigDecimal bm = BigDecimal.ZERO;

	private BigDecimal bn = BigDecimal.ZERO;

	private String ca;

	private int state = 0;

	public RawProd() {
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

	public String getBa() {
		return ba;
	}

	public void setBa(String ba) {
		this.ba = ba;
	}

	public String getBg() {
		return bg;
	}

	public void setBg(String bg) {
		this.bg = bg;
	}

	public int getBh() {
		return bh;
	}

	public void setBh(int bh) {
		this.bh = bh;
	}

	public int getBi() {
		return bi;
	}

	public void setBi(int bi) {
		this.bi = bi;
	}

	public int getBj() {
		return bj;
	}

	public void setBj(int bj) {
		this.bj = bj;
	}

	public BigDecimal getBm() {
		return bm;
	}

	public void setBm(BigDecimal bm) {
		this.bm = bm;
	}

	public BigDecimal getBn() {
		return bn;
	}

	public void setBn(BigDecimal bn) {
		this.bn = bn;
	}

	public String getCa() {
		return ca;
	}

	public void setCa(String ca) {
		this.ca = ca;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
