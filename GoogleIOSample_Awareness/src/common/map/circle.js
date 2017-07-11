function Circle(map, options){
	
	this.map = map;
	this.center = new Array();
	this.options = options;
}

Circle.prototype.circle = null;
Circle.prototype.center = null;
Circle.prototype.options = null;

Circle.prototype.setOptions = function(options){
	this.options = options
}

Circle.prototype.getOptions = function(options){
	return this.options;
}


Circle.prototype.setCenter = function(lat, lng){
	this.center = {
		  lat : lat
		, lng : lng
	};
}

Circle.prototype.draw = function(option){
	
	if(this.options == null){
		this.options = {
    		strokeColor: '#FFFFFF',
            strokeOpacity: 1.0,
            strokeWeight: 2,
            fillColor: '#6799FF',
            fillOpacity: 0.8,
            map: map,
            center: this.center,
            radius: 100.0
        }
		
	}
	else{
		this.options.center = this.center;
	}
	
	this.circle = new google.maps.Circle(this.options);
	
	this.circle.setMap(this.map);
       
}


