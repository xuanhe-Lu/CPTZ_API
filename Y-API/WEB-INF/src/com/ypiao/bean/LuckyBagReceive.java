package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @NAME:LuckyBagReceive
 * @DESCRIPTION: 福袋领取
 * @AUTHOR:luxh
 * @DATE:2018/8/3
 * @VERSION:1.0
 */
public class LuckyBagReceive implements Serializable {
    private static final long serialVersionUID = 2496023384201672022L;

    private long bagId;
    private int redId;
    private long uid;
    private BigDecimal money;
    private long time;
    private long failureTime;

    public long getBagId() {
        return bagId;
    }

    public void setBagId(long bagId) {
        this.bagId = bagId;
    }

    public int getRedId() {
        return redId;
    }

    public void setRedId(int redId) {
        this.redId = redId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(long failureTime) {
        this.failureTime = failureTime;
    }

    @Override
    public String toString() {
        return "LuckyBagReceive{" +
                "bagId=" + bagId +
                ", redId=" + redId +
                ", uid=" + uid +
                ", money=" + money +
                ", time=" + time +
                ", failureTime=" + failureTime +
                '}';
    }
}
