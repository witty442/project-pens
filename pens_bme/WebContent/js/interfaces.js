function reGen(path, type) {
	document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=reGen&rf=N&action=submited";
	document.interfacesForm.submit();
	return true;
}
function importData(path, type) {
	var confirmText = "�׹�ѹ��� Import ����к� ";
	var chImportAll = document.getElementsByName("monitorBean.importAll")[0];
	var requestTable =document.getElementsByName("monitorBean.requestTable")[0];
	var requestUserName =document.getElementsByName("monitorBean.requestImportUserName")[0];
	if(type =='admin'){
		//alert(requestTable.value);
	   if( requestTable.value == 'm_inventory_onhand|TRANSACTION' ){
			if(requestUserName.value ==''){
				requestUserName.focus();
			    alert("��س��к� User ����ͧ��� update")	;
			    return false;
			}
	   }
	   
	   confirmText += " TableName:"+requestTable.value ;
		if(chImportAll.checked){
			confirmText += " �Ӣ����������������� (ALL) "; 
		}
	}
	if(confirm(confirmText)){
		document.interfacesForm.action = path + "/jsp/interfacesAction.do?do=importData&rf=N&action=submited";
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

