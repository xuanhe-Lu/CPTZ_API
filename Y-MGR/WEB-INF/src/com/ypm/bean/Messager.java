package com.ypm.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ypm.util.AUtils;

public abstract class Messager implements Serializable {

	private static final long serialVersionUID = 5877508503164269513L;

	private static final String KEY_LIST = "list";

	private static final String KEY_OBJ = "obj";

	protected Map<String, String> cache = new HashMap<String, String>();

	protected Map<String, Object> objs = new HashMap<String, Object>();

	protected int clazz = 0;

	protected int code = -10;

	protected String action = "index";

	protected String message = null;

	protected File file;

	public abstract Messager add();

	public abstract void destroy();

	public final boolean contains(String key) {
		return cache.containsKey(key);
	}

	public boolean failure() {
		return (code == -1);
	}

	public abstract int getAck();

	public int getClazz() {
		return clazz;
	}

	public int getCode() {
		return code;
	}

	public String getAction() {
		return action;
	}

	public String getMessage() {
		return message;
	}

	public File getFile() {
		return file;
	}

	public boolean isFile() {
		return (file != null && file.isFile());
	}

	public boolean isList() {
		return contains(KEY_LIST);
	}

	public boolean isObject() {
		return contains(KEY_OBJ);
	}

	public float getFloat(String key) {
		return AUtils.toFloat(getString(key));
	}

	public double getDouble(String key) {
		return AUtils.toDouble(getString(key));
	}

	public int getInt(String key) {
		return AUtils.toInt(getString(key));
	}

	public long getLong(String key) {
		return AUtils.toLong(getString(key));
	}

	public String getString(String key) {
		return cache.get(key);
	}

	public <E> List<E> getList(Class<E> cls) {
		return getList(KEY_LIST, cls);
	}

	public <E> List<E> getList(String key, Class<E> cls) {
		@SuppressWarnings("unchecked")
		List<E> ls = (List<E>) this.objs.get(key);
		if (ls == null) {
			ls = AUtils.toObjects(cls, getString(key));
			this.objs.put(key, ls);
		}
		return ls;
	}

	public <E> E getObject(Class<E> cls) {
		return getObject(KEY_OBJ, cls);
	}

	public <E> E getObject(String key, Class<E> cls) {
		@SuppressWarnings("unchecked")
		E e = (E) this.objs.get(key);
		if (e == null) {
			e = AUtils.toObject(cls, getString(key));
			this.objs.put(key, e);
		}
		return e;
	}

	/** 检测是否成功操作 */
	public boolean successfully() throws IOException {
		try {
			if (failure()) {
				throw new IOException(getMessage());
			}
			return true;
		} finally {
			this.destroy();
		}
	}
}
