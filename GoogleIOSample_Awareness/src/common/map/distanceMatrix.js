/**
 * http://usejsdoc.org/
 */

$DistanceMatrix = {
	
	getData : function(option, callback){
		var service = new google.maps.DistanceMatrixService();
		service.getDistanceMatrix(
		{
		    origins: option.origins,
		    destinations: option.destinations,
		    travelMode: 'DRIVING',
		    //transitOptions: TransitOptions,
		    //drivingOptions: DrivingOptions,
		    unitSystem: google.maps.UnitSystem.METRIC,
		    avoidHighways: false,
		    avoidTolls: false,
		}, callback);

	
	}
}
