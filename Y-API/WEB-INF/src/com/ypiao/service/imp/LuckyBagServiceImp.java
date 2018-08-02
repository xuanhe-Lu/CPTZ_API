package com.ypiao.service.imp;

import com.ypiao.bean.LuckyBagCondfig;
import com.ypiao.data.JPrepare;
import com.ypiao.service.LuckyBagService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
            ps.setBigDecimal(1,money);
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
}
