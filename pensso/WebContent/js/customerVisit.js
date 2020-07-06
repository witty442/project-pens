
function prepare(path,type,id){
	if(id!=null){
		document.customervisitForm.action = path + "/jsp/customervisitAction.do?do=prepare&id="+id+"&action="+type;
	}else{
		document.customervisitForm.action = path + "/jsp/customervisitAction.do?do=prepare"+"&action="+type;
	}
	document.customervisitForm.submit();
	return true;
}

function backToCustomer(path, customerId) {
	var from = document.getElementsByName('criteria.from')[0].value;
	if(from=='process'){
		window.location = path + '/jsp/customerAction.do?do=prepare&id=' + customerId + '&action=process';
	}else if(from=='view'){
		window.location = path + '/jsp/customerAction.do?do=prepare&id=' + customerId + '&action=view';
	}
}

function search(path){
	if(!datedifference(document.getElementById('dateFrom'),document.getElementById('dateTo'))){return false;}
	
	document.customervisitForm.action = path + "/jsp/customervisitAction.do?do=search&rf=Y";
	document.customervisitForm.submit();
	return true;
}

function backsearch(path, customerId) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.customervisitForm.action = path + "/jsp/customervisitAction.do?do=search";
	} else {
		document.customervisitForm.action = path + "/jsp/customervisitAction.do?do=prepare&customerId="+customerId;
	}
	document.customervisitForm.submit();
	return true;
}

function changeSalesClose(){
	if(document.getElementsByName('salesClose')[0].checked){
		document.getElementsByName('visit.unClosedReason')[0].value = '';
		document.getElementsByName('visit.unClosedReason')[0].disabled = true;
		document.getElementsByName('visit.salesClose')[0].value = 'Y';
		document.getElementById('div_require').innerHTML = '&nbsp;&nbsp;';
	}else{
		document.getElementsByName('visit.unClosedReason')[0].disabled = false;
		document.getElementsByName('visit.salesClose')[0].value = 'N';
		document.getElementById('div_require').innerHTML = '<font color="red">*</font>';
	}
}

function changeInterfaces(){
	if(document.getElementsByName('Exported')[0].checked){
		document.getElementsByName('visit.exported')[0].value = 'Y';
	}else{
		document.getElementsByName('visit.exported')[0].value = 'N';
	}
}
function save(path) {
	if(document.getElementsByName('salesClose')[0].checked){
		document.getElementsByName('visit.unClosedReason')[0].value = '';
		if(!createProductList()){alert('ใส่ข้อมูลสินค้า');return false;}
	}else{
		if(document.getElementsByName('visit.unClosedReason')[0].value == ''){
			alert('กรุณาระบุข้อมูลให้ครบถ้วน');
			document.getElementsByName('visit.unClosedReason')[0].focus();
			return false;
		}
		createProductList();
	}
	
	document.customervisitForm.action = path + "/jsp/customervisitAction.do?do=save";
	document.customervisitForm.submit();
	return true;
}

function clearForm(path){
	document.customervisitForm.action = path + "/jsp/customervisitAction.do?do=clearForm";
	document.customervisitForm.submit();
	return true;
}

/** PRODUCT */
function open_product(path, rowNo){
	if(rowNo == null){
		window.open(path + "/jsp/pop/memberProductPopup.jsp?visit=Y", "Product", "width=939,height=350,location=No,resizable=No");
	}else{
		window.open(path + "/jsp/pop/memberProductPopup.jsp?row="+rowNo+'&visit=Y', "Product", "width=939,height=350,location=No,resizable=No");
	}
	return;
}

/**Product Table*/
function addProduct2(path,objValue){
	var jQtable = $('#tblProduct');
    jQtable.each(function(){
        var $table = $(this);
        // Number of td's in the last table row
        var n = $('tr', this).length;
        var className="lineO";
        if(n%2==0)
        	className="lineE";
        objValue.row=n;
       
        var tds = '<tr class='+className+'>';
        tds += '<td align="center"></td>';
        tds += '<td align="center"></td>';
        tds += '<td align="left"></td>';
        tds += '<td align="center"></td>';
        tds += '<td align="right"></td>';
        tds += '<td align="center"></td>';
        tds += '</tr>';
        if($('tbody', this).length > 0){
            $('tbody', this).append(tds);
        }else {
            $(this).append(tds);
        }
    });
    objValue.row = eval(objValue.row) - 1;
    setValueToProduct(path,objValue);
}

function addProduct(path,objValue){
	var tbl = document.getElementById('tblProduct');
	var index = findDupIndex(tbl,objValue);
	if(index<0){
		addProduct2(path,objValue);
		return;
	}else{
		var qty = eval(jQuery.trim(tbl.rows[index+1].cells[4].innerHTML))+eval(objValue.amount);
		tbl.rows[index+1].cells[4].innerHTML = qty;
		document.getElementsByName('prod.orderQty')[index].value = qty;
	}
}

function findDupIndex(tbl,objValue){
	var i;
	var index = -1;
	for(i=0;i<tbl.rows.length;i++){
		if(i!=0){
			if(document.getElementsByName('prod.product.code')[i-1].value==objValue.pCode){
				if(document.getElementsByName('prod.uomId')[i-1].value==objValue.uomId){
					index = (i-1);
				}
			}
		}
	}
	return index;
}

/**Set Value to Product*/
function setValueToProduct(path, objValue){
	
	var tbl = document.getElementById('tblProduct');
	var inputLabel="";
	inputLabel+="<input type='hidden' name='prod.id' value='" + objValue.id + "'/>";
	inputLabel+="<input type='hidden' name='prod.product.id' value='" + objValue.pId + "'/>";
	inputLabel+="<input type='hidden' name='prod.row' value='" + (eval(objValue.row) + 1) + "'>";
	inputLabel+="<input type='hidden' name='prod.product.code' value='" + objValue.pCode + "'>";
	/*if(objValue.pName.split(' ').length > 0){
		inputLabel+="<input type='text' name='prod.product.name' value='" + objValue.pName.split(' ')[1] + "'>";
	}else{
		
	}*/
	inputLabel+="<input type='hidden' name='prod.product.name' value='" + objValue.pName + "'>";
	inputLabel+="<input type='hidden' name='prod.uomId' value='" + objValue.uomId + "'>";
	inputLabel+="<input type='hidden' name='uomLabel' value='" + objValue.uomLabel + "'>";
	inputLabel+="<input type='hidden' name='prod.orderQty' value='" + objValue.amount + "'>";
	inputLabel+="<input type='hidden' name='prod.orderQty2' value='" + objValue.amount2 + "'>";
	
	var checkBoxLabel='<input type="checkbox" name="ids" />';
	var iconLabel="";
	iconLabel+='<a href="#" onclick="open_product(\''+path+'\','+(eval(objValue.row) + 1)+');">';
	iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
	objValue.row = eval(objValue.row) + 1;
	tbl.rows[objValue.row].cells[0].innerHTML=objValue.row;
	tbl.rows[objValue.row].cells[1].innerHTML=checkBoxLabel;
	tbl.rows[objValue.row].cells[2].innerHTML=objValue.pCode + ' ' +objValue.pName + inputLabel;
	tbl.rows[objValue.row].cells[3].innerHTML=objValue.uomLabel;
	tbl.rows[objValue.row].cells[4].innerHTML=objValue.amount+'/'+objValue.amount2;
	tbl.rows[objValue.row].cells[5].innerHTML=iconLabel;
	
	return true;
}

/**Create Product Lazy List*/
function createProductList(){
	var divProd = document.getElementById('productList');
	var ids = document.getElementsByName('prod.id');
	var pId = document.getElementsByName('prod.product.id');
	var pCode = document.getElementsByName('prod.product.code');
	var pName = document.getElementsByName('prod.product.name');
	var uomId = document.getElementsByName('prod.uomId');
	var uomLabel = document.getElementsByName('uomLabel');
	var orderQty = document.getElementsByName('prod.orderQty');
	var orderQty2 = document.getElementsByName('prod.orderQty2');
	
	var inputLabel="";
	if(ids.length==0)
	{
		return false;
	}
	divProd.innerHTML = "";
	for(i=0;i<ids.length;i++){
		inputLabel="";
		inputLabel+="<input type='text' name='lines["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].product.id' value='"+pId[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].product.code' value='"+pCode[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].product.name' value='"+pName[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].uom.id' value='"+uomId[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].uom.name' value='"+uomLabel[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].amount' value='"+orderQty[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].amount2' value='"+orderQty2[i].value+"'>";
		inputLabel+="<hr/>";
		divProd.innerHTML += inputLabel;
	}
	return true;
}

function deleteProduct(path){
	var tbl = document.getElementById('tblProduct');
	var chk = document.getElementsByName("ids");
	var delId = document.getElementsByName('deletedId')[0];
	var drow;
	var bcheck=false;
	for(i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			delId.value+=","+chk[i].value;
			// alert(i);
			drow = tbl.rows[i+1];
			$(drow).remove();
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	chk = document.getElementsByName("ids");
	var iconLabel="";
	for(i=0;i<chk.length;i++){
		tbl.rows[i+1].cells[0].innerHTML=(i+1);
		iconLabel="";
		iconLabel+='<a href="#" onclick="open_product(\''+path+'\','+(i+1)+');">';
		iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
		tbl.rows[i+1].cells[5].innerHTML=iconLabel;
	}
}
