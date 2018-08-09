package com.ypiao.service.imp;

import com.ypiao.bean.*;
import com.ypiao.service.*;
import com.ypiao.util.GState;
import com.ypiao.util.VeStr;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TriggerServiceImp extends AConfig implements TriggerService {
    private static Logger logger = Logger.getLogger(TriggerServiceImp.class);
    private CouponInfoService couponInfoService;

    private UserCouponService userCouponService;
    private UserVipService userVipService;
    private UserCatService userCatService;
    private UserInfoService  userInfoService;

    protected void checkSQL() {
    }

    public UserInfoService getUserInfoService() {
        return userInfoService;
    }

    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public UserCatService getUserCatService() {
        return userCatService;
    }

    public void setUserCatService(UserCatService userCatService) {
        this.userCatService = userCatService;
    }

    public UserVipService getUserVipService() {
        return userVipService;
    }

    public void setUserVipService(UserVipService userVipService) {
        this.userVipService = userVipService;
    }

    public CouponInfoService getCouponInfoService() {
        return couponInfoService;
    }

    public void setCouponInfoService(CouponInfoService couponInfoService) {
        this.couponInfoService = couponInfoService;
    }

    public UserCouponService getUserCouponService() {
        return userCouponService;
    }

    public void setUserCouponService(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    /**
     * 计算转换后的券码
     */
    private void compute(int tid, long uid, long time) throws SQLException {
        List<CouponInfo> ls = this.getCouponInfoService().findCouponByTid(tid, time);
        int size = (ls == null) ? 0 : ls.size();
        List<UserCoupon> fs = new ArrayList<UserCoupon>(size);
        if (size >= 1) {
            Calendar t = Calendar.getInstance();
            for (CouponInfo c : ls) {
                UserCoupon uc = new UserCoupon();
                uc.setSid(VeStr.getUSid());
                uc.setUid(uid);
                uc.setCid(c.getCid());
                uc.setWay(WAY_AUTO);
                uc.setName(c.getName());
                uc.setType(c.getType());
                uc.setTma(c.getTma());
                uc.setTmb(c.getTmb());
                uc.setToall(c.getToall());
                uc.setToday(c.getToday());
                uc.setSday(c.getSday());
                long eday = c.getEday();
                if (c.getStid() == 0) {
                    // Ignroed
                } else if (c.getTday() >= 1) {
                    t.setTimeInMillis(GState.USER_TODAX);
                    t.add(Calendar.DATE, c.getTday());
                    eday = t.getTimeInMillis();
                } else if (c.getEday() >= GState.USER_TODAY) {
                    eday = GState.USER_TODAY;
                }
                uc.setEday(eday - 1000);
                uc.setRemark(c.getRemark());
                uc.setGmtA(time);
                uc.setGmtB(0); // 未使用
                uc.setState(STATE_NORMAL);
                uc.setTime(time);
                fs.add(uc);
            }
            this.getUserCouponService().save(fs, time);
            SyncMap.getAll().add("time", time).sender(SYS_A512, "save", fs);
        }
    }

    public void register(UserInfo info) throws SQLException {
        logger.info("come in register ,info:"+info.toString());
        this.compute(1, info.getUid(), info.getTime());
        //新增邀请好友送邀请人1%加息券
        if (info.getUPS() != 0 && info.getUPS() >= 100000) {
            this.compute(7, info.getUPS(), info.getTime());
            //根据会员等级增加猫粮
            boolean isVip = false;
            logger.info(String.format("查询【%s】在【%s】时，是否是会员，", info.getUPS(), System.currentTimeMillis()));
            UserVip userVip = null;
            try {
                userVip = this.getUserVipService().queryVipLog(info.getUPS(), System.currentTimeMillis());
            } catch (Exception e) {
                logger.error(String.format("查询【%s】在【%s】时，是否是会员时出错，", info.getUPS(), System.currentTimeMillis()));
                e.printStackTrace();
            }
            logger.info(String.format("该用户【%s】信息为:【%s】", info.getUPS(), userVip.toString()));
            isVip = userVip.getLevel() < 2 ? false : true;
            if (isVip) {
                if(userVip.getLevel()<2){
                    logger.info(String.format("该用户【%s】不是会员", info.getUPS()));
                }else if(userVip.getLevel() == 2){
                    try {
                        logger.info(String.format("该用户【%s】是白银会员", info.getUPS()));
                        this.getUserCatService().updateCatFood(info.getUPS(),100,"邀请好友["+info.getUid()+"]注册得猫粮");
                    } catch (Exception e) {
                        logger.error("保存猫粮失败");
                        e.printStackTrace();
                    }
                }else if(userVip.getLevel() == 3){
                    try {
                        logger.info(String.format("该用户【%s】是黄金会员", info.getUPS()));
                        this.getUserCatService().updateCatFood(info.getUPS(),150,"邀请好友["+info.getUid()+"]注册得猫粮");
                    } catch (Exception e) {
                        logger.error("保存猫粮失败");
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public void bindCard(long uid, long time) throws SQLException {
        this.compute(3, uid, time);
        //绑卡得猫粮
        //根据会员等级增加猫粮
        boolean isVip = false;
        UserInfo userInfo = this.getUserInfoService().findUserInfoByUid(uid);
        if(userInfo.getUPS()>0){
            //说明有邀请人
            logger.info(String.format("查询【%s】在【%s】时，是否是会员，", userInfo.getUPS(), System.currentTimeMillis()));
            UserVip userVip = null;
            try {
                userVip = this.getUserVipService().queryVipLog(userInfo.getUPS(), System.currentTimeMillis());
            } catch (Exception e) {
                logger.error(String.format("查询【%s】在【%s】时，是否是会员时出错，", userInfo.getUPS(), System.currentTimeMillis()));
                e.printStackTrace();
            }
            logger.info(String.format("该用户【%s】信息为:【%s】", userInfo.getUPS(), userVip.toString()));
            isVip = userVip.getLevel() < 2 ? false : true;
            if (isVip) {
                if(userVip.getLevel()<2){
                    logger.info(String.format("该用户【%s】不是会员", userInfo.getUPS()));
                }else if(userVip.getLevel() == 2){
                    try {
                        logger.info(String.format("该用户【%s】是白银会员", userInfo.getUPS()));
                        this.getUserCatService().updateCatFood(userInfo.getUPS(),400,"邀请好友["+userInfo.getUid()+"]绑卡得猫粮");
                    } catch (Exception e) {
                        logger.error("保存猫粮失败");
                        e.printStackTrace();
                    }
                }else if(userVip.getLevel() == 3){
                    try {
                        logger.info(String.format("该用户【%s】是黄金会员", userInfo.getUPS()));
                        this.getUserCatService().updateCatFood(userInfo.getUPS(),600,"邀请好友["+userInfo.getUid()+"]绑卡得猫粮");
                    } catch (Exception e) {
                        logger.error("保存猫粮失败");
                        e.printStackTrace();
                    }
                }
            }
        }else
            logger.info(String.format("用户【%s】没有邀请人,不享受邀请奖励.",uid));
    }

    public void invite(long uid, long time) throws SQLException {
        this.compute(5, uid, time);
    }
}
