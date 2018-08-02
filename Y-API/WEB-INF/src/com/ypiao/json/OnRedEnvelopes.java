package com.ypiao.json;

import com.ypiao.bean.AjaxInfo;
import org.apache.log4j.Logger;

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
        long uid = this.getLong("uid");
        
        return createRedEnvelopes();
    }

    public String createRedEnvelopes() {
        return null;
    }
}
