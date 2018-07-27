package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.dto.GtReq.Target;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Push;

/**
 * 推送管理业务层接口
 * 
 * Created by xk on 2018-06-04.
 */
public interface PushService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, long sid, int row) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo list(StringBuilder sql, List<Object> fs, String order, int offset, int max);
	
	// ==================== API 接口层 ====================
	public AjaxInfo list(StringBuilder sql);

	public Push findPushBySId(long sid) throws SQLException;

	public void savePush(Push push) throws IOException, SQLException;

	public boolean remove(long sid) throws SQLException;
	
	/**
	 * 单个用户推送消息接口
	 * 
	 * @param message is SingleMessage
	 * @param target is Target
	 * @return IPushResult
	 */
	public IPushResult pushMessageToSingle(SingleMessage message, Target target);
	
	/**
	 * 单个用户推送消息异常重试接口
	 * 
	 * @param message is SingleMessage
	 * @param target is Target
	 * @param requestId String
	 * @return IPushResult
	 */
	public IPushResult pushMessageToSingle(SingleMessage message, Target target, String requestId);

}
