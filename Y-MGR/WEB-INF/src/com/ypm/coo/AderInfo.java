package com.ypm.coo;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AdsInfo;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.service.AderInfoService;
import com.ypm.util.GMTime;
import com.ypm.util.VeStr;

public class AderInfo extends Action {

	private static final long serialVersionUID = 4316360885731261689L;

	private static int getType(int a, int b) {
		if (a >= 10) {
			return a;
		} else if (b < 10) {
			return b;
		}
		return 0;
	}

	private AderInfoService aderInfoService;

	private File files;

	public AderInfoService getAderInfoService() {
		return aderInfoService;
	}

	public void setAderInfoService(AderInfoService aderInfoService) {
		this.aderInfoService = aderInfoService;
	}

	public File getFiles() {
		return files;
	}

	public void setFiles(File files) {
		this.files = files;
	}

	public String tree() {
		int tid = this.getInt("tid");
		if (tid <= 0) {
			tid = 0;
		}
		this.setAjaxInfo(getAderInfoService().findTreeByInfo(tid));
		return JSON;
	}

	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		String sid = this.getString("sid");
		try {
			AdsInfo a = this.getAderInfoService().findAderBySid(sid);
			if (a == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("SID", a.getSid());
				json.append("TID", a.getTid());
				json.append("NAME", a.getName());
				json.append("TAGS", a.getTags());
				json.append("TYPE", a.getType());
				json.append("DIST", a.getDist());
				json.append("URL", a.getUrl());
				json.append("VER", a.getVer());
				json.append("STATE", a.getState());
				json.append("TIME", a.getTime());
				json.append( "IMG", a.getImg() );
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		} finally {
			sid = null;
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		int tid = this.getInt("Tid", -1);
		if (tid >= 1) {
			sql.append(" AND Tid=?");
			fs.add(tid);
		}
		String key = this.getString("key");
		if (key == null) {
			// Ignored
		} else {
			sql.append(" AND NAME LIKE ?");
			fs.add('%' + key + '%');
		} // 排序信息
		StringBuilder sb = new StringBuilder();
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Sid")) {
			sb.append("Sid");
			sort = "Sid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Sid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Sid DESC");
		} else {
			sb.append(" ASC,Sid DESC");
		} // 加载数据信息
		this.setAjaxInfo(this.getAderInfoService().findAderByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String sid = this.getString("Sid");
		FileInfo f = FileInfo.getImg(this.getFiles(), 1);
		try {
			AdsInfo ads = null;
			if (this.checkValid(sid, 5)) {
				ads = this.getAderInfoService().findAderBySid(sid);
				if (ads == null) {
					json.addError(this.getText("system.error.none"));
				} else if (!f.image(ads.getVer() >= 1)) {
					json.addError(this.getText("coo.error.001"));
					ads = null; // 图片不能为空
				} else {
					int type = getType(ads.getType(), getInt("Type"));
					ads.setType(type);
				}
			} else if (!f.image()) {
				json.addError(this.getText("coo.error.001"));
			} else if (f.getSize() > FILE_MAX_SIZE) {
				json.addError(this.getText("upload.error.smax", new String[] { String.valueOf(f.getSize()), String.valueOf(FILE_MAX_SIZE) }));
			} else {
				ads = new AdsInfo();
				ads.setSid(VeStr.getUSid(true));
				ads.setType(getType(0, getInt("Type")));
			}
			if (ads != null) {
				ads.setTid(getInt("Tid"));
				ads.setName(getString("Name"));
				ads.setTags(getString("Tags"));
				if (ads.getType() > 10) {
					// Ignored
				} else {
					ads.setUrl(getString("Url"));
				}
				ads.setState(getInt("State"));
				ads.setTime(GMTime.currentTimeMillis());
				if (f.getSize() > 0) {
					f.setPid(ads.getSid());
					ads.setVer(ads.getVer() + 1);
				}
				f.setRule(1080, 1920, true);
				this.getAderInfoService().saveAder(ads, f);
				json.addMessage(this.getText("data.save.succeed"));
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			json.addError(this.getText("data.save.failed"));
		} finally {
			f.destroy();
			sid = null;
		}
		return JSON;
	}

	public String order() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 5)) {
				this.getAderInfoService().orderInfo(ids);
				json.addMessage(this.getText("data.order.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.order.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	public String disable() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 5)) {
				this.getAderInfoService().stateInfo(ids, STATE_DISABLE);
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

	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 5)) {
				this.getAderInfoService().removeInfo(ids);
				json.addMessage(this.getText("data.delete.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.delete.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
