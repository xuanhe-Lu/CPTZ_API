package com.ypm.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.*;

public interface UserInfoService {

	// ==================== APS 接口层 ====================
	public void reg(UserReger reg) throws SQLException;

	public void save(UserInfo info) throws SQLException;

	public int update(Connection conn, UserAuth a) throws SQLException;

	public int update(Connection conn, UserBank b) throws SQLException;

	public int update(Connection conn, long uid, int coupon, long time) throws SQLException;

//	public int updateAddTX(Connection conn, long uid, BigDecimal rmb, long time) throws SQLException;

	/** 投资回款 */
	public int updateAddTZ(Connection conn, long uid, BigDecimal rmb, BigDecimal sy, long time) throws SQLException;
	public int updateYpiaoAddTZ(Connection conn, long uid, BigDecimal rmb, BigDecimal sy, long time) throws SQLException;
	/** 提现操作 */
	public int updateSubTX(Connection conn, long uid, BigDecimal rmb, long time) throws SQLException;
	/** 投资操作 */
	public int updateSubTZ(Connection conn, long uid, BigDecimal rmb, BigDecimal sy, long time) throws SQLException;

	public int updatePwd(long uid, String Pwd, long time) throws SQLException;
	/** 月统计汇总 */
	public int updateMonth() throws SQLException;
	// ==================== API 接口层 ====================
	public AjaxInfo findUserByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public UserInfo findUserByUid(long uid) throws SQLException;

	public void updatePwd(long uid, String Pwd) throws SQLException;
}
