(function(){
	tinymce.PluginManager.requireLangPack('parmtext');
	tinymce.create('tinymce.plugins.parmtextPlugin',{
		init : function(ed, url) 
		{
			// Register the command so that it can be invoked by using tinyMCE.activeEditor.execCommand('mceExample');
			ed.addCommand('mceparmtext', function() 
			{								
				var node = ed.selection.getNode();					
				var textnode = null;
				var imgsrc = url + '/img/control.gif';
				if (node.nodeName === 'INPUT' && node.type=='image' && node.src==imgsrc)				
					textnode = node;
				
				var dialog = new Ext.parm.workflow.manager.formflow.TextPropertiesWin(ed,url,textnode);
				dialog.show();
			});

			// Register example button
			ed.addButton('parmtext', {
				title : 'parmtext.desc',
				cmd : 'mceparmtext',
				image : url + '/img/control.gif'
			});
			ed.onNodeChange.add(function(ed, cm, n) {	
				var imgsrc = url + '/img/control.gif';						
				cm.setActive('parmtext', n.nodeName == 'INPUT'&& n.type=='image' && n.src==imgsrc);
			});
			ed.onClick.add(function(ed, e) {
				e = e.target;		
				var imgsrc = url + '/img/control.gif';				
				if (e.nodeName === 'INPUT' && e.type=='image' && e.src==imgsrc)
					ed.selection.select(e);
			});
		},
		createControl : function(n, cm) {
			return null;
		},
		getInfo : function() {
			return {
				longname : 'Example plugin',
				author : 'Some author',
				authorurl : 'http://tinymce.moxiecode.com',
				infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/example',
				version : "1.0"
			};
		}
	});
	tinymce.PluginManager.add('parmtext', tinymce.plugins.parmtextPlugin);
})();