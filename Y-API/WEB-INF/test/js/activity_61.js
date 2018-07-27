/*关闭弹框*/
$('#dialog .cha').on('click',function(){
    $('#dialog').hide();
});

ajax61();

//标1
$('#main .haoli1 .touzi').on('click',function(){
    var ida = $(this).attr('productId');
    product(ida);
});

//标2
$('#main .haoli2 .touzi').on('click',function(){
    var idb = $(this).attr('productId');
    product(idb);
});

/*获取产品id*/
function ajax61(){
    $.ajax({
        type:'POST',
        url : baseData.houduan + '/act_info',
        contentType: "application/json",
        data:JSON.stringify({ "adj":2,"max":1}),
        dataType:'json',
        success:function(d){
            console.log(d);
            var data = d.data;
            var productId=0;
            if(data.length>=1){
                productId = data[0].pid;                
            }
            $('#main .haoli1 .touzi').attr('productId',productId);
            $('#main .haoli2 .touzi').attr('productId',productId);            
        } 
    })
}

function product(x){
    if(x == 0){
        fade($('#week'),'稍等片刻，努力上架产品中…');
    }else if(checkIsIos){
        window.location.href = "yingpiao://onProductDetail?proId="+x;
    }else{
        window.android.onProductDetail(x)
    }
}


function loginStatus(userId,key1) {
    uid = userId;
    if(uid){
        ajax61();
    }
}