
function prepare(path,type,id){
	if(id!=null){
		document.orderForm.action = path + "/jsp/saleOrderAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.orderForm.action = path + "/jsp/saleOrderAction.do?do=prepare"+"&action="+type;
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

function openCancelOrderPopup(path,orderNo,id,payment){
	window.open(path + "/jsp/pop/cancelOrderPopup.jsp?id="+id+"&orderNo="+orderNo+"&payment="+payment, "Edit Shipping Date.", "width=400,height=260,location=No,resizable=No");
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
	window.open(path + "/jsp/pop/nextVisitPopup.jsp?rand="+rand+"&role="+role, "Print Receipt/Tax Invoice", "width="+w+",height="+h+", top="+top+", left="+left+",location=0,resizable=0");
}


function backadd(path) {
	if(!createProductList()){return false;}
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=backToAdd";
	document.orderForm.submit();
	return true;
}

function save(path,role) {
	//Case Payment By Creditcard Validate
	var paymentMethod = document.getElementById('paymentMethod');
	if("CR" ==paymentMethod.value){
		var creditCardNo = document.getElementById('creditCardNo');
		var creditCardExpireDate = document.getElementById('creditCardExpireDate');
		if(creditCardNo.value != "" && creditCardNo.value.length <16){
			alert("กรูณาระบุ รหัสบัตรเครดิตให้ถูกต้อง");
			creditCardNo.focus();
			return false;
		}
		if(creditcardMonthExpire.value == "" || creditcardYearExpire.value == ""){
			alert("กรูณาระบุ วันที่หมดอายุ บัตรเครดิตให้ถูกต้อง");
			if(creditcardMonthExpire.value == "" )creditcardMonthExpire.focus();
			if(creditcardYearExpire.value == "" )creditcardYearExpire.focus();
			return false;
		}
	}
	
	if(!createProductList()){return false;}
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=save";
	document.orderForm.submit();
	return true;
}

function search(path){
	if(!datedifference(document.getElementById('orderDateFrom'),document.getElementById('orderDateTo'))){return false;}
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=search&rf=Y";
	document.orderForm.submit();
	return true;
}

function clearForm(path){
	document.orderForm.action = path + "/jsp/saleOrderAction.do?do=search&action=new";
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

function add_product(path, id){
	window.open(path + "/jsp/pop/productPopup.jsp?id=" + id, "Product", "width=700,height=400,location=No,resizable=No");
	return;
}

function open_product(path, rowNo){
	if(rowNo==null)
		window.open(path + "/jsp/pop/productPopup.jsp", "Product", "width=700,height=450,location=No,resizable=No");
	else
		window.open(path + "/jsp/pop/productPopup.jsp?row="+rowNo, "Product", "width=700,height=450,location=No,resizable=No");
	return;
}
function toFixed(num, pre){
	num = Number(num);
	num *= Math.pow(10, pre);
	num = (Math.round(num, pre) + (((num - Math.round(num, pre))>=0.5)?1:0)) / Math.pow(10, pre);
	return num.toFixed(pre);
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
