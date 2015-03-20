
function search(path, type) {
	document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=search&rf=N&";
	document.salesAnalystReportForm.submit();
	return true;
}

function export(path,type){
	//var url = path + "/jsp/salesAnalystReportAction.do?do=export";
    //window.open(url,"",
			 //  "menubar=yes,resizable=no,toolbar=no,scrollbars=yes,width=800px,height=500px,status=yes,left="+ 50 + ",top=" + 0);
	document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=export";
	document.salesAnalystReportForm.submit();
	return true;
}

function clearForm(path, type) {
	document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=prepare&type="+type;
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



