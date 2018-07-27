package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.InfoVIPS;

public interface UserRoleService {

	public void save(InfoVIPS v) throws SQLException;
	// ==================== API 接口层 ====================
	public AjaxInfo findVIPByAll(StringBuilder sql, List<Object> ls, String order, int offset, int max);

	public AjaxInfo getVIPChildrens();

	public Map<Integer, String> getVIPByAll();

	public InfoVIPS getInfoByVIP(int vip);

	public InfoVIPS findInfoByVIP(int vip) throws SQLException;

	public void saveVIP(InfoVIPS v) throws SQLException;

}