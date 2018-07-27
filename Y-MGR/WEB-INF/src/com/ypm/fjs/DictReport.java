package com.ypm.fjs;

import java.sql.SQLException;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FieldMenu;
import com.ypm.service.SystemService;

public class DictReport extends Action {

	private static final long serialVersionUID = -8486348920708506245L;

	private SystemService systemService;

	public SystemService getSystemService() {
		return systemService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public String getChildrens() {
		int sid = this.getInt("Sid");
		if (sid <= 0) {
			int tid = this.getInt("Tid");
			this.setAjaxInfo(this.getSystemService().findMenuChildrens(tid));
		} else {
			this.setAjaxInfo(this.getSystemService().findInfoChildrens(sid));
		}
		return JSON;
	}
	/** 报表菜单 */
	public String getFieldMenu() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int sid = this.getInt("Sid");
			FieldMenu menu = this.getSystemService().findFieldMenuById(sid);
			if (menu == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("SID", menu.getSid());
				json.append("SNO", menu.getSno());
				json.append("NAME", menu.getName());
				json.append("TITLE", menu.getTitle());
				json.append("FNAME", menu.getValue());
			}
		} catch (Exception e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}
	/** 报表详情 */
	public String getFieldInfo() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int sid = this.getInt("Sid");
			String[] arr = this.getText("dict.list.align").split(",");
			this.getSystemService().findFieldInfoById(json, sid, arr);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String add() {
		AjaxInfo json = this.getAjaxInfo();
		String sno = this.getString("SNO");
		String name = this.getString("NAME");
		try {
			if (sno == null) {
				json.addError(this.getText("dict.error.001"));
				return JSON;
			} else if (name == null) {
				json.addError(this.getText("dict.error.002"));
				return JSON;
			} // 检测是否存在
			if (this.getSystemService().isFieldMenuBySid(sno)) {
				json.addError(this.getText("dict.error.003"));
				return JSON;
			} // set DictMenu
			FieldMenu menu = new FieldMenu();
			FieldMenu fm = this.getSystemService().findFieldMenuById(getInt("Tid"));
			if (fm == null) {
				menu.setTid(0);
			} else {
				menu.setTid(fm.getSid());
			} // Add Info
			menu.setSno(sno);
			menu.setName(name);
			this.getSystemService().saveFieldMenu(menu);
			json.addMessage(this.getText("data.save.succeed"));
		} catch (Exception e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			sno = name = null;
		}
		return JSON;
	}

	public String update() {
		AjaxInfo json = this.getAjaxInfo();
		String sno = this.getString("SNO");
		String name = this.getString("NAME");
		try {
			if (sno == null) {
				json.addError(this.getText("dict.error.001"));
				return JSON;
			} else if (name == null) {
				json.addError(this.getText("dict.error.002"));
				return JSON;
			} // 检测是否存在
			FieldMenu menu = this.getSystemService().findFieldMenuById(getInt("Sid"));
			if (menu == null) {
				json.addError(this.getText("dict.error.004"));
			} else if (!menu.getSno().equalsIgnoreCase(sno) && this.getSystemService().isFieldMenuBySid(sno)) {
				json.addError(this.getText("dict.error.003"));
			} else {
				menu.setName(name);
				if (menu.isLosk()) {
					// 锁定信息，禁止修改
				} else {
					menu.setSno(sno);
				}
				this.getSystemService().saveFieldMenu(menu);
				json.addMessage(this.getText("data.update.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			sno = name = null;
		}
		return JSON;
	}
	/** 保存列表设置 */
	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int sid = this.getInt("Sid");
			FieldMenu menu = this.getSystemService().findFieldMenuById(sid);
			if (menu == null) {
				json.addError(this.getText("dict.error.004"));
			} else {
				menu.setName(getString("NAME"));
				menu.setTitle(getString("TITLE"));
				menu.setValue(getString("FNAME"));
				this.getSystemService().saveFieldInfo(menu, getString("dbSRC"));
				json.addMessage(this.getText("data.update.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.update.failed"));
		}
		return JSON;
	}

	public String move() {
		int sid = this.getInt("Sid");
		int tid = this.getInt("Tid");
		try {
			int index = this.getInt("Index");
			if (index <= 0) {
				index = 1;
			} else {
				index += 1;
			} // 更新移动后位置
			this.getSystemService().moveFieldMenu(sid, tid, index);
			this.getAjaxInfo().addMessage(this.getText("data.move.succeed"));
		} catch (Exception e) {
			this.getAjaxInfo().addError(this.getText("data.move.failed"));
		}
		return JSON;
	}

	public String order() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			int sid = this.getInt("Sid");
			if (sid <= 0) {
				this.getSystemService().orderFieldMenu(ids);
			} else {
				this.getSystemService().orderFieldInfo(sid, ids);
			}
			json.addMessage(this.getText("data.order.succeed"));
		} catch (SQLException e) {
			json.addError(this.getText("data.order.failed"));
		}
		return JSON;
	}
	/** 删除目录 */
	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int sid = this.getInt("Sid");
			FieldMenu menu = this.getSystemService().findFieldMenuById(sid);
			if (menu == null) {
				json.addError(this.getText("dict.error.005"));
			} else if (menu.isLosk()) {
				json.addError(this.getText("dict.error.010"));
			} else if (this.getSystemService().isFieldMenuByTid(menu.getSid())) {
				json.addError(this.getText("dict.error.006"));
			} else {
				this.getSystemService().removeFieldMenu(menu);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.delete.failed"));
		}
		return JSON;
	}

}
