var HLIB = (function (lib) {
	var util = lib.util = lib.util || {};
	
	/**
	 * remove query string from url if url is given, othervise use current url
	 * url: http://localhost/login/	       = http://localhost/login
	 * url: http://localhost/login/#       = http://localhost/login
	 * url: http://localhost/login?var=... = http://localhost/login
	 * 
	 * window.location.port
     * window.location.protocol
     * window.location.pathname
     * window.location.href
     * window.location.hash
	 */
	util.cleanURL = function(_url) {
		var url = null;
		if (_url==undefined || _url == null) {
		    url = window.location.protocol;
		    if (url.charAt(url.length - 1) != ":")
		        url += ':';
		    
		    url += "//" + window.location.host + window.location.pathname; 
		} 
		else {
		    var reg = new RegExp("^([^?|#])+");
		    var r = reg.exec(url);
		    
		    url = r[0];
		}
		
		if (url=="null")
		    url = null;
		
		return url;
	}
	
	util.getNoNull = function (value, _default) {
		if (value==undefined  || value==null) {
			if (_default!=undefined && _default!=null)
				return _default;
			else
				return "null";
		}
		else
			return value;
	}
	
	util.typeOf = function(obj) {
		if (typeof obj != 'object')
			return typeof obj;
		
		if (obj === null)
			return "null";

		//object, array, function, date, regexp, string, number, boolean, error
		var internalClass = Object.prototype.toString.call(obj).match(/\[object\s(\w+)\]/)[1];
		
		return internalClass.toLowerCase();
	},
	
	util.isArray = function(obj) {
		if (obj.isArray)
			return obj.isArray;
		var type = util.typeOf(obj); 
		if (type=='array')
			return true;
		else
			return false;
	}
	
	util.stringify  = function (obj) {
        var t = typeof (obj);
        if (t != "object" || obj === null) {
            // simple data type
            if (t == "string") obj = '"' + obj + '"';
            return String(obj);
        } else {
            // recurse array or object
            var n, v, json = [], arr = (obj && obj.constructor == Array);

            for (n in obj) {
                v = obj[n];
                t = typeof(v);
                if (obj.hasOwnProperty(n)) {
                    if (t == "string") {
                    	v = util.encode(v);
                    	v = '"' + v + '"'; 
                    }
                    else if (t == "object" && v !== null) {
                    	v = util.stringify(v);
                    } 
                    
                    // esvape sequence (http://json.org/)
                    // for: " 
                    
                    json.push((arr ? "" : '"' + n + '":') + v);
                }
            }
            return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
        }
    }
    
	util.encode = function(_string) {
		var buff = "";
		for(var i = 0; i<_string.length; i++) {
			var ch = _string.charAt(i);
			if (ch=='\"')
				ch = "\\\"";
			else if (ch=='\n')
			    ch = "\\n";
			
			buff += ch;
		}
		return buff;
	}
	
	util.__AJAX_LOADER = "__ajax_loader__";
	/**
	 * Create ajax loader with id='__ajax_loader__' element inside given elementId, if not exists
	 * bShow - defaullt false. Show or hide element after create
	 * imgUrl - for custom imageUrl of ajax loader
	 * 
	 * return reference to created element
	 */
	util.addAjaxLoader = function (elementId, bShow, imgUrl) {
	    var hide = "display: none;";
	    if (bShow!=undefined && bShow!=null && bShow==true) 
	        hide = "";
	        
	    // http://localhost:9000/assets/images/ajax_a_16.gif
	    var img =  window.location.protocol +"//"+  window.location.hostname + ":" +window.location.port + "/assets/images/ajax_a_16.gif";
	    if (imgUrl!=undefined && imgUrl!=null && imgUrl.length!=0)
	        img = imgUrl;
	    
	    var ajaxLoader = '<div id="' + util.__AJAX_LOADER + '" style="' + hide + '">' + '<img src="' + img + '" />' + '</div>';
	    
	    $(elementId).each(function() {
	        var el = $(this);
	        if (el.has('#' + util.__AJAX_LOADER).length !=0) {
	            if (bShow==true);
	            el.show();
	        } else {
	            $(this).append(ajaxLoader);
	        }
	    });;
	}
	
	util.hasContent = function(elementId) {
	    var el = document.getElementById(elementId);
	    var hasContent = false;
	    
	    if (el.childNodes!=undefined && el.childNodes!=null) {
	        if (el.childNodes.length > 0)
	            hasContent = true;
	    } else {
	        if (el.innerHTML != '')
	            hasContent = true;
	    }
	    
	    return hasContent;
	}
	
	util.findUpTreeAttribute = function (startElement, _attributeName, appendFindAttrToStartEl) {
	    var el = $(startElement);
	    if (el.length!=0) 
	        el = el[0];
	    
	    if (el!=undefined && el != null) {
	        
	        var attrVal = null;
	        var elParent = el.parentElement;
            while(elParent != null) {
                attrVal = elParent.getAttribute(_attributeName);
                if (attrVal != undefined && attrVal != null && attrVal !='') {
                    if (appendFindAttrToStartEl) {
                        el.setAttribute(_attributeName, attrVal);
                    }
                    break;
                }
                
                
                elParent = elParent.parentElement;
            }
            
            return attrVal;
	    }
	    
	    return null;
	}
	
	return lib;
}(HLIB || {}));