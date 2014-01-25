var HLIB = (function (lib) {
	var element = lib.element = lib.element || {};
	
	//
	// GET DATA
	//
	
	/**
	 * PRIVATE METHOD
	 * Params:
	 * 		get: <jquery selector>
	 * 		getRoot: [true|false] :: true - include root element data  
	 */
	element.getData = function(params) {
		//
		// Params
		//
		var bHasProperty = false;
		var selector = null;
		if (params.get!=undefined && params.get!=null) {
			selector = params.get;
			bHasProperty = true;
		}

		var root = null;
		if (params.root!=undefined && params.root!=null) {
			root = params.root;
			bHasProperty = true;
		}
		
		if (bHasProperty == false) {
			selector = params;
		}
		
		//
		// logic...
		//
		var json = new Object();
		var jhtml = new Array();

		// get root elements
		var data = new Object();
		
		if (root!=null)
			getElement(root, data);
		
		// get child elements
		getChildNodes(selector, data);
		
		if (root!=null)
			jhtml.push(data);
		else
			jhtml = data.childNodes;

			
		json.html = jhtml;
		return json;
	}
	
	getElement = function(el, data) {
		return element.util.getElementData(el, data);
	},

	getChildNodes = function(selector, data) {
		var cn = data.childNodes = [];
		
		$(selector).each(function(index) {
			var el = $(this);
			var elData = new Object();
			getElement(el, elData);
			
			// get this element child nodes
			//getChildNodes(this, elData);
			
			if (elData.childNodes!=undefined && elData.childNodes!=null && elData.childNodes.length == 0) {
				delete elData.childNodes;
			}
			
			cn.push(elData);
		});
	},
	
	
	//
	// SET DATA
	//
	
	element.processElementData = function(json) {
		// Iterate over json array
		if (json!=undefined && json!=null && json.html!=undefined && json.html!=null) {
			for(var i = 0; i < json.html.length; i++) {
				var jsNode = json.html[i];
				element.util.setElement(jsNode);
			}
		}
		
		return json;
	}
	

	
	return lib;
}(HLIB || {}));