package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @NAME:LuckyBagBouns
 * @DESCRIPTION: 福袋领取金额统计
 * @AUTHOR:luxh
 * @DATE:2018/8/4
 * @VERSION:1.0
 */
public class LuckyBagBouns implements Serializable {
    private static final long serialVersionUID = -8463710044001055653L;
    private long uid;
    private long time;
    private BigDecimal total;//总计
    private BigDecimal cashMoney;//已提现金额
    private BigDecimal remainMoney;//剩余金额

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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(BigDecimal cashMoney) {
        this.cashMoney = cashMoney;
    }

    public BigDecimal getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(BigDecimal remainMoney) {
        this.remainMoney = remainMoney;
    }

    @Override
    public String toString() {
        return "LuckyBagBouns{" +
                "uid=" + uid +
                ", time=" + time +
                ", total=" + total +
                ", cashMoney=" + cashMoney +
                ", remainMoney=" + remainMoney +
                '}';
    }
}
