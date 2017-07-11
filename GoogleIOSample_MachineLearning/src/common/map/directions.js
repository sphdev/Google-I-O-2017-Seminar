$Direction = {
	  map : null
	, init : function(map, domPanelId){
		  
		this.map = map;
		this.domPanelId = domPanelId;
		this.directionsDisplayList = new Array();
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
		
		if(this.directionsDisplayList.length != 0){
			for(var index = 0 ; index < this.directionsDisplayList.length ; index++){
				var directionsDisplayTemp = this.directionsDisplayList[index];
				directionsDisplayTemp.setMap(null);
			}
			
			this.directionsDisplayList = new Array();
		}
		
		
		var requestOption = {
			origin : option.origin,
         	destination : option.destination,
         	waypoints : option.waypoints,
         	provideRouteAlternatives: true,
         	travelMode : option.travelMode,
         	optimizeWaypoints : true,
         	transitOptions: {
         	    departureTime: option.departureTime,
         	    modes: ['BUS', 'RAIL', 'SUBWAY', 'TRAIN', 'TRAM'],
         	    routingPreference: 'FEWER_TRANSFERS'
         	},
  	        drivingOptions: {
  	        	departureTime: option.departureTime
  	        }
     	}
		
		console.log("==> Direction : this.positions : ", this.positions);
		console.log("==> Direction : requestOption : ", requestOption);
		
     	var self = this;
     	this.directionsService.route(requestOption, function(response, status) {
        	if(status == google.maps.DirectionsStatus.OK) {
        		console.log("==> response : ", response.routes);
        		
        		
        		if(response.routes != null){
        			
        			var lineColor = ["#6799FF", "#A566FF", "#F361A6", "#47C83E", "#F2CB61"];
					
					var maxLength = (response.routes.length > lineColor.length ? lineColor.length : response.routes.length);
					
					
        			
        			for(var index = (maxLength - 1)  ; index >= 0 ; index--){
        				
        				var infowindow = new google.maps.InfoWindow();
        				var rendererOption = {
    	        			draggable : true,
    	        			directions : response,
    	        			map : self.map,
    	        			routeIndex : index
    	        		};
        				
        				rendererOption.polylineOptions = {
        					  strokeColor: lineColor[index]
        					, strokeOpacity : 0.8
        					, strokeWeight : 5
        				};
    	        		  
    				
//        				console.log("==> index : ", index);
//        				
//        				if(index != 0){
//	        				rendererOption.polylineOptions = {
//	        					  strokeColor: "#747474"
//	        					, strokeOpacity : 0.8
//	        					, strokeWeight : 5
//	        				}
//        				}
//        				else{
//        					rendererOption.polylineOptions = {
//  	        					  strokeColor: "#6799FF"
//  	        					, strokeOpacity : 0.8
//  	        					, strokeWeight : 5
//  	        				}
//        				}
//        				
//        				console.log("==> directionsRendererTemp : ", rendererOption);
        				
        				
        				var directionsRendererTemp = new google.maps.DirectionsRenderer(rendererOption);
        				
        				//directionsRendererTemp.setMap(self.map);
        				//directionsRendererTemp.setDirections(response);
        				
        				
        				console.log("==> directionsRendererTemp : ", directionsRendererTemp);
        				
        				self.directionsDisplayList.push(directionsRendererTemp);
        				
        			}
        			
        		}
        		
        		

				if(self.domPanelId != null){
					$("#"+self.domPanelId).empty();
					
					if(option.displayPanel != false){
						directionsRendererTemp.setPanel(document.getElementById(self.domPanelId));
					}
        		}
				
				
        		var panel = document.getElementById(self.domPanelId);
        		panel.style.display = "block";
        		
        		callback(response.routes, option);
        	}
        	else{
        		swal('Warning', 'Traffic information is only available for future and current times.', 'error');
				
        		callback(null);
        	}
     	});
		
		
	}
}