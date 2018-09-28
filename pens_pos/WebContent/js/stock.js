
function cancelStock(path){
	//alert(moveOrderType+":"+requestNumber);
	
	//if(confirm("ยืนยันการ ยกเลิกรายการ")){
		//var input= confirmInputReason();
		//if(input){
			//document.getElementsByName('bean.description')[0].value = input;
		    document.stockForm.action = path + "/jsp/stockAction.do?do=cancelStock";
		    document.stockForm.submit();
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
		document.stockForm.action = path + "/jsp/stockAction.do?do=save";
		document.stockForm.submit();
		return true;
	}
	return false;
}

function checkTableCanSave(){
	var itemCode = document.getElementsByName("productCode");
	var qty = document.getElementsByName("qty");
	var qty2 = document.getElementsByName("qty2");
	var qty3= document.getElementsByName("qty3");
	
	var sub = document.getElementsByName("sub");
	var sub2 = document.getElementsByName("sub2");
	var sub3= document.getElementsByName("sub3");
	
	var expireDate = document.getElementsByName("expireDate");
	var expireDate2 = document.getElementsByName("expireDate2");
	var expireDate3= document.getElementsByName("expireDate3");
	
	var status = document.getElementsByName("status");
	var r = true;
	var groupInsert = false;
	var rowSelectOne = false;
	
	for(var i=0;i<itemCode.length;i++){
	  //alert(itemCode[i].value+":"+status[i].value);
	 
	  if(itemCode[i].value =='' && status[i].value !='DELETE'){
		  //alert("check");
		  if(itemCode[i].value =='' && qty[i].value =='' && qty2[i].value =='' && qty3[i].value ==''
			 && sub[i].value =='' && sub2[i].value =='' && sub3[i].value ==''
			 && expireDate[i].value =='' && expireDate2[i].value =='' && expireDate3[i].value ==''
			){
			  if(rowSelectOne==false){
				  alert("กรุณาระบุข้อมูล อย่างน้อย 1 แถว");
				  itemCode[i].focus();
				  itemCode[i].className ='errorNumber';
				  qty[i].className ='errorNumber';
				  qty2[i].className ='errorNumber';
				  qty3[i].className ='errorNumber';
				  r = false;
			  }
		   }
	   }else if(itemCode[i].value !='' && status[i].value !='DELETE'){
		      itemCode[i].className ='enableNumber';
			  qty[i].className ='enableNumber';
			  qty2[i].className ='enableNumber';
			  qty3[i].className ='enableNumber';
			  
			  rowSelectOne = true;
			  
			 // alert("["+qty[i].value+"]["+sub[i].value+"]["+expireDate[i].value+"]");
			  if( qty[i].value =='' && qty2[i].value =='' && qty3[i].value ==''
				 && sub[i].value =='' && sub2[i].value =='' && sub3[i].value ==''
				 && expireDate[i].value =='' && expireDate2[i].value =='' && expireDate3[i].value ==''
				){
				  //alert("กรุณาระบุข้อมูล อย่างน้อย 1 กลุ่ม");
				  /*groupInsert = false;
				  qty[i].className ='errorNumber';
				  qty2[i].className ='errorNumber';
				  qty3[i].className ='errorNumber';
				  sub[i].className ='errorNumber'; 
				  sub2[i].className ='errorNumber';
				  sub3[i].className ='errorNumber';
				  expireDate[i].className ='errorNumber';
				  expireDate2[i].className ='errorNumber';
				  expireDate3[i].className ='errorNumber';*/
				  
			  }else{
				  groupInsert = true;
				  qty[i].className ='enableNumber';
				  qty2[i].className ='enableNumber';
				  qty3[i].className ='enableNumber';
				  sub[i].className ='enableNumber';
				  sub2[i].className ='enableNumber';
				  sub3[i].className ='enableNumber';
				  expireDate[i].className ='normalText';
				  expireDate2[i].className ='normalText';
				  expireDate3[i].className ='normalText';
				  
				  //Group 1
                  if(expireDate[i].value ==''){
                	r = false;
                	expireDate[i].className ='errorNumber';
                  }else{
                	  if( qty[i].value =='' && sub[i].value =='' ){
                		  r = false;
                		  qty[i].className ='errorNumber';
                	  }else  if( qty[i].value =='' ){
                		  //sub 
                		  if( sub[i].value =='' ){
                			  r = false;
                			  sub[i].className ='errorNumber';
                		  }
                	  }
                  }
			  }
		}//if
	}//for
	
	if(r==false){
		  alert("กรุณาระบุข้อมูล ให้ครบถ้วน");
	  }
	if(groupInsert== false){
		r = false;
	}
	return r;
}

function clearForm(path){
	document.stockForm.action = path + "/jsp/stockAction.do?do=clear";
	document.stockForm.submit();
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

//Check enter only
function  getProductKeypress(e,itemCodeObj,rowId){
//	alert("keypress");
	var productCode = document.getElementsByName("productCode");
	var productName = document.getElementsByName("productName");
	var inventoryItemId = document.getElementsByName("inventoryItemId");
	var fullUom = document.getElementsByName("fullUom");
	var conversionRate = document.getElementsByName("conversionRate");
	var qty = document.getElementsByName("qty");
	var ENTERKEY =13;
	if(e != null && (e.keyCode == ENTERKEY)){
		if(itemCodeObj.value ==''){
			productName[rowId-1].value = '';
			inventoryItemId[rowId-1].value = '';
			fullUom[rowId-1].value = '';
			conversionRate[rowId-1].value = '';
		}else{
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
	var found = false;
	var path = document.getElementById("path").value;
	//value
	var productCode = document.getElementsByName("productCode");
	var productName = document.getElementsByName("productName");
	var inventoryItemId = document.getElementsByName("inventoryItemId");
	var fullUom = document.getElementsByName("fullUom");
	var conversionRate = document.getElementsByName("conversionRate");
	
	//pass parameter
	var param  = "productCode=" + itemCodeObj.value;
	var returnString = "";
	var getData = $.ajax({
			url: path+"/jsp/ajax/getProductStockQuery.jsp",
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
			
			productCode[rowId-1].value = '';
			productName[rowId-1].value = '';
			inventoryItemId[rowId-1].value = '';
			fullUom[rowId-1].value = '';
			conversionRate[rowId-1].value = '';
		}else if(returnString=='DUPLICATE'){
			alert("ไม่สามารถกรอก รหัสสินค้าซ้ำได้  "+itemCodeObj.value);
			itemCodeObj.focus();
			
			productCode[rowId-1].value = '';
			productName[rowId-1].value = '';
			inventoryItemId[rowId-1].value = '';
			fullUom[rowId-1].value = '';
			conversionRate[rowId-1].value = '';
		}else{
			found = true;
			var s = returnString.split("|");
			inventoryItemId[rowId-1].value = s[0];
			productName[rowId-1].value = s[1];
			fullUom[rowId-1].value = s[2];
			conversionRate[rowId-1].value = s[3];
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
    var no = rows;
	tabIndex++;
	
	//alert("rowId["+rowId+"]");
	
	var rowData ="<tr class='"+className+"'>"+
	    "<td class='td_text_center' width='3%'> " +
	    "  <input type='checkbox' tabindex ='-1' name='linechk' id='lineChk' value='0'/>" +
	    "  <input type='hidden' tabindex ='-1' name='lineId' id='lineId' value='0'/>"+
	    "  <input type='hidden' tabindex ='-1' name='status' id='status' value='SV' />"+
	    "</td>"+
	   /* "<td class='td_text_center' width='3%'> " +
	    "  <input type='text' name='no' value='"+no+"' id='no' size='2' readonly class='disableText'>" +
	    "</td>"+*/
	   
	    "<td class='td_text_center' width='5%'> "+
	    "  <input type='text' name='productCode' id='productCode' size='5' class='normalText' "+
	    "   onkeypress='getProductKeypress(event,this,"+rowId+")' "+
	    "   onchange='checkProductOnblur(event,this,"+rowId+")' " +
	    "   tabindex ="+tabIndex+
	    " />  </td>"+
	    "<td class='td_text'  width='15%'> "+
	    " <input type='text' tabindex ='-1' name='productName' size='40' readonly class='disableText' />" +
	    " <input type='hidden' tabindex ='-1' name='inventoryItemId' id='inventoryItemId'/>"+
	    "</td>"+
	    "<td class='td_text_center'  width='5%'> "+
	    " <input type='text' name='avgOrderQty' id='avgOrderQty' value ='' size='5' readonly class='disableNumber'/>" +
	    "</td>"+
	    "<td class='td_text_center'  width='5%'> "+
	    " <input type='text' name='fullUom' id='fullUom' value ='' size='6' readonly class='disableText'/>" +
	    "</td>"+
	    "<td class='td_text_center'  width='5%'> "+
	    " <input type='text' name='conversionRate' id='conversionRate' value ='' size='7' readonly class='disableText'/>" +
	    "</td>"+
	    
	    tabIndex++;
	    rowData +="<td class='td_number' width='3%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='qty' size='5' "+
	    "  onkeydown='return isNum0to9andpoint(this,event);'class='numberText' /> "+
	    "  </td>"+
	    tabIndex++;
	    rowData +="<td class='td_number' width='3%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='sub' size='5' "+
	    "  onkeydown='return isNum0to9andpoint(this,event);'class='numberText' /> "+
	    "  </td>"+
	    tabIndex++;
	    rowData +="<td class='td_text_center' width='5%'> "+
	    "  <input type='text' name='expireDate' value='' id='expireDate' size='8' onmouseover='popCalendar(this,this)' readonly>"+
	    "  </td>"+
	    
	    tabIndex++;
	    rowData +="<td class='td_number' width='3%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='qty2' size='5' "+
	    "  onkeydown='return isNum0to9andpoint(this,event);'class='numberText' /> "+
	    "  </td>"+
	    tabIndex++;
	    rowData +="<td class='td_number' width='3%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='sub2' size='5' "+
	    "  onkeydown='return isNum0to9andpoint(this,event);'class='numberText' /> "+
	    "  </td>"+
	    tabIndex++;
	    rowData +="<td class='td_text_center' width='5%'> "+
	    "  <input type='text' name='expireDate2' value='' id='expireDate2' size='8' onmouseover='popCalendar(this,this)' readonly>"+
	    "  </td>"+
	    tabIndex++;
	    rowData +="<td class='td_number' width='3%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='qty3' size='5' "+
	    "  onkeydown='return isNum0to9andpoint(this,event);'class='numberText' /> "+
	    "  </td>"+
	    tabIndex++;
	    rowData +="<td class='td_number' width='3%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='sub3' size='5' "+
	    "  onkeydown='return isNum0to9andpoint(this,event);'class='numberText' /> "+
	    "  </td>"+
	    tabIndex++;
	    rowData +="<td class='td_text_center' width='5%'> "+
	    "  <input type='text' name='expireDate3' value='' id='expireDate3' size='8' onmouseover='popCalendar(this,this)' readonly>"+
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
		var drow;
		var bcheck=false;
		var ietmCodeArr ="";
		for(var i=0;i<chk.length;i++){
			if(chk[i].checked){
				drow = tbl.rows[i+2];
				status[i].value ="DELETE";
				$(drow).hide();
				bcheck=true;
				
				//alert(itemCode[i].value);
				ietmCodeArr += itemCode[i].value+"|";
			}
		}
		if(!bcheck){
			alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;
		}else{
			//clear session product Item In Page
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
		}
	}
	return false;
}
