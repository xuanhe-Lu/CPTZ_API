package com.ypm.coo;

import java.util.ArrayList;
import java.util.List;

import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.service.ChannelSumService;

/**
 * 渠道汇总 Action.
 * 
 * Created by xk on 2018-06-08.
 */
public class ChannelSumAction extends Action {

	private static final long serialVersionUID = -2869052693820517973L;

	// 注入ChannelSumService
	private ChannelSumService channelSumService;
	
	public ChannelSumService getChannelSumService() {
		return channelSumService;
	}

	public void setChannelSumService(ChannelSumService channelSumService) {
		this.channelSumService = channelSumService;
	}

	/**
	 * @author xk
	 * @return String
	 * 
	 * 渠道汇总数据
	 */
	public String list() {
		// 加载数据信息
		AjaxInfo json = null;
		
		try {
			json = this.getChannelSumService().list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setAjaxInfo(json);
		
		return JSON;
	}
}

