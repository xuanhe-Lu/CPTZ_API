package com.ypm.service;

import java.sql.SQLException;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FieldMenu;

public interface SystemService {
	/** 替换扩展显示属性 */
	public String getFields(String body, String key);
	// ==================== 报表字典信息 ===================
	public void saveFieldMenu(FieldMenu menu) throws SQLException;

	public void saveFieldInfo(FieldMenu menu, String dsrc) throws SQLException;
	/** 详情表单 */
	public void findFieldInfoById(AjaxInfo json, int sid, String[] arr) throws SQLException;

	public AjaxInfo findMenuChildrens(int tid);

	public AjaxInfo findInfoChildrens(int sid);

	public FieldMenu findFieldMenuById(int sid) throws SQLException;

	public boolean isFieldMenuBySid(String sid);

	public boolean isFieldMenuByTid(int tid);

	public void moveFieldMenu(int sid, int tid, int index) throws SQLException;

	public void orderFieldMenu(String ids) throws SQLException;

	public void orderFieldInfo(int sid, String ids) throws SQLException;

	public void removeFieldMenu(FieldMenu menu) throws SQLException;

}
