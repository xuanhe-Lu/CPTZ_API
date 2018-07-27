package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @NAME:Lottery
 * @DESCRIPTION:抽奖实体
 * @AUTHOR:luxh
 * @DATE:2018/7/19
 * @VERSION:1.0
 */
public class Lottery implements Serializable {

    private static final long serialVersionUID = 1774876752307620190L;
    private int id;//ID
    private String name;//奖项名称
    private BigDecimal money;//奖励金额
    private int type;//奖励类型 1是体验金，2是加息券，3是现金券, 4是实物,9是谢谢惠顾
    private int probability;//中奖概率
    private String url;//图片地址
    private long time;//添加时间
    private String remark;//备注


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Lottery{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                ", type=" + type +
                ", probability=" + probability +
                ", url='" + url + '\'' +
                ", time=" + time +
                ", remark='" + remark + '\'' +
                '}';
    }
}
