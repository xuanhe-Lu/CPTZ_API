package com.ypm.fjs.set;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Config;
import com.ypm.service.ConfigInfoService;
import com.ypm.util.AUtils;
import com.ypm.util.GMTime;

/**
 * Update by xk on 2018-07-10.
 * 
 * 系统参数配置 Action. 
 */
public class ConfigMgr extends Action {

	private static final long serialVersionUID = 4391039703236214353L;

	private ConfigInfoService configInfoService;

	public ConfigInfoService getConfigInfoService() {
		return configInfoService;
	}

	public void setConfigInfoService(ConfigInfoService configInfoService) {
		this.configInfoService = configInfoService;
	}

	/**
	 * @return string 
	 * 
	 * 单项数据信息
	 */
	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		String id = this.getString("Id");
		try {
			Config c = this.getConfigInfoService().findConfigById(id);
			if (c == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("ID", c.getId());
				json.append("REMARK", c.getRemark());
				if (c.getType() == 7) {
					json.append("CONTENT", (c.getTimeout() + GMTime.CHINA));
				} else if (c.getType() >= 8) {
					json.append("CONTENT", c.getTimeout());
				} else {
					json.append("CONTENT", c.getContent());
				}
			}
		} finally {
			id = null;
		}
		return JSON;
	}

	/**
	 * @return string
	 * 
	 * 列表数据信息
	 */
	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Sortid")) {
			sb.append("Sortid");
			sort = "Sortid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Id")) {
			if (dir == null || dir.equalsIgnoreCase("ASC")) {
				sb.append(" ASC");
			} else {
				sb.append(" DESC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("ASC")) {
			sb.append(" ASC,Id ASC");
		} else {
			sb.append(" ASC,Id DESC");
		} // 加载数据信息
		this.setAjaxInfo(this.getConfigInfoService().findConfigByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 新增系统参数配置
	 */
	public String add() {
		AjaxInfo json = this.getAjaxInfo();
		String id = this.getString("Id");
		try {
			if (id == null || id.length() <= 3) {
				json.addError(this.getText("sys.error.301"));
			} else if (this.getConfigInfoService().isConfigById(id)) {
				json.addError(this.getText("sys.error.302", new String[] { id }));
			} else {
				Config c = new Config();
				c.setId(id);
				c.setLosk(STATE_ENABLE);
				c.setType(getInt("Type"));
				c.setSindex(getString("Sindex"));
				c.setRemark(getString("Remark"));
				this.getConfigInfoService().saveConfig(c);
				json.addMessage(this.getText("data.save.succeed"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			id = null;
		}
		return JSON;
	}

	/**
	 * @return string
	 * 
	 * 更新系统参数信息
	 */
	public String update() {
		AjaxInfo json = this.getAjaxInfo();
		String id = getString("Id");
		String sid = this.getString("Sid");
		try {
			Config c = this.getConfigInfoService().findConfigById(sid);
			if (c == null) {
				json.addError(this.getText("system.error.none"));
			} else if (this.checkValid(id, 3)) {
				if (STATE_DISABLE == c.getLosk() || c.getId().equalsIgnoreCase(id)) {
					// 不允许修改
				} else if (this.getConfigInfoService().isConfigById(id)) {
					json.addError(this.getText("sys.error.302", new String[] { id }));
					return JSON;
				} else {
					c.setSid(id);
					c.setLosk(STATE_ENABLE);
				}
				c.setType(getInt("Type"));
				c.setSindex(getString("Sindex"));
				c.setRemark(getString("Remark"));
				this.getConfigInfoService().saveConfig(c);
				json.addMessage(this.getText("data.update.succeed"));
			} else {
				json.addError(this.getText("sys.error.301"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			id = sid = null;
		}
		return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 修改保存系统参数值
	 */
	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String id = this.getString( "id" );
		String str = this.getString( "Content" );
		
		try {
			Config c = this.getConfigInfoService().findConfigById(id);
			if (c == null) {
				json.addError(this.getText( "system.error.none" ));
			} else {
				switch (c.getType()) {
				case 5: // 下接选择
					c.setContent(str);
					c.setTimeout(AUtils.toInt(str));
					break;
				case 6: // 关联选择
					c.setContent(str);
					c.setTimeout(AUtils.toInt(str));
					break;
				case 7: // 日期选择
					c.setContent(str);
					c.setTimeout(GMTime.valueOf(str, GMTime.CHINA));
					break;
				default:
					c.setContent(str);
					c.setTimeout(0);
				} 
				
				// 保存数据修改
				this.getConfigInfoService().saveConfig(c);
				json.addMessage(this.getText( "data.update.succeed" ));
			}
		} catch (Exception e) {
			json.addError(this.getText( "data.update.failed" ));
		} finally {
			str = id = null;
		}
		
		return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 获取所有系统参数的id、备注说明
	 */
	public String tree() {
		this.setAjaxInfo(this.getConfigInfoService().findConfigByAll());
		return JSON;
	}

	/**
	 * @return string
	 * 
	 * 批量排序
	 */
	public String order() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			this.getConfigInfoService().orderConfig(ids);
			json.addMessage(this.getText("data.order.succeed"));
		} catch (SQLException e) {
			json.addError(this.getText("data.order.failed"));
		}
		return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 批量删除
	 */
	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (ids == null) {
				json.addError(this.getText("system.error.pars"));
			} else if (this.getConfigInfoService().removeConfig(ids)) {
				json.addMessage(this.getText("data.delete.succeed"));
			} else {
				json.addError(this.getText("system.error.config"));
			}
		} catch (IOException | SQLException e) {
			json.addError(this.getText("data.delete.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 导出数据
	 */
	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
