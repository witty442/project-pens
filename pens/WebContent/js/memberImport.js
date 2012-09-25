function memberUpload(path){
	var form = document.memberImportForm;

	var extension = '';
	if(form.memberFile.value.indexOf(".") > 0){
		extension = form.memberFile.value.substring(form.memberFile.value.lastIndexOf(".") + 1).toLowerCase();
		//alert(extension);
	}

	if(form.memberFile.value == '' || extension != "xls"){
		alert("กรุณาเลือกไฟล์นามสกุล .xls");
		return;
	}

	form.action = path + "/jsp/memberImportAction.do?do=memberUpload";
	form.submit();
	return true;
}

function doEditMember(id){
	var form = document.memberImportForm;

	if(form.oldId.value != "" && form.oldId.value != id){
		//alert("กรุณากดปุ่มบันทึกหรือยกเลิกข้อมูลที่กำลังแก้ไขก่อน");
		//return;

		doCancelMember(form.oldId.value);
	}

	var orderLineRemain = document.getElementById("orderLineRemain" + id);
	var startNextYear = document.getElementById("startNextYear" + id);
	var edit = document.getElementById("edit" + id);
	var save = document.getElementById("save" + id);

	orderLineRemain.disabled = false;
	orderLineRemain.style.border = "1px solid #7F9DB9";
	startNextYear.disabled = false;
	startNextYear.style.border = "1px solid #7F9DB9";
	edit.style.display = "none";
	save.style.display = "inline";

	form.oldId.value = id;
	form.oldOrderLineRemain.value = orderLineRemain.value;
	form.oldStartNextYear.value = startNextYear.value;
	form.btnImport.disabled = true;
	form.btnBack.disabled = true;
}

function doSaveMember(id, path){
	var form = document.memberImportForm;

	var orderLineRemain = document.getElementById("orderLineRemain" + id);
	var startNextYear = document.getElementById("startNextYear" + id);

	form.currentId.value = id;
	form.currentOrderLineRemain.value = orderLineRemain.value;
	form.currentStartNextYear.value = startNextYear.value;

	form.action = path + "/jsp/memberImportAction.do?do=memberUploadEdit";
	form.submit();
	return true;
}

function doCancelMember(id){
	var form = document.memberImportForm;

	var orderLineRemain = document.getElementById("orderLineRemain" + id);
	var startNextYear = document.getElementById("startNextYear" + id);
	var edit = document.getElementById("edit" + id);
	var save = document.getElementById("save" + id);

	orderLineRemain.value = form.oldOrderLineRemain.value;
	startNextYear.value = form.oldStartNextYear.value;

	orderLineRemain.disabled = true;
	orderLineRemain.style.border = "none";
	startNextYear.disabled = true;
	startNextYear.style.border = "none";
	edit.style.display = "inline";
	save.style.display = "none";

	form.oldId.value = "";
	form.oldOrderLineRemain.value = "";
	form.oldStartNextYear.value = "";
	form.btnImport.disabled = false;
	form.btnBack.disabled = false;
}

function doCheckAll(isOnload){
	var form = document.memberImportForm;

	if(form.ids != "undefined" && form.ids != null){
		if(form.ids.length != "undefined" && form.ids.length > 0){
			//alert("more than 1 row");
			if(form.checkAll.checked){
				for(i=0; i<form.ids.length; i++){
					form.ids[i].checked = true;
					if(!isOnload){
						if(form.tempSelectedIds.value.indexOf("[" + form.ids[i].value + "],") == -1){
							form.tempSelectedIds.value += "[" + form.ids[i].value + "],";
						}
					}
				}
			}else{
				for(i=0; i<form.ids.length; i++){
					form.ids[i].checked = false;
				}
	
				if(!isOnload){
					form.tempSelectedIds.value = "";
				}
			}
		}else{
			//alert("1 row");
			if(form.ids.checked != "undefined"){
				if(form.checkAll.checked){
					form.ids.checked = true;
				}else{
					form.ids.checked = false;
	
					if(!isOnload){
						form.tempSelectedIds.value = "";
					}
				}
			}
		}

		doValidateCheckBetweenPage();
	}
}

function doValidateCheckBetweenPage(){
	var form = document.memberImportForm;

	//keep checked between page
	if(form.tempSelectedIds.value != ""){
		var ids = form.tempSelectedIds.value.split(",");
		//alert("tempSelectedIds.length: " + ids.length);

		if(form.ids.length != "undefined" && form.ids.length > 0){
			//alert("more than 1 row");
			for(i=0; i<ids.length; i++){
				for(j=0; j<form.ids.length; j++){
					if(ids[i].replace("[", "").replace("]", "") == form.ids[j].value){
						form.ids[j].checked = true;
					}
				}
			}
		}else{
			//alert("1 row");
			if(form.ids.checked != "undefined"){
				for(i=0; i<ids.length; i++){
					if(ids[i].replace("[", "").replace("]", "") == form.ids.value){
						form.ids.checked = true;
					}
				}
			}
		}
	}
}

function doValidateCheck(){
	var form = document.memberImportForm;

	if(form.ids.length != "undefined" && form.ids.length > 0){
		//alert("more than 1 row");
		var countChecked = 0;
		for(i=0; i<form.ids.length; i++){
			if(!form.ids[i].checked){
				form.tempSelectedIds.value = form.tempSelectedIds.value.replace("[" + form.ids[i].value + "],", "");
				countChecked--;
			}else{
				if(form.tempSelectedIds.value.indexOf("[" + form.ids[i].value + "],") == -1){
					form.tempSelectedIds.value += "[" + form.ids[i].value + "],";
				}
				countChecked++;
			}
		}

		//alert(form.tempSelectedIds.value);

		//compare checked
		if(countChecked != form.ids.length){
			form.checkAll.checked = false;
		}else{
			form.checkAll.checked = true;
		}
	}else{
		//alert("1 row");
		if(form.ids.checked != "undefined"){
			if(!form.ids.checked){
				form.checkAll.checked = false;
			}else{
				form.checkAll.checked = true;
			}
		}
	}
}

function isSelectedData(){
	var form = document.memberImportForm;

	if(form.ids != "undefined" && form.ids != null){
		if(form.ids.length != "undefined" && form.ids.length > 0){
			//alert("more than 1 row");
			for(i=0; i<form.ids.length; i++){
				if(form.ids[i].checked){
					return true;
				}
			}
		}else{
			//alert("1 row");
			if(form.ids.checked != "undefined" && form.ids.checked){
				return true;
			}
		}
	}

	if(form.tempSelectedIds.value != ""){
		return true;
	}

	return false;
}

function memberImport(path){
	var form = document.memberImportForm;

	if(!isSelectedData()){
		alert("กรุณาเลือกรายการที่ต้องการ Import");
		return;
	}

	form.action = path + "/jsp/memberImportAction.do?do=memberImport";
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.memberImportForm;
	form.reset();
	form.action = path + "/jsp/memberImportAction.do?do=clearForm";
	form.submit();
	return true;
}
