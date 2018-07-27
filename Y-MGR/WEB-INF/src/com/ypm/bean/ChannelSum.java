package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 渠道汇总 Bean.
 * 
 * Created by xk on 2018-06-08.
 */
public class ChannelSum implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// 平台投资总额(元)
	private BigDecimal sum = BigDecimal.ZERO;
	
	// 平台存量(元)
	private BigDecimal stock = BigDecimal.ZERO;
	
	// 投资未到期总金额(元)
	private BigDecimal noexpire = BigDecimal.ZERO;
	
	// 平台收益总金额(元)
	private BigDecimal income = BigDecimal.ZERO;
	
	// 平台注册人数
	private long rcount = 0;
	
	// 已实名认证人数
	private long acount = 0;
	
	// 未实名认证人数
	private long nacount = 0;
	
	// 已绑定银行卡人数
	private long bcount = 0;
	
	// 未绑定银行卡人数
	private long nbcount = 0;
	
	// 实际投资人数
	private long tzcount = 0;
	
	// 尚未投资人数
	private long ntzcount = 0;
	
	// 标的贴息总金额
	private BigDecimal txsum = BigDecimal.ZERO;
	
	// 新手投资赠送(元)
	private BigDecimal tzzs = BigDecimal.ZERO;
	
	// 用户充值手续费总额(元)
	private BigDecimal czsxf = BigDecimal.ZERO;
	
	// 用户提现手续费总额(元)
	private BigDecimal txsxf = BigDecimal.ZERO;
	
	// 更新时间
	private long time = 0;
	
	// Constructor
	public ChannelSum() {
	}

	public ChannelSum(BigDecimal sum, BigDecimal stock, BigDecimal noexpire, BigDecimal income, long rcount,
			long acount, long nacount, long bcount, long nbcount, long tzcount, long ntzcount, BigDecimal txsum,
			BigDecimal tzzs, BigDecimal czsxf, BigDecimal txsxf, long time) {
		super();
		this.sum = sum;
		this.stock = stock;
		this.noexpire = noexpire;
		this.income = income;
		this.rcount = rcount;
		this.acount = acount;
		this.nacount = nacount;
		this.bcount = bcount;
		this.nbcount = nbcount;
		this.tzcount = tzcount;
		this.ntzcount = ntzcount;
		this.txsum = txsum;
		this.tzzs = tzzs;
		this.czsxf = czsxf;
		this.txsxf = txsxf;
		this.time = time;
	}

	// Getter and Setter
	public BigDecimal getSum() {
		return sum;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public BigDecimal getStock() {
		return stock;
	}

	public void setStock(BigDecimal stock) {
		this.stock = stock;
	}

	public BigDecimal getNoexpire() {
		return noexpire;
	}

	public void setNoexpire(BigDecimal noexpire) {
		this.noexpire = noexpire;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public long getRcount() {
		return rcount;
	}

	public void setRcount(long rcount) {
		this.rcount = rcount;
	}

	public long getAcount() {
		return acount;
	}

	public void setAcount(long acount) {
		this.acount = acount;
	}

	public long getNacount() {
		return nacount;
	}

	public void setNacount(long nacount) {
		this.nacount = nacount;
	}

	public long getBcount() {
		return bcount;
	}

	public void setBcount(long bcount) {
		this.bcount = bcount;
	}

	public long getNbcount() {
		return nbcount;
	}

	public void setNbcount(long nbcount) {
		this.nbcount = nbcount;
	}

	public long getTzcount() {
		return tzcount;
	}

	public void setTzcount(long tzcount) {
		this.tzcount = tzcount;
	}

	public long getNtzcount() {
		return ntzcount;
	}

	public void setNtzcount(long ntzcount) {
		this.ntzcount = ntzcount;
	}

	public BigDecimal getTxsum() {
		return txsum;
	}

	public void setTxsum(BigDecimal txsum) {
		this.txsum = txsum;
	}

	public BigDecimal getTzzs() {
		return tzzs;
	}

	public void setTzzs(BigDecimal tzzs) {
		this.tzzs = tzzs;
	}

	public BigDecimal getCzsxf() {
		return czsxf;
	}

	public void setCzsxf(BigDecimal czsxf) {
		this.czsxf = czsxf;
	}

	public BigDecimal getTxsxf() {
		return txsxf;
	}

	public void setTxsxf(BigDecimal txsxf) {
		this.txsxf = txsxf;
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
		return "ChannelSum [sum=" + sum + ", stock=" + stock + ", noexpire=" + noexpire + ", income=" + income
				+ ", rcount=" + rcount + ", acount=" + acount + ", nacount=" + nacount + ", bcount=" + bcount
				+ ", nbcount=" + nbcount + ", tzcount=" + tzcount + ", ntzcount=" + ntzcount + ", txsum=" + txsum
				+ ", tzzs=" + tzzs + ", czsxf=" + czsxf + ", txsxf=" + txsxf + ", time=" + time + "]";
	}

}

