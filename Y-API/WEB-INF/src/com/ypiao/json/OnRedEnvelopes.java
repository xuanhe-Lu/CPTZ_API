package com.ypiao.json;

import com.alibaba.fastjson.JSONObject;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.LuckyBagReceive;
import com.ypiao.bean.LuckyBagSend;
import com.ypiao.bean.UserSession;
import com.ypiao.service.LuckyBagService;
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
        AjaxInfo json = new AjaxInfo();
        UserSession us = this.getUserSession();
        long uid = us.getUid();
        long giftId = this.getLong("giftid");
        //通过giftId 查找luckyBag_send 表中的数据
        LuckyBagSend luckyBagSend = new LuckyBagSend();
        try {
            long time = System.currentTimeMillis();
            logger.info(String.format("查找用户【%s】下的【%s】福袋", uid, giftId));
            luckyBagSend = this.getLuckyBagService().findLuckBagInfo(giftId, uid, time);
            logger.info(String.format("查到福袋信息为:%s", luckyBagSend.toString()));
            if (luckyBagSend.getBagId() != giftId || luckyBagSend.getUid() != uid) {
                logger.info("该福袋已过期，请重新选择分享的福袋");
                json.addError("该福袋已过期，请重新选择分享的福袋");
                return JSON;
            }
        } catch (Exception e) {
            logger.error("查到福袋信息出错,请重新确认");
            e.printStackTrace();
            json.addError("查到福袋信息出错,请重新确认");
        }
        logger.info("开始生成随机红包");
        RadomLuckBag radomLuckBag = new RadomLuckBag();
        ConcurrentLinkedQueue<BigDecimal> bagList = new ConcurrentLinkedQueue<>();
        try {
            bagList = radomLuckBag.getBag(luckyBagSend.getBagCount(), luckyBagSend.getNum(), luckyBagSend.getLastEnvelopes());
        } catch (InterruptedException e) {
            logger.info("生成随机红包失败");
            e.printStackTrace();
        }
        logger.info("生成随机红包结束，开始存入数据库，bagList：" + bagList.toString());
        int count = bagList.size();
        long time = luckyBagSend.getFailureTime() + System.currentTimeMillis();//失效时间
        int num = 1;
        for (BigDecimal bigDecimal : bagList) {
            LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
            luckyBagReceive.setMoney(bigDecimal);
            luckyBagReceive.setBagId(luckyBagSend.getBagId());
            luckyBagReceive.setFailureTime(time);
            luckyBagReceive.setRedId(num);
            try {
                logger.info("更新updateSend操作第" + num + "次，参数为:" + luckyBagReceive.toString());
                this.getLuckyBagService().updateSend(luckyBagReceive);
                logger.info("更新updateSend操作完成");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        json.success(API_OK);
        return JSON;
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
        int type = this.getInt("type");//获取的数据类型 0，已失效，1，未失效
        long time = 1;
        if(type == 1){
            time = 1;
        }else{
            time = System.currentTimeMillis();
        }
        List<LuckyBagSend> luckyBagSendList = null;
        try {
            luckyBagSendList = this.getLuckyBagService().findPersionalBag(uid,time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (LuckyBagSend luckyBagSend : luckyBagSendList) {
            Map<String,Object> map = new HashMap<>();
            map.put("money",luckyBagSend.getBagCount());//福袋总额
            map.put("name","福袋红包");
            map.put("startTime",luckyBagSend.getCreateTime());
            mapList.add(map);
        }
        jsonObject.put("bagList",luckyBagSendList);
        logger.info("json:"+jsonObject.toString());
        json.addText("body",jsonObject.toString());
        logger.info("json:"+json.toString());
        return JSON;
    }

    /*
     * @NAME:luckyBagHis
     * @DESCRIPTION:根据福袋ID，uid查询用户是否抽取
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public String luckyBagHis(){
        logger.info("COME IN luckyBagHis");
        AjaxInfo ajaxInfo = this.getAjaxInfo();
        long uid = this.getLong("uid");
        long giftId = this.getLong("giftid");
        //首先查询是否已经过期
        long result = 0;
        try {
            result = this.getLuckyBagService().qryIsExpire(giftId);
        } catch (Exception e) {
            logger.error("查询福袋是否过期失败，");
            e.printStackTrace();
        }
        if(result<=0){
            logger.info("该福袋已经过期了");
            ajaxInfo.addError("该福袋已经过期了");
            return JSON;
        }
        //查询是否已经领取
//        this.getLuckyBagService().qryluckyBagHis(uid,giftId);
        //查询是否已经




        return JSON;
    }
    public LuckyBagService getLuckyBagService() {
        return luckyBagService;
    }

    public void setLuckyBagService(LuckyBagService luckyBagService) {
        this.luckyBagService = luckyBagService;
    }
}
