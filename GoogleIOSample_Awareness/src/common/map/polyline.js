function Polyline(map, options){
	
	this.map = map;
	this.coordinates = new Array();
	this.options = options;
}

Polyline.prototype.polyline = null;
Polyline.prototype.coordinates = null;
Polyline.prototype.options = null;

Polyline.prototype.setOptions = function(options){
	this.options = options
}

Polyline.prototype.getOptions = function(options){
	return this.options;
}


Polyline.prototype.addPoint = function(lat, lng){
	this.coordinates.push({
		  lat : lat
		, lng : lng
	})
}

Polyline.prototype.draw = function(option){
	
	if(this.options == null){
		this.options = {
			path: this.coordinates,
			geodesic: true,
			strokeColor: '#FF0000',
			strokeOpacity: 1.0,
			strokeWeight: 2
        }
	}
	else{
		this.options.path = this.coordinates;
	}
	
	this.polyline = new google.maps.Polyline(this.options);
	
	this.polyline.setMap(this.map);
       
}


