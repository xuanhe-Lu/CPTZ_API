package com.ypm.server;

import java.util.Map;
import com.sunsw.net.DataOutputStream;
import com.sunsw.net.IoHandler;
import com.sunsw.net.IoSession;
import com.ypm.bean.Messager;

public class ReadHandler implements IoHandler {

	private Map<Integer, NetRunner> cache;

	public ReadHandler(Map<Integer, NetRunner> cache) {
		this.cache = cache;
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		Integer key = session.getRemoteAddress().hashCode();
		Integer v = SendNoder.KVS.get(key);
		DataOutputStream dos = session.getDataOutputStream();
		try {
			dos.writeByte(0x7F);
			if (v == null) {
				// Ignored
			} else {
				dos.writeByte(v.intValue());
			}
			dos.writeByte(0);
			dos.flush();
		} finally {
			dos.close();
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Object obj = session.getReceiveBuffer();
		if (obj != null && (obj instanceof Messager)) {
			Messager mgr = (Messager) obj;
			try {
				// Ignored
			} finally {
				mgr.destroy();
			}
		}
	}

	@Override
	public void messageReceived(IoSession session, Object obj) throws Exception {
		Messager mgr = (Messager) obj;
		NetRunner net = cache.remove(mgr.getAck());
		if (net == null) {
			mgr.destroy();
		} else {
			net.setMessager(mgr);
			synchronized (net) {
				net.notifyAll();
			}
		}
	}

}
