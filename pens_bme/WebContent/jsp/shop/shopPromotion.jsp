<%@page import="com.isecinc.pens.web.shop.ShopBean"%>
<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="shopForm" class="com.isecinc.pens.web.shop.ShopForm" scope="session" />
<%
String pageName = Utils.isNull(request.getParameter("pageName"));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/DateUtils.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>

<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
	addRowAction();
}
function clearForm(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=search&action=back";
	form.submit();
	return true;
}
function save(path){
	var form = document.shopForm;
	var promoName = document.getElementById("promoName");
	var startDate = document.getElementById("startDate");
	var endDate = document.getElementById("endDate");
	if(promoName.value ==""){
		alert("กรุณาระบุ  ชื่อ Promotion หลัก");
		promoName.focus();
		return false;
	}
	if(startDate.value ==""){
		alert("กรุณาระบุ  วันที่ เริ่มต้น");
		startDate.focus();
		return false;
	}
	if(endDate.value ==""){
		alert("กรุณาระบุ  วันที่ สิ้นสุด");
		endDate.focus();
		return false;
	}
	//validate endDate must morethan starDate or equals
	var r = compareDate(startDate.value,endDate.value);
	//alert(r);
	if(r== -1){
		alert("วันที่ สิ้นสุดต้องทากว่าเท่ากับ วันที่เริ่มต้น ");
		endDate.focus();
		return false;
	}
	
	if(!validateDataTable()){
		alert("กรุณาระบุข้อมูลให้ครบถ้วน ")
		return false;
	}

	form.action = path + "/jsp/shopAction.do?do=save";
	form.submit();
	return true;
}
function validateDataTable(){
	var pass = true;
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	
	if(rows.length ==1){
		return false;
	}
	
	var keyData = document.getElementsByName("keyData");
	var subPromoName = document.getElementsByName("subPromoName");
	var startPromtionQty = document.getElementsByName("startPromtionQty");
	var endPromtionQty = document.getElementsByName("endPromtionQty");
	var discountPercent = document.getElementsByName("discountPercent");
	
	//alert(itemIssue.length);
	var errorList = new Object();
	for(var i= 0;i<subPromoName.length;i++){
		var rowObj = new Object();
		var lineClass ="lineE";
		if(i%2==0){
			lineClass = "lineO";
		}
		//alert(itemIssue[i].value );
	    if(keyData[i].value != 'CANCEL' && subPromoName[i].value !=""){
			if(startPromtionQty[i].value =="" ){
				 rows[i+1].className ='lineError';
				 pass = false;
			}
			if(endPromtionQty[i].value =="" ){
				 rows[i+1].className ='lineError';
				 pass = false;
			}
			if(discountPercent[i].value =="" ){
				 rows[i+1].className ='lineError';
				 pass = false;
			}
			//no error
			if(pass){
				rows[i+1].className =lineClass;
			}
	    }
		//alert("rows["+i+"]:"+rows[i].className);
	}// for
	return pass ;
}
function back(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}
function addRowAction(){
	// $('#myTable tr').length;
	var rows = $('#tblProduct tr').length-1;
	var rowId = rows;
	//alert("rowId["+rowId+"]");
	var tabclass='lineE';
	if(rowId%2==0){ 
		tabclass="lineO";
	}

	var rowData ="<tr class='"+tabclass+"'>"+
	    "<td class='td_text_center' width='5%'> "+
	     " <input type='checkbox' name='lineChk'>"+
	     " <input type='hidden' name='keyData' id='keyData' /> "+
	     " <input type='hidden' name='rowId' id='rowId' value='"+rowId+"' /> "+
	    "</td>"+
	    "<td class='td_text_center' width='15%'> "+
	    "  <input type='hidden' name='subPromoId' id='subPromoId' value=''/>"+
	    "  <input type='text' name='subPromoName' id='subPromoName' value='' size='40' autoComplete=off /> "+
	    "  <font color='red'></font> "+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='text' name='startPromtionQty' id='startPromtionQty' autoComplete=off  value='' size='15' class='enableNumber' onblur='isNum(this);'/> "+
	    "  <font color='red'></font> "+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='text' name='endPromtionQty' id='endPromtionQty'  autoComplete=off  value='' size='15' class='enableNumber' onblur='isNum(this);'/> "+
	    "  <font color='red'></font> "+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='text' name='discountPercent' id='discountPercent'  autoComplete=off  value='' size='15' class='enableNumber' onblur='isNum(this);'/> "+
	    "  <font color='red'></font> "+
	    "</td>"+
	    "</tr>";

    $('#tblProduct').append(rowData);
    //set focus default
    var subPromoName = document.getElementsByName("subPromoName");
    subPromoName[rowId].focus();
}
function removeRowAction(path){
	if(confirm("ยืนยันลบข้อมูล")){
		var tbl = document.getElementById('tblProduct');
		var chk = document.getElementsByName("lineChk");
		//alert(chk.length);
		var drow;
		var bcheck=false;
		for(var i=chk.length-1;i>=0;i--){
			if(chk[i].checked){
				//alert(chk[i].checked);
				drow = tbl.rows[i+1];
				//$(drow).remove();
				bcheck=true;
				//hide row table
				removeRowByIndex(path,drow,i);
			}
		}
		if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}else{sumTotal();}
	}
} 
function removeRowByIndex(path,drow,index){
    //alert(drow);
	//todo play with type	
	drow.style.display ='none';
	//set  staus = CANCEL 
	//alert(index);
	document.getElementsByName("keyData")[index].value ='CANCEL';
}
 function isNum(obj){
	  if(obj.value != ""){
		var newNum = parseInt(obj.value);
		if(isNaN(newNum)){
			alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
			obj.value = "";
			obj.focus();
			return false;
		}else{return true;}
	   }
	  return true;
	}
</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	    
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="ShopPromotion"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/shopAction">
						<jsp:include page="../error.jsp"/>
						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td colspan="2" align="center"><font size="3"><!-- เพิ่มข้อมูล  --></font> </td>
                                 </tr>
								<tr>
                                    <td> ชื่อ Promotion หลัก   <font color="red">*</font></td>
									<td >
									   <html:hidden property="bean.promoId"/>
						               <html:text property="bean.promoName" styleId="promoName" size="40" styleClass="\" autoComplete=\"off"/>
									</td>
								</tr>	
								<tr>
                                    <td align="right">เริ่มต้น<font color="red">*</font> </td>
									<td >
						               <html:text property="bean.startDate" styleId="startDate" size="15" readonly ="true" styleClass="\" autoComplete=\"off"/>
						                                
						                 &nbsp;&nbsp;สิ้นสุด <font color="red">*</font>
						                <html:text property="bean.endDate" styleId="endDate" size="15" readonly ="true" styleClass="\" autoComplete=\"off"/>
									   
									</td>
								</tr>	
						   </table>
				    <c:if test="${shopForm.results != null}">
				        <table id="tblProductDel" align="center" border="0" cellpadding="3" cellspacing="2"  width="100%">
	                         <tr><td >
	                             <input type="button" name="add" value="  เพิ่มข้อมูล  " onclick="addRowAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
	                             <input type="button" name="delete" value="  ลบข้อมูล  " onclick="removeRowAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
	                           </td></tr>
	                     </table>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
					        <tr>
					            <th></th>
								<th>ชื่อ Promotion ย่อย</th>
								<th>จำนวนชิ้นเริ่มต้น</th>
								<th>จำนวนชิ้นสิ้นสุด</th>
								<th>เปอร์เซ็นส่วนลด </th>
							 </tr>
							<% 
							try{
							String tabclass ="lineE";
							List<ShopBean> resultList = shopForm.getResults();
							for(int n=0;n<resultList.size();n++){
								ShopBean mc = (ShopBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
								<tr class="<%=tabclass%>">
								   <td class="td_text_center" width="5%">
								       <input type="checkbox" name="lineChk" id="lineChk" 
								        value="<%=mc.getSubPromoId()%>" >
								       <input type="hidden" name="keyData" id="keyData" value="<%=mc.getSubPromoId()%>" />
								       <input type="hidden" name="rowId" id="rowId" value="<%=n%>" />
								    </td>
									<td class="td_text_center" width="15%">
									  <input type="hidden" name="subPromoId" id="subPromoId" value="<%=mc.getSubPromoId()%>"/>
									  <input type="text" name="subPromoName" id="subPromoName" size="40" value="<%=mc.getSubPromoName()%>" autoComplete=off />
									  <font color="red"></font>
									</td>
									<td class="td_text_center" width="10%">
									  <input type="text" name="startPromtionQty" onkeydown="return inputNum(event)"  id="startPromtionQty" autoComplete=off  class='enableNumber' size="15" value="<%=mc.getStartPromtionQty()%>"/>
									  <font color="red"></font>
									</td>
								    <td class="td_text_center" width="10%"> 
								      <input type="text" name="endPromtionQty" onkeypress="isNum(this)"  id="endPromtionQty" autoComplete=off  class='enableNumber' size="15" value="<%=mc.getEndPromtionQty()%>"/>
								     <font color="red"></font>
								     </td>
									<td class="td_text_center" width="10%"> 
								      <input type="text" name="discountPercent" onkeypress="isNum(this)" id="discountPercent" autoComplete=off  class='enableNumber' size="15" value="<%=mc.getDiscountPercent()%>"/>
								      <font color="red"></font>
								     </td>
								</tr>
							<%}
							}catch(Exception e){
								e.printStackTrace();
							}
							%>
					</table>
				</c:if>
				  
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									<a href="javascript:save('${pageContext.request.contextPath}')">
									  <input type="button" value="  บันทึกข้อมูล    " class="newPosBtnLong"> 
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>	
									<%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="   Export   " class="newPosBtnLong">
									</a>	 --%>				
								</td>
							</tr>
						</table>
				 </div>
		          <!-- ************************Result ***************************************************-->	
					
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
					
					</html:form>
					<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
   <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>