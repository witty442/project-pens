function save(path) {
	document.sysConfigForm.action = path + "/jsp/sysconfigAction.do?do=save";
	document.sysConfigForm.submit();
	return true;
}
