package com.ypiao.service;

import com.ypiao.bean.AjaxInfo;

import java.util.List;

/**
 * @NAME:UserAttenService
 * @DESCRIPTION: 签到相关service
 * @AUTHOR:luxh
 * @DATE:2018/7/16
 * @VERSION:1.0
 */
public interface UserAttenService {
    public int save(long time,long uid,int count) throws Exception;

    public int findUserCountByMaxTime() throws Exception;

    public List<Long> findUserAtten(long date, long uid, AjaxInfo json) throws Exception;


}
