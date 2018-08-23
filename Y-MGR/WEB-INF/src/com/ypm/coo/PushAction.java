package com.ypm.coo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Push;
import com.ypm.service.PushService;
import com.ypm.util.GMTime;

/**
 * 推送管理 Action
 * 
 * Created by xk on 2018-06-04.
 */
public class PushAction extends Action {

	private static final long serialVersionUID = 1L;
	
	// 注入 PushService
	private PushService pushService;
	
	public PushService getPushService() {
		return pushService;
	}

	public void setPushService(PushService pushService) {
		this.pushService = pushService;
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
		
		// 推送标题关键词过滤
		String key = this.getString( "Key" );
		if (key != null) {
			sql.append( " AND Title LIKE ?" );
			fs.add( '%' + key + '%' );
		} 
		
		// 推送系统过滤
		int system = this.getInt( "System" );
		if (system != 0) {
			sql.append( " AND System = ?" );
			fs.add(system);
		} 
		
		// 状态过滤
		int state = this.getInt( "State" );
		if (state == 0 || state == 1) {
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
		AjaxInfo ajaxInfo = this.getPushService().list( sql, fs, sb.toString(), getStart(), getLimit() );
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
		long sid = this.getLong( "Sid" );
		
		try {
			Push push = null;
			// 编辑保存
			if (sid > 0) {
				push = this.getPushService().findPushBySId(sid);
				if (push == null) {
					json.addError(this.getText( "system.error.none" ));
				} else {
					String title = this.getString( "Title" );
					String content = this.getString( "Content" );
					int system = this.getInt( "System" );
					int type = this.getInt( "Type" );
					long timer = this.getLong( "Timer" );
					int target = this.getInt( "Target" );
					long uid = this.getLong( "Uid" );
					int state = this.getInt( "State" );
					
					push.setTitle(title);
					push.setContent(content);;
					push.setSystem(system);
					push.setType(type);
					push.setTimer(timer);
					push.setTarget(target);
					push.setUid(uid);
					push.setState(state);
					push.setTime(System.currentTimeMillis());
				}
			} else { // 新增保存
				push = new Push();
				push.setTitle(this.getString( "Title" ));
				push.setContent(this.getString( "Content" ));;
				push.setSystem(this.getInt( "System" ));
				push.setType(this.getInt( "Type" ));
				push.setTimer(this.getLong( "Timer" ));
				push.setTarget(this.getInt( "Target" ));
				push.setUid(this.getLong( "Uid" ));
				//push.setState(this.getInt( "State" ));
				push.setTime(System.currentTimeMillis());
			}
			
			if (push != null) {
				this.getPushService().savePush(push);
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
		long ids = this.getLong( "Ids" );
		
		try {
			if (ids > 0) {
				this.getPushService().remove(ids);
				json.addMessage(this.getText( "data.delete.succeed" ));
			} else {
				json.addError(this.getText( "system.error.pars" ));
			}
		} catch (SQLException e) {
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
		long sid = this.getLong( "sid" );
		
		try {
			Push push = this.getPushService().findPushBySId(sid);
			if (push == null) {
				json.addError(this.getText( "system.error.none" ));
			} else {
				json.data();
				json.append( "SID", push.getSid() );
				json.append( "TITLE", push.getTitle() );
				json.append( "CONTENT", push.getContent() );
				json.append( "SYSTEM", push.getSystem() );
				json.append( "TYPE", push.getType() );
				json.append( "TIMER", GMTime.format( push.getTimer(), GMTime.CHINA ) );
				json.append( "TARGET", push.getTarget() );
				json.append( "UID", push.getUid() );
				json.append( "STATE", push.getState() );
				json.append( "TIME", GMTime.format( push.getTime(), GMTime.CHINA ) );
			}
		} catch (SQLException e) {
			json.addError(this.getText( "system.error.info" ));
		}
		
		return JSON;
	}
}

