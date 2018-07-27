Ext.namespace('Ext.parm.portal.desktop');
Ext.parm.portal.desktop.ContentWindow = Ext.extend(Ext.Window,{           
    parent:null,
    form:null,
    portalTemplateId:null,
    constructor: function(templateId)
    { 
    	this.portalTemplateId = templateId;
	    this.form = new Ext.form.FormPanel
        ({  
        	frame:true,
        	hideLabels: true,
       		layout:'auto',
            items: 
            [  
            	{
                    xtype:'parf-store-checkboxgroup',
                    fieldLabel: '',
                    boldLabel:true,
                    hasAllButton:false,                           //没有全选按钮   
                    storeurl: Ext.parm.portal.desktop.g_mainurl,
                    storeParams:
                    {
                    	actionName: 'getUserAvailablePortletList',
                    	templateId: this.portalTemplateId 
                    } ,
                    name: 'portlets',
                    displayField: 'PORTLETNAME',       
                    valueField: 'PORTLETID', 
                    width:580,
                    groupcfg:
                    {
                        name: 'portlets', 
                        columns: 3,                        
                        allowBlank: false                           //是否允许为空                    
                    }       
                }
            ]         
        });
        Ext.parm.portal.desktop.ContentWindow.superclass.constructor.call(this,
        {
            title: '选择门户内容',
            layout:'fit',                                                                 //样式充满窗口
            width:600,                                                                    //窗口宽度
            height:300,                                                                   //窗口高度
            buttonAlign :'center',
            closeAction:'hide',                                                           //默认窗口隐藏close,hide
            plain: true,    
            modal: true,                                                                  //模式窗口，默认为false
            resizable: false,                                                             //窗口的大小不允许拖动,默认为true
            border:false,        
            items: this.form,
            buttons: 
            [
            	{           
                    text: '确定',         
                    iconCls:'btn-accept1',
                    handler: this.window_submit,
                    scope: this
                },
                {           
                    text: '关闭',
                    iconCls:'btn-stop1',
                    handler: this.window_close,
                    scope: this
                }
            ]
        });
        this.on('show',this.loadFormdata,this);      
    },
    loadFormdata:function()
    {      
    	this.form.getForm().reset();
        var action = Ext.parf.loadAction
        ({
            url:Ext.parm.portal.desktop.g_mainurl, 
            params: { actionName: 'getUserPortletList',templateId: this.portalTemplateId }
        });
        this.form.getForm().load(action);        
    },        
    window_submit:function()
    {
        if(!Ext.parf.checkFormValid(this.form))  return;          
        
        var action = Ext.parf.updateAction
        ({      
            url:Ext.parm.portal.desktop.g_mainurl, 
            params: 
            { 
            	actionName: 'changeUserPortlet',
            	templateId: this.portalTemplateId
            },
            waitTitle:  '提示',
            waitMsg:  '正在更新选择的门户内容，请稍等...',
            success: this.update_success,
            scope: this
        });
        this.form.getForm().submit(action);  
    },
    window_close:function()
    {               
        this.hide();
    },    
    update_success:function(form, action) 
    {                
        this.hide();
        this.parent.refresh_click();        
    }
});

Ext.namespace('Ext.parm.portal.desktop');

Ext.parm.portal.desktop.g_mainurl = '/portal/desktop.do';

Ext.parm.portal.desktop.closePortlet = function(portlet)
{	
	var column = portlet.ownerCt;
    column.remove(portlet, true);
    column.ownerCt.updateUserDesktop();
};

Ext.parm.portal.desktop.DeskTopPanel = Ext.extend(Ext.Panel,
{           
	portal: null,
	portalTemplateId: null,
	parameter: null,
	
    constructor: function(config)
    { 
    	if(config.templateId)
    	{
    		this.portalTemplateId = config.templateId;
    	}
    	if(config.parameter)
    	{
    		this.parameter = config.parameter;
    	}
    	var cfg = 
    	{                                              
            layout:'fit' ,   
            tbar:
            [
               '->',
               {
                   text:'恢复缺省',
                   iconCls:'btn-setting',
                   handler:this.restoreButton_onclick,
                   scope:this
               },               
               '-',
               {
                   text:'选择内容',
                   iconCls:'btn-edit',
                   handler:this.contentButton_onclick,
                   scope:this
               },               
               '-',
               {
                   text:'刷新',
                   iconCls:'btn-refresh',
                   handler:this.refresh_click,
                   scope:this
               }
            ],
            items:
            [ 
            	this.portal = new Ext.parm.portal.desktop.DeskTopPortal(this.portalTemplateId,this.parameter)
            ]
        };
    	cfg = Ext.apply(cfg,config);
        Ext.parm.portal.desktop.DeskTopPanel.superclass.constructor.call(this,cfg);       
    },
    contentButton_onclick:function(btn)
    {                    
        if(!this.contentWindow)
        {
           this.contentWindow = new Ext.parm.portal.desktop.ContentWindow(this.portalTemplateId);
           this.contentWindow.parent = this;
        }
        this.contentWindow.show(btn.id);
    },
    restoreButton_onclick:function()
    {
    	var Options = 
	    { 
	        url: Ext.parm.portal.desktop.g_mainurl, 
	        params:
	        {
	            actionName:'deleteUserPortal',
	            templateId: this.portalTemplateId 
	        }, 	        
	        method: 'post',
	        success : this.restoreOption_success,
			scope : this
	    };
	    Ext.fjs.ajax(Options);
    },
    restoreOption_success: function(response,options)
	{
		var result = Ext.decode(response.responseText);		
		if(result.success)
		{
			this.portal.removeAll();
	    	this.portal.initPortal();
		}
	},
    refresh_click:function()
    {
    	this.portal.removeAll();
    	this.portal.initPortal();
    },
    beforeDestroy:function()
    {
    	this.portal.removeAll();
    	if(this.contentWindow)  Ext.destroy(this.contentWindow);      	
    }
});

Ext.parm.portal.desktop.DeskTopPortal = Ext.extend(Ext.parf.portal.Portal,
{       
	allowdrag:true,
	portalTemplateId: null,
	parameter : null,
	
    constructor: function(templateId,parameter)
    { 
    	this.portalTemplateId = templateId;
        this.parameter = parameter;
    	Ext.parm.portal.desktop.DeskTopPortal.superclass.constructor.call(this,
        {    		    		
    		autoScroll:true,   
    		border:false,
		    listeners: 
		    {       
		        dragover: 
		        {
        	       fn:this.evt_dragover,
        	       scope:this
		        },
		        beforedrop: 
		        {
        	       fn:this.evt_beforedrop,
        	       scope:this
		        },
		        drop: 
		        {
		        	fn:this.evt_drop,
		        	scope:this
		        }
		    }
        });
    	this.initPortal();        
    },  
    initPortal:function()
    {
    	if(this.portalTemplateId)
    	{
            Ext.fjs.ajax
            ({   
                method: 'POST',   
                disableCaching: false,   
                params: 
                { 
                	actionName: 'getUserPortalInfo',
                	templateId: this.portalTemplateId
                },
                url: Ext.parm.portal.desktop.g_mainurl,
                success:this.init_success,
                scope: this
            });    		
    	}
    },
    init_success:function(res){
        var resText = "var columnlist=" + res.responseText;
        eval(resText);
        for(var i=0; i<columnlist.length; i++){
        	var column = new Ext.parf.portal.PortalColumn
		    ({
		        columnWidth: this.getWidthByType(columnlist[i].COLUMNTYPE),		        
		        columnType: columnlist[i].COLUMNTYPE,
		        style:'padding:5px 5px 5px 5px;'     
		    });    
        	this.add(column);
        	var portletlist = columnlist[i].COLPORTLETS;
        	for(var j=0;j<portletlist.length;j++)
        	{
        		var ptl = null;
        		
        		var paraObj = {};
        		if(portletlist[j].PORTLETPARAMETER!='')
        		{
        			paraObj = Ext.decode(portletlist[j].PORTLETPARAMETER);
        		}
        		paraObj = Ext.apply(paraObj, this.parameter);        		
        		if(portletlist[j].ICONNAME)
        		{        			
        			paraObj.portletIcon = 'mnu-'+ portletlist[j].ICONNAME ;	        			
        		}        		        		
        		
        		var	text = " ptl = new " + portletlist[j].PORTLETCLASS+"("+Ext.encode(paraObj)+");";        			        		
        		try
        		{
        			eval(text);
        		}
        		catch(e)
        		{
        			alert('无法找到类' + portletlist[j].PORTLETCLASS +'!');
        			throw e;
        			return;
        		}        		
        		ptl.portletId = portletlist[j].PORTLETID;
        		ptl.portletName = portletlist[j].PORTLETNAME;				
				ptl.columnType = portletlist[j].COLUMNTYPE;
								
				ptl.templateId = this.portalTemplateId;				
        		column.add(ptl);        	   
        	}        	
        }
        this.doLayout();
        (function()
        {
        	Ext.fjs.module.tabPanel.doLayout();	
        }).defer(100);
        
    },
    getWidthByType: function(typestr)
    {
    	var width = 1;
		if(typestr=='T1W') width = (1/3).toFixed(2);
		if(typestr=='T2W') width = (2/3).toFixed(2); 
		if(typestr=='T3W') width = (1/2).toFixed(2); 
		if(typestr=='T4W') width = (1/4).toFixed(2); 
		if(typestr=='T5W') width = (3/4).toFixed(2);
		return width;
    },
    updateUserDesktop:function()
    {
    	var portArray = [];
    	for(var i=0;i<this.items.length;i++)
    	{    		    		
    		var col = this.items.itemAt(i);    		
    		var portletstr = '';
    		for(var j=0;j<col.items.length;j++)
    		{
    			var portlet = col.items.itemAt(j);
    			portletstr = portletstr + portlet.portletId + ',' ;    			
    		}
    		portletstr = portletstr.substr(0, portletstr.length-1);
    		portArray.push({
    			COLINDEX: i,
    			COLUMNTYPE: col.columnType,
    			COLPORTLETS: portletstr
    		});
    	}
    	
	    var Options = 
	    { 
	        url: Ext.parm.portal.desktop.g_mainurl, 
	        params:
	        {
	            actionName:'updateUserPortal',
	            portletsInfo: Ext.encode(portArray),
	            templateId: this.portalTemplateId 
	        }, 
	        method: 'post'
	    };
	    Ext.fjs.ajax(Options);
    },
	evt_drop:function(e)
	{
		this.updateUserDesktop();
	},	
	evt_dragover:function(e)
	{
		if( e.panel.columnType.indexOf(e.column.columnType)==-1)
	        this.allowdrag = false; 
	},	
	evt_beforedrop:function(e)
	{
	    if(!this.allowdrag)
	    {
	        this.allowdrag = true;
	        Ext.MessageBox.show
	        ({
	           title: Ext.parf.common_msgTitle,
	           msg: "信息块列类型与目标位置列类型不同，无法移动到目标位置 ",
	           buttons: Ext.MessageBox.OK,   
	           icon: Ext.MessageBox.ERROR
	        });
	        return false;
	    }
	    return true;
	}
});
