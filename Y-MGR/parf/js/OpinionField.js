Ext.util.CSS.swapStyleSheet('parf-opinion','/parf/css/opinion.css');
Ext.namespace('Ext.parf.form');
Ext.parf.form.OpinionPicker = Ext.extend(Ext.Component,{
	initComponent : function()
	{
		Ext.ColorPalette.superclass.initComponent.call(this);
		this.addEvents('select');

		if(this.handler){
			this.on('select', this.handler, this.scope, true);
		}
	},
	onRender : function(ct, position)
	{
		var pid = this.id;
		
		this.autoEl = 
		{
			tag: 'div'
		};
	
		Ext.parf.form.OpinionPicker.superclass.onRender.call(this, ct, position);
		
		this.store = new Ext.data.JsonStore
		({
			autoLoad : true,
			autoDestroy : true,
			url : '/parm/opinion.json',
			idProperty : 'OPINIONID',
			baseParams : { actionName : 'getMyOpinion'},
			fields : ['OPINIONID','OPINION'] 
		});
		
		this.tpl = new Ext.XTemplate
		(
			'<tpl for=".">',
				'<div class="item-wrap">',
					'<div class="item-content-new">',
						'<table>',
							'<tr>',
								'<td width="370">{opinionHtml}</td>',
								'<td><div class="delBtn" onclick={delFn}></div></td>',
							'</tr>',
						'</table>',
					'</div>',
				'</div>',
			'</tpl>',
			'<div class="x-clear"></div>'
		);
		
		this.dataview = new Ext.DataView
		({
			cls : 'parf-opinion',
			store: this.store,
			style:'overflow:auto;',
			autoScroll : true,
			overClass:'x-view-over',
			tpl: this.tpl,
			multiSelect : false,
			singleSelect : false,
			itemSelector : 'div.item-wrap',
			prepareData: function(data)
			{
				data.opinionHtml = data.OPINION.replace( /&/g ,'&amp;');
				data.opinionHtml = data.opinionHtml.replace( / /g ,'&nbsp;');
				data.opinionHtml = data.opinionHtml.replace( /</g ,'&lt;');
				data.opinionHtml = data.opinionHtml.replace( />/g ,'&gt;');
				data.opinionHtml = data.opinionHtml.replace( /\n/g ,'<br>');
				data.delFn = 'Ext.parf.reflect.ExtCmp(\''+ pid +'\',\'delOpinion\',\''+ data.OPINIONID+'\');';
				return data;
			}
		});
		
		this.dataview.on('dblclick',function(dv,index,node,e)
		{
			var rec = this.store.getAt(index);
			this.fireEvent('select',rec.get('OPINION'));
		},this);
		
		this.opinionPanel = new Ext.Panel
		({
			renderTo : this.el.dom,
			width : 400,
			height : 300,
			layout : 'fit',
			border : false,
			style : "background-color:#FFFFFF;",
			items : this.dataview
		});
	},
	delOpinion : function(opinionId)
	{
		Ext.fjs.get({
			url : '/parm/opinion.json',
			params : {
				actionName : 'deleteMyOpinion',
				OPINIONID : opinionId
			},
			success : this.deleteOpinion_success,
			scope : this
		});
	},
	deleteOpinion_success : function(response, options)
	{
		var res = Ext.decode(response.responseText);
		if(res.errorMessage)
		{
			Ext.Msg.alert(Ext.parf.common_msgTitle, res.errorMessage);
		}else{
			this.reloadOpinion();
		}
	},
	reloadOpinion : function()
	{
		 this.store.load({params:{actionName : 'getMyOpinion'}});
	}
});

Ext.parf.form.OpinionMenu =  Ext.extend(Ext.menu.Menu, 
{
	hideOnClick : true,
	initComponent : function()
	{
		Ext.apply(this, {
			plain: true,
			items: this.picker = new Ext.parf.form.OpinionPicker()
		});
		this.relayEvents(this.picker, ['select']);
		this.on('select', this.menuHide, this);
		if(this.handler)
		{
			this.on('select', this.handler, this.scope || this);
		}
		Ext.parf.form.OpinionMenu.superclass.initComponent.call(this);
	},
	menuHide : function()
	{
		if(this.hideOnClick)
		{
			this.hide(true);
		}
	}
});

Ext.parf.form.OpinionWindow = Ext.extend(Ext.Window,
{
	constructor : function()
	{ 
		this.teaxarea = new Ext.form.TextArea
		({
			border : false,
			value : '',
			maxLength : 50
		});
		
		Ext.parf.form.OpinionWindow.superclass.constructor.call(this,
		{
			title : "填写意见",
			height : 300,
			width : 400,
			layout:'fit',
			buttonAlign :'center',
			closeAction:'hide',
			plain: true,
			modal:true,
			resizable:false,
			border : false,
			items:[
			{
				layout : 'fit',
				items : [this.teaxarea],
				tbar : 
				{
					items:
					[
						{
							text: '常用意见',
							iconCls:'mnu-icon_favourites',
							handler : this.showOpinions,
							scope : this
						},{
							text: '保存为常用意见',
							iconCls:'btn-save',
							handler : this.saveOpinion,
							scope : this
						},'->',{
							text: '清空',
							iconCls:'btn-delete',
							handler : this.clearOpinion,
							scope : this
						}
					]
				}
			}],
			buttons : 
			[{
				text: '确定',
				iconCls: 'btn-accept',
				handler: this.submit_onclick,
				scope:this
			},{
				text: '取消',
				iconCls: 'btn-stop',
				handler: this.cancle_onclick,
				scope:this
			}]
		});
	},
	initComponent: function()
	{
		this.addEvents('submit');  
		Ext.parf.form.OpinionWindow.superclass.initComponent.call(this); 
	},
	setValue : function(v)
	{
		this.value = v;
		if(this.rendered)
		{
			this.teaxarea.setValue(this.value);
		}else{
			
			this.on("afterrender",function()
			{
				this.teaxarea.setValue(this.value);
			},this);
		}
	},
	submit_onclick : function(btn,e)
	{
		if(!this.teaxarea.validate())
		{
			Ext.Msg.alert(Ext.parf.common_msgTitle,this.teaxarea.getActiveError());
			return;
		}
		this.fireEvent('submit',this.teaxarea.getValue(),e);
		this.hide();
	},
	cancle_onclick : function(btn,e)
	{
		this.hide();
	},
	showOpinions : function(btn,e)
	{
		if(this.opinionMenu == null)
		{
			this.opinionMenu = new Ext.parf.form.OpinionMenu();
			this.opinionMenu.on('select',function(v)
			{
				v = v + "\n" + this.teaxarea.getValue();
				this.teaxarea.setValue(v);
			},this);
		}
		this.opinionMenu.showAt(btn.getPosition());
	},
	clearOpinion : function()
	{
		this.teaxarea.setValue("");
	},
	saveOpinion : function()
	{
		if(!this.teaxarea.validate())
		{
			Ext.Msg.alert(Ext.parf.common_msgTitle,this.teaxarea.getActiveError());
			return;
		}
		var value = this.teaxarea.getValue();
		if(Ext.isEmpty(value))
		{
			Ext.Msg.alert(Ext.parf.common_msgTitle,'意见不能为空！');
			return;
		}
		Ext.fjs.get({
			url : '/parm/opinion.json',
			params : {
				actionName : 'saveMyOpinion',
				OPINION : value
			},
			success : this.saveOpinion_success,
			scope : this
		}); 
	},
	saveOpinion_success : function(response, options)
	{
		var res = Ext.decode(response.responseText);
		
		if(res.returnMessage != null)
		{
			Ext.Msg.alert(Ext.parf.common_msgTitle, res.returnMessage);
		}
		if(this.opinionMenu != null)
		{
			this.opinionMenu.picker.reloadOpinion();
		}
	}
});

Ext.parf.form.OpinionField = Ext.extend(Ext.form.Field,
{
	dateFormat : 'Y/m/d H:i',
	
	height : 100,
	
	hasHistory : false,
	
	defaultAutoCreate : {tag: 'div'},
	
	initComponent: function()
	{
		this.hiddenName = this.name;
		this.name = null;
		Ext.parf.form.OpinionField.superclass.initComponent.call(this); 
		
		if (this.allowBlank === false && this.fieldLabel) 
		{
			this.fieldLabel = this.fieldLabel + ' <font color=red>(*)</font> ';   
		} 
		if(this.boldLabel)
		{
			this.fieldLabel = "<b>" + this.fieldLabel + "</b>";  
		}
		
		this.hiddenField = new Ext.form.Hidden
		({
			name : this.hiddenName
		});
		
		this.store = new Ext.data.ArrayStore
		({
			autoDestroy : true,
			sortInfo :
			{
				field: 'date',
				direction: 'DESC'
			},
			fields: ['opinion', 'userid', 'username', 'date', 'isnew', 'isadd']
		});
		this.tpl = new Ext.XTemplate
		(
			'<tpl for=".">',
				'<div class="item-wrap">',
					'<div class="item-content-{style}">{opinionHtml}</div>',
					'<div class="item-catpion-{style}">{username}&nbsp;&nbsp;{date}</div>',
				'</div>',
			'</tpl>',
			'<div class="x-clear"></div>'
		);
		
		this.dataview = new Ext.DataView
		({
			cls : 'parf-opinion',
			store: this.store,
			style:'overflow:auto;',
			autoScroll : true,
			overClass:'x-view-over',
			tpl: this.tpl,
			multiSelect : false,
			singleSelect : false,
			itemSelector : 'div.item-wrap',
			prepareData: function(data)
			{
				data.style = data.isnew ? "new" : "old";
				data.opinionHtml = data.opinion.replace( /&/g ,'&amp;');
				data.opinionHtml = data.opinionHtml.replace( / /g ,'&nbsp;');
				data.opinionHtml = data.opinionHtml.replace( /</g ,'&lt;');
				data.opinionHtml = data.opinionHtml.replace( />/g ,'&gt;');
				data.opinionHtml = data.opinionHtml.replace( /\n/g ,'<br>');
				return data;
			}
		});
		
		this.toolbar = new Ext.Toolbar
		({
			items:
			[
				'->',
				this.btnHis = new Ext.Button
				({
					text: '显示历史意见',
					tooltip:'显示或隐藏历史意见',
					iconCls:'btn-detail',
					enableToggle: true,
					disabled : true,
					handler: this.filterOpinion,
					scope: this
				}),
				this.btnAdd = new Ext.Button
				({
					text: '填写',
					iconCls:'btn-add',
					tooltip:'填写处理意见',
					handler : this.openOpinionWin,
					scope: this
				})
			]
		});
	},
	//private
	getName : function()
	{
		return this.rendered && this.hiddenName ? this.hiddenName : this.name || this.id || '';
	},
	onRender : function(ct, position)
	{
		Ext.parf.form.OpinionField.superclass.onRender.call(this, ct, position);
		
		this.opinionPanel = new Ext.Panel
		({
			bodyStyle : "background-color:#FFFFFF;",
			layout : 'fit',
			border : false,
			items : 
			[
				this.dataview,
				this.hiddenField
			],
			tbar : this.toolbar
		});
		
		this.opinionPanel.render(this.el);
		
		if(this.readOnly || this.disabled)
		{
			this.btnAdd.hide();
			this.hiddenField.disable();
		}
	},
	setValue : function(v)
	{
		this.store.removeAll();
		v = v || [];
		
		this.value = Ext.isString(v) ?  v : Ext.encode(v);
		
		
		if(Ext.isEmpty(v)) return;
		
		if(Ext.isString(v))v = Ext.decode(v);
		if(!Ext.isArray(v)) v = [v];
		var arr = [];
		var len = v.length;
		for(var i = 0; i < len; i++)
		{
			var o = v[i];
			arr.push([v[i].opinion,v[i].userid,v[i].username,v[i].date, v[i].isnew, v[i].isadd ? true : false]);
			if(!v[i].isnew) this.hasHistory = true;
		}
		
		this.store.loadData(arr);
		this.store.filter('isnew',true);
		this.store.multiSort
		([{
			field: 'isadd',
			direction: 'DESC'
		},{
			field: 'isnew',
			direction: 'DESC'
		},{
			field: 'date',
			direction: 'DESC'
		}]);
		
		if(this.hasHistory)
		{
			this.btnHis.enable();
		}
		var hv = this.getAddOpinion();
		if(hv)
		{
			hv = Ext.encode(hv);
		}else{
			hv = "";
		}
		
		this.hiddenField.setValue(hv);
	},
	getValue : function()
	{
		return this.value;
	},
	reset : function()
	{
		Ext.parf.form.OpinionField.superclass.reset.call(this);
		if(this.btnHis.pressed)
		{
			this.btnHis.toggle(false,true);
		}
		this.hiddenField.setValue("");
	},
	onDisable : function()
	{
		this.btnAdd.hide();
		this.hiddenField.disable();
	},
	onEnable : function()
	{
		if(this.readOnly) return;
		this.btnAdd.show();
		this.hiddenField.enable();
	},
	setReadOnly : function(readOnly)
	{
		this.readOnly = readOnly;
		return this[readOnly ? 'disable' : 'enable']();
	},
	validate : function()
	{
		if(this.readOnly || this.disabled || this.allowBlank)
		{
			return true;
		}
		var hasAdd = false;
		var count = this.store.getCount();
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			if(rec.get('isadd'))
			{
				hasAdd = true;
				break;
			}
		}
		if(!hasAdd)
		{
			var str1 = "【"+this.fieldLabel+"】不能为空，请点击【填写】按钮填写处理意见 ";
			Ext.Msg.show({title:Ext.parf.common_msgTitle,msg: str1,buttons: Ext.Msg.OK,icon: Ext.MessageBox.ERROR});
			return false;
		}
		return true;
	},
	onResize : function(aw, ah, w, h)
	{
		Ext.parf.form.OpinionField.superclass.onResize.call(this,aw, ah, w, h);
		if(w)this.opinionPanel.setWidth(w);
		if(h)this.opinionPanel.setHeight(h);
	},
	filterOpinion : function(btn)
	{
		if(btn.pressed)
		{
			this.store.clearFilter();
		}else{
			this.store.filter('isnew',true);
		}
		this.store.multiSort
		([{
			field: 'isadd',
			direction: 'DESC'
		},{
			field: 'date',
			direction: 'DESC'
		}]);
	},
	openOpinionWin : function()
	{	
		if(this.opinionWin == null)
		{
			this.opinionWin = new Ext.parf.form.OpinionWindow();
			this.opinionWin.on("submit",function(v,e)
			{
				var count = this.store.getCount();
				for(var i = 0; i < count; i++)
				{
					var rec = this.store.getAt(i);
					if(rec.get('isadd'))
					{
						this.store.removeAt(i);
						break;
					}
				}
				var obj = null;
				if(!Ext.isEmpty(v))
				{
					obj = 
					{
						opinion : v,
						userid : Ext.parf.UserInfo.userId,
						username : Ext.parf.UserInfo.userName,
						date : new Date().format(this.dateFormat),
						isnew : true,
						isadd : true
					};
					
					this.hiddenField.setValue(Ext.encode(obj));
					
					var recType = this.store.recordType;
					var p = new recType(obj);
					this.store.addSorted(p);
					this.store.multiSort
					([{
						field: 'isadd',
						direction: 'DESC'
					},{
						field: 'date',
						direction: 'DESC'
					}]);
				}
				else
				{
					this.hiddenField.setValue("");
				}
			},this);
		}
		
		var v = this.getAddOpinion();
		this.opinionWin.setValue(v ? v.opinion : "" );
		this.opinionWin.show();
	},
	getAddOpinion : function()
	{
		var count = this.store.getCount();
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			if(rec.get('isadd'))
			{
				return {
					opinion : rec.get('opinion'),
					userid : rec.get('userid'),
					username : rec.get('username'),
					date : rec.get('date'),
					isnew : rec.get('isnew'),
					isadd : rec.get('isadd')
				};
			}
		}
		return null;
	},
	getAllOpinion : function()
	{
		var array = [];
		var count = this.store.getCount();
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			var obj = {
				opinion : rec.get('opinion'),
				userid : rec.get('userid'),
				username : rec.get('username'),
				date : rec.get('date'),
				isnew : rec.get('isnew'),
				isadd : rec.get('isadd')
			};
			array.push(obj);
		}
		return array;
	}
});

Ext.parf.form.OpinionObj = Ext.extend(Ext.util.Observable,
{
	constructor : function(v)
	{
		this.v = v || "";
		
		this.store = new Ext.data.ArrayStore
		({
			autoDestroy : true,
			sortInfo: 
			{
				field: 'date',
				direction: 'DESC'
			},
			fields: ['opinion', 'userid', 'username', 'date', 'isnew', 'isadd']
		});
		
		if(Ext.isEmpty(v))return;
		
		if(Ext.isString(v)) v = Ext.decode(v);
		if(!Ext.isArray(v)) v = [v];
		var arr = [];
		var len = v.length;
		for(var i = 0; i < len; i++)
		{
			var o = v[i];
			arr.push([v[i].opinion,v[i].userid,v[i].username,v[i].date, v[i].isnew, v[i].isadd ? true : false]);
		}
		this.store.loadData(arr);
	},
	getDisplayText : function()
	{
		var text = "";
		var count = this.store.getCount();
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			if(rec.get('isnew'))
			{
				text += rec.get('opinion') + "\n";
				text += "              ";
				text += rec.get('username') + "    ";
				text += rec.get('date') + "    ";
				if(i < count - 1)
				{	
					text += "\n";
					text += "-----------------------------------------";
					text += "\n\n";
				}
			}
		}
		return text;
	},
	getHtmlDisplayText : function()
	{
		var text = "";
		var count = this.store.getCount();
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			if(rec.get('isnew'))
			{
				text += rec.get('opinion');
				text += "<br/>";
				text += "<span>[";
				text += rec.get('username') + "&nbsp;&nbsp;";
				text += rec.get('date');
				text += "]</span><br/>";
			}
		}
		text = text.replace(/\s+/g,'&nbsp;');
		return text;
	},
	getSubmitValue : function()
	{
		var count = this.store.getCount();
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			if(rec.get('isadd'))
			{
				var obj =  {
					opinion : rec.get('opinion'),
					userid : rec.get('userid'),
					username : rec.get('username'),
					date : rec.get('date'),
					isnew : rec.get('isnew'),
					isadd : rec.get('isadd')
				};
				return Ext.encode(obj);
			}
		}
		return "";
	},
	getHiddenValue : function()
	{
		var count = this.store.getCount();
		var array = [];
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			var obj =  {
				opinion : rec.get('opinion'),
				userid : rec.get('userid'),
				username : rec.get('username'),
				date : rec.get('date'),
				isnew : rec.get('isnew'),
				isadd : rec.get('isadd')
			};
			array.push(obj);
		}
		return array.length > 0 ? Ext.encode(array): "";
	}
});

Ext.reg('parf-opinion', Ext.parf.form.OpinionField);


Ext.parf.form.SealField = Ext.extend(Ext.form.Field,
{
	dateFormat : 'Y/m/d H:i',
		
	hasHistory : false,
	
	sealAlign: 'bottom',
	
	defaultAutoCreate : {tag: 'div'},
		
	initComponent: function()
	{
		this.hiddenName = this.name;
		this.name = null;
		Ext.parf.form.SealField.superclass.initComponent.call(this); 
		
		if (this.allowBlank === false && this.fieldLabel) 
		{
			this.fieldLabel = this.fieldLabel + ' <font color=red>(*)</font> ';   
		} 
		if(this.boldLabel)
		{
			this.fieldLabel = "<b>" + this.fieldLabel + "</b>";  
		}
		
		this.hiddenField = new Ext.form.Hidden
		({
			name : this.hiddenName
		});		
				
		this.store = new Ext.data.ArrayStore
		({
			autoDestroy : true,
			sortInfo :
			{
				field: 'date',
				direction: 'DESC'
			},
			fields: ['opinion', 'userid', 'username', 'date', 'isnew', 'isadd','sealLogId','sealId'
			         , 'sealdisplay','opiniondisplay', 'usernamedisplay','datedisplay']
		});				
				
		if(this.sealAlign=='left')
		{
			this.tpl = new Ext.XTemplate
			(
				'<tpl for=".">',
					'<div class="item-wrap">',
					    '<table border="0" width="100%"><tr><td align="left"><div class="sealdisplay-{sealdisplaycss}">{sealImg}</div></td><td>',
						'<div class="item-content-{style} sealdisplay-{opiniondisplaycss}">{opinionHtml}</div>',
						'<div class="item-catpion-{style}"><div class="sealdisplay-{usernamedisplaycss}">{username}</div>',
						'&nbsp;&nbsp;<div class="sealdisplay-{datedisplaycss}">{date}</div></div>',
						'</td></tr></table>',
					'</div>',
				'</tpl>',
				'<div class="x-clear"></div>'
			);
		}
		else if(this.sealAlign=='right')
		{
			this.tpl = new Ext.XTemplate
			(
				'<tpl for=".">',
					'<div class="item-wrap">',
					    '<table border="0" width="100%"><tr><td>',
						'<div class="item-content-{style} sealdisplay-{opiniondisplaycss}">{opinionHtml}</div>',
						'<div class="item-catpion-{style}"><div class="sealdisplay-{usernamedisplaycss}">{username}</div>',
						'&nbsp;&nbsp;<div class="sealdisplay-{datedisplaycss}">{date}</div></div>',
						'</td><td align="right"><div class="sealdisplay-{sealdisplaycss}">{sealImg}</div></td></tr></table>',
					'</div>',
				'</tpl>',
				'<div class="x-clear"></div>'
			);
		}
		else if(this.sealAlign=='top')
		{
			this.tpl = new Ext.XTemplate
			(
				'<tpl for=".">',
					'<div class="item-wrap">',
					    '<table border="0" width="100%"><tr><td align="center">',
					    '<div class="sealdisplay-{sealdisplaycss}">{sealImg}</div></td></tr><tr><td>',
						'<div class="item-content-{style} sealdisplay-{opiniondisplaycss}">{opinionHtml}</div>',
						'<div class="item-catpion-{style}"><div class="sealdisplay-{usernamedisplaycss}">{username}</div>',
						'&nbsp;&nbsp;<div class="sealdisplay-{datedisplaycss}">{date}</div></div>',
						'</td></tr></table>',
					'</div>',
				'</tpl>',
				'<div class="x-clear"></div>'
			);
		}
		else if(this.sealAlign=='bottom')
		{
			this.tpl = new Ext.XTemplate
			(
				'<tpl for=".">',
					'<div class="item-wrap">',
					    '<table border="0" width="100%"><tr><td>',
						'<div class="item-content-{style} sealdisplay-{opiniondisplaycss}">{opinionHtml}</div>',
						'<div class="item-catpion-{style}"><div class="sealdisplay-{usernamedisplaycss}">{username}</div>',
						'&nbsp;&nbsp;<div class="sealdisplay-{datedisplaycss}">{date}</div></div>',
						'</td></tr><tr><td align="center">',
					    '<div class="sealdisplay-{sealdisplaycss}">{sealImg}</div></td></tr></table>',
					'</div>',
				'</tpl>',
				'<div class="x-clear"></div>'
			);
		}
		
		this.dataview = new Ext.DataView
		({
			cls : 'parf-opinion',
			store: this.store,
			style:'overflow:auto;',
			autoScroll : true,
			overClass:'x-view-over',
			tpl: this.tpl,
			multiSelect : false,
			singleSelect : false,
			itemSelector : 'div.item-wrap',
			prepareData: function(data)
			{
				data.style = data.isnew ? "new" : "old";
				data.opinionHtml = data.opinion.replace( /&/g ,'&amp;');
				data.opinionHtml = data.opinionHtml.replace( / /g ,'&nbsp;');
				data.opinionHtml = data.opinionHtml.replace( /</g ,'&lt;');
				data.opinionHtml = data.opinionHtml.replace( />/g ,'&gt;');
				data.opinionHtml = data.opinionHtml.replace( /\n/g ,'<br>');
				
				if(data.sealId)
				{
					data.sealImg = "<img src='/parm/seal/sealmanage.json?actionName=sealpic&sealId="+data.sealId+"'>";
				}
				else
				{
					data.sealImg = '';
				}		
				if(data.sealdisplay)  data.sealdisplaycss = 'on';
				else   data.sealdisplaycss = 'off';
				
				if(data.opiniondisplay) data.opiniondisplaycss = 'on';
				else  data.opiniondisplaycss = 'off';
				
				if(data.usernamedisplay) data.usernamedisplaycss = 'on';
				else  data.usernamedisplaycss = 'off';
				
				if(data.datedisplay) data.datedisplaycss = 'on';
				else  data.datedisplaycss = 'off';
				return data;
			}
		});
		
		this.btnHis = new Ext.menu.CheckItem
		({
			text: '历史签章',
			tooltip:'显示或隐藏历史签章',
			checked: false,
			disabled : true,
			listeners : 
            {
                'checkchange' : { fn: this.filterOpinion, scope: this }
            }
		});
		
		this.displaySeal = new Ext.menu.CheckItem
		({
			text: '签章',
			tooltip:'显示签章',
			checked: true,			
			listeners : 
            {
                'checkchange' : { fn: this.evt_displaySeal, scope: this }
            }
		});
		
		this.displayOpinion = new Ext.menu.CheckItem
		({
			text: '意见',
			tooltip:'显示意见',
			checked: true,			
			listeners : 
            {
                'checkchange' : { fn: this.evt_displayOpinion, scope: this }
            }
		});
		
		this.displayName = new Ext.menu.CheckItem
		({
			text: '签章人姓名',
			tooltip:'显示签章人姓名',
			checked: true,			
			listeners : 
            {
                'checkchange' : { fn: this.evt_displayUsername, scope: this }
            }
		});

		this.displayDate = new Ext.menu.CheckItem
		({
			text: '签章时间',
			tooltip:'显示签章时间',
			checked: true,			
			listeners : 
            {
                'checkchange' : { fn: this.evt_displayDate, scope: this }
            }
		});
		
		this.toolbar = new Ext.Toolbar
		({
			items:
			[
				'->',		
				{
					text: '显示选项',
					iconCls:'btn-setting',
					menu: [this.btnHis,this.displaySeal,this.displayOpinion,this.displayName,this.displayDate]					
				},				
				'-',
				this.btnAdd = new Ext.Button
				({
					text: '意见',
					iconCls:'btn-add',
					tooltip:'填写处理意见',
					handler : this.openOpinionWin,
					scope: this
				}),
				'-',
				this.doSeal = new Ext.Button
				({
					text: '盖章',
					iconCls:'mnu-medal_gold_1',
					tooltip:'盖章',
					handler : this.openSealWin,
					scope: this
				}),
				'-',
				this.cancelSeal = new Ext.Button
				({
					text: '撤销',
					iconCls:'btn-delete',
					tooltip:'撤销盖章',
					handler : this.cancelSeal,
					scope: this
				})
			]
		});
	},
	evt_displaySeal:function(item,checked)
	{
		this.setStoreDisplay('sealdisplay',checked);		
	},
	evt_displayOpinion:function(item,checked)
	{
		this.setStoreDisplay('opiniondisplay',checked);		
	},	
	evt_displayUsername:function(item,checked)
	{
		this.setStoreDisplay('usernamedisplay',checked);		
	},
	evt_displayDate:function(item,checked)
	{
		this.setStoreDisplay('datedisplay',checked);		
	},
	setStoreDisplay:function(display, bool)
	{		
		var count = this.store.getCount();
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);			
			rec.set(display,bool);			
		}
	},
	//private
	getName : function()
	{
		return this.rendered && this.hiddenName ? this.hiddenName : this.name || this.id || '';
	},
	onRender : function(ct, position)
	{
		Ext.parf.form.SealField.superclass.onRender.call(this, ct, position);
		
		this.innerPanel = new Ext.Panel
		({
			bodyStyle : "background-color:#FFFFFF;",
			border : false,
			items : 
			[		
			    this.dataview,
				this.hiddenField
			],
			tbar : this.toolbar
		});
		
		this.innerPanel.render(this.el);
		
		if(this.readOnly || this.disabled)
		{
			this.toolbar.hide();
			this.hiddenField.disable();
		}		
	},
	setValue : function(v)
	{		
		this.store.removeAll();
		v = v || [];
		
		this.value = Ext.isString(v) ?  v : Ext.encode(v);
				
		if(Ext.isEmpty(v)) return;
		
		if(Ext.isString(v))v = Ext.decode(v);
		if(!Ext.isArray(v)) v = [v];
		var arr = [];
		var len = v.length;		
		for(var i = 0; i < len; i++)
		{
			var o = v[i];
			arr.push([v[i].opinion, v[i].userid, v[i].username, v[i].date, v[i].isnew, v[i].isadd ? true : false,
					v[i].sealLogId, v[i].sealId,true,true,true,true]);
			if(!v[i].isnew) this.hasHistory = true;
		}
		
		this.store.loadData(arr);
		this.store.filter('isnew',true);
		this.store.multiSort
		([{
			field: 'isadd',
			direction: 'DESC'
		},{
			field: 'isnew',
			direction: 'DESC'
		},{
			field: 'date',
			direction: 'DESC'
		}]);
		
		if(this.hasHistory)
		{
			this.btnHis.enable();
		}
		var hv = this.getAddOpinion();
		if(hv)
		{
			hv = Ext.encode(hv);
		}else{
			hv = "";
		}
		
		this.hiddenField.setValue(hv);
	},
	getValue : function()
	{
		return this.hiddenField.getValue();
	},
	reset : function()
	{
		this.setValue(this.originalValue);		
		this.clearInvalid();
	},
	onDisable : function()
	{
		this.btnAdd.hide();
		this.doSeal.hide();
		this.cancelSeal.hide();
		this.hiddenField.disable();
	},
	onEnable : function()
	{
		if(this.readOnly) return;
		this.btnAdd.show();
		this.doSeal.show();
		this.cancelSeal.show();
		this.hiddenField.enable();
	},
	setReadOnly : function(readOnly)
	{
		this.readOnly = readOnly;
		return this[readOnly ? 'disable' : 'enable']();
	},
	validate : function()
	{
		if(this.readOnly || this.disabled || this.allowBlank)
		{
			this.clearInvalid();
			return true;
		}
		var rec = this.getAddRec();
		if(rec == null)
		{
			var str1 = "【"+this.fieldLabel+"】不能为空，请点击【盖章】按钮进行盖章 ";
			this.markInvalid(str1);
			return false;
		}
		else
		{
			if(!rec.get('sealLogId'))
			{
				var str1 = "【"+this.fieldLabel+"】没有盖章，请点击【盖章】按钮进行盖章 ";
				this.markInvalid(str1);
				return false;
			}
		}
		this.clearInvalid();
		return true;
	},
	onResize : function(aw, ah, w, h)
	{
		Ext.parf.form.SealField.superclass.onResize.call(this,aw, ah, w, h);
		if(w)this.innerPanel.setWidth(w);
		if(h)this.innerPanel.setHeight(h);
	},	
	filterOpinion : function(item,checked)
	{		
		if(checked)
		{
			this.store.clearFilter();
		}else{
			this.store.filter('isnew',true);
		}
		this.store.multiSort
		([{
			field: 'isadd',
			direction: 'DESC'
		},{
			field: 'date',
			direction: 'DESC'
		}]);
	},
	openOpinionWin : function()
	{	
		if(this.opinionWin == null)
		{
			this.opinionWin = new Ext.parf.form.OpinionWindow();
			this.opinionWin.on("submit",function(v,e)
			{
				var rec = this.getAddRec();
				if(rec != null)
				{
					if(Ext.isEmpty(rec.get('sealLogId')) && Ext.isEmpty(v))
					{
						this.store.remove(rec);
						this.hiddenField.setValue(null);
						return;
					}
					rec.set('opinion',v);
					rec.set('userid',Ext.parf.UserInfo.userId);
					rec.set('username',Ext.parf.UserInfo.userName);
					rec.set('date',new Date().format(this.dateFormat));	
					
					var obj = {
						opinion : v,
						userid : Ext.parf.UserInfo.userId,
						username : Ext.parf.UserInfo.userName,
						date : new Date().format(this.dateFormat),
						isnew : rec.get('isnew'),
						isadd : rec.get('isadd'),
						sealLogId : rec.get('sealLogId'),
						sealId : rec.get('sealId')
					};					
					this.hiddenField.setValue(Ext.encode(obj));
				}
				else
				{
					if(Ext.isEmpty(v))
					{
						return;
					}
					var obj = 
					{
						opinion : v,
						userid : Ext.parf.UserInfo.userId,
						username : Ext.parf.UserInfo.userName,
						date : new Date().format(this.dateFormat),
						isnew : true,
						isadd : true,
						sealdisplay:true,
						opiniondisplay:true,
						usernamedisplay:true,
						datedisplay:true
					};							
					this.hiddenField.setValue(Ext.encode(obj));
					
					var recType = this.store.recordType;
					var p = new recType(obj);
					this.store.addSorted(p);
					this.store.multiSort
					([{
						field: 'isadd',
						direction: 'DESC'
					},{
						field: 'date',
						direction: 'DESC'
					}]);
				}
			},this);			
		}
		
		var rec = this.getAddRec();
		if(rec!=null)
		{
			this.opinionWin.setValue(rec.get('opinion') );
		}
		else
		{
			this.opinionWin.setValue('');
		}		
		this.opinionWin.show();
	},
	getAddRec : function()
	{
		var count = this.store.getCount();
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			if(rec.get('isadd'))
			{
				return rec;
			}
		}
		return null;
	},
	getAddOpinion : function()
	{
		var count = this.store.getCount();
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			if(rec.get('isadd'))
			{
				return {
					opinion : rec.get('opinion'),
					userid : rec.get('userid'),
					username : rec.get('username'),
					date : rec.get('date'),
					isnew : rec.get('isnew'),
					isadd : rec.get('isadd'),
					sealLogId : rec.get('sealLogId'),
					sealId : rec.get('sealId')
				};
			}
		}
		return null;
	},	
	openSealWin:function()
	{
		if(this.sealWin==null)
		{
			this.sealWin = new Ext.parf.form.SealWindow();
			this.sealWin.on("submit",function(sealLogId,sealId)
			{	
				var rec = this.getAddRec();
				if(rec != null)
				{
					rec.set('sealId',sealId);
					rec.set('sealLogId',sealLogId);
					rec.set('userid',Ext.parf.UserInfo.userId);
					rec.set('username',Ext.parf.UserInfo.userName);
					rec.set('date',new Date().format(this.dateFormat));	
					
					var obj = 
					{
						opinion : rec.get('opinion'),
						userid : Ext.parf.UserInfo.userId,
						username : Ext.parf.UserInfo.userName,
						date : new Date().format(this.dateFormat),
						isnew : rec.get('isnew'),
						isadd : rec.get('isadd'),
						sealLogId : sealLogId,
						sealId : sealId
					};					
					this.hiddenField.setValue(Ext.encode(obj));
				}
				else
				{
					var obj = 
					{
						opinion : '',
						userid : Ext.parf.UserInfo.userId,
						sealId: sealId,
						sealLogId: sealLogId,
						username : Ext.parf.UserInfo.userName,
						date : new Date().format(this.dateFormat),
						isnew : true,
						isadd : true,
						sealdisplay:true,
						opiniondisplay:true,
						usernamedisplay:true,
						datedisplay:true
					};
					this.hiddenField.setValue(Ext.encode(obj));
					
					var recType = this.store.recordType;
					var p = new recType(obj);
					this.store.addSorted(p);
					this.store.multiSort
					([{
						field: 'isadd',
						direction: 'DESC'
					},{
						field: 'date',
						direction: 'DESC'
					}]);
				}
			},this);
		}
		this.sealWin.show();
	},
	cancelSeal:function()
	{
		var rec2 = this.getAddRec();
		if(rec2 == null || Ext.isEmpty(rec2.get('sealLogId')))
		{
			Ext.parf.showInfo('您还没有盖章');
			return;
		}
		if(this.cancelSealWin == null)
		{
			this.cancelSealWin = new Ext.parf.form.CancelSealWindow();
			this.cancelSealWin.on("submit",function()
			{
				var rec = this.getAddRec();
				if(Ext.isEmpty(rec.get('opinion')))
				{
					this.store.remove(rec);
					this.hiddenField.setValue(null);
					return;
				}
				rec.set('sealId','');
				rec.set('sealLogId','');
				
				var obj = 
				{
					opinion : rec.get('opinion'),
					userid : Ext.parf.UserInfo.userId,
					username : Ext.parf.UserInfo.userName,
					date : new Date().format(this.dateFormat),
					isnew : rec.get('isnew'),
					isadd : rec.get('isadd'),
					sealLogId : '',
					sealId : ''
				};
				this.hiddenField.setValue(Ext.encode(obj));
				
			},this);
		}
		this.cancelSealWin.sealLogId = rec2.get('sealLogId');
		this.cancelSealWin.show();
	},	
	getValue4Html:function()
	{
		var count = this.store.getCount();
		var html = '';
		for(var i = count - 1; i > -1; i--)
		{
			var rec = this.store.getAt(i);
			if(rec.get('isnew') !== true)
			{
				continue;
			}
			var obj = {
				opinion : rec.get('opinion'),
				userid : rec.get('userid'),
				username : rec.get('username'),
				date : rec.get('date'),
				isnew : rec.get('isnew'),
				isadd : rec.get('isadd'),
				sealLogId : rec.get('sealLogId'),
				sealId : rec.get('sealId'),
				sealdisplay : rec.get('sealdisplay'),
				opiniondisplay : rec.get('opiniondisplay'),
				usernamedisplay : rec.get('usernamedisplay'),
				datedisplay : rec.get('datedisplay')
			};
			if(obj.sealLogId)
			{
				obj.sealImg = "<img src='"+ Ext.parm.HostAddr.getAddr() +"/parm/seal/sealmanage.json?actionName=sealpic&sealId="+obj.sealId+"&SSID="+ Ext.parf.UserInfo.ssid+"'>";
			}
			else
			{
				obj.sealImg = '';
			}
			obj.opioninHTML = obj.opinion;
			if(!obj.opioninHTML)  obj.opioninHTML = '';
			obj.opioninHTML = obj.opioninHTML.replace(/\n/g,'<br>');
			obj.opioninHTML = obj.opioninHTML.replace(/\S\s/g,'&nbsp;');
			if(obj.opioninHTML.length > 0 && obj.opioninHTML.lastIndexOf("<br>") < obj.opioninHTML.length)
			{
				obj.opioninHTML += "<br>";
			}
			html = html + this.getHtml(obj);
		}		
		return html;
	},
	getValue4SignDoc : function()
	{
		var count = this.store.getCount();
		var arr = [];
		for(var i = 0; i < count; i++)
		{
			var rec = this.store.getAt(i);
			if(rec.get('isnew') !== true)
			{
				continue;
			}
			var obj = 
			{
				opinion : rec.get('opinion'),
				userid : rec.get('userid'),
				username : rec.get('username'),
				date : rec.get('date'),
				isnew : rec.get('isnew'),
				isadd : rec.get('isadd'),
				sealLogId : rec.get('sealLogId'),
				sealId : rec.get('sealId')
			};	
			var v = {sealpicurl : "", comments : ""};
			if(obj.sealLogId && obj.sealLogId != "")
			{
				v.sealpicurl = Ext.parm.HostAddr.getAddr() +"/parm/seal/sealmanage.json?actionName=sealpic&sealId="+obj.sealId+"&SSID="+ Ext.parf.UserInfo.ssid;
			}
			else
			{
				continue;
			}
			v.comments += (obj.opinion || "") + "\n";
			v.comments += "[" + (obj.username || "") + " ";
			v.comments += (obj.date || "") + "]\n";
			arr.push(v);
		}
		return {sealrec:arr};
	},
	getHtml : function(data)
	{
		var opioninHTML = "";
		if(data.opiniondisplay && data.opioninHTML != "")
		{
			opioninHTML = '<span>{opioninHTML}</span>';
		}
		var sealImg = "";
		if(data.sealdisplay &&  data.sealImg != "")
		{
			sealImg = '{sealImg}';
		}
		var userAndDate = "";
		if(data.usernamedisplay || data.datedisplay)
		{
			if(data.usernamedisplay && data.datedisplay)
			{
				userAndDate = '<span>[{username}&nbsp;{date}]</span>';
			}
			else if(data.usernamedisplay)
			{
				userAndDate = '<span>[{username}]</span>';
			}
			else if(data.datedisplay)
			{
				userAndDate = '<span>[{date}]</span>';
			}
		}
		var tpl = null;
		if(this.sealAlign == 'left')
		{
			tpl = new Ext.XTemplate
			(
				"<table width='100%' border='0' cellspacing='0' cellpadding='0' class='noborder'>",
					"<tr>",
						"<td>" + sealImg + "</td>",
						"<td>" + opioninHTML + userAndDate + "</td>",
					"</tr>",
				"</table>"
			);
		}
		else if(this.sealAlign == 'right')
		{
			tpl = new Ext.XTemplate
			(
				"<table width='100%' border='0' cellspacing='0' cellpadding='0' class='noborder'>",
					"<tr>",
						"<td>" + opioninHTML + userAndDate + "</td>",
						"<td>" + sealImg + "</td>",
					"</tr>",
				"</table>"
			);
		}
		else if(this.sealAlign == 'top')
		{
			tpl = new Ext.XTemplate
			(
				"<table width='100%' border='0' cellspacing='0' cellpadding='0' class='noborder'>",
					"<tr>",
						"<td>" + sealImg + "</td>",
					"</tr>",
					"<tr>",
						"<td>" + opioninHTML + userAndDate + "</td>",
					"</tr>",
				"</table>"
			);
		}
		else
		{
			tpl = new Ext.XTemplate
			(
				"<table width='100%' border='0' cellspacing='0' cellpadding='0' class='noborder'>",
					"<tr>",
						"<td>" + opioninHTML + userAndDate + "</td>",
					"</tr>",
					"<tr>",
						"<td>" + sealImg + "</td>",
					"</tr>",
				"</table>"
			);
		}
		return tpl.apply(data);
	},
	beforeDestroy:function()
	{		
		if(this.cancelSealWin) Ext.destroy(this.cancelSealWin);
		if(this.sealWin) Ext.destroy(this.sealWin);
		if(this.opinionWin) Ext.destroy(this.opinionWin);
		if(this.innerPanel)this.innerPanel.destroy(); 
		Ext.parf.form.SealField.superclass.beforeDestroy.call(this);
	}
});

Ext.reg('parf-seal', Ext.parf.form.SealField);

Ext.parf.form.SealWindow = Ext.extend(Ext.Window,
{
	g_sealurl: '/parm/seal/sealmanage.json',
	
	constructor : function()
	{ 		
		var store = new Ext.data.JsonStore
		({
			url : this.g_sealurl,
			baseParams : {actionName : 'getSealList'},
			autoDestroy: true, 
	        root: '',  
			fields : [				
				{name:'SealId'},
				{name:'SealName'}				
			]
		});
		
		this.sealcombo = new Ext.parf.form.StoreComboBox({
			xtype:'parf-store-combo',
			fieldLabel: '印章名称', //文字显示
			boldLabel:true,
			allrec:false,
			hiddenName: 'SealId',		
			displayField: 'SealName',		
			valueField: 'SealId',
			width: 250,
			store: store
		});
		this.sealcombo.on("select",this.evt_combo_select,this);	
		
		this.sealImage = new Ext.BoxComponent
		({
		    fieldLabel: '<b>印章图片</b>',
		    boldLabel:true,
		    name:'selectImageName',
		    style:'padding-top:3px;padding-bottom:3px',		    
			autoEl: 
			{
		        tag: 'img',
		        src: '/ext/images/default/s.gif'
		    }
		});
		
		this.passwordText = new Ext.form.TextField({
			 fieldLabel: '印章密码',
			 boldLabel:true,
			 name:'sealpassword',
			 width: 250,
			 inputType: 'password'
		});
		
		Ext.parf.form.SealWindow.superclass.constructor.call(this,
		{
			title : "选择印章",
			height : 380,
			width : 380,
			layout:'fit',
			buttonAlign :'center',
			closeAction:'hide',
			plain: true,
			modal:true,
			resizable:false,
			border : false,
			items:
			[{
				xtype:'panel',
				autoScroll:true,
				cls:'x-panel-mc',
				style: 'padding-top:0px;',
				border:true,
				items:
				[
					this.formPanel = new Ext.form.FormPanel
					({
						style: 'padding: 5 5 5 5',
						labelWidth : 65,
						items:
						[
						 	this.sealcombo,
						 	{
						 		xtype:'panel',
						 		fieldLabel: '<b>印章图片</b>',
						 		style: 'background-color:white;border: 1px solid #8db2e3;',
						 		items: this.sealImage,
						 		width: 250,
						 		height: 250
						 	},						 	
						 	this.passwordText
						]
					})
				]					
			}],
			buttons : 
			[{
				text: '确定',
				iconCls: 'btn-accept',
				handler: this.submit_onclick,
				scope:this
			},{
				text: '取消',
				iconCls: 'btn-stop',
				handler: this.cancle_onclick,
				scope:this
			}]
		});
	},
	initComponent: function()
	{
		this.addEvents('submit');  
		Ext.parf.form.SealWindow.superclass.initComponent.call(this); 
	},	
	evt_combo_select:function()
	{
		var sealId = this.sealcombo.getValue();		
		this.sealImage.el.dom.src = this.g_sealurl + "?actionName=sealpic&sealId=" + sealId;
	},
	submit_onclick : function(btn,e)
	{		
		var sealId = this.sealcombo.getValue();
		if(sealId=='')
		{
			Ext.parf.showInfo('请选择印章');
			return;
		}
		var password = this.passwordText.getValue();
		if(password=='')
		{
			Ext.parf.showInfo('请输入印章密码');
			return;
		}
		Ext.Ajax.request( 
        { 
            url: this.g_sealurl, 
            params:
            {
                actionName:'doSeal',
                sealId: sealId,
                password: password
            }, 
            method: 'post', 
            success: this.dosealOption_success,
            scope: this
        });                          		
	},
	dosealOption_success:function(response, opts)
	{
		var seallogId = response.responseText;
		if(seallogId=='-1')
		{
			Ext.parf.showInfo('印章密码错误');
			return;
		}
		this.fireEvent('submit',seallogId,opts.params.sealId);
		this.passwordText.setValue('');
		this.hide();
	},
	cancle_onclick : function(btn,e)
	{
		this.passwordText.setValue('');
		this.hide();
	}
});


Ext.parf.form.CancelSealWindow = Ext.extend(Ext.Window,
{
	g_sealurl: '/parm/seal/sealmanage.json',
	sealLogId: null,
	
	constructor : function()
	{ 						
		this.passwordText = new Ext.form.TextField({
			 fieldLabel: '印章密码',
			 boldLabel:true,
			 name:'sealpassword',
			 width: 100,
			 inputType: 'password'
		});
		
		Ext.parf.form.CancelSealWindow.superclass.constructor.call(this,
		{
			title : "撤销印章",
			height : 100,
			width : 250,
			layout:'fit',
			buttonAlign :'center',
			closeAction:'hide',
			plain: true,
			modal:true,
			resizable:false,
			border : false,
			items:
			[{
				xtype:'panel',
				autoScroll:true,
				cls:'x-panel-mc',
				style: 'padding-top:0px;',
				border:true,
				items:
				[
					this.formPanel = new Ext.form.FormPanel
					({
						style: 'padding: 5 5 5 5',
						labelWidth : 65,
						items:
						[						 					 	
						 	this.passwordText
						]
					})
				]					
			}],
			buttons : 
			[{
				text: '确定',
				iconCls: 'btn-accept',
				handler: this.submit_onclick,
				scope:this
			},{
				text: '取消',
				iconCls: 'btn-stop',
				handler: this.cancle_onclick,
				scope:this
			}]
		});
	},
	initComponent: function()
	{
		this.addEvents('submit');  
		Ext.parf.form.SealWindow.superclass.initComponent.call(this); 
	},	
	submit_onclick : function(btn,e)
	{				
		var password = this.passwordText.getValue();
		if(password=='')
		{
			Ext.parf.showInfo('请输入印章密码');
			return;
		}
		Ext.Ajax.request( 
        { 
            url: this.g_sealurl, 
            params:
            {
                actionName:'cancelSeal',
                sealLogId: this.sealLogId,
                password: password
            }, 
            method: 'post', 
            success: this.cancelSealOption_success,
            scope: this
        });                          		
	},
	cancelSealOption_success:function(response, opts)
	{
		var result = response.responseText;
		if(result=='-1')
		{
			Ext.parf.showInfo('印章密码错误');
			return;
		}
		this.fireEvent('submit');
		this.passwordText.setValue('');
		this.hide();
	},
	cancle_onclick : function(btn,e)
	{
		this.passwordText.setValue('');
		this.hide();
	}
});