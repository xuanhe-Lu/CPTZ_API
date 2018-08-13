package com.ypiao.service.imp;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.framework.context.ApplicationContext;
import com.ypiao.bean.Manager;
import com.ypiao.bean.NetService;
import com.ypiao.bean.UserSession;
import com.ypiao.server.APIServer;
import com.ypiao.service.APIBaseService;
import com.ypiao.service.APInterService;
import com.ypiao.service.Abstracter;

public class APIBaseServiceImp implements APIBaseService {

	private static Logger logger = Logger.getLogger(APIBaseServiceImp.class);

	private Abstracter apis;

	private final Map<Long, NetService> cache;

	private final Map<Long, UserSession> yings;

	public APIBaseServiceImp() {
		this.cache = new ConcurrentHashMap<Long, NetService>(1000);
		this.yings = new ConcurrentHashMap<Long, UserSession>(1000);
	}

	public final void initServer(ApplicationContext ac) {
		try {
			this.apis = new com.ypiao.service.aps.AbstractAPS(ac);
			APIServer.initServer(this);
		} catch (Exception e) {
			// Ignored
		}
	}

	public final NetService addNeter(NetService net) {
		return cache.put(net.getId(), net);
	}

	public final NetService getNetService(Long sid) {
		return cache.get(sid);
	}

	public NetService remove(long sid) {
		return cache.remove(sid);
	}

	public NetService remove(NetService net) {
		return remove(net.getId());
	}

	public final UserSession addNeter(UserSession us) {
		UserSession os = null;
		if (us == null) {
			// Ignored
		} else {
			os = yings.get(us.getUid());
			this.yings.put(us.getUid(), us);
		}
		return os;
	}

	public final UserSession getUserSession(long uid) {
		return yings.get(uid);
	}

	public UserSession remove(UserSession us) {
		return yings.remove(us.getUid());
	}

	public final void sendAdmin(Manager mgr) throws IOException {
		logger.info("come in sendAdmin");
		APInterService service = this.apis.getInterface(mgr.getClazz());
		try {
			if (service == null) {
				mgr.getMessage().addError("Admin No Service!");
			} else {
				service.execute(mgr);
			}
		} finally {
			mgr.sendReply();
		}
	}

}
