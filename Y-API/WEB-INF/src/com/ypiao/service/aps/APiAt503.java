package com.ypiao.service.aps;

import com.ypiao.bean.AderNote;
import com.ypiao.bean.Manager;
import com.ypiao.service.AderNoteService;
import org.apache.log4j.Logger;

/**
 * Creat by xk on 2018-05-10.
 * 
 * 通知管理信息同步APS接口.
 */
public class APiAt503 extends Abstract {

	// 注入AderNoteService
	private AderNoteService aderNoteService;

	public AderNoteService getAderNoteService() {
		return aderNoteService;
	}

	private static Logger logger = Logger.getLogger(APiAt503.class);

	public void setAderNoteService(AderNoteService aderNoteService) {
		this.aderNoteService = aderNoteService;
	}

	/**
	 * 说明：
	 * 同步保存方法, Y-MGR 项目中 AderNoteServiceImp 中 SyncMap.getAll().sender( SYS_A503, "save", obj ) 的 "save" 是这里的方法名
	 */
	public void save (Manager mgr) {
		try {
			AderNote aderNote = mgr.getObject(AderNote.class);
			this.getAderNoteService().save(aderNote);
		} catch (Exception e) {
			logger.info("async failed...");
			// 同步保存数据失败
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
	
	/**
	 * 说明：
	 * 同步删除方法, Y-MGR 项目中 AderNoteServiceImp 中 SyncMap.getAll().sender( SYS_A503, "remove", obj ) 的 "remove" 是这里的方法名
	 */
	public void remove (Manager mgr) {
		String sid = mgr.getString( "sid" );
		
		try {
			this.getAderNoteService().remove(sid);
		} catch (Exception e) {
			// 同步删除数据失败
			mgr.addError(DATA_DELETE_FAILED);
		} finally {
			sid = null;
		}
	}
	
}
