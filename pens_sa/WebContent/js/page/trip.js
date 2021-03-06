
function clearForm(path){
    document.locationForm.action = path + "/jsp/locationAction.do?do=prepare&action=new";
    document.locationForm.submit();
}

function loadSalesrepCodeList(path){
	var cboDistrict = document.getElementsByName('bean.salesrepCode')[0];
	var param  ="salesChannelNo=" + document.getElementsByName('bean.salesChannelNo')[0].value;
	    param +="&custCatNo="+ document.getElementsByName('bean.custCatNo')[0].value;
	    param +="&salesZone="+ document.getElementsByName('bean.salesZone')[0].value;
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