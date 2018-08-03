package com.ypiao.json;

import com.ypiao.bean.*;
import com.ypiao.fuiou.*;
import com.ypiao.service.*;
import com.ypiao.sign.Fuiou;
import com.ypiao.util.*;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OnUserBuy extends Action {

    private static final long serialVersionUID = -7584077415632858067L;
    private static Logger logger = Logger.getLogger(OnUserBuy.class);
    private PayInfoService payInfoService;

    private ProdInfoService prodInfoService;

    private ProdModelService prodModelService;

    private TradeInfoService tradeInfoService;

    private UserAuthService userAuthService;

    private UserBankService userBankService;

    private UserChargeService userChargeService;

    private UserCouponService userCouponService;

    private UserInfoService userInfoService;
    private UserVipService userVipService;
    private UserCatService userCatService;
    private LuckyBagService luckyBagService;
    private static final double MIN = 0.01;

    public OnUserBuy() {
        super(true);
    }

    public PayInfoService getPayInfoService() {
        return payInfoService;
    }

    public void setPayInfoService(PayInfoService payInfoService) {
        this.payInfoService = payInfoService;
    }

    public ProdInfoService getProdInfoService() {
        return prodInfoService;
    }

    public void setProdInfoService(ProdInfoService prodInfoService) {
        this.prodInfoService = prodInfoService;
    }

    public ProdModelService getProdModelService() {
        return prodModelService;
    }

    public void setProdModelService(ProdModelService prodModelService) {
        this.prodModelService = prodModelService;
    }

    public TradeInfoService getTradeInfoService() {
        return tradeInfoService;
    }

    public void setTradeInfoService(TradeInfoService tradeInfoService) {
        this.tradeInfoService = tradeInfoService;
    }

    public UserAuthService getUserAuthService() {
        return userAuthService;
    }

    public void setUserAuthService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    public UserBankService getUserBankService() {
        return userBankService;
    }

    public void setUserBankService(UserBankService userBankService) {
        this.userBankService = userBankService;
    }

    public UserChargeService getUserChargeService() {
        return userChargeService;
    }

    public void setUserChargeService(UserChargeService userChargeService) {
        this.userChargeService = userChargeService;
    }

    public UserCouponService getUserCouponService() {
        return userCouponService;
    }

    public void setUserCouponService(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    public UserInfoService getUserInfoService() {
        return userInfoService;
    }

    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public String commit() {
        AjaxInfo json = this.getAjaxInfo();
        String Pwd = this.getParameter("pwd");
        BigDecimal amt = this.getBigDecimal("amt"); // 购买金额
        try {
            if (Pwd == null || Pwd.length() < 6) {
                json.addError(this.getText("user.error.027"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 检测产品信息
            long Pid = this.getLong("pid"); // 产品编号
            ProdInfo info = this.getProdInfoService().findProdByPid(Pid);
            if (info == null) {
                json.addError(this.getText("system.error.none"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 非销售状态
            if (info.getAu() != SALE_A1) {
                json.addError(this.getText("user.error.850"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 检测最低余额
            if (amt.compareTo(BigDecimal.ZERO) <= 0) {
                json.addError(this.getText("user.error.851", new String[]{DF2.format(info.getMc())}));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 检测最低余额
            if (info.getMc().compareTo(amt) >= 1) {
                json.addError(this.getText("user.error.851", new String[]{DF2.format(info.getMc())}));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 检测最高限额
            if (info.getMb().compareTo(BigDecimal.ZERO) >= 1) {
                if (amt.compareTo(info.getMb()) >= 1) {
                    json.addError(this.getText("user.error.852", new String[]{DF2.format(info.getMb())}));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            } // 产品余额
            BigDecimal yu = info.getMa().subtract(info.getMd());
            if (amt.compareTo(yu) >= 1) {
                json.addError(this.getText("user.error.853"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 产品售罄
            if (info.getMc().compareTo(yu) >= 1) {
                json.addError(this.getText("user.error.855"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 产品Model
            ProdModel m = this.getProdModelService().getProdModelByTid(info.getTid());
            if (m == null) {
                json.addError(this.getText("user.error.856"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 优惠券信息
            long cid = this.getLong("sid");
            long cid1 = this.getLong("sid1");
            long time = GMTime.currentTimeMillis();
            UserSession us = this.getUserSession();
            UserCoupon uc = this.getUserCouponService().findCouponBySid(cid);
            UserCoupon uc1 = new UserCoupon();
            if (cid1 != 0l) {
                uc1 = this.getUserCouponService().findCouponBySid(cid1);
            }
            if (uc1 == null) {
                // Ignored
            } else if (uc1.getUid() != us.getUid()) {
                uc1 = null; // Ignored
            } else if (uc1.getState() != STATE_NORMAL || time >= uc1.getEday()) {
                json.addError(this.getText("user.error.857"));
                System.out.println("json:" + json.toString());
                return JSON;
            } else {
                if (STATE_DISABLE != m.getTofee()) {
                    json.addError(this.getText("user.error.858"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                } // 投资金额不满足
                if (uc1.getToall().compareTo(amt) >= 1) {
                    json.addError(this.getText("user.error.859"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                } // 理财天数不满足
                if (uc1.getToday() > info.getAj()) {
                    json.addError(this.getText("user.error.859"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            }


            if (uc == null) {
                // Ignored
            } else if (uc.getUid() != us.getUid()) {
                uc = null; // Ignored
            } else if (uc.getState() != STATE_NORMAL || time >= uc.getEday()) {
                json.addError(this.getText("user.error.857"));
                System.out.println("json:" + json.toString());
                return JSON;
            } else {
                if (STATE_DISABLE != m.getTofee()) {
                    json.addError(this.getText("user.error.858"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                } // 投资金额不满足
                if (uc.getToall().compareTo(amt) >= 1) {
                    json.addError(this.getText("user.error.859"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                } // 理财天数不满足
                if (uc.getToday() > info.getAj()) {
                    json.addError(this.getText("user.error.859"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            } // 全局投资次数
            if (m.getTotal() >= 1) {
                if (this.getTradeInfoService().isProdByAll(us.getUid(), m.getTid(), m.getTotal())) {
                    json.addError(this.getText("user.error.860", new String[]{m.getName(), String.valueOf(m.getTotal())}));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            } // 当前标的次数
            if (m.getToall() >= 1) {
                if (this.getTradeInfoService().isProdByPid(us.getUid(), info.getPid(), m.getToall())) {
                    json.addError(this.getText("user.error.861", new String[]{info.getAa(), String.valueOf(m.getToall())}));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            } // 检测实名信息
            UserAuth a = this.getUserAuthService().findAuthByUid(us.getUid());
            if (a == null) {
                json.addError(this.getText("user.error.050"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 校对支付密码
            Pwd = VeStr.toMD5(Pwd); // 格式化
            if (!Pwd.equalsIgnoreCase(a.getPays())) {
                json.addError(this.getText("user.error.028"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 检测账户信息
            synchronized (doLock(us.getUid())) {
                UserStatus s = this.getUserInfoService().findUserStatusByUid(us.getUid());
                if (s == null) {
                    json.addError(this.getText("user.error.888"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                } // 抵扣券处理
                BigDecimal rmb = amt;
                LogOrder log = new LogOrder();
                // 增加对会员权益日黄金会员加息券叠加的处理
                if (uc != null && uc1 != null) {
                    log.setCid(uc.getSid());
                    switch (uc.getType()) {
                        case 1: // 体验券
                            log.setWay("体验金购买");
                            rmb = amt.subtract(uc.getTma());
                            break;
                        case 2: // 加息券
                            log.setTme(uc.getTmb().add(uc1.getTmb()));
                            break;
                        case 3: // 抵扣券
                        default:
                            rmb = amt.subtract(uc.getTma());
                    } // 是否足额抵扣
                    if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                        rmb = BigDecimal.ZERO;
                    } // 扣除金额
                    if (uc.getType() == 1) {
                        log.setTmd(amt.subtract(rmb));
                    }
                } else if (uc != null) {
                    log.setCid(uc.getSid());
                    switch (uc.getType()) {
                        case 1: // 体验券
                            log.setWay("体验金购买");
                            rmb = amt.subtract(uc.getTma());
                            break;
                        case 2: // 加息券
                            log.setTme(uc.getTmb());
                            break;
                        case 3: // 抵扣券
                        default:
                            rmb = amt.subtract(uc.getTma());
                    } // 是否足额抵扣
                    if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                        rmb = BigDecimal.ZERO;
                    } // 扣除金额
                    if (uc.getType() == 1) {
                        log.setTmd(amt.subtract(rmb));
                    }
                } // 检测账户余额是否足
                if (rmb.compareTo(s.getMb()) >= 1) {
                    json.addError(this.getText("user.error.854"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                } // 构建订单信息
                log.setSid(VeStr.getUSid());
                log.setUid(us.getUid());
                log.setPid(info.getPid());
                log.setTid(info.getTid());
                log.setName(info.getAa());
                //增加会员年化利率
                BigDecimal bigDecimal = new BigDecimal("" + this.getBigDecimal("vipRate"));
                if (!"0.00".equals(bigDecimal)) {
                    log.setRate((info.getAn().add(info.getAds())).multiply(bigDecimal));
                } else {
                    log.setRate(info.getAn().add(info.getAds()));
                }
                log.setAds(info.getAds());
                log.setTma(amt); // 总金额
                log.setTmb(rmb); // 支付金额
                log.setTmc(amt.subtract(rmb));
                if (log.getWay() == null) {
                    log.setWay("余额支付");
                }
                if (log.execute(info.getAh(), info.getAj())) {
                    // Ignored
                } else {
                    json.addError(this.getText("user.error.856"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
                int state = this.getTradeInfoService().commit(info, log, uc);
                if (state == 0) {
                    json.addObject();
                    json.append("id", log.getSid());
                    json.append("name", log.getName());
                    json.append("amt", DF2.format(amt));
                    json.append("fee", DF2.format(0));
                    StringBuilder sb = new StringBuilder();
                    if (log.getTmc().compareTo(BigDecimal.ZERO) >= 1) {
                        sb.append('，').append("抵扣：").append(DF2.format(log.getTmc()));
                    }
                    if (log.getTme().compareTo(BigDecimal.ZERO) >= 1) {
                        sb.append('，').append("加息：").append(DF2.format(log.getTme())).append("%");
                    } else if (sb.length() <= 0) {
                        sb.append('，').append("无");
                    } // 信息
                    json.append("text", sb.substring(1));
                    json.append("time", GMTime.format(log.getTime(), GMTime.CHINA));
                } else if (state == 1) {
                    json.addError(this.getText("user.error.853"));
                } else {
                    json.addError(this.getText("user.error.854"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            json.addError(this.getText("user.error.856"));
        } finally {
            Pwd = null;
        }
        System.out.println("json:" + json.toString());
        return JSON;
    }

    public String index() {
        AjaxInfo json = this.getAjaxInfo();
        try {
            long Pid = this.getLong("pid");
            ProdInfo info = this.getProdInfoService().findProdByPid(Pid);
            if (info == null) {
                json.addError(this.getText("system.error.none"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 非销售状态
            if (info.getAu() != SALE_A1) {
                json.addError(this.getText("user.error.850"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 产品售罄
            if (info.getMa().compareTo(info.getMd()) <= 0) {
                json.addError(this.getText("user.error.855"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 产品Model
            ProdModel m = this.getProdModelService().getProdModelByTid(info.getTid());
            if (m == null) {
                json.addError(this.getText("user.error.856"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 全局投资次数
            UserSession us = this.getUserSession();
            if (m.getTotal() >= 1) {
                if (this.getTradeInfoService().isProdByAll(us.getUid(), m.getTid(), m.getTotal())) {
                    json.addError(this.getText("user.error.860", new String[]{m.getName(), String.valueOf(m.getTotal())}));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            } // 当前标的次数
            if (m.getToall() >= 1) {
                if (this.getTradeInfoService().isProdByPid(us.getUid(), info.getPid(), m.getToall())) {
                    json.addError(this.getText("user.error.861", new String[]{info.getAa(), String.valueOf(m.getToall())}));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            }
            int na = 0; // 可用券码数量
            if (STATE_DISABLE == m.getTofee()) {
                na = this.getUserCouponService().loadByUid(json, us.getUid(), info.getAj());
            } else {
                json.datas(API_OK);
            }
            UserBank b = this.getUserBankService().findBankByUid(us.getUid());
            UserStatus s = this.getUserInfoService().findUserStatusByUid(us.getUid());
            json.close().add("obj");
            json.append("uid", us.getUid());
            json.append("mb", DF2.format(s.getMb()));
            json.append("vip", s.getVIP() + "");
            json.append("na", na);
            if (b == null) {
                json.append("bkname", "未绑卡");
                json.append("bkinfo", "****");
            } else {
                int beg = b.getCardNo().length() - 4;
                json.append("bkname", b.getBankName());
                json.append("bkinfo", b.getCardNo().substring(beg));
            }

        } catch (SQLException e) {
            json.addError(this.getText("system.error.info"));
        }
        System.out.println("json:" + json.toString());
        return JSON;
    }

    /**
     * 验证信息
     */
    public String verify() {
        logger.info("comein verify");
        AjaxInfo json = this.getAjaxInfo();
        BigDecimal amt = this.getBigDecimal("amt");
        try {
            long Pid = this.getLong("pid"); // 产品编号
            ProdInfo info = this.getProdInfoService().findProdByPid(Pid);
            if (info == null) {
                json.addError(this.getText("system.error.none"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 非销售状态
            if (info.getAu() != SALE_A1) {
                json.addError(this.getText("user.error.850"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 检测最低余额
            if (amt.compareTo(BigDecimal.ZERO) <= 0) {
                json.addError(this.getText("user.error.851", new String[]{DF2.format(info.getMc())}));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 检测最低余额
            if (info.getMc().compareTo(amt) >= 1) {
                json.addError(this.getText("user.error.851", new String[]{DF2.format(info.getMc())}));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 检测最高限额
            if (info.getMb().compareTo(BigDecimal.ZERO) >= 1) {
                if (amt.compareTo(info.getMb()) >= 1) {
                    json.addError(this.getText("user.error.852", new String[]{DF2.format(info.getMb())}));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            } // 产品余额
            BigDecimal yu = info.getMa().subtract(info.getMd());
            if (amt.compareTo(yu) >= 1) {
                json.addError(this.getText("user.error.853"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 产品售罄
            if (info.getMc().compareTo(yu) >= 1) {
                json.addError(this.getText("user.error.855"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 产品Model
            ProdModel m = this.getProdModelService().getProdModelByTid(info.getTid());
            if (m == null) {
                json.addError(this.getText("user.error.856"));
                System.out.println("json:" + json.toString());
                return JSON;
            }
            //add by luxh 会员权益日当天黄金会员可以使用两张加息券
            UserSession us = this.getUserSession();
            long time = GMTime.currentTimeMillis();
            UserVip userVip = new UserVip();
            boolean isVipTwo = false;
            try {
                userVip = this.getUserVipService().queryVipLog(us.getUid(), time);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (userVip.getLevel() > 1) {
                isVipTwo = true;
            }
            //end

            // 优惠券信息
            long cid = this.getLong("sid");
            long cid1 = this.getLong("sid1");

            //UserSession us = this.getUserSession();
            UserCoupon uc = null;
            if (cid > 0) {
                uc = this.getUserCouponService().findCouponBySid(cid);
                if (uc == null) {
                    // Ignored
                } else if (uc.getUid() != us.getUid()) {
                    uc = null; // Ignored
                } else if (uc.getState() != STATE_NORMAL || time >= uc.getEday()) {
                    json.addError(this.getText("user.error.857"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                } else {
                    if (STATE_DISABLE != m.getTofee()) {
                        json.addError(this.getText("user.error.858"));
                        System.out.println("json:" + json.toString());
                        return JSON;
                    } // 投资金额不满足
                    if (uc.getToall().compareTo(amt) >= 1) {
                        json.addError(this.getText("user.error.859"));
                        System.out.println("json:" + json.toString());
                        return JSON;
                    } // 理财天数不满足
                    if (uc.getToday() > info.getAj()) {
                        json.addError(this.getText("user.error.859"));
                        System.out.println("json:" + json.toString());
                        return JSON;
                    }
                }
            }
            UserCoupon uc1 = null;
            if (cid1 > 0) {
                if (isVipTwo) {
                    uc1 = this.getUserCouponService().findCouponBySid(cid1);
                    if (uc1 == null) {
                        // Ignored
                    } else if (uc1.getUid() != us.getUid()) {
                        uc1 = null; // Ignored
                    } else if (uc1.getState() != STATE_NORMAL || time >= uc1.getEday()) {
                        json.addError(this.getText("user.error.857"));
                        System.out.println("json:" + json.toString());
                        return JSON;
                    } else {
                        if (STATE_DISABLE != m.getTofee()) {
                            json.addError(this.getText("user.error.858"));
                            System.out.println("json:" + json.toString());
                            return JSON;
                        } // 投资金额不满足
                        if (uc1.getToall().compareTo(amt) >= 1) {
                            json.addError(this.getText("user.error.859"));
                            System.out.println("json:" + json.toString());
                            return JSON;
                        } // 理财天数不满足
                        if (uc1.getToday() > info.getAj()) {
                            json.addError(this.getText("user.error.859"));
                            System.out.println("json:" + json.toString());
                            return JSON;
                        }
                    }


                }
            }

            // 全局投资次数
            if (m.getTotal() >= 1) {
                if (this.getTradeInfoService().isProdByAll(us.getUid(), m.getTid(), m.getTotal())) {
                    json.addError(this.getText("user.error.860", new String[]{m.getName(), String.valueOf(m.getTotal())}));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            } // 当前标的次数
            if (m.getToall() >= 1) {
                if (this.getTradeInfoService().isProdByPid(us.getUid(), info.getPid(), m.getToall())) {
                    json.addError(this.getText("user.error.861", new String[]{info.getAa(), String.valueOf(m.getToall())}));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            } // 检测实名信息
            UserAuth a = this.getUserAuthService().findAuthByUid(us.getUid());
            if (a == null) {
                json.addError(this.getText("user.error.050"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 检测支付余额
            BigDecimal rmb = amt;
            int way = this.getInt("way");
            SysOrder s = new SysOrder();
            s.setSid(VeStr.getUSid());
            s.setUid(us.getUid());
            s.setPid(info.getPid());
            if (uc != null && uc1 != null) {
                if (uc.getType() == uc1.getType()) {
                    s.setCid(uc.getSid());
                    s.setCid1(uc1.getSid());
                    switch (uc.getType()) {
                        case 1: // 体验券
                            s.setWay("体验金购买");
                            rmb = amt.subtract(uc.getTma().add(uc1.getTma()));
                            break;
                        case 2: // 加息券
                            s.setTme(uc.getTmb().add(uc1.getTmb()));
                            break;
                        case 3: // 抵扣券
                        default:
                            rmb = amt.subtract(uc.getTma().add(uc1.getTma()));
                    } // 是否足额抵扣
                    if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                        rmb = BigDecimal.ZERO;
                    } // 扣除金额
                    if (uc.getType() == 1) {
                        s.setTmd(amt.subtract(rmb));
                    }
                } else {
                    s.setCid(uc.getSid());
                    s.setCid1(uc1.getSid());
                    switch (uc.getType()) {
                        case 1: // 体验券
                            s.setWay("体验金购买");
                            rmb = amt.subtract(uc.getTma());
                            break;
                        case 2: // 加息券
                            s.setTme(uc.getTmb());
                            break;
                        case 3: // 抵扣券
                        default:
                            rmb = amt.subtract(uc.getTma());
                    }
                    switch (uc1.getType()) {
                        case 1: // 体验券
                            s.setWay("体验金购买");
                            rmb = amt.subtract(uc1.getTma());
                            break;
                        case 2: // 加息券
                            s.setTme(uc1.getTmb());
                            break;
                        case 3: // 抵扣券
                        default:
                            rmb = amt.subtract(uc1.getTma());
                    }
                    // 是否足额抵扣
                    if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                        rmb = BigDecimal.ZERO;
                    } // 扣除金额
                    if (uc.getType() == 1 || uc1.getType() == 1) {
                        s.setTmd(amt.subtract(rmb));
                    }
                }
            } else if (uc != null) {
                s.setCid(uc.getSid());
                switch (uc.getType()) {
                    case 1: // 体验券
                        s.setWay("体验金购买");
                        rmb = amt.subtract(uc.getTma());
                        break;
                    case 2: // 加息券
                        s.setTme(uc.getTmb());
                        break;
                    case 3: // 抵扣券
                    default:
                        rmb = amt.subtract(uc.getTma());
                } // 是否足额抵扣
                if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                    rmb = BigDecimal.ZERO;
                } // 扣除金额
                if (uc.getType() == 1) {
                    s.setTmd(amt.subtract(rmb));
                }
            } // 检测支付方式
            s.setTid(info.getTid());
            s.setName(info.getAa());
            s.setRate((info.getAn().multiply(userVip.getReceipt())).add(info.getAds()));
            s.setAds(info.getAds());
            s.setTma(amt); // 总金额
            s.setTmb(rmb); // 支付金额
            s.setTmc(amt.subtract(rmb)); // 抵扣金额
            s.execute(info.getAh(), info.getAj());
            s.setMobile(us.getMobile());
            s.setNicer(a.getName()); // 真实姓名
            s.setState(STATE_NORMAL);
            s.setStext("待付款");
            if (way >= 3 && rmb.compareTo(BigDecimal.ZERO) >= 0) {
                s.setWay("银行卡支付");
                UserBank b = this.getUserBankService().findBankByUid(us.getUid());
                LogCharge c = new LogCharge();
                c.setSid(s.getSid() - 1); // 订单编号
                c.setUid(us.getUid());
                c.setAmount(rmb); // 交易金额
                c.setIdCard(b.getIdCard());
                c.setMobile(b.getMobile());
                c.setName(b.getName());
                c.setBankName(b.getBankName());
                c.setBankCard(b.getCardNo());
                c.setSigntp("MD5");
                c.setHSIP(VeStr.getRemoteAddr(request));
                int amount = VeRule.toPer(rmb).intValue(); // 金额处理
                PayInfo pay = this.getPayInfoService().getInfoByFuiou();
                c.setBackUrl(pay.getNotifyUrl());
                UserProto p = this.getUserChargeService().findProtoByUid(us.getUid());
                synchronized (doLock(us.getUid())) {
                    OrderResponse res = null;
                    if (p == null) {
                        OrderRequest req = new OrderRequest();
                        req.setMchntcd(pay.getSellid());
                        req.setMchntorderid(String.valueOf(c.getSid()));
                        req.setUserId(c.getUid());
                        req.setAmt(amount); // 支付金额
                        req.setBankcard(c.getBankCard());
                        req.setName(c.getName());
                        req.setIdno(c.getIdCard());
                        req.setMobile(c.getMobile().replace("+86-", ""));
                        req.setBackurl(c.getBackUrl());
                        req.setRem1(c.getHSIP());
                        req.setUserIP(c.getHSIP());
                        res = Fuiou.order(req, pay.getSecret()); // 首次充值
                    } else if (p.getCNo().equalsIgnoreCase(b.getCardNo())) {
                        ProtoRequest req = new ProtoRequest();
                        req.setMchntcd(pay.getSellid());
                        req.setMchntorderid(String.valueOf(c.getSid()));
                        req.setUserId(p.getUserId());
                        req.setAmt(amount); // 支付金额
                        req.setProtocolno(p.getSNo()); // 协议号
                        req.setBackurl(c.getBackUrl());
                        req.setRem1(c.getHSIP());
                        req.setUserIP(c.getHSIP());
                        res = Fuiou.order(req, pay.getSecret()); // 协议支付
                    } else {
                        Fuiou.unBindCard(pay.getSellid(), pay.getSecret(), p.getUserId(), p.getSNo());
                        p.setState(STATE_CHECK); // 更新状态信息
                        this.getUserChargeService().unBindProto(p);
                        OrderRequest req = new OrderRequest();
                        req.setMchntcd(pay.getSellid());
                        req.setMchntorderid(String.valueOf(c.getSid()));
                        req.setUserId(c.getUid());
                        req.setAmt(amount); // 支付金额
                        req.setBankcard(c.getBankCard());
                        req.setName(c.getName());
                        req.setIdno(c.getIdCard());
                        req.setMobile(c.getMobile().replace("+86-", ""));
                        req.setBackurl(c.getBackUrl());
                        req.setRem1(c.getHSIP());
                        req.setUserIP(c.getHSIP());
                        res = Fuiou.order(req, pay.getSecret()); // 新卡首次充值
                    } // 记录充值信息
                    c.setSignpay(res.getSignpay());
                    if (res.getResponsecode().equals("0000")) {
                        c.setOrderId(res.getOrderId());
                        c.setState(APState.ORDER_USER_NO_VERCD);
                        json.addObject();
                        json.append("sid", c.getSid());
                        json.append("orderid", c.getOrderId());
                    } else {
                        c.setState(APState.ORDER_USER_ACTION_FAIL);
                        json.addError(res.getResponsemsg());
                        s.setState(STATE_ERRORS);
                        s.setStext(res.getResponsemsg());
                    }
                    c.setRes_code(res.getResponsecode());
                    c.setRes_msg(res.getResponsemsg());
                }
                this.getUserChargeService().saveLog(c);
                this.getTradeInfoService().saveOrder(s);
                if (STATE_NORMAL == s.getState()) {
                    json.addObject();
                    json.append("sid", s.getSid());
                    json.append("way", 3); // 银行卡支付
                    json.append("mobile", VeRule.toStar(c.getMobile(), 4, 0, 3, 4, 4, "-"));
                } else {
                    json.addError(this.getText(s.getStext()));
                }
            } else {
                logger.info("come in 余额支付");
                if (s.getWay() == null) {
                    s.setWay("余额支付");
                }
                this.getTradeInfoService().saveOrder(s);
                json.addObject();
                json.append("sid", s.getSid());
                json.append("way", 0); // 余额支付
            }
        } catch (SQLException | JAXBException | IOException e) {
            json.addError(this.getText("user.error.856"));
        }
        System.out.println("json:" + json.toString());
        return JSON;
    }

    /**
     * 新版购买接口
     */
    public String submit() {
        logger.info("come in submit");
        AjaxInfo json = this.getAjaxInfo();
        String Pwd = this.getParameter("pwd");
        try {
            long sid = this.getLong("sid");
            SysOrder s = this.getTradeInfoService().findSysOrderBySid(sid);
            if (s == null || STATE_TOEXIT == s.getState()) {
                logger.info("no SysOrder" + sid);
                json.addError(this.getText("system.error.pars"));
                System.out.println("json:" + json.toString());
                return JSON;
            }
            long now = GMTime.currentTimeMillis();
            if ((now - 1800000) > s.getTime()) {
                s.setState(6); // 超时关闭
                this.getTradeInfoService().saveOrder(s);
                System.out.println("json:" + json.toString());
                return JSON;
            } // 根据信息校验
            UserSession us = this.getUserSession();
            boolean addM = false;
            int way = this.getInt("way");
            if (way >= 3) {
//                String code = this.getString("vcode");
                String code = this.getString("vcode");
                LogCharge c = this.getUserChargeService().findChargeBySid(sid - 1);
                if (c == null) {
                    json.addError(this.getText("user.error.856"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
                c.setVercd(code); // 验证码
                UserProto p = this.getUserChargeService().findProtoByUid(us.getUid());
                synchronized (doLock(us.getUid())) {
                    PayResponse res = null;
                    PayInfo pay = this.getPayInfoService().getInfoByFuiou();
                    if (p == null) {
                        PayRequest req = new PayRequest();
                        req.setMchntcd(pay.getSellid());
                        req.setMchntorderid(String.valueOf(c.getSid()));
                        req.setUserId(c.getUid());
                        req.setOrderId(c.getOrderId());
                        req.setBankcard(c.getBankCard());
                        req.setMobile(c.getMobile().replace("+86-", ""));
                        req.setVercd(code);
                        req.setSignpay(c.getSignpay());
                        req.setRem1(String.valueOf(c.getUid()));
                        req.setUserIP(c.getHSIP());
                        res = Fuiou.toPay(req, pay.getSecret());
                    } else {
                        ProtoPayRequest req = new ProtoPayRequest();
                        req.setMchntcd(pay.getSellid());
                        req.setMchntorderid(String.valueOf(c.getSid()));
                        req.setUserId(p.getUserId());
                        req.setOrderId(c.getOrderId());
                        req.setProtocolno(p.getSNo());
                        req.setVercd(code);
                        req.setSignpay(c.getSignpay());
                        req.setRem1(String.valueOf(c.getUid()));
                        req.setUserIP(c.getHSIP());
                        res = Fuiou.toPay(req, pay.getSecret());
                        //TODO  待删除
                        addM = true;
                    } // 执行付款操作
                    if (res.getResponsecode().equals("0000")) {
                        c.setState(APState.ORDER_PAY_NO_BACK);
                        addM = true;
                    } else {
                        c.setState(APState.ORDER_USER_PAY_FAIL);
                    } // 返回充值结果
                    c.setRes_code(res.getResponsecode());
                    c.setRes_msg(res.getResponsemsg());
                    this.getUserChargeService().saveLog(c);
                    if (addM) {
                        UserRmbs r = this.getUserChargeService().recharge(c);
                        if (r == null) {
                            s.setState(STATE_ERRORS);
                            s.setStext(this.getText("支付超时，请联系客服！"));
                            this.getTradeInfoService().saveOrder(s);
                            json.addError(s.getStext());
                            System.out.println("json:" + json.toString());
                            return JSON;
                        } else {
                            us.setMoney(r.getTotal());
                        }
                    } else {
                        s.setState(STATE_ERRORS);
                        s.setStext(res.getResponsemsg());
                        this.getTradeInfoService().saveOrder(s);
                        json.addError(res.getResponsemsg());
                        System.out.println("json:" + json.toString());
                        return JSON;
                    }
                }
            } else if (Pwd == null || Pwd.length() < 6) {
                json.addError(this.getText("user.error.027"));
                System.out.println("json:" + json.toString());
                return JSON;
            } else {
                Pwd = VeStr.toMD5(Pwd);
                UserAuth a = this.getUserAuthService().findAuthByUid(us.getUid());
                if (a == null) {
                    s.setState(STATE_CHECK);
                    s.setStext("实名认证错误！");
                    this.getTradeInfoService().saveOrder(s);
                    json.addError(this.getText("user.error.050"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                } // 校对支付密码
                if (!Pwd.equalsIgnoreCase(a.getPays())) {
                    s.setState(STATE_READER);
                    s.setStext("支付密码错误！");
                    this.getTradeInfoService().saveOrder(s);
                    json.addError(this.getText("user.error.028"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            } // 产品信息加载
            ProdInfo info = this.getProdInfoService().findProdByPid(s.getPid());
            if (info == null) {
                json.addError(this.getText("system.error.none"));
                System.out.println("json:" + json.toString());
                return JSON;
            } // 券码信息处理
            UserCoupon uc = null;
            if (s.getCid() >= USER_UID_MAX) {
                uc = this.getUserCouponService().findCouponBySid(s.getCid());
                if (uc == null || STATE_NORMAL != uc.getState()) {
                    json.addError(this.getText("user.error.857"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            }
            UserCoupon uc1 = null;
            if (s.getCid1() >= USER_UID_MAX) {
                uc1 = this.getUserCouponService().findCouponBySid(s.getCid1());
                if (uc1 == null || STATE_NORMAL != uc1.getState()) {
                    json.addError(this.getText("user.error.857"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
            }
            logger.info("执行投资信息操作");
            // 执行投资信息操作
            synchronized (doLock(us.getUid())) {
                BigDecimal rmb = s.getTma();
                LogOrder log = new LogOrder();


                if (uc != null && uc1 != null) {
                    if (uc.getType() == uc1.getType()) {
                        log.setCid(uc.getSid());
                        switch (uc.getType()) {
                            case 1: // 体验券
                                log.setWay("体验金购买");
                                rmb = s.getTma().subtract(uc.getTma().add(uc1.getTma()));
                                break;
                            case 2: // 加息券
                                log.setTme((uc.getTmb().add(uc1.getTmb())));
                                break;
                            case 3: // 抵扣券
                            default:
                                rmb = s.getTma().subtract(uc.getTma().add(uc1.getTma()));
                        } // 是否足额抵扣
                        if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                            rmb = BigDecimal.ZERO;
                        } // 扣除金额
                        if (uc.getType() == 1) {
                            s.setTmd(s.getTma().subtract(rmb));
                        }
                    } else {
                        s.setCid(uc.getSid());
                        s.setCid1(uc1.getSid());
                        switch (uc.getType()) {
                            case 1: // 体验券
                                log.setWay("体验金购买");
                                rmb = s.getTma().subtract(uc.getTma());
                                break;
                            case 2: // 加息券
                                log.setTme(uc.getTmb());
                                break;
                            case 3: // 抵扣券
                            default:
                                rmb = s.getTma().subtract(uc.getTma());
                        }
                        switch (uc1.getType()) {
                            case 1: // 体验券
                                log.setWay("体验金购买");
                                rmb = s.getTma().subtract(uc1.getTma());
                                break;
                            case 2: // 加息券
                                log.setTme(uc.getTmb());
                                break;
                            case 3: // 抵扣券
                            default:
                                rmb = s.getTma().subtract(uc1.getTma());
                        }
                        // 是否足额抵扣
                        if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                            rmb = BigDecimal.ZERO;
                        } // 扣除金额
                        if (uc.getType() == 1 || uc1.getType() == 1) {
                            s.setTmd(s.getTma().subtract(rmb));
                        }
                    }
                } else if (uc != null) {
                    s.setCid(uc.getSid());
                    switch (uc.getType()) {
                        case 1: // 体验券
                            log.setWay("体验金购买");
                            rmb = s.getTma().subtract(uc.getTma());
                            break;
                        case 2: // 加息券
                            log.setTme(uc.getTmb());
                            break;
                        case 3: // 抵扣券
                        default:
                            rmb = s.getTma().subtract(uc.getTma());
                    } // 是否足额抵扣
                    if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                        rmb = BigDecimal.ZERO;
                    } // 扣除金额
                    if (uc.getType() == 1) {
                        s.setTmd(s.getTma().subtract(rmb));
                    }
                }







               /* if (uc != null  ) {
                    log.setCid(uc.getSid());
                    switch (uc.getType()) {
                        case 1: // 体验券
                            log.setWay("体验金购买");
                            rmb = s.getTma().subtract(uc.getTma());
                            break;
                        case 2: // 加息券
                            log.setTme(uc.getTmb());
                            break;
                        case 3: // 抵扣券
                        default:
                            rmb = s.getTma().subtract(uc.getTma());
                    } // 是否足额抵扣
                    if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                        rmb = BigDecimal.ZERO;
                    } // 扣除金额
                    if (uc.getType() == 1) {
                        log.setTmd(s.getTma().subtract(rmb));
                    }
                }
                if (uc != null) {
                    log.setCid(uc.getSid());
                    switch (uc.getType()) {
                        case 1: // 体验券
                            log.setWay("体验金购买");
                            rmb = s.getTma().subtract(uc.getTma());
                            break;
                        case 2: // 加息券
                            log.setTme(uc.getTmb());
                            break;
                        case 3: // 抵扣券
                        default:
                            rmb = s.getTma().subtract(uc.getTma());
                    } // 是否足额抵扣
                    if (rmb.compareTo(BigDecimal.ZERO) <= 0) {
                        rmb = BigDecimal.ZERO;
                    } // 扣除金额
                    if (uc.getType() == 1) {
                        log.setTmd(s.getTma().subtract(rmb));
                    }
                }
*/


                // 检测账户余额是否足
                if (rmb.compareTo(s.getTmb()) == 0) {
                    // Next todo
                } else {
                    json.addError(this.getText("user.error.856"));
                    System.out.println("json:" + json.toString());
                    return JSON;
                }
                log.setSid(s.getSid());
                log.setUid(us.getUid());
                log.setPid(s.getPid());
                log.setTid(s.getTid());
                log.setName(s.getName());
                log.setRate(s.getRate());
                log.setRday(s.getRday());
                log.setAny(s.getRday());
                log.setDay(s.getDay());
                log.setWay(s.getWay());
                log.setAds(s.getAds());
                log.setTma(s.getTma()); // 总金额
                log.setTmb(rmb); // 支付金额
                log.setTmc(s.getTma().subtract(rmb));
                log.setTmd(s.getTmd());
                log.setTme(s.getTme());
                log.setTmf(s.getTmf());
                log.setTmg(s.getTmg());
                log.setYma(s.getYma());
                log.setYmb(s.getYmb());
                log.setGmtA(s.getGmtA());
                log.setGmtB(s.getGmtB());
                log.setGmtC(s.getGmtC());
                log.setGmtD(s.getGmtD());
                logger.info("getTradeInfoService.commit");
                int state = this.getTradeInfoService().commit(info, log, uc);
                if (state == 0) {
                    s.setState(STATE_TOEXIT);
                    s.setStext("成功");
                    json.addObject();
                    json.append("id", log.getSid());
                    json.append("name", log.getName());
                    json.append("yma", DF2.format(s.getYma()));
                    json.append("amt", DF2.format(s.getTma()));
                    json.append("yma", DF2.format(s.getYma()));
                    json.append("ymb", DF2.format(s.getYmb()));
                    StringBuilder sb = new StringBuilder();
                    if (log.getTmc().compareTo(BigDecimal.ZERO) >= 1) {
                        sb.append('，').append("抵扣：").append(DF2.format(log.getTmc()));
                    }
                    if (log.getTme().compareTo(BigDecimal.ZERO) >= 1) {
                        sb.append('，').append("加息：").append(DF2.format(log.getTme())).append("%");
                    } else if (sb.length() <= 0) {
                        sb.append('，').append("无");
                    } // 信息
                    json.append("text", sb.substring(1));
                    json.append("time", GMTime.format(log.getTime(), GMTime.CHINA));
                    json.append("gmtb", GMTime.format(log.getGmtB(), GMTime.CHINA, GMTime.OUT_SHORT));
                    json.append("gmtc", GMTime.format(log.getGmtC(), GMTime.CHINA, GMTime.OUT_SHORT));
                    //保存用户投标获取的猫粮
                    long time = GMTime.currentTimeMillis();
                    UserVip userVip = new UserVip();
                    try {
                        userVip = this.getUserVipService().queryVipLog(us.getUid(), time);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("获取用户VIP信息失败");
                    }
                /*
                catfood 计算方式
                    普通会员 RMB*0.003*day
                    白银会员 RMB*0.003*day*1.05
                    黄金会员 RMB*0.003*day*1.10
                 */
                    int catFoodInt = 0;
                    BigDecimal catFood = s.getTma().multiply(new BigDecimal(s.getRday())).multiply(new BigDecimal(1.00)).multiply(new BigDecimal(0.003));
                    if (userVip.getLevel() < 2) {
                        catFoodInt = (catFood.multiply(new BigDecimal(1.05))).intValue();
                    } else {
                        catFoodInt = (catFood.multiply(new BigDecimal(1.10))).intValue();
                    }
                    try {
                        this.getUserCatService().updateCatFood(us.getUid(), catFoodInt);
                    } catch (Exception e) {
                        logger.error("存储猫粮数据出错");
                        e.printStackTrace();
                        json.addError("存储猫粮数据出错,请稍后再试");
                    }
                    //投标金额大于等于1000时，产生福袋，返回给前台福袋ID
                    if (s.getTma().compareTo(new BigDecimal("1000")) >= 0) {
                        logger.info(String.format("[%s]投标金额[%s]大于等于1000元，满足福袋生成条件", us.getUid(), s.getTma()));
                        LuckyBagCondfig luckyBagCondfig = new LuckyBagCondfig();
                        try {
                            luckyBagCondfig = this.getLuckyBagService().qryLuckyBagConfig(s.getTma());
                            logger.info(String.format("本次福袋创建规则:[%s]", luckyBagCondfig.toString()));
                        } catch (Exception e) {
                            logger.error("查询福袋配置出错，请检查相关配置");
                            e.printStackTrace();
                        }
                        if (luckyBagCondfig.getNum() > 1) {
                            logger.info("开始根据福袋产生规则产生福袋");
                            BigDecimal amountBag = (s.getTma().multiply(new BigDecimal("0.0011")).multiply(new BigDecimal(s.getRday()))).setScale(2, BigDecimal.ROUND_HALF_UP);
//                            createBag(luckyBagCondfig.getNum(),amountBag,luckyBagCondfig.getLastEnvelopes());


                            try {

                                long bagId = VeStr.getUSid();
                                //保存福袋到福袋发送表中，time暂时0，等待点击发送后更新time
                                LuckyBagSend luckyBagSend = new LuckyBagSend();
                                luckyBagSend.setBagId(bagId);
                                luckyBagSend.setUid(us.getUid());
                                luckyBagSend.setLendMoney(s.getTma());
                                luckyBagSend.setLastEnvelopes(luckyBagCondfig.getLastEnvelopes());
                                luckyBagSend.setNum(luckyBagCondfig.getNum());
                                luckyBagSend.setSid(s.getSid());
                                luckyBagSend.setCreateTime(System.currentTimeMillis());
                                luckyBagSend.setBagCount(amountBag);
                                logger.info("luckyBagSend:"+luckyBagSend.toString());
                                this.getLuckyBagService().insertLuckBag(luckyBagSend);
                                logger.info("保存福袋成功");
                                json.append("luckyBag",bagId);
                            } catch (Exception e) {
                                logger.error("生成随机红包失败");
                                e.printStackTrace();
                            }
                        } else {
                            logger.info("查询福袋规则失败");
                        }
                    } else {
                        logger.info(String.format("[%s]投标金额[%s]小于1000元，不满足福袋生成条件", us.getUid(), s.getTma()));
                    }
                } else {
                    if (state == 1) {
                        s.setState(STATE_DELETE);
                        s.setStext(this.getText("user.error.853"));
                    } else {
                        s.setState(STATE_CHECK);
                        s.setStext(this.getText("user.error.854"));
                    }
                    if (addM) {
                        json.addError(this.getText("user.error.865"));
                    } else {
                        json.addError(s.getStext());
                    }
                } // 保存预处理订单
                logger.info("getTradeInfoService.saveOrder");
                this.getTradeInfoService().saveOrder(s);
                logger.info("end saveOrder");


            }
        } catch (SQLException | JAXBException | IOException e) {
            json.addError(this.getText("system.error.info"));
        } finally {
            Pwd = null;
        }
        logger.info("json:" + json.toString());
        return JSON;
    }

    public UserVipService getUserVipService() {
        return userVipService;
    }

    public void setUserVipService(UserVipService userVipService) {
        this.userVipService = userVipService;
    }

    public UserCatService getUserCatService() {
        return userCatService;
    }

    public void setUserCatService(UserCatService userCatService) {
        this.userCatService = userCatService;
    }

    public LuckyBagService getLuckyBagService() {
        return luckyBagService;
    }

    public void setLuckyBagService(LuckyBagService luckyBagService) {
        this.luckyBagService = luckyBagService;
    }

    /*
     * @NAME:createBag
     * @DESCRIPTION:红包随机生成
     * @AUTHOR:luxh
     * @DATE:2018/8/3
     * @VERSION:1.0
     */
    public List<BigDecimal> createBag(int num, BigDecimal amount, BigDecimal lastEnvelopes) {
        BigDecimal lastBag = amount.multiply(lastEnvelopes);
        amount = amount.subtract(lastBag);
        List<BigDecimal> bigDecimals = new ArrayList<>();
        boolean flag = false;
        for (int i = 0; i < num; i++) {
            if (flag) {

            }
        }
        return null;
    }
}
