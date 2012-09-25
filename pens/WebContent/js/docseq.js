function prepare(path, id) {
	document.docSeqForm.action = path + "/jsp/docseqAction.do?do=prepare&id=" + id;
	document.docSeqForm.submit();
	return true;
}

function save(path) {
	document.docSeqForm.action = path + "/jsp/docseqAction.do?do=save";
	document.docSeqForm.submit();
	return true;
}

function genseq(path) {
	document.docSeqForm.action = path + "/jsp/docseqAction.do?do=genSeq";
	document.docSeqForm.submit();
	return true;
}

function backsearch(path) {
	document.docSeqForm.action = path + "/jsp/docseqAction.do?do=search";
	document.docSeqForm.submit();
	return true;
}