<%@page import="com.isecinc.pens.bean.SATranBean"%>
<%@page import="com.isecinc.pens.dao.SAEmpDAO"%>
<%@page import="com.isecinc.pens.bean.SAEmpBean"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
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
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="saTranForm" class="com.isecinc.pens.web.sa.SATranForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
String mode = saTranForm.getMode();
SATranBean bean = saTranForm.getBean(); 



//PayTypeList
List<PopupForm> payTypeList = new ArrayList();
PopupForm ref2 = new PopupForm("",""); 
payTypeList.add(ref2);
payTypeList.addAll(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","SApaytype"));
//session.setAttribute("payTypeList",billTypeList2);

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}

span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}

.day {
  width: 14%;
}
.canchange_style {
  width: 14%;
  background-color: #7DCEA0;
}

input[type=checkbox]
{
  /* Double-sized Checkboxes */
  -ms-transform: scale(2); /* IE */
  -moz-transform: scale(2); /* FF */
  -webkit-transform: scale(2); /* Safari and Chrome */
  -o-transform: scale(2); /* Opera */
  padding: 10px;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/DateUtils.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	<%if("add".equals(mode)){%>
	  
	<%}else{%>
	  //  setStyletextField();
	 <%}%>
	 
   new Epoch('epoch_popup', 'th', document.getElementById('payDate'));
   new Epoch('epoch_popup', 'th', document.getElementById('countStockDate'));
}

function setStyletextField(){
	document.getElementById('empId').className= "disableText";
	document.getElementById('empId').readOnly= true;
	document.getElementById('startDate').className= "disableText";
}

function clearForm(path){
	var form = document.saTranForm;
	form.action = path + "/jsp/saTranAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.saTranForm;
	form.action = path + "/jsp/saTranAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.saTranForm;

	if( $('#payDate').val()==""){
		alert("กรุณาระบุ  วันที่ส่งเงิน(+)");
		$('#payDate').focus();
		return false;
	}
	
	if( $('#countStockDate').val()==""){
		alert("กรุณาระบุ  วันที่เข้าตรวจนับ");
		$('#countStockDate').focus();
		return false;
	}else{
		//countStockDate must < payDate
		var payDate = thaiDateToChristDate($('#payDate').val());
		
		var countStockDate = thaiDateToChristDate($('#countStockDate').val());
		
		if(countStockDate >=  payDate){
			alert("วันที่เข้าตรวจนับ ต้องน้อยกว่า วันที่ส่่งเงิน (+)");
			$('#countStockDate').focus();
			return false;
		}
	}
	
	if(confirm("กรุณายืนยันการบันทึก ค่าเฝ้าตู้")){
		form.action = path + "/jsp/saTranAction.do?do=save&action=newsearch";
		form.submit();
		return true;
	}
	return false;
}

function isNum(obj){
	//alert("isNum");
	  if(obj.value != ""){
		if(isNaN(obj.value)){
			alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
			obj.value = "";
			obj.focus();
			return false;
		}else{return true;}
	   }
	  return true;
	}

function setCanSave(chk,index){
	//alert(index+":"+chk.checked);
	
	if(chk.checked){
		document.getElementsByName("canSave")[index].value ="canSave";
	}else{
		document.getElementsByName("canSave")[index].value ="";
	}
}
</script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerMC.jsp"/></td>
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
				<jsp:param name="function" value="saTran"/>
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
						<html:form action="/jsp/saTranAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
									<td colspan="6" align="center">	
									   <font size="3" ><b>
										 <%if("add".equals(saTranForm.getMode())){ %>
									                                เพิ่มข้อมูล
										 <%}else{ %>
									                            แก้ไขข้อมูล
										 <%} %>
										  ค่าเฝ้าตู้ &nbsp;<bean:write name="saTranForm" property="bean.type"/>
										 </b>
										 </font>	
									</td>
								</tr>
								<tr>
                                    <td  align="right">Employee ID<font color="red"></font></td>
									<td colspan="3">		
								       <html:text property="bean.empId" styleId="empId" size="30" readonly="true" styleClass="disableText"> </html:text>	   
									</td>
								</tr>
								<tr>
                                    <td align="right">Name</td>
									<td colspan="3">	
									<html:text property="bean.name" styleId="name"  size="30"  readonly="true" styleClass="disableText"/>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									Surname
									    <html:text property="bean.surname" styleId="surname" size="30"  readonly="true" styleClass="disableText"/>
									 </td>
								</tr>
								<tr>
                                    <td  align="right">วันที่ส่งเงิน (+)<font color="red">*</font></td>
									<td>		
										 <html:text property="bean.payDate" styleId="payDate" size="30" readonly="true"> </html:text>
									</td>
									 <td  align="right"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;วันที่เข้าตรวจนับ <font color="red">*</font></td>
									<td> <html:text property="bean.countStockDate" styleId="countStockDate" size="30" readonly="true"> </html:text></td>
								</tr>
						   </table>
						   
						   <!-- Table Items -->
						   <%if(saTranForm.getBean().getItems() != null){ %>
						   <table id="tblProduct" align="center" width="75%" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth">
						       <tr> 
						            <th>เคยจ่ายแล้ว</th>
						            <th>ค่าเฝ้าตู้ของเดือน</th>
									<th>วันที่ได้ชำระ</th>
									<th>Amt</th>								
									<th>วันที่เข้าตรวจนับ</th>
									<th>บันทึกความเสียหายแล้ว</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<SATranBean> items = saTranForm.getBean() !=null?saTranForm.getBean().getItems():null;
							if(items != null)
							   System.out.println("Screen items size:"+items.size());
							int tabindex = 1;
							int no = 0;
							String isPayed ="";
							String isPayedChk = "checked";
						
							if(items != null && items.size() >0){
								for(int n=0;n<items.size();n++){
									SATranBean item = item = items.get(n);
									tabclass="lineO";
									 if( item.isCanChange() == true){
										 tabclass="canchange_style";
									 }
									%>
									<tr class="<%=tabclass%>">
									    <td class="td_text_center"  width="10%">
									   <%--  <input type="text" name="xx" value="<%=n %>"/> --%>
									   <%  if( item.isCanChange() == false){ %>
									           <%if(item.isExistDB()){ %>
										           <input type="checkbox" name="isPayed" value="IS_PAYED"  class="disableText" checked disabled/>
										           <input type="hidden" name="canSave" value="no_action"/>
										           <input type="hidden" name="status" id="status" value="DB"/>
										      <%}else{ %>
										          <input type="checkbox" name="isPayed" value="IS_PAYED"  class="disableText" disabled/>
										          <input type="hidden" name="canSave" value=""/>
										          <input type="hidden" name="status" id="status" value=""/>
										      <%} %>
										<%}else{ %>
										     <%if(item.isExistDB()){ %>
										          <input type="checkbox" name="isPayed" value="PAY" onclick="setCanSave(this,<%=n%>)" checked/>
										          <input type="hidden" name="canSave" value="canSave"/>
										          <input type="hidden" name="status" id="status" value="DB"/>
										      <%}else{ %>
										          <input type="checkbox" name="isPayed" value="PAY" onclick="setCanSave(this,<%=n%>)" />
										          <input type="hidden" name="canSave" value=""/>
										          <input type="hidden" name="status" id="status" value=""/>
										      <%} %>
										<%} %>
										
										</td>
									     <td class="td_text_center" width="15%">
										    <input type="text" name="yearMonth" id="yearMonth" value="<%=item.getYearMonth() %>" size="5" class="disableText"
										    readonly tabindex="<%out.print(tabindex);tabindex++;%>">
										</td>
										<td class="td_text_center" width="15%" >
											 <input type="text" name="payDates" readonly id="payDates" size="20"  value="<%=item.getPayDate() %>" tabindex="-1" class="disableNumber">
										</td>
										<td class="td_text_center" width="15%">
										   <input type="text" name="amt" readonly id="amt" size="20"  value="<%=item.getAmt() %>" tabindex="-1" class="disableNumber">
										</td>
										<td class="td_text_center" width="10%"> 
										   <input type="text" name="countStockDate" readonly id="countStockDate" size="20"  value="<%=Utils.isNull(item.getCountStockDate()) %>" tabindex="-1"  class="disableNumber">
										</td>
										<td class="td_text_center" width="10%"> 
										<%if(item.isUsed()){ %>
										    <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
										  <%} %>
										</td>
									</tr>
							<%} }//for  %>
							
					</table>
					   <!-- Items -->
					   <p></p>
					  <%} %>
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
						           <%if(saTranForm.getBean().getItems() != null){ %>
						              <c:if test="${saTranForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="    บันทึก   " class="newPosBtnLong"> 
											</a>
											<a href="javascript:clearForm('${pageContext.request.contextPath}')">
											  <input type="button" value="   Clear   " class="newPosBtnLong">
											</a>
										</c:if>
									  <%} %>
									<a href="javascript:back('${pageContext.request.contextPath}','','add')">
									  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
									</a>							
								</td>
							</tr>
					  </table>
				  </div>
				
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
					<html:hidden property="bean.type"></html:hidden>
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