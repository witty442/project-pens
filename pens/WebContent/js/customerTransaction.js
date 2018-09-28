function toOrder(path, customerId, type){
	var searchKey = document.getElementsByName('criteria.searchKey')[0].value;
	if(type == 'cus'){
		window.location = path+'/jsp/saleOrderAction.do?do=prepare&customerId='+customerId+"&key="+searchKey;
	}else if(type=='mem'){
		window.location = path+'/jsp/saleOrderAction.do?do=prepare&memberId='+customerId+"&key="+searchKey+"&action=search";
	}
}

function toReceipt(path, customerId, type){
	var searchKey = document.getElementsByName('criteria.searchKey')[0].value;
	if(type == 'cus'){
		window.location = path+'/jsp/receiptAction.do?do=prepare&customerId='+customerId+"&key="+searchKey;
	}else if(type=='mem'){
		window.location = path+'/jsp/receiptAction.do?do=prepare&memberId='+customerId+"&key="+searchKey+"&action=search";
	}
}

function toVisit(path, customerId, type, from){
	var searchKey = document.getElementsByName('criteria.searchKey')[0].value;
	window.location = path + '/jsp/customervisitAction.do?do=prepare&action=search&customerId='+customerId+"&key="+searchKey+"&type="+type+"&from="+from;
}

function toProduct(path, customerId, type){
	var searchKey = document.getElementsByName('criteria.searchKey')[0].value;
	window.location = path+'/jsp/product/productC4.jsp?&customerId='+customerId+"&key="+searchKey+"&type="+type;
}


function toCreateNewOrder(path,type,id){
	document.customerForm.action = path + "/jsp/saleOrderAction.do?do=prepare&shotcut_customerId=" + id+"&action="+type;
	document.customerForm.submit();
	return true;
}
function toCreateNewOrderSpecial(path,type,id){
	document.customerForm.action = path + "/jsp/saleOrderSpecialAction.do?do=prepare&shotcut_customerId=" + id+"&action="+type;
	document.customerForm.submit();
	return true;
}
function toCreateNewReceipt(path,type,id){
	document.customerForm.action = path + "/jsp/receiptAction.do?do=prepare&shotcut_customerId=" + id+"&action="+type;
	document.customerForm.submit();
	return true;
}

function toCreateNewReqPromotion(path,id,fromPage){
	document.customerForm.action = path + "/jsp/requestPromotionAction.do?do=createRequestPromotion&shotcut_customerId=" +id+"&fromPage="+fromPage;
	document.customerForm.submit();
	return true;
}