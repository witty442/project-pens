function prepare(path,id){
	document.inventoryForm.action = path + "/jsp/inventoryAction.do?do=prepare&id="+id;
	document.inventoryForm.submit();
	return true;
}


function search(path){
	document.inventoryForm.action = path + "/jsp/inventoryAction.do?do=search&rf=Y";
	document.inventoryForm.submit();
	return true;
}

function searchSub(path){
	document.inventoryForm.action = path + "/jsp/inventoryAction.do?do=searchSub&rf=Y";
	document.inventoryForm.submit();
	return true;
}

function backsearch(path) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.inventoryForm.action = path + "/jsp/inventoryAction.do?do=search";
	} else {
		document.inventoryForm.action = path + "/jsp/inventory.do";
	}
	document.inventoryForm.submit();
	return true;
}

function clearForm(path){
	document.inventoryForm.action = path + "/jsp/inventoryAction.do?do=clearForm";
	document.inventoryForm.submit();
	return true;
}

function clearFormSub(path){
	document.inventoryForm.action = path + "/jsp/inventoryAction.do?do=clearFormSub";
	document.inventoryForm.submit();
	return true;
}