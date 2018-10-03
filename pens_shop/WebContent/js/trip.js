function processCustomer(path,type,id){
	var tf = document.getElementsByName('tripFlag')[0].value;
	if(id!=null){
		window.location = path + "/jsp/customerAction.do?do=prepare&id=" + id+"&action="+type+'&tf='+tf;
	}else{
		window.location = path + "/jsp/customerAction.do?do=prepare"+"&action="+type+'&tf='+tf;
	}
	
	return true;
}

function open_copy_trip(path){
	window.open(path + "/jsp/pop/copyTripPopup.jsp", "Copy Trip", "width=939,height=320,location=No,resizable=No");
}
function open_copy_trip_month(path){
	window.open(path + "/jsp/pop/copyTripMonthPopup.jsp", "Copy Trip", "width=939,height=320,location=No,resizable=No");
}
function search(path, type) {
	if(type=='admin'){
		if($('#uCode').val()==''){
			alert('��س�������͹�㹡�ä���');
			$('#uCode').focus();
			return;
		}
	}
	
	if($('#tripDateFrom').val()==''){
		alert('��س�������͹�㹡�ä���');
		$('#tripDateFrom').focus();
		return;
	}
	if($('#tripDateTo').val()==''){
		alert('��س�������͹�㹡�ä���');
		$('#tripDateTo').focus();
		return;
	}
	if(!datedifference(document.getElementById('tripDateFrom'),document.getElementById('tripDateTo'))){return false;}
	
	document.tripForm.action = path + "/jsp/tripAction.do?do=search&rf=Y&type="+type;
	document.tripForm.submit();
	return true;
}


function clearForm(path, type) {
	document.tripForm.action = path + "/jsp/tripAction.do?do=prepare&type="+type;
	document.tripForm.submit();
	return true;
}

function addTrip(){
	// show input for new record.
	$('#div_new_record').slideDown(300);
	
	new Epoch('epoch_popup','th',document.getElementById('trip_date'));
}

function save(path){
	if($('#trip_date').val()==''){
		alert('��س��кآ��������ú��ǹ');
		$('#trip_date').focus();
		return;
	}
	if($('#cCode').val()=='' || $('#cName').val()==''){
		alert('��س��кآ��������ú��ǹ');
		$('#cCode').focus();
		return;
	}
	if(checkCustomerCode(document.getElementsByName('customerCode'))){
		alert('�����ū��');
		return;
	}
	document.tripForm.action = path + "/jsp/tripAction.do?do=save";
	document.tripForm.submit();
	return true;
}
function checkCustomerCode(code){
	var i=0;
	var result = false;
	for(i=0;i<code.length;i++){
		if($('#cCode').val()==code[i].value){
			result=true;
		}
	}
	
	return result;
}
function deleteTrip(path){
	var i,count=0;
	var ids = document.getElementsByName('ids');
	var tripId = document.getElementsByName('tripId');
	
	for(i=0;i<ids.length;i++){
		if(ids[i].checked){
			tripId[0].value += ids[i].value + ",";
			count++;
		}
	}
	
	if(count==0){
		alert('��س����͡���ҧ���� 1 ������');
		return;
	}
	
	if(!confirm('��ͧ���ź������?')){
		return;
	}
	
	document.tripForm.action = path + "/jsp/tripAction.do?do=delete";
	document.tripForm.submit();
	return true;
}

function cancel(){
	$('#div_new_record').slideUp(300);
	$('#div_calendar_show').show();
	$('#div_calendar_hide').hide();
}