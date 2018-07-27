package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class LogCharge implements Serializable {

	private static final long serialVersionUID = 9040537379652407316L;

	private long sid = 0;

	private long uid = 0;

	private String idCard;

	private String mobile;

	private String name;

	private String bankName;

	private String bankCard;

	private String backUrl;

	private BigDecimal amount = BigDecimal.ZERO;

	private String signtp;

	private String signpay;

	private String OrderId;

	private String res_code;

	private String res_msg;

	private String vercd;

	private String HSIP;

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

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getSigntp() {
		return signtp;
	}

	public void setSigntp(String signtp) {
		this.signtp = signtp;
	}

	public String getSignpay() {
		return signpay;
	}

	public void setSignpay(String signpay) {
		this.signpay = signpay;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		OrderId = orderId;
	}

	public String getRes_code() {
		return res_code;
	}

	public void setRes_code(String res_code) {
		this.res_code = res_code;
	}

	public String getRes_msg() {
		return res_msg;
	}

	public void setRes_msg(String res_msg) {
		this.res_msg = res_msg;
	}

	public String getVercd() {
		return vercd;
	}

	public void setVercd(String vercd) {
		this.vercd = vercd;
	}

	public String getHSIP() {
		return HSIP;
	}

	public void setHSIP(String hSIP) {
		HSIP = hSIP;
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
