<%@taglib uri="/WEB-INF/struts-tags.tld" prefix="s"%><%
request.setAttribute("decorator", "none");
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setHeader("Content-type", "text/html;charset=utf-8");
response.setDateHeader("Expires", 0); //prevents caching at the proxy server
response.setContentType("application/json");
%><s:property value="ajaxInfo.getString()" escape="false"/>