$(function(){	
	//根字体大小20px（iphone6）
	document.documentElement.style.fontSize = document.documentElement.clientWidth / 750*100 + 'px';
	window.addEventListener('resize',function(){
		document.documentElement.style.fontSize = document.documentElement.clientWidth / 750*100 + 'px';
	})
	//ie6-9禁止复制文字写法
	document.body.onselectstart = document.body.ondrag = function(){
		return false;
	}
})

var checkIsIos = null; //全局变量
function checkPhoneType(){	
	var u = navigator.userAgent;
	var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
	var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端			
	if (isiOS) {				
		//执行iso交互操作
		//alert('是否是iOS：'+isiOS);
		checkIsIos = true;			
	}else{				
		//否则就是Android则执行Android操作
		//alert('是否是Android：'+isAndroid);
		checkIsIos = false;
	}	
}
checkPhoneType();


//判断网页是否在微信
function isWeixin(){
    var ua = navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i)=="micromessenger") {
        return true;
    } else {
        return false;
    }
}

//获取url传参的参数
function getRequest(name){//获取url参数                          
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);        
    if (r != null){                                             
        return r[2];                                            
    }                                                           
    return null;                                                
} 

var uid;
var key; 
   


//淡入淡出
function fade($obj,str){
	$obj.html(str);
	$obj.fadeTo("200",1,function(){
		var timer = setTimeout(function(){
			$obj.fadeOut("200");
			clearTimeout(timer);
		},1000);
	});
}