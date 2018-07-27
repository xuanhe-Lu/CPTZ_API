package com.ypm.service;

import java.sql.SQLException;
import java.util.Map;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.DictInfo;
import com.ypm.bean.DictMenu;

public interface DictInfoService {

	public String findDictInfoByDef(String sid, String id);

	public DictMenu findDictMenuBySid(int sid) throws SQLException;

	public boolean isDictMenuBySid(String sno);

	public boolean isDictMenuByTid(int tid);

	public boolean isDictInfoByName(String sid, String name);

	public void saveDictMenu(DictMenu menu) throws SQLException;

	public void orderDictMenu(String ids) throws SQLException;

	public void removeDictMenu(DictMenu menu) throws SQLException;
	// ==================== 字典详细信息 ===================
	public AjaxInfo findDictChildrens(int type, int sid) throws SQLException;

	public AjaxInfo findDictInfsBySid(int type, int sid);

	public AjaxInfo findDictOrderBySid(int type, int sid);
	/** 树型列表 */
	public AjaxInfo getDictInfoBySid(String sid);

	public AjaxInfo getDictInfsBySid(String sid);

	public DictInfo findDictInfoBySid(int type, long id) throws SQLException;

	public Map<String, String> getDictBySSid(String sid);

	public boolean isDictInfoByName(int type, int sid, String name);

	public void saveDictInfo(DictInfo info) throws SQLException;

	public void saveDictInfo(DictInfo info, String fix) throws SQLException;

	public void saveDictInfo(DictInfo info, String fix, int s) throws SQLException;

	public void orderDictInfo(int sid, String ids) throws SQLException;

	public void removeDictInfo(DictMenu menu, String id) throws SQLException;

	public void removeDictInfo(int type, String ids) throws SQLException;

}
