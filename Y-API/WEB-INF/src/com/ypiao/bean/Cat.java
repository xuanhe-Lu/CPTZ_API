package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @NAME:Cat
 * @DESCRIPTION:猫属性
 * @AUTHOR:luxh
 * @DATE:2018/7/25
 * @VERSION:1.0
 */
public class Cat implements Serializable {
    private static final long serialVersionUID = 8208672060769224382L;
    private int id;//id
    private int catLevel = 2;//类型 2为白银，3为黄金
    private long uid;//用户 ID
    private String catName;//猫名称
    private String userName;//用户名称
    private int gender = 0;//性别 ，0为雄性，1为雌性
    private int catFood = 0;//猫粮
    private int state = 0;//猫状态，0为成长期，1为成熟期，
    private BigDecimal growth = new BigDecimal(0.00);//成长值
    private BigDecimal maturity = new BigDecimal(1000.00);//成熟期
    private long bathTime = 0; //最近一次洗澡时间
    private long clearTime = 0;//最近一次清理时间
    private long shareTime = 0;//最近一次分享时间
    private long feedTime = 0;//最近一次喂食时间
    private int IsShovel = 0;//是否铲屎，0未铲屎，1铲屎
    private String remark;//备注
    private String img;//猫的图片地址


    public int getIsShovel() {
        return IsShovel;
    }

    public void setIsShovel(int isShovel) {
        IsShovel = isShovel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCatLevel() {
        return catLevel;
    }

    public void setCatLevel(int catLevel) {
        this.catLevel = catLevel;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getCatFood() {
        return catFood;
    }

    public void setCatFood(int catFood) {
        this.catFood = catFood;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public BigDecimal getGrowth() {
        return growth;
    }

    public void setGrowth(BigDecimal growth) {
        this.growth = growth;
    }

    public BigDecimal getMaturity() {
        return maturity;
    }

    public void setMaturity(BigDecimal maturity) {
        this.maturity = maturity;
    }

    public long getBathTime() {
        return bathTime;
    }

    public void setBathTime(long bathTime) {
        this.bathTime = bathTime;
    }

    public long getClearTime() {
        return clearTime;
    }

    public void setClearTime(long clearTime) {
        this.clearTime = clearTime;
    }

    public long getShareTime() {
        return shareTime;
    }

    public void setShareTime(long shareTime) {
        this.shareTime = shareTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getFeedTime() {
        return feedTime;
    }

    public void setFeedTime(long feedTime) {
        this.feedTime = feedTime;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "id=" + id +
                ", catLevel=" + catLevel +
                ", uid=" + uid +
                ", catName='" + catName + '\'' +
                ", userName='" + userName + '\'' +
                ", gender=" + gender +
                ", catFood=" + catFood +
                ", state=" + state +
                ", growth=" + growth +
                ", maturity=" + maturity +
                ", bathTime=" + bathTime +
                ", clearTime=" + clearTime +
                ", shareTime=" + shareTime +
                ", feedTime=" + feedTime +
                ", IsShovel=" + IsShovel +
                ", remark='" + remark + '\'' +
                ", img='" + img + '\'' +
                '}';
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
