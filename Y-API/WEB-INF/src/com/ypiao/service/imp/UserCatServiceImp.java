package com.ypiao.service.imp;

import com.ypiao.bean.Cat;
import com.ypiao.bean.CatConfig;
import com.ypiao.bean.CatFood;
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
            ps = conn.prepareStatement("SELECT id,uid,catName,catLevel,gender,catFood,state,maturity,growth,bathTime,clearTime,shareTime,IsShovel,userName,img,feedTime FROM cat_status where state < 2 AND " + sqlSuffix);
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
                cat.setImg(rs.getString(15));
                cat.setFeedTime(rs.getLong(16));
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
            JPrepare.close(ps, conn);
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
    public void updateCatActTimeByIdAndUidAndTime(long uid, int id, int type, long time, int catFoodChange, int catFood, BigDecimal grow, String catName, int state) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            log.info(String.format("come in updateCatActTimeByIdAndUidAndTime,uid:[%s],id:[%s],type:[%s],time:[%s],catFood:[%s],grow:[%s],catName:[%s],state[%s]", uid, id, type, time, catFood, grow, catName, state));
            String sql = "update cat_status set state = ?,";
            String sqlSuffix = " where uid = ? and id = ?  and state <1";
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
//            ps.setLong(1, type);
            ps.setInt(1, state);
            ps.setLong(2, time);
            if (type == 1 || type == 2 || type == 3) {
                ps.setLong(3, catFood);
            } else if (type == 4) {
                ps.setBigDecimal(3, grow);
            }
            ps.setLong(4, uid);
            ps.setInt(5, id);
            //更新正表
            log.info("入正表");
            if (ps.executeUpdate() > 0) {
                log.info("入历史表");
                //入历史表
                String sqlHis = "insert into cat_logs (uid,catId,type,catName,time,remark,catFood) values (?,?,?,?,?,?,?)";
                ps = conn.prepareStatement(sqlHis);
                ps.setLong(1, uid);
                ps.setInt(2, id);
                ps.setInt(3, type);
                ps.setString(4, catName);
                ps.setLong(5, time);
                ps.setString(6, String.format("更新猫粮:[%s],成长值:[%s]，总猫粮[%s]", catFoodChange, grow, catFood));
                ps.setInt(7, catFoodChange);
                int i = ps.executeUpdate();
                log.info("change rows :" + i);
            } else {
                log.info("updateCatActTimeByIdAndUidAndTime,更新失败");
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

    public int updateName(long id, String name) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("update cat_status set catName = ?  where id = ?");
            ps.setString(1, name);
            ps.setLong(2, id);

            return ps.executeUpdate();
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    @Override
    public void insCat(Cat cat) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("insert into cat_status (uid,userName,catLevel,maturity) values (?,?,?,?)");
            ps.setLong(1, cat.getUid());
            ps.setString(2, cat.getUserName());
            ps.setInt(3, cat.getCatLevel());
            ps.setBigDecimal(4, cat.getMaturity());
            ps.execute();
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    @Override
    public Cat qryCatHis(long uid, int id, int type) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        Cat cat = new Cat();
        try {
            ps = conn.prepareStatement("select catFood ,id from cat_logs where uid =? and catId =? and type = ? order by time desc limit 1");
            ps.setLong(1, uid);
            ps.setInt(2, id);
            ps.setInt(3, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cat.setCatFood(rs.getInt(1));
                log.info("qryCatHis,"+rs.getInt(2));

            }
            return cat;
        } finally {
            JPrepare.close(ps, conn);
        }

    }

    public CatFood qryCatFood(long uid) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        CatFood cat;
        try {
            cat = new CatFood();
            ps = conn.prepareStatement("select catFood,isMember,userName from cat_userInfo where uid = ?  order by time desc ");
            ps.setLong(1, uid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cat.setCatFood(rs.getInt(1));
                cat.setIsMember(rs.getInt(2));
                cat.setUserName(rs.getString(3));
            }
        } finally {
            JPrepare.close(ps, conn);
        }
        return cat;
    }

    public int updateCatFood(long uid, int catfood) throws Exception {
        log.info(String.format("come in updateCatFood,uid[%s],catfood[%s]", uid, catfood));
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        CatFood cat = new CatFood();
        try {
            ps = conn.prepareStatement("update cat_userInfo set  catFood = ?   where uid = ?  ");
            ps.setInt(1, catfood);
            ps.setLong(2, uid);
            int i = 0;
            i = ps.executeUpdate();
            log.info(String.format("执行update 影响条数为:[%s]", i));
            if (i < 1) {
                log.info(String.format("开始执行insert"));
                ps = conn.prepareStatement("insert into cat_userInfo (uid,catFood ,time) values(?,?,?)  ");
                ps.setInt(2, catfood);
                ps.setLong(1, uid);
                ps.setLong(3, System.currentTimeMillis());
                i = ps.executeUpdate();
                log.info("insert:" + i);
            }
            return i;
        } finally {
            JPrepare.close(ps, conn);
        }
    }
    public int updateCatFood(long uid, int catfood,String remark) throws Exception {
        log.info(String.format("come in updateCatFood,uid[%s],catfood[%s]", uid, catfood));
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        CatFood cat = new CatFood();
        try {
            ps = conn.prepareStatement("update cat_userInfo set  catFood = ? ,remark = ?  where uid = ?  ");
            ps.setInt(1, catfood);
            ps.setString(2,remark);
            ps.setLong(3, uid);
            int i = 0;
            i = ps.executeUpdate();
            log.info(String.format("执行update 影响条数为:[%s]", i));
            if (i < 1) {
                log.info(String.format("开始执行insert"));
                ps = conn.prepareStatement("insert into cat_userInfo (uid,catFood ,time,remark) values(?,?,?,?)  ");
                ps.setInt(2, catfood);
                ps.setLong(1, uid);
                ps.setLong(3, System.currentTimeMillis());
                ps.setString(4,remark);
                i = ps.executeUpdate();
                log.info("insert:" + i);
            }
            return i;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public int updateIsMember(long uid, int isMember) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        CatFood cat = new CatFood();
        try {
            ps = conn.prepareStatement("update cat_userInfo set  isMember = ?   where uid = ?  ");
            ps.setInt(1, isMember);
            ps.setLong(2, uid);
            return ps.executeUpdate();
        } finally {
            JPrepare.close(ps, conn);
        }

    }

    public int updateuserName(long uid, String userName) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        CatFood cat = new CatFood();
        try {
            ps = conn.prepareStatement("update cat_userInfo set  userName = ?   where uid = ?  ");
            ps.setString(1, userName);
            ps.setLong(2, uid);
            return ps.executeUpdate();
        } finally {
            JPrepare.close(ps, conn);
        }

    }


}
