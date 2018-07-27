package com.ypiao.service.imp;

import com.sunsw.struts.ServletActionContext;
import com.ypiao.bean.Lottery;
import com.ypiao.bean.LotteryLog;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserLotteryService;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @NAME:UserDrawServiceImp
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/7/19
 * @VERSION:1.0
 */


public class UserLotteryServiceImp implements UserLotteryService {
    private static Logger log = Logger.getLogger(UserLotteryServiceImp.class);

    @Override
    public int findLotteryCountByUidAndTime(long uid) throws Exception {


        log.info(String.format("come in findLotteryCountByUidAndTime,用户为【%s】", uid));
        //在user_lotteryLog表中根据uid查找最近一次抽奖时的剩余次数
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        int count;
        try {
            ps = conn.prepareStatement("SELECT  count FROM user_lotterylogs where uid = ?  order by id desc  limit 1");
            ps.setLong(1, uid);
            ResultSet rs = ps.executeQuery();
            count = 0;
            while (rs.next()) {
                count = rs.getInt(1);

            }
        } finally {
            JPrepare.close(ps,conn );

        }
        return count;
    }

    @Override
    public int findLotteryProbability() throws Exception {
        return 0;
    }

    @Override
    public List<Lottery> findLotteryConfig() throws Exception {


        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        List<Lottery> lotteries;
        try {
            ps = conn.prepareStatement("SELECT ID,name,money,type,probability,url FROM lottery_config ");
            ResultSet rs = ps.executeQuery();
            lotteries = new ArrayList<>();
            while (rs.next()) {
                Lottery lottery = new Lottery();
                lottery.setId(rs.getInt(1));
                lottery.setName(rs.getString(2));
                lottery.setMoney(rs.getBigDecimal(3));
                lottery.setType(rs.getInt(4));
                lottery.setProbability(rs.getInt(5));
                lottery.setUrl(rs.getString(6));
                lotteries.add(lottery);
            }
        } finally {
            JPrepare.close(ps, conn);
        }
        return lotteries;
    }

    @Override
    public void saveLotteryLog(LotteryLog lotteryLog) throws Exception {


        Connection connection = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("insert into user_lotterylogs (lotteryId,name ,uid,createTime,endTime,remark,sign,count )values (?,?,?,?,?,?,?,?)");
            ps.setInt(1,lotteryLog.getLotteryId());
            ps.setString(2,lotteryLog.getName());
            ps.setLong(3,lotteryLog.getUid());
            ps.setLong(4,lotteryLog.getCreateTime());
            ps.setLong(5,lotteryLog.getEndTime());
            ps.setString(6,lotteryLog.getRemark());
            ps.setString(7,lotteryLog.getSign());
            ps.setInt(8,lotteryLog.getCount());
            ps.executeUpdate();
        } finally {
            JPrepare.close(ps,connection );
        }
    }

    @Override
    public void addLotterCount() throws Exception {

    }
}
