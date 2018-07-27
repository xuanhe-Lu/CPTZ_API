package com.ypiao.bean;


import java.io.Serializable;
import java.math.BigDecimal;

/*
 * @CLASSNAME:LotteryLog
 * @DESCRIPTION:抽奖记录实体
 * @AUTHOR:luxh
 * @DATE:2018/07/20
 * @VERSION:1.0
 */
public class LotteryLog implements Serializable {
    private static final long serialVersionUID = -4649833280431640229L;
    private int id;//ID
    private int lotteryId;//lotteryId
    private long uid;
    private long createTime;
    private long endTime;
    private String name;//奖项名称
    private String remark;//备注
    private String sign;//标识 I 是新增，即投资成功后增加一次抽奖，D是删除，即用户抽奖一次，
    private int count;

    @Override
    public String toString() {
        return "LotteryLog{" +
                "id=" + id +
                ", lotteryId=" + lotteryId +
                ", uid=" + uid +
                ", createTime=" + createTime +
                ", endTime=" + endTime +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", sign='" + sign + '\'' +
                ", count=" + count +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
