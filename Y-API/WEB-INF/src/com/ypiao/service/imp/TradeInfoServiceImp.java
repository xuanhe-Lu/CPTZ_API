package com.ypiao.service.imp;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.ActivityService;
import com.ypiao.service.ProdInfoService;
import com.ypiao.service.TradeInfoService;
import com.ypiao.service.UserCouponService;
import com.ypiao.service.UserInfoService;
import com.ypiao.service.UserMoneyService;
import com.ypiao.service.UserOrderService;
import com.ypiao.util.APState;
import com.ypiao.util.GMTime;
import com.ypiao.util.Table;
import com.ypiao.util.VeStr;
import org.apache.log4j.Logger;

/**
 * 交易信息接口实现类. 
 */
public class TradeInfoServiceImp extends AConfig implements TradeInfoService {
	private static Logger logger = Logger.getLogger(TradeInfoServiceImp.class);
	private ActivityService activityService;

	private ProdInfoService prodInfoService;

	private UserCouponService userCouponService;

	private UserInfoService userInfoService;

	private UserMoneyService userMoneyService;

	private UserOrderService userOrderService;
	//private static final String TBL_USER_STATUS = "user_status";

	protected void checkSQL() {
	}

	public ActivityService getActivityService() {
		return activityService;
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	public ProdInfoService getProdInfoService() {
		return prodInfoService;
	}

	public void setProdInfoService(ProdInfoService prodInfoService) {
		this.prodInfoService = prodInfoService;
	}

	public UserCouponService getUserCouponService() {
		return userCouponService;
	}

	public void setUserCouponService(UserCouponService userCouponService) {
		this.userCouponService = userCouponService;
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public UserMoneyService getUserMoneyService() {
		return userMoneyService;
	}

	public void setUserMoneyService(UserMoneyService userMoneyService) {
		this.userMoneyService = userMoneyService;
	}

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
	}

	public void save(LogOrder log) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, log.getUid());
			if (this.getUserOrderService().adds(conn, log) >= 1) {
				this.getProdInfoService().update(conn, log.getPid(), log.getTma());
				this.getUserInfoService().updateSubTZ(conn, log.getUid(), log.getTmb(), log.getYma(), log.getTime());
				r.setSid(log.getSid());
				r.setFid(0);
				r.setTid(APState.TRADE_OUT4); // 投资
				r.setWay(APState.EVENT_P014);
				r.setEvent("购买" + log.getName());
				r.sub(log.getTmb());
				r.setTime(log.getTime());
				this.getUserMoneyService().save(conn, r);
			}
			if (log.getCid() >= USER_UID_MAX) {
				this.getUserCouponService().update(conn, log.getCid(), log.getUid(), log.getSid(), log.getTime());
			}
		} finally {
			JPrepare.close(conn);
		}
	}

	public void save(SysOrder s) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + Table.TBL_SYS_ORDER + " SET State=?,Stext=?,Time=? WHERE Sid=?");
			ps.setInt(1, s.getState());
			ps.setString(2, s.getStext());
			ps.setLong(3, s.getTime());
			ps.setLong(4, s.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
//				String sql = "INSERT INTO sys_order (Sid,Uid,Pid,Cid,Tid,Name,Rate,Rday,Day,Way,Ads,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,GmtD,Mobile,Nicer,State,Stext,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//				if(s.getCid1() != 0 ){
				String	sql = "INSERT INTO sys_order (Sid,Uid,Pid,Cid,Tid,Name,Rate,Rday,Day,Way,Ads,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,GmtD,Mobile,Nicer,State,Stext,Time,cid1) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//				}
				ps = conn.prepareStatement(sql);
				ps.setLong(1, s.getSid());
				ps.setLong(2, s.getUid());
				ps.setLong(3, s.getPid());
				ps.setLong(4, s.getCid());
				ps.setInt(5, s.getTid());
				ps.setString(6, s.getName());
				ps.setBigDecimal(7, s.getRate());
				ps.setInt(8, s.getRday());
				ps.setInt(9, s.getDay());
				ps.setString(10, s.getWay());
				ps.setBigDecimal(11, s.getAds());
				ps.setBigDecimal(12, s.getTma());
				ps.setBigDecimal(13, s.getTmb());
				ps.setBigDecimal(14, s.getTmc());
				ps.setBigDecimal(15, s.getTmd());
				ps.setBigDecimal(16, s.getTme());
				ps.setBigDecimal(17, s.getTmf());
				ps.setBigDecimal(18, s.getTmg());
				ps.setBigDecimal(19, s.getYma());
				ps.setBigDecimal(20, s.getYmb());
				ps.setLong(21, s.getGmtA());
				ps.setLong(22, s.getGmtB());
				ps.setLong(23, s.getGmtC());
				ps.setLong(24, s.getGmtD());
				ps.setString(25, s.getMobile());
				ps.setString(26, s.getNicer());
				ps.setInt(27, s.getState());
				ps.setString(28, s.getStext());
				ps.setLong(29, s.getTime());
//				if(s.getCid1() != 0 ){
					ps.setLong(30, s.getCid1());
//				}
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int commit(ProdInfo info, LogOrder log, UserCoupon uc) throws SQLException {
		logger.info("come in commit");
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		//ResultSet rs = null;
		
		try {
			int result = STATE_READER;
			conn.setAutoCommit(false);
			if (this.getProdInfoService().update(conn, log.getPid(), log.getTma()) <= 0) {
				result = STATE_CHECK; // 产品售罄
			} else {
//				long time = System.currentTimeMillis();
				long time = System.currentTimeMillis();
				UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, log.getUid());
				if (log.getTmb().compareTo(r.getTotal()) >= 1) {
					// 余额不足
				} else if (this.getUserInfoService().updateSubTZ(conn, log.getUid(), log.getTmb(), log.getYma(), time) >= 1) {
					logger.info("余额充足");
					log.setGmtA(time);
					log.setGmtD(time);
					log.setTime(time);
					r.setSid(log.getSid());
					r.setFid(0);
					r.setTid(APState.TRADE_OUT4); // 投资
					r.setWay(APState.EVENT_P014);
					r.setEvent("购买" + log.getName());
					r.sub(log.getTmb());
					r.setTime(time);
					logger.info("time:"+time);
					this.getUserMoneyService().insert(conn, r);
					logger.info("更新优惠券1，"+log.getCid());
					if (log.getCid() >= USER_UID_MAX) {
						this.getUserCouponService().update(conn, log.getCid(), log.getUid(), log.getSid(), time);
					}
					//增加对会员权益日的叠加优惠券的使用
					logger.info("更新优惠券2，"+log.getCid1());
					if (log.getCid1() >= USER_UID_MAX) {
						this.getUserCouponService().update(conn, log.getCid1(), log.getUid(), log.getSid(), time);
					}
					log.setState(SALE_A1);
					this.getUserOrderService().adds(conn, log);
					result = STATE_NORMAL; // 购买成功
					
					/******************* xk add start ********************/
					// 判断购买用户是否受邀注册，且自注册日30day内，若其累计投资满10000元，赠送邀请Ta注册的用户100元红包【使用条件：起投金额≥5,000元；理财期限≥30天；有效期30天，名称邀请好友】
					//String sql = "SELECT Uid, (SELECT Rtime FROM " + TBL_USER_STATUS + " WHERE Uid = 100058 AND UPS > 1000) AS Rtime FROM " + TBL_USER_STATUS + " WHERE Uid = (SELECT UPS FROM " + TBL_USER_STATUS + " WHERE Uid = " + log.getUid() + " AND UPS > 1000)";
					//ps = conn.prepareStatement(sql);
					//rs = ps.executeQuery();
					// 若有上线
					//if (rs.next()) {
						//if (rs.getLong(1) >= USER_UID_BEG) {
							//Date regDate = (Date) new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).parse(GMTime.format(rs.getLong(2), GMTime.CHINA));
							/*if () {
								
							}*/
						//}
					//}
					/******************* xk add end ********************/
					//TODO 购买成功，是否有邀请人，计算是否累计1W，如果都满足，则邀请人和被邀请人均获得38元现金奖励。
					//先查询是否有邀请人。注册绑卡后总共投资了多少，是否大于1W，如果大于等于1W，则计算减去当前一笔，是否小于等于1W，如果是，则返现，否则为已经返现。
					/*UserInfo userInfo = this.getUserInfoService().findUserInfoByUid(log.getUid());
					if(userInfo.getUid() == log.getUid() && userInfo.getUPS()>=100000){
						logger.info(String.format("该用户【%s】有邀请人【%s】，满足活动条件",log.getUid(),userInfo.getUPS()));
						BigDecimal sum = this.getUserMoneyService().findSumMoneyByUid(log.getUid());
						sum = sum.abs();
						logger.info(String.format("用户[%s]已经投资[%s]",log.getUid(),sum));
						if(sum.compareTo(new BigDecimal("10000"))==0){
							//正好等于1W
							logger.info(String.format("该用户【%s】投资刚好达到1W元",log.getUid()));
							//保存被邀请人38元现金奖励
							logger.info("开始保存被邀请人的活动奖励");
							UserStatus userStatus = this.getUserInfoService().findUserStatusByUid(log.getUid());
							UserRmbs rmbs = new UserRmbs();
							rmbs.setSid(VeStr.getUSid());
							rmbs.setTid(5);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
							rmbs.setUid(userInfo.getUPS());
							rmbs.setFid(0);
							rmbs.setWay("理财回款");
							String mobile = userStatus.getMobile();
							mobile = mobile.substring(0, mobile.length() - 8) + "****" + mobile.substring(mobile.length() - 4);
							rmbs.setEvent("累计投满1万，返现38元");
							BigDecimal adds = new BigDecimal("38");
							logger.info("adds"+adds.toString());
							rmbs.setAdds(adds);
							rmbs.setCost(userStatus.getMa());
							rmbs.setTotal(userStatus.getMa().add(adds));
							rmbs.setState(0);
							rmbs.setTime(System.currentTimeMillis());
							this.getUserMoneyService().save(rmbs);

							//保存邀请人38元现金奖励
							logger.info("开始保存邀请人的活动奖励");
							UserStatus userStatusUps = this.getUserInfoService().findUserStatusByUid(userInfo.getUPS());
							UserRmbs rmbsUps = new UserRmbs();
							rmbsUps.setSid(VeStr.getUSid());
							rmbsUps.setTid(5);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
							rmbsUps.setUid(userInfo.getUPS());
							rmbsUps.setFid(0);
							rmbsUps.setWay("理财回款");
							 mobile = userStatusUps.getMobile();
							mobile = mobile.substring(0, mobile.length() - 8) + "****" + mobile.substring(mobile.length() - 4);
							rmbsUps.setEvent("邀请好友"+ mobile +"累计投满1万，返现38元");
							 adds = new BigDecimal("38");
							logger.info("adds"+adds.toString());
							rmbsUps.setAdds(adds);
							rmbsUps.setCost(userStatusUps.getMa());
							rmbsUps.setTotal(userStatusUps.getMa().add(adds));
							rmbsUps.setState(0);
							rmbsUps.setTime(System.currentTimeMillis());
							this.getUserMoneyService().save(rmbsUps);
							logger.info("结束保存邀请人的活动奖励");
						}else if( sum.compareTo(new BigDecimal("10000"))>0 && (sum.add(r.getAdds())).compareTo(new BigDecimal("10000"))<0){
							//累计投资金额大于1W，并且不算当前投资金额，累计金额小于1W
							logger.info(String.format("该用户【%s】累计投资金额大于1W，并且不算当前投资金额，累计金额小于1W",log.getUid()));
							//保存被邀请人38元现金奖励
							logger.info("开始保存被邀请人的活动奖励");
							UserStatus userStatus = this.getUserInfoService().findUserStatusByUid(log.getUid());
							UserRmbs rmbs = new UserRmbs();
							rmbs.setSid(VeStr.getUSid());
							rmbs.setTid(5);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
							rmbs.setUid(userInfo.getUPS());
							rmbs.setFid(0);
							rmbs.setWay("理财回款");
							String mobile = userStatus.getMobile();
							mobile = mobile.substring(0, mobile.length() - 8) + "****" + mobile.substring(mobile.length() - 4);
							rmbs.setEvent("累计投满1万，返现38元");
							BigDecimal adds = new BigDecimal("38");
							logger.info("adds"+adds.toString());
							rmbs.setAdds(adds);
							rmbs.setCost(userStatus.getMa());
							rmbs.setTotal(userStatus.getMa().add(adds));
							rmbs.setState(0);
							rmbs.setTime(System.currentTimeMillis());
							this.getUserMoneyService().save(rmbs);

							//保存邀请人38元现金奖励
							logger.info("开始保存邀请人的活动奖励");
							UserStatus userStatusUps = this.getUserInfoService().findUserStatusByUid(userInfo.getUPS());
							UserRmbs rmbsUps = new UserRmbs();
							rmbsUps.setSid(VeStr.getUSid());
							rmbsUps.setTid(5);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
							rmbsUps.setUid(userInfo.getUPS());
							rmbsUps.setFid(0);
							rmbsUps.setWay("理财回款");
							mobile = userStatusUps.getMobile();
							mobile = mobile.substring(0, mobile.length() - 8) + "****" + mobile.substring(mobile.length() - 4);
							rmbsUps.setEvent("邀请好友"+ mobile +"累计投满1万，返现38元");
							adds = new BigDecimal("38");
							logger.info("adds"+adds.toString());
							rmbsUps.setAdds(adds);
							rmbsUps.setCost(userStatusUps.getMa());
							rmbsUps.setTotal(userStatusUps.getMa().add(adds));
							rmbsUps.setState(0);
							rmbsUps.setTime(System.currentTimeMillis());
							this.getUserMoneyService().save(rmbsUps);
							logger.info("结束保存邀请人的活动奖励");
						}else if( sum.compareTo(new BigDecimal("10000"))>0 && (sum.add(r.getAdds())).compareTo(new BigDecimal("10000"))<0){
							//累计投资金额大于1W，并且不算当前投资金额，累计金额小于1W
							logger.info(String.format("该用户【%s】累计投资金额大于1W，并且不算当前投资金额，累计金额小于1W",log.getUid()));
							//保存被邀请人38元现金奖励
							logger.info("开始保存被邀请人的活动奖励");
							UserStatus userStatus = this.getUserInfoService().findUserStatusByUid(log.getUid());
							UserRmbs rmbs = new UserRmbs();
							rmbs.setSid(VeStr.getUSid());
							rmbs.setTid(5);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
							rmbs.setUid(userInfo.getUPS());
							rmbs.setFid(0);
							rmbs.setWay("理财回款");
							String mobile = userStatus.getMobile();
							mobile = mobile.substring(0, mobile.length() - 8) + "****" + mobile.substring(mobile.length() - 4);
							rmbs.setEvent("累计投满1万，返现38元");
							BigDecimal adds = new BigDecimal("38");
							logger.info("adds"+adds.toString());
							rmbs.setAdds(adds);
							rmbs.setCost(userStatus.getMa());
							rmbs.setTotal(userStatus.getMa().add(adds));
							rmbs.setState(0);
							rmbs.setTime(System.currentTimeMillis());
							this.getUserMoneyService().save(rmbs);

							//保存邀请人38元现金奖励
							logger.info("开始保存邀请人的活动奖励");
							UserStatus userStatusUps = this.getUserInfoService().findUserStatusByUid(userInfo.getUPS());
							UserRmbs rmbsUps = new UserRmbs();
							rmbsUps.setSid(VeStr.getUSid());
							rmbsUps.setTid(5);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
							rmbsUps.setUid(userInfo.getUPS());
							rmbsUps.setFid(0);
							rmbsUps.setWay("理财回款");
							mobile = userStatusUps.getMobile();
							mobile = mobile.substring(0, mobile.length() - 8) + "****" + mobile.substring(mobile.length() - 4);
							rmbsUps.setEvent("邀请好友"+ mobile +"累计投满1万，返现38元");
							adds = new BigDecimal("38");
							logger.info("adds"+adds.toString());
							rmbsUps.setAdds(adds);
							rmbsUps.setCost(userStatusUps.getMa());
							rmbsUps.setTotal(userStatusUps.getMa().add(adds));
							rmbsUps.setState(0);
							rmbsUps.setTime(System.currentTimeMillis());
							this.getUserMoneyService().save(rmbsUps);
							logger.info("结束保存邀请人的活动奖励");
						}

					}*/
				}
			}
			conn.commit();
			
			if (STATE_NORMAL == result) {

				//检查邀请人是否是会员，不是会员不享受投资收益返现
       /* boolean isVip = false;
        try {
            logger.info(String.format("查询【%s】在【%s】时，是否是会员，", s.getUPS(), System.currentTimeMillis()));
            UserVip userVip = this.getUserVipService().queryVipLog(s.getUPS(), System.currentTimeMillis());
            logger.info(String.format("该用户【%s】信息为:【%s】", s.getUPS(), userVip.toString()));
            isVip = userVip.getLevel() < 2 ? false : true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 处理邀请等信息

        if (isVip) {
            logger.info(String.format("该用户【%s】当前是会员，享受权益", s.getUPS()));
            if (s.getUPS() >= USER_UID_BEG) {
                if (s.getNp() == 1 && log.getTma().intValue() >= 1000) {
                    this.getTriggerService().invite(s.getUPS(), log.getTime());
                } // 分享收益加成
                BigDecimal rmb = log.getTmg().divide(BigDecimal.TEN); // 基础收益10%
                if (rmb.compareTo(BigDecimal.ZERO) >= 1) {
                    this.invite(s.getUPS(), log.getUid(), log.getSid(), rmb, log.getTime(),log.getTma(),s.getMobile());
                }
            } else {
                // Ignored
            }
        } else
            logger.info(String.format("该用户【%s】当前不是会员，不享受权益", s.getUPS()));

*/



//				SyncMap.getAll().sender(SYS_A850, "save", log);
				this.execute(() -> {
					try {
						logger.info("start ActivityService().take ,");
						this.getActivityService().take(info, log);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}); // 检测标的是否满
				BigDecimal amt = log.getTma().add(info.getMd());
				BigDecimal yu = info.getMa().subtract(amt); // 剩余额度
				if (yu.compareTo(BigDecimal.ZERO) <= 0 || info.getMc().compareTo(yu) >= 1) {
					this.execute(() -> {
						try {
							this.getProdInfoService().saveOver(info.getPid(), log.getTime());
						} catch (SQLException e) {
							e.printStackTrace();
						}
					});
				}
			}
			return result;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	public void saveOrder(SysOrder s) throws SQLException {
		s.setTime(System.currentTimeMillis());
		if (USER_TIME >= s.getGmtA()) {
			s.setGmtA(s.getTime());
			s.setGmtD(s.getTime());
		}
		logger.info("save!!!!!!!!!!");
		this.save(s); // 保存记录
		logger.info("save success!!!!!!!!!!");
//		SyncMap.getAll().sender(SYS_A850, "order", s);
	}

	public boolean isProdByAll(long uid, int tid, int total) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long all = 0;
			ps = conn.prepareStatement("SELECT COUNT(1) FROM " + Table.TBL_LOG_ORDER + " WHERE Uid=? AND Tid=? AND State>=?");
			ps.setLong(1, uid);
			ps.setInt(2, tid);
			ps.setInt(3, STATE_CHECK);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				all = rs.getInt(1);
			}
			rs.close();
			return (all >= total);
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isProdByPid(long uid, long Pid, int total) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long all = 0;
			ps = conn.prepareStatement("SELECT COUNT(1) FROM " + Table.TBL_LOG_ORDER + " WHERE Pid=? AND Uid=? AND State>=?");
			ps.setLong(1, Pid);
			ps.setLong(2, uid);
			ps.setInt(3, STATE_CHECK);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				all = rs.getInt(1);
			}
			rs.close();
			return (all >= total);
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public LogOrder findLogOrderBySid(long sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			LogOrder log = null;
			ps = conn.prepareStatement("SELECT Sid,Uid,Pid,Cid,Tid,Name,Rate,Rday,Any,Day,Way,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,GmtD,GURL,State,Time FROM " + Table.TBL_LOG_ORDER + " WHERE Sid=?");
			ps.setLong(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				log = new LogOrder();
				log.setSid(rs.getLong(1));
				log.setUid(rs.getLong(2));
				log.setPid(rs.getLong(3));
				log.setCid(rs.getLong(4));
				log.setTid(rs.getInt(5));
				log.setName(rs.getString(6));
				log.setRate(rs.getBigDecimal(7));
				log.setRday(rs.getInt(8));
				log.setAny(rs.getInt(9));
				log.setDay(rs.getInt(10));
				log.setWay(rs.getString(11));
				log.setTma(rs.getBigDecimal(12));
				log.setTmb(rs.getBigDecimal(13));
				log.setTmc(rs.getBigDecimal(14));
				log.setTmd(rs.getBigDecimal(15));
				log.setTme(rs.getBigDecimal(16));
				log.setTmf(rs.getBigDecimal(17));
				log.setTmg(rs.getBigDecimal(18));
				log.setYma(rs.getBigDecimal(19));
				log.setYmb(rs.getBigDecimal(20));
				log.setGmtA(rs.getLong(21));
				log.setGmtB(rs.getLong(22));
				log.setGmtC(rs.getLong(23));
				log.setGmtD(rs.getLong(24));
				log.setUrl(rs.getString(25));
				log.setState(rs.getInt(26));
				log.setTime(rs.getLong(27));
			}
			rs.close();
			return log;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public SysOrder findSysOrderBySid(long sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			SysOrder s = null;
			ps = conn.prepareStatement("SELECT Sid,Uid,Pid,Cid,Cid1,Tid,Name,Rate,Rday,Day,Way,Ads,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,GmtD,Mobile,Nicer,State,Stext,Time FROM " + Table.TBL_SYS_ORDER + " WHERE Sid=?");
			ps.setLong(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				s = new SysOrder();
				s.setSid(rs.getLong(1));
				s.setUid(rs.getLong(2));
				s.setPid(rs.getLong(3));
				s.setCid(rs.getLong(4));
				s.setCid1(rs.getLong(5));
				s.setTid(rs.getInt(6));
				s.setName(rs.getString(7));
				s.setRate(rs.getBigDecimal(8));
				s.setRday(rs.getInt(9));
				s.setDay(rs.getInt(10));
				s.setWay(rs.getString(11));
				s.setAds(rs.getBigDecimal(12));
				s.setTma(rs.getBigDecimal(13));
				s.setTmb(rs.getBigDecimal(14));
				s.setTmc(rs.getBigDecimal(15));
				s.setTmd(rs.getBigDecimal(16));
				s.setTme(rs.getBigDecimal(17));
				s.setTmf(rs.getBigDecimal(18));
				s.setTmg(rs.getBigDecimal(19));
				s.setYma(rs.getBigDecimal(20));
				s.setYmb(rs.getBigDecimal(21));
				s.setGmtA(rs.getLong(22));
				s.setGmtB(rs.getLong(23));
				s.setGmtC(rs.getLong(24));
				s.setGmtD(rs.getLong(25));
				s.setMobile(rs.getString(26));
				s.setNicer(rs.getString(27));
				s.setState(rs.getInt(28));
				s.setStext(rs.getString(29));
				s.setTime(rs.getLong(30));
			}
			rs.close();
			return s;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo sendTreadByUid(AjaxInfo json, long uid, int state) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (SALE_A4 == state) {
				ps = conn.prepareStatement("SELECT Sid,Uid,Pid,Name,Rday,Any,TMA,TMB,YMA,State,Time FROM " + Table.TBL_LOG_ORDER + " WHERE Uid=? AND State=? ORDER BY Time DESC");
				ps.setLong(1, uid);
				ps.setInt(2, 4);
			} else {
				ps = conn.prepareStatement("SELECT Sid,Uid,Pid,Name,Rday,Any,TMA,TMB,YMA,State,Time FROM " + Table.TBL_LOG_ORDER + " WHERE Uid=? AND State<=? AND State>=? ORDER BY Any ASC");
				ps.setLong(1, uid);
				ps.setInt(2, 3);
				ps.setInt(3, 1);
			}
			json.datas(API_OK);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("sid", rs.getLong(1));
				json.append("pid", rs.getLong(2));
				json.append("name", rs.getString(4));
				json.append("day", rs.getInt(5));
				json.append("any", rs.getInt(6));
				json.append("tma", DF2.format(rs.getFloat(7)));
				BigDecimal B = rs.getBigDecimal(8);
				BigDecimal Y = rs.getBigDecimal(9);
				json.append("tmg", DF2.format(Y.subtract(B)));
				json.append("state", rs.getInt(10));
				json.append("time", GMTime.format(rs.getLong(11), GMTime.CHINA, GMTime.OUT_SHORT));
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
