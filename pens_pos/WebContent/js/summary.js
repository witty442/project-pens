function prepare(path,type,id){
	if(id!=null){
		document.summaryForm.action = path + "/jsp/summaryAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.summaryForm.action = path + "/jsp/summaryAction.do?do=prepare"+"&action="+type;
	}
	document.summaryForm.submit();
	return true;
}


function search(path){
	document.summaryForm.action = path + "/jsp/summaryAction.do?do=search&rf=Y";
	document.summaryForm.submit();
	return true;
}

function exportExcel(path){
	document.summaryForm.action = path + "/jsp/summaryAction.do?do=export";
	document.summaryForm.submit();
	return true;
}

function backsearch(path) {

}

function save(path) {
	
}

function clearForm(path){
	document.summaryForm.action = path + "/jsp/summaryAction.do?do=clear";
	document.summaryForm.submit();
	return true;
}

