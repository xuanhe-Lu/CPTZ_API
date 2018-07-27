package com.ypiao.service.imp;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserAttenService;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @NAME:UserAttenServiceImp
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/7/16
 * @VERSION:1.0
 */
public class UserAttenServiceImp extends AConfig implements UserAttenService {
    // 表名
    private static final String TBL_USER_ATTEN = "user_atten";
    private static Logger logger = LoggerFactory.getLogger(UserAttenServiceImp.class);

    /*
     * @CLASSNAME:findUserCountByMaxTime
     * @DESCRIPTION:根据最大time查找连续签到天数
     * @AUTHOR:luxh
     * @DATE:2018/7/17
     * @VERSION:1.0
     */
    @Override
    public int findUserCountByMaxTime() throws Exception {
        logger.info("come in findUserCountByMaxTime");
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        int count = 0;//连续签到天数
        try {
            ps = conn.prepareStatement("SELECT COUNT FROM " + TBL_USER_ATTEN + " ORDER BY TIME DESC LIMIT  1 ");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                logger.info(String.format("查询到数据，返回给service"));
                count = rs.getInt(1);
            }
        } finally {
            JPrepare.close(ps, conn);
        }
        return count;
    }

    /*
     * @CLASSNAME:save
     * @DESCRIPTION:保存签到记录，
     * @AUTHOR:luxh
     * @DATE:2018/7/16
     * @VERSION:1.0
     */
    @Override
    public int save(long time, long uid, int count) throws Exception {
        logger.info(String.format("come in UserAttenServiceImp.save,uid:%s,time:%s,count:%s", uid, time, count));
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        int countRe = 0;//计数
        try {
            ps = conn.prepareStatement("INSERT INTO user_atten (UID,time,remark,count )" + "values (?,?,?,?)");
            ps.setLong(1, uid);
            ps.setLong(2, time);
            ps.setString(3, "签到");
            ps.setInt(4, count);

            countRe = ps.executeUpdate();
        } finally {
            JPrepare.close(ps, conn);
        }
        return countRe;
    }

    @Override
    public List<Long> findUserAtten(long date, long uid, AjaxInfo json) throws Exception {
        logger.info(String.format("come in UserAttenServiceImp.findUserAtten,date:%s,uid:%s", date, uid));
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        List<Long> timeList;
        try {
            ps = conn.prepareStatement("SELECT time  FROM " + TBL_USER_ATTEN + " WHERE UID = ? and time >  ? order by time ASC ");
            ps.setLong(1, uid);
            ps.setLong(2, date - 1);
            ResultSet rs = ps.executeQuery();
            timeList = new ArrayList<>();
            while (rs.next()) {
                timeList.add(rs.getLong(1));
            }
        } finally {
            JPrepare.close(ps, conn);
        }
        return timeList;
    }

    @Override
    protected void checkSQL() {

    }
}
