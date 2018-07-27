package com.ypiao.bean;

import java.util.ArrayList;
import java.util.List;

public class HtmlBase implements BeanInfo {

	private static final long serialVersionUID = -46725425034684565L;

	protected static final int SIZE = 12;

	private static List<HtmlBase> CACHE = new ArrayList<HtmlBase>(SIZE);

	public synchronized static HtmlBase get() {
		HtmlBase base = null;
		if (CACHE.size() > 0) {
			base = CACHE.remove(0);
		} else {
			base = new HtmlBase();
		} // 默认加载失败
		base.setLastModified(0);
		base.setFailed(true);
		base.setExpired(true);
		return base;
	}

	protected String sid;

	protected String name;

	protected boolean failed = true;

	protected boolean expired = true;

	protected String text;

	protected long lastModified = 0;

	protected long timeout = 0;

	public HtmlBase() {
	}
	/** 关闭当前对象 */
	public void close() {
		this.sid = this.text = null;
		if (CACHE.size() < SIZE) {
			CACHE.add(this);
		}
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public String getText() {
		return text;
	}

	public void setText(Object text) {
		this.setText(String.valueOf(text));
	}

	public void setText(String text) {
		this.text = text;
	}

	public long lastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	/** 检测过期 */
	public boolean expired() {
		long time = System.currentTimeMillis() - this.lastModified;
		if (this.timeout >= time) {
			this.setFailed(false);
			this.setExpired(false);
		} else if (BeanInfo.TIMEOUT > time) {
			this.setFailed(false);
			this.setExpired(true);
		} else {
			this.setFailed(true);
			this.setExpired(true);
		}
		return this.isExpired();
	}

}
