function PopupCenter(url, title, w, h) {
    // Fixes dual-screen position                         Most browsers      Firefox
    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    var left = ((width / 2) - (w / 2)) + dualScreenLeft;
    var top = ((height / 2) - (h / 2)) + dualScreenTop;
    if(title==''){
    	title ='���Ң�����';
    }
    w = 700;
    var newWindow = window.open(url, title, 'scrollbars=yes, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);

    // Puts focus on the newWindow
    if (window.focus) {
        newWindow.focus();
    }
}

function PopupCenterFullHeight(url, title, w) {
    // Fixes dual-screen position                         Most browsers      Firefox
    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

 
    var left = ((width / 2) - (w / 2)) + dualScreenLeft;
    var top = 0;
    
    if(title==''){
    	title ='���Ң�����';
    }
    w = 700;
    var newWindow = window.open(url, title, 'scrollbars=yes,location=no,resizable=no,titlebar=no, width=' + w + ', height=' + height + ', top=' + top + ', left=' + left);

    // Puts focus on the newWindow
    if (window.focus) {
        newWindow.focus();
    }
}

function PopupCenterFullHeightCustom(url, title, w,delH) {
    // Fixes dual-screen position                         Most browsers      Firefox
    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    var left = ((width / 2) - (w / 2)) + dualScreenLeft;
    var top = 0;
    
    if(title==''){
    	title ='���Ң�����';
    }
    height = height+ delH;//adjust height(-)
    var newWindow = window.open(url, title, 'scrollbars=yes,location=no,resizable=no,titlebar=no, width=' + w + ', height=' + height + ', top=' + top + ', left=' + left);

    // Puts focus on the newWindow
    if (window.focus) {
        newWindow.focus();
    }
}

function PopupCenterFull(url, title) {
    // Fixes dual-screen position                         Most browsers      Firefox
    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    var left = 0;
    var top = 0;
    
    if(title==''){
    	title ='���Ң�����';
    }
    var newWindow = window.open(url, title, 'scrollbars=yes,location=no,resizable=no,titlebar=no, width=' + width + ', height=' + height + ', top=' + top + ', left=' + left);

    // Puts focus on the newWindow
    if (window.focus) {
        newWindow.focus();
    }
}

function PopupCenterFullDisableAll(url, title) {
    // Fixes dual-screen position   Most browsers      Firefox
    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    var left = 0;
    var top = 0;
    
    if(title==''){
    	title ='���Ң�����';
    }
    var newWindow = window.open(url, title, 'fullscreen=yes,status=no,menubar=no,scrollbars=yes,location=no,resizable=no,titlebar=no,toolbar=no, width=' + width + ', height=' + height + ', top=' + top + ', left=' + left);

    // Puts focus on the newWindow
    if (window.focus) {
        newWindow.focus();
    }
}
function popupFull(url){
	var params = [
          'height='+screen.height,
          'width='+screen.width,
          'fullscreen=yes' // only works in IE, but here for completeness
      ].join(',');
           // and any other options from
           // https://developer.mozilla.org/en/DOM/window.open

      var popup = window.open(url, 'popup_window', params); 
      popup.moveTo(0,0);
}
function popupBatchTaskPopup(url){
	
	// Fixes dual-screen position   Most browsers  Firefox
    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
   
    //manual screen
    var w = width;
    var h = 200;
    var systemZoom = width / window.screen.availWidth;
    var left = (width - w) / 2 / systemZoom + dualScreenLeft
    var top = (height - h) / 2 / systemZoom + dualScreenTop
    var title ='Submit Batch Task';
    var newWindow = window.open(url, title, 'scrollbars=no,location=no,resizable=no,titlebar=no, width=' + (w/systemZoom) + ', height=' + (h/systemZoom) + ', top=' + top + ', left=' + left);

    // Puts focus on the newWindow
    if (window.focus) {
        newWindow.focus();
    }
}