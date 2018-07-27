Ext.namespace('Ext.qwareinfo.oa.pubinfo.document.plugin');
Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocFileViewPanel = Ext.extend(Ext.Panel,{
	//成员对象
    m_fileviewToolBar: null,    
    m_fileStore:null, 
    m_currentFolderId: '0',
    m_pagesize : 100,
    m_viewName: 'icon',
    m_view: null,
    m_contextmenu: null,
    m_filePathList: null,
    m_curFolder: null,
    
    m_sourceId:'',
    m_sourceDocType:'',
    m_sourceDocName: '',
    m_sourceType:'',
            
    f_treePanel:null,
    f_mainPanel:null,
        
    constructor: function()
    {   
    	this.m_emptyStore = new Ext.data.SimpleStore
	    ({
            fields: ['DOCID','FILENAME','DOCTYPE','suffix','ShortName'],
            data : []
        });
        this.m_fileStore = new Ext.parf.data.PageStore
        ({
              url: Ext.qwareinfo.oa.pubinfo.document.plugin.g_mainurl,
              baseParams: { actionName:'getFolderFiles'},                    //调用json的方法名   //从后台取哪些字段         
              fields: ['DOCID','FILENAME','DOCTYPE','FILELEN','LASTUPDATETIME','FILESUFFIX','suffix','ShortName','FILEPERM']                                                                 
        });  
        
        this.m_fileviewToolBar = new Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocFileViewToolBar();
        this.m_fileviewToolBar.f_fileview = this;                        
            
        this.m_iconview = new Ext.DataView
        ({
            store: this.m_fileStore,               
            style:'overflow:auto',                  
            overClass:'x-view-over',
            tpl: new Ext.XTemplate
            (
	            '<tpl for=".">',
	                '<div class="icon-wrap" title="{FILENAME}">',
	                '<table width="60"><tr><td align="center"><div class="icon"><img src="/extjs/filetype/{suffix}-32.gif" ></div></td></tr></table>',
	                '<span class="x-editable">{ShortName}</span></div>',
	            '</tpl>',
	            '<div class="x-clear"></div>'
	        ),               
            multiSelect: true,                     
            itemSelector:'div.icon-wrap',
            emptyText: '' ,            
            prepareData: function(data)
		    {           
		        data.ShortName = data.FILENAME.ellipse(13);             
		        return data;
		    }
        });
        this.m_view = this.m_iconview;
        
        this.m_thumbview = new Ext.DataView
        ({
            store: this.m_emptyStore,               
            style:'overflow:auto',                  
            overClass:'x-view-over',
            tpl: new Ext.XTemplate
            (
                '<tpl for=".">',
                    '<div class="thumb-wrap" title="{FILENAME}">',
                    '<table class="thumb-table"><tr><td align="center">',
                    '<div class="thumb"><img src="{FilePath}" pic="{pic}" onload="Ext.qwareinfo.oa.pubinfo.document.plugin.loadPic(this)"></div>',
                    '</td></tr></table>',
                    '<span class="x-editable">{ShortName}</span></div>',
                '</tpl>',
                '<div class="x-clear"></div>'
            ),   
            hidden:true,
            multiSelect: true,             
            itemSelector:'div.thumb-wrap',
            emptyText: '' ,
            m_picsuffix: ['gif','jpg','jpeg'],
            m_mimetype: ['image/gif','image/jpeg','image/jpeg'],
            prepareData: function(data)
            {           
                data.ShortName = data.FILENAME.ellipse(20);
                var index = this.m_picsuffix.indexOf(data.suffix.toLowerCase());
                if(index==-1)
                {
                	data.FilePath = '/extjs/filetype/' + data.suffix + '-48.gif'
                	data.pic = 0;
                }
                else 
                {
                	data.FilePath = '/qwareinfo/oa/pubinfo/document/displaypic.json?DOCID=' 
                		            + data.DOCID + "&mimetype="+ this.m_mimetype[index];
                	data.pic = 1;                	
                }
                return data;
            }
        });
        
        this.m_listview = new Ext.list.ListView
        ({
            store: this.m_emptyStore,
            multiSelect: true,
            emptyText: '',
            hidden:true,
            reserveScrollOffset: true,            
	        columns: 
	        [
	           {
	               header: '文件',
	               width: .6,
	               tpl:'<table border="0" cellspacing="0" cellspadding="0"><tr><td height="16" width="18"><img src="/extjs/filetype/{suffix}-16.gif"></td><td class="listview" valgin="bottom">{FILENAME}</td></tr></table>',
	               dataIndex: 'FILENAME'
	           },
	           {
	               header: '修改日期',
	               tpl:'{LASTUPDATETIME:javaDate}',
	               width: .25, 
	               dataIndex: 'LASTUPDATETIME'
	           },
	           {
	               header: '大小',	               
	               dataIndex: 'FILELEN',	
	               tpl: '{FILELEN:qwarefilesize}',
	               align: 'right'	            
	           }
	        ]
	    });
        
        Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocFileViewPanel.superclass.constructor.call(this,
        {    
            region:'center',
            border:false,
            style : 'border-left: 1px solid #8db2e3;', 
            title: '/',
            autoScroll:true,
            cls: 'oa-doc-icon',
            layout:'fit',
            items: [this.m_thumbview,this.m_iconview,this.m_listview], 
            bbar: Ext.parf.createPageToolBar({pageSize:this.m_pagesize, store: this.m_fileStore}),                                 //Grid 下方的翻页Button   点击刷新按钮时，从gridStore取得记录                     
            tbar: this.m_fileviewToolBar          
        });
        this.on("afterrender",this.evt_afterrender,this);
        this.m_iconview.on("dblclick",this.evt_iconview_dblclick,this);
        this.m_thumbview.on("dblclick",this.evt_iconview_dblclick,this);
        this.m_listview.on("dblclick",this.evt_iconview_dblclick,this);      
    },
    evt_afterrender: function()
    {            	
    	this.body.dom.onselectstart=function(){return false };    	
    	this.m_fileStore.load({params:{start:0, limit:this.m_pagesize}});  //指明列表默认显示的记录数。    	
    },
    beforeDestroy:function()
    {           	
        this.m_emptyStore.destroy();
        this.m_fileStore.destroy();
        Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocFileViewPanel.superclass.beforeDestroy.call(this);
    },
    evt_iconview_dblclick:function(dataview, index, node, e)
    {
    	var rec = dataview.getRecord(node);
    	var DOCID = rec.get("DOCID");
    	var DOCTYPE = rec.get("DOCTYPE");
    	if(Ext.isEmpty(DOCTYPE)) return;
    	if(DOCTYPE == '1')
    	{    	
    		this.openFolder(DOCID);
    	   	this.selectTreeNode(DOCID);
    	}
    },
    getSelectRecords:function()
    {
    	var nodes = this.m_view.getSelectedNodes();
    	if(nodes.length==0) return null;
    	return this.m_view.getSelectedRecords();            	
    },    
    selectTreeNode:function(docid)
    {
        var node = this.f_treePanel.getNodeById(docid);
        node.expand();
        node.select();
    },
    openFolder:function(folderId)
    {
    	this.m_currentFolderId = folderId;
    	Ext.parf.setProperty(this.m_fileStore.baseParams,'PARENTID',folderId);
        this.m_fileStore.reload({params:{start:0, limit:this.m_pagesize}});
        this.selectTreeNode(folderId);
        if(folderId=='0') 
        {
        	this.setTitle("/");           	
        	return;
        }
        var showPathOption = 
        { 
            url: Ext.qwareinfo.oa.pubinfo.document.plugin.g_mainurl, 
            params:
            {
                actionName:'getFolderPath',
                folderId: folderId                
            }, 
            method: 'post', 
            success: this.showPathOption_success,
            scope: this
        };                    
        Ext.Ajax.request(showPathOption);
    },
    showPathOption_success:function(response, opts)
    {    	    	    	
    	var res = Ext.decode(response.responseText);     	    
    	this.m_filePathList = res.list;
    	this.m_curFolder = res.doc;
    	var list = res.list;
    	var title = "";
    	for(var i=0;i<list.length;i++)
    	{
    		if(i<list.length-1)
    		{
    		   title = title + '/<a href="javascript:void(0);" onclick="Ext.parf.reflect.ExtCmp(\''
                  		     + this.getId()+'\',\'openFolder\',\''+ list[i].DOCID + '\')">' + list[i].FILENAME +'</a>';
    		}
    		else
    		{
    		   title = title + "/" + list[i].FILENAME;  	
    		}
    	}    	
        this.setTitle(title);           
    }
});

Ext.namespace('Ext.qwareinfo.oa.pubinfo.document.plugin');

Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocFileViewToolBar = Ext.extend(Ext.Toolbar,
{               
    f_fileview: null,
    
    constructor: function()
    { 
        this.folderButton = new Ext.Button(
        {            
            text: '显示目录',
            iconCls:'mnu-folder',                    
            tooltip:'打开/关闭文件目录',            
            handler: this.folderButton_onclick,
            scope: this
        });
        this.folderUpButton = new Ext.Button(
        {            
            text: '向上',
            icon:'/images/publicinfo/folder_up.gif',                    
            tooltip:'进入上层文件夹',            
            handler: this.folderUpButton_onclick,
            scope: this
        });
        this.viewButton = new Ext.Button
        ({
        	text: '视图',
            iconCls:'mnu-application_view_detail',                    
            tooltip:'文件视图',            
            scope: this,
            menu: 
            [
                {
                    text : '缩略图',                    
                    handler : this.thumbview_onclick,
                    checked: false,
                    group: 'toolview',
                    scope : this
                },                
                {
                    text : '图标',
                    checked: true,
                    group: 'toolview',
                    handler : this.iconview_onclick,
                    scope : this
                },
                {
                    text : '详细信息',
                    checked: false,
                    group: 'toolview',
                    handler : this.listview_onclick,
                    scope : this
                }
            ]
        });
        this.orderButton = new Ext.Button
        ({
        	text: '排列',
            iconCls:'btn-order',                    
            tooltip:'文件排列',            
            scope: this,
            menu: 
            [
                {
                    text : '名称',                    
                    handler : this.ordername_onclick,
                    checked: true,
                    group: 'toolorder',
                    scope : this
                },
                {
                    text : '大小',
                    checked: false,
                    group: 'toolorder',
                    handler : this.ordersize_onclick,
                    scope : this
                },
                {
                    text : '类型',
                    checked: false,
                    group: 'toolorder',
                    handler : this.ordertype_onclick,
                    scope : this
                },
                {
                    text : '修改时间',
                    checked: false,
                    group: 'toolorder',
                    handler : this.ordertime_onclick,
                    scope : this
                }
            ]
        });
        Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocFileViewToolBar.superclass.constructor.call(this,
        {            
            items:[this.folderButton,'-',this.folderUpButton,'->',this.viewButton,'-',this.orderButton ]       //按钮之间的“|”
        }); 
    },                        
    folderUpButton_onclick:function(btn)
    {
    	if(this.f_fileview.m_currentFolderId=='0') 
    	{
    		Ext.MessageBox.alert(Ext.parf.common_msgTitle, '当前已经是根目录无法进入到上级！');
    		return;
    	}
    	var parentFolderOption = 
        { 
            url: Ext.qwareinfo.oa.pubinfo.document.plugin.g_mainurl, 
            params:
            {
                actionName:'getFolderInfo',
                folderId: this.f_fileview.m_currentFolderId                
            }, 
            method: 'post', 
            success: this.parentFolderOption_success,
            scope: this
        };                    
        Ext.Ajax.request(parentFolderOption);
    },    
    parentFolderOption_success:function(response, opts)
    {    	
    	var result = Ext.decode(response.responseText);               
        if(result.success)
        {
            this.f_fileview.openFolder(result.data.PARENTID);               
        }
        else
        {                       
            Ext.parf.showError(result.errorMessage);
        }
    },
    folderButton_onclick: function(btn)
    {   
        if(this.f_fileview.f_treePanel.isVisible())
        {
        	this.f_fileview.f_treePanel.collapse();
        }
        else
        {
        	this.f_fileview.f_treePanel.expand();
        }        
    },
    ordername_onclick:function()
    {
    	this.f_fileview.m_fileStore.sort('FILENAME','asc');
    },
    ordersize_onclick:function()
    {
    	this.f_fileview.m_fileStore.sort('FILELEN','asc');
    },
    ordertype_onclick:function()
    {
    	this.f_fileview.m_fileStore.sort('FILESUFFIX','asc');
    },
    ordertime_onclick:function()
    {
    	this.f_fileview.m_fileStore.sort('LASTUPDATETIME','asc');
    },
    thumbview_onclick:function()
    {
    	this.f_fileview.m_view.hide();    	
    	this.f_fileview.m_viewName = 'thumb';
    	this.f_fileview.m_thumbview.setStore(this.f_fileview.m_fileStore);
    	this.f_fileview.m_thumbview.show();
    	this.f_fileview.m_view = this.f_fileview.m_thumbview;
    },
    iconview_onclick:function()
    {
        this.f_fileview.m_view.hide();        
    	this.f_fileview.m_viewName = 'icon';
    	this.f_fileview.m_iconview.setStore(this.f_fileview.m_fileStore);
        this.f_fileview.m_iconview.show();       
        this.f_fileview.m_view = this.f_fileview.m_iconview;
    },
    listview_onclick:function()
    {
        this.f_fileview.m_view.hide();        
        this.f_fileview.m_viewName = 'list';
        this.f_fileview.m_listview.setStore(this.f_fileview.m_fileStore);
        this.f_fileview.m_listview.show();
        this.f_fileview.m_view = this.f_fileview.m_listview;
    }
});

Ext.namespace('Ext.qwareinfo.oa.pubinfo.document.plugin');

Ext.qwareinfo.oa.pubinfo.document.plugin.g_mainurl = '/qwareinfo/oa/pubinfo/document/plugin.json';

Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocPlugin = Ext.extend(Ext.util.Observable, {
	
	field: null,
	getdocwin: null,
	
	init:function(uploadfield)
	{
		if(uploadfield.uploadType!='upload') return;
		this.field = uploadfield;
		this.field.on('render', this.onRender, this);
		this.field.on('destroy', this.onDestroy, this);
	},
	onRender: function() 
	{
		var toolbar = this.field.innerCt.getTopToolbar();
		toolbar.addButton(new Ext.Toolbar.Separator());
		this.fetchDocBtn = new Ext.Button
		({
			text : '从文档库获取',
            iconCls:'mnu-database_go',
            handler : this.fetchFromDoc_onclick,            
            scope : this
		});
		toolbar.addButton(this.fetchDocBtn);
	},
	onDestroy: function() 
	{		
		if(this.getdocwin!=null)  Ext.destroy(this.getdocwin);
	},
	fetchFromDoc_onclick:function(btn)
	{
		if(this.getdocwin==null)
		{
			this.getdocwin = new Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocWin();
			this.getdocwin.parent = this;
		}
		this.getdocwin.show(btn.id)
	},
	createRecords:function(list)
	{
		for(var i=0;i<list.length;i++)
		{
			var REC = this.field.store.recordType;
	        var p = new REC({
	            fn: list[i].FILENAME,
	            id: list[i].FILEID,
	            suffix: list[i].FILETYPE
	        });
	        var count = this.field.store.getCount();  
	        this.field.store.insert(count, p);       
	        this.field.createTip(p);			
		}        
		var count = this.field.store.getCount();
        var str = '';
        for(var i=0;i<count;i++)
        {
            var rec = this.field.store.getAt(i);
            str = str + rec.get('fn') + "|" + rec.get('id') + ",";            
        }
        str = str.substr(0, str.length-1);
        this.field.hiddenfield.setValue(str);
	}
});

Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocWin = Ext.extend(Ext.Window,
{     
	m_treePanel: null ,
	m_fileviewPanel: null,
	parent: null,
	
	constructor: function()
    {    
		this.m_treePanel = new Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocTreePanel();        
		this.m_fileviewPanel = new Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocFileViewPanel() ;
		this.m_fileviewPanel.f_treePanel = this.m_treePanel;
		this.m_fileviewPanel.f_mainPanel = this;
		this.m_treePanel.f_fileviewPanel = this.m_fileviewPanel;
        Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocWin.superclass.constructor.call(this,
        {               
            layout:'border', 
            title: '文档库',
            width:700,                                                                    //窗口宽度
            height:500,                                                                   //窗口高度            
            buttonAlign :'center',
            closeAction:'hide', 
            plain: true,    
            modal: true,                                                                  //模式窗口，默认为false
            resizable: false,    
            items: [ this.m_treePanel , this.m_fileviewPanel ],
            buttons : 
            [
                this.saveButton= new Ext.Button
                ({
                    text : '确定',
                    iconCls : 'btn-accept1', 
                    handler : this.window_submit,
                    scope:this
                }), 
                {
                    text : '关闭',
                    iconCls : 'btn-stop1',
                    handler : this.window_close,
                    scope:this
                }
            ]
        });	        
    },
    window_submit:function()
    {
    	var recs = this.m_fileviewPanel.getSelectRecords();
    	if(recs==null) 
    	{
    		Ext.parf.showInfo('请您选择文件');
    		return;
    	}
    	var filesId = '';
    	for(var i=0;i<recs.length;i++)
    	{
    		if(recs[i].get("DOCTYPE")=='1')
    		{
    			Ext.parf.showInfo('您选择了文件夹，请取消文件夹选择！');
    			return;
    		}
    		var fileperm = recs[i].get("FILEPERM");
    		if(!Ext.qwareinfo.oa.pubinfo.document.plugin.checkPermission(fileperm,'Y[YN][YN][YN][YN][YN][YN]Y'))            
            {
    			var filename = recs[i].get("FILENAME");
    			Ext.parf.showInfo('您对选择的文件【'+filename+'】没有复制权限，不能选择该文件！');
    			return;
    		}
    		filesId = filesId + recs[i].get("DOCID") + ","; 
    	}
    	if(filesId.length>0)  filesId = filesId.substring(0, filesId.length-1);
    	var copyUploadOption = 
        { 
            url: Ext.qwareinfo.oa.pubinfo.document.plugin.g_mainurl, 
            params:
            {
                actionName:'copyFilesToUpload',
                filesId: filesId
            }, 
            method: 'post', 
            success: this.copyUploadOption_success,
            scope: this
        };                    
        Ext.Ajax.request(copyUploadOption);    	
    },
    copyUploadOption_success:function(response,opts)
    {    	    	
    	var result = Ext.decode(response.responseText);               
        if(result.success)
        {
        	this.parent.createRecords(result.data.list);
            this.hide();
        }
        else
        {                       
            Ext.parf.showError(result.errorMessage);
        }
    },
    window_close:function()
    {
    	this.hide();
    }
}); 


String.prototype.ellipse = function(maxLength)
{
	var obj=this.match(/[\u4e00-\u9fa5]/g)
    if(obj==null)
    {
        var ss = this.split(" "); 
        if(ss.length==1)
        { 
            if(this.length > maxLength)
            {
                return this.substr(0, (maxLength+3)/2) + '...';
            }
            else
                return this;
        }
        else
        {        	
            if(this.length > 2*(maxLength-3))
            {
                return this.substr(0, maxLength-3) + '...';
            }
        	else
        		return this;
        }
    }
	else
	{
		if(this.length > maxLength)
		{
            return this.substr(0, maxLength-3) + '...';
        }
		else
			return this;
	}        
};
Ext.namespace('Ext.qwareinfo.oa.pubinfo.document.plugin');

Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocTreeToolBar = Ext.extend(Ext.Toolbar,
{
	//成员对象
	refreshButton: null,
	expandButton: null,
	collapseButton: null,
	
	//引用对象
	f_treePanel:null,
	
	constructor: function()
    { 
	    this.refreshButton = new Ext.Button (
	    {            
            iconCls:'btn-refresh',
			text:'刷新',       
            tooltip:'刷新',          
            handler: this.refreshButton_onclick ,  
            scope: this         
	    });
	    
	    this.expandButton = new Ext.Button (
	    {            
            iconCls:'btn-expand',    
            text:'展开',
            tooltip:'展开',          
            handler: this.expandButton_onclick ,  
            scope: this         
	    });
	    
	    this.collapseButton = new Ext.Button (
	    {            
            iconCls:'btn-collapse',
			text:'收起',    
            tooltip:'收起',          
            handler: this.collapseButton_onclick ,  
            scope: this         
	    });
		
		Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocTreeToolBar.superclass.constructor.call(this,
	    {
			buttonAlign :'left',               
            items:[this.refreshButton,'-',this.expandButton,'-',this.collapseButton]       //按钮之间的“|”
        });	    
    },    
    refreshButton_onclick:function()
    {
    	if( this.f_treePanel )
	    { 
	    	this.f_treePanel.root.reload(); 
	    }	
    },
    expandButton_onclick:function()
    {
    	if( this.f_treePanel )
	    { 
	    	this.f_treePanel.expandAll();
	    }
    },
    collapseButton_onclick:function()
    {
    	if( this.f_treePanel )
	    { 
	    	this.f_treePanel.collapseAll();
	    }
    }
});

Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocTreePanel = Ext.extend(Ext.tree.TreePanel,
{   	
	m_treeToolBar:null,
	
	f_fileviewPanel: null,
	
	constructor: function()
    {
    	
    	this.m_treeToolBar = new Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocTreeToolBar() ;    	
		
     	Ext.qwareinfo.oa.pubinfo.document.plugin.GetDocTreePanel.superclass.constructor.call(this,
        {    
			iconCls:'mnu-folder',
			title : '文档目录',
			region: 'west',
			width : 180,
			border:false,
			style : 'border-right: 1px solid #8db2e3;', 
			collapsible : true,
			collapseMode:'mini',			
			collapsed: false,
			split : true,			
			minSize: 150,
       		maxSize: 250,
			autoScroll:true,
			lines : false ,//显示树形控件的前导线
			enableDD : false,//是否允许拖拽
			useArrows: true,
			loader : new Ext.tree.TreeLoader(),
			root : new Ext.tree.AsyncTreeNode({ id:'0', text:'根目录',expanded : true	}),
			rootVisible: Ext.part.UserInfo.isSuperUser,
			tbar:this.m_treeToolBar
		});
		this.m_treeToolBar.f_treePanel = this;
		this.on("beforeload", this.tree_beforeload,this);  //不能用listener
		this.on("click", this.tree_click,this);  
    },
	
	//处理组件事件函数
	tree_beforeload: function(node)
    {        
        var nodeid = node.id; 
		if( isNaN(nodeid) )
		{
			nodeid = 0 ;
		}
		var url = Ext.qwareinfo.oa.pubinfo.document.plugin.g_mainurl +'?actionName=getDictionaryTree&PARENTID=' + nodeid;
		this.loader.dataUrl = url; 
    },
	tree_click: function(node)
    {        
		node.expand(false);
	    this.f_fileviewPanel.openFolder(node.id);		           			
    }
});


Ext.namespace('Ext.qwareinfo.oa.pubinfo.document.plugin');

Ext.qwareinfo.oa.pubinfo.document.plugin.SaveIntoDocPlugin = Ext.extend(Ext.util.Observable, 
{	
	field: null,
	
	constructor: function()
	{
		Ext.qwareinfo.oa.pubinfo.document.plugin.SaveIntoDocPlugin.superclass.constructor.call(this);
	},
	init:function(uploadfield)
	{
		if(uploadfield.uploadType!='upload') return;
		this.field = uploadfield;
		this.field.on('render', this.onRender, this);	
		this.field.on('destroy', this.onDestroy, this);
	},
	onRender: function() 
	{
		var toolbar = this.field.innerCt.getTopToolbar();
		toolbar.addButton(new Ext.Toolbar.Separator());
		this.saveDocBtn = new Ext.Button
		({
			text : '存入文档库',
            iconCls:'mnu-database_save',
            handler : this.saveIntoDoc_onclick,            
            scope : this
		});
		toolbar.addButton(this.saveDocBtn);
	},
	onDestroy: function() 
	{
		if(this.saveDocWin!=null)  Ext.destroy(this.saveDocWin);
	},
	saveIntoDoc_onclick:function(btn)
	{
		if(this.field.dataview.getSelectionCount()==0)
		{
			Ext.parf.showInfo('请选择已上传文件！');
			return;
		}		
		if(this.saveDocWin==null)
		{
			this.saveDocWin = new Ext.qwareinfo.oa.pubinfo.document.plugin.SaveIntoDocWin();
			this.saveDocWin.parent = this;
		}
		this.saveDocWin.show(btn.id);
	},
	getSelectUploadIds:function()
	{
		var recs = this.field.dataview.getSelectedRecords();
		var ids = "";
		for(var i=0;i<recs.length;i++)
		{
			ids = ids + recs[i].get("id") + ","
		}
		if(ids.length>0) ids = ids.substring(0, ids.length-1);
		return ids;
	}
});

Ext.qwareinfo.oa.pubinfo.document.plugin.SaveIntoDocWin = Ext.extend(Ext.Window,
{     
	m_treePanel: null ,	
	parent: null,
	
	constructor: function()
    {    
		this.m_treePanel = new Ext.tree.TreePanel
		({    
			iconCls:'mnu-folder',
			title : '文档目录',
			border:false,			
			autoScroll:true,
			lines : false ,//显示树形控件的前导线
			enableDD : false,//是否允许拖拽
			useArrows: true,
			loader : new Ext.tree.TreeLoader(),
			root : new Ext.tree.AsyncTreeNode({ id:'0', text:'根目录',expanded : true	}),
			rootVisible: false
		});        
        Ext.qwareinfo.oa.pubinfo.document.plugin.SaveIntoDocWin.superclass.constructor.call(this,
        {               
            layout:'fit', 
            title: '文档库',
            width:280,                                                                    //窗口宽度
            height:400,                                                                   //窗口高度            
            buttonAlign :'center',
            closeAction:'hide', 
            plain: true,    
            modal: true,                                                                  //模式窗口，默认为false
            resizable: false,    
            items: [ this.m_treePanel ],
            buttons : 
            [
                this.saveButton= new Ext.Button
                ({
                    text : '确定',
                    iconCls : 'btn-accept1', 
                    handler : this.window_submit,
                    scope:this
                }), 
                {
                    text : '关闭',
                    iconCls : 'btn-stop1',
                    handler : this.window_close,
                    scope:this
                }
            ]
        });
        this.m_treePanel.on("beforeload", this.tree_beforeload,this);  //不能用listener
        this.m_treePanel.on("click", this.tree_click,this);
    },
    tree_beforeload: function(node)
    {        
        var nodeid = node.id; 
		if( isNaN(nodeid) )
		{
			nodeid = 0 ;
		}
		var url = Ext.qwareinfo.oa.pubinfo.document.plugin.g_mainurl +'?actionName=getDictionaryTree&PARENTID=' + nodeid;
		this.m_treePanel.loader.dataUrl = url; 
    },
	tree_click: function(node)
    {        
		node.expand(false);	    		           		
    },
    window_submit:function()
    {
    	var node = this.m_treePanel.getSelectionModel().getSelectedNode();
    	if(node==null)
    	{
    		return;
    	}
    	var copyUploadOption = 
        { 
            url: Ext.qwareinfo.oa.pubinfo.document.plugin.g_mainurl, 
            params:
            {
                actionName:'copyUploadToDoc',
                folderId: node.id,
                uploadIds: this.parent.getSelectUploadIds()
            }, 
            method: 'post', 
            success: this.copyUploadOption_success,
            scope: this
        };                    
        Ext.Ajax.request(copyUploadOption); 
    },
    copyUploadOption_success:function(response,opts)
    {    	    	
    	var result = Ext.decode(response.responseText);               
        if(result.success)
        {
        	Ext.parf.showInfo(result.returnMessage);
        	this.hide();
        }
        else
        {                       
            Ext.parf.showError(result.errorMessage);
        }
    },
    window_close:function()
    {
    	this.hide();
    }
}); 

Ext.namespace('Ext.qwareinfo.oa.pubinfo.document.plugin');

Ext.qwareinfo.oa.pubinfo.document.plugin.loadPic = function(ImgD)
{
	if(ImgD.pic=='1')  
	{
		var image = new Image();
		var iwidth = 90;
		var iheight = 90;
		image.src = ImgD.src;
		ImgD.style.display='none';
		if(image.width>0 && image.height>0)
		{			
            flag=true;
            if(image.width/image.height>= iwidth/iheight)
            {
                if(image.width>iwidth)
                {
                    ImgD.width=iwidth;
                    ImgD.height=(image.height*iwidth)/image.width;
                }
                else
                {
                    ImgD.width=image.width;
                    ImgD.height=image.height;
                }
            }
            else
            {
                if(image.height>iheight)
                {
                    ImgD.height=iheight;
                    ImgD.width=(image.width*iheight)/image.height;
                }
                else
                {
                    ImgD.width=image.width;
                    ImgD.height=image.height;
                }
            }
        }
		ImgD.style.display=''
	}
	else
	{
		ImgD.width=48;
        ImgD.height=48;
	}
};

Ext.qwareinfo.oa.pubinfo.document.plugin.getFileExtension = function(filename) 
{
    var result = null;
    var parts = filename.split(".");
    if (parts.length > 1) {
        result = parts.pop()
    }
    return result;
};


Ext.qwareinfo.oa.pubinfo.document.plugin.checkPermission = function(fileperm,perm)
{
    if(Ext.part.UserInfo.isSuperUser) return true;
    var re = new RegExp("\\S*F\\[1]\\(" + perm + "\\S*");	    
    var b = fileperm.match(re);	    
    if(b) return true;
    
    re = new RegExp("\\S*U\\[" + Ext.part.UserInfo.userId + "]\\(" + perm + "\\S*");
    b = fileperm.match(re);
    if(b) return true;	    
    
    if(Ext.part.UserInfo.orgId)
    {
        re = new RegExp("\\S*O\\[" + Ext.part.UserInfo.orgId + "]\\(" + perm + "\\S*");
        b = fileperm.match(re);
        if(b) return true;
    }
    if(Ext.part.UserInfo.deptId)
    {
        re = new RegExp("\\S*D\\[" + Ext.part.UserInfo.deptId + "]\\(" + perm + "\\S*");
        b = fileperm.match(re);
        if(b) return true;
    }
    if(Ext.part.UserInfo.jobTitleId)
    {
        re = new RegExp("\\S*J\\[" + Ext.part.UserInfo.jobTitleId + "]\\(" + perm + "\\S*");
        b = fileperm.match(re);
        if(b) return true;
    }
    if(Ext.part.UserInfo.workgroupIds)
    {
        var groups = Ext.part.UserInfo.workgroupIds.split(",")
        for(var i=0;i<groups.length;i++)
        {
           if(groups[i]!="")
           {
               re = new RegExp("\\S*W\\[" + groups[i] + "]\\(" + perm + "\\S*");
               b = fileperm.match(re);
               if(b) return true;
           }
        }        
    }
    return false;
};
