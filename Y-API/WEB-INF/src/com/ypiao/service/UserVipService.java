package com.ypiao.service;

/*
 * @CLASSNAME:UserVipService
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/7/22
 * @VERSION:1.0
 */

import com.ypiao.bean.UserVip;

public interface UserVipService {
    /**
     *  查询用户VIP信息
     * @param uid
     * @return
     */
    public UserVip queryVipLog(long uid,long endTime) throws Exception;

    /**
     * 记录用户vip信息
     * @param userVip
     */
    public void uptVipLog(UserVip userVip) throws Exception;
}
