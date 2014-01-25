var SLIB = (function (lib) {
	
	
	lib.popbox = function(elOpen, elPopUp, options) {
		var methods = {
				open : function(event, options) {
						var opt = event.data;
						var openEl = null;
						var popUpEl = null;
						
						if (opt==undefined || opt == null) {
						    opt = {};
                            opt.elOpen = "#" + event;
                            opt.elPopUp = "#" + event + "_popbox";
                            
                            openEl = $(opt.elOpen);
                            popUpEl = $(opt.elPopUp);
                            
                            event = null;
						    
						    if (options!=undefined && options!=null) {
						        if (options.position!=undefined && options.position!=null)
						            opt.position = options.position;
						    }
						    
						} else {
						    openEl = $(opt.elOpen);
						    popUpEl = $(opt.elPopUp);
						}
						
						
						
						event = event || window.event;
						var el = event.target || event.srcElement;
						// get open element position
						var elCord = SLIB.util.getObjectCoordinates(el);
						
						// pozicija popupa
						// default pozicija
						var top = elCord.y + elCord.height + 8;
						var left = elCord.x;

						// pozicija ovisno o parametru
						if(opt!=null && opt.position!=undefined && opt.position!=null && opt.position=='left') {
						    var left = elCord.x + (elCord.width * 1.5)  - popUpEl.width() + 10;
						}

						popUpEl.css({ 'top': top, 'left' : left });
						
						// pozicija strelice
						// elCord.width / 2 - 10
						var arrowX = (elCord.x - left) + (elCord.width / 2) - 10;
						popUpEl.find('.arrow').css({'left': arrowX });
						popUpEl.find('.arrow-border').css({'left': arrowX });
					
						//
						// show popup
						//
						if (popUpEl.is(':hidden')) {
    						popUpEl.fadeIn('fast');
    	
    						var newEvent = function() { 
    						        SLIB.popbox().close(opt); 
    						    };
    						
    						if (SLIB.doc.newEvent.click!=null && SLIB.doc.newEvent.click!=newEvent) {
    							SLIB.doc.newEvent.click();
    						}
    						
    	
    						SLIB.doc.newEvent.click=newEvent;	
    						SLIB.doc.oldEvent.click = document.onclick;
    						window.document.onclick = SLIB.doc.newEvent.click;
    						
    						
    						popUpEl.bind('click', function(event) { SLIB.util.stopPropagation(); });
						}

						SLIB.util.stopPropagation();
					
				},
				
				close : function(opt) {
				    var popUpEl = null;
				    
					if (opt!=undefined && opt!=null && opt.elPopUp!=undefined) {
						popUpEl = $(opt.elPopUp);
					} else {
                        popUpEl = $("#" + opt + "_popbox");
					}
	
					popUpEl.fadeOut('fast');
					document.onclick = SLIB.doc.oldEvent.click;
					SLIB.doc.newEvent.click = null;
					SLIB.doc.oldEvent.click = null;
				}
		}

		// INIT
		if (elOpen!=undefined && elOpen!=null && elPopUp!=undefined && elPopUp!=null) {
			var openEl = $(elOpen);
			var popUpEl = $(elPopUp);
			var opt = options || {};
			
			opt.elOpen = elOpen;
			opt.elPopUp = elPopUp;
			popUpEl.css({ 'top': -5000, 'left' : -5000 });
			popUpEl.hide();
			
			openEl.bind('click', opt, function(event) { SLIB.popbox().open(event); } );
		} 
		
		return methods;
	}
	
	lib.popbox.open = function() {
        var methods = lib.popbox();
        return methods.open.apply(methods, arguments);
    }

	lib.popbox.close = function() {
	    if (document.onclick != undefined && document.onclick != null)
	        document.onclick();
	}
	
	return lib;
}(SLIB || {}));


