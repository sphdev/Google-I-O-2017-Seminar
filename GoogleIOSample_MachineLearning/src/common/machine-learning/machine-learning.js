/**
 * Date : 2015.07.13
 * Writer : 조용균
 * File : request.js
 * Description
 * : JS App에서 서버와 통신
 */


/**
 * Date : 2015.07.13
 * Writer : 조용균
 * Description
 * : 서버와의 통신을 Ajax로 제어하기 위한 Request Object
 */
$ML = {
	
	/*
	 * Date : 2015.07.13
	 * Writer : 조용균
	 * Description
	 * : Node.js로 구성된 JS Server를 통해 우회해서 Cross Domain에 대한 서버와의 ajax 통신
	 */
	  serverIP   : "http://localhost"
    , serverPort : "3000"
	, vision : function(param, callbackObject, methodType){
		
		var apiPath = "vision.api";
		var serverUrl  = this.serverIP + ":" + this.serverPort + "/" + apiPath;
		
		setTimeout(function(){

			
	//		console.log("==> vision : param : ", param);
			

			var paramData = new FormData();
			paramData.append('param', JSON.stringify(param));
			paramData.append('file', param.file);
			
			var paramString = JSON.stringify(param);
			
		//	console.log("==> obj_Param : ", paramData);
		//	console.log("==> paramString : ", paramString);
			
			
			
			var httpRequest = new XMLHttpRequest();
		    httpRequest.open("POST", serverUrl, true);
		    httpRequest.onreadystatechange = function(){
		    	if(httpRequest.readyState == 4) {
		    		if(httpRequest.status == 200){
		    			
		    			var result = eval("("+httpRequest.responseText+")");
		    			if(callbackObject != null){
		    				callbackObject.callback(result, callbackObject.data);
		    			}
		         	}
		        }
		    }
		    httpRequest.send(paramData);
		}, 10);
	},
	
	setCallback : function(callback, data){
		
		var callbackObject = {
			  data : data
			, callback : callback
		}
		
		return callbackObject;
	},
	
	
};
