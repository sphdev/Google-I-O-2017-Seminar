/**
 * Date : 2015.07.13
 * Writer : 조용균
 * File : loading.js
 * Description
 * : JS App에서 사용하는 로딩화면
 */


/*
 * Date : 2015.07.13
 * Writer : 조용균
 * Description
 * : 로딩화면 UI 인스턴스 설정
 */
objOverlay = $('<div id="divOverlay" class="view-loading-overlay"></div>');
objModal = $('<div id="divModal" class="view-loading-modal"></div>');
objContent = $('<div id="divContent" class="view-loading-content"></div>');
objImg = $('<center><img src=\"./src/view/loading/loading.gif\" id="divLoading" class="view-loading-image"/></center>');

objModal.hide();
objModal.hide();
objModal.append(objImg, objContent);

$(document).ready(function(){
	$('body').append(objOverlay, objModal);
});


/**
 * Date : 2015.07.13
 * Writer : 조용균
 * Description
 * : 로딩화면을 제어하기 위한 Loading Object
 */
$Loading = {

	/*
	 * Date : 2015.07.13
	 * Writer : 조용균
	 * Description
	 * : 로딩화면 화면의 가운데 정렬
	 */
	center : function () {
	    var top, left;

	    top = Math.max($(window).height() - objModal.outerHeight(), 0) / 2;
	    left = Math.max($(window).width() - objModal.outerWidth(), 0) / 2;

	    objModal.css({
	        top:top + $(window).scrollTop(), 
	        left:left + $(window).scrollLeft()
	    });
	},
	
	/*
	 * Date : 2015.07.13
	 * Writer : 조용균
	 * Description
	 * : 일반 로딩화면 화면을 호출
	 */
	show : function (settings) {
		
		if(settings == null){
			settings = {
				content : "Loading."	
			};
		}
		
		objContent.empty().append(settings.content);

		objModal.css({
	        width: settings.width || 'auto', 
	        height: settings.height || 'auto'
	    });

		$Loading.center();

	    $(window).bind('resize.modal', $Loading.center);

	    objModal.show();
	    objOverlay.show();
	},
	
	
	/*
	 * Date : 2015.07.13
	 * Writer : 조용균
	 * Description
	 * : 처리률이 출력되는 로딩화면을 호출
	 */
	intProcessCount : 0,
    intTotalCount : 0,
	progress : function (intProcessCount, intTotalCount) {
		var settings = {content: "처리중.<br>" + intProcessCount +" / " + intTotalCount + ""}
		objContent.empty().append(settings.content);
	},
	
	/*
	 * Date : 2015.07.13
	 * Writer : 조용균
	 * Description
	 * : 로딩화면 닫기
	 */
	close : function () {
		objModal.hide();
	    objOverlay.hide();
	    objContent.empty();
	    $(window).unbind('resize.modal');
	}
		
};


$Loading.close();
