$(function(){
    $('#left').on('click',function(){//我的奖励
        window.location.href = baseData.href + "/app/reward_activity.html?userId="+uid;
    });
    $('#right').on('click',function(){//邀请好友
        // var id = getRequest("id");
        var iosUrl = Base64.encode(baseData.href+"/app/register_activity.html?userId="+uid);
        if(checkIsIos){     			
            window.location.href="yingpiao://onShare2?title=我在【盈喵金服】理财，安全高息，还有更多豪礼！&subTitle=注册即享888大红包，新手年化收益率高达15%！&imagePath=aHR0cDovL3AxNzdhdG04cy5ia3QuY2xvdWRkbi5jb20vaWNvbi5wbmc=&shareUrl="+iosUrl; //需改			
        }else{
            try{
                ('我在【盈喵金服】理财，安全高息，还有更多豪礼！','注册即享888大红包，新手年化收益率高达15%！','https://app.yingpiaolicai.com/img/logo.png',baseData.href+'/app/register_activity.html?userId='+uid);			// http://www.yingpiaolicai.com/appsredpacket/app/springFeedback.html
            }catch(err){
                window.android.onShare();
            }		
        }            
    });
    //明暗文
    switchPwd();

    //获取验证码
    //fade($('#week'),str)        
    $('#huoqu').on('click',function(){
        if(checkTel()){
            var phone = $('#phone').val();
            //发送验证码
            $.ajax({
                type:'POST',
                url : baseData.houduan + '/coder',
                contentType: "application/json",
                data:JSON.stringify({ "mobile":phone}),
                dataType:'json',
                success:function(data){
                    console.log(data);                     
                    fade($('#week'),data.message);
                    if(data.success == true){
                        $('#huoqu').attr('disabled','disabled');
                        countDown();
                    }                                      
                } 
            })
        }
    })
    // 获取推荐人userID
    $('#tuijian').val(getRequest('userId'))
    //注册
    $('#zhuce').on('click',function(){
        var userId = getRequest('userId');
        console.log(userId)
        //userId = '2vaXEHq4Ank3UyRC71S';
        if(checkTel() && checkYzm() && checkMima()){ 
            var phone = $('#phone').val(); //15920445518
            var smscode = $('#yzm').val();//
            //密码rsa加密传
            var mima = $('#mima').val();
            // var encrypt = new JSEncrypt();
            // encrypt.setPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXaW6I2CN+PgH6sx3QVuRF2oVNEzDhrzfzpAsoTBGGH8nZv1AcnVaAjI7EgM69bgCpxKNIxfYx4G+ruGOv/SwmIW3r+4otv5K1ziSin2txNSh6yvc8y2BRQ+JbiQ6TyDE5NwSSXjuKLFYPoTAh4CYc0bawJ4KjLh88eXbxZm41eQIDAQAB");
            // var pwd = encrypt.encrypt(mima);                                 
            $.ajax({
                type: 'POST',
                url: baseData.houduan+'/register',                
                contentType: "application/json",
                data:JSON.stringify({ "mobile":phone,"pwd":mima,"vcode":smscode,"cid":userId}),
                dataType:'json',                
                success:function(data){
                    console.log(data);
                    if(data.success == true){
                        $('#dialog').show();
                    }else{
                        fade($('#week'),data.message);
                    }
                },
                error: function(data){
                    alert(data);
                }
            })
        }
    })

    //弹框点差 关闭
    $('#dialog .cha').on('click',function(){
        $('#dialog').hide();
    });


    //验证手机格式
    function checkTel(){
        $('.register li:nth-of-type(1) .tishi').html('');
        var phone = $('#phone').val();
        var reg = /^1[3,4,5,6,7,8,9]\d{9}$/; 
        if(phone == ''){
            $('.register li:nth-of-type(1) .tishi').html('手机号不能为空，请输入');
            return false;
        }else if(!reg.test(phone)){ 
            $('.register li:nth-of-type(1) .tishi').html('手机号格式有误，请重新输入');
            return false; 
        }
        return true; 
    }

    //验证验证码格式
    function checkYzm(){
        $('.register li:nth-of-type(2) .tishi').html('');
        var yzm = $('#yzm').val();
        var reg=/^\d{4,6}$/;        
        if(yzm == ''){
            $('.register li:nth-of-type(2) .tishi').html('验证码不能为空，请输入');
            return false;
        }else if(!reg.test(yzm)){
            $('.register li:nth-of-type(2) .tishi').html('验证码格式有误，请重新输入');
            return false;
        }
        return true;
    }

    //密码格式
    function checkMima(){
        $('.register li:nth-of-type(3) .tishi').html('');
        var mima = $('#mima').val();
        var len = mima.length;               
        if(mima == ''){
            $('.register li:nth-of-type(3) .tishi').html('密码不能为空，请输入');
            return false;
        }else if(len<6 || len>20){
            $('.register li:nth-of-type(3) .tishi').html('密码格式有误，请重新输入');
            return false;
        }
        return true;
    }

    //明文 暗文
    function switchPwd() {
        $('.yan').on('click',function(){
            var type = $('#mima').attr('type');
            if(type == 'password'){
                $('#mima').attr('type','text');
                $('.yanjing').attr('src','./images/invite_activity/zhengyan.png');
            }else{
                $('#mima').attr('type','password');
                $('.yanjing').attr('src','./images/invite_activity/biyan.png')
            }            
        });     
    }  
    
    //倒计时    
    function countDown(){        
        var time = 60;
        $('#huoqu').css('backgroundColor','#ccc');
        $('#huoqu').html(time+"s");
        var timer = setInterval(function(){
            time -- ;
            $('#huoqu').html(time+'s');
            if(time <= 0){                
                $('#huoqu').removeAttr('disabled');
                $('#huoqu').html('获取验证码');
                $('#huoqu').css('backgroundColor','#ffc000');
                clearInterval(timer);
            }
        },1000);
    }
})