var HLIB = (function (lib) {
	var element = lib.element = lib.element || {};
	var select = element.select = element.select || {};
	
	
	select.set = function(el, jsData) {
	    if (jsData.value!=undefined && jsData.value!=null) {
	        $(el).find("option[value='" + jsData.value + "']").attr("selected","selected");
	    } else if (jsData.index!=undefined && jsData.index!=null) {
	        $(el).find("option[index='" + jsData.index + "']").attr("selected","selected");
	    }
	}

	select.get = function(el, data) {
		// select value
	    var _value = lib.util.getNoNull(el.val());
		data.value = _value;

		var e = el[0];
		if (e!=undefined && e!=null && e.selectedIndex != -1) {
		    var index = e.selectedIndex;
		    data.index = index;
		    
		    data.text = e.options[index].text;
		}

		return data;
	}
	
	
	return lib;
}(HLIB || {}));