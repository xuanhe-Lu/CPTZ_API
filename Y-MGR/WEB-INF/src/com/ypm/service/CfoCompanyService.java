package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.ComProd;
import com.ypm.bean.ComRaws;
import com.ypm.bean.FieldInfo;
import com.ypm.bean.RawInfo;

public interface CfoCompanyService {

	// ==================== APS 接口层 ====================
	public int update(ComRaws r) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findMoneyByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo loadMoneyBySum(AjaxInfo json, StringBuilder sql, List<Object> fs) throws SQLException;

	public ComRaws findRawByRid(long rid) throws SQLException;

	public ComProd findProdByRid(long rid) throws SQLException;

	public int updatePay(ComRaws r) throws SQLException;
	
	public String export(StringBuilder sql, List<Object> fs, String order, String excelSuffix) throws SQLException;

	/**
	 * @author xk
	 * @param excelSuffix string 导出文件后缀
	 * @param datas List<RawInfo> 打款数据
	 * @param exportFields List<String> 导出字段信息
	 * 
	 * 企业打款导出,只返回生成的excel文件名
	 */
	public String export(String excelSuffix, List<RawInfo> datas, List<FieldInfo> exportFields);
	
	/**
	 * @author xk
	 * @param sql StringBuilder sql语句
	 * @param objs List<Object> sql参数
	 * @param order String 排序
	 * 
	 * 根据筛选条件查询企业打款数据,会过滤掉配置中不导出的字段
	 */
	public List<RawInfo> findForExport(StringBuilder sql, List<Object> objs, String order);
}
