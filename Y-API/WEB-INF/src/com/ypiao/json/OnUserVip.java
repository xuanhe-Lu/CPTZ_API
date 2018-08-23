package com.ypiao.json;


import com.ypiao.bean.*;
import com.ypiao.service.*;
import com.ypiao.util.Constant;
import com.ypiao.util.MonthFound;
import com.ypiao.util.VeStr;
import org.apache.catalina.User;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.math.BigDecimal.ROUND_HALF_UP;
/*
 * @CLASSNAME:OnUserVip
 * @DESCRIPTION: 用户会员购买变更
 * @AUTHOR:luxh
 * @DATE:2018/7/22
 * @VERSION:1.0
 */

public class OnUserVip extends Action {
    private static final long serialVersionUID = 2872145999784518120L;

    public OnUserVip() {
        super(true);
    }

    private static Logger logger = Logger.getLogger(OnUserVip.class);
    private UserVipService userVipService;
    private UserInfoService userInfoService;
    private UserAuthService userAuthService;
    private UserCatService userCatService;
    private UserMoneyService userMoneyService;

    @Override
    public String index() {
        return changeVip();
    }

    /**
     * 会员等级变更
     *
     * @return
     */
    public String changeVip() {
        logger.info("come in OnUserVip.changeVip");
        AjaxInfo json = this.getAjaxInfo();
        UserSession us = this.getUserSession();
        long uid = us.getUid();
//        long uid = this.getLong("uid");
        int level = this.getInt("level");
        String Pwd = this.getParameter("pwd");
        BigDecimal rmb;
        if (level == 2) {
            rmb = new BigDecimal(100.00).setScale(2, ROUND_HALF_UP);
        } else if (level == 3) {
            rmb = new BigDecimal(1000.00).setScale(2, ROUND_HALF_UP);
        } else {
            logger.error("请选择需要购买的会员等级,level:" + level);
            json.addError("请选择需要购买的会员等级");
            logger.info("json:" + json.toString());
            return JSON;
        }
        try {
            // 检测支付密码
            if (Pwd == null || Pwd.length() < 6) {
                json.addError(this.getText("user.error.027"));
                logger.info("json:" + json.toString());
                return JSON;
            } // 检测认证信息
            UserAuth a = this.getUserAuthService().findAuthByUid(uid);
            if (a == null) {
                json.addError(this.getText("user.error.050"));
                logger.info("json:" + json.toString());
                return JSON;
            } // 校对支付密码
            Pwd = VeStr.toMD5(Pwd); // 格式化
            if (!Pwd.equalsIgnoreCase(a.getPays())) {
                json.addError(this.getText("user.error.028"));
                logger.info("json:" + json.toString());
                return JSON;
            }
            logger.info(String.format("uid为:【%s】,想要变更为【%s】等级", uid, level));
            //1 查询用户会员记录表，校验该用户是否是正在期限内的会员。
            long nowTime = System.currentTimeMillis();
            UserVip userVip = this.getUserVipService().queryVipLog(uid, nowTime);
            long startTime = 0;
            long endTime = 0;
            logger.info(String.format("queryVipLog.uid:[%s],id:[%s]", userVip.getUid(), userVip.getId()));
            //2 如果是期限内的会员，不能购买同级别的会员，购买非同级别的会员则重新计算时间。
            if (userVip != null && userVip.getId() != 0 && userVip.getLevel() >= level) {

                logger.info(String.format("该用户【%s】已经是会员了，只能购买等级高于当前的会员", uid));
                json.addError(String.format("尊敬的用户，您已经是会员了，只能购买等级高于当前的会员"));
                logger.info("json:" + json.toString());
                return JSON;
            } else {
                Date now = null;
                if (userVip != null && userVip.getId() != 0) {
                    now = new Date(userVip.getStartTime());
                } else {
                    //获取当前时间
                    now = new Date();
                }
                Calendar calendar = Calendar.getInstance(); //得到日历
                calendar.setTime(now);//把当前时间赋给日历
                int timecount = 0;
                if (level == 2) {
                    timecount = 6;
                } else if (level == 3) {
                    timecount = 12;
                }
                calendar.add(Calendar.MONTH, timecount);  //设置为6月后
                Date dateAfter = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
                String defaultStartDate = sdf.format(dateAfter);    //格式化6月后的时间
                String defaultEndDate = sdf.format(now); //格式化当前时间
                logger.info(String.format("会员开始时间是【%s】,会员到期时间是【%s】", now, dateAfter));
                startTime = MonthFound.getDataStamp(defaultEndDate, "yyyy-MM-dd");
                endTime = MonthFound.getDataStamp(defaultStartDate, "yyyy-MM-dd");
                userVip.setStartTime(startTime);
                userVip.setEndTime(endTime);

            }
            //3.扣除用户账户余额，如果不够，则提示该用户进行充值,如果余额充足，则扣除余额，入账户资金记录表。
//            UserStatus s = this.getUserInfoService().findUserStatusByUid(uid); //findMoneyByUid
            UserRmbs s = this.getUserMoneyService().findMoneyByUid(uid);
            if (s == null) {
                json.addError(this.getText("user.error.888"));
                logger.info("json:" + json.toString());
                return JSON;
            }
            if (s.getTotal().compareTo(rmb) < 0) {
                logger.info(String.format("用户[%s]余额不足，请充值后再购买。", uid));
                json.addError("用户余额不足，请充值后再购买。");
                logger.info("json:" + json.toString());
                return JSON;
            }
            int i = 0;
            //购买会员后，用户余额修改
            i = this.getUserInfoService().updateSubHY(uid, rmb, level);
            // 修改user_rmbs表中数据
            // 看是否有邀请人，如果有邀请人，则填入到fid字段。
            long ups = us.getUPS() >= 10000 ? us.getUPS() : 0;
            UserRmbs rmbs = new UserRmbs();
            rmbs.setSid(VeStr.getUSid());
            rmbs.setTid(4);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
            rmbs.setUid(uid);
            rmbs.setFid(ups);
            rmbs.setWay("理财消费");
            rmbs.setEvent("购买会员");
            rmbs.setCost(s.getTotal());//总额
            rmbs.setAdds(new BigDecimal("-" + rmb));//消费
            rmbs.setTotal(s.getTotal().add(new BigDecimal("-" + rmb)));//进行此次操作后剩余总额
            rmbs.setState(0);
            rmbs.setTime(System.currentTimeMillis());
            logger.info("rmbs:" + rmbs.toString());
            this.getUserMoneyService().save(rmbs);

            //购买会员成功后，如果邀请人是会员，则返现到邀请人账户
            //查找
//            UserStatus sUps = this.getUserInfoService().findUserStatusByUid(ups);
            UserRmbs sUps = this.getUserMoneyService().findMoneyByUid(us.getUPS());
            if (sUps == null) {

                json.addError(this.getText("邀请人信息获取失败,ups:" + ups));
                logger.info("json:" + json.toString());
            } else {
                long time = System.currentTimeMillis();
                logger.info(String.format("查到邀请人信息,ups[%s],time[%s]", sUps.getUid(), time));
                UserVip userVipUps = this.getUserVipService().queryVipLog(ups, System.currentTimeMillis());
                logger.info(String.format("userVipUps:[%s]", userVipUps.toString()));
                if (userVipUps.getUid() == ups && userVipUps.getLevel() >= 2) {
                    int levelUps = userVipUps.getLevel();
                    UserRmbs rmbUps = new UserRmbs();
                    rmbUps.setSid(VeStr.getUSid());
                    rmbUps.setTid(5);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
                    rmbUps.setUid(ups);
                    rmbUps.setFid(0);
                    rmbUps.setWay("理财回款");
                    String mobile = us.getMobile();
//                    String mobile = "";
                    mobile = mobile.substring(0, mobile.length() - 8) + "****" + mobile.substring(mobile.length() - 4);
                    rmbUps.setEvent("邀请好友" + mobile + "购买会员");
                    BigDecimal adds = new BigDecimal("0.00");
                    if (levelUps == 2) {
                        adds = rmb.multiply(new BigDecimal("0.20"));
                    } else if (levelUps == 3) {
                        adds = rmb.multiply(new BigDecimal("0.30"));
                    }
                    logger.info("adds" + adds.toString());
                    rmbUps.setAdds(adds);
                    rmbUps.setCost(sUps.getTotal());
                    rmbUps.setTotal(sUps.getTotal().add(adds));
//                    rmbUps.add(adds.abs());
                    rmbUps.setState(0);
                    rmbUps.setTime(System.currentTimeMillis());
                    this.getUserMoneyService().save(rmbUps);
                } else {
                    logger.info(String.format("[%s]不是会员,无法享受会员返现奖励", uid));
                }
            }

            // 二级分销
            UserInfo userInfo = this.getUserInfoService().findUserInfoByUid(us.getUPS());
            if (userInfo.getUPS() > 100000) {
                logger.info("最上级推荐人是" + userInfo.getUPS());
                UserInfo userInfoUps = this.getUserInfoService().findUserInfoByUid(userInfo.getUPS());
                if (userInfoUps.getVIP() < 2) {
                    logger.info("最上级推荐人不是会员");
                }

                if (userInfoUps == null) {

                    json.addError(this.getText("邀请人信息获取失败,ups:" + ups));
                    logger.info("json:" + json.toString());
                } else if (userInfoUps.getVIP() < 2) {
                    logger.info("最上级推荐人不是会员");
                }else {
                    long time = System.currentTimeMillis();
                    logger.info(String.format("查到最上级邀请人信息,ups[%s],time[%s]", userInfoUps.getUid(), time));
                    UserVip userVipUps = this.getUserVipService().queryVipLog( userInfoUps.getUid(), time);
                    logger.info(String.format("userVipUps:[%s]", userVipUps.toString()));
                    if (userVipUps.getUid() == ups && userVipUps.getLevel() >= 2) {
                        int levelUps = userVipUps.getLevel();
                        UserRmbs rmbUps = new UserRmbs();
                        rmbUps.setSid(VeStr.getUSid());
                        rmbUps.setTid(5);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
                        rmbUps.setUid(ups);
                        rmbUps.setFid(0);
                        rmbUps.setWay("理财回款");
                        String mobile = us.getMobile();
//                    String mobile = "";
                        mobile = mobile.substring(0, mobile.length() - 8) + "****" + mobile.substring(mobile.length() - 4);
                        rmbUps.setEvent("间接邀请购买会员");
                        BigDecimal adds = new BigDecimal("0.00");
                        if (levelUps == 2) {
                            adds = rmb.multiply(new BigDecimal("0.07"));
                        } else if (levelUps == 3) {
                            adds = rmb.multiply(new BigDecimal("0.10"));
                        }
                        logger.info("adds" + adds.toString());
                        rmbUps.setAdds(adds);
                        rmbUps.setCost(sUps.getTotal());
                        rmbUps.setTotal(sUps.getTotal().add(adds));
//                    rmbUps.add(adds.abs());
                        rmbUps.setState(0);
                        rmbUps.setTime(System.currentTimeMillis());
                        this.getUserMoneyService().save(rmbUps);
                    } else {
                        logger.info(String.format("[%s]不是会员,无法享受会员返现奖励", uid));
                    }
                }
            }


            //4.将变更后的会员信息入表，
            userVip.setLevel(level);
            userVip.setUid(uid);
            if (i >= 0) {
                //TODO 数据同步到MGR
                //修改ymgr库中的user_rmbs
            }
            // TODO 应急 ，先写死，后期改成查数据库配置表
            if (level == 2) {
                userVip.setName("白银会员");
                userVip.setReceipt(new BigDecimal("1.05"));
            } else if (level == 3) {
                userVip.setName("黄金会员");
                userVip.setReceipt(new BigDecimal("1.10"));
            }
            userVip.setRemark("购买会员成功");
            this.getUserVipService().uptVipLog(userVip);
            logger.info("购买会员记录成功");
            logger.info(String.format("变更用户信息VIP等级为【%s】", level));
            this.getUserInfoService().updateUserVip(uid, level);

            //如果猫舍没满的话，新增一个同等级的猫
            List<Cat> catList = new ArrayList<>();
            catList = this.getUserCatService().qryCatInfo(uid, 1);//1是根据uid查找
            logger.info(String.format("用户[%s]的猫舍现有[%s]只猫", uid, catList.size()));
            if (catList.size() >= 2) {
                json.addMessage("因猫舍中猫的数量已达上限,小猫无法出生.");
            } /*else if(catList.size() == 1){
                logger.info("");
            }*/ else {
                //根据购买的会员等级，新增相对应的猫
                Cat cat = new Cat();
                cat.setUserName(String.valueOf(uid));
                cat.setUid(uid);
                cat.setCatLevel(level);
                if (level == 2) {
                    cat.setImg(Constant.SILVER_CAT_IMG);
                    cat.setMaturity(new BigDecimal("1000.00"));
                } else if (level == 3) {
                    cat.setImg(Constant.GOLD_CAT_IMG);
                    cat.setMaturity(new BigDecimal("10000.00"));
                }
                this.getUserCatService().insCat(cat);
                us.setVIP(level);
            }
            json.success(API_OK);
            logger.info("json:" + json.toString());
            return JSON;
        } catch (Exception e) {
            e.printStackTrace();
            json.addError(this.getText("user.error.856"));
        }


        logger.info("json:" + json.toString());
        return JSON;
    }

    /*
     * @NAME:findVipInfo
     * @DESCRIPTION:根据uid查询用户信息
     * @AUTHOR:luxh
     * @DATE:2018/7/23
     * @VERSION:1.0
     */
    public String findVipInfo() {
        logger.info("come in findVipInfo");
        AjaxInfo json = this.getAjaxInfo();
        UserSession us = this.getUserSession();
        Long uid = us.getUid();
//        Long uid = this.getLong("uid");
        logger.info(String.format("用户ID【%s】", uid));
        //根据uid查询用户会员信息
        try {
            long nowTime = System.currentTimeMillis();
            ;
            UserVip userVip = this.getUserVipService().queryVipLog(uid, nowTime);
            if (userVip == null || userVip.getId() == 0) {
                logger.info(String.format("该用户[%s]不是会员", uid));
//                json.addError("该用户不是会员，请购买会员后尝试！");
                json.success(API_OK);
                json.add("body");
                // id,name,uid,level,receipt,startTime,endTime,remark
                json.formater();
                json.append("name", "普通会员");
                json.append("level", 1);
                json.append("receipt", 1.00);
                json.append("startTime", 0);
                json.append("endTime", 4092599349000l);
                json.append("remark", "注册即为普通会员");
                logger.info("json:" + json.toString());
                return JSON;
            } else {
                json.success(API_OK);
                json.add("body");
                // id,name,uid,level,receipt,startTime,endTime,remark
                json.formater();
                json.append("name", userVip.getName());
                json.append("level", userVip.getLevel());
                json.append("receipt", userVip.getReceipt());
                json.append("startTime", userVip.getStartTime());
                json.append("endTime", userVip.getEndTime());
                json.append("remark", userVip.getRemark());
                logger.info("json:" + json.toString());
                return JSON;
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.addError("未查到会员信息，请重试！");
            logger.info("json:" + json.toString());
            return JSON;
        }
    }

    public UserVipService getUserVipService() {
        return userVipService;
    }

    public void setUserVipService(UserVipService userVipService) {
        this.userVipService = userVipService;
    }

    public UserInfoService getUserInfoService() {
        return userInfoService;
    }

    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public UserAuthService getUserAuthService() {
        return userAuthService;
    }

    public void setUserAuthService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    public UserCatService getUserCatService() {
        return userCatService;
    }

    public void setUserCatService(UserCatService userCatService) {
        this.userCatService = userCatService;
    }

    public UserMoneyService getUserMoneyService() {
        return userMoneyService;
    }

    public void setUserMoneyService(UserMoneyService userMoneyService) {
        this.userMoneyService = userMoneyService;
    }
}
