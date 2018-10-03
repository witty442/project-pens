function prepare(path,type,id){
	if(id!=null){
		document.moveOrderSummaryForm.action = path + "/jsp/moveOrderSummaryAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.moveOrderSummaryForm.action = path + "/jsp/moveOrderSummaryAction.do?do=prepare"+"&action="+type;
	}
	document.summaryForm.submit();
	return true;
}


function search(path){
	document.moveOrderSummaryForm.action = path + "/jsp/moveOrderSummaryAction.do?do=search&rf=Y";
	document.moveOrderSummaryForm.submit();
	return true;
}

function exportExcel(path){
	document.moveOrderSummaryForm.action = path + "/jsp/moveOrderSummaryAction.do?do=export";
	document.moveOrderSummaryForm.submit();
	return true;
}

function backsearch(path) {

}

function save(path) {
	
}

function clearForm(path){
	document.moveOrderSummaryForm.action = path + "/jsp/moveOrderSummaryAction.do?do=clear";
	document.moveOrderSummaryForm.submit();
	return true;
}

