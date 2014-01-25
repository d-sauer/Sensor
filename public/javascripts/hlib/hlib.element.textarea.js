var HLIB = (function (lib) {
	var element = lib.element = lib.element || {};
	var textarea = element.textarea = element.textarea || {};
	
	
	textarea.set = function(el, jsData) {
        el.val(jsData.value);
	}

	textarea.get = function(el, data) {
		var _value = lib.util.getNoNull(el.val());
		
		data.value = _value;
		return data;
	}
	
	
	return lib;
}(HLIB || {}));