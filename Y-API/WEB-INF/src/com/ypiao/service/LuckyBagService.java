package com.ypiao.service;

import com.ypiao.bean.LuckyBagBouns;
import com.ypiao.bean.LuckyBagCondfig;
import com.ypiao.bean.LuckyBagReceive;
import com.ypiao.bean.LuckyBagSend;

import java.math.BigDecimal;
import java.util.List;

/*
 * @NAME:LuckyBagService
 * @DESCRIPTION:福袋分享接口
 * @AUTHOR:luxh
 * @DATE:2018/8/2
 * @VERSION:1.0
 */
public interface LuckyBagService {
    /*
     * @NAME:qryLuckyBagConfig
     * @DESCRIPTION:查询福袋规则
     * @AUTHOR:luxh
     * @DATE:2018/8/2
     * @VERSION:1.0
     */
    public LuckyBagCondfig qryLuckyBagConfig(BigDecimal money) throws Exception;

    /*
     * @NAME:insertLuckBag
     * @DESCRIPTION:插入福袋send
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public int insertLuckBag(LuckyBagSend luckyBagSend) throws Exception;

    /*
     * @NAME:findLuckBagInfo
     * @DESCRIPTION:查找福袋信息
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public LuckyBagSend findLuckBagInfo(long giftId, long uid, long time) throws Exception;

    /*
     * @NAME:updateSend
     * @DESCRIPTION:发送福袋，更新福袋记录
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public void updateSend(LuckyBagReceive luckyBagReceive) throws Exception;

    /*
     * @NAME:findPersionalBag
     * @DESCRIPTION:发现个人福袋列表
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public List<LuckyBagSend> findPersionalBag(long uid, long time) throws Exception;

    /*
     * @NAME:qryluckyBagHis
     * @DESCRIPTION:查询该福袋，用户是否已经领取
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public LuckyBagReceive qryluckyBagHis(long uid, long time) throws Exception;

    /*
     * @NAME:qryIsExpire
     * @DESCRIPTION:查询该福袋是否已经过期
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public long qryIsExpire(long giftId) throws Exception;

    /*
     * @NAME:qryIsout
     * @DESCRIPTION:查看福袋是否已经全部领取
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public List<LuckyBagReceive> qryIsout(long giftId) throws Exception;
    /*
     * @NAME:qryIsNotout
     * @DESCRIPTION:查看福袋尚未领取
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public LuckyBagReceive qryIsNotout(long giftId) throws Exception;
    /*
     * @NAME:updateUidAndTime
     * @DESCRIPTION:更新福袋领取记录
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public void updateUidAndTime(LuckyBagReceive luckyBagReceive) throws Exception;
    /*
     * @NAME:saveRmbs
     * @DESCRIPTION:保存金额到用户表
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public void saveRmbs(LuckyBagReceive luckyBagReceive) throws Exception;

    public LuckyBagBouns qryPersionnalBouns(long uid) throws Exception;
    public void updateBouns(BigDecimal rmb,long uid) throws Exception ;

    public long  findBagById(long giftId)  throws Exception;
}
