Ext.namespace('Ext.parm.altermsg');
Ext.parm.altermsg.AlterMsgManagePanel = Ext.extend(Ext.Panel,{
	constructor : function() 
	{
		this.dataView = new Ext.parm.altermsg.AlterMsgDataView(2);
		this.queryType = "1";
		
		Ext.parm.altermsg.AlterMsgManagePanel.superclass.constructor.call(this,
		{
			title : '提前提醒设置',
			autoScroll : true,
			items:[this.dataView],
			bbar : Ext.parf.createPageToolBar({ store : this.dataView.store}),
			tbar : 
			[{
				text:'我设置的提醒',
				enableToggle : true,
				toggleGroup : 'queryType',
				queryType : "1",
				pressed : true,
				handler : this.queryType_onclick,
				scope:this
			},'-',{
				text:'提醒我的设置',
				enableToggle : true,
				toggleGroup : 'queryType',
				queryType : "2",
				handler : this.queryType_onclick,
				scope:this
			},'-',{
				text:'所有提醒设置',
				enableToggle : true,
				toggleGroup : 'queryType',
				queryType : "3",
				handler : this.queryType_onclick,
				scope:this
			},'->',{
				text : '刷新',
				iconCls : 'btn-refresh',
				handler : this.refresh_onclick,
				scope:this
			}]
		});
		
		this.dataView.store.on('beforeload',function()
		{
			if(this.loadMask == null)
			{
				this.loadMask = new Ext.LoadMask(this.bwrap,{store:this.dataView.store});
			}
			this.loadMask.show();
		},this)
		
		this.dataView.store.on('load',function()
		{
			if(this.loadMask)this.loadMask.hide();
		},this)
		
		this.on('afterrender',this.loadData,this);
	},
	loadData : function()
	{
		var storeParas = {};
		
		if(this.queryType != null) storeParas.QUERYTYPE = this.queryType;
		
		this.dataView.store.baseParams = storeParas;
		this.dataView.store.load({params:{start:0,limit:20}});
	},
	queryType_onclick : function(btn)
	{
		this.queryType = btn.queryType;
		this.loadData();
	},
	refresh_onclick : function()
	{
		this.getBottomToolbar().doRefresh();
	},
	beforeDestroy:function()
	{
		if(this.loadMask)Ext.destroy(this.loadMask);
		Ext.parm.altermsg.AlterMsgManagePanel.superclass.beforeDestroy.call(this);
	}
});
Ext.namespace('Ext.parm.altermsg');

Ext.parm.altermsg.AlterMsgSettingWindow = Ext.extend(Ext.Window,
{
	constructor : function() 
	{
		this.alterMsgPanel = new Ext.parm.altermsg.AlterMsgSettingPanel();
		
		Ext.parm.altermsg.AlterMsgSettingWindow.superclass.constructor.call(this,
		{
			title : '新增',
			width : 680,
			closable : true, 
			buttonAlign  : 'center',
			closeAction : 'hide',
			autoHeight : true,
			border : false,
			modal : true,
			items : [this.alterMsgPanel],
			buttons : 
			[{
				text : '确定', 
				iconCls : 'btn-accept1', 
				handler : this.submitButton_onclick,
				scope : this
			},{
				text : '取消', 
				iconCls : 'btn-stop1', 
				handler : this.cancleButton_onclick,
				scope : this
			}]
		});
		
		this.addEvents('submit');
		this.on('show',this.evt_show,this);
	},
	init4Update : function(id)
	{
		this.alterMsgPanel.init4Update(id);
		this.setTitle("修改");
	},
	init4Add : function(dataId)
	{
		this.alterMsgPanel.init4Add(dataId);
		this.setTitle("新增");
	},
	evt_show : function()
	{	
		this.alterMsgPanel.loadData();
	},
	submitButton_onclick : function()
	{
		if(!this.alterMsgPanel.validateAlterForm())return;
		
		var jsonStr = this.alterMsgPanel.getAlterMsgSetting();
		var formType = this.alterMsgPanel.formType;
		
		if(formType == "add")
		{
			Ext.parf.Ajax.request
			({
				url : '/parm/altermsg.json',
				params:
				{ 
					actionName : 'insertAlterMsg', 
					DATAID : this.alterMsgPanel.dataId, 
					MODULEID: this.alterMsgPanel.moduleId,
					ALTERMSGJSON : jsonStr
				},
				disableCaching : true,
				success : this.submit_success,
				scope: this
			});
		}
		else if(formType == "update")
		{
			Ext.parf.Ajax.request
			({
				url : '/parm/altermsg.json',
				params:
				{ 
					actionName : 'updateAlterMsg', 
					ID : this.alterMsgPanel.alterMsgId, 
					ALTERMSGJSON : jsonStr
				},
				disableCaching : true,
				success : this.submit_success,
				scope: this
			});
		}
	},
	submit_success : function(response, option)
	{
		var res = Ext.decode(response.responseText);
		if(res.success)
		{
			this.fireEvent('submit');
			Ext.parf.showInfo(res.returnMessage);
			this.hide();
		}
		else
		{
			Ext.parf.showInfo(res.errorMessage);
		}
	},
	cancleButton_onclick : function()
	{
		this.hide();
	}
});
Ext.namespace('Ext.parm.altermsg');

Ext.parm.altermsg.AlterMsgWindow = Ext.extend(Ext.Window,
{
	constructor : function() 
	{
		this.alterMsgPanel = new Ext.parm.altermsg.AlterMsgPanel();
		
		Ext.parm.altermsg.AlterMsgWindow.superclass.constructor.call(this,
		{
			iconCls : 'ico-altermsg',
			title : '提前提醒设置',
			width : 680,
			closable : true, 
			buttonAlign  : 'center',
			closeAction : 'hide',
			autoHeight : true,
			border : false,
			modal : true,
			items : [this.alterMsgPanel],
			buttons : 
			[{
				text : '关闭', 
				iconCls : 'btn-stop1', 
				handler : this.closeBtn_onclick,
				scope : this
			}]
		});
		
		this.on('show',this.evt_show,this);
	},
	init : function(conf)
	{
		Ext.apply(this.alterMsgPanel,conf);
	},
	setDataId : function(dataId)
	{
		this.alterMsgPanel.dataId = dataId;
		this.needLoad = true;
	},
	evt_show : function()
	{	
		this.alterMsgPanel.loadData();
	},
	closeBtn_onclick : function()
	{
		this.hide();
	}
});

Ext.parm.altermsg.AlterMsgPanel = Ext.extend(Ext.Panel,
{
	constructor : function() 
	{
		this.dataView = new Ext.parm.altermsg.AlterMsgDataView();
		this.queryType = "1";
		
		Ext.parm.altermsg.AlterMsgPanel.superclass.constructor.call(this,
		{
			autoScroll : true,
			items:[this.dataView],
			bbar : Ext.parf.createPageToolBar({ store : this.dataView.store}),
			height : 400,
			tbar : 
			[{
				text:'我设置的提醒',
				enableToggle : true,
				toggleGroup : 'queryType',
				queryType : "1",
				pressed : true,
				handler : this.queryType_onclick,
				scope:this
			},'-',{
				text:'提醒我的设置',
				enableToggle : true,
				toggleGroup : 'queryType',
				queryType : "2",
				handler : this.queryType_onclick,
				scope:this
			},'-',{
				text:'所有提醒设置',
				enableToggle : true,
				toggleGroup : 'queryType',
				queryType : "3",
				handler : this.queryType_onclick,
				scope:this
			},'->',{
				text : '新增设置',
				iconCls : 'btn-add',
				handler : this.addBtn_onclick,
				scope:this
			},'-',{
				text : '刷新',
				iconCls : 'btn-refresh',
				handler : this.refresh_onclick,
				scope:this
			}]
		});
		
		this.dataView.store.on('beforeload',function()
		{
			if(this.loadMask == null)
			{
				this.loadMask = new Ext.LoadMask(this.bwrap,{store:this.dataView.store});
			}
			this.loadMask.show();
		},this)
		
		this.dataView.store.on('load',function()
		{
			if(this.loadMask)this.loadMask.hide();
		},this)
		
	},
	loadData : function()
	{
		var storeParas = {};
		if(this.dataId != null) storeParas.DATAID = this.dataId;
		if(this.moduleId != null) storeParas.MODULEID = this.moduleId;
		if(this.queryType != null) storeParas.QUERYTYPE = this.queryType;
		
		this.dataView.store.baseParams = storeParas;
		this.dataView.store.load({params:{start:0,limit:20}});
	},
	queryType_onclick : function(btn)
	{
		this.queryType = btn.queryType;
		this.loadData();
	},
	addBtn_onclick : function()
	{
		var conf = {};
		conf.dataId = this.dataId;
		conf.moduleId = this.moduleId;
		conf.titleField = this.titleField;
		conf.refTimeField = this.refTimeField;
		conf.sendeeRangeField = this.sendeeRangeField;
		
		this.dataView.addAlterMsg(conf);
	},
	refresh_onclick : function()
	{
		this.getBottomToolbar().doRefresh();
	},
	beforeDestroy:function()
	{
		if(this.loadMask)Ext.destroy(this.loadMask);
		Ext.parm.altermsg.AlterMsgPanel.superclass.beforeDestroy.call(this);
	}
});

Ext.parm.altermsg.AlterMsgDataView = Ext.extend(Ext.DataView,{
	
	//1 某模块的 2所有的
	type : 1,
	constructor : function(type) 
	{
		this.type = type || 1;
		
		var store = new Ext.parf.data.PageStore
		({
			autoLoad : false,
			url : '/parm/altermsg.json?actionName=getMyAlterMsgPP',
			baseParams : {},
			fields : ['ID','TITLE','USERID','USERNAME','MODULEID','DATAID','REFTIME','ALTERTIME',
					'ISLOOP','SETTING','REFFIELD','SENDEEREF','SENDEE','MSGCONTENT',
					'ALTERTIMES','CREATETIME','MODIFYTIME','LASTALTERTIME','STATE']
		}); 
		
		var tpl_1 = new Ext.XTemplate
		(
			'<tpl for=".">',
				'<div class="view-item" >',
					'<table width="100%">',
						'<tr>',
							'<td>',
								'<table>',
									'<tr>',
										'<td width="20">{ICO}</td>',
										'<td class="subject">设置人：{USERNAME}&nbsp;&nbsp;<span>设置时间：{CREATETIME_STR}</span></td>',
									'</tr>',
									'<tr>',
										'<td></td>',
										'<td class="content">',
											'{STARTTIME_STR}',
											'{INTERVAL_STR}',
											'{SENDEE_STR}',
											'{MSGCONTENT_STR}',
											'{STATE_STR}',
										'</td>',
									'</tr>',
								'</table>',
							'</td>',
							'<td width="80" valign="top">{UpdateFn}&nbsp;&nbsp;{DeleteFn}</td>',
						'</tr>',
					'</table>',
				'</div>',
			'</tpl>',
			'<div class="x-clear"></div>'
		);
		
		var tpl_2 = new Ext.XTemplate
		(
			'<tpl for=".">',
				'<div class="view-item" >',
					'<table width="100%">',
						'<tr>',
							'<td>',
								'<table>',
									'<tr>',
										'<td width="20">{ICO}</td>',
										'<td class="subject" style="font-size:14px;color:#15428B;font-weight: bold;">{TITLE}</td>',
									'</tr>',
									'<tr>',
										'<td></td>',
										'<td class="subject">设置人：{USERNAME}&nbsp;&nbsp;<span>设置时间：{CREATETIME_STR}</span></td>',
									'</tr>',
									'<tr>',
										'<td></td>',
										'<td class="content">',
											'{STARTTIME_STR}',
											'{INTERVAL_STR}',
											'{SENDEE_STR}',
											'{MSGCONTENT_STR}',
											'{STATE_STR}',
										'</td>',
									'</tr>',
								'</table>',
							'</td>',
							'<td width="80" valign="top">{UpdateFn}&nbsp;&nbsp;{DeleteFn}</td>',
						'</tr>',
					'</table>',
				'</div>',
			'</tpl>',
			'<div class="x-clear"></div>'
		);
		
		Ext.parm.altermsg.AlterMsgDataView.superclass.constructor.call(this,
		{
			store : store,
			tpl : this.type == 1 ? tpl_1 : tpl_2,
			cls : 'parm-altermsg',
			style : 'overflow:auto',
			itemSelector : 'div.view-item',
			emptyText : '暂无'
		});
	},
	prepareData : function(data,num,record)
	{ 
		//console.log(data);
		data.ICO = "<img src=\"/parm/altermsg/images/clock_"+ data.ISLOOP +".png\" width=\"16\" height=\"16\"/>";
		data.CREATETIME_STR = new Date(data.CREATETIME.time).format('Y-m-d H:i');
		
		if(data.SETTING.STARTTIME.TYPE == 1)
		{
			data.STARTTIME_STR = "";
			if(data.SETTING.STARTTIME.S_AHEADTIME_DAY > 0)data.STARTTIME_STR += data.SETTING.STARTTIME.S_AHEADTIME_DAY + "天";
			if(data.SETTING.STARTTIME.S_AHEADTIME_HOUR > 0)data.STARTTIME_STR += data.SETTING.STARTTIME.S_AHEADTIME_HOUR + "小时";
			if(data.SETTING.STARTTIME.S_AHEADTIME_MI > 0)data.STARTTIME_STR += data.SETTING.STARTTIME.S_AHEADTIME_MI + "分钟";
			if(data.STARTTIME_STR == "") data.STARTTIME_STR = "0分钟";
			data.STARTTIME_STR = "提醒时间：提前" + data.STARTTIME_STR;
		}
		else
		{
			data.STARTTIME_STR = "提醒时间：指定于" + data.SETTING.STARTTIME.S_FIXEDTIME;
		}
		data.STARTTIME_STR += "<br/>"
		
		data.INTERVAL_STR = "";
		if(data.ISLOOP == 1)
		{
			data.INTERVAL_STR = "提醒频率：间隔";
			if(data.SETTING.INTERVAL.INTERVAL_DAY > 0)data.INTERVAL_STR += data.SETTING.INTERVAL.INTERVAL_DAY + "天";
			if(data.SETTING.INTERVAL.INTERVAL_HOUR > 0)data.INTERVAL_STR += data.SETTING.INTERVAL.INTERVAL_HOUR + "小时";
			if(data.SETTING.INTERVAL.INTERVAL_MI > 0)data.INTERVAL_STR += data.SETTING.INTERVAL.INTERVAL_MI + "分钟";
			if(data.SETTING.INTERVAL.INTERVAL_TIMES > 0)
			{
				data.INTERVAL_STR += "&nbsp;共需提醒" + data.SETTING.INTERVAL.INTERVAL_TIMES + "次"; 
			}
			else
			{
				data.INTERVAL_STR += "&nbsp;提醒次数不限"; 
			}
			data.INTERVAL_STR += "&nbsp;已提醒" + data.ALTERTIMES + "次";
			if(data.LASTALTERTIME != null)
			{
				data.INTERVAL_STR += "&nbsp;上次提醒时间：" + new Date(data.LASTALTERTIME.time).format('Y-m-d H:i');
			}
			data.INTERVAL_STR +="</br>";
		}
		
		data.SENDEE_STR = "提醒人：" + new Ext.part.form.userselection.ValueObjArray(data.SENDEE).getDisplayText()+"</br>";
		
		data.MSGCONTENT_STR = "";
		if(!Ext.isEmpty(data.MSGCONTENT))
		{
			data.MSGCONTENT_STR = "提醒内容：" + data.MSGCONTENT + "<br/>";
		}
		data.STATE_STR = "状态：";
		var alterTimeStr = "";
		if(data.ALTERTIME && data.ALTERTIME.time)
		{
			alterTimeStr = new Date(data.ALTERTIME.time).format('Y-m-d H:i');
		}
		if(data.STATE == 0 || data.STATE == 2)
		{
			data.STATE_STR += "<font color=\"#DDAA00\">等待中</font>，提醒时间【"+ alterTimeStr +"】";
		}
		else if(data.STATE == 1)
		{
			data.STATE_STR += "<font color=\"#008800\">已提醒</font>，提醒时间【"+ alterTimeStr +"】";
		}
		else if(data.STATE == 3)
		{
			data.STATE_STR += "<font color=\"#008800\">已提醒</font>，提醒时间【"+ alterTimeStr +"】，不再提醒";
		}
		else if(data.STATE == 4 || data.STATE == 6)
		{
			data.STATE_STR += "<font color=\"#CC0000\">提醒失败</font>";
		}
		else if(data.STATE == 5)
		{
			data.STATE_STR += "<font color=\"#CC0000\">已过期</font>";
		}
		data.STATE_STR += "<br>";
		if(data.USERID == Ext.part.UserInfo.userId)
		{
			data.UpdateFn = "<a href=\"javascript:void(0);\" onclick=\"Ext.parf.reflect.ExtCmp('"+ this.id +"','updateAlterMsg','"+data.ID+"');\">修改</a>";
			data.DeleteFn = "<a href=\"javascript:void(0);\" onclick=\"Ext.parf.reflect.ExtCmp('"+ this.id +"','deleteAlterMsg','"+data.ID+"');\">删除</a>";
		}
		else
		{
			data.UpdateFn = "";
			data.DeleteFn = "";
		}
		return data;
	},
	addAlterMsg : function(conf)
	{
		if(this.AlterSettingWin == null)
		{
			this.AlterSettingWin  = new Ext.parm.altermsg.AlterMsgSettingWindow();
			this.AlterSettingWin.on('submit',function()
			{
				this.store.load({params:{start:0,limit:20}});
			},this);
		}
		this.AlterSettingWin.init4Add(conf);
		this.AlterSettingWin.show();
	},
	updateAlterMsg : function(id)
	{
		if(this.AlterSettingWin == null)
		{
			this.AlterSettingWin  = new Ext.parm.altermsg.AlterMsgSettingWindow();
			this.AlterSettingWin.on('submit',function()
			{
				this.store.load({params:{start:0,limit:20}});
			},this);
		}
		this.AlterSettingWin.init4Update(id);
		this.AlterSettingWin.show();
	},
	deleteAlterMsg : function(id)
	{
		Ext.Msg.confirm(Ext.parf.delete_confirmTitle,"您确定要删除该提醒设置吗？", function(btn)
		{
			if(btn != 'yes') return;
			
			Ext.parf.Ajax.request
			({
				url : '/parm/altermsg.json',
				params : {actionName : 'deleteAlterMsg', ID : id},
				success : this.deleteAlterMsg_success,
				scope : this
			});
		}, this);
	},
	deleteAlterMsg_success : function(response,options)
	{
		var result = Ext.decode(response.responseText);
		
		if(result.success)
		{
			Ext.parf.showInfo(result.returnMessage);
			this.store.reload();
		}
		else
		{
			Ext.parf.showInfo(result.errorMessage);
		}
	},
	beforeDestroy:function()
	{
		if(this.AlterSettingWin)Ext.destroy(this.AlterSettingWin);
		Ext.parm.altermsg.AlterMsgDataView.superclass.beforeDestroy.call(this);
	}
})
Ext.namespace('Ext.parm.altermsg');

Ext.parm.altermsg.SettingPanel = Ext.extend(Ext.Panel,{
	
	constructor: function()
	{
		var panelId  = Ext.id();
		this.formPanel = new Ext.form.FormPanel
		({
			layout : 'htmlform',
			width : 800,
			layoutConfig :
			{
				ejstemplate : '/parm/altermsg/from/setting.ejs',
				ejsdata : {
					panelId : panelId,
					sms_open :  '1' == Ext.part.SysParameter.SMS_OPEN
				}
			},
			items : 
			[
				this.titleLableDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'TITLE_LABLE_DIS'
				}),
				this.titleDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'TITLE_DIS'
				}),
				this.refTimeLableDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'REFTIME_LABLE_DIS'
				}),
				this.refTimeDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'REFTIME_DIS'
				}),
				this.needAlter = new Ext.form.RadioGroup
				({
					hideLabel : true,
					name : 'NEEDALTER',
					width : 200,
					items : 
					[
						{boxLabel: '需要提醒', name: 'NEEDALTER', inputValue:'1'},
						{boxLabel: '不需要提醒', name: 'NEEDALTER', inputValue:'0',checked:true}
					]
				}),
				this.isLoop = new Ext.parf.form.StoreRadioGroup
				({
					hideLabel : true,
					hiddenName : 'ISLOOP',
					dictId : 'parm.altermsg.loop',
					width : 200,
					value : '0',
					groupcfg :
					{
						name : 'ISLOOP',
						allowBlank : false
					}
				}),
				this.startTimeType = new Ext.form.RadioGroup
				({
					hideLabel : true,
					hiddenName : 'STARTTIME_TYPE',
					width : 150,
					items : 
					[
						{boxLabel: '提前', name: 'STARTTIME_TYPE', inputValue:'1',checked:true},
						{boxLabel: '指定时间', name: 'STARTTIME_TYPE', inputValue:'2'}
					]
				}),
				this.startAheadTimeDay = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'S_AHEADTIME_DAY',
					hiddenName : 'S_AHEADTIME_DAY',
					dictId : 'parm.altermsg.day',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'S_AHEADTIME_DAY'
					}
				}),
				this.startAheadTimeHour = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'S_AHEADTIME_HOUR',
					hiddenName : 'S_AHEADTIME_HOUR',
					dictId : 'parm.altermsg.hour',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'S_AHEADTIME_HOUR'
					}
				}),
				this.startAheadTimeMi = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'S_AHEADTIME_MI',
					hiddenName : 'S_AHEADTIME_MI',
					dictId : 'parm.altermsg.mi',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'S_AHEADTIME_MI'
					}
				}),
				this.startFixedTime = new Ext.parf.form.DateTimeField
				({
					hideLabel : true,
					name : 'S_FIXEDTIME',
					hiddenName : 'S_FIXEDTIME',
					hidden : true
				}),
				this.intervalDay = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_DAY',
					hiddenName : 'INTERVAL_DAY',
					dictId : 'parm.altermsg.day',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_DAY'
					}
				}),
				this.intervalHour = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_HOUR',
					hiddenName : 'INTERVAL_HOUR',
					dictId : 'parm.altermsg.hour',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_HOUR'
					}
				}),
				this.intervalMi = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_MI',
					hiddenName : 'INTERVAL_MI',
					dictId : 'parm.altermsg.mi',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_MI'
					}
				}),
				this.intervalTimes = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_TIMES',
					hiddenName : 'INTERVAL_TIMES',
					dictId : 'parm.altermsg.intervaltimes',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_TIMES'
					}
				}),
				this.sendeeRange = new Ext.form.CheckboxGroup
				({
					hideLabel : true,
					name : 'SENDEE_RANGE',
					items : [],
					width : 500
				}),
				this.sendeeOther = new Ext.part.form.userselection.MultiUserField
				({
					hideLabel : true,
					name : 'SENDEE_OTHER',
					width : 500,
					height : 50,
					allowBlank : true
				}),
				this.sendee = new Ext.part.form.userselection.MultiUserField
				({
					hideLabel : true,
					name : 'SENDEE',
					readOnly : true,
					width : 540,
					height:100
				}),
				this.msgContent = new Ext.form.TextArea
				({
					hideLabel : true,
					name : 'MSGCONTENT',
					width : 500,
					height:150,
					maxLength : 200
				}),
				this.hasSms = new Ext.form.RadioGroup
				({
					hideLabel : true,
					name : 'HASSMS',
					width : 200,
					items : 
					[
						{boxLabel: '否', name: 'HASSMS', inputValue:'0', checked:true},
						{boxLabel: '是', name: 'HASSMS', inputValue:'1'}
					]
				})
			]
		});
		
		Ext.parm.altermsg.SettingPanel.superclass.constructor.call(this,
		{
			id : panelId,
			title : '提醒设置',
			layout : 'parfcenter',
			cls : 'x-panel-mc',
			autoScroll : true,
			items : [ this.formPanel]
		});
		
		this.isLoop.groupbox.on("change",this.evt_isLoop_change,this);
		this.startTimeType.on("change",this.evt_startTimeType_change,this);
		this.sendeeRange.on("change",this.initSendeeByRange,this);
		this.sendeeOther.on("changevalue",this.evt_sendeeOther_change,this);
		this.on('show',this.initValue,this);
		this.on('beforeshow',this.checkRefForm,this);
		this.needAlter.on("change",this.checkRefForm,this);
	},
	evt_isLoop_change : function(rg, rchecked)
	{
		var isLoop = rchecked.inputValue;
		if("0" == isLoop)
		{
			document.getElementById(this.id + "_INTERVAL").style.display = "none";
			
		}else{
			document.getElementById(this.id + "_INTERVAL").style.display = "";
		}
	},
	evt_startTimeType_change : function(rg, rchecked)
	{
		var type = rchecked.inputValue;
		if("1" == type)
		{
			this.startFixedTime.hide();
			this.startAheadTimeDay.show();
			this.startAheadTimeHour.show();
			this.startAheadTimeMi.show();
		}else{
			this.startFixedTime.show();
			this.startFixedTime.datefield.setWidth(100);
			this.startFixedTime.timefield.setWidth(80);
			this.startAheadTimeDay.hide();
			this.startAheadTimeHour.hide();
			this.startAheadTimeMi.hide();
		}
	},
	initRefForm : function(form, refField)
	{
		this.refForm = form;
		this.refField = refField;
		
		if(this.refField.refTimeField.refTimeType == "1")
		{
			this.refField.refTimeFormat = "Y-m-d 00:00";
		}
		else
		{
			this.refField.refTimeFormat = "Y-m-d H:i";
		}
		var sendeeRangeField = this.refField.sendeeRangeField || [];
		var seendRangeItems = [];
		for(var i = 0; i < sendeeRangeField.length; i++)
		{
			seendRangeItems.push
			({
				boxLabel : sendeeRangeField[i].ISCURUSER ? "我" : sendeeRangeField[i].label,
				name : 'SENDEE_RANGE',
				inputValue : sendeeRangeField[i].field,
				isCurUser : sendeeRangeField[i].ISCURUSER ? true : false,
				checked : sendeeRangeField[i].checked ? true : false,
				hiddenLabel : sendeeRangeField[i].label
			});
		}
		
		this.sendeeRange.items = seendRangeItems;
		
		this.titleLableDispaly.setValue(this.refField.titleField.lable + "：");
		this.refTimeLableDispaly.setValue(this.refField.refTimeField.lable + "：");
	},
	initValue : function()
	{
		var title = this.getRefFieldValue(this.refField.titleField.name);
		this.titleDispaly.setValue(title);
		
		var refTime = this.getRefFieldValue(this.refField.refTimeField.name);
		var value = refTime.format ? refTime.format(this.refField.refTimeFormat) : refTime;
		this.refTimeDispaly.setValue(value);
		
		this.initSendeeByRange();
	},
	evt_sendeeOther_change : function()
	{
		this.sendee.setValue("");
		this.initSendeeByRange();
	},
	initSendeeByRange : function()
	{
		var items = this.sendeeRange.items;
		items = items.items || items;
		
		Ext.each(items,function(range)
		{
			var field = range.inputValue;
			var checked = range.checked;
			var isCurUser = range.isCurUser;
			if(checked) return;
			var v = "";
			if(isCurUser)
			{
				v = "U[" + Ext.parf.UserInfo.userId +"]<" + Ext.parf.UserInfo.userName + ">";
			}
			else if(field == "OTHER")
			{
				v = this.sendeeOther.getValue();
				this.sendeeOther.setVisible(checked);
				this.sendeeOther.setValue("");
			}
			else
			{
				v = this.getRefFieldValue(field)
			}
			this.sendee.removeUser(v);
		},this);
		
		Ext.each(items,function(range)
		{
			var field = range.inputValue;
			var checked = range.checked;
			var isCurUser = range.isCurUser;
			if(!checked) return;
			var v = "";
			if(isCurUser)
			{
				v = "U[" + Ext.parf.UserInfo.userId +"]<" + Ext.parf.UserInfo.userName + ">";
			}
			else if(field == "OTHER")
			{
				v = this.sendeeOther.getValue();
				this.sendeeOther.setVisible(checked);
			}
			else
			{
				v = this.getRefFieldValue(field)
			}
			this.sendee.pushUser(v);
		},this);
	},
	getRefFieldValue : function(field)
	{
		if(this.refForm != null)
		{
			return this.refForm.getForm().findField(field) ? this.refForm.getForm().findField(field).getValue() : "";
		}
		return "";
	},
	checkRefForm : function()
	{
		this.initSettingForm();
		
		this.refTime = "";
		if(this.needAlter.getValue() == null || this.needAlter.getValue().inputValue == "0")
		{
			return true;
		}
		
		var refTime = this.getRefFieldValue(this.refField.refTimeField.name);
		
		if(Ext.isEmpty(refTime))
		{
			Ext.parf.showInfo("请先输入" + this.refField.refTimeField.lable);
			this.needAlter.setValue('0');
		}
		
		this.refTime = refTime;
		
		return true;
	},
	initSettingForm : function()
	{
		if(this.needAlter.getValue() == null)return;
		var showSetting = "";
		if(this.needAlter.getValue().inputValue == "0")
		{
			showSetting = "none";
		}
		
		var eles = document.getElementsByName(this.id + "_SETTING");
		Ext.each(eles,function(el){
			el.style.display = showSetting;
		},this);
	},
	validateAlterForm : function()
	{
		if(this.needAlter.getValue() == null || this.needAlter.getValue().inputValue == "0") return true;
		
		if(!this.isVisible())
		{
			this.initValue();
		}
		
		var refTime = new Date(Date.parse(this.refTimeDispaly.getValue().replace(/-/g, "/")));
		var curTime = new Date();
		
		////console.log("refTime===:" + refTime.format("Y-m-d H:i"));
		////console.log("refTime > curTime===:" + (refTime > curTime));
		if(refTime < curTime)
		{
			Ext.parf.showInfo(this.refTimeLableDispaly.getValue() + this.refTimeDispaly.getValue()+ "早于当前时间，不能设置提前提醒！");
			return false;
		}
		
		var startTimeType = parseInt(this.startTimeType.getValue().inputValue);
		var startTime = null;
		if(startTimeType == 2)
		{
			var fixedTime = "";
			try
			{
				fixedTime = this.startFixedTime.getValue();
			}
			catch(e)
			{}
			
			if(Ext.isEmpty(fixedTime))
			{
				Ext.parf.showInfo("请输入设置正确的提醒时间！");
				return false;
				////console.log("开始时间错误！");
			}
			startTime = new Date(Date.parse(fixedTime.replace(/-/g, "/")));
			////console.log("startTime=2==:" + startTime.format("Y-m-d H:i"));
		}
		else if(startTimeType == 1)
		{
			var S_AHEADTIME_DAY = parseInt(this.startAheadTimeDay.getValue());
			var S_AHEADTIME_HOUR = parseInt(this.startAheadTimeHour.getValue());
			var S_AHEADTIME_MI = parseInt(this.startAheadTimeMi.getValue());
			startTime = new Date(Date.parse(this.refTimeDispaly.getValue().replace(/-/g, "/")));
			startTime.setDate(startTime.getDate() - S_AHEADTIME_DAY);
			startTime.setHours(startTime.getHours() - S_AHEADTIME_HOUR);
			startTime.setMinutes(startTime.getMinutes() - S_AHEADTIME_MI);
			////console.log("startTime=1==:" + startTime.format("Y-m-d H:i"));
		}
		if(startTime < curTime)
		{
			Ext.parf.showInfo("提醒时间早于当前时间，请设置正确的提醒时间！");
			return false;
		}
		
		var isLoop = parseInt(this.isLoop.groupbox.getValue().inputValue);
		if(isLoop == 1)
		{
			var INTERVAL_DAY = parseInt(this.intervalDay.getValue());
			var INTERVAL_HOUR = parseInt(this.intervalHour.getValue());
			var INTERVAL_MI = parseInt(this.intervalMi.getValue());
			if(INTERVAL_MI == 0 && INTERVAL_HOUR == 0 && INTERVAL_DAY == 0)
			{
				Ext.parf.showInfo("周期提醒的时间间隔不能为0，请重新设置！");
				return false;
				////console.log("重复时间错误！");
			}
		}
		
		var sendee = this.sendee.getValue().getHiddenValue();
		if(Ext.isEmpty(sendee))
		{
			Ext.parf.showInfo("请设置提醒接收人！");
			return false;
		}
		return true;
	},
	getAlterMsgSetting : function()
	{
		if(this.needAlter.getValue() == null || this.needAlter.getValue().inputValue == "0") return "";
		
		var alterMsg = {};
		
		var refField = {};
		
		refField.TITLEFIELD = {
			LABLE : this.refField.titleField.lable,
			NAME : this.refField.titleField.name
		}
		refField.REFTIMEFIELD = {
			LABLE : this.refField.refTimeField.lable,
			NAME : this.refField.refTimeField.name,
			TYPE : this.refField.refTimeField.refTimeType
		}
		alterMsg.REFFIELD = refField;
		
		var sendeeRef = {};
		var sendeeRange = [];
		var items = this.sendeeRange.items;
		items = items.items || items;
		Ext.each(items,function(range)
		{
			var label = range.hiddenLabel;
			var field = range.inputValue;
			var checked = range.checked;
			var isCurUser = range.isCurUser;
			
			if(isCurUser && checked)
			{
				sendeeRef.USER  = "U[" + Ext.parf.UserInfo.userId +"]<" + Ext.parf.UserInfo.userName + ">";
			}
			if(field == "OTHER" && checked)
			{
				sendeeRef.OTHER = this.sendeeOther.getValue().getHiddenValue();
			}
			sendeeRange.push
			({
				LABEL : label,
				FIELD : field,
				CHECKED : checked,
				ISCURUSER : isCurUser
			})
		},this);
		
		sendeeRef.SENDEERANGE = sendeeRange;
		alterMsg.SENDEEREF = sendeeRef;
		
		alterMsg.SENDEE = this.sendee.getValue().getHiddenValue();
		alterMsg.MSGCONTENT = this.msgContent.getValue();
		
		alterMsg.ISLOOP = parseInt(this.isLoop.groupbox.getValue().inputValue);
		
		var setting = {};
		
		var startTime = {};
		startTime.TYPE = parseInt(this.startTimeType.getValue().inputValue);
		if(startTime.TYPE == 1)
		{
			startTime.S_AHEADTIME_DAY = parseInt(this.startAheadTimeDay.getValue());
			startTime.S_AHEADTIME_HOUR = parseInt(this.startAheadTimeHour.getValue());
			startTime.S_AHEADTIME_MI = parseInt(this.startAheadTimeMi.getValue());
		}
		else
		{
			startTime.S_FIXEDTIME = this.startFixedTime.getValue();
		}
		
		setting.STARTTIME = startTime;		
		if(alterMsg.ISLOOP == 1)
		{
			var interval = {};
			interval.INTERVAL_DAY = parseInt(this.intervalDay.getValue());
			interval.INTERVAL_HOUR = parseInt(this.intervalHour.getValue());
			interval.INTERVAL_MI = parseInt(this.intervalMi.getValue());
			interval.INTERVAL_TIMES = parseInt(this.intervalTimes.getValue());
			setting.INTERVAL = interval;
		}
		
		setting.HASSMS = this.hasSms.getValue().inputValue;
		alterMsg.SETTING = setting;
		
		alterMsg.USERID = Ext.parf.UserInfo.userId;
		alterMsg.REFTIME = this.refTimeDispaly.getValue();
		
		alterMsg.TITLE = this.titleLableDispaly.getValue() + this.titleDispaly.getValue();
		alterMsg.TITLE += "&nbsp;&nbsp;&nbsp;&nbsp;";
		alterMsg.TITLE += this.refTimeLableDispaly.getValue() + this.refTimeDispaly.getValue();
		
		//console.log("====alterMsg====");
		//console.log(alterMsg);
		//console.log("====alterMsg====");
		return Ext.encode(alterMsg);
	}
})
Ext.namespace('Ext.parm.altermsg');

Ext.parm.altermsg.AlterMsgSettingPanel = Ext.extend(Ext.Panel,{
	
	constructor: function()
	{
		var panelId  = Ext.id();
		
		this.formPanel = new Ext.form.FormPanel
		({
			layout : 'html',
			layoutConfig :
			{
				ejstemplate : '/parm/altermsg/from/setting2.ejs',
				ejsdata : 
				{
					panelId : panelId,
					sms_open :  '1' == Ext.part.SysParameter.SMS_OPEN
				},
				pagetop :  0,
				pageleft : 0,
				pageright : 0,
				pagebottom : 0
			},
			items : 
			[
				this.titleLableDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'TITLE_LABLE_DIS'
				}),
				this.titleDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'TITLE_DIS'
				}),
				this.refTimeLableDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'REFTIME_LABLE_DIS'
				}),
				this.refTimeDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'REFTIME_DIS'
				}),
				this.isLoop = new Ext.parf.form.StoreRadioGroup
				({
					hideLabel : true,
					name : 'ISLOOP',
					hiddenName : 'ISLOOP',
					dictId : 'parm.altermsg.loop',
					value : '0',
					width : 200,
					groupcfg :
					{
						name : 'ISLOOP',
						allowBlank : false
					}
				}),
				this.startTimeType = new Ext.form.RadioGroup
				({
					hideLabel : true,
					hiddenName : 'STARTTIME_TYPE',
					width : 150,
					items : 
					[
						{boxLabel: '提前', name: 'STARTTIME_TYPE', inputValue:'1',checked:true},
						{boxLabel: '指定时间', name: 'STARTTIME_TYPE', inputValue:'2'}
					]
				}),
				this.startAheadTimeDay = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'S_AHEADTIME_DAY',
					hiddenName : 'S_AHEADTIME_DAY',
					dictId : 'parm.altermsg.day',
					value : "0",
					allrec : false,
					width : 60
				}),
				this.startAheadTimeHour = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'S_AHEADTIME_HOUR',
					hiddenName : 'S_AHEADTIME_HOUR',
					dictId : 'parm.altermsg.hour',
					value : "0",
					allrec : false,
					width : 60
				}),
				this.startAheadTimeMi = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'S_AHEADTIME_MI',
					hiddenName : 'S_AHEADTIME_MI',
					dictId : 'parm.altermsg.mi',
					value : "0",
					allrec : false,
					width : 60
				}),
				this.startFixedTime = new Ext.parf.form.DateTimeField
				({
					hideLabel : true,
					name : 'S_FIXEDTIME',
					hiddenName : 'S_FIXEDTIME',
					hidden : true
				}),
				this.intervalDay = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_DAY',
					hiddenName : 'INTERVAL_DAY',
					dictId : 'parm.altermsg.day',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_DAY'
					}
				}),
				this.intervalHour = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_HOUR',
					hiddenName : 'INTERVAL_HOUR',
					dictId : 'parm.altermsg.hour',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_HOUR'
					}
				}),
				this.intervalMi = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_MI',
					hiddenName : 'INTERVAL_MI',
					dictId : 'parm.altermsg.mi',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_MI'
					}
				}),
				this.intervalTimes = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_TIMES',
					hiddenName : 'INTERVAL_TIMES',
					dictId : 'parm.altermsg.intervaltimes',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_TIMES'
					}
				}),
				this.sendeeRangePanel = new Ext.Panel
				({
					name : 'SENDEE_RANGE_PANEL',
					border : false,
					width : 500
				}),
				this.sendeeOther = new Ext.part.form.userselection.MultiUserField
				({
					hideLabel : true,
					name : 'SENDEE_OTHER',
					hidden : true,
					width : 500,
					height : 50,
					allowBlank : true
				}),
				this.sendee = new Ext.part.form.userselection.MultiUserField
				({
					hideLabel : true,
					name : 'SENDEE',
					readOnly : true,
					width : 540,
					height:100
				}),
				this.msgContent = new Ext.form.TextArea
				({
					hideLabel : true,
					name : 'MSGCONTENT',
					width : 500,
					height:150,
					maxLength : 200
				}),
				this.hasSms = new Ext.form.RadioGroup
				({
					hideLabel : true,
					name : 'HASSMS',
					width : 200,
					items : 
					[
						{boxLabel: '否', name: 'HASSMS', inputValue:'0', checked:true},
						{boxLabel: '是', name: 'HASSMS', inputValue:'1'}
					]
				})
			]
		});
		
		Ext.parm.altermsg.AlterMsgSettingPanel.superclass.constructor.call(this,
		{
			id : panelId,
			autoScroll : true,
			border : false,
			height:400,
			items : [ this.formPanel]
		});
		
		this.isLoop.groupbox.on("change",this.evt_isLoop_change,this);
		this.startTimeType.on("change",this.evt_startTimeType_change,this);
		this.sendeeOther.on("changevalue",this.evt_sendeeOther_change,this);
	},
	evt_isLoop_change : function(rg, rchecked)
	{
		var isLoop = rchecked.inputValue;
		if("0" == isLoop)
		{
			
			document.getElementById(this.id + "_INTERVAL").style.display = "none";
			
		}else{
			document.getElementById(this.id + "_INTERVAL").style.display = "";
		}
	},
	evt_startTimeType_change : function(rg, rchecked)
	{
		var type = rchecked.inputValue;
		if("1" == type)
		{
			this.startFixedTime.hide();
			this.startAheadTimeDay.show();
			this.startAheadTimeHour.show();
			this.startAheadTimeMi.show();
		}else{
			this.startFixedTime.show();
			this.startFixedTime.datefield.setWidth(100);
			this.startFixedTime.timefield.setWidth(80);
			this.startAheadTimeDay.hide();
			this.startAheadTimeHour.hide();
			this.startAheadTimeMi.hide();
		}
	},
	init4Update : function(id)
	{
		this.alterMsgId = id;
		this.formType = "update";
	},
	init4Add : function(conf)
	{
		this.dataId = conf.dataId;
		this.moduleId = conf.moduleId;
		
		var sendRange = [];
		for(var i = 0; i < conf.sendeeRangeField.length; i++)
		{
			sendRange.push
			({
				FIELD : conf.sendeeRangeField[i].field,
				ISCURUSER : conf.sendeeRangeField[i].ISCURUSER ? true : false,
				CHECKED : conf.sendeeRangeField[i].checked ? true : false,
				LABEL : conf.sendeeRangeField[i].label
			});
		}
		
		this.alterMsg = {
			DATAID : this.dataId,
			MODULEID : this.moduleId,
			REFFIELD : 
			{
				TITLEFIELD:
				{
					NAME : conf.titleField.name,
					LABLE : conf.titleField.lable
				},
				REFTIMEFIELD:
				{
					NAME : conf.refTimeField.name,
					LABLE : conf.refTimeField.lable,
					TYPE : conf.refTimeField.refTimeType
				}
			},
			SENDEEREF :
			{
				SENDEERANGE : sendRange
			},
			ISLOOP : 0,
			SETTING : 
			{
				STARTTIME :
				{
					TYPE : '1',
					S_AHEADTIME_DAY : 0,
					S_AHEADTIME_HOUR : 0,
					S_AHEADTIME_MI : 0
				},
				HASSMS : "0"
			}
			
		};
		this.formType = "add";
	},
	loadData : function()
	{
		if(this.formType == "update")
		{
			this.loadData4Update();
		}
		else if(this.formType == "add")
		{
			this.loadData4Add();
		}
	},
	loadData4Add : function()
	{
		Ext.parf.Ajax.request
		({
			url : '/parm/altermsg.json',
			params:{ actionName : 'getAlterMsgDataMap', DATAID : this.dataId, MODULEID: this.moduleId},
			disableCaching : true,
			success : this.loadData_success,
			scope: this
		});
	},
	loadData4Update : function()
	{
		Ext.parf.Ajax.request
		({
			url : '/parm/altermsg.json',
			params:{ actionName : 'getAlterMsgMap', ID : this.alterMsgId},
			disableCaching : true,
			success : this.loadData_success,
			scope: this
		});
	},
	loadData_success : function(response, option)
	{
		var res = Ext.decode(response.responseText);
		if(res.success)
		{
			////console.log(res.data);
			this.refData = res.data.DATA;
			
			if(this.formType == "update")
			{
				this.alterMsg = res.data.ALTERMSG;
			}
			
			var recordData = {};
			
			recordData.TITLE_DIS = this.getRefFieldValue(this.alterMsg.REFFIELD.TITLEFIELD.NAME);
			recordData.TITLE_LABLE_DIS = this.alterMsg.REFFIELD.TITLEFIELD.LABLE + "：";
			var reftime = this.getRefFieldValue(this.alterMsg.REFFIELD.REFTIMEFIELD.NAME);
			recordData.REFTIME_LABLE_DIS = this.alterMsg.REFFIELD.REFTIMEFIELD.LABLE + "：";
			
			if(this.alterMsg.REFFIELD.REFTIMEFIELD.TYPE == "1")
			{
				recordData.REFTIME_DIS = new Date(reftime.time).format("Y-m-d 00:00");
			}
			else
			{
				recordData.REFTIME_DIS = new Date(reftime.time).format("Y-m-d H:i");
			}
			
			var startTime = this.alterMsg.SETTING.STARTTIME;
			recordData.STARTTIME_TYPE = startTime.TYPE;
			if("1" == recordData.STARTTIME_TYPE)
			{
				recordData.S_AHEADTIME_DAY = startTime.S_AHEADTIME_DAY || 0;
				recordData.S_AHEADTIME_HOUR = startTime.S_AHEADTIME_HOUR || 0;
				recordData.S_AHEADTIME_MI = startTime.S_AHEADTIME_MI || 0;
			}
			else
			{
				recordData.S_FIXEDTIME = startTime.S_FIXEDTIME || "";
			}
			
			recordData.ISLOOP = this.alterMsg.ISLOOP;
			if(1 == recordData.ISLOOP)
			{
				var interval = this.alterMsg.SETTING.INTERVAL;
				recordData.INTERVAL_DAY = interval.INTERVAL_DAY || 0;
				recordData.INTERVAL_HOUR = interval.INTERVAL_HOUR || 0;
				recordData.INTERVAL_MI = interval.INTERVAL_MI || 0;
				recordData.INTERVAL_TIMES = interval.INTERVAL_TIMES || 0;
			}else{
				recordData.INTERVAL_DAY = 0;
				recordData.INTERVAL_HOUR = 0;
				recordData.INTERVAL_MI = 0;
				recordData.INTERVAL_TIMES = 0;
			}
			
			recordData.MSGCONTENT = this.alterMsg.MSGCONTENT;
			recordData.SENDEE_OTHER = this.alterMsg.SENDEEREF.OTHER || "";
			recordData.SENDEE = this.alterMsg.SENDEE || "";
			
			var sendeeRange = this.alterMsg.SENDEEREF.SENDEERANGE;
			var seendRangeItems = [];
			for(var i = 0; i < sendeeRange.length; i++)
			{
				seendRangeItems.push
				({
					boxLabel : sendeeRange[i].LABEL,
					name : 'SENDEE_RANGE',
					inputValue : sendeeRange[i].FIELD,
					isCurUser : sendeeRange[i].ISCURUSER,
					checked : sendeeRange[i].CHECKED ? true : false,
					hiddenLabel : sendeeRange[i].LABEL
				});
			}
			
			if(this.sendeeRange != null)
			{
				this.sendeeRangePanel.removeAll();
			}
			
			recordData.HASSMS = this.alterMsg.SETTING.HASSMS || "0";
			
			////console.log("=====form data========");
			////console.log(recordData);
			////console.log("=====form data========");
			this.formPanel.getForm().loadRecord(new Ext.data.Record(recordData));
			
			this.sendeeRange = new Ext.form.CheckboxGroup
			({
				hideLabel : true,
				name : 'SENDEE_RANGE',
				items : seendRangeItems,
				width : 500
			});
			
			this.sendeeRangePanel.add(this.sendeeRange);
			this.sendeeRangePanel.doLayout();
			
			if(this.formType == "add")
			{
				this.initSendeeByRange();
			}
			
			this.sendeeRange.on("change",this.initSendeeByRange,this);
		}
		else
		{
			alert("error");
		}
	},
	evt_sendeeOther_change : function()
	{
		this.sendee.setValue("");
		this.initSendeeByRange();
	},
	initSendeeByRange : function()
	{
		var items = this.sendeeRange.items;
		items = items.items || items;
		
		Ext.each(items,function(range)
		{
			var field = range.inputValue;
			var checked = range.checked;
			var isCurUser = range.isCurUser;
			if(checked) return;
			var v = "";
			if(isCurUser)
			{
				v = "U[" + Ext.parf.UserInfo.userId +"]<" + Ext.parf.UserInfo.userName + ">";
			}
			else if(field == "OTHER")
			{
				v = this.sendeeOther.getValue();
				this.sendeeOther.setVisible(checked);
				this.sendeeOther.setValue("");
			}
			else
			{
				v = this.getRefFieldValue(field)
			}
			this.sendee.removeUser(v);
		},this);
		
		Ext.each(items,function(range)
		{
			var field = range.inputValue;
			var checked = range.checked;
			var isCurUser = range.isCurUser;
			if(!checked) return;
			var v = "";
			if(isCurUser)
			{
				v = "U[" + Ext.parf.UserInfo.userId +"]<" + Ext.parf.UserInfo.userName + ">";
			}
			else if(field == "OTHER")
			{
				v = this.sendeeOther.getValue();
				this.sendeeOther.setVisible(checked);
			}
			else
			{
				v = this.getRefFieldValue(field)
			}
			this.sendee.pushUser(v);
		},this);
	},
	getRefFieldValue : function(field)
	{
		if(this.refData != null)
		{
			return this.refData[field] ? this.refData[field] : "";
		}
		return "";
	},
	validateAlterForm : function()
	{
		var refTime = new Date(Date.parse(this.refTimeDispaly.getValue().replace(/-/g, "/")));
		var curTime = new Date();
		
		////console.log("refTime===:" + refTime.format("Y-m-d H:i"));
		////console.log("refTime > curTime===:" + (refTime > curTime));
		if(refTime < curTime)
		{
			Ext.parf.showInfo(this.refTimeLableDispaly.getValue() + this.refTimeDispaly.getValue()+ "早于当前时间，不能设置提前提醒！");
			return false;
		}
		
		var startTimeType = parseInt(this.startTimeType.getValue().inputValue);
		var startTime = null;
		if(startTimeType == 2)
		{
			var fixedTime = "";
			try
			{
				fixedTime = this.startFixedTime.getValue();
			}
			catch(e)
			{}
			
			if(Ext.isEmpty(fixedTime))
			{
				Ext.parf.showInfo("请输入设置正确的提醒时间！");
				return false;
				////console.log("开始时间错误！");
			}
			startTime = new Date(Date.parse(fixedTime.replace(/-/g, "/")));
			////console.log("startTime=2==:" + startTime.format("Y-m-d H:i"));
		}
		else if(startTimeType == 1)
		{
			var S_AHEADTIME_DAY = parseInt(this.startAheadTimeDay.getValue());
			var S_AHEADTIME_HOUR = parseInt(this.startAheadTimeHour.getValue());
			var S_AHEADTIME_MI = parseInt(this.startAheadTimeMi.getValue());
			startTime = new Date(Date.parse(this.refTimeDispaly.getValue().replace(/-/g, "/")));
			startTime.setDate(startTime.getDate() - S_AHEADTIME_DAY);
			startTime.setHours(startTime.getHours() - S_AHEADTIME_HOUR);
			startTime.setMinutes(startTime.getMinutes() - S_AHEADTIME_MI);
			////console.log("startTime=1==:" + startTime.format("Y-m-d H:i"));
		}
		if(startTime < curTime)
		{
			Ext.parf.showInfo("提醒时间早于当前时间，请设置正确的提醒时间！");
			return false;
		}
		
		var isLoop = parseInt(this.isLoop.groupbox.getValue().inputValue);
		if(isLoop == 1)
		{
			var INTERVAL_DAY = parseInt(this.intervalDay.getValue());
			var INTERVAL_HOUR = parseInt(this.intervalHour.getValue());
			var INTERVAL_MI = parseInt(this.intervalMi.getValue());
			//console.log("INTERVAL_DAY==" + INTERVAL_DAY);
			//console.log("INTERVAL_HOUR==" + INTERVAL_HOUR);
			//console.log("INTERVAL_MI==" + INTERVAL_MI);
			if(INTERVAL_MI == 0 && INTERVAL_HOUR == 0 && INTERVAL_DAY == 0)
			{
				Ext.parf.showInfo("周期提醒的时间间隔不能为0，请重新设置！");
				return false;
				//
			}
		}
		
		var sendee = this.sendee.getValue().getHiddenValue();
		if(Ext.isEmpty(sendee))
		{
			Ext.parf.showInfo("请设置提醒接收人！");
			return false;
		}
		
		return true;
	},
	getAlterMsgSetting : function()
	{
		var alterMsg = {};
		
		var sendeeRef = {};
		var sendeeRange = [];
		var items = this.sendeeRange.items;
		items = items.items || items;
		Ext.each(items,function(range)
		{
			var label = range.hiddenLabel;
			var field = range.inputValue;
			var checked = range.checked;
			var isCurUser = range.isCurUser;
			
			if(isCurUser && checked)
			{
				sendeeRef.USER  = "U[" + Ext.parf.UserInfo.userId +"]<" + Ext.parf.UserInfo.userName + ">";
			}
			if(field == "OTHER" && checked)
			{
				sendeeRef.OTHER = this.sendeeOther.getValue().getHiddenValue();
			}
			sendeeRange.push
			({
				LABEL : label,
				FIELD : field,
				CHECKED : checked,
				ISCURUSER : isCurUser
			})
		},this);
		
		sendeeRef.SENDEERANGE = sendeeRange;
		alterMsg.SENDEEREF = sendeeRef;
		
		alterMsg.SENDEE = this.sendee.getValue().getHiddenValue();
		alterMsg.MSGCONTENT = this.msgContent.getValue();
		
		alterMsg.ISLOOP = parseInt(this.isLoop.groupbox.getValue().inputValue);
		
		var setting = {};
		
		var startTime = {};
		startTime.TYPE = parseInt(this.startTimeType.getValue().inputValue);
		if(startTime.TYPE == 1)
		{
			startTime.S_AHEADTIME_DAY = parseInt(this.startAheadTimeDay.getValue());
			startTime.S_AHEADTIME_HOUR = parseInt(this.startAheadTimeHour.getValue());
			startTime.S_AHEADTIME_MI = parseInt(this.startAheadTimeMi.getValue());
		}
		else
		{
			startTime.S_FIXEDTIME = this.startFixedTime.getValue();
		}
		
		setting.STARTTIME = startTime;		
		if(alterMsg.ISLOOP == 1)
		{
			var interval = {};
			interval.INTERVAL_DAY = parseInt(this.intervalDay.getValue());
			interval.INTERVAL_HOUR = parseInt(this.intervalHour.getValue());
			interval.INTERVAL_MI = parseInt(this.intervalMi.getValue());
			interval.INTERVAL_TIMES = parseInt(this.intervalTimes.getValue());
			setting.INTERVAL = interval;
		}
		setting.HASSMS = this.hasSms.getValue().inputValue;
		
		alterMsg.SETTING = setting;
		
		if(this.formType == "add")
		{
			alterMsg.USERID = Ext.parf.UserInfo.userId;
			alterMsg.REFTIME = this.refTimeDispaly.getValue();
			alterMsg.REFFIELD = this.alterMsg.REFFIELD;
			
			alterMsg.TITLE = this.titleLableDispaly.getValue() + this.titleDispaly.getValue();
			alterMsg.TITLE += "&nbsp;&nbsp;&nbsp;&nbsp;";
			alterMsg.TITLE += this.refTimeLableDispaly.getValue() + this.refTimeDispaly.getValue();
		
		}
		
		//console.log(alterMsg);
		return Ext.encode(alterMsg);
	}
})
Ext.namespace('Ext.parm.altermsg');

Ext.parm.altermsg.SettingWindowPanel = Ext.extend(Ext.Panel,{
	
	constructor: function()
	{
		var panelId  = Ext.id();
		this.formPanel = new Ext.form.FormPanel
		({
			layout : 'html',
			layoutConfig :
			{
				ejstemplate : '/parm/altermsg/from/setting3.ejs',
				ejsdata : {
					panelId : panelId,
					sms_open :  '1' == Ext.part.SysParameter.SMS_OPEN
				}
			},
			items : 
			[
				this.titleLableDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'TITLE_LABLE_DIS'
				}),
				this.titleDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'TITLE_DIS'
				}),
				this.refTimeLableDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'REFTIME_LABLE_DIS'
				}),
				this.refTimeDispaly = new Ext.form.DisplayField
				({
					hideLabel : true,
					name : 'REFTIME_DIS'
				}),
				this.needAlter = new Ext.form.RadioGroup
				({
					hideLabel : true,
					name : 'NEEDALTER',
					width : 200,
					items : 
					[
						{boxLabel: '需要提醒', name: 'NEEDALTER', inputValue:'1'},
						{boxLabel: '不需要提醒', name: 'NEEDALTER', inputValue:'0',checked:true}
					]
				}),
				this.isLoop = new Ext.parf.form.StoreRadioGroup
				({
					hideLabel : true,
					hiddenName : 'ISLOOP',
					dictId : 'parm.altermsg.loop',
					width : 200,
					value : '0',
					groupcfg :
					{
						name : 'ISLOOP',
						allowBlank : false
					}
				}),
				this.startTimeType = new Ext.form.RadioGroup
				({
					hideLabel : true,
					hiddenName : 'STARTTIME_TYPE',
					width : 150,
					items : 
					[
						{boxLabel: '提前', name: 'STARTTIME_TYPE', inputValue:'1',checked:true},
						{boxLabel: '指定时间', name: 'STARTTIME_TYPE', inputValue:'2'}
					]
				}),
				this.startAheadTimeDay = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'S_AHEADTIME_DAY',
					hiddenName : 'S_AHEADTIME_DAY',
					dictId : 'parm.altermsg.day',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'S_AHEADTIME_DAY'
					}
				}),
				this.startAheadTimeHour = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'S_AHEADTIME_HOUR',
					hiddenName : 'S_AHEADTIME_HOUR',
					dictId : 'parm.altermsg.hour',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'S_AHEADTIME_HOUR'
					}
				}),
				this.startAheadTimeMi = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'S_AHEADTIME_MI',
					hiddenName : 'S_AHEADTIME_MI',
					dictId : 'parm.altermsg.mi',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'S_AHEADTIME_MI'
					}
				}),
				this.startFixedTime = new Ext.parf.form.DateTimeField
				({
					hideLabel : true,
					name : 'S_FIXEDTIME',
					hiddenName : 'S_FIXEDTIME',
					hidden : true
				}),
				this.intervalDay = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_DAY',
					hiddenName : 'INTERVAL_DAY',
					dictId : 'parm.altermsg.day',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_DAY'
					}
				}),
				this.intervalHour = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_HOUR',
					hiddenName : 'INTERVAL_HOUR',
					dictId : 'parm.altermsg.hour',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_HOUR'
					}
				}),
				this.intervalMi = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_MI',
					hiddenName : 'INTERVAL_MI',
					dictId : 'parm.altermsg.mi',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_MI'
					}
				}),
				this.intervalTimes = new Ext.parf.form.StoreComboBox
				({
					hideLabel : true,
					name : 'INTERVAL_TIMES',
					hiddenName : 'INTERVAL_TIMES',
					dictId : 'parm.altermsg.intervaltimes',
					value : "0",
					allrec : false,
					width : 60,
					groupcfg :
					{
						name : 'INTERVAL_TIMES'
					}
				}),
				this.sendeeRange = new Ext.form.CheckboxGroup
				({
					hideLabel : true,
					name : 'SENDEE_RANGE',
					items : [],
					width : 500
				}),
				this.sendeeOther = new Ext.part.form.userselection.MultiUserField
				({
					hideLabel : true,
					name : 'SENDEE_OTHER',
					width : 500,
					height : 50,
					allowBlank : true
				}),
				this.sendee = new Ext.part.form.userselection.MultiUserField
				({
					hideLabel : true,
					name : 'SENDEE',
					readOnly : true,
					width : 540,
					height:100
				}),
				this.msgContent = new Ext.form.TextArea
				({
					hideLabel : true,
					name : 'MSGCONTENT',
					width : 500,
					height :150,
					maxLength : 200
				}),
				this.hasSms = new Ext.form.RadioGroup
				({
					hideLabel : true,
					name : 'HASSMS',
					width : 200,
					items : 
					[
						{boxLabel: '否', name: 'HASSMS', inputValue:'0', checked:true},
						{boxLabel: '是', name: 'HASSMS', inputValue:'1'}
					]
				})
			]
		});
		
		Ext.parm.altermsg.SettingWindowPanel.superclass.constructor.call(this,
		{
			id : panelId,
			title : '提醒设置',
			border : false,
			autoScroll : true,
			items : [ this.formPanel]
		});
		
		this.isLoop.groupbox.on("change",this.evt_isLoop_change,this);
		this.startTimeType.on("change",this.evt_startTimeType_change,this);
		this.sendeeRange.on("change",this.initSendeeByRange,this);
		this.sendeeOther.on("changevalue",this.evt_sendeeOther_change,this);
		this.on('show',this.initValue,this);
		this.on('beforeshow',this.checkRefForm,this);
		this.needAlter.on("change",this.checkRefForm,this);
	},
	evt_isLoop_change : function(rg, rchecked)
	{
		var isLoop = rchecked.inputValue;
		if("0" == isLoop)
		{
			document.getElementById(this.id + "_INTERVAL").style.display = "none";
			
		}else{
			document.getElementById(this.id + "_INTERVAL").style.display = "";
		}
	},
	evt_startTimeType_change : function(rg, rchecked)
	{
		var type = rchecked.inputValue;
		if("1" == type)
		{
			this.startFixedTime.hide();
			this.startAheadTimeDay.show();
			this.startAheadTimeHour.show();
			this.startAheadTimeMi.show();
		}else{
			this.startFixedTime.show();
			this.startFixedTime.datefield.setWidth(100);
			this.startFixedTime.timefield.setWidth(80);
			this.startAheadTimeDay.hide();
			this.startAheadTimeHour.hide();
			this.startAheadTimeMi.hide();
		}
	},
	initRefForm : function(form, refField)
	{
		this.refForm = form;
		this.refField = refField;
		
		if(this.refField.refTimeField.refTimeType == "1")
		{
			this.refField.refTimeFormat = "Y-m-d 00:00";
		}
		else
		{
			this.refField.refTimeFormat = "Y-m-d H:i";
		}
		var sendeeRangeField = this.refField.sendeeRangeField || [];
		var seendRangeItems = [];
		for(var i = 0; i < sendeeRangeField.length; i++)
		{
			seendRangeItems.push
			({
				boxLabel : sendeeRangeField[i].ISCURUSER ? "我" : sendeeRangeField[i].label,
				name : 'SENDEE_RANGE',
				inputValue : sendeeRangeField[i].field,
				isCurUser : sendeeRangeField[i].ISCURUSER ? true : false,
				checked : sendeeRangeField[i].checked ? true : false,
				hiddenLabel : sendeeRangeField[i].label
			});
		}
		
		this.sendeeRange.items = seendRangeItems;
		
		this.titleLableDispaly.setValue(this.refField.titleField.lable + "：");
		this.refTimeLableDispaly.setValue(this.refField.refTimeField.lable + "：");
	},
	initValue : function()
	{
		var title = this.getRefFieldValue(this.refField.titleField.name);
		this.titleDispaly.setValue(title);
		
		var refTime = this.getRefFieldValue(this.refField.refTimeField.name);
		var value = refTime.format ? refTime.format(this.refField.refTimeFormat) : refTime;
		this.refTimeDispaly.setValue(value);
		
		this.initSendeeByRange();
	},
	evt_sendeeOther_change : function()
	{
		this.sendee.setValue("");
		this.initSendeeByRange();
	},
	initSendeeByRange : function()
	{
		var items = this.sendeeRange.items;
		items = items.items || items;
		
		Ext.each(items,function(range)
		{
			var field = range.inputValue;
			var checked = range.checked;
			var isCurUser = range.isCurUser;
			if(checked) return;
			var v = "";
			if(isCurUser)
			{
				v = "U[" + Ext.parf.UserInfo.userId +"]<" + Ext.parf.UserInfo.userName + ">";
			}
			else if(field == "OTHER")
			{
				v = this.sendeeOther.getValue();
				this.sendeeOther.setVisible(checked);
				this.sendeeOther.setValue("");
			}
			else
			{
				v = this.getRefFieldValue(field)
			}
			this.sendee.removeUser(v);
		},this);
		
		Ext.each(items,function(range)
		{
			var field = range.inputValue;
			var checked = range.checked;
			var isCurUser = range.isCurUser;
			if(!checked) return;
			var v = "";
			if(isCurUser)
			{
				v = "U[" + Ext.parf.UserInfo.userId +"]<" + Ext.parf.UserInfo.userName + ">";
			}
			else if(field == "OTHER")
			{
				v = this.sendeeOther.getValue();
				this.sendeeOther.setVisible(checked);
			}
			else
			{
				v = this.getRefFieldValue(field)
			}
			this.sendee.pushUser(v);
		},this);
	},
	getRefFieldValue : function(field)
	{
		if(this.refForm != null)
		{
			return this.refForm.getForm().findField(field) ? this.refForm.getForm().findField(field).getValue() : "";
		}
		return "";
	},
	checkRefForm : function()
	{
		this.initSettingForm();
		
		this.refTime = "";
		if(this.needAlter.getValue() == null || this.needAlter.getValue().inputValue == "0")
		{
			return true;
		}
		
		var refTime = this.getRefFieldValue(this.refField.refTimeField.name);
		
		if(Ext.isEmpty(refTime))
		{
			Ext.parf.showInfo("请先输入" + this.refField.refTimeField.lable);
			this.needAlter.setValue('0');
		}
		
		this.refTime = refTime;
		
		return true;
	},
	initSettingForm : function()
	{
		if(this.needAlter.getValue() == null)return;
		var showSetting = "";
		if(this.needAlter.getValue().inputValue == "0")
		{
			showSetting = "none";
		}
		
		var eles = document.getElementsByName(this.id + "_SETTING");
		Ext.each(eles,function(el){
			el.style.display = showSetting;
		},this);
	},
	validateAlterForm : function()
	{
		if(this.needAlter.getValue() == null || this.needAlter.getValue().inputValue == "0") return true;
		
		if(!this.isVisible())
		{
			this.initValue();
		}
		
		var refTime = new Date(Date.parse(this.refTimeDispaly.getValue().replace(/-/g, "/")));
		var curTime = new Date();
		
		////console.log("refTime===:" + refTime.format("Y-m-d H:i"));
		////console.log("refTime > curTime===:" + (refTime > curTime));
		if(refTime < curTime)
		{
			Ext.parf.showInfo(this.refTimeLableDispaly.getValue() + this.refTimeDispaly.getValue()+ "早于当前时间，不能设置提前提醒！");
			return false;
		}
		
		var startTimeType = parseInt(this.startTimeType.getValue().inputValue);
		var startTime = null;
		if(startTimeType == 2)
		{
			var fixedTime = "";
			try
			{
				fixedTime = this.startFixedTime.getValue();
			}
			catch(e)
			{}
			
			if(Ext.isEmpty(fixedTime))
			{
				Ext.parf.showInfo("请输入设置正确的提醒时间！");
				return false;
				////console.log("开始时间错误！");
			}
			startTime = new Date(Date.parse(fixedTime.replace(/-/g, "/")));
			////console.log("startTime=2==:" + startTime.format("Y-m-d H:i"));
		}
		else if(startTimeType == 1)
		{
			var S_AHEADTIME_DAY = parseInt(this.startAheadTimeDay.getValue());
			var S_AHEADTIME_HOUR = parseInt(this.startAheadTimeHour.getValue());
			var S_AHEADTIME_MI = parseInt(this.startAheadTimeMi.getValue());
			startTime = new Date(Date.parse(this.refTimeDispaly.getValue().replace(/-/g, "/")));
			startTime.setDate(startTime.getDate() - S_AHEADTIME_DAY);
			startTime.setHours(startTime.getHours() - S_AHEADTIME_HOUR);
			startTime.setMinutes(startTime.getMinutes() - S_AHEADTIME_MI);
			////console.log("startTime=1==:" + startTime.format("Y-m-d H:i"));
		}
		if(startTime < curTime)
		{
			Ext.parf.showInfo("提醒时间早于当前时间，请设置正确的提醒时间！");
			return false;
		}
		
		var isLoop = parseInt(this.isLoop.groupbox.getValue().inputValue);
		if(isLoop == 1)
		{
			var INTERVAL_DAY = parseInt(this.intervalDay.getValue());
			var INTERVAL_HOUR = parseInt(this.intervalHour.getValue());
			var INTERVAL_MI = parseInt(this.intervalMi.getValue());
			if(INTERVAL_MI == 0 && INTERVAL_HOUR == 0 && INTERVAL_DAY == 0)
			{
				Ext.parf.showInfo("周期提醒的时间间隔不能为0，请重新设置！");
				return false;
				////console.log("重复时间错误！");
			}
		}
		
		var sendee = this.sendee.getValue().getHiddenValue();
		if(Ext.isEmpty(sendee))
		{
			Ext.parf.showInfo("请设置提醒接收人！");
			return false;
		}
		return true;
	},
	getAlterMsgSetting : function()
	{
		if(this.needAlter.getValue() == null || this.needAlter.getValue().inputValue == "0") return "";
		
		var alterMsg = {};
		
		var refField = {};
		
		refField.TITLEFIELD = {
			LABLE : this.refField.titleField.lable,
			NAME : this.refField.titleField.name
		}
		refField.REFTIMEFIELD = {
			LABLE : this.refField.refTimeField.lable,
			NAME : this.refField.refTimeField.name,
			TYPE : this.refField.refTimeField.refTimeType
		}
		alterMsg.REFFIELD = refField;
		
		var sendeeRef = {};
		var sendeeRange = [];
		var items = this.sendeeRange.items;
		items = items.items || items;
		Ext.each(items,function(range)
		{
			var label = range.hiddenLabel;
			var field = range.inputValue;
			var checked = range.checked;
			var isCurUser = range.isCurUser;
			
			if(isCurUser && checked)
			{
				sendeeRef.USER  = "U[" + Ext.parf.UserInfo.userId +"]<" + Ext.parf.UserInfo.userName + ">";
			}
			if(field == "OTHER" && checked)
			{
				sendeeRef.OTHER = this.sendeeOther.getValue().getHiddenValue();
			}
			sendeeRange.push
			({
				LABEL : label,
				FIELD : field,
				CHECKED : checked,
				ISCURUSER : isCurUser
			})
		},this);
		
		sendeeRef.SENDEERANGE = sendeeRange;
		alterMsg.SENDEEREF = sendeeRef;
		
		alterMsg.SENDEE = this.sendee.getValue().getHiddenValue();
		alterMsg.MSGCONTENT = this.msgContent.getValue();
		
		alterMsg.ISLOOP = parseInt(this.isLoop.groupbox.getValue().inputValue);
		
		var setting = {};
		
		var startTime = {};
		startTime.TYPE = parseInt(this.startTimeType.getValue().inputValue);
		if(startTime.TYPE == 1)
		{
			startTime.S_AHEADTIME_DAY = parseInt(this.startAheadTimeDay.getValue());
			startTime.S_AHEADTIME_HOUR = parseInt(this.startAheadTimeHour.getValue());
			startTime.S_AHEADTIME_MI = parseInt(this.startAheadTimeMi.getValue());
		}
		else
		{
			startTime.S_FIXEDTIME = this.startFixedTime.getValue();
		}
		
		setting.STARTTIME = startTime;		
		if(alterMsg.ISLOOP == 1)
		{
			var interval = {};
			interval.INTERVAL_DAY = parseInt(this.intervalDay.getValue());
			interval.INTERVAL_HOUR = parseInt(this.intervalHour.getValue());
			interval.INTERVAL_MI = parseInt(this.intervalMi.getValue());
			interval.INTERVAL_TIMES = parseInt(this.intervalTimes.getValue());
			setting.INTERVAL = interval;
		}
		setting.HASSMS = this.hasSms.getValue().inputValue;
		
		alterMsg.SETTING = setting;
		
		alterMsg.USERID = Ext.parf.UserInfo.userId;
		alterMsg.REFTIME = this.refTimeDispaly.getValue();
		
		alterMsg.TITLE = this.titleLableDispaly.getValue() + this.titleDispaly.getValue();
		alterMsg.TITLE += "&nbsp;&nbsp;&nbsp;&nbsp;";
		alterMsg.TITLE += this.refTimeLableDispaly.getValue() + this.refTimeDispaly.getValue();
		
		//console.log("====alterMsg====");
		//console.log(alterMsg);
		//console.log("====alterMsg====");
		return Ext.encode(alterMsg);
	}
})
