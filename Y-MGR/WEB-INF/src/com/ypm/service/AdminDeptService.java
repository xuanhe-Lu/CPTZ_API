package com.ypm.service;

import java.sql.SQLException;
import java.util.Map;
import com.ypm.bean.AdminDept;
import com.ypm.bean.AjaxInfo;

public interface AdminDeptService {

	public AdminDept findDeptById(String id) throws SQLException;

	public AjaxInfo findDeptByAll(int start, int max);

	public AjaxInfo getDetpChildrens();

	public Map<String, String> getAdminDetpAll();

	public boolean isDeptByName(String name);

	public void saveDept(AdminDept dept) throws SQLException;

	public void removeDept(String ids) throws SQLException;

}
