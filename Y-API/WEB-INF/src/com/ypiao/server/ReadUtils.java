package com.ypiao.server;

import java.io.IOException;
import com.sunsw.net.IoSession;
import com.ypiao.service.APIBaseService;

public class ReadUtils {

	private static void execute(IoSession session, NetManager mgr, APIBaseService service) {
		session.getService().getExecutor().execute(new Runnable() {
			public void run() {
				try {
					service.sendAdmin(mgr);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					mgr.destroy();
				}
			}
		});
	}

	public static boolean invoke(IoSession session, NetManager mgr, int cls) {
		try {
			SendAtFiler send = new SendAtFiler(session, cls);
			send.set(mgr.getFiler().add(), mgr.getFileName());
			session.getService().getExecutor().execute(send);
			return true;
		} finally {
			session.setReceiveBuffer(null);
			mgr.destroy();
		}
	}

	public static boolean invoke(IoSession session, NetManager mgr, Object obj) {
		try {
			if (obj == null) {
				// Ignored
			} else if (obj instanceof APIBaseService) {
				execute(session, mgr.add(), (APIBaseService) obj);
			}
			return true;
		} finally {
			session.setReceiveBuffer(null);
			mgr.destroy();
		}
	}

}
