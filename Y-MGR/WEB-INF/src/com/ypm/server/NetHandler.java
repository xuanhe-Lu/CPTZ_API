package com.ypm.server;

import java.io.IOException;
import com.sunsw.net.IoHandler;
import com.sunsw.net.IoSession;
import com.ypm.bean.Manager;
import com.ypm.service.APIBaseService;

public class NetHandler implements IoHandler {

	private final APIBaseService service;

	public NetHandler(APIBaseService service) {
		this.service = service;
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.getConfig().setSoTimeout(10000);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		try {
			Object obj = session.getReceiveBuffer();
			if (obj != null && (obj instanceof Manager)) {
				Manager mgr = (Manager) obj;
				try {
					// Ignored
				} finally {
					mgr.destroy();
				}
			}
		} finally {
			this.service.remove(session.getId());
		}
	}

	@Override
	public void messageReceived(IoSession session, Object object) throws Exception {
		final NetManager mgr = (NetManager) object;
		session.getService().getExecutor().execute(new Runnable(){
			public void run() {
				try {
					service.sendReply(mgr);
				} catch (IOException e) {
					session.getProcessor().remove(session);
				} finally {
					mgr.destroy();
				}
			}
		});
	}

}
