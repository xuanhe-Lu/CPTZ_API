package com.ypm.coo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ypm.Action;
import com.ypm.bean.AderNote;
import com.ypm.bean.AjaxInfo;
import com.ypm.service.AderNoteService;
import com.ypm.util.GMTime;
import com.ypm.util.VeStr;

/**
 * 通知管理 Action
 * Create by xk on 2018-04-26
 */
public class AderNoteAction extends Action {

	private static final long serialVersionUID = -2869052693820517973L;
	
	// 注入AderNoteService
	private AderNoteService aderNoteService;
	
	public AderNoteService getAderNoteService() {
		return aderNoteService;
	}

	public void setAderNoteService(AderNoteService aderNoteService) {
		this.aderNoteService = aderNoteService;
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
		
		// 模板类型过滤
		int tid = this.getInt( "Tid" );
		if (tid == 1 || tid == 2) {
			sql.append( " AND Tid = ?" );
			fs.add(tid);
		} 
		
		// 显示位置过滤
		int position = this.getInt( "Position" );
		if (position != 0) {
			sql.append( " AND Position = ?" );
			fs.add(position);
		} 
		
		// 状态过滤
		int state = this.getInt( "State" );
		if (state == 1 || state == 2) {
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
		AjaxInfo ajaxInfo = this.getAderNoteService().findAderNoteByAll( sql, fs, sb.toString(), getStart(), getLimit() );
		this.setAjaxInfo(ajaxInfo);
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 新增保存通知
	 */
	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String sid = this.getString( "Sid" );
		long sday = GMTime.valueOf(this.getString( "Sday" ));
		
		try {
			AderNote aderNote = null;
			// 编辑保存
			if (this.checkValid( sid, 5 )) {
				aderNote = this.getAderNoteService().findAderNoteBySId(sid);
				if (aderNote == null) {
					json.addError(this.getText( "system.error.none" ));
				} else {
					int tid = this.getInt( "Tid" );
					int position = this.getInt( "Position" );
					String title = this.getString( "Title" );
					String detail = this.getString( "Detail" );
					int state = this.getInt( "State" );
					
					aderNote.setTid(tid);
					aderNote.setPosition(position);
					aderNote.setTitle(title);
					aderNote.setDetail(detail);
					aderNote.setSday(sday);
					aderNote.setState(state);
					aderNote.setTime(GMTime.currentTimeMillis());
				}
			} else { // 新增保存
				aderNote = new AderNote();
				aderNote.setSid(VeStr.getUSid(true));
				aderNote.setTid(this.getInt( "Tid" ));
				aderNote.setPosition(this.getInt( "Position" ));
				aderNote.setTitle(this.getString( "Title" ));
				aderNote.setAuthor(this.getUserSession().getUserName());
				aderNote.setDetail(this.getString( "Detail" ));
				aderNote.setSday(sday);
				aderNote.setState(this.getInt( "State" ));
				aderNote.setTime(GMTime.currentTimeMillis());
			}
			
			if (aderNote != null) {
				this.getAderNoteService().saveAderNote(aderNote);
				json.addMessage(this.getText( "data.save.succeed" ));
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			json.addError(this.getText( "data.save.failed" ));
		} finally {
			sid = null;
		}
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 删除通知
	 */
	public String delete () {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString( "Ids" );
		
		try {
			if (this.checkValid( ids, 5 )) {
				this.getAderNoteService().remove(ids);
				json.addMessage(this.getText( "data.delete.succeed" ));
			} else {
				json.addError(this.getText( "system.error.pars" ));
			}
		} catch (SQLException e) {
			json.addError(this.getText( "data.delete.failed" ));
		} finally {
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
			AderNote aderNote = this.getAderNoteService().findAderNoteBySId(sid);
			if (aderNote == null) {
				json.addError(this.getText( "system.error.none" ));
			} else {
				json.data();
				json.append( "SID", aderNote.getSid() );
				json.append( "TID", aderNote.getTid() );
				json.append( "POSITION", aderNote.getPosition() );
				json.append( "TITLE", aderNote.getTitle() );
				json.append( "AUTHOR", aderNote.getAuthor() );
				json.append( "DETAIL", aderNote.getDetail() );
				json.append( "SDAY", GMTime.format( aderNote.getSday(), GMTime.CHINA ) );
				json.append( "STATE", aderNote.getState() );
				json.append( "TIME", GMTime.format( aderNote.getTime(), GMTime.CHINA ) );
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
