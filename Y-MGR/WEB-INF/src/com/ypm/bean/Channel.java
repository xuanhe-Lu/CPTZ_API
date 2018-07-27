package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 渠道统计 Bean.
 * 
 * Created by xk on 2018-05-18.
 */
public class Channel implements Serializable {

	private static final long serialVersionUID = 1L;

	// 渠道编号
	private int sid;
	
	// 渠道分类
	private int tid = 0;

	// 渠道名称
	private String name;
	
	// 注册地址
	private String raddr;
	
	// 下载地址
	private String daddr;
	
	// 注册人数
	private long rcount = 0;
	
	// 实名人数
	private long rname = 0;
	
	// 绑卡人数
	private long bcount = 0;
	
	// 新手标投资人数
	private long ncount = 0;
	
	// 新手标投资金额
	private BigDecimal nmoney = BigDecimal.ZERO;
	
	// 首投人数
	private long fcount = 0;
	
	// 复投人数
	private long scount = 0;
	
	// 首投金额
	private BigDecimal fmoney = BigDecimal.ZERO;
	
	// 复投金额
	private BigDecimal smoney = BigDecimal.ZERO;
	
	// 投资总额
	private BigDecimal tzsum = BigDecimal.ZERO;
	
	// 提现总额
	private BigDecimal txsum = BigDecimal.ZERO;
	
	// 新增存量
	private BigDecimal nastock = BigDecimal.ZERO;
	
	// 平台总存量
	private BigDecimal sstock = BigDecimal.ZERO;
	
	// 更新时间
	private long time = 0;
	
	// Constructor
	public Channel() {
	}

	public Channel(int sid, int tid, String name, String raddr, String daddr, long rcount, long rname, long bcount, long ncount,
			BigDecimal nmoney, long fcount, long scount, BigDecimal fmoney, BigDecimal smoney, BigDecimal tzsum,
			BigDecimal txsum, BigDecimal nastock, BigDecimal sstock, long time) {
		super();
		this.sid = sid;
		this.tid = tid;
		this.name = name;
		this.raddr = raddr;
		this.daddr = daddr;
		this.rcount = rcount;
		this.rname = rname;
		this.bcount = bcount;
		this.ncount = ncount;
		this.nmoney = nmoney;
		this.fcount = fcount;
		this.scount = scount;
		this.fmoney = fmoney;
		this.smoney = smoney;
		this.tzsum = tzsum;
		this.txsum = txsum;
		this.nastock = nastock;
		this.sstock = sstock;
		this.time = time;
	}

	// getter and setter
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

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

	public String getRaddr() {
		return raddr;
	}

	public void setRaddr(String raddr) {
		this.raddr = raddr;
	}

	public String getDaddr() {
		return daddr;
	}

	public void setDaddr(String daddr) {
		this.daddr = daddr;
	}

	public long getRcount() {
		return rcount;
	}

	public void setRcount(long rcount) {
		this.rcount = rcount;
	}
	
	public long getRname() {
		return rname;
	}

	public void setRname(long rname) {
		this.rname = rname;
	}

	public long getBcount() {
		return bcount;
	}

	public void setBcount(long bcount) {
		this.bcount = bcount;
	}

	public long getNcount() {
		return ncount;
	}

	public void setNcount(long ncount) {
		this.ncount = ncount;
	}

	public BigDecimal getNmoney() {
		return nmoney;
	}

	public void setNmoney(BigDecimal nmoney) {
		this.nmoney = nmoney;
	}

	public long getFcount() {
		return fcount;
	}

	public void setFcount(long fcount) {
		this.fcount = fcount;
	}

	public long getScount() {
		return scount;
	}

	public void setScount(long scount) {
		this.scount = scount;
	}

	public BigDecimal getFmoney() {
		return fmoney;
	}

	public void setFmoney(BigDecimal fmoney) {
		this.fmoney = fmoney;
	}

	public BigDecimal getSmoney() {
		return smoney;
	}

	public void setSmoney(BigDecimal smoney) {
		this.smoney = smoney;
	}

	public BigDecimal getTzsum() {
		return tzsum;
	}

	public void setTzsum(BigDecimal tzsum) {
		this.tzsum = tzsum;
	}

	public BigDecimal getTxsum() {
		return txsum;
	}

	public void setTxsum(BigDecimal txsum) {
		this.txsum = txsum;
	}

	public BigDecimal getNastock() {
		return nastock;
	}

	public void setNastock(BigDecimal nastock) {
		this.nastock = nastock;
	}

	public BigDecimal getSstock() {
		return sstock;
	}

	public void setSstock(BigDecimal sstock) {
		this.sstock = sstock;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	// toString method
	@Override
	public String toString() {
		return "Channel [sid=" + sid + ", tid=" + tid + ", name=" + name + ", raddr=" + raddr + ", daddr=" + daddr
				+ ", rcount=" + rcount + ", rname=" + rname + ", bcount=" + bcount + ", ncount=" + ncount + ", nmoney="
				+ nmoney + ", fcount=" + fcount + ", scount=" + scount + ", fmoney=" + fmoney + ", smoney=" + smoney
				+ ", tzsum=" + tzsum + ", txsum=" + txsum + ", nastock=" + nastock + ", sstock=" + sstock + ", time="
				+ time + "]";
	}

}

