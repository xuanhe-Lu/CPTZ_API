package com.ypiao.json;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserRmbs;
import com.ypiao.service.ActivityService;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @NAME:OnUseractivity
 * @DESCRIPTION:会员四重好礼活动
 * @AUTHOR:luxh
 * @DATE:2018/7/31
 * @VERSION:1.0
 */
public class OnUserActivity extends Action {
    private static Logger logger = Logger.getLogger(OnUserActivity.class);
    private ActivityService activityService;

    /*
     * @NAME:index
     * @DESCRIPTION:查询用户四重好礼奖励
     * @AUTHOR:luxh
     * @DATE:2018/7/31
     * @VERSION:1.0
     */
    @Override
    public String index() {
        logger.info("come in index");
        AjaxInfo json = this.getAjaxInfo();
        // 一重好礼，邀请获得加息券的人数
        long ups = this.getLong("uid");
        logger.info(String.format("uid:[%s]", ups));
        int num = 0;
        try {
            num = this.getActivityService().qryUserByUps(ups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        json.add("body");
        json.append("num", num);
        json.add("register");
        json.append("inviteNum", num);
        //二重好礼,邀请好友投资累计满1W元，双方获得38元现金奖励
        List<String> stringList = new ArrayList<>();
        try {
            stringList = this.getActivityService().qryUserByUid(ups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (stringList != null && stringList.size() > 0) {
            logger.info(String.format("有累计1W的活动奖励"));
            json.add("total");
            json.append("totalCount", stringList.size());
            json.adds("list");
            for (String s : stringList) {
                //邀请好友%累计投满1万，返现38元
                String str2 = s.substring(4, s.indexOf("累"));
                json.append("phone", str2);
                json.append("money", 38);
            }
        }
        //三重好礼，邀请人获得投资收益的10%
        //邀请"+mobile+"投资"+tma+"获得奖励金
        List<UserRmbs> userRmbsList = new ArrayList<>();
        try {
            userRmbsList = this.getActivityService().qryInvestByUidAndEvent(ups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        json.add("invest");
        //从 EVENT 拆分手机号，投资金额和奖励
        for (UserRmbs userRmbs : userRmbsList) {
            json.formater();
            String s = userRmbs.getEvent();
            String mobile = s.substring(2,s.indexOf("投"));
            String amt = s.substring(s.indexOf("资"),s.indexOf("获"));
            BigDecimal rmb = userRmbs.getAdds();
            json.append("mobile",mobile);
            json.append("amt",amt);
            json.append("rmb",rmb);
        }
        //四重好礼，购买会员返现金
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = this.getActivityService().qryUserVipByUid(ups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        json.adds("returnMoney");
        json.add("list");
        int countS = 0;//白银统计
        boolean isS = false;
        for (Map<String, Object> map : list) {
            BigDecimal money = new BigDecimal(String.valueOf(map.get("adds")));
            if (String.valueOf(money).startsWith("2")) {
                countS += 1;
                isS = true;
            }
            json.append("phone", String.valueOf(map.get("event")));
            json.append("level", isS ? "白银会员" : "黄金会员");
            json.append("money", money);
            isS = false;
        }
        json.append("countSri", countS);
        json.append("countGold", list.size() - countS);

        return JSON;
    }

    public OnUserActivity() {
        super(true);
    }

    public ActivityService getActivityService() {
        return activityService;
    }

    public void setActivityService(ActivityService activityService) {
        this.activityService = activityService;
    }
}
