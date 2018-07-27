package com.ypm.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.*;

public interface AderInfoService {

	public static final String ADER_DIR = "ads";

	public AdsInfo findAderBySid(String sid) throws SQLException;

	public AjaxInfo findAderByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo findTreeByInfo(int tid);

	public void saveAds(List<AdsInfo> ads) throws SQLException;

	public void saveAder(AdsInfo ads, FileInfo f) throws IOException, SQLException;

	public void orderInfo(String ids) throws SQLException;

	public void stateInfo(String ids, int state) throws SQLException;

	public void removeInfo(String ids) throws SQLException;
}
