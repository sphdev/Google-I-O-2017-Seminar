$Marker = {
	  list : null
	, map : null
	
	, init : function(map){
		this.list = new Array();
		this.map = map;
	}

	, add : function(marker){
		this.list.push(marker);
		marker.setMap(this.map);
	}
	
	, remove : function(marker){
		
		var delindex = -1;
		for(var index = 0 ; index < this.list.length ; index++){
			
			var markerTemp = this.list[index];
			if(marker == markerTemp){
				delindex = index;
				break;
			}
		}
		if(delindex != -1){
			this.list.splice(delindex, 1);
			marker.setMap(null);
		}
	}
	
	, removeAll : function(marker){
		
		for(var index = 0 ; index < this.list.length ; index++){
		
			var markerTemp = this.list[index];
			markerTemp.setMap(null);
		
		}
		
		this.list = new Array();
	}
	
	, draw : function(options){
		
		var position = new google.maps.LatLng(options.lat, options.lng);
	
		var markerOption = {
		    position : position,
		    title : options.title,
		    icon : options.icon
		}
		
		var marker = new google.maps.Marker(markerOption);
		marker.data = options.data;
		
		if(options.map == null){
			marker.setMap(this.map);
			this.map.setCenter(position);
			this.map.setZoom(16);
		}
		else{
			marker.setMap(options.map);
			options.map.setCenter(position);
			options.map.setZoom(16);
		}
		
		var infowindowContents = "<div style=\"padding-top:5px;bolder;padding-bottom:5px;\"><span style=\"color:#4C4C4C;font-size:16px;font-weight:bolder;\">" + options.contents + "</span></div>";
							   
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
				
		
		
		this.list.push(marker);
			
	}
}