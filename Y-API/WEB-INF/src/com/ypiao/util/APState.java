package com.ypiao.util;

public interface APState {

	/** 充值 */
	public static final int TRADE_ADD1 = 1;
	/** 提现 */
	public static final int TRADE_OUT2 = 2;
	/** 提现退回 */
	public static final int TRADE_ADD3 = 3;
	/** 理财消费 */
	public static final int TRADE_OUT4 = 4;
	/** 理财回款 */
	public static final int TRADE_ADD5 = 5;

	public static final int TRADE_OUT6 = 6;

	public static final String EVENT_P000 = "账户余额";

	public static final String EVENT_P001 = "支付宝";

	public static final String EVENT_P002 = "微信支付";

	public static final String EVENT_P003 = "银行充值";

	public static final String EVENT_P004 = "佣金转入";

	public static final String EVENT_P005 = "收益";

	public static final String EVENT_P011 = "提现";

	public static final String EVENT_P012 = "消费";

	public static final String EVENT_P013 = "保证金支付";

	public static final String EVENT_P014 = "理财消费";

	public static final String EVENT_P015 = "理财回款";

	public static final int ORDER_USER_MONEY_ERROR = 0;

	public static final int ORDER_USER_NOBILD_CARD = 1;

	public static final int ORDER_USER_NOREAL_NAME = 2;

	public static final int ORDER_USER_ACTION_FAIL = 3;

	public static final int ORDER_USER_NO_VERCD = 4;

	public static final int ORDER_USER_PAY_FAIL = 5;

	public static final int ORDER_PAY_NO_BACK = 6;

	public static final int ORDER_SIGN_FAIL = 7;

	public static final int ORDER_OTHER = 8;
	/** 支付成功 */
	public static final int ORDER_SUCCESS = 9;

}
