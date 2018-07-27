package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;

public interface ActInfoService {

	public AjaxInfo findInfoByUid(AjaxInfo json, long uid) throws SQLException;

	public AjaxInfo findListByUid(AjaxInfo json, long uid) throws SQLException;

}
