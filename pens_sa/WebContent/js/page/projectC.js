function openPopup(path,pageName){
	var form = document.projectCForm;
	var param = "&pageName="+pageName+"&hideAll=true&selectont=true";
	if("CustomerProjectC" == pageName){
	}else if("BranchProjectC" == pageName){
		if(form.storeCode.value==""){
			alert("กรุณาระบุร้านค้า");
			form.storeCode.focus();
			return "";
		}
		param  +="&storeCode="+form.storeCode.value;
	}

	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.projectCForm;
	if("CustomerProjectC" == pageName){
		form.storeCode.value = code;
		form.storeName.value = desc;
	}else if("BrancProjectC" == pageName){
		form.branchId.value = '';
		form.branchName.value = "";
	}
} 
/** get autoKeypress Ajax **/
function getAutoOnblur(e,obj,pageName){
	var form = document.projectCForm;
	if(obj.value ==''){
		if("CustomerProjectC" == pageName){
			form.storeCode.value = '';
			form.storeName.value = "";
		}else if("BranchProjectC" == pageName){
			form.branchId.value = '';
			form.branchName.value = "";
		}
	}else{
		getAutoDetail(obj,pageName);
	}
}
function getAutoKeypress(e,obj,pageName){
	var form = document.projectCForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("SalesrepSales" == pageName){
				form.storeCode.value = '';
				form.storeName.value = "";
			}else if("BranchProjectC" == pageName){
				form.branchId.value = '';
				form.branchName.value = "";
			}
		}else{
			getAutoDetail(obj,pageName);
		}
	}
}
function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.projectCForm;
	var path = form.path.value;
	
	//prepare parameter
	var param = "";
	if("CustomerProjectC"==pageName){
		param   ="pageName="+pageName;
		param  +="&storeCode="+obj.value;
	}else if("BranchProjectC"==pageName){
		if(form.storeCode.value==""){
			alert("กรุณาระบุร้านค้า");
			form.storeCode.focus();
			return "";
		}
		param   ="pageName="+pageName;
		param  +="&storeCode="+form.storeCode.value;
		param  +="&branchId="+obj.value;
	}
	var getData = $.ajax({
			url: path+"/jsp/ajax/getAutoKeypressAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	if("CustomerProjectC" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.storeCode.value = retArr[1];
			form.storeName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.storeCode.focus();
			form.storeCode.value = '';
			form.storeName.value = "";
		}
	}else if("BranchProjectC" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.branchId.value = retArr[1];
			form.branchName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.branchId.focus();
			form.branchId.value = '';
			form.branchName.value = "";
		}
		
	}
}

function openImage(path,fileName){
	var url =path+"/jsp/prodshow/showImage.jsp?fileName="+fileName;
	PopupCenter(url,"",600,600);
}