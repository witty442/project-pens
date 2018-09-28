
function search(path){
    var valid = true;
	if(document.getElementsByName("bean.day")[0].value ==""){
		alert("กรุณากรอกวันที่");
		document.getElementsByName("bean.day")[0].focus();
		valid = false;
	}
	if(document.getElementsByName("bean.day")[0].value !=""
		&& document.getElementsByName("bean.dayTo")[0].value !=""){
		var mmDay = getMonthOfDate(document.getElementsByName("bean.day")[0].value);
		var mmDayTo = getMonthOfDate(document.getElementsByName("bean.dayTo")[0].value);
		if(mmDay != mmDayTo){
			valid = false;
			alert("ไม่สามารถเลือกวันที่ข้ามเดือนได้");
			document.getElementsByName("bean.dayTo")[0].focus();
		}
	}
    if(valid){
       document.locationForm.action = path + "/jsp/locationAction.do?do=search";
       document.locationForm.submit();
       return true;
    }
  return false;
}

function clearForm(path){
    document.locationForm.action = path + "/jsp/locationAction.do?do=prepare&action=new";
    document.locationForm.submit();
}

function exportReport(path){
  if(true){
     document.locationForm.action = path + "/jsp/locationAction.do?do=exportReport";
     document.locationForm.submit();
     return true;
  }
  return false;
}
function loadSalesrepCodeList(path){
	var cboDistrict = document.getElementsByName('bean.salesrepCode')[0];
	var param  ="salesChannelNo=" + document.getElementsByName('bean.salesChannelNo')[0].value;
	    param +="&custCatNo="+ document.getElementsByName('bean.custCatNo')[0].value;
	$(function(){
		var getData = $.ajax({
			url: path+"/jsp/location/ajax/genSalesrepCodeListAjax.jsp",
			data : param,
			async: false,
			success: function(getData){                                                                                                                                                                                       
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}

function loadDistrict(path){
	var cboDistrict = document.getElementsByName('bean.district')[0];
	$(function(){
		var getData = $.ajax({
			url: path+"/jsp/location/ajax/DistrictAjax.jsp",
			data : "provinceId=" + document.getElementsByName('bean.province')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}
function openPopup(path,pageName){
	var form = document.locationForm;
	var param = "&pageName="+pageName;
	if("CustomerLocation" == pageName){
		param +="&custCatNo="+form.custCatNo.value;
		param +="&salesChannelNo="+form.salesChannelNo.value;
		param +="&salesrepId="+form.salesrepCode.value;
        param +="&province="+form.province.value;
        param +="&district="+form.district.value;
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.locationForm;
	if("CustomerLocation" == pageName){
		form.customerCode.value = code;
	}
}
function disabledObj(obj,flag){
   obj.disabled = flag;
}