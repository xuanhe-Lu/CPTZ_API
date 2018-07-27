package com.ypiao.bean;

import java.io.Serializable;

/**
 * @NAME:UserAtten
 * @DESCRIPTION:签到属性
 * @AUTHOR:luxh
 * @DATE:2018/7/16
 * @VERSION:1.0
 */
public class UserAtten implements Serializable {
    private static final long serialVersionUID = 5691535744789214376L;
    private long id;//ID
    private long uid;//USERID
    private String remark;//备注
    private long time;//时间
    private int count;//连续签到次数


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "UserAtten{" +
                "id=" + id +
                ", uid=" + uid +
                ", remark='" + remark + '\'' +
                ", time='" + time + '\'' +
                ", count=" + count +
                '}';
    }
}
