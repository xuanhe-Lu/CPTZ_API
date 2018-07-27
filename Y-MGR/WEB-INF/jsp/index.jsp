<%@taglib uri="/WEB-INF/struts-tags.tld" prefix="s"
%><!doctype html><html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
<title><s:sites type="sitename" name="system.title"/></title>
<link rel="stylesheet" type="text/css" href="ext/css/ext-all.css"/>
<link rel="stylesheet" type="text/css" href="ext/css/menu.css"/>
<link rel="stylesheet" type="text/css" href="fjs/css/index.css"/>
<link rel="stylesheet" type="text/css" href="css/common-cn.css"/>
<link rel="stylesheet" type="text/css" href="css/oa-module.css"/>
<link rel="stylesheet" type="text/css" href="css/portlet.css"/> 
<script type="text/javascript" src="ext/js/ext-all.js"></script>
<script type="text/javascript" src="ext/js/ext-fjs.js"></script>
<script type="text/javascript" src="ext/js/lang-cn.js"></script>
<script type="text/javascript" src="parf/js/ext-parf.js"></script>
<script type="text/javascript" src="parf/js/DisplayMacroField.js"></script> 
<script type="text/javascript" src="parf/js/OpinionField.js"></script>
<script type="text/javascript" src="tiny_mce/tiny_mce.js"></script>
<script type="text/javascript" src="ext/index.js"></script>
<script language="javascript">
var debug="null";
var _isCF=false;
var mxBasePath="parm/workflow/base";
window.exportStatus="";
var indexView;
Ext.onReady(function(){
	Ext.useShims=true;
	Ext.parf.form.TinyMCE.initTinyMCE();
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget='side';
	indexView = new Ext.fjs.Index(debug,"fjs.oa.desktop.personal");
	(function(){Ext.fjs.module.loadLib(['upload','extux','option','websign','pubdoc_plugin','userselection','chart','altermsg'])}).defer(1000);
	(function(){checkIA300();}).defer(1000);
});
window.onbeforeunload=function(){
	if(!Ext.isIE){if(window.exportStatus=='export'){window.exportStatus=''}else{Ext.fjs.module.tabPanel.removeAll()}}
};
function checkIA300(){}
</script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"></body>
</html>