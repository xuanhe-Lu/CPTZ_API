package com.ypm.service.imp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ypm.bean.AjaxInfo;
import com.ypm.bean.ComProd;
import com.ypm.bean.ComRaws;
import com.ypm.bean.FieldInfo;
import com.ypm.bean.RawInfo;
import com.ypm.bean.SyncMap;
import com.ypm.data.JPrepare;
import com.ypm.service.BankInfoService;
import com.ypm.service.CfoCompanyService;
import com.ypm.util.Constant;
import com.ypm.util.ExportUtils;
import com.ypm.util.GMTime;
import com.ypm.util.Table;

public class CfoCompanyServiceImp extends AConfig implements CfoCompanyService {

	private BankInfoService bankInfoService;
	
	private CfoCompanyService cfoCompanyService;

	protected void checkSQL() {
	}

	public BankInfoService getBankInfoService() {
		return bankInfoService;
	}

	public void setBankInfoService(BankInfoService bankInfoService) {
		this.bankInfoService = bankInfoService;
	}
	
	public CfoCompanyService getCfoCompanyService() {
		return cfoCompanyService;
	}

	public void setCfoCompanyService(CfoCompanyService cfoCompanyService) {
		this.cfoCompanyService = cfoCompanyService;
	}

	public int update(ComRaws r) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + Table.TBL_ASSET_RAWS + " SET State=?,Time=?,adM=?,adN=? WHERE Rid=?");
			ps.setInt(1, r.getState());
			ps.setLong(2, r.getTime());
			ps.setLong(3, r.getAdm());
			ps.setString(4, r.getAdn());
			ps.setLong(5, r.getRid());
			int num = ps.executeUpdate();
			if (num >= 1) {
				if (SALE_A5 == r.getState()) {
					ps.close(); // 下级更新
					ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET AU=?,Time=? WHERE Rid=?");
					ps.setInt(1, r.getState());
					ps.setLong(2, r.getTime());
					ps.setLong(3, r.getRid());
					ps.executeUpdate();
				}
			}
			return num;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findMoneyByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + Table.TBL_ASSET_RAWS).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, Table.TBL_ASSET_RAWS, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			}
			float r = 0;
			long adm = 0;
			Map<String, String> ms = this.getDictInfoBySSid(ASSET_RAW_STATE);
			Map<Integer, String> bks = this.getBankInfoService().getBankByAll();
			sql.insert(0, "SELECT Rid,Tid,BA,BB,BC,BD,BE,BF,BG,BH,BI,BJ,BN,BO,CA,CE,CF,CG,CH,CK,MA,MB,MC,MD,ME,MF,MG,YMA,State,Time,adM,adN ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql.toString(), offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("RID", rs.getLong(1));
				json.append("TID", rs.getLong(2));
				json.append("BA", rs.getString(3));
				json.append("BB", rs.getString(4));
				json.append("BC", GMTime.formatInt(rs.getString(5)));
				json.append("BD", GMTime.formatInt(rs.getString(6)));
				json.append("BE", rs.getString(7));
				json.append("BF", rs.getString(8));
				json.append("BG", rs.getString(9));
				json.append("BH", GMTime.formatInt(rs.getString(10)));
				json.append("BI", GMTime.formatInt(rs.getString(11)));
				json.append("BJ", rs.getInt(12));
				json.append("BN", DF2.format(rs.getFloat(13)), "%");
				if ((r = rs.getFloat(14)) > 0) {
					json.append("BO", DF2.format(r), "%");
				} else {
					json.append("BO", "-");
				}
				json.append("CA", rs.getString(15));
				// json.append("CE", rs.getString(16));
				// json.append("CF", rs.getString(17));
				json.append("CG", bks.get(rs.getInt(20)));
				json.append("CH", rs.getString(19));
				json.append("MA", DF3.format(rs.getDouble(21)));
				json.append("MB", DF3.format(rs.getDouble(22)));
				json.append("MC", DF3.format(rs.getDouble(23)));
				json.append("MD", DF3.format(rs.getDouble(24)));
				json.append("ME", DF3.format(rs.getDouble(25)));
				json.append("MF", DF3.format(rs.getDouble(26)));
				json.append("MG", DF3.format(rs.getDouble(27)));
				json.append("YMA", DF3.format(rs.getDouble(28)));
				json.append("STATE", ms.get(rs.getString(29)));
				json.append("STATS", rs.getInt(29));
				json.append("TIME", GMTime.format(rs.getLong(30), GMTime.CHINA));
				if ((adm = rs.getLong(31)) >= USER_IDS) {
					json.append("ADM", adm);
					json.append("ADN", rs.getString(32));
				} else {
					json.append("ADM", "");
					json.appends("ADN", "-");
				}
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo loadMoneyBySum(AjaxInfo json, StringBuilder sql, List<Object> fs) throws SQLException {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			sql.insert(0, "SELECT SUM(MD),SUM(ME),SUM(MF),SUM(MG),SUM(YMA) FROM " + Table.TBL_ASSET_RAWS);
			ps = conn.prepareStatement(sql.toString());
			for (Object obj : fs) {
				ps.setObject(index++, obj);
			}
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				json.append("TMD", DF3.format(rs.getDouble(1)), "元");
				json.append("TME", DF3.format(rs.getDouble(2)), "元");
				json.append("TMF", DF3.format(rs.getDouble(3)), "元");
				json.append("TMG", DF3.format(rs.getDouble(4)), "元");
				json.append("YMA", DF3.format(rs.getDouble(5)), "元");
			} else {
				json.append("TMD", "-");
				json.append("TME", "-");
				json.append("TMF", "-");
				json.append("TMG", "-");
				json.append("YMA", "-");
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public ComRaws findRawByRid(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ComRaws r = null;
			ps = conn.prepareStatement("SELECT Rid,Cid,Tid,Code,BA,BB,BC,BD,BE,BF,BG,BH,BI,BJ,BM,BN,BO,BU,BV,CA,CB,CC,CD,CE,CF,CG,CH,CK,MA,MB,MC,MD,ME,MF,MG,YMA,Total,State,Time,adM,adN FROM " + Table.TBL_ASSET_RAWS + " WHERE Rid=?");
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				r = new ComRaws();
				r.setRid(rs.getLong(1));
				r.setCid(rs.getInt(2));
				r.setTid(rs.getInt(3));
				r.setCode(rs.getString(4));
				r.setBa(rs.getString(5));
				r.setBb(rs.getBigDecimal(6));
				r.setBc(rs.getInt(7));
				r.setBd(rs.getInt(8));
				r.setBe(rs.getInt(9));
				r.setBf(rs.getString(10));
				r.setBg(rs.getString(11));
				r.setBh(rs.getInt(12));
				r.setBi(rs.getInt(13));
				r.setBj(rs.getInt(14));
				r.setBm(rs.getBigDecimal(15));
				r.setBn(rs.getBigDecimal(16));
				r.setBo(rs.getBigDecimal(17));
				r.setBu(rs.getString(18));
				r.setBv(rs.getInt(19));
				r.setCa(rs.getString(20));
				r.setCb(rs.getString(21));
				r.setCc(rs.getString(22));
				r.setCd(rs.getString(23));
				r.setCe(rs.getString(24));
				r.setCf(rs.getString(25));
				r.setCg(rs.getString(26));
				r.setCh(rs.getString(27));
				r.setCk(rs.getInt(28));
				r.setMa(rs.getBigDecimal(29));
				r.setMb(rs.getBigDecimal(30));
				r.setMc(rs.getBigDecimal(31));
				r.setMd(rs.getBigDecimal(32));
				r.setMe(rs.getBigDecimal(33));
				r.setMf(rs.getBigDecimal(34));
				r.setMg(rs.getBigDecimal(35));
				r.setYma(rs.getBigDecimal(36));
				r.setTotal(rs.getInt(37));
				r.setState(rs.getInt(38));
				r.setTime(rs.getLong(39));
				r.setAdm(rs.getLong(40));
				r.setAdn(rs.getString(41));
			}
			rs.close();
			return r;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public ComProd findProdByRid(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ComProd c = null;
			ps = conn.prepareStatement("SELECT Pid,Rid,AA,State,Time FROM " + Table.TBL_PROD_INFO + " WHERE Rid=? ORDER BY Pid ASC");
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				c = new ComProd();
				c.setPid(rs.getLong(1));
				c.setRid(rs.getLong(2));
				c.setName(rs.getString(3));
			}
			rs.close();
			return c;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int updatePay(ComRaws r) throws SQLException {
		r.setTime(System.currentTimeMillis());
		if (this.update(r) >= 1) {
			SyncMap.getAll().sender(SYS_A852, "money", r);
		}
		return r.getState();
	}

	/**
	 * @author xk
	 * @param excelSuffix string 导出文件后缀
	 * @param datas List<RawInfo> 打款数据
	 * @param exportFields List<String> 导出字段信息
	 * 
	 * 企业打款导出,只返回生成的excel文件名
	 */
	public String export(String excelSuffix, List<RawInfo> datas, List<FieldInfo> exportFields) {
		// 表格头部数据和导出字段名集合
		List<String> headers = new ArrayList<>(), fields = new ArrayList<>();
		for (FieldInfo f : exportFields) {
			headers.add(f.getNice());
			fields.add(f.getName());
		}
		
		// 将数据装载到指定格式的excel文件中，并生成文件到系统总导出目录下
		SimpleDateFormat sdf2 = new SimpleDateFormat( "yyyyMMddHHmmss" );
		Date curDate = new Date();
		StringBuilder sb = new StringBuilder(Constant.ROOTPATH);
		sb.append(Constant.EXPORTS).append(File.separator).append(sdf2.format(curDate)).append( '.' ).append(excelSuffix);
		String filePath = sb.toString();

        File file = new File(filePath);
        File fP = file.getParentFile();
		if (!fP.exists()) {
			fP.mkdirs();
		} 
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Workbook workbook = null;
        if (excelSuffix.equals( "xls" )) { // 03版
        	workbook = new HSSFWorkbook();
        } else if (excelSuffix.endsWith( "xlsx" )) { // 07版
        	workbook = new XSSFWorkbook();
        }
        Sheet sheet = workbook.createSheet( "企业打款数据" );
        
        // 创建表头行
        Row headRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
        	headRow.createCell(i).setCellValue(headers.get(i));
        }
        headRow.setHeightInPoints(30);

        // 循环创建数据行
        Row row = null;
        int i = 1, cell = 0;
        for (RawInfo rawInfo : datas) {
        	if (i > datas.size()) {
        		break;
        	} else {
        		row = sheet.createRow(i);
        		if (fields.contains("Rid")) {
        			row.createCell(0).setCellValue(String.valueOf(rawInfo.getRid()));
        			cell++;
        		}
        		if (fields.contains("Cid")) {
        			row.createCell(cell).setCellValue(rawInfo.getCid());
        			cell++;
        		}
        		if (fields.contains("Tid")) {
        			row.createCell(cell).setCellValue(rawInfo.getTid());
        			cell++;
        		}
        		if (fields.contains("Code")) {
        			row.createCell(cell).setCellValue(rawInfo.getCode());
        			cell++;
        		}
        		if (fields.contains("BA")) {
        			row.createCell(cell).setCellValue(rawInfo.getBa());
        			cell++;
        		}
        		if (fields.contains("BB")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getBb()));
        			cell++;
        		}
        		if (fields.contains("BC")) {
        			row.createCell(cell).setCellValue(rawInfo.getBc());
        			cell++;
        		}
        		if (fields.contains("BD")) {
        			row.createCell(cell).setCellValue(rawInfo.getBd());
        			cell++;
        		}
        		if (fields.contains("BE")) {
        			row.createCell(cell).setCellValue(rawInfo.getBe() == 1 ? "是" : "否");
        			cell++;
        		}
        		if (fields.contains("BF")) {
        			row.createCell(cell).setCellValue(rawInfo.getBf());
        			cell++;
        		}
        		if (fields.contains("BG")) {
        			row.createCell(cell).setCellValue(rawInfo.getBg());
        			cell++;
        		}
        		if (fields.contains("BH")) {
        			row.createCell(cell).setCellValue(rawInfo.getBh());
        			cell++;
        		}
        		if (fields.contains("BI")) {
        			row.createCell(cell).setCellValue(rawInfo.getBi());
        			cell++;
        		}
        		if (fields.contains("BJ")) {
        			row.createCell(cell).setCellValue(rawInfo.getBj());
        			cell++;
        		}
        		if (fields.contains("BM")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getBm()));
        			cell++;
        		}
        		// 年化
        		if (fields.contains("BN")) {
        			row.createCell(cell).setCellValue( String.valueOf(rawInfo.getBn().setScale(2)) + "%" );
        			cell++;
        		}
        		// 管理
        		if (fields.contains("BO")) {
        			row.createCell(cell).setCellValue( "-" );
        			cell++;
        		}
        		if (fields.contains("BU")) {
        			row.createCell(cell).setCellValue(rawInfo.getBu());
        			cell++;
        		}
        		if (fields.contains("BV")) {
        			row.createCell(cell).setCellValue(rawInfo.getBv());
        			cell++;
        		}
        		if (fields.contains("CA")) {
        			row.createCell(cell).setCellValue(rawInfo.getCa());
        			cell++;
        		}
        		if (fields.contains("CB")) {
        			row.createCell(cell).setCellValue(rawInfo.getCb());
        			cell++;
        		}
        		if (fields.contains("CC")) {
        			row.createCell(cell).setCellValue(rawInfo.getCc());
        			cell++;
        		}
        		if (fields.contains("CD")) {
        			row.createCell(cell).setCellValue(rawInfo.getCd());
        			cell++;
        		}
        		if (fields.contains("CE")) {
        			row.createCell(cell).setCellValue(rawInfo.getCe());
        			cell++;
        		}
        		if (fields.contains("CF")) {
        			row.createCell(cell).setCellValue(rawInfo.getCf());
        			cell++;
        		}
        		// 企业开户行
        		if (fields.contains("CG")) {
        			row.createCell(cell).setCellValue(rawInfo.getCkStr());
        			cell++;
        		}
        		if (fields.contains("CH")) {
        			row.createCell(cell).setCellValue(rawInfo.getCh());
        			cell++;
        		}
        		// 银行选项
        		if (fields.contains("CK")) {
        			row.createCell(cell).setCellValue(rawInfo.getCkStr());
        			cell++;
        		}
        		if (fields.contains("MA")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getMa()));
        			cell++;
        		}
        		if (fields.contains("MB")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getMb()));
        			cell++;
        		}
        		if (fields.contains("MC")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getMc()));
        			cell++;
        		}
        		if (fields.contains("MD")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getMd()));
        			cell++;
        		}
        		if (fields.contains("ME")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getMe()));
        			cell++;
        		}
        		if (fields.contains("MF")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getMf()));
        			cell++;
        		}
        		if (fields.contains("MG")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getMg()));
        			cell++;
        		}
        		if (fields.contains("YMA")) {
        			row.createCell(cell).setCellValue(String.valueOf(rawInfo.getYma()));
        			cell++;
        		}
        		if (fields.contains("Total")) {
        			row.createCell(cell).setCellValue(rawInfo.getTotal());
        			cell++;
        		}
        		if (fields.contains("State")) {
        			row.createCell(cell).setCellValue(rawInfo.getState() == 3 ? "待打款" : rawInfo.getState() == 4 ? "待回款" : rawInfo.getState() == 5 ? "已完成" : "");
        			cell++;
        		}
        		if (fields.contains("Time")) {
        			row.createCell(cell).setCellValue(GMTime.format( rawInfo.getTime(), GMTime.CHINA ));
        			cell++;
        		}
        		if (fields.contains("adM")) {
        			row.createCell(cell).setCellValue(rawInfo.getAdM() == 0 ? "" : String.valueOf(rawInfo.getAdM()));
        			cell++;
        		}
        		if (fields.contains("adN")) {
        			row.createCell(cell).setCellValue(rawInfo.getAdN());
        			cell++;
        		}
        	}
        	i++;
        	cell = 0;
        }

        // 设置第一个sheet为选中状态
        workbook.setActiveSheet(0);
        try {
            workbook.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

		return file == null ? null : file.getName();
	}

	/**
	 * @author xk
	 * @param sql StringBuilder sql语句
	 * @param objs List<Object> sql参数
	 * @param order String 排序
	 * 
	 * 根据筛选条件查询企业打款数据,会过滤掉配置中不导出的字段
	 */
	public List<RawInfo> findForExport(StringBuilder sql, List<Object> objs, String order) {
		List<RawInfo> list = new ArrayList<>();
		if (objs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} 
		objs.add( 0, sql.insert( 0, "FROM " + Table.TBL_ASSET_RAWS).toString() );
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = JPrepare.getConnection();
			sql.insert( 0, "SELECT Rid,Cid,Tid,Code,BA,BB,BC,BD,BE,BF,BG,BH,BI,BJ,BM,BN,BO,BU,BV,CA,CB,CC,CD,CE,CF,CG,CH,(SELECT Nice FROM comm_bank WHERE Bid = CK) AS ckStr,MA,MB,MC,MD,ME,MF,MG,YMA,Total,State,Time,adM,adN " ).append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(sql.toString());
			for (int i = 1, j = objs.size(); i < j; i++) {
				ps.setObject( i, objs.remove(1) );
			} 
			ResultSet rs = ps.executeQuery();
			RawInfo rawInfo = null;
			while (rs.next()) {
				rawInfo = new RawInfo();
				rawInfo.setRid(rs.getLong(1));
				rawInfo.setCid(rs.getInt(2));
				rawInfo.setTid(rs.getInt(3));
				rawInfo.setCode(rs.getString(4));
				rawInfo.setBa(rs.getString(5));
				rawInfo.setBb(rs.getBigDecimal(6));
				rawInfo.setBc(rs.getInt(7));
				rawInfo.setBd(rs.getInt(8));
				rawInfo.setBe(rs.getInt(9));
				rawInfo.setBf(rs.getString(10));
				rawInfo.setBg(rs.getString(11));
				rawInfo.setBh(rs.getInt(12));
				rawInfo.setBi(rs.getInt(13));
				rawInfo.setBj(rs.getInt(14));
				rawInfo.setBm(rs.getBigDecimal(15));
				rawInfo.setBn(rs.getBigDecimal(16));
				rawInfo.setBo(rs.getBigDecimal(17));
				rawInfo.setBu(rs.getString(18));
				rawInfo.setBv(rs.getInt(19));
				rawInfo.setCa(rs.getString(20));
				rawInfo.setCb(rs.getString(21));
				rawInfo.setCc(rs.getString(22));
				rawInfo.setCd(rs.getString(23));
				rawInfo.setCe(rs.getString(24));
				rawInfo.setCf(rs.getString(25));
				rawInfo.setCg(rs.getString(26));
				rawInfo.setCh(rs.getString(27));
				rawInfo.setCkStr(rs.getString(28));
				rawInfo.setMa(rs.getBigDecimal(29));
				rawInfo.setMb(rs.getBigDecimal(30));
				rawInfo.setMc(rs.getBigDecimal(31));
				rawInfo.setMd(rs.getBigDecimal(32));
				rawInfo.setMe(rs.getBigDecimal(33));
				rawInfo.setMf(rs.getBigDecimal(34));
				rawInfo.setMg(rs.getBigDecimal(35));
				rawInfo.setYma(rs.getBigDecimal(36));
				rawInfo.setTotal(rs.getInt(37));
				rawInfo.setState(rs.getInt(38));
				rawInfo.setTime(rs.getLong(39));
				rawInfo.setAdM(rs.getLong(40));
				rawInfo.setAdN(rs.getString(41));
				list.add(rawInfo);
				rawInfo = null;
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JPrepare.close( ps, conn );
		}
		
		return list;
	}

	/**
	 * 企业打款数据导出 
	 */
	public String export(StringBuilder sql, List<Object> fs, String order, String excelSuffix) throws SQLException {
		List<FieldInfo> exportFields = ExportUtils.getExportFields( "cfo.company.money" );
		List<RawInfo> datas = this.findForExport( sql, fs, order );
		
		return this.export( excelSuffix, datas, exportFields );
	}
}
