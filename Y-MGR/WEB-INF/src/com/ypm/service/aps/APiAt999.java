package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.Manager;
import com.ypm.bean.Message;
import com.ypm.bean.ServerInfo;
import com.ypm.service.NodeInfoService;

public class APiAt999 extends Abstract {

	private NodeInfoService nodeInfoService;

	public NodeInfoService getNodeInfoService() {
		return nodeInfoService;
	}

	public void setNodeInfoService(NodeInfoService nodeInfoService) {
		this.nodeInfoService = nodeInfoService;
	}

	/** 获得服务器列表信息 */
	public void server(Manager mgr) {
		Message msg = mgr.getMessage();
		try {
			if (mgr.isParameter("mfk")) {
				ServerInfo info = mgr.getObject(ServerInfo.class);
				if (info.getIper() == null) {
					info.setIper(mgr.getRemoteAddress().getAddress().getHostAddress());
				}
				this.getNodeInfoService().saveInfo(info);
			} // server list
			this.getNodeInfoService().sendByAll(msg);
		} catch (SQLException e) {
			msg.addError(SYSTEM_ERROR_INFO);
		}
	}

}
