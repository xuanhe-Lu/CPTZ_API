package com.ypiao.json;

import com.alibaba.fastjson.JSONObject;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.LuckyBagReceive;
import com.ypiao.bean.LuckyBagSend;
import com.ypiao.bean.UserSession;
import com.ypiao.service.LuckyBagService;
import com.ypiao.service.UserFaceService;
import com.ypiao.service.UserMoneyService;
import com.ypiao.util.GMTime;
import com.ypiao.util.RadomLuckBag;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @NAME:OnRedEnvelopes
 * @DESCRIPTION:福袋分享
 * @AUTHOR:luxh
 * @DATE:2018/8/1
 * @VERSION:1.0
 */
public class OnRedEnvelopes extends Action {
    private static final long serialVersionUID = 2317478859910903480L;
    private static Logger logger = Logger.getLogger(OnRedEnvelopes.class);

    private LuckyBagService luckyBagService;
    private UserMoneyService userMoneyService;
    private UserFaceService userFaceService;

    public OnRedEnvelopes() {
        super(true);
    }

    /*
     * @NAME:index
     * @DESCRIPTION:福袋分享接口
     * @AUTHOR:luxh
     * @DATE:2018/8/2
     * @VERSION:1.0
     */
    @Override
    public String index() {
        logger.info("come in index");
        AjaxInfo json = this.getAjaxInfo();
        UserSession us = this.getUserSession();
        long uid = us.getUid();
//       long uid = this.getLong("uid");
        long giftId = this.getLong("giftid");
        //通过giftId 查找luckyBag_send 表中的数据
        LuckyBagSend luckyBagSend = new LuckyBagSend();
        try {
            long time = System.currentTimeMillis();
            logger.info(String.format("查找用户【%s】下的【%s】福袋", uid, giftId));
            luckyBagSend = this.getLuckyBagService().findLuckBagInfo(giftId, uid, time);
            logger.info(String.format("查到福袋信息为:%s", luckyBagSend.toString()));
            if (luckyBagSend.getBagId() != giftId || luckyBagSend.getUid() != uid) {
                logger.info("该福袋已过期或不存在，请重新选择分享的福袋");
                json.addError("该福袋已过期或不存在，请重新选择分享的福袋");
                return JSON;
            }

        } catch (Exception e) {
            logger.error("查到福袋信息出错,请重新确认");
            e.printStackTrace();
            json.addError("查到福袋信息出错,请重新确认");
            return JSON;
        }
        //检查是否已经有该GIFTID的福袋在send
        long bagId = 0;
        try {
            bagId = this.getLuckyBagService().findBagById(luckyBagSend.getBagId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(bagId == luckyBagSend.getBagId()) {
            LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
            try {
                luckyBagReceive = this.getLuckyBagService().findMaxMoney(bagId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            logger.info("该福袋已经生成过");
            json.success(API_OK);
            json.add("body");
            json.append("count",luckyBagReceive.getRedId());
            json.append("moneyMax",luckyBagReceive.getMoney());
        }else {
            logger.info("未找到该福袋，可以生成");
            logger.info("开始生成随机红包");
            RadomLuckBag radomLuckBag = new RadomLuckBag();
            ConcurrentLinkedQueue<BigDecimal> bagList = new ConcurrentLinkedQueue<>();
            try {
                bagList = radomLuckBag.getBag(luckyBagSend.getBagCount(), luckyBagSend.getNum() - 1, luckyBagSend.getLastEnvelopes());
            } catch (InterruptedException e) {
                logger.info("生成随机红包失败");
                e.printStackTrace();
            }
            logger.info("生成随机红包结束，开始存入数据库，bagList：" + bagList.toString());
            int count = bagList.size();
            long time = 86400000 + System.currentTimeMillis();//失效时间
            int num = 1;
            BigDecimal moneyMax = new BigDecimal("0.00");
            for (BigDecimal bigDecimal : bagList) {
                LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
                luckyBagReceive.setMoney(bigDecimal);
                luckyBagReceive.setBagId(luckyBagSend.getBagId());
                luckyBagReceive.setFailureTime(time);
                luckyBagReceive.setRedId(num);
                luckyBagReceive.setUid(uid);
                moneyMax = luckyBagReceive.getMoney();
                try {
                    logger.info("更新updateSend操作第" + num + "次，参数为:" + luckyBagReceive.toString());
                    this.getLuckyBagService().updateSend(luckyBagReceive);
                    num = num + 1;
                    logger.info("更新updateSend操作完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("count", count);
            map.put("moneyMax", moneyMax);
            json.success(API_OK);
            json.add("body");
            json.append("count",Integer.parseInt(map.get("count")+""));
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(map.get("moneyMax")));
            json.append("moneyMax",bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP));
        }

        return JSON;
    }

    private void getBagInfo(AjaxInfo json, long uid, long giftId) {

    }

    /*
     * @NAME:persionalBag
     * @DESCRIPTION:个人福袋查询
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public String persionalBag() {
        logger.info("come in persionalBag");
        AjaxInfo json = this.getAjaxInfo();
        UserSession us = this.getUserSession();
        long uid = us.getUid();
        logger.info("uid:"+uid);
//        uid = this.getLong("uid");
        int type = this.getInt("type");//获取的数据类型 0，已失效，1，未失效
        long time = 1;
      /*  if (type == 1) {
            time = 1;
        } else {
            time = System.currentTimeMillis();
        }*/
        List<LuckyBagSend> luckyBagSendList = null;
        try {
            logger.info("come in findPersionalBag");
            luckyBagSendList = this.getLuckyBagService().findPersionalBag(uid, type);
            logger.info("end findPersionalBag");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (LuckyBagSend luckyBagSend : luckyBagSendList) {
            Map<String, Object> map = new HashMap<>();
            map.put("bagCount", luckyBagSend.getBagCount());//福袋总额
            map.put("bagId",luckyBagSend.getBagId());
            map.put("name", "福袋红包");
            map.put("lendMoney", luckyBagSend.getLendMoney());
            map.put("num", luckyBagSend.getNum());
            //TODO
            map.put("moneyMax", (luckyBagSend.getBagCount().multiply(luckyBagSend.getLastEnvelopes())).setScale(2, BigDecimal.ROUND_HALF_UP));

            map.put("startTime", GMTime.format(luckyBagSend.getCreateTime(), GMTime.CHINA));
//            map.put("startTime", GMTime.format(luckyBagSend.getSendTime(), GMTime.CHINA));
            map.put("failureTime", GMTime.format(luckyBagSend.getFailureTime(), GMTime.CHINA));
            mapList.add(map);
        }
//        jsonObject.put("bagList", luckyBagSendList);
        jsonObject.put("bagList", mapList);
        logger.info("json:" + jsonObject.toString());
        json.success(API_OK);
        json.addText("body", jsonObject.toString());
        logger.info("json:" + json.toString());
        return JSON;
    }

    /*
     * @NAME:luckyBagSave
     * @DESCRIPTION:领取福袋
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public String luckyBagSave() {
        logger.info("COME IN luckyBagHis");
        AjaxInfo ajaxInfo = this.getAjaxInfo();
        long uid = this.getLong("uid");
        long giftId = this.getLong("giftid");
        //针对安卓，查找是否已生成，未生成红包的先生成红包



        //通过giftId 查找luckyBag_send 表中的数据
        LuckyBagSend luckyBagSend = new LuckyBagSend();
        try {
            long time = System.currentTimeMillis();
            logger.info(String.format("查找【%s】福袋,时间是[%s]",  giftId,time));
            luckyBagSend = this.getLuckyBagService().findLuckBagInfo(giftId, time);
            logger.info(String.format("查到福袋信息为:%s", luckyBagSend.toString()));
            if (luckyBagSend.getBagId() != giftId ) {
                logger.info("该福袋已过期或不存在，请重新选择福袋");
                ajaxInfo.addError("该福袋已过期或不存在，请重新选择福袋");
                return JSON;
            }

        } catch (Exception e) {
            logger.error("查到福袋信息出错,请重新确认");
            e.printStackTrace();
            ajaxInfo.addError("查到福袋信息出错,请重新确认");
            return JSON;
        }
        //检查是否已经有该GIFTID的福袋在send
        long bagId = 0;
        try {
            bagId = this.getLuckyBagService().findBagById(luckyBagSend.getBagId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(bagId == luckyBagSend.getBagId()) {
            LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
            try {
                luckyBagReceive = this.getLuckyBagService().findMaxMoney(bagId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            logger.info("该福袋已经生成过");
//            ajaxInfo.success(API_OK);
//            ajaxInfo.add("body");
//            ajaxInfo.append("count",luckyBagReceive.getRedId());
//            ajaxInfo.append("moneyMax",luckyBagReceive.getMoney());
        }else {
            logger.info("未找到该福袋，可以生成");
            logger.info("开始生成随机红包");
            RadomLuckBag radomLuckBag = new RadomLuckBag();
            ConcurrentLinkedQueue<BigDecimal> bagList = new ConcurrentLinkedQueue<>();
            try {
                bagList = radomLuckBag.getBag(luckyBagSend.getBagCount(), luckyBagSend.getNum() - 1, luckyBagSend.getLastEnvelopes());
            } catch (InterruptedException e) {
                logger.info("生成随机红包失败");
                e.printStackTrace();
            }
            logger.info("生成随机红包结束，开始存入数据库，bagList：" + bagList.toString());
            int count = bagList.size();
            long time = 86400000 + System.currentTimeMillis();//失效时间
            int num = 1;
//            BigDecimal moneyMax = new BigDecimal("0.00");
            for (BigDecimal bigDecimal : bagList) {
                LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
                luckyBagReceive.setMoney(bigDecimal);
                luckyBagReceive.setBagId(luckyBagSend.getBagId());
                luckyBagReceive.setFailureTime(time);
                luckyBagReceive.setRedId(num);
                luckyBagReceive.setUid(luckyBagSend.getUid());
//                moneyMax = luckyBagReceive.getMoney();
                try {
                    logger.info("更新updateSend操作第" + num + "次，参数为:" + luckyBagReceive.toString());
                    this.getLuckyBagService().updateSend(luckyBagReceive);
                    num = num + 1;
                    logger.info("更新updateSend操作完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }





        //首先查询是否已经过期
        long result = 0;
        try {
            result = this.getLuckyBagService().qryIsExpire(giftId);
        } catch (Exception e) {
            logger.error("查询福袋是否过期失败，");
            e.printStackTrace();
        }
        if (result <= 0) {
            logger.info("该福袋已经过期了");
            ajaxInfo.success(API_OK);
            ajaxInfo.add("body");
            ajaxInfo.append("msg","该福袋已经过期了");
            ajaxInfo.append("state",0);//过期
            logger.info("ajaxInfo:"+ajaxInfo);
            return JSON;
        }
        //查询是否已经领取
        LuckyBagReceive luckyBagReceive = null;
        try {
            logger.info("查询福袋是否领取");
            luckyBagReceive = this.getLuckyBagService().qryluckyBagHis(uid, giftId);
        } catch (Exception e) {
            logger.error("查询福袋是否领取失败，");
            e.printStackTrace();
        }
        if (luckyBagReceive.getUid() == uid) {
            logger.error("该福袋已经领取过了");
            ajaxInfo.success(API_OK);

            try {
                Map<String, Object> objectMap = getbagHis(giftId);
                objectMap.put("state",1);//已领取
                objectMap.put("msg","该福袋已经领取过了");//已领取
                JSONObject jsonObject = new JSONObject(objectMap);
                ajaxInfo.success(API_OK);
                ajaxInfo.addText("body",jsonObject.toString());
                logger.info("ajaxInfo:"+ajaxInfo.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //TODO 返回该福袋领取历史
            return JSON;
        }
        //查询是否已经全部领取
        List<LuckyBagReceive> luckyBagReceives = new ArrayList<>();
        try {
            luckyBagReceives = this.getLuckyBagService().qryIsout(giftId);
        } catch (Exception e) {
            logger.info("查询福袋是否全部领取失败");
            e.printStackTrace();
            ajaxInfo.addError("查询福袋是否全部领取失败");
            return JSON;
        }
        if (luckyBagReceives.size() <= 0) {
            logger.error(String.format("%s该福袋已经全部领取", giftId));
            ajaxInfo.success(API_OK);
            ajaxInfo.add("body");
            ajaxInfo.append("msg","该福袋已经全部领取");
            ajaxInfo.append("state",2);//全部领取.
            return JSON;
        } else {
            logger.info(String.format("%s该福袋尚未全部领取", giftId));
            LuckyBagReceive luckyBagReceive1 = new LuckyBagReceive();
            try {
                luckyBagReceive1 = this.getLuckyBagService().qryIsNotout(giftId);
            } catch (Exception e) {
                logger.error("查询福袋尚未领取部分失败");
                e.printStackTrace();
                ajaxInfo.addError("查询福袋尚未领取部分失败");
                return JSON;
            }
            if (luckyBagReceive1.getMoney().doubleValue() > 0) {
                synchronized (doLock(giftId)) {
                    //保存领取福袋到receive
                    luckyBagReceive1.setUid(uid);
                    luckyBagReceive1.setTime(System.currentTimeMillis());
                    try {
                        logger.info("更新福袋记录到福袋记录表,luckyBagReceive1;" + luckyBagReceive1.toString());
                        this.getLuckyBagService().updateUidAndTime(luckyBagReceive1);
                        logger.info("增加用户余额,");
                        this.getLuckyBagService().saveRmbs(luckyBagReceive1);
                        logger.info("增加用户余额结束");
                        logger.info("查询该福袋的所有领取记录");
                        Map<String, Object> objectMap = getbagHis(giftId);
                        objectMap.put("money",luckyBagReceive1.getMoney());
                        objectMap.put("state",3);//领取成功
                        JSONObject jsonObject = new JSONObject(objectMap);
                        ajaxInfo.success(API_OK);
                        ajaxInfo.addText("body",jsonObject.toString());
                        logger.info("ajaxInfo:"+ajaxInfo.toString());
                        return JSON;
                    } catch (Exception e) {
                        logger.error("保存领取福袋失败");
                        e.printStackTrace();
                    }
                }
            } else {
                logger.info("红包金额为0");
            }
        }

        ajaxInfo.success(API_OK);
        return JSON;
    }

    /*
     * @NAME:getbagHis
     * @DESCRIPTION:获取福袋历史记录对外接口
     * @AUTHOR:luxh
     * @DATE:2018/8/4
     * @VERSION:1.0
     */
    public String getbagHis(){
        long bagId = this.getLong("giftid");
        AjaxInfo ajaxInfo= this.getAjaxInfo();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = getbagHis(bagId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ajaxInfo.success(API_OK);
        ajaxInfo.addText("body",jsonObject.toString());
        return  JSON;
    }


    /*
     * @NAME:getbagHis
     * @DESCRIPTION:获取福袋历史记录
     * @AUTHOR:luxh
     * @DATE:2018/8/4
     * @VERSION:1.0
     */
    private JSONObject getbagHis(long giftId) throws Exception {
        List<LuckyBagReceive> luckyBagReceiveList = this.getLuckyBagService().qryBagHis(giftId);
        List<Map<String, Object>> list = new ArrayList<>();
        LuckyBagReceive luckyBagReceive2 = this.getLuckyBagService().findMaxMoney(giftId);
        int count = luckyBagReceive2.getRedId();
        int num = 1;
        for (LuckyBagReceive bagReceive : luckyBagReceiveList) {
            int state = 0;
            if(count == num){
                state =  1;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("redId", bagReceive.getRedId());
            map.put("uid", bagReceive.getUid());
            map.put("money", bagReceive.getMoney());
            map.put("time", bagReceive.getTime());
            map.put("state", state);
            int ver = this.getUserFaceService().findFaceByUid(bagReceive.getUid());
            Map<String,Object>map1 = new HashMap<>();
            map1.put("uid",bagReceive.getUid());
            map1.put("ver",ver);
            map.put("facer",map1);
            list.add(map);
            num = num +1;
        }
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("bag",list);
        JSONObject jsonObject = new JSONObject(objectMap);
        return jsonObject;
    }




    public LuckyBagService getLuckyBagService() {
        return luckyBagService;
    }

    public void setLuckyBagService(LuckyBagService luckyBagService) {
        this.luckyBagService = luckyBagService;
    }

    public UserMoneyService getUserMoneyService() {
        return userMoneyService;
    }

    public void setUserMoneyService(UserMoneyService userMoneyService) {
        this.userMoneyService = userMoneyService;
    }

    public UserFaceService getUserFaceService() {
        return userFaceService;
    }

    public void setUserFaceService(UserFaceService userFaceService) {
        this.userFaceService = userFaceService;
    }
}
