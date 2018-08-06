package com.ypiao.service.imp;

import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.LuckyBagService;
import com.ypiao.service.UserMoneyService;
import com.ypiao.util.APState;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @NAME:LuckyBagServiceImp
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/8/2
 * @VERSION:1.0
 */
public class LuckyBagServiceImp implements LuckyBagService {
    private UserMoneyService userMoneyService;
    private static Logger log = Logger.getLogger(LuckyBagServiceImp.class);

    @Override
    public LuckyBagCondfig qryLuckyBagConfig(BigDecimal money) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select  num,lastEnvelopes,failureTime from ypiao.luckyBag_config where lendMin <? and lendMax >? limit 1");
            ps.setBigDecimal(1, money);
            ps.setBigDecimal(2, money);
            ResultSet rs = ps.executeQuery();
            LuckyBagCondfig luckyBagCondfig = new LuckyBagCondfig();
            while (rs.next()) {
                luckyBagCondfig.setNum(rs.getInt(1));
                luckyBagCondfig.setLastEnvelopes(rs.getBigDecimal(2));
                luckyBagCondfig.setFailureTime(rs.getLong(3));
            }
            return luckyBagCondfig;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public int insertLuckBag(LuckyBagSend luckyBagSend) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        int i = 0;
        try {
            ps = conn.prepareStatement("insert into ypiao.luckyBag_send (bagId,uid,sid,lendMoney,num,lastEnvelopes,createTime,bagCount,failureTime) values (?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, luckyBagSend.getBagId());
            ps.setLong(2, luckyBagSend.getUid());
            ps.setLong(3, luckyBagSend.getSid());
            ps.setBigDecimal(4, luckyBagSend.getLendMoney());
            ps.setInt(5, luckyBagSend.getNum());
            ps.setBigDecimal(6, luckyBagSend.getLastEnvelopes());
            ps.setLong(7, luckyBagSend.getCreateTime());
            ps.setBigDecimal(8, luckyBagSend.getBagCount());
            Calendar calendar = Calendar.getInstance(); //得到日历
            //获取当前时间
            Date now = new Date();
            calendar.setTime(now);//把当前时间赋给日历
            calendar.add(Calendar.YEAR, 1);  //设置为1年后
            Date dateAfter = calendar.getTime();
            long time = dateAfter.getTime();
            log.info("failureTime:" + dateAfter);
            ps.setLong(9, time);
            i = ps.executeUpdate();
            return i;
        } finally {
            JPrepare.close(ps, conn);
        }

    }

    public LuckyBagSend findLuckBagInfo(long giftId, long uid, long time) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("select bagId,uid,sid,lendMoney,num,lastEnvelopes,createTime,bagCount,failureTime from  ypiao.luckyBag_send where bagId = ? and uid = ? and sendtIme >=  0 and failureTime >?  limit 1");
            ps.setLong(1, giftId);
            ps.setLong(2, uid);
            ps.setLong(3, time);
            ResultSet rs = ps.executeQuery();
            LuckyBagSend luckyBagSend = new LuckyBagSend();
            while (rs.next()) {
                luckyBagSend.setBagId(rs.getLong(1));
                luckyBagSend.setUid(rs.getLong(2));
                luckyBagSend.setSid(rs.getLong(3));
                luckyBagSend.setLendMoney(rs.getBigDecimal(4));
                luckyBagSend.setNum(rs.getInt(5));
                luckyBagSend.setLastEnvelopes(rs.getBigDecimal(6));
                luckyBagSend.setCreateTime(rs.getLong(7));
                luckyBagSend.setBagCount(rs.getBigDecimal(8));
                luckyBagSend.setFailureTime(rs.getLong(9));
            }
            return luckyBagSend;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public void updateSend(LuckyBagReceive luckyBagReceive) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {

            ps = conn.prepareStatement("update ypiao.luckyBag_send set sendTime = ? , failureTime = ? where uid =? and  bagId = ?");
            ps.setLong(1, System.currentTimeMillis());
            ps.setLong(2, luckyBagReceive.getFailureTime());
            ps.setLong(3, luckyBagReceive.getUid());
            ps.setLong(4, luckyBagReceive.getBagId());
            if (ps.executeUpdate() > 0) {
                ps.close();
//                ps = conn.prepareStatement("insert into ypiao.luckyBag_receive (bagId,redId,/*uid*/,money,failureTime) values  (?,?,/*?,*/?,?)");
                ps = conn.prepareStatement("insert into ypiao.luckyBag_receive (bagId,redId,money,failureTime) values  (?,?,?,?)");
                ps.setLong(1, luckyBagReceive.getBagId());
                ps.setInt(2, luckyBagReceive.getRedId());
//                ps.setLong(3, luckyBagReceive.getUid());
                ps.setBigDecimal(3, luckyBagReceive.getMoney());
                ps.setLong(4, luckyBagReceive.getFailureTime());
                ps.executeUpdate();
            }
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public List<LuckyBagSend> findPersionalBag(long uid, int type) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        String sql = " >?";
        if (type != 1) {
            sql = "<?";
        }
        long time = System.currentTimeMillis();
        try {
            ps = conn.prepareStatement("select bagId,uid,sid,lendMoney,num,lastEnvelopes,createTime,bagCount,failureTime from  ypiao.luckyBag_send where  uid = ?  and failureTime  " + sql);
            ps.setLong(1, uid);
            ps.setLong(2, time);
            ResultSet rs = ps.executeQuery();
            List<LuckyBagSend> luckyBagSends = new ArrayList<>();
            while (rs.next()) {
                LuckyBagSend luckyBagSend = new LuckyBagSend();
                luckyBagSend.setBagId(rs.getLong(1));
                luckyBagSend.setUid(rs.getLong(2));
                luckyBagSend.setSid(rs.getLong(3));
                luckyBagSend.setLendMoney(rs.getBigDecimal(4));
                luckyBagSend.setNum(rs.getInt(5));
                luckyBagSend.setLastEnvelopes(rs.getBigDecimal(6));
                luckyBagSend.setCreateTime(rs.getLong(7));
                luckyBagSend.setBagCount(rs.getBigDecimal(8));
                luckyBagSend.setFailureTime(rs.getLong(9));
                luckyBagSends.add(luckyBagSend);
            }
            return luckyBagSends;
        } finally {
            JPrepare.close(ps, conn);
        }

    }

    public LuckyBagReceive qryluckyBagHis(long uid, long giftId) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        ps = conn.prepareStatement("select bagId , redId,uid,money,time ,failureTime  from ypiao.luckyBag_receive where bagId = ? and uid= ? limit 1");
        ps.setLong(1, giftId);
        ps.setLong(2, uid);
        ResultSet rs = ps.executeQuery();
        LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
        while (rs.next()) {
            luckyBagReceive.setBagId(rs.getLong(1));
            luckyBagReceive.setRedId(rs.getInt(2));
            luckyBagReceive.setUid(rs.getLong(3));
            luckyBagReceive.setMoney(rs.getBigDecimal(4));
            luckyBagReceive.setTime(rs.getLong(5));
            luckyBagReceive.setFailureTime(rs.getLong(6));
        }
        return luckyBagReceive;
    }

    public long qryIsExpire(long giftId) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            long time = System.currentTimeMillis();
            ps = conn.prepareStatement("select bagId from ypiao.luckyBag_send where bagId = ? and failureTime > ?");
            ps.setLong(1, giftId);
            ps.setLong(2, time);
            ResultSet rs = ps.executeQuery();
            long result = 0;
            while (rs.next()) {
                result = rs.getLong(1);
            }
            return result;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public List<LuckyBagReceive> qryIsout(long giftId) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        long time = System.currentTimeMillis();
        ps = conn.prepareStatement("select redId,uid,money,time,failureTime from  ypiao.luckyBag_receive where  time =0 and uid =0 and bagId =? and failureTime> ? order by redId asc");
        ps.setLong(1, giftId);
        ps.setLong(2, time);
        ResultSet rs = ps.executeQuery();
        List<LuckyBagReceive> luckyBagReceives = new ArrayList<>();
        while (rs.next()) {
            LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
            luckyBagReceive.setRedId(rs.getInt(1));
            luckyBagReceive.setUid(rs.getLong(2));
            luckyBagReceive.setMoney(rs.getBigDecimal(3));
            luckyBagReceive.setTime(rs.getLong(4));
            luckyBagReceive.setFailureTime(rs.getLong(5));
            luckyBagReceives.add(luckyBagReceive);
        }
        return luckyBagReceives;


    }


    public List<LuckyBagReceive> qryBagHis(long giftId) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        long time = System.currentTimeMillis();
        ps = conn.prepareStatement("select redId,uid,money,time,failureTime from  ypiao.luckyBag_receive where  time > 0 and uid > 0 and bagId =?  order by redId asc");
        ps.setLong(1, giftId);
//        ps.setLong(2, time);
        ResultSet rs = ps.executeQuery();
        List<LuckyBagReceive> luckyBagReceives = new ArrayList<>();
        while (rs.next()) {
            LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
            luckyBagReceive.setRedId(rs.getInt(1));
            luckyBagReceive.setUid(rs.getLong(2));
            luckyBagReceive.setMoney(rs.getBigDecimal(3));
            luckyBagReceive.setTime(rs.getLong(4));
            luckyBagReceive.setFailureTime(rs.getLong(5));
            luckyBagReceives.add(luckyBagReceive);
        }
        return luckyBagReceives;


    }


    public LuckyBagReceive qryIsNotout(long giftId) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        long time = System.currentTimeMillis();
        ps = conn.prepareStatement("select redId,uid,money,time,failureTime,bagId from  ypiao.luckyBag_receive where  time = 0 and uid = 0 and bagId =? and failureTime> ? order by redId asc limit 1");
        ps.setLong(1, giftId);
        ps.setLong(2, time);
        ResultSet rs = ps.executeQuery();
        LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
        while (rs.next()) {

            luckyBagReceive.setRedId(rs.getInt(1));
            luckyBagReceive.setUid(rs.getLong(2));
            luckyBagReceive.setMoney(rs.getBigDecimal(3));
            luckyBagReceive.setTime(rs.getLong(4));
            luckyBagReceive.setFailureTime(rs.getLong(5));
            luckyBagReceive.setBagId(rs.getLong(6));
        }
        return luckyBagReceive;
    }

    public void updateUidAndTime(LuckyBagReceive luckyBagReceive) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("update ypiao.luckyBag_receive set uid =?, time = ? where bagId = ? and redId = ?");
            ps.setLong(1, luckyBagReceive.getUid());
            ps.setLong(2, luckyBagReceive.getTime());
            ps.setLong(3, luckyBagReceive.getBagId());
            ps.setInt(4, luckyBagReceive.getRedId());
            if (ps.executeUpdate() > 0) {
                ps.close();
                PreparedStatement ps1 = conn.prepareStatement("update ypiao.luckyBag_bonus set total = total+?,remainMoney = remainMoney+? ,time = ? where uid =?");
                ps1.setBigDecimal(1, luckyBagReceive.getMoney());
                ps1.setBigDecimal(2, luckyBagReceive.getMoney());
                ps1.setLong(3, luckyBagReceive.getTime());
                ps1.setLong(4, luckyBagReceive.getUid());
                if (ps1.executeUpdate() <= 0) {
                    ps1.close();
                    PreparedStatement ps2 = conn.prepareStatement("insert  into ypiao.luckyBag_bonus (total,remainMoney,time,uid) values (?,?,?,?)");
                    ps2.setBigDecimal(1, luckyBagReceive.getMoney());
                    ps2.setBigDecimal(2, luckyBagReceive.getMoney());
                    ps2.setLong(3, luckyBagReceive.getTime());
                    ps2.setLong(4, luckyBagReceive.getUid());
                    ps2.close();
                }
            }
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public void saveRmbs(LuckyBagReceive luckyBagReceive) throws Exception {
        Connection conn = JPrepare.getConnection();
        try {
            UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, luckyBagReceive.getUid());
            r.setSid(luckyBagReceive.getBagId()); // 索引信息
            r.setTid(APState.TRADE_ADD5);
            r.setWay(APState.EVENT_P015);
            r.setEvent("通过福袋获得奖励金" + luckyBagReceive.getMoney());
            r.add(luckyBagReceive.getMoney());
            r.setTime(luckyBagReceive.getTime());
            this.getUserMoneyService().share(r);
        } finally {
            JPrepare.close(conn);
        }
    }

    public List<LuckyBagReceive> qryluckyBagHisAll(long uid, long giftId) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        ps = conn.prepareStatement("select bagId , redId,uid,money,time ,failureTime  from ypiao.luckyBag_receive where bagId = ?  order by redId asc ");
        ps.setLong(1, giftId);
        ps.setLong(1, uid);
        ResultSet rs = ps.executeQuery();
        List<LuckyBagReceive> luckyBagReceives = new ArrayList<>();
        while (rs.next()) {
            LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
            luckyBagReceive.setBagId(rs.getLong(1));
            luckyBagReceive.setUid(rs.getLong(2));
            luckyBagReceive.setMoney(rs.getBigDecimal(3));
            luckyBagReceive.setTime(rs.getLong(4));
            luckyBagReceive.setFailureTime(rs.getLong(5));
            luckyBagReceives.add(luckyBagReceive);
        }
        return luckyBagReceives;
    }

    public LuckyBagBouns qryPersionnalBouns(long uid) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select uid , total,cashMoney,remainMoney,time  from ypiao.luckyBag_bonus where uid = ?   ");
            ps.setLong(1, uid);
            ResultSet rs = ps.executeQuery();
            LuckyBagBouns luckyBagBouns = new LuckyBagBouns();
            while (rs.next()) {
                luckyBagBouns.setUid(rs.getLong(1));
                luckyBagBouns.setTotal(rs.getBigDecimal(2));
                luckyBagBouns.setCashMoney(rs.getBigDecimal(3));
                luckyBagBouns.setRemainMoney(rs.getBigDecimal(4));
                luckyBagBouns.setTime(rs.getLong(5));
            }
            return luckyBagBouns;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public void updateBouns(BigDecimal rmb, long uid) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("update ypiao.luckyBag_bonus set total = total - ?, remainMoney = remainMoney - ? where uid = ?");
            ps.setBigDecimal(1, rmb);
            ps.setBigDecimal(2, rmb);
            ps.setLong(3, uid);
            ps.executeUpdate();
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public long findBagById(long giftId) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        ps = conn.prepareStatement("select bagId from  ypiao.luckyBag_receive where bagId = ?");
        ps.setLong(1, giftId);
        ResultSet rs = ps.executeQuery();
        long bagId = 0;
        while (rs.next()) {
            bagId = rs.getLong(1);
        }
        return bagId;
    }

    public LuckyBagReceive findMaxMoney(long giftId) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select bagId,money,redId from  ypiao.luckyBag_receive where bagId = ? order by redId desc limit 1");
            ps.setLong(1, giftId);
            ResultSet rs = ps.executeQuery();
            LuckyBagReceive luckyBagReceive = new LuckyBagReceive();
            while (rs.next()) {
                luckyBagReceive.setRedId(rs.getInt(3));
                luckyBagReceive.setBagId(rs.getLong(1));
                luckyBagReceive.setMoney(rs.getBigDecimal(2));
            }
            return luckyBagReceive;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public UserMoneyService getUserMoneyService() {
        return userMoneyService;
    }

    public void setUserMoneyService(UserMoneyService userMoneyService) {
        this.userMoneyService = userMoneyService;
    }
}
