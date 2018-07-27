package com.ypiao.service.aps;

import com.ypiao.bean.Channel;
import com.ypiao.bean.Manager;
import com.ypiao.service.ChannelService;

/**
 * Created by xk on 2018-05-18.
 * 
 * 渠道统计信息同步APS接口.
 */
public class APiAt507 extends Abstract {

	// 注入ChannelService
	private ChannelService channelService;
	
	public ChannelService getChannelService() {
		return channelService;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	/**
	 * 说明：
	 * 同步保存方法, Y-MGR 项目中 ChannelServiceImp 中 SyncMap.getAll().sender( SYS_A507, "save", obj ) 的 "save" 是这里的方法名
	 */
	public void save(Manager mgr) {
		Channel channel = mgr.getObject(Channel.class);
		
		try {
			this.getChannelService().save(channel);
		} catch (Exception e) {
			// 同步保存数据失败
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
	
	/**
	 * 说明：
	 * 同步删除方法, Y-MGR 项目中 ChannelServiceImp 中 SyncMap.getAll().sender( SYS_A507, "remove", obj ) 的 "remove" 是这里的方法名
	 */
	public void remove (Manager mgr) {
		int sid = mgr.getInt( "sid" );
		
		try {
			this.getChannelService().remove(sid);
		} catch (Exception e) {
			// 同步删除数据失败
			mgr.addError(DATA_DELETE_FAILED);
		}
	}
		
}
