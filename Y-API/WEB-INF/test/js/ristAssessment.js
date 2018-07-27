$(function(){
    var ind;    
    var score = 0;
    //uid = '2vaXEHq4Ank3UyRC71S';
    /*第一题*/
    firstQuestion();
    $('#anniu1').on('click',function(){
        uid = uid || getRequest('uid');
        key = key || getRequest('key');
        window.location.href = baseData.href+"/app/riskAssessment_second.html?uid="+uid+"&score=" + score+"&key=" + key;
    });

    /*第二题*/
    question(2);
    $('#anniu2').on('click',function(){
        uid = getRequest('uid');
        key = getRequest('key');
        window.location.href = baseData.href+"/app/riskAssessment_third.html?uid="+uid+"&score=" + score+"&key=" + key;
    });

    /*第三题*/
    question(3);
    $('#anniu3').on('click',function(){
        uid = getRequest('uid');
        key = getRequest('key');
        window.location.href = baseData.href+"/app/riskAssessment_fourth.html?uid="+uid+"&score=" + score+"&key=" + key;
    });

    /*第四题*/
    question(4);
    $('#anniu4').on('click',function(){
        uid = getRequest('uid');
        key = getRequest('key');
        window.location.href = baseData.href+"/app/riskAssessment_fifth.html?uid="+uid+"&score=" + score+"&key=" + key;
    });

    /*第五题*/
    question(5);
    $('#anniu5').on('click',function(){
        uid = getRequest('uid');
        key = getRequest('key');
        window.location.href = baseData.href+"/app/riskAssessment_result.html?uid="+uid+"&score=" + score+"&key=" + key;
    });

    function question(num){//2-5问题         
        var s =  parseInt(getRequest('score'));      
        $('#ti.t'+num+' li').on('click',function(){
            ind = $(this).index();//first = ind; 
            onTab(); 
            switch(ind){
                case 0 : score = s+40;break;
                case 1 : score = s+80;break;
                case 2 : score = s+120;break;
                case 3 : score = s+180;break;
                case 4 : score = s+200;break;
                default: break;             
            }
        });
    }

    function firstQuestion(){  //第一个问题 
        $('#ti.t1 li').on('click',function(){                       
            ind = $(this).index();//first = ind; 
            onTab(); 
            switch(ind){
                case 0 : score = 120;break;
                case 1 : score = 160;break;
                case 2 : score = 200;break;
                case 3 : score = 80;break;
                case 4 : score = 20;break;
                default: break;            
            }                     
        });        
    }

    function onTab(){
        $('#ti .anniu').removeAttr('disabled');
        $('#ti li').eq(ind).addClass('on').siblings('#ti li').removeClass('on');
    }

})

function loginStatus(userId,key1) {//用户id app端给      
    uid = userId;
    key = key1;
}
