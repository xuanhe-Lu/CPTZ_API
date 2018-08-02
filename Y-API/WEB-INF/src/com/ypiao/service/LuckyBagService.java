package com.ypiao.service;

import com.ypiao.bean.LuckyBagCondfig;

import java.math.BigDecimal;

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
    public LuckyBagCondfig qryLuckyBagConfig(BigDecimal money)  throws  Exception ;
}
