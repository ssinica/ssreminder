
var remainder = remainder || {};

remainder.init = function() {
	var currentTime = new Date();
	//alert(currentTime);
	$('#date3').DatePicker({
        flat : true,
        //date : [ '2008-07-28', '2008-07-31' ],
        //date : '2012-10-13',
        //current : '2012-10-13',
        date : currentTime,
        current : currentTime,
        calendars : 3,
        mode : 'single',                
    	starts : 1
    });	
}

remainder.getDate = function() {
	var date = $('#date3').DatePickerGetDate(false);
	return date;
}
	