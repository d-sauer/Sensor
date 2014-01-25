var sreg = {
		submit : function(url, scopeId) {
			$("#ajax_loading").show();
			HLib.sendData(url, scopeId).done(function(json) {
				HLib.processData(json);
				$("#ajax_loading").hide();
				
				var isValid = json.data.isValid;
			});
		}
}