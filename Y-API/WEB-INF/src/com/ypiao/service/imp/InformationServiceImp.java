package com.ypiao.service.imp;

import com.ypiao.bean.Information;
import com.ypiao.data.JPrepare;
import com.ypiao.service.InformationService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @NAME:InformationServiceImp
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/8/3
 * @VERSION:1.0
 */
public class InformationServiceImp implements InformationService {
    @Override
    public List<Information> findInfo(int type) throws Exception {
        Connection conn = JPrepare.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select type,name,img,dist,time,url from ypiao.financial_info where type  = ?");
            ps.setInt(1, type);
            ResultSet rs = ps.executeQuery();
            List<Information> list = new ArrayList<>();
            while (rs.next()) {
                Information information = new Information();
                information.setType(rs.getInt(1));
                information.setName(rs.getString(2));
                information.setImg(rs.getString(3));
                information.setDist(rs.getString(4));
                information.setTime(rs.getLong(5));
                information.setUrl(rs.getString(6));
                list.add(information);
            }
            return list;
        } finally {
            JPrepare.close(ps, conn);
        }
    }
}
