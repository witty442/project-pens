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
		alert("��س�����ѹ����Ѻ�Թ���");
		return false;
	}
	
	var currDate = new Date();
    var currMonth = currDate.getMonth()+1;
    var currYear = currDate.getFullYear();
    
	var reqYear = billPlanRequestDate.split('/')[2]-543;
	var reqMonth = billPlanRequestDate.split('/')[1];
	
	//alert(currMonth+"="+reqMonth+","+currYear+"="+reqYear);
	if(reqMonth != currMonth || reqYear != currYear){
		alert("��سҷ���¡��������͹");
		document.getElementsByName('billPlan.billPlanRequestDate')[0].focus();
		return false;
	}
	
	var billPlanDateObj = thaiDateToChristDate(billPlanDate);
	var billPlanRequestDateObj = thaiDateToChristDate(billPlanRequestDate);
	
	if(billPlanRequestDateObj < billPlanDateObj){
		alert("�ѹ����Ѻ�Թ��ҵ�ͧ�ҡ���������ҡѺ �ѹ����͡��ú�� T");
		document.getElementsByName('billPlan.billPlanRequestDate')[0].focus();
		return false;
	}
	
	if(confirm("�׹�ѹ����Ѻ�Թ������ PD")){
		document.billPlanForm.action = path + "/jsp/billPlanAction.do?do=confirmBillPlan";
		document.billPlanForm.submit();
		return true;
	}
	return false;
}

function cancelBillPlan(path,billPlanNo){
	if(confirm("¡��ԡ����Ѻ�Թ������ PD")){
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

