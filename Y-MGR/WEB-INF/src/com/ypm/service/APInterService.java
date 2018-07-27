package com.ypm.service;

import java.io.IOException;
import com.ypm.bean.Manager;

public interface APInterService {

	public static final String DATA_DELETE_FAILED = "删除记录失败，请稍后再试！";

	public static final String DATA_SAVE_FAILED = "保存数据失败，请稍候再试！";

	public static final String DATA_UPDATE_FAILED = "更新数据失败，请稍候再试！";

	public static final String SYSTEM_ERROR_INFO = "系统错误，请稍后再试！";

	public static final String SYSTEM_ERROR_NONE = "未找到相关信息！";

	public static final String SYSTEM_ERROR_PARS = "参数错误，数据处理失败！";

	public void execute(Manager mgr) throws IOException;
}
