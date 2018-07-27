﻿window.FZM=window.FZM||{}
FZM.dragdrop=FZM.dragdrop||{};
FZM.dragdrop.dragGhostStyle="cursor:move;position:absolute;border:1px solid #06c;background:#6cf;z-index:1000;color:#003;overflow:hidden";
FZM.dragdrop.startDrag=FZM.dragdrop.doStartDrag=function(e,handlerId,target,opts){
	var _d=FZM.dom,_e=FZM.event,_s=FZM.dragdrop,et,t;if(_e.getButton(e)!=0||((et=_e.getTarget(e))&&et.noDrag)){return;}
	if(opts.ignoreTagName==et.tagName||et.noDragdrop){return;}
	var size=_d.getSize(target),stylePosition=_d.getStyle(target,"position"),isAbsolute=stylePosition=="absolute"||stylePosition=="fixed",ghost=null,hasGhost=false,xy=null;if(opts.rangeElement){var _re=opts.rangeElement,_el=FZM.dom.get(_re[0]),_elSize=FZM.dom.getSize(_el),_r=_re[1];if(!_re[2]){opts.range=[_r[0]?0:null,_r[1]?0:null,_r[2]?_elSize[1]:null,_r[3]?_elSize[0]:null];}else{var _elXY=FZM.dom.getXY(_el);opts.range=[_r[0]?_elXY[1]:null,_r[1]?_elXY[0]:null,_r[2]?_elXY[1]+_elSize[1]:null,_r[3]?_elXY[0]+_elSize[0]:null];}}
	xy=isAbsolute?[parseInt(target.style.left,10),parseInt(target.style.top,10)]:_d.getXY(target);if(isNaN(xy[0])||isNaN(xy[1])){t=_d.getXY(target);isNaN(xy[0])&&(xy[0]=t[0]);isNaN(xy[1])&&(xy[1]=t[1]);}
	if(!isAbsolute||opts.ghost){ghost=_d.createElementIn("div",isAbsolute?target.parentNode:document.body,false,{style:opts.ghostStyle||_s.dragGhostStyle});ghost.id="dragGhost";_d.setStyle(ghost,"opacity","0.8");setTimeout(function(){_d.setStyle(target,"opacity","0.5");},0);if(opts.ghostSize){_d.setSize(ghost,opts.ghostSize[0],opts.ghostSize[1]);xy=[e.clientX+FZM.dom.getSL()-30,e.clientY+FZM.dom.getST()-20];}else{_d.setSize(ghost,size[0]-2,size[1]-2);}
	_d.setXY(ghost,xy[0],xy[1]);hasGhost=true;}
	var dragTarget=ghost||target;_s.currentDragCache={size:size,xy:xy,mXY:xy,dragTarget:dragTarget,target:target,x:e.clientX-parseInt(xy[0]),y:e.clientY-parseInt(xy[1]),ghost:ghost,hasGhost:hasGhost,isAbsolute:isAbsolute,options:opts,scrollRangeTop:_s._scrollRange||0,scrollRangeBottom:FZM.dom.getCH()-(_s._scrollRange||0),maxScrollRange:Math.max(FZM.dom.getSH()-FZM.dom.getCH(),0)};_e.on(document,"mousemove",_s.doDrag,[handlerId,_s.currentDragCache,opts]);_e.on(document,"mouseup",_s.endDrag,[handlerId,_s.currentDragCache,opts]);t=_s.dragdropPool[handlerId];t&&(typeof(t.onStartDrag)=="function")&&t.onStartDrag.apply(t,[e,handlerId,_s.currentDragCache,opts]);try{_e.preventDefault(e);}catch(ign){}
};
FZM.dragdrop.doDrag=function(e,handlerId,dragCache,opts){
	var pos={},_s=FZM.dragdrop;if(opts.autoScroll){if(e.clientY<dragCache.scrollRangeTop){if(!_s._scrollTop){_s._stopScroll();_s._scrollTimer=setTimeout(function(){_s._doScroll(true,dragCache)},200);}}else if(e.clientY>dragCache.scrollRangeBottom){if(!_s._scrollBottom){_s._stopScroll();_s._scrollTimer=setTimeout(function(){_s._doScroll(false,dragCache)},200);}}else{_s._stopScroll();}}
	var mX=e.clientX-dragCache.x;var mY=e.clientY-dragCache.y;var xy=_s._countXY(mX,mY,dragCache.size,opts);mX=xy.x;mY=xy.y;FZM.dom.setXY(dragCache.dragTarget,mX,mY);dragCache.mXY=[mX,mY];(_s.dragdropPool[handlerId]&&typeof(_s.dragdropPool[handlerId].onDoDrag)=="function")&&_s.dragdropPool[handlerId].onDoDrag.apply(null,[e,handlerId,dragCache,opts]);if(FZM.userAgent.ie){document.body.setCapture();}else if(window.captureEvents){window.captureEvents(Event.MOUSEMOVE|Event.MOUSEUP);}
	FZM.event.preventDefault();
};
FZM.dragdrop.endDrag=function(e,handlerId,dragCache,opts){
	var _d=FZM.dom,_s=FZM.dragdrop,t;
	if(dragCache.hasGhost){
		FZM.dom.removeElement(dragCache.dragTarget);
		var _t=dragCache.target;setTimeout(function(){FZM.dom.setStyle(_t,"opacity","1");_t=null;},0);
		if(dragCache.isAbsolute){
			var x=parseInt(_d.getStyle(_t,"left"),10)+(dragCache.mXY[0]-dragCache.xy[0]);var y=parseInt(_d.getStyle(_t,"top"),10)+(dragCache.mXY[1]-dragCache.xy[1]);if(isNaN(x)||isNaN(y)){t=_d.getXY(_t);isNaN(x)&&(x=t[0]+(dragCache.mXY[0]-dragCache.xy[0]));isNaN(y)&&(y=t[1]+(dragCache.mXY[1]-dragCache.xy[1]));}
			var xy=_s._countXY(x,y,dragCache.size,opts);
			FZM.dom.setXY(_t,xy.x,xy.y);
		}
	}
	FZM.event.removeEvent(document,"mousemove",_s.doDrag);
	FZM.event.removeEvent(document,"mouseup",_s.endDrag);

	(_s.dragdropPool[handlerId]&&typeof(_s.dragdropPool[handlerId].onEndDrag)=="function")&&_s.dragdropPool[handlerId].onEndDrag.apply(null,[e,handlerId,dragCache,opts]);
	dragCache=null;_s._stopScroll();if(FZM.userAgent.ie){document.body.releaseCapture();}else if(window.releaseEvents){window.releaseEvents(Event.MOUSEMOVE|Event.MOUSEUP);}};

FZM.dragdrop._doScroll=function(isUp,dc){step=isUp?-15:15;var _st=FZM.dom.getST(),_s=FZM.dragdrop;if(isUp&&_st+step<0){step=0;}
if(!isUp&&_st+step>dc.maxScrollRange){step=0;}
FZM.dom.setScrollTop(_st+step);dc.y=dc.y-step;_s._scrollTop=isUp;_s._scrollBottom=!isUp;_s._scrollTimer=setTimeout(function(){_s._doScroll(isUp,dc)},16);};

FZM.dragdrop._stopScroll=function(){var _s=FZM.dragdrop;_s._scrollTop=_s._scrollBottom=false;clearTimeout(_s._scrollTimer);};
FZM.dragdrop._countXY=function(x,y,size,opts){var pos={x:x,y:y};if(opts.x){pos["x"]=parseInt(pos["x"]/opts.x,10)*opts.x+(pos["x"]%opts.x<opts.x/2?0:opts.x);}
if(opts.y){pos["y"]=parseInt(pos["y"]/opts.y,10)*opts.y+(pos["y"]%opts.y<opts.y/2?0:opts.y);}
if(opts.range){var _r=opts.range;var i=0,j=0;while(i<_r.length&&j<2){if(typeof _r[i]!="number"){i++;continue;};var k=i%2?"x":"y";var v=pos[k];pos[k]=i<2?Math.max(pos[k],_r[i]):Math.min(pos[k],_r[i]-size[(i+1)%2]);if(pos[k]!=v){j++;};i++;}}
return pos};
