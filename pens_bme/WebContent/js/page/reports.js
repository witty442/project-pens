function openPopupCustomer(path,types,storeType){
    var param = "&types="+types;
        param += "&storeType="+storeType;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare2&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function openPopupCustomerWithSubInv(path,types,storeType,methodName){
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&methodName="+methodName;
        
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}

function openPopupCustomerAll(path,types,storeType,hideAll){
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&hideAll="+hideAll;
        
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function openPopupBranchAll(path,types,storeType,hideAll){
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&hideAll="+hideAll;
        
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepareBranch&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function openPopupGroup(path,selectOne,storeType){
    var param = "&selectOne="+selectOne+"&storeType="+storeType;
	url = path + "/jsp/searchGroupPopupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}

function openPopupProduct(path,types,storeType){
	var param = "&types="+types+"&storeType="+storeType;
	url = path + "/jsp/searchProductPopupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}

function setStoreMainValue(code,desc,types){
	var form = document.reportsForm;
	//alert(form);
	if("from" == types){
		form.pensCustCodeFrom.value = code;
		form.pensCustNameFrom.value = desc;
	}else{
		form.pensCustCodeTo.value = code;
		form.pensCustNameTo.value = desc;
	}
} 

function setGroupMainValue(code,desc,types){
	var form = document.reportsForm;
	form.group.value = code;
	form.groupDesc.value = desc;
}

function setProductMainValue(code,desc,types){
	var form = document.reportsForm;
	//alert(form);
	if("from" == types){
		form.pensItemFrom.value = code;
	}else{
		form.pensItemTo.value = code;
	}
} 

function getCustNameKeypress(path,e,custCode,fieldName){
	var form = document.reportsForm;
	var storeType = form.storeType.value;
	
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("pensCustNameFrom" == fieldName){
				form.pensCustNameFrom.value = '';
			}
			if("pensCustNameTo" ==fieldName){
				form.pensCustNameTo.value = '';
			}
		}else{
		   getCustName(path,custCode,fieldName,storeType);
		}
	}
}

function getCustName(path,custCode,fieldName,storeType){
	var returnString = "";
	var form = document.reportsForm;
	var getData = $.ajax({
			url: path+"/jsp/ajax/getCustNameAjax.jsp",
			data : "custCode=" + custCode.value+"&storeType="+storeType,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if("pensCustNameFrom" == fieldName){
		if(returnString != ''){
		   form.pensCustNameFrom.value = returnString;
		}else{
			custCode.value ='';
			custCode.focus();
			alert("ไม่พบข้อมูล");
		}
	}
	if("pensCustNameTo" ==fieldName){
		if(returnString != ''){
		   form.pensCustNameTo.value = returnString;
		}else{
			custCode.value ='';
			custCode.focus();
			alert("ไม่พบข้อมูล");
		}
	}
}
function getCustNameWithSubInvKeypress(path,e,custCode,storeType,fieldName){
	var form = document.reportsForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("pensCustNameFrom" == fieldName){
				form.pensCustNameFrom.value = '';
			}
			if("pensCustNameTo" ==fieldName){
				form.pensCustNameTo.value = '';
			}
		}else{
			getCustNameWithSubInv(path,custCode,fieldName,storeType);
		}
	}
}
function getCustNameWithSubInv(path,custCode,fieldName,storeType){
	var returnString = "";
	var form = document.reportsForm;
	var getData = $.ajax({
			url: path+"/jsp/ajax/getCustNameWithSubInvAjax.jsp",
			data : "custCode=" + custCode.value+"&storeType="+storeType,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if("pensCustNameFrom" == fieldName){
		if(returnString != ''){
		   var reArr = returnString.split("|");
		   form.pensCustNameFrom.value = reArr[0];
		   form.subInv.value = reArr[2];
		}else{
			custCode.value ='';
			form.pensCustNameFrom.value ='';
			form.subInv.value ='';
			custCode.focus();
			alert("ไม่พบข้อมูล");
		}
	}
	if("pensCustNameTo" ==fieldName){
		if(returnString != ''){
			var reArr = returnString.split("|");
			form.pensCustNameTo.value = reArr[0];
			form.subInv.value = reArr[2];
		}else{
			custCode.value ='';
			form.pensCustNameTo.value ='';
			form.subInv.value ='';
			custCode.focus();
			alert("ไม่พบข้อมูล");
		}
	}
}
function getBranchNameKeypress(path,e,custCode,fieldName,storeType){
	var form = document.reportsForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("pensCustNameFrom" == fieldName){
				form.pensCustNameFrom.value = '';
			}
			if("pensCustNameTo" ==fieldName){
				form.pensCustNameTo.value = '';
			}
		}else{
		   getBranchName(path,custCode,fieldName,storeType);
		}
	}
}
function getBranchName(path,custCode,fieldName,storeType){
	var returnString = "";
	var form = document.reportsForm;
	var getData = $.ajax({
			url: path+"/jsp/ajax/getBranchNameAjax.jsp",
			data : "custCode=" + custCode.value+"&storeType="+storeType,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if("pensCustNameFrom" == fieldName){
		if(returnString != ''){
		   form.pensCustNameFrom.value = returnString;
		}else{
			custCode.value ='';
			custCode.focus();
			alert("ไม่พบข้อมูล");
		}
	}
	if("pensCustNameTo" ==fieldName){
		if(returnString != ''){
		   form.pensCustNameTo.value = returnString;
		}else{
			custCode.value ='';
			custCode.focus();
			alert("ไม่พบข้อมูล");
		}
	}
}