package com.ypm.service.imp;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.framework.context.ApplicationContext;
import com.ypm.bean.Manager;
import com.ypm.bean.NetService;
import com.ypm.server.APIServer;
import com.ypm.service.APIBaseService;
import com.ypm.service.APInterService;
import com.ypm.service.Abstracter;

public class APIBaseServiceImp implements APIBaseService {

	private Abstracter apps;

	private final Map<Long, NetService> cache;

	public APIBaseServiceImp() {
		this.cache = new ConcurrentHashMap<Long, NetService>(1000);
	}

	public void initServer(ApplicationContext ac) {
		try {
			this.apps = new com.ypm.service.aps.AbstractAPS(ac);
			APIServer.initServer(this);
		} catch (Exception e) {
			// Ignored
		}
	}

	public NetService addNeter(NetService net) {
		return cache.put(net.getId(), net);
	}

	public NetService remove(long sid) {
		return cache.get(sid);
	}
	/** APS数据接口 */
	public void sendReply(Manager mgr) throws IOException {
		APInterService service = this.apps.getInterface(mgr.getClazz());
		try {
			if (service == null) {
				mgr.getMessage().addError("No Service!");
			} else {
				service.execute(mgr);
			}
		} finally {
			mgr.sendReply();
		}
	}

}
