
function prepare(path,type,id){
	if(id!=null){
		document.orderForm.action = path + "/jsp/saleOrderAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.orderForm.action = path + "/jsp/saleOrderAction.do?do=prepare"+"&action="+type;
	}
	document.orderForm.submit();
	return true;
}

/** Link Sales Order Special **/
function prepareSP(path,type,id){
	if(id!=null){
		document.orderForm.action = path + "/jsp/saleOrderSpecialAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.orderForm.action = path + "/jsp/saleOrderSpecialAction.do?do=prepare"+"&action="+type;
	}
	document.orderForm.submit();
	return true;
}

function prepareEditOrder(path,type,id){
	if(id!=null){
	  document.orderForm.action = path + "/jsp/saleOrderAction.do?do=prepareEditOrder&id=" + id;
	}
	document.orderForm.submit();
	return true;
}
function prepareEditOrderSP(path,type,id){
	if(id!=null){
	  document.orderForm.action = path + "/jsp/saleOrderSpecialAction.do?do=prepareEditOrder&id=" + id;
	}
	document.orderForm.submit();
	return true;
}
function prepareEditReceipt(path,type,id){
	if(id!=null){
	  document.orderForm.action = path + "/jsp/saleOrderAction.do?do=prepareEditReceipt&id=" + id;
	}
	document.orderForm.submit();
	return true;
}

function cancelOrder(path){
	var remark = document.getElementsByName('order.reason');
	
	if(remark[0].value == ''){
		alert("กรุณากรอกเหตุผลในการยกเลิก");
		remark[0].focus();
		return false;
	}
	if(remark[0].value.length > 300){
		alert("ข้อความยาวเกินกว่าที่ระบบตั้งไว้ กรุณากรอกเหตุผลในการยกเลิกใหม่");
		remark[0].focus();
		return false;
	}
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=cancelOrder";
    document.orderForm.submit();
    return true;
}
function manageProdShow(path,orderNo){
	if(orderNo!=null){
	  document.orderForm.action = path + "/jsp/prodshow/prodShow.jsp?action=new&orderNo="+orderNo;
	}
	document.orderForm.submit();
	return true;
}

function openCancelOrderPopup(path,orderNo,id,payment){
	window.open(path + "/jsp/pop/cancelOrderPopup.jsp?id="+id+"&orderNo="+orderNo+"&payment="+payment, "Edit Shipping Date.", "width=400,height=260,location=No,resizable=No");
}

function creditNoteList(path,invoiceNo){
	window.open(path + "/jsp/pop/creditNoteList.jsp?invoiceNo="+invoiceNo, "Credit Note List", "width=520,height=260,location=No,resizable=No");
}

function add_auto_transaction(path, type, id){
	prepare(path, type, id);
}

function add_product(path, id){
	window.open(path + "/jsp/pop/productPopup.jsp?id=" + id, "Product", "width=700,height=400,location=No,resizable=No");
	return;
}

function open_product(path, rowNo){
	if(rowNo==null){
		window.open(path + "/jsp/pop/productPopup.jsp", "Product", "width=700,height=450,location=No,resizable=No");
	}else{
		window.open(path + "/jsp/pop/productPopup.jsp?row="+rowNo, "Product", "width=700,height=450,location=No,resizable=No");
	}
	return;
}
function gotoReport(path, role){
	var rand = Math.floor(Math.random()*11);
	var w = 700;
	var h = 280;
	var left =  (screen.width/2)-(w/2);
    var top = (screen.height/2)-(h/2);
	window.open(path + "/jsp/pop/nextVisitPopup.jsp?rand="+rand+"&role="+role, "Print Receipt/Tax Invoice", "width="+w+",height="+h+", top="+top+", left="+left+",location=0,resizable=0");
}

function presave(path) {
	if(document.getElementsByName('order.shipAddressId')[0].value==''){
		alert('ไม่มีที่อยู่แบบ Ship to ไม่สามารถบันทึกข้อมูลได้');
		return false;
	}
	
	if(document.getElementsByName('order.billAddressId')[0].value==''){
		alert('ไม่มีที่อยู่แบบ Bill to ไม่สามารถบันทึกข้อมูลได้');
		return false;
	}
    //Validate Van Payment method
	if( !validateVanPaymentMethod()){return false;}
	
	//Validate Van Credit Limit
	if( !validateVanCreditLimit()){return false;}
	
	if(!createProductList()){return false;}
	
	//Validate item vat and no vat not in same bill
	if( !validateItemVatOrNoNotInSameBill()){return false;}
	
	/**Control Save Lock Screen **/
    //startControlSaveLockScreen();
    
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=preSave";
	document.orderForm.submit();
	return true;
}

function backadd(path) {
	if(!createProductList()){return false;}
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=backToAdd";
	document.orderForm.submit();
	return true;
}

function autoReceipt(path,type) {
	
	if(document.getElementsByName('order.shipAddressId')[0].value==''){
		alert('ไม่มีที่อยู่แบบ Ship to ไม่สามารถบันทึกข้อมูลได้');
		return false;
	}
	
	if(document.getElementsByName('order.billAddressId')[0].value==''){
		alert('ไม่มีที่อยู่แบบ Bill to ไม่สามารถบันทึกข้อมูลได้');
		return false;
	}
	
	var amount=0;
	var billId=0;
	amount = document.getElementsByName('order.netAmount')[0].value;
	billId = document.getElementsByName('order.id')[0].value;
	
	if(type=='VAN'){
		window.open(path + "/jsp/pop/autoReceiptPopupVan.jsp?amount="+amount+"&billId="+billId, "AutoReceipt", "width=1000,height=500,location=No,resizable=No");
	}else{
		save(path);
	}
}

function autoReceiptNew(path,type,canReceiptMoreCash) {
	
	if(document.getElementsByName('order.shipAddressId')[0].value==''){
		alert('ไม่มีที่อยู่แบบ Ship to ไม่สามารถบันทึกข้อมูลได้');
		return false;
	}
	
	if(document.getElementsByName('order.billAddressId')[0].value==''){
		alert('ไม่มีที่อยู่แบบ Bill to ไม่สามารถบันทึกข้อมูลได้');
		return false;
	}
	
	var amount=0;
	var billId=0;
	amount = document.getElementsByName('order.netAmount')[0].value;
	billId = document.getElementsByName('order.id')[0].value;
	
	if(type=='VAN'){
		window.open(path + "/jsp/pop/autoReceiptPopupVan.jsp?amount="+amount+"&billId="+billId+"&canReceiptMoreCash="+canReceiptMoreCash, "AutoReceipt", "width=1000,height=500,location=No,resizable=No");
	}else{
		save(path);
	}
}

function save(path,role) {
	var paymentCashNow = document.getElementsByName('order.paymentCashNow')[0];
	//Validate Van Payment method
	if( !validateVanPaymentMethod()){return false;}
	//Validate Van Credit Limit
	if( !validateVanCreditLimit()){return false;}
	
	if(!createProductList()){return false;}
	
	/**Control Save Lock Screen **/
    //startControlSaveLockScreen();
    
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=save";
	document.orderForm.submit();
	return true;
}

function search(path){
	if(!datedifference(document.getElementById('orderDateFrom'),document.getElementById('orderDateTo'))){return false;}
	
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=search&action=newsearch";
	document.orderForm.submit();
	return true;
}
function gotoPage(currPage){
    var path = document.getElementById("path").value;
    document.orderForm.action = path + "/jsp/saleOrderAction.do?do=search&currPage="+currPage;
	document.orderForm.submit();
	return true;
}

function clearForm(path){
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=clearForm";
	document.orderForm.submit();
	return true;
}

function backsearch(path,customerId) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.orderForm.action = path + "/jsp/saleOrderAction.do?do=search";
	} else {
		document.orderForm.action = path + "/jsp/saleOrderAction.do?do=prepare&customerId="+customerId;
	}
	document.orderForm.submit();
	return true;
}

function backToCusotmer(path,customerId) {
	var orderType = document.getElementsByName("order.orderType")[0].value;
	if(orderType=='DD'){
		window.location = path+'/jsp/memberAction.do?do=prepare&id='+customerId+'&action=process';
	}else{
		window.location = path+'/jsp/customerAction.do?do=prepare&id='+customerId+'&action=process';
	}
}


function toFixed(num, pre){
	num = Number(num);
	num *= Math.pow(10, pre);
	num = (Math.round(num, pre) + (((num - Math.round(num, pre))>=0.5)?1:0)) / Math.pow(10, pre);
	return num.toFixed(pre);
}


function createAutoReceipt(path,action) {
	//alert("action:"+action);
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=createAutoReceipt&actionSave="+action;
	document.orderForm.submit();
	return true;
}

/** 10/10/2555 to 10/10/2012**/
function thaiDateToChristDate(thaiDate){
	if(thaiDate != ''){
		var dd = thaiDate.substring(0,2);
	    var mm =  thaiDate.substring(3,5);
	    var yyyy =  parseFloat(thaiDate.substring(6,10))-543;
		//alert(dd+"/"+mm+"/"+yyyy);
	    return new Date(mm+"/"+dd+"/"+yyyy);
	}
}

