Ext.namespace('Ext.parf.form');
Ext.parf.form.DisplayMacroField = Ext.extend(Ext.form.DisplayField,{   	
	msgTarget : 'qtip',
	allowBlank : false,
	dispaly : false,
	readOnly : false,
	
	onRender : function(ct, position)
	{
		Ext.parf.form.DisplayMacroField.superclass.onRender.call(this, ct, position);
		var hiddenTag = {tag: "input", type: "hidden", name: this.name, value: ""};
		this.hiddenField = this.el.createChild(hiddenTag);		        
	},
	setValue : function(str)
	{
		if(this.readOnly === true && "99" != this.macrotype)
    	{
    		str = "";
    	}
    	else if(Ext.isEmpty(str))
		{
			this.originalValue = Ext.parf.form.MacroUtil.getValue(this.macrotype);
			str = this.originalValue;
		}
		this.hiddenField.dom.value = str;
		var text = Ext.parf.form.MacroUtil.getDispalyText(str);
		Ext.parf.form.DisplayMacroField.superclass.setValue.call(this,text);
    },
    getValue : function()
    {
    	return this.hiddenField.dom.value;
    },
	validate : function()
	{
		if(this.dispaly === true)
		{
			this.clearInvalid();
			return true;
		}
		var v = this.getValue();
		if(Ext.isEmpty(v) && !this.allowBlank)
		{
			if(this.macrotype == "99")//数据源
			{
				this.markInvalid("该项为必输项，请选择！");
				return false;
			}
		}
		this.clearInvalid();
		return true;
	}
});

Ext.parf.form.MacroUtil = function(){ 
	return {
			
		ssid: null,
		
		getValue: function(macroType)
		{
			var str = '';
			//当前日期
			if(macroType=='01') str = ''+Ext.util.Format.javaDate(new Date(),'Y-m-d')+'';
			//当前月份
			if(macroType=='02') str = ''+Ext.util.Format.javaDate(new Date(),'Y-m')+'';
			//当前年份
			if(macroType=='03') str = ''+Ext.util.Format.javaDate(new Date(),'Y')+'';
			//当前用户名名称
			if(macroType=='04') str = ''+Ext.part.UserInfo.userName+'';
			//当前部门名称
			if(macroType=='05') str = ''+Ext.part.UserInfo.deptName+'';
			//当前职位名称
			if(macroType=='06') str = ''+Ext.part.UserInfo.jobTitleName+''; 
			//当前用户ID
			if(macroType=='07') str = ''+Ext.part.UserInfo.userId+''; 
			//当前用户
			if(macroType=='08')
			{
				str = 'U[' + Ext.part.UserInfo.userId + ']<' + Ext.part.UserInfo.userName + '>';
			}
			//当前部门ID
			if(macroType=='09') str = ''+Ext.part.UserInfo.deptId+''; 
			//当前部门
			if(macroType=='10')
			{
				str = 'D[' + Ext.part.UserInfo.deptId + ']<' + Ext.part.UserInfo.deptName + '>';
			}
			//当前职位ID
			if(macroType=='11') str = ''+Ext.part.UserInfo.jobTitleId+'';
			//当前职位
			if(macroType=='12')
			{
				str = 'J[' + Ext.part.UserInfo.jobTitleId + ']<' + Ext.part.UserInfo.jobTitleName + '>';
			}
			//当前组织机构编号
			if(macroType=='13')
			{
				str = '' + Ext.part.UserInfo.orgId + '';
			}
			//当前组织机构
			if(macroType=='14')
			{
				str = 'O[' + Ext.part.UserInfo.orgId + ']<' + Ext.part.UserInfo.orgName + '>';
			}
			return str;
		},
		getDispalyText : function(v)
		{
			var str = v;
    		if(Ext.isEmpty(str))
			{	
				str = '';
			}
			else if(Ext.isString(str) && (str.indexOf('DS[') > -1 || str.indexOf('U[') > -1 || str.indexOf('D[') > -1 || str.indexOf('O[') > -1 || str.indexOf('J[') > -1))
			{
				str = new Ext.part.form.userselection.ValueObjArray(str).getDisplayText();
			}
			else if(str.getDisplayText)
			{
				str = str.getDisplayText();
			}
    		return str;
		}
	};
}();

Ext.reg('parf-macrofield', Ext.parf.form.DisplayMacroField);

Ext.namespace('Ext.parf.form');

Ext.parf.form.WFIncreField = Ext.extend(Ext.form.DisplayField,
{
	dispaly : false,
	readOnly : false,
	
	onRender: function(ct, position)
	{
		Ext.parf.form.WFIncreField.superclass.onRender.call(this, ct, position);
		var hiddenTag = {tag: "input", type: "hidden", name: this.name, value: ""};
		this.hiddenField = this.el.createChild(hiddenTag);
	},
	setValue : function(str)
	{
		if(this.readOnly === true)
		{
			str = "";
		}
		else
		{
			if(Ext.isEmpty(str))
			{
				str = Ext.parf.form.WFIncreUtil.getNextValue(this.key);
				this.originalValue = str;
			}
		}
		this.hiddenField.dom.value = str;
		Ext.parf.form.WFIncreField.superclass.setValue.call(this,str);
	},
	getValue:function()
	{
		return this.hiddenField.dom.value;
	}
});

Ext.parf.form.WFIncreUtil = function(){ 
	return {
			
		getNextValue: function(key)
		{
			var url = "/parm/workflow/handle/wfdata.json?actionName=getIncreId&KEY=" + key;
			var res = Ext.parf.syncRequest(url,"");
			res = Ext.decode(res);
			if(res.success)
			{
				return res.data.ID;
			}
			return "";
		}
	};
}();

Ext.reg('parm-wfincrefield', Ext.parf.form.WFIncreField);