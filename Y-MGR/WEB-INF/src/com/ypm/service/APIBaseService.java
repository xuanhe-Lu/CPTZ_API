package com.ypm.service;

import java.io.IOException;
import org.framework.context.ApplicationContext;
import com.ypm.bean.Manager;
import com.ypm.bean.NetService;

public interface APIBaseService {

	public void initServer(ApplicationContext ac);

	public NetService addNeter(NetService net);

	public NetService remove(long sid);
	/** API数据接口 */
	public void sendReply(Manager mgr) throws IOException;

}
