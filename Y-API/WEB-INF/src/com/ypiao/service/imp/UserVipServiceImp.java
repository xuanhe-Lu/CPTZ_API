package com.ypiao.service.imp;

/*
 * @CLASSNAME:UserVipService
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/7/22
 * @VERSION:1.0
 */

import com.ypiao.bean.UserVip;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserVipService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserVipServiceImp implements UserVipService {
    private static Logger logger = Logger.getLogger(UserVipServiceImp.class);
    /*
     * @NAME:queryVipLog
     * @DESCRIPTION:根据UID查询用户会员信息
     * @AUTHOR:luxh
     * @DATE:2018/7/23
     * @VERSION:1.0
     */
    @Override
    public UserVip queryVipLog(long uid,long endTime ) throws Exception {
        logger.info("queryVipLog.uid"+uid);
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        UserVip userVip = new UserVip();
        try {
            ps = conn.prepareStatement("SELECT  id,name,uid,level,receipt,startTime,endTime,remark,memberBenefits FROM  user_viplogs where uid = ? and endTime > ?");
            ps.setLong(1, uid);
            ps.setLong(2, endTime);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userVip.setId(rs.getInt(1));
                userVip.setName(rs.getString(2));
                userVip.setUid(rs.getLong(3));
                userVip.setLevel(rs.getInt(4));
                userVip.setReceipt(rs.getBigDecimal(5));
                userVip.setStartTime(rs.getLong(6));
                userVip.setEndTime(rs.getLong(7));
                userVip.setRemark(rs.getString(8));
                userVip.setMemberBenefits(rs.getInt(9));
            }
        } finally {
            JPrepare.close(ps, conn);
        }
        return userVip;
    }

    @Override
    public void uptVipLog(UserVip userVip) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement( "update user_viplogs set name = ?,level = ?,receipt = ?,startTime =? ,endTime =?,remark =? where uid = ?");
            ps.setString(1,userVip.getName());
            ps.setInt(2,userVip.getLevel());
            ps.setBigDecimal( 3,userVip.getReceipt());
            ps.setLong(4,userVip.getStartTime());
            ps.setLong(5,userVip.getEndTime());
            ps.setString(6,userVip.getRemark());
            ps.setLong(7,userVip.getUid());
            int i = 0;
            i = ps.executeUpdate();
            if(i == 0 ){
                ps.close();
                ps = conn.prepareStatement("insert into user_viplogs (name,level,receipt,startTime,endTime,remark,uid,memberBenefits) values (?,?,?,?,?,?,?,?)");
                ps.setString(1,userVip.getName());
                ps.setInt(2,userVip.getLevel());
                ps.setBigDecimal( 3,userVip.getReceipt());
                ps.setLong(4,userVip.getStartTime());
                ps.setLong(5,userVip.getEndTime());
                ps.setString(6,userVip.getRemark());
                ps.setLong(7,userVip.getUid());
                ps.setInt(8,userVip.getMemberBenefits());
                ps.execute();
            }
        } finally {
            JPrepare.close(ps,conn);
        }
    }
}
