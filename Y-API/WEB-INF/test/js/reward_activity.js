$(function(){
    //获取id
    var userId = getRequest('userId'); 
    //userId = '2vaXEHq4Ank3UyRC71S';

    ajaxTable1();

    //tab切换
    $('#reward .reward_top li').on('click',function(){
        var index = $(this).index();        
        $(this).addClass('active').siblings('#reward .reward_top li').removeClass('active');
        $('#reward .table').eq(index).show().siblings('#reward .table').hide();
        if(index == 0){
            ajaxTable1();  
        }else{
            ajaxTable2();
        }
    })



    //ajax加载函数     
    function ajaxTable1(){
        $.ajax({
            type: 'POST',
            url: baseData.houduan+'/share',                
            contentType: "application/json",
            data:JSON.stringify({"state":0,"uid":userId}),
            dataType:'json',            
            success:function(d){                
                var totalBonus = d.obj.mf;
                var totalPersonCount = d.obj.nf;
                var inviteRecorderModel = d.data;                
                var result = '';
                $.each(inviteRecorderModel,function(i,item){                     
                    result +='<tr><td>' + item.time +'</td><td>'+ item.mobile+'</td><td>'+(item.state?"已投资":"未投资")+'</td></tr>';
                });                
                result += '<tr><td colspan="3" style="border:0;">暂无更多数据</td></tr>';                               
                $('#reward .table1 tbody').html(result);
                $('#banner2 .left span').html(totalPersonCount);
                $('#banner2 .right span').html(totalBonus);                
            },
            error:function(data){
                console.log(data);
            }
        })
    }

    function ajaxTable2(){
        $.ajax({
            type: 'POST',
            url: baseData.houduan+'/share',                
            contentType: "application/json",
            data:JSON.stringify({"state":1,"uid":userId}),
            dataType:'json',            
            success:function(d){
                var totalBonus = d.obj.mf;
                var totalPersonCount = d.obj.nf;
                var inviteRecorderModel = d.data;                
                var result = '';
                $.each(inviteRecorderModel,function(i,item){  
                    result +='<tr><td>' + item.time +'</td><td>'+ item.mobile +'投资</td><td>'+item.money+'</td></tr>';
                });
                result += '<tr><td colspan="3" style="border:0;">暂无更多数据</td></tr>';                 
                $('#reward .table2 tbody').html(result);
                $('#banner2 .left span').html(totalPersonCount);
                $('#banner2 .right span').html(totalBonus);  
            },
            error:function(data){
                console.log(data);
            }
        })
    }    
})