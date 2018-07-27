package com.ypm.server;

import java.io.File;
import java.util.UUID;
import com.ypm.bean.Messager;
import com.ypm.util.AUtils;
import com.ypm.util.Constant;

public class NetMessager extends Messager {

	private static final long serialVersionUID = 6169288992262282056L;

	public static NetMessager getBean(NetFuture future) {
		NetMessager mgr = NetNode.getMessager();
		mgr.destroy = 0;
		mgr.future = future;
		return mgr;
	}

	private int ack = 0;

	private int cls = 0;

	private int rev = 0;

	private int destroy = 0;

	private NetFuture future;

	public final NetMessager add() {
		this.destroy++;
		return this;
	}

	@Override
	public final void destroy() {
		if (this.destroy-- <= 0) {
			this.cache.clear();
			this.objs.clear();
			NetNode.offer(this);
		}
	}

	public final int getAck() {
		return ack;
	}

	public final int getCls() {
		return cls;
	}

	public final int getRev() {
		return rev;
	}

	public File makeFile() {
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

	public NetFuture getFuture() {
		return future;
	}

	public void setFuture(NetFuture future) {
		this.future = future;
	}

	public void setClass(int cls, int ack, int rev, int code) {
		this.cls = cls;
		this.ack = ack;
		this.rev = rev;
		this.code = code;
	}

	public void setBody(String text) {
		if (text == null) {
			// Ignored
		} else {
			for (String str : text.split(AUtils.SEP[0])) {
				int a = str.indexOf(":");
				if (a != -1) {
					String k = str.substring(0, a);
					String v = str.substring(a + 1);
					cache.put(k, v);
				}
			} // 解析基本信息
			this.action = getString("act");
			this.message = getString("message");
		}
	}

}
