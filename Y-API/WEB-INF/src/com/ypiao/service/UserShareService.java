package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;

public interface UserShareService {

	public void sendByAll(AjaxInfo json, long uid, int state) throws SQLException;
}
