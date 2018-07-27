package com.ypm.service;

import java.sql.SQLException;
import java.util.Map;
import com.ypm.bean.AdminJobs;
import com.ypm.bean.AdminOrgs;
import com.ypm.bean.AjaxInfo;

public interface AdminRoleService {

	public void saveJob(AdminJobs job) throws SQLException;

	public boolean isAdminJobByName(String name);

	public boolean isAdminJobByCode(int code);

	public boolean isAdminJobByType(int type);

	public AjaxInfo findJobChildrens(int code);

	public AjaxInfo getJobChildrens(int code);

	public Map<String, String> getAdminJobAll();

	public AdminJobs getAdminJobById(int code) throws SQLException;

	public void moveJob(AdminJobs job, int type, int sortid) throws SQLException;

	public void orderJob(String ids, int code) throws SQLException;

	public void removeJob(AdminJobs job) throws SQLException;

	public void saveOrg(AdminOrgs org) throws SQLException;

	public boolean isAdminOrgByName(String name);

	public boolean isAdminOrgByCode(int code);

	public boolean isAdminOrgByType(int type);

	public AjaxInfo findOrgChildrens(int code);

	public AjaxInfo getOrgChildrens(int code);

	public Map<String, String> getAdminOrgAll();

	public AdminOrgs getAdminOrgById(int code) throws SQLException;

	public void moveOrg(AdminOrgs org, int type, int sortid) throws SQLException;

	public void orderOrg(String ids, int code) throws SQLException;

	public void removeOrg(AdminOrgs org) throws SQLException;

}
