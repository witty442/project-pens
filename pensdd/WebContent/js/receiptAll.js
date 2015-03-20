function search(path){
	document.receiptAllForm.action = path + "/jsp/receiptAllAction.do?do=search&rf=Y";
	document.receiptAllForm.submit();
	return true;
}

function prepare(path,type,id){
	if(id!=null){
		document.receiptAllForm.action = path + "/jsp/receiptAllAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.receiptAllForm.action = path + "/jsp/receiptAllAction.do?do=prepare"+"&action="+type;
	}
	document.receiptAllForm.submit();
	return true;
}

function clearForm(path){
	document.receiptAllForm.action = path + "/jsp/receiptAllAction.do?do=clearForm";
	document.receiptAllForm.submit();
	return true;
}

function save(path,total) {
	var confirmDate = document.getElementById("confirmDate").value;
	if(confirmDate == null || confirmDate ==''){
		alert("กรุณากำหนดวันที่ยืนยันการรับชำระ");
		return false;
	}
	
	var isChecked = false;
	for(var i=0;i < total ; i++){
		isChecked = document.getElementById('confirms'+i+'.isConfirm').checked;
		if(isChecked)
			break;
	}
	
	if(!isChecked){
		alert("กรุณาเลือกรายการที่ต้องการรับชำระ");
		return false;
	}
	else{
		document.receiptAllForm.action = path + "/jsp/receiptAllAction.do?do=save";
		document.receiptAllForm.submit();
		return true;
	}
}
