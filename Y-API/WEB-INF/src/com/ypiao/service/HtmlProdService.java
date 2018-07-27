package com.ypiao.service;

import com.ypiao.bean.HtmlInfo;
import com.ypiao.bean.SystemInfo;

public interface HtmlProdService {

	public HtmlInfo setAtOrder(HtmlInfo html, SystemInfo sys, long Pid) throws Exception;
}
