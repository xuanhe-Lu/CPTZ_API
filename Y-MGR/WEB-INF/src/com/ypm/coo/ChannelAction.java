package com.ypm.coo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Channel;
import com.ypm.service.ChannelService;
import com.ypm.util.GMTime;

/**
 * 渠道统计 Action.
 * 
 * Created by xk on 2018-05-18.
 */
public class ChannelAction extends Action {

	private static final long serialVersionUID = -2869052693820517973L;

	// 注入ChannelService
	private ChannelService channelService;
	
	public ChannelService getChannelService() {
		return channelService;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
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
		// 渠道分类过滤
		int tid = this.getInt( "Tid" );
		if (tid != 0) {
			sql.append( " AND Tid = ?" );
			fs.add(tid);
		} 
		
		// 日期范围过滤
		String tmpStartTime = this.getString( "Starttime" );
		String tmpEndTime = this.getString( "Endtime" );
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		Date dateStartTime = null;
		Date dateEndTime = null;
		Long startTime = null;
		Long endTime = null;
		if (tmpStartTime != null) {
			tmpStartTime = tmpStartTime.replace( "T", " " );
			try {
				dateStartTime = sdf.parse(tmpStartTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			startTime = dateStartTime.getTime();
		}
		if (tmpEndTime != null) {
			tmpEndTime = tmpEndTime.replace( "T00:00:00", " 23:59:59" );
			try {
				dateEndTime = sdf.parse(tmpEndTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			endTime = dateEndTime.getTime();
		}
		if (dateStartTime != null) {
			sql.append( " AND Time >= ?" );
			fs.add(startTime);
		}
		if (dateEndTime != null) {
			sql.append( " AND Time <= ?" );
			fs.add(endTime);
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
		AjaxInfo ajaxInfo = null;
		try {
			ajaxInfo = this.getChannelService().findChannelByAll( sql, fs, sb.toString(), getStart(), getLimit() );
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		int sid = this.getInt( "Sid" );
		
		try {
			Channel channel = null;
			// 编辑保存
			if (this.checkValid( sid, 1 )) {
				channel = this.getChannelService().findChannelBySId(sid);
				if (channel == null) {
					json.addError(this.getText( "system.error.none" ));
				} else {
					int tid = this.getInt( "Tid" );
					String name = this.getString( "Name" );
					String raddr = this.getString( "Raddr" );
					String daddr = this.getString( "Daddr" );
					
					channel.setTid(tid);
					channel.setName(name);
					channel.setRaddr(raddr);
					channel.setDaddr(daddr);
					channel.setTime(GMTime.currentTimeMillis());
				}
			} else { // 新增保存
				channel = new Channel();
				channel.setSid(this.getInt( "Nextsid" ));
				channel.setTid(this.getInt( "Tid" ));
				channel.setName(this.getString( "Name" ));
				channel.setRaddr(this.getString( "Raddr" ));
				channel.setDaddr(this.getString( "Daddr" ));
				channel.setTime(GMTime.currentTimeMillis());
			}
			
			if (channel != null) {
				this.getChannelService().saveChannel(channel);
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
	 * 删除一条数据
	 */
	public String delete () {
		AjaxInfo json = this.getAjaxInfo();
		int ids = this.getInt( "Ids" );
		
		try {
			if (this.checkValid( ids, 1 )) {
				this.getChannelService().remove(ids);
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
		int sid = this.getInt( "Sid" );
		
		try {
			Channel channel = this.getChannelService().findChannelBySId(sid);
			if (channel == null) {
				json.addError(this.getText( "system.error.none" ));
			} else {
				json.data();
				json.append( "SID", channel.getSid() );
				json.append( "TID", channel.getTid() );
				json.append( "NAME", channel.getName() );
				json.append( "RADDR", channel.getRaddr() );
				json.append( "DADDR", channel.getDaddr() );
				json.append( "RCOUNT", channel.getRcount() );
				json.append( "RNAME", channel.getRname() );
				json.append( "BCOUNT", channel.getBcount() );
				json.append( "NCOUNT", channel.getNcount() );
				json.append( "NMONEY", channel.getNmoney() );
				json.append( "FCOUNT", channel.getFcount() );
				json.append( "SCOUNT", channel.getScount() );
				json.append( "FMONEY", channel.getFmoney() );
				json.append( "SMONEY", channel.getSmoney() );
				json.append( "TZSUM", channel.getTzsum() );
				json.append( "TXSUM", channel.getTxsum() );
				json.append( "NASTOCK", channel.getNastock() );
				json.append( "SSTOCK", channel.getSstock() );
				json.append( "TIME", GMTime.format( channel.getTime(), GMTime.CHINA ) );
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		}
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 渠道统计汇总数据
	 */
	public String sum() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			// 获取渠道总统计数据
			Map<String, Object> res = channelService.sum();
			
			json.data();
			json.append( "RCOUNT", (Long) res.get("rcount") );
			json.append( "RNAME", (Long) res.get("rname") );
			json.append( "BCOUNT", (Long) res.get("bcount") );
			json.append( "NCOUNT", (Long) res.get("ncount") );
			json.append( "NMONEY", (BigDecimal) res.get("nmoney") );
			json.append( "FCOUNT", (Long) res.get("fcount") );
			json.append( "SCOUNT", (Long) res.get("scount") );
			json.append( "FMONEY", (BigDecimal) res.get("fmoney") );
			json.append( "SMONEY", (BigDecimal) res.get("smoney") );
			json.append( "TZSUM", (BigDecimal) res.get("tzsum") );
			json.append( "TXSUM", (BigDecimal) res.get("txsum") );
			json.append( "NASTOCK", (BigDecimal) res.get("nastock") );
			json.append( "SSTOCK", (BigDecimal) res.get("sstock") );
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		}
		System.out.println(json.toString());
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String 
	 * 
	 * 新增渠道，获取渠道编号
	 */
	public String getNextSid() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			// 获取最大渠道编号+1
			int sid = channelService.getNextSid();
			sid = sid <= 0 ? (sid + 1) : sid;
			
			json.data();
			json.append( "NEXTSID", sid );
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		}
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 渠道名称下拉数据
	 */
	public String getRinfos() {
		this.setAjaxInfo(this.getChannelService().getRinfos());
		return JSON;
	}
}

