var HLIB = (function (lib) {
	var element = lib.element = lib.element || {};
	var label = element.label = element.label || {};
	
	
	label.set = function(el, jsData) {
        if (jsData.forTag != undefined && jsData.forTag != null)
            el.attr('for', jsData.forAttribute);
	}

	label.get = function(el, data) {
		data.innerHTML = el.html();
		data.forAttribute = el.attr('for');
		
		return data;
	}
	
	
	return lib;
}(HLIB || {}));