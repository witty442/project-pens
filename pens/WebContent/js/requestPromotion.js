function search(path){
	document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=search&rf=Y";
	document.requestPromotionForm.submit();
	return true;
}

function createRequestPromotion(path){
	document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=createRequestPromotion";
	document.requestPromotionForm.submit();
	return true;
}

function editRequestPromotion(path,requestNumber){
	//alert(moveOrderType+":"+requestNumber);
	document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=editRequestPromotion&requestNo="+requestNumber;
	document.requestPromotionForm.submit();
	return true;
}

function printRequestPromotion(path){

    document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=popupPrint";
    document.requestPromotionForm.submit();
    return true;
}

function popupPrint(path,requestNo){
	var param = "&requestNo="+requestNo;
        //window.open(path + "/jsp/requestPromotionAction.do?do=popupPrint"+param, "Print", "width=80,height=50,location=No,resizable=No");
	
	 document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=popupPrint"+param;
	 document.requestPromotionForm.submit();
	 return true;
}

function cancelRequestPromotion(path,moveOrderType){
	//alert(moveOrderType+":"+requestNumber);
	
	if(confirm("ยืนยันการ ยกเลิกรายการ")){
		/*var input= confirmInputReason();
		if(input){*/
			//document.getElementsByName('moveOrder.description')[0].value = input;
		    document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=cancelRequestPromotion";
		    document.requestPromotionForm.submit();
		    return true;
		//}
	}
	return false;
}

function confirmInputReason(){
	var desc= prompt("กรุณาใส่เหตุผลในการยกเลิก","");
	if(desc == '') {
		confirmInputReason();
	}
	return desc;
}

function viewRequestPromotion(path,moveOrderType,requestNumber){
	document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=viewRequestPromotion&requestNumber="+requestNumber;
	document.requestPromotionForm.submit();
	return true;
}

function save(path){
	
	var productType = document.getElementsByName("requestPromotion.productType");
//	alert(productType[0].checked +":"+productType[1].checked);
	
	if(productType[0].checked == false && productType[1].checked ==false){
		alert("กรุณาเลือกประเภทรายการ  โปรโมชั่น  หรือ เคลียร์สต็อก");
		return false;
	}
	
	if($("#customerName").val()  ==''){
		alert("กรุณาใส่ รหัสร้านค้า");
		return false;
	}
	
	if($("#name").val()  ==''){
		alert("กรุณาใส่่ชื่อรายการ");
		return false;
	}
	
	if($("#customerCode").val()  ==''){
		alert("กรุณาใส่ รหัสร้านค้า");
		return false;
	}
	
	if($("#promotionStartDate").val() ==''){
		alert("กรุณาใส่่วันที่ ระยะเวลาจาก");
		return false;
	}
	
	if($("#promotionEndDate").val()  ==''){
		alert("กรุณาใส่่วันที่ ระยะเวลาถึง");
		return false;
	}
	
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=save";
	document.requestPromotionForm.submit();
	return true;
}

function backsearch(path) {

	document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=prepare"+"&action=back";
	document.requestPromotionForm.submit();
}

function backToCusotmer(path,customerId,fromPage) {
	if(fromPage =='customerSearch'){
	   window.location = path+'/jsp/customer.do';
	}else{
	   window.location = path+'/jsp/customerAction.do?do=prepare&id='+customerId+'&action=process';
	}
}

function clearForm(path){
	document.requestPromotionForm.action = path + "/jsp/requestPromotionAction.do?do=clear";
	document.requestPromotionForm.submit();
	return true;
}

function toFixed(num, pre){
	num = Number(num);
	num *= Math.pow(10, pre);
	num = (Math.round(num, pre) + (((num - Math.round(num, pre))>=0.5)?1:0)) / Math.pow(10, pre);
	return num.toFixed(pre);
}

