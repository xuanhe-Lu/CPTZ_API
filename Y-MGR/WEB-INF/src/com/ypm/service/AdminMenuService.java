package com.ypm.service;

import java.sql.SQLException;
import com.ypm.bean.AdminMenu;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.DictInfo;

public interface AdminMenuService {

	public AjaxInfo getIcons();

	public AjaxInfo getMenuListByTid(String id);
	/** 用户菜单 */
	public AjaxInfo getMenuListByUid(String id);

	public AjaxInfo findMenuListById(String id);

	public AdminMenu findAdminMenuById(String id);

	public boolean isMenuByTid(int tid);

	public void saveMenu(AdminMenu menu) throws SQLException;

	public void moveMenu(String id, int tid, int index) throws SQLException;

	public void orderMenu(int tid, String ids) throws SQLException;

	public void removeMenu(int id) throws SQLException;
	// ==================== 管理菜单模板 ====================
	public DictInfo getTemplateById(String id);

	public boolean isTemplateByName(String name);

	public void saveTemplate(DictInfo info) throws SQLException;

	public void removeTemplate(String id) throws SQLException;

}
