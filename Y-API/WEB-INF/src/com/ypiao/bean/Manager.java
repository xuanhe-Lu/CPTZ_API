package com.ypiao.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.ypiao.util.AUtils;

public abstract class Manager implements Serializable {

	private static final long serialVersionUID = 5489021826281749286L;

	private static final String KEY_LIST = "list";

	protected Map<String, String> cache = new HashMap<String, String>();

	protected Map<String, String> heads = new HashMap<String, String>();

	protected Integer _hash;

	protected Message message = Message.get();

	protected int clazz = 0;

	protected int code = 0;

	protected String action;

	protected String body;

	private long time = 0;

	public abstract Manager add();

	public abstract void destroy();

	public abstract void formater();

	public abstract long getSid();

	public abstract String getForwarded();

	public abstract InetSocketAddress getRemoteAddress();

	public abstract Locale getLocale();

	public abstract UserSession getSession();

	public abstract void setSession(UserSession us);

	public abstract void sendReply() throws IOException;

	public abstract void sendReply(boolean transmit) throws IOException;
	/** 带次数发送信息 */
	public abstract boolean sendReply(int size) throws IOException;

	public abstract boolean sendReplys() throws IOException;

	public abstract void setTransmit(boolean transmit);

	public Integer hashKey() {
		if (_hash == null) {
			if (body == null) {
				_hash = new Integer(0);
			} else {
				_hash = body.hashCode();
			}
		} // body code
		return _hash;
	}

	public Message getMessage() {
		return message;
	}

	public String getMessage(String name) {
		return getMessage(name, null);
	}

	public String getMessage(String name, Object[] objs) {
		if (name.indexOf('.') == -1) {
			return name;
		} else {
			return com.ypiao.service.SysBeaner.getMessage(name, objs, getLocale());
		}
	}

	public int getClazz() {
		return clazz;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getAction() {
		return action;
	}

	public String getBody() {
		return body;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	// ==================== 国际化方案法 ====================
	public Message addError(int code, String name) {
		return message.addError(code, getMessage(name));
	}

	public Message addError(String name) {
		return addError(name, null);
	}

	public Message addError(String name, Object[] objs) {
		return message.addError(getMessage(name, objs));
	}

	public Message addMessage(String name) {
		return message.addMessage(getMessage(name));
	}
	// ==================== 参数获取方法 ====================
	public abstract File getFile();

	public abstract boolean isFile();

	public abstract void setFile(File file);
	/** 文件名称获取 */
	public String getFileName() {
		return this.getParameter("filename");
	}
	/** 参数信息集 */
	public Map<String, String> getParameters() {
		return cache;
	}
	/** Key-Value */
	public String getParameter(String key) {
		return this.cache.get(key);
	}

	public String getString(String key) {
		String str = getParameter(key);
		if (str == null) {
			return null;
		} else {
			String txt = str.trim();
			if (txt.length() <= 0 || txt.equalsIgnoreCase("null")) {
				return null;
			} else {
				return txt;
			}
		}
	}

	public String getTable() {
		return cache.get("_tbl");
	}

	public BigDecimal getBigDecimal(String key) {
		try {
			return new BigDecimal(getString(key));
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	public float getFloat(String key) {
		return AUtils.toFloat(getParameter(key));
	}

	public double getDouble(String key) {
		return AUtils.toDouble(getParameter(key));
	}

	public int getInt(String key) {
		return AUtils.toInt(getParameter(key));
	}

	public int getInt(String key, int def) {
		return AUtils.toInt(getParameter(key), def);
	}

	public long getLong(String key) {
		return AUtils.toLong(getParameter(key));
	}

	public long getLong(String key, long def) {
		return AUtils.toLong(getParameter(key), def);
	}

	public boolean isList() {
		return this.isParameter(KEY_LIST);
	}

	public boolean isParameter(String key) {
		return (cache.containsKey(key));
	}
	// ==================== 公共方法调用 ====================
	public <E> E getObject(Class<E> cls) {
		return AUtils.toObject(cls, cache);
	}

	public <E> List<E> getList(Class<E> cls) {
		return getList(KEY_LIST, cls);
	}

	public <E> List<E> getList(String key, Class<E> cls) {
		return AUtils.toObjects(cls, cache.get(key), 2);
	}

	public void setObject(Object obj) {
		AUtils.convert(obj, cache);
	}

}
