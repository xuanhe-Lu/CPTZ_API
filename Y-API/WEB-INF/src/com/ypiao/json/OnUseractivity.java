package com.ypiao.json;

import com.ypiao.bean.AjaxInfo;

/**
 * @NAME:OnUseractivity
 * @DESCRIPTION:会员四重好礼活动
 * @AUTHOR:luxh
 * @DATE:2018/7/31
 * @VERSION:1.0
 */
public class OnUseractivity extends  Action {
    /*
     * @NAME:index
     * @DESCRIPTION:查询用户四重好礼奖励
     * @AUTHOR:luxh
     * @DATE:2018/7/31
     * @VERSION:1.0
     */
    @Override
    public String index() {
        AjaxInfo json  = this.getAjaxInfo();
        // 一重好礼，邀请获得加息券
        return null;
    }

    public OnUseractivity() {
        super(true);
    }


}
