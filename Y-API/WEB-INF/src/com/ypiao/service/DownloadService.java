package com.ypiao.service;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface DownloadService {

	public boolean doGet(HttpServletRequest req, HttpServletResponse res, String uri) throws ServletException, IOException;

}
