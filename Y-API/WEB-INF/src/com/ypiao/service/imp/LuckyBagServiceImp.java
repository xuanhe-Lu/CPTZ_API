package com.ypiao.service.imp;

import com.ypiao.bean.LuckyBagCondfig;
import com.ypiao.bean.LuckyBagReceive;
import com.ypiao.bean.LuckyBagSend;
import com.ypiao.data.JPrepare;
import com.ypiao.service.LuckyBagService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @NAME:LuckyBagServiceImp
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/8/2
 * @VERSION:1.0
 */
public class LuckyBagServiceImp implements LuckyBagService {
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
            ps = conn.prepareStatement("insert into ypiao.luckyBag_send (bagId,uid,sid,lendMoney,num,lastEnvelopes,createTime,bagCount) values (?,?,?,?,?,?,?,?)");
            ps.setLong(1, luckyBagSend.getBagId());
            ps.setLong(2, luckyBagSend.getUid());
            ps.setLong(3, luckyBagSend.getSid());
            ps.setBigDecimal(4, luckyBagSend.getLendMoney());
            ps.setInt(5, luckyBagSend.getNum());
            ps.setBigDecimal(6, luckyBagSend.getLastEnvelopes());
            ps.setLong(7, luckyBagSend.getCreateTime());
            ps.setBigDecimal(8, luckyBagSend.getBagCount());
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
            ps = conn.prepareStatement("select bagId,uid,sid,lendMoney,num,lastEnvelopes,createTime,bagCount,failureTime from  ypiao.luckyBag_send where bagId = ?and uid = ? and sendtIme >=  0 and failureTime < ? limit = 1");
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
            ps = conn.prepareStatement("update ypiao.luckyBag_send set sendTime = ? and failureTime = ? where uid =? and  bagId = ?");
            ps.setLong(1, System.currentTimeMillis());
            ps.setLong(2, luckyBagReceive.getFailureTime());
            ps.setLong(3, luckyBagReceive.getUid());
            ps.setLong(4, luckyBagReceive.getBagId());
            if (ps.executeUpdate() > 0) {
                ps.close();
                ps = conn.prepareStatement("insert into ypiao.luckyBag_receive (bagId,redId,uid,getMoney,failureTime) values  (?,?,?,?,?)");
                ps.setLong(1, luckyBagReceive.getBagId());
                ps.setInt(2, luckyBagReceive.getRedId());
                ps.setLong(3, luckyBagReceive.getUid());
                ps.setBigDecimal(4, luckyBagReceive.getMoney());
                ps.setLong(5, luckyBagReceive.getFailureTime());
                ps.executeUpdate();
            }
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public List<LuckyBagSend> findPersionalBag(long uid, long time) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select bagId,uid,sid,lendMoney,num,lastEnvelopes,createTime,bagCount,failureTime from  ypiao.luckyBag_send where  uid = ?  and failureTime <? ");
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

    public List<LuckyBagReceive> qryluckyBagHis(long uid, long time) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        ps = conn.prepareStatement("select bagId , redId,uid,money,time ,failureTime  from ypiao.luckyBag_receive where bagId = ? and uid= ?");
        return null;
    }

    public long qryIsExpire(long giftId) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            long time = System.currentTimeMillis();
            ps = conn.prepareStatement("select bagId from ypiao.luckyBag_receive where bagId = ? and failureTime < ?");
            ps.setLong(1, giftId);
            ps.setLong(2, time);
            ResultSet rs = ps.executeQuery();
            long result = 0;
            while (rs.next()) {
                result = rs.getInt(1);
            }
            return result;
        } finally {
            JPrepare.close(ps, conn);
        }
    }
}
