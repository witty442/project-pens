<%@page import="com.isecinc.pens.web.pay.PayWhiteForm"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.bean.PayBean"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="payWhiteForm" class="com.isecinc.pens.web.pay.PayWhiteForm" scope="session" />
<%
User user = (User) request.getSession().getAttribute("user");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
.day {
  width: 14%;
}
.holiday {
  width: 14%;
  background-color: #F78181;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(path){
	<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.WHITEDIT}) ){%>
	   new Epoch('epoch_popup', 'th', document.getElementById('docDate'));
	<%}%>
	
	document.getElementsByName('bean.deptId')[0].value = '${payWhiteForm.bean.deptId}';
	
	loadSection();
	<%if( !"".equals(payWhiteForm.getBean().getSectionId())){ %>
	  document.getElementsByName('bean.sectionId')[0].value = <%=payWhiteForm.getBean().getSectionId()%>;
	<% } %>
	
	sumTotal();
	
	//popup print case print and save
	<%if(request.getAttribute("saveAndPrint") != null) {%>
	   printReportPopup(path);
	<%}%>
}

function printReportPopup(path){
	var docNo = document.getElementsByName("bean.docNo")[0].value;
   // window.open(path + "/jsp/saleOrderAction.do?do=printListOrderProductReport&customerId="+customerId[0].value, "Print2", "width=100,height=100,location=No,resizable=No");
	//window.open(path + "/jsp/popup/printPayPopup.jsp?report_name=PayInReport&docNo="+docNo[0].value, "Print2", "width=400,height=400,location=No,resizable=No");
	 
	var url = path+"/jsp/popup/printPayWhitePopup.jsp?report_name=PayInReport&docNo="+docNo;
	PopupCenter(url,'Printer',800,350);
}

function clearForm(path){
	var form = document.payWhiteForm;
	form.action = path + "/jsp/payWhiteAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.payWhiteForm;
	form.action = path + "/jsp/payWhiteAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.payWhiteForm;
	form.action = path + "/jsp/payWhiteAction.do?do=save&action=newsearch";
	form.submit();
	return true;
}

function printReport(path){
	var form = document.payWhiteForm;
	form.actionFlag.value ="saveAndPrint";
	
	form.action = path + "/jsp/payWhiteAction.do?do=save&action=newsearch";
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
						<html:form action="/jsp/payWhiteAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                    <td>�ѹ���<font color="red"></font></td>
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
                                    <td> ����<font color="red"></font></td>
									<td>		
										 <html:text property="bean.payToName" styleId="payToName" size="50"  maxlength="40"> </html:text>
									</td>
								</tr>
								<tr>
                                    <td> ����<font color="red"></font></td>
									<td>		
										 <html:select property="bean.deptId" styleId="deptId" onchange="loadSection()">
											<html:options collection="deptList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> Ἱ� <font color="red"></font></td>
									<td>		
										 <html:select property="bean.sectionId" styleId="sectionId">
										
									    </html:select>
									</td>
								</tr>
								<%-- <tr>
                                    <td></td>
									<td>		
										 <html:radio property="bean.paymethod" styleId="paymethod" value="C"/>�Թʴ
										 <html:radio property="bean.paymethod" styleId="paymethod" value="CH"/>�� 
										 <html:checkbox property="bean.cashFlag" styleId="cashFlag" onclick="switchFlag(this,'cashFlag');">�Թʴ</html:checkbox>
										 <html:checkbox property="bean.chequeFlag" styleId="chequeFlag" onclick="switchFlag(this,'chequeFlag');">��</html:checkbox>
									</td>
								</tr> --%>
						   </table>
						   
						   <!-- Items -->
						   <table id="tblProduct" align="center" width="75%" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth">
						       <tr>
						            <th >���ͺѭ��</th>
									<th >��¡��</th>
									<th >�ӹǹ�Թ</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							//System.out.println("results:"+session.getAttribute("results"));
							PayWhiteForm pay = (PayWhiteForm) session.getAttribute("payWhiteForm");
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
								  <b>�ӹǹ�Թ�������ѡ��</b>
								</td>
								<td class="td_text" width="30%"><input class="disableTextBold" type="text" name="totalQtyLetter" id="totalQtyLetter" size="90" readonly></td>
								<td class="td_text" width="20%"> 
								   <input type="text" name="totalQty" id="totalQty" size="22" class="disableNumberBold" readonly>
								</td>
							</tr>
					</table>
						   <!-- Items -->
						   <p></p>

						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									 <c:if test="${payWhiteForm.bean.canPrint == true}"> 
							            <a href="javascript:printReport('${pageContext.request.contextPath}')">
										  <input type="button" value="   �����   " class="newPosBtnLong">
										</a>
									</c:if>
										<a href="javascript:save('${pageContext.request.contextPath}')">
										  <input type="button" value="    �ѹ�֡   " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   �Դ˹�Ҩ�   " class="newPosBtnLong">
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