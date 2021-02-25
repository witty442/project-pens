
function save(path,moveOrderType){
	if(checkCanSave()){
		document.stockMCForm.action = path + "/jsp/stockMCAction.do?do=save";
		document.stockMCForm.submit();
		return true;
	}
	return false;
}
function isProductDuplicate(curRowIdChk){
	var itemCode = document.getElementsByName("productCode");
	var status = document.getElementsByName("status");
	
	var itemCodeCheck = itemCode[curRowIdChk-1].value;
	var curCheck = itemCodeCheck;
	var checkTemp = "";
	var dup = false;
	for(var i=0;i<itemCode.length;i++){
	   if(status[i].value !='DELETE' && (i != (curRowIdChk-1)) ){
		   checkTemp = itemCode[i].value;
		   if(checkTemp==curCheck){
			   dup = true;
		   }
	   }
	}//for
	//alert('dup:'+dup);
	return dup;
}

function checkCanSave(){
	var selectOne = false;
	var itemCode = document.getElementsByName("productCode");
	//var status = document.getElementsByName("status");
	
	var mcName = document.getElementById("mcName");
	var customerCode = document.getElementById("customerCode");
	var storeCode = document.getElementById("storeCode");
	
	if(mcName.value ==''){
		alert("กรุณาระบุ ชื่อ-นามสกุล พีซี");
		mcName.focus();
		r = false;
		return r;
	}
	if(customerCode.value ==''){
		alert("กรุณาระบุ ห้าง");
		customerCode.focus();
		r = false;
		return r;
	}
	if(storeCode.value ==''){
		alert("กรุณาระบุ สาขา");
		storeCode.focus();
		r = false;
		return r;
	}
	
    //check insert 1 row
	for(var i=0;i<itemCode.length;i++){
	  //alert(itemCode[i].value+":"+status[i].value);
	  if(itemCode[i].value !='' ){//}&& status[i].value !='DELETE'){
		  selectOne = true;
	   }
	}//for
    
	if(selectOne ==false){
		alert("กรุณาระบุข้อมูล อย่างน้อย 1 แถว");
		itemCode[0].focus();
		return false;
	}
	return true;
}

function clearForm(path){
	document.stockMCForm.action = path + "/jsp/stockMCAction.do?do=clear";
	document.stockMCForm.submit();
	return true;
}

function escapeParameter(param){
	return param.replace("%","%25");
}

function popCalendar(thisObj, thisEvent) {
	new Epoch('epoch_popup', 'th', thisObj);
}
function checkCustNameOnblur(e,customerCodeObj){
	 //alert("ONBLUR");
	//alert(itemCodeObj.value);
	var customerName = document.getElementById("customerName");
	if(customerName.value ==''){
		customerCodeObj.value ='';
		customerCodeObj.focus();
	}
}
function getCustNameKeypress(e,customerCode){
	var form = document.stockMCForm;
	if(e != null && e.keyCode == 13){
		//alert(customerCode.value);
		if(customerCode.value ==''){
			form.customerName.value = '';
		}else{
			getCustNameModel(customerCode);
		}
	}
}
function getCustNameModel(customerCode){
	var returnString = "";
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	var getData = $.ajax({
			url: path+"/jsp/stockMC/ajax/getCustNameAjax.jsp",
			data : "customerCode=" + customerCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	if(returnString !=''){
		//var retArr = returnString.split("|");
		form.customerName.value =returnString;	
	}else{
		alert("ไม่พบข้อมูล");
		form.customerCode.value = "";	
		form.customerName.value = "";
	}
}
function openPopup(path,pageName,nextStep){
	var form = document.stockMCForm;
	var param = "&pageName="+pageName;
	var nextStepObj  = document.getElementById("nextStep");
	nextStepObj.value = nextStep;
	
	if("CustomerStockMC" == pageName){
		param +="&hideAll=true&filterUser=true&customerCode="+form.customerCode.value;
	}else if("BranchStockMC" == pageName){
		//check input customerCode;
		var customerCode  = document.getElementById("customerCode");
		if(customerCode.value != ""){
		   param +="&hideAll=true&filterUser=true&customerCode="+form.customerCode.value;
		}else{
		   alert("กรุณาระบุห้าง ");
		   customerCode.focus();
		   return false;
		}
	}else if("BrandStockMC" == pageName){
		param +="&hideAll=true&customerCode="+form.customerCode.value;
	}
	url = path + "/jsp/popupAction.do?do=prepareAndSearch&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.stockMCForm;
	var nextStepObj  = document.getElementById("nextStep");
	if("CustomerStockMC" == pageName){
		form.customerCode.value = code;
		form.customerName.value = desc;
		
		//case change custCode clear branch
		form.storeCode.value = '';
		form.storeName.value = '';
	}else if("BranchStockMC" == pageName){
		form.storeCode.value = code;
		form.storeName.value = desc;
		
		//after select branch action Next
		if(nextStepObj.value =="true"){
		   gotoStockMCMStep3();
		}
	}else if("BrandStockMC" == pageName){
		form.brand.value = code;
		form.brandName.value = desc;
		
		//after select Brand action Next
		if(nextStepObj.value =="true"){
		   gotoStockMCMStep4();
		}
	}
} 


