package com.ypiao.service;

import java.io.IOException;
import com.ypiao.bean.Manager;
import com.ypiao.util.AState;

public interface APInterService extends AState {

	public static final String DATA_DELETE_FAILED = "data.delete.failed";

	public static final String DATA_SAVE_FAILED = "data.save.failed";

	public static final String DATA_UPDATE_FAILED = "data.update.failed";
	/** 系统错误，获取数据失败！ */
	public static final String SYSTEM_ERROR_GETS = "system.error.get";
	/** 系统错误请稍后再试…… */
	public static final String SYSTEM_ERROR_INFO = "system.error.info";

	public static final String SYSTEM_ERROR_NONE = "system.error.none";

	public static final String SYSTEM_ERROR_PARS = "system.error.pars";

	public void execute(Manager mgr) throws IOException;

}
