var SLIB = (function (lib) {
	var util = lib.util = lib.util || {};
	
	util.stopPropagation = function(event) {
        if (!event) var event = window.event || arguments.callee.caller.arguments[0];
        event.cancelBubble = true;
        if (event.stopPropagation) event.stopPropagation();
    }
	
	/**
	 * Get data about screen dimension, visible area on screen
	 */
	util.getScreenData = function () {
        var myWidth = 0, myHeight = 0;
        if( typeof( window.innerWidth ) == 'number' ) {
          //Non-IE
          myWidth = window.innerWidth;
          myHeight = window.innerHeight;
        } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
          //IE 6+ in 'standards compliant mode'
          myWidth = document.documentElement.clientWidth;
          myHeight = document.documentElement.clientHeight;
        } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
          //IE 4 compatible
          myWidth = document.body.clientWidth;
          myHeight = document.body.clientHeight;
        }
 
        var scrOfX = 0, scrOfY = 0;
        var scrWidth = 0, scrHeight = 0;
        if( typeof( window.pageYOffset ) == 'number' ) {
          //Netscape compliant
          scrOfY = window.pageYOffset;
          scrOfX = window.pageXOffset;
          scrWidth = document.documentElement.scrollWidth; 
          scrHeight = document.documentElement.scrollHeight;
        } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
          //DOM compliant
          scrOfY = document.body.scrollTop;
          scrOfX = document.body.scrollLeft;
          scrWidth = document.documentElement.scrollWidth; 
          scrHeight = document.documentElement.scrollHeight;
        } else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
          //IE6 standards compliant mode
          scrOfY = document.documentElement.scrollTop;
          scrOfX = document.documentElement.scrollLeft;
          scrWidth = document.documentElement.scrollWidth; 
          scrHeight = document.documentElement.scrollHeight;
        }
       
       
        // vidljivo podrucje
        var visX = scrOfX;
        var visY = scrOfY;
        var visWidth = scrWidth != 0 ? scrWidth : myWidth;
        var visHeight = scrHeight != 0 ? scrHeight : myHeight;
       
        var visible = {"x" : visX, "y" : visY, "width" : visWidth, "height" : visHeight};
        var scroll = {"x" : scrOfX, "y" : scrOfY, "width" : scrWidth, "height" : scrHeight};
        var page = {"width" : myWidth, "height" : myHeight};
 
        var screen = {"page" : page, "scroll" : scroll, "visible" : visible};
       
        return screen;     
      }
	
	 
   /**
	* Pozicija i dimenzije objekta
	*/
	util.getObjectCoordinates = function(object) {
	          // ODREDI POZICIJU OBJEKTA nad kojim se prikazuje tooltip
	           var offsetTop = object.offsetTop;
	           var offsetLeft = object.offsetLeft;
	           var offsetParent = object.offsetParent;
//	           var offsetParent = object.parentElement;
	           while(offsetParent!=null) {
	               offsetTop += offsetParent.offsetTop;
	               offsetLeft += offsetParent.offsetLeft;
	              
	               offsetParent = offsetParent.offsetParent;
//	               offsetParent = offsetParent.parentElement;
	           }
	          
	           return {"x" : offsetLeft, "y" : offsetTop, "width" : object.offsetWidth, "height" : object.offsetHeight};
	    }
	      
	util.getCoordinates = function(elX, elY, elWidth, elHeight, width, height, offsetX, offsetY) {
	          if(offsetX == undefined || offsetX == null)
	              offsetX = 0;
	          if(offsetY == undefined || offsetY == null)
	              offsetY = 0;
	          
	          var screen = this.getScreenData();
	          var x = elX;
	          var y = elY + elHeight;
	         
	          if ( (elX + width) > screen.visible.width) {
	              x = elX - width + offsetX;
	          } else {
	              x += offsetX
	          }
	         
	          if ((elY + elHeight + height) > screen.visible.height) {
	              y = elY - height - offsetY;
	          } else {
	              y += offsetY;
	          }
	         
	          return {"x" : x, "y" : y};
	    }
	
	return lib;
}(SLIB || {}));


