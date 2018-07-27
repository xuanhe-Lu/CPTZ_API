window.FZM=window.FZM||{};
FZM.dialog=FZM.dialog||{count:0,instances:{}};
FZM.dialog.base=function(opts){var _s=FZM.dialog;this.opts=opts;this.id='fdialog'+(++_s.count);_s.instances[this.id]=this};
FZM.dialog.base.prototype.init=function(opts){
	var _s=FZM.dialog,dv,l,t,buff,tmp,n=_s.count;opts=opts||this.opts;opts.buttonConfig=opts.buttonConfig||[];
	opts.hasFooter=(opts.button.length);
	!this.zIndex&&(this.zIndex=6000+n);
	dv=document.createElement('div');
	dv.className='qz_dialog_layer';
	opts&&opts.noShadow&&(dv.className+=" qz_dialog_layer_no_border");
	t=opts.top||Math.round(Math.max((FZM.dom.getCH()-opts.height)/2+FZM.dom.getST(),1));
	l=opts.left||Math.round(Math.max((FZM.dom.getCW()-opts.width)/2+FZM.dom.getSL(),1));
	dv.style.cssText=';position:absolute;top:'+t+'px;left:'+l+'px;'+(opts.width?('width:'+parseInt(opts.width,10)+'px;'):'')+'z-index:'+this.zIndex;
	dv.id='qz_dialog_instance_'+this.id;
	this.uniqueID=n;
	this.mainId="dialog_main_"+n;
	this.headId="dialog_head_"+n;
	this.titleId="dialog_title_"+n;
	this.closeId="dialog_button_"+n;
	this.contentId="dialog_content_"+n;
	this.frameId="dialog_frame_"+n;
	this.titleBarHeight=opts.noBorder?0:30;
	buff=[];
	buff.push('<div id="',this.mainId,'" class="qz_dialog_layer_main',opts.hasFooter?' qz_dialog_btn_bpadding':'','"',(opts.noBorder?' style="border-width:0;padding:0;margin:0;background-color:transparent;"':''),'>',opts.noCustom?'':'<div class="qz_dialog_custom custom_left"></div><div class="qz_dialog_custom custom_top"></div>','<div id="',this.headId,'" class="qz_dialog_layer_title"',(opts.noBorder?' style="display:none;"':''),'><h3 id="',this.titleId,'">',opts.title,'</h3><button id="',this.closeId,'" title="',FZM.lang.dat['close'],'" class="qz_dialog_btn_close',opts.noCloseButton?" none":"",'"><span class="none">&#9587;</span></button></div>','<div id="',this.contentId,'" class="qz_dialog_layer_cont" style="height:',parseInt(opts.height,10),'px;',(opts.noBorder?'background-color:transparent;':''),'">',FZM.dialog.base.genContent(opts.content),'</div>');
	if(opts.hasFooter){
		buff.push('<div class="qz_dialog_layer_ft">','<div class="qz_dialog_layer_other">',opts.statusContent||'','</div>','<div class="qz_dialog_layer_op">');
		for(var i=0,len=opts.button.length;i<len;++i){
			tmp=opts.button[i];
			buff.push("<button class=\"xf-btn\">",tmp.text,"</buttion>");
		}
		buff.push('</div></div>');
	}
	buff.push('</div>');
	if(opts.noIframeMask){}else{if(FZM.ua.ie){buff.push('<iframe id="',this.frameId,'" class="qz_dialog_layer_mask" frameBorder="no" style="opacity:0;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);position:absolute;top:0;left:0;z-index:-1;" width="',(FZM.ua.ie==6)?((parseInt(opts.width,10))+'px'):'100%','" height="',(FZM.ua.ie==6)?((parseInt(opts.height,10)+this.titleBarHeight)+'px'):'100%','"></iframe>');}}
	dv.innerHTML=buff.join('');
	document.body.appendChild(dv);
	this.dialogBody=dv;
	this.dialogTitle=FZM.element.get('.qz_dialog_layer_title',dv).eq(0);
	this.dialogContent=FZM.element.get('.qz_dialog_layer_cont',dv).eq(0);
	this.dialogFooter=FZM.element.get('.qz_dialog_layer_ft',dv).eq(0);
	this.dialogMask=FZM.element.get('.qz_dialog_layer_mask',dv).eq(0);
	FZM.element.get('.qz_dialog_btn_close',this.dialogBody).bind("click",FZM.dialog.base.close,this.id);
	FZM.event.addEvent(this.dialogBody,'click',FZM.dialog.base.clickFocus,[this.id]);
	FZM.event.addEvent(FZM.element.get('.qz_dialog_layer_op',this.dialogFooter).eq(0),'click',FZM.dialog.base.clickOpr,[this.id]);
	if(FZM.dragdrop&&FZM.dragdrop.register){
		FZM.dragdrop.register(this.dialogTitle,dv);
	}
	this.focus();
	opts.showMask&&FZM.maskLayout&&FZM.maskLayout.create(this.zIndex-1);
	(typeof opts.onLoad=='function')&&((this.onLoad=opts.onLoad)(this));
};
FZM.dialog.base.prototype.fillTitle=function(html){this.dialogTitle&&FZM.element.get('h3',this.dialogTitle).setHtml(html)};
FZM.dialog.base.fTemplate='<iframe width="100%" height="100%" src="{src}" allowtransparency="yes" frameborder="no" scrolling="no"></iframe>';
FZM.dialog.base.genContent=function(c){(c&&typeof(c)=='object')&&(c=FZM.string.format(FZM.dialog.base.fTemplate,c));return c};

FZM.dialog.base.prototype.fillContent=function(html,onAfterFill){this.dialogContent&&(this.dialogContent.innerHTML=FZM.dialog.base.genContent(html));(typeof onAfterFill=='function')&&(onAfterFill(this.dialogBody));};
FZM.dialog.base.prototype.hide=function(){this.dialogBody&&(this.dialogBody.style.display='none');this.blur();};
FZM.dialog.base.prototype.show=function(){this.dialogBody&&(this.dialogBody.style.display='');this.focus();};
FZM.dialog.base.close=function(evt,sid){var _s=FZM.dialog,t=_s.instances[sid];t&&t.unload();};
FZM.dialog.base.clickFocus=function(evt,sid){var _s=FZM.dialog,t=_s.instances[sid];t&&t.focus();};
FZM.dialog.base.clickOpr=function(evt,sid){
	!evt&&(evt=FZM.event.getEvent(evt));
	var _s=FZM.dialog,ins=_s.instances[sid],t=FZM.event.getTarget(evt),
	l=FZM.element.get('.xf-btn',ins.dialogFooter),idx=-1,tmp;
	l.each(function(obj,itr){if(obj==t||FZM.dom.contains(obj,t)){idx=itr;}});
	if(idx>-1&&ins){
		if(tmp=ins.opts.button[idx]){
			(typeof tmp.handler=='function')&&tmp.handler();
			ins.unload();
		}
	}
	FZM.event.preventDefault(evt);
};
FZM.dialog.base.computePosition=function(w,h,opts){var l,t;t=opts&&opts.top||Math.round(Math.max((FZM.dom.getCH()-h)/2+FZM.dom.getST(),1));l=opts&&opts.left||Math.round(Math.max((FZM.dom.getCW()-w)/2+FZM.dom.getSL(),1));return[l,t];};
FZM.dialog.base.prototype.unload=function(){
	var _s=FZM.dialog;
	if((typeof(this.onBeforeUnload)!='function')||this.onBeforeUnload(this.returnValue)){
		this.blur();FZM.dragdrop&&FZM.dragdrop.register&&FZM.dragdrop.unRegister(this.dialogTitle);
		FZM.event.removeEvent(this.dialogBody,'click',FZM.dialog.base.clickFocus);
		if(FZM.userAgent.ie===8&&this.dialogBody){this.dialogBody.style.display='none';}
		(typeof(this.onUnload)=='function')&&this.onUnload(this.returnValue);FZM.dom.remove(this.dialogBody);
		this.onUnload=this.onBeforeUnload=this.onLoad=this.onConfirm=this.onNegative=this.onNo=this.onCancel=this.returnValue=this.dialogTitle=this.dialogContent=this.dialogFooter=this.dialogBody=null;
		this.opts.showMask&&FZM.maskLayout&&FZM.maskLayout.remove();delete _s.instances[this.id];
	}
};
FZM.dialog.base.prototype.setReturnValue=function(v){(typeof v!='undefined')&&(this.returnValue=v);};
FZM.dialog.base.prototype.focus=function(){
	var _s=FZM.dialog;if(!this.isCurrent&&this.dialogBody){_s.current&&_s.current.blur();this.dialogBody.style.zIndex=parseInt(this.zIndex)+3000;this.isCurrent=true;_s.current=this;}
};
FZM.dialog.base.prototype.blur=function(){
	var _s=FZM.dialog;if(this.isCurrent&&this.dialogBody){(_s.current===this)&&(_s.current=null);this.dialogBody.style.zIndex=parseInt(this.zIndex);this.isCurrent=false;}
};
FZM.dialog.base.prototype.getZIndex=function(){return parseInt(this.dialogBody.style.zIndex,10);};
FZM.dialog.base.prototype.setSize=function(w,h){
	var p,t,wTween=false;(w=parseInt(w,10))&&((FZM.effect&&FZM.effect.run)?(wTween=true):(this.dialogBody.style.width=w+'px'));(h=parseInt(h,10))&&((FZM.effect&&FZM.effect.run)?FZM.effect.run(this.dialogContent,{'height':h},{'duration':300}):(this.dialogContent.style.height=h+'px'));if(this.dialogMask){(w=parseInt(w,10))&&(this.dialogMask.style.width=w+'px');(h=parseInt(h,10))&&(this.dialogMask.style.height=(h+this.titleBarHeight)+'px');}
	if(this.opts.top||this.opts.left){if(FZM.effect&&FZM.effect.run){t={};wTween&&(t.width=w);FZM.effect.run(this.dialogBody,t,{'duration':300});}}else{p=FZM.dialog.base.computePosition(w,h,this.opts);if(FZM.effect&&FZM.effect.run){t={'left':p[0],'top':p[1]};wTween&&(t.width=w);FZM.effect.run(this.dialogBody,t,{'duration':300});}else{(t=this.dialogBody).style.left=p[0]+'px';t.style.top=p[1]+'px';}}
};
FZM.dialog.getById=function(s){
	var n,_s=FZM.dialog;if(!s){return _s.getCurrentDialog();}
	s=String(s);n=parseInt(s,10);(!isNaN(n))&&(s='fdialog'+s);return _s.instances[s];
};
FZM.dialog.getCurrentDialog=function(){return FZM.dialog.current;};
FZM.dialog.shell=FZM.dialog.base;
