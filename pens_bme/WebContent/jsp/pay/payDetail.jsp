<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.isecinc.pens.web.pay.PayForm"%>
<%@page import="com.isecinc.pens.bean.PayBean"%>
<%@page import="com.isecinc.pens.dao.PayDAO"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="payForm" class="com.isecinc.pens.web.pay.PayForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");

//if(session.getAttribute("deptList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(PayDAO.searchDeptList(new PopupForm(),""));
	
	session.setAttribute("deptList",billTypeList);
//}
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
.holiday {
  width: 14%;
  background-color: #F78181;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(path){
	<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.REDEDIT}) ){%>
	   new Epoch('epoch_popup', 'th', document.getElementById('docDate'));
	<%}%>
	
	document.getElementsByName('bean.deptId')[0].value = '${payForm.bean.deptId}';
	
	loadSection();
	<%if( !"".equals(payForm.getBean().getSectionId())){ %>
	  document.getElementsByName('bean.sectionId')[0].value = <%=payForm.getBean().getSectionId()%>;
	<% } %>
	
	sumTotal();
	
	//popup print case print and save
	<%if(request.getAttribute("saveAndPrint") != null) {%>
	   printReportPopup(path);
	<%}%>
}

function printReportPopup(path){
	var docNo = document.getElementsByName("bean.docNo");
   // window.open(path + "/jsp/saleOrderAction.do?do=printListOrderProductReport&customerId="+customerId[0].value, "Print2", "width=100,height=100,location=No,resizable=No");
	window.open(path + "/jsp/popup/printPopup.jsp?report_name=PayInReport&docNo="+docNo[0].value, "Print2", "width=200,height=200,location=No,resizable=No");
}

function clearForm(path){
	var form = document.payForm;
	form.action = path + "/jsp/payAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.payForm;
	form.action = path + "/jsp/payAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.payForm;
	form.action = path + "/jsp/payAction.do?do=save&action=newsearch";
	form.submit();
	return true;
}

function printReport(path){
	var form = document.payForm;
	form.actionFlag.value ="saveAndPrint";
	
	form.action = path + "/jsp/payAction.do?do=save&action=newsearch";
	form.submit();
	return true;
}

function sumTotal(){
	var amount = document.getElementsByName("amount");
	var totalQtyTemp = 0;
	for(var i=0;i<amount.length;i++){
		if(amount[i].value != ""){
		   var amountTemp = amount[i].value;
		   amountTemp = amountTemp.replace(/\,/g,''); //alert(r);
		   totalQtyTemp += parseFloat(amountTemp);	
		}
	}
	var t = addCommas(Number(toFixed(totalQtyTemp,2)).toFixed(2));
	$('#totalQty').val(t);
	 
	 calcTotalQtyLetter(totalQtyTemp);
}

function calcTotalQtyLetter(totalQty){
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/calcNumberToLetterAjax.jsp",
			data : "totalQty=" + totalQty,
			async: true,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				$('#totalQtyLetter').val(returnString);
			}
		}).responseText;
	});
}

function loadSection(){
	var cboSection = document.getElementsByName('bean.sectionId')[0];
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/sectionAjax.jsp",
			data : "deptId=" + document.getElementsByName('bean.deptId')[0].value,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboSection.innerHTML=returnString;
			}
		}).responseText;
	});
}

function switchFlag(obj,name){
	if(name =='cashFlag'){
		if(obj.checked){
			document.getElementsByName('bean.chequeFlag')[0].checked = false;
		}
	}else{
		if(obj.checked){
			document.getElementsByName('bean.cashFlag')[0].checked = false;
		}	
	}
	
}

</script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe('${pageContext.request.contextPath}');MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerDoc.jsp"/></td>
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
				<jsp:param name="function" value="pay"/>
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
						<html:form action="/jsp/payAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                    <td>วันที่<font color="red"></font></td>
									<td>		
									<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.REDEDIT}) ){%>
										 <html:text property="bean.docDate" styleId="docDate" styleClass="normalText"  readonly="true"> </html:text>
									<%}else{ %>
									    <html:text property="bean.docDate" styleId="docDate" styleClass="disableText" readonly="true"> </html:text>
									<%} %>
										  Doc No
										 <html:text property="bean.docNo" styleId="docNo" styleClass="disableText" readonly="true"> </html:text>
									</td>
								</tr>
								<tr>
                                    <td> จ่าย<font color="red"></font></td>
									<td>		
										 <html:text property="bean.payToName" styleId="payToName" size="50"  maxlength="40"> </html:text>
									</td>
								</tr>
								<tr>
                                    <td> ฝ่าย<font color="red"></font></td>
									<td>		
										 <html:select property="bean.deptId" styleId="deptId" onchange="loadSection()">
											<html:options collection="deptList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> แผนก <font color="red"></font></td>
									<td>		
										 <html:select property="bean.sectionId" styleId="sectionId">
										
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td></td>
									<td>		
										<%--  <html:radio property="bean.paymethod" styleId="paymethod" value="C"/>เงินสด
										 <html:radio property="bean.paymethod" styleId="paymethod" value="CH"/>เช็ค --%> 
										 <html:checkbox property="bean.cashFlag" styleId="cashFlag" onclick="switchFlag(this,'cashFlag');">เงินสด</html:checkbox>
										 <html:checkbox property="bean.chequeFlag" styleId="chequeFlag" onclick="switchFlag(this,'chequeFlag');">เช็ค</html:checkbox>
									</td>
								</tr>
						   </table>
						   
						   <!-- Items -->
						   <table id="tblProduct" align="center" width="75%" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth">
						       <tr>
						            <th >ชื่อบัญชี</th>
									<th >รายการ</th>
									<th >จำนวนเงิน</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							System.out.println("results:"+session.getAttribute("results"));
							PayForm pay = (PayForm) session.getAttribute("payForm");
							List<PayBean> items = pay.getBean() !=null?pay.getBean().getItems():null;
							int tabindex = 1;
							if(items == null){
								
								for(int n=0;n<4;n++){
									if(n%2==0){
										tabclass="lineO";
									}
									%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="25%">
										  <input type="hidden" name="lineId" id="lineId" >
										  <input type="text" name="accountName" id="accountName" size="30" maxlength="15" tabindex="<%out.print(tabindex);tabindex++;%>">
										</td>
										<td class="td_text" width="30%"><input type="text" name="description" id="description" size="120" maxlength="56" tabindex="<%out.print(tabindex);tabindex++;%>"></td>
										<td class="td_text" width="20%"> 
										   <input type="text" name="amount" id="amount" size="30" tabindex="<%out.print(tabindex);tabindex++;%>"
										    onblur="isNum2Digit(this);sumTotal();"  class="enableNumber">
										</td>
									</tr>
							<% } //for
							}else{
                                 for(int n=0;n<4;n++){
                                	PayBean item = new PayBean();
                                	if(n<items.size()){
                                	  item = items.get(n);
                                	}
									if(n%2==0){
										tabclass="lineO";
									}
									%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="15%">
										  <input type="hidden" name="lineId" id="lineId" value="<%=item.getLineId()%>">
										  <input type="text" name="accountName" id="accountName" size="30" maxlength="15"
										    value="<%=Utils.isNull(item.getAccountName())%>" tabindex="<%out.print(tabindex);tabindex++;%>">
										</td>
										<td class="td_text" width="30%"><input type="text" name="description" id="description" size="120" 
										     maxlength="56" value="<%=Utils.isNull(item.getDescription())%>" tabindex="<%out.print(tabindex);tabindex++;%>"></td>
										<td class="td_text" width="15%">
                                          <input type="text" name="amount" id="amount" size="30" value="<%=Utils.isNull(item.getAmount())%>" 
                                          onblur="isNum2Digit(this);sumTotal();" class="enableNumber" tabindex="<%out.print(tabindex);tabindex++;%>">
                                        </td>
									</tr>
						    <%}//for
						    
						    
						    }//if %>
							 <tr>
                             	<td class="td_text_center" width="25%">
								  <b>จำนวนเงินตามตัวอักษร</b>
								</td>
								<td class="td_text" width="30%"><input class="disableTextBold" type="text" name="totalQtyLetter" id="totalQtyLetter" size="90" readonly></td>
								<td class="td_text" width="20%"> 
								   <input type="text" name="totalQty" id="totalQty" size="22" class="disableNumberBold" readonly>
								</td>
							</tr>
					</table>
						   <!-- Items -->
						   <p></p>
						 <%--  <table id="tblProduct" align="center" width="75%" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth">
						        <tr>
                                    <th class="td_text_center">DR</th>
									<th class="td_text_center">Amount</th>
									<th>&nbsp;--------</th>
									<th class="td_text_center">CR</th>
									<th class="td_text_center">Amount</th>
								</tr>
								<tr  class="lineO">
                                    <td>A/C No. <html:text property="bean.DR_AC_NO" styleId="DR_AC_NO" size="30"  maxlength="25"> </html:text></td>
									<td> </td>
									<td>&nbsp;</td>
									<td>A/C No. <html:text property="bean.CR_AC_NO" styleId="CR_AC_NO" size="30"  maxlength="25"> </html:text></td>
									<td></td>
								</tr>
								<tr  class="lineO"> 
                                    <td>Desc. &nbsp;&nbsp;&nbsp;<html:text property="bean.DR_DESC" styleId="DR_DESC" size="30"  maxlength="25"> </html:text></td>
									<td>
									   <html:text property="bean.DR_AMOUNT" styleId="DR_AMOUNT" size="20"  maxlength="20" onblur="isNum2Digit(this);"  styleClass="enableNumber"> </html:text>
									</td>
									<td>&nbsp;</td>
									<td>Desc. &nbsp;&nbsp;&nbsp;<html:text property="bean.CR_DESC" styleId="CR_DESC" size="30"  maxlength="25"> </html:text></td>
									<td>
									   <html:text property="bean.CR_AMOUNT" styleId="CR_AMOUNT" size="20"  maxlength="20" onblur="isNum2Digit(this);"  styleClass="enableNumber"> </html:text>
									</td>
								</tr>
								<tr  class="lineO">
                                    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;INPUT TAX</td>
									<td>
									   <html:text property="bean.DR_INPUT_TAX_AMOUNT" styleId="DR_INPUT_TAX_AMOUNT" size="20"  maxlength="20" onblur="isNum2Digit(this);"  styleClass="enableNumber"> </html:text>
									</td>
									<td>&nbsp;</td>
									<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ACCRUE W/T</td>
									<td>
									   <html:text property="bean.CR_ACC_WT_TAX_AMOUNT" styleId="CR_ACC_WT_TAX_AMOUNT" size="20"  maxlength="20" onblur="isNum2Digit(this);"  styleClass="enableNumber"> </html:text>
									</td>
								</tr>
								<tr  class="lineO">
                                    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TOTAL</td>
									<td>
									   <html:text property="bean.DR_TOTAL" styleId="DR_TOTAL" size="20"  maxlength="20" onblur="isNum2Digit(this);"  styleClass="enableNumber"> </html:text>
									</td>
									<td>&nbsp;</td>
									<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TOTAL</td>
									<td>
									   <html:text property="bean.CR_TOTAL" styleId="CR_TOTAL" size="20"  maxlength="20" onblur="isNum2Digit(this);"  styleClass="enableNumber"> </html:text>
									</td>
								</tr>
						   </table> --%>
						   
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									 <c:if test="${payForm.bean.canPrint == true}"> 
							            <a href="javascript:printReport('${pageContext.request.contextPath}')">
										  <input type="button" value="   พิมพ์   " class="newPosBtnLong">
										</a>
									</c:if>
										<a href="javascript:save('${pageContext.request.contextPath}')">
										  <input type="button" value="    บันทึก   " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>							
									</td>
								</tr>
							</table>
					  </div>
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
					<input type="hidden" name="actionFlag" value=""/>
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