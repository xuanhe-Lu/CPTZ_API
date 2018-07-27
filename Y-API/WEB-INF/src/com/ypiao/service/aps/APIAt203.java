package com.ypiao.service.aps;

import java.sql.SQLException;
import java.util.List;
import com.ypiao.bean.AutoRaws;
import com.ypiao.bean.Manager;
import com.ypiao.bean.ProdInfo;
import com.ypiao.service.AssetRawService;
import com.ypiao.service.ProdInfoService;

/**
 * 标的信息APS同步.
 */
public class APIAt203 extends Abstract {

	private AssetRawService assetRawService;

	private ProdInfoService prodInfoService;

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

	public void over(Manager mgr) {
		try {
			List<AutoRaws> ls = mgr.getList(AutoRaws.class);
			if (ls.size() >= 1) {
				this.getAssetRawService().save(ls);
				this.getProdInfoService().save(ls);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void save(Manager mgr) {
		try {
			ProdInfo info = mgr.getObject(ProdInfo.class);
			this.getProdInfoService().save(info);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void saveAuto(Manager mgr) {
		try {
			long Pid = mgr.getLong("pid");
			long time = mgr.getLong("time");
			this.getProdInfoService().update(Pid, time);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void saveOver(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			long time = mgr.getLong("time");
			this.getProdInfoService().update(ids, time);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		} finally {
			ids = null;
		}
	}

	public void saveState(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			int state = mgr.getInt("state");
			long time = mgr.getLong("time");
			this.getProdInfoService().update(ids, state, time);
		} catch (Exception e) {
			mgr.addError(DATA_SAVE_FAILED);
		} finally {
			ids = null;
		}
	}

	public void remove(Manager mgr) {
		try {
			long Pid = mgr.getLong("pid");
			long Rid = mgr.getLong("rid");
			long time = mgr.getLong("time");
			this.getProdInfoService().remove(Pid, Rid, time);
		} catch (SQLException e) {
			mgr.addError(DATA_DELETE_FAILED);
		}
	}
}
