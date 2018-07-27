<%@taglib uri="/WEB-INF/struts-tags.tld" prefix="s"
%><!doctype html><html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
<title><s:sites type="sitename" name="system.title"/></title>
<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript" src="js/login.js"></script>
<style>
body{background-color:#E3E8EB;font-size:13px;font-family:Arial,sans-serif;color:#FFF;}
*{margin:0;padding:0;list-style:none;}form,img{border:none;}
button{padding:0 10px;font-weight:normal;font-family:inherit;}button,label{cursor:pointer}
.login_bg{position:absolute;width:100%;height:40%;background:#CCDAE7 url('images/login_bg.jpg') left bottom repeat-x;}
.login_info{position:absolute;top:40%;left:50%;margin:-209px -350px;width:700px;height:500px;background:url('images/login.jpg') no-repeat;}
.login{width:320px;margin:auto;margin-top:250px;}
.login td{height:36px;line-height:30px;padding:2px;font-size:14px;color:#3A4B88;}
.login td.field_label{text-align:right;}
.footer{position:absolute;width:100%;bottom:45px;text-align:center;color:#F00;}
.fbs{float:left;margin:5px;}.fbs button{height:30px;line-height:25px;padding:0 15px;font-size:13px;}
.xf-text{height:18px;line-height:18px;padding:2px 3px;border:#58A 1px solid;vertical-align:middle;}
.xf-field{font:12px tahoma,arial,helvetica,sans-serif;}
.xf-select{height:21px;line-height:23px;padding:2px;}
.xf-btn{height:25px;line-height:20px;overflow:hidden;vertical-align:middle;font-size:12px}
</style>
</head>
<body>
<div class="login_bg"></div>
<div id="login_info" class="login_info">
<form method="post" name="form" action="login.jsp" onsubmit="return my.submit()">
<table class="login" align="center" cellpadding="0" cellspacing="0">
 <tr>
  <td class="field_label"><span class="lang" id="login_msg_user">登录账号</span>:</td>
  <td id="name_box"></td>
 </tr>
 
 <tr>
  <td class="field_label"><span class="lang" id="login_msg_pass">登录密码</span>:</td>
  <td id="pass_box"></td>
 </tr>
 
 <tr>
  <td class="field_label"><span class="lang" id="login_msg_pass">工号验证</span>:</td>
  <td id="num_box"></td>
 </tr>
 
 <tr>
  <td class="field_label"></td>
  <td><div class="fbs" unselectable="on"><button id="login_msg_submit">登录</button></div><div class="fbs" unselectable="on"><button id="login_msg_reset" onclick="my.reset();return false;">重置</button></td>
 </tr>
</table>

<div class="footer"><span class="lang" id="login_service">销售与服务热线：</span><s:property value="tels" escape="false"/></div>
</form></div>
</body>
</html>