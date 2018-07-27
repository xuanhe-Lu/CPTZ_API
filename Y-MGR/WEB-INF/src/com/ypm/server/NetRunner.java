package com.ypm.server;

import java.io.Serializable;
import com.ypm.bean.Messager;

public class NetRunner implements Serializable {

	private static final long serialVersionUID = 8182254805231561764L;

	private boolean ready = false;

	private int ack = 0;

	private Messager mgr;

	public final void clear() {
		this.ready = false;
		this.ack = 0;
		this.mgr = null;
	}

	public boolean isReady() {
		return ready;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public Messager getMessager() {
		return mgr;
	}

	public void setMessager(Messager mgr) {
		this.mgr = mgr;
		this.ready = true;
	}
}
