function search(path){
	document.billPlanForm.action = path + "/jsp/billPlanAction.do?do=search&rf=Y";
	document.billPlanForm.submit();
	return true;
}

function view(path,billPlanNo){
	document.billPlanForm.action = path + "/jsp/billPlanAction.do?do=view&billPlanNo="+billPlanNo;
	document.billPlanForm.submit();
	return true;
}


function confirmBillPlan(path,billPlanNo){
	var billPlanDate = document.getElementsByName('billPlan.billPlanDate')[0].value;//Date from Oracle 
	var billPlanRequestDate = document.getElementsByName('billPlan.billPlanRequestDate')[0].value;
	
	if(billPlanRequestDate ==''){
		alert("กรุณาใส่วันที่รับสินค้า");
		return false;
	}
	
	var currDate = new Date();
    var currMonth = currDate.getMonth()+1;
    var currYear = currDate.getFullYear();
    
	var reqYear = billPlanRequestDate.split('/')[2]-543;
	var reqMonth = billPlanRequestDate.split('/')[1];
	
	//alert(currMonth+"="+reqMonth+","+currYear+"="+reqYear);
	if(reqMonth != currMonth || reqYear != currYear){
		alert("กรุณาทำรายการภายในเดือน");
		document.getElementsByName('billPlan.billPlanRequestDate')[0].focus();
		return false;
	}
	
	var billPlanDateObj = thaiDateToChristDate(billPlanDate);
	var billPlanRequestDateObj = thaiDateToChristDate(billPlanRequestDate);
	
	if(billPlanRequestDateObj < billPlanDateObj){
		alert("วันที่รับสินค้าต้องมากว่าหรือเท่ากับ วันที่เอกสารบิล T");
		document.getElementsByName('billPlan.billPlanRequestDate')[0].focus();
		return false;
	}
	
	if(confirm("ยืนยันการรับสินค้าเข้า PD")){
		document.billPlanForm.action = path + "/jsp/billPlanAction.do?do=confirmBillPlan";
		document.billPlanForm.submit();
		return true;
	}
	return false;
}

function cancelBillPlan(path,billPlanNo){
	if(confirm("ยกเลิกการรับสินค้าเข้า PD")){
		document.billPlanForm.action = path + "/jsp/billPlanAction.do?do=cancelBillPlan";
		document.billPlanForm.submit();
		return true;
	}
	return false;
}

function backsearch(path) {
	document.billPlanForm.action = path + "/jsp/billPlanAction.do?do=prepare"+"&action=back";
	document.billPlanForm.submit();
}

function clearForm(path){
	document.billPlanForm.action = path + "/jsp/billPlanAction.do?do=clear";
	document.billPlanForm.submit();
	return true;
}

