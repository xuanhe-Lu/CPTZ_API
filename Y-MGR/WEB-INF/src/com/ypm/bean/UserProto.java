package com.ypm.bean;

import java.io.Serializable;

public class UserProto implements Serializable {

	private static final long serialVersionUID = -1469182082971187571L;

	private long uid;

	private String cno;

	private String sno;

	private String userId;

	private String orderId;

	private String merorder;

	private int total = 0;

	private int state = 0;

	private long time = 0;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getCNo() {
		return cno;
	}

	public void setCNo(String cno) {
		this.cno = cno;
	}

	public String getSNo() {
		return sno;
	}

	public void setSNo(String sno) {
		this.sno = sno;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getMerorder() {
		return merorder;
	}

	public void setMerorder(String merorder) {
		this.merorder = merorder;
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
