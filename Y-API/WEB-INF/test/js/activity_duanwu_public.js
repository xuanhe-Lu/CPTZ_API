//排名
// ajax_rank();
function ajax_rank(){
    // confirm(uid);
    $.ajax({
        type:'POST',
        url : baseData.houduan + '/20180618',
        contentType: "application/json",
        dataType:'json',
        data:JSON.stringify({ "uid":uid}),
        success:function(d){
            console.log(d);
            var data = d.data;                                                
            var len = data.length;
            var num = len<10?len:10;
            var str='';
            for(var i=0;i<len;i++){
                var rank = '';
                if(data[i].index>3){
                    rank=data[i].index;
                }                
                str+='<tr><td>'+rank+'</td><td>'+data[i].mobile+'</td><td>'+data[i].money+'</td></tr>';
            }
            $('#main .paiming .table tbody').html(str);
            var myRank = d.index;
            if(myRank>0){
                $('#main .myRanking').html('我的排名：'+ myRank);
            }
        } 
    })
}

//弹框
$('#youce .guize').on('click',function(){
    $('#dialog').show();
});
$('#dialog .cha').on('click',function(){
    $('#dialog').hide();
});


