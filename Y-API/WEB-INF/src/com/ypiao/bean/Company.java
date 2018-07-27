package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class Company implements Serializable {

	private static final long serialVersionUID = 3030530181761745321L;

	private int cid = 0;

	private int tid = 0;

	private int bank = 0;

	private String ca, cb, cc, cd, ce, cf, cg, ch;

	private int ck = 0;

	private BigDecimal cm = BigDecimal.ZERO;

	private BigDecimal cn = BigDecimal.ZERO;

	private int total = 0;

	private int state = 0;

	private long time = 0;

	public Company() {
	}

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

	public int getBank() {
		return bank;
	}

	public void setBank(int bank) {
		this.bank = bank;
	}

	public String getCa() {
		return ca;
	}

	public void setCa(String ca) {
		this.ca = ca;
	}

	public String getCb() {
		return cb;
	}

	public void setCb(String cb) {
		this.cb = cb;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getCd() {
		return cd;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public String getCe() {
		return ce;
	}

	public void setCe(String ce) {
		this.ce = ce;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public String getCg() {
		return cg;
	}

	public void setCg(String cg) {
		this.cg = cg;
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	public int getCk() {
		return ck;
	}

	public void setCk(int ck) {
		this.ck = ck;
	}

	public BigDecimal getCm() {
		return cm;
	}

	public void setCm(BigDecimal cm) {
		this.cm = cm;
	}

	public BigDecimal getCn() {
		return cn;
	}

	public void setCn(BigDecimal cn) {
		this.cn = cn;
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
