function login(path){
	//set screenWidth
	//alert(document.getElementById('screenWidth').value);
	document.getElementsByName('screenWidth')[0].value = screen.width-50;
	document.getElementsByName('screenHeight')[0].value = screen.height-150;
	//alert(document.getElementsByName('screenHeight')[0].value);
	
    if(Trim(document.getElementsByName('userName')[0].value)==''){
        document.getElementsByName('userName')[0].focus();
        return false;
    }
    if(Trim(document.getElementsByName('password')[0].value)==''){
    	document.getElementsByName('password')[0].focus();
        return false;
    }
    document.loginForm.action=path+"/login.do?do=login";
    document.loginForm.submit();
    return true;
}