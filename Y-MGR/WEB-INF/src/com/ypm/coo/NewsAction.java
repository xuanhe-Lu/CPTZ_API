package com.ypm.coo;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.bean.News;
import com.ypm.service.NewsService;
import com.ypm.util.GMTime;
import com.ypm.util.VeStr;

/**
 * 新闻管理 Action.
 * 
 * Create by xk on 2018-05-15.
 */
public class NewsAction extends Action {

	private static final long serialVersionUID = -2869052693820517973L;
	
	// 接收文件域
	private File files;
	
	public File getFiles() {
		return files;
	}

	public void setFiles(File files) {
		this.files = files;
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
	 * @author xk
	 * @return String
	 * 
	 * 数据列表
	 */
	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		
		// 标题关键词过滤
		String key = this.getString( "Key" );
		if (key != null) {
			sql.append( " AND Title LIKE ?" );
			fs.add( '%' + key + '%' );
		} 
		
		// 显示位置过滤
		int position = this.getInt( "Position" );
		if (position != 0) {
			sql.append( " AND Position = ?" );
			fs.add(position);
		} 
		
		// 状态过滤
		int state = this.getInt( "State" );
		if (state != 0) {
			sql.append( " AND State = ?" );
			fs.add(state);
		} 
		
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase( "Sid" )) {
			sb.append( "Sid" );
			sort = "Sid";
		} else {
			sb.append(sort);
		} 
		
		// 加载二次排序
		if (sort.equalsIgnoreCase( "Sid" )) {
			if (dir == null || dir.equalsIgnoreCase( "DESC" )) {
				sb.append( " DESC" );
			} else {
				sb.append( " ASC" );
			}
		} else if (dir == null || dir.equalsIgnoreCase( "DESC" )) {
			sb.append( " DESC, Sid DESC" );
		} else {
			sb.append( " ASC, Sid ASC" );
		}
		
		// 加载数据信息
		AjaxInfo ajaxInfo = this.getNewsService().findNewsByAll( sql, fs, sb.toString(), getStart(), getLimit() );
		this.setAjaxInfo(ajaxInfo);
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 新增/编辑保存
	 */
	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String sid = this.getString( "Sid" );
		FileInfo fileInfo = FileInfo.getImg( this.getFiles(), 1 );
		
		try {
			News news = null;
			// 编辑保存
			if (this.checkValid( sid, 5 )) {
				news = this.getNewsService().findNewsBySId(sid);
				if (news == null) {
					json.addError(this.getText( "system.error.none" ));
				} else if (!fileInfo.image(news.getVer() >= 1)) {
					json.addError(this.getText( "coo.error.032" ));
					news = null; // 图片不能为空
				} else {
					String title = this.getString( "Title" );
					String subject = this.getString( "Subject" );
					int position = this.getInt( "Position" );
					String detail = this.getString( "Detail" );
					int state = this.getInt( "State" );
					
					news.setTitle(title);
					news.setSubject(subject);
					news.setPosition(position);
					news.setDetail(detail);
					news.setState(state);
					news.setTime(System.currentTimeMillis());
				}
			} else if (!fileInfo.image()) {// 非图片
				json.addError(this.getText( "coo.error.023" ));
			} else if (fileInfo.getSize() > FILE_MAX_SIZE) { // 图片大小超出限制
				json.addError(this.getText( "upload.error.smax", new String[] { String.valueOf(fileInfo.getSize()), String.valueOf(FILE_MAX_SIZE) } ));
			} else { // 新增保存
				news = new News();
				news.setSid(VeStr.getUSid(true));
				news.setTitle(this.getString( "Title" ));
				news.setSubject(this.getString( "Subject" ));
				news.setPosition(this.getInt( "Position" ));
				news.setAuthor(this.getUserSession().getUserName());
				news.setDetail(this.getString( "Detail" ));
				news.setState(this.getInt( "State" ));
				news.setTime(System.currentTimeMillis());
			}
			
			if (news != null) {
				if (fileInfo.getSize() > 0) {
					fileInfo.setPid(news.getSid());
					news.setVer( news.getVer() + 1 );
				}
				fileInfo.setRule( 1080, 1920, true );
				this.getNewsService().saveNews( news, fileInfo );
				json.addMessage(this.getText( "data.save.succeed" ));
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "data.save.failed" ));
		} finally {
			// 释放资源
			fileInfo.destroy();
			sid = null;
		}
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 删除
	 */
	public String delete () {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString( "Ids" );
		
		try {
			if (this.checkValid( ids, 5 )) {
				this.getNewsService().remove(ids);
				json.addMessage(this.getText( "data.delete.succeed" ));
			} else {
				json.addError(this.getText( "system.error.pars" ));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			json.addError(this.getText( "data.delete.failed" ));
		} finally {
			// 释放对象资源
			ids = null;
		}
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 查看预览
	 */
	public String view() {
		AjaxInfo json = this.getAjaxInfo();
		String sid = this.getString( "sid" );
		
		try {
			News news = this.getNewsService().findNewsBySId(sid);
			if (news == null) {
				json.addError(this.getText( "system.error.none" ));
			} else {
				json.data();
				json.append( "SID", news.getSid() );
				json.append( "TITLE", news.getTitle() );
				json.append( "SUBJECT", news.getSubject() );
				json.append( "POSITION", news.getPosition() );
				json.append( "AUTHOR", news.getAuthor() );
				json.append( "DETAIL", news.getDetail() );
				json.append( "DIST", news.getDist() );
				json.append( "VER", news.getVer() );
				json.append( "STATE", news.getState() );
				json.append( "TIME", GMTime.format( news.getTime(), GMTime.CHINA ) );
			}
		} catch (SQLException e) {
			json.addError(this.getText( "system.error.info" ));
		} finally {
			// 释放对象资源
			sid = null;
		}
		
		return JSON;
	}
}

