
function changeDispCri(obj){
	if(obj.value =='MAP'){
	   showTable('table_map');	
	   hideTable('table_data');
	}else{
	   // data
	   hideTable('table_map');
	   showTable('table_data');	
	}
}
function hideTable(id){
	var lTable = document.getElementById(id);
	lTable.style.display = 'none';
}
function showTable(id){
	var lTable = document.getElementById(id);
	lTable.style.display = 'table';
}

function search(path){
	  var foundChkMonth = false;
	  var typeSearch = document.getElementsByName("bean.typeSearch")[0];
	  if(typeSearch.value == 'DAY'){
		  if(document.getElementsByName("bean.day")[0].value ==""){
			  alert("กรุณากรอกวันที่");
			  document.getElementsByName("bean.day")[0].focus();
		  }else{
			   foundChkMonth = true;
		  }
	  }else  if(typeSearch.value == 'MONTH'){
		  var monthList = document.getElementsByName("bean.chkMonth");
			for(var i=0;i<monthList.length;i++){
		       var monthChk = document.getElementsByName("bean.chkMonth")[i];
		       if(monthChk.checked){
		    	   foundChkMonth = true;
		    	   break;
		       }
		    }
			if(foundChkMonth == false){
				 alert("กรุณาระบุข้อมูลเดือน อย่างน้อย 1 เดือน");
			}
	  }
	  
	  //case show By Type Map Valid check to show
	  var validCheck = false;
	  if(document.getElementById("dispType").value =='MAP'){
		  /*if(document.getElementsByName("bean.dispAllStore")[0].checked){
			  validCheck = true; 
		  }
		  if(document.getElementsByName("bean.dispAllOrder")[0].checked){
			  validCheck = true; 
		  }
		  if(document.getElementsByName("bean.dispAllVisit")[0].checked){
			  validCheck = true; 
		  }*/
		 
		  if(validCheck==false){
			 /* alert("กรุณาเลือกการแสดงข้อมูลแผนที่อย่างน้อย 1 อย่าง");
			  chkShow[0].focus();
			  return false;*/
		  }
	  }
	  
    if(foundChkMonth){
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
	  var foundChkMonth = false;
	  var typeSearch = document.getElementsByName("bean.typeSearch")[0];
	  if(typeSearch.value == 'DAY'){
		  if(document.getElementsByName("bean.day")[0].value ==""){
			  alert("กรุณากรอกวันที่");
			  document.getElementsByName("bean.day")[0].focus();
		  }else{
			   foundChkMonth = true;
		  }
	  }else  if(typeSearch.value == 'MONTH'){
		  var monthList = document.getElementsByName("bean.chkMonth");
			for(i=0;i<monthList.length;i++){
		       var monthChk = document.getElementsByName("bean.chkMonth")[i];
		       if(monthChk.checked){
		    	   foundChkMonth = true;
		    	   break;
		       }
		    }
			if(foundChkMonth == false){
				 alert("กรุณาระบุข้อมูลเดือน อย่างน้อย 1 เดือน");
			}
	  }
	  
  if(foundChkMonth){
     document.locationForm.action = path + "/jsp/locationAction.do?do=exportReport";
     document.locationForm.submit();
     return true;
  }
return false;
}

function MarkLocationMap(path){
	PopupCenterFull(path+"/jsp/location/markLocationMap.jsp?", "Mark location map"); 
}

function chkSearch(){
	   var typeSearch = document.getElementsByName("bean.typeSearch")[0];
	   //alert(typeSearch.value);
		disabledObj(document.getElementsByName("bean.day")[0] ,false);
		disabledObj(document.getElementsByName("bean.dayTo")[0] ,false);
		
		var monthList = document.getElementsByName("bean.chkMonth");
		for(i=0;i<monthList.length;i++){
	       disabledObj(document.getElementsByName("bean.chkMonth")[i],false);
	    }
		
	   if(typeSearch.value == 'DAY'){
		   for(i=0;i<monthList.length;i++){
		      disabledObj(document.getElementsByName("bean.chkMonth")[i],true);
		   }
		   
	    }else  if(typeSearch.value == 'MONTH'){
	       disabledObj(document.getElementsByName("bean.day")[0] ,true);
	       disabledObj(document.getElementsByName("bean.dayTo")[0] ,true);
	    }
	}

function chkYear(){
	var year = $('select#yearList').val();
	var yearList = $('select#yearList option');

	//Month
	for(var i=0; i<yearList.size();i++){
		if(yearList[i].value == year){
			$('tr#'+yearList[i].value).show();
		}
		else{
			$('tr#'+yearList[i].value).hide();
		}
	}		
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