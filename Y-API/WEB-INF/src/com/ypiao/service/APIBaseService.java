package com.ypiao.service;

import java.io.IOException;
import org.framework.context.ApplicationContext;
import com.ypiao.bean.Manager;
import com.ypiao.bean.NetService;
import com.ypiao.bean.UserSession;

public interface APIBaseService {

	public void initServer(ApplicationContext ac);
	/** 缓存接入对象 */
	public NetService addNeter(NetService net);
	/** 获取接入对象 */
	public NetService getNetService(Long sid);
	/** 移除接入对象 */
	public NetService remove(long sid);
	/** 移除接入对象 */
	public NetService remove(NetService net);

	public UserSession addNeter(UserSession us);

	public UserSession getUserSession(long uid);

	public UserSession remove(UserSession us);
	/** 管理后台接口 */
	public void sendAdmin(Manager mgr) throws IOException;

}
