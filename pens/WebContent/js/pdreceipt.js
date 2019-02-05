function search(path){
	document.pdReceiptForm.action = path + "/jsp/pdReceipt.do?do=search";
	document.pdReceiptForm.submit();
	return true;
}

function clearForm(path){
	document.pdReceiptForm.action = path + "/jsp/pdReceipt.do?do=clearForm";
	document.pdReceiptForm.submit();
	return true;
}

function defaultDate(paymentMethod,row,currDate){
	if(paymentMethod.value!=null && paymentMethod.value.length>0){
		if(document.getElementsByName("pdReceiptDate")[eval(row-1)].value == null || document.getElementsByName("pdReceiptDate")[eval(row-1)].value.length ==0){
			document.getElementsByName("pdReceiptDate")[eval(row-1)].value=currDate;
		}
		//Case Cheque enable chequeDate
		//alert(paymentMethod.value+",row["+row+"]");
		if(paymentMethod.value =='CH'){
			document.getElementsByName("chequeDate")[eval(row-1)].disabled=false;
		}else{
			document.getElementsByName("chequeDate")[eval(row-1)].disabled=true;
			document.getElementsByName("chequeDate")[eval(row-1)].value='';
		}
	}else{
		document.getElementsByName("pdReceiptDate")[eval(row-1)].value='';
    }
	
	
}

function save(path){
	if(!confirm("ยืนยันบันทึกข้อมูล")){
		return false;
	}
	var p_receiptId = "ids=";
	var p_paymentMethods ="pms=";
	var p_pdPaidDate ="pdates=";
	var p_chequeDate ="pchequeDates=";
	
	var receiptIds = document.getElementsByName("receiptId");
	var paymentMethods = document.getElementsByName("pd.paymentMethod");
	var paidDates = document.getElementsByName("pdReceiptDate");
	var chequeDates = document.getElementsByName("chequeDate");
	
	var isFirst = true;
	var noValueSelect = true;
	for(var i=0;i<paymentMethods.length;i++){
		if(paymentMethods[i].value != null && paymentMethods[i].value.length > 0){
			if(paidDates[i].value == null || trim(paidDates[i].value).length == 0 ){
				alert("กรุณาระบุ วันที่ส่งเงิน");
				paidDates[i].focus();
				return false;
			}
			if(paymentMethods[i].value == 'CH'){
				if(chequeDates[i].value == null || trim(chequeDates[i].value).length == 0 ){
					alert("กรุณาระบุ วันที่หน้าเช็ค");
					chequeDates[i].focus();
					return false;
				}
			}
			
			if(isFirst){
				p_receiptId = p_receiptId+receiptIds[i].value;
				p_paymentMethods = p_paymentMethods+paymentMethods[i].value;
				p_pdPaidDate = p_pdPaidDate+paidDates[i].value;
				p_chequeDate = p_chequeDate+chequeDates[i].value;
				
				isFirst = false;
				noValueSelect = false;
			}else{
				p_receiptId = p_receiptId+","+receiptIds[i].value;
				p_paymentMethods = p_paymentMethods+","+paymentMethods[i].value;
				p_pdPaidDate = p_pdPaidDate+","+paidDates[i].value;
				p_chequeDate = p_chequeDate+","+chequeDates[i].value;
			}
		}
		else{
			if(paidDates[i].value != null && paidDates[i].value.length > 0){
				alert("กรุณาระบุ วิธีการชำระเงิน");
				paymentMethods[i].focus();
				return false;
			}
		}
	}
	
	if(noValueSelect){
		alert("ไม่มีข้อมูลกำหนด");
		return false;
	}
	
	document.pdReceiptForm.action = path + "/jsp/pdReceipt.do?do=save&"+p_receiptId+"&"+p_paymentMethods+"&"+p_pdPaidDate+"&"+p_chequeDate;
	document.pdReceiptForm.submit();
	return true;
}

