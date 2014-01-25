var HLIB = (function (lib) {
	var element = lib.element = lib.element || {};
	var input = element.input = element.input || {};
	
	
	input.set = function(el, jsData) {
	    if ((el.attr('type')=='checkbox' || el.attr('type')=='radio') && jsData.checked != undefined) {
	        if (jsData.checked == true)
	            $(el).prop('checked', true);
	        else
	            $(el).prop('checked', false);
	    } 
	    
	    el.val(jsData.value);
	}

	input.get = function(el, data) {
		var _value = lib.util.getNoNull(el.attr('value'));
		
		data.value = _value;
		data.type = lib.util.getNoNull(el.prop('type'));
		
		if (data.type == 'checkbox' || data.type == 'radio') {
		    data.checked = el.prop('checked');
		}
		
		return data;
	}
	
	
	return lib;
}(HLIB || {}));