package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.FileInfo;
import com.ypiao.bean.Manager;
import com.ypiao.bean.RawInfo;
import com.ypiao.service.AssetRawService;
import org.apache.log4j.Logger;

public class APIAt202 extends Abstract {

	private static Logger logger = Logger.getLogger(APIAt202.class);

	private AssetRawService assetRawService;

	public AssetRawService getAssetRawService() {
		return assetRawService;
	}

	public void setAssetRawService(AssetRawService assetRawService) {
		this.assetRawService = assetRawService;
	}

	public void order(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			long rid = mgr.getLong("rid");
			long time = mgr.getLong("time");
			this.getAssetRawService().save(rid, ids, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		} finally {
			ids = null;
		}
	}

	public void imgs(Manager mgr) {
		try {
			FileInfo f = mgr.getObject(FileInfo.class);
			this.getAssetRawService().save(f);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void delete(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			long rid = mgr.getLong("rid");
			this.getAssetRawService().remove(rid, ids, 0);
		} catch (SQLException e) {
			mgr.addError(DATA_DELETE_FAILED);
		} finally {
			ids = null;
		}
	}

	public void save(Manager mgr) {
		try {
			logger.info("come in save ,mgr:"+mgr.toString());
			RawInfo r = mgr.getObject(RawInfo.class);
			logger.info("come in save ,r:"+r.toString());
			this.getAssetRawService().save(r);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void remove(Manager mgr) {
		try {
			long rid = mgr.getLong("rid");
			this.getAssetRawService().remove(rid);
		} catch (SQLException e) {
			mgr.addError(DATA_DELETE_FAILED);
		}
	}
}
