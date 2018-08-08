package com.ypiao.service.imp;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.SyncMap;
import com.ypiao.bean.UserRmbs;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserMoneyService;
import com.ypiao.util.GMTime;
import com.ypiao.util.MonthFound;
import com.ypiao.util.Table;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMoneyServiceImp extends AConfig implements UserMoneyService {
private static Logger logger = Logger.getLogger(UserMoneyServiceImp.class);
    private static final String TBL_USER_RMBS = "user_rmbs";

    private static String SQL_BY_RMB;

    protected void checkSQL() {
        SQL_BY_RMB = JPrepare.getQuery("SELECT Sid,Tid,Uid,Fid,Way,Event,Cost,Adds,Total,State,Time FROM " + TBL_USER_RMBS + " WHERE Uid=? ORDER BY Time DESC", 1);
    }

    public int insert(Connection conn, UserRmbs r) throws SQLException {
        logger.info("come in insert:"+r.toString());
        PreparedStatement ps = conn.prepareStatement("INSERT INTO " + TBL_USER_RMBS + " (Sid,Tid,Uid,Fid,Way,Event,Cost,Adds,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
        try {
            ps.setLong(1, r.getSid());
            ps.setInt(2, r.getTid());
            ps.setLong(3, r.getUid());
            ps.setLong(4, r.getFid());
            ps.setString(5, r.getWay());
            ps.setString(6, r.getEvent());
            ps.setBigDecimal(7, r.getCost());
            ps.setBigDecimal(8, r.getAdds());
            ps.setBigDecimal(9, r.getTotal());
            ps.setInt(10, r.getState());
            ps.setLong(11, r.getTime());
            return ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    public int update(Connection conn, UserRmbs r) throws SQLException {
        logger.info("update,r"+r.toString());
        PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_RMBS + " SET Tid=?,Uid=?,Fid=?,Way=?,Event=?,Cost=?,Adds=?,Total=?,State=?,Time=? WHERE Sid=?");
        try {
            ps.setInt(1, r.getTid());
            ps.setLong(2, r.getUid());
            ps.setLong(3, r.getFid());
            ps.setString(4, r.getWay());
            ps.setString(5, r.getEvent());
            ps.setBigDecimal(6, r.getCost());
            ps.setBigDecimal(7, r.getAdds());
            ps.setBigDecimal(8, r.getTotal());
            ps.setInt(9, r.getState());
            ps.setLong(10, r.getTime());
            ps.setLong(11, r.getSid());
            return ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    public void save(Connection conn, UserRmbs r) throws SQLException {
        logger.info("save,r"+r.toString());
        if (this.update(conn, r) >= 1) {
            // Ignored
        } else if (this.insert(conn, r) >= 1) {
            PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_USER_STATUS + " SET MA=(MC+?),MB=?,Time=? WHERE Uid=?");
            try {
                ps.setBigDecimal(1, r.getTotal());
                ps.setBigDecimal(2, r.getTotal());
                ps.setLong(3, r.getTime());
                ps.setLong(4, r.getUid());
                ps.executeUpdate();
                SyncMap.getAll().sender(SYS_A128, "save", r);
            } finally {
                ps.close();
            }
        }
    }

    public void save(UserRmbs rmb) throws SQLException {
        Connection conn = JPrepare.getConnection();
        try {
            this.save(conn, rmb);
        } finally {
            JPrepare.close(conn);
        }
    }

    public void share(Connection conn, UserRmbs r) throws SQLException {
        logger.info("share,rmb:"+r.toString());
        if (this.update(conn, r) >= 1) {
            // Ignroed
        } else if (this.insert(conn, r) >= 1) {
            PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_USER_STATUS + " SET MA=(MC+?),MB=?,MF=(MF+?),Time=? WHERE Uid=?");
            try {
                ps.setBigDecimal(1, r.getTotal());
                ps.setBigDecimal(2, r.getTotal());
                ps.setBigDecimal(3, r.getAdds());
                ps.setLong(4, r.getTime());
                ps.setLong(5, r.getUid());
                ps.executeUpdate();
                SyncMap.getAll().sender(SYS_A128, "share", r);
            } finally {
                ps.close();
            }
        }
    }

    public void share(UserRmbs rmb) throws SQLException {
        logger.info("share,rmd:"+rmb.toString());
        Connection conn = JPrepare.getConnection();
        try {
            this.share(conn, rmb);
        } finally {
            JPrepare.close(conn);
        }
    }

    /**
     * 获取当前余额
     */
    public UserRmbs findMoneyByUid(Connection conn, long uid) throws SQLException {
        logger.info("come in findMoneyByUid ,uid:"+uid);
        PreparedStatement ps = conn.prepareStatement(SQL_BY_RMB);
        try {
            ps.setLong(1, uid);
            UserRmbs r = new UserRmbs(uid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                r.setSid(rs.getLong(1));
                r.setTid(rs.getInt(2));
                r.setFid(rs.getLong(4));
                r.setWay(rs.getString(5));
                r.setEvent(rs.getString(6));
                r.setCost(rs.getBigDecimal(7));
                r.setAdds(rs.getBigDecimal(8));
                r.setTotal(rs.getBigDecimal(9));
                r.setState(rs.getInt(10));
                r.setTime(rs.getLong(11));
            }
            rs.close();
            return r;
        } finally {
            ps.close();
        }
    }

    public UserRmbs findMoneyBySid(long sid, long uid) throws SQLException {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            UserRmbs r = null;
            ps = conn.prepareStatement("SELECT Sid,Tid,Uid,Fid,Way,Event,Cost,Adds,Total,State,Time FROM " + TBL_USER_RMBS + " WHERE Sid=? AND Uid=?");
            ps.setLong(1, sid);
            ps.setLong(2, uid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                r = new UserRmbs();
                r.setSid(rs.getLong(1));
                r.setTid(rs.getInt(2));
                r.setFid(rs.getLong(4));
                r.setWay(rs.getString(5));
                r.setEvent(rs.getString(6));
                r.setCost(rs.getBigDecimal(7));
                r.setAdds(rs.getBigDecimal(8));
                r.setTotal(rs.getBigDecimal(9));
                r.setState(rs.getInt(10));
                r.setTime(rs.getLong(11));
            }
            rs.close();
            return r;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public UserRmbs findMoneyByUid(long uid) throws SQLException {
        Connection conn = JPrepare.getConnection();
        try {
            return findMoneyByUid(conn, uid);
        } finally {
            JPrepare.close(conn);
        }
    }

    public AjaxInfo sendMoneyByUid(AjaxInfo json, long uid) throws SQLException {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            json.datas(API_OK);
            ps = conn.prepareStatement(JPrepare.getQuery("SELECT Sid,Tid,Event,Adds,Total,State,Time FROM " + TBL_USER_RMBS + " WHERE Uid=? ORDER BY Time DESC", 50));
            ps.setLong(1, uid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                json.formater();
                json.append("sid", rs.getLong(1));
                json.append("tid", rs.getInt(2));
                json.append("event", rs.getString(3));
                json.append("adds", DF2.format(rs.getDouble(4)));
                json.append("total", DF2.format(rs.getDouble(5)));
//                json.append("time", GMTime.format(rs.getLong(7), GMTime.CHINA));
                json.append("time", MonthFound.getDataFormat(rs.getLong(7),"yyyy-MM-dd"));
            }
            rs.close();
            return json;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public BigDecimal findSumMoneyByUid(long uid) throws SQLException {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select sum(adds) from user_rmbs where uid =  ? and EVENT like '购买%' and EVENT not like '购买新手专享标%'");
            ResultSet rs = ps.executeQuery();
            BigDecimal sum = new BigDecimal("0.00");
            while (rs.next()) {
                sum = rs.getBigDecimal(1);
            }
            return sum;
        } finally {
            JPrepare.close(ps, conn);
        }
    }
}
