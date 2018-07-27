package com.ypiao.server;

import com.sunsw.net.IoHandler;
import com.sunsw.net.IoSession;
import com.ypiao.bean.Manager;
import com.ypiao.bean.UserSession;
import com.ypiao.service.APIBaseService;

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
			} // session info
			obj = session.getUseSession();
			if (obj != null && (obj instanceof UserSession)) {
				UserSession us = (UserSession) obj;
				try {
					this.service.remove(us);
					NetNoder net = (NetNoder) us.getNetService();
					net.destroy(); // Destroy IoSession!
				} finally {
					us.destroy();
				}
			}
		} finally {
			this.service.remove(session.getId());
		}
	}

	@Override
	public void messageReceived(IoSession session, Object obj) throws Exception {
		final NetManager mgr = (NetManager) obj;
		session.getService().getExecutor().execute(new Runnable(){
			public void run() {
				try {
					session.getProcessor().remove(session);
				} finally {
					mgr.destroy();
				}
			}
		});
	}

}
