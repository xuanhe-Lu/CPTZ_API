package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import com.ypiao.util.GMTime;
import com.ypiao.util.GState;
import com.ypiao.util.VeRule;
import org.apache.log4j.Logger;

public class LogOrder implements Serializable {

	private static final long serialVersionUID = 8580510708297348992L;

	private static Logger logger = Logger.getLogger(LogOrder.class);

	private long sid = 0;

	private long uid = 0;

	private long pid = 0;

	private long cid = 0;

	private long cid1 = 0;

	private int tid = 0;

	private String name;

	private BigDecimal rate = BigDecimal.ZERO;

	private int rday, any, day;

	private String way;

	private BigDecimal ads = BigDecimal.ZERO;

	private BigDecimal tma = BigDecimal.ZERO;

	private BigDecimal tmb = BigDecimal.ZERO;

	private BigDecimal tmc = BigDecimal.ZERO;

	private BigDecimal tmd = BigDecimal.ZERO;

	private BigDecimal tme = BigDecimal.ZERO;

	private BigDecimal tmf = BigDecimal.ZERO;

	private BigDecimal tmg = BigDecimal.ZERO;

	private BigDecimal yma = BigDecimal.ZERO;

	private BigDecimal ymb = BigDecimal.ZERO;

	private long gmtA, gmtB, gmtC, gmtD;

	private String url;

	private int state = 0;

	private long time = 0;

	/** 汇总收益信息 */
	public boolean execute(int ah, int day) {
		logger.info("LogOrder.execute，ah:"+ah+"day:"+day);
		long eday = GMTime.valueOf(ah); // 到期时间
		logger.info("eday:"+eday);
		long tday = (eday - GState.USER_TODAY) / GMTime.MILLIS_PER_DAY;
		logger.info("tday:"+tday);
		int all = day; // 理财天数
		if (Integer.MAX_VALUE >= tday) {
			all = (int) tday;
		}
		int tx = (all - day);
		this.setRday(all);
		this.setDay(tx);
		this.setYmb(BigDecimal.ZERO);
		BigDecimal jday = new BigDecimal(all);
		this.setTmg(VeRule.income(tma, rate, jday));
		if (tme.compareTo(BigDecimal.ZERO) >= 1) {
			this.setTmf(VeRule.income(tma, tme, jday));
			this.setYma(tmb.add(tmc).subtract(tmd).add(tmg).add(tmf));
			if (tx >= 1) {
				this.setYmb(VeRule.income(tma, rate.add(tme), tx));
			}
		} else {
			this.setYma(tmb.add(tmc).subtract(tmd).add(tmg));
			if (tx >= 1) {
				this.setYmb(VeRule.income(tma, rate, tx));
			}
		} // 起息时间
		this.setGmtB(GState.USER_TODAY);
		this.setGmtC(eday);
		return (tx >= 0);
	}

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

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
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

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public int getRday() {
		return rday;
	}

	public void setRday(int rday) {
		this.rday = rday;
	}

	public int getAny() {
		return any;
	}

	public void setAny(int any) {
		this.any = any;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public BigDecimal getAds() {
		return ads;
	}

	public void setAds(BigDecimal ads) {
		this.ads = ads;
	}

	public BigDecimal getTma() {
		return tma;
	}

	public void setTma(BigDecimal tma) {
		this.tma = tma;
	}

	public BigDecimal getTmb() {
		return tmb;
	}

	public void setTmb(BigDecimal tmb) {
		this.tmb = tmb;
	}

	public BigDecimal getTmc() {
		return tmc;
	}

	public void setTmc(BigDecimal tmc) {
		this.tmc = tmc;
	}

	public BigDecimal getTmd() {
		return tmd;
	}

	public void setTmd(BigDecimal tmd) {
		this.tmd = tmd;
	}

	public BigDecimal getTme() {
		return tme;
	}

	public void setTme(BigDecimal tme) {
		this.tme = tme;
	}

	public BigDecimal getTmf() {
		return tmf;
	}

	public void setTmf(BigDecimal tmf) {
		this.tmf = tmf;
	}

	public BigDecimal getTmg() {
		return tmg;
	}

	public void setTmg(BigDecimal tmg) {
		this.tmg = tmg;
	}

	public BigDecimal getYma() {
		return yma;
	}

	public void setYma(BigDecimal yma) {
		this.yma = yma;
	}

	public BigDecimal getYmb() {
		return ymb;
	}

	public void setYmb(BigDecimal ymb) {
		this.ymb = ymb;
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

	public long getGmtD() {
		return gmtD;
	}

	public void setGmtD(long gmtD) {
		this.gmtD = gmtD;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public long getCid1() {
		return cid1;
	}

	public void setCid1(long cid1) {
		this.cid1 = cid1;
	}
}
