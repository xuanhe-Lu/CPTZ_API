package com.ypm.coo;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.bean.Welfare;
import com.ypm.service.WelfareService;
import com.ypm.util.GMTime;
import com.ypm.util.VeStr;

/**
 * 福利专区 Action.
 * 
 * Created by xk on 2018-06-12.
 */
public class WelfareAction extends Action {

	private static final long serialVersionUID = -2869052693820517973L;
	
	// 接收图片文件域
	private File files;
	
	public File getFiles() {
		return files;
	}

	public void setFiles(File files) {
		this.files = files;
	}

	// 注入 WelfareService
	private WelfareService welfareService;
	
	public WelfareService getWelfareService() {
		return welfareService;
	}

	public void setWelfareService(WelfareService welfareService) {
		this.welfareService = welfareService;
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
		
		// 类型过滤
		int type = this.getInt( "Type" );
		if (type != 0) {
			sql.append( " AND Type = ?" );
			fs.add(type);
		} 
		
		// 状态过滤
		int state = this.getInt( "State", -1 );
		if (state >= 0) {
			sql.append( " AND State = ?" );
			fs.add(state);
		} 
		
		// 标题关键词过滤
		String key = this.getString( "Key" );
		if (key != null) {
			sql.append( " AND Title LIKE ?" );
			fs.add( '%' + key + '%' );
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
		AjaxInfo ajaxInfo = this.getWelfareService().list( sql, fs, sb.toString(), getStart(), getLimit() );
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
			Welfare welfare = null;
			// 编辑保存
			if (this.checkValid( sid, 5 )) {
				welfare = this.getWelfareService().bySid(sid);
				if (welfare == null) {
					json.addError(this.getText( "system.error.none" ));
				} else if (!fileInfo.image(welfare.getVer() >= 1)) {
					json.addError(this.getText( "coo.error.023" ));
					welfare = null; // 图片不能为空
				} else {
					int type = this.getInt( "Type" );
					int state = this.getInt( "State" );
					String title = this.getString( "Title" );
					String url = this.getString( "Url" );
					
					welfare.setType(type);
					welfare.setState(state);
					welfare.setTitle(title);
					welfare.setUrl(url);
					welfare.setTime(GMTime.currentTimeMillis());
				}
			} else if (!fileInfo.image()) {// 非图片
				json.addError(this.getText( "coo.error.023" ));
			} else if (fileInfo.getSize() > FILE_MAX_SIZE) { // 图片大小超出限制
				json.addError(this.getText( "upload.error.smax", new String[] { String.valueOf(fileInfo.getSize()), String.valueOf(FILE_MAX_SIZE) } ));
			} else { // 新增保存
				welfare = new Welfare();
				welfare.setSid(VeStr.getUSid(true));
				welfare.setType(this.getInt( "Type" ));
				welfare.setState(this.getInt( "State" ));
				welfare.setTitle(this.getString( "Title" ));
				welfare.setUrl(this.getString( "Url" ));
				welfare.setTime(GMTime.currentTimeMillis());
			}
			
			if (welfare != null) {
				if (fileInfo.getSize() > 0) {
					fileInfo.setPid(welfare.getSid());
					welfare.setVer( welfare.getVer() + 1 );
				}
				fileInfo.setRule( 1080, 1920, true );
				this.getWelfareService().saveWelfare( welfare, fileInfo );
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
				this.getWelfareService().remove(ids);
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
			Welfare welfare = this.getWelfareService().bySid(sid);
			
			if (welfare == null) {
				json.addError(this.getText( "system.error.none" ));
			} else {
				json.data();
				json.append( "SID", welfare.getSid() );
				json.append( "TYPE", welfare.getType() );
				json.append( "STATE", welfare.getState() );
				json.append( "TITLE", welfare.getTitle() );
				json.append( "URL", welfare.getUrl() );
				json.append( "DIST", welfare.getDist() );
				json.append( "VER", welfare.getVer() );
				json.append( "TIME", GMTime.format( welfare.getTime(), GMTime.CHINA ) );
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

