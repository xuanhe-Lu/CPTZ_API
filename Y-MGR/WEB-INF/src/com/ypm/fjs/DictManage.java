package com.ypm.fjs;

import java.sql.SQLException;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.DictInfo;
import com.ypm.bean.DictMenu;
import com.ypm.service.DictInfoService;

public class DictManage extends Action {

	private static final long serialVersionUID = 7643788949973499506L;

	private DictInfoService dictInfoService;

	public DictInfoService getDictInfoService() {
		return dictInfoService;
	}

	public void setDictInfoService(DictInfoService dictInfoService) {
		this.dictInfoService = dictInfoService;
	}

	private int getDefine() {
		if (getInt("define") == 1) {
			return DICT_INFO;
		} else {
			return DICT_USER;
		}
	}

	public String getChildrens() {
		try {
			int type = getDefine();
			int sid = this.getInt("sid");
			this.setAjaxInfo(this.getDictInfoService().findDictChildrens(type, sid));
		} catch (SQLException e) {
			this.getAjaxInfo().addError(this.getText("system.error.pars"));
		}
		return JSON;
	}

	public String getDictMenu() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int sid = this.getInt("sid");
			DictMenu menu = this.getDictInfoService().findDictMenuBySid(sid);
			if (menu == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("DICTSNO", menu.getSNo());
				json.append("DICTNAME", menu.getName());
				json.append("OLDSNO", menu.getSNo());
				json.append("OLDNAME", menu.getName());
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String getDictInfo() {
		int type = getDefine();
		int sid = this.getInt("sid");
		this.setAjaxInfo(this.getDictInfoService().findDictInfsBySid(type, sid));
		return JSON;
	}

	public String getDictOrder() {
		int type = getDefine();
		int sid = this.getInt("sid");
		this.setAjaxInfo(this.getDictInfoService().findDictOrderBySid(type, sid));
		return JSON;
	}

	public String add() {
		AjaxInfo json = this.getAjaxInfo();
		String sno = this.getString("DICTSNO");
		String name = this.getString("DICTNAME");
		try {
			if (sno == null) {
				json.addError(this.getText("dict.error.001"));
				return JSON;
			} else if (name == null) {
				json.addError(this.getText("dict.error.002"));
				return JSON;
			} // 检测是否存在
			if (this.getDictInfoService().isDictMenuBySid(sno)) {
				json.addError(this.getText("dict.error.003"));
				return JSON;
			} // set DictMenu
			DictMenu menu = new DictMenu();
			DictMenu dm = this.getDictInfoService().findDictMenuBySid(getInt("tid"));
			if (dm == null) {
				menu.setTid(0);
			} else {
				menu.setTid(dm.getSid());
			} // define Type
			menu.setType(getDefine());
			menu.setSNo(sno);
			menu.setName(name);
			this.getDictInfoService().saveDictMenu(menu);
			json.addMessage(this.getText("data.save.succeed"));
		} catch (SQLException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			sno = name = null;
		}
		return JSON;
	}

	public String update() {
		AjaxInfo json = this.getAjaxInfo();
		String sno = this.getString("DICTSNO");
		String name = this.getString("DICTNAME");
		try {
			if (sno == null) {
				json.addError(this.getText("dict.error.001"));
				return JSON;
			} else if (name == null) {
				json.addError(this.getText("dict.error.002"));
				return JSON;
			} // 检测是否存在
			int sid = this.getInt("sid");
			DictMenu menu = this.getDictInfoService().findDictMenuBySid(sid);
			if (menu == null) {
				json.addError(this.getText("dict.error.004"));
			} else if (!menu.getSNo().equalsIgnoreCase(sno) && this.getDictInfoService().isDictMenuBySid(sno)) {
				json.addError(this.getText("dict.error.003"));
			} else {
				menu.setSNo(sno);
				menu.setName(name);
				this.getDictInfoService().saveDictMenu(menu);
				json.addMessage(this.getText("data.update.succeed"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			sno = name = null;
		}
		return JSON;
	}
	/** 保存条词内容 */
	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String refName = this.getString("REFID");
		String refValue = this.getString("REFVALUE");
		try {
			int sid = this.getInt("sid");
			DictMenu menu = this.getDictInfoService().findDictMenuBySid(sid);
			if (menu == null || menu.getType() <= 0) {
				json.addError(this.getText("dict.error.005"));
				return JSON;
			} else if (refName == null) {
				json.addError(this.getText("dict.error.007"));
				return JSON;
			} else if (refValue == null) {
				json.addError(this.getText("dict.error.008"));
				return JSON;
			}
			DictInfo info = null;
			int id = this.getInt("DICTID");
			if (id <= 0) {
				if (this.getDictInfoService().isDictInfoByName(menu.getType(), sid, refName)) {
					json.addError(this.getText("dict.error.009"));
					return JSON;
				}
				info = new DictInfo();
			} else {
				info = this.getDictInfoService().findDictInfoBySid(menu.getType(), id);
				if (info == null) {
					json.addError(this.getText("dict.error.004"));
					return JSON;
				} else if (!info.getName().equalsIgnoreCase(refName) && this.getDictInfoService().isDictInfoByName(menu.getType(), sid, refName)) {
					json.addError(this.getText("dict.error.009"));
					return JSON;
				}
			}
			info.setSid(menu.getSid());
			info.setSNo(menu.getSNo());
			info.setType(menu.getType());
			info.setName(refName);
			info.setValue(refValue);
			info.setRemark(getString("DEFCHECK"));
			this.getDictInfoService().saveDictInfo(info);
			json.addMessage(this.getText("data.save.succeed"));
		} catch (SQLException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			refName = refValue = null;
		}
		return JSON;
	}

	public String order() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			int sid = this.getInt("sid");
			if (sid <= 0) {
				this.getDictInfoService().orderDictMenu(ids);
			} else {
				this.getDictInfoService().orderDictInfo(sid, ids);
			}
			json.addMessage(this.getText("data.order.succeed"));
		} catch (SQLException e) {
			json.addError(this.getText("data.order.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}
	/** 删除目录 */
	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			int sid = this.getInt("sid");
			DictMenu menu = this.getDictInfoService().findDictMenuBySid(sid);
			if (menu == null) {
				json.addError(this.getText("dict.error.005"));
			} else if (ids == null) {
				if (menu.getLosk() == 1) {
					json.addError(this.getText("dict.error.010"));
				} else if (this.getDictInfoService().isDictMenuByTid(menu.getSid())) {
					json.addError(this.getText("dict.error.006"));
				} else {
					this.getDictInfoService().removeDictMenu(menu);
					json.addMessage(this.getText("data.delete.succeed"));
				}
			} else {
				this.getDictInfoService().removeDictInfo(menu.getType(), ids);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.delete.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

}
