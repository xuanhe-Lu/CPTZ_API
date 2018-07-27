package com.ypiao.service.aps;

import com.ypiao.bean.Manager;
import com.ypiao.bean.Version;
import com.ypiao.service.VersionService;

/**
 * Created by xk on 2018-06-06.
 * 
 * app版本更新信息同步APS接口.
 */
public class APiAt509 extends Abstract {

	// 注入VersionService
	private VersionService versionService;
	
	public VersionService getVersionService() {
		return versionService;
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}

	/**
	 * 说明：
	 * 同步保存方法, Y-MGR 项目中 VersionServiceImp 中 SyncMap.getAll().sender( SYS_A509, "save", obj ) 的 "save" 是这里的方法名
	 */
	public void save(Manager mgr) {
		Version version = mgr.getObject(Version.class);
		
		try {
			this.getVersionService().save(version);
		} catch (Exception e) {
			// 同步保存数据失败
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
	
	/**
	 * 说明：
	 * 同步删除方法, Y-MGR 项目中 VersionServiceImp 中 SyncMap.getAll().sender( SYS_A509, "remove", obj ) 的 "remove" 是这里的方法名
	 */
	public void remove (Manager mgr) {
		String sid = mgr.getString( "sid" );
		
		try {
			this.getVersionService().remove(sid);
		} catch (Exception e) {
			// 同步删除数据失败
			mgr.addError(DATA_DELETE_FAILED);
		}
	}
		
}
