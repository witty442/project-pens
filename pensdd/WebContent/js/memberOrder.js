function searchKeypress(e,path){
	if(e != null && e.keyCode == 13){
		search(path);
	}
}

function nextKeypress(e,path){
	if(e != null && e.keyCode == 13){
		$('#memberCode').focus();
	}
}

function search(path){
	if ($('#orderNo').val()=='' && $('#memberCode').val()=='') {
		alert('ใส่เงื่อนไขอย่างน้อย 1 อย่าง');
		return false;
	}
	document.orderForm.action = path + "/jsp/memberOrderAction.do?do=searchMO&rf=Y";
	document.orderForm.submit();
	return true;
}

function clearForm(path){
	document.orderForm.action = path + "/jsp/memberOrderAction.do?do=clearForm";
	document.orderForm.submit();
	return true;
}


function prepare(path, id) {
	if (id != null) {
		window.open(path + "/jsp/memberOrderAction.do?do=prepareMO&id="+id, "EditOrder", "width=1024,height=768,location=No,resizable=No");
	}
	return true;
}
