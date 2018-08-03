package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @NAME:LuckyBag
 * @DESCRIPTION:福袋发送
 * @AUTHOR:luxh
 * @DATE:2018/8/3
 * @VERSION:1.0
 */
public class LuckyBagSend implements Serializable {
    private static final long serialVersionUID = -3984269604360019314L;
    private long bagId;//福袋ID
    private long uid;//用户ID
    private long sid;//订单ID
    private BigDecimal lendMoney;//出借金额
    private BigDecimal bagCount;//红包总金额
    private  int num;//红包个数
    private BigDecimal lastEnvelopes;//最后一个红包大小
    private long createTime;//创建时间
    private long sendTime;//发送时间
    private long failureTime;//失效时间

    @Override
    public String toString() {
        return "LuckyBagSend{" +
                "bagId=" + bagId +
                ", uid=" + uid +
                ", sid=" + sid +
                ", lendMoney=" + lendMoney +
                ", bagCount=" + bagCount +
                ", num=" + num +
                ", lastEnvelopes=" + lastEnvelopes +
                ", createTime=" + createTime +
                ", sendTime=" + sendTime +
                ", failureTime=" + failureTime +
                '}';
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getBagId() {
        return bagId;
    }

    public void setBagId(long bagId) {
        this.bagId = bagId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public BigDecimal getLendMoney() {
        return lendMoney;
    }

    public void setLendMoney(BigDecimal lendMoney) {
        this.lendMoney = lendMoney;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public BigDecimal getLastEnvelopes() {
        return lastEnvelopes;
    }

    public void setLastEnvelopes(BigDecimal lastEnvelopes) {
        this.lastEnvelopes = lastEnvelopes;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(long failureTime) {
        this.failureTime = failureTime;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public BigDecimal getBagCount() {
        return bagCount;
    }

    public void setBagCount(BigDecimal bagCount) {
        this.bagCount = bagCount;
    }
}
