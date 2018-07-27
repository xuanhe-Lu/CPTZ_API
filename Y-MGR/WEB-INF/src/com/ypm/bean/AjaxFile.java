package com.ypm.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.commons.io.FileUtils;
import com.ypm.util.Constant;

public class AjaxFile implements Serializable {

	private static final long serialVersionUID = 1206118879926134324L;

	private static final int SIZE = 5;

	private static List<AjaxFile> CACHE = new ArrayList<AjaxFile>(SIZE);

	private StringBuilder sb = new StringBuilder();

	private Charset encoding = Constant.CHARSET;

	private boolean expired = false;

	private File file;

	private long size = -1;

	private long time = -1;

	private String text;

	public static AjaxFile get(String fileName) {
		return get(null, null, fileName);
	}

	public static AjaxFile get(String dir, String name) {
		return get(dir, null, name);
	}

	public static AjaxFile get(String dir, String fix, Object name) {
		AjaxFile f = null;
		if (CACHE.size() > 0) {
			f = CACHE.remove(0);
		} else {
			f = new AjaxFile();
		}
		f.set(dir, fix, name);
		return f;
	}

	private void set(String dir, String fix, Object name) {
		this.expired = true;// 初始化状态
		this.time = -1;		// 初始化时间
		try {
			sb.setLength(0);
			sb.append(Constant.ROOTPATH).append(Constant.OSCACHE);
			if (dir != null) sb.append(File.separator).append(dir.replace('/', File.separatorChar));
			if (fix == null) {
				sb.append(File.separator);
			} else {
				sb.append(File.separator).append(fix);
			} // file info
			this.file = new File(sb.append(name).append(".txt").toString());
			if (this.file.isFile()) {
				this.time = this.file.lastModified();
				this.text = FileUtils.readFile(this.file, this.encoding);
				this.size = this.text.length();
				long time = System.currentTimeMillis();
				if (this.time > (time - 300000)) {
					this.expired = false;
				} else {
					this.file.setLastModified(time);
				}
			} else {
				this.size = -1;
			}
		} catch (Exception e) {
			this.size = -1;
		} finally {
			sb.setLength(0);
		}
	}

	public void close() {
		if (this.isExpired()) {
			// Ignored
		} else if (CACHE.size() < SIZE) {
			this.text = null;
			CACHE.add(this);
		}
	}
	/** 删除缓存文件 */
	public void delete() {
		if (this.file.isFile()) {
			this.file.delete();
		}
	}

	public String getBody() {
		return text;
	}

	public long getTime() {
		return time;
	}
	/** 文件缓存过期 */
	public boolean isExpired() {
		return expired;
	}
	/** 文件加载失败 */
	public boolean isFailed() {
		return (size < 1);
	}
	/** 缓存写入 */
	public void write(AjaxInfo json) {
		if (this.isFailed()) {
			this.write(json.toString());
		} else {
			this.write(json.getString());
		}
	}

	public void write(String data) {
		try {
			this.size = data.length();
			this.text = data; // text
			FileUtils.write(this.file, data, this.encoding);
		} catch (IOException e) {
			// Ignored
		} finally {
			this.expired = false;
		}
	}

}
