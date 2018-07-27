package com.ypm.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetNode {

	private static final int SIZE = 12;

	private static BlockingQueue<NetManager> MAGRS = new LinkedBlockingQueue<NetManager>(SIZE);

	private static BlockingQueue<NetMessager> MGRS = new LinkedBlockingQueue<NetMessager>(SIZE);

	private static BlockingQueue<NetRunner> NETS = new LinkedBlockingQueue<NetRunner>(SIZE);

	private static SendFuture send6AES = new SendAt6AES();

	public static NetManager getManager() {
		NetManager mgr = MAGRS.poll();
		if (mgr == null) {
			mgr = new NetManager();
		}
		return mgr;
	}

	public static boolean offer(NetManager mgr) {
		return MAGRS.offer(mgr);
	}

	public static NetMessager getMessager() {
		NetMessager mgr = MGRS.poll();
		if (mgr == null) {
			mgr = new NetMessager();
		}
		return mgr;
	}

	public static boolean offer(NetMessager mgr) {
		return MGRS.offer(mgr);
	}

	public static NetRunner getNetRunner(int ack) {
		NetRunner net = NETS.poll();
		if (net == null) {
			net = new NetRunner();
		} else {
			net.clear();
		}
		net.setAck(ack);
		return net;
	}

	public static boolean offer(NetRunner net) {
		net.clear(); // clear info
		return NETS.offer(net);
	}

	/** 0x7F */
	public static NetFuture getAdmin() {
		NetFuture file = new ReadFile();
		NetFuture body = new ReadBody(file);
		return new Read6Int(body);
	}

	public static SendFuture sendAt6AES() {
		return send6AES;
	}

}
