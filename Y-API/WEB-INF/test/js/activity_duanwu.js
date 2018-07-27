//微信分享
$('#banner .share').on('click',function(){
    //var id = getRequest("id");
    var iosUrl = Base64.encode(baseData.href+"/app/activity_duanwu_weixin.html");        
    if(checkIsIos){     			
        window.location.href="yingpiao://onShare2?title=盈喵金服&subTitle=迎端午，送壕礼！现在投资，就有5000元红包等你拿，投资30万额外赠送一部iphoneX哦！&imagePath=aHR0cDovL3AxNzdhdG04cy5ia3QuY2xvdWRkbi5jb20vaWNvbi5wbmc=&shareUrl="+iosUrl; //需改			
    }else{
        try{
            window.android.onShare('盈喵金服','迎端午，送壕礼！现在投资，就有5000元红包等你拿，投资30万额外赠送一部iphoneX哦！','https://app.yingpiaolicai.com/img/logo.png',baseData.href+'/app/activity_duanwu_weixin.html');			
        }catch(err){
            window.android.onShare();
        }		
    }   
});

ajax618();//端午节标

//立即参与 获取产品
$('#touzi').on('click',function(){
    var productId = $(this).attr('productId');
    product(productId);
});


//用户存在
if(uid){
    alreadyLogin(); 
}

//投资记录
$('#youce .jilu').on('click',function(){
    if(uid){
        $('#youce .jilu').attr('href','./activity_duanwu_record.html?uid='+uid);
    }else{
        login();
    }
});

//累计投资金额
function ajax_touzi(){
    $.ajax({
        type:'POST',
        url : baseData.houduan + '/20180618/list',
        contentType: "application/json",
        dataType:'json',
        data:JSON.stringify({ "uid":uid}),
        success:function(d){
            console.log(d);            
            var data = d.data;
            if(data.length>0){
                $('#main .phone .yes span').html(data[0].all+'元'); 
            }
        } 
    })
}

//登录后查看我的投资金额
$('#main .phone .no').on('click',function(){
    login();
});


//已经登录了
function alreadyLogin(){
    $('#main .phone .no').hide();
    $('#main .phone .yes').show();
    ajax_touzi();
}

/*获取产品id*/
function ajax618(){
    $.ajax({
        type:'POST',
        url : baseData.houduan + '/act_info',
        contentType: "application/json",
        data:JSON.stringify({ "adj":3,"max":1}),
        dataType:'json',
        success:function(d){
            //console.log(d);
            var data = d.data;
            var productId=0;
            if(data.length>=1){
                productId = data[0].pid;                
            }
            $('#touzi').attr('productId',productId);          
        } 
    })
}

function product(x){
    // if(x == 0){
    //     fade($('#week'),'稍等片刻，努力上架产品中…');
    // }else 
    if(checkIsIos){
        window.location.href = "yingpiao://onProductDetail?proId="+x;
        // window.location.href = "yingpiao://toProductList" ;
    }else{
        // window.android.onProductDetail(x);
        window.android.toProductList();
    }
}

//登录
function login(){
    if(checkIsIos){
        window.location.href = "yingpiao://onLogin";
    }else{
        window.android.toLogin();//安卓登录
    }
}

//uid
function loginStatus(userId,key1) {
    uid = userId;
    if(uid){
        alreadyLogin(); 
    }
    ajax_rank();
}
