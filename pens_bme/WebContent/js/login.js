function login(path){
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