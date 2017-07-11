$Direction = {
	  map : null
	, init : function(map, domPanelId){
		  
		this.map = map;
		this.domPanelId = domPanelId;
		//this.positions = new Array();
		//create directionsService object here
	    this.directionsService = new google.maps.DirectionsService();
	
	
	}

	
	, route : function(option, callback){
		
		if(option.origin == null){
			return;
		}
		
		if(option.destination == null){
			return;
		}
		
		if(this.directionsDisplay != null){
			this.directionsDisplay.setMap(null);
			this.directionsDisplay = null;
		}
		  
		//setup directionsDisplay object here
		var rendererOption = {
			draggable : true
		};
		  
	    this.directionsDisplay = new google.maps.DirectionsRenderer(rendererOption);
		this.directionsDisplay.setMap(this.map);
		
		if(this.domPanelId != null){
			this.directionsDisplay.setPanel(document.getElementById(this.domPanelId));
		}
		
		
		var requestOption = {
			origin : option.origin,
         	destination : option.destination,
         	waypoints : option.waypoints,
         	provideRouteAlternatives: false,
         	travelMode : option.travelMode,
         	optimizeWaypoints : true,
  	        drivingOptions: {
  	        	departureTime: option.departureTime,
  	        	trafficModel: 'pessimistic'
  	        }
     	}
		
		console.log("==> Direction : this.positions : ", this.positions);
		console.log("==> Direction : requestOption : ", requestOption);
		
     	var self = this;
     	this.directionsService.route(requestOption, function(response, status) {
        	if(status == google.maps.DirectionsStatus.OK) {
        		console.log("==> self.directionsDisplay : ", self.directionsDisplay);
        		self.directionsDisplay.setMap(self.map);
        		self.directionsDisplay.setDirections(response);
        		
        		var panel = document.getElementById(self.domPanelId);
        		panel.style.display = "block";
        		
        		callback();
        	}
     	});
		
		
	}
}