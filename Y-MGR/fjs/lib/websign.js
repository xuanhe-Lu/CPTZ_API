Ext.namespace('Ext.parm.websign');
Ext.parm.websign.SignField = Ext.extend(Ext.form.Field,{
	signObjectId: null,
	
    defaultAutoCreate:{tag: "div"},
    
    onRender: function(ct, position)
    {
        Ext.parm.websign.SignField.superclass.onRender.call(this, ct, position); 
        this.createObject();
    },
    createObject:function()
    {
    	this.signObjectId = 'DWebSignSeal'+ Ext.id();
        var s = "<object id='" + this.signObjectId + "' classid='CLSID:77709A87-71F9-41AE-904F-886976F99E3E' "
        s += " style='position:absolute;width:0px;height:0px;left:0px;top:0px;' codebase='websign.dll#version=4,1,0,0'>"        
        s += "</OBJECT>"        
        this.el.dom.innerHTML = s;
    },
    removeObject:function()
    {
    	var signObj = document.getElementById(this.signObjectId);           
        var strObjectName;
        strObjectName = signObj.FindSeal("", 0);
        while (strObjectName != "") 
        {               
            var pos = signObj.GetPosition(strObjectName);
            var posobj = document.getElementById(pos);
            for(var i=0;i<posobj.childNodes.length;i++)
            {               
                posobj.removeChild(posobj.childNodes[i]);
            }             
            strObjectName = signObj.FindSeal(strObjectName, 0);
        }
    	for(var i=0;i<this.el.dom.childNodes.length;i++)
        {               
           this.el.dom.removeChild(this.el.dom.childNodes[i]);
        } 
    },
    reloadObject:function()
    {
    	this.removeObject();   
        this.createObject();
    },
    beforeDestroy:function()
    {                        
    	this.removeObject();   	
        Ext.parm.websign.SignField.superclass.beforeDestroy.call(this);
    },	
	/*
     * sealName:印章名称 sealPostion:印章绑定的位置signData:印章绑定的数据
    */        
	addseal : function(sealName, sealPostion, signData) 
	{
		if(!sealPostion)  sealPostion = sealName + "sealpostion";
		if(!signData)     signData = sealName;
		try 
		{
			var signObj = document.getElementById(this.signObjectId);
			// 是否已经盖章
			var strObjectName;
			strObjectName = signObj.FindSeal("", 0);
			while (strObjectName != "") 
			{				
				if (sealName == strObjectName) {
					alert("当前页面已经加盖过印章：【" + sealName + "】请核实");
					return false;
				}
				strObjectName = signObj.FindSeal(strObjectName, 0);
			}
			// 设置当前印章绑定的表单域
			this.Enc_onclick(signData);
			signObj.SetCurrUser("盖章人");
			signObj.SetPosition(1, 1, sealPostion);
			signObj.AddSeal("", "");

		} catch (e) {
			alert("控件没有安装，请刷新本页面，控件会自动下载。\r\n或者下载安装程序安装。" + e);
		}
	},
	Enc_onclick : function(tex_name) 
	{
		try 
		{
			var signObj = document.getElementById(this.signObjectId);
			// 清空原绑定内容
			signObj.SetSignData("-");
			// str为待绑定的字符串数据
			signObj.SetSignData("+LIST:" + tex_name + ";");
		} catch (e) {
			alert("控件没有安装，请刷新本页面，控件会自动下载。\r\n或者下载安装程序安装。" + e);
		}
	},	
	/* 
	 * sealName:手写名称       sealPostion:印章绑定的位置       signData:印章绑定的数据
    */
	handwrite : function (sealName, sealPostion,signData)
	{
		var newsealName = sealName + "handwrite";
		if(!sealPostion)  sealPostion = sealName + "sealpostion";
        if(!signData)     signData = sealName;
		try
		{
			var signObj = document.getElementById(this.signObjectId);
	        //设置当前印章绑定的表单域
	        this.Enc_onclick(signData);
	        //设置签名人，可以是OA的用户名
	        signObj.SetCurrUser("全屏手写");
	        //设置签名时间，可以有服务器传过来
	        //document.all.SetCurrTime("2006-02-07 11:11:11");
	                 //调用签名的接口
	        signObj.SetPosition(100,10,sealPostion);       
	        if("" == signObj.HandWrite(0,255,newsealName)){
	             alert("全屏幕签名失败");
	             return false;
	        } 
	    }catch(e) {
	        alert("控件没有安装，请刷新本页面，控件会自动下载。\r\n或者下载安装程序安装。" +e);
	    }
	},
	getValue:function()
	{
		var signObj = document.getElementById(this.signObjectId);
		var v = signObj.GetStoreData();
		if(v.length =="")
		{			
			return "";
		}
		return v; 
	},
	setValue:function(sealdata)
	{
		var signObj = document.getElementById(this.signObjectId);		
		signObj.SetStoreData(sealdata);
		signObj.ShowWebSeals();
		
		this.lockSealPosition();
	},
	lockSealPosition:function()
	{
		var signObj = document.getElementById(this.signObjectId);
		signObj.lockSealPosition("");
		
		var strObjectName ;
		strObjectName = signObj.FindSeal("",0);
		while(strObjectName != ""){
			signObj.SetMenuItem(strObjectName,4);
			strObjectName = signObj.FindSeal(strObjectName,0);
		}

	}
});

Ext.reg('parm-sign', Ext.parm.websign.SignField);


Ext.namespace('Ext.parm.weboffice');

Ext.parm.weboffice.OfficeField = Ext.extend(Ext.form.Field,  
{ 
    officeObjectId: null,
    
    defaultAutoCreate:{tag: "div"},
    
    onRender: function(ct, position)
    {
        Ext.parm.weboffice.OfficeField.superclass.onRender.call(this, ct, position); 
        this.createObject();
    },
    createObject:function()
    {
        this.officeObjectId = 'OfficeOBJ'+ Ext.id();
        if(Ext.isIE)
        {
            var s = '<object style="LEFT: 0px; TOP: 0px"  classid="clsid:E5A4A0BE-2ED4-49A6-BFC0-78B4E9FE45A8" '
            	+ ' id="' + this.officeObjectId + '"  height="0" width="0">'
            	+'<param name="_ExtentX" value="0"><param name="_ExtentY" value="0"></object>';           
            this.el.dom.innerHTML = s;        	
        }
        else
        {
        	 var s = '<object style="LEFT: 0px; TOP: 0px" TYPE="application/x-itst-activex" '
        	    + ' clsid="{E5A4A0BE-2ED4-49A6-BFC0-78B4E9FE45A8}" progid="WishTech.MainCtrl" '                	 
             	+ ' id="' + this.officeObjectId	+ '"  height="1" width="1">'
             	+'<param name="_ExtentX" value="1"><param name="_ExtentY" value="1"></object>';            
             this.el.dom.innerHTML = s;  
        }        
    },
    removeObject:function()
    {        
        for(var i=0;i<this.el.dom.childNodes.length;i++)
        {               
           this.el.dom.removeChild(this.el.dom.childNodes[i]);
        } 
    },   
    beforeDestroy:function()
    {                        
        this.removeObject();    
        Ext.parm.weboffice.OfficeField.superclass.beforeDestroy.call(this);
    },      
    getValue:function()
    {
    },
    getWebOffice:function()
    {
    	var webObj = document.getElementById(this.officeObjectId);
    	return webObj;
    },
    setValue:function(filedata)
    {             
    }
});

Ext.reg('parm-office', Ext.parm.weboffice.OfficeField);


Ext.parm.weboffice.IEFrameField = Ext.extend(Ext.form.Field,  
{ 
    frameObjectId: null,
    
    url: '',
    
    defaultAutoCreate:{tag: "div"},
    
    onRender: function(ct, position)
    {
    	Ext.parm.weboffice.IEFrameField.superclass.onRender.call(this, ct, position); 
        this.createObject();
    },
    createObject:function()
    {
        this.frameObjectId = 'IEFrameOBJ'+ Ext.id();
        if(!Ext.isIE)
        {            
        	var newurl = location.protocol + "//" + location.host + this.url;         	
        	 var s = '<object style="LEFT: 0px; TOP: 0px" type="application/viewinie" '    
        		+ ' param-location="'+newurl+'" src="" '
             	+ ' id="' + this.frameObjectId	+ '"  height="100%" width="100%">'
             	+'<param name="_ExtentX" value="1"><param name="_ExtentY" value="1"></object>';            
             this.el.dom.innerHTML = s;  
        }        
    },
    removeObject:function()
    {        
        for(var i=0;i<this.el.dom.childNodes.length;i++)
        {               
           this.el.dom.removeChild(this.el.dom.childNodes[i]);
        } 
    },   
    beforeDestroy:function()
    {                        
        this.removeObject();    
        Ext.parm.weboffice.IEFrameField.superclass.beforeDestroy.call(this);
    },      
    getValue:function()
    {
    },
    getObject:function()
    {
    	var obj = document.getElementById(this.frameObjectId);
    	return obj;
    },
    setValue:function(filedata)
    {             
    }
});

Ext.reg('parm-ieframe', Ext.parm.weboffice.IEFrameField);


Ext.parm.weboffice.DDAField = Ext.extend(Ext.form.Field,  
{ 
    ddaObjectId: null,
    
    defaultAutoCreate:{tag: "div"},
    
    fId: null,
    
    initComponent : function()
    {
    	Ext.parm.weboffice.DDAField.superclass.initComponent.call(this);
        this.addEvents('GridLClickUp','GridRClickUp','GridSelectRange','GridCellBtnLClickDown','GridDblClick','GridBeginPrint','GridFinishPrint');
    },
    onRender: function(ct, position)
    {
        Ext.parm.weboffice.DDAField.superclass.onRender.call(this, ct, position); 
        this.createObject();
    },
    DDAGridLClickUp:function(nFlags, x, y, nRow, nCol)
    {     	    	
    	this.fireEvent('GridLClickUp', this, nFlags,  x, y, nRow, nCol);
    },
    DDAGridRClickUp:function(nFlags, x, y, nRow, nCol)
    {     	    	
    	this.fireEvent('GridRClickUp', this, nFlags,  x, y, nRow, nCol);
    },
    DDAGridSelectRange:function(nMinRow,nMinCol,nMaxRow,nMaxCol)
    {     	    	
    	this.fireEvent('GridSelectRange', this, nMinRow,nMinCol,nMaxRow,nMaxCol);
    },
    DDAGridCellBtnLClickDown:function(nRow,nCol,nIndex,nState)
    {
    	this.fireEvent('GridCellBtnLClickDown',this,nRow,nCol,nIndex,nState);
    },
    DDAGridDblClick:function(nFlags, x, y, nRow, nCol)
    {
    	this.fireEvent('GridDblClick',this,nFlags, x, y, nRow, nCol);
    },
    GridBeginPrint:function()
    {
    	this.fireEvent('GridBeginPrint',this);
    },
    GridFinishPrint:function()
    {
    	this.fireEvent('GridFinishPrint',this);
    },
    createObjectEvent:function()
    {
    	this.fId = Ext.id(null,'Fun');
    	var cmpId = this.getId();
    	var functionstr = 'window.' + this.fId+ '_GridLClickUp=function(nFlags, x, y, nRow, nCol){'
    	     + ' var field = Ext.getCmp("' + cmpId + '");\n'
    	     + ' field.DDAGridLClickUp(nFlags, x, y, nRow, nCol);}\n';    
    	
    	functionstr = functionstr + 'window.' + this.fId+ '_GridRClickUp=function(nFlags, x, y, nRow, nCol){'
	     	+ ' var field = Ext.getCmp("' + cmpId + '");\n'
	     	+ ' field.DDAGridRClickUp(nFlags, x, y, nRow, nCol);}\n'; 
    	
    	functionstr = functionstr + 'window.' + this.fId+ '_GridSelectRange=function(nMinRow,nMinCol,nMaxRow,nMaxCol){'
	     	+ ' var field = Ext.getCmp("' + cmpId + '");\n'
	     	+ ' field.DDAGridSelectRange(nMinRow,nMinCol,nMaxRow,nMaxCol);}\n'; 
    	
    	functionstr = functionstr + 'window.' + this.fId+ '_GridCellBtnLClickDown=function(nRow,nCol,nIndex,nState){'
     	+ ' var field = Ext.getCmp("' + cmpId + '");\n'
     	+ ' field.DDAGridCellBtnLClickDown(nRow,nCol,nIndex,nState);}\n'; 
    	
    	functionstr = functionstr + 'window.' + this.fId+ '_GridDblClick=function(nFlags, x, y, nRow, nCol){'
     	+ ' var field = Ext.getCmp("' + cmpId + '");\n'
     	+ ' field.DDAGridDblClick(nFlags, x, y, nRow, nCol);}\n'; 
     	
     	functionstr = functionstr + 'window.' + this.fId+ '_GridBeginPrint=function(){'
     	+ ' var field = Ext.getCmp("' + cmpId + '");\n'
     	+ ' field.GridBeginPrint();}\n'; 
     	
     	functionstr = functionstr + 'window.' + this.fId+ '_GridFinishPrint=function(){'
     	+ ' var field = Ext.getCmp("' + cmpId + '");\n'
     	+ ' field.GridFinishPrint();}\n'; 
    	
    	eval(functionstr);     	
    	
    	var eventstr = ' event_GridLClickUp="' + this.fId + '_GridLClickUp" ';
    	eventstr = eventstr + ' event_GridRClickUp="' + this.fId + '_GridRClickUp" ';
    	eventstr = eventstr + ' event_GridSelectRange="' + this.fId + '_GridSelectRange" ';
    	eventstr = eventstr + ' event_GridCellBtnLClickDown="' + this.fId + '_GridCellBtnLClickDown" ';
    	eventstr = eventstr + ' event_GridDblClick="' + this.fId + '_GridDblClick" ';
    	eventstr = eventstr + ' event_GridBeginPrint="' + this.fId + '_GridBeginPrint" ';
    	eventstr = eventstr + ' event_GridFinishPrint="' + this.fId + '_GridFinishPrint" ';
    	
    	return eventstr;
    },
    createObject:function()
    {    	    	    	    		    	
    	this.ddaObjectId = 'DDAOBJ'+ Ext.id();
    	if(Ext.isIE)
        {
	        var s = '<object style="LEFT: 0px; TOP: 0px"  classid="clsid:26300273-AC93-4F21-A145-14DB95C10804" '
	        	+ ' codebase="DiDaSG.cab#version=3,8,3,6"  id="' + this.ddaObjectId 
	        	+ '"  height="100%" width="100%"><param name="_ExtentX" value="6350">'
	        	+'<param name="_ExtentY" value="6350"></object>';           
	        this.el.dom.innerHTML = s;
        }else{
        	 var eventstr = this.createObjectEvent();
	       	 var s = '<object style="LEFT: 0px; TOP: 0px" TYPE="application/x-itst-activex" '
	     	    + ' clsid="{26300273-AC93-4F21-A145-14DB95C10804}" progid="DIDASG.DiDaSGCtrl.1" '                	 
	          	+ ' param__ExtentX="6350" param__ExtentY="6350" id="' + this.ddaObjectId + '"  '+ eventstr
	          	+' height="100%" width="100%"></object>';     	       	 
	         this.el.dom.innerHTML = s;  	         
        }        
    },
    removeObject:function()
    {        
        for(var i=0;i<this.el.dom.childNodes.length;i++)
        {               
           this.el.dom.removeChild(this.el.dom.childNodes[i]);
        } 
    },   
    beforeDestroy:function()
    {            
    	if(Ext.isChrome)
    	{
        	var str =   ' window.' + this.fId + '_GridLClickUp=null;\n ';
        	str = str + ' window.' + this.fId + '_GridRClickUp=null;\n ';
        	str = str + ' window.' + this.fId + '_GridSelectRange=null;\n ';
        	str = str + ' window.' + this.fId + '_GridCellBtnLClickDown=null;\n ';
        	str = str + ' window.' + this.fId + '_GridDblClick=null;\n ';
        	eval(str);    		
    	}    	
        this.removeObject();    
        Ext.parm.weboffice.DDAField.superclass.beforeDestroy.call(this);
    },      
    getValue:function()
    {
    },
    getDDA:function()
    {
    	var obj = document.getElementById(this.ddaObjectId);
    	return obj;
    },
    setValue:function(filedata)
    {             
    }
});

Ext.reg('parm-dda', Ext.parm.weboffice.DDAField);

Ext.namespace('Ext.parm.weboffice');

Ext.parm.weboffice.ShowExcelFormWin = Ext.extend(Ext.Window,    
{         
    constructor: function()
    {
    	Ext.parm.weboffice.ShowExcelFormWin.superclass.constructor.call(this,
        {            
            title:'编辑文档',               
            layout:'fit',
            width:20,                                                                    //窗口宽度
            height:20,
            buttonAlign :'center',
            closeAction:'hide', 
            plain: true,    
            modal: true,                                                                  //模式窗口，默认为false
            resizable: false,
            
            items:
            [            	
                this.weboffice = new Ext.parm.weboffice.OfficeField
				({
					name: 'office',
					height: 1,
					width: 1
				})             
            ]
        }); 		    	  
    	this.weboffice.on("afterrender",this.evt_afterrender,this);	      	   
    },        
    evt_afterrender:function()
    {    	
    	try
    	{
    		this.weboffice.getWebOffice().createExcelForm();
    	}
    	catch(e)
    	{    		
    		Ext.Msg.show
            ({
                title:Ext.parf.common_msgTitle,
                msg: "您的电脑需要安装威实协同管理软件Web组件，否则系统中某些功能无法正常运行，请您下载并按照安装提示进行安装！ ",
                buttons: Ext.Msg.OK,                    
                icon: Ext.MessageBox.INFO,
                fn:function()
                {
                	window.exportStatus='export';             
                    location.href= '/download/extensions/setup.exe';   
                }
            }); 
			this.close();
			return;  
    	}    	
    },      
    openExcel:function(url)
    {    	
    	var newurl = location.protocol + "//" + location.host + url;    	    	    	    	
    	newurl = newurl + "&rnd="+ Math.random() + "&SSID=" + Ext.part.UserInfo.ssid;    	    	
    	var officeObj = this.weboffice.getWebOffice();      	     	    	
    	officeObj.openExcelForm(newurl);
    },   
    openExcelByDDABase64:function(base64)
    {
    	var officeObj = this.weboffice.getWebOffice();      	     	    	
    	officeObj.openExcelFormByDDABase64(base64);
    },
    showExcel:function()
    {
    	var officeObj = this.weboffice.getWebOffice();
    	officeObj.showExcelForm();
    },
    saveExcel:function(url, param)
    {
    	var officeObj = this.weboffice.getWebOffice();
    	var newurl = location.protocol + "//" + location.host + url;
    	newurl = newurl + "?rnd="+ Math.random() + "&SSID=" + Ext.part.UserInfo.ssid;    	  	
    	var returnValue = officeObj.saveExcelFile(newurl,param,"files"); 
    	return returnValue;
    },
    window_close:function()
    {
    	this.hide();
    },    
    beforeDestroy:function()
    {       
    	var officeObj = this.weboffice.getWebOffice();     	
    	officeObj.closeExcelForm();
    	Ext.parm.weboffice.ShowExcelFormWin.superclass.beforeDestroy.call(this);
    }
});

Ext.parm.weboffice.ShowWordFormWin = Ext.extend(Ext.Window,    
{         
    constructor: function()
    {
    	Ext.parm.weboffice.ShowWordFormWin.superclass.constructor.call(this,
        {            
            title:'编辑文档',               
            layout:'fit',
            width:20,                                                                    //窗口宽度
            height:20,
            buttonAlign :'center',
            closeAction:'hide', 
            plain: true,    
            modal: true,                                                                  //模式窗口，默认为false
            resizable: false,
            
            items:
            [            	
                this.weboffice = new Ext.parm.weboffice.OfficeField
				({
					name: 'office',
					height: 1,
					width: 1
				})             
            ]
        }); 		    	  
    	this.weboffice.on("afterrender",this.evt_afterrender,this);	      	   
    },        
    evt_afterrender:function()
    {    	
    	try
    	{
    		this.weboffice.getWebOffice().createWebOfficeForm();
    	}
    	catch(e)
    	{
    		Ext.Msg.show
            ({
                title:Ext.parf.common_msgTitle,
                msg: "您的电脑需要安装威实协同管理软件Web组件，否则系统中某些功能无法正常运行，请您下载并按照安装提示进行安装！ ",
                buttons: Ext.Msg.OK,                    
                icon: Ext.MessageBox.INFO,
                fn:function()
                {
                	window.exportStatus='export';             
                    location.href= '/download/extensions/setup.exe';   
                }
            }); 
			this.close();
			return;  
    	}    		
    }, 
    createWord:function()
    {    	   	
    	var officeObj = this.weboffice.getWebOffice();      	     	    	
    	officeObj.WebOfficeLoadFile("","doc");
    },    
    openWord:function(url)
    {    	
    	var newurl = location.protocol + "//" + location.host + url;    	    	    	    	
    	newurl = newurl + "&rnd="+ Math.random() + "&SSID=" + Ext.part.UserInfo.ssid;    	    	
    	var officeObj = this.weboffice.getWebOffice();      	     	    	
    	officeObj.WebOfficeLoadFile(newurl,"doc");
    }, 
    closeWord:function()
    {
    	var officeObj = this.weboffice.getWebOffice();     	
    	officeObj.WebOfficeCloseFile(0);
    },
    revisionWord:function()
    {
    	var officeObj = this.weboffice.getWebOffice();     	
    	officeObj.revisionWebOffice(Ext.part.UserInfo.userName);    	
    },
    showWord:function()
    {
    	var officeObj = this.weboffice.getWebOffice();
    	officeObj.showWebOfficeForm();
    },
    saveWord:function(url, param)
    {
    	var officeObj = this.weboffice.getWebOffice();
    	var newurl = location.protocol + "//" + location.host + url;
    	newurl = newurl + "?rnd="+ Math.random() + "&SSID=" + Ext.part.UserInfo.ssid;
    	var returnValue = officeObj.saveWebOfficeFile(newurl,param,"files"); 
    	return returnValue;
    },
    loadFormValue:function(str)
    {
    	var officeObj = this.weboffice.getWebOffice();
    	var arr = str.split('&');
    	for(var i=0;i<arr.length;i++)
    	{
    		var val = arr[i].split('=');
    		officeObj.loadFormValueToWord("${" + val[0] + "}", Ext.util.Format.htmlDecode(val[1]));
    	}
    },
    window_close:function()
    {
    	this.hide();
    },    
    beforeDestroy:function()
    {       
    	var officeObj = this.weboffice.getWebOffice();     	
    	officeObj.WebOfficeCloseFile(0);
    	Ext.parm.weboffice.ShowWordFormWin.superclass.beforeDestroy.call(this);
    }
});

Ext.parm.weboffice.CheckWebComponentWin = Ext.extend(Ext.Window,    
{         
    constructor: function()
    {
    	Ext.parm.weboffice.CheckWebComponentWin.superclass.constructor.call(this,
        {            
            title:'安装Web组件',               
            layout:'fit',
            width:1,                                                                    //窗口宽度
            height:1,
            buttonAlign :'center',
            closeAction:'close', 
            plain: true,      
            modal: true,
            resizable: false,
            
            items:
            [            	
                this.weboffice = new Ext.parm.weboffice.OfficeField
				({
					name: 'office',
					height: 1,
					width: 1
				})             
            ]
        }); 		    	  
    },    
    getCtrlVersion:function()
    {    	    	
    	try
    	{
    		return this.weboffice.getWebOffice().getCtrlVersion();
    	}
    	catch(e)
    	{
    		Ext.Msg.show
            ({
                title:Ext.parf.common_msgTitle,
                msg: "您的电脑需要安装威实协同管理软件Web组件，否则系统中某些功能无法正常运行，请您下载并按照安装提示进行安装！ ",
                buttons: Ext.Msg.OK,                    
                icon: Ext.MessageBox.INFO,
                fn:function()
                {
                	window.exportStatus='export';
                    location.href= '/download/extensions/setup.exe';
                }
            }); 
    	}
    	return "";
    },             
    beforeDestroy:function(){
    	Ext.parm.weboffice.CheckWebComponentWin.superclass.beforeDestroy.call(this);
    }
});
