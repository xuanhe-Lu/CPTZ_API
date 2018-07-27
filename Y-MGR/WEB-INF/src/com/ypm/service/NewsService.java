package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.bean.News;

/**
 * 新闻管理业务层接口.
 * 
 * Created by xk on 2018-05-15.
 */
public interface NewsService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, int sid, int row) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findNewsByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);
	
	// ==================== API 接口层 ====================
	public AjaxInfo findNewsByAll(StringBuilder sql);

	public News findNewsBySId(String sid) throws SQLException;

	public void saveNews(News news, FileInfo fileInfo) throws IOException, SQLException;

	public boolean remove(String sid) throws SQLException;

}
