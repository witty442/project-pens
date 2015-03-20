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