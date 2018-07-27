package com.ypm.coo;

import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Help;
import com.ypm.service.HelpService;
import com.ypm.util.GMTime;
import com.ypm.util.VeStr;

/**
 * 常见问题 Action.
 * 
 * Created by xk on 2018-05-09.
 */
public class HelpAction extends Action {

	private static final long serialVersionUID = 1L;
	
	// 注入 HelpService
	private HelpService helpService;
	
	public HelpService getHelpService() {
		return helpService;
	}

	public void setHelpService(HelpService helpService) {
		this.helpService = helpService;
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
		
		// 问题分类过滤
		int tid = this.getInt( "Tid" );
		if (tid != 0) {
			sql.append( " AND Tid = ?" );
			fs.add(tid);
		} 
		
		// 问题关键词过滤
		String key = this.getString( "Key" );
		if (key != null) {
			sql.append( " AND Question LIKE ?" );
			fs.add( '%' + key + '%' );
		} 
		
		// 状态过滤
		/*int state = this.getInt( "State" );
		if (state == 0 || state == 1) {
			sql.append( " AND State = ?" );
			fs.add(state);
		} */
		
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
		AjaxInfo ajaxInfo = this.getHelpService().findHelpByAll( sql, fs, sb.toString(), getStart(), getLimit() );
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
		
		try {
			Help help = null;
			// 编辑保存
			if (this.checkValid( sid, 5 )) {
				help = this.getHelpService().findHelpBySId(sid);
				if (help == null) {
					json.addError(this.getText( "system.error.none" ));
				} else {
					int tid = this.getInt( "Tid" );
					String question = this.getString( "Question" );
					String answer = this.getString( "Answer" );
					int sortid = this.getInt( "Sortid" );
					int state = this.getInt( "State" );
					
					help.setTid(tid);
					help.setQuestion(question);;
					help.setAnswer(answer);
					help.setSortid(sortid);
					help.setState(state);
					help.setTime(GMTime.currentTimeMillis());
				}
			} else { // 新增保存
				help = new Help();
				help.setSid(VeStr.getUSid(true));
				help.setTid(this.getInt( "Tid" ));
				help.setQuestion(this.getString( "Question" ));
				help.setAnswer(this.getString( "Answer" ));
				help.setSortid(this.getInt( "Sortid" ));
				help.setState(this.getInt( "State" ));
				help.setTime(GMTime.currentTimeMillis());
			}
			
			if (help != null) {
				this.getHelpService().saveHelp(help);
				json.addMessage(this.getText( "data.save.succeed" ));
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "data.save.failed" ));
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
				this.getHelpService().remove(ids);
				json.addMessage(this.getText( "data.delete.succeed" ));
			} else {
				json.addError(this.getText( "system.error.pars" ));
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "data.delete.failed" ));
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
			Help help = this.getHelpService().findHelpBySId(sid);
			if (help == null) {
				json.addError(this.getText( "system.error.none" ));
			} else {
				json.data();
				json.append( "SID", help.getSid() );
				json.append( "TID", help.getTid() );
				json.append( "QUESTION", help.getQuestion() );
				json.append( "ANSWER", help.getAnswer() );
				json.append( "SORTID", help.getSortid() );
				json.append( "STATE", help.getState() );
				json.append( "TIME", GMTime.format( help.getTime(), GMTime.CHINA ) );
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		}
		
		return JSON;
	}
}

