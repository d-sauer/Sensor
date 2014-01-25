var HLIB = (function (lib) {
	
	
	/**
	 * First argument URL or params
	 * Params:
	 * 		- url
	 * 		- selector
	 *      - loader - show defined element ID on beginnign, and hide after ajax request is finish 
	 *      - make_loader - create ajax loader inseide given element
	 * 
	 * Second argument, selector, or use selector as argumen in first argumen, in params
	 */
	lib.server = function(_params, _selector) {
		//
		// Parametri
		//
		var bHasParams = false;
		var p_url = null;
		var p_module = null;
		var p_selector = null;
		var p_loader = null;
		var p_make_loader = null;
		var p_content = null; 
		
		
		if (_params != undefined && _params != null) {
		    // URL
			if (_params.url != undefined && _params.url!=null) {
				p_url = _params.url;
				bHasParams = true;
			}
			
			if (_params.loader != undefined && _params.loader != null) 
			    p_loader = $(_params.loader);

			if (_params.make_loader != undefined && _params.make_loader != null) 
			    p_make_loader = _params.make_loader;
			
			if (_params.module != undefined && _params.module != null) 
			    p_module = _params.module;
			
			if (_params.content != undefined && _params.content != null) 
                p_content = _params.content;
			
		}
		
		// SELECTOR 
		if (_selector != undefined && _selector != null) {
			p_selector = _selector;
		} 
		else if (_params != undefined && _params!= null && _params.selector != undefined && _params.selector != null) {
			p_selector = _params.selector;
			bHasParams = true;
		}
		
		
		//
		// URL
		//
		tp_url = HLIB.util.cleanURL(p_url);	
		if (tp_url != null)
		    p_url = tp_url;
		
		// set selector to default, element type: input, textarea
		if (p_selector == undefined || p_selector == null) {
		    p_selector = ":input, :textarea";
		}
		
		var methods = {
				url : p_url,
				
				selector : p_selector,
				
				loader : {
				    element : p_loader,
				    make    : p_make_loader
				},
				
				content : p_content,
				
				before : function(func) {
                    if (func!=undefined && func != null)
                        func();
                    
                    return this;
                },
				
				call : function() {
				    // Ajax loader
				    if (this.loader.element != undefined && this.loader.element != null) {
				        this.loader.element.each(function() {
				            $(this).show();
				        });
				    } 
				    else if (this.loader.make != undefined && this.loader.make != null) {
				        lib.util.addAjaxLoader(this.loader.make, true);
				    }
				        
				        
				    // 
				    // make call
				    //    
				    var javaFunction = arguments[0];
				    
				    var params = [];
				    var c = 0;
				    for(var i = 1; i < arguments.length; i++) {
				        var arg = arguments[i];
				        if (hlib.util.isArray(arg)) {
				            for(var ia = 0; ia < arg.length; ia++) {
				                params[c++] = arg[ia];
				            }
				        } else {
				            params[c++] = arg;
				        }
				    }
				    
				    // URL
				    var callURL = this.url;
				    if (callURL.charAt(p_url.length - 1)!='/')
				        callURL += '/';
				    
				    callURL += "call_" + javaFunction;
				    
				    var caller = {
				            java_module     : p_module,
				            java_function   : javaFunction,
				            params          : params,
				            content         : this.content
				    }
					
				    // get selector data
					var json = lib.element.getData(this.selector);
					
				    var _json = lib.util.stringify(json);
				    var _caller = lib.util.stringify(caller);
				    
					// make ajax request
				    var ajax = $.ajax({
		                type: "POST",
		                url : callURL,
		                cache: false,
		                dataType : 'json',
		                contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		                data: { "json" : _json , "call" : _caller}
		            });
					
				    // hide ajax loader - if request OK
				    var lo = this.loader;
				    ajax.done(function() {
				        if (lo.element != undefined && lo.element != null) {
	                        lo.element.each(function() {
	                            $(this).hide();
	                        });
	                    }
				        else if (lo.make != undefined && lo.make != null) {
				            $(lo.make).each(function(){
				                $('#__ajax_loader__', this).hide();
				            });
				        }
				        
				    });

				    // hide ajax loader - if request Error
				    ajax.fail(function() {
				        if (lo.element != undefined && lo.element != null) {
				            lo.element.each(function() {
				                $(this).hide();
				            });
				        }
				        else if (lo.make != undefined && lo.make != null) {
				            $(lo.make).each(function(){
				                $('#__ajax_loader__', this).hide();
				            });
				        }
				    });
				    
					// return ajax
					return ajax;
				}
		}
		
		return methods;
	}
	
	
	lib.server.call = function() {
	    var methods = lib.server();
		return methods.call.apply(methods, arguments);
	}

	lib.server.before = function() {
	    var methods = lib.server();
	    return methods.before.apply(methods, arguments);
	}
	
	
	lib.sendData = function(url, params) {
		var json = lib.element.getData(params);
		return sendAjaxData(url, json);
	}
	
	sendAjaxData = function(_url, _json) {
		var ajax = $.ajax({
			 	type: "POST",
				url : _url,
				cache: false,
				dataType : 'json',
				contentType: 'application/json; charset=utf-8',
				data: lib.util.stringify(_json)
			});
		return ajax; 
	}

	lib.processData = function(json) {
		return lib.element.processElementData(json);
	}

	return lib;
}(HLIB || {}));




var H = HLIB;
var HLib = HLIB;
var hlib = HLIB;

