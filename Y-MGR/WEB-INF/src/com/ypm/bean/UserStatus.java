package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserStatus implements Serializable {

	private static final long serialVersionUID = -5776707497261109513L;

	private long uid, ups;

	private int vip = 0;

	private int pay = 0;

	private int cid = 0;

	private String name, mobile, idCard;

	private int bkId = 0;

	private String bkName, bkInfo;

	private int binds, reals;

	private String rinfo, rtels;

	private long rtime = 0;

	private BigDecimal ma = BigDecimal.ZERO;

	private BigDecimal mb = BigDecimal.ZERO;

	private BigDecimal mc = BigDecimal.ZERO;

	private BigDecimal md = BigDecimal.ZERO;

	private BigDecimal me = BigDecimal.ZERO;

	private BigDecimal mf = BigDecimal.ZERO;

	private BigDecimal mg = BigDecimal.ZERO;

	private int na, nb, nc, nm, np;

	private int state = 0;

	private long time = 0;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getUPS() {
		return ups;
	}

	public void setUPS(long ups) {
		this.ups = ups;
	}

	public int getVIP() {
		return vip;
	}

	public void setVIP(int vip) {
		this.vip = vip;
	}

	public int getPay() {
		return pay;
	}

	public void setPay(int pay) {
		this.pay = pay;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public int getBkId() {
		return bkId;
	}

	public void setBkId(int bkId) {
		this.bkId = bkId;
	}

	public String getBkName() {
		return bkName;
	}

	public void setBkName(String bkName) {
		this.bkName = bkName;
	}

	public String getBkInfo() {
		return bkInfo;
	}

	public void setBkInfo(String bkInfo) {
		this.bkInfo = bkInfo;
	}

	public int getBinds() {
		return binds;
	}

	public void setBinds(int binds) {
		this.binds = binds;
	}

	public int getReals() {
		return reals;
	}

	public void setReals(int reals) {
		this.reals = reals;
	}

	public String getRinfo() {
		return rinfo;
	}

	public void setRinfo(String rinfo) {
		this.rinfo = rinfo;
	}

	public String getRtels() {
		return rtels;
	}

	public void setRtels(String rtels) {
		this.rtels = rtels;
	}

	public long getRtime() {
		return rtime;
	}

	public void setRtime(long rtime) {
		this.rtime = rtime;
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

	public int getNa() {
		return na;
	}

	public void setNa(int na) {
		this.na = na;
	}

	public int getNb() {
		return nb;
	}

	public void setNb(int nb) {
		this.nb = nb;
	}

	public int getNc() {
		return nc;
	}

	public void setNc(int nc) {
		this.nc = nc;
	}

	public int getNm() {
		return nm;
	}

	public void setNm(int nm) {
		this.nm = nm;
	}

	public int getNp() {
		return np;
	}

	public void setNp(int np) {
		this.np = np;
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
