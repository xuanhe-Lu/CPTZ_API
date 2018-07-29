package com.ypiao.bean;

import java.io.Serializable;

/**
 * @NAME:CatFood
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/7/29
 * @VERSION:1.0
 */
public class CatFood implements Serializable {

    private static final long serialVersionUID = 1804802892316835777L;
    private int id ;
    private int isMember  = 0 ;//是否是会员 0 ,不是,1,是
    private long uid ;
    private long time ;
    private int catFood = 0 ;
    private String remark;
    private String userName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsMember() {
        return isMember;
    }

    public void setIsMember(int isMember) {
        this.isMember = isMember;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCatFood() {
        return catFood;
    }

    public void setCatFood(int catFood) {
        this.catFood = catFood;
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public String toString() {
        return "CatFood{" +
                "id=" + id +
                ", isMember=" + isMember +
                ", uid=" + uid +
                ", time=" + time +
                ", catFood=" + catFood +
                ", remark='" + remark + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
