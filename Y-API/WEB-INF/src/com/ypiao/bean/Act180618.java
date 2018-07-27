package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class Act180618 implements Serializable {

	private static final long serialVersionUID = 6585090393105235040L;

	private long sid;

	private String tday;

	private BigDecimal tma;

	private BigDecimal all;

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public String getTday() {
		return tday;
	}

	public void setTday(String tday) {
		this.tday = tday;
	}

	public BigDecimal getTma() {
		return tma;
	}

	public void setTma(BigDecimal tma) {
		this.tma = tma;
	}

	public BigDecimal getAll() {
		return all;
	}

	public void setAll(BigDecimal all) {
		this.all = all;
	}
}
