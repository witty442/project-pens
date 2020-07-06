function search(path,type){
	document.memberRenewForm.action = path + "/jsp/memberRenewAction.do?do=search&rf=Y&type="+type;
	document.memberRenewForm.submit();
	return true;
}

function clearForm(path){
	document.memberRenewForm.action = path + "/jsp/memberRenewAction.do?do=clearForm";
	document.memberRenewForm.submit();
	return true;
}

function prepare(path,action,id){
	document.memberRenewForm.action = path + "/jsp/memberAction.do?do=prepare&id="+id+"&action="+action;
	document.memberRenewForm.submit();
	return true;
}

function open_renew(path, id, type){
	window.open(path + "/jsp/pop/memberRenewPopup.jsp?id="+id+"&type="+type, "Member Renew", "width=939,height=300,location=No,resizable=No");
}

function open_renew_follow(path, id){
	window.open(path + "/jsp/pop/memberFollowPopup.jsp?id="+id, "Member Renew Follow", "width=939,height=500,location=No,resizable=No");
}




