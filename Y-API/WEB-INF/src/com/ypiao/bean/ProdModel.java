package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProdModel implements Serializable {

	private static final long serialVersionUID = 8704929681847634283L;

	private int tid = 0;

	private String name;

	private int total = 0;

	private int toall = 0;

	private int tofee = 0;

	private BigDecimal ma = BigDecimal.ZERO;

	private BigDecimal mb = BigDecimal.ZERO;

	private BigDecimal mc = BigDecimal.ZERO;

	private String remark;

	private int state = 0;

	private long time = 0;

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getToall() {
		return toall;
	}

	public void setToall(int toall) {
		this.toall = toall;
	}

	public int getTofee() {
		return tofee;
	}

	public void setTofee(int tofee) {
		this.tofee = tofee;
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
