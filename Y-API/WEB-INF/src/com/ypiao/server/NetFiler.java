package com.ypiao.server;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;
import com.ypiao.util.Constant;

public class NetFiler implements Serializable {

	private static final long serialVersionUID = 1051909784411714499L;

	private File file;

	private long pos = 0;

	private long size = 0;

	private long time = 0;

	private int destroy = 0;

	public final NetFiler add() {
		this.destroy++;
		return this;
	}

	public final void destroy() {
		if (this.destroy-- > 0 || file == null) {
			// Ignored
		} else if (file.isFile()) {
			file.delete(); // 删除临时文件
		}
	}

	public final File makeFile() {
		if (file == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(Constant.ROOTPATH).append(File.separator).append(Constant.OSCACHE).append(File.separator);
			file = new File(sb.append(UUID.randomUUID()).append(".tmp").toString());
			File Pf = file.getParentFile();
			if (Pf.isDirectory()) {
				// Ignored
			} else {
				Pf.mkdirs();
			}
		}
		return file;
	}

	public final long remaining() {
		return (size - pos);
	}

	public final File getFile() {
		return file;
	}

	public final boolean isFile() {
		return (file != null && file.isFile());
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getPos() {
		return pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}

	public final long getSize() {
		return size;
	}

	public final void setSize(long size) {
		this.size = size;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
