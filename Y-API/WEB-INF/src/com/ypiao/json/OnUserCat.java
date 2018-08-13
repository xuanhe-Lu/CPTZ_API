package com.ypiao.json;

import com.alibaba.fastjson.JSONObject;
import com.ypiao.bean.*;
import com.ypiao.service.UserCatService;
import com.ypiao.service.UserFaceService;
import com.ypiao.service.UserMoneyService;
import com.ypiao.service.UserVipService;
import com.ypiao.util.Constant;
import com.ypiao.util.MonthFound;
import com.ypiao.util.VeStr;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;

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
    private UserMoneyService userMoneyService;

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
        long uid = this.getLong("uid");
        int id = this.getInt("id");
        int type = 1;
        if (id == 0) {
            type = 1;//根据用户查找
        } else
            type = 2;//根据catId查找
        try {
            CatFood catFoodInfo = this.getUserCatService().qryCatFood(uid);
            int catFood = catFoodInfo.getCatFood();
            long time = System.currentTimeMillis();
            log.info("uid:" + uid + ",time:" + time);
            UserVip userVip = this.getUserVipService().queryVipLog(uid, time);
            log.info("userVip:" + userVip.toString());
            //根据uid 查询猫信息
            log.info(String.format("[%s]调用UserCatService().qryCatInfo", uid));
            List<Cat> catList = this.getUserCatService().qryCatInfo(id == 0 ? uid : id, type);
            log.info(String.format("[%s]调用UserCatService().qryCatInfo结束", uid));
            json.success(API_OK);
            json.add("body");
            CatFood catFood1 = this.getUserCatService().qryCatFood(uid);
            log.info("catFood1.getUserName()," + catFood1.toString());
            String userName = "".equals(catFood1.getUserName()) || catFood1.getUserName() == null ? String.valueOf(uid) : catFood1.getUserName();
            log.info("userName:" + userName);
            json.append("userName", userName);
            json.append("catFood", catFood);
            json.append("uid", uid);
            json.append("level", userVip.getLevel());
            json.adds("catInfo");
            for (Cat cat : catList) {
                if (cat != null && cat.getId() != 0) {
                    json.formater();
                    json.append("id", cat.getId());
                    json.append("uid", cat.getUid());
                    json.append("catName", cat.getCatName());
                    // json.append("userName", userName);
                    json.append("catLevel", cat.getCatLevel());
                    json.append("gender", cat.getGender());
                    // json.append("catFood", cat.getCatFood());
                    json.append("state", cat.getState());
                    json.append("maturity", cat.getMaturity());
                    json.append("growth", cat.getGrowth());
                    json.append("bathTime", cat.getBathTime());
                    json.append("clearTime", cat.getClearTime());
                    json.append("shareTime", cat.getShareTime());
                    json.append("remark", cat.getRemark());
                    json.append("IsShovel", cat.getIsShovel());
                    json.append("img", cat.getImg());
                }
            }
            log.info("json:" + json.toString());
            return JSON;
        } catch (Exception e) {
            log.info("获取猫舍信息出错,uid:" + uid);
            json.addError("获取猫舍信息出错");
            log.info("json:" + json.toString());
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
        int catFoodRe = this.getInt("catfood");
        int type = this.getInt("type");//获取动作,根据动作查找猫粮配置表，
        log.info(String.format("uid:[%s],time:[%s],type:[%s],catFood[%s],type:[%s]", uid, time, type, catFoodRe, type));

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
                        log.info(String.format("level:[%s]", level + ",grow:" + grow));
                        catFood = catConfig.getSilverRight();
                        grow = catConfig.getSilverGrowthAdd();
                        log.info(String.format("level:[%s],grow:[%s]", level, grow));
                    } else if (level == 3) {
                        log.info(String.format("level:[%s]", level + ",grow:" + grow));
                        catFood = catConfig.getGoldRight();
                        grow = catConfig.getGoldGrowthAdd();
                        log.info(String.format("level:[%s],grow:[%s]", level, grow));
                    }
                    //检查猫的状态和动作时间
                    Cat cat = this.getUserCatService().findCatStatus(id, uid, type);
                    log.info("catInfo:" + cat.toString());
                    //查询猫粮
                    CatFood catFood1 = this.getUserCatService().qryCatFood(uid);
                    String date = "";
                    String dateNew = "";
                    String name = "";
                   /* if (cat.getState() == 1) {
                        log.info("该猫已经是成熟期了");
                        //TODO 成熟期返回数据
                        log.info("json:" + json.toString());
                        return JSON;
                    }
                    if(cat.getState()  == 2) {
                        log.info("该猫可以升级了");
                    }else*/
                    {
                        if (type == 1) {//分享
                            log.info(String.format("[%s]会员本次操作的动作是[%s],时间是[%s]", uid, catConfig.getName(), time));
                            savetime = cat.getShareTime();
                            date = MonthFound.getDataFormat(savetime, "yyyy-MM-dd");
                            dateNew = MonthFound.getDataFormat(time, "yyyy-MM-dd");
                            name = "分享";
                            if (date.equals(dateNew)) {
                                log.info(String.format("[%s]会员今日已分享过了,不再获得猫粮", uid));
                                json.success(API_OK);
                                json.add("body");
                                json.append("type", type);
                                json.append("state", 0);
                                json.append("msg", String.format("您今日已分享过了,不再获得猫粮"));
                                log.info("json:" + json.toString());
                                return JSON;
                            }
                        } else if (type == 2) {//洗澡
                            log.info(String.format("[%s]会员本次操作的动作是[%s],时间是[%s]", uid, catConfig.getName(), time));
                            savetime = cat.getBathTime();
                            log.info("savetime:" + savetime);
                            date = MonthFound.getDataFormat(savetime, "yyyy-MM-dd");
                            dateNew = MonthFound.getDataFormat(time, "yyyy-MM-dd");
                            name = "洗澡";
                            log.info(String.format("date:[%s],dateNew:[%s]", date, dateNew));
                            if (date.equals(dateNew)) {
                                log.info(String.format("[%s]会员今日已给[%s]猫洗过澡了,不再获得猫粮", uid, id));
                                json.success(API_OK);
                                json.add("body");
                                json.append("type", type);
                                json.append("state", 0);
                                json.append("msg", String.format("您今日已给猫洗过澡了,不再获得猫粮"));
                                log.info("json:" + json.toString());
                                return JSON;
                            }
                        } else if (type == 3) {//喂食
                            name = "喂食";
                            log.info(String.format("[%s]会员本次操作的动作是[%s],时间是[%s]", uid, catConfig.getName(), time));
                            if (catFood1.getCatFood() < catFoodRe) {
                                log.info(String.format("喂食的猫粮数量超过当前拥有的猫粮数量，请重新输入"));
                                json.success(API_OK);
                                json.add("body");
                                json.append("type", type);
                                json.append("state", 0);
                                json.append("msg", String.format("喂食的猫粮数量超过当前拥有的猫粮数量，请重新输入"));
                                log.info("json:" + json.toString());
                                return JSON;
                            }
                            if (cat.getFeedTime() > 0 && cat.getClearTime() <= cat.getFeedTime()) {
                                // 如果铲屎时间小于等于喂食时间，则是已喂食，但未铲屎
                                log.info(String.format("[%s]会员今日已给[%s]猫喂食，但是未铲屎，请铲屎后再喂食。", uid, id));
                                json.success(API_OK);
                                json.add("body");
                                json.append("type", type);
                                json.append("state", 0);
                                json.append("msg", String.format("您今日已给猫喂食了，但是未铲屎，请铲屎后再喂食"));
                                log.info("json:" + json.toString());
                                return JSON;
                            }
                        } else if (type == 4) {//铲屎
                            name = "铲屎";
                            log.info(String.format("[%s]会员本次操作的动作是[%s],时间是[%s]", uid, catConfig.getName(), time));
                            savetime = cat.getFeedTime();
                            //判断铲屎时间是否已经符合2小时后的标准
                            if (savetime == 0L) {
                                //如果喂食时间是0 ，则表明没有进行过喂食，需要先进行喂食，才能铲屎
                                log.info(String.format("[%s]会员尚未给[%s]猫喂食，请在喂食后2小时铲屎", uid, id));
                                json.success(API_OK);
                                json.add("body");
                                json.append("type", type);
                                json.append("state", 0);
                                json.append("msg", String.format("您尚未给猫喂食，请在喂食后2小时铲屎"));
                                log.info("json:" + json.toString());
                                return JSON;
                            } else if ((time - savetime) < 7200000) {
                                //如果时间差小于两小时，则表明有喂食，但是未达到铲屎时间
                                log.info(String.format("[%s]会员已经给[%s]猫喂食，但是未达到两小时时间，请在喂食后2小时铲屎", uid, id));
                                json.success(API_OK);
                                json.add("body");
                                json.append("type", type);
                                json.append("state", 0);
                                json.append("msg", String.format("尚未达到铲屎时间，请在喂食后2小时铲屎"));
                                log.info("json:" + json.toString());
                                return JSON;
                            } else if (cat.getClearTime() > cat.getFeedTime()) {
                                //增加对喂食后已经铲屎的判断
                                log.info(String.format("[%s]会员已经给[%s]猫喂食并且铲屎了", uid, id));
                                json.success(API_OK);
                                json.add("body");
                                json.append("type", type);
                                json.append("state", 0);
                                json.append("msg", String.format("已经给猫铲屎了,请在喂食后2小时尝试铲屎"));
                                log.info("json:" + json.toString());
                                return JSON;
                            }
                        }
                    }
                    if (type == 1 || type == 2) {
                        catFood = catFood /*+catFood1.getCatFood()*/;
                    } else if (type == 3) {
                        catFood = /*catFood1.getCatFood() - */catFoodRe * -1;
                    }


                    //保存猫粮和成长值到正表和历史表中。
                    int state = 0;
                    if (type == 1 || type == 2 || type == 3) {
                        log.info("猫粮存储到CAT_USERINFO表中");
                        //修改catfood
                        int i = 0;
                        i = this.getUserCatService().updateCatFood(uid, catFood);
                        if (i < 1) {
                            log.info("猫粮存储到CAT_USERINFO表失败");
                            json.addError("获取猫粮失败，请稍后再试");
                            log.info("json:" + json.toString());
                            return JSON;
                        }
                    } else if (type == 4) {
                        //查询最近一条喂食记录，转换成成长值入正表和记录表
                        //更改需求，铲屎后成长值平均分配给每只猫。 查询到猫粮后，如果猫粮大于0 则查询用户
                        Cat cat1 = new Cat();
                        log.info("come in qryCatHis,uid" + uid + "id：" + id);
                        cat1 = this.getUserCatService().qryCatHis(uid, id, 3);
                        log.info("end  qryCatHis,cat1" + cat1.toString());
                        grow = grow.multiply(new BigDecimal(cat1.getCatFood() * 0.1));
//                        grow = grow.add(cat.getGrowth());
                        //改需求，成长值平均分配，故state 暂时不变
                        /*if ((grow.add(cat.getGrowth())).compareTo(cat.getMaturity())>0) {
                            log.info(String.format("【%s】下的【%s】成长值已经达到成熟标准。"));
                            state = 1;
                        }*/
                        //查找用户下的猫数量，然后平均分配成长值。
                        List<Cat> list = this.getUserCatService().qryCatInfo(uid, 1);//1根据uid查询
                        if (list == null) {
                            json.addError("该用户下没有猫信息");
                            return JSON;
                        }
                        int count = list.size() <= 1 ? 1 : list.size();
                        log.info("count:" + count + ",grow:" + grow);
                        grow = grow.divide(new BigDecimal(count + ""), 2, BigDecimal.ROUND_HALF_UP);
                        log.info("grow:" + grow);
                        catFood = 0;
                    }
                    int catFoodTemp = 0;
                    if (type == 3) {
                        catFoodTemp = catFoodRe;
                    } else {

                        if (level == 2) {
                            catFoodTemp = catConfig.getSilverRight();
                        } else if (level == 3) {
                            catFoodTemp = catConfig.getGoldRight();
                        }
                    }
                    grow = grow.setScale(2, BigDecimal.ROUND_HALF_UP);//两位小数设置
                    log.info(String.format("start updateCatActTimeByIdAndUidAndTime,uid:[%s],id:[%s],type:[%s],time:[%s],catFoodTemp:[%s] ,catFood:[%s],grow:[%s],name:[%s]", uid, id, type, time, catFoodTemp, catFood, grow, name));
                    this.getUserCatService().updateCatActTimeByIdAndUidAndTime(uid, id, type, time, catFoodTemp, catFood, grow, name, state);

                    json.success(API_OK);
                    json.add("body");
                    json.append("state", 1);
                    json.append("type", type);
                    json.append("catFood", catFood1.getCatFood() + catFood);
                    json.append("grow", grow);
                    json.append("name", name);
                    log.info("json:" + json.toString());
                    return JSON;

                }
            } else {
                log.info("您不是会员，无法获得通过该动作获得猫粮或者成长值");
                json.addError("您不是会员，无法获得通过该动作获得猫粮或者成长值");
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.addError("系统错误，请稍后再试");
        }
        log.info("json:" + json.toString());
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
//        json.add("body");
//        json.adds("catList");
        log.info("循环获取前100名信息");
        JSONObject jsonObject = new JSONObject();

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Cat cat : catList) {
            Map<String, Object> map = new HashMap<>();
            if (cat.getUid() == uid) {
                sign = 1;
            }

            int ver = this.getUserFaceService().findFaceByUid(cat.getUid());
            map.put("id", cat.getId());
            map.put("uid", cat.getUid());
            map.put("userName", cat.getUserName());
            map.put("catLevel", cat.getCatLevel());
            map.put("growth", cat.getGrowth());
            map.put("sign", sign);
            Map<String, Object> map1 = new HashMap<>();
            map1.put("uid", cat.getUid());
            map1.put("ver", ver);
            map.put("facer", map1);

            mapList.add(map);


//            json.append("id", cat.getId());
//            json.append("uid", cat.getUid());
//            json.append("userName", cat.getUserName());
//            json.append("catLevel", cat.getCatLevel());
//            json.append("growth", cat.getGrowth());
//            json.append("sign", sign);
//            json.add("facer");
//            json.append("uid", uid);
//            json.append("ver", ver);
            sign = 0;
        }
        json.success(API_OK);
        jsonObject.put("catList", mapList);
        json.addText("body", jsonObject.toString());
        log.info("json:" + json.toString());
        return JSON;
    }

    /*
     * @NAME:changeName
     * @DESCRIPTION:修改名字（用户或者猫的）
     * @AUTHOR:luxh
     * @DATE:2018/7/27
     * @VERSION:1.0
     */
    public String changeName() throws Exception {
        log.info(String.format("come in changeName"));
        AjaxInfo json = this.getAjaxInfo();
        int type = this.getInt("type");
        String name = this.getString("name");
        long id = 0L;
        int i = 0;
        if (type == 1) {//代表是用户改名字
            id = this.getLong("uid");
            i = this.getUserCatService().updateuserName(id, name);
        } else {
            id = this.getInt("id");
            i = this.getUserCatService().updateName(id, name);
        }
        if (i < 1) {
            json.addError("修改昵称失败，请稍后再试.");
            log.info("json:" + json.toString());
            return JSON;
        } else {
            json.success(API_OK);
            json.addMessage("修改昵称成功");
        }
        return JSON;
    }


    /*
     * @NAME:recovery
     * @DESCRIPTION:猫成熟后回寄，
     * @AUTHOR:luxh
     * @DATE:2018/8/10
     * @VERSION:1.0
     */
    public String recovery() {
        log.info("come in recovery");
        AjaxInfo ajaxInfo = this.getAjaxInfo();
        //首先判断该用户账号下的所有猫的成长值，如果有符合的，则将符合的猫进行回寄，
        long uid = this.getLong("uid");
        int catId = this.getInt("id");
        log.info("uid:" + uid);
        Cat cat = new Cat();
        try {
            cat = this.getUserCatService().qryCatInfoByUidAndId(uid , catId);//type = 1
        } catch (Exception e) {
            log.error("查询猫信息出错");
            e.printStackTrace();
            ajaxInfo.addError("查询猫信息出错");
            return JSON;
        }
        if (cat.getUid()!=uid && cat.getId() !=catId) {
            log.info(String.format("用户【%s】没有猫", uid));
//            ajaxInfo.addError("猫舍没有猫,无法进行回寄");
            ajaxInfo.success(API_OK);
            ajaxInfo.add("body");
            ajaxInfo.append("state", 0);//猫舍没有猫,无法进行回寄
            ajaxInfo.append("msg", "猫舍没有猫,无法进行回寄");
            return JSON;
        } else {
            log.info(String.format("用户【%s】有猫", uid));
            //循环判断猫信息，成长值是否符合回寄标准。
                log.info("cat:" + cat.toString());
                if (cat.getGrowth().compareTo(cat.getMaturity()) >= 0) {
                    log.info(String.format("【%s】猫已经到达成熟期。可以回寄", cat));
                    BigDecimal changeMoney = new BigDecimal("0.00");
                    if (cat.getCatLevel() == 2) {
                        log.info("猫等级为2，回寄获得99元，1%进行捐献");
                        changeMoney = changeMoney.add(new BigDecimal("99.00"));
                    } else if (cat.getCatLevel() == 3) {
                        log.info("猫等级为3，回寄获得990元，1%进行捐献");
                        changeMoney = changeMoney.add(new BigDecimal("990.00"));
                    }
                    changeMoney = changeMoney.setScale(2, BigDecimal.ROUND_HALF_UP);

                    try {
                        //将猫舍中的猫删除 根据CATID
                        log.info("开始删除猫信息,catId:" + cat.getId());
                        int delCount = 0;
                        delCount = this.getUserCatService().delCatInfo(uid, cat.getId());
                        if (delCount < 1) {
                            log.info("删除猫信息失败,catId:" + cat.getId());
//                            ajaxInfo.addError("猫咪回寄失败，请稍后重新尝试");
                            ajaxInfo.success(API_OK);
                            ajaxInfo.add("body");
                            ajaxInfo.append("state", 3);//猫咪回寄失败，请稍后重新尝试
                            ajaxInfo.append("msg", "猫咪回寄失败，请稍后重新尝试");
                        } else {
                            log.info("删除猫信息成功,catId:" + cat.getId());
                            log.info("开始增加用户余额");
                            UserRmbs s = this.getUserMoneyService().findMoneyByUid(uid);
                            UserRmbs rmbs = new UserRmbs();
                            rmbs.setSid(VeStr.getUSid());
                            rmbs.setTid(5);//1,充值,2,提现,3,提现退回,4理财消费,5,理财回款
                            rmbs.setUid(uid);
                            rmbs.setWay("理财回款");
                            rmbs.setEvent("猫咪回寄");
                            rmbs.setCost(s.getTotal());//总额
                            rmbs.setAdds(changeMoney);//返现
                            rmbs.setTotal(s.getTotal().add(changeMoney));//进行此次操作后剩余总额
                            rmbs.setState(0);
                            rmbs.setTime(System.currentTimeMillis());
                            logger.info("rmbs:" + rmbs.toString());
                            this.getUserMoneyService().save(rmbs);
                            ajaxInfo.success(API_OK);
                            ajaxInfo.add("body");
                            ajaxInfo.append("state", 1);//猫咪成长值没有达到要求，无法回寄
                            ajaxInfo.append("msg", "猫咪回寄成功");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                    ajaxInfo.addError("猫咪成长值没有达到要求，无法回寄");
                    ajaxInfo.success(API_OK);
                    ajaxInfo.add("body");
                    ajaxInfo.append("state", 2);//猫咪成长值没有达到要求，无法回寄
                    ajaxInfo.append("msg", "猫咪成长值没有达到要求，无法回寄");
                }

        }
        return JSON;
    }

    /*
     * @NAME:mating
     * @DESCRIPTION:交配
     * @AUTHOR:luxh
     * @DATE:2018/8/11
     * @VERSION:1.0
     */
    public String copulation() {
        AjaxInfo ajaxInfo = this.getAjaxInfo();
        log.info("come in  copulation");
        long uid = this.getLong("uid");
        if (uid < 100000) {
            ajaxInfo.addError("未找到该用户信息，请重新登陆");
            return JSON;
        }
        //根据uid查询猫舍中猫的数量，
        List<Cat> catList = new ArrayList<>();
        try {
            log.info("开始查询猫舍信息,uid:" + uid);
            catList = this.getUserCatService().qryCatInfo(uid, 1);
        } catch (Exception e) {
            log.info("查询猫舍信息出错");
            e.printStackTrace();
        }
        if (catList == null || catList.size() == 0) {
            log.info("该用户下的猫舍没有猫信息");
            ajaxInfo.addError("该用户下的猫舍没有猫信息");
            return JSON;
        } else if (catList.size() >= 2) {
            log.info("该用户猫舍中数量已达上限，不能交配");
            ajaxInfo.success(API_OK);
            ajaxInfo.add("body");
            ajaxInfo.append("state", 3);//该用户猫舍中数量已达上限，不能交配
            ajaxInfo.append("msg", "该用户猫舍中数量已达上限，不能交配");
            return JSON;
        }

        //查询猫状态
        Cat cat = catList.get(0);
        if (cat.getIsCopulatory() != 0) {
            log.info("猫咪已经交配过了，不能在进行交配");
            ajaxInfo.success(API_OK);
            ajaxInfo.add("body");
            ajaxInfo.append("state", 2);//猫咪已经交配过了，不能在进行交配
            ajaxInfo.append("msg", "猫咪已经交配过了，不能在进行交配");
        } else if(cat.getGrowth().compareTo(cat.getMaturity())<0) {
            log.info("猫咪成长值不符合要求，无法交配");
            ajaxInfo.success(API_OK);
            ajaxInfo.add("body");
            ajaxInfo.append("state", 4);//猫咪成长值不符合要求，无法交配
            ajaxInfo.append("msg", "猫咪成长值不符合要求，无法交配");
        }else {
                log.info("猫咪没有交配，可以进行交配");
                //新增一个猫咪 白银会员只能得白银猫，黄金会员可以得黄金猫
                Random random = new Random();
                int num = 0;
                num = random.nextInt(11) + 1;
                Cat cat1 = new Cat();
                cat1.setUserName(String.valueOf(uid));
                cat1.setUid(uid);
                if (num == 11) {
                    log.info("随机生成数字为11,新增黄金猫咪到用户:" + uid);
                    //根据购买的会员等级，新增相对应的猫
//                Cat cat1 =
                    cat1.setCatLevel(3);
                    cat1.setImg(Constant.GOLD_CAT_IMG);
                    cat1.setMaturity(new BigDecimal("10000.00"));

                } else {
                    log.info("随机生成数字不为11,新增白银猫咪到用户:" + uid);
                    cat1.setCatLevel(2);
                    cat1.setImg(Constant.SILVER_CAT_IMG);
                    cat1.setMaturity(new BigDecimal("1000.00"));
                }
                try {
                    log.info("开始新增猫咪");
                    this.getUserCatService().insCat(cat1);
                    log.info("对已经交配的猫咪标记");
                    this.getUserCatService().updateCatCopulatory(cat);
                    ajaxInfo.success(API_OK);
                    ajaxInfo.add("body");
                    ajaxInfo.append("state", 1);//成功交配
                    ajaxInfo.append("msg", "交配成功");
                } catch (Exception e) {
                    log.error("新增猫咪失败，请检查参数,cat:" + cat1.toString());
                    e.printStackTrace();
                    ajaxInfo.success(API_OK);
                    ajaxInfo.add("body");
                    ajaxInfo.append("state", 0);//交配失败
                    ajaxInfo.append("msg", "交配失败");
                }
            }

        return JSON;
    }

    /*
     * @NAME:updateLevel
     * @DESCRIPTION:更新猫咪等级
     * @AUTHOR:luxh
     * @DATE:2018/8/11
     * @VERSION:1.0
     */
    public String updateLevel() {
        AjaxInfo json = this.getAjaxInfo();
        /**
         * 猫咪升级，查看当前成长值，符合条件升级后扣除相应成长值，提升猫咪等级
         */
        long uid = this.getLong("uid");
        int catId = this.getInt("id");
        log.info("查询猫信息,catId," + catId + ",uid:" + uid);
        Cat cat = null;
        try {
            cat = this.getUserCatService().findCatStatus(catId, uid, 0);
        } catch (Exception e) {
            log.error("查看猫信息失败");
            e.printStackTrace();
            json.success(API_OK);
            json.add("body");
            json.append("state", 2);//查看猫信息失败
            json.append("msg", "查看猫信息失败");
        }
        log.info("查询到猫信息:cat:" + cat.toString());
        if (cat.getCatLevel() == 2) {
            log.info("猫咪的等级为2，可以升级");
            BigDecimal updateLevel = new BigDecimal("3000.00");
            if (cat.getGrowth().compareTo(updateLevel) >= 0) {
                log.info("成长值大于所需成长值，可以升级");
                int count = 0;
                try {
                    cat.setImg(Constant.GOLD_CAT_IMG);
                    count = this.getUserCatService().updateCatInfo(cat);
                } catch (Exception e) {
                    log.error("更新猫等级失败");
                    e.printStackTrace();
                }
                log.info("影响" + count + "条数据");
                json.success(API_OK);
                json.add("body");
                json.append("state", 1);//成功
                json.append("msg", "更新猫等级成功");
            }else{
                log.info("成长值不满足条件，不可以升级");
                json.success(API_OK);
                json.add("body");
                json.append("state", 4);//成功
                json.append("msg", "成长值不满足条件，不可以升级");
            }
        } else {
            log.info("猫咪的等级不为2，不可以升级");
            json.success(API_OK);
            json.add("body");
            json.append("state", 3);//猫咪的等级不为2，不可以升级
            json.append("msg", "猫咪的等级不为2，不可以升级");
        }
        return JSON;
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

    public UserMoneyService getUserMoneyService() {
        return userMoneyService;
    }

    public void setUserMoneyService(UserMoneyService userMoneyService) {
        this.userMoneyService = userMoneyService;
    }
}
