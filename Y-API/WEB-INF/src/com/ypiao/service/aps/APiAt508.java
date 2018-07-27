package com.ypiao.service.aps;

import com.ypiao.bean.Manager;
import com.ypiao.bean.Push;
import com.ypiao.service.PushService;

/**
 * Created by xk on 2018-06-06.
 * 
 * 推送管理信息同步APS接口.
 */
public class APiAt508 extends Abstract {

	// 注入PushService
	private PushService pushService;
	
	public PushService getPushService() {
		return pushService;
	}

	public void setPushService(PushService pushService) {
		this.pushService = pushService;
	}

	/**
	 * 说明：
	 * 同步保存方法, Y-MGR 项目中 PushServiceImp 中 SyncMap.getAll().sender( SYS_A508, "save", obj ) 的 "save" 是这里的方法名
	 */
	public void save(Manager mgr) {
		Push push = mgr.getObject(Push.class);
		
		try {
			this.getPushService().save(push);
		} catch (Exception e) {
			// 同步保存数据失败
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
	
	/**
	 * 说明：
	 * 同步删除方法, Y-MGR 项目中 PushServiceImp 中 SyncMap.getAll().sender( SYS_A508, "remove", obj ) 的 "remove" 是这里的方法名
	 */
	public void remove (Manager mgr) {
		long sid = mgr.getLong( "sid" );
		
		try {
			this.getPushService().remove(sid);
		} catch (Exception e) {
			// 同步删除数据失败
			mgr.addError(DATA_DELETE_FAILED);
		}
	}
		
}
