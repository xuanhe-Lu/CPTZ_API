package com.ypm.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class AdminLoger implements Serializable {

	private static final long serialVersionUID = -5637248094065099992L;

	private static final int cfo_USER = 1;

	private static final String CFO_USER = "将用户【%d】余额由【%.2f】变更为【%.2f】。";

	private static final String DEF_INFO = "原信息【%2$s】变更为【%3$s】。";

	public static AdminLoger cfoUSER() {
		return get(cfo_USER);
	}

	public static AdminLoger get(int type) {
		return new AdminLoger(type);
	}

	private int type = 0;

	private long sid;

	private long uid, fid;

	private String name;

	private String event;

	private String source, value;

	private BigDecimal A, B, C;

	private String SIP;

	private long time;

	public AdminLoger(int type) {
		this.type = type;
	}

	public String doEvent() {
		switch (type) {
		case cfo_USER:
			if (A == null) {
				A = BigDecimal.ZERO;
			}
			if (C == null) {
				C = BigDecimal.ZERO;
			} // 格式化数据
			return String.format(CFO_USER, fid, A, C);
		default:
			if (source == null) {
				source = "";
			}
			if (value == null) {
				value = "";
			} // 格式化数据
			return String.format(DEF_INFO, fid, source, value);
		}
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

	public long getFid() {
		return fid;
	}

	public void setFid(long fid) {
		this.fid = fid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEvent() {
		if (event == null) {
			event = this.doEvent();
		}
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public BigDecimal getA() {
		return A;
	}

	public void setA(BigDecimal a) {
		A = a;
	}

	public BigDecimal getB() {
		return B;
	}

	public void setB(BigDecimal b) {
		B = b;
	}

	public BigDecimal getC() {
		return C;
	}

	public void setC(BigDecimal c) {
		C = c;
	}

	public String getSIP() {
		return SIP;
	}

	public void setSIP(String sIP) {
		SIP = sIP;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
