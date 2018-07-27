package com.ypiao.service;

import java.sql.SQLException;
import java.util.List;
import com.ypiao.bean.ServerInfo;

public interface NodeInfoService {

	public void saveAll(List<ServerInfo> ls) throws SQLException;

	public List<ServerInfo> findByAll() throws SQLException;

}
