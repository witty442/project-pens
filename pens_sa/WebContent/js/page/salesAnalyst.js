/**
 * Witty
 * 06/06/2019
 * sales_analyst.js
 */
function search(path, type) {
	if(validateCriteria()){
	   document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=search&rf=N&";
	   document.salesAnalystReportForm.submit();
	   return true;
	}
	return false;
}

function searchOrder(path, type, field) {
       var search_url2= path + "/jsp/salesAnalystReportAction.do?do=search&rf=N&order_type="+type+"&order_by_name="+field;
       //alert(search_url2);
	   document.salesAnalystReportForm.action = search_url2;
	   document.salesAnalystReportForm.submit();
	   return true;
}

function exportData(path,type){

	var columnCount = document.getElementsByName("columnCount")[0].value;
	var cond1 = document.getElementsByName("salesBean.condName1")[0];
    var cond2 = document.getElementsByName("salesBean.condName2")[0];
    var cond3 = document.getElementsByName("salesBean.condName3")[0];
    var cond4 = document.getElementsByName("salesBean.condName4")[0];

    var condDisp1 = cond1.options[cond1.selectedIndex].text;
    var condDisp2 = cond1.options[cond2.selectedIndex].text;
    var condDisp3 = cond1.options[cond3.selectedIndex].text;
    var condDisp4 = cond1.options[cond4.selectedIndex].text;
    //var condDisp5 = document.getElementsByName("salesBean.condName1")[0];
    //var maxOrderedDate = session.getAttribute("maxOrderedDate");
    //var maxOrderedTime = session.getAttribute("maxOrderedTime");
    
    var maxOrderedDate = document.getElementById('maxOrderedDate').value;
    var maxOrderedTime = document.getElementById('maxOrderedTime').value;
  
    
    //alert("xx"+maxOrderedDate);
	var condDisp5 = "ข้อมูล ณ วันที่  "+maxOrderedDate+" เวลา "+maxOrderedTime;
	//var condDisp5 = "ข้อมูล ณ วันที่ : "+maxOrderedDate; 
	//var condDisp5 = "ข้อมูล ณ วันที่ : ";
	
    // alert(cond1.value+":"+);
    var param  ="&condDisp1="+condDisp1;
        param +="&condDisp2="+condDisp2;
        param +="&condDisp3="+condDisp3;
        param +="&condDisp4="+condDisp4;
        param +="&condDisp5="+condDisp5;
                                                                
	document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=export&columnCount="+columnCount+param;
	document.salesAnalystReportForm.submit();
	
	return true;
}
function getSQL(path, type) {
	if(validateCriteria()){
	   document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=getSQL";
	   document.salesAnalystReportForm.submit();
	   return true;
	}
	return false;
}

function saveProfile(path, type) {
	var profileId = $('#profileId').val();
	if(profileId != '0'){
	   document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=saveProfile";
	   document.salesAnalystReportForm.submit();
	   return true;
	}
	return false;
}

function editProfile(path, type) {
	var profileId = $('#profileId').val();
    var url = path + "/jsp/manageProfileSearchAction.do?do=prepare&action=new&profileId="+profileId;
    PopupCenter(url, "", 600, 300) ;
}


function changeProfile(path, profileId) {
	if(profileId != '0'){
	   document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=changeProfile";
	   document.salesAnalystReportForm.submit();
	   return true;
	}
	return false;
}
function clearForm(path, type) {
	document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=prepare&action=new&type="+type;
	document.salesAnalystReportForm.submit();
	return true;
}


function hide(name){
	//alert(name);
	 if (document.getElementById) { // DOM3 = IE5, NS6 
		 if(name =='1'){
		    document.getElementById('sample1').style.visibility = 'hidden'; 
		 }
		 if(name =='2'){
			document.getElementById('sample2').style.visibility = 'hidden'; 
		}
	 }else{
		 document.getElementById('sample1').style.visibility='hidden'; 
	 } 		
}

function show(name){
	 if (document.getElementById) { // DOM3 = IE5, NS6 
		 if(name =='1'){
		    document.getElementById('sample1').style.visibility = 'visible'; 
		 }
		 if(name =='2'){
			 document.getElementById('sample2').style.visibility = 'visible'; 
		  }
	 }else{
		 document.getElementById(eval(name)).style.visibility='visible'; 
	 } 		
}


function disabledObj(obj,flag){
	  obj.disabled = flag;
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
	
	//Quarter
	for(var i=0; i<yearList.size();i++){
		if(yearList[i].value == year){
			$('tr#'+yearList[i].value+"_Q").show();
		}
		else{
			$('tr#'+yearList[i].value+"_Q").hide();
		}
	}
}
function validateCriteria(){
	 var typeSearch = document.getElementsByName("salesBean.typeSearch")[0];
	 if(typeSearch.value == 'DAY'){
		 var day = document.getElementsByName("salesBean.day")[0];
		 var dayTo = document.getElementsByName("salesBean.dayTo")[0];
		 if(day.value =='' && dayTo.value == ''){
			 alert("กรุณากรอกวันที่");
			 return false;
		 }

		 if(day.value != '' && dayTo.value != ''){
			// Check Date From Should More Than or Equal To Date To
			var dateFrom = getDateFromFormat(day.value,"dd/MM/yyyy");
			var dateTo = getDateFromFormat(dayTo.value,"dd/MM/yyyy");

			if(dateFrom > dateTo){
				alert("กรุณาระบุวันที่ให้ถูกต้อง");
				return false;
			}	 
		 }
	 }else  if(typeSearch.value == 'MONTH'){
		 var obj = document.getElementsByName("salesBean.chkMonth");
		 if( !isChkOne(obj)){
			 alert("โปรดเลือกแสดงข้อมูลอย่างน้อย 1 เดือน");
			 return false;
		 }

	 }else  if(typeSearch.value == 'QUARTER'){
		 var obj = document.getElementsByName("salesBean.chkQuarter");
		 if( !isChkOne(obj)){
			 alert("โปรดเลือกแสดงข้อมูลอย่างน้อย 1 ไตรมาส");
			 return false;
		 }
	 }else  if(typeSearch.value == 'YEAR'){
		 var obj = document.getElementsByName("salesBean.chkYear");
		 if( !isChkOne(obj)){
			 alert("โปรดเลือกแสดงข้อมูลอย่างน้อย 1 ปี");
			 return false;
		 }
	 }

	 if(!validateDisp()){
		 alert("โปรดเลือก คอลัมน์ ที่จะแสดงอย่างน้อย 1 คอลัมน์ ");
		 return false;
	 }
	 if(!validateCond()){
		 alert("โปรดเลือก เงื่อนไขในการเลือกข้อมูล");
	    return false;
	 }
	 return true;
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

function validateDisp(){
	var disp1 = document.getElementsByName("salesBean.colNameDisp1")[0];
	var disp2 = document.getElementsByName("salesBean.colNameDisp2")[0];
	var disp3 = document.getElementsByName("salesBean.colNameDisp3")[0];
	var disp4 = document.getElementsByName("salesBean.colNameDisp4")[0];
	if(disp1.value =='0' && disp2.value =='0' && disp3.value =='0' && disp4.value =='0'){
        return false;
	}
	return true;
}

function validateCond(){
	
	if (document.getElementsByName("salesBean.condName1")[0].value == 'INVOICE_NO'){
        return true;
	}
	
	if (document.getElementsByName("salesBean.condName1")[0].value == 'SALES_ORDER_NO'){
		return true;
	}

	if (document.getElementsByName("salesBean.condName2")[0].value == 'INVOICE_NO') {
	   return true;
	}
	
	if (document.getElementsByName("salesBean.condName2")[0].value == 'SALES_ORDER_NO'){
	   return true;
	}

	if (document.getElementsByName("salesBean.condName3")[0].value == 'INVOICE_NO'){
	   return true;
	}
	
	if (document.getElementsByName("salesBean.condName3")[0].value == 'SALES_ORDER_NO'){
	   return true;
	}

	if (document.getElementsByName("salesBean.condName4")[0].value == 'INVOICE_NO'){
	   return true;
	}
	
	if (document.getElementsByName("salesBean.condName4")[0].value == 'SALES_ORDER_NO'){
	   return true;
	}
	
	if (document.getElementsByName("salesBean.condName1")[0].value != '-1' &&
			document.getElementsByName("salesBean.condValue1")[0].value == ''	)
	{
		 return false;
	}
	if (document.getElementsByName("salesBean.condName2")[0].value != '-1' &&
			document.getElementsByName("salesBean.condValue2")[0].value == ''	)
	{
		 return false;
	}
	if (document.getElementsByName("salesBean.condName3")[0].value != '-1' &&
			document.getElementsByName("salesBean.condValue3")[0].value == ''	)
	{
		 return false;
	}
	if (document.getElementsByName("salesBean.condName4")[0].value != '-1' &&
			document.getElementsByName("salesBean.condValue4")[0].value == ''	)
	{
		 return false;
	}
	return true;
}


function refereshListBox(path){
    /** remove ListBox Value **/
	remove_all(path);
	/** Init new ListBox **/
	var returnString =document.getElementsByName("salesBean.returnString")[0].value;
	if(returnString ==''){
		append_item(path, "0", "ไม่เลือก");
	}else{
		var t = returnString.split(",");  //ถ้าเจอวรรคแตกเก็บลง array t
		for(var i=0; i<t.length ; i++){
	        var code = t[i].split("|")[0];
	        var value = t[i].split("|")[1];
		    append_item(path, code, value);
		}
	}
}

//manipulation
function get_selected(path){
	return $(path + " option:selected");
}
function remove_selected(path){
	$(path + " option:selected").remove();
}
function append_item(path, value, text){
	$(path).append("<option value='" + value + "'>" + text + "</option>");
}
function select_first(path){
	$(path + " option:first-child").attr("selected","selected");
}
function select_last(path){
	$(path + " option:last-child").attr("selected","selected");
}
function remove_all(path){
	$(path).empty();
}
function select_by_value(path, value){
	$(path + " option[value=" + value + "]").attr("selected","selected");
}
function remove_by_value(path, value){
	$(path + " option[value=" + value + "]").remove();
}

function showSearchValuePopup(path,currCondNo){
	var currCondTypeValue = "";
	//alert(path);
	/** Set Fro Filter By Parent Listbox **/
	var condType1 = document.getElementsByName("salesBean.condName1")[0].value;
	var condCode1 = document.getElementsByName("salesBean.condCode1")[0].value;
	var condValueDisp1 = document.getElementsByName("salesBean.condValueDisp1")[0].value;
	
	var condType2 = document.getElementsByName("salesBean.condName2")[0].value;
	var condCode2 = document.getElementsByName("salesBean.condCode2")[0].value;
	var condValueDisp2 = document.getElementsByName("salesBean.condValueDisp2")[0].value;
	
	var condType3 = document.getElementsByName("salesBean.condName3")[0].value;
	var condCode3  = document.getElementsByName("salesBean.condCode3")[0].value;
	var condValueDisp3 = document.getElementsByName("salesBean.condValueDisp3")[0].value;

	if(currCondNo=='1'){
		currCondTypeValue = document.getElementsByName("salesBean.condName1")[0].value;
	}
	if(currCondNo=='2'){
		currCondTypeValue = document.getElementsByName("salesBean.condName2")[0].value;
	}
	if(currCondNo=='3'){
		currCondTypeValue = document.getElementsByName("salesBean.condName3")[0].value;
	}
	if(currCondNo=='4'){
		currCondTypeValue = document.getElementsByName("salesBean.condName4")[0].value;
	}

	if(currCondTypeValue =='-1'){
       alert("โปรดเลือก ประเภทขอบเขตข้อมูล");
	}else{
		var param = "";
		var url =  "";
		if(currCondNo=='1'){
			param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
	    }else if(currCondNo=='2'){
	    	param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
			param += "&condType1="+condType1+"&condCode1="+condCode1+"&condValueDisp1="+condValueDisp1;
		}else if(currCondNo =='3'){
			param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
			param += "&condType1="+condType1+"&condCode1="+condCode1+"&condValueDisp1="+condValueDisp1;
			param += "&condType2="+condType2+"&condCode2="+condCode2+"&condValueDisp2="+condValueDisp2;
		}else if(currCondNo =='4'){
			param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
			param += "&condType1="+condType1+"&condCode1="+condCode1+"&condValueDisp1="+condValueDisp1;
			param += "&condType2="+condType2+"&condCode2="+condCode2+"&condValueDisp2="+condValueDisp2;
			param += "&condType3="+condType3+"&condCode3="+condCode3+"&condValueDisp3="+condValueDisp3;
		}
		
		param +="&decode=true&load=1";
		//alert(param);
		
		url = path + "/jsp/searchValuePopupAction.do?do=prepare&action=new"+param;
	    PopupCenterFullHeight(encodeURI(url),"","700");
	}
}

// declare variable using in multiple selection
var multicode ;
var multikey ;
var multiValueDisp;
function setMultiCode(p_code){
	multicode = p_code;
}

function setMultiKey(p_key){
	multikey = p_key;
}

function setMultiValueDisp(p_value_disp){
	multiValueDisp = p_value_disp;
}

function setValueCondition(condNo){
	if(condNo =='1'){
	   document.getElementsByName("salesBean.condCode1")[0].value = multicode;
	   document.getElementsByName("salesBean.condValue1")[0].value = multikey;
	   document.getElementsByName("salesBean.condValueDisp1")[0].value = multiValueDisp;
	}
	if(condNo =='2'){
	   document.getElementsByName("salesBean.condCode2")[0].value = multicode;
	   document.getElementsByName("salesBean.condValue2")[0].value = multikey;
	   document.getElementsByName("salesBean.condValueDisp2")[0].value = multiValueDisp;
	}
	if(condNo =='3'){
	   document.getElementsByName("salesBean.condCode3")[0].value = multicode;
	   document.getElementsByName("salesBean.condValue3")[0].value = multikey;
	   document.getElementsByName("salesBean.condValueDisp3")[0].value = multiValueDisp;
	}
	if(condNo =='4'){
	   document.getElementsByName("salesBean.condCode4")[0].value = multicode;
	   document.getElementsByName("salesBean.condValue4")[0].value = multikey;
	   document.getElementsByName("salesBean.condValueDisp4")[0].value = multiValueDisp;
	}	
}

function setMainValue(code,key,desc,condNo){
	if(condNo =='1'){
	   document.getElementsByName("salesBean.condCode1")[0].value = key;
	   document.getElementsByName("salesBean.condValue1")[0].value = code;
	   document.getElementsByName("salesBean.condValueDisp1")[0].value = desc;
	}
	if(condNo =='2'){
	   document.getElementsByName("salesBean.condCode2")[0].value = key;
	   document.getElementsByName("salesBean.condValue2")[0].value = code;
	   document.getElementsByName("salesBean.condValueDisp2")[0].value = desc;
	}
	if(condNo =='3'){
	   document.getElementsByName("salesBean.condCode3")[0].value = key;
	   document.getElementsByName("salesBean.condValue3")[0].value = code;
	   document.getElementsByName("salesBean.condValueDisp3")[0].value = desc;
	}
	if(condNo =='4'){
	   document.getElementsByName("salesBean.condCode4")[0].value = key;
	   document.getElementsByName("salesBean.condValue4")[0].value = code;
	   document.getElementsByName("salesBean.condValueDisp4")[0].value = desc;
	}	
}

function clearText(condNo){
	if(condNo =='1'){
	   document.getElementsByName("salesBean.condCode1")[0].value = '';
	   document.getElementsByName("salesBean.condValue1")[0].value = '';
	   document.getElementsByName("salesBean.condValueDisp1")[0].value = '';
	}
	if(condNo =='2'){
	   document.getElementsByName("salesBean.condCode2")[0].value = '';
	   document.getElementsByName("salesBean.condValue2")[0].value = '';
	   document.getElementsByName("salesBean.condValueDisp2")[0].value = '';
	}
	if(condNo =='3'){
	   document.getElementsByName("salesBean.condCode3")[0].value = '';
	   document.getElementsByName("salesBean.condValue3")[0].value = '';
	   document.getElementsByName("salesBean.condValueDisp3")[0].value = '';
	}
	if(condNo =='4'){
	   document.getElementsByName("salesBean.condCode4")[0].value = '';
	   document.getElementsByName("salesBean.condValue4")[0].value = '';
	   document.getElementsByName("salesBean.condValueDisp4")[0].value = '';
	}	
}

function set_display_value1(e, field){
	loadValueOnchange(e, field,true);
}

function set_display_value2(e, field){
	loadValueOnchange(e, field,true);
}

function set_display_value3(e, field){
	loadValueOnchange(e, field,true);
}

function set_display_value4(e, field){
	loadValueOnchange(e, field,true);
}

//call ajax
function loadValueOnchange(e, field,change){
	var condType = new Object();
	var condCode = new Object();
	var condValue = new Object();
	var condDisp = new Object();
	switch(field){
	case 1 : {
		condType  = $('#condName1');
		condCode  = $('#condCode1');
        }break;
	case 2 : {
		condType  = $('#condName2');
		condCode  = $('#condCode2');
		}break;
	case 3 : {
		condType  = $('#condName3');
		condCode  = $('#condCode3');
		}break;
	case 4 : {
		condType  = $('#condName4');
		condCode  = $('#condCode4');
		}break;
	default: break;
	}
	
	if(condType.val() == 0){
		alert("โปรดเลือก ประเภทขอบเขตข้อมูล");
		return;
	}
	
	if(condCode.val() !=""){
		loadValue(e, field,change);
	}
}

//call ajax
function loadValue(e, field,change){
	var path = document.getElementById("path").value;
	var condType = new Object();
	var condCode = new Object();
	var condValue = new Object();
	var condDisp = new Object();
	switch(field){
	case 1 : {
		condType  = $('#condName1');
		condCode  = $('#condCode1');
		condValue = $('#condValue1');
		condDisp  = $('#condValueDisp1');}break;
	case 2 : {
		condType  = $('#condName2');
		condCode  = $('#condCode2');
		condValue = $('#condValue2');
		condDisp  = $('#condValueDisp2');}break;
	case 3 : {
		condType  = $('#condName3');
		condCode  = $('#condCode3');
		condValue = $('#condValue3');
		condDisp  = $('#condValueDisp3');}break;
	case 4 : {
		condType  = $('#condName4');
		condCode  = $('#condCode4');
		condValue = $('#condValue4');
		condDisp  = $('#condValueDisp4');}break;
	default: break;
	}

	if(e == null || (e != null && e.keyCode == 13) || change){
		if(condType.val() == 0){
			alert("โปรดเลือก ประเภทขอบเขตข้อมูล");
			return;
		}
		//alert('condCode.val():'+condCode.val()+',condType.val(:'+condType.val());
		
		if(condCode.val() != ''){
			$(function(){
				var getData = $.ajax({
					url: path+"/jsp/ajax/searchValueQuery.jsp",
					data : "condCode=" + encodeURIComponent(condCode.val()) + "&condType=" + condType.val(),
					//async: false,
					cache: true,
					success: function(getData){
						var returnString = jQuery.trim(getData);
					    if(returnString.split('|')[0] ==''){
						   alert("ไม่พบข้อมูล");
						   condCode.focus();
					       condCode.val('');
					       condValue.val('');
						   condDisp.val('');
					    }else{
						   condValue.val(returnString.split('|')[0]);
						   condDisp.val(returnString.split('|')[1]);
					    }
					}
				}).responseText;
			});
		}
	}
}

 function isNumber(value){
	var numberRegex = /^[+-]?\d+(\.\d+)?([eE][+-]?\d+)?$/;
	var str = value;
	str = ReplaceAll(str,",","",true);
	//alert(str);
	if(numberRegex.test(str)) {
	   //alert('I am a number');
	   return true;
	}
	return false;
}

function ReplaceAll( inText, inFindStr, inReplStr, inCaseSensitive ) {
    var searchFrom = 0;
    var offset = 0;
    var outText = "";
    var searchText = "";
    if ( inCaseSensitive == null ) {
       inCaseSensitive = false;
    }
    if ( inCaseSensitive ) {
       searchText = inText.toLowerCase();
       inFindStr = inFindStr.toLowerCase();
    } else {
       searchText = inText;
    }
    offset = searchText.indexOf( inFindStr, searchFrom );
    while ( offset != -1 ) {
       outText += inText.substring( searchFrom, offset );
       outText += inReplStr;
       searchFrom = offset + inFindStr.length;
       offset = searchText.indexOf( inFindStr, searchFrom );
    }
    outText += inText.substring( searchFrom, inText.length );

    return ( outText );
 }
 
 function controlRoleTab(){
	 var roleTabText =document.getElementsByName("roleTabText")[0];
	 var span = document.getElementById('roleTabSpan');
	 while( span.firstChild ) {
	     span.removeChild( span.firstChild );
	 }
	 
	 if("" ==roleTabText.value){
		 //show 
		 document.getElementById("roleTab").style.display = 'block';
		 // document.getElementById("roleTabText").innerHTML = "(Hide Role )";
		 
		 span.appendChild( document.createTextNode("(Hide Role Detail") );
		 
		 //alert(document.getElementById("roleTabText").innerHTML);
		 roleTabText.value ="show";
	 }else{
		 //HIDE
		 document.getElementById("roleTab").style.display = 'none';    
		 // document.getElementById("roleTabText").innerHTML = "(Show Role )";
		 span.appendChild( document.createTextNode("(Show Role Detail)") );
		 roleTabText.value ="";
	 }
 }


 function genHeadTable(path){
 	 var columnCount = document.getElementsByName("columnCount")[0].value;
 	 
 	 var cond1 = document.getElementsByName("salesBean.condName1")[0];
     var cond2 = document.getElementsByName("salesBean.condName2")[0];
     var cond3 = document.getElementsByName("salesBean.condName3")[0];
     var cond4 = document.getElementsByName("salesBean.condName4")[0];

     var condDisp1 = cond1.options[cond1.selectedIndex].text;
     var condDisp2 = cond1.options[cond2.selectedIndex].text;
     var condDisp3 = cond1.options[cond3.selectedIndex].text;
     var condDisp4 = cond1.options[cond4.selectedIndex].text;
 	    
     var maxOrderedDate = document.getElementById('maxOrderedDate').value;
     var maxOrderedTime = document.getElementById('maxOrderedTime').value;
   
     //alert("xx"+maxOrderedDate);
 	 var condDisp5 = "ข้อมูล ณ วันที่  "+maxOrderedDate+" เวลา "+maxOrderedTime;
 	
     var headerTable = "";
 	 var param   = "columnCount="+columnCount;
 	     param  += "&condDisp1="+condDisp1;
 	     param  += "&condDisp2="+condDisp2;
 	     param  += "&condDisp3="+condDisp3;
 	     param  += "&condDisp4="+condDisp4;
 	     param  += "&condDisp5="+condDisp5
 	     
 	 $(function(){
 			var getData = $.ajax({
 				url: path+"/jsp/ajax/genHeaderExcelAjax.jsp",
 				data : param ,
 				async: false,
 				cache: true,
 				success: function(getData){
 					headerTable = jQuery.trim(getData);
 				}
 			}).responseText;
 		});
 	 
 	// alert(headerTable);
 	 return headerTable;
 }
 
 function fnExcelReport(){
	 var path = document.getElementById("path").value;
	 var style ="<style>"
	         +".summary{"
		     +"   font-weight: bold;"
		     +" } ";
		     +"</style> ";
	 var headerTable = genHeadTable(path);
	// alert(headerTable);
	 
    var tab_text= headerTable+"\n <table border='2px'><tr bgcolor=''>";
    var textRange; var j=0;
    tab = document.getElementById('sort-table'); // id of table

    for(j = 0 ; j < tab.rows.length ; j++){     
        tab_text=tab_text+tab.rows[j].innerHTML+"</tr>";
        //tab_text=tab_text+"</tr>";
    }

    tab_text= tab_text+"</table>";
    tab_text= tab_text.replace(/<A[^>]*>|<\/A>/g, "");//remove if u want links in your table
    tab_text= tab_text.replace(/<img[^>]*>/gi,""); // remove if u want images in your table
    tab_text= tab_text.replace(/<input[^>]*>|<\/input>/gi, ""); // reomves input params

    var ua = window.navigator.userAgent;
   // alert("ua:"+ua);
    var msie = ua.indexOf("MSIE "); 

    if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./))      // If Internet Explorer
    {
        alert("msie > 0");
        txtArea1.document.open("txt/html","replace");
        txtArea1.document.write(tab_text);
        txtArea1.document.close();
        txtArea1.focus(); 
        sa=txtArea1.document.execCommand("SaveAs",true,"Say Thanks to Submit.xls");
    }else{                 //other browser not tested on IE 11
       // sa = window.open('data:application/vnd.ms-excel,' + encodeURIComponent(tab_text));  
    
    	 // Specify file name
        var filename = filename?filename+'.xls':'data.xls';
        // Create download link element
        var downloadLink = document.createElement("a");
        document.body.appendChild(downloadLink);
        
        // Create a link to the file
        downloadLink.href = 'data:application/vnd.ms-excel, ' + encodeURIComponent(tab_text);
     
        // Setting the file name
        downloadLink.download = filename;
         
        //triggering the function
        downloadLink.click();
    }
}
 
 /**** For Sort  Table **************************************************/

 $(document).ready(function() {
 	$('.link-sort').click(function(e) {
 		var $sort = this;
 		var $table = $('#sort-table');
 		var $rows = $('tbody > tr',$table);
 		var id = $(this).attr("id");
 		//alert(id);
 		$rows.sort(function(a, b){
 			var keyA = 0;
 			var keyB = 0;
 			
             if($('td.sort_'+id,a).text() != '' && $('td.sort_'+id,b).text()){
 				
 				if(isNumber($('td.sort_'+id,a).text()) && isNumber($('td.sort_'+id,b).text())){
 					keyA = parseFloat(ReplaceAll($('td.sort_'+id,a).text(),",","",false));
 					keyB = parseFloat(ReplaceAll($('td.sort_'+id,b).text(),",","",false));
 				}else{
 				    keyA = $('td.sort_'+id,a).text();
 				    keyB = $('td.sort_'+id,b).text();
 				}
 				//alert(keyA+":"+keyB);
 				if($($sort).hasClass('asc')){
 					//alert("asc");
 					return (keyA > keyB) ? 1 : -1;
 				} else {
 					//alert("desc");
 					return (keyA < keyB) ? 1 : -1;
 				}
 			}
 		});
 		var no =1;
 		$.each($rows, function(index, row){
 			//alert(row[0].cells);
 			row.cells[0].innerHTML = no;
 			$table.append(row);
 			no++;
 		});
 		e.preventDefault();
 	});
 	
 });
