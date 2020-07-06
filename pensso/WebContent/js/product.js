function prepare(path,id){
	document.productForm.action = path + "/jsp/productAction.do?do=prepare&id="+id;
	document.productForm.submit();
	return true;
}


function search(path){
	document.productForm.action = path + "/jsp/productAction.do?do=search&rf=Y";
	document.productForm.submit();
	return true;
}

function backsearch(path) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.productForm.action = path + "/jsp/productAction.do?do=search";
	} else {
		document.productForm.action = path + "/jsp/product.do";
	}
	document.productForm.submit();
	return true;
}

function clearForm(path){
	document.productForm.action = path + "/jsp/productAction.do?do=clearForm";
	document.productForm.submit();
	return true;
}

function viewPrice(path, id){
	document.productForm.action = path + "/jsp/productAction.do?do=viewprice&id="+id;
	document.productForm.submit();
	return true;
}