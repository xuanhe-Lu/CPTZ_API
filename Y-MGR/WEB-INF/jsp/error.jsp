<%@taglib uri="/WEB-INF/struts-tags.tld" prefix="s"%><%
%><!doctype html><html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
<title>500 - 内部服务器错误。</title>
<style type="text/css">
<!--
body{margin:0;font-size:0.7em;font-family:Verdana,Arial,Helvetica,sans-serif;background:#EEE;}
fieldset{padding:5px 15px} 
h1{font-size:2.4em;margin:0px;color:#FFF;}
h2{font-size:1.7em;margin:0px;font:700 17px/20px Arial,'微软雅黑';color:#C00;} 
h3{font-size:1.2em;margin-top:10px;font:700 13px/20px Arial,'微软雅黑';color:#000;} 
#header{width:96%;margin:0px;padding:6px 2%;font-family:"trebuchet MS", Verdana, sans-serif;color:#FFF;background-color:#555;}
#content{margin:0 0 0 2%;position:relative;}
.container{background:#FFF;width:96%;margin-top:8px;padding:10px;position:relative;}
-->
</style>
</head>
<body>
<div id="header"><h1>服务器错误</h1></div>
<div id="content">
 <div class="container">
  <fieldset>
   <h2>500 - 内部服务器错误。</h2>
   <h3>您查找的资源存在问题，因而无法显示。<br/><s:property value="ajaxInfo.getString()" escape="false"/></h3>
  </fieldset>
 </div>
</div>
</body>
</html>