package com.ypiao.service.imp;

import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.*;
import com.ypiao.util.APState;
import com.ypiao.util.SmsUtils;
import com.ypiao.util.Table;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 活动业务层接口实现类.
 */
public class ActivityServiceImp extends AConfig implements ActivityService {

    private static Logger logger = Logger.getLogger(ActivityServiceImp.class);
    private SenderService senderService;

    private TriggerService triggerService;

    private UserInfoService userInfoService;

    private UserMoneyService userMoneyService;

    private UserVipService userVipService;

    protected void checkSQL() {
    }

    public SenderService getSenderService() {
        return senderService;
    }

    public void setSenderService(SenderService senderService) {
        this.senderService = senderService;
    }

    public TriggerService getTriggerService() {
        return triggerService;
    }

    public void setTriggerService(TriggerService triggerService) {
        this.triggerService = triggerService;
    }

    public UserInfoService getUserInfoService() {
        return userInfoService;
    }

    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public UserMoneyService getUserMoneyService() {
        return userMoneyService;
    }

    public void setUserMoneyService(UserMoneyService userMoneyService) {
        this.userMoneyService = userMoneyService;
    }

    private void save(Connection conn, ActInfo a) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_ACT_INFO + " SET Name=?,Rate=?,Remark=?,Sday=?,Eday=?,State=?,Time=? WHERE Adj=?");
        try {
            ps.setString(1, a.getName());
            ps.setBigDecimal(2, a.getRate());
            ps.setString(3, a.getRemark());
            ps.setLong(4, a.getSday());
            ps.setLong(5, a.getEday());
            ps.setInt(6, a.getState());
            ps.setLong(7, a.getTime());
            ps.setInt(8, a.getAdj());
            if (ps.executeUpdate() <= 0) {
                ps.close(); // add model
                ps = conn.prepareStatement("INSERT INTO " + Table.TBL_ACT_INFO + " (Adj,Name,Rate,Remark,Sday,Eday,State,Time) VALUES (?,?,?,?,?,?,?,?)");
                ps.setInt(1, a.getAdj());
                ps.setString(2, a.getName());
                ps.setBigDecimal(3, a.getRate());
                ps.setString(4, a.getRemark());
                ps.setLong(5, a.getSday());
                ps.setLong(6, a.getEday());
                ps.setInt(7, a.getState());
                ps.setLong(8, a.getTime());
                ps.executeUpdate();
            }
        } finally {
            ps.close();
        }
    }

    public void save(ActInfo info) throws SQLException {
        Connection conn = JPrepare.getConnection();
        try {
            this.save(conn, info);
        } finally {
            JPrepare.close(conn);
        }
    }

    public int update(String ids, int state, long time) throws SQLException {
        Set<Integer> set = this.toInt(ids);
        if (set.size() <= 0) {
            return 0;
        } // update state
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("UPDATE " + Table.TBL_ACT_INFO + " SET State=?,Time=? WHERE Adj=?");
            for (Integer adj : set) {
                ps.setInt(1, state);
                ps.setLong(2, time);
                ps.setInt(3, adj.intValue());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            return set.size();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public ActRule findRuleByAmt(Connection conn, int adj, BigDecimal amt) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT Sid,Adj,Sortid,TMA,TMB,YMC,State,Time FROM act_rule WHERE Adj=? AND TMA<=? AND TMB>=?");
        try {
            ActRule r = null;
            ps.setInt(1, adj);
            ps.setBigDecimal(2, amt);
            ps.setBigDecimal(3, amt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                r = new ActRule();
                r.setSid(rs.getLong(1));
                r.setAdj(rs.getInt(2));
                r.setSortid(rs.getInt(3));
                r.setTma(rs.getBigDecimal(4));
                r.setTmb(rs.getBigDecimal(5));
                r.setYmc(rs.getBigDecimal(6));
                r.setState(rs.getInt(7));
                r.setTime(rs.getLong(8));
            }
            rs.close();
            return r;
        } finally {
            ps.close();
        }
    }

    public void take(int adj, String mobile, LogOrder log) throws SQLException {
        ActRule r = null;
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            r = this.findRuleByAmt(conn, adj, log.getTma());
            if (r == null) {
                // Ignored
            } else {
                ps = conn.prepareStatement("INSERT INTO user_return (Sid,Uid,Pid,TMA,YMC,State,Time) VALUES (?,?,?,?,?,?,?)");
                ps.setLong(1, log.getSid());
                ps.setLong(2, log.getUid());
                ps.setLong(3, log.getPid());
                ps.setBigDecimal(4, log.getTma());
                ps.setBigDecimal(5, r.getYmc());
                ps.setInt(6, STATE_NORMAL);
                ps.setLong(7, log.getTime());
                ps.executeUpdate();
            }
        } finally {
            JPrepare.close(ps, conn);
        }

        // 获取短信发送签名
        String signName = SmsUtils.getSmsSignName();
        // 短信发送处理
        if (r == null) {
            // Ignored
        } else if (r.getSortid() == 1) {
            this.getSenderService().sendByText(mobile, signName + "恭喜您投资成功，您已获得28元红包，将在活动结束 3个工作日之内自动发放至您的账户，续投2万还可获得58元红包。退订回复TD");
        } else if (r.getSortid() == 2) {
            this.getSenderService().sendByText(mobile, signName + "恭喜您投资成功，您已获得58元红包，将在活动结束3个工作日之内自动发放至您的账户，续投3万还可获得88元红包。退订回复TD");
        } else if (r.getSortid() == 3) {
            this.getSenderService().sendByText(mobile, signName + "恭喜您投资成功，您已获得88元红包，将在活动结束3个工作日之内自动发放至您的账户，续投4万还可获得118元红包。退订回复TD");
        } else if (r.getSortid() == 4) {
            this.getSenderService().sendByText(mobile, signName + "恭喜您投资成功，您已获得118元红包，将在活动结束3个工作日之内自动发放至您的账户，续投5万还可获得168元红包。退订回复TD");
        } else if (r.getSortid() == 5) {
            this.getSenderService().sendByText(mobile, signName + "恭喜您投资成功，您已获得168元红包，将在活动结束3个工作日之内自动发放至您的账户，续投15万还可获得518元红包。退订回复TD");
        } else if (r.getSortid() == 6) {
            this.getSenderService().sendByText(mobile, signName + "恭喜您投资成功，您已获得518元红包，将在活动结束3个工作日之内自动发放至您的账户，红包多投多得。退订回复TD");
        }
    }

    private void invite(long uid, long fid, long sid, BigDecimal rmb, long time,BigDecimal tma,String mobile) throws SQLException {
        Connection conn = JPrepare.getConnection();
        try {
            logger.info(String.format("uid:[%s],fid:[%s],sid:[%s],rmb:[%s],time:[%s],tma:[%s],mobile:[%s]", uid,  fid,  sid,  rmb,  time, tma, mobile));
            UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, uid);
            r.setSid(sid + 1); // 索引信息
            r.setFid(fid); // 信息来源
            r.setTid(APState.TRADE_ADD5);
            r.setWay(APState.EVENT_P015);
            mobile = mobile.substring(0, mobile.length() - 8) + "****" + mobile.substring(mobile.length() - 4);
            r.setEvent("邀请"+mobile+"投资"+tma+"获得奖励金"+rmb);
            r.add(rmb);
            r.setTime(time);
            this.getUserMoneyService().share(r);
        } finally {
            JPrepare.close(conn);
        }
    }

    public void take(ProdInfo info, LogOrder log) throws SQLException {
        UserStatus s = this.getUserInfoService().findUserStatusByUid(log.getUid());
        if (info.getAdj() >= 1) {
            this.take(info.getAdj(), s.getMobile(), log);
        }
        //检查邀请人是否是会员，不是会员不享受投资收益返现
        boolean isVip = false;
        try {
            logger.info(String.format("查询【%s】在【%s】时，是否是会员，", s.getUPS(), System.currentTimeMillis()));
            UserVip userVip = this.getUserVipService().queryVipLog(s.getUPS(), System.currentTimeMillis());
            logger.info(String.format("该用户【%s】信息为:【%s】", s.getUPS(), userVip.toString()));
            isVip = userVip.getLevel() < 2 ? false : true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 处理邀请等信息

        if (isVip) {
            logger.info(String.format("该用户【%s】当前是会员，享受权益", s.getUPS()));
            if (s.getUPS() >= USER_UID_BEG) {
                if (s.getNp() == 1 && log.getTma().intValue() >= 1000) {
                    this.getTriggerService().invite(s.getUPS(), log.getTime());
                } // 分享收益加成
                BigDecimal rmb = log.getTmg().divide(BigDecimal.TEN); // 基础收益10%
                rmb = rmb.setScale(2, BigDecimal.ROUND_HALF_UP);
                if (rmb.compareTo(BigDecimal.ZERO) >= 1) {
                    this.invite(s.getUPS(), log.getUid(), log.getSid(), rmb, log.getTime(),log.getTma(),s.getMobile());
                }
            } else {
                // Ignored
            }
        } else
            logger.info(String.format("该用户【%s】当前不是会员，不享受权益", s.getUPS()));
    }

    @Override
    public void activityForVipBuy(ProdInfo info, LogOrder log) throws SQLException {

    }

    public int qryUserByUps(long ups) throws SQLException {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select count(1) from user_info where ups = ?");
            ps.setLong(1, ups);
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next()) {
                i = rs.getInt(1);
            }
            return i;
        } finally {
            JPrepare.close(ps, conn);
        }
    }
    public List<String>  qryUserByUid(long ups ) throws SQLException{
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select Event from user_rmbs where uid = ? and  Event like '邀请好友%累计投满1万，返现38元'");  // 邀请好友"+ mobile +"累计投满1万，返现38元
            ps.setLong(1, ups);
            ResultSet rs = ps.executeQuery();
            List<String> stringList = new ArrayList<>();
            while (rs.next()) {
                String event = "";
                event = rs.getString(1);
                stringList.add(event);
            }
            return stringList;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public List<Map<String,Object>> qryUserVipByUid(long ups ) throws SQLException{
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select Event,adds from user_rmbs where uid = ? and  Event like '邀请好友%购买会员'");  // "邀请好友" + mobile + "购买会员
            ps.setLong(1, ups);
            ResultSet rs = ps.executeQuery();
            List<Map<String,Object>> stringList = new ArrayList<>();
            while (rs.next()) {
               Map<String,Object> map  = new HashMap<>();
               String str = rs.getString(1);
                map.put("event",str.substring(4,str.indexOf("购")));
                map.put("adds",rs.getBigDecimal(2));
                stringList.add(map);
            }
            return stringList;
        } finally {
            JPrepare.close(ps, conn);
        }
    }

    public List<UserRmbs> qryInvestByUidAndEvent(long ups ) throws SQLException{
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select Event ,Adds from user_rmbs where uid = ? and  Event like '邀请%投资%获得奖励金%'");  // 邀请"+mobile+"投资"+tma+"获得奖励金
            ps.setLong(1, ups);
            ResultSet rs = ps.executeQuery();
            List<UserRmbs> stringList = new ArrayList<>();
            while (rs.next()) {
                UserRmbs userRmbs = new UserRmbs();
                userRmbs.setEvent(rs.getString(1));
                userRmbs.setAdds(rs.getBigDecimal(2));
                stringList.add(userRmbs);
            }
            return stringList;
        } finally {
            JPrepare.close(ps, conn);
        }
    }
    public UserVipService getUserVipService() {
        return userVipService;
    }

    public void setUserVipService(UserVipService userVipService) {
        this.userVipService = userVipService;
    }
}
