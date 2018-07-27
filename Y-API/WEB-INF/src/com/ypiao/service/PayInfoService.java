package com.ypiao.service;

import com.ypiao.bean.PayInfo;

public interface PayInfoService {

	public PayInfo getInfoByFuiou();

	public PayInfo getPayInfo(String key);
}
