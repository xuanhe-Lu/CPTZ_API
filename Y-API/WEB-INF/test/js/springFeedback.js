$(function(){
	//点击登录
	$('#login button').on('click',function(){		
		if(isWeixin()){
			document.getElementById('weixinLogin').click();	
		}else{				
			//区分浏览器，安卓，ios
			if(checkIsIos){	
     			window.location.href="yingpiao://onLogin";     							
			}else{				
				document.getElementById('weixinLogin').click();
			}
		}				
	});	
		
	var liObj = {};
	var userId = getCookie('userId');	
	if(userId){
		loginStatus(userId);//初始化页面
	}
	$('#hongbao li').on('click',function(){
		var d= new Date();
		var day = d.getDate();		
		if(day < 13){
			$('#week').html('活动还未开始，请敬请期待！');
			fade();
			return false;
		}else if(day > 22){
			$('#week').html('活动已经结束！');
			fade();
			return false;
		}
		userId = getCookie('userId');
		//alert(1);
		if(userId){
			var $that = $(this);			
			//1.判断红包是否翻过，没翻过的情况下
			if($(this).attr('isOpen')==0){							
				//2.今天有没有翻过
				$.ajax({
			        type : "GET",
			        url : "http://api-pro.yingpiaolicai.com/api/redpacket/redPacketIsOpen",
			        async : false,            
			        data:{"userId":userId},			        
			        success:function(data){
			        	//1代表今天没翻过，0代表已经翻过
			           	if(data==0){
			            	fade();
							return false;
			           	}else{
			           		liObj.index = $that.index();
			           		addCookie("index",liObj.index+1,1);
							liObj.x=$that.offset().left;
							liObj.y=$that.offset().top;
							//console.log(liObj);
							successOpenToday(0.88);	
							//tab1();		
							$('#dialog_container').show();										
							jinzhi();
			           	}
			           	$that.attr('isOpen',1);
			        }        
			    });
						
			}
		}
	});

	


	//弹框	
	//算了吧
	$('#dialog_container .d_right').on('click',function(){				
		//先放着
		jiechu();//页面恢复滚动
		$('#dialog_container').css({
			'background':'none'
		});
		$('#dialog_container button').hide();
		$('#dialog_container .dialog').animate({
			'left': liObj.x +'px',
			'top': liObj.y +'px',
			'width':'1.65rem',
			'height':'1.61rem'
		},200,function(){
			$('#dialog_container').hide();
			//复原
			oldDialog();
			$('#hongbao li').eq(liObj.index).children('.money').html('<div class="moneyYes">恭喜您获得<br /><span>￥0.88</span></div>');
			$('#hongbao li').eq(liObj.index).children('.img1').attr('src','images/springFeedback/chaikaihongbao.png');
			
			window.location.reload();

		});
	})

	//测试
	//去分享,微信	

	if(isWeixin()){//在微信打开
		suanLba();		
	}else{		
		$('#dialog_container .d_left').on('click',function(){
			var userAgent = navigator.userAgent.toLowerCase();//获取userAgent
     		//var isInapp = userAgent.indexOf("sunyuki")>=0;//查询是否有相关app的相关字段
			//调用原生的分享			
			if(checkIsIos){     			
     			window.location.href="yingpiao://onShare2?title=盈票理财&subTitle=春节大回馈，送您新年大红包，各种福利拿到手软，快来参与吧！&imagePath=aHR0cDovL3AxNzdhdG04cy5ia3QuY2xvdWRkbi5jb20vaWNvbi5wbmc=&shareUrl=aHR0cDovL3d3dy55aW5ncGlhb2xpY2FpLmNvbS9hcHBzcmVkcGFja2V0L2FwcC9zcHJpbmdGZWVkYmFjay5odG1sP3g=";     			
			}else{				
				window.android.onShare('盈票理财','春节大回馈，送您新年大红包，各种福利拿到手软，快来参与吧！','http://p177atm8s.bkt.clouddn.com/icon.png','http://www.yingpiaolicai.com/appsredpacket/app/springFeedback.html?x=');			// TODO:sjdasdhs					
			}				
		});
	}

	function jinzhi(){//禁止页面滚动
		$('html').css('overflow','hidden')
		$('html').css('height','100%');
		$('body').css('overflow','hidden');
		$('body').css('height','100%');
	}

	function jiechu(){//恢复页面的滚动
		$('html').css('overflow','visible')
		$('html').css('height','auto');
		$('body').css('overflow','visible');
		$('body').css('height','auto');	
	}

	//红包
	function oldDialog(){//弹框前
		$('#dialog_container').css({
			'background':'rgba(49, 49, 49, .65)'
		});
		$('#dialog_container .dialog').css({
			width:'6.06rem',
			height:'6.74rem',
			top:'3.42rem',
			left:'.72rem'
		});
		$('#dialog_container button').show();
	}

	//淡入淡出
	function fade(){
		$('#week').fadeTo("200",1,function(){
			var timer = setTimeout(function(){
				$('#week').fadeOut("200");
				clearTimeout(timer);
			},1000);
		});
	}

	function suanLba(){//算了吧样式
		$('#dialog_container .d_left').html('关闭');
		$('#dialog_container p').html('下载APP参与双倍');
		$('#dialog_container .d_left').on('click',function(){
			$('#dialog_container').hide();
			//window.location.reload();
			document.getElementById('weixinHD').click();			
		});
	}

	//返回今天有没有翻红包的信息给后端
	function successOpenToday(x){
		var totalMoney = Number(parseFloat($('#login span').html())+x).toFixed(2); 	    		
		//console.log(totalMoney);			
		//传红包信息
		$.ajax({
			type: "POST",
	        url: "http://api-pro.yingpiaolicai.com/api/redpacket/insertRedPacketInfo",
	        async : false,
	        contentType: "application/json",
	        data:JSON.stringify({"userId":userId,"isOpen": 1,"redMoney": x,"redPacketId": liObj.index+1,"totalMoney":totalMoney}),
	        dataType:'json',
	        success:function(data){
	        	console.log(data);
	        }        
		});
	}
})