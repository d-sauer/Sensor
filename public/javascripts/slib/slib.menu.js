var SLIB = (function (lib) {
    var menu = lib.menu = lib.menu || {};
    
	menu.load = function(menuContainerId) {
	    var mc = $('#' + menuContainerId);
	    
	    // root menu item
	    $('ul.root_menu > li', mc).each(function() {
	        var li = this;
	        $('a', li).not('ul.sub_menu > li > a').bind('click', function(e) {
	            var module = $(this).attr('module');
	            if (module!=undefined && module!=null && module!='') {
	                var m = slib.menu.parseModule(module);
	                if (m._params != null)
	                    lib.module({ 'module': m._package , 'content': lib.MODULE_CONTENT, 'module_attr': true }).call(m._method, m._params);
	                else
	                    lib.module({ 'module': m._package , 'content': lib.MODULE_CONTENT, 'module_attr': true }).call(m._method);
	            }

	            slib.menu.selectRoot(li, this);
	            
	            return false;
	        });
	    });

	    // submenu item
	    $('ul.sub_menu > li > a', mc).each(function() {
	        $(this).bind('click', function(e) {
	            var module = $(this).attr('module');
	            var m = slib.menu.parseModule(module);
	            if (m._params != null)
	                lib.module({ 'module': m._package , 'content': lib.MODULE_CONTENT, 'module_attr': true }).call(m._method, m._params);
	            else
	                lib.module({ 'module': m._package , 'content': lib.MODULE_CONTENT, 'module_attr': true }).call(m._method);
	            
	            return false;
	        });
	    });
	}

	menu.parseModule = function(_module) {
	    var data = {
	                    _package : null,
	                    _method : null,
	                    _params : null
	                };
	    
	    var lastDot=_module.lastIndexOf("\.");
	    var fb =_module.indexOf("(");
	    var lb =_module.lastIndexOf(")");
	    
	    var pkg = _module.substring(0, lastDot);
	    var meth = _module.substring(lastDot + 1, fb);

	    if ((fb+1) < lb) {
    	    var par = _module.substring(fb + 1, lb);
	        data._params = par.split(',');
	    }
	    
	    data._package = pkg;
	    data._method = meth;
	    
	    return data;
	}
	
	menu.selected = null;
	
	menu.selectRoot = function(holder, menuItem) {
	    if (menu.selected!=null) {
	        $(menu.selected).removeClass('selected');
	        menu.closeSubMenu(menu.selected);
	    }
	    
	    $(holder).addClass('selected');
	    menu.selected = holder;
	    
	    menu.openSubMenu(holder);
	}
	
	menu.openSubMenu = function(holder) {
	    var submenu = $('ul.sub_menu', holder);
	    submenu.show();
	}

	menu.closeSubMenu = function(holder) {
	    var submenu = $('ul.sub_menu', holder);
	    submenu.hide();
        
	}
	
	
	return lib;
}(SLIB || {}));


