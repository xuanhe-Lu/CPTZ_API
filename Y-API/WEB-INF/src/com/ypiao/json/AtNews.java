package com.ypiao.json;

import javax.servlet.http.HttpServletResponse;

import com.sunsw.struts.ServletActionContext;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.NewsService;

/**
 * Created by xk on 2018-05-15.
 * 
 * 新闻管理数据接口. 
 */
public class AtNews extends Action {

	private static final long serialVersionUID = 8598185592101882657L;
	
	// Constructor
	public AtNews() {
		super(true);
	}
	
	// 注入 NewsService
	private NewsService newsService;
	
	public NewsService getNewsService() {
		return newsService;
	}

	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}

	/**
	 * @return String
	 * 
	 * 新闻数据列表
	 */
	public String index() {
		// 解决接口调用跨域问题
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader( "Access-Control-Allow-Origin", "*" );
		
		AjaxInfo json = this.getAjaxInfo();
		try {
			this.getNewsService().sendByAll(json);
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}
		
		logger.info("json:"+json.toString());
 return JSON;
	}
	
}
