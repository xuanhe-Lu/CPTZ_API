package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.Message;
import com.ypm.bean.ServerInfo;

public interface NodeInfoService {

	public List<ServerInfo> findByAll() throws SQLException;

	public void saveInfo(ServerInfo info) throws SQLException;

	public void sendByAll(Message msg) throws SQLException;

}
