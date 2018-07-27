package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @NAME:CatConfig
 * @DESCRIPTION:猫粮配置
 * @AUTHOR:luxh
 * @DATE:2018/7/26
 * @VERSION:1.0
 */
public class CatConfig implements Serializable {
    private static final long serialVersionUID = -4257275037614179700L;
    private int id;//获取猫粮途径
    private int ordinaryRight;//普通会员权益
    private int silverRight;//白银会员权益
    private int goldRight;//黄金会员权益
    private String  name;//名称
    private BigDecimal ordinaryGrowthAdd;//普通会员成长值加成
    private BigDecimal silverGrowthAdd;//白银会员成长值加成
    private BigDecimal goldGrowthAdd;//黄金会员成长值加成

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrdinaryRight() {
        return ordinaryRight;
    }

    public void setOrdinaryRight(int ordinaryRight) {
        this.ordinaryRight = ordinaryRight;
    }

    public int getSilverRight() {
        return silverRight;
    }

    public void setSilverRight(int silverRight) {
        this.silverRight = silverRight;
    }

    public int getGoldRight() {
        return goldRight;
    }

    public void setGoldRight(int goldRight) {
        this.goldRight = goldRight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getOrdinaryGrowthAdd() {
        return ordinaryGrowthAdd;
    }

    public void setOrdinaryGrowthAdd(BigDecimal ordinaryGrowthAdd) {
        this.ordinaryGrowthAdd = ordinaryGrowthAdd;
    }

    public BigDecimal getSilverGrowthAdd() {
        return silverGrowthAdd;
    }

    public void setSilverGrowthAdd(BigDecimal silverGrowthAdd) {
        this.silverGrowthAdd = silverGrowthAdd;
    }

    public BigDecimal getGoldGrowthAdd() {
        return goldGrowthAdd;
    }

    public void setGoldGrowthAdd(BigDecimal goldGrowthAdd) {
        this.goldGrowthAdd = goldGrowthAdd;
    }

    @Override
    public String toString() {
        return "CatConfig{" +
                "id=" + id +
                ", ordinaryRight=" + ordinaryRight +
                ", silverRight=" + silverRight +
                ", goldRight=" + goldRight +
                ", name='" + name + '\'' +
                ", ordinaryGrowthAdd=" + ordinaryGrowthAdd +
                ", silverGrowthAdd=" + silverGrowthAdd +
                ", goldGrowthAdd=" + goldGrowthAdd +
                '}';
    }
}
