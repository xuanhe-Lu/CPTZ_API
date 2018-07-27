package com.ypiao.json;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.Cat;
import com.ypiao.bean.CatConfig;
import com.ypiao.bean.UserVip;
import com.ypiao.service.UserCatService;
import com.ypiao.service.UserFaceService;
import com.ypiao.service.UserVipService;
import com.ypiao.util.MonthFound;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;

/**
 * @NAME:OnUserCat
 * @DESCRIPTION:猫舍
 * @AUTHOR:luxh
 * @DATE:2018/7/24
 * @VERSION:1.0
 */
public class OnUserCat extends Action {
    private static final long serialVersionUID = -2420022571044575064L;
    private static Logger log = Logger.getLogger(OnUserCat.class);
    private UserCatService userCatService;
    private UserVipService userVipService;
    private UserFaceService userFaceService;

    @Override
    public String index() {
        return null;
    }

    public OnUserCat() {
        super(true);
    }

    /*
     * @NAME:getCatInfo
     * @DESCRIPTION:获取猫信息
     * @AUTHOR:luxh
     * @DATE:2018/7/24
     * @VERSION:1.0
     */
    public String getCatInfo() {
        log.info("come in OnUserCat.getCatInfo");
        AjaxInfo json = this.getAjaxInfo();
//        UserSession us = this.getUserSession();
//        log.info("us.getUid():"+us.getUid());
        long uid = this.getLong("uid");
        int id = this.getInt("id");
        int type = 1;
        if (id == 0) {
            type = 1;//根据用户查找
        } else
            type = 2;//根据catId查找
        try {
//            json.formater();
//            json.add("catInfo");
//            json.formates();
            //根据uid 查询猫信息
            log.info(String.format("[%s]调用UserCatService().qryCatInfo", uid));
            List<Cat> catList = this.getUserCatService().qryCatInfo(id == 0 ? uid : id, type);
            log.info(String.format("[%s]调用UserCatService().qryCatInfo结束", uid));
            json.success(API_OK);
            json.add("body");
            json.adds("catInfo");
            for (Cat cat : catList) {
                if (cat != null && cat.getId() != 0) {
                    json.formater();
                    json.append("id", cat.getId());
                    json.append("uid", cat.getUid());
                    json.append("catName", cat.getCatName());
                    json.append("userName", cat.getUserName() == "" ? String.valueOf(cat.getUid()) : cat.getUserName());
                    json.append("catLevel", cat.getCatLevel());
                    json.append("gender", cat.getGender());
                    json.append("catFood", cat.getCatFood());
                    json.append("state", cat.getState());
                    json.append("maturity", cat.getMaturity());
                    json.append("growth", cat.getGrowth());
                    json.append("bathTime", cat.getBathTime());
                    json.append("clearTime", cat.getClearTime());
                    json.append("shareTime", cat.getShareTime());
                    json.append("remark", cat.getRemark());
                    json.append("IsShovel", cat.getIsShovel());
                }
            }
            return JSON;
        } catch (Exception e) {
            log.info("获取猫舍信息出错,uid:" + uid);
            json.addError("获取猫舍信息出错");
            return JSON;
        }
    }

    /*
     * @NAME:getCatFood
     * @DESCRIPTION:新增猫粮保存
     * @AUTHOR:luxh
     * @DATE:2018/7/26
     * @VERSION:1.0
     */
    public String saveCatFood() {
        log.info("come in getCatFood");
        AjaxInfo json = this.getAjaxInfo();
        //type 1为分享，2洗澡，3喂食，4铲屎
        long savetime = 0L;
        long uid = this.getLong("uid");
        int id = this.getInt("id");
        long time = this.getLong("time");
        int type = this.getInt("type");//获取动作,根据动作查找猫粮配置表，
        log.info(String.format("uid:[%s],time:[%s],type:[%s]", uid, time, type));

        try {
            // 根据时间和type 查询当天是否已经执行了动作。
//            MonthFound.getDataStamp();
            UserVip userVip = this.getUserVipService().queryVipLog(uid, time);
            if (userVip != null && userVip.getId() != 0) {//是否获取到会员信息
                log.info(String.format("start findcatConfig"));
                CatConfig catConfig = this.getUserCatService().findcatConfig(type);
                log.info(String.format("end findcatConfig"));
                if (catConfig != null && catConfig.getId() != 0) {
                    log.info(String.format("findcatConfig获取到了数据:[%s]", catConfig.toString()));
                    int level = userVip.getLevel();
                    int catFood = 0;
                    BigDecimal grow = new BigDecimal(0.00);
                    if (level == 2) {//白银会员
                        log.info(String.format("level:[%s]", level));
                        catFood = catConfig.getSilverRight();
                        grow = catConfig.getSilverGrowthAdd();
                    } else if (level == 3) {
                        log.info(String.format("level:[%s]", level));
                        catFood = catConfig.getGoldRight();
                        grow = catConfig.getGoldGrowthAdd();
                    }
                    //检查猫的状态和动作时间
                    Cat cat = this.getUserCatService().findCatStatus(id, uid);

                    String date = "";
                    String dateNew = "";
                    String name = "";
                    if (type == 1) {//分享
                        log.info(String.format("[%s]会员本次操作的动作是[%s],时间是[%s]", uid, catConfig.getName(), time));
                        savetime = cat.getShareTime();
                        date = MonthFound.getDataFormat(savetime, "yyyy-MM-dd");
                        dateNew = MonthFound.getDataFormat(time, "yyyy-MM-dd");
                        name = "分享";
                        if (date.equals(dateNew)) {
                            log.info(String.format("[%s]会员今日已分享过了,不在获得猫粮", uid));
                            json.success(API_OK);
                            json.add("body");
                            json.append("type", type);
                            json.append("state", 0);
                            json.append("msg", String.format("您今日已分享过了,不在获得猫粮"));
                            return JSON;
                        }
                    } else if (type == 2) {//洗澡
                        log.info(String.format("[%s]会员本次操作的动作是[%s],时间是[%s]", uid, catConfig.getName(), time));
                        savetime = cat.getBathTime();
                        date = MonthFound.getDataFormat(savetime, "yyyy-MM-dd");
                        dateNew = MonthFound.getDataFormat(time, "yyyy-MM-dd");
                        name = "洗澡";
                        if (date.equals(dateNew)) {
                            log.info(String.format("[%s]会员今日已给[%s]猫洗过澡了,不在获得猫粮", uid, id));
                            json.success(API_OK);
                            json.add("body");
                            json.append("type", type);
                            json.append("state", 0);
                            json.append("msg", String.format("您今日已给猫洗过澡了,不在获得猫粮"));
                            return JSON;
                        }
                    } else if (type == 3) {//喂食
                        name = "喂食";
                        log.info(String.format("[%s]会员本次操作的动作是[喂食],时间是[%s]", uid, catConfig.getName(), time));
                        if (cat.getClearTime() <= cat.getFeedTime()) {
                            // 如果铲屎时间小于等于喂食时间，则是已喂食，但未铲屎
                            log.info(String.format("[%s]会员今日已给[]猫喂食，但是未铲屎，请铲屎后在喂食。", uid, id));
                            json.success(API_OK);
                            json.add("body");
                            json.append("type", type);
                            json.append("state", 0);
                            json.append("msg", String.format("您今日已给猫喂食了，但是未铲屎，请铲屎后在喂食"));
                            return JSON;
                        }
                    } else if (type == 4) {//铲屎
                        name = "铲屎";
                        log.info(String.format("[%s]会员本次操作的动作是[铲屎],时间是[%s]", uid, catConfig.getName(), time));
                        savetime = cat.getFeedTime();
                        //判断铲屎时间是否已经符合2小时后的标准
                        if (savetime == 0L) {
                            //如果喂食时间是0 ，则表明没有进行过喂食，需要先进行喂食，才能铲屎
                            log.info(String.format("[%s]会员尚未给[%s]猫喂食，请在喂食后2小时铲屎", uid, id));
                            json.success(API_OK);
                            json.add("body");
                            json.append("type", type);
                            json.append("state", 0);
                            json.append("msg", String.format("您尚未给猫喂食了，请在喂食后2小时铲屎"));
                            return JSON;
                        } else if ((time - savetime) < 72000000) {
                            //如果时间差小于两小时，则表明有喂食，但是未达到铲屎时间
                            log.info(String.format("[%s]会员已经给[%s]猫喂食，但是未达到两小时时间，请在喂食后2小时铲屎", uid, id));
                            json.success(API_OK);
                            json.add("body");
                            json.append("type", type);
                            json.append("state", 0);
                            json.append("msg", String.format("尚未达到铲屎时间，请在喂食后2小时铲屎"));
                            return JSON;
                        }
                    }

                    log.info(String.format("start updateCatActTimeByIdAndUidAndTime,uid:[%s],id:[%s],type:[%s],time:[%s],catFood:[%s],grow:[%s],name:[%s]", uid, id, type, time, catFood, grow, name));
                    //保存猫粮和成长值到正表和历史表中。
                    if(type == 4){
                        //查询最近一条喂食记录，转换成成长值入正表和记录表
                        //TODO
                    }
                    this.getUserCatService().updateCatActTimeByIdAndUidAndTime(uid, id, type, time, catFood, cat.getGrowth().add(grow), name);
                    json.append("state", 1);
                    json.append("type", type);
                    json.append("catFood", catFood);
                    json.append("grow", grow);
                    json.append("name", name);
                    return JSON;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.addError("系统错误，请稍后再试");
        }
        return JSON;
    }

    /*
     * @NAME:rankList
     * @DESCRIPTION:根据猫的成长值返回猫排行榜,用户昵称，猫的等级，成长值，用户头像
     * @AUTHOR:luxh
     * @DATE:2018/7/27
     * @VERSION:1.0
     */
    public String rankList() throws Exception {
        log.info("come in rankList");
        AjaxInfo json = this.getAjaxInfo();
        long uid = this.getLong("uid");
        List<Cat> catList = this.getUserCatService().findRankList();
        int sign = 0;
        json.add("body");
        json.adds("catList");
        log.info("循环获取前100名信息");
        for (Cat cat : catList) {
            if (cat.getUid() == uid) {
                sign = 1;
            }
            int ver = this.getUserFaceService().findFaceByUid(cat.getUid());
            json.append("id", cat.getId());
            json.append("uid", cat.getUid());
            json.append("userName", cat.getUserName());
            json.append("catLevel", cat.getCatLevel());
            json.append("growth", cat.getGrowth());
            json.append("sign", sign);
            json.add("facer");
            json.append("uid", uid);
            json.append("ver", ver);
            sign = 0;
        }
        return JSON;
    }

    /*
     * @NAME:changeName
     * @DESCRIPTION:修改名字（用户或者猫的）
     * @AUTHOR:luxh
     * @DATE:2018/7/27
     * @VERSION:1.0
     */
    public String changeName()throws Exception{
        log.info(String.format("come in changeName"));
        AjaxInfo json = this.getAjaxInfo();
        int type = this.getInt("type");
        String name  = this.getString("name");
        long id = 0L;
        if(type ==1){//代表是用户改名字
            id = this.getLong("uid");
        }else
            id = this.getInt("id");
        int i = 0;
        i = this.getUserCatService().updateName( id, type,name);
        if(i <1){
            json.addError("修改昵称失败，请稍后再试.");
            return JSON;
        }else{
            json.success(API_OK);
            json.addMessage("修改昵称成功");
        }
        return  JSON;
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

    public UserFaceService getUserFaceService() {
        return userFaceService;
    }

    public void setUserFaceService(UserFaceService userFaceService) {
        this.userFaceService = userFaceService;
    }
}
