package com.ypm.util;

import java.util.ArrayList;
import java.util.List;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;

/**
 * 个推推送.
 * 
 * Created by xk on 2018-06-05.
 */
public final class GeTui {

    public static final String APP_ID = "";
    
    public static final String APP_KEY = "";
    
    public static final String MASTER_SECRET = "";
    
    public static final String URL = "http://sdk.open.api.igexin.com/apiex.htm";
    
    /**
     * 快速入门 
     */
    @SuppressWarnings("deprecation")
	public static void quickStart (String title, String text, String url) {
    	IGtPush push = new IGtPush( URL, APP_KEY, MASTER_SECRET );

        // 定义"点击链接打开通知模板"，并设置标题、内容、链接
        LinkTemplate template = new LinkTemplate();
        template.setAppId(APP_ID);
        template.setAppkey(APP_KEY);
        template.setTitle(title);
        template.setText(text);
        template.setUrl(url);

        List<String> appIds = new ArrayList<String>();
        appIds.add(APP_ID);

        // 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表、是否支持离线发送、以及离线消息有效期(单位毫秒)
        AppMessage message = new AppMessage();
        message.setData(template);
        message.setAppIdList(appIds);
        message.setOffline(true);
        message.setOfflineExpireTime( 1000 * 600 );

        IPushResult ret = push.pushMessageToApp(message);
        System.out.println(ret.getResponse().toString());
    }
}
