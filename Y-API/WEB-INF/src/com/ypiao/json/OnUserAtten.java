package com.ypiao.json;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.CatConfig;
import com.ypiao.bean.UserSession;
import com.ypiao.bean.UserVip;
import com.ypiao.service.UserAttenService;
import com.ypiao.service.UserCatService;
import com.ypiao.service.UserVipService;
import com.ypiao.service.imp.UserAttenServiceImp;
import com.ypiao.util.MonthFound;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;

import java.text.ParseException;
import java.util.List;

/**
 * @NAME:OnUserAtten
 * @DESCRIPTION:签到相关ACTION
 * @AUTHOR:luxh
 * @DATE:2018/7/16
 * @VERSION:1.0
 */
public class OnUserAtten extends Action {

    private static Logger logger = LoggerFactory.getLogger(UserAttenServiceImp.class);
    private static final long serialVersionUID = -5994314495730896937L;

    private UserAttenService userAttenService;
    private UserCatService userCatService;

    private UserVipService userVipService;
    public OnUserAtten() {
        super(true);
    }

    @Override
    public String index() {
        AjaxInfo json = this.getAjaxInfo();
        long time = this.getLong("time");
        UserSession us = this.getUserSession();
        long uid = us.getUid();
        logger.info(String.format("index:time:%s,uid:%s", time, uid));
        logger.info(String.format("index:time:%s,uid:%s", time, uid));
        time = Long.parseLong(this.getParameter("time"));
        uid = Long.parseLong(this.getParameter("uid"));
        logger.info(String.format("index:time:%s,uid:%s", time, uid));
        logger.info("json:" + json.toString());
        return JSON;
    }

    /*
     * @CLASSNAME:findInfo
     * @DESCRIPTION:查找用户当月签到记录和当天是否签到。
     * @AUTHOR:luxh
     * @DATE:2018/7/16
     * @VERSION:1.0
     */
    public String findInfo() throws Exception {
        logger.info(String.format("come in findInfo"));
        AjaxInfo json = this.getAjaxInfo();
        long time = Long.parseLong(this.getParameter("time"));
        UserSession us = this.getUserSession();
        long uid = us.getUid();
        logger.info(String.format("time:%s,uid:%s", time, uid));
        //获取date格式的当前时间
        String format = "yyyy-MM-dd";
        String dateNow = MonthFound.getDataFormat(time, format);
        logger.info(String.format("获取当前日期，%s", dateNow));
        //获取本月第一天的时间戳格式
        String dateFirst = dateNow.substring(0, dateNow.length() - 2).concat("01");
        format = "yyyy-MM-dd";
        logger.info(String.format("当月第一天时间是:%s,格式为:%s", dateFirst, format));
        long firstDay = 0;
        try {
            firstDay = MonthFound.getDataStamp(dateFirst, format);
        } catch (ParseException e) {
            logger.error(String.format("时间转换出错,MonthFound.getDataStamp，入参为:%s,转换格式为:%s", dateFirst, format));
            e.printStackTrace();
            json.addError(String.format("时间转换出错,MonthFound.getDataStamp，入参为:%s,转换格式为:%s", dateFirst, format));
        }
        logger.info(String.format("firstDay:%s", firstDay));
        //查找当月签到的日期 ,即大于本月第一天的时间戳。
        List<Long> longList = this.getUserAttenService().findUserAtten(firstDay, uid, json);
        int count = 0;
        int stats = 0;//当天是否已经签到，0是未签到，1是已签到


        if (longList != null && longList.size() > 0) {

//            Map<String, List<Long>> map = new HashMap<>();
//            map.put("signDay", longList);
//            JSONObject jsonObject = new JSONObject(map);
//            json.setBody(jsonObject.toString());


//            json.formates();
//            json.append("signDay", longList);
            logger.info(String.format("return json:%s", json.toString()));
            //获取表中最大的时间戳，与dateNow比较，小于一天,则是连续签到，否则从0开始计算。
            Long lastTime = longList.get(longList.size() - 1);
//            String lastDay = MonthFound.getDataFormat(lastTime, format);
            long timeNow = MonthFound.getDataStamp(dateNow, "yyyy-MM-dd");  //获取当天零点的时间戳
            if (timeNow - lastTime <= 86400000 && timeNow - lastTime > 0) {//前一天有签到，
                logger.info(String.format("用户昨天签到了,连续签到生效."));
                //根据time，查询表中连续签到次数。
                count = this.getUserAttenService().findUserCountByMaxTime(uid);
                logger.info(String.format("用户【%s】今天没签到，连续签到天数是%s", uid, count));
            } else if (timeNow - lastTime <= 0) {//今天已签到
                count = this.getUserAttenService().findUserCountByMaxTime(uid);
                stats = 1;
                logger.info(String.format("用户【%s】今天已经签到，连续签到天数是%s", uid, count));
            } else {//用户前一天没有签到，
                count = 0;
                logger.info(String.format("用户【%s】昨天和今天都没有签到，连续签到天数是0", uid));
            }
//            json.datas(API_OK);
            json.success(API_OK);
            json.add("body");
            json.formater();


            json.append("count", count);
            json.append("stats", stats);
            json.append("monthCount", longList.size());
            json.add("signDay");
            json.formates();//list
            for (Long aLong : longList) {
                json.append(aLong);
            }
        } else {
            json.success(API_OK);
            json.add("body");
            json.formater();
            json.append("count", count);
            json.append("stats", stats);
            json.append("monthCount", 0);
            logger.info(String.format("该用户【%s】没有签到记录", uid));
        }
        //TODO json 监控下是否正常

        logger.info(String.format("该用户【%s】count:%s,stats:%s", uid, count, stats));

        logger.info("json:" + json.toString());
        return JSON;
    }

    /*
     * @CLASSNAME:save
     * @DESCRIPTION:保存用户签到记录。
     * @AUTHOR:luxh
     * @DATE:2018/7/17
     * @VERSION:1.0
     */
    public String save() throws Exception {
        logger.info(String.format("come in OnUserAtten.save"));
        AjaxInfo json = this.getAjaxInfo();
        long time = this.getLong("time");
        UserSession us = this.getUserSession();
        long uid = us.getUid();
//        int count = this.getInt("count");
        int count = 0;
        int countRe = 1;
        synchronized (doLock(uid)) {
            count = this.getUserAttenService().findUserCountByMaxTime(uid);
            count = count + 1;
            countRe = this.getUserAttenService().save(time, uid, count);
        }
        if (countRe == 1) {
            //签到成功，则查询用户会员等级，
            UserVip vips = this.getUserVipService().queryVipLog(uid,System.currentTimeMillis());
            int vip = vips.getLevel();

            CatConfig catConfig = this.getUserCatService().findcatConfig(5);
            //签到成功根据会员等级不同,获取不同数量的猫粮
            if (vip < 2) {
                logger.info(String.format("该用户【%s】为普通会员,签到获取的猫粮【%s】g", uid, catConfig.getOrdinaryRight()));
                this.getUserCatService().updateCatFood(uid, catConfig.getOrdinaryRight());
                logger.info(String.format("保存猫粮成功"));
                json.addMessage(String.format("您为普通会员,签到获取的猫粮【%s】g", catConfig.getOrdinaryRight()));
            } else if (vip == 2) {
                logger.info(String.format("该用户【%s】为白银会员,签到获取的猫粮【%s】g", uid, catConfig.getSilverRight()));
                this.getUserCatService().updateCatFood(uid, catConfig.getSilverRight());
                logger.info(String.format("保存猫粮成功"));
                json.addMessage(String.format("您为白银会员,签到获取的猫粮【%s】g",  catConfig.getSilverRight()));
            } else if (vip == 3) {
                logger.info(String.format("该用户【%s】为黄金会员,签到获取的猫粮【%s】g", uid, catConfig.getGoldRight()));
                this.getUserCatService().updateCatFood(uid, catConfig.getGoldRight());
                logger.info(String.format("保存猫粮成功"));
                json.addMessage(String.format("您为黄金会员,签到获取的猫粮【%s】g", catConfig.getGoldRight()));
            }
        } else {
            json.addError("用户本次签到失败,请重新签到!");
        }
        logger.info("json:" + json.toString());
        return JSON;
    }

    public UserAttenService getUserAttenService() {
        return userAttenService;
    }

    public void setUserAttenService(UserAttenService userAttenService) {
        this.userAttenService = userAttenService;
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
}
