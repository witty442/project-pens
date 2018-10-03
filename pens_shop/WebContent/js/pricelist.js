function prepare(path,id){
	document.pricelistForm.action = path + "/jsp/pricelistAction.do?do=prepare&id="+id;
	document.pricelistForm.submit();
	return true;
}

function search(path){
	if(!datedifference(document.getElementById('effectiveDate'),document.getElementById('effectiveToDate'))){return false;}
	
	document.pricelistForm.action = path + "/jsp/pricelistAction.do?do=search&rf=Y";
	document.pricelistForm.submit();
	return true;
}

function backsearch(path) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.pricelistForm.action = path + "/jsp/pricelistAction.do?do=search";
	} else {
		document.pricelistForm.action = path + "/jsp/pricelist.do";
	}
	document.pricelistForm.submit();
	return true;
}

function clearForm(path){
	document.pricelistForm.action = path + "/jsp/pricelistAction.do?do=clearForm";
	document.pricelistForm.submit();
	return true;
}

function viewPrice(path, id){
	document.pricelistForm.action = path + "/jsp/pricelistAction.do?do=viewprice&id="+id;
	document.pricelistForm.submit();
	return true;
}
