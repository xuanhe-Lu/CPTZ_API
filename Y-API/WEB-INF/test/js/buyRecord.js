$(function(){
    //获取产品的id
    var productId = getRequest('productId');    
    //下拉加载，10个一页
    var currentPage = 1;
    var totalPage;//总页数 
    //进入页面即加载10条数据
    ajaxTable();
    //点击加载
    $('#click').on('click',function(){
        ajaxTable();
    });

    //ajax加载函数
    function ajaxTable(){
        $.ajax({
            type: 'POST',
            url: 'http://api-pro.yingpiaolicai.com/api/product/getProductPurchaseRecorde',                
            contentType: "application/json",
            data:JSON.stringify({"currentPage":currentPage,"pageSize":10,"productId":productId}),
            dataType:'json',
            beforeSend:function(XMLHttpRequest){
                //alert('远程调用开始...');
                $("#click").html("<img src='./images/loading.gif'/>");
            },
            success:function(data){
                var products = data.products;                    
                totalPage = data.totalPage;//总页数                    
                var result = '';
                if(currentPage<=totalPage){
                    $.each(products,function(i,item){  
                        result +='<tr><td>'+item.user_phone+'</td><td>'+item.order_total_amount+'</td><td>'+item.order_create_time+'</td></tr>';
                    });
                    $('#click').html('点击加载更多');
                }else{
                    noData();
                }
                currentPage++;                
                $('#buyRecord tbody').append(result);
            },
            error: function(xhr, type){
                if(totalPage < currentPage){
                    noData();                        
                }
            }
        })
    }

    //没有数据时
    function noData(){
        $('#click').html('暂无更多数据');
        $('#click').attr('disabled','disabled');
    }

    
})