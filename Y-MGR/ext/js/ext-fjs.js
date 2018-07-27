var $=function(e){return(typeof(e)=="string")?document.getElementById(e):e};
Ext.BLANK_IMAGE_URL="/ext/images/default/s.gif";
Ext.util.CSS.swapStyleSheet("fjs-fjslayout","/fjs/css/fjslayout.css");
Ext.namespace("Ext.fjs");
Ext.fjs=function(){return{defPSize:25,btnPath:"/ext/icons/btn/",menuPath:"/ext/icons/menu/",add_wait:"正在保存数据请稍候",load_wait:"正在装载数据请稍候",save_wait:"正在保存数据请稍候",del_confirm:"删除数据后，将无法恢复！您确定要执行删除操作吗？",
	formInvalid:"",ERROR:"错误",NOTICE:"提示",SE:{401:"您长时间没有进行系统操作，为了安全系统已自动为您注销，请您点击【确定】后重新登录！"},
	show:function(b){var M=Ext.msg;if(b==401){M.show({title:this.ERROR,msg:this.SE[401],fn:function(){location.href="login.jsp"},buttons:M.OK,icon:M.ERROR})}else{if(b<=0)b=100;M.show({title:this.ERROR,msg:(this.SE[b]||(this.SE[99]+b)),buttons:M.OK,icon:M.ERROR})}},
	showInfo:function(b){Ext.msg.show({title:this.NOTICE,msg:b+" ",buttons:Ext.msg.OK,icon:Ext.msg.INFO})},showError:function(b){Ext.msg.show({title:this.ERROR,msg:(b||" "),buttons:Ext.msg.OK,icon:Ext.msg.ERROR})},
	ajax:function(b){b.disableCaching=false,b.failure=function(c,d){Ext.fjs.show(c.status)};Ext.Ajax.request(b)},get:function(b){b.method="GET";this.ajax(b)},post:function(b){b.method="POST";this.ajax(b)},
	failure:function(b,c){var A=Ext.form.Action,t=c.failureType;if(t==A.CONNECT_FAILURE){Ext.fjs.show(c.response.status)}else if(t==A.SERVER_INVALID||t==A.LOAD_FAILURE){Ext.fjs.showError(c.result.message)}},
	include:function(a){if(a&&a!=null){var b=document.getElementsByTagName("HEAD").item(0),c=document.createElement("script");c.language="javascript";c.type="text/javascript";c.text=a;b.appendChild(c)}},
	loadAction:function(b){if(b.show){b.success=function(){this.show()}};return Ext.apply({method:"POST",waitTitle:this.NOTICE,waitMsg:this.load_wait,failure:this.failure},b)},
	addAction:function(b){return Ext.apply({method:"POST",clientValidation:true,waitTitle:this.NOTICE,waitMsg:this.add_wait,submitEmptyText:false,failure:this.failure},b)},
	saveAction:function(b){return Ext.apply({method:"POST",clientValidation:true,waitTitle:this.NOTICE,waitMsg:this.save_wait,submitEmptyText:false,failure:this.failure},b)},
	checkValid:function(b){if(b.getForm().isValid()){return true}else if(Ext.msg.isVisible()){Ext.fjs.showError(Ext.fjs.formInvalid)}return false},
	destoryMembers:function(c){for(var b in c){if(c[b] && typeof c[b]=="object"){c[b]=null}}},
	download:function(b){if(!_isCF){window.exportStatus="export";if(Ext.isIE6){window.event.srcElement.href="#"}location.href=b}else{location.href=b}},
	getPKId:function(c,e){var sb=[],f=c.getSelectionModel().getSelections();for(var i=0,j=f.length;i<j;i++){sb.push(f[i].get(e))}return sb.join(",")},
	getMIcon:function(b){return (b=="")?"":(Ext.fjs.menuPath+b+".gif")},getMIconCls:function(b){return (b=="")?"":("mnu-"+b)},
	getFieldValue:function(b,c){var d=b.getForm().findField(c);return (d==null)?null:d.getValue()},setFieldValue:function(b,c,d){var e=b.getForm().findField(c);if(e!=null){e.setValue(d)}},
	image:function(id,s,f,v){var a=Ext.get(id);if(a){if(f==undefined)f="jpg";if(v==undefined)v=1;if(s&&s.length>=10&&v>=1){a.dom.src="/img/"+s.substring(0,6)+"/"+s.substring(6)+"."+f+"?v="+v}else{a.dom.src="/images/s.gif"}if(!a.state){a.state=true;a.on("click",function(a,e){window.open(e.src,"news","resizable=no")})}}},
	PageToolBar:function(b){b=Ext.apply({buttonAlign:"right",displayInfo:true,displayMsg:"显示第<b>{0}</b> - <b>{1}</b>条记录 / 共<b>{2}</b>记录",emptyMsg:"没有记录",pageSize:Ext.fjs.defPSize,plugins:[new Ext.fjs.PageResizer()]},b);return new Ext.PagingToolbar(b)},
	reflect:function(){},setProperty:function(e,c,d){var b=Ext.decode("{"+c+":'"+d+"'}");Ext.apply(e,b)},set:function(c,n,v){var r=c.getSelectionModel().getSelected();try{r.set(n,v);r.commit()}catch(e){}},
	syncRequest:function(f,d){var c;if(window.XMLHttpRequest){c=new XMLHttpRequest()}else if(window.ActiveXObject){c=new ActiveXObject("Msxml2.XMLHTTP")}else{c=new ActiveXObject("Microsoft.XMLHTTP")}c.open("POST",f,false);c.setRequestHeader("Content-Type","application/x-www-form-urlencoded;");c.send(d);return c.responseText}
}}();
Ext.fjs.module=function(){return{
	URL:"/module.do",debugMode:false,tabPanel:null,realTabIds:null,menuModuleMapping:null,libstatus:null,moduleConf:null,menuItems:null,
	showChildP:function(P,b){if(b){P.b=b;P.tabTitle=b.tabTitle}var f=this.tabPanel.findById(P.getId());if(f){f.show()}else{this.tabPanel.add(P);P.show()}},
	closeChildP:function(P){var f=this.tabPanel.findById(P.getId());if(f){this.tabPanel.remove(f);this.tabPanel.doLayout()}},
	loadPanelTitleFromMenu:function(moduleId,panel){
		if(this.menuItems==null){return}for(var i=0;i<this.menuItems.length;i++){var it=this.findItem(moduleId,this.menuItems[i]);if(it!=null){panel.tabTitle=this.getItemPath(it);panel.setTitle(it.text,it.iconCls);document.title=panel.tabTitle}}
	},
	getItemPath:function(item){
		if(item.parent!=null){return this.getItemPath(item.parent)+"->"+item.text}return item.text;
	},
	findItem:function(moduleId,item){
		if(item.module==moduleId){return item}if(!item.leaf&&item.children){for(var i=0;i<item.children.length;i++){var it=this.findItem(moduleId,item.children[i]);if(it!=null){return it}}}return null;
	},
	initModule:function(tab,debug,cb,sc){
		this.tabPanel=tab;this.debugMode=debug;this.realTabIds=new Ext.util.MixedCollection();this.menuModuleMapping=new Ext.util.MixedCollection();this.libstatus=new Ext.util.MixedCollection();this.loadConfig(cb,sc);
	},
	loadLib:function(libs,cb,sc){
		Ext.fjs.post({url:"modlib.do",params:{libs:libs.toString()},success:function(res){Ext.fjs.include(res.responseText);for(var i=0,j=libs.length;i<j;i++){if(!this.libstatus.containsKey(libs[i])){this.libstatus.add(libs[i],"1")}}if(cb){if(sc){cb.call(sc||this)}else{cb()}}},scope:this});
	},
	loadConfig:function(cb,sc){
		Ext.fjs.post({url:"module.jsp",success:function(res){eval("this.moduleConf="+res.responseText);if(cb){if(sc){cb.call(sc||this)}else{cb()}}},scope:this});
	},
	getModuleConfig:function(moduleId){
		if(this.moduleConf==null){alert("Module cannot init!")}for(var i=0;i<this.moduleConf.length;i++){if(this.moduleConf[i].id==moduleId){return this.moduleConf[i]}}return null;
	},
	getMainPanel:function(moduleId){
		var config=this.getModuleConfig(moduleId);if(config!=null){var panel=config.mainPanel||"";if(panel!=""){return panel}}return "Ext."+moduleId+".MainPanel";
	},
	getPanelByModuleId:function(moduleId){
		if(moduleId==null){return null}var realId=this.realTabIds.get(moduleId);if(realId!=null){var fp=this.tabPanel.findById(realId);if(fp){return fp}}return null;
	},
	getChildPanel:function(id){return (id==null)?null:(this.tabPanel.findById(id)||null)},
	getModule:function(moduleId,callback,scope){
		Ext.fjs.post({params:{Id:moduleId,libs:this.getModuleLib(moduleId),depend:this.getDependModules(moduleId)},url:this.URL,success:function(res){var resText=res.responseText;if(resText==""){alert("您对模块"+moduleId+"没有权限！");return}
		Ext.fjs.include(resText);this.checkLibStatus(moduleId);this.checkDependModuleStatus(moduleId);var p=null;var pname=this.getMainPanel(moduleId);try{var str="var p = new "+pname+"()";eval(str)}catch(e){alert("创建类"+pname+"产生了错误："+e);throw e;return}if(callback){if(scope){callback.call(scope||this,p)}else{callback(p)}}},scope:this});
	},
	loadMainMenuModule:function(menuId,moduleId,callback,scope){
		var mapping=this.menuModuleMapping.get(menuId);if(mapping!=null){var panelId=mapping.panelId;if(this.tabPanel.findById(panelId)){var p=this.tabPanel.findById(panelId);p.show();if(callback){if(scope){callback.call(scope||this,p)}else{callback(p)}}return}else{eval("var p = new "+this.getMainPanel(moduleId)+"()");this.tabPanel.add(p);p.show();this.realTabIds.replace(moduleId,p.getId());mapping.panelId=p.getId();this.loadPanelTitleFromMenu(moduleId,p);if(callback){if(scope){callback.call(scope||this,p)}else{callback(p)}}return}}else{var realId=this.realTabIds.get(moduleId);if(realId!=null){eval("var p = new "+this.getMainPanel(moduleId)+"()");this.tabPanel.add(p);p.show();this.realTabIds.replace(moduleId,p.getId());this.menuModuleMapping.add(menuId,{moduleId:moduleId,panelId:p.getId()});this.loadPanelTitleFromMenu(moduleId,p);if(callback){if(scope){callback.call(scope||this,p)}else{callback(p)}}return}else{this.tabPanel.loadMask.show();
		Ext.fjs.post({
			params:{Id:moduleId,libs:this.getModuleLib(moduleId),depend:this.getDependModules(moduleId)},
			url:this.URL,success:function(res){var resText=res.responseText;if(resText==""){alert("您对模块"+moduleId+"没有权限！");this.tabPanel.loadMask.hide();return}
		Ext.fjs.include(resText);this.checkLibStatus(moduleId);this.checkDependModuleStatus(moduleId);var p=null;var pname=this.getMainPanel(moduleId);try{var str="var p = new "+pname+"()";eval(str)}catch(e){alert("创建类"+pname+"产生了错误："+e);throw e;this.tabPanel.loadMask.hide();return}this.tabPanel.add(p);p.show();this.realTabIds.add(moduleId,p.getId());this.menuModuleMapping.add(menuId,{moduleId:moduleId,panelId:p.getId()});this.tabPanel.loadMask.hide();this.loadPanelTitleFromMenu(moduleId,p);if(callback){if(scope){callback.call(scope||this,p)}else{callback(p)}}},scope:this})}}
	},
	loadModule:function(moduleId,callback,scope){
		var realId=this.realTabIds.get(moduleId);if(realId!=null){if(this.tabPanel.findById(realId)){var p=this.tabPanel.findById(realId);p.show();if(callback){if(scope){callback.call(scope||this,p)}else{callback(p)}}return}else{eval("var p = new "+this.getMainPanel(moduleId)+"()");this.tabPanel.add(p);p.show();this.realTabIds.replace(moduleId,p.getId());this.loadPanelTitleFromMenu(moduleId,p);if(callback){if(scope){callback.call(scope||this,p)}else{callback(p)}}return}}else{if(this.debugMode){var p=null;var pname=this.getMainPanel(moduleId);try{var str="var p = new "+pname+"()";eval(str)}catch(e){alert("创建类"+pname+"产生了错误：浏览器没有找到类，请检查是否将js文件引入/debug.jsp");throw e;return}this.tabPanel.add(p);p.show();this.realTabIds.replace(moduleId,p.getId());this.loadPanelTitleFromMenu(moduleId,p);if(callback){if(scope){callback.call(scope||this,p)}else{callback(p)}}return}else{this.tabPanel.loadMask.show();
		Ext.fjs.post({params:{Id:moduleId,libs:this.getModuleLib(moduleId),depend:this.getDependModules(moduleId)},url:this.URL,success:function(res){var resText=res.responseText;if(resText==""){alert("您对模块"+moduleId+"没有权限！");this.tabPanel.loadMask.hide();return}
		Ext.fjs.include(resText);this.checkLibStatus(moduleId);this.checkDependModuleStatus(moduleId);var p=null;var pname=this.getMainPanel(moduleId);try{var str="var p = new "+pname+"()";eval(str)}catch(e){alert("创建类"+pname+"产生了错误："+e);throw e;this.tabPanel.loadMask.hide();return}this.tabPanel.add(p);p.show();this.realTabIds.add(moduleId,p.getId());this.tabPanel.loadMask.hide();this.loadPanelTitleFromMenu(moduleId,p);if(callback){if(scope){callback.call(scope||this,p)}else{callback(p)}}},scope:this})}}
	},
	checkLibStatus:function(moduleId){
		var moduleConfig=this.getModuleConfig(moduleId);if(moduleConfig!=null){var arr=moduleConfig.dependlib;for(var i=0;i<arr.length;i++){if(!this.libstatus.containsKey(arr[i])){this.libstatus.add(arr[i],"1")}}}
	},
	checkDependModuleStatus:function(moduleId){
		var moduleConfig=this.getModuleConfig(moduleId);if(moduleConfig!=null){var arr=moduleConfig.dependmodule;for(var i=0;i<arr.length;i++){if(!this.realTabIds.containsKey(arr[i])){this.realTabIds.add(arr[i],"noexist")}}}
	},
	getModuleLib:function(moduleId){
		var moduleConfig=this.getModuleConfig(moduleId);if(moduleConfig!=null){var arr=moduleConfig.dependlib;var str="";for(var i=0;i<arr.length;i++){var v=this.libstatus.get(arr[i]);if(v==null){str=str+arr[i]+","}}if(str.length>0){str=str.substr(0,str.length-1)}return str}return"";
	},
	getDependModules:function(moduleId){
		var moduleConfig=this.getModuleConfig(moduleId);if(moduleConfig!=null){var arr=moduleConfig.dependmodule;var str="";for(var i=0;i<arr.length;i++){var realId=this.realTabIds.get(arr[i]);if(realId==null){str=str+arr[i]+","}}if(str.length>0){str=str.substr(0,str.length-1)}return str}return"";
	}
}}();
Ext.namespace("Ext.fjs.layout");
Ext.fjs.layout.HtmlLayout=Ext.extend(Ext.layout.ContainerLayout,{
	no:true,type:"html",prefix:null,onLayout:function(g,p){
		if(this.no){
			if(this.templateHTML){
				this.readerHtml(g,p,this.templateHTML);
			}else if(this.ejstemplate){
				var f=new EJS({url:this.ejstemplate}).render(this.ejsdata);
				this.readerHtml(g,p,f);
			}else{
				var tp=this.template;tp+=(tp.indexOf('?')>0)?"&rnd=":"?rnd="+Math.random();
				var f=Ext.fjs.syncRequest("/exp/"+tp);
				this.renderHtml(g,p,f);
			}
			this.no=false;
		}
	},
	renderHtml:function(g,p,f){
		this.prefix=Ext.id();
		var a=p.createChild({tag:"div"},null,true);
		var h=g.items.items,k,n;
		for(var i=0,j=h.length;i<j;i++){
			k=h[i],n=k.name||k.hiddenName||"";
			if(k instanceof Ext.form.DisplayField&&k.noWordWrap){
				k.style=k.style||"";
				k.style+="white-space:normal;";
				f=f.replace("${"+n+"}",'<div class="x-form-field-wrap"><table><tr><td id="'+this.prefix+"-"+n+'"></td></tr></table></div>')
			}else{
				f=f.replace("${"+n+"}",'<div id="'+this.prefix+"-"+n+'" class="x-form-field-wrap"></div>')
			}
		}
		if(this.css){a.className=this.css};
		a.innerHTML=f;
		this.renderAll(g,p);
	},
	renderItem:function(f,b,e){if(f&&!f.rendered){var d=f.name||f.hiddenName||"";if(d!=""){f.render(this.prefix+"-"+d);this.configureItem(f,b)}}}
});
Ext.fjs.reflect.exit=function(){var a=arguments,b,c,d;if(a.length<2){alert("Ext.fjs.reflect.exit: Arguments error")}else if(typeof a[0]!=="string"||typeof a[1]!=="string"){alert("Ext.fjs.reflect.exit: Arguments 0 and 1 must be string ")}else if(Ext.isEmpty(b=Ext.getCmp(a[0]))){alert("Ext.fjs.reflect.exit: Can not find Ext CMP "+a[0])}else if(Ext.isFunction(c=b[a[1]])){d=[].slice.apply(a);c.apply(b,d.slice(2,d.length))}else{alert("Ext.fjs.reflect.exit: Can not find method "+a[1]+" of Ext CMP "+a[0])}};
Ext.fjs.PageResizer=Ext.extend(Object,{ps:[20,25,50,100,200,250],prefixText:"每页",postfixText:"条",constructor:function(b){Ext.apply(this,b);Ext.fjs.PageResizer.superclass.constructor.call(this,b)},init:function(c){var e=this.ps,d=new Ext.form.ComboBox({typeAhead:true,triggerAction:"all",lazyRender:true,mode:"local",editable:false,width:45,store:e,listeners:{select:function(l,h,f){c.pageSize=e[f];var k=0,g=c.findParentBy(function(m,n){return(m instanceof Ext.grid.GridPanel)}),j=g.getSelectionModel();if(undefined!=j&&j.hasSelection()){if(j instanceof Ext.grid.RowSelectionModel){k=g.store.indexOf(j.getSelected())}else if(j instanceof Ext.grid.CellSelectionModel){k=j.getSelectedCell()[0]}}k+=c.cursor;c.doLoad(Math.floor(k/c.pageSize)*c.pageSize)}}});Ext.iterate(this.ps,function(f){if(f==c.pageSize){d.setValue(f);return}});var b=c.items.indexOf(c.nextText);c.insert(++b,this.prefixText);c.insert(++b,d);c.insert(++b,this.postfixText);c.insert(++b,"-");c.on({beforedestroy:function(){d.destroy()}})}});
Ext.fjs.PageStore=Ext.extend(Ext.data.JsonStore,{constructor:function(b){b=Ext.apply({autoDestroy:true,remoteSort:true,root:"data",totalProperty:"total"},b);Ext.fjs.PageStore.superclass.constructor.call(this,b)}});

Ext.Component.override({destroy:Ext.Component.prototype.destroy.createSequence(function(){Ext.fjs.destoryMembers(this)})});
Ext.Container.Layouts.html=Ext.fjs.layout.HtmlLayout;
Ext.Ajax.on("beforerequest",function(b,c){if(c.enableCaching===true){c.disableCaching=false}else{c.disableCaching=true}return true});
Ext.Window.override({initComponent:Ext.Window.prototype.initComponent.createInterceptor(function(){if(!this.iconCls){this.iconCls="mnu-application"}})});

Ext.data.DataProxy.on("exception",function(e,f,g,d,b,c){if(f=="response"){Ext.fjs.show(b.status)}});
Ext.data.MemoryProxy=function(c){var A=Ext.data.Api.actions,b={};b[A.read]=true;b[A.create]=true;b[A.update]=true;b[A.destroy]=true;Ext.data.MemoryProxy.superclass.constructor.call(this,{api:b});this.data=c};

Ext.grid.RowNumber=Ext.extend(Ext.grid.RowNumber,{renderer:function(f,e,b,g,d,c){if(c.lastOptions&&c.lastOptions.params.start){g+=c.lastOptions.params.start}return g+1}});
Ext.grid.GridPanel.override({initComponent:Ext.grid.GridPanel.prototype.initComponent.createSequence(function(){if(this.boldHeader){var m=this.colModel,c=m.getColumnCount(false);for(var b=0;b<c;b++){m.setColumnHeader(b,"<b>"+m.getColumnHeader(b)+"</b>")}}})});
