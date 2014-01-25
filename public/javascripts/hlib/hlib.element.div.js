var HLIB = (function (lib) {
	var element = lib.element = lib.element || {};
	var div = element.div = element.div || {};
	
	
	div.set = function(el, jsData) {
		var _html = null;
		if (jsData.value !=undefined && jsData.value!=null)
			_html = jsData.value;
		else if (jsData.innerHTML !=undefined && jsData.innerHTML!=null)
			_html = jsData.innerHTML;
		
		if (_html!=null)
			el.html(_html);
	}
	
	div.get = function(el, data) {
		
	}

	
	return lib;
}(HLIB || {}));