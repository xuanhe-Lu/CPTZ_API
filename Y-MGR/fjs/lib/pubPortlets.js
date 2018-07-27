Ext.namespace('Ext.qwareinfo.oa.portlet');
Ext.qwareinfo.oa.portlet.BasePortlet = Ext.extend(Ext.parf.portal.Portlet,{ 
	dataloaded : false,
	bodyDivId : '',
	contentPanel : null,
    constructor: function(conf)
    { 
    	Ext.apply(this,conf);
    	
    	this.portletClass =  this.portletClass || 'oa_portlet_b_1';
    	this.isMore = this.isMore || false;
    	this.portletTitle = this.portletTitle || '标题';
    	
    	this.contentPanel = new Ext.Panel(
    	{
    		html : '加载中...',
    		border : false
    	});
    	this.id = Ext.id();
    	this.bodyDivId = "content" + this.id;
    	
        Ext.qwareinfo.oa.portlet.BasePortlet.superclass.constructor.call(this,
        {               
        	frame : false,
        	header : false,
        	border:false,
			html : this.getTemplate()
        });
        
        this.contentPanel.on('afterrender',this.beforeLoadData,this);
        
        this.on('afterrender',function(){
        	 this.contentPanel.render(this.bodyDivId);
        },this);
    },
    colsePortlet : function()
    {
    	//this.ownerCt.ownerCt.closePortlet(this);	
    	Ext.parm.portal.desktop.closePortlet(this);
    },
    toggleCollapse : function()
    {    
    	this.contentPanel.toggleCollapse(true);
    },
    beforeLoadData : function()
    {
    	if(this.dataloaded) return;
    	this.dataloading = true;
    	
    	var defertime = Math.random()*(1500 - 300) + 300;
    	(function(){
    		this.loadData();
    	}).defer(defertime,this);
    },
    refreshData : function()
    {
    	if(this.dataloading) return;
    	this.dataloaded = false;
    	this.beforeLoadData();
    },
    loadData : function()
    {
    	Ext.fjs.ajax
		({
			url : this.dataURL||this.getDataURL(),
			disableCaching : true,
			success : this.loadData_success,
			scope: this
		});
    },
    loadData_success : function(response,options)
	{
		var res = Ext.decode(response.responseText);
		
		if(res.length>0)
		{
			for(var i=0;i<res.length;i++)
			{
				this.prepareData(res, i, res[i]);
			}
		}
		var data = {data : res};
		var html = new EJS({url : this.ejsURL}).render(data);
		Ext.getDom(this.contentPanel.body).innerHTML = html;
       	this.contentPanel.doLayout();
		this.doLayout();
		this.dataloading = false;
	},
	prepareData : function(dataArray, index, data)
	{
		
	},
    moreData : function()
    {
    	alert('moreData');
    },
    getTemplate : function()
    {
    	var bottom = '<div class="oa_portlet_br"><div class="oa_portlet_bl"><div class="oa_portlet_bc"></div></div></div>';
    	var body = '<div class="oa_portlet_ml"><div class="oa_portlet_mr"><div class="oa_portlet_mc" id="'+ this.bodyDivId +'"></div></div></div>';
    	
    	var header = '<div class="oa_portlet_tr"></div>';
    	header += '<div class="oa_portlet_tc">';
    	if(this.portletIcon)
    	{
    		header += '<div class="titleIco '+ this.portletIcon + '"></div>';
    		this.portletTitle = "&nbsp;" + this.portletTitle;
    	}
    	header += this.portletTitle;
    	header += '</div>';
    	header += '<div class="oa_portlet_tl"></div>';
    	header += '<div class="oa_portlet_tool">';
    	header += '<div class="oa_portlet_btn btn1" onclick="Ext.parf.reflect.ExtCmp(\''+ this.id +'\',\'toggleCollapse\');"></div>';
    	if(this.isMore)
    	{
    		header += '<div class="oa_portlet_btn btn2" onclick="Ext.parf.reflect.ExtCmp(\''+ this.id +'\',\'moreData\');"></div>';
    	}	
    	header += '<div class="oa_portlet_btn btn3" onclick="Ext.parf.reflect.ExtCmp(\''+ this.id +'\',\'colsePortlet\');"></div>';
    	header += '</div>';
    	
    	
    	return '<div class="oa_portlet"><div class="'+ this.portletClass +'">' + header + "</div>" + body + bottom + "</div>";
    	
    },
    getDataURL : function()
    {
    	return "";
    },
    beforeDestroy : function()
    {
    	Ext.destroy(this.contentPanel);
    	Ext.qwareinfo.oa.portlet.BasePortlet.superclass.beforeDestroy.call(this);
    }
});

Ext.namespace('Ext.qwareinfo.oa.portlet.book');

Ext.qwareinfo.oa.portlet.book.BookPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.book.BookPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/book.json?actionName=getPortalList',
        	ejsURL : '/qwareinfo/oa/portlet/book/template/bookportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.BOOKID+"');";
    	data.bookType = "<img height='16' src='/images/officework/book_type_"+data.BOOKTYPE+".gif' ext:qtip="+data.BOOKTYPENAME+">";
    	
    	data.qtip = '['+data.BOOKTYPENAME+']'+data.BOOKNAME;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.officework.book.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.officework.book.browse',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.book');

Ext.qwareinfo.oa.portlet.book.MyBespokenBookPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.book.MyBespokenBookPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/book.json?actionName=getReservePortalList',
        	ejsURL : '/qwareinfo/oa/portlet/book/template/bookreserveportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.BOOKID+"');";
    	data.bookType = "<img height='16' src='/images/officework/book_type_"+data.BOOKTYPE+".gif' ext:qtip="+data.BOOKTYPENAME+">";
    	data.reverseTime = Ext.parf.renderDate('Y-m-d H:i')(data.RESERVETIME);
    	data.qtip = '['+data.BOOKTYPENAME+']'+data.BOOKNAME+'&nbsp;&nbsp;'+data.reverseTime;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.invokeItem('qwareinfo.oa.officework.book.mybook','1','0','0');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.officework.book.mybook','1','2',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet');

Ext.qwareinfo.oa.portlet.book.MyBorrowBookPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.book.MyBorrowBookPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/book.json?actionName=getBorrowPortalList',
        	ejsURL : '/qwareinfo/oa/portlet/book/template/bookborrowportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.BORROWID+"');";
    	data.bookType = "<img height='16' src='/images/officework/book_type_"+data.BOOKTYPE+".gif' ext:qtip="+data.BOOKTYPENAME+">";
    	data.qtip = '['+data.BOOKTYPENAME+']'+data.BOOKNAME;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.invokeItem('qwareinfo.oa.officework.book.mybook','0','0','0');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.officework.book.mybook','0','1',Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.car');

Ext.qwareinfo.oa.portlet.car.CarUsePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.car.CarUsePortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/caruse.json?actionName=getCarUsePortalByApplyUser',
        	ejsURL : '/qwareinfo/oa/portlet/car/template/caruseportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.CARINFOID+"');";
    	data.qtip = data.REASON+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.USECARSTARTTIME)+
    				' 至 '+Ext.parf.renderDate('Y-m-d H:i')(data.USECARENDTIME);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.dailyoffice.car.usestate');
    },
	openDetail : function(Id)
	{
			Ext.parf.invokeItem('qwareinfo.oa.dailyoffice.car.usestate', Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet');

Ext.qwareinfo.oa.portlet.ChartPortlet = Ext.extend(Ext.parf.portal.Portlet,
{ 
	dataloaded : false,
	bodyDivId : '',
	chart : null,
    constructor: function(conf)
    { 
    	Ext.apply(this,conf);
    	
    	this.portletClass =  this.portletClass || 'oa_portlet_b_1';
    	this.isMore = this.isMore || false;
    	this.portletTitle = this.portletTitle || '标题';
    	if(this.chart == null)
    	{
    		alert('chart is null');
    	}
    	this.chart.height = 200;
    	this.contentPanel = new Ext.Panel(
    	{
    		layout:'fit',
    		border : false,
    		items:[this.chart]
    	});
    	this.id = Ext.id();
    	this.bodyDivId = "content" + this.id;
    	
        Ext.qwareinfo.oa.portlet.ChartPortlet.superclass.constructor.call(this,
        {               
        	frame : false,
        	header : false,
        	border:false,
			html : this.getTemplate()
        });
        
        this.contentPanel.on('afterrender',this.beforeLoadData,this);
        
        this.on('afterrender',function(){
        	 this.contentPanel.render(this.bodyDivId);
        },this);
        
        this.on('resize',function(){      
        	this.contentPanel.doLayout();
        },this);
    },
    colsePortlet : function()
    {
    	Ext.parm.portal.desktop.closePortlet(this);
    },
    toggleCollapse : function()
    {    
    	this.contentPanel.toggleCollapse(true);
    },
    beforeLoadData : function()
    {
    	if(this.dataloaded) return;
    	this.loadData();
    },
    refreshData : function()
    {
    	this.dataloaded = false;
    	this.beforeLoadData();
    },
    loadData : function()
    {
    	this.chart.store.load();
    },
    moreData : function()
    {
    	alert('moreData');
    },
    getTemplate : function()
    {
    	var bottom = '<div class="oa_portlet_br"><div class="oa_portlet_bl"><div class="oa_portlet_bc"></div></div></div>';
    	var body = '<div class="bwarp oa_portlet_ml"><div class="oa_portlet_mr"><div class="oa_portlet_mc" id="'+ this.bodyDivId +'"></div></div></div>';
    	
    	var header = '<div class="oa_portlet_tr"></div>';
    	header += '<div class="oa_portlet_tc">'
    	if(this.portletIcon)
    	{
    		header += '<div class="titleIco '+ this.portletIcon + '"></div>';
    		this.portletTitle = "&nbsp;" + this.portletTitle;
    	}
    	header += this.portletTitle;
    	header += '</div>';
    	header += '<div class="oa_portlet_tl"></div>';
    	header += '<div class="oa_portlet_tool">';
    	header += '<div class="oa_portlet_btn btn1" onclick="Ext.parf.reflect.ExtCmp(\''+ this.id +'\',\'toggleCollapse\');"></div>';
    	if(this.isMore)
    	{
    		header += '<div class="oa_portlet_btn btn2" onclick="Ext.parf.reflect.ExtCmp(\''+ this.id +'\',\'moreData\');"></div>';
    	}	
    	header += '<div class="oa_portlet_btn btn3" onclick="Ext.parf.reflect.ExtCmp(\''+ this.id +'\',\'colsePortlet\');"></div>';
    	header += '</div>';
    	
    	
    	return '<div class="oa_portlet"><div class="'+ this.portletClass +'">' + header + "</div>" + body + bottom + "</div>";
    	
    },
    beforeDestroy : function()
    {
    	Ext.destroy(this.contentPanel);
    	Ext.qwareinfo.oa.portlet.ChartPortlet.superclass.beforeDestroy.call(this);
    }
});

Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.DeptDocPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.dept.DeptDocPortlet.superclass.constructor.call(this,
        {               
       		portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/dept/doc.json?actionName=getDeptDocPortletList&DEPTID='+conf.deptId,
        	ejsURL : '/qwareinfo/oa/portlet/dept/templete/deptdocportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
		data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.DOCID+"','"+data.FILEPERM+"');";
    	
    	data.img = '';
		if(!data.FILESUFFIX)
		{
			data.img = '<img src="/extjs/filetype/folder-16.gif" valign="_bottom"/>';
		}
		else
		{
			data.img = '<img src="/extjs/filetype/'+data.FILESUFFIX+'-16.gif"/>';
		}
		
    	data.qtip = data.FILENAME+'&nbsp;&nbsp;'+data.CREATEUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.CREATETIME);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.document.explorer');
    },
	openDetail : function(Id , fileperm)
	{
		if(Ext.qwareinfo.oa.pubinfo.document.plugin.checkPermission(fileperm,'Y[YN][YN][YN][YN][YN]Y'))
		{
			var url = '/qwareinfo/oa/pubinfo/document/main.json?actionName=downloadFile&DOCID='+Id;
			Ext.parf.download(url);
		}
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.DeptExpensePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{
	constructor: function(conf)
	{
		this.deptId = conf.deptId;
		
		Ext.qwareinfo.oa.portlet.dept.DeptExpensePortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			isMore : true,
			dataURL : '/qwareinfo/oa/portlet/expense.json?actionName=getDeptExpensePortletList&DEPTID='+conf.deptId,
			ejsURL : '/qwareinfo/oa/portlet/dept/templete/expenseportlet.ejs'
		});
	},
	moreData : function()
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.dept.expense.query', 'loadByDeptId', this.deptId);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.InformationPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.dept.InformationPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/dept/info.json?actionName=getDeptInfoPortletList&DEPTID='+conf.deptId,
			ejsURL : '/qwareinfo/oa/portlet/dept/templete/informationportlet.ejs'
			
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.INFOID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.CREATETIME);
    	data.qtip =  data.INFOTITLE+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.information.infobrowse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.information.infobrowse',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.DeptIntroPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.dept.DeptIntroPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	dataURL : '/qwareinfo/oa/portlet/dept.json?actionName=getDept&DEPTID='+conf.deptId,
			ejsURL : '/qwareinfo/oa/portlet/dept/templete/deptintroportlet.ejs'
			
        });
    }
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.DeptNoticePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
    constructor: function(conf)
    { 
	
        Ext.qwareinfo.oa.portlet.dept.DeptNoticePortlet.superclass.constructor.call(this,
        {                           
	        portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/dept/notice.json?actionName=getDeptPortalNoticeList&PUBLISHORG='+conf.deptId,
        	ejsURL : '/qwareinfo/oa/portlet/dept/templete/deptnoticeportlet.ejs'
        });
    }
	,
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.CHECKID+"');";
    	data.lvl = '<img src=/images/publicinfo/notice_lvl_'+data.LVL+'.gif width=16 height=16 ext:qtip =\''+ data.LVLNAME + '\'>';					  	
    	data.isread = '已读';
    	if(data.ISREAD==0)
    	{
    		data.isread = '未读';
    	}
    	data.qtip = data.TITLE+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d')(data.PUBLISHTIME);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.notice.check');
  		
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.notice.check',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.DeptTaskPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
	
        Ext.qwareinfo.oa.portlet.dept.DeptTaskPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/dept/worktask.json?actionName=getDeptTaskPortletList&DEPTID='+conf.deptId,
        	ejsURL : '/qwareinfo/oa/portlet/dept/templete/depttaskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
    	data.qtip = data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)
   		+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.dept.task');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.dept.task',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.DeptTopicPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.dept.DeptTopicPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/dept/topic.json?actionName=getDeptTopicPortletList&DEPTID='+conf.deptId,
        	ejsURL : '/qwareinfo/oa/portlet/dept/templete/depttopicportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TOPICID+"');";
    	data.qtip = data.TOPICTITLE+'&nbsp;&nbsp;'+data.POSTUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.POSTTIME);
   		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.communication.forum.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.communication.forum.browse',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.DeptWorkloadPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{
	constructor: function(conf)
	{
		this.deptId = conf.deptId;
		
		Ext.qwareinfo.oa.portlet.dept.DeptWorkloadPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			isMore : true,
			dataURL : '/qwareinfo/oa/portlet/workload.json?actionName=getDeptWorkloadPortletList&DEPTID='+conf.deptId,
			ejsURL : '/qwareinfo/oa/portlet/dept/templete/workloadportlet.ejs'
		});
	},
	prepareData : function(dataArray, index, data)
	{
		data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.WORKLOADID+"');";
		
		data.attchIco = "";
		if (!Ext.isEmpty(data.ATTACHMENT))
		{
			data.attchIco = "<img src=/images/ico-attch.gif width=16 height=16>";
		}
	},
	moreData : function()
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.dept.workload.query', 'loadByDeptId', this.deptId);
	},
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.dept.workload.query', Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.MyPlanPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		
        Ext.qwareinfo.oa.portlet.dept.MyPlanPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/dept/plan.json?actionName=getDeptPlanPortletList&DEPTID='+conf.deptId,
        	ejsURL : '/qwareinfo/oa/portlet/dept/templete/deptplanportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.PLANID+"');";
   		data.qtip = data.PLANTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)
   		+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.dept.plan');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.dept.plan',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.PlanCompletedPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		
        Ext.qwareinfo.oa.portlet.dept.PlanCompletedPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/dept/plan.json?actionName=getDeptPlanCompletedPortletList&DEPTID='+conf.deptId,
        	ejsURL : '/qwareinfo/oa/portlet/dept/templete/deptplanportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.PLANID+"');";
   		data.qtip = data.PLANTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)
   		+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.dept.plan');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.dept.plan',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.TaskColumnChartPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.ChartPortlet,
{
	constructor: function(conf)
	{
		Ext.qwareinfo.oa.portlet.dept.TaskColumnChartPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			chart : new Ext.qwareinfo.oa.portlet.dept.DeptTaskChartLastWeek(conf.deptId)
		});
	}
});


Ext.qwareinfo.oa.portlet.dept.DeptTaskChartLastWeek = Ext.extend(Ext.parf.HighChart,
{
	constructor : function(deptId)
	{
		var store = new Ext.data.JsonStore
		({
//			url : '/temp/data.jsp?f=portlet/depttaskchartlaskweek.txt',
			url : '/qwareinfo/oa/portlet/dept/worktask.json?actionName=getDeptPortletTaskColumnChartList&DEPTID='+deptId,
//			root : 'data',
			fields : ['STATSDATE','COMPLETED','UNDERTAKE']
		});
		Ext.qwareinfo.oa.portlet.dept.DeptTaskChartLastWeek.superclass.constructor.call(this,
		{
			layout :'fit',
			store : store,
			loadMask : true,
			xField : 'STATSDATE',
			series : 
			[
				{
					name: '承办任务',
					dataIndex: 'UNDERTAKE',
					yAxis: 0
				},{
					type : 'spline',
					name : '完成任务',
					dataIndex : 'COMPLETED'/*,
					yAxis: 1*/
				}
			],
			chartConfig :
			{
				chart : 
				{
					defaultSeriesType: 'column'			
				},
				title: 				
				{
					text: ''
				},
				xAxis:
				[{
					
					labels :
					{
						rotation : 0, y : 10
					}
				}],
				yAxis: 
				[
					{
						min : 0,
						labels: 
						{
							formatter: function() 
							{
								return this.value;
							}
						},
						title: 
						{
							text: ''
						}
					}/*,{
						min : 0,
						max : 100,
						labels: 
						{
							formatter: function() 
							{
								return this.value;
							},
							style: 
							{
								color: '#89A54E'
							}
						},
						title: 
						{
							text: '百分比',
							style: 
							{
								color: '#89A54E'
							}
						},
						opposite: true
	      			}*/
	      		],
				tooltip:								
			    {
			    	formatter: function() {
	      				var y = this.series.type=='spline' ?Highcharts.numberFormat(this.y, 2)+'%' : this.y;
	      				
		    			return '<b>'+ this.x +'</b><br/>'+
			                this.series.name +': '+ this.y +'<br/>';
			   		}
		      	},
		      	plotOptions: 
		      	{
		      	   	series :
		      		{
		      			dataLabels: 				
						{
							x : 23,
							enabled: true
						}
					},
					column: 
					{
						pointPadding: 0.2,
						borderWidth: 0
					},
					line:
					{
						dataLabels: 				//设置图表显示内容
						{
//							formatter: function()
//							{
//								return  Highcharts.numberFormat(this.y, 2);	
//							}
						}
					}
				},
				exporting:
				{
					enabled :false,
					filename: '导出图表',
					url:'/parf/svg.json'
				}
			}
		});		
	}
})
/**
 * 本周已完成重要任务
 */
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.TaskCompletedPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		
        Ext.qwareinfo.oa.portlet.dept.TaskCompletedPortlet.superclass.constructor.call(this,
        {      
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
//        	dataURL : '/temp/data.jsp?f=portlet/deptcompletedtask.txt',
        	dataURL : '/qwareinfo/oa/portlet/dept/worktask.json?actionName=getDeptPortletCompletedTaskList&DEPTID='+conf.deptId,
        	ejsURL : '/qwareinfo/oa/portlet/dept/templete/deptcompletedtaskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
        data.qtip = data.TASKTITLE+'&nbsp;&nbsp;'+data.EXECUTUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)
   		+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.dept.task');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.dept.task',Id);
	}
});
/**
 * 超期未完成任务
 */
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.TaskOverdueIncompletePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		
        Ext.qwareinfo.oa.portlet.dept.TaskOverdueIncompletePortlet.superclass.constructor.call(this,
        {      
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
//        	dataURL : '/temp/data.jsp?f=portlet/deptoverdueincompletetask.txt',
        	dataURL : '/qwareinfo/oa/portlet/dept/worktask.json?actionName=getDeptPortletOverdueIncompleteTaskList&DEPTID='+conf.deptId,
        	ejsURL : '/qwareinfo/oa/portlet/dept/templete/depttaskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
   		data.qtip = data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+data.EXECUTUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.dept.task');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.dept.task',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.TaskPieChartPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.ChartPortlet,
{
	constructor: function(conf)
	{
		Ext.qwareinfo.oa.portlet.dept.TaskPieChartPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			chart : new Ext.qwareinfo.oa.portlet.dept.DeptTaskPie(conf.deptId)
		});
	}
});


Ext.qwareinfo.oa.portlet.dept.DeptTaskPie = Ext.extend(Ext.parf.HighChart,
{
	constructor : function(deptId)
	{
		var store = new Ext.data.JsonStore
		({
//			url : '/temp/data.jsp?f=example/dept_task_stats.txt',
			url : '/qwareinfo/oa/portlet/dept/worktask.json?actionName=getDeptPortletTaskPieChartList&DEPTID='+deptId,
//			root : 'data',
			fields : ['STATSINDEXNAME','STATSINDEXNUM']
		});
		Ext.qwareinfo.oa.portlet.dept.DeptTaskPie.superclass.constructor.call(this,
		{
			layout :'fit',
			store : store,
			series:
			[{
				type: 'pie',
				categorieField : 'STATSINDEXNAME',
				name: '任务',
				dataField : 'STATSINDEXNUM'
			}],
			chartConfig : 
			{
				chart :
				{
					defaultSeriesType : 'pie',
					plotBackgroundColor : null,
					plotBorderWidth : null,
					plotShadow : false
				},
				title :
				{
					text : ''
				},
				tooltip :
				{
					formatter: function() 
					{
						return '<b>'+ this.point.name +'</b>: '+ this.y + '个';
					}
				},
				plotOptions:
				{
					pie : 
					{
						center: ['48%', '50%'],
						size: '70%',
						allowPointSelect: true,
						cursor: 'pointer',
						dataLabels: 
						{
							enabled: true,
							distance: 7,
							formatter: function() 
							{
								return '<b>'+ this.point.name +'</b>: ' + 
								Highcharts.numberFormat(this.percentage, 2) +' %';
							}
						},
						showInLegend: true
					}
				},
				legend:
				{
					align: 'right',
					verticalAlign: 'bottom',
					layout: 'vertical'
				},
				exporting:
				{
					enabled :false
				}
			}
		});		
	}
})
/**
 * 本周待办重要任务
 */
Ext.namespace('Ext.qwareinfo.oa.portlet.dept');

Ext.qwareinfo.oa.portlet.dept.TaskToDoPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		
        Ext.qwareinfo.oa.portlet.dept.TaskToDoPortlet.superclass.constructor.call(this,
        {      
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
//        	dataURL : '/temp/data.jsp?f=portlet/deptToDotask.txt',
        	dataURL : '/qwareinfo/oa/portlet/dept/worktask.json?actionName=getDeptPortletToDoTaskList&DEPTID='+conf.deptId,
        	ejsURL : '/qwareinfo/oa/portlet/dept/templete/depttaskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
   		data.qtip = data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+data.EXECUTUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.dept.task');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.dept.task',Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.document');

Ext.qwareinfo.oa.portlet.document.DocumentPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
	{
		Ext.qwareinfo.oa.portlet.document.DocumentPortlet.superclass.constructor.call(this,
		{			   
	  		portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			isMore : true,
			dataURL : '/qwareinfo/oa/portlet/document.json?actionName=getPortalDocumentList',
			ejsURL : '/qwareinfo/oa/portlet/document/template/documentportlet.ejs'
		});
	},
	prepareData : function(dataArray, index, data)
	{
		data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.DOCID+"','"+data.FILEPERM+"');";
		
		data.img = '';
		if(!data.FILESUFFIX)
		{
			data.img = '<img src="/extjs/filetype/folder-16.gif" valign="_bottom"/>';
		}
		else
		{
			data.img = '<img src="/extjs/filetype/'+data.FILESUFFIX+'-16.gif"/>';
		}
		
		data.createUser = '';
		if (data.CREATEUSER != null)
		{
			data.createUser = data.CREATEUSER; 
		}
		data.qtip = data.FILENAME+'&nbsp;&nbsp;'+Ext.util.Format.qwarefilesize(data.FILELEN)+'&nbsp;&nbsp;'+data.createUser;
		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
	},
	moreData : function()
	{
		Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.document.mydoc');
	},
	openDetail : function(Id , fileperm)
	{
		if(Ext.qwareinfo.oa.pubinfo.document.plugin.checkPermission(fileperm,'Y[YN][YN][YN][YN][YN]Y'))
		{
			var url = '/qwareinfo/oa/pubinfo/document/main.json?actionName=downloadFile&DOCID='+Id;
			Ext.parf.download(url);
		}
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.feedback');

Ext.qwareinfo.oa.portlet.feedback.FeedBackPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
	{
		this.contentPanel = new Ext.Panel
		({
			width : '100%',
			border : false
		});
		
		this.contentPanelId = this.contentPanel.getId();
		
		Ext.qwareinfo.oa.portlet.feedback.FeedBackPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
			dataURL : '/qwareinfo/oa/portlet/feedback.json?actionName=getPortalFeedBackList',
			ejsURL : '/qwareinfo/oa/portlet/feedback/template/feedbackportlet.ejs'
		});
	},
	prepareData : function(dataArray, index, data)
	{
		data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.FEEDBACKID+"','"+data.FEEDBACKSTATUS+"');";
		data.qtip = data.REQTITLE+'&nbsp;&nbsp;'+data.REQUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d')(data.REQBEGINTIME)+' 至 '+Ext.parf.renderDate('Y-m-d')(data.REQENDTIME);
		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
	},
	moreData : function()
	{
		Ext.parf.Module.loadModule('qwareinfo.oa.communication.feedback.todoresponse');
	},
	openDetail : function(Id,status)
	{
		Ext.parf.invokeItem('qwareinfo.oa.communication.feedback.todoresponse',Id,status);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.feedback');

Ext.qwareinfo.oa.portlet.feedback.MyFeedBackPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
	{
		this.contentPanel = new Ext.Panel
		({
			width : '100%',
			border : false
		});
		
		this.contentPanelId = this.contentPanel.getId();
		
		Ext.qwareinfo.oa.portlet.feedback.MyFeedBackPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
			dataURL : '/qwareinfo/oa/portlet/feedback.json?actionName=getPortalMyFeedBackList',
			ejsURL : '/qwareinfo/oa/portlet/feedback/template/myfeedbackportlet.ejs'
		});
	},
	prepareData : function(dataArray, index, data)
	{
		data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.REQID+"','"+data.REQSTATUS+"');";
		data.qtip = data.REQTITLE+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d')(data.REQBEGINTIME)+' 至 '+Ext.parf.renderDate('Y-m-d')(data.REQENDTIME);
		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
	},
	moreData : function()
	{
		Ext.parf.Module.loadModule('qwareinfo.oa.communication.feedback.manage');
	},
	openDetail : function(Id,reqStatus)
	{
		Ext.parf.invokeItem('qwareinfo.oa.communication.feedback.manage',Id,reqStatus);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.forum');

Ext.qwareinfo.oa.portlet.forum.ForumPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.forum.ForumPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/forum.json?actionName=getTopicPortletList',
        	ejsURL : '/qwareinfo/oa/portlet/forum/template/forumportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TOPICID+"');";
    	data.qtip = '['+data.TOPICTYPENAME+']'+data.TOPICTITLE+"&nbsp;&nbsp;" + 
    				data.POSTUSERID+"&nbsp;&nbsp;"+Ext.parf.renderDate('Y-m-d H:i')(data.POSTTIME);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.communication.forum.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.communication.forum.browse',Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.info');

Ext.qwareinfo.oa.portlet.info.InformationBrowsePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.info.InformationBrowsePortlet.superclass.constructor.call(this,
        {                
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : false,
        	dataURL : '/qwareinfo/oa/portlet/information.json?actionName=getBrowsePortletList',
        	ejsURL : '/qwareinfo/oa/portlet/info/template/informationbrowseportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.INFOID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.CREATETIME);
    	data.qtip = "["+data.CATALOGIDNAME+"]"+data.INFOTITLE+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.information.infobrowse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.information.infobrowse',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.info');

Ext.qwareinfo.oa.portlet.info.InformationCommentPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.info.InformationCommentPortlet.superclass.constructor.call(this,
        {          
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : false,
        	dataURL : '/qwareinfo/oa/portlet/information.json?actionName=getCommentPortletList',
        	ejsURL : '/qwareinfo/oa/portlet/info/template/informationcommentportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.INFOID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.CREATETIME);
    	data.qtip = "["+data.CATALOGIDNAME +"]"+ data.INFOTITLE+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.information.infobrowse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.information.infobrowse',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.info');

Ext.qwareinfo.oa.portlet.info.InformationPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
    	this.parentCatalogId = "";
    	if(conf)
    	{
    		this.parentCatalogId = conf.CATALOGID;
    	}
    	
		Ext.qwareinfo.oa.portlet.info.InformationPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/information.json?actionName=getPortletListByCatalogId&CATALOGID='+this.parentCatalogId,
			ejsURL : '/qwareinfo/oa/portlet/info/template/informationportlet.ejs'
			 
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.INFOID+"','"+data.CATALOGID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.CREATETIME);
    	data.qtip = "["+data.CATALOGIDNAME +"]"+ data.INFOTITLE+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.information.infobrowse', 'showCatalogId', this.parentCatalogId);
    },
	openDetail : function(infoId, catalogId)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.information.infobrowse', infoId, catalogId);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.info'); 

Ext.qwareinfo.oa.portlet.info.InformationRecommPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.info.InformationRecommPortlet.superclass.constructor.call(this,
        {                
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : false,
        	dataURL : '/qwareinfo/oa/portlet/information.json?actionName=getRecommendPortletList',
        	ejsURL : '/qwareinfo/oa/portlet/info/template/informationrecommendportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.INFOID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.CREATETIME);
    	data.qtip = "["+data.CATALOGIDNAME +"]"+ data.INFOTITLE+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.information.infobrowse');
    },
	openDetail : function(Id)
	{ 
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.information.infobrowse',Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.knowledge');

Ext.qwareinfo.oa.portlet.knowledge.KnowledgeBookPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.knowledge.KnowledgeBookPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle :conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/knowledge.json?actionName=getBookPortletList',
			ejsURL : '/qwareinfo/oa/portlet/knowledge/template/knowledgebookportlet.ejs'
			
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.BOOKID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.PUBLISHTIME);
    	data.qtip = "["+data.CATALOGIDNAME+"]"+data.BOOKNAME+"&nbsp;&nbsp;"+data.SAFEKEEPUSERIDNAME+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },  
    moreData : function()
    {
    	Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','5','0','0');
//    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.knowledge.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','5','7',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.knowledge');

Ext.qwareinfo.oa.portlet.knowledge.KnCaseStudyoPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.knowledge.KnCaseStudyoPortlet.superclass.constructor.call(this,
        {         
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle :conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/knowledge.json?actionName=getCaseStudyoPortletList', 
			ejsURL : '/qwareinfo/oa/portlet/knowledge/template/knowledgecasestudyoportlet.ejs'
			
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.CASESTUDYOID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.PUBLISHTIME);
    	data.qtip = "["+data.CATALOGIDNAME+"]"+data.CASESTUDYOTITLE+"&nbsp;&nbsp;"+data.PUBLISHUSERNAME+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },  
    moreData : function()
    {
    	Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','6','0','0'); 
//    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.knowledge.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','6','8',Id); 
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.knowledge');

Ext.qwareinfo.oa.portlet.knowledge.KnExperiencePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.knowledge.KnExperiencePortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle :conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/knowledge.json?actionName=getExperiencePortletList',
			ejsURL : '/qwareinfo/oa/portlet/knowledge/template/knowledgeexperienceportlet.ejs'
			
        });  
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.EXPERIENCEID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.PUBLISHTIME);
    	data.qtip = "["+data.CATALOGIDNAME+"]"+data.EXPERIENCETITLE+"&nbsp;&nbsp;"+data.PUBLISHUSERNAME+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','2','0','0');
//    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.knowledge.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','2','3',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.knowledge');

Ext.qwareinfo.oa.portlet.knowledge.KnowledgeExplainPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.knowledge.KnowledgeExplainPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle :conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/knowledge.json?actionName=getExplaionPortletList',
			ejsURL : '/qwareinfo/oa/portlet/knowledge/template/knowledgeexplainportlet.ejs'
			
        }); 
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.EXPLAINID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.PUBLISHTIME);
    	data.qtip ="["+data.CATALOGIDNAME+"]"+data.EXPLAINTITLE+"&nbsp;&nbsp;"+data.PUBLISHUSERNAME+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','0','0','0');
//    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.knowledge.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','0','1',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.knowledge');

Ext.qwareinfo.oa.portlet.knowledge.KnowledgeGainedPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.knowledge.KnowledgeGainedPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle :conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/knowledge.json?actionName=getGainedPortletList',
			ejsURL : '/qwareinfo/oa/portlet/knowledge/template/knowledgegainedportlet.ejs'
			
        });  
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.GAINEDID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.PUBLISHTIME);
    	data.qtip = "["+data.CATALOGIDNAME+"]"+data.GAINEDTITLE+"&nbsp;&nbsp;"+data.PUBLISHUSERNAME+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','3','0','0'); 
//    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.knowledge.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','3','5',Id); 
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.knowledge');

Ext.qwareinfo.oa.portlet.knowledge.KnInnovationPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.knowledge.KnInnovationPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : "知识创新",
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/knowledge.json?actionName=getInnovationPortletList',
			ejsURL : '/qwareinfo/oa/portlet/knowledge/template/knowledgeinnovationportlet.ejs'
			
        });
    }, 
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.INNOVATIONID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.PUBLISHTIME);
    	data.qtip = "["+data.CATALOGIDNAME+"]"+data.INNOVATIONTITLE+"&nbsp;&nbsp;"+data.PUBLISHUSERNAME+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','4','0','0'); 
//    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.knowledge.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','4','6',Id); 
	}
}); 
Ext.namespace('Ext.qwareinfo.oa.portlet.knowledge');

Ext.qwareinfo.oa.portlet.knowledge.KnowledgePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function()
    {
        Ext.qwareinfo.oa.portlet.knowledge.KnowledgePortlet.superclass.constructor.call(this,
        {
        	portletTitle : '知识库',
        	dataURL : '/qwareinfo/oa/portlet/knowledge.json?actionName=getPortletListByUser',
        	ejsURL : '/qwareinfo/oa/portlet/knowledge/template/knowledgeportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.KNTYPE+"','"+data.KNID+"');";
    }, 
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.knowledge.browse');
    },
	openDetail : function(Type,Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse',Type,Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.knowledge');

Ext.qwareinfo.oa.portlet.knowledge.KnowledgeQuestionPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.knowledge.KnowledgeQuestionPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle :conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/knowledge.json?actionName=getQuestionPortletList',
			ejsURL : '/qwareinfo/oa/portlet/knowledge/template/knowledgequestionportlet.ejs'
			
        }); 
    }, 
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.QUESTIONID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.PUBLISHTIME);
    	data.qtip ="["+data.CATALOGIDNAME+"]"+data.QUESTIONTITLE+"&nbsp;&nbsp;"+data.PUBLISHUSERNAME+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','1','0','0');
//    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.knowledge.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.knowledge.browse','1','2',Id); 
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.knowledge');

Ext.qwareinfo.oa.portlet.knowledge.KnowledgeStarPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.knowledge.KnowledgeStarPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle :conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/knowledge.json?actionName=getStarPortlet',
			ejsURL : '/qwareinfo/oa/portlet/knowledge/template/knowledgestarportlet.ejs'
        }); 
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.knowledge.star');
    }
});


Ext.namespace('Ext.qwareinfo.oa.portlet.message');

Ext.qwareinfo.oa.portlet.message.MessagePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.message.MessagePortlet.superclass.constructor.call(this,
        {   
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/message.json?actionName=getPortalListByUser',
        	ejsURL : '/qwareinfo/oa/portlet/message/template/messageportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.INBOXID+"');";
    	data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.SENDTIME);
    	
		data.read =  '<img src=/images/communication/feedback_isread_'+data.ISREAD+'.png width=16 height=16 ext:qtip =\''+ data.ISREADNAME + '\'>';	
    	data.attchIco = "";
    	if (data.HASATTACH == 1)
    	{
    		data.attchIco = "<img src=/images/ico-attch.gif width=16 height=16>";
    	}
    	data.qtip = data.SENDER+'&nbsp;&nbsp;'+data.MSGTITLE+'&nbsp;&nbsp;'+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.communication.message.inbox');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.communication.message.inbox',Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.mtroom');

Ext.qwareinfo.oa.portlet.mtroom.MtroomPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.mtroom.MtroomPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/mtroom.json?actionName=getMtroomPortalByUser',
        	ejsURL : '/qwareinfo/oa/portlet/mtroom/template/mtroomportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.MAID+"');";
    	data.qtip = data.MATHEME+'&nbsp;&nbsp;'+data.MTNAME+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.MASTARTTIME)+'至'+Ext.parf.renderDate('Y-m-d H:i')(data.MAENDTIME);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.dailyoffice.mtroom.usestate');
    },
	openDetail : function( Id )
	{
		Ext.parf.invokeItem('qwareinfo.oa.dailyoffice.mtroom.usestate', Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.notice');

Ext.qwareinfo.oa.portlet.notice.NoticePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
    constructor: function(conf)
    { 
        Ext.qwareinfo.oa.portlet.notice.NoticePortlet.superclass.constructor.call(this,
        {                           
	        portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/notice.json?actionName=getPortalNoticeList',
        	ejsURL : '/qwareinfo/oa/portlet/notice/template/noticeportlet.ejs'
        });
    }
	,
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.CHECKID+"');";
		data.lvl = '<img src=/images/publicinfo/notice_lvl_'+data.LVL+'.gif width=16 height=16 ext:qtip =\''+ data.LVLNAME + '\'>';					  	
    	data.isread = '已读';
    	if(data.ISREAD==0)
    	{
    		data.isread = '未读';
    	}
    	data.timeStr = Ext.parf.renderDate('Y-m-d')(data.PUBLISHTIME);
    	data.qtip = '['+data.NOTICETYPE+']'+data.TITLE+'&nbsp;&nbsp;'+data.PUBLISHUSERIDNAME+'&nbsp;&nbsp;'+data.timeStr;
    	
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.notice.check');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.notice.check',Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.plan');

Ext.qwareinfo.oa.portlet.plan.ConcernPlanPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.plan.ConcernPlanPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/plan.json?actionName=getPortletMyConcernList',
        	ejsURL : '/qwareinfo/oa/portlet/plan/template/concernplanportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.PLANID+"');";
		data.pertain = '';
		if(data.PERTAIN==1)
		{
			data.pertain = "部门";
		}
		else
		{
			data.pertain = "项目";
		}
    	
		data.timeStr = '';
		if(!Ext.isEmpty(data.STARTDATE))
		{
			data.timeStr = Ext.parf.renderDate('Y-m-d')(data.STARTDATE)+'至'+Ext.parf.renderDate('Y-m-d')(data.ENDDATE);
		}
		data.qtip = '['+data.pertain+']'+data.PLANTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'&nbsp;&nbsp;'+data.timeStr;
		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.workplan.myattention');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workplan.myattention',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.plan');

Ext.qwareinfo.oa.portlet.plan.MyPlanPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.plan.MyPlanPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/plan.json?actionName=getPortletMyPlanList',
        	ejsURL : '/qwareinfo/oa/portlet/plan/template/myplanportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.PLANID+"');";
    	data.pertain = '';
		if(data.PERTAIN==1)
		{
			data.pertain = "部门";
		}
		else
		{
			data.pertain = "项目";
		}
    	
		data.timeStr = '';
		if(!Ext.isEmpty(data.STARTDATE))
		{
			data.timeStr = Ext.parf.renderDate('Y-m-d')(data.STARTDATE)+'至'+Ext.parf.renderDate('Y-m-d')(data.ENDDATE);
		}
		data.qtip = '['+data.pertain+']'+data.PLANTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'&nbsp;&nbsp;'+data.timeStr;
		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.workplan.myresponsible');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workplan.myresponsible',Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.schedule');

Ext.qwareinfo.oa.portlet.schedule.SchedulePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.schedule.SchedulePortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/schedule.json?actionName=getSchedulePortalList',
        	ejsURL : '/qwareinfo/oa/portlet/schedule/template/scheduleportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.SCHEDULEID+"');";
    	data.qtip = '['+data.SCHEDULELEVELNAME+']'+data.SCHEDULECONTENT+'&nbsp;&nbsp;'+
    	Ext.parf.renderDate('Y-m-d H:i')(data.SCHEDULESTARTTIME)+'至'+Ext.parf.renderDate('Y-m-d H:i')(data.SCHEDULEENDTIME);
    	
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.communication.schedule.personal');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.communication.schedule.personal', Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.suvey');

Ext.qwareinfo.oa.portlet.suvey.SuveyPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.suvey.SuveyPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/survey.json?actionName=getPortalListByUser',
        	ejsURL : '/qwareinfo/oa/portlet/suvey/template/surveyportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.PARTICIPATEID+"');";
    	data.qtip = data.SURVEYTITLE+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.PUBLISHTIME);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.communication.survey.mysurvey');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.communication.survey.mysurvey',Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.task');

Ext.qwareinfo.oa.portlet.task.CheckTaskPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.task.CheckTaskPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/task.json?actionName=getPortalMyCheckList',
        	ejsURL : '/qwareinfo/oa/portlet/task/template/checktaskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
    	data.pertain = '';
		if(data.PERTAIN==1)
		{
			data.pertain = "部门";
		}
		else
		{
			data.pertain = "项目";
		}
    	
		data.timeStr = '';
		if(!Ext.isEmpty(data.STARTDATE))
		{
			data.timeStr = Ext.parf.renderDate('Y-m-d')(data.STARTDATE)+'至'+Ext.parf.renderDate('Y-m-d')(data.ENDDATE);
		}
		data.qtip = '['+data.pertain+']'+data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'&nbsp;&nbsp;'+data.timeStr;
		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.worktask.mycheck');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.worktask.mycheck',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.task');

Ext.qwareinfo.oa.portlet.task.ConcernTaskPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.task.ConcernTaskPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/task.json?actionName=getPortalMyConcernList',
        	ejsURL : '/qwareinfo/oa/portlet/task/template/concerntaskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
    	data.pertain = '';
		if(data.PERTAIN==1)
		{
			data.pertain = "部门";
		}
		else
		{
			data.pertain = "项目";
		}
    	
		data.timeStr = '';
		if(!Ext.isEmpty(data.STARTDATE))
		{
			data.timeStr = Ext.parf.renderDate('Y-m-d')(data.STARTDATE)+'至'+Ext.parf.renderDate('Y-m-d')(data.ENDDATE);
		}
		data.qtip = '['+data.pertain+']'+data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'&nbsp;&nbsp;'+data.timeStr;
		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.worktask.myconcern');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.worktask.myconcern',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.task');

Ext.qwareinfo.oa.portlet.task.MyAllotTaskPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.task.MyAllotTaskPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/task.json?actionName=getPortalMyAllotTaskList',
        	ejsURL : '/qwareinfo/oa/portlet/task/template/myallottaskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
    	data.pertain = '';
		if(data.PERTAIN==1)
		{
			data.pertain = "部门";
		}
		else
		{
			data.pertain = "项目";
		}
    	
		data.timeStr = '';
		if(!Ext.isEmpty(data.STARTDATE))
		{
			data.timeStr = Ext.parf.renderDate('Y-m-d')(data.STARTDATE)+'至'+Ext.parf.renderDate('Y-m-d')(data.ENDDATE);
		}
		data.qtip = '['+data.pertain+']'+data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'&nbsp;&nbsp;'+data.timeStr;
		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.worktask.myallot');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.worktask.myallot',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.task');

Ext.qwareinfo.oa.portlet.task.MyTaskPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.task.MyTaskPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/task.json?actionName=getPortalMyTaskList',
        	ejsURL : '/qwareinfo/oa/portlet/task/template/mytaskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
    	data.pertain = '';
		if(data.PERTAIN==1)
		{
			data.pertain = "部门";
		}
		else
		{
			data.pertain = "项目";
		}
    	
		data.timeStr = '';
		if(!Ext.isEmpty(data.STARTDATE))
		{
			data.timeStr = Ext.parf.renderDate('Y-m-d')(data.STARTDATE)+'至'+Ext.parf.renderDate('Y-m-d')(data.ENDDATE);
		}
		data.qtip = '['+data.pertain+']'+data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'&nbsp;&nbsp;'+data.timeStr;
		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.worktask.myapproval');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.worktask.myapproval',Id);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.workflow');

Ext.qwareinfo.oa.portlet.suvey.ApprovalPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.suvey.ApprovalPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/workflow.json?actionName=getApprovalInstanceList',
        	ejsURL : '/qwareinfo/oa/portlet/workflow/template/approvalPortlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.arriveTime = Ext.parf.renderDate('Y-m-d H:i')(data.ARRIVETIME)
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.STEPID+"','"+ data.PACKAGEID+"');";
    	data.qtip = '[' + data.CATALOGNAME + ']' + data.INSTANCENAME + '-' + data.INSTANCEID + 
    		data.CREATORNAME + '&nbsp;&nbsp;' + data.STEPNAME + data.SPETSTATUSNAME + 
    		'&nbsp;&nbsp;' + data.arriveTime;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('parm.workflow.handle.approval');
    },
	openDetail : function(setpId,packageId)
	{
		Ext.parf.invokeItem('parm.workflow.handle.approval',setpId,packageId);
	}
});


Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.PlanCompletedPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
	
        Ext.qwareinfo.oa.portlet.workgroup.PlanCompletedPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/workgroup/plan.json?actionName=getWorkGroupPlanCompletedPortletList&WORKGROUP='+conf.workgroupId,
        	ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/planportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.PLANID+"');";
    	data.qtip = data.PLANTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)
   		+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.workgroup.plan');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workgroup.plan',Id);
	}
});
/**
 * 项目门户-上周任务完成情况
 */
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.TaskColumnChartPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.ChartPortlet,
{
	constructor: function(conf)
	{
		Ext.qwareinfo.oa.portlet.workgroup.TaskColumnChartPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			chart : new Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTaskChartLastWeek(conf.workgroupId)
		});
	}
});


Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTaskChartLastWeek = Ext.extend(Ext.parf.HighChart,
{
	constructor : function(workgroupId)
	{
		var store = new Ext.data.JsonStore
		({
//			url : '/temp/data.jsp?f=portlet/workgrouptaskchartlaskweek.txt',
			url : '/qwareinfo/oa/portlet/dept/worktask.json?actionName=getWkPortletTaskColumnChartList&WORKGROUPID='+workgroupId,
//			root : 'data',
			fields : ['STATSDATE','COMPLETED','UNDERTAKE']
		});
		Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTaskChartLastWeek.superclass.constructor.call(this,
		{
			layout :'fit',
			store : store,
			loadMask : true,
			xField : 'STATSDATE',
			series : 
			[
				{
					name: '承办任务',
					dataIndex: 'UNDERTAKE',
					yAxis: 0
				},{
					type : 'spline',
					name : '完成任务',
					dataIndex : 'COMPLETED'/*,
					yAxis: 1*/
				}
			],
			chartConfig :
			{
				chart : 
				{
					defaultSeriesType: 'column'			
				},
				title: 				
				{
					text: ''
				},
				xAxis:
				[{
					
					labels :
					{
						rotation : 0, y : 10
					}
				}],
				yAxis: 
				[
					{
						min : 0,
						labels: 
						{
							formatter: function() 
							{
								return this.value;
							}
						},
						title: 
						{
							text: ''
						}
					}/*,{
						min : 0,
						max : 100,
						labels: 
						{
							formatter: function() 
							{
								return this.value;
							},
							style: 
							{
								color: '#89A54E'
							}
						},
						title: 
						{
							text: '百分比',
							style: 
							{
								color: '#89A54E'
							}
						},
						opposite: true
	      			}*/
	      		],
				tooltip:								
			    {
			    	formatter: function() {
	      				var y = this.series.type=='spline' ?Highcharts.numberFormat(this.y, 2)+'%' : this.y;
	      				
		    			return '<b>'+ this.x +'</b><br/>'+
			                this.series.name +': '+ this.y +'<br/>';
			   		}
		      	},
		      	plotOptions: 
		      	{
		      	   	series :
		      		{
		      			dataLabels: 				
						{
							x : 23,
							enabled: true
						}
					},
					column: 
					{
						pointPadding: 0.2,
						borderWidth: 0
					},
					line:
					{
						dataLabels: 				//设置图表显示内容
						{
//							formatter: function()
//							{
//								return  Highcharts.numberFormat(this.y, 2);	
//							}
						}
					}
				},
				exporting:
				{
					enabled :false,
					filename: '导出图表',
					url:'/parf/svg.json'
				}
			}
		});		
	}
})
/**
 *上周已完成重要任务
 */

Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.TaskCompletedPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		
        Ext.qwareinfo.oa.portlet.workgroup.TaskCompletedPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
//        	dataURL : '/temp/data.jsp?f=portlet/workgroupcompletedtask.txt',
        	dataURL : '/qwareinfo/oa/portlet/dept/worktask.json?' +
        			  'actionName=getWkPortletCompletedTaskList&WORKGROUPID='+conf.workgroupId,
        	ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/completedtaskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
        data.qtip = data.TASKTITLE+'&nbsp;&nbsp;'+data.EXECUTUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)
   		+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
   		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.workgroup.task');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workgroup.task',Id);
	}
});
/**
 *超期未完成任务
 */

Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.TaskOverdueIncompletePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		
        Ext.qwareinfo.oa.portlet.workgroup.TaskOverdueIncompletePortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
//        	dataURL : '/temp/data.jsp?f=portlet/workgroupoverdueincompletetask.txt',
        	dataURL : '/qwareinfo/oa/portlet/dept/worktask.json?' +
        			  'actionName=getWkPortletOverdueIncompleteTaskList&WORKGROUPID='+conf.workgroupId,
        	ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/taskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
        data.qtip = data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+data.EXECUTUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
   		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.workgroup.task');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workgroup.task',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.TaskPieChartPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.ChartPortlet,
{
	constructor: function(conf)
	{
		Ext.qwareinfo.oa.portlet.workgroup.TaskPieChartPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			chart : new Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTaskPie(conf.workgroupId)
		});
	}
});


Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTaskPie = Ext.extend(Ext.parf.HighChart,
{
	constructor : function(workgroupId)
	{
		var store = new Ext.data.JsonStore
		({
//			url : '/temp/data.jsp?f=example/workgroup_task_stats.txt',
			url : '/qwareinfo/oa/portlet/dept/worktask.json?actionName=getWkPortletTaskPieChartList&WORKGROUPID='+workgroupId,
//			root : 'data',
			fields : ['STATSINDEXNAME','STATSINDEXNUM']
		});
		Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTaskPie.superclass.constructor.call(this,
		{
			layout :'fit',
			store : store,
			series:
			[{
				type: 'pie',
				categorieField : 'STATSINDEXNAME',
				name: '任务',
				dataField : 'STATSINDEXNUM'
			}],
			chartConfig : 
			{
				chart :
				{
					defaultSeriesType : 'pie',
					plotBackgroundColor : null,
					plotBorderWidth : null,
					plotShadow : false
				},
				title :
				{
					text : ''
				},
				tooltip :
				{
					formatter: function() 
					{
						return '<b>'+ this.point.name +'</b>: '+ this.y + '个';
					}
				},
				plotOptions:
				{
					pie : 
					{
						center: ['48%', '50%'],
						size: '70%',
						allowPointSelect: true,
						cursor: 'pointer',
						dataLabels: 
						{
							enabled: true,
							distance: 7,
							formatter: function() 
							{
								return '<b>'+ this.point.name +'</b>: ' + 
								Highcharts.numberFormat(this.percentage, 2) +' %';
							}
						},
						showInLegend: true
					}
				},
				legend:
				{
					align: 'right',
					verticalAlign: 'bottom',
					layout: 'vertical'
				},
				exporting:
				{
					enabled :false
				}
			}
		});		
	}
})
/**
 * 本周代办重要任务
 */
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.TaskToDoPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		
        Ext.qwareinfo.oa.portlet.workgroup.TaskToDoPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
//        	dataURL : '/temp/data.jsp?f=portlet/workgrouptodotask.txt',
        	dataURL : '/qwareinfo/oa/portlet/dept/worktask.json?' +
        			  'actionName=getWkPortletToDoTaskList&WORKGROUPID='+conf.workgroupId,
        	ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/taskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
        data.qtip = data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+data.EXECUTUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
   		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.workgroup.task');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workgroup.task',Id);
	}
});

Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupAttrPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{
	constructor: function(conf)
	{
		this.workgroupId = conf.workgroupId;
		
		Ext.qwareinfo.oa.portlet.workgroup.WorkGroupAttrPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			dataURL : '/qwareinfo/oa/portlet/workgroup.json?actionName=getWorkgroupAttrPortlet&WORKGROUPID='+conf.workgroupId,
			ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/attrportlet.ejs'
		});
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupDocPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.workgroup.WorkGroupDocPortlet.superclass.constructor.call(this,
        {               
       		portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/workgroup/doc.json?actionName=getWorkGroupDocPortletList&WORKGROUPID='+conf.workgroupId,
        	ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/docportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
		data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.DOCID+"','"+data.FILEPERM+"');";
		
    	data.img = '';
		if(!data.FILESUFFIX)
		{
			data.img = '<img src="/extjs/filetype/folder-16.gif" valign="_bottom"/>';
		}
		else
		{
			data.img = '<img src="/extjs/filetype/'+data.FILESUFFIX+'-16.gif"/>';
		}
    	data.qtip = data.FILENAME+'&nbsp;&nbsp;'+data.CREATEUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.CREATETIME);
   		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.document.explorer');
    },
	openDetail : function(Id , fileperm)
	{
		if(Ext.qwareinfo.oa.pubinfo.document.plugin.checkPermission(fileperm,'Y[YN][YN][YN][YN][YN]Y'))
		{
			var url = '/qwareinfo/oa/pubinfo/document/main.json?actionName=downloadFile&DOCID='+Id;
			Ext.parf.download(url);
		}
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupExpensePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{
	constructor: function(conf)
	{
		this.workgroupId = conf.workgroupId;
		
		Ext.qwareinfo.oa.portlet.workgroup.WorkGroupExpensePortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			isMore : true,
			dataURL : '/qwareinfo/oa/portlet/expense.json?actionName=getWorkGroupExpensePortletList&WORKGROUPID='+conf.workgroupId,
			ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/expenseportlet.ejs'
		});
	},
	moreData : function()
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workgroup.expense.query', 'loadByWorkgroupId', this.workgroupId);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupInfoPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.workgroup.WorkGroupInfoPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/workgroup/info.json?actionName=getWorkGroupInfoPortletList&WORKGROUPID='+conf.workgroupId,
			ejsURL : '/qwareinfo/oa/portlet/dept/templete/informationportlet.ejs'
			
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.INFOID+"');";
  		data.timeStr = Ext.parf.renderDate('Y-m-d H:i')(data.CREATETIME);
    	data.qtip =  data.INFOTITLE+"&nbsp;&nbsp;"+data.timeStr;
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.information.infobrowse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.information.infobrowse',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupIntroPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		Ext.qwareinfo.oa.portlet.workgroup.WorkGroupIntroPortlet.superclass.constructor.call(this,
        {        
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	dataURL : '/qwareinfo/oa/portlet/workgroup.json?actionName=getWorkGroup&WORKGROUPID='+conf.workgroupId,
			ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/introportlet.ejs'
			
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	if(data.HEADER=="")
    	{
    		data.HEADER = "无"
    	}
    	if(data.MEMBER=="")
    	{
    		data.MEMBER = "无";
    	}
    	if(data.VIEWER=="")
    	{
    		data.VIEWER = "无";
    	}
    }
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupNoticePortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
    constructor: function(conf)
    { 
	
        Ext.qwareinfo.oa.portlet.workgroup.WorkGroupNoticePortlet.superclass.constructor.call(this,
        {                           
	        portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/workgroup/notice.json?actionName=getWorkGroupPotletNoticePP&PUBLISHORG='+conf.workgroupId,
        	ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/noticeportlet.ejs'
        });
    }
	,
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.CHECKID+"');";
    	data.lvl = '<img src=/images/publicinfo/notice_lvl_'+data.LVL+'.gif width=16 height=16 ext:qtip =\''+ data.LVLNAME + '\'>';
    	data.qtip = data.TITLE+'&nbsp;&nbsp;'+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.PUBLISHTIME);
   		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.pubinfo.notice.check');
  		
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.pubinfo.notice.check',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupPlanPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
	
        Ext.qwareinfo.oa.portlet.workgroup.WorkGroupPlanPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/workgroup/plan.json?actionName=getWorkGroupPlanPortletList&WORKGROUP='+conf.workgroupId,
        	ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/planportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.PLANID+"');";
    	data.qtip = data.PLANTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)
   		+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
    	data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.workgroup.plan');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workgroup.plan',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupStatsPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{
	constructor: function(conf)
	{
		this.workgroupId = conf.workgroupId;
		
		Ext.qwareinfo.oa.portlet.workgroup.WorkGroupStatsPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			dataURL : '/qwareinfo/oa/portlet/workgroup.json?actionName=getWorkgroupOverviewPortlet&WORKGROUPID='+conf.workgroupId,
			ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/statsportlet.ejs'
		});
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTaskPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
		
        Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTaskPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/workgroup/worktask.json?actionName=getWorkGroupTaskPortletList&WORKGROUP='+conf.workgroupId,
        	ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/taskportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TASKID+"');";
        data.qtip = data.TASKTITLE+'&nbsp;&nbsp;'+data.PCTCOMPLETED+'%&nbsp;&nbsp;'+data.EXECUTUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.STARTDATE)+'&nbsp;至&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.ENDDATE);
   		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.taskplan.workgroup.task');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workgroup.task',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTopicPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{ 
	constructor: function(conf)
    {
        Ext.qwareinfo.oa.portlet.workgroup.WorkGroupTopicPortlet.superclass.constructor.call(this,
        {               
        	portletClass : conf.portletClass,
        	portletIcon : conf.portletIcon,
        	portletTitle : conf.portletTitle,
        	isMore : true,
        	dataURL : '/qwareinfo/oa/portlet/workgroup/topic.json?actionName=getWorkGroupTopicPortletList&WORKGROUPID='+conf.workgroupId,
        	ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/topicportlet.ejs'
        });
    },
    prepareData : function(dataArray, index, data)
    {
    	data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.TOPICID+"');";
		data.qtip = data.TOPICTITLE+'&nbsp;&nbsp;'+data.POSTUSER+'&nbsp;&nbsp;'+Ext.parf.renderDate('Y-m-d H:i')(data.POSTTIME);
  		data.qtip = data.qtip.replace(/"([^"]*)"/g, "&quot;$1&quot;");
    },
    moreData : function()
    {
    	Ext.parf.Module.loadModule('qwareinfo.oa.communication.forum.browse');
    },
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.communication.forum.browse',Id);
	}
});
Ext.namespace('Ext.qwareinfo.oa.portlet.workgroup');

Ext.qwareinfo.oa.portlet.workgroup.WorkGroupWorkloadPortlet = Ext.extend(Ext.qwareinfo.oa.portlet.BasePortlet,
{
	constructor: function(conf)
	{
		this.workgroupId = conf.workgroupId;
		
		Ext.qwareinfo.oa.portlet.workgroup.WorkGroupWorkloadPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			isMore : true,
			dataURL : '/qwareinfo/oa/portlet/workload.json?actionName=getWorkGroupWorkloadPortletList&WORKGROUPID='+conf.workgroupId,
			ejsURL : '/qwareinfo/oa/portlet/workgroup/templete/workloadportlet.ejs'
		});
	},
	prepareData : function(dataArray, index, data)
	{
		data.clickFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','openDetail','"+data.WORKLOADID+"');";
		
		data.attchIco = "";
		if (!Ext.isEmpty(data.ATTACHMENT))
		{
			data.attchIco = "<img src=/images/ico-attch.gif width=16 height=16>";
		}
	},
	moreData : function()
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workgroup.workload.query', 'loadByWorkgroupId', this.workgroupId);
	},
	openDetail : function(Id)
	{
		Ext.parf.invokeItem('qwareinfo.oa.taskplan.workgroup.workload.query', Id);
	}
});



Ext.namespace('Ext.qwareinfo.customize.portlet');

Ext.qwareinfo.customize.portlet.BasePortlet = Ext.extend(Ext.parf.portal.Portlet,
{ 
	dataloaded : false,
	bodyDivId : '',
	contentPanel : null,
    constructor: function(conf)
    { 
    	Ext.apply(this,conf);
    	
    	this.portletClass =  this.portletClass || 'oa_portlet_b_1';
    	this.isMore = this.isMore || false;
    	this.portletTitle = this.portletTitle || '标题';
    	
    	this.contentPanel = new Ext.Panel(
    	{
    		html : '加载中...',
    		border : false
    	});
    	this.id = Ext.id();
    	this.bodyDivId = "content" + this.id;
    	
        Ext.qwareinfo.customize.portlet.BasePortlet.superclass.constructor.call(this,
        {               
        	frame : false,
        	header : false,
        	border:false,
			html : this.getTemplate()
        });
        
        this.contentPanel.on('afterrender',this.beforeLoadData,this);
        
        this.on('afterrender',function(){
        	 this.contentPanel.render(this.bodyDivId);
        },this);
    },
    colsePortlet : function()
    {
    	//this.ownerCt.ownerCt.closePortlet(this);	
    	Ext.parm.portal.desktop.closePortlet(this);
    },
    toggleCollapse : function()
    {    
    	this.contentPanel.toggleCollapse(true);
    },
    beforeLoadData : function()
    {
    	if(this.dataloaded) return;
    	this.loadData();
    },
    refreshData : function()
    {
    	this.dataloaded = false;
    	this.beforeLoadData();
    },
    loadData : function()
    {
    	Ext.fjs.ajax
		({
			url : this.dataURL||this.getDataURL(),
			disableCaching : true,
			success : this.loadData_success,
			scope: this
		});
    },
    loadData_success : function(response,options)
	{
		var res = Ext.decode(response.responseText);
		
		if(res.length>0)
		{
			for(var i=0;i<res.length;i++)
			{
				this.prepareData(res, i, res[i]);
			}
		}
		var data = {data : res};
		var html = new EJS({url : this.ejsURL}).render(data);
		Ext.getDom(this.contentPanel.body).innerHTML = html;
       	this.contentPanel.doLayout();
		this.doLayout();
	},
	prepareData : function(dataArray, index, data)
	{
		
	},
    moreData : function()
    {
    	alert('moreData');
    },
    getTemplate : function()
    {
    	var bottom = '<div class="oa_portlet_br"><div class="oa_portlet_bl"><div class="oa_portlet_bc"></div></div></div>';
    	var body = '<div class="oa_portlet_ml"><div class="oa_portlet_mr"><div class="oa_portlet_mc" id="'+ this.bodyDivId +'"></div></div></div>';
    	
    	var header = '<div class="oa_portlet_tr"></div>';
    	header += '<div class="oa_portlet_tc">'
    	if(this.portletIcon)
    	{
    		header += '<div class="titleIco '+ this.portletIcon + '"></div>';
    		this.portletTitle = "&nbsp;" + this.portletTitle;
    	}
    	header += this.portletTitle;
    	header += '</div>';
    	header += '<div class="oa_portlet_tl"></div>';
    	header += '<div class="oa_portlet_tool">';
    	header += '<div class="oa_portlet_btn btn1" onclick="Ext.parf.reflect.ExtCmp(\''+ this.id +'\',\'toggleCollapse\');"></div>';
    	if(this.isMore)
    	{
    		header += '<div class="oa_portlet_btn btn2" onclick="Ext.parf.reflect.ExtCmp(\''+ this.id +'\',\'moreData\');"></div>';
    	}	
    	header += '<div class="oa_portlet_btn btn3" onclick="Ext.parf.reflect.ExtCmp(\''+ this.id +'\',\'colsePortlet\');"></div>';
    	header += '</div>';
    	
    	
    	return '<div class="oa_portlet"><div class="'+ this.portletClass +'">' + header + "</div>" + body + bottom + "</div>";
    	
    },
    getDataURL : function()
    {
    	return "";
    },
    beforeDestroy : function()
    {
    	Ext.destroy(this.contentPanel);
    	Ext.qwareinfo.customize.portlet.BasePortlet.superclass.beforeDestroy.call(this);
    }
});

Ext.namespace('Ext.qwareinfo.customize.portlet.timeattendance');

Ext.qwareinfo.customize.portlet.timeattendance.DailyRegisterPortlet = Ext.extend(Ext.qwareinfo.customize.portlet.BasePortlet,
{
	constructor: function(conf)
	{
		Ext.qwareinfo.customize.portlet.timeattendance.DailyRegisterPortlet.superclass.constructor.call(this,
		{
			portletClass : conf.portletClass,
			portletIcon : conf.portletIcon,
			portletTitle : conf.portletTitle,
			isMore : false,
			dataURL : '/qwareinfo/customize/portlet/timeattendance.json?actionName=getDailyRegister',
			ejsURL : '/qwareinfo/customize/portlet/timeattendance/templete/dailyregisterportlet.ejs'
		});
	},
	prepareData : function(dataArray, index, data)
	{
		data.clickSignInFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','signIn','"+data.TIMEATTENDANCEID 
								+"','"+data.ATTENDANCETIME.time +"','"+data.LATETIME.time+"');";
		data.clickSignOutFn = "Ext.parf.reflect.ExtCmp('"+this.id+"','signOut','"+data.TIMEATTENDANCEID 
								+"','"+data.ATTENDANCETIME.time +"','"+data.LEAVEEARLYTIME.time+"');";
	},
	signIn : function(Id,signTine,overTime)
	{
		var attendanceType = '';
		if( overTime < new Date().getTime())
		{
			Ext.parf.showError('不能签到!');
			return;
		}
		if( signTine < new Date().getTime())
		{
			attendanceType = '2';
		}
		
		Ext.Ajax.request
		({
			url : '/qwareinfo/customize/timeattendance/personalattendance/dailyregister.json',
			params : 
			{
				actionName : 'signIn' ,
				TIMEATTENDANCEID : Id,
				ATTENDANCETYPE : attendanceType
			},
			success :this.sign_success,
			scope : this
		});
	},
	signOut : function(Id,signTine,overTime)
	{
		var attendanceType = '';
		if( overTime > new Date().getTime())
		{
			Ext.parf.showError('不能签退!');
			return;
		}
		if( signTine > new Date().getTime())
		{
			attendanceType = '3';
		}
		
		Ext.Ajax.request
		({
			url : '/qwareinfo/customize/timeattendance/personalattendance/dailyregister.json',
			params : 
			{
				actionName : 'signOut' ,
				TIMEATTENDANCEID : Id,
				ATTENDANCETYPE : attendanceType
			},
			success :this.sign_success,
			scope : this
		});
	},
	sign_success :function(response ,opts){
		var result = Ext.decode(response.responseText);
		Ext.parf.showInfo(result.returnMessage);
		this.loadData();
	}
});
