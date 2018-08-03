package com.ypiao.json;

import com.alibaba.fastjson.JSONObject;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.Information;
import com.ypiao.service.InformationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @NAME:OnInformation
 * @DESCRIPTION:理财资讯
 * @AUTHOR:luxh
 * @DATE:2018/8/3
 * @VERSION:1.0
 */
public class OnInformation extends Action {

    private static final long serialVersionUID = 2133212567150057867L;

    private InformationService informationService;
    public OnInformation() {
        super(true);
    }

    @Override
    public String index() {
        AjaxInfo ajaxInfo = this.getAjaxInfo();
        int type = this.getInt("type");
        List<Information> list = null;
        try {
            list = this.getInformationService().findInfo(type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Map<String,Object>>mapList = new ArrayList<>();
        for (Information information : list) {
            Map<String,Object> map = new HashMap<>();
            map.put("type",information.getType() );
            map.put("img",information.getImg() );
            map.put("name",information.getName() );
            map.put("time",information.getTime() );
            map.put("url",information.getUrl() );
            map.put("dist",information.getDist() );
            mapList.add(map);
        }
        JSONObject jsonObject  = new JSONObject();
        jsonObject.put("info",mapList);
        ajaxInfo.addText("body",jsonObject.toString());
        logger.info("ajaxInfo:"+ajaxInfo.toString());
        return JSON;
    }

    public InformationService getInformationService() {
        return informationService;
    }

    public void setInformationService(InformationService informationService) {
        this.informationService = informationService;
    }
}
