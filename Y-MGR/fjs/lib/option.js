Ext.namespace("Ext.fjs.data");
Ext.fjs.data.DictStore=Ext.extend(Ext.data.Store,{
	constructor:function(id,b){
		var c,d={action:"getDictList",Sid:id};if(b){d=Ext.apply(d,b)}
		c={url:"dic.do",autoDestroy:true,baseParams:d,reader:new Ext.data.JsonReader({root:"",fields:["REFVALUE","REFID","DEFCHECK"]})};
		Ext.fjs.data.DictStore.superclass.constructor.call(this,c);
	}
});
Ext.namespace("Ext.fjs.form");
Ext.fjs.form.DateField=Ext.extend(Ext.form.DateField,{
	format:"Y-m-d",value:new Date(),width:90,
	initComponent:function(){
	},
	getValue:function(){
		return this.parseDate(Ext.form.DateField.superclass.getValue.call(this))||"";
	},
	setValue:function(b){
		if(Ext.isDate(b)){
			return Ext.fjs.form.DateField.superclass.setValue.call(this,b)
		}
		var c;
		if(Ext.isEmpty(b)){
			return;
		}else if(Ext.isEmpty(b.time)){
			if(Ext.isIE9){
				c=Date.parseDate(b,this.format);
			}else{
				c=new Date(b);
			}
		}else{
			c=new Date(b.time);
		}
		return Ext.fjs.form.DateField.superclass.setValue.call(this,c);
	},
	emptyValue:function(){
		return Ext.form.DateField.superclass.setValue.call(this,"");
	}
});
Ext.reg("fjs-datefield",Ext.fjs.form.DateField);
Ext.fjs.form.ftdField=Ext.extend(Ext.form.CompositeField,{
	showOption:true,optionData:[[0,"清空",0,0],[1,"最近一周",Date.DAY,-7],[2,"最近一月",Date.MONTH,-1],[3,"最近两月",Date.MONTH,-2],[4,"最近三月",Date.MONTH,-3],[5,"近一年",Date.YEAR,-1]],optionWidth:80,width:320,defIndex:1,
	initComponent:function(){
		var c={width:90,allowBlank:true,format:"Y-m-d",vtype:"daterange",parent:this,name:"StartDate",value:new Date()};
		if(!this.fromconf){this.fromconf=c}else{this.fromconf=Ext.applyIf(this.fromconf,c)}
		this.fdField=new Ext.fjs.form.DateField(this.fromconf);
		var b={width:90,allowBlank:true,format:"Y-m-d",vtype:"daterange",parent:this,name:"EndDate",value:new Date()};
		if(!this.toconf){this.toconf=b}else{this.toconf=Ext.applyIf(this.toconf,b)}
		this.tdField=new Ext.fjs.form.DateField(this.toconf);
		this.optStore=new Ext.data.ArrayStore({fields:["value","text","interval","number"],data:this.optionData});
		this.optField=new Ext.form.ComboBox({mode:"local",editable:false,triggerAction:"all",store:this.optStore,value:1,submitValue:false,width:this.optionWidth,valueField:"value",displayField:"text"});
		this.optField.on("afterrender",this.evt_optField_afterrender,this);
		this.optField.on("select",this.evt_optField_select,this);
		var M=Ext.msg.text;
		if(this.showOption){
			this.items=[{xtype:"displayfield",value:M.from},this.fdField,{xtype:"displayfield",value:M.to},this.tdField,this.optField];
		}else{
			this.items=[{xtype:"displayfield",value:M.from},this.fdField,{xtype:"displayfield",value:M.to},this.tdField];this.width=240;
		}
		Ext.fjs.form.ftdField.superclass.initComponent.call(this);
	},
	fd:function(f){return this.evt(this.fdField,f)},
	td:function(f){return this.evt(this.tdField,f)},
	evt:function(a,f){var v=a.getValue();if(v&&v!=""){return new Date(v).format(f)}else{return ""}},
	evt_optField_afterrender:function(b){
		b.fireEvent("select",b,b.store.getAt(this.defIndex),this.defIndex);
	},
	evt_optField_select:function(e,b,c){
		if(c==0){
			this.fdField.emptyValue();
			this.tdField.emptyValue();
		}else{
			this.tdField.setValue(new Date());
			var d=this.tdField.getValue();
			d=d.add(b.get("interval"),b.get("number"));
			this.fdField.setValue(d);
		}
	},
	beforeDestroy:function(){
		this.optStore.destroy();Ext.destroy(this.optField);
		Ext.fjs.form.ftdField.superclass.beforeDestroy.call(this);
	}
});
Ext.reg("fjs-fromto",Ext.fjs.form.ftdField);
Ext.fjs.form.ItemSelector=Ext.extend(Ext.form.Field,{
	imgPath:"/ext/images/",iconUp:"up2.gif",iconDown:"down2.gif",iconLeft:"left2.gif",iconRight:"right2.gif",iconTop:"top2.gif",iconBottom:"bottom2.gif",
	hideNavIcons:false,drawUpIcon:true,drawDownIcon:true,drawLeftIcon:true,drawRightIcon:true,drawTopIcon:true,drawBotIcon:true,delimiter:',',
	bodyStyle:null,border:false,defaultAutoCreate:{tag:"div"},multiselects:null,
	initComponent:function(){
		Ext.fjs.form.ItemSelector.superclass.initComponent.call(this);
		this.addEvents({"change":true,"rowdblclick":true});
	},
	onRender:function(ct,position){
		Ext.fjs.form.ItemSelector.superclass.onRender.call(this,ct,position);
		var config=[{legend:"备选",draggable:true,droppable:true,width:100,height:100},{legend:"已选",draggable:true,droppable:true,width:100,height:100}];
		this.fromMultiselect=new Ext.fjs.form.MultiSelect(Ext.applyIf(this.multiselects[0],config[0]));
		this.fromMultiselect.on("dblclick",this.onRowDblClick,this);
		this.toMultiselect=new Ext.fjs.form.MultiSelect(Ext.applyIf(this.multiselects[1],config[1]));
		this.toMultiselect.on("dblclick",this.onRowDblClick,this);
		var P=this.ownerPanel=new Ext.Panel({bodyStyle:this.bodyStyle,border:this.border,layout:"table",layoutConfig:{columns:3}});
		P.add(this.fromMultiselect);
		var icons=this.iconsPanel=new Ext.Panel({header:false});
		P.add(icons);
		P.add(this.toMultiselect);
		P.render(this.el);
		icons.el.down('.'+icons.bwrapCls).remove();
		if(this.imgPath!=""&&this.imgPath.charAt(this.imgPath.length-1)!='/') this.imgPath+='/';
		this.iconUp=this.imgPath+(this.iconUp||"up2.gif");
		this.iconDown=this.imgPath+(this.iconDown||"down2.gif");
		this.iconLeft=this.imgPath+(this.iconLeft||"left2.gif");
		this.iconRight=this.imgPath+(this.iconRight||"right2.gif");
		this.iconTop=this.imgPath+(this.iconTop||"top2.gif");
		this.iconBottom=this.imgPath+(this.iconBottom||"bottom2.gif");
		var el=icons.getEl();
		this.toTopIcon=el.createChild({tag:"img",src:this.iconTop,style:{cursor:"pointer",margin:"2px"}});
		el.createChild({tag:"br"});
		this.upIcon=el.createChild({tag:"img",src:this.iconUp,style:{cursor:"pointer",margin:"2px"}});
		el.createChild({tag:"br"});
		this.addIcon=el.createChild({tag:"img",src:this.iconRight,style:{cursor:"pointer",margin:"2px"}});
		el.createChild({tag:"br"});
		this.removeIcon=el.createChild({tag:"img",src:this.iconLeft,style:{cursor:"pointer",margin:"2px"}});
		el.createChild({tag:"br"});
		this.downIcon=el.createChild({tag:"img",src:this.iconDown,style:{cursor:"pointer",margin:"2px"}});
		el.createChild({tag:"br"});
		this.toBottomIcon=el.createChild({tag:"img",src:this.iconBottom,style:{cursor:"pointer",margin:"2px"}});
		this.toTopIcon.on("click",this.toTop,this);
		this.upIcon.on("click",this.up,this);
		this.downIcon.on("click",this.down,this);
		this.toBottomIcon.on("click",this.toBottom,this);
		this.addIcon.on("click",this.fromTo,this);
		this.removeIcon.on("click",this.toFrom,this);
		if(!this.drawUpIcon||this.hideNavIcons){this.upIcon.dom.style.display="none"}
		if(!this.drawDownIcon||this.hideNavIcons){this.downIcon.dom.style.display="none"}
		if(!this.drawLeftIcon||this.hideNavIcons){this.addIcon.dom.style.display="none"}
		if(!this.drawRightIcon||this.hideNavIcons){this.removeIcon.dom.style.display="none"}
		if(!this.drawTopIcon||this.hideNavIcons){this.toTopIcon.dom.style.display="none"}
		if(!this.drawBotIcon||this.hideNavIcons){this.toBottomIcon.dom.style.display="none"}
		this.el.setWidth(P.body.first().getWidth());
		P.body.removeClass();
		this.hiddenName=this.name;
		this.hiddenField=this.el.createChild({tag:"input",type:"hidden",value:"",name:this.name});
	},
	doLayout:function(){
		if(this.rendered){
			this.fromMultiselect.fs.doLayout();
			this.toMultiselect.fs.doLayout();
		}
	},
	afterRender:function(){
		Ext.fjs.form.ItemSelector.superclass.afterRender.call(this);
		this.toStore=this.toMultiselect.store;
		this.toStore.on("add",this.changed,this);
		this.toStore.on("remove",this.changed,this);
		this.toStore.on("load",this.changed,this);
		this.changed(this.toStore);
	},
	toTop:function(){
		var rs=[],arr=this.toMultiselect.view.getSelectedIndexes();
		if(arr.length>0){
			arr.sort();
			for(var i=0,j=arr.length;i<j;i++){
				rs.push(this.toMultiselect.view.store.getAt(arr[i]));
			}
			arr=[];
			for(var i=rs.length-1;i>-1;i--){
				this.toMultiselect.view.store.remove(rs[i]);
				this.toMultiselect.view.store.insert(0,rs[i]);
				arr.push(((rs.length-1)-i));
			}
		}
		this.toMultiselect.view.refresh();
		this.toMultiselect.view.select(arr);
	},
	toBottom:function(){
		var rs=[],arr=this.toMultiselect.view.getSelectedIndexes();
		if(arr.length>0){
			arr.sort();
			for(var i=0,j=arr.length;i<j;i++){
				rs.push(this.toMultiselect.view.store.getAt(arr[i]));
			}
			arr=[];
			for(var i=0,j=rs.length;i<j;i++){
				this.toMultiselect.view.store.remove(rs[i]);
				this.toMultiselect.view.store.add(rs[i]);
				arr.push((this.toMultiselect.view.store.getCount())-(j-i));
			}
		}
		this.toMultiselect.view.refresh();
		this.toMultiselect.view.select(arr);
	},
	up:function(){
		var r=null,rs=[],arr=this.toMultiselect.view.getSelectedIndexes();
		arr.sort();
		if(arr.length>0){
			for(var i=0,j=arr.length;i<j;i++){
				r=this.toMultiselect.view.store.getAt(arr[i]);
				if((arr[i]-1)>=0){
					this.toMultiselect.view.store.remove(r);
					this.toMultiselect.view.store.insert(arr[i]-1,r);
					rs.push(arr[i]-1);
				}
			}
			this.toMultiselect.view.refresh();
			this.toMultiselect.view.select(rs);
		}
	},
	down:function(){
		var r=null,rs=[],arr=this.toMultiselect.view.getSelectedIndexes();
		arr.sort();arr.reverse();
		if(arr.length>0){
			for(var i=0,j=arr.length;i<j;i++){
				r=this.toMultiselect.view.store.getAt(arr[i]);
				if((arr[i]+1)<this.toMultiselect.view.store.getCount()){
					this.toMultiselect.view.store.remove(r);
					this.toMultiselect.view.store.insert(arr[i]+1,r);
					rs.push(arr[i]+1);
				}
			}
			this.toMultiselect.view.refresh();
			this.toMultiselect.view.select(rs);
		}
	},
	fromTo:function(){
		var r,rs=[],arr=this.fromMultiselect.view.getSelectedIndexes();
		if(arr.length>0){
			for(var i=0,j=arr.length;i<j;i++){
				rs.push(this.fromMultiselect.view.store.getAt(arr[i]));
			}
			if(!this.allowDup)arr=[];
			for(var i=0,j=rs.length;i<j;i++){
				r=rs[i];
				if(this.allowDup){
					var x=new Ext.data.Record();
					r.id=x.id;delete x;
					this.toMultiselect.view.store.add(r);
				}else{
					this.fromMultiselect.view.store.remove(r);
					this.toMultiselect.view.store.add(r);
					arr.push((this.toMultiselect.view.store.getCount()-1));
				}
			}
		}
		this.toMultiselect.view.refresh();
		this.fromMultiselect.view.refresh();
		var si=this.toMultiselect.store.sortInfo;
		if(si){
			this.toMultiselect.store.sort(si.field,si.direction);
		}
		this.toMultiselect.view.select(arr);
	},
	toFrom:function(){
		var rs=[],arr=this.toMultiselect.view.getSelectedIndexes();
		if(arr.length>0){
			for(var i=0,j=arr.length;i<j;i++){
				rs.push(this.toMultiselect.view.store.getAt(arr[i]));
			}
			arr=[];
			for(var i=0,j=rs.length;i<j;i++){
				this.toMultiselect.view.store.remove(rs[i]);
				if(!this.allowDup){
					this.fromMultiselect.view.store.add(rs[i]);
					arr.push((this.fromMultiselect.view.store.getCount()-1));
				}
			}
		}
		this.fromMultiselect.view.refresh();
		this.toMultiselect.view.refresh();
		var si=this.fromMultiselect.store.sortInfo;
		if(si){
			this.fromMultiselect.store.sort(si.field,si.direction);
		}
		this.fromMultiselect.view.select(arr);
	},
	changed:function(store){
		var sb=[];
		for(var i=0,j=store.getCount();i<j;i++){
			sb.push(store.getAt(i).get(this.toMultiselect.valueField));
		}
		this.hiddenField.dom.value=sb.join(this.delimiter);
		this.fireEvent("change",this,this.getValue(),this.hiddenField.dom.value);
	},
	getValue:function(){
		return this.hiddenField.dom.value;
	},
	onRowDblClick:function(vw,index,node,e){
		if(vw==this.toMultiselect.view){
			this.toFrom();
		}else if(vw==this.fromMultiselect.view){
			this.fromTo();
		}
		return this.fireEvent("rowdblclick",vw,index,node,e);
	},
	reset:function(){
		var range=this.toMultiselect.store.getRange();
		this.toMultiselect.store.removeAll();
		this.fromMultiselect.store.add(range);
		var si=this.fromMultiselect.store.sortInfo;
		if(si){
			this.fromMultiselect.store.sort(si.field,si.direction);
		}
		this.changed(this.toMultiselect.store);
	},
	selectFromAll:function(){
		this.fromMultiselect.view.selectRange(0,Number.MAX_VALUE);
	},
	selectToAll:function(){
		this.toMultiselect.view.selectRange(0,Number.MAX_VALUE);
	},
	beforeDestroy:function(){
		Ext.destroy(this.hiddenField);
		Ext.destroy(this.iconsPanel);
		Ext.destroy(this.fromMultiselect);
		Ext.destroy(this.toMultiselect);
		Ext.destroy(this.ownerPanel);
		Ext.fjs.form.ItemOrder.superclass.beforeDestroy.call(this);
	}
});
Ext.reg("fjs-itemselector", Ext.fjs.form.ItemSelector);
Ext.fjs.form.ItemOrder=Ext.extend(Ext.form.Field,{
	imgPath:"/ext/images/",iconUp:"up2.gif",iconDown:"down2.gif",iconTop:"top2.gif",iconBottom:"bottom2.gif",drawUpIcon:true,drawDownIcon:true,drawTopIcon:true,drawBotIcon:true,
	delimiter:',',bodyStyle:null,border:false,defaultAutoCreate:{tag: "div"},multiselects:null,
	initComponent:function(){
		Ext.fjs.form.ItemOrder.superclass.initComponent.call(this);
		this.addEvents({"change":true,"rowdblclick":true});
	},
	onRender:function(ct,position){
		Ext.fjs.form.ItemOrder.superclass.onRender.call(this,ct,position);
		var config={legend:"",draggable:true,droppable:true,width:100,height:100};
		this.toMultiselect=new Ext.fjs.form.MultiSelect(Ext.applyIf(this.multiselects,config));
		this.toMultiselect.on("dblclick",this.onRowDblClick,this);
		var P=this.ownerPanel=new Ext.Panel({bodyStyle:this.bodyStyle,border:this.border,layout:"table",layoutConfig:{columns:2}});
		var icons=this.iconsPanel=new Ext.Panel({header:false,width:18});
		P.add(this.toMultiselect);P.add(icons);P.render(this.el);
		icons.el.down('.'+icons.bwrapCls).remove();
		if(this.imgPath!=""&&this.imgPath.charAt(this.imgPath.length-1)!='/')this.imgPath+='/';
		this.iconUp=this.imgPath + (this.iconUp||"up2.gif");
		this.iconDown=this.imgPath + (this.iconDown||"down2.gif");
		this.iconTop=this.imgPath + (this.iconTop||"top2.gif");
		this.iconBottom=this.imgPath + (this.iconBottom||"bottom2.gif");
		var el=icons.getEl();
		this.toTopIcon=el.createChild({tag:"img",src:this.iconTop,style:{cursor:"pointer",margin:"2px"}});
		el.createChild({tag:"br"});
		this.upIcon=el.createChild({tag:"img",src:this.iconUp,style:{cursor:"pointer",margin:"2px"}});
		el.createChild({tag:"br"});
		this.downIcon=el.createChild({tag:"img",src:this.iconDown,style:{cursor:"pointer",margin:"2px"}});
		el.createChild({tag:"br"});
		this.toBottomIcon=el.createChild({tag:"img",src:this.iconBottom,style:{cursor:"pointer",margin:"2px"}});
		this.toTopIcon.on("click",this.toTop,this);
		this.upIcon.on("click",this.up,this);
		this.downIcon.on("click",this.down,this);
		this.toBottomIcon.on("click", this.toBottom,this);
		if(!this.drawUpIcon||this.hideNavIcons){this.upIcon.dom.style.display="none"}
		if(!this.drawDownIcon||this.hideNavIcons){this.downIcon.dom.style.display="none"}
		if(!this.drawTopIcon||this.hideNavIcons){this.toTopIcon.dom.style.display="none"}
		if(!this.drawBotIcon||this.hideNavIcons){this.toBottomIcon.dom.style.display="none"}
		this.el.setWidth(P.body.first().getWidth());P.body.removeClass();
		this.hiddenName=this.name;
		this.hiddenField=this.el.createChild({tag:"input",type:"hidden",value:"",name:this.name});
	},
	doLayout: function(){
		if(this.rendered){
			this.toMultiselect.fs.doLayout();
		}
	},
	afterRender:function(){
		Ext.fjs.form.ItemOrder.superclass.afterRender.call(this);
		this.toStore=this.toMultiselect.store;
		this.toStore.on("add",this.changed,this);
		this.toStore.on("remove",this.changed,this);
		this.toStore.on("load",this.changed,this);
		this.changed(this.toStore);
	},
	changed:function(store){
		var sb=[];
		for(var i=0,j=store.getCount();i<j;i++){
			sb.push(store.getAt(i).get(this.toMultiselect.valueField));
		}
		this.hiddenField.dom.value=sb.join(this.delimiter);
		this.fireEvent("change",this,this.getValue(),this.hiddenField.dom.value);
	},
	getValue:function(){
		return this.hiddenField.dom.value;
	},
	selectAll:function(){
		this.toMultiselect.view.selectRange(0,Number.MAX_VALUE);
	},
	toTop:function(){
		var rs=[],arr=this.toMultiselect.view.getSelectedIndexes();
		if(arr.length>0){
			arr.sort();
			for(var i=0;i<arr.length;i++){
				rs.push(this.toMultiselect.view.store.getAt(arr[i]));
			}
			arr=[];
			for(var i=rs.length-1;i>-1;i--){
				this.toMultiselect.view.store.remove(rs[i]);
				this.toMultiselect.view.store.insert(0,rs[i]);
				arr.push(((rs.length-1)-i));
			}
		}
		this.toMultiselect.view.refresh();
		this.toMultiselect.view.select(arr);
	},
	toBottom:function(){
		var rs=[],arr=this.toMultiselect.view.getSelectedIndexes();
		if(arr.length>0){
			arr.sort();
			for(var i=0,j=arr.length;i<j;i++){
				rs.push(this.toMultiselect.view.store.getAt(arr[i]));
			}
			arr=[];
			for(var i=0;i<rs.length;i++){
				this.toMultiselect.view.store.remove(rs[i]);
				this.toMultiselect.view.store.add(rs[i]);
				arr.push((this.toMultiselect.view.store.getCount())-(rs.length-i));
			}
		}
		this.toMultiselect.view.refresh();
		this.toMultiselect.view.select(arr);
	},
	up:function(){
		var r=null,rs=[],arr=this.toMultiselect.view.getSelectedIndexes();
		arr.sort();
		if(arr.length>0){
			for(var i=0;i<arr.length;i++){
				r=this.toMultiselect.view.store.getAt(arr[i]);
				if((arr[i]-1)>=0){
					this.toMultiselect.view.store.remove(r);
					this.toMultiselect.view.store.insert(arr[i]-1,r);
					rs.push(arr[i]-1);
				}
			}
			this.toMultiselect.view.refresh();
			this.toMultiselect.view.select(rs);
		}
	},
	down:function(){
		var r=null,rs=[],arr=this.toMultiselect.view.getSelectedIndexes();
		arr.sort();arr.reverse();
		if(arr.length>0){
			for(var i=0;i<arr.length;i++){
				r=this.toMultiselect.view.store.getAt(arr[i]);
				if((arr[i]+1)<this.toMultiselect.view.store.getCount()){
					this.toMultiselect.view.store.remove(r);
					this.toMultiselect.view.store.insert(arr[i]+1,r);
					rs.push(arr[i]+1);
				}
			}
			this.toMultiselect.view.refresh();
			this.toMultiselect.view.select(rs);
		}
	},
	beforeDestroy:function(){
		Ext.destroy(this.hiddenField);
		Ext.destroy(this.iconsPanel);
		Ext.destroy(this.toMultiselect);		
		Ext.destroy(this.ownerPanel);
		Ext.fjs.form.ItemOrder.superclass.beforeDestroy.call(this);
	}
});
Ext.reg("fjs-itemorder",Ext.fjs.form.ItemOrder);
Ext.util.CSS.swapStyleSheet("fjs-multiselect","/fjs/css/multiselect.css");
Ext.fjs.form.MultiSelect=Ext.extend(Ext.form.Field,{
	ddReorder:false,appendOnly:false,width:100,height:100,displayField:0,valueField:1,storeAutoLoad:true,allowBlank:true,maxSelections:Number.MAX_VALUE,minSelections:0,blankText:Ext.form.TextField.prototype.blankText,
	maxSelectionsText:"最多{0}选项允许被选择",minSelectionsText:"至少{0}个选项必须被选择",delimiter:',',defaultAutoCreate:{tag:"div"},
	initComponent:function(){
		Ext.fjs.form.MultiSelect.superclass.initComponent.call(this);
		if(this.storeurl){
			if(!this.storeParams) this.storeParams={};
			this.store=new Ext.data.Store({url:this.storeurl,baseParams:this.storeParams,autoDestroy:true,reader:new Ext.data.JsonReader({root:"",fields:[this.displayField,this.valueField]})});
		}else{
			if(this.dictId){
				this.store=new Ext.fjs.data.DictStore(this.dictId);
				this.displayField="REFVALUE";
				this.valueField="REFID";
			}else if(!this.store){
				alert("请设置store或者storeurl或者dictId属性！");return;
			}
		}
		if(this.storeAutoLoad&&this.store.getCount()==0){
			this.store.load({});
		}
		this.addEvents({"change":true,"click":true,"dblclick":true,"drop":true});
	},
	onRender:function(ct,position){
		Ext.fjs.form.MultiSelect.superclass.onRender.call(this,ct,position);
		this.view=new Ext.ListView({multiSelect:true,store: this.store,columns:[{header:"Value",width:1,dataIndex:this.displayField}],hideHeaders:true});
		this.fs=new Ext.form.FieldSet({renderTo:this.el,title:this.legend,width:this.width,height:this.height,style:"padding:0px;",items:this.view});
		this.fs.body.addClass("ux-mselect");
		this.view.on("click", this.onViewClick,this);
		this.view.on("beforeclick",this.onViewBeforeClick,this);
		this.view.on("dblclick",this.onViewDblClick,this);
		this.hiddenName=this.name||Ext.id();
		this.hiddenField=this.el.createChild({tag:"input",type:"hidden",value:"",name:this.hiddenName});
		this.hiddenField.dom.disabled=this.hiddenName!=this.name;
		this.fs.doLayout();
	},
	afterRender:function(){
		Ext.fjs.form.MultiSelect.superclass.afterRender.call(this);
		if(this.ddReorder&&!this.dragGroup&&!this.dropGroup){
			this.dragGroup=this.dropGroup="MultiselectDD-"+Ext.id();
		}
		if(this.draggable||this.dragGroup){
			this.dragZone=new Ext.fjs.form.MultiSelect.DragZone(this,{ddGroup:this.dragGroup});
		}
		if(this.droppable||this.dropGroup){
			this.dropZone=new Ext.fjs.form.MultiSelect.DropZone(this,{ddGroup:this.dropGroup});
		}
	},
	onViewClick:function(vw,index,node,e){
		this.fireEvent("change",this,this.getValue(),this.hiddenField.dom.value);
		this.hiddenField.dom.value=this.getValue();
		this.fireEvent("click",this,e);
		this.validate();
	},
	onViewBeforeClick: function(vw,index,node,e){
		if(this.disabled||this.readOnly){
			return false;
		}
	},
	onViewDblClick:function(vw,index,node,e){
		return this.fireEvent("dblclick",vw,index,node,e);
	},
	getValue:function(vf){
		var rs=[],arr=this.view.getSelectedIndexes();
		if(arr.length==0) return "";
		for(var i=0,j=arr.length;i<j;i++){
			rs.push(this.store.getAt(arr[i]).get((vf!=null)?vf:this.valueField));
		}
		return rs.join(this.delimiter);
	},
	setValue:function(vs){
		var index,ss=[];
		this.view.clearSelections();
		this.hiddenField.dom.value="";
		if(!vs||(vs==""))return;
		if(!Ext.isArray(vs)){vs=vs.split(this.delimiter)}
		for(var i=0,j=vs.length;i<j;i++){
			index=this.view.store.indexOf(this.view.store.query(this.valueField,new RegExp('^'+vs[i]+'$',"i")).itemAt(0));
			ss.push(index);
		}
		this.view.select(ss);
		this.hiddenField.dom.value=this.getValue();
		this.validate();
	},
	reset:function(){
		this.setValue("");
	},
	getRawValue:function(f){
		var tmp=this.getValue(f);
		if(tmp.length){
			return tmp.split(this.delimiter);
		}else{
			return [];
		}
	},
	setRawValue:function(vs){
		setValue(vs);
	},
	validateValue:function(value){
		if(value.length<1){ // if it has no value
			if(this.allowBlank){
				this.clearInvalid();return true;
			}else{
				this.markInvalid(this.blankText);return false;
			}
		}
		if(value.length<this.minSelections){
			this.markInvalid(String.format(this.minSelectionsText,this.minSelections));
			return false;
		}
		if(value.length>this.maxSelections){
			this.markInvalid(String.format(this.maxSelectionsText,this.maxSelections));
			return false;
		}
		return true;
	},
	disable:function(){
		this.disabled=true;
		this.hiddenField.dom.disabled=true;
		this.fs.disable();
	},
	enable:function(){
		this.disabled=false;
		this.hiddenField.dom.disabled=false;
		this.fs.enable();
	},
	beforeDestroy:function(){
		this.store.destroy();
		this.view.store.destroy();
		Ext.destroy(this.hiddenField);
		this.view.destroy();
		Ext.destroy(this.fs);
		Ext.destroy(this.dragZone);
		Ext.destroy(this.dropZone);
		Ext.fjs.form.MultiSelect.superclass.beforeDestroy.call(this);
	}
});
Ext.reg("fjs-multiselect",Ext.fjs.form.MultiSelect);
Ext.fjs.form.MultiSelect.DragZone=function(ms,config){
	this.ms=ms;this.view=ms.view;
	var dd,ddGroup=config.ddGroup||"MultiselectDD";
	if(Ext.isArray(ddGroup)){
		dd=ddGroup.shift();
	}else{
		dd=ddGroup;ddGroup=null;
	}
	Ext.fjs.form.MultiSelect.DragZone.superclass.constructor.call(this,this.ms.fs.body,{containerScroll:true,ddGroup:dd});
	this.setDraggable(ddGroup);
};
Ext.extend(Ext.fjs.form.MultiSelect.DragZone,Ext.dd.DragZone,{
	onInitDrag:function(x,y){
		var el=Ext.get(this.dragData.ddel.cloneNode(true));
		this.proxy.update(el.dom);el.setWidth(el.child("em").getWidth());
		this.onStartDrag(x,y);return true;
	},
	collectSelection:function(data){
		data.repairXY=Ext.fly(this.view.getSelectedNodes()[0]).getXY();
		var i=0;
		this.view.store.each(function(rec){
			if(this.view.isSelected(i)){
				var n=this.view.getNode(i);
				var dragNode=n.cloneNode(true);
				dragNode.id=Ext.id();
				data.ddel.appendChild(dragNode);
				data.records.push(this.view.store.getAt(i));
				data.viewNodes.push(n);
			}
		i++},this);
	},
	onEndDrag:function(data,e){
		var d=Ext.get(this.dragData.ddel);
		if(d&&d.hasClass("multi-proxy")){
			d.remove();
		}
	},
	getDragData: function(e){
		var target=this.view.findItemFromChild(e.getTarget());
		if(target){
			if(!this.view.isSelected(target)&&!e.ctrlKey&&!e.shiftKey){
				this.view.select(target);
				this.ms.setValue(this.ms.getValue());
			}
			if(this.view.getSelectionCount()==0||e.ctrlKey||e.shiftKey) return false;
			var dds={sourceView:this.view,viewNodes:[],records:[]};
			if(this.view.getSelectionCount()==1){
				var i=this.view.getSelectedIndexes()[0];
				var n=this.view.getNode(i);
				dds.viewNodes.push(dds.ddel=n);
				dds.records.push(this.view.store.getAt(i));
				dds.repairXY=Ext.fly(n).getXY();
			}else{
				dds.ddel=document.createElement("div");
				dds.ddel.className="multi-proxy";
				this.collectSelection(dds);
			}
			return dds;
		}
		return false;
	},
	getRepairXY:function(e){
		return this.dragData.repairXY;
	},
	setDraggable:function(dg){
		if(!dg) return;
		if(Ext.isArray(dg)){
			Ext.each(dg,this.setDraggable,this);return;
		}else{
			this.addToGroup(dg);
		}
	}
});
Ext.fjs.form.MultiSelect.DropZone=function(ms,config){
	this.ms=ms;this.view=ms.view;
	var dd,dg=config.ddGroup||"MultiselectDD";
	if(Ext.isArray(dg)){
		dd=dg.shift();
	}else{
		dd=dg;dg=null;
	}
	Ext.fjs.form.MultiSelect.DropZone.superclass.constructor.call(this,this.ms.fs.body,{containerScroll:true,ddGroup:dd});
	this.setDroppable(dg);
};
Ext.extend(Ext.fjs.form.MultiSelect.DropZone,Ext.dd.DropZone,{
	getTargetFromEvent:function(e){
		return e.getTarget();
	},
	getDropPoint:function(e,n,dd){
		if(n==this.ms.fs.body.dom){return "below"}
		var t=Ext.lib.Dom.getY(n),b=t+n.offsetHeight;
		var c=t+(b-t)/2;
		var y=Ext.lib.Event.getPageY(e);
		return (y<=c)?"above":"below";
	},
	isValidDropPoint:function(pt,n,data){
		if(!data.viewNodes||(data.viewNodes.length!=1)){
			return true;
		}
		var d=data.viewNodes[0];
		if(d==n) return false;
		if((pt=="below")&&(n.nextSibling==d)){
			return false;
		}
		if((pt=="above")&&(n.previousSibling==d)){
			return false;
		}
		return true;
	},
	onNodeEnter:function(n,dd,e,data){
		return false;
	},
	onNodeOver:function(n,dd,e,data){
		var dragElClass=this.dropNotAllowed;
		var pt=this.getDropPoint(e,n,dd);
		if(this.isValidDropPoint(pt,n,data)){
			if(this.ms.appendOnly){
				return "x-tree-drop-ok-below";
			} // set the insert point style on the target node
			if(pt){
				var targetElClass;
				if (pt=="above"){
					dragElClass=n.previousSibling ? "x-tree-drop-ok-between" : "x-tree-drop-ok-above";
					targetElClass="x-view-drag-insert-above";
				}else{
					dragElClass=n.nextSibling ? "x-tree-drop-ok-between" : "x-tree-drop-ok-below";
					targetElClass="x-view-drag-insert-below";
				}
				if(this.lastInsertClass!=targetElClass){
					Ext.fly(n).replaceClass(this.lastInsertClass,targetElClass);
					this.lastInsertClass=targetElClass;
				}
			}
		}
		return dragElClass;
	},
	onNodeOut:function(n,dd,e,data){
		this.removeDropIndicators(n);
	},
	onNodeDrop:function(n,dd,e,data){
		if(this.ms.fireEvent("drop",this,n,dd,e,data)===false){
			return false;
		}
		var pt=this.getDropPoint(e,n,dd);
		if(n!=this.ms.fs.body.dom)
		n=this.view.findItemFromChild(n);
		if(this.ms.appendOnly){
			insertAt=this.view.store.getCount();
		}else{
			insertAt=n==this.ms.fs.body.dom ? this.view.store.getCount() - 1 : this.view.indexOf(n);
			if(pt=="below") insertAt++;
		}
		var dir=false;
		// Validate if dragging within the same MultiSelect
		if(data.sourceView==this.view){
			// If the first element to be inserted below is the target node,remove it
			if(pt=="below"){
				if(data.viewNodes[0]==n){
					data.viewNodes.shift();
				}
			}else if(data.viewNodes[data.viewNodes.length-1]==n){
				data.viewNodes.pop();
			} // Nothing to drop...
			if(!data.viewNodes.length){
				return false;
			} // If we are moving DOWN,then because a store.remove() takes place first, the insertAt must be decremented.
			if(insertAt>this.view.store.indexOf(data.records[0])){
				dir="down";insertAt--;
			}
		}
		for(var i=0,j=data.records.length;i<j;i++){
			var r=data.records[i];
			if(data.sourceView){
				data.sourceView.store.remove(r);
			}
			this.view.store.insert(dir=="down"?insertAt:insertAt++,r);
			var si=this.view.store.sortInfo;
			if(si){
				this.view.store.sort(si.field,si.direction);
			}
		}
		return true;
	},
	removeDropIndicators:function(n){
		if(n){
			Ext.fly(n).removeClass(["x-view-drag-insert-above","x-view-drag-insert-left","x-view-drag-insert-right","x-view-drag-insert-below"]);
			this.lastInsertClass="_noclass";
		}
	},
	setDroppable:function(dg){
		if(!dg) return;
		if(Ext.isArray(dg)){
			Ext.each(dg,this.setDroppable,this);
		}else{
			this.addToGroup(dg);
		}
	}
});
Ext.fjs.Multiselect = Ext.fjs.form.MultiSelect;
Ext.fjs.form.StoreComboBox=Ext.extend(Ext.form.ComboBox,{
	allrec:true,alltext:"全部",allvalue:"",defValue:null,editable:false,inputFilter:false,storeAutoLoad:true,mode:"local",triggerAction:"all",emptyText:"请选择",valueNotFoundText:"",
	initComponent:function(){
		if(!this.displayField)this.displayField="text";
		if(!this.valueField)this.valueField="id";
		if(this.dictId){
			this.displayField="REFVALUE";
			this.valueField="REFID";
			this.store=new Ext.fjs.data.DictStore(this.dictId,this.dictParas);
		}else if(this.dictUrl){
			if(!this.storeParams){this.storeParams={}}
			this.store=new Ext.data.Store({autoDestroy:true,url:this.dictUrl,baseParams:this.storeParams,reader:new Ext.data.JsonReader({root:"",fields:[this.displayField,this.valueField]})});
		}else if(!this.store){
			alert("请设置store或者dictUrl或者dictId属性！");return;
		}
		if(this.storeAutoLoad&&this.store.getCount()==0){this.store.load({})}
		if(this.allrec||(this.defValue!=null)){this.store.on("load",this.evt_store_load,this)}
		if(this.inputFilter){
			this.editable=true;this.typeAhead=true;this.typeAheadDelay=5000;
			this.on("beforequery",function(a){var b,c=a.combo;if(!a.forceAll){b=a.query;c.store.filterBy(function(e,g){return(e.get(c.displayField).indexOf(b)!=-1)});c.expand();return false}});
		}
		Ext.fjs.form.StoreComboBox.superclass.initComponent.call(this);
	},
	evt_store_load:function(c,b,d){
		if(this.allrec){
			var e=new Ext.data.Record();e.set(this.displayField,this.alltext);e.set(this.valueField,this.allvalue);c.insert(0,e);
		}else{
			this.setValue(this.defValue||0);
		}
	}
});
Ext.reg("fjs-store-combo",Ext.fjs.form.StoreComboBox);
Ext.fjs.form.StoreRadioGroup=Ext.extend(Ext.form.Field,{
	displayField:"",valueField:"",chkField:"checked",border:false,defaultAutoCreate:{tag:"div"},boldLabel:false,
	initComponent:function(){
		Ext.fjs.form.StoreRadioGroup.superclass.initComponent.call(this);
		if(this.dictUrl){
			if(!this.storeParams){this.storeParams={}}if(!this.displayField)this.displayField="text";if(!this.valueField)this.valueField="id";this.chkField="chk";
			this.store=new Ext.data.Store({autoDestroy:true,url:this.dictUrl,baseParams:this.storeParams,reader:new Ext.data.JsonReader({root:"",fields:[this.displayField,this.valueField,this.chkField]})});
		}else if(this.dictId){
			this.store=new Ext.fjs.data.DictStore(this.dictId,this.dictParas);
			this.displayField="REFVALUE";this.valueField="REFID";this.chkField="DEFCHECK";
		}else if(!this.store){
			Ext.fjs.showError("请设置store或者dictUrl或者dictId属性！");return;
		}
		if(this.groupcfg){
			this.groupbox=new Ext.fjs.form.RadioGroup(this.groupcfg);
		}else{
			Ext.fjs.showError("请设置groupcfg属性！");return;
		}
		if(this.groupcfg.width){Ext.fjs.showError("请不要设置groupcfg的width属性！");return}
		if(this.groupcfg.allowBlank===false&&this.fieldLabel){this.fieldLabel+=" <font color=red>(*)</font> "}
		if(this.boldLabel){this.fieldLabel="<b>"+this.fieldLabel+"</b>"}
		this.storeLoaded=false;
	},
	onRender:function(c,b){
		Ext.fjs.form.StoreRadioGroup.superclass.onRender.call(this,c,b);
		this.innerCt=new Ext.Panel({border:this.border,height:this.height,layout:"auto",width:this.width});
		this.innerCt.render(this.el);
		var d=(this.store instanceof Ext.data.ArrayStore);
		if(d){
			this.on("afterrender",this.evt_afterrender,this);
		}else{
			if(this.store.getCount()==0){this.store.load({})}
			this.store.on("load",this.evt_store_load,this);
		}
	},
	evt_afterrender:function(){
		if(this.store instanceof Ext.data.ArrayStore){this.loadStore(this.store)}
	},
	evt_store_load:function(c,b,d){this.loadStore(c)},
	getTransValue:function(){return this.groupbox.getValue().initialConfig.boxLabel},
	getGroupValue:function(){return this.groupbox.getValue().initialConfig.inputValue},
	setValue:function(b){if(this.groupbox){this.groupbox.setValue(b)}},
	loadStore:function(k){
		var j=k.getCount();if(j==0){return}
		var f=[],n=this.groupcfg.name||"";
		for(var i=0;i<j;i++){
			var c=k.getAt(i),e=c.get(this.chkField),h={boxLabel:c.get(this.displayField),name:n,inputValue:c.get(this.valueField)};
			if(e){h=Ext.apply(h,{checked:(e=="1")})}
			f.push(h);
		}
		if(this.disabled){this.groupbox.disabled=true}
		this.groupbox.items=f;
		this.groupbox.setWidth(this.width);
		this.innerCt.add(this.groupbox);
		this.storeLoaded=true;
		this.innerCt.doLayout();
	},
	enable:function(){
		this.groupbox.enable();
		Ext.fjs.form.StoreRadioGroup.superclass.enable.call(this);
	},
	beforeDestroy:function(){
		if(!this.storeLoaded){Ext.destroy(this.groupbox)}
		this.store.destroy();
		if(this.innerCt){this.innerCt.destroy()}
		Ext.fjs.form.StoreRadioGroup.superclass.beforeDestroy.call(this);
	}
});
Ext.reg("fjs-store-radio",Ext.fjs.form.StoreRadioGroup);
Ext.fjs.form.RadioGroup=Ext.extend(Ext.form.RadioGroup,{
	onRender:function(j,g){
		if(!this.el){
			var a={autoEl:{id:this.id},cls:this.groupCls,layout:"column",autoScroll:true,renderTo:j,bufferResize:false};
			var b={xtype:"container",defaultType:this.defaultType,layout:"form",defaults:{hideLabel:true,anchor:"100%"}};
			if(this.items[0].items){
				Ext.apply(a,{layoutConfig:{columns:this.items.length},defaults:this.defaults,items:this.items});
				for(var f=0,m=this.items.length;f<m;f++){Ext.applyIf(this.items[f],b)}
			}else{
				var e,n=[];
				if(typeof this.columns=="string"){this.columns=this.items.length}
				if(!Ext.isArray(this.columns)){
					var k=[];for(var f=0;f<this.columns;f++){k.push((100/this.columns)*0.01)}this.columns=k;
				}
				e=this.columns.length;
				for(var f=0;f<e;f++){
					var c=Ext.apply({items:[]},b);
					c[this.columns[f]<=1?"columnWidth":"width"]=this.columns[f];
					if(this.defaults){c.defaults=Ext.apply(c.defaults||{},this.defaults)}
					n.push(c);
				}
				if(this.vertical){
					var r=Math.ceil(this.items.length/e),o=0;
					for(var f=0,m=this.items.length;f<m;f++){if(f>0&&f%r==0){o++}if(this.items[f].fieldLabel){this.items[f].hideLabel=false}n[o].items.push(this.items[f])}
				}else{
					for(var f=0,m=this.items.length;f<m;f++){var q=f%e;if(this.items[f].fieldLabel){this.items[f].hideLabel=false}n[q].items.push(this.items[f])}
				}
				Ext.apply(a,{layoutConfig:{columns:e},items:n});
			}
			this.panel=new Ext.Panel(a);
			this.panel.ownerCt=this;this.el=this.panel.getEl();
			if(this.forId&&this.itemCls){
				var d=this.el.up(this.itemCls).child("label",true);if(d){d.setAttribute("htmlFor",this.forId)}
			}
			var h=this.panel.findBy(function(l){return l.isFormField},this);
			this.items=new Ext.util.MixedCollection();this.items.addAll(h);
		}
		Ext.fjs.form.RadioGroup.superclass.onRender.call(this,j,g);
	}
});
Ext.fjs.form.ComboTree=Ext.extend(Ext.form.ComboBox,{
	listWidth:200,treeHeight:180,maxHeight:500,resizable:false,store:new Ext.data.SimpleStore({fields:[],data:[[]]}),editable:false,lazyInit:false,mode:"local",selectedClass:"",triggerAction:"all",onSelect:Ext.emptyFn,emptyText:"请选择",allowUnLeafClick:true,
	clearValue:function(){if(this.PF){this.PF.value=""}this.setRawValue("")},
	treeClk:function(n,e){if(n.isLeaf()||this.allowUnLeafClick){this.setValue(n.text);if(this.PF){this.PF.value=n.id}this.collapse();this.fireEvent("treeselected",n)}else{e.stopEvent()}},
	listeners:{
		"expand":{fn:function(){if(!this.tree.rendered&&this.tId){this.initLayout();this.tree.render(this.tId);if(this.tP.sync)this.tree.expandAll()}this.tree.show()}},
		"render":{fn:function(){this.tree.on("click",this.treeClk,this);if(this.PN){this.PF=this.getEl().insertSibling({tag:"input",type:"hidden",name:this.PN,id:this.passId||Ext.id()},"before",true);this.PF.value=this.PV!==undefined?this.PV:(this.value!==undefined?this.value:"");this.el.dom.removeAttribute("name")}}},
		"beforedestroy":{fn:function(a){this.purgeListeners();this.tree.purgeListeners()}},
		"collapse":{fn:function(a){if(this.store.getCount()==0){this.store.add(new Ext.data.Record([{}]))}}}
	},
	getValue:function(v){return (v&&v=="text")?this.PF.value:this.PV},rsetValue:function(v,t){if(this.PF)this.PF.value=v;if(t)this.setRawValue(t)},
	loadValue:function(v){if(this.PF)this.PF.value=v;this.hasFocus=true;this.expand();this.collapse();this.initnode(this.tree.root)},
	initComponent:function(){
		if(!this.PN){this.PN=this.name}
		var a={},t=this.tP;if(t.sync){a.dataUrl=t.url}t.loader=new Ext.tree.TreeLoader(a);
		if(!this.tree)this.tree=new Ext.tree.TreePanel(t);
		Ext.fjs.form.ComboTree.superclass.initComponent.call(this);
		this.tId=Ext.id();
		this.tpl='<div id="'+this.tId+'" style="height:'+this.treeHeight+';overflow:hidden;"></div>';
		this.addEvents('treeselected');
	},
	initLayout:function(){
		var a=this.tree,t=this.tP;a.autoScroll=true;a.border=false;a.containerScroll=false;a.height=this.treeHeight;this.listWidth=a.width+3;
		if(t.sync){a.getLoader().on("load",function(){this.initnode(a.root)},this)}else{a.on("beforeload",function(){this.loader.dataUrl=t.url},a)}
	},
	initnode:function(n){
		if(n.id==this.PF.value){this.setValue(n.text);n.select()}else{for(var i=0;i<n.childNodes.length;i++){n.childNodes[i].expand();this.initnode(n.childNodes[i])}}
	},
	beforeDestroy:function(){
		Ext.destroy(this.PF);this.tree.destroy();
		Ext.fjs.form.ComboTree.superclass.beforeDestroy.call(this);
	},
	onViewClick:function(df){}
});
Ext.reg('fjs-tree-combo', Ext.fjs.form.ComboTree);

Ext.namespace("Ext.tinymce.flash");
Ext.tinymce.flash.UploadWin=Ext.extend(Ext.ux.UploadDialog.Dialog,{
	constructor:function(ed,url){
		this.ed=ed;
		this.pluginURL=url;
		var permitted_size=10 * 1024 * 1024;//10M
		Ext.tinymce.flash.UploadWin.superclass.constructor.call(this,{
			url:"/upload.do?action=flash&size="+permitted_size,
			post_var_name:"files",
			title:"上传Flash至服务器",
			permitted_extensions:["swf"],
			draggable:true,modal:true,resizable:true,
			constraintoviewport:true,
			allow_close_on_upload:false //关闭上传窗口是否仍然上传文件
			//upload_autostart:true // 是否自动上传文件
		});
		this.on("uploadsuccess",this.evt_dialog_success,this);
		this.on("uploadcomplete",this.evt_dialog_complete,this);
		this.on("uploaderror",this.evt_dialog_uploaderror,this);
	},
	evt_dialog_success:function(dialog,filename,resp_data,record){
		var flash = "/parm/tinymce/upload.json?actionName=download&fileId=" + resp_data.data.FILEID;
    	var c = '<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" TYPE="application/x-shockwave-flash" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0" align="middle">';  					
    	c += '<param name="movie" value="' + flash + '" /><param name="quality" value="high" />';
    	c += '<embed width="550" src="' + flash + '" quality="high" ';
    	c += ' align="middle" TYPE="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" >';
    	c += '</object>';
    	this.ed.execCommand('mceInsertContent', false, c);
	},
	evt_dialog_uploaderror : function(dialog,filename,res){
		Ext.fjs.showError(res.errorMessage);
	},
	evt_dialog_complete:function(dialog){
		(function(){dialog.close()}).defer(200);
	}
});
Ext.namespace("Ext.tinymce.image");
Ext.tinymce.image.UploadWin=Ext.extend(Ext.ux.UploadDialog.Dialog,{
	constructor:function(ed,url){
		this.ed=ed;
		this.pluginURL=url;
		var permitted_size=2 * 1024 * 1024;//2M
		Ext.tinymce.image.UploadWin.superclass.constructor.call(this,{
			url:"/upload.do?action=image&size="+permitted_size,
			post_var_name:"files",
			title:"上传图片至服务器",
			permitted_extensions:["jpg","jpeg","gif","png"],
			draggable:true,modal:true,resizable:true,
			constraintoviewport:true,
			allow_close_on_upload:false //关闭上传窗口是否仍然上传文件
			//upload_autostart:true //是否自动上传文件
		});
		this.on("uploadsuccess",this.evt_dialog_success,this);
		this.on("uploadcomplete",this.evt_dialog_complete,this);
		this.on("uploaderror",this.evt_dialog_uploaderror,this);
	},
	evt_dialog_success:function(dialog,filename,res,record){
		var content="<img width='550' src='/img/"+res.data.FILEPATH+"'>";
		this.ed.execCommand("mceInsertContent",false,content);
	},
	evt_dialog_uploaderror:function(dialog,filename,res){
		Ext.fjs.showError(res.errorMessage);
	},
	evt_dialog_complete:function(dialog){
		(function(){dialog.close()}).defer(200);
	}
});
Ext.namespace("Ext.tinymce.template");
