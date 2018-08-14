package com.ypiao.bean;

/*
 * @CLASSNAME:UserVip
 * @DESCRIPTION:用户等级信息
 * @AUTHOR:luxh
 * @DATE:2018/7/22
 * @VERSION:1.0
 */

import java.io.Serializable;
import java.math.BigDecimal;

public class UserVip  implements Serializable {

    private static final long serialVersionUID = -1566393143542146651L;
    private int id;
    private String name;
    private int level = 1;
    private long uid;
    private long startTime;
    private long endTime;
    private BigDecimal receipt = new BigDecimal( "1.00");//会员收益
    private String remark;
    private int memberBenefits = 14;//会员权益日

    public int getMemberBenefits() {
        return memberBenefits;
    }

    public void setMemberBenefits(int memberBenefits) {
        this.memberBenefits = memberBenefits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getReceipt() {
        return receipt;
    }

    public void setReceipt(BigDecimal receipt) {
        this.receipt = receipt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserVip{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", uid=" + uid +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", receipt=" + receipt +
                ", remark='" + remark + '\'' +
                '}';
    }
}
