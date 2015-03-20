function search(path){
	var shipDate = document.getElementById("shipDate");
	var memberCode = document.getElementById("memberCode");
	
	if(memberCode.value=='' && shipDate.value==''){
		alert("กำหนดเงื่อนไขอย่างน้อย 1 อย่าง");
		shipDate.focus();
		return false;
	}

	document.shipmentForm.action = path + "/jsp/shipmentAction.do?do=search&rf=Y";
	document.shipmentForm.submit();
	
	return true;
}

function searchKeypress(e,path){
	if(e != null && e.keyCode == 13){
		search(path);
	}
}

function searchCancelKeypress(e,path){
	if(e != null && e.keyCode == 13){
		searchCancel(path);
	}
}

function nextKeypress(e,path){
	if(e != null && e.keyCode == 13){
		$('#memberCode').focus();
	}
}

function validate(input,type , idx){
	if(type='qty'){
		var msg = checkConfirmQty(input ,idx);
		if(msg!=null){
			alert(msg);
			input.focus();
			return false;
		}
	}
	
	return true;
}

function inputConfirmQty(input,type , idx){
	if(type='qty'){
		var msg = checkConfirmQty(input ,idx);
		if(msg!=null){
			alert(msg);
			input.focus();
			return false;
		}
	}
	
	//calculateSummary(idx,document.getElementsByName("confirms["+idx+"].isCancel")[0]);
	
	return true;
}

function calculateSummary(){
	//if(!checkIsConfirm(idx,chk)){
	//	return false;
	//}
	
	document.getElementById("progressbar").style.visibility = 'visible';
	
	var stawQty = 0;
	var mixedQty = 0;
	var orangeQty = 0;
	var totalQty = 0;
	var totalAmt = 0;
	
	var pCodes = document.getElementsByName("productCode");
	for(var i=0; i < pCodes.length ; i ++ ){
		var isConfirm = document.getElementsByName("confirms["+i+"].isConfirm")[0];
		if(isConfirm.checked){
			var confirmQty = document.getElementsByName("confirms["+i+"].confirmQty")[0];
			var amount = document.getElementsByName("confirms["+i+"].orderLine.needBill");
			var isNeedToCalculate = document.getElementsByName("confirms["+i+"].isNeedToCalculate")[0].value;
			if(amount.length >0){
				if("Y"==isNeedToCalculate)
					totalAmt +=Number(amount[0].value);
			}
			//alert(pCodes[i].value+" "+confirmQty.value);
			if(pCodes[i].value == '302010'){
				stawQty+=Number(confirmQty.value);
			}
			else if(pCodes[i].value == '302011'){
				mixedQty+=Number(confirmQty.value);
			}
			else if(pCodes[i].value == '302009'){
				orangeQty+=Number(confirmQty.value);
			}
			totalQty +=Number(confirmQty.value);
		}
	}
	
	document.getElementById("orageTotalQty").value = orangeQty;
	document.getElementById("stawberryTotalQty").value = stawQty;
	document.getElementById("mixberryTotalQty").value = mixedQty;
	document.getElementById("totalQty").value = totalQty;
	document.getElementById("totalAmount").value = totalAmt;
	
	document.getElementById("progressbar").style.visibility = 'hidden'; 
}

function calculateCancelSummary(){
	
	var stawQty = 0;
	var mixedQty = 0;
	var orangeQty = 0;
	var totalQty = 0;
	
	var pCodes = document.getElementsByName("productCode");
	for(var i=0; i < pCodes.length ; i ++ ){
		var isCancel = document.getElementsByName("confirms["+i+"].isCancel")[0];
		if(!isCancel.checked){
			var confirmQty = document.getElementsByName("confirms["+i+"].confirmQty")[0];
			//alert(pCodes[i].value+" "+confirmQty.value);
			if(pCodes[i].value == '302010'){
				stawQty+=Number(confirmQty.value);
			}
			else if(pCodes[i].value == '302011'){
				mixedQty+=Number(confirmQty.value);
			}
			else if(pCodes[i].value == '302009'){
				orangeQty+=Number(confirmQty.value);
			}
			totalQty +=Number(confirmQty.value);
		}
	}
	
	document.getElementById("orageTotalQty").value = orangeQty;
	document.getElementById("stawberryTotalQty").value = stawQty;
	document.getElementById("mixberryTotalQty").value = mixedQty;
	document.getElementById("totalQty").value = totalQty;
}

function checkConfirmQty(input,idx){
	if(trim(input.value) == '' || input.value == null){
		return "ไม่สามารถกำหนด จำนวนขวดที่จัดส่งได้ เป็นค่าว่าง";
	}
	
	if( parseInt(input.value) < 0){
		return "ไม่สามารถกำหนด จำนวนขวดที่จัดส่งได้ น้อยกว่า 0 ";
	}
	
	if(!isInteger(input.value)){
		return "กรุณากำหนด จำนวนขวดที่จัดส่งได้ เป็นจำนวนเต็ม";
	}
	
	var orderLineQty = document.getElementsByName("orderLineQty")[idx].value;
	if(parseInt(input.value) > parseInt(orderLineQty)){
		return "ไม่สามารถกำหนด จำนวนขวดที่จัดส่งได้ มากกว่า จำนวนขวดที่แผน ";
	}
	
	return null;
}

function prepare(path,type,id){
	if(id!=null){
		document.shipmentForm.action = path + "/jsp/shipmentAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.shipmentForm.action = path + "/jsp/shipmentAction.do?do=prepare"+"&action="+type;
	}
	document.shipmentForm.submit();
	return true;
}

function clearForm(path){
	document.shipmentForm.action = path + "/jsp/shipmentAction.do?do=clearForm";
	document.shipmentForm.submit();
	return true;
}

function save(path,items) {	
	document.shipmentForm.action = path + "/jsp/shipmentAction.do?do=save";
	document.shipmentForm.submit();
}

function saveCancel(path,items) {	
	//alert("xx:"+items);
	var isChecked = false;
	for(var i=0;i < items ; i++){
		isChecked = document.getElementById('confirms'+i+'.isCancel').checked;
		if(isChecked)
			break;
	}
	
	if(!isChecked){
		alert("กรุณาเลือกรายการที่ต้องการรับชำระ");
		return false;
	}else{
	  document.shipmentForm.action = path + "/jsp/shipmentAction.do?do=saveCancel";
	  document.shipmentForm.submit();
	}
}

function clearCancelForm(path){
	document.shipmentForm.action = path + "/jsp/shipmentAction.do?do=clearCancelForm";
	document.shipmentForm.submit();
	return true;
}

function searchCancel(path){
	var shipDate = document.getElementById("shipDate");
	var memberCode = document.getElementById("memberCode");
	
	if(memberCode.value=='' && shipDate.value==''){
		alert("กำหนดเงื่อนไขอย่างน้อย 1 อย่าง");
		shipDate.focus();
		return false;
	}

	document.shipmentForm.action = path + "/jsp/shipmentAction.do?do=searchCancel&rf=Y";
	document.shipmentForm.submit();
	
	return true;
}

function saveConfirmLine(path,idx){
	var lineId = document.getElementsByName("confirms["+idx+"].lineId")[0].value;
	var isCancel = document.getElementsByName("confirms["+idx+"].isCancel")[0];
	
	var isCancelValue = "N";
	if(isCancel.checked)
		isCancelValue = "Y";

	var confirmDate = document.getElementsByName("confirms["+idx+"].orderLine.confirmShipDate")[0].value;
	
	if(confirm("ท่านต้องการแก้ไขข้อมูลใช่หรือไม่")){
		$(function(){
			var getData = $.ajax({
				url: path+"/jsp/shipment/cancelShipLineSave.jsp",
				data : "lineId=" + lineId
				+"&isCancel="+isCancelValue
				+"&confirmDate="+confirmDate,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					//alert(returnString);
					searchCancel(path);
				}
			}).responseText;
		});
	}
}

function checkIsConfirm(idx,chk,name){
	var isPostpone = document.getElementsByName("confirms["+idx+"].isPostponeShipDate")[0];
	var isConfirm = document.getElementsByName("confirms["+idx+"].isConfirm")[0];
	var isPostponeReqDate = document.getElementsByName("confirms["+idx+"].isPostponeReqDate")[0];
	var isReSchedule = document.getElementsByName("confirms["+idx+"].isReSchedule")[0];
	
	if("ISPOSTPONE" == name){
		if(isPostpone.checked){
			isConfirm.checked = false;
			isPostponeReqDate.checked = false;
			isReSchedule.checked = false;
			document.getElementsByName("confirms["+idx+"].confirmQty")[0].readOnly = true;
		}
		
		checkIsPostponeDate(idx);
	}
	else if("ISPOSTPONEREQDATE" == name){
		if(isPostponeReqDate.checked){
			isPostpone.checked = false;
			isConfirm.checked = false;
			isReSchedule.checked = false;
			document.getElementsByName("confirms["+idx+"].confirmQty")[0].readOnly = true;
		}
		
		checkIsPostponeReqDate(idx);
	}
	else if("ISCONFIRM" == name){
		if(isConfirm.checked){
			isPostpone.checked = false;
			isPostponeReqDate.checked = false;
			isReSchedule.checked = false;
			document.getElementsByName("confirms["+idx+"].confirmQty")[0].readOnly = false;
		}
		
		checkConfirm(idx);
	}
	else if("ISRESCHEDULE" == name){
		if(isReSchedule.checked){
			isPostpone.checked = false;
			isPostponeReqDate.checked = false;
			isConfirm.checked = false;
			document.getElementsByName("confirms["+idx+"].confirmQty")[0].readOnly = true;
		}
		
		checkReSchedule(idx);
	}
	
	if(isPostpone.checked && isConfirm.checked){
		alert("ไม่สามารถกำหนด ส่งของ พร้อมเลื่อนวันส่งได้");
		chk.checked = false ;
		chk.focus();
		return false;
	}
	
	if(isPostpone.checked || isPostponeReqDate.checked || isReSchedule.checked){
		$("span#shipDateTxt"+idx).hide();
		$("span#shipDateInput"+idx).show();
	}
	else{
		$("span#shipDateTxt"+idx).show();
		$("span#shipDateInput"+idx).hide();
	}
	
	return true;
}

function checkReSchedule(idx){
	// Get Current Value
	var orderNo = document.getElementsByName("confirms["+idx+"].orderNo")[0].value;
	var tripNo =  document.getElementsByName("confirms["+idx+"].tripNo")[0].value;
	var totalRows = document.getElementById("totalRows").value;
	
	var shipmentDate =  document.getElementsByName("confirms["+idx+"].postponeDate")[0].value;
	var isReSchedule =  document.getElementsByName("confirms["+idx+"].isReSchedule")[0].checked;
	
	//check next
	var n = 1;
	var next = true;
	while(next){
		var no = idx+n;
		if(no < totalRows){
			var nOrderNo = document.getElementsByName("confirms["+no+"].orderNo")[0].value;
			var nTripNo = document.getElementsByName("confirms["+no+"].tripNo")[0].value;
			if(nOrderNo == orderNo && nTripNo == tripNo){
				document.getElementsByName("confirms["+(no)+"].postponeDate")[0].value = shipmentDate;
				document.getElementsByName("confirms["+(no)+"].isReSchedule")[0].checked = isReSchedule;
				n++;
				if(isReSchedule){
					$("span#shipDateTxt"+no).hide();
					$("span#shipDateInput"+no).show();
					
					// Remove Other Flag
					document.getElementsByName("confirms["+no+"].isPostponeShipDate")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isConfirm")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeReqDate")[0].checked = false;
				}
				else{
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
				}
			}
			else{
				next = false;
			}
		}
		else{
			next = false;
		}
	}
	
	
	//check previous
	n = 1;
	var previous = true;
	while(previous){
		var no = idx-n;
		if(no>=0){
			var nOrderNo = document.getElementsByName("confirms["+no+"].orderNo")[0].value;
			var nTripNo = document.getElementsByName("confirms["+no+"].tripNo")[0].value;
			if(nOrderNo == orderNo && nTripNo == tripNo){
				document.getElementsByName("confirms["+no+"].postponeDate")[0].value = shipmentDate;
				document.getElementsByName("confirms["+no+"].isReSchedule")[0].checked = isReSchedule;
				n++;
				if(isReSchedule){
					$("span#shipDateTxt"+no).hide();
					$("span#shipDateInput"+no).show();
					
					// Remove Other Flag
					document.getElementsByName("confirms["+no+"].isPostponeShipDate")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isConfirm")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeReqDate")[0].checked = false;
				}
				else{
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
				}
			}
			else{
				previous = false;
			}
		}
		else{
			previous = false;
		}
	}
}


function checkIsPostponeDate(idx){
	// Get Current Value
	var orderNo = document.getElementsByName("confirms["+idx+"].orderNo")[0].value;
	var tripNo =  document.getElementsByName("confirms["+idx+"].tripNo")[0].value;
	var totalRows = document.getElementById("totalRows").value;
	
	var shipmentDate =  document.getElementsByName("confirms["+idx+"].postponeDate")[0].value;
	var isPostponeShipDate =  document.getElementsByName("confirms["+idx+"].isPostponeShipDate")[0].checked;
	
	//check next
	var n = 1;
	var next = true;
	while(next){
		var no = idx+n;
		if(no < totalRows){
			var nOrderNo = document.getElementsByName("confirms["+no+"].orderNo")[0].value;
			var nTripNo = document.getElementsByName("confirms["+no+"].tripNo")[0].value;
			if(nOrderNo == orderNo && nTripNo == tripNo){
				document.getElementsByName("confirms["+(no)+"].postponeDate")[0].value = shipmentDate;
				document.getElementsByName("confirms["+(no)+"].isPostponeShipDate")[0].checked = isPostponeShipDate;
				n++;
				if(isPostponeShipDate){
					$("span#shipDateTxt"+no).hide();
					$("span#shipDateInput"+no).show();
					
					// Remove Other Flag
					document.getElementsByName("confirms["+no+"].isReSchedule")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isConfirm")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeReqDate")[0].checked = false;
				}
				else{
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
				}
			}
			else{
				next = false;
			}
		}
		else{
			next = false;
		}
	}
	
	
	//check previous
	n = 1;
	var previous = true;
	while(previous){
		var no = idx-n;
		if(no>=0){
			var nOrderNo = document.getElementsByName("confirms["+no+"].orderNo")[0].value;
			var nTripNo = document.getElementsByName("confirms["+no+"].tripNo")[0].value;
			if(nOrderNo == orderNo && nTripNo == tripNo){
				document.getElementsByName("confirms["+no+"].postponeDate")[0].value = shipmentDate;
				document.getElementsByName("confirms["+no+"].isPostponeShipDate")[0].checked = isPostponeShipDate;
				n++;
				if(isPostponeShipDate){
					$("span#shipDateTxt"+no).hide();
					$("span#shipDateInput"+no).show();
					
					// Remove Other Flag
					document.getElementsByName("confirms["+no+"].isReSchedule")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isConfirm")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeReqDate")[0].checked = false;
				}
				else{
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
				}
			}
			else{
				previous = false;
			}
		}
		else{
			previous = false;
		}
	}
}

function checkIsPostponeReqDate(idx){
	// Get Current Value
	var orderNo = document.getElementsByName("confirms["+idx+"].orderNo")[0].value;
	var tripNo =  document.getElementsByName("confirms["+idx+"].tripNo")[0].value;
	
	var totalRows = document.getElementById("totalRows").value;
	var shipmentDate =  document.getElementsByName("confirms["+idx+"].postponeDate")[0].value;
	var isPostponeReqDate =  document.getElementsByName("confirms["+idx+"].isPostponeReqDate")[0].checked;
	
	//check next
	var n = 1;
	var next = true;
	while(next){
		var no = idx+n;
		if(no < totalRows){
			var nOrderNo = document.getElementsByName("confirms["+no+"].orderNo")[0].value;
			var nTripNo = document.getElementsByName("confirms["+no+"].tripNo")[0].value;
			if(nOrderNo == orderNo && nTripNo == tripNo){
				document.getElementsByName("confirms["+(no)+"].postponeDate")[0].value = shipmentDate;
				document.getElementsByName("confirms["+(no)+"].isPostponeReqDate")[0].checked = isPostponeReqDate;
				n++;
				if(isPostponeReqDate){
					$("span#shipDateTxt"+no).hide();
					$("span#shipDateInput"+no).show();
					
					// Remove Other Flag
					document.getElementsByName("confirms["+no+"].isReSchedule")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isConfirm")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeShipDate")[0].checked = false;
				}
				else{
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
				}
			}
			else{
				next = false;
			}
		}
		else{
			next = false;
		}
	}
	
	
	//check previous
	n = 1;
	var previous = true;
	while(previous){
		var no = idx-n;
		if(no>=0){
			var nOrderNo = document.getElementsByName("confirms["+no+"].orderNo")[0].value;
			var nTripNo = document.getElementsByName("confirms["+no+"].tripNo")[0].value;
			
			if(nOrderNo == orderNo && nTripNo == tripNo){
				document.getElementsByName("confirms["+no+"].postponeDate")[0].value = shipmentDate;
				document.getElementsByName("confirms["+no+"].isPostponeReqDate")[0].checked = isPostponeReqDate;
				n++;
				if(isPostponeReqDate){
					$("span#shipDateTxt"+no).hide();
					$("span#shipDateInput"+no).show();
					
					// Remove Other Flag
					document.getElementsByName("confirms["+no+"].isReSchedule")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isConfirm")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeShipDate")[0].checked = false;
				}
				else{
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
				}
			}
			else{
				previous = false;
			}
		}
		else{
			previous = false;
		}
	}
}

function checkConfirm(idx){
	// Get Current Value
	var orderNo = document.getElementsByName("confirms["+idx+"].orderNo")[0].value;
	var tripNo =  document.getElementsByName("confirms["+idx+"].tripNo")[0].value;
	
	var totalRows = document.getElementById("totalRows").value;
	var isConfirm =  document.getElementsByName("confirms["+idx+"].isConfirm")[0].checked;
	
	//check next
	var n = 1;
	var next = true;
	while(next){
		var no = idx+n;
		if(no < totalRows){
			var nOrderNo = document.getElementsByName("confirms["+no+"].orderNo")[0].value;
			var nTripNo = document.getElementsByName("confirms["+no+"].tripNo")[0].value;
			if(nOrderNo == orderNo && nTripNo == tripNo){
				//document.getElementsByName("confirms["+(no)+"].postponeDate")[0].value = shipmentDate;
				document.getElementsByName("confirms["+(no)+"].isConfirm")[0].checked = isConfirm;
				n++;
				if(isConfirm){
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
					
					// Remove Other Flag
					document.getElementsByName("confirms["+no+"].isReSchedule")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeReqDate")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeShipDate")[0].checked = false;
				}else{
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
				}
			}
			else{
				next = false;
			}
		}
		else{
			next = false;
		}
	}
	
	
	//check previous
	n = 1;
	var previous = true;
	while(previous){
		var no = idx-n;
		if(no>=0){
			var nOrderNo = document.getElementsByName("confirms["+no+"].orderNo")[0].value;
			var nTripNo = document.getElementsByName("confirms["+no+"].tripNo")[0].value;
			if(nOrderNo == orderNo && nTripNo == tripNo){
				//document.getElementsByName("confirms["+no+"].postponeDate")[0].value = shipmentDate;
				document.getElementsByName("confirms["+no+"].isConfirm")[0].checked = isConfirm;
				n++;
				if(isConfirm){
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
					
					// Remove Other Flag
					document.getElementsByName("confirms["+no+"].isReSchedule")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeReqDate")[0].checked = false;
					document.getElementsByName("confirms["+no+"].isPostponeShipDate")[0].checked = false;
				}
				else{
					$("span#shipDateTxt"+no).show();
					$("span#shipDateInput"+no).hide();
				}
			}
			else{
				previous = false;
			}
		}
		else{
			previous = false;
		}
	}
	
}

function checkedCancelGroup(idx){
	// Get Current Value
	//alert(idx);
	var orderNo = document.getElementsByName("confirms["+idx+"].orderNo")[0].value;
	var tripNo = document.getElementsByName("confirms["+idx+"].tripNo")[0].value;
	var totalRows = document.getElementById("totalRows").value;
	
	var isCancel =  document.getElementsByName("confirms["+idx+"].isCancel")[0].checked;
	
	//check next
	for(var n=0;n<totalRows;n++){
		var nOrderNo = document.getElementsByName("confirms["+n+"].orderNo")[0].value;
		var nTripNo = document.getElementsByName("confirms["+n+"].tripNo")[0].value;
		if(nOrderNo == orderNo && nTripNo == tripNo){
			//alert("orderNo["+orderNo+"]nOrderNo["+nOrderNo+"]nTripNo["+nTripNo+"]tripNo["+tripNo+"]");
			document.getElementsByName("confirms["+(n)+"].isCancel")[0].checked = isCancel;
		}
	}//for
}

function validateNeedBill(obj,needBillPlan){
	//alert("editNeedBill["+obj.value+"],needBillPlan["+needBillPlan+"]");
	if(obj.value != '' && obj.value != '0.0' && obj.value != '0' ){
	   var editNeedBill = parseFloat(obj.value);
	   var needBillPlanAmt = parseFloat(needBillPlan);
	   if(editNeedBill != needBillPlanAmt && editNeedBill != 0){
		   alert("ยอดชำระไม่ตรงกับที่ระบบได้วางแผนไว้ ("+needBillPlan+") สามารถใส่เป็น 0 ได้");
		   obj.value = needBillPlan;//default
		   obj.focus();
		   return false;  
	   }
	}
	return true;
}