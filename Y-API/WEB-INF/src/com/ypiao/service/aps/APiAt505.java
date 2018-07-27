package com.ypiao.service.aps;

import com.ypiao.bean.Help;
import com.ypiao.bean.Manager;
import com.ypiao.service.HelpInfoService;

/**
 * Created by xk on 2018-05-10.
 * 
 * 常见问题信息同步APS接口.
 */
public class APiAt505 extends Abstract {

	// 注入HlepInfoService
	private HelpInfoService helpInfoService;

	public HelpInfoService getHelpInfoService() {
		return helpInfoService;
	}

	public void setHelpInfoService(HelpInfoService helpInfoService) {
		this.helpInfoService = helpInfoService;
	}

	/**
	 * 说明：
	 * 同步保存方法, Y-MGR 项目中 HelpServiceImp 中 SyncMap.getAll().sender( SYS_A505, "save", obj ) 的 "save" 是这里的方法名
	 */
	public void save(Manager mgr) {
		Help help = mgr.getObject(Help.class);
		
		try {
			this.getHelpInfoService().save(help);
		} catch (Exception e) {
			// 同步保存数据失败
			mgr.addError(DATA_SAVE_FAILED);
			e.printStackTrace();
			System.out.println("sync save help failed...");
		}
	}
	
	/**
	 * 说明：
	 * 同步删除方法, Y-MGR 项目中 AderNoteServiceImp 中 SyncMap.getAll().sender( SYS_A505, "remove", obj ) 的 "remove" 是这里的方法名
	 */
	public void remove (Manager mgr) {
		String sid = mgr.getString( "sid" );
		
		try {
			this.getHelpInfoService().remove(sid);
		} catch (Exception e) {
			// 同步删除数据失败
			mgr.addError(DATA_DELETE_FAILED);
			e.printStackTrace();
			System.out.println("sync delete help failed...");
		}
	}
		
}
