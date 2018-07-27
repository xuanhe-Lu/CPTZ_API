package com.ypiao.service;

import java.sql.SQLException;

public interface SendInfoService {

	public static final int SOK = 0;
	/** 非常规手机号 */
	public static final int STA = 1;
	/** 账号为白名单 */
	public static final int STB = 2;
	/** 账号为黑名单 */
	public static final int STC = 3;
	/** 请求过于频繁 */
	public static final int STD = 4;
	/** 当天额度用完 */
	public static final int STE = 5;

	/** 检测手机状态 */
	public int detect(String addr, String sm) throws SQLException;

	public boolean isCode(String sm, String code) throws SQLException;

	public boolean isUser(String sm);
	/** 发送验证码信息 */
	public boolean sendCode(String addr, String fix, String sm, int tid) throws SQLException;

}
