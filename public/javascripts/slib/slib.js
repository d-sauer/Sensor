var SLIB = (function (lib) {
	var util = lib.util = lib.util || {};
	
	// handle document event
	var doc = lib.doc = lib.doc || {};
	doc.initEvent = { click : document.onclick };
	doc.oldEvent = {};
	doc.newEvent = {};
	
	doc.reset = function() {
	    document.onclick = doc.oldEvent.click;
        doc.newEvent.click = null;
        doc.oldEvent.click = null;
	}
	
	// bind function to var util
	lib.MODULE_CONTENT = "#sensor_content";
	
	lib.loadModule = function(_module, _content) {
	    var content = lib.MODULE_CONTENT;
	    if (_content != undefined && _content !=null)
	        content = _content;
	    
	    $(content).attr('module', _module);
	    
	    var ajax = lib.module(_module).call('loadModule');
	    return ajax;
	}
	
	/**
	 * Ako nije definirano poziva se trenutni modul, inace se poziva metoda navedena u call za definirani modul.
	 * Ako nije definirana metoda modula, tada se poziva loadModul metoda
	 */
	lib.module = function(_module) {
	       var p_module = null; 
	       var p_content = lib.MODULE_CONTENT;
	       var p_set_content_module_attr = false;
	       
           var isMultiparam = false;
           
           if (_module!=undefined && _module!=null && ((_module.module != undefined && _module.module != null) || (_module.content != undefined && _module.content != null)))
               isMultiparam = true;
           
           
           if (isMultiparam == false) {
               var content = $(p_content);
               if ((_module==undefined || _module==null) && content != undefined && content != null) {
                    var tmpMod = content.attr('module');
                    if (tmpMod != undefined && tmpMod != null)
                        p_module = tmpMod;
               } else {
                   p_module = _module;
               }
           } else {
               if (_module.content !=undefined && _module.content!=null) {
                   p_content = _module.content;
               }

               if (_module.module !=undefined && _module.module!=null) {
                   p_module = _module.module;
               } else {
                   var content = $(p_content);
                   if (content != undefined && content != null) {
                       var tmpMod = content.attr('module');
                       if (tmpMod != undefined && tmpMod != null)
                           p_module = tmpMod;
                   }
                   
                   if (p_module == undefined || p_module == null) {
                       p_module = hlib.util.findUpTreeAttribute(p_content, 'module', true);
                   }
                   
               }
               
               if (_module.module_attr != undefined && _module.module_attr != null && _module.module_attr == true) {
                   p_set_content_module_attr = true;
               }

               // add call to breadcrumb
               if (_module.breadcrumb != undefined && _module.breadcrumb != null) {
                   p_set_content_module_attr = true;
               }
               
           }
           
           if (p_set_content_module_attr) {
               $(p_content).attr('module', p_module);
           }
	       
           
	       var p_url = "/module/";
	    
	       var methods = {
	                url : p_url,
	                module: p_module,
	                content: p_content,
	                
	                before : function(func) {
	                    if (func!=undefined && func != null)
	                        func();
	                    
	                    return this;
	                },
	                
	                call : function() {
	                    var methods = hlib.server({ 'url' : this.url, 'module' : this.module, 'content' : this.content }, this.content + ' :input');
	                    var ac = methods.call.apply(methods, arguments);
	                    
	                    ac.done(function (json) {
	                        hlib.processData(json);
	                    });
	                    
	                    ac.error(function (html) {
	                        $(this.content).html(html.responseText);
	                    })
	                    
	                    return ac;
	                }
	       }
	       
	       return methods;
	}
	
	lib.module.call = function() {
        var methods = lib.module();
        return methods.call.apply(methods, arguments);
    }

	lib.module.before = function() {
	    var methods = lib.module();
	    return methods.before.apply(methods, arguments);
	}
	
	return lib;
}(SLIB || {}));


var S = SLIB;
var SLib = SLIB;
var slib = SLIB;
