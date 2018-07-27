package com.ypm.service;

import java.sql.SQLException;
import java.util.Map;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.RegionInfo;
import com.ypm.bean.SiteCity;

public interface SiteAreaService {

	public void saveRegion(RegionInfo info) throws Exception;

	public boolean isRegionByCode(String code);

	public boolean isRegionByName(String code, String name);

	public AjaxInfo findRegionChildrens(String code);

	public AjaxInfo findRegionChildrens(String code, int tj) throws SQLException;

	public RegionInfo findRegionInfo(String code);

	public void removeRegion(String code) throws Exception;

	public Map<String, SiteCity> getProvince();

	public Map<String, SiteCity> getSiteCity(String code);

	public SiteCity getSiteCityByCode(String code);

	/** 地址拼接->All */
	public String getCityByAll(String code);

}
