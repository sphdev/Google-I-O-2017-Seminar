$Place = {
	  map : null
	, placeList : null
	, init : function(map){
		  
		this.map = map;
		this.placeList = new Array();
		
	}
	
	, radarSearch : function(options, callback){
		
		var location = new google.maps.LatLng(options.lat, options.lng);
		
		var request = {
			  location : location
			, radius   : options.radius
		//	, type     : options.type
			, openNow  : true
			, rankBy   : google.maps.places.RankBy.PROMINENCE
		};
		
		if(options.keyword != null){
			request.keyword = options.keyword;
		}
		
		if(options.type != null){
			request.type = options.type;
		}
		
		if(options.icon != null){
			request.icon = options.icon;
		}
		
		var service = new google.maps.places.PlacesService(map);
		
		var self = this;
		service.radarSearch(request, function(results, status){
			
			if (status !== google.maps.places.PlacesServiceStatus.OK) {
			    console.log(status);
			    return;
			}

			var maxSize = 10;
			var size = (results.length > maxSize ? maxSize : results.length);
			for (var index = 0 ; index < size ; index++) {	
				
				var result = results[index];
				self.drawPlaceMarker(result, options);
			

			}
		});
	} 
	
	, drawPlaceMarker : function(data, options){
		
		var self = this;
		service = new google.maps.places.PlacesService(this.map);
		service.getDetails({
			placeId: data.place_id
		}, function(place, status){
			
			if (status !== google.maps.places.PlacesServiceStatus.OK) {
			    console.log("getDetails : " + status);
			    return;
			}
			
			
			console.log("==> drawPlaceMarker : place : ", place);
			
			
			var marker = new google.maps.Marker({
			    map: this.map,
			    position: place.geometry.location,
			    icon: {
			      url: place.icon,
			      anchor: new google.maps.Point(24, 24),
			      scaledSize: new google.maps.Size(24, 29)
			    }
			});
			
			
			var opentime = "정보없음";
			if(place.opening_hours.periods[0].open != null){
				opentime = place.opening_hours.periods[0].open.time.substring(0,2) + " : "  + place.opening_hours.periods[0].open.time.substring(2,4);
			}

			
			var closetime = "";
			if(place.opening_hours.periods[0].close != null){
				closetime = place.opening_hours.periods[0].close.time.substring(0,2) + " : "  + place.opening_hours.periods[0].close.time.substring(2,4);
			}
			
			
			var type = "";
			if(place.types != null){
				
				if(place.types.length > 1){
					type = place.types[0] + ", " + place.types[1]
				}
				else{
					type = place.types[0]
				}
			}
			
			
			var infowindowContents = "<div style=\"width:250px;\">"
								   + "  <div style=\"padding-top:5px;bolder;padding-bottom:5px;\"><span style=\"color:#4C4C4C;font-size:16px;font-weight:bolder;\">" + place.name + "</span></div>"
								   + "  <div style=\"padding-bottom:5px;\">"+ place.adr_address +"</div>"
								   + "  <div style=\"padding-bottom:5px;\">"+ place.international_phone_number +"</div>"
								   + "  <div style=\"padding-bottom:5px;\">"+ opentime + " ~ " + closetime +"</div>"
								   + "  <div style=\"padding-bottom:5px;color:#A566FF;\">"+ type +"</div>"
								   + "  <div><span style=\"color:#4374D9;font-weight:bolder;\">Ranking : "+place.rating+"</span></div>"
								   + "</div>"
			
			var infowindow = new google.maps.InfoWindow({
			    content: infowindowContents
			});
			
			marker.infowindow = infowindow;
			marker.options = options;
			
			marker.addListener('click', function() {
				if(self.preInfoWindow != null){
					self.preInfoWindow.close();
				}
				
				self.preInfoWindow = this.infowindow;
				self.preInfoWindow.open(this.map, this);
			});
					
			self.placeList.push(marker);
			

		});
		
		
	}
	, clear : function(){
		for(var index = 0 ; index < this.placeList.length ; index++){
			var markerTemp = this.placeList[index];
			markerTemp.setMap(null);
		}
	}
	

}
