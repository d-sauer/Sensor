var HLIB = (function (lib) {
	var element = lib.element = lib.element || {};
	var util = element.util = element.util || {};
	
	
	util.getElementData = function(el, data) {
		var _tagName = lib.util.getNoNull(el.prop('tagName')).toLowerCase();
		
		data.tag = _tagName;
		
		this.getHTag(el, data);
		
		// depent of element tag append element data
		if (_tagName!=undefined && _tagName!=null) {
			if (_tagName=='input')
				element.input.get(el, data);
			else if (_tagName=='div')
				element.div.get(el, data);
			else if (_tagName=='select')
			    element.select.get(el, data);
			else if (_tagName=='textarea')
			    element.textarea.get(el, data);
			else if (_tagName=='label')
			    element.label.get(el, data);
			else {
				var _value = el.val();
				if (_value == null || _value.length == 0)
					_value = el.innerHTML || el[0].innerHTML;
				data.value = lib.util.getNoNull(_value);
			}
		}
		
		return data;
	}
	
	util.setElement = function(jsData) {
        if (jsData.selector != null) {
            $(jsData.selector).each(function(index) {
                var el = $(this);
                util.setElementData(el, jsData);
            });
        }
        else if (jsData.id != null) {
            $("#" + jsData.id).each(function(index) {
                var el = $(this);
                util.setElementData(el, jsData);
            });
        }
    }
	
	util.setElementData = function(el, jsData) {
        if(el != null && jsData!=null) {
            var tagName = el.prop('tagName').toLowerCase();
            lib.element.util.setHTag(el, jsData);
            
            // default custom attribute - module
            if (jsData.module!=undefined && jsData.module!=null) {
                el.attr('module', jsData.module);
            }
            
            
            if (jsData.innerHTML!=undefined && jsData.innerHTML!=null)
            	el.html(jsData.innerHTML); 
            
            
            // HTML tags..
            if (tagName=='input') {
                lib.element.input.set(el, jsData);
            } 
            else if (tagName=='div') {
                lib.element.div.set(el, jsData);
            } 
            else if (tagName=='select') {
                lib.element.select.set(el, jsData);
            } 
            else if (tagName=='textarea') {
                lib.element.textarea.set(el, jsData);
            } 
            else if (tagName=='label') {
                lib.element.label.set(el, jsData);
            } 
            else {
            	//
            }
        }
    }
	
	util.getHTag = function(el, data) {
		var _id =  el.attr('id');
		var _name = el.attr('name');
		var _class = el.attr('class');
		var _style = el.attr('style');
		var _title = el.attr('title');
		
		if (_id!=undefined && _id!=null)
			data.id = _id;
		if (_name!=undefined && _name!=null)
			data.name = _name;
		if (_class!=undefined && _class!=null)
			data.clazz = _class;
		if (_style!=undefined && _style!=null)
			data.style = _style;
		if (_title!=undefined && _title!=null)
			data.title = _title;

		return data;
	}
	
	util.setHTag = function(el, data) {
		if (data.name!=undefined && data.name!=null) {
			el.attr('name', data.name);
		}

		if (data.clazz!=undefined && data.clazz!=null) {
			el.attr('class', data.clazz);
		}
		
		if (data.style!=undefined && data.style!=null) {
			el.attr('style', data.style);
		}

		if (data.title!=undefined && data.title!=null) {
			el.attr('title', data.title);
		}
	}
	
	
	return lib;
}(HLIB || {}));