package com.ypm.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Config;
import com.ypm.bean.FileInfo;
import com.ypm.bean.SetClient;

/**
 * 系统参数配置业务层接口定义. 
 */
public interface ConfigInfoService {

	public static final int ADT_YPLC = 1;

	public AjaxInfo findConfigByAll();

	public AjaxInfo findConfigByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo findClientByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public SetClient findClientBySid(int sid) throws SQLException;

	public SetClient findClientByTid(int tid) throws SQLException;

	public void saveClient(SetClient sc) throws IOException, SQLException;
	
	public void saveClient(SetClient sc, FileInfo fileInfo) throws IOException, SQLException;

	public void updateClient(int type, String ids, int state) throws IOException, SQLException;

	public void removeClient(int type, String ids) throws IOException, SQLException;

	public Config findConfigById(String id);

	public void saveConfig(Config cfg) throws SQLException;

	public boolean isConfigById(String id);

	public void orderConfig(String ids) throws SQLException;

	public boolean removeConfig(String ids) throws IOException, SQLException;

}
