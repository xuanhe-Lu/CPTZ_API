ajax518();

//标1
$('#btn1').on('click',function(){
    var ida = $('#main .tehui li:nth-of-type(1)').attr('productId');
    product(ida);
});

//标2
$('#btn2').on('click',function(){
    var idb = $('#main .tehui li:nth-of-type(2)').attr('productId');
    product(idb);
});

//点击登录
$('#btn3').on('click',function(){
    if(checkIsIos){
        window.location.href = "yingpiao://onLogin";
    }else{
        window.android.toLogin();//安卓登录
    }
})

//点击查看明细
$('#btn4').on('click',function(){
    window.location.href = baseData.href + "/app/activity_518_fanxian.html?uid="+uid;
})

function product(x){
    if(x == 0){
        fade($('#week'),'稍等片刻，努力上架产品中…');
    }else if(checkIsIos){
        window.location.href = "yingpiao://onProductDetail?proId="+x;
    }else{
        window.android.onProductDetail(x)
    }
}

//jiekou
function ajax518(){
    $.ajax({
        type:'POST',
        url : baseData.houduan + '/user_return',
        contentType: "application/json",
        data:JSON.stringify({ "uid":uid}),
        dataType:'json',
        success:function(d){
            var obj = d.obj;
            $('#main .tehui li:nth-of-type(1)').attr('productId',obj.ida);
            $('#main .tehui li:nth-of-type(2)').attr('productId',obj.idb);
            $('#main .yes h4').html(obj.all+"元");                                                 
        } 
    })
}


function loginStatus(userId,key1) {
    uid = userId;
    if(uid){
        $('#main .no').hide();
        $('#main .yes').show();
        ajax518();
    }
}



