package com.ypiao.service;

import com.ypiao.bean.Cat;
import com.ypiao.bean.CatConfig;
import com.ypiao.bean.CatFood;
import com.ypiao.bean.UserInfo;

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
    public List<Cat> qryCatInfo(Long uid,int type)throws  Exception;
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
    public Cat findCatStatus(int id,long uid,int type)throws  Exception;
    /*
     * @NAME:updateCatActTimeByIdAndUidAndTime
     * @DESCRIPTION:更新猫的动作和时间
     * @AUTHOR:luxh
     * @DATE:2018/7/27
     * @VERSION:1.0
     */
    public void updateCatActTimeByIdAndUidAndTime(long uid, int id, int type, long time, int catfoodCahnge ,int catFood, BigDecimal grow,String name,int state )throws Exception ;

    /*
     * @NAME:findRankList
     * @DESCRIPTION:查找成长值前100 的猫
     * @AUTHOR:luxh
     * @DATE:2018/7/27
     * @VERSION:1.0
     */
    public List<Cat> findRankList() throws Exception;
    /*
     * @NAME:updateName
     * @DESCRIPTION:修改猫姓名
     * @AUTHOR:luxh
     * @DATE:2018/7/27
     * @VERSION:1.0
     */
    public int updateName(long id,String name) throws Exception;
    /*
     * @NAME:insCat
     * @DESCRIPTION:创建猫
     * @AUTHOR:luxh
     * @DATE:2018/7/27
     * @VERSION:1.0
     */
    public void insCat(Cat cat)throws  Exception;

    /*
     * @NAME:qryCatHis
     * @DESCRIPTION:
     * @AUTHOR:luxh
     * @DATE:2018/7/28
     * @VERSION:1.0
     */
    public Cat qryCatHis(long uid,int id ,int type )throws Exception ;

    /*
     * @NAME:qryCatFood
     * @DESCRIPTION:查询猫粮
     * @AUTHOR:luxh
     * @DATE:2018/7/29
     * @VERSION:1.0
     */
    public CatFood qryCatFood(long uid)throws Exception ;

    /*
     * @NAME:updateCatFood
     * @DESCRIPTION:保存猫粮
     * @AUTHOR:luxh
     * @DATE:2018/7/29
     * @VERSION:1.0
     */
    public int updateCatFood(long uid ,int catfood) throws Exception ;
    /*
     * @NAME:updateIsMember
     * @DESCRIPTION:购买会员后修改isMember字段为1
     * @AUTHOR:luxh
     * @DATE:2018/7/29
     * @VERSION:1.0
     */
    public int updateIsMember(long uid ,int isMember) throws Exception;
    /*
     * @NAME:updateuserName
     * @DESCRIPTION:更新用户昵称
     * @AUTHOR:luxh
     * @DATE:2018/7/29
     * @VERSION:1.0
     */
    public int updateuserName(long uid ,String userName) throws Exception;

    /*
     * @NAME:updateuserName
     * @DESCRIPTION:保存猫粮和备注,供邀请奖励统计使用
     * @AUTHOR:luxh
     * @DATE:2018/7/29
     * @VERSION:1.0
     */

    public int updateCatFood(long uid, int catfood,String remark) throws Exception;
    /*
     * @NAME:delCatInfo
     * @DESCRIPTION:删除猫信息
     * @AUTHOR:luxh
     * @DATE:2018/8/11
     * @VERSION:1.0
     */
    public int delCatInfo(long uid,int catId) throws  Exception;
}
