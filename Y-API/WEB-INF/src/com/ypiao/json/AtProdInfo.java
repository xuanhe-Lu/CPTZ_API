package com.ypiao.json;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;

import com.ypiao.service.UserVipService;
import org.apache.log4j.Logger;

import com.ypiao.bean.*;
import com.ypiao.service.AssetRawService;
import com.ypiao.service.ProdInfoService;
import com.ypiao.util.GMTime;
import com.ypiao.util.VeRule;

/**
 * 标的信息接口. 
 */
public class AtProdInfo extends Action {

	private static final long serialVersionUID = 5166475064331058592L;
	
	private static final Logger LOGGER = Logger.getLogger(AtProdInfo.class);

	private AssetRawService assetRawService;

	private ProdInfoService prodInfoService;

	private UserVipService userVipService;

	public AtProdInfo() {
		super(true);
	}

	public AssetRawService getAssetRawService() {
		return assetRawService;
	}

	public void setAssetRawService(AssetRawService assetRawService) {
		this.assetRawService = assetRawService;
	}

	public ProdInfoService getProdInfoService() {
		return prodInfoService;
	}

	public void setProdInfoService(ProdInfoService prodInfoService) {
		this.prodInfoService = prodInfoService;
	}

	/**
	 * @return string
	 * 
	 * 标的列表数据 
	 */
	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			this.getProdInfoService().sendList(json);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		return JSON;
	}

	/**
	 * @return string
	 * 
	 * 在售产品列表
	 */
	public String sale() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			this.getProdInfoService().sendSale(json);
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			json.addError(this.getText("system.error.get"));
		}
		return JSON;
	}
	
	/**
	 * @return string
	 * 
	 * 标的详情 
	 */
	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			long Pid = this.getLong( "pid" );
			ProdInfo info = this.getProdInfoService().findProdByPid(Pid);
			// 未找到标的信息
			if (info == null) {
				json.addError(this.getText( "system.error.info" ));
			} else {
				json.addObject();
				// luxh 根据uid获取用户VIP等级和会员权益日，判断是否可以使用两张加息券
				UserSession us = this.getUserSession();
				int status = 0;//0为不允许，1为允许
				BigDecimal vipRate = new BigDecimal(1.00);
				if(us.getUid()== 0){
					logger.info("用户未登录,无法查询用户VIP等级");
				}else {
					long nowTime = System.currentTimeMillis();
					UserVip  userVip = this.getUserVipService().queryVipLog(us.getUid(), nowTime);
					if(userVip!=null && userVip.getUid() ==us.getUid()){
						vipRate = new BigDecimal(1.05);
						logger.info(String.format("该用户有会员信息，会员等级为[%s]",userVip.getName()));
						Calendar cal=Calendar.getInstance();
						int d = cal.get(Calendar.DATE);
						if(userVip.getLevel()>2  ){
							vipRate = new BigDecimal(1.10);
							if(d == userVip.getMemberBenefits()) {
								logger.info("该会员享受会员权益日福利");
								status = 1;
							}
						}
					}
				}


				json.formater();
//				json.append("vipstatus",status);

				json.append("vipRate",vipRate);
				json.append("vipstatus",1);

				//end luxh
				json.append( "pid", info.getPid() );
				json.append( "rid", info.getRid() );
				json.append( "cid", info.getCid() );
				json.append( "tid", info.getTid() );
				json.append( "aa", info.getAa() );
				json.append( "ae", info.getAe() );
				json.append( "ah", info.getAh() );
				json.append( "ai", info.getAi() );
				json.append( "aj", info.getAj() );
				json.append( "ak", GMTime.getTday(info.getGmtB(), GMTime.CHINA, 0) );
				json.append( "an", DF2.format(info.getAn()) );
				json.append( "au", info.getAu() );
				json.append( "ads", DF2.format(info.getAds()) ); // 加息
				RawInfo r = this.getAssetRawService().findRawByRid(info.getRid());
				if (r != null) {
					json.append( "bf", r.getBf() );
					json.append( "bg", r.getBg() );
					json.append( "bu", r.getBu() );
					json.append( "ca", VeRule.toStar(r.getCa(), 2, 4, 3, "-") );
					json.append( "cb", VeRule.toStar(r.getCb(), 4, 0, 3, "-") );
					json.append( "cd", VeRule.toStar(r.getCd(), 6, 0, 3, "-") );
					json.append( "ce", VeRule.toStar(r.getCe(), 1, 0, 3, "-") );
				} 
				// 计算加载相关额度
				json.append( "ma", DF2.format(info.getMa()) );
				if (info.getAu() == SALE_A1) {
					//UserSession us = this.getUserSession();
					if (us.isLogin()) {
						// 登录计算用户可购买金额
					} else {
						// 未登录计算用户可购买金额
					}
					BigDecimal me = info.getMa().subtract(info.getMd());
					if (info.getMb().compareTo(BigDecimal.ZERO) >= 1) {
						if (info.getMb().compareTo(me) >= 1) {
							json.append( "mb", DF2.format(me) );
						} else {
							json.append( "mb", DF2.format(info.getMb()) );
						}
					} else {
						json.append( "mb", DF2.format(me) );
					}
					json.append( "mc", DF2.format(info.getMc()) );
					json.append( "md", DF2.format(info.getMd()) );
					json.append( "me", DF2.format(me) ); // 剩余额度
					json.append( "rate", VeRule.toPers(info.getMa(), info.getMd()).intValue() );
//					json.append( "rate", VeRule.toPers(info.getMa(), info.getMd()).doubleValue() );
				} else {
					json.append( "mb", 0 ); // 限额信息
					json.append( "mc", DF2.format(info.getMc()) );
					json.append( "md", DF2.format(info.getMa()) );
					json.append( "me", 0 ); // 剩余额度
					json.append( "rate", 100 );
				} 
				// 加载票据信息
				json.close();
				json.adds( "imgs" );
				if (r != null) {
					json.append( "pid", r.getBu() );
					json.append( "name", "Raw" );
				} 
				// 加载相关合同
				json.formater();
				json.append( "pid", "20180111K3Z8YM2" );
				json.append( "name", "CB-1" );
				json.formater();
				json.append( "pid", "20180111K3Z8YM3" );
				json.append( "name", "CB-2" );
				this.getAssetRawService().loadRawByRid(json, info.getRid());


			}
		} catch (Exception e) {
			LOGGER.info( "加载标的详情发生异常，异常信息：" + e.getMessage() );
			json.addError(this.getText( "system.error.get" ));
		}
		
		return JSON;
	}

	/**
	 * @return string
	 * 
	 * 根据状态加载产品列表 
	 */
	public String list() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			int state = this.getInt( "state" );
			if (SALE_A4 > state) {
				this.getProdInfoService().sendList( json, SALE_A3 );
			} else {
				this.getProdInfoService().sendList( json, SALE_A5 );
			}
		} catch (Exception e) {
			LOGGER.info( "根据状态加载产品列表发生异常，异常信息：" + e.getMessage() );
			json.addError(this.getText( "system.error.get" ));
		}
		
		return JSON;
	}

	public UserVipService getUserVipService() {
		return userVipService;
	}

	public void setUserVipService(UserVipService userVipService) {
		this.userVipService = userVipService;
	}
}
