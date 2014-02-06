function prepare(path, id) {
	document.userForm.action = path + "/jsp/userAction.do?do=prepare&id=" + id;
	document.userForm.submit();
	return true;
}

function addNewUser(path) {
	document.userForm.action = path + "/jsp/userAction.do?do=prepare";
	document.userForm.submit();
	return true;
}

function search(path) {
	document.userForm.action = path + "/jsp/userAction.do?do=search&rf=Y";
	document.userForm.submit();
	return true;
}

function savePassword(path) {
	document.userForm.action = path + "/jsp/userAction.do?do=savePassword&rf=Y";
	document.userForm.submit();
	return true;
}

function backToMain(path) {
	document.userForm.action = path + "/jsp/userAction.do?do=backToMain";
	document.userForm.submit();
	return true;
}

function save(path) {
	if (document.getElementsByName('user.startDate')[0].value == '') {
		 document.getElementsByName('user.startDate')[0].focus();
        alert('กรุณาใส่วันที่เริ่มต้น');
        return false;
    }
	if (document.getElementsByName('user.name')[0].value == '') {
		 document.getElementsByName('user.name')[0].focus();
        alert('กรุณาใส่ชื่อ');
        return false;
    }
	 if (document.getElementsByName('user.userGroupId')[0].value == '') {
		 document.getElementsByName('user.userGroupId')[0].focus();
         alert('กรุณาใส่กลุ่ม');
         return false;
     }
	 if (document.getElementsByName('user.userName')[0].value == '') {
		 document.getElementsByName('user.userName')[0].focus();
         alert('กรุณาใส่ User Name');
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

function changePassword(path) {
	
	 /*if (document.getElementsByName('user.userName')[0].value == '') {
		 document.getElementsByName('user.userName')[0].focus();
         alert('กรุณาใส่ User Name');
         return false;
     }
	if (Trim(document.getElementsByName('user.password')[0].value) == '') {
		document.getElementsByName('user.password')[0].focus();
		alert('กรุณาใส่รหัสผ่านเดิม');
		return false;
	}*/
	if (Trim(document.getElementsByName('user.newPassword')[0].value) == '') {
		document.getElementsByName('user.newPassword')[0].focus();
		alert('กรุณาใส่รหัสผ่านใหม่');
		return false;
	}
	if (Trim(document.getElementsByName('user.reNewPassword')[0].value) == '') {
		document.getElementsByName('user.reNewPassword')[0].focus();
		alert('กรุณาใส่ยืนยันรหัสผ่านใหม่');
		return false;
	}
	document.userForm.action = path + "/jsp/userAction.do?do=changePassword";
	document.userForm.submit();
	return true;
}

function profileSave(path) {
	if (Trim(document.getElementsByName('user.password')[0].value) == '') {
		document.getElementsByName('user.password')[0].focus();
		alert('กรุณาใส่รหัสผ่าน');
		return false;
	}
	if (Trim(document.getElementsByName('user.confirmPassword')[0].value) == '') {
		document.getElementsByName('user.confirmPassword')[0].focus();
		alert('กรุณาใส่รหัสผ่านยืนยัน');
		return false;
	}
	if (document.getElementsByName('user.password')[0].value != document
			.getElementsByName('user.confirmPassword')[0].value) {
		document.getElementsByName('user.confirmPassword')[0].focus();
		alert('รหัสผ่านไม่ตรงกัน');
		return false;
	}
	
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

function changeActive(path, active,obj) {
	if(isChkOne(obj)){
		document.userForm.action = path + "/jsp/userAction.do?status=" + active
				+ "&do=changeActive";
		document.userForm.submit();
		return true;
	}else{
		alert("กรุณาเลือกรายการอย่างน้อยหนึ่งรายการ");
		return false;
	}
}

function changeUserGroup(path,obj) {
	if(isChkOne(obj)){
		var userGroupId = document.getElementsByName("user.changeUserGroup")[0].value ;
		document.userForm.action = path + "/jsp/userAction.do?userGroupId=" + userGroupId
				+ "&do=changeUserGroup";
		document.userForm.submit();
		return true;
	}else{
		alert("กรุณาเลือกรายการอย่างน้อยหนึ่งรายการ");
		return false;
	}
}

function isChkOne(obj){
	for(i=0;i<obj.length;i++){
		var chk = obj[i].checked;
		if(chk){
		  return true;
		}
	}
	return false;
}