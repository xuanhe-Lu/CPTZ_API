package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;

public interface UserReturnService {

	// ==================== API 接口层 ====================
	public AjaxInfo sendByAll(AjaxInfo json, long uid) throws SQLException;

	public AjaxInfo sendByUid(AjaxInfo json, long uid) throws SQLException;
}
