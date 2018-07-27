package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AdminInfo;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.UserSession;

public interface AdminInfoService {

	public void saveInfo(AdminInfo info) throws SQLException;

	public void update(long uid, int state) throws SQLException;

	public void updatePwd(long uid, String pwd) throws SQLException;

	public void delete(long uid, String ids) throws SQLException;

	public boolean isAdminByUserNo(String uno);

	public boolean isAdminByUserName(String name);

	public AjaxInfo findAdminInfo(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AdminInfo findAdminInfoByName(String name);

	public AdminInfo findAdminInfoByUid(long uid);

	public UserSession getUserSession(AdminInfo info);
	
	public void updateFailCt(long uid, int failCount) throws SQLException;

}
