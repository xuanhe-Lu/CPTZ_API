package com.ypiao.service.aps;

import com.ypiao.bean.Manager;
import com.ypiao.bean.Xues;
import com.ypiao.service.XuerInfoService;

/**
 * Created by xk on 2018-05-10.
 * 
 * 票友学堂信息同步APS接口.
 */
public class APiAt504 extends Abstract {

	// 注入XuerInfoService
	private XuerInfoService xuerInfoService;

	public XuerInfoService getXuerInfoService() {
		return xuerInfoService;
	}

	public void setXuerInfoService(XuerInfoService xuerInfoService) {
		this.xuerInfoService = xuerInfoService;
	}

	/**
	 * 说明：
	 * 同步保存方法, Y-MGR 项目中 XuesServiceImp 中 SyncMap.getAll().sender( SYS_A504, "save", obj ) 的 "save" 是这里的方法名
	 */
	public void save(Manager mgr) {
		try {
			Xues xues = mgr.getObject(Xues.class);
			this.getXuerInfoService().save(xues);
		} catch (Exception e) {
			// 同步保存数据失败
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
	
	/**
	 * 说明：
	 * 同步删除方法, Y-MGR 项目中 XuesServiceImp 中 SyncMap.getAll().sender( SYS_A504, "remove", obj ) 的 "remove" 是这里的方法名
	 */
	public void remove (Manager mgr) {
		String sid = mgr.getString( "sid" );
		
		try {
			this.getXuerInfoService().remove(sid);
		} catch (Exception e) {
			// 同步删除数据失败
			mgr.addError(DATA_DELETE_FAILED);
		} finally {
			sid = null;
		}
	}
		
}
