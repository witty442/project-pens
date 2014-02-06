function prepare(path, id) {
	document.roleForm.action = path + "/jsp/roleAction.do?do=prepare&id=" + id;
	document.roleForm.submit();
	return true;
}

function search(path) {
	document.roleForm.action = path + "/jsp/roleAction.do?do=search&rf=Y";
	document.roleForm.submit();
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
	document.roleForm.action = path + "/jsp/roleAction.do?do=save";
	document.roleForm.submit();
	return true;
}

function addRole(path,roleId) {
	document.roleForm.action = path + "/jsp/roleAddAction.do?do=addRole&roleId="+roleId;
	document.roleForm.submit();
	return true;
}

function editRole(path,roleId,roleName) {
	document.roleForm.action = path + "/jsp/roleAddAction.do?do=editRole&roleId="+roleId;;
	document.roleForm.submit();
	return true;
}

function backsearch(path) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.roleForm.action = path + "/jsp/roleAction.do?do=search";
	} else {
		document.roleForm.action = path + "/jsp/user.do";
	}
	document.roleForm.submit();
	return true;
}

function clearForm(path) {
	document.roleForm.action = path + "/jsp/roleAction.do?do=clearList";
	document.roleForm.submit();
	return true;
}

function backToMain(path) {
	document.roleForm.action = path + "/jsp/roleAction.do?do=backToMain";
	document.roleForm.submit();
	return true;
}

