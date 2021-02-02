

function search(path, type) {
	document.interfacesForm.action = path + "/jsp/monitorInterfacesAction.do?do=search&rf=N&";
	document.interfacesForm.submit();
	return true;
}


function clearForm(path, type) {
	document.interfacesForm.action = path + "/jsp/monitorInterfacesAction.do?do=prepare&type="+type;
	document.interfacesForm.submit();
	return true;
}

function save(path){
	
	document.interfacesForm.action = path + "/jsp/monitorInterfacesAction.do?do=save";
	document.interfacesForm.submit();
	return true;
}

function getLog(path,transType,fileName,userName){
	var param = "&fileName="+fileName+"&transType="+transType+"&userName="+userName;
	var url = path+"/jsp/monitorInterfacesAction.do?do=getLog"+param;
    window.open(url,"",
			   "menubar=yes,resizable=no,toolbar=no,scrollbars=yes,width=800px,height=500px,status=yes,left="+ 50 + ",top=" + 0);
}


function showItemExport(path,monitorItemId,tableName){
	var param = "&monitorItemId="+monitorItemId+"&tableName="+tableName;
	var url = path+"/jsp/monitorInterfacesAction.do?do=showItemExport"+param;
    window.open(url,"",
			   "menubar=yes,resizable=no,toolbar=no,scrollbars=yes,width=800px,height=500px,status=yes,left="+ 50 + ",top=" + 0);
}

function getTextFile(path,transType,fileName,status,type){
	var param = "&fileName="+fileName+"&transType="+transType+"&status="+status+"&type="+type;
	var url = path+"/jsp/monitorInterfacesAction.do?do=getTextFile"+param;
    window.open(url,"",
			   "menubar=yes,resizable=no,toolbar=no,scrollbars=yes,width=800px,height=500px,status=yes,left="+ 50 + ",top=" + 0);
}

