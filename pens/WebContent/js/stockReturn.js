
function cancelStockReturn(path){
	//alert(moveOrderType+":"+requestNumber);
	
	//if(confirm("ยืนยันการ ยกเลิกรายการ")){
		//var input= confirmInputReason();
		//if(input){
			//document.getElementsByName('bean.description')[0].value = input;
		    document.stockReturnForm.action = path + "/jsp/stockReturnAction.do?do=cancelStockReturn";
		    document.stockReturnForm.submit();
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
function save(path,moveOrderType){
	if(checkTableCanSave()){
		document.stockReturnForm.action = path + "/jsp/stockReturnAction.do?do=save";
		document.stockReturnForm.submit();
		return true;
	}
	return false;
}
function checkTableCanSave(){
	var itemCode = document.getElementsByName("productCode");
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	var remainPriQty = document.getElementsByName("remainPriQty");
	var uom1Qty = document.getElementsByName("uom1Qty");
	var uom2Qty = document.getElementsByName("uom2Qty");
	var uom2 = document.getElementsByName("uom2");
	var uom1Pac = document.getElementsByName("uom1Pac");
	var uom2Pac = document.getElementsByName("uom2Pac");
	var uom1Price = document.getElementsByName("uom1Price");
	var discount = document.getElementsByName("discount");
	var totalAmount = document.getElementsByName("totalAmount");
	
	var status = document.getElementsByName("status");
	var r = true;
	var error = false;
	var rowSelectOne = false;
	
	for(var i=0;i<itemCode.length;i++){
	 // alert(itemCode[i].value+":"+status[i].value+":"+arInvoiceNo[i].value);
	   if(itemCode[i].value !='' && status[i].value !='DELETE'){
	      itemCode[i].className ='enableNumber';
	      uom1Qty[i].className ='enableNumber';
	      uom2Qty[i].className ='enableNumber';
	      arInvoiceNo[i].className ='normalTextInput';
	      
		  rowSelectOne = true;
		  
		 // alert("["+qty[i].value+"]["+sub[i].value+"]["+expireDate[i].value+"]");
		  if( arInvoiceNo[i].value =='' ){
			  arInvoiceNo[i].className ='errorTextInput';
			  error = true;
		  }
		  if(uom1Qty[i].value =='' && uom2Qty[i].value==''){
			  uom1Qty[i].className ='errorNumberInput';
		      uom2Qty[i].className ='errorNumberInput';
		      error = true;
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
	document.stockReturnForm.action = path + "/jsp/stockReturnAction.do?do=clear";
	document.stockReturnForm.submit();
	return true;
}

function escapeParameter(param){
	return param.replace("%","%25");
}

function popCalendar(thisObj, thisEvent) {
	new Epoch('epoch_popup', 'th', thisObj);
}
function checkProductOnblur(e,itemCodeObj,rowId){
	 //alert("ONBLUR");
	//alert(itemCodeObj.value);
	var productName = document.getElementsByName("productName");
	if(productName[rowId-1].value ==''){
		itemCodeObj.value ='';
		itemCodeObj.focus();
	}
}

function sumTotalInRow(index){
	var pcsQty = 0;
	var priQty = document.getElementsByName("priQty");
	var remainPriAllQty = document.getElementsByName("remainPriAllQty");
	var uom1Qty = document.getElementsByName("uom1Qty");
	var uom2Qty = document.getElementsByName("uom2Qty");
	var uom2 = document.getElementsByName("uom2");
	var uom1ConvRate = document.getElementsByName("uom1ConvRate");
	var uom2ConvRate = document.getElementsByName("uom2ConvRate");
	var uom1Price = document.getElementsByName("uom1Price");
	var discount = document.getElementsByName("discount");
	var totalAmount = document.getElementsByName("totalAmount");
	
	var productCode = document.getElementsByName("productCode");
	var arInvoiceNo = document.getElementsByName("arInvoiceNo");
	
	if(productCode[index-1].value != '' && arInvoiceNo[index-1].value != ''
		&& (uom1Qty[index-1].value != '' || uom2Qty[index-1].value != '') ){
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
			alert("จำนวนที่คีย์ มากกว่าจำนวนในบิลที่สามารถคืนได้ กรุณาตรวจสอบ");
			uom1Qty[index-1].focus();
			uom1Qty[index-1].className ="errorNumber";
			uom2Qty[index-1].className ="errorNumber";
			totalAmount[index-1].value ="0";
			uom1Qty[index-1].value ="";
			uom2Qty[index-1].value ="";
			priQty[index-1].value = "";
		}else{
			uom1Qty[index-1].className ="enableNumber";
			uom2Qty[index-1].className ="enableNumber";
			
			//calc total Amount = (priQty*uom1Price) -discount
			totalAmount[index-1].value = priQtyTemp * (convetTxtObjToFloat(uom1Price[index-1]) - convetTxtObjToFloat(discount[index-1]) );
			toCurreny(totalAmount[index-1]);
		}
		//cal amount totalAllRowAmount
		sumTotalAllRow();
	}else{
		if(productCode[index-1].value == ''){
			alert("กรุณาระบุ รหัสสินค้าก่อน ");
			productCode[index-1].className ="errorTextInput";
			productCode[index-1].focus();
			uom1Qty[index-1].value = "";
			uom2Qty[index-1].value = "";
		}else{
			productCode[index-1].className ="normalTextInput";
		}
		 
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
	var totalAmount = document.getElementsByName("totalAmount");
	var status = document.getElementsByName("status");
	
	var totalAllNonVatAmount = document.getElementById("totalAllNonVatAmount");
	var totalAllVatAmount = document.getElementById("totalAllVatAmount");
	var totalAllAmount = document.getElementById("totalAllAmount");
	var sumAllNonVat= 0;
	
	for(var i=0;i<itemCode.length;i++){
	 // alert(itemCode[i].value+":"+status[i].value+":"+arInvoiceNo[i].value);
	   if(itemCode[i].value !='' && status[i].value !='DELETE'){
		   sumAllNonVat += convetTxtObjToFloat(totalAmount[i]);
	   }
	}//for
	
	totalAllNonVatAmount.value = sumAllNonVat;
	//calc vat
	totalAllVatAmount.value = sumAllNonVat*0.07;
	//totalAmount
	totalAllAmount.value = sumAllNonVat + (sumAllNonVat*0.07);
	
	//convert to currency
	toCurreny(totalAllNonVatAmount);
	toCurreny(totalAllVatAmount);
	toCurreny(totalAllAmount);
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
	var discount = document.getElementsByName("discount");
	var totalAmount = document.getElementsByName("totalAmount");
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
			discount[rowId-1].value = '';
			totalAmount[rowId-1].value = '';
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
	var discount = document.getElementsByName("discount");
	var totalAmount = document.getElementsByName("totalAmount");
	var uom1ConvRate = document.getElementsByName("uom1ConvRate");
	var uom2ConvRate = document.getElementsByName("uom2ConvRate");
	
	//pass parameter
	var param  = "productCode=" + itemCodeObj.value+"&requestNumber="+document.getElementById("requestNumber").value;
	var returnString = "";
	var getData = $.ajax({
			url: path+"/jsp/stockReturn/ajax/getProductStockReturnQuery.jsp",
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
			discount[rowId-1].value = '';
			totalAmount[rowId-1].value = '';
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
		}
	return found;
}

/**
 * AddRow(setFocus)
 */
function addRow(setFocus){
	var rows = $('#tblProduct tr').length-1;
	var className = 'lineO';
	if(rows%2 !=0){
		className = 'lineE';
	}
	var rowId = rows-1;
    var tabIndex = parseFloat(document.getElementById("tabIndex").value);
    var no = rows-1;
	tabIndex++;
	
	//alert("rowId["+rowId+"]");
	
	var rowData ="<tr class='"+className+"'>"+
	    "<td class='td_text_center' width='5%'> " +
	    "  <input type='checkbox' tabindex ='-1' name='linechk' id='lineChk' value='0'/>" +
	    "  <input type='hidden' tabindex ='-1' name='lineId' id='lineId' value='0'/>"+
	    "  <input type='hidden' tabindex ='-1' name='status' id='status' value='SV' />"+
	    "</td>"+
	    "<td class='td_text_center' width='5%'> " +
	    "  <input type='text' name='no' value='"+no+"' id='no' size='2' readonly class='disableTextCenter'>" +
	    "</td>"+
	   
	    "<td class='td_text_center' width='6%'> "+
	    "  <input type='text' name='productCode' id='productCode' size='5' class='normalText' "+
	    "   onkeypress='getProductKeypress(event,this,"+rowId+")' "+
	    "   onchange='checkProductOnblur(event,this,"+rowId+")' " +
	    "   tabindex ="+tabIndex+
	    "  autoComplete='off'/>  </td>"+
	    "<td class='td_text'  width='15%'> "+
	    " <input type='text' tabindex ='-1' name='productName' size='40' readonly class='disableText' />" +
	    " <input type='hidden' tabindex ='-1' name='inventoryItemId' id='inventoryItemId'/>"+
	    " <input type='hidden' size='3' class='disableText' tabindex ='-1' name='uom1ConvRate' id='uom1ConvRate'/>"+
	    " <input type='hidden' size='3' class='disableText' tabindex ='-1' name='uom2ConvRate' id='uom2ConvRate'/>"+
	    "</td>";
	    tabIndex++;
	    rowData +="<td class='td_text_center'  width='10%' nowrap> "+
	    " <input type='text' name='arInvoiceNo' id='arInvoiceNo' autoComplete='off' value ='' size='9'  readonly tabindex ="+tabIndex+"/>" +
	    " <input type='button' name='bt3' value='...' onclick='openPopupInvoice("+no+")'/> "+
	    "</td>"+
	    "<td class='td_number'  width='6%'> "+
	    " <!--remainPriAllQty:--><input type='text' size='8' name='remainPriAllQty' id='remainPriAllQty' value ='' readonly class='disableNumber'/>" +
	    " <!--remainPriQty:--><input type='hidden' size='3' class='disableText' name='remainPriQty' id='remainPriQty' value =''  readonly />" +
	    " <!--remainSubQty:--><input type='hidden' size='3' class='disableText' name='remainSubQty' id='remainSubQty' value =''  readonly />" +
	    "</td>"+
	   
	    tabIndex++;
	    rowData +="<td class='td_number' width='6%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='uom1Qty' size='5' "+
	    "  onblur ='sumTotalInRow("+no+")' autoComplete='off'"+
	    "  onkeydown='return isNum0to9andpoint(this,event);'class='numberText' /> "+
	    " <!--priQty:--><input type='hidden' size='3' tabindex ='-1' class='disableText' name='priQty' id='priQty'/>"+
	    "  </td>"+
	    tabIndex++;
	    rowData +="<td class='td_number' width='6%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='uom2Qty' size='5' "+
	    "  onblur ='sumTotalInRow("+no+")' "+
	    "  onkeydown='return isNum0to9andpoint(this,event);' class='numberText' autoComplete='off'/> "+
	    "  </td>";
	
	    rowData +="<td class='td_text_center' width='7%'> "+
	    "  <input type='text' name='uom2' value='' id='uom2' size='3' readonly class='disableText'>"+
	    "  </td>"+
	    "<td class='td_number'  width='7%'> "+
	    " <input type='text' name='uom1Pac' id='uom1Pac' value ='' size='6' readonly class='disableNumber'/>" +
	    "</td>"+
	    "<td class='td_number'  width='7%'> "+
	    " <input type='text' name='uom2Pac' id='uom2Pac' value ='' size='6' readonly class='disableNumber'/>" +
	    "</td>"+
	    "<td class='td_number'  width='7%'> "+
	    " <input type='text' name='uom1Price' id='uom1Price' value ='' size='6' readonly class='disableNumber'/>" +
	    "</td>";
	    tabIndex++;
	    rowData +="<td class='td_number' width='7%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='discount' size='5' onblur='sumTotalInRow("+no+")' "+
	    "  onkeydown='return isNum(this,event);' class='numberText' autoComplete='off'/> "+
	    "  </td>";
	    rowData +="<td class='td_number' width='7%'> "+
	    "  <input type='text' name='totalAmount' id='totalAmount' size='10' readonly class='disableNumber'>"+
	    "  </td>"+
	    "</tr>";

	//alert(rowData);
    $('#tblProduct').append(rowData);
    //set next tabIndex
    document.getElementById("tabIndex").value = tabIndex;
    
    //set focus default
    var itemCode = document.getElementsByName("productCode");
    //alert(setFocus);
    if(setFocus){
       itemCode[rowId-1].focus();
    }
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
		var ietmCodeArr ="";
		for(var i=0;i<chk.length;i++){
			if(chk[i].checked){
				drow = tbl.rows[i+3];
				status[i].value ="DELETE";
				$(drow).hide();
				bcheck=true;
				
				//alert(itemCode[i].value);
				ietmCodeArr += (itemCode[i].value+"_"+arInvoiceNo[i].value)+"|";
			}
		}
		if(!bcheck){
			alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;
		}else{
			//rearrangNo
			rearrangNo();
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
	var form = document.stockReturnForm;
	
    var param = "";
	var url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	//window.open(encodeURI(url),"",
			//   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	PopupCenterFullHeight(url,"",600);
}

function setStoreMainValue(code,desc,address,customerId){
	var form = document.stockReturnForm;
	//alert(form);
	
	$("#customerCode").val(code);
	$("#customerName").val(desc);
	$("#customerAddress").val(address);
	$("#customerId").val(customerId);
} 

function getCustNameKeypress(e,custCode){
	var form = document.stockReturnForm;
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
	var form = document.stockReturnForm;
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
	var form = document.stockReturnForm;
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
		}else{
			alert("ไม่พบข้อมูล");
			$("#customerCode").val('');
			$("#customerName").val('');
			$("#customerAddress").val('');
			$("#customerId").val('');
		}
}
function openPopupInvoiceModel(path,index,userId){
	var form = document.stockReturnForm;
	//alert("index:"+index);
	var productCodeObj = document.getElementsByName("productCode")[index-1];
	
	if(productCodeObj.value != '' && $("#customerCode").val() != ''){
	    var param  = "&index="+index;
	        param += "&productCode="+productCodeObj.value;
	        param += "&userId="+userId;
	        param += "&customerCode="+$("#customerCode").val();
	        param += "&requestNumber="+$("#requestNumber").val();
	        param += "&backDate="+$("#backDate").val();
	        //alert(param);
		var url = path + "/jsp/popupAction.do?do=prepare&page=INVOICE_STOCK_RETURN&action=new"+param;
		PopupCenterFullHeight(url,"",600);
	}else{
		if($("#customerCode").val() == ''){
		   alert("กรุณาระบุรหัส ร้านค้าก่อน");
		   $("#customerCode").focus();
		}
		if(productCodeObj.value == ''){	
		   alert("กรุณาระบุรหัสสินค้าก่อน ");
		   productCodeObj.focus();
		}
	}
}

function setInvoiceValue(code,priAllQty,priQty,subQty,uom1Price,index){
	var arInvoiceNo = document.getElementsByName("arInvoiceNo")[index-1];
	var remainPriAllQty = document.getElementsByName("remainPriAllQty")[index-1];
	var remainPriQty = document.getElementsByName("remainPriQty")[index-1];
	var remainSubQty = document.getElementsByName("remainSubQty")[index-1];
	var uom1PriceObj = document.getElementsByName("uom1Price")[index-1];
	
	if(code != ''){
	  arInvoiceNo.value = code;
	  remainPriAllQty.value = priAllQty;
	  remainPriQty.value = priQty;
	  remainSubQty.value = subQty;
	  uom1PriceObj.value = uom1Price;
	  
	  if(checkItemDuplicate(index)){
		 arInvoiceNo.className ="errorTextInput";
		 alert("ไม่สามารถ กรอกข้อมูลสินค้าเดียวกันได้ ใน InvoiceNo เดียวกัน")  ;
		 arInvoiceNo.value = '';
		 remainPriAllQty.value = '';
		 remainPriQty.value = '';
		 remainSubQty.value = '';
		 uom1PriceObj.value = '';
	  }else{
		  arInvoiceNo.className ="normalTextInput";
	  }
	}else{
	   arInvoiceNo.value = '';
	   remainPriAllQty.value = '';
	   remainPriQty.value = '';
	   remainSubQty.value = '';
	   uom1PriceObj.value ='';
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
