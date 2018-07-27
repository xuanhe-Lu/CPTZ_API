var score;
//uid = 'egmURJN2QyhiFXUvezX';
//console.log((new Date()).getTime());

/*结果*/
// if(checkIsIos == false){
//     uid = window.android.getUserId();
//     uidScore();      
// }

uidScore();

function uidScore(){
    uid = uid || getRequest('uid');
    key = key || getRequest('key');
    score = getRequest('score') || 0;    
    if(uid){
        ajaxScore();
        if(score == getRequest('score')){
            result(score);
        }
    }
}

function ajaxScore(){
    var t = (new Date()).getTime();
    $.ajax({
        type : "POST",
        url : baseData.houduan + "/user_info/score",        
        async : false,
        contentType: "application/json",
        headers: {
            'uid': uid,
            'nonce_str': t,
            'key': hex_md5(key+''+t)
        },
        //data:JSON.stringify({"userId":uid,"score":score,'key':key+''+(new Date()).getTime()}),
        data:JSON.stringify({'nc':score}),
        dataType:'json',
        success:function(data){ 
            console.log(data)                                         
            if(data.nc || data.nc == '0'){
                result(data.nc);
            }
        }        
    });
}

//重新测评
$('#reassessment').on('click',function(){
    uid = uid || getRequest('uid');
    key = key || getRequest('key');
    window.location.href = baseData.href+"/app/riskAssessment_first.html?uid="+uid+"&key=" + key;
})

function result(score){ //结果                       
    $('#result h4 span').html(score);
    if(score>=0 && score<=200){
        $('#result h3').html('谨慎性');
        $('#result p').html('您对风险比较敏感，投资时谨慎小心，更注重资产的安全性，确保本金绝对安全。');
    }else if(score>=201 && score<=400){
        $('#result h3').html('稳健型');
        $('#result p').html('您对风险有一定的认识，愿意承受轻度的投资风险，可以接受收益小幅波动，投资是选择安全文件的产品。');
    }else if(score>=401 && score<=600){
        $('#result h3').html('平衡型');
        $('#result p').html('您是稳健的投资人，风险承受度适中，偏向于资产均衡配置，也追求一定的资产收益。');
    }else if(score>=601 && score<=800){
        $('#result h3').html('成长型');
        $('#result p').html('您偏向于积极的资产配置，对风险有较高的承受能力，投资收益预期相对较高。');
    }else if(score>=801 && score<=1000){
        $('#result h3').html('进取型');
        $('#result p').html('你的风险承受能力较高，投资时您大胆进取，用于尝试，资产配置以高风险投资品种为主，追求收益最大化，是资深的投资者。');
    };
    if(checkIsIos==false){
        window.android.updateScore(score);//给安卓传分数         
    };
} 

function loginStatus(userId,key1) {//用户id app端给       
    uid = userId; 
    key = key1;
    uidScore();    
}
