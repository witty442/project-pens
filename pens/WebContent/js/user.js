function prepare(path, id) {
	document.userForm.action = path + "/jsp/userAction.do?do=prepare&id=" + id;
	document.userForm.submit();
	return true;
}

function search(path) {
	document.userForm.action = path + "/jsp/userAction.do?do=search&rf=Y";
	document.userForm.submit();
	return true;
}

function save(path) {
	if (document.getElementsByName('user.type')[0].value == '') {
		document.getElementsByName('user.type')[0].focus();
		alert('กรุณาใส่บทบาท');
		return false;
	}
	if (Trim(document.getElementsByName('user.password')[0].value) == '') {
		document.getElementsByName('user.password')[0].focus();
		alert('กรุณาใส่รหัสผ่าน');
		return false;
	}
	document.userForm.action = path + "/jsp/userAction.do?do=save";
	document.userForm.submit();
	return true;
}

function profileSave(path) {
	document.userForm.action = path + "/jsp/userAction.do?do=profileSave";
	document.userForm.submit();
	return true;
}

function backsearch(path) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.userForm.action = path + "/jsp/userAction.do?do=search";
	} else {
		document.userForm.action = path + "/jsp/user.do";
	}
	document.userForm.submit();
	return true;
}

function clearForm(path) {
	document.userForm.action = path + "/jsp/userAction.do?do=clearForm";
	document.userForm.submit();
	return true;
}

function changeActive(path, active) {
	document.userForm.action = path + "/jsp/userAction.do?status=" + active
			+ "&do=changeActive";
	document.userForm.submit();
	return true;
}