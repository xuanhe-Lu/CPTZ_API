package com.ypm.cfo;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.commons.lang.StringUtils;
import com.ypm.Action;
import com.ypm.bean.*;
import com.ypm.service.BankInfoService;
import com.ypm.service.CfoCompanyService;
import com.ypm.util.*;

/**
 * Update by xk on 2018-07-13.
 * 
 * 企业打款 Action.
 */
public class ComAtMoney extends Action {

	private static final long serialVersionUID = 8323185045847718087L;
	
	private static final Logger LOGGER = Logger.getLogger(ComAtMoney.class);

	private BankInfoService bankInfoService;

	private CfoCompanyService cfoCompanyService;

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

	public String gather() {
		AjaxInfo json = this.getAjaxInfo();
		StringBuilder sb = new StringBuilder();
		
		try {
			StringBuilder sql = new StringBuilder();
			List<Object> fs = new ArrayList<Object>();
			int state = this.getInt( "State", -1 );
			if (state >= SALE_A3) {
				Map<String, String> ms = this.getDictBySid(VeKey.ASSET_RAW_STATE);
				sb.append('【').append(ms.get(String.valueOf(state))).append('】');
				sql.append( " AND State = ?" );
				fs.add(state);
			} else {
				sql.append( " AND State >= ?" );
				fs.add(SALE_A3); // 锁定
			} 
			// 查询条件信息
			String key = this.getString( "key" );
			if (key != null) {
				int len = key.length();
				int pos = (len - 1);
				if (key.charAt(0) == '-') {
					int day = VeRule.toMinDay(key.substring(1));
					if (len == 9) {
						sql.append( " AND BH = ?" );
					} else {
						sql.append( " AND BH <= ? AND BH >= ?" );
						fs.add(VeRule.toMaxDay(day, pos));
					}
					fs.add(day);
					sb.append("还款【").append(day).append('】');
				} else if (key.charAt(pos) == '-') {
					int day = VeRule.toMinDay(key.substring( 0, pos ));
					if (len == 9) {
						sql.append( " AND BI = ?" );
					} else {
						sql.append( " AND BI <= ? AND BI >= ?" );
						fs.add(VeRule.toMaxDay( day, pos ));
					}
					fs.add(day);
					sb.append("借款【").append(day).append('】');
				} else if (StringUtils.isNumber(key)) {
					if (len == 8) {
						sql.append( " AND BI = ?" );
						fs.add(Integer.parseInt(key));
						sb.append("借款【").append(key).append('】');
					} else if (len >= 4) {
						sql.append( " AND BI >= ?" );
						fs.add(VeRule.toMinDay(key));
						sb.append("借款【").append(key).append('】');
					} else {
						sql.append( " AND BJ = ?" );
						fs.add(Integer.parseInt(key));
						sb.append("理财天数【").append(key).append('】');
					}
				} else {
					sql.append( " AND CA LIKE ?" );
					sb.append("企业名【").append(key).append('】');
					fs.add('%' + key + '%');
				}
			} else if (sb.length() <= 0) {
				sb.append( "ALL" );
			}
			json.data(API_OK);
			json.append( "SKEY", sb.toString() );
			this.getCfoCompanyService().loadMoneyBySum( json, sql, fs );
		} catch (Exception e) {
			LOGGER.info( "Exception：" + e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		} finally {
			sb.setLength(0);
		}
		
		return JSON;
	}

	/**
	 * @return string
	 * 
	 * 企业打款信息预览
	 */
	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			long rid = this.getLong( "Rid" );
			ComRaws comRaws = this.getCfoCompanyService().findRawByRid(rid);
			if (comRaws == null) {
				json.addError(this.getText( "system.error.none" ));
			} else {
				ComProd p = this.getCfoCompanyService().findProdByRid(rid);
				if (p == null) {
					json.addError(this.getText( "system.error.pars" ));
				} else {
					Map<Integer, String> ms = this.getBankInfoService().getBankByAll();
					Map<String, String> mt = this.getDictBySid(VeKey.ASSET_RAW_OWNER);
					Map<String, String> be = this.getDictBySid(VeKey.ASSET_RAW_SAFE);
					json.data();
					json.append( "RID", rid );
					json.append( "PID", p.getPid() );
					json.append( "TID", mt.get(String.valueOf(comRaws.getTid())) );
					json.append( "NAME", p.getName() );
					json.append( "BA", comRaws.getBa() );
					json.append( "BB", VeRule.toRMB(comRaws.getBb(), 4) );
					json.append( "BC", GMTime.formatInt(comRaws.getBc()) );
					json.append( "BD", GMTime.formatInt(comRaws.getBd()) );
					json.append( "BE", be.get(String.valueOf(comRaws.getBe())) );
					json.append( "BF", comRaws.getBf() );
					json.append( "BG", comRaws.getBg() );
					json.append( "BH", GMTime.formatInt(comRaws.getBh()) );
					json.append( "BI", GMTime.formatInt(comRaws.getBi()) );
					json.append( "BJ", comRaws.getBj(), "天" );
					json.append( "BN", DF2.format(comRaws.getBn()), "%" );
					if (comRaws.getBo().compareTo(BigDecimal.ZERO) >= 1) {
						json.append( "BO", DF2.format(comRaws.getBo()), "%" );
					} else {
						json.append( "BO", "-" );
					}
					json.append( "BU", comRaws.getBu() );
					json.append( "CA", comRaws.getCa() );
					json.append( "CB", comRaws.getCb() );
					json.append( "CC", comRaws.getCc() );
					json.append( "CD", comRaws.getCd() );
					json.append( "CE", comRaws.getCe() );
					json.append( "CF", comRaws.getCf() );
					json.append( "CG", comRaws.getCg() );
					json.append( "CH", comRaws.getCh() );
					json.append( "CK", ms.get(comRaws.getCk()) );
					json.append( "MA", DF3.format(comRaws.getMa()) );
					json.append( "MB", DF3.format(comRaws.getMb()) );
					json.append( "MC", DF3.format(comRaws.getMc()) );
					json.append( "MD", DF3.format(comRaws.getMd()) );
					json.append( "ME", DF3.format(comRaws.getMe()) );
					json.append( "MF", DF3.format(comRaws.getMf()) );
					json.append( "MG", DF3.format(comRaws.getMg()) );
					json.append( "YMA", DF3.format(comRaws.getYma()) );
				}
			}
		} catch (Exception e) {
			LOGGER.info( "预览企业打款信息失败，异常信息：" + e.getMessage() );
			e.printStackTrace();
		}
		
		return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 企业打款列表信息
	 */
	public String list() {
		StringBuilder sb = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		
		int state = this.getInt( "State", -1 );
		if (state >= SALE_A3) {
			sql.append( " AND State = ?" );
			fs.add(state);
		} else {
			sql.append( " AND State >= ?" );
			fs.add(SALE_A3); // 锁定
		}
		String key = this.getString( "key" );
		if (key != null) {
			int len = key.length();
			int pos = (len - 1);
			if (key.charAt(0) == '-') {
				int day = VeRule.toMinDay(key.substring(1));
				if (len == 9) {
					sql.append( " AND BH = ?" );
				} else {
					sql.append( " AND BH <= ? AND BH >= ?" );
					fs.add(VeRule.toMaxDay( day, pos ));
				}
				fs.add(day);
			} else if (key.charAt(pos) == '-') {
				int day = VeRule.toMinDay(key.substring(0, pos));
				if (len == 9) {
					sql.append( " AND BI = ?" );
				} else {
					sql.append( " AND BI <= ? AND BI >= ?" );
					fs.add(VeRule.toMaxDay(day, pos));
				}
				fs.add(day);
			} else if (StringUtils.isNumber(key)) {
				if (len == 8) {
					sql.append( " AND BI = ?" );
					fs.add(Integer.parseInt(key));
				} else if (len >= 4) {
					sql.append( " AND BI >= ?" );
					fs.add(VeRule.toMinDay(key));
				} else {
					sql.append( " AND BJ = ?" );
					fs.add(Integer.parseInt(key));
				}
			} else {
				sql.append( " AND CA LIKE ?" );
				fs.add(sb.append('%').append(key).append('%').toString());
			}
		} 
		// 排序信息
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Rid")) {
			sb.append("Rid");
			sort = "Rid";
		} else {
			sb.append(sort);
		} 
		// 加载二次排序
		if (sort.equalsIgnoreCase( "Rid" )) {
			if (dir == null || dir.equalsIgnoreCase( "DESC" )) {
				sb.append( " DESC" );
			} else {
				sb.append( " ASC" );
			}
		} else if (dir == null || dir.equalsIgnoreCase( "DESC" )) {
			sb.append( " DESC, Rid DESC" );
		} else {
			sb.append( " ASC, Rid ASC" );
		} 
		// 加载数据信息
		this.setAjaxInfo(this.getCfoCompanyService().findMoneyByAll( sql, fs, sb.toString(), getStart(), getLimit() ));
		
		return JSON;
	}

	/**
	 * @return string
	 * 
	 * 新增/保存企业打款信息 
	 */
	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			long rid = this.getLong( "Rid" );
			ComRaws r = this.getCfoCompanyService().findRawByRid(rid);
			if (r == null) {
				json.addError(this.getText( "cfo.error.301" ));
			} else {
				int ds = r.getState();
				int state = this.getInt( "state" );
				if (SALE_A3 == state) {
					ds = SALE_A4; // 待回款
				} else if (r.getBh() > GState.USER_TOADD) {
					json.addError(this.getText( "cfo.error.304" ));
					return JSON;
				} else {
					ds = SALE_A5; // 已还款
				} // 处理成功
				if (ds <= r.getState()) {
					// Ignared
				} else {
					UserSession us = this.getUserSession();
					r.setState(ds); // 状态信息
					r.setAdm(us.getUserId());
					r.setAdn(us.getUserName());
					state = this.getCfoCompanyService().updatePay(r);
				}
				json.success(API_OK).append( "state", r.getState() );
			}
		} catch (Exception e) {
			LOGGER.info( "新增或保存企业打款信息失败，异常信息：" + e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "system.error.info" ));
		}
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return string
	 * 
	 * 企业打款导出
	 */
	public String export () {
		AjaxInfo json = this.getAjaxInfo();
		
		// like xlsx or xls
		String excelSuffix = this.getString( "suffix" );
		// if none, get -1
		int state = this.getInt( "state" );
		// if none, get null
		String key = this.getString( "key" );
		
		// 根据筛选条件查询记录数据
		StringBuilder sb = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		if (state >= SALE_A3) {
			sql.append( " AND State = ?" );
			fs.add(state);
		} else {
			sql.append( " AND State >= ?" );
			fs.add(SALE_A3);
		}
		if (key != null) {
			int len = key.length();
			int pos = (len - 1);
			if (key.charAt(0) == '-') {
				int day = VeRule.toMinDay(key.substring(1));
				if (len == 9) {
					sql.append( " AND BH = ?" );
				} else {
					sql.append( " AND BH <= ? AND BH >= ?" );
					fs.add(VeRule.toMaxDay(day, pos));
				}
				fs.add(day);
			} else if (key.charAt(pos) == '-') {
				int day = VeRule.toMinDay(key.substring(0, pos));
				if (len == 9) {
					sql.append( " AND BI = ?" );
				} else {
					sql.append( " AND BI <= ? AND BI >= ?" );
					fs.add(VeRule.toMaxDay(day, pos));
				}
				fs.add(day);
			} else if (StringUtils.isNumber(key)) {
				if (len == 8) {
					sql.append( " AND BI = ?" );
					fs.add(Integer.parseInt(key));
				} else if (len >= 4) {
					sql.append( " AND BI >=  ?" );
					fs.add(VeRule.toMinDay(key));
				} else {
					sql.append( " AND BJ = ?" );
					fs.add(Integer.parseInt(key));
				}
			} else {
				sql.append( " AND CA LIKE ?" );
				fs.add(sb.append('%').append(key).append('%').toString());
			}
		}
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase( "Rid" )) {
			sb.append( "Rid" );
			sort = "Rid";
		} else {
			sb.append(sort);
		} 
		if (sort.equalsIgnoreCase( "Rid" )) {
			if (dir == null || dir.equalsIgnoreCase( "DESC" )) {
				sb.append( " DESC" );
			} else {
				sb.append( " ASC" );
			}
		} else if (dir == null || dir.equalsIgnoreCase( "DESC" )) {
			sb.append( " DESC, Rid DESC" );
		} else {
			sb.append( " ASC, Rid ASC" );
		} 
		
		// 生成文件并导出
		if (StringUtils.isEmpty(excelSuffix)) {
			json.addError( "system.export.nosuffix" );
			return JSON;
		}
		String fileName = null;
		try {
			fileName = this.getCfoCompanyService().export( sql, fs, sb.toString(), excelSuffix );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (StringUtils.isEmpty(fileName)) {
			json.addError( "system.export.fail" );
		} else {
			json.success(API_OK).append( "fname", fileName );
		}
		
		return JSON;
	}
}
