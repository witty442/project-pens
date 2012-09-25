
function prepare(path,id){
	document.salestargetForm.action = path + "/jsp/salestargetAction.do?do=prepare&id=" + id;
	document.salestargetForm.submit();
	return true;
}


function search(path){
	document.salestargetForm.action = path + "/jsp/salestargetAction.do?do=search&rf=Y";
	document.salestargetForm.submit();
	return true;
}

function backsearch(path) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.salestargetForm.action = path + "/jsp/salestargetAction.do?do=search";
	} else {
		document.salestargetForm.action = path + "/jsp/salestarget.do";
	}
	document.salestargetForm.submit();
	return true;
}

function clearForm(path){
	document.salestargetForm.action = path + "/jsp/salestargetAction.do?do=clearForm";
	document.salestargetForm.submit();
	return true;
}
