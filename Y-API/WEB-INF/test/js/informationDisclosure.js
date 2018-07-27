$(function(){
    /*tab切换*/
    $('nav li').on('click',function(){
        var i = $(this).index();
        $(this).addClass('active').siblings('nav li').removeClass('active');
        $('#tab .tab').eq(i).show().siblings('#tab .tab').hide();
    });
    /*展开与隐藏*/
    $('#tab .show').on('click',function(){        
        $(this).siblings('#tab .zhanKai').show();
        $(this).siblings('#tab .hide').show();
        $(this).hide();        
    });
    $('#tab .hide').on('click',function(){        
        $(this).siblings('#tab .zhanKai').hide();
        $(this).siblings('#tab .show').show();
        $(this).hide();        
    });
});
