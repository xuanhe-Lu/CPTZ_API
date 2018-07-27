(function(){
tinymce.PluginManager.requireLangPack("parmimage");
tinymce.create("tinymce.plugins.parmimagePlugin",{
	init:function(ed,url){
		ed.addCommand("mceparmimage",function(){var A=new Ext.tinymce.image.UploadWin(ed,url);A.show();});
		ed.addButton("parmimage",{title:"parmimage.desc",cmd:"mceparmimage",image:url + "/img/sample.gif"});
	},
	createControl:function(n,cm){
		return null;
	}
});
tinymce.PluginManager.add("parmimage",tinymce.plugins.parmimagePlugin);
})();