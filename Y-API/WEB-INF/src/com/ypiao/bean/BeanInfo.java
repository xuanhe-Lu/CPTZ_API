package com.ypiao.bean;

import java.io.Serializable;

public interface BeanInfo extends Serializable {
	/** 系统默认过期时间  2 小时 */
	public static final long TIMEOUT = 2 * 60 * 60 * 1000;

	public String getSid();

	public String getName();

	public boolean isFailed();

	public boolean isExpired();

	public long lastModified();

	public void setLastModified(long lastModified);

	public void setTimeout(long timeout);

	public void setText(Object text);

	public void setText(String text);
	/** 检测过期 */
	public boolean expired();

}
