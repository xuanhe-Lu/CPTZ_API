package com.ypm.service;

import java.util.List;
import com.ypm.bean.AjaxInfo;

public interface CfoUserService {

	// ==================== API 接口层 ====================
	public AjaxInfo findUserByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

}
