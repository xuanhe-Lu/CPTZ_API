package com.ypiao.json;

import com.sunsw.struts.ServletActionContext;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.Lottery;
import com.ypiao.bean.LotteryLog;
import com.ypiao.bean.UserCoupon;
import com.ypiao.service.CouponInfoService;
import com.ypiao.service.UserAuthService;
import com.ypiao.service.UserCouponService;
import com.ypiao.service.UserLotteryService;
import com.ypiao.util.GMTime;
import com.ypiao.util.GState;
import com.ypiao.util.VeStr;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @NAME:OnUserLottery
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/7/19
 * @VERSION:1.0
 */
public class OnUserLottery extends Action {
    private static final long serialVersionUID = -2009333380089573935L;
    private static Logger logger = LoggerFactory.getLogger(OnUserLottery.class);
    private UserLotteryService userLotteryService;
    private CouponInfoService couponInfoService;
    private UserCouponService userCouponService;
    private UserAuthService userAuthService;

    public OnUserLottery() {
        super(true);
    }

    /*
     * @NAME:findLottery
     * @DESCRIPTION:查找奖项配置和抽奖次数。
     * @AUTHOR:luxh
     * @DATE:2018/7/19
     * @VERSION:1.0
     */
    public String findLottery() throws Exception {
        logger.info(String.format("come in OnUserDraw.findLottery"));
        AjaxInfo json = this.getAjaxInfo();
        long uid = this.getLong("uid");
        logger.info(String.format("uid:%s", uid));
        int count = 0;
        try {
            count = this.userLotteryService.findLotteryCountByUidAndTime(uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(String.format("用户【%s】共有%s次抽奖次数", uid, count));
        if (count < 1) {
            logger.info(String.format("该用户【%s】0次抽奖机会，只返回count", uid));
            json.success(API_OK);
            json.add("body");
            json.formater();
            json.append("count", count);
            return JSON;
        }
        List<Lottery> lotteryList = null;
        try {
            lotteryList = this.userLotteryService.findLotteryConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lotteryList == null || lotteryList.size() == 0) {
            logger.info(String.format("未找到抽奖相关配置，请检查配置表!!!"));
            json.addError("未找到抽奖相关配置，请检查配置表!!!");
            return JSON;
        }
        json.success(API_OK);
        json.add("body");
//        json.formater();
        json.append("count", count);
//        json.formates();
        json.adds("lotteryList");
        for (Lottery lottery : lotteryList) {
            int id = lottery.getId();
            String name = lottery.getName();
            String url = lottery.getUrl();
            BigDecimal money = lottery.getMoney();
            int type = lottery.getType();
            int probability = lottery.getProbability();
            json.formater();
            json.append("id", id);
            json.append("name", name);
            json.append("url", url);
            json.append("money", money);
            json.append("type", type);
            json.append("probability", probability);
        }
        return JSON;
    }

    /*
     * @NAME:save
     * @DESCRIPTION:用户抽奖，抽奖次数-1，并将抽到的奖励写入到相关表中
     * @AUTHOR:luxh
     * @DATE:2018/7/19
     * @VERSION:1.0
     */
    public String save() {
        logger.info(String.format("come in OnUserDraw.save"));
        AjaxInfo json = this.getAjaxInfo();
        long uid = this.getLong("uid");
        logger.info(String.format("uid:%s", uid));
        //1.首先校验该用户是否还有抽奖次数。
        int count = 0;
        try {
            count = this.userLotteryService.findLotteryCountByUidAndTime(uid);
        } catch (Exception e) {
            logger.error(String.format("查找抽奖次数出错，请稍后再试."));
            e.printStackTrace();
        }
        Lottery lottery = new Lottery();
        if (count < 1) {
            logger.error(String.format("该用户【%s】没有抽奖次数", uid));
            json.addError("用户已经没有抽奖次数了!!!");
            return JSON;
        } else {
            //2. 查找奖项和概率
            List<Lottery> lotteryList = null;
            try {
                lotteryList = this.userLotteryService.findLotteryConfig();
            } catch (Exception e) {
                logger.error(String.format("查找抽奖次数出错，请稍后再试."));
                json.addError("查找抽奖次数出错，请稍后再试.");
                e.printStackTrace();
                return JSON;
            }
            int sum = 100;// 抽奖概率写死成100，后期如果需要改动，则放开接口。
//            sum = this.userLotteryService.findLotteryProbability();
            if (lotteryList == null || lotteryList.size() == 0) {
                logger.info(String.format("未找到抽奖相关配置，请检查配置表!!!"));
                json.addError("未找到抽奖相关配置，请检查配置表!!!");
                return JSON;
            }
            List<Integer> list = new ArrayList<>();
            int i = (int) (Math.random() * sum + 1);
            logger.info("概率为：" + i);
            int num = 0;
            //3. 抽奖
            for (Lottery lottery1 : lotteryList) {
                logger.info(String.format("lottery:【%s】", lottery1.toString()));
                num = lottery1.getProbability() + num;
                if (num >= i) {
                    lottery = lottery1;
                    logger.info(String.format("该用户【%s】抽中【%s】奖项，奖项名称为【%s】", uid, lottery.getId(), lottery.getName()));
                    break;
                }
            }
            //4. 将抽奖结果入表，并返回给前台
            if (lottery.getType() == 9) {//type =9为谢谢惠顾
                //TODO 增加log
                logger.info(String.format("该用户【%s】的本次抽奖结果为：%s", uid, lottery.getName()));
                count = count - 1;
                try {
                    LotteryLog lotteryLog = new LotteryLog();
                    lotteryLog.setCount(count);
                    lotteryLog.setUid(uid);
                    lotteryLog.setLotteryId(lottery.getId());
                    lotteryLog.setName(lottery.getName());
                    lotteryLog.setSign("D");
                    lotteryLog.setRemark("成功抽取一次奖励.");
                    lotteryLog.setCreateTime(GMTime.currentTimeMillis());

                    Calendar t = Calendar.getInstance();
                    t.setTimeInMillis(GState.USER_TODAX);
                    t.add(Calendar.DATE, 30);
                    lotteryLog.setEndTime(t.getTimeInMillis() - 1000);
                    this.userLotteryService.saveLotteryLog(lotteryLog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info(String.format("该用户【%s】的本次抽奖结果入表成功,", uid));

//                return JSON;
            } else if (lottery.getType() == 2 || lottery.getType() == 3 || lottery.getType() == 1) { //1是体验金，2是加息券，3是现金券,
                //到期时间，起投金额，开始时间，结束时间。 使用期限是领取开始一个月内
                logger.info(String.format("该用户【%s】的本次抽奖结果为：%s", uid, lottery.getName()));
                UserCoupon uc = new UserCoupon();
                uc.setSid(VeStr.getUSid());
                uc.setUid(uid);
                uc.setCid(99);
                uc.setWay(WAY_AUTO);
                uc.setName(lottery.getName());
                uc.setType(lottery.getType());
                BigDecimal tma = lottery.getMoney();
                uc.setTma(tma);//票券金额
                BigDecimal tmb = new BigDecimal(0.00);
                uc.setTmb(tmb);
                BigDecimal toall = new BigDecimal(100000.00);
                uc.setToall(toall);//累计限额
                uc.setToday(30);//理财天数
                uc.setSday(GMTime.currentTimeMillis());
//                uc.setEday(GMTime.currentTimeMillis()+2592000000l);//30天
                Calendar t = Calendar.getInstance();
                t.setTimeInMillis(GState.USER_TODAX);
                t.add(Calendar.DATE, 30);
                uc.setEday(t.getTimeInMillis() - 1000);//30天
                uc.setRemark(lottery.getRemark());
                uc.setGmtA(GMTime.currentTimeMillis());
                uc.setGmtB(0); // 未使用
                uc.setState(STATE_NORMAL);
                uc.setTime(GMTime.currentTimeMillis());
                List<UserCoupon> userCouponList = new ArrayList<>();
                userCouponList.add(uc);
                try {
                    this.getUserCouponService().save(userCouponList, GMTime.currentTimeMillis());
                } catch (SQLException e) {
                    logger.error(String.format("抽奖奖励存储失败"));
                    e.printStackTrace();
                    json.addError("抽奖奖励存储失败");
//                    return JSON;
                }
                //TODO 抽奖记录入表
                count = count - 1;
                try {
                    LotteryLog lotteryLog = new LotteryLog();
                    lotteryLog.setCount(count);
                    lotteryLog.setUid(uid);
                    lotteryLog.setLotteryId(lottery.getId());
                    lotteryLog.setName(lottery.getName());
                    lotteryLog.setSign("D");
                    lotteryLog.setRemark("成功抽取一次奖励.");
                    lotteryLog.setCreateTime(GMTime.currentTimeMillis());

                    t = Calendar.getInstance();
                    t.setTimeInMillis(GState.USER_TODAX);
                    t.add(Calendar.DATE, 30);
                    lotteryLog.setEndTime(t.getTimeInMillis() - 1000);


                    this.userLotteryService.saveLotteryLog(lotteryLog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info(String.format("该用户【%s】的本次抽奖结果入表成功,", uid));
             /*   json.success(API_OK);
                json.add("body");
                json.formater();
                json.append("id", lottery.getId());
                json.append("name", lottery.getName());
                json.append("money", lottery.getMoney());
                return JSON;*/
            } else if (lottery.getType() == 4) {//4是返现
                logger.info(String.format("该用户【%s】的本次抽奖结果为：%s", uid, lottery.getName()));
                //返现直接到账户中，但是提现的时候需要校验是否有在投产品。
                //TODO 返现暂时概率为零
                count = count - 1;
                LotteryLog lotteryLog = new LotteryLog();
                lotteryLog.setCount(count);
                lotteryLog.setUid(uid);
                lotteryLog.setLotteryId(lottery.getId());
                lotteryLog.setName(lottery.getName());
                lotteryLog.setSign("D");
                lotteryLog.setRemark("成功抽取一次奖励.");
                lotteryLog.setCreateTime(GMTime.currentTimeMillis());

                Calendar t = Calendar.getInstance();
                t.setTimeInMillis(GState.USER_TODAX);
                t.add(Calendar.DATE, 30);
                lotteryLog.setEndTime(t.getTimeInMillis() - 1000);
                try {
                    this.userLotteryService.saveLotteryLog(lotteryLog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        json.datas(API_OK);
//        json.add("body");
//        json.formater();
//        json.append("id", lottery.getId());
//        json.append("name", lottery.getName());
//        json.append("money", lottery.getMoney());
        return JSON;
    }

    @Override
    public String index() {
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setHeader( "Access-Control-Allow-Origin", "*" );
        AjaxInfo json = new AjaxInfo();
        return JSON;
    }

    public UserLotteryService getUserLotteryService() {
        return userLotteryService;
    }

    public void setUserLotteryService(UserLotteryService userLotteryService) {
        this.userLotteryService = userLotteryService;
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

    public UserAuthService getUserAuthService() {
        return userAuthService;
    }

    public void setUserAuthService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }
}
