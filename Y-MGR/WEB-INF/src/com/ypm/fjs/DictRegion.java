package com.ypm.fjs;

import java.sql.SQLException;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.RegionInfo;
import com.ypm.service.SiteAreaService;

public class DictRegion extends Action {

	private static final long serialVersionUID = -4912748740569294377L;

	private SiteAreaService siteAreaService;

	public SiteAreaService getSiteAreaService() {
		return siteAreaService;
	}

	public void setSiteAreaService(SiteAreaService siteAreaService) {
		this.siteAreaService = siteAreaService;
	}
	/** 权限树结构 */
	public String tree() {
		String code = this.getString("Node");
		try {
			if (code == null || code.equals("rootid")) {
				code = this.getString("Code");
			}
			int tj = this.getInt("Tj");
			this.setAjaxInfo(this.getSiteAreaService().findRegionChildrens(code, tj));
		} catch (SQLException e) {
			this.getAjaxInfo().addError(this.getText("system.error.info"));
		} finally {
			code = null;
		}
		return JSON;
	}

	public String getChildrens() {
		String code = this.getString("Node");
		if (code == null || code.equals("rootid")) {
			code = this.getString("Code");
		} // 加载数据
		this.setAjaxInfo(this.getSiteAreaService().findRegionChildrens(code));
		return JSON;
	}

	public String getCityInfo() {
		AjaxInfo json = this.getAjaxInfo();
		String code = this.getString("code");
		RegionInfo info = this.getSiteAreaService().findRegionInfo(code);
		if (info == null) {
			json.addError(this.getText("system.error.none"));
		} else {
			json.data();
			json.append("SID", info.getSid());
			json.append("CODE", info.getCode());
			json.append("CNA", info.getCna());
			json.append("CNB", info.getCnb());
			json.append("ENA", info.getEna());
			json.append("ENB", info.getEnb());
			json.append("ZIPCODE", info.getZipCode());
			json.append("TELCODE", info.getTelCode());
			json.append("TELNUM", info.getTelNum());
		}
		return JSON;
	}
	/** 省份信息 */
	public String prov() {
		AjaxInfo json = this.getAjaxInfo();
		String cna = this.getString("cna");
		if (cna == null) {
			json.addError(this.getText("dict.error.043"));
			return JSON;
		} // 检测编号
		String code = this.getString("Code");
		if (code == null || code.length() != 2) {
			json.addError(this.getText("dict.error.041"));
			return JSON;
		}
		String sid = this.getString("Sid");
		String cnb = this.getString("cnb");
		if (cnb == null) cnb = cna;
		try {
			if (sid == null || sid.length() < 2) {
				if (this.getSiteAreaService().isRegionByCode(code)) {
					json.addError(this.getText("dict.error.042", new String[]{code}));
				} else if (this.getSiteAreaService().isRegionByName(null, cna)) {
					json.addError(this.getText("dict.error.044", new String[]{cna}));
				} else {
					RegionInfo info = new RegionInfo();
					info.setSid(null); // 新增
					info.setCode(code);
					info.setCna(cna);
					info.setCnb(cnb);
					info.setEna(getString("ena"));
					info.setEnb(getString("enb"));
					this.getSiteAreaService().saveRegion(info);
					json.addMessage(this.getText("data.save.succeed"));
				}
			} else {
				RegionInfo info = this.getSiteAreaService().findRegionInfo(sid);
				if (info == null) {
					json.addError(this.getText("system.error.none"));
				} else if (!info.getCode().equals(code) && this.getSiteAreaService().isRegionByCode(code)) {
					json.addError(this.getText("dict.error.042", new String[]{code}));
				} else if (!info.getCna().equalsIgnoreCase(cna) && this.getSiteAreaService().isRegionByName(null, cna)) {
					json.addError(this.getText("dict.error.044", new String[]{cna}));
				} else {
					info.setCode(code);
					info.setCna(cna);
					info.setCnb(cnb);
					info.setEna(getString("ena"));
					info.setEnb(getString("enb"));
					this.getSiteAreaService().saveRegion(info);
					json.addMessage(this.getText("data.update.succeed"));
				}
			}
		} catch (Exception e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			sid = code = cna = cnb = null;
		}
		return JSON;
	}
	/** 区域信息 */
	public String city() {
		AjaxInfo json = this.getAjaxInfo();
		String cna = this.getString("cna");
		if (cna == null) {
			json.addError(this.getText("dict.error.045"));
			return JSON;
		} // 检测编号
		String code = this.getString("Code");
		int len = (code == null) ? 0 : code.length();
		if (len < 4 || len > 6) {
			json.addError(this.getText("dict.error.041"));
			return JSON;
		} // 名称简称
		String sid = this.getString("Sid");
		String cnb = this.getString("cnb");
		if (cnb == null) cnb = cna;
		try {
			String tid = code.substring(0, len - 2);
			if (sid == null || sid.length() < 2) {
				if (!this.getSiteAreaService().isRegionByCode(tid)) {
					json.addError(this.getText("dict.error.040"));
				} else if (this.getSiteAreaService().isRegionByCode(code)) {
					json.addError(this.getText("dict.error.042", new String[]{code}));
				} else if (this.getSiteAreaService().isRegionByName(null, cna)) {
					json.addError(this.getText("dict.error.046", new String[]{cna}));
				} else {
					RegionInfo info = new RegionInfo();
					info.setSid(null); // 新增
					info.setCode(code);
					info.setCna(cna);
					info.setCnb(cnb);
					info.setEna(getString("ena"));
					info.setEnb(getString("enb"));
					info.setZipCode(getString("ZipCode"));
					info.setTelCode(getString("TelCode"));
					info.setTelNum(getInt("TelNum"));
					this.getSiteAreaService().saveRegion(info);
					json.addMessage(this.getText("data.save.succeed"));
				}
			} else {
				RegionInfo info = this.getSiteAreaService().findRegionInfo(sid);
				if (info == null) {
					json.addError(this.getText("system.error.none"));
					return JSON;
				}
				if (!info.getCode().equalsIgnoreCase(code)) {
					if (!this.getSiteAreaService().isRegionByCode(tid)) {
						json.addError(this.getText("dict.error.040"));
						return JSON;
					} else if (this.getSiteAreaService().isRegionByCode(code)) {
						json.addError(this.getText("dict.error.042", new String[]{code}));
						return JSON;
					}
				} // 检测名称并保存
				if (!info.getCna().equalsIgnoreCase(cna) && this.getSiteAreaService().isRegionByName(null, cna)) {
					json.addError(this.getText("dict.error.046", new String[]{cna}));
				} else {
					info.setCode(code);
					info.setCna(cna);
					info.setCnb(cnb);
					info.setEna(getString("ena"));
					info.setEnb(getString("enb"));
					info.setZipCode(getString("ZipCode"));
					info.setTelCode(getString("TelCode"));
					info.setTelNum(getInt("TelNum"));
					this.getSiteAreaService().saveRegion(info);
					json.addMessage(this.getText("data.update.succeed"));
				}
			}
		} catch (Exception e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			sid = code = cna = cnb = null;
		}
		return JSON;
	}
	/** 村镇信息 */
	public String town() {
		AjaxInfo json = this.getAjaxInfo();
		String cna = this.getString("cna");
		if (cna == null) {
			json.addError(this.getText("dict.error.047"));
			return JSON;
		} // 检测编号
		String code = this.getString("Code");
		int len = (code == null) ? 0 : code.length();
		if (len < 6 || len > 10) {
			json.addError(this.getText("dict.error.041"));
			return JSON;
		} // 名称简称
		String sid = this.getString("Sid");
		String cnb = this.getString("cnb");
		if (cnb == null) cnb = cna;
		try {
			String tid = code.substring(0, len - 2);
			if (sid == null || sid.length() < 2) {
				if (!this.getSiteAreaService().isRegionByCode(tid)) {
					json.addError(this.getText("dict.error.040"));
				} else if (this.getSiteAreaService().isRegionByCode(code)) {
					json.addError(this.getText("dict.error.042", new String[]{code}));
				} else if (this.getSiteAreaService().isRegionByName(null, cna)) {
					json.addError(this.getText("dict.error.048", new String[]{cna}));
				} else {
					RegionInfo info = new RegionInfo();
					info.setSid(null); // 新增
					info.setCode(code);
					info.setCna(cna);
					info.setCnb(cnb);
					info.setEna(getString("ena"));
					info.setEnb(getString("enb"));
					info.setZipCode(getString("ZipCode"));
					info.setTelCode(getString("TelCode"));
					info.setTelNum(getInt("TelNum"));
					this.getSiteAreaService().saveRegion(info);
					json.addMessage(this.getText("data.save.succeed"));
				}
			} else {
				RegionInfo info = this.getSiteAreaService().findRegionInfo(sid);
				if (info == null) {
					json.addError(this.getText("system.error.none"));
					return JSON;
				}
				if (!info.getCode().equalsIgnoreCase(code)) {
					if (!this.getSiteAreaService().isRegionByCode(tid)) {
						json.addError(this.getText("dict.error.040"));
						return JSON;
					} else if (this.getSiteAreaService().isRegionByCode(code)) {
						json.addError(this.getText("dict.error.042", new String[]{code}));
						return JSON;
					}
				} // 检测名称并保存
				if (!info.getCna().equalsIgnoreCase(cna) && this.getSiteAreaService().isRegionByName(null, cna)) {
					json.addError(this.getText("dict.error.048", new String[]{cna}));
				} else {
					info.setCode(code);
					info.setCna(cna);
					info.setCnb(cnb);
					info.setEna(getString("ena"));
					info.setEnb(getString("enb"));
					info.setZipCode(getString("ZipCode"));
					info.setTelCode(getString("TelCode"));
					info.setTelNum(getInt("TelNum"));
					this.getSiteAreaService().saveRegion(info);
					json.addMessage(this.getText("data.update.succeed"));
				}
			}
		} catch (Exception e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			sid = code = cna = cnb = null;
		}
		return JSON;
	}

	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		String code = this.getString("Code");
		try {
			this.getSiteAreaService().removeRegion(code);
			json.addMessage(this.getText("data.delete.succeed"));
		} catch (Exception e) {
			json.addError(this.getText("data.delete.failed"));
		} finally {
			code = null;
		}
		return JSON;
	}

}
