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
 * @NAME:UserCatServiceImp
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/7/25
 * @VERSION:1.0
 */
public class UserCatServiceImp implements UserCatService {
    private static Logger log = Logger.getLogger(UserCatServiceImp.class);

    @Override
    public List<Cat> qryCatInfo(Long uid) throws Exception {
        log.info("come in qryCatInfo,uid " + uid);
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        List<Cat> catList = new ArrayList<>();
        try {
            ps = conn.prepareStatement("SELECT id,uid,name,catLevel,gender,catFood,state,maturity,growth,bathTime,clearTime,shareTime,IsShovel FROM cat_status where uid=?");
            ps.setLong(1, uid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                log.info("查到数据");
                Cat cat = new Cat();
                cat.setId(rs.getInt(1));
                cat.setUid(rs.getLong(2));
                cat.setName(rs.getString(3));
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
        ps = conn.prepareStatement("select id,name,ordinaryRight,silverRight,goldRight,ordinaryGrowthAdd,silverGrowthAdd,goldGrowthAdd from cat_config where id =? ");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        CatConfig catConfig = new CatConfig();
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
        return catConfig;
    }

    @Override
    public Cat findCatStatus(int id, long uid) throws Exception {
        log.info("come in findCatStatus,id " + id + " ,uid " + uid);
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        Cat cat = new Cat();
        ps = conn.prepareStatement("select id ,uid, name,catLevel,gender,catFood,state,maturity,growth,bathTime, clearTime, shareTime,feedTime, IsShovel, remark from cat_status where uid =? and id =?");
        ps.setLong(1, uid);
        ps.setInt(2, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            log.info("查到数据");
            cat.setId(rs.getInt(1));
            cat.setUid(rs.getLong(2));
            cat.setName(rs.getString(3));
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
        return cat;
    }

    @Override
    public void updateCatActTimeByIdAndUidAndTime(long uid, int id, int type, long time, int catFood, BigDecimal grow, String name) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            log.info(String.format("come in updateCatActTimeByIdAndUidAndTime,uid:[%s],id:[%s],type:[%s],time:[%s],catFood:[%s],grow:[%s],name:[%s]", uid, id, type, time, catFood, grow, name));
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
            ps.setString(2, name);

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
                String sqlHis = "insert into cat_logs (uid,catId,type,name,time) values (?,?,?,?,?)";
                ps.setLong(1, uid);
                ps.setInt(2, id);
                ps.setInt(3, type);
                ps.setString(4, name);
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
        ps = conn.prepareStatement("select id,uid,");
        return null;
    }
}
