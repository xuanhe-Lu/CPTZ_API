package com.ypiao.json;

import com.alibaba.fastjson.JSONObject;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserRmbs;
import com.ypiao.service.ActivityService;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static final long serialVersionUID = 8118139925849357456L;
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
        Map<String, Object> map = new HashMap<>();
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
        json.formater();


        //二重好礼,邀请好友投资累计满1W元，双方获得38元现金奖励
        /*List<String> stringList = new ArrayList<>();
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
                json.formater();
                String str2 = s.substring(4, s.indexOf("累"));
                json.append("phone", str2);
                json.append("money", 38);
            }
        } else {
            logger.info(String.format("没有累计1W的活动奖励"));
//            json.add("total");
            json.append("totalCount", 0);
//            json.adds("list");
        }*/
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
            String mobile = s.substring(2, s.indexOf("投"));
            String amt = s.substring(s.indexOf("资"), s.indexOf("获"));
            BigDecimal rmb = userRmbs.getAdds();
            json.append("mobile", mobile);
            json.append("amt", amt);
            json.append("rmb", rmb);
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
        json.formates();
        for (Map<String, Object> map213 : list) {
            BigDecimal money = new BigDecimal(String.valueOf(map213.get("adds")));
            if (String.valueOf(money).startsWith("2")) {
                countS += 1;
                isS = true;
            }
            json.formater();
            json.append("phone", String.valueOf(map213.get("event")));
            json.append("level", isS ? "白银会员" : "黄金会员");
            json.append("money", money);
            isS = false;
        }
        json.append("countSri", countS);
        json.append("countGold", list.size() - countS);

        return JSON;
    }


    public String getInfo() {
        logger.info("come in index");
        AjaxInfo json = this.getAjaxInfo();
        Map<String, Object> map = new HashMap<>();
        // 一重好礼，邀请获得加息券的人数
        long ups = this.getLong("uid");
        logger.info(String.format("uid:[%s]", ups));
        if (ups < 100000) {
            json.success(API_OK).addText("body", new HashMap<String, Object>().toString());
//            json.("body");
//            json.formater();
            return JSON;
        }
        BigDecimal sum = new BigDecimal("0.00");//总金额
        int num = 0;
        try {
            num = this.getActivityService().qryUserByUps(ups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, Object> registerMap = new HashMap<>();
        registerMap.put("inviteNum", num);
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("register", registerMap);
        //二重好礼,邀请好友投资累计满1W元，双方获得38元现金奖励
        /*List<String> stringList = new ArrayList<>();
        try {
            stringList = this.getActivityService().qryUserByUid(ups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (stringList != null && stringList.size() > 0) {
            logger.info(String.format("有累计1W的活动奖励"));
            List<Map<String, Object>> list = new ArrayList<>();
            for (String s : stringList) {
                //邀请好友%累计投满1万，返现38元
                String str2 = s.substring(4, s.indexOf("累"));
                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put("phone", str2);
                tempMap.put("name", "已满足活动金额");
                tempMap.put("money", 38);
                sum = sum.add(new BigDecimal("38"));
                list.add(tempMap);
            }
            Map<String, Object> map1 = new HashMap<>();
            map1.put("totalCount", stringList.size());
            map1.put("list", list);
            bodyMap.put("total", map1);
        } else {
            logger.info(String.format("没有累计1W的活动奖励"));
            Map<String, Object> map1 = new HashMap<>();
            map1.put("totalCount", 0);
            map1.put("list", new ArrayList<>());
            bodyMap.put("total", map1);
        }*/
        //三重好礼，邀请人获得投资收益的10%
        //邀请"+mobile+"投资"+tma+"获得奖励金
        List<UserRmbs> userRmbsList = new ArrayList<>();
        try {
            userRmbsList = this.getActivityService().qryInvestByUidAndEvent(ups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        json.add("invest");
        //从 EVENT 拆分手机号，投资金额和奖励
        List<Map<String, Object>> list1 = new ArrayList<>();
        for (UserRmbs userRmbs : userRmbsList) {
            String s = userRmbs.getEvent();
            String mobile = s.substring(2, s.indexOf("投"));
            String amt = s.substring(s.indexOf("资")+1, s.indexOf("获"));
            BigDecimal rmb = userRmbs.getAdds();
            Map<String, Object> map1 = new HashMap<>();
            map1.put("mobile", mobile);
            map1.put("amt", amt);
            map1.put("rmb", rmb);
            sum = sum.add(rmb);
            list1.add(map1);
        }
        Map<String, Object> map122 = new HashMap<>();
        map122.put("list", list1);
        map122.put("count", list1 == null ? 0 : list1.size());
        bodyMap.put("invest", map122);
        //四重好礼，购买会员返现金
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = this.getActivityService().qryUserVipByUid(ups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int countS = 0;//白银统计
        boolean isS = false;
        List<Map<String, Object>> list2 = new ArrayList<>();
        for (Map<String, Object> map11 : list) {
            BigDecimal money = new BigDecimal(String.valueOf(map11.get("adds")));
            if (String.valueOf(money).startsWith("2")) {
                countS += 1;
                isS = true;
            }
            Map<String, Object> map1 = new HashMap<>();
            map1.put("phone", String.valueOf(map11.get("event")));
            map1.put("level", isS ? "白银会员" : "黄金会员");
            map1.put("money", money);
            sum = sum.add(money);
            isS = false;
            list2.add(map1);
        }
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("list", list2);
        objectMap.put("countSilver", countS);
        objectMap.put("countGold", list.size() - countS);
        bodyMap.put("returnMoney", objectMap);
        bodyMap.put("sum", sum);
        JSONObject jsonObject = new JSONObject(bodyMap);
        String str = jsonObject.toString();
        logger.info("str:" + str);
        json.addText("body", str);
        logger.info("json.out:" + json.toString());
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
