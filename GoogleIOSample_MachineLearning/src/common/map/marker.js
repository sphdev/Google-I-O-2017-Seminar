$Marker = {
	  startPosition : null
	, endPosition   : null  
	, wayPoint : null
	, map : null
	
	, init : function(map){
		this.wayPoint = new Array();
		this.map = map;
	}

	, add : function(marker){
		this.wayPoint.push(marker);
		marker.setMap(this.map);
	}
	
	, remove : function(marker){
		
		var delindex = -1;
		for(var index = 0 ; index < this.wayPoint.length ; index++){
			
			var markerTemp = this.wayPoint[index];
			if(marker == markerTemp){
				delindex = index;
				break;
			}
		}
		if(delindex != -1){
			this.wayPoint.splice(delindex, 1);
			marker.setMap(null);
		}
	}
	
	, removeAll : function(marker){
		
		for(var index = 0 ; index < this.wayPoint.length ; index++){
		
			var markerTemp = this.wayPoint[index];
			markerTemp.setMap(null);
		
		}
		
		this.wayPoint.clear();
	}
	
	, draw : function(options){
		
		if(options.isWayPoint == true){
			this.drawWaypoint(options);
		}
		else if(options.isStartPosition == true){
			this.drawStartPosition(options);
		}
		else if(options.isEndPosition == true){
			this.drawEndPosition(options);
		}
		else{
			this.drawWaypoint(options);
		}
		
	}
	
	, drawStartPosition : function(options){
		
		if(this.startPosition != null){
			this.startPosition.setMap(null);
		} 
		
		var position = new google.maps.LatLng(options.lat, options.lng);
		
		var marker = new google.maps.Marker({
		    position : position,
		    title    : "StartPosition",
		    icon     : './img/position-marker-start.png',
		    data	 : options.data
		});
		

		if(options.map == null){
			marker.setMap(this.map);
			//this.map.setCenter(position);
			//this.map.setZoom(14);
		}
		else{
			marker.setMap(options.map);
			//options.map.setCenter(position);
			//options.map.setZoom(14);
		}
		
		var infowindowContents = "<div style=\"padding-top:5px;bolder;padding-bottom:5px;\"><span style=\"color:#4C4C4C;font-size:14px;font-weight:bolder;\">" + options.data.address + "</span></div>"
		
		var infowindow = new google.maps.InfoWindow({
		    content: infowindowContents
		});
		
		marker.infowindow = infowindow;
		marker.options = options;
		
		var self = this;
		marker.addListener('click', function() {
			if(self.preInfoWindow != null){
				self.preInfoWindow.close();
			}
			
			self.preInfoWindow = this.infowindow;
			self.preInfoWindow.open(this.map, this);
		});
				
		this.startPosition = marker;
		   
	}
	
	, drawEndPosition : function(options){
		

		if(this.endPosition != null){
			this.endPosition.setMap(null);
		} 
		
		var position = new google.maps.LatLng(options.lat, options.lng);
		
		var marker = new google.maps.Marker({
		    position : position,
		    title    : "StartPosition",
		    icon     : './img/position-marker-end.png'
		});
		

		if(options.map == null){
			marker.setMap(this.map);
			//this.map.setCenter(position);
			//this.map.setZoom(14);
		}
		else{
			marker.setMap(options.map);
			//options.map.setCenter(position);
			//options.map.setZoom(14);
		}
		
		var infowindowContents = "<div style=\"padding-top:5px;bolder;padding-bottom:5px;\"><span style=\"color:#4C4C4C;font-size:14px;font-weight:bolder;\">" + options.title + "</span></div>"
		
		var infowindow = new google.maps.InfoWindow({
		    content: infowindowContents
		});
		
		marker.infowindow = infowindow;
		marker.options = options;
		
		var self = this;
		marker.addListener('click', function() {
			if(self.preInfoWindow != null){
				self.preInfoWindow.close();
			}
			
			self.preInfoWindow = this.infowindow;
			self.preInfoWindow.open(this.map, this);
		});
				
		this.endPosition = marker;
		
	}
	
	, drawWaypoint : function(options){
		
		var position = new google.maps.LatLng(options.lat, options.lng);
		
		var marker = new google.maps.Marker({
		    position : position,
		    title    : options.title,
		
		});
		
		if(options.map == null){
			marker.setMap(this.map);
			this.map.setCenter(position);
			this.map.setZoom(14);
		}
		else{
			marker.setMap(options.map);
			options.map.setCenter(position);
			options.map.setZoom(14);
		}
		
		var hashTag = "";
		var labelAnnotations = options.detection.labelAnnotations;
		for(var index = 0 ; index < labelAnnotations.length ; index++){
			var labelAnnotation = labelAnnotations[index];
			hashTag = hashTag + "#"+labelAnnotation.description + " ";
		}		
		
		var infowindowContents = "<div style=\"width:" + parseInt(options.data.width/3 + 50) + "px;\">"
							   + "  <img src=\"" + options.data.dataURL + "\" style=\"width:" + parseInt(options.data.width/3 + 20) + "px;height:" + parseInt(options.data.height/3) + "px\"></img>"	
							   + "  <div style=\"padding-top:5px;bolder;padding-bottom:5px;\"><span style=\"color:#4C4C4C;font-size:16px;font-weight:bolder;\">" + options.title + "</span></div>"
							   + "  <div><span style=\"color:#4374D9;\">"+hashTag+"</span></div>"
							   + "</div>"
		
		var infowindow = new google.maps.InfoWindow({
		    content: infowindowContents
		});
		
		marker.infowindow = infowindow;
		marker.options = options;
		
		var self = this;
		marker.addListener('click', function() {
			if(self.preInfoWindow != null){
				self.preInfoWindow.close();
			}
			
			self.preInfoWindow = this.infowindow;
			self.preInfoWindow.open(this.map, this);
		});
				
		
		
		this.wayPoint.push(marker);
			
	}
}