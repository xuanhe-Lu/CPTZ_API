package com.ypm.util;

public interface AState {

	/** 数据处理失败 */
	public static final int API_ERROR = -1;
	/** 成功处理请求 */
	public static final int API_OK = 0;
	/** 成功处理请求 */
	public static final int API_YES = 1;
	/** 默认字典 */
	public static final int DICT_DEFS = 0;
	/** 系统字典 */
	public static final int DICT_INFO = 1;
	/** 用户字典 */
	public static final int DICT_USER = 2;
	/** 正常值 -> 待售中 -> 待付款 */
	public static final int SALE_A0 = 0;
	/** 审核值 -> 销售中 -> 待起息 */
	public static final int SALE_A1 = 1;
	/** 锁定值 -> 已售罄 -> 待回款 */
	public static final int SALE_A2 = 2;
	/** 待打款 -> 待回款 -> 回款中 */
	public static final int SALE_A3 = 3;
	/** 待回款 -> 回款中 -> 已完成 */
	public static final int SALE_A4 = 4;
	/** 已完成 -> 已完成 */
	public static final int SALE_A5 = 5;
	/** 正常 */
	public static final int STATE_ENABLE = 0;
	/** 禁用 */
	public static final int STATE_DISABLE = 1;
	/** 状态正常 */
	public static final int STATE_NORMAL = 0;
	/** 状态审核 */
	public static final int STATE_CHECK = 1;
	/** 状态锁定 */
	public static final int STATE_READER = 2;
	/** 状态错误 */
	public static final int STATE_ERRORS = 3;
	/** 状态过期 */
	public static final int STATE_EXPIRE = 4;
	/** 状态删除 */
	public static final int STATE_DELETE = 5;
	/** 删除临界值 */
	public static final int USER_FMAX = 30;
	/** 用户起始时间=1970-01-01 16:16:40 */
	public static final int USER_TIME = 1000000;
	/** 数字起始编号 */
	public static final long USER_IDS = 100000;
	/** 用户起始编号 */
	public static final long USER_UID_BEG = 100000;
	/** 系统临界值=9位 */
	public static final long USER_UID_MAX = 1000000000L;

	public static final String WAY_AUTO = "AUTO";

	public static final String WAY_USER = "USER";

	public static final String WAY_TSYS = "TSYS";
}
