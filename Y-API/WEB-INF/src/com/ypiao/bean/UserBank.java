package com.ypiao.bean;

import java.io.Serializable;

public class UserBank implements Serializable {

	private static final long serialVersionUID = 1034180583397578035L;

	private long uid;

	private int bid = 0;

	private String cardNo;

	private String code;

	private String bankId;

	private String bankName;

	private int binId, binStat;

	private String cardName, cardTy, channel;

	private String idCard;

	private String mobile;

	private String name;

	private long gmtA, gmtB, gmtC;

	private int gdef = 0;

	private int state = 0;

	private long time = 0;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public int getBinId() {
		return binId;
	}

	public void setBinId(int binId) {
		this.binId = binId;
	}

	public int getBinStat() {
		return binStat;
	}

	public void setBinStat(int binStat) {
		this.binStat = binStat;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCardTy() {
		return cardTy;
	}

	public void setCardTy(String cardTy) {
		this.cardTy = cardTy;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
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

	public long getGmtA() {
		return gmtA;
	}

	public void setGmtA(long gmtA) {
		this.gmtA = gmtA;
	}

	public long getGmtB() {
		return gmtB;
	}

	public void setGmtB(long gmtB) {
		this.gmtB = gmtB;
	}

	public long getGmtC() {
		return gmtC;
	}

	public void setGmtC(long gmtC) {
		this.gmtC = gmtC;
	}

	public int getGdef() {
		return gdef;
	}

	public void setGdef(int gdef) {
		this.gdef = gdef;
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
