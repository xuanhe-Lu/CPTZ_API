package com.ypiao.service.aps;

import com.ypiao.bean.Manager;
import com.ypiao.bean.News;
import com.ypiao.service.NewsService;

/**
 * Created by xk on 2018-05-15.
 * 
 * 新闻信息同步APS接口.
 */
public class APiAt506 extends Abstract {

	// 注入NewsService
	private NewsService newsService;
	
	public NewsService getNewsService() {
		return newsService;
	}

	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}

	/**
	 * 说明：
	 * 同步保存方法, Y-MGR 项目中 NewsServiceImp 中 SyncMap.getAll().sender( SYS_A506, "save", obj ) 的 "save" 是这里的方法名
	 */
	public void save(Manager mgr) {
		News news = mgr.getObject(News.class);
		
		try {
			this.getNewsService().save(news);
		} catch (Exception e) {
			// 同步保存数据失败
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
	
	/**
	 * 说明：
	 * 同步删除方法, Y-MGR 项目中 NewsServiceImp 中 SyncMap.getAll().sender( SYS_A506, "remove", obj ) 的 "remove" 是这里的方法名
	 */
	public void remove (Manager mgr) {
		String sid = mgr.getString( "sid" );
		
		try {
			this.getNewsService().remove(sid);
		} catch (Exception e) {
			// 同步删除数据失败
			mgr.addError(DATA_DELETE_FAILED);
		} finally {
			sid = null;
		}
	}		
}
