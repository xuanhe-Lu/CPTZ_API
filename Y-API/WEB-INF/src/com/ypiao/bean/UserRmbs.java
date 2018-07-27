package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserRmbs implements Serializable {

	private static final long serialVersionUID = -2842876715500163071L;

	private long sid = 0;

	private int tid = 0;

	private long fid, uid = 0;

	private String way, event;

	private BigDecimal cost = BigDecimal.ZERO;

	private BigDecimal adds = BigDecimal.ZERO;

	private BigDecimal total = BigDecimal.ZERO;

	private int state = 0;

	private long time = 0;

	public UserRmbs() {
	}

	public UserRmbs(long uid) {
		this.uid = uid;
	}

	public void add(BigDecimal rmb) {
		this.adds = rmb;
		this.cost = total;
		this.total = cost.add(rmb);
	}

	public boolean sub(BigDecimal rmb) {
		this.cost = total;
		if (rmb.compareTo(BigDecimal.ZERO) >= 1) {
			this.adds = rmb.negate();
			this.total = cost.subtract(rmb);
		} else {
			this.adds = rmb;
			this.total = cost.add(rmb);
		} // 余额不能为负数
		return (total.compareTo(BigDecimal.ZERO) >= 0);
	}

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public long getFid() {
		return fid;
	}

	public void setFid(long fid) {
		this.fid = fid;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getAdds() {
		return adds;
	}

	public void setAdds(BigDecimal adds) {
		this.adds = adds;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
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

	@Override
	public String toString() {
		return "UserRmbs{" +
				"sid=" + sid +
				", tid=" + tid +
				", fid=" + fid +
				", uid=" + uid +
				", way='" + way + '\'' +
				", event='" + event + '\'' +
				", cost=" + cost +
				", adds=" + adds +
				", total=" + total +
				", state=" + state +
				", time=" + time +
				'}';
	}
}
