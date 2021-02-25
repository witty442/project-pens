
function cancelStockDiscount(path){
	//if(confirm("ยืนยันการ ยกเลิกรายการ")){
		//var input= confirmInputReason();
		//if(input){
			//document.getElementsByName('bean.description')[0].value = input;
		    document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=cancelStockReturn";
		    document.stockDiscountForm.submit();
		    return true;
		//}
	//}
	//return false;
}
function confirmInputReason(){
	var desc= prompt("กรุณาใส่เหตุผลในการยกเลิก","");
	if(desc == '') {
		confirmInputReason();
	}
	return desc;
}

/** onchange vatRate **/
function reCalcChangeVatRate(){
	/** Case change VatRate  recalc row new **/
	var productName = document.getElementsByName("productName");
	var status = document.getElementsByName("status");
	var index= 0;
	for(var i=0;i<productName.length;i++){
		index++;
		if(productName[i].value !="" && status[i].value != "DELETE"){
			sumTotalInRow(index,"AMOUNT");
		}
	}
}

function save(path,moveOrderType){
	if(checkTableCanSave()){
		
		var reason = document.getElementById("description");
		if(reason.value.length <10){
			alert("กรุณาระบุ เหตุผลการให้ส่วนลดร้านค้า  อย่าน้อย 10 ตัวอักษร");
			reason.focus();
			return false;
		}
		
		
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=save";
		document.stockDiscountForm.submit();
		return true;
	}
	return false;
}
function canAddNewRow(){
	var maxRow =12;
	var countRow = 0;
	var itemCode = document.getElementsByName("productCode");
	var status = document.getElementsByName("status");
	for(var i=0;i<itemCode.length;i++){
		 if(status[i].value !='DELETE'){
			 countRow++; 
		 }
	}//for
	if(countRow < maxRow){
		return true;
	}
	return false;
}
function checkTableCanSave(){
	var productCode = document.getElementsByName("productCode");
	var productName = document.getElementsByName("productName");
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	var remainPriQty = document.getElementsByName("remainPriQty");
	var uom1Qty = document.getElementsByName("uom1Qty");
	var uom2Qty = document.getElementsByName("uom2Qty");
	var uom2 = document.getElementsByName("uom2");
	var uom1Pac = document.getElementsByName("uom1Pac");
	var uom2Pac = document.getElementsByName("uom2Pac");
	var uom1Price = document.getElementsByName("uom1Price");
	var lineAmount = document.getElementsByName("lineAmount");
	
	var status = document.getElementsByName("status");
	var docType = document.getElementById("docType").value;
	var r = true;
	var error = false;
	var rowSelectOne = false;
	
	for(var i=0;i<productCode.length;i++){
	 // alert(itemCode[i].value+":"+status[i].value+":"+arInvoiceNo[i].value);
	   if( ( productCode[i].value !='' || productName[i].value !='') 
		    && status[i].value !='DELETE'){
		   
		   if( docType =="KEY_PRODUCT"){
		      productCode[i].className ='enableNumber';
	          uom1Qty[i].className ='enableNumber';
	          uom2Qty[i].className ='enableNumber';
		   }
		   
	      arInvoiceNo[i].className ='normalTextInput';
	      lineAmount[i].className ='enableNumber';
	      
		  rowSelectOne = true;
		  
		 // alert("["+qty[i].value+"]["+sub[i].value+"]["+expireDate[i].value+"]");
		  if( arInvoiceNo[i].value =='' ){
			  arInvoiceNo[i].className ='errorTextInput';
			  error = true;
		  }
		  if( lineAmount[i].value =='' ){
			  lineAmount[i].className ='errorNumberInput';
			  error = true;
		  }
		  if( docType =="KEY_PRODUCT"){
			  if(uom1Qty[i].value =='' && uom2Qty[i].value==''){
				  uom1Qty[i].className ='errorNumberInput';
			      uom2Qty[i].className ='errorNumberInput';
			      error = true;
			  }
		  }
		}//if
	}//for
	
	if(rowSelectOne==false){
		alert("กรุณาระบุข้อมูล อย่างน้อย  หนึ่งแถว");
		return false;
	}
	if(error==true){
		alert("กรุณาระบุข้อมูล ให้ครบถ้วน");
		r= false;
	 }
	
	return r;
}

function clearForm(path){
	document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=clear";
	document.stockDiscountForm.submit();
	return true;
}

function escapeParameter(param){
	return param.replace("%","%25");
}

function checkProductOnblur(e,itemCodeObj,rowId){
	 //alert("ONBLUR");
	//alert("rowId:"+rowId);
	var productName = document.getElementsByName("productName");
	if(productName[rowId-1].value ==''){
		itemCodeObj.value ='';
		itemCodeObj.focus();
	}
}

function sumTotalInRow(index,calcType){
	var vatRate = document.getElementById("vatRate");
	var pcsQty = 0;
	var priQty = document.getElementsByName("priQty");
	var remainPriAllQty = document.getElementsByName("remainPriAllQty");
	var uom1Qty = document.getElementsByName("uom1Qty");
	var uom2Qty = document.getElementsByName("uom2Qty");
	var uom2 = document.getElementsByName("uom2");
	var uom1ConvRate = document.getElementsByName("uom1ConvRate");
	var uom2ConvRate = document.getElementsByName("uom2ConvRate");
	var uom1Price = document.getElementsByName("uom1Price");
	var lineAmount = document.getElementsByName("lineAmount");
	var vatAmount = document.getElementsByName("vatAmount");
	var netAmount = document.getElementsByName("netAmount");
	
	var productCode = document.getElementsByName("productCode");
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	var remainAmount = document.getElementsByName("remainAmount");
	//alert("calcType:"+calcType);
	if(productCode[index-1].value != '' && arInvoiceNo[index-1].value != ''){
		
		//Validate CtnQty,subQty
	   if(calcType=="CTN" && (uom1Qty[index-1].value != '' || uom2Qty[index-1].value != '')) {
			//convert uom2Qty to Pri_qty
			if(uom2Qty[index-1].value != '' && convetTxtObjToFloat(uom2ConvRate[index-1]) > 0){
				var qty2Temp = (convetTxtObjToFloat(uom2ConvRate[index-1])/convetTxtObjToFloat(uom1ConvRate[index-1]))*convetTxtObjToFloat(uom2Qty[index-1]);
				console.log("qty2Temp:"+qty2Temp);
				pcsQty = parseFloat(qty2Temp);
				console.log("pcsQty:"+pcsQty);
				//convert to 5 digit
				pcsQty = toFixed(pcsQty,5);
				console.log("pcsQty toFixed5:"+pcsQty);
			}
			//alert("["+uom1Qty[index-1].value+"]:["+pcsQty+"]");
			console.log("uom1Qty[index-1]:"+convetTxtObjToFloat(uom1Qty[index-1]));
			var priQtyTemp = convetTxtObjToFloat(uom1Qty[index-1]) + parseFloat(pcsQty);
			console.log("priQtyTemp:"+priQtyTemp);
			priQty[index-1].value = priQtyTemp;
			
			//compare priQty <= remainPriAllQty 
			if(priQtyTemp > convetTxtObjToFloat(remainPriAllQty[index-1])){
				alert("จำนวนที่คีย์ มากกว่าจำนวนในบิลที่สามารถทำได้ กรุณาตรวจสอบ");
				uom1Qty[index-1].focus();
				uom1Qty[index-1].className ="errorNumber";
				uom2Qty[index-1].className ="errorNumber";
				uom1Qty[index-1].value ="";
				uom2Qty[index-1].value ="";
				priQty[index-1].value = "";
			}else{
				uom1Qty[index-1].className ="enableNumber";
				uom2Qty[index-1].className ="enableNumber";
			}
	   }
	   
	   //calc remainAmount = remainAmount -(lineAmount(same invoiceNo) not same product in page )
		var sumLineAmountByArInvoiceNotCurTemp = sumLineAmountByArInvoiceNotCur(index,arInvoiceNo[index-1].value);
		//alert("sumLineAmountByArInvoiceNotCurTemp:"+sumLineAmountByArInvoiceNotCurTemp);
		remainAmountCalcTemp = convetTxtObjToFloat(remainAmount[index-1])-sumLineAmountByArInvoiceNotCurTemp;
		  
	   //calc Vat 7 of lineAmount
		var vatAmountTemp = convetTxtObjToFloat(lineAmount[index-1]) * (convetTxtObjToFloat(vatRate)/100);
		vatAmount[index-1].value = vatAmountTemp;
		toCurreny(vatAmount[index-1]);
		
		//calc Net Amount = discount +vat (vat 7)
		if(vatRate.value =="7"){
		   netAmount[index-1].value = convetTxtObjToFloat(lineAmount[index-1]) + vatAmountTemp;
		}else{
			//vatRate <> 7  netAmount = discount - vat
			netAmount[index-1].value = convetTxtObjToFloat(lineAmount[index-1]) - vatAmountTemp;
		}
		toCurreny(netAmount[index-1]);
		
		//compare netAmount vs remainAmount in invoiceNo
		if(convetTxtObjToFloat(lineAmount[index-1]) > remainAmountCalcTemp ){
			alert("ยอดเงินรวมเกินยอด Invoice amt คงเหลือในบิลนี้ กรุณาอ้างบิลอื่นใหม");
			lineAmount[index-1].value ="";
			vatAmount[index-1].value ="";
			netAmount[index-1].value = "";
		}
		
		//calc total Amount all row
		sumTotalAllRow();
		
	/** Case productCode is null **/
	}else if(productCode[index-1].value == '' && arInvoiceNo[index-1].value != ''){
	   /** Calc VatAmount of Discount ***/
	   if(calcType=="AMOUNT" && lineAmount[index-1].value != ''){
		   //alert("calcType["+calcType+"]lineAmount:"+lineAmount[index-1].value);
		   
		   //calc remainAmount = remainAmount -(lineAmount(same invoiceNo) not same product in page )
			var sumLineAmountByArInvoiceNotCurTemp = sumLineAmountByArInvoiceNotCur(index,arInvoiceNo[index-1].value);
			//alert("sumLineAmountByArInvoiceNotCurTemp:"+sumLineAmountByArInvoiceNotCurTemp);
			var remainAmountCalcTemp = convetTxtObjToFloat(remainAmount[index-1])-sumLineAmountByArInvoiceNotCurTemp;
			
		   //calc Vat 7 of lineAmount
			var vatAmountTemp = convetTxtObjToFloat(lineAmount[index-1]) * (convetTxtObjToFloat(vatRate)/100);
			vatAmount[index-1].value = vatAmountTemp;
			toCurreny(vatAmount[index-1]);
			
			//calc Net Amount = discount +vat (vat 7)
			if(vatRate.value =="7"){
			   netAmount[index-1].value = convetTxtObjToFloat(lineAmount[index-1]) + vatAmountTemp;
			}else{
				//vatRate <> 7  netAmount = discount - vat
				netAmount[index-1].value = convetTxtObjToFloat(lineAmount[index-1]) - vatAmountTemp;
			}
			toCurreny(netAmount[index-1]);
			
			//compare netAmount vs remainAmount in invoiceNo
			if(convetTxtObjToFloat(lineAmount[index-1]) > remainAmountCalcTemp ){
				alert("ยอดเงินรวมเกินยอด Invoice amt คงเหลือในบิลนี้ กรุณาอ้างบิลอื่นใหม");
				lineAmount[index-1].value ="";
				vatAmount[index-1].value ="";
				netAmount[index-1].value = "";
			}
	   }
	   
	   //calc total Amount all row
	   sumTotalAllRow();
	}else{
		/*if(productCode[index-1].value == ''){
			alert("กรุณาระบุ รหัสสินค้าก่อน ");
			productCode[index-1].className ="errorTextInput";
			productCode[index-1].focus();
			uom1Qty[index-1].value = "";
			uom2Qty[index-1].value = "";
		}else{
			productCode[index-1].className ="normalTextInput";
		}*/
		 
		if(arInvoiceNo[index-1].value == ''){
			alert("กรุณาระบุ เลขที่บิล ก่อน ");
			arInvoiceNo[index-1].className ="errorTextInput";
			arInvoiceNo[index-1].focus();
			uom1Qty[index-1].value = "";
			uom2Qty[index-1].value = "";
		}else{
			arInvoiceNo[index-1].className ="normalTextInput";
		}
	}
}
function sumTotalAllRow(){
	var itemCode = document.getElementsByName("productCode");
	var productName = document.getElementsByName("productName");
	var lineAmount = document.getElementsByName("lineAmount");
	var vatAmount = document.getElementsByName("vatAmount");
	var netAmount = document.getElementsByName("netAmount");
	var status = document.getElementsByName("status");
	
	var lineAmountTemp = 0;
	var vatAmountTemp = 0;
	var netAmountTemp = 0;
	for(var i=0;i<itemCode.length;i++){
	 // alert(itemCode[i].value+":"+status[i].value+":"+arInvoiceNo[i].value);
	   if( (itemCode[i].value !='' || productName[i].value !='') && status[i].value !='DELETE'){
		   lineAmountTemp += convetTxtObjToFloat(lineAmount[i]);
		   vatAmountTemp += convetTxtObjToFloat(vatAmount[i]);
		   netAmountTemp += convetTxtObjToFloat(netAmount[i]);
	   }
	}//for
	document.getElementById("totalLineAmount").value = lineAmountTemp;
	document.getElementById("totalVatAmount").value = vatAmountTemp;
	document.getElementById("totalNetAmount").value = netAmountTemp;
	
	//convert to currency
	toCurreny(document.getElementById("totalLineAmount"));
	toCurreny(document.getElementById("totalVatAmount"));
	toCurreny(document.getElementById("totalNetAmount"));
}

//Check enter only
function  getProductKeypress(e,itemCodeObj,rowId){
//	alert("keypress");
	var productCode = document.getElementsByName("productCode");
	var productName = document.getElementsByName("productName");
	var inventoryItemId = document.getElementsByName("inventoryItemId");
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	var remainPriQty = document.getElementsByName("remainPriQty");
	var uom1Qty = document.getElementsByName("uom1Qty");
	var uom2Qty = document.getElementsByName("uom2Qty");
	var uom2 = document.getElementsByName("uom2");
	var uom1Pac = document.getElementsByName("uom1Pac");
	var uom2Pac = document.getElementsByName("uom2Pac");
	var uom1Price = document.getElementsByName("uom1Price");
	var lineAmount = document.getElementsByName("lineAmount");
	var vatAmount = document.getElementsByName("vatAmount");
	var netAmount = document.getElementsByName("netAmount");
	var uom1ConvRate = document.getElementsByName("uom1ConvRate");
	var uom2ConvRate = document.getElementsByName("uom2ConvRate");
	
	var ENTERKEY =13;
	if(e != null && (e.keyCode == ENTERKEY)){
		if(itemCodeObj.value ==''){
			productName[rowId-1].value = '';
			inventoryItemId[rowId-1].value = '';
			arInvoiceNo[rowId-1].value = '';
			remainPriQty[rowId-1].value = '';
			uom1Qty[rowId-1].value = '';
			uom2Qty[rowId-1].value = '';
			uom2[rowId-1].value = '';
			uom1Pac[rowId-1].value = '';
			uom2Pac[rowId-1].value = '';
			//uom1Price[rowId-1].value = '';
			lineAmount[rowId-1].value = '';
			vatAmount[rowId-1].value = '';
			netAmount[rowId-1].value = '';
			uom1ConvRate[rowId-1].value = '';
			uom2ConvRate[rowId-1].value = '';
		}else{
			if($("#customerCode").val() == ''){
				  alert("กรุณาระบุรหัส ร้านค้าก่อน");
				  $("#customerCode").focus();
				  return false;
			}
			
			var found = getProductModel(itemCodeObj,rowId);
			if(found){
				//alert(found);
				var index = rowId-1;
				//Set Prev row readonly 
				productCode[index].className ="disableText";
				productCode[index].readOnly = true;
				//qty[index].focus();
        		//Add New Row Auto
				addRow(false);	
			}//if
		}//if
	}//if
}

function getProductModel(itemCodeObj,rowId){
	//alert(rowId);
	var found = false;
	var path = document.getElementById("path").value;
	//value
	var productCode = document.getElementsByName("productCode");
	var productName = document.getElementsByName("productName");
	var inventoryItemId = document.getElementsByName("inventoryItemId");
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	var remainPriQty = document.getElementsByName("remainPriQty");
	var uom1Qty = document.getElementsByName("uom1Qty");
	var uom2Qty = document.getElementsByName("uom2Qty");
	var uom2 = document.getElementsByName("uom2");
	var uom1Pac = document.getElementsByName("uom1Pac");
	var uom2Pac = document.getElementsByName("uom2Pac");
	var uom1Price = document.getElementsByName("uom1Price");
	var lineAmount = document.getElementsByName("lineAmount");
	var vatAmount = document.getElementsByName("vatAmount");
	var netAmount = document.getElementsByName("netAmount");
	var uom1ConvRate = document.getElementsByName("uom1ConvRate");
	var uom2ConvRate = document.getElementsByName("uom2ConvRate");
	
	/** validate prodcutCode on same Brand in Doc **/
	if(checkItemIsMoreOneBrand(rowId)){
		itemCodeObj.className ="errorTextInput";
		alert("รหัสสินค้า ไม่ได้อยู่ในแบรนด์เดียวกัน กรุณาตรวจสอบ")  ;
		itemCodeObj.focus();
			
		productName[rowId-1].value = '';
		inventoryItemId[rowId-1].value = '';
		arInvoiceNo[rowId-1].value = '';
		remainPriQty[rowId-1].value = '';
		uom1Qty[rowId-1].value = '';
		uom2Qty[rowId-1].value = '';
		uom2[rowId-1].value = '';
		uom1Pac[rowId-1].value = '';
		uom2Pac[rowId-1].value = '';
		//uom1Price[rowId-1].value = '';
		lineAmount[rowId-1].value = '';
		vatAmount[rowId-1].value = '';
		netAmount[rowId-1].value = '';
		uom1ConvRate[rowId-1].value = '';
		uom2ConvRate[rowId-1].value = '';
		return false;
	}
	
	//pass parameter
	var param  = "productCode=" + itemCodeObj.value+"&requestNumber="+document.getElementById("requestNumber").value;
	var returnString = "";
	var getData = $.ajax({
			url: path+"/jsp/stockDiscount/ajax/getProductStockDiscountQuery.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	    //alert("x:"+returnString);
	    
		if(returnString==''){
			alert("ไม่พบข้อมูลสินค้า  "+itemCodeObj.value);
			itemCodeObj.focus();
			
			productName[rowId-1].value = '';
			inventoryItemId[rowId-1].value = '';
			arInvoiceNo[rowId-1].value = '';
			remainPriQty[rowId-1].value = '';
			uom1Qty[rowId-1].value = '';
			uom2Qty[rowId-1].value = '';
			uom2[rowId-1].value = '';
			uom1Pac[rowId-1].value = '';
			uom2Pac[rowId-1].value = '';
			//uom1Price[rowId-1].value = '';
			lineAmount[rowId-1].value = '';
			vatAmount[rowId-1].value = '';
			netAmount[rowId-1].value = '';
			uom1ConvRate[rowId-1].value = '';
			uom2ConvRate[rowId-1].value = '';
		}else{
			//p.getId()+"|"+p.getName()+"|"+p.getUom2()+"|"+p.getUom1Pac()+"|"+p.getUom2Pac()+"|"+p.getUom1Price();
			//|uom1ConvRate|uom2ConvRate
			found = true;
			var s = returnString.split("|");
			
			inventoryItemId[rowId-1].value = s[0];
			productName[rowId-1].value = s[1];
			uom2[rowId-1].value = s[2];
			uom1Pac[rowId-1].value = s[3];
			uom2Pac[rowId-1].value = s[4];
			//uom1Price[rowId-1].value = s[5];
			uom1ConvRate[rowId-1].value = s[6];
			uom2ConvRate[rowId-1].value = s[7];
			
			productName[rowId-1].readOnly = true;
			productName[rowId-1].className = "disableText";
			
			//disable key detail
			disableInputDetail(rowId);
		}
	return found;
}

function disableInputProduct(rowId){
	if(document.getElementById("docType").value ==""){
	   document.getElementById("docType").value ="KEY_DETAIL";
	}
	
	var productCode = document.getElementsByName("productCode");
	var uom1Qty = document.getElementsByName("uom1Qty");
	var uom2Qty = document.getElementsByName("uom2Qty");
	
	productCode[rowId-1].readOnly = true;
	uom1Qty[rowId-1].readOnly = true;
	uom2Qty[rowId-1].readOnly = true;
	
	productCode[rowId-1].className = "disableText";
	uom1Qty[rowId-1].className = "disableText";
	uom2Qty[rowId-1].className = "disableText";
	
	//alert("loadMe");
	addRow(true);	
}
function disableInputDetail(rowId){
	if(document.getElementById("docType").value ==""){
	   document.getElementById("docType").value ="KEY_PRODUCT";
	}
	
	var productCode = document.getElementsByName("productCode");
	var productName = document.getElementsByName("productName");
	productName[rowId-1].readOnly = true;
	productName[rowId-1].className = "disableText";
}
function nextRowKeypress(e,rowId){
	var TABKEY =9;
	var ENTERKEY =13;
	if(e != null && (e.keyCode == ENTERKEY || e.keyCode == TABKEY)){
		var itemCode = document.getElementsByName("itemCode");
		//alert(itemCode[rowId]);
		if(itemCode[rowId] != null)
		  itemCode[rowId].focus();
	}
}
function removeRow(path){
	//todo play with type
	if(confirm("ยืนยันลบข้อมูล")){
		var tbl = document.getElementById('tblProduct');
		var chk = document.getElementsByName("linechk");
		var status = document.getElementsByName("status");
		var itemCode = document.getElementsByName("productCode");
		var arInvoiceNo = document.getElementsByName("arInvoiceNo");
		var drow;
		var bcheck=false;
		var itemCodeArr ="";
		for(var i=0;i<chk.length;i++){
			if(chk[i].checked){
				drow = tbl.rows[i+1];
				status[i].value ="DELETE";
				$(drow).hide();
				bcheck=true;
				
				//alert(itemCode[i].value);
				itemCodeArr += (itemCode[i].value+"_"+arInvoiceNo[i].value)+"|";
			}
		}
		if(!bcheck){
			alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;
		}else{
			//rearrangNo
			rearrangNo();
			
			//resum Total 
			sumTotalAllRow();
			
			//key Product Or detail 1 type only
			checkDocType();
		}
	}
	return false;
}

function rearrangNo(){
	var itemCode = document.getElementsByName("productCode");
	var no = document.getElementsByName("no");
	var status = document.getElementsByName("status");
    var noCount = 0;
	for(var i=0;i<itemCode.length;i++){
	   if(status[i].value !='DELETE'){
		  noCount++;
		  no[i].value = noCount;
	   }
	}//for
}

function openPopupCustomer(path){
	var form = document.stockDiscountForm;
	
    var param = "";
	var url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	//window.open(encodeURI(url),"",
			//   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	PopupCenterFullHeight(url,"",600);
}

function setStoreMainValue(code,desc,address,customerId){
	var form = document.stockDiscountForm;
	//alert(form);
	
	$("#customerCode").val(code);
	$("#customerName").val(desc);
	$("#customerAddress").val(address);
	$("#customerId").val(customerId);
	
	disableFindCustomer();
} 
/** hide find customer **/
function disableFindCustomer(){
	$("#customerCode").attr('readonly', true);
	document.getElementById('customerCode').className='disableText';
	document.getElementById('btFindCust').style.visibility = 'hidden';
}
function getCustNameKeypress(e,custCode){
	var form = document.stockDiscountForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			$("#customerCode").val('');
			$("#customerName").val('');
			$("#customerAddress").val('');
			$("#customerId").val('');
		}else{
		   getCustName(custCode);
		}
	}
}
function getCustNameOnblur(e,custCode){
	var form = document.stockDiscountForm;
	if(e != null ){
		if(custCode.value ==''){
			$("#customerCode").val('');
			$("#customerName").val('');
			$("#customerAddress").val('');
			$("#customerId").val('');
		}else{
		  getCustName(custCode);
		}
	}
}
function getCustName(custCode){
	//alert("getCustName custCode:"+custCode.value);
	var returnString = "";
	var form = document.stockDiscountForm;
	var path = document.getElementById("path").value;

	var getData = $.ajax({
			url: path+"/jsp/ajax/getCustNameWithAddressAjax.jsp",
			data : "custCode=" + custCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	//alert(returnString);
		if(returnString !=''){
			var retArr = returnString.split("|");
			$("#customerName").val(retArr[0]);
			$("#customerAddress").val(retArr[1]);
			$("#customerId").val(retArr[2]);
			
			disableFindCustomer();
		}else{
			alert("ไม่พบข้อมูล");
			$("#customerCode").val('');
			$("#customerName").val('');
			$("#customerAddress").val('');
			$("#customerId").val('');
		}
}
function openPopupInvoiceModel(path,index,userId){
	var form = document.stockDiscountForm;
	//alert("index:"+index);
	var productCodeObj = document.getElementsByName("productCode")[index-1];
	var productNameObj = document.getElementsByName("productName")[index-1];

	if($("#customerCode").val() != '' && $("#vatRate").val() != '' 
		&&( productCodeObj.value != '' || productNameObj.value != '') ){
	    var param  = "&index="+index;
	        param += "&productCode="+productCodeObj.value;
	        param += "&userId="+userId;
	        param += "&customerCode="+$("#customerCode").val();
	        param += "&requestNumber="+$("#requestNumber").val();
	        param += "&backDate="+$("#backDate").val();
	        //alert(param);
		var url = path + "/jsp/popupAction.do?do=prepare&page=INVOICE_STOCK_DISCOUNT&action=new"+param;
		PopupCenterFullHeight(url,"",600);
	}else{
		if($("#customerCode").val() == ''){
		   alert("กรุณาระบุรหัส ร้านค้าก่อน");
		   $("#customerCode").focus();
		}
		if($("#vatRate").val() == ''){
		   alert("กรุณาระบุ Vat Rate");
		   $("#vatRate").focus();
		}
		if(productCodeObj.value == '' || productNameObj.value == ''){
			 alert("กรุณาระบุรหัสสินค้า หรือ รายละเอียด");
			 productCodeObj.focus();
		}
	}
}

function setInvoiceValue(code,priAllQty,priQty,subQty,uom1Price,remainAmount,index){
	var productCode = document.getElementsByName("productCode")[index-1];
	var arInvoiceNo = document.getElementsByName("arInvoiceNo")[index-1];
	var remainPriAllQty = document.getElementsByName("remainPriAllQty")[index-1];
	var remainPriQty = document.getElementsByName("remainPriQty")[index-1];
	var remainSubQty = document.getElementsByName("remainSubQty")[index-1];
	var uom1PriceObj = document.getElementsByName("uom1Price")[index-1];
	var remainAmountObj = document.getElementsByName("remainAmount")[index-1];
	if(productCode.value != ""){
		if(code != ''){
		  arInvoiceNo.value = code;
		  remainPriAllQty.value = priAllQty;
		  remainPriQty.value = priQty;
		  remainSubQty.value = subQty;
		  uom1PriceObj.value = uom1Price;
	
		  remainAmountObj.value = remainAmount;//from popup
		  
		  arInvoiceNo.className ="normalTextInput";
		  if(checkItemDuplicate(index)){
			 arInvoiceNo.className ="errorTextInput";
			 alert("ไม่สามารถ กรอกข้อมูลสินค้าเดียวกันได้ ใน InvoiceNo เดียวกัน")  ;
			 arInvoiceNo.value = '';
			 remainPriAllQty.value = '';
			 remainPriQty.value = '';
			 remainSubQty.value = '';
			 uom1PriceObj.value = '';
			 remainAmountObj.value = '';
		  }
		}else{
			/** not found **/
		   arInvoiceNo.value = '';
		   remainPriAllQty.value = '';
		   remainPriQty.value = '';
		   remainSubQty.value = '';
		   uom1PriceObj.value ='';
		   remainAmountObj.value = '';
		}
	}else{
		/** case search  no productCode **/
		if(code != ''){
		  arInvoiceNo.value = code;
		  remainPriAllQty.value = priAllQty;
		  remainPriQty.value = priQty;
		  remainSubQty.value = subQty;
		  uom1PriceObj.value = uom1Price;
		  remainAmountObj.value = remainAmount;
		  
		  var uom1Qty = document.getElementsByName("uom1Qty")[index-1];
		  var uom2Qty = document.getElementsByName("uom2Qty")[index-1];
		  
		  uom1Qty.readOnly = true;
		  uom2Qty.readOnly = true;
		  uom1Qty.className = "disableNumber";
		  uom2Qty.className = "disableNumber";
		  
		  arInvoiceNo.className ="normalTextInput";
		  if(checkInvoiceDuplicate(index)){
			 arInvoiceNo.className ="errorTextInput";
			 alert("ไม่สามารถ กรอกข้อมูล InvoiceNo เดียวกันได้")  ;
			 arInvoiceNo.value = '';
			 remainPriAllQty.value = '';
			 remainPriQty.value = '';
			 remainSubQty.value = '';
			 uom1PriceObj.value = '';
			 remainAmountObj.value = '';
		  }
		}else{
		   /** not found **/
		   arInvoiceNo.value = '';
		   remainPriAllQty.value = '';
		   remainPriQty.value = '';
		   remainSubQty.value = '';
		   uom1PriceObj.value ='';
		   remainAmountObj.value = '';
		}
	}
} 
function checkItemDuplicate(curRowIdChk){
	var itemCode = document.getElementsByName("productCode");
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	var status = document.getElementsByName("status");
	
	var itemCodeCheck = itemCode[curRowIdChk-1].value;
	var arInvoiceNoCheck = arInvoiceNo[curRowIdChk-1].value;
	var curCheck = itemCodeCheck+"_"+arInvoiceNoCheck;

	var checkTemp = "";
	var dup = false;
	for(var i=0;i<itemCode.length;i++){
	   if(status[i].value !='DELETE' && (i != (curRowIdChk-1)) ){
		   checkTemp = itemCode[i].value+"_"+arInvoiceNo[i].value;
		   if(checkTemp==curCheck){
			   dup = true;
		   }
	   }
	}//for
	return dup;
}
function checkInvoiceDuplicate(curRowIdChk){
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	var status = document.getElementsByName("status");

	var curArInvoiceNoCheck = arInvoiceNo[curRowIdChk-1].value;
	var checkTemp = "";
	var dup = false;
	for(var i=0;i<status.length;i++){
	   if(status[i].value !='DELETE' && (i != (curRowIdChk-1)) ){
		   checkTemp = arInvoiceNo[i].value;
		   if(checkTemp==curArInvoiceNoCheck){
			   dup = true;
		   }
	   }
	}//for
	return dup;
}

//sum all row lineAmount same arInvoiceNo <> not cur rowId
function sumLineAmountByArInvoiceNotCur(curRowIdChk,arInvoiceNoCur){
	var lineAmount = document.getElementsByName("lineAmount");
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	var status = document.getElementsByName("status");
	var totalLineAmount = 0;
	for(var i=0;i<status.length;i++){
	   if(status[i].value !='DELETE' && (i != (curRowIdChk-1)) ){
		   if(arInvoiceNo[i].value ==arInvoiceNoCur){
			   totalLineAmount += convetTxtObjToFloat(lineAmount[i]);
		   }
	   }
	}//for
	return totalLineAmount;
}

function checkItemIsMoreOneBrand(curRowIdChk){
	var itemCode = document.getElementsByName("productCode");
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	var status = document.getElementsByName("status");
	
	var itemCodeCheck = itemCode[curRowIdChk-1].value;
	var arInvoiceNoCheck = arInvoiceNo[curRowIdChk-1].value;
	var curBrandCheck =itemCodeCheck.substring(0,3) ;

	var brandCheckTemp = "";
	var brandError = false;
	for(var i=0;i<itemCode.length;i++){
	   if(status[i].value !='DELETE' && (i != (curRowIdChk-1)) ){
		   brandCheckTemp = itemCode[i].value.substring(0,3);
		   if(brandCheckTemp !="" && brandCheckTemp !=curBrandCheck){
			   brandError = true;
		   }
	   }
	}//for
	return brandError;
}

/** check DocType from 1 row **/
function checkDocType(){
	var productCode = document.getElementsByName("productCode");
	var productName = document.getElementsByName("productName");
	var status = document.getElementsByName("status");
	var docTypeObj = document.getElementById("docType");
	
	var docType ="";
	if(productCode.length >=1){
		 if(status[0].value !='DELETE'){
			 if(productCode[0].value != ""){
			    docTypeObj.value = "KEY_PRODUCT";
			 }else {
				docTypeObj.value = "KEY_DETAIL"; 
			 }
		 }else{
			// row blank reset docType
			docTypeObj.value = "";
			//enbale can key 2 Type
			var productCode = document.getElementsByName("productCode");
			var productName = document.getElementsByName("productName");
			for(var i=0;i<productCode.length;i++){
				productCode[i].readOnly = false;
				productName[i].readOnly = false;
				productCode[i].className = 'normalText';
				productName[i].className = 'normalText';
			}
		 }
	}else{
		// row blank reset docType
		docTypeObj.value = "";
	}
	
}