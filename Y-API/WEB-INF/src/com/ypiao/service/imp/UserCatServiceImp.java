package com.ypiao.service.imp;

import com.ypiao.bean.Cat;
import com.ypiao.bean.CatConfig;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserCatService;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @catName:UserCatServiceImp
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/7/25
 * @VERSION:1.0
 */
public class UserCatServiceImp implements UserCatService {
    private static Logger log = Logger.getLogger(UserCatServiceImp.class);

    @Override
    public List<Cat> qryCatInfo(Long uid, int type) throws Exception {
        log.info("come in qryCatInfo,uid " + uid + "type:" + type);
        String sqlSuffix = " ";
        if (type == 1) {
            sqlSuffix += " uid = ?";
        } else {
            sqlSuffix += " id = ?";
        }
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        List<Cat> catList = new ArrayList<>();
        try {
            ps = conn.prepareStatement("SELECT id,uid,catName,catLevel,gender,catFood,state,maturity,growth,bathTime,clearTime,shareTime,IsShovel,userName FROM cat_status where state < 2 AND " + sqlSuffix);
            ps.setLong(1, uid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                log.info("查到数据");
                Cat cat = new Cat();
                cat.setId(rs.getInt(1));
                cat.setUid(rs.getLong(2));
                cat.setCatName(rs.getString(3));
                cat.setCatLevel(rs.getInt(4));
                cat.setGender(rs.getInt(5));
                cat.setCatFood(rs.getInt(6));
                cat.setState(rs.getInt(7));
                cat.setMaturity(rs.getBigDecimal(8));
                cat.setGrowth(rs.getBigDecimal(9));
                cat.setBathTime(rs.getLong(10));
                cat.setClearTime(rs.getLong(11));
                cat.setShareTime(rs.getLong(12));
                cat.setIsShovel(rs.getInt(13));
                cat.setUserName(rs.getString(14));
                catList.add(cat);
            }
        } finally {
            JPrepare.close(ps, conn);
        }
        return catList;
    }

    @Override
    public CatConfig findcatConfig(int id) throws Exception {
        log.info("come in findcatConfig,id " + id);
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        CatConfig catConfig;
        try {
            ps = conn.prepareStatement("select id,name,ordinaryRight,silverRight,goldRight,ordinaryGrowthAdd,silverGrowthAdd,goldGrowthAdd from cat_config where id =? ");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            catConfig = new CatConfig();
            while (rs.next()) {
                log.info("查到数据");
                catConfig.setId(rs.getInt(1));
                catConfig.setName(rs.getString(2));
                catConfig.setOrdinaryRight(rs.getInt(3));
                catConfig.setSilverRight(rs.getInt(4));
                catConfig.setGoldRight(rs.getInt(5));
                catConfig.setOrdinaryGrowthAdd(rs.getBigDecimal(6));
                catConfig.setSilverGrowthAdd(rs.getBigDecimal(7));
                catConfig.setGoldGrowthAdd(rs.getBigDecimal(8));
            }
        } finally {
            JPrepare.close(ps,conn);
        }
        return catConfig;
    }

    @Override
    public Cat findCatStatus(int id, long uid) throws Exception {
        log.info("come in findCatStatus,id " + id + " ,uid " + uid);
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        Cat cat = new Cat();
        try {
            ps = conn.prepareStatement("select id ,uid, catName,catLevel,gender,catFood,state,maturity,growth,bathTime, clearTime, shareTime,feedTime, IsShovel, remark from cat_status where uid =? and id =?");
            ps.setLong(1, uid);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                log.info("查到数据");
                cat.setId(rs.getInt(1));
                cat.setUid(rs.getLong(2));
                cat.setCatName(rs.getString(3));
                cat.setCatLevel(rs.getInt(4));
                cat.setGender(rs.getInt(5));
                cat.setCatFood(rs.getInt(6));
                cat.setState(rs.getInt(7));
                cat.setMaturity(rs.getBigDecimal(8));
                cat.setGrowth(rs.getBigDecimal(9));
                cat.setBathTime(rs.getLong(10));
                cat.setClearTime(rs.getLong(11));
                cat.setShareTime(rs.getLong(12));
                cat.setFeedTime(rs.getLong(13));
                cat.setIsShovel(rs.getInt(14));
                cat.setRemark(rs.getString(15));
            }
        } finally {
            JPrepare.close(ps, conn);
        }
        return cat;
    }

    @Override
    public void updateCatActTimeByIdAndUidAndTime(long uid, int id, int type, long time, int catFood, BigDecimal grow, String catName) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            log.info(String.format("come in updateCatActTimeByIdAndUidAndTime,uid:[%s],id:[%s],type:[%s],time:[%s],catFood:[%s],grow:[%s],catName:[%s]", uid, id, type, time, catFood, grow, catName));
            String sql = "update cat_status set remark = ?,";
            String sqlSuffix = " where uid = ? and id = ?  ";
            String word = null;
            if (type == 1) {
                word = " shareTime  = ? ,catFood = ?";
            } else if (type == 2) {
                word = " bathTime  = ? ,catFood = ?";
            } else if (type == 3) {
                word = " feedTime  = ? ,catFood = ?";
            } else if (type == 4) {
                word = " clearTime  = ? , growth = ?";
            }
            String finalSql = sql + word + sqlSuffix;


            ps = conn.prepareStatement(finalSql);
            ps.setLong(1, time);
            ps.setString(2, catName);

            if (type == 1 || type == 2 || type == 3) {
                ps.setLong(3, catFood);
            } else if (type == 4) {
                ps.setBigDecimal(3, grow);
            }
            //更新正表
            log.info("入正表");
            if (ps.executeUpdate() > 0) {
                log.info("入历史表");
                //入历史表
                String sqlHis = "insert into cat_logs (uid,catId,type,catName,time) values (?,?,?,?,?)";
                ps.setLong(1, uid);
                ps.setInt(2, id);
                ps.setInt(3, type);
                ps.setString(4, catName);
                ps.setLong(5, time);
                int i = ps.executeUpdate();
                log.info("change rows :" + i);
            }
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public List<Cat> findRankList() throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        List<Cat> catList;
        try {
            ps = conn.prepareStatement("select id,uid,userName,catName,growth,catLevel from cat_status order by growth desc limit 100");
            ResultSet rs = ps.executeQuery();
            catList = new ArrayList<>();
            while (rs.next()) {
                Cat cat = new Cat();
                cat.setId(rs.getInt(1));
                cat.setUid(rs.getLong(2));
                cat.setUserName(rs.getString(3));
                cat.setCatName(rs.getString(4));
                cat.setGrowth(rs.getBigDecimal(5));
                cat.setCatLevel(rs.getInt(6));
                catList.add(cat);
            }
        } finally {
            JPrepare.close(ps, conn);
        }
        return catList;
    }

    public int updateName(long id, int type, String name) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        String sqlInfo = "";
        String sqlSuffix = "";
        int i = 0;
        if (type == 1) {//用户改名
            sqlInfo = "userName = ?";
            sqlSuffix = "uid = ?";
        } else {
            sqlInfo = "catName = ?";
            sqlSuffix = "id = ?";
        }
        try {
            ps = conn.prepareStatement("update cat_status set " + sqlInfo + " where " + sqlSuffix);
            ps.setString(1, name);
            ps.setLong(2, id);

            i = ps.executeUpdate();
        } finally {
            JPrepare.close(ps,conn);
        }
        return i;
    }

    @Override
    public void insCat(Cat cat) {

    }

    @Override
    public Cat qryCatHis() {
        return null;
    }
}
