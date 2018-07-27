package com.ypm.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import com.ypm.office.ExcelWriter;
import com.ypm.util.Constant;

public class ExportInfo implements Serializable {

	private static final long serialVersionUID = 2754335402792665060L;

	private static final long BIG_SIZE = 5 * 1024 * 1024;

	/** 超大文件过期时间 */
	private static final long BIG_TIME = 10 * 60 * 1000;

	/** 普通文件过期时间 */
	private static final long GENERAL = 2 * 60 * 1000;

	private ExcelWriter ew;

	private File file;

	private String fileName;

	private long sid = 0;

	private String suffix;

	public ExportInfo(String suffix) {
		this.suffix = suffix;
	}

	/** 创建Excel格式 */
	public ExcelWriter createExcel() {
		return (ew = ExcelWriter.get(suffix));
	}

	public String getFileName() {
		if (fileName == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(sid);
			if (suffix == null || !suffix.equalsIgnoreCase("xls")) {
				sb.append(".xlsx");
			} else {
				sb.append(".xls");
			} // 文件名称
			this.fileName = sb.toString();
		}
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/** 检测文件属性 */
	public boolean isExpired() {
		if (file == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(Constant.ROOTPATH).append(Constant.EXPORTS);
			sb.append(File.separator).append(this.getFileName());
			this.file = new File(sb.toString());
		} // 检测是否有效
		if (!file.isFile()) {
			file.getParentFile().mkdirs();
			return true;
		} // 检测是否过期
		long time = System.currentTimeMillis() - file.lastModified();
		if (file.length() > BIG_SIZE) {
			return (time > BIG_TIME);
		} else {
			return (time > GENERAL);
		}
	}

	public boolean isNotExpired() {
		return (!isExpired());
	}

	public boolean output() throws IOException {
		return ew.output(file);
	}
}
