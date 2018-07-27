package com.ypiao.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetNode {

	private static final int SIZE = 32;

	private static BlockingQueue<NetManager> MAGRS = new LinkedBlockingQueue<NetManager>(SIZE);

	private static BlockingQueue<NetMessager> MGRS = new LinkedBlockingQueue<NetMessager>(SIZE);

	private static BlockingQueue<NetRunner> NETS = new LinkedBlockingQueue<NetRunner>(SIZE);

	private static SendFuture send5AES = new SendAt5AES();

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

	public static NetFuture getFiler() {
		NetFuture file = new ReadFiler();
		return new ReadFile9(file);
	}

	/** 0x35 */
	public static NetFuture get5AES() {
		NetFuture file = new ReadFile();
		NetFuture body = new Read5Body(file);
		NetFuture head = new Read5Head(body);
		return new Read5Int(head);
	}

	/** 0x36 */
	public static NetFuture get6AES() {
		NetFuture file = new ReadFile();
		NetFuture body = new Read6Body(file);
		NetFuture head = new Read6Head(body);
		return new Read6Int(head);
	}

	/** 0x7F */
	public static NetFuture getAdmin(Object obj) {
		NetFuture file = new ReadFiles(obj);
		NetFuture body = new ReadBodys(file, obj);
		return new Read6Int(body);
	}

	public static SendFuture sendAt5AES() {
		return send5AES;
	}

	public static SendFuture sendAt6AES() {
		return send6AES;
	}
}
