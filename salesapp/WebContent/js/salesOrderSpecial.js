
function prepare(path,type,id){
	if(id!=null){
		document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=prepare"+"&action="+type;
	}
	document.orderSpecialForm.submit();
	return true;
}

function prepareEditOrder(path,type,id){
	if(id!=null){
	  document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=prepareEditOrder&id=" + id;
	}
	document.orderSpecialForm.submit();
	return true;
}

function prepareEditReceipt(path,type,id){
	if(id!=null){
	  document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=prepareEditReceipt&id=" + id;
	}
	document.orderSpecialForm.submit();
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
	document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=cancelOrder";
    document.orderSpecialForm.submit();
    return true;
}
function manageProdShow(path,orderNo){
	if(orderNo!=null){
	  document.orderSpecialForm.action = path + "/jsp/prodshow/prodShow.jsp?action=new&orderNo="+orderNo;
	}
	document.orderSpecialForm.submit();
	return true;
}

/*function openCancelOrderPopup(path,orderNo,id,payment){
	window.open(path + "/jsp/pop/cancelOrderPopup.jsp?id="+id+"&orderNo="+orderNo+"&payment="+payment, "Edit Shipping Date.", "width=400,height=260,location=No,resizable=No");
}*/

function creditNoteList(path,invoiceNo){
	window.open(path + "/jsp/salesspecial/pop/creditNoteList.jsp?invoiceNo="+invoiceNo, "Credit Note List", "width=520,height=260,location=No,resizable=No");
}

function add_auto_transaction(path, type, id){
	prepare(path, type, id);
}

function edit_shipdate(path, shipdate, start_row){
	window.open(path + "/jsp/pop/editShipDatePopup.jsp?sd="+shipdate+"&srow="+start_row, "Edit Shipping Date.", "width=400,height=260,location=No,resizable=No");
}

function put_new_shipdate(new_ship_date, start_row){
	var diff_day = eval(document.getElementsByName('order.roundTrip')[0].value);
	var round=''; 
	var day;
	
	//alert('new_ship_date--->'+new_ship_date);
	var d = new Date();
	
	var year = new_ship_date.split('/')[2];
	var month = new_ship_date.split('/')[1];
	var date = new_ship_date.split('/')[0];
	
	//d.setFullYear(2011, 11-1, 09);
	d.setFullYear(year-543, month-1, date);
	//alert('d--->'+d);
	//alert('d--->'+d.getDay);
	
	var shippingDay = document.getElementsByName('order.shippingDay')[0].value;
	//alert('shippingDay--->'+shippingDay);
	
	if(shippingDay ==  'Mon'){
		day = 1;
	}else if(shippingDay ==  'Tue'){
		day = 2;
	}else if(shippingDay ==  'Wed'){
		day = 3;
	}else if(shippingDay ==  'Thu'){
		day = 4;
	}else if(shippingDay ==  'Fri'){
		day = 5;
	}else if(shippingDay ==  'Sat'){
		day = 6;
	}else{
		day = 0;
	}
	
	//alert('day--->'+day);
	//alert('getDay--->'+d.getDay());
	if(d.getDay() != day){
		alert('วันจัดส่งไม่ถูกต้องกับที่กำหนด');
		return false;
	}
	
	
	var tbl = document.getElementById('tblProduct');
	var i;
	for(i=eval(start_row);i<tbl.rows.length;i++){
		if(i==1){round=i+"";}
		if(round!=jQuery.trim(tbl.rows[i].cells[9].innerHTML)){
			new_ship_date = newDayAdd(new_ship_date, calWeekByRoundtrip(diff_day));
			
		}
		tbl.rows[i].cells[10].innerHTML=new_ship_date;
		tbl.rows[i].cells[11].innerHTML = new_ship_date;
		document.getElementsByName('lines.ship')[i-1].value=new_ship_date;
		document.getElementsByName('lines.req')[i-1].value=new_ship_date;
		round=jQuery.trim(tbl.rows[i].cells[9].innerHTML);
	}
}


function calWeekByRoundtrip(diff_day){
	//alert('diff_day1--->'+diff_day);
	var week = diff_day / 7;
	week = (Math.floor(week));
	//alert('week--->'+week);
	var diff_day = week * 7;
	//alert('diff_day2--->'+diff_day);
	
    return diff_day;
}

function newDayAdd(inputDate, addDay){
	var newDay = inputDate.split('/')[1] +'/'+ inputDate.split('/')[0]+'/'+ inputDate.split('/')[2];
    var d = new Date(newDay);  
    d.setDate(d.getDate()+addDay);  
    mkMonth=d.getMonth()+1;  
    mkMonth=new String(mkMonth);  
    if(mkMonth.length==1){  
        mkMonth="0"+mkMonth;  
    }  
    mkDay=d.getDate();  
    mkDay=new String(mkDay);  
    if(mkDay.length==1){  
        mkDay="0"+mkDay;  
    }     
    mkYear=d.getFullYear();  
    return mkDay+"/"+mkMonth+"/"+mkYear;      
}  

function gotoReport(path, role){
	var rand = Math.floor(Math.random()*11);
	var w = 700;
	var h = 280;
	var left =  (screen.width/2)-(w/2);
    var top = (screen.height/2)-(h/2);
	window.open(path + "/jsp/salesspecial/pop/nextVisitPopup.jsp?rand="+rand+"&role="+role, "Print Receipt/Tax Invoice", "width="+w+",height="+h+", top="+top+", left="+left+",location=0,resizable=0");
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
	
	document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=preSave";
	document.orderSpecialForm.submit();
	return true;
}

function backadd(path) {
	if(!createProductList()){return false;}
	document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=backToAdd";
	document.orderSpecialForm.submit();
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
	document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=save";
	document.orderSpecialForm.submit();
	return true;
}

function search(path){
	if(!datedifference(document.getElementById('orderDateFrom'),document.getElementById('orderDateTo'))){return false;}
	document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=search&rf=Y";
	document.orderSpecialForm.submit();
	return true;
}

function clearForm(path){
	document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=clearForm";
	document.orderSpecialForm.submit();
	return true;
}

function backsearch(path,customerId) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.orderSpecialForm.action = path + "/jsp/saleOrderAction.do?do=search";
	} else {
		document.orderSpecialForm.action = path + "/jsp/saleOrderAction.do?do=prepare&customerId="+customerId;
	}
	document.orderSpecialForm.submit();
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

/*function add_product(path, id){
	window.open(path + "/jsp/pop/productPopup.jsp?id=" + id, "Product", "width=700,height=400,location=No,resizable=No");
	return;
}*/

/*function open_product(path, rowNo){
	if(rowNo==null)
		window.open(path + "/jsp/pop/productPopup.jsp", "Product", "width=700,height=450,location=No,resizable=No");
	else
		window.open(path + "/jsp/pop/productPopup.jsp?row="+rowNo, "Product", "width=700,height=450,location=No,resizable=No");
	return;
}*/

function validateItemVatOrNoNotInSameBill(){
	var ids=document.getElementsByName('lines.id');
	var productIds=document.getElementsByName('lines.productId');
	var products=document.getElementsByName('lines.product');
	var productLabels=document.getElementsByName('lines.productLabel');
	var taxables=document.getElementsByName('lines.taxable');
	
	var countItemHavevat = 0;
	var countItemNoVat = 0;
	for(var i=0;i<ids.length;i++){
		if(taxables[i].value =='Y'){
			countItemHavevat++;
		}else{
			countItemNoVat++;
		}
	}
	
	if(countItemHavevat > 0 && countItemNoVat >0 ){
		alert("สินค้ามี VAT และ ไม่มี VAT ห้ามเปิดร่วมกันให้แยกบิลกัน");
		return false;
	}
	return true;
}

function toFixed(num, pre){
	num = Number(num);
	num *= Math.pow(10, pre);
	num = (Math.round(num, pre) + (((num - Math.round(num, pre))>=0.5)?1:0)) / Math.pow(10, pre);
	return num.toFixed(pre);
}


function createAutoReceipt(path,action) {
	//alert("action:"+action);
	document.orderSpecialForm.action = path + "/jsp/saleOrderSpecialAction.do?do=createAutoReceipt&actionSave="+action;
	document.orderSpecialForm.submit();
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

