function reGen(path, type) {
	document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=reGen&rf=N&action=submited";
	document.interfacesForm.submit();
	return true;
}
function syschronizeFromOracle(path, type) {
	var confirmText = "ยืนยันการ Import เข้าระบบ Sales";
	var chImportAll = document.getElementsByName("monitorBean.importAll")[0];
	var requestTable =document.getElementsByName("monitorBean.requestTable")[0];
	var requestUserName =document.getElementsByName("monitorBean.requestImportUserName")[0];
	if(type =='admin'){
		//alert(requestTable.value);
	   if( requestTable.value == 'm_inventory_onhand|TRANSACTION' ){
			if(requestUserName.value ==''){
				requestUserName.focus();
			    alert("กรุณาระบุ User ที่ต้องการ update")	;
			    return false;
			}
	   }
	   
	   confirmText += " TableName:"+requestTable.value ;
		if(chImportAll.checked){
			confirmText += " นำข้อมูลเข้าใหม่ทั้งหมด (ALL) "; 
		}
	}
	if(confirm(confirmText)){
		document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=syschronizeFromOracle&rf=N&action=submited";
		document.interfacesForm.submit();
		return true;
	}
	return false;
}

function updateSalesTransaction(path, type) {
	var confirmText = "ยืนยันการ Update Sales Transaction";
	var requestUserName =document.getElementsByName("monitorBean.requestImportUpdateUserName")[0];
	var requestUpdateSalesTable =document.getElementsByName("monitorBean.requestUpdateSalesTable")[0];
	var chImportAll = document.getElementsByName("monitorBean.importAll")[0];
	if(type =='admin'){
		if(requestUserName.value ==''){
			requestUserName.focus();
		    alert("กรุณาระบุ User ที่ต้องการ update")	;
		    return false;
		}
		confirmText += " TableName:"+requestUpdateSalesTable.value +" User Name :"+requestUserName.value;
		if(chImportAll.checked){
			confirmText += " นำข้อมูลเข้าใหม่ทั้งหมด (ALL) "; 
		}
	}
	if(confirm(confirmText)){
		document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=updateSalesTransaction&rf=N&action=submited";
		document.interfacesForm.submit();
		return true;
	}
	return false;
}

function importWebMember(path, type) {
	var confirmText = "ยืนยันการ Import Web Member";
	var requestUserName =document.getElementsByName("monitorBean.requestImportWebMemberUserName")[0];
	var requestUpdateSalesTable =document.getElementsByName("monitorBean.requestWebMemberTable")[0];
	var chImportAll = document.getElementsByName("monitorBean.importAll")[0];
	if(type =='admin'){
		if(requestUserName.value ==''){
			requestUserName.focus();
		    alert("กรุณาระบุ User ที่ต้องการ Import")	;
		    return false;
		}
		confirmText += " TableName:"+requestUpdateSalesTable.value +" User Name :"+requestUserName.value;
		if(chImportAll.checked){
			confirmText += " นำข้อมูลเข้าใหม่ทั้งหมด (ALL) "; 
		}
	}
	if(confirm(confirmText)){
		document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=importWebMember&action=submited";
		document.interfacesForm.submit();
		return true;
	}
	return false;
}


function syschronizeToOracle(path, type) {
	var confirmText = "ยืนยันการ Export";
	if(type =='admin'){
	  var requestTable =document.getElementsByName("monitorBean.requestExportTable")[0];
	  var requestExportUserName =document.getElementsByName("monitorBean.requestExportUserName")[0];
	 
	  if(   requestTable.value == 'm_customer|MASTER' || requestTable.value == 't_order|TRANSACTION'
		 || requestTable.value == 't_receipt|TRANSACTION' || requestTable.value == 't_visit|TRANSACTION' ){
			if(requestExportUserName.value ==''){
				requestExportUserName.focus();
			    alert("กรุณาระบุ User Name ที่ต้องการ Export");
			    return false;
			}
			confirmText += " Export TableName:"+requestTable.value+ " UserName :"+requestExportUserName.value;
	   }
	  
	}
	if(confirm(confirmText)){
		document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=syschronizeToOracle&rf=N&";
		document.interfacesForm.submit();
		return true;
	}
	return false;
}

function search(path, type) {
	document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=search&rf=N&";
	document.interfacesForm.submit();
	return true;
}

function searchDetail(path, type,value) {
	document.interfacesForm.action = path + "/jsp/interfacesAction.do?monitor_id="+value+"&do=searchDetail&rf=N&";
	document.interfacesForm.submit();
	return true;
}

function clearForm(path, type) {
	document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=prepare&type="+type;
	document.interfacesForm.submit();
	return true;
}

function save(path){
	
	document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=save";
	document.interfacesForm.submit();
	return true;
}

function backToInterfaces(path){
	document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=search&rf=Y&";
	document.interfacesForm.submit();
	return true;
}

function getLog(path,transType,fileName){
	var param = "&fileName="+fileName+"&transType="+transType;
	var url = path+"/jsp/interfacesAction.do?do=getLog"+param;
    window.open(url,"",
			   "menubar=yes,resizable=no,toolbar=no,scrollbars=yes,width=800px,height=500px,status=yes,left="+ 50 + ",top=" + 0);
}

function getTextFile(path,transType,fileName,status,type){
	var param = "&fileName="+fileName+"&transType="+transType+"&status="+status+"&type="+type;
	var url = path+"/jsp/interfacesAction.do?do=getTextFile"+param;
    window.open(url,"",
			   "menubar=yes,resizable=no,toolbar=no,scrollbars=yes,width=800px,height=500px,status=yes,left="+ 50 + ",top=" + 0);
}

function showItemExport(path,monitorItemId,tableName){
	var param = "&monitorItemId="+monitorItemId+"&tableName="+tableName;
	var url = path+"/jsp/interfacesAction.do?do=showItemExport"+param;
    window.open(url,"",
			   "menubar=yes,resizable=no,toolbar=no,scrollbars=yes,width=800px,height=500px,status=yes,left="+ 50 + ",top=" + 0);
}

