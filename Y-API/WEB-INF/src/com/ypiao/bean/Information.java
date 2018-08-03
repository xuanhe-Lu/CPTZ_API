package com.ypiao.bean;

import java.io.Serializable;

/**
 * @NAME:Information
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/8/3
 * @VERSION:1.0
 */
public class Information implements Serializable {
    private static final long serialVersionUID = 7715239454503095020L;
    private int type;//类型
    private  String name ;//名称
    private String img ;//图片地址
    private String url ;//资讯路径
    private String dist ;//图片类型
    private long time ;//时间

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }
}
