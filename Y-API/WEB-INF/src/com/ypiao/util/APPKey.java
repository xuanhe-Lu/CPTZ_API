package com.ypiao.util;

public interface APPKey {

	// ==================== 以下为未登录也可操作 ====================
	/** 心跳包信息 */
	public static final int USER_HEART = 100;
	/** 基础信息查询 */
	public static final int USER_A112 = 112;
	/** 基础信息查询 */
	public static final int USER_A113 = 113;
	/** 区域数据信息 */
	public static final int USER_A114 = 114;
	/** 验证码发送 */
	public static final int USER_A115 = 115;
	/** 取回登录密码 */
	public static final int USER_A116 = 116;
	/** 用户登录处理 */
	public static final int USER_A120 = 120;

	// ==================== 以下为登录后方可操作 ====================
	public static final int USER_A121 = 121;
	/** 头像修改处理 */
	public static final int USER_A122 = 122;
	/** 用户资料修改 */
	public static final int USER_A123 = 123;
	/** 实名认证处理 */
	public static final int USER_A124 = 124;
	/** 用户系统设置 */
	public static final int USER_A127 = 127;
	// /** 修改手机账号 */
	// public static final int USER_A130 = 130;
	/** 二维码信息 */
	public static final int USER_A132 = 132;
	// /** 等级特权接口 */
	// public static final int USER_A133 = 133;
	/** 分享服务接口 */
	public static final int USER_A135 = 135;
	/** 资产服务接口 */
	public static final int USER_A138 = 138;
	/** 芝麻信息接口 */
	public static final int USER_A150 = 150;
	/** 银行账号信息 */
	public static final int USER_A151 = 151;
	/** 账号充值信息 */
	public static final int USER_A152 = 152;
	/** 付款操作信息 */
	public static final int USER_A153 = 153;
	/** 提现操作信息 */
	public static final int USER_A154 = 154;
	/** 账单明细信息 */
	public static final int USER_A158 = 158;
	/** 会员支付接口 */
	public static final int USER_A168 = 168;
	// ==================== 以下为未登录也可操作 ====================
	/** 意见反馈 */
	public static final int USER_A900 = 900;
	/** 支付、登录密码修改 */
	public static final int USER_A916 = 916;
}
