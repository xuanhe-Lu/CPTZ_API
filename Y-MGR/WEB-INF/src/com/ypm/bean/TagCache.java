package com.ypm.bean;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class TagCache implements Serializable {

	private static final long serialVersionUID = 4802230197680301823L;

	private Map<String, String> map = new LinkedHashMap<String, String>();

	private File file;

	private long last = 0;

	private long time = 0;

	public TagCache(File file) {
		this.file = file;
		this.last = file.lastModified();
	}

	public void add(String key, String value) {
		this.map.put(key, value);
	}

	public boolean isExpired() {
		if (map.size() <= 0)
			return true;
		if (System.currentTimeMillis() < this.time) {
			return false;
		} // 检测过期信息
		this.time = System.currentTimeMillis() + 20000;
		if (file.lastModified() == this.last) {
			return false;
		} else {
			this.last = file.lastModified();
			return true;
		}
	}

	public Map<String, String> getInfo() {
		return map;
	}

}
