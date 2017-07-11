$Geocode = {
	getAddress : function(options, callback){
		var geocoder = new google.maps.Geocoder;
		
		var latlng = {
				  lat: options.lat
				, lng: options.lng
		}
		
		geocoder.geocode({'location': latlng}, function(results, status) {
			if (status === 'OK') {
				if (results[1]) {
					callback(results[1])
				} 
				else {
					window.alert('No results found');
				}
		    } 
			else {
				window.alert('Geocoder failed due to: ' + status);
		    }
		});
	}
	, getLatLng : function(options){
		
	}
	
}