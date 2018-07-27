package com.ypiao.service;

import com.ypiao.bean.SMSLogs;

public interface SenderService {

	public boolean sendByCode(SMSLogs log);

	public boolean sendByBank(String mobile, String code);

	public boolean sendByText(String mobile, String content);

}
