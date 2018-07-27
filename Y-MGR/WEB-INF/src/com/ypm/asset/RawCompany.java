package com.ypm.asset;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Company;
import com.ypm.bean.FileInfo;
import com.ypm.service.AssetComService;
import com.ypm.util.GMTime;
import com.ypm.util.VeRule;

public class RawCompany extends Action {

	private static final long serialVersionUID = 1656176141379358594L;

	private AssetComService assetComService;

	private File files;

	public AssetComService getAssetComService() {
		return assetComService;
	}

	public void setAssetComService(AssetComService assetComService) {
		this.assetComService = assetComService;
	}

	public File getFiles() {
		return files;
	}

	public void setFiles(File files) {
		this.files = files;
	}

	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int cid = this.getInt("Cid");
			Company c = this.getAssetComService().findCompanyById(cid);
			if (c == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("CID", c.getCid());
				json.append("TID", c.getTid());
				json.append("CA", c.getCa());
				json.append("CB", c.getCb());
				json.append("CC", c.getCc());
				json.append("CD", c.getCd());
				json.append("CE", c.getCe());
				json.append("CF", c.getCf());
				json.append("CG", c.getCg());
				json.append("CH", c.getCh());
				json.append("CK", c.getCk());
				json.append("CM", c.getCm());
				json.append("CN", c.getCn());
				json.append("TOTAL", c.getTotal());
				json.append("STATE", c.getState());
				json.append("TIME", (c.getTime() + GMTime.CHINA));
			}
		} catch (SQLException e) {
			json.addError(this.getText(""));
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		int tid = this.getInt("Tid");
		if (tid <= 2 && tid >= 1) {
			sql.append(" AND Tid=?");
			fs.add(tid); // 类型
		} // 检测关键字
		String key = this.getString("KEY");
		String com = this.getString("COM");
		if (key == null) {
			// Ignored
		} else if (VeRule.isYes("(CA|CB|CD|CE)", com)) {
			sql.append(" AND ").append(com).append(" LIKE ?");
			fs.add(sb.append('%').append(key).append('%').toString());
		} else {
			sql.append(" AND MATCH(CA,CB,CD,CE) AGAINST(?)");
			fs.add(key);
		}
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Cid")) {
			sb.append("Cid");
			sort = "Cid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Cid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Cid DESC");
		} else {
			sb.append(" ASC,Cid ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getAssetComService().findCompanyByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	/** 保存企业 */
	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		FileInfo f = FileInfo.getImg(this.getFiles(), 11);
		try {
			Company c = null;
			int cid = this.getInt("Cid");
			String sno = this.getString("CB");
			if (cid >= 1) {
				if (f.image(true)) {
					c = this.getAssetComService().findCompanyById(cid);
					if (c == null) {
						json.addError(this.getText("system.error.none"));
					} else if (sno.equalsIgnoreCase(c.getCb())) {
						// Ignored
					} else if (this.getAssetComService().isCompanyBySNo(sno)) {
						json.addError(this.getText("asset.error.004", new String[] { sno }));
					} else {
						c.setCb(sno);
					}
				} else {
					json.addError(this.getText("asset.error.005"));
				}
			} else if (!f.image()) {
				json.addError(this.getText("asset.error.005"));
			} else if (this.getAssetComService().isCompanyBySNo(sno)) {
				json.addError(this.getText("asset.error.004", new String[] { sno }));
			} else {
				c = new Company();
				c.setCb(sno);
			} // 加载相关参数信息
			if (c != null) {
				c.setTid(getInt("Tid"));
				c.setCa(getString("CA"));
				if (f.isFile()) {
					c.setCc(f.getPid());
				}
				c.setCd(getString("CD"));
				c.setCe(getString("CE"));
				c.setCf(getString("CF"));
				c.setCg(getString("CG"));
				c.setCh(getString("CH"));
				c.setCk(getInt("CK"));
				f.setRule(1080, 1920, true);
				this.getAssetComService().saveCompany(c, f);
				json.addMessage(this.getText("data.save.succeed"));
			}
		} catch (SQLException | IOException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			f.destroy();
		}
		return JSON;
	}

	/** 发布票据 */
	public String send() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int cid = this.getInt("ids");
			Company c = this.getAssetComService().findCompanyById(cid);
			if (c == null) {
				json.addError(this.getText("system.error.none"));
			} else if (c.getState() != STATE_NORMAL) {
				json.addError(this.getText("asset.error.013"));
			} else if (this.getAssetComService().sendYPiao(c)) {
				json.addMessage(this.getText("asset.msg.002"));
			} else {
				json.addError(this.getText("asset.error.015"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.update.failed"));
		}
		return JSON;
	}

	/** 删除企业 */
	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int cid = this.getInt("ids");
			if (this.getAssetComService().remove(cid)) {
				json.addMessage(this.getText("data.delete.succeed"));
			} else {
				json.addError(this.getText("asset.error.011"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.delete.failed"));
		}
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
