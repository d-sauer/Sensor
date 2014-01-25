$("#login_form_ajax_img").hide();
$('#login_form_msg_wrapper').hide();
    	
$("#regButton").click(function() {
    $("#login_form_ajax_img").show();
	$('#login_form_msg_wrapper').hide();

	HLib.sendData('@routes.Application.login()', '#login_form :input').done(function(json) {
        HLib.processData(json);
        
        if (json.data && json.data.validation_msg) {
        	$('#login_form_ajax_img').val(json.data.validation_msg);
        	$('#login_form_msg_wrapper').show();
        }
        
        $("#login_form_ajax_img").hide();
    });
});