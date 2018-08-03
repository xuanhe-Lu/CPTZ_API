package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @NAME:LuckyBag
 * @DESCRIPTION:福袋配置
 * @AUTHOR:luxh
 * @DATE:2018/8/2
 * @VERSION:1.0
 */
public class LuckyBagCondfig implements Serializable {

    private static final long serialVersionUID = -2360846590678713462L;

    private BigDecimal lendMin ;//最低出借金额
//    private BigDecimal lendMax ;//最高出借金额
    private int num;//红包个数
    private long failureTime;//失效时间(24H)
    private BigDecimal lastEnvelopes;//最后一个红包大小

    @Override
    public String toString() {
        return "LuckyBagCondfig{" +
                "lendMin=" + lendMin +
                ", num=" + num +
                ", failureTime=" + failureTime +
                ", lastEnvelopes=" + lastEnvelopes +
                '}';
    }

    public BigDecimal getLendMin() {
        return lendMin;
    }

    public void setLendMin(BigDecimal lendMin) {
        this.lendMin = lendMin;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(long failureTime) {
        this.failureTime = failureTime;
    }

    public BigDecimal getLastEnvelopes() {
        return lastEnvelopes;
    }

    public void setLastEnvelopes(BigDecimal lastEnvelopes) {
        this.lastEnvelopes = lastEnvelopes;
    }
}
