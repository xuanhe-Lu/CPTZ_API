$(function(){
	var secondsNum = 60;

	//获取验证码
	$('#huoqu').on('click',function(){
		//先判断有没有输手机号
		if (checkPhone()) {
			if (checkPassword()) {
			//发送验证码
			var userPhone =  $('#tel').val();
			var password = $('#password').val();
		    $.ajax({
		        type: "POST",
		        url: "getCode.do",
		        data:{phone:userPhone,password:password},
		        success: function(data) {
		     		var success = data.success;
		 			var msg = data.message;
		 			if(success==true){	
		 				var timer = setInterval(function(){
		 					secondsNum --;
		 					// console.log(secondsNum);
		 					$('#huoqu').attr('disabled',true)
		 					$('#huoqu').text(secondsNum);
		 					if(secondsNum <= 0){
		 						clearInterval(timer);
		 						secondsNum  = 60 ;
		 						$('#huoqu').text('获取验证码');
		 						$('#huoqu').attr('disabled',false)
		 					}

		 				},1000)
		 			}else{
		 				$('#alertInfoWrap').show();			
		 				$('#alertInfoWrap .alertContent').html(msg);
		 			}	    	        	  	        	            
		        } ,
		        error:function(data){
		        	console.log(data);
		        }
		    });	
			
		}
		}
	});

	//手机号的判断
	function checkPhone(){
		var $tel = $('#tel').val();
		if($tel == ""){	
			$('#alertInfoWrap').show();		
			$('#alertInfoWrap .alertContent').html('请输入手机号');
			return false;
		}else if(!(/^1[3|4|5|7|8][0-9]{9}$/.test($tel))){
			$('#alertInfoWrap').show();			
			$('#alertInfoWrap .alertContent').html('请输入正确的手机号');
			return false;
		}
		return true;
	}

	//密码的判断
	function checkPassword(){
		var $password = $('#password').val();
		if($password == ''){//判断密码
			$('#alertInfoWrap').show();		
			$('#alertInfoWrap .alertContent').html('请输入密码');
			return false;
		}else if($password.length<6 ||$password.length>16){
			$('#alertInfoWrap').show();	
			$('#alertInfoWrap .alertContent').html('请输入6-16位密码');
			return false;
		}
		return true;
	}

	

	//提示信息部分,关闭
	$('#hideAlertInfo').on('click',function(){
		$('#alertInfoWrap').hide();
	})

	//注册成功，关闭
	$('#closeImg').on('click',function(){
		$('#regSAlert').hide();
	})

	$('#zhuce').on('click',function(){
	
		var password = $('#password').val();
		var verificationCode = $('#yzm').val();
		var userPhone =  $('#tel').val();
		var InviteCode=$('#getInviteCode').val();
		
		if(password.length>16 || password.length < 6){
			$('#alertInfoWrap').show();	
			$('#alertInfoWrap .alertContent').html('请输入6-16位密码');
		}
		
	    $.ajax({
	        type: "POST",
	        url: "CheckRegister.do",
	        data:{phone:userPhone,password:md5(password),verificationCode:verificationCode,InviteCode:InviteCode},
	        success: function(data) {
	     		var success = data.success;
	 			var msg = data.message;
	 			if(success==true){	 	
	 				$('#regSAlert').show();
	 				return;
	 			}else{
	 				$('#alertInfoWrap').show();			
	 				$('#alertInfoWrap .alertContent').html(msg);
	 				return;
	 			}	    	        	  	        	            
	        }    	       
	    });			
	});

});

