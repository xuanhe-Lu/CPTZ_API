(function(){
tinymce.PluginManager.requireLangPack("parmflash");
tinymce.create("tinymce.plugins.parmflashPlugin",{
	init:function(ed,url){
		ed.addCommand("mceparmflash",function(){var A=new Ext.tinymce.flash.UploadWin(ed,url);A.show();});
		ed.addButton("parmflash",{title:"parmflash.desc",cmd:"mceparmflash",image:url+"/img/sample.gif"});
	},
	createControl:function(n,cm){return null;}
});
tinymce.PluginManager.add("parmflash",tinymce.plugins.parmflashPlugin);
})();