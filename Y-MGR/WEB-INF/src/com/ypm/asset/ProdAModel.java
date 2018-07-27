package com.ypm.asset;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.ProdModel;
import com.ypm.service.ProdModelService;
import com.ypm.util.GMTime;

public class ProdAModel extends Action {

	private static final long serialVersionUID = 7469889835867332829L;

	private ProdModelService prodModelService;

	public ProdModelService getProdModelService() {
		return prodModelService;
	}

	public void setProdModelService(ProdModelService prodModelService) {
		this.prodModelService = prodModelService;
	}

	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int tid = this.getInt("Tid");
			ProdModel m = this.getProdModelService().findModelByTid(tid);
			if (m == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("TID", m.getTid());
				json.append("NAME", m.getName());
				json.append("TOTAL", m.getTotal());
				json.append("TOALL", m.getToall());
				json.append("TOFEE", m.getTofee());
				json.append("MA", m.getMa());
				json.append("MB", m.getMb());
				json.append("MC", m.getMc());
				json.append("REMARK", m.getRemark());
				json.append("STATE", m.getState());
				json.append("TIME", (m.getTime() + GMTime.CHINA));
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		int state = this.getInt("State", -1);
		if (state <= 1 && state >= 0) {
			sql.append(" AND State=?");
			fs.add(state); // 类型
		} // 检测关键字
		String key = this.getString("KEY");
		if (key == null) {
			// Ignored
		} else {
			sql.append(" AND Name LIKE ?");
			fs.add(sb.append('%').append(key).append('%').toString());
		}
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Tid")) {
			sb.append("Tid");
			sort = "Tid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Tid")) {
			if (dir == null || dir.equalsIgnoreCase("ASC")) {
				sb.append(" ASC");
			} else {
				sb.append(" DESC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("ASC")) {
			sb.append(" ASC,Tid ASC");
		} else {
			sb.append(" DESC,Tid DESC");
		} // 加载数据信息
		this.setAjaxInfo(this.getProdModelService().findModelByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String name = this.getString("Name");
		try {
			ProdModel m = null;
			int tid = this.getInt("Tid");
			if (name == null) {
				json.addError(this.getText("asset.error.101"));
			} else if (tid >= 1) {
				m = this.getProdModelService().findModelByTid(tid);
				if (m == null) {
					json.addError(this.getText("system.error.none"));
				} else if (name.equalsIgnoreCase(m.getName())) {
					// Ignored
				} else if (this.getProdModelService().isModelByName(name)) {
					json.addError(this.getText("asset.error.102", new String[] {name}));
					m = null;
				}
			} else if (this.getProdModelService().isModelByName(name)) {
				json.addError(this.getText("asset.error.102", new String[] {name}));
			} else {
				m = new ProdModel();
			}
			if (m != null) {
				m.setName(name);
				m.setTotal(getInt("Total"));
				m.setToall(getInt("Toall"));
				m.setTofee(getInt("Tofee"));
				m.setMa(getBigDecimal("MA"));
				m.setMb(getBigDecimal("MB"));
				m.setMc(getBigDecimal("MC"));
				m.setRemark(getString("Remark"));
				m.setState(getInt("State"));
				if (m.getState() != STATE_DISABLE) {
					m.setState(STATE_ENABLE);
				} // 保存数据信息
				this.getProdModelService().saveModel(m);
				json.addMessage(this.getText("data.save.succeed"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			name = null;
		}
		return JSON;
	}

	public String state() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 1)) {
				int state = this.getInt("State");
				if (state != STATE_DISABLE) {
					state = STATE_ENABLE;
				}
				this.getProdModelService().saveState(ids, state);
				json.addMessage(this.getText("data.update.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	public String tree() {
		this.setAjaxInfo(this.getProdModelService().findModelByAll(STATE_ENABLE));
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
