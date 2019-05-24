
function calcTargetAmount(targetQtyObj,rowId){
	var targetAmount = document.getElementsByName("targetAmount")[rowId-1];
	var price = document.getElementsByName("price")[rowId-1];
	if(price.value !=''){
		var targetAmountTemp = currenyToNum(targetQtyObj)*currenyToNum(price);
		targetAmount.value = targetAmountTemp;
		toCurreny(targetAmount);

		summaryTotal();
	}
}
function clearTotal(){
	document.getElementsByName("totalOrderAmt12Month")[0].value="";
	document.getElementsByName("totalOrderAmt3Month")[0].value="";
	document.getElementsByName("totalTargetAmount")[0].value="";
	document.getElementsByName("totalTargetQty")[0].value="";
}
function summaryTotal(){
	var totalOrderAmt12Month = 0;
	var totalOrderAmt3Month =0;
	var totalTargetAmount = 0;
	var totalTargetQty = 0;
	var orderAmt12Month = document.getElementsByName("orderAmt12Month");
	var orderAmt3Month = document.getElementsByName("orderAmt3Month");
	var targetAmount = document.getElementsByName("targetAmount");
	var targetQty = document.getElementsByName("targetQty");
	
	for(var i=0;i<targetAmount.length;i++){
		if(targetQty[i].value != ''){
		  totalTargetAmount += parseFloat(currenyToNum(targetAmount[i]));
		  totalTargetQty += parseFloat(currenyToNum(targetQty[i]));
		
		  totalOrderAmt12Month += parseFloat(currenyToNum(orderAmt12Month[i]));
		  totalOrderAmt3Month += parseFloat(currenyToNum(orderAmt3Month[i]));
		}
	}
	
	//set Show total
	document.getElementsByName("totalOrderAmt12Month")[0].value=totalOrderAmt12Month;
	document.getElementsByName("totalOrderAmt3Month")[0].value=totalOrderAmt3Month;
	document.getElementsByName("totalTargetAmount")[0].value=totalTargetAmount;
	document.getElementsByName("totalTargetQty")[0].value=totalTargetQty;
	
	toCurreny(document.getElementsByName("totalOrderAmt12Month")[0]);
	toCurreny(document.getElementsByName("totalOrderAmt3Month")[0]);
	toCurreny(document.getElementsByName("totalTargetAmount")[0]);
	toCurrenyNoDigit(document.getElementsByName("totalTargetQty")[0]);
}
function checkProductOnblur(e,itemCodeObj,rowId){
	 //alert("ONBLUR");
	//alert(itemCodeObj.value);
	var itemName = document.getElementsByName("itemName");
	if(itemName[rowId-1].value ==''){
		itemCodeObj.value ='';
		itemCodeObj.focus();
	}
}
//Check enter only
function  getProductKeypress(e,itemCodeObj,rowId){
//	alert("keypress");
	var itemCode = document.getElementsByName("itemCode");
	var itemName = document.getElementsByName("itemName");
	var itemId = document.getElementsByName("itemId");
	var price = document.getElementsByName("price");
	var targetQty = document.getElementsByName("targetQty");
	var orderAmt12Month = document.getElementsByName("orderAmt12Month");
	var orderAmt3Month = document.getElementsByName("orderAmt3Month");
	var ENTERKEY =13;
	if(e != null && (e.keyCode == ENTERKEY)){
		if(itemCodeObj.value ==''){
			itemName[rowId-1].value = '';
			itemId[rowId-1].value = '';
			price[rowId-1].value = '';
			orderAmt12Month[rowId-1].value = '';
			orderAmt3Month[rowId-1].value = '';
		}else{
			var found = getProductModel(itemCodeObj,rowId);
			if(found){
				var index = rowId-1;
				//Set Prev row readonly 
				itemCode[index].className ="disableText";
				itemCode[index].readOnly = true;
				targetQty[index].focus();
        		//Add New Row Auto
				addRow(false);	
			}//if
		}//if
	}//if
}

/** check TAB ONLY **/
function  getProductKeydown(e,itemCodeObj,rowId){;
	var itemCode = document.getElementsByName("itemCode");
	var itemName = document.getElementsByName("itemName");
	var itemId = document.getElementsByName("itemId");
	var price = document.getElementsByName("price");
	var targetQty = document.getElementsByName("targetQty");
	var orderAmt12Month = document.getElementsByName("orderAmt12Month");
	var orderAmt3Month = document.getElementsByName("orderAmt3Month");
	var TABKEY =9;
	if(itemName[rowId-1].value ==''){
		if(e != null && (e.keyCode == TABKEY)){
			if(itemCodeObj.value ==''){
				itemName[rowId-1].value = '';
				itemId[rowId-1].value = '';
				price[rowId-1].value = '';
				orderAmt12Month[rowId-1].value = '';
				orderAmt3Month[rowId-1].value = '';
			}else{
				var found = getProductModel(itemCodeObj,rowId);
				if(found){
					var index = rowId-1;
					//Set Prev row readonly 
					itemCode[index].className ="disableText";
					itemCode[index].readOnly = true;
	        		//Add New Row Auto
					addRow(false);
				}//if
			}//if
		}//if
	}//if
}
function getProductModel(itemCodeObj,rowId){
	var found = false;
	var path = document.getElementById("path").value;
	//value
	var priceListId = document.getElementById("priceListId").value;
	var period = document.getElementById("period").value;
	var custCatNo = document.getElementById("custCatNo").value;
	var customerId = document.getElementById("customerId").value;
	var salesrepId = document.getElementById("salesrepId").value;
	var brand = document.getElementById("brand").value;
	
	var itemCode = document.getElementsByName("itemCode");
	var itemName = document.getElementsByName("itemName");
	var itemId = document.getElementsByName("itemId");
	var price = document.getElementsByName("price");
	var orderAmt12Month = document.getElementsByName("orderAmt12Month");
	var orderAmt3Month = document.getElementsByName("orderAmt3Month");
	//pass parameter
	var param  = "itemCode=" + itemCodeObj.value;
    param +="&priceListId=" + priceListId;
    param +="&period=" + period;
    param +="&custCatNo=" + custCatNo;
    param +="&customerId=" + customerId;
    param +="&salesrepId=" +salesrepId;
    param +="&brand=" +brand;
    
	var returnString = "";
	var getData = $.ajax({
			url: path+"/jsp/ajax/getProductAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	   // alert("x:"+returnString);
	    
		if(returnString==''){
			alert("ไม่พบข้อมูลสินค้า  "+itemCodeObj.value);
			itemCodeObj.focus();
			
			itemCode[rowId-1].value = '';
			itemName[rowId-1].value = '';
			itemId[rowId-1].value = '';
			price[rowId-1].value = '';
			orderAmt12Month[rowId-1].value = '';
			orderAmt3Month[rowId-1].value = '';
		}else if(returnString=='DUPLICATE'){
			alert("ไม่สามารถกรอก รหัสสินค้าซ้ำได้  "+itemCodeObj.value);
			itemCodeObj.focus();
			
			itemCode[rowId-1].value = '';
			itemName[rowId-1].value = '';
			itemId[rowId-1].value = '';
			price[rowId-1].value = '';
			orderAmt12Month[rowId-1].value = '';
			orderAmt3Month[rowId-1].value = '';
		}else{
			var s = returnString.split("|");
			//Price <> 0
			if(s[3]=='' || s[3]=='0' || s[3]=='0.0'){
				alert("ไม่สามารถกรอก รหัสสินค้านี้ได้ เนื่องจากไม่มี ราคา Price List ");
			    itemCodeObj.focus();
				itemCode[rowId-1].value = '';
				itemName[rowId-1].value = '';
				itemId[rowId-1].value = '';
				price[rowId-1].value = '';
				orderAmt12Month[rowId-1].value = '';
				orderAmt3Month[rowId-1].value = '';
			}else{
				itemCode[rowId-1].className ="disableText";
				itemCode[rowId-1].value = s[0];
				itemId[rowId-1].value = s[1];
				itemName[rowId-1].value = s[2];
				price[rowId-1].value = s[3];
				orderAmt12Month[rowId-1].value = s[4];
				orderAmt3Month[rowId-1].value = s[5];
				found = true;
			}
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
	var rowId = rows;
    var tabIndex = parseFloat(document.getElementById("tabIndex").value);
    //alert(rowId);
	tabIndex++;
	
	//alert("rowId["+rowId+"]");
	var rowData ="<tr class='"+className+"'>"+
	    "<td class='td_text_center' width='3%'> " +
	    "  <input type='checkbox' tabindex ='-1' name='linechk' id='lineChk' value='0'/>" +
	    "  <input type='hidden' tabindex ='-1' name='lineId' id='lineId' value='0'/>"+
	    "</td>"+
	    "<td class='td_text_center' width='7%'> "+
	    "  <input type='text' name='itemCode' id='itemCode' size='5' class='normalText' "+
	    "   onkeypress='getProductKeypress(event,this,"+rowId+")' "+
	    "   onkeydown='getProductKeydown(event,this,"+rowId+")' "+
	    "   onchange='checkProductOnblur(event,this,"+rowId+")' " +
	    "   autoComplete='off'  tabindex ="+tabIndex+
	    " />  </td>"+
	    "<td class='td_text_center'  width='19%'> "+
	    " <input type='text' tabindex ='-1' name='itemName' size='35' readonly class='disableText' />" +
	    " <input type='hidden' tabindex ='-1' name='itemId' id='itemId'/>"+
	    "</td>"+
	    "<td class='td_number' width='7%'> <input type='text' tabindex ='-1' name='orderAmt12Month' id='orderAmt12Month' readonly class='disableNumber' size='8' /></td>"+
	    "<td class='td_number' width='7%'> <input type='text' tabindex ='-1' name='orderAmt3Month' id='orderAmt3Month' readonly class='disableNumber' size='8' /></td>"+
	    "<td class='td_number' width='7%'> <input type='text' tabindex ='-1' name='price' id='price' readonly class='disableNumber' size='8' /></td>";
	  	   
	    tabIndex++;
	    rowData +="<td class='td_number' width='9%'> "+
	    "  <input type='text' name='targetQty' id='targetQty' size='10' class='enableNumber' "+
	    "   onblur='isNumPositive(this);calcTargetAmount(this,"+rowId+")'  tabindex ="+tabIndex+""+
	    "   onkeypress='nextRowKeypress(event,"+rowId+")'  autoComplete='off' "+
	    " />  </td>"+
	    
	    "<td class='td_number' width='9%'><input type='text' tabindex ='-1' name='targetAmount' id='targetAmount' readonly class='disableNumber' size='10'/></td>"+
	    "<td class='td_text_center' width='6%'><input type='text' tabindex ='-1' name='status' id='status' value='Open' readonly class='disableTextCenter' size='6'/></td>"+
	    "<td class='td_text_center' width='15%'><input type='text' tabindex ='-1' name='remark' id='remark'  class='normalText' size='20'  autoComplete='off' /></td>"+
	    "<td class='td_text_center' width='11%'><input type='text' tabindex ='-1' name='rejectReason' id='rejectReason' readonly class='disableText' size='18'/></td>"+
	    "</tr>";

	//alert(rowData);
    $('#tblProduct').append(rowData);
    //set next tabIndex
    document.getElementById("tabIndex").value = tabIndex;
    
    //set focus default
    var itemCode = document.getElementsByName("itemCode");
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

function checkRemoveAllRow(){
	var chk = document.getElementsByName("linechk");
	var status = document.getElementsByName("status");
	var itemCode = document.getElementsByName("itemCode");
	var deleteAllFlag = true;
	for(var i=chk.length-1;i>=0;i--){
		if(itemCode[i].value !='' && status[i].value !="DELETE"){
			deleteAllFlag = false;
		}
	}
	return deleteAllFlag;
}

function removeRow(path){
	var msg = "ยืนยันลบข้อมูล";
	//todo play with type
	if(confirm(msg)){
		var tbl = document.getElementById('tblProduct');
		var chk = document.getElementsByName("linechk");
		var status = document.getElementsByName("status");
		var itemCode = document.getElementsByName("itemCode");
		var drow;
		var bcheck=false;
		var ietmCodeArr ="";
		for(var i=chk.length-1;i>=0;i--){
			if(chk[i].checked){
				// alert(i);
				drow = tbl.rows[i+1];
				status[i].value ="DELETE";
				$(drow).hide();
				bcheck = true;
				
				//alert(itemCode[i].value);
				ietmCodeArr += itemCode[i].value+"|";
			}
		}
		if(!bcheck){
			alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;
		}else{
			summaryTotal();
			//clear session check duplicate
			var param ="itemCodeArr="+ietmCodeArr;
			var getData = $.ajax({
				url: path+"/jsp/ajax/clearSessionProductInPageAjax.jsp",
				data : param,
				async: true,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
			
			var removeRowAll = checkRemoveAllRow();
			if(removeRowAll){
				clearTotal();
				//if(confirm("ยืนยันลบข้อมูลทั้งหมด")){
					document.salesTargetForm.action = path + "/jsp/salesTargetAction.do?do=deleteAll";
					document.salesTargetForm.submit();
				//}
			}
		}
	}
	return false;
}
function backToMainPage(path){
	var form = document.salesTargetForm;
	form.action = path + "/jsp/mainpage.jsp";
	form.submit();
	return true;
}

function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
}

function checkTableCanSave(){
	var itemCode = document.getElementsByName("itemCode");
	var targetQty = document.getElementsByName("targetQty");
	var status = document.getElementsByName("status");
	var r = true;
	
	/** Check Row 1 found data **/
	if(itemCode[0].value ==''){
		alert("กรุณาระบุข้อมูลอย่างน้อย 1 รายการ");
		itemCode[0].focus();
		r = false;
	 }
	if(r==true){
		for(var i=0;i<itemCode.length;i++){
			if(status[i].value !='DELETE'){
			  if(itemCode[i].value !=''&& targetQty[i].value ==''){
				  alert("กรุณาระบุข้อมูล เป้าหมาย ขาย(หีบ) ให้ครบถ้วน");
				  targetQty[i].focus();
				  targetQty[i].className ='errorNumber';
				  r = false;
				 // break;
			  }
		    }
		}
	}
	return r;
}

function checkCanPostToSalesFound(){
	var r= false;
	var itemCode = document.getElementsByName("itemCode");
	var status = document.getElementsByName("status");
	for(var i=0;i<itemCode.length;i++){
	  
	  if(itemCode[i].value !='' && (status[i].value != 'Open' || status[i].value !='Reject')) {
		  //alert(itemCode[i].value+","+status[i].value);
		  r = true;
		  break;
	  }
	}
	return r;
}

function checkCanAccept(){
	var r= true;
	var status = document.getElementsByName("status");
	for(var i=0;i<itemCode.length;i++){
	  if(status[i].value != 'Post' && status[i].value !='Unaccept' ) {
		  //alert(itemCode[i].value+","+status[i].value);
		  r = false;
		  break;
	  }
	}
	return r;
}

