package com.ypiao.service;

import com.ypiao.bean.Lottery;
import com.ypiao.bean.LotteryLog;

import java.util.List;

/*
 * @CLASSNAME:UserDrawService
 * @DESCRIPTION: 用户抽奖
 * @AUTHOR:luxh
 * @DATE:2018/7/19
 * @VERSION:1.0
 */
public interface UserLotteryService {
    /*
     * @NAME:findLotteryNumber
     * @DESCRIPTION:查找抽奖次数
     * @AUTHOR:luxh
     * @DATE:2018/7/19
     * @VERSION:1.0
     */
    public int findLotteryCountByUidAndTime(long uid) throws Exception ;

    /*
     * @NAME:findLotteryConfig
     * @DESCRIPTION:查询抽奖配置
     * @AUTHOR:luxh
     * @DATE:2018/7/19
     * @VERSION:1.0
     */
    public List<Lottery> findLotteryConfig() throws Exception ;

    /*
     * @NAME:findLotteryProbability
     * @DESCRIPTION:查询抽奖总概率
     * @AUTHOR:luxh
     * @DATE:2018/7/19
     * @VERSION:1.0
     */
    public int findLotteryProbability() throws Exception ;

    /*
     * @NAME:saveLotteryLog
     * @DESCRIPTION:保存抽奖奖励
     * @AUTHOR:luxh
     * @DATE:2018/7/20
     * @VERSION:1.0
     */
    public void saveLotteryLog(LotteryLog lotteryLog) throws Exception ;

    /*
     * @NAME:${METHODNAME}
     * @DESCRIPTION:
     * @AUTHOR:luxh
     * @DATE:${time}${date}
     * @VERSION:1.0
     */
    public void  addLotterCount() throws Exception ;
}
