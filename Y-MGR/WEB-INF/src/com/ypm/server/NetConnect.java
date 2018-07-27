package com.ypm.server;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.sunsw.net.DataOutputStream;
import com.sunsw.net.IoSession;
import com.sunsw.net.future.ConnectFuture;
import com.ypm.bean.Messager;
import com.ypm.bean.ServerInfo;
import com.ypm.bean.SyncInfo;
import com.ypm.service.PoolService;

public class NetConnect {

	private BlockingQueue<SyncInfo> ADDS = new LinkedBlockingQueue<SyncInfo>();

	private Map<Integer, NetRunner> cache;

	public ConnectFuture future;

	private NetSender sender;

	private ServerInfo server;

	private int type = 0;

	public NetConnect(ConnectFuture future, ServerInfo server, Map<Integer, NetRunner> cache, String mfk) {
		this(future, server, cache, Integer.valueOf(mfk, 36));
	}

	public NetConnect(ConnectFuture future, ServerInfo server, Map<Integer, NetRunner> cache, int type) {
		this.future = future;
		this.server = server;
		this.cache = cache;
		this.type = type;
	}

	public SyncInfo getInfo() {
		return ADDS.poll();
	}

	public ServerInfo getServer() {
		return server;
	}

	public long getId() {
		return future.getId();
	}

	private boolean isFile(File file) {
		if (file == null) {
			// Ignored
		} else if (file.isFile()) {
			return (server == null || server.getSid() != 999);
		}
		return false;
	}

	private void send(DataOutputStream dos, int cls, int ack, int rev, String msg, File file) throws IOException {
		try {
			if (this.isFile(file)) {
				dos.writeInt(-cls);
				dos.writeInt(ack);
				dos.writeInt(rev);
				dos.writeAES(msg);
				dos.writeFile(file);
			} else {
				dos.writeInt(cls);
				dos.writeInt(ack);
				dos.writeInt(rev);
				dos.writeAES(msg);
			}
			dos.flush();
		} finally {
			dos.close();
		}
	}

	private Messager send(int cls, int rev, String msg, File file, boolean reset) throws IOException {
		IoSession session = future.getSession();
		if (session == null) {
			return null;
		} // 发送数据处理
		int ack = future.getAck();
		NetRunner net = NetNode.getNetRunner(ack);
		try {
			this.cache.put(ack, net);
			synchronized (net) {
				try {
					future.getId();
					send(session.getDataOutputStream(), cls, ack, rev, msg, file);
					future.offer(session); // 回流池子
					if (net.isReady()) {
						// Ignored
					} else {
						net.wait(15000);
					}
				} catch (IOException e) {
					session.getProcessor().remove(session);
					throw e; // IOException
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Messager mgr = net.getMessager();
			if (mgr == null) {
				session.close(30000);
				if (reset) {
					return send(cls, rev, msg, file, false);
				}
			}
			return mgr;
		} finally {
			this.cache.remove(ack);
			NetNode.offer(net);
		}
	}

	public Messager send(int cls, int rev, String msg) throws IOException {
		return this.send(cls, rev, msg, null, true);
	}

	public Messager send(int cls, int rev, String msg, File file) throws IOException {
		return this.send(cls, rev, msg, file, true);
	}

	/** 发送同步信息 */
	public Messager send(SyncInfo s) throws IOException {
		return send(s.getCls(), s.getRev(), s.getContent(), s.getFile(), false);
	}

	/** 同步滞留信息 */
	public void syncer() {
		if (sender == null || sender.shutdown()) {
			this.sender = new NetSender(this, type);
			PoolService.getService().execute(sender);
		} else {
			this.sender.wakeup();
		}
	}

	public void sender(int cls, int rev, String msg) throws IOException {
		this.sender(cls, rev, msg, null);
	}

	public void sender(int cls, int rev, String msg, File file) {
		SyncInfo info = new SyncInfo();
		info.setCls(cls);
		info.setRev(rev);
		info.setContent(msg);
		info.setFile(file);
		if (this.ADDS.add(info)) {
			this.syncer(); // 启动同步
		} else {
			try {
				this.send(cls, rev, msg, file, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
