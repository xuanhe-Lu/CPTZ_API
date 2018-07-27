package com.ypm.asset;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.commons.lang.TimeUtils;
import com.ypm.Action;
import com.ypm.bean.*;
import com.ypm.service.AssetRawService;
import com.ypm.util.GMTime;
import com.ypm.util.GState;
import com.ypm.util.VeRule;

public class RawManage extends Action {

	private static final long serialVersionUID = 8140949088819825483L;

	private AssetRawService assetRawService;

	private File files;

	public AssetRawService getAssetRawService() {
		return assetRawService;
	}

	public void setAssetRawService(AssetRawService assetRawService) {
		this.assetRawService = assetRawService;
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
			long rid = this.getLong("Rid");
			RawInfo r = this.getAssetRawService().findRawByRid(rid);
			if (r == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("RID", r.getRid());
				json.append("CID", r.getCid());
				json.append("TID", r.getTid());
				json.append("CODE", r.getCode());
				json.append("BA", r.getBa());
				json.append("BB", VeRule.toRMB(r.getBb(), 4));
				String bc = GState.TODAY;
				if (r.getBc() > USER_TIME) {
					bc = GMTime.formatInt(r.getBc());
				} // 出票日期
				json.append("BC", bc);
				String bd = GState.TODAY;
				if (r.getBd() > USER_TIME) {
					bd = GMTime.formatInt(r.getBd());
				} // 承兑日期
				json.append("BD", bd);
				json.append("BE", r.getBe());
				json.append("BF", r.getBf());
				json.append("BG", r.getBg());
				String bh = GState.TODAY;
				if (r.getBh() > USER_TIME) {
					bh = GMTime.formatInt(r.getBh());
				} // 还款日期
				json.append("BH", bh);
				String bi = GState.TODAY;
				if (r.getBi() > USER_TIME) {
					bi = GMTime.formatInt(r.getBi());
				} // 借款日期
				json.append("BI", bi);
				json.append("BJ", TimeUtils.getTwoDay(bi, bh));
				json.append("BM", VeRule.toRMB(r.getBm(), 4));
				json.append("BN", r.getBn());
				json.append("BO", r.getBo());
				json.append("BU", r.getBu());
				json.append("BV", r.getBv());
				json.append("CA", r.getCa());
				json.append("CB", r.getCb());
				json.append("CC", r.getCc());
				json.append("CD", r.getCd());
				json.append("CE", r.getCe());
				json.append("CF", r.getCf());
				json.append("CG", r.getCg());
				json.append("CH", r.getCh());
				json.append("CK", r.getCk());
				json.append("TOTAL", r.getTotal());
				json.append("STATE", r.getState());
				json.append("TIME", (r.getTime() + GMTime.CHINA));
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
		int tid = this.getInt("Tid");
		if (tid <= 2 && tid >= 1) {
			sql.append(" AND Tid=?");
			fs.add(tid); // 类型
		} // 检测关键字
		String key = this.getString("KEY");
		String raw = this.getString("RAW");
		if (key == null) {
			// Ignored
		} else {
			if (VeRule.isYes("(BA|CA|CB|CD|CE)", raw)) {
				sql.append(" AND ").append(raw).append(" LIKE ?");
			} else {
				sql.append(" AND BA LIKE ?");
			}
			fs.add(sb.append('%').append(key).append('%').toString());
		}
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Rid")) {
			sb.append("Rid");
			sort = "Rid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Rid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Rid DESC");
		} else {
			sb.append(" ASC,Rid ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getAssetRawService().findRawByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String listImgs() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long rid = this.getLong("Rid");
			this.getAssetRawService().findImgsByRid(json, rid);
		} catch (Exception e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		FileInfo f = FileInfo.getImg(this.getFiles(), 12);
		try {
			long rid = this.getLong("Rid");
			RawInfo r = this.getAssetRawService().findRawByRid(rid);
			if (r == null) {
				json.addError(this.getText("system.error.none"));
			} else if (r.getState() >= STATE_READER) {
				json.addError(this.getText("asset.error.026"));
			} else if (!f.image(this.checkValid(r.getBu(), 10))) {
				json.addError(this.getText("asset.error.018"));
			} else {
				String sbi = this.getString("BI"); // 借款日期
				int bi = GMTime.parse(sbi);
				int bj = this.getInt("BJ"); // 借款天数
				if (GState.USER_TOADD >= bi) {
					json.addError(this.getText("asset.error.023"));
				} else if (bj <= 0) {
					json.addError(this.getText("asset.error.024"));
				} else {
					int bc = GMTime.parse(getString("BC"));
					int bd = GMTime.parse(getString("BD"));
					int bh = GMTime.getTday(sbi, bj); // 还款日期
					BigDecimal bb = VeRule.toWan(getBigDecimal("BB"));
					BigDecimal bm = VeRule.toWan(getBigDecimal("BM"));
					BigDecimal bn = this.getBigDecimal("BN");
					int rate = bn.intValue(); // 年化利率
					if (bc >= bd) {
						json.addError(this.getText("asset.error.019"));
					} else if (bh >= bd) {
						json.addError(this.getText("asset.error.020"));
					} else if (bm.compareTo(BigDecimal.ZERO) <= 0) {
						json.addError(this.getText("asset.error.021"));
					} else if (bm.compareTo(bb) >= 0) {
						json.addError(this.getText("asset.error.022"));
					} else if (rate < 1 || rate >= 50) {
						json.addError(this.getText("asset.error.025"));
					} else {
						r.setTid(getInt("Tid"));
						r.setBa(getString("BA"));
						r.setBb(bb); // 票据金额
						r.setBc(bc); // 出票日期
						r.setBd(bd); // 承兑日期
						r.setBe(getInt("BE"));
						r.setBf(getString("BF"));
						r.setBg(getString("BG"));
						r.setBh(bh); // 还款日期
						r.setBi(bi); // 借款日期
						r.setBj(bj); // 借款天数
						r.setBm(bm); // 借款金额
						r.setBn(bn); // 年化利率
						r.setMa(bm); // 借款总金额
						r.setBo(getBigDecimal("BO"));
						r.setCa(getString("CA"));
						r.setCb(getString("CB"));
						r.setCd(getString("CD"));
						r.setCe(getString("CE"));
						r.setCf(getString("CF"));
						r.setCg(getString("CG"));
						r.setCh(getString("CH"));
						r.setCk(getInt("CK"));
						if (r.getBv() >= 1) {
							r.setState(SALE_A0);
						} // 保存数据信息
						f.setRule(1080, 1920, true);
						this.getAssetRawService().saveRawInfo(r, f);
						json.addMessage(this.getText("data.save.succeed"));
					}
				}
			}
		} catch (SQLException | IOException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			f.destroy();
		}
		return JSON;
	}

	public String imgs() {
		AjaxInfo json = this.getAjaxInfo();
		String Pid = this.getString("Pid");
		FileInfo f = FileInfo.getImg(this.getFiles(), 13);
		try {
			long rid = this.getLong("Rid");
			if (this.checkValid(Pid, 10)) {
				f.setPid(Pid); // 文档编号
				if (!this.getAssetRawService().loadImg(f)) {
					json.addError(this.getText("system.error.none"));
					return JSON;
				} else if (!f.image(true)) {
					json.addError(this.getText("asset.error.028"));
					return JSON;
				}
			} else if (!f.image()) {
				json.addError(this.getText("asset.error.028"));
				return JSON;
			} else {
				f.setSid(String.valueOf(rid));
				f.setSortid(this.getAssetRawService().findImgByRid(rid));
			} // 检测文件是否过大
			if (f.getSortid() <= 0) {
				json.addError(this.getText("system.error.pars"));
			} else if (f.getSize() > FILE_MAX_SIZE) {
				json.addError(this.getText("upload.error.smax", new String[] { String.valueOf(f.getSize()), String.valueOf(FILE_MAX_SIZE) }));
			} else {
				f.setName(getString("Name"));
				f.setInfo(getString("Info"));
				f.setState(getInt("State")); // 状态信息
				f.setRule(1080, 1920, true);
				this.getAssetRawService().saveRawImgs(f, rid);
				json.addMessage(this.getText("data.save.succeed"));
			}
		} catch (SQLException | IOException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			f.destroy();
			Pid = null;
		}
		return JSON;
	}

	public String tree() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long rid = this.getLong("Rid");
			this.getAssetRawService().findTreeByRid(json, rid);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String order() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			long rid = this.getLong("Rid");
			if (this.checkValid(ids, 10)) {
				this.getAssetRawService().saveOrder(rid, ids);
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
		try {
			long rid = this.getLong("Rid");
			RawInfo r = this.getAssetRawService().findRawByRid(rid);
			if (r == null) {
				json.addError(this.getText("system.error.none"));
			} else if (r.getTotal() >= 1) {
				json.addError(this.getText("asset.error.030"));
			} else {
				this.getAssetRawService().removeInfo(r.getRid());
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.delete.failed"));
		}
		return JSON;
	}

	public String delImgs() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			long rid = this.getLong("Rid");
			if (this.checkValid(ids, 10)) {
				if (this.getAssetRawService().removePics(rid, ids)) {
					json.addMessage(this.getText("data.delete.succeed"));
				} else {
					json.addError(this.getText("asset.error.029"));
				}
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
