package com.ypiao.service.aps;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.ypiao.bean.Config;
import com.ypiao.bean.Manager;
import com.ypiao.bean.SetClient;
import com.ypiao.service.ConfigService;

/**
 * 系统参数配置信息同步APS接口. 
 */
public class APIAt112 extends Abstract {
	
	private static final Logger LOGGER = Logger.getLogger(APIAt112.class);

	private ConfigService configService;

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public void syncClient(Manager mgr) {
		SetClient sc = mgr.getObject(SetClient.class);
		try {
			this.getConfigService().saveClient(sc);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void stateClient(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			int state = mgr.getInt("state");
			long time = mgr.getLong("time");
			this.getConfigService().updateClient(ids, state, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}

	public void delClient(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			this.getConfigService().removeClient(ids);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}

	/**
	 * @param mgr is Manager
	 * 
	 * 同步保存系统参数配置信息 
	 */
	public void saveConfig(Manager mgr) {
		LOGGER.info( "进入同步保存系统参数配置信息方法..." );
		try {
			Config cfg = mgr.getObject(Config.class);
			LOGGER.info( "同步保存系统参数配置信息，同步对象：" + cfg.toString() );
			this.getConfigService().saveConfig(cfg);
		} catch (Exception e) {
			LOGGER.info( "同步保存系统参数配置信息失败，错误信息：" + e.getMessage() );
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}

	public void orderConfig(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			this.getConfigService().orderConfig(ids);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}

	public void delConfig(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			this.getConfigService().removeConfig(ids);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}
}
