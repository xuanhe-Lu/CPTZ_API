$(function () {
    var uid = getRequest('userId')
    alert(uid)
    $.ajax({
        type:'POST',
        url : "demoJson/data1.json",
        contentType: "application/json",
        // data:JSON.stringify({"uid":107918}),
        dataType:'json',
        success:function(res){
            console.log(res);
            for(index in res.body.lotterys){
                //渲染奖品区域数据
                var str = "",btn="";
                var clum = res.body.lotterys[index].id-1
                str += '<li class="item_'+clum+' itmes">' +
                    '                    <img src="'+res.body.lotterys[index].url+'" alt="">' +
                    '                    <div class="shadow"></div>' +
                    '                </li>'
                $('.list_wrap').append(str);
            }
            btn = "<li id=\"btn\">\n" +
                "                    <img src=\"images/lottery/rewards_9.png\" alt=\"\">\n" +
                "                    <p class=\"count\">剩余次数：<span class=\"num\">"+res.body.count+"</span></p>\n" +
                "                </li>"
            $('.item_7').after(btn)
        }
    });
    var luck={
        index:0,	//当前转动到哪个位置，起点位置
        count:0,	//总共有多少个位置
        timer:0,	//setTimeout的ID，用clearTimeout清除
        speed:20,	//初始转动速度
        times:0,	//转动次数
        cycle:50,	//转动基本次数：即至少需要转动多少次再进入抽奖环节
        prize:-1,	//中奖位置
        init:function(id){
            if ($("."+id).find(".itmes").length>0) {
                $luck = $("."+id);
                $units = $luck.find(".itmes");
                this.obj = $luck;
                this.count = $units.length;
                $luck.find(".item_"+this.index).find('.shadow').show();
            };
        },


        roll:function(){
            var index = this.index;
            var count = this.count;
            var luck = this.obj;
            $luck.find(".itmes").find('.shadow').hide();
            index += 1;
            if (index>count-1) {
                index = 0;
            };
            $luck.find(".item_"+(this.index)).find('.shadow').show();
            console.log(this.index)
            this.index=index;
            return false;
        },
        stop:function(index){
            this.prize=index;
            return false;
        },

    };
    var  prizeId,name
    function roll(){
        luck.times += 1;
        // console.log(luck.times)
        luck.roll();
        if (luck.times > luck.cycle+10 && luck.prize==luck.index) {
            clearTimeout(luck.timer);
            function popup(){
                $('.bgShadow').show();
                $('.pop_up').show();
                $('.prize').text(name)
            }
            popup();
            luck.prize=-1;
            luck.times=0;
            click=false;
        }else{
            if (luck.times<luck.cycle) {
                luck.speed -= 10;
            }else if(luck.times==luck.cycle) {
                // var prizeId = 7
                // console.log(prizeId+"fdsfs")
                luck.prize = prizeId;//最终中奖位置

            }else{
                if (luck.times > luck.cycle+10 && ((luck.prize==0 && luck.index==7) || luck.prize==luck.index+1)) {
                    luck.speed += 110;
                }else{
                    luck.speed += 20;
                }
            }
            if (luck.speed<40) {
                luck.speed=40;
            };
            luck.timer = setTimeout(roll,luck.speed);
        }
        return false;
    }

//弹出框关闭
    $('.sure').click(function () {
        $('.bgShadow').hide();
        $('.pop_up').hide();
    })
    var click=false;
    window.onload=function(){
        luck.init('list_wrap');
        $("#btn").click(function(){
            if(click) {
                return false;
            }
            else{
                var cont = $('.num').text()
                cont --
                if(cont<0){
                    console.log("sb")
                }else {
                    //调取接口，弹出中奖信息
                    $.ajax({
                        type:'POST',
                        url : "demoJson/save.json",
                        contentType: "application/json",
                        async:false,
                        // data:JSON.stringify({"uid":107918}),
                        dataType:'json',
                        success:function(res){
                            $('.num').text(cont)
                            console.log(res);
                            prizeId=res.body.id;
                            name = res.body.name
                            luck.speed=100;
                            roll();
                            click=true;
                        }
                    });
                }
                return false;
            }

        });
    };
});