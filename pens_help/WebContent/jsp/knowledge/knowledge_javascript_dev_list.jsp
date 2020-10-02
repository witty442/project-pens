<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>Knowledge Javascript Dev Tips</title>
<script type="text/javascript">
 function showDiv(id){
	 //alert(id);
	 document.getElementById(id).style.display = "block";
 }
</script>
</head>
<body>
<table align="center" border="1" cellspacing="10" width="100%">
   <tr><td>
   <font size="3"> <b>Knowledge Javascript Dev List Sample </b></font>
   <a href="knowledge_list.jsp">Back to MainPage</a>
   </td></tr>
    <tr><td>
        <b> Pass Value Thai Javascript <a href="javascript:showDiv('div_passvalue_thai');">Show Detail</a></b>
         <div style="display:;" id="div_passvalue_thai">
              <textarea rows="6" cols="160">
              //Call ajax pass ProvinceName
				data : "provinceName=" + encodeURI(document.getElementsByName('bean.projectCBean.province')[0].value),
			//Jsp 
			  provinceName =new String(provinceName.getBytes("ISO8859_1"), "UTF-8");
		    </textarea>
		</div>	 
   </td></tr>
  <tr><td>
     <b> การแบ่ง Paging
     <a href="javascript:showDiv('div_paging');">Show Detail</a></b>
         <div style="display:;" id="div_paging">
            <textarea rows="15" cols="200">
	    function search(path){
			var form = document.nsForm;
			form.action = path + "/jsp/nsAction.do?do=search&action=newsearch";
			form.submit();
			return true;
		}
		function gotoPage(path,currPage){
			var form = document.nsForm;
			form.action = path + "/jsp/nsAction.do?do=search&currPage="+currPage;
		    form.submit();
		    return true;
		}
		// Jsp
		View  Source
        replcae $
		   $ jsp:include page="../pageing_new.jsp" $
	         $ jsp:param name="totalPage" value="$%=//nsForm.getTotalPage() %$" /$
	         $ jsp:param name="totalRecord" value="$%=//nsForm.getTotalRecord() %$" /$
	         $ jsp:param name="currPage" value="$%=//nsForm.getCurrPage() %$" /$
	         $ jsp:param name="startRec" value="$%=//nsForm.getStartRec() %$" /$
	         $ jsp:param name="endRec" value="$%=//nsForm.getEndRec() %$"/$ 
           $ / jsp:include $  
         
		   </textarea>
		 
		</div>	 
  </td></tr>
  <tr><td>
     <a href="pens_bme/jsp/autoCNAction.do?do=prepare2&action=new">add row,delete row table Sample(Request Job เพื่อทำ Auto-CN(LOTUS)</a>
  </td></tr>
   <tr><td>
      <a href="pens_bme/jsp/popupsearch/popupSearch.jsp">Popup search all </a>
   </td></tr>
   <tr><td>
      <a href="pens_bme/jsp/toolManageAction.do?do=prepareSearch&action=new&pageName=ToolManageReport">Call Popup Search Sample(	รายงานสรุปยอดอุปกรณ์และของพรีเมี่ยม) </a>
   </td></tr>
   <tr><td>
      Control Save Lock Screen 
       <textarea rows="6" cols="200">
       
       <!-- Control Save Lock Screen -->
		</ jsp:include page="../controlSaveLockScreen.jsp"/>
		
		 <!-- Control Save Lock Screen No Jquery-->
		</ jsp:include page="../controlSaveLockScreenNoJquery.jsp"/>
		
		/**  Control Save Lock Screen **/
		startControlSaveLockScreen();
		
       </textarea>
   </td></tr>
   <tr><td><b>
      <!-- <a href="pens_bme/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=ImportOrderToOracleFromExcel"> -->
      Batch Task 
        <a href="http://localhost:8080/pens_help/jsp/knowledge/batchTask.jsp">Show Detail</a></b>
   </td></tr>
    <tr><td>
      <b> Struts autoComplete=off Text Field-> </b>
     <textarea rows="6" cols="200">
         <html_X:text property="order.creditCardExpireDate" styleClass="disableText \" autoComplete=\"off"/>
         styleClass="\" autoComplete=\"off"
         autocomplete="off"
         autocomplete='off'
     </textarea>
    </td></tr>	
    <tr><td>
      <b>Validate Data ,ex Number ,Date,Currency (Javascript)</b>
      <a href="http://localhost:8080/pens_help/jsp/knowledge/validateDataTable.html">Show Detail</a></b>
   </td></tr>
    <tr><td>
      <b> Call Ajax (Javascript)</b><a href="javascript:showDiv('div_call_ajax');">Show Detail</a>
          
   </td></tr>
    <tr><td>
           <b>  การ Export to Excel ปัญหาภาษาไทย  add tag <.. meta charset='utf-8'..></b>
   </td></tr>
   <tr><td>
        <b>การ Import pic หลายรูปพร้อมกัน  ProdShowServlet path->   /pens/jsp/prodshow/prodshow.jsp</b>
   </td></tr>
   <tr><td>
        <b> Calendar Popup (support thai Calendar) <a href="javascript:showDiv('div_Calendar');">Show Detail</a></b>
         <div style="display: none;" id="div_Calendar">
            <textarea rows="15" cols="200">
         <!-- Calendar -->
			<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/calendar/jquery.calendars.picker.css" type="text/css" />
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script> 
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.plugin.js"></script> 
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.plus.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker.js"></script> 
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai-th.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker-th.js"></script>
		    
		    //$('#expireDate1_1').calendarsPicker({calendar: $.calendars.instance('thai','th')});
		    </textarea>
		</div>	 
   </td></tr>
   <tr><td>
        <b> Calendar epoch_popup (support thai Calendar) <a href="javascript:showDiv('div_Calendar2');">Show Detail</a></b>
         <div style="display:block ;" id="div_Calendar2">
            <textarea rows="4" cols="200">
              /** Init Calendar **/
             new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
		    </textarea>
		</div>	 
   </td></tr>
    <tr><td>
         <b> Jquery fix head table 
         <a href="http://localhost:8080/pens_help/jsp/knowledge/fixTableHeader.jsp">Show Detail</a></b>
   </td></tr>
   <tr><td>
         <b>  fix head table alternative 2
         <a href="http://localhost:8080/pens_help/jsp/knowledge/fixTableHeader2.jsp">Show Detail</a></b>
   </td></tr>
   <tr><td>
         <b>  fix head table alternative 3 (work colspan and rowspan) (sample page preNissinOrder(pens_sa) ,Page AutoOrder(pens_bme))
         <a href="http://localhost:8080/pens_help/jsp/knowledge/fixTableHeader3.jsp">Show Detail</a></b>
   </td></tr>
    <tr><td>
         <b>  ***(use)fix head table alternative 4 (work fix column and row jquery-stickytable-3.0.js) (sample page stock CallCard(pens_sa) )
           <a href="http://localhost:8080/pens_help/jsp/knowledge/fixTableHeader4.jsp">Show Detail</a>
         </b>
   </td></tr>
    <tr><td>
        <b> Disable Back Button Javascript <a href="javascript:showDiv('div_disable_back');">Show Detail</a></b>
         <div style="display: none;" id="div_disable_back">
              <textarea rows="20" cols="160">
				  window.location.hash="no-back-button";
				  window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
				  window.onhashchange=function(){window.location.hash="no-back-button";}
		    </textarea>
		</div>	 
   </td></tr>
   <tr><td>
        <b> AutoKeyAll <a href="http://localhost:8080/pens_help/jsp/knowledge/autoKeyAjaxAll.html">Show Detail</a></b>
   </td></tr>
    <tr><td>
        <b> Open Popup Javascript <a href="javascript:showDiv('div_popup_all');">Show Detail</a></b>
         <div style="display: none;" id="div_popup_all">
              <textarea rows="20" cols="160">
            <XX input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC')"/>   
	
			function openPopup(path,pageName){
				var form = document.stockMCForm;
				var param = "&pageName="+pageName;
				 if("CustomerStockMC" == pageName){
					param +="&hideAll=true&customerCode="+form.customerCode.value;
				}
				//pens_sa
				url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
				//pens_bme
				url = path + "/jsp/popupAction.do?do=prepareAll&action=new"+param;
				PopupCenterFullHeight(url,"",600);
			}
			function setDataPopupValue(code,desc,pageName){
				var form = document.stockMCForm;
				if("CustomerStockMC" == pageName){
					//alert(code);
					form.customerCode.value = code;
					form.customerName.value = desc;
				}
			} 
			
			
		    </textarea>
		</div>	 
   </td></tr>
   <tr><td>
        <b>Search and paging (case gen html table by code)</b>
        <p>Page pens_sa/jsp/vanAction/OrderVanVOProcess</p>
   </td></tr>
  <tr><td>
        <b> scroll Table(css) <a href="javascript:showDiv('div_scroll_table');">Show Detail</a></b>
         <div style="display: none;" id="div_scroll_table">
              <textarea rows="20" cols="160">
              <_style>
				  #scroll {
				    width:<%=(String)session.getAttribute("screenWidth")%>px;
				    height:400px;
				    background:#FFFFFF;
					border:1px solid #000;
					overflow:auto;
					white-space:nowrap;
					box-shadow:0 0 25px #000;
					}
				<_/style>
				 <div id ="scroll" align="center">
					<table>data</table>		
				</div>
		    </textarea>
		</div>	 
   </td></tr>
   <tr><td>
        <b> Export To Excel By Javascript <a href="javascript:showDiv('div_exportExcelByJavascript');">Show Detail</a></b>
         <div style="display: none;" id="div_exportExcelByJavascript">
              <textarea rows="50" cols="160">
              function fnExcelReport(){
	 var path = document.getElementById("path").value;
	 var style ="<style>"
	         +".summary{"
		     +"   font-weight: bold;"
		     +" } "
			 +".text{"
		     +"   mso-number-format:'@';"
		     +" } ";
		     +"</style> ";
		     
	//export excel thai unicode character support
	style+="<meta charset='utf-8'>";
		     
	var headerTable = genHeadTable(path);
	// alert(headerTable);
	 
    var tab_text= style+headerTable+"\n <table border='2px'><tr bgcolor=''>";
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
    }else{                 
    	//other browser not tested on IE 11
       // sa = window.open('data:application/vnd.ms-excel,' + encodeURIComponent(tab_text));  
    
    	 // Specify file name
        var filename = filename?filename+'.xls':'data.xls';
        // Create download link element
        var downloadLink = document.createElement("a");
        document.body.appendChild(downloadLink);
        
        //alert(tab_text);
        // Create a link to the file
        downloadLink.href = 'data:application/vnd.ms-excel, ' + encodeURIComponent(tab_text);
     
        // Setting the file name
        downloadLink.download = filename;
         
        //triggering the function
        downloadLink.click();
    }
}
		    </textarea>
		</div>	 
   </td></tr>
  </table>
</body>
</html>