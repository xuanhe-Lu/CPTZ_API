package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class BankInfo implements Serializable {

	private static final long serialVersionUID = -7614827385534184655L;

	private int bid;

	private int bankId = 0;

	private int sortid = 0;

	private int type = 0;

	private String name, nice;

	private BigDecimal month = BigDecimal.ZERO;

	private BigDecimal toall = BigDecimal.ZERO;

	private BigDecimal today = BigDecimal.ZERO;

	private int total = 0;

	private int state = 0;

	private long time = 0;

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public int getBankId() {
		return bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
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

	public String getNice() {
		return nice;
	}

	public void setNice(String nice) {
		this.nice = nice;
	}

	public BigDecimal getMonth() {
		return month;
	}

	public void setMonth(BigDecimal month) {
		this.month = month;
	}

	public BigDecimal getToall() {
		return toall;
	}

	public void setToall(BigDecimal toall) {
		this.toall = toall;
	}

	public BigDecimal getToday() {
		return today;
	}

	public void setToday(BigDecimal today) {
		this.today = today;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
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
