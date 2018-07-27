Ext.namespace("Ext.fjs");
Ext.fjs.isWebsign=true;
Ext.fjs.isHandWrite=true;
Ext.fjs.Index=Ext.extend(Ext.Viewport,{
	heaer:null,menu:null,tabs:null,
	getTitlePanelHtml:function(){
		return '<div class="oa-header">'+
			'<div class="headerNav">' +
				'<div class="nav">' +
				'<table>' + 
					'<tr>' +
						'<td>' +
							'<div style="text-align:right;"><span id="sys_date"></span></div>' +
							'<div style="text-align:right;">' +
							'欢迎，<span id="sys_user">管理员</span>' +
							'&nbsp;&nbsp;' + 
							'<a href="javascript:void(0);" onclick="Ext.fjs.reflect.exit(\''+ this.id +'\',\'logout\');">[退出]</a>' +
							'</div>' +
						'</td>' +
					'</tr>' + 
				'</table>' +
				'</div>' +
			'</div>' +
		'</div>';
	},
	createHeader:function(us){
		this.heaer=new Ext.Panel({region:"north",border:false,height:50,margins:"0 0 5 0",html:this.getTitlePanelHtml()});
	},
	openPWDWin:function(){
		var win = new Ext.part.login.ModifyPwdWin();
		win.show();
	},
	openInfoWin:function(){
		var win = new Ext.part.userinfo.UserOtherInfoWindow();
		win.show();
	},
	logout:function(){
		Ext.msg.confirm(Ext.fjs.NOTICE,"您确定要退出吗？",function(btn){if(btn=="yes"){location.href="logout.jsp";}},this);
	},
	loadDesktop:function(mod){
		Ext.fjs.module.loadModule(mod);
	},
	loadMsg:function(){
		this.reminder.showMsgPanel();
	},
	createMenu:function(){
		this.menu=new Ext.Panel({region:"west",title:Ext.msg.menu,margins:"0 0 5 5",split:true,width:180,minSize:150,maxSize:270,animCollapse:false,animate:false,collapsible:true,collapseFirst:false,collapseMode:"mini",layout:{animate:true,type:"accordion"}});
		Ext.fjs.get({url:"menubar.do",success:this.loadMenu,scope:this});
	},
	loadMenu:function(res){
		eval("var items="+res.responseText);
		for(var i=0,j=items.length;i<j;i++){
			var tree=new Ext.tree.TreePanel({
			title:items[i].text,iconCls:items[i].iconCls,
			useArrows:true,
			animate:true,
			autoScroll:true,
			border:false,
			containerScroll:true,
			rootVisible:false,
			root:this.loadTreeNode(items[i])});
			items[i].parent=null;
			tree.on("click",this.evt_menuTree,this);
			this.menu.add(tree);
		}
		Ext.fjs.module.menuItems=items;
		this.menu.doLayout();
	},
	evt_menuTree:function(node){
		if(Ext.isIE&&this.tabs.items.getCount()>30){Ext.fjs.showInfo(Ext.msg.warn[0]);return;}
		if(node.hasChildNodes()){if(node.isExpanded()){node.collapse();}else{node.expand();}}
		if(node.module!=""){
			node.disable();
			if(this.isMenuDefer){(function(){this.loadnode(node);}).defer(this.menuDeferTime,this);}else{this.loadnode(node);}
			Ext.fjs.post({url:"opt.do",params:{Id:node.id,code:node.module,name:node.getPath("text")},scope:this});
		}
	},
	loadnode:function(node){
		try{
			var moduleobj = Ext.decode(node.module);
		if(moduleobj.moduleId){
			Ext.fjs.module.loadMainMenuModule(node.id, moduleobj.moduleId,function(panel){
				node.enable();
				if(moduleobj.bizId){
					panel.loadBiz(moduleobj.bizId,node.text,node.icon);
				}
				if(moduleobj.reportURL){
					panel.loadReport(moduleobj.reportURL,node.text,node.icon);
				}
			});
		}
	}catch(e){
		Ext.fjs.module.loadMainMenuModule(node.id, node.module,function(panel){
			node.enable();
		});
	}
	},
	loadTreeNode:function(item){
		var node=new Ext.tree.TreeNode(item);
		node.icon="/ext/icons/menu/"+item.iconCls+".gif";
		node.module=item.module||"";
		if(!item.leaf&&item.children){
			for(var i=0,j=item.children.length;i<j;i++){
				item.children[i].parent=item;
				node.appendChild(this.loadTreeNode(item.children[i]));
			}
		}
		return node;
	},
	createTabs:function(){
		this.tabs=new Ext.TabPanel({region:"center",margins:"0 5 5 0",activeTab:0,enableTabScroll:true,items:[]});
		this.tabs.on("afterrender",this.evt_afterrender,this);
		this.tabs.on("tabchange",this.evt_tabchange,this);
	},
	evt_afterrender:function(){
		this.tabs.loadMask=new Ext.LoadMask(this.tabs.body,{msg:Ext.msg.loading});
	},
	evt_tabchange:function(tab,P){
		if(P&&P.tabTitle){document.title=P.tabTitle;}
	},
	constructor:function(debug,desktop){
		this.isMenuDefer=true;
		this.menuDeferTime=3000;
		this.id=Ext.id();
		this.createHeader();
		this.createMenu();
		this.createTabs();
		Ext.fjs.Index.superclass.constructor.call(this,{layout:"border",items:[this.heaer,this.menu,this.tabs],renderTo:Ext.getBody()});
		var mode=(debug=="portal");
		Ext.fjs.module.initModule(this.tabs,mode,function(){
			Ext.fjs.module.loadModule(desktop);
			if(debug=="portal") Ext.fjs.module.debugMode=false;
			if(debug=="true") Ext.fjs.module.debugMode=true;
		});
		//this.reminder = new Ext.parm.sitemsg.Reminder();
		this.day = new Ext.parm.day.Time();
		if(this.isMenuDefer){(function(){this.isMenuDefer = false;}).defer(this.menuDeferTime,this);}
		(function(){Ext.fjs.Platform.fireEvent("ready");}).defer(3000,this);
	},
	beforeDestroy:function(){
		Ext.destroy(this.tabs.loadMask);
		Ext.destroy(this.heaer);
		Ext.destroy(this.menu);
		this.tabs.removeAll();
		Ext.destroy(this.tabs);
		this.reminder.destroy();
		Ext.destroy(this.remindTask);
		this.day.destroy();
		if(Ext.fjs.module.tabPanel.MultiUserSelectWindow!=null){
			Ext.destroy(Ext.fjs.module.tabPanel.MultiUserSelectWindow);
		}
		if(Ext.fjs.module.tabPanel.PermissionSelectWindow!= null){
			Ext.destroy(Ext.fjs.module.tabPanel.PermissionSelectWindow);
		}
		if(Ext.fjs.module.tabPanel.SingelUserSelectWindow!=null){
			Ext.destroy(Ext.fjs.module.tabPanel.SingelUserSelectWindow);
		}
		Ext.fjs.module.tabPanel=null;
		Ext.fjs.module.menuItems=null;
		Ext.fjs.module.realTabIds=null;
		Ext.fjs.module.libstatus=null;
		Ext.fjs.Index.superclass.beforeDestroy.call(this);
	}
});
Ext.fjs.Platform=function(){var A=Ext.extend(Ext.util.Observable,{constructor:function(){A.superclass.constructor.call(this);this.addEvents("ready");},onReady:function(fn,s,opt){this.addListener("ready",fn,s,opt);}});return new A();}();
Ext.namespace("Ext.parm.day");
Ext.parm.day.Time=Ext.extend(Object,{task:null,
	constructor:function(){
		this.task=new Ext.util.TaskRunner();
		this.task.start({run:this.showDay,interval:1000,scope:this});
	},
	showDay:function(){var e=this.e||$("sys_date");e.innerHTML=Date.showDay();},
	destroy:function(){if(this.task!=null)this.task.stopAll();}
});
Ext.namespace("Ext.parm");
Ext.parm.HostAddr=function(){return{
	getAddr:function(){var h=location.href;return h.substring(0,h.lastIndexOf('/'));}
};}();
