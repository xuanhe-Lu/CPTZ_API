package com.ypiao.bean;

import java.io.Serializable;

public class TagIndex implements Serializable {

	private static final long serialVersionUID = -5654503448586401891L;

	private static final int TIMEOUT = 5 * 60 * 1000;

	private String key;

	private long next = 0;

	private long time = 0;

	private String body;

	public TagIndex() {
	}

	public void expired() {
		this.next = 0;
	}

	/** 过期处理 */
	public boolean isExpired() {
		this.time = System.currentTimeMillis();
		if (body == null) {
			return true;
		} else {
			return (time >= next);
		}
	}

	/** 刷新处理 */
	public boolean isRefresh() {
		return (TIMEOUT >= (this.time - this.next));
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.next = System.currentTimeMillis() + TIMEOUT;
		this.body = body;
	}
}
