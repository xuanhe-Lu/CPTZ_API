package com.ypiao.service.aps;

import com.ypiao.bean.Manager;
import com.ypiao.bean.Welfare;
import com.ypiao.service.WelfareService;

/**
 * Created by xk on 2018-06-12.
 * 
 * 福利专区信息同步APS接口.
 */
public class APiAt510 extends Abstract {

	// 注入WelfareService
	private WelfareService welfareService;
	
	public WelfareService getWelfareService() {
		return welfareService;
	}

	public void setWelfareService(WelfareService welfareService) {
		this.welfareService = welfareService;
	}

	/**
	 * 说明：
	 * 同步保存方法, Y-MGR 项目中 WelfareServiceImp 中 SyncMap.getAll().sender( SYS_A510, "save", obj ) 的 "save" 是这里的方法名
	 */
	public void save(Manager mgr) {
		try {
			Welfare welfare = mgr.getObject(Welfare.class);
			this.getWelfareService().save(welfare);
		} catch (Exception e) {
			// 同步保存数据失败
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
	
	/**
	 * 说明：
	 * 同步删除方法, Y-MGR 项目中 WelfareServiceImp 中 SyncMap.getAll().sender( SYS_A510, "remove", obj ) 的 "remove" 是这里的方法名
	 */
	public void remove (Manager mgr) {
		String sid = mgr.getString( "sid" );
		
		try {
			this.getWelfareService().remove(sid);
		} catch (Exception e) {
			// 同步删除数据失败
			mgr.addError(DATA_DELETE_FAILED);
		} finally {
			// 释放资源
			sid = null;
		}
	}
		
}
