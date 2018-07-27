package com.ypiao.service;

import com.ypiao.bean.Cat;
import com.ypiao.bean.CatConfig;

import java.math.BigDecimal;
import java.util.List;

/**
 * @NAME:UserCatService
 * @DESCRIPTION:猫舍
 * @AUTHOR:luxh
 * @DATE:2018/7/25
 * @VERSION:1.0
 */
public interface UserCatService {
    /*
     * @NAME:qryCatInfo
     * @DESCRIPTION:根据用户uid查询猫信息
     * @AUTHOR:luxh
     * @DATE:2018/7/25
     * @VERSION:1.0
     */
    public List<Cat> qryCatInfo(Long uid)throws  Exception;
    /*
     * @NAME:findcatConfig
     * @DESCRIPTION:根据ID查找配置
     * @AUTHOR:luxh
     * @DATE:2018/7/26
     * @VERSION:1.0
     */
    public CatConfig findcatConfig(int id)throws  Exception;
    /*
     * @NAME:findCatStatus
     * @DESCRIPTION:查找猫的动作当天是否已经完成。
     * @AUTHOR:luxh
     * @DATE:2018/7/26
     * @VERSION:1.0
     */
    public Cat findCatStatus(int id,long uid)throws  Exception;
    /*
     * @NAME:updateCatActTimeByIdAndUidAndTime
     * @DESCRIPTION:更新猫的动作和时间
     * @AUTHOR:luxh
     * @DATE:2018/7/27
     * @VERSION:1.0
     */
    public void updateCatActTimeByIdAndUidAndTime(long uid, int id, int type, long time, int catFood, BigDecimal grow,String name )throws Exception ;

    public List<Cat> findRankList() throws Exception;
}
