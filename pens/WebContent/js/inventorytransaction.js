function prepare(path,id){
	document.inventoryTransactionForm.action = path + "/jsp/inventoryTransactionAction.do?do=prepare&id="+id;
	document.inventoryTransactionForm.submit();
	return true;
}


function search(path){
	document.inventoryTransactionForm.action = path + "/jsp/inventoryTransactionAction.do?do=search&rf=Y";
	document.inventoryTransactionForm.submit();
	return true;
}

function backsearch(path) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.inventoryTransactionForm.action = path + "/jsp/inventoryTransactionAction.do?do=search";
	} else {
		document.inventoryTransactionForm.action = path + "/jsp/inventoryTransaction.do";
	}
	document.inventoryTransactionForm.submit();
	return true;
}

function clearForm(path){
	document.inventoryTransactionForm.action = path + "/jsp/inventoryTransactionAction.do?do=clearForm";
	document.inventoryTransactionForm.submit();
	return true;
}

