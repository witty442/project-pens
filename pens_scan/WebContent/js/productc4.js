function backToCusotmer(path,customerId,type) {
	if(type=='mem'){
		window.location = path+'/jsp/memberAction.do?do=prepare&id='+customerId+'&action=process';
	}else{
		window.location = path+'/jsp/customerAction.do?do=prepare&id='+customerId+'&action=process';
	}
}

function toOrder(path, customerId, type){
	var searchKey = document.getElementsByName('criteria.searchKey')[0].value;
	var pobj = document.getElementsByName('productids');
	var pid = "";
	for(i=0;i<pobj.length;i++)
	{
		if(pobj[i].checked)
			pid+=','+pobj[i].value;
	}
	if(pid.length>0)pid=pid.substring(1, pid.length);
	var locations="";
	if(type == 'cus'){
		locations = path+'/jsp/saleOrderAction.do?do=prepare&customerId='+customerId+"&key="+searchKey;
	}else if(type=='mem'){
		locations = path+'/jsp/saleOrderAction.do?do=prepare&memberId='+customerId+"&key="+searchKey+"&action=search";
	}
	if(pid.length>0)
		locations+="&productIds="+pid;
	window.location=locations;
}