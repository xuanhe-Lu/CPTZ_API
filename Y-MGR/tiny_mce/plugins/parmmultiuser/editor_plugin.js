/**
 * editor_plugin_src.js
 *
 * Copyright 2009, Moxiecode Systems AB
 * Released under LGPL License.
 *
 * License: http://tinymce.moxiecode.com/license
 * Contributing: http://tinymce.moxiecode.com/contributing
 */

(function() {
	// Load plugin specific language pack
	tinymce.PluginManager.requireLangPack('parmmultiuser');

	tinymce.create('tinymce.plugins.parmmultiuserPlugin', {
		/**
		 * Initializes the plugin, this will be executed after the plugin has been created.
		 * This call is done before the editor instance has finished it's initialization so use the onInit event
		 * of the editor instance to intercept that event.
		 *
		 * @param {tinymce.Editor} ed Editor instance that the plugin is initialized in.
		 * @param {string} url Absolute URL to where the plugin is located.
		 */
		init : function(ed, url) 
		{
			// Register the command so that it can be invoked by using tinyMCE.activeEditor.execCommand('mceExample');
			ed.addCommand('mceparmmultiuser', function() 
			{								
				var node = ed.selection.getNode();					
				var textnode = null;
				var imgsrc = url + '/img/control.gif';
				if (node.nodeName === 'INPUT' && node.type=='image' && node.src==imgsrc)				
					textnode = node;
				
				var dialog = new Ext.parm.workflow.manager.formflow.MultiUserPropertiesWin(ed,url,textnode);
				dialog.show();
			});

			// Register example button
			ed.addButton('parmmultiuser', {
				title : 'parmmultiuser.desc',
				cmd : 'mceparmmultiuser',
				image : url + '/img/control.gif'
			});		
			
			ed.onNodeChange.add(function(ed, cm, n) {	
				var imgsrc = url + '/img/control.gif';						
				cm.setActive('parmmultiuser', n.nodeName == 'INPUT'&& n.type=='image' && n.src==imgsrc);	
//				if (n.nodeName === 'INPUT' && n.type=='image' && n.src==imgsrc)
//					ed.selection.select(n);
			});
			
			ed.onClick.add(function(ed, e) {
				e = e.target;		
				var imgsrc = url + '/img/control.gif';				
				if (e.nodeName === 'INPUT' && e.type=='image' && e.src==imgsrc)
					ed.selection.select(e);
			});
		},

		/**
		 * Creates control instances based in the incomming name. This method is normally not
		 * needed since the addButton method of the tinymce.Editor class is a more easy way of adding buttons
		 * but you sometimes need to create more complex controls like listboxes, split buttons etc then this
		 * method can be used to create those.
		 *
		 * @param {String} n Name of the control to create.
		 * @param {tinymce.ControlManager} cm Control manager to use inorder to create new control.
		 * @return {tinymce.ui.Control} New control instance or null if no control was created.
		 */
		createControl : function(n, cm) {
			return null;
		},

		/**
		 * Returns information about the plugin as a name/value array.
		 * The current keys are longname, author, authorurl, infourl and version.
		 *
		 * @return {Object} Name/value array containing information about the plugin.
		 */
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

	// Register plugin
	tinymce.PluginManager.add('parmmultiuser', tinymce.plugins.parmmultiuserPlugin);
})();
