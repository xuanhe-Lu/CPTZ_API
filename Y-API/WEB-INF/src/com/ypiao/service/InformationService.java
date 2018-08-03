package com.ypiao.service;

import com.ypiao.bean.Information;

import java.util.List;

/**
 * @NAME:InformationService
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/8/3
 * @VERSION:1.0
 */
public interface InformationService {
    public List<Information>  findInfo(int type)throws Exception ;
}
