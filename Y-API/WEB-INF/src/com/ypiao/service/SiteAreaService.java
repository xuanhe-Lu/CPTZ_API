package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.RegionInfo;

public interface SiteAreaService {

	public void saveRegion(RegionInfo info) throws SQLException;

}
