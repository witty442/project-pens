function prepare(path,id){
	document.modifierForm.action = path + "/jsp/modifierAction.do?do=prepare&id="+id;
	document.modifierForm.submit();
	return true;
}


function search(path){
	if(!datedifference(document.getElementById('startDate'),document.getElementById('endDate'))){return false;}
	
	document.modifierForm.action = path + "/jsp/modifierAction.do?do=search&rf=Y";
	document.modifierForm.submit();
	return true;
}

function backsearch(path) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.modifierForm.action = path + "/jsp/modifierAction.do?do=search";
	} else {
		document.modifierForm.action = path + "/jsp/modifier.do";
	}
	document.modifierForm.submit();
	return true;
}

function clearForm(path){
	document.modifierForm.action = path + "/jsp/modifierAction.do?do=clearForm";
	document.modifierForm.submit();
	return true;
}

function view(path, id){
	document.modifierForm.action = path + "/jsp/modifierAction.do?do=prepare&id="+id;
	document.modifierForm.submit();
	return true;
}

function showDetail(path,type,id){
	if(type=='DIS')
		window.open(path + "/jsp/pricePromotion/promotion_discount.jsp?lineId="+id, "Discount", "width=300,height=200,location=No,resizable=No");
	else if (type=='PBH')
		window.open(path + "/jsp/pricePromotion/promotion_pricebreak.jsp?lineId="+id, "Pricebreak", "width=500,height=400,location=No,resizable=No");
	else if (type=='PRG')
		window.open(path + "/jsp/pricePromotion/promotion_promotionalgood.jsp?lineId="+id, "PromotionalGood", "width=500,height=300,location=No,resizable=No");
	else
		;
}