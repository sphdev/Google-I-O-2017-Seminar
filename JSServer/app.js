
/**
 * Module dependencies.
 */

var express = require('express');
var routes = require('./routes');
var user = require('./routes/user');
var http = require('http');
var path = require('path');
var querystring = require('querystring');
var request = require("request");


var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);


// development only : io js
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.all('*', function(req, res, next) {
	
	res.header("Access-Control-Allow-Origin", "*");
	res.header("Access-Control-Allow-Headers", "X-Requested-With");
	next();
});


app.get('/', routes.index);
app.get('/users', user.list);

app.post('/vision.api', function (req, res) {

	//console.log("Start vision.api ! : ", req.body.param);
	
	var param = JSON.parse(req.body.param);

	//var requestParam = new Array();
	
	//for(var index = 0 ; index < param.length ; index++){
	
	var imageData = param.data;
	
	var paramData = {
			"image":{
				"content":imageData
			}
			, "features" : [
				 {"type":"TYPE_UNSPECIFIED","maxResults":50}
				,{"type":"LANDMARK_DETECTION","maxResults":50}
				,{"type":"FACE_DETECTION","maxResults":50}
				,{"type":"LOGO_DETECTION","maxResults":50}
				,{"type":"LABEL_DETECTION","maxResults":50}
				,{"type":"TEXT_DETECTION","maxResults":50}
				,{"type":"DOCUMENT_TEXT_DETECTION","maxResults":50}
				,{"type":"SAFE_SEARCH_DETECTION","maxResults":50}
				,{"type":"IMAGE_PROPERTIES","maxResults":50}
				,{"type":"CROP_HINTS","maxResults":50}
				,{"type":"WEB_DETECTION","maxResults":50}
			]
			,"imageContext":{"cropHintsParams":{"aspectRatios":[0.8,1,1.2]}}
		};
		
	//	requestParam.push(paramData)
		
	//}

	
	var Vision = require('@google-cloud/vision');

	// Your Google Cloud Platform project ID
	var projectId = 'machine-learning-169506';

	// Instantiates a client
	var visionClient = Vision({
		  projectId   : projectId
		, keyFilename : 'Machine_Learning_KEY_JSON_FILE'
	});
	
	
	visionClient.annotate(paramData, function(error, detections, apiResponse) {
		
		
		console.log("==> error : ", error);
		console.log("==> detections : ", detections);
		console.log("==> apiResponse : ", apiResponse);
		
		res.header("Access-Control-Allow-Origin", "*");
		res.header("Access-Control-Allow-Headers", "X-Requested-With");
		res.send({
			  "error"       : error
			, "detections"  : detections
			, "apiResponse" : apiResponse
			, "id"          : param.id
		});
	
	});
	
});


http.createServer(app).listen(app.get('port'), function(){
	
	console.log('Express server listening on port : ' + app.get('port'));
	
});
