package com.ypm.asset;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.ypm.Action;
import com.ypm.bean.*;
import com.ypm.service.*;
import com.ypm.util.GMTime;
import com.ypm.util.VeKey;
import com.ypm.util.VeStr;

/**
 * 标的管理 Action. 
 */
public class ProdManage extends Action {

	private static final long serialVersionUID = 9173766297618649341L;

	private ActivityService activityService;

	private AssetRawService assetRawService;

	private ProdInfoService prodInfoService;

	private ProdModelService prodModelService;

	public ActivityService getActivityService() {
		return activityService;
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	public AssetRawService getAssetRawService() {
		return assetRawService;
	}

	public void setAssetRawService(AssetRawService assetRawService) {
		this.assetRawService = assetRawService;
	}

	public ProdInfoService getProdInfoService() {
		return prodInfoService;
	}

	public void setProdInfoService(ProdInfoService prodInfoService) {
		this.prodInfoService = prodInfoService;
	}

	public ProdModelService getProdModelService() {
		return prodModelService;
	}

	public void setProdModelService(ProdModelService prodModelService) {
		this.prodModelService = prodModelService;
	}

	/** 活动规则信息 */
	public String getAct() {
		this.setAjaxInfo(this.getActivityService().findActByAll());
		return JSON;
	}

	/** 获取可用票据 */
	public String getRaws() {
		this.setAjaxInfo(this.getAssetRawService().findRawByAll());
		return JSON;
	}

	/** 获取产品信息 */
	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long Pid = this.getLong("Pid");
			ProdInfo info = this.getProdInfoService().findProdByPid(Pid);
			if (info == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				RawProd r = this.getAssetRawService().findProdByRid(info.getRid());
				if (r == null) {
					json.addError(this.getText("system.error.pars"));
				} else {
					ProdModel m = this.getProdModelService().findModelByTid(info.getTid());
					if (m == null) {
						m = new ProdModel();
					}
					json.data();
					json.append("PID", info.getPid());
					json.append("RID", r.getBa()); // 关联票据
					json.append("CID", info.getCid());
					json.append("TID", info.getTid());
					json.append("ADJ", info.getAdj());
					if (info.getAdk().compareTo(BigDecimal.ZERO) >= 1) {
						json.append("ADK", DF2.format(info.getAdk()), "%");
					} else {
						json.append("ADK", "-");
					} // 加息
					json.append("ADS", info.getAds());
					json.append("AA", info.getAa());
					json.append("AB", info.getAb());
					json.append("AC", info.getAc());
					json.append("AD", info.getAd());
					json.append("AE", info.getAe());
					json.append("AF", info.getAf());
					json.append("AM", info.getAm());
					json.append("AN", info.getAn());
					json.append("AP", info.getAp());
					json.append("AT", info.getAt());
					json.append("MB", info.getMb());
					json.append("MC", info.getMc());
					json.append("SAJ", info.getAj(), "天");
					json.append("SBM", DF2.format(r.getBm()));
					json.append("SBN", DF2.format(r.getBn()), "%");
					json.append("SBI", GMTime.formatInt(info.getAi()));
					json.append("SMB", DF2.format(info.getMb()), "元");
					json.append("SMC", DF2.format(info.getMc()), "元");
					StringBuilder sb = json.getBuilder("CYCLE");
					sb.append('"').append(GMTime.formatInt(info.getAi())).append(" - ").append(GMTime.formatInt(info.getAh())).append('"');
					json.append("GMTB", (info.getGmtB() + GMTime.CHINA));
					json.append("GMTC", (info.getGmtC() + GMTime.CHINA));
					if (m.getTofee() == 1) {
						json.append("TOFEE", "可以使用优惠券");
					} else {
						json.append("TOFEE", "禁止使用优惠券");
					}
				}
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long Pid = this.getLong("Pid");
			ProdInfo info = this.getProdInfoService().findProdByPid(Pid);
			if (info == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				RawProd r = this.getAssetRawService().findProdByRid(info.getRid());
				if (r == null) {
					json.addError(this.getText("system.error.pars"));
				} else {
					ProdModel m = this.getProdModelService().findModelByTid(info.getTid());
					if (m == null) {
						m = new ProdModel();
					}
					json.data(); // 构建信息
					Map<String, String> ab = this.getDictBySid(VeKey.ASSET_PROD_TAGS);
					Map<String, String> ac = this.getDictBySid(VeKey.ASSET_PROD_TYPE);
					Map<String, String> ad = this.getDictBySid(VeKey.ASSET_PROD_SALES);
					Map<String, String> ap = this.getDictBySid(VeKey.ASSET_PROD_REPAY);
					Map<String, String> at = this.getDictBySid(VeKey.COO_PROD_SHOW);
					DecimalFormat df3 = new DecimalFormat("#,##0.00");
					json.append("RID", r.getBa()); // 关联票据
					json.append("TID", m.getName());
					if (info.getAdj() >= 1) {
						ActInfo a = this.getActivityService().getActByAdj(info.getAdj());
						if (a == null) {
							json.append("ADJ", "-");
							json.append("ADK", "");
						} else {
							json.append("ADJ", a.getName());
							if (a.getRate().compareTo(BigDecimal.ZERO) >= 1) {
								json.append("ADK", DF2.format(a.getRate()), "%");
							} else {
								json.append("ADK", "");
							}
						}
					} else {
						json.append("ADJ", "-");
						json.append("ADK", "");
					} // 加息
					if (info.getAds().compareTo(BigDecimal.ZERO) >= 1) {
						json.append("ADS", DF2.format(info.getAds()), "%");
					} else {
						json.append("ADS", "0");
					} // Prod name
					json.append("AA", info.getAa());
					json.append("AB", ab.get(String.valueOf(info.getAb())));
					json.append("AC", ac.get(String.valueOf(info.getAc())));
					json.append("AD", ad.get(String.valueOf(info.getAd())));
					json.append("AE", info.getAe());
					json.append("AF", info.getAf());
					json.append("AM", df3.format(info.getAm()));
					json.append("AN", DF2.format(info.getAn()), "%");
					json.append("AP", ap.get(String.valueOf(info.getAp())));
					json.append("AT", at.get(String.valueOf(info.getAt())));
					json.append("MB", df3.format(info.getMb()));
					json.append("MC", df3.format(info.getMc()));
					json.append("SAJ", info.getAj(), "天");
					json.append("SBM", df3.format(r.getBm()));
					json.append("SBN", DF2.format(r.getBn()), "%");
					json.append("SBI", GMTime.formatInt(info.getAi()));
					json.append("SMB", DF2.format(info.getMb()), "元");
					json.append("SMC", DF2.format(info.getMc()), "元");
					StringBuilder sb = json.getBuilder("CYCLE");
					sb.append('"').append(GMTime.formatInt(info.getAi())).append(" - ").append(GMTime.formatInt(info.getAh())).append('"');
					json.append("GMTB", GMTime.format(info.getGmtB(), GMTime.CHINA));
					json.append("GMTC", GMTime.format(info.getGmtC(), GMTime.CHINA));
					if (m.getTofee() == 1) {
						json.append("TOFEE", "可以使用优惠券");
					} else {
						json.append("TOFEE", "禁止使用优惠券");
					}
				}
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
		int au = this.getInt("AU", -1);
		if (au <= 4 && au >= 0) {
			sql.append(" AND AU=?");
			fs.add(au); // 类型
		} // 检测关键字
		String key = this.getString("KEY");
		if (key == null) {
			// Ignored
		} else {
			sql.append(" AND AA LIKE ?");
			fs.add(sb.append('%').append(key).append('%').toString());
		}
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Pid")) {
			sb.append("Pid");
			sort = "Pid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Pid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Pid DESC");
		} else {
			sb.append(" ASC,Pid ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getProdInfoService().findProdByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		String aa = this.getString("AA");
		String gmtB = this.getString("GMTB"); // 上架时间
		String gmtC = this.getString("GMTC"); // 截止时间
		try {
			boolean save = false;
			ProdInfo info = null;
			long Pid = this.getLong("Pid"); // 产品编号
			long sbi = 0;
			long GmtC = GMTime.valueOf(gmtC);
			if (aa == null) {
				json.addError(this.getText("asset.error.111"));
			} else if (Pid >= USER_IDS) {
				info = this.getProdInfoService().findProdByPid(Pid);
				if (info == null) {
					json.addError(this.getText("system.error.none"));
				} else if (info.getState() >= STATE_READER) {
					json.addError(this.getText("asset.error.120"));
				} else {
					sbi = GMTime.valueOf(info.getAi());
					if (GmtC > sbi) {
						json.addError(this.getText("asset.error.117", new String[] { GMTime.format(sbi, GMTime.CHINA) }));
					} else if (!aa.equalsIgnoreCase(info.getAa()) && this.getProdInfoService().isProdByName(aa)) {
						json.addError(this.getText("asset.error.112", new String[] { aa }));
					} else {
						save = true;
					}
				}
			} else if (this.getProdInfoService().isProdByName(aa)) {
				json.addError(this.getText("asset.error.112", new String[] { aa }));
			} else {
				long Rid = this.getLong("Rid"); // 关联票据
				RawProd r = this.getAssetRawService().findProdByRid(Rid);
				if (r == null || r.getState() != STATE_NORMAL) {
					json.addError(this.getText("asset.error.114"));
				} else {
					sbi = GMTime.valueOf(r.getBi());
					if (GmtC > sbi) {
						json.addError(this.getText("asset.error.117", new String[] { GMTime.format(sbi, GMTime.CHINA) }));
					} else {
						info = new ProdInfo();
						info.setPid(VeStr.getUSid());
						info.setRid(r.getRid());
						info.setCid(r.getCid());
						info.setBa(r.getBa());
						info.setBg(r.getBg());
						info.setCa(r.getCa());
						info.setAg(r.getBh()); // 回款
						info.setAh(r.getBh()); // 还款
						info.setAi(r.getBi()); // 起息
						info.setAj(r.getBj()); // 期限
						info.setAm(r.getBm());
						info.setMa(r.getBm());
						info.setGmtA(GMTime.currentTimeMillis());
						save = true;
					}
				}
			} // 修改相关信息
			if (save) {
				info.setGmtB(GMTime.valueOf(gmtB));
				if (sbi == GmtC) {
					info.setGmtC(sbi - 300000);
				} else {
					info.setGmtC(GmtC);
				} // 检测上架时间早于结标时间
				if (info.getGmtB() >= info.getGmtC()) {
					json.addError(this.getText("asset.error.119", new String[] { gmtC }));
				} else {
					info.setAa(aa);
					info.setTid(getInt("Tid"));
					info.setAdj(0);
					info.setAdk(BigDecimal.ZERO);
					int adj = this.getInt("adj");
					if (adj >= 1) {
						ActInfo a = this.getActivityService().getActByAdj(adj);
						if (a != null) {
							info.setAdj(a.getAdj());
							info.setAdk(a.getRate());
						}
					} // 加息
					info.setAds(getBigDecimal("Ads"));
					info.setAb(getInt("AB"));
					info.setAc(getInt("AC"));
					info.setAd(getInt("AD"));
					info.setAe(getString("AE"));
					info.setAf(getString("AF"));
					info.setAn(getBigDecimal("AN"));
					info.setAp(getInt("AP"));
					info.setAt(getInt("AT"));
					info.setAu(SALE_A0); // 待售
					info.setMb(getBigDecimal("MB"));
					info.setMc(getBigDecimal("MC"));
					info.setState(STATE_NORMAL);
					info.setStime(info.getGmtB());
					this.getProdInfoService().saveProd(info);
					json.addMessage(this.getText("data.save.succeed"));
				}
			}
		} catch (Exception e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			aa = gmtB = gmtC = null;
		}
		return JSON;
	}

	/** 发布广告 */
	public String ads() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 5)) {
				this.getProdInfoService().saveAds(ids);
				json.addMessage(this.getText("data.ads.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.ads.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	/** 手动结标 */
	public String over() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 15)) {
				this.getProdInfoService().saveOver(ids);
				json.addMessage(this.getText("data.save.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	/** 上/下架标的 */
	public String state() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 15)) {
				int state = this.getInt("state");
				this.getProdInfoService().saveState(ids, state);
				json.addMessage(this.getText("data.save.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			json.addError(this.getText("data.save.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	/** 删除标的信息 */
	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long Pid = this.getLong("Pid");
			ProdInfo info = this.getProdInfoService().findProdByPid(Pid);
			if (info == null) {
				json.addError(this.getText("system.error.none"));
			} else if (info.getState() >= STATE_READER) {
				json.addError(this.getText("asset.error.120"));
			} else if (info.getMd().compareTo(BigDecimal.ZERO) >= 1) {
				json.addError(this.getText("asset.error.121"));
			} else {
				this.getProdInfoService().removeProd(info);
				json.addMessage(this.getText("data.delete.succeed"));
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
