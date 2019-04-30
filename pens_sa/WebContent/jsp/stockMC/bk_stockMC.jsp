<%@page import="util.UserUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@page import="util.SIdUtils"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<jsp:useBean id="stockMCForm" class="com.isecinc.pens.web.stockmc.StockMCForm" scope="session" />
<%
try{
int tabIndex = 0;
String[] uom = null;
if(stockMCForm.getResults() != null){
	tabIndex = stockMCForm.getResults().size()*2;
}

List<String[]> uomList = StockMCUtils.getUomList();
User user = (User)session.getAttribute("user");
String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}
String pageName = Utils.isNull(request.getParameter("pageName")); 
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<style type="text/css">
 #scroll {
<%if(!"0".equals(screenWidth)){%>
     width:101%;  
    <%-- width:<%=screenWidth%>px;  --%>
    height:250px;
	overflow:scroll;
	/* position:relative; */
	white-space:nowrap;
<%}%>
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/stockMC.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

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
 

<script type="text/javascript">
function loadMe(){
	<%if(stockMCForm.getResults() != null && stockMCForm.getResults().size() >0){ %>
		<% List<StockMCBean> results = stockMCForm.getResults();
		 for(int i=0;i<results.size();i++){ %>
           $('#expireDate1_<%=(i+1)%>').calendarsPicker({calendar: $.calendars.instance('thai','th')});
           $('#expireDate2_<%=(i+1)%>').calendarsPicker({calendar: $.calendars.instance('thai','th')});
           $('#expireDate3_<%=(i+1)%>').calendarsPicker({calendar: $.calendars.instance('thai','th')});
		<% }
	  }
	%>
	
}
function hideTrHead(){
	  var tr = document.getElementById("tr_head_blank");
      tr.style.display = "none";
}
function backsearch(path) {
    document.stockMCForm.action = path + "/jsp/stockMCAction.do?do=searchHead&action=back";
	document.stockMCForm.submit();
}
function clearForm(path){
	var form = document.stockMCForm;
    var param ="&action=add";
	
	form.action = path + "/jsp/stockMCAction.do?do=clearForm"+param;
	form.submit();
	return true;
}
function loadItem(path){
	var form = document.stockMCForm;
    var param ="";
	if(form.customerCode.value ==''){
		alert("��س��к���ҧ ");
		form.customerCode.focus();
		return false;
	}
	form.action = path + "/jsp/stockMCAction.do?do=loadItem"+param;
	form.submit();
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
				<jsp:param name="function" value="StockMC"/>
				<jsp:param name="code" value=""/>
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
						<html:form action="/jsp/stockMCAction">
						<jsp:include page="../error.jsp"/>
						
						<!-- Hidden -->
		                 <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
						 <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
						   
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td colspan="4" align="center">
								   <table align="center" border="0" cellpadding="3" cellspacing="0" width="80%">
									<tr>
										<td align="right" nowrap>
										   �ѹ����Ǩ��ʵ�͡
										</td>
										<td align="left" colspan="3" nowrap>
											<html:text property="bean.stockDate" styleId="stockDate" size="10" readonly="true" styleClass="disableText"/>
											&nbsp;&nbsp;
											����-���ʡ�� �ի� <font color="red">*</font>
											<html:text property="bean.mcName" styleId="mcName" size="40" styleClass="\" autoComplete=\"off"/>
										</td>
									</tr>
									<tr>
						                <td  align="right" nowrap> ��ҧ  <font color="red">*</font></td>
										<td colspan="2" nowrap>
										 <html:text property="bean.customerCode" styleId="customerCode" 
										   size="10" styleClass="\" autoComplete=\"off"
										   onkeypress="getCustNameKeypress(event,this)" onblur="checkCustNameOnblur(event,this)"/>
										    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC')"/>   
										   <html:text property="bean.customerName" styleId="customerName" size="40" styleClass="disableText" readonly="true"/>
										   &nbsp;&nbsp;
										 �Ң� <font color="red">*</font>
										 <html:text property="bean.storeCode" styleId="storeCode" size="30" styleClass="\" autoComplete=\"off"/>
										
										  &nbsp;&nbsp; 
										   <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
											   <c:if test="${stockMCForm.bean.canEdit ==true}">
												<a href="#" onclick="return save('${pageContext.request.contextPath}');">
												  <input type="button" value="�ѹ�֡��¡��" class="newPosBtnLong">
												</a>	
											   </c:if>	
											    &nbsp;
											  <a href="#" onclick="clearForm('${pageContext.request.contextPath}');">
												  <input type="button" value="Clear"  class="newPosBtnLong">
											    </a>
										    <%} %>
										  &nbsp;
										  <a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
											  <input type="button" value="�Դ˹�Ҩ�"  class="newPosBtnLong">
										    </a>
										</td>
									</tr>	
									</table>
							     </td>
							  </tr>
							<tr>
								<td colspan="4" align="center">
								<div align="left" style="margin-left:13px;">
								    <c:if test="${stockMCForm.bean.canEdit =='true'}">
								       <div align="left">
								          <%--  <input type="button" class="newPosBtn" value="������¡��" onclick="addRow('${pageContext.request.contextPath}');"/>
								           <input type="button" class="newPosBtn" value="ź��¡��" onclick="removeRow('${pageContext.request.contextPath}');"/>	
								            --%>
								            <c:if test="${stockMCForm.mode =='add'}">
								              <input type="button" class="newPosBtnLong" value="�ʴ���¡���Թ���" onclick="loadItem('${pageContext.request.contextPath}');"/>
							                </c:if>
							           </div> 
								     </c:if>
								</div>
					
								<!--  Results  -->
								<table id="tblProductHead" align="center" border="1" cellpadding="3" cellspacing="2" 
								class="tableSearchNoWidthNoOverflow" width="100%">
									<tr>
										<th rowspan="3">����<br/>�Թ���</th>
										<th rowspan="3">������</th>
										<th rowspan="3">��������´�Թ���</th>
										<th rowspan="3">��è�</th>
										<th rowspan="3">����<br/>�Թ���</th>
										<th rowspan="3">�Ҥ�<br/>��ա</th>
										<th rowspan="3">�Ҥ� <br/>�������</th>
										<th rowspan="3">��</th>
										<th colspan="12">ʵ�͡�Թ���</th>
									</tr>
									<tr>
									    <th rowspan="2">�<br/>�к�<br/>��ҧ</th>
										<th rowspan="2">��ѧ<br/>��ҹ</th>
										<th rowspan="2">˹���<br/>��è�</th>
									    <th colspan="3">�����������ط�� 1</th>
										<th colspan="3">�����������ط�� 2</th>
										<th colspan="3">�����������ط�� 3</th>
									</tr>
									<tr> 
										<th >˹��<br/>��ҹ</th>
										<th >˹���<br/>��è�</th>
										<th >�ѹ�������</th>
										<th >˹��<br/>��ҹ</th>
										<th >˹���<br/>��è�</th>
										<th >�ѹ�������</th>
										<th >˹��<br/>��ҹ</th>
										<th >˹���<br/>��è�</th>
										<th >�ѹ�������</th>
									</tr>
									
									 <tr id="tr_head_blank" height="1px">
										<td class ="td_text_center"  width="4%"></td>
										<td class="td_text" width="8%"></td>
										<td class="td_text"  width="8%"></td><!-- ProductName -->
										<td class="td_text_center" width="5%"></td>
										<td class="td_text_number" width="4%"></td>
										<td class="td_text_center"  width="5%"></td>
										<td class="td_text_center"  width="4%"></td><!-- PromotionPrice -->
										<td class="td_text_center"  width="3%"></td>
										<td class="td_text_center"  width="4%"></td>
										<td class="td_text_center"  width="4%"></td>
										<td class="td_text_center"  width="5%"></td>
										
										<td class="td_number" width="4%"></td>
										<td class="td_number"  width="5%"></td>
										<td class="td_text_center" width="6%"></td>
										
										<td class="td_number" width="4%"></td>
										<td class="td_number" width="5%" ></td>
										<td class="td_text_center" width="6%"></td>
										
										<td class="td_number" width="3%"></td>
										<td class="td_number" width="4%" ></td>
										<td class="td_text_center" width="6%"></td>
									</tr> 
								</table>
								<div id ="scroll">
								<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="2" 
								    class="tableSearchNoWidth" width="100%">
									<tbody>
								<%
								if(stockMCForm.getResults() != null && stockMCForm.getResults().size() >0){
								 List<StockMCBean> results = stockMCForm.getResults();
								 String tabclass = "";StockMCBean b = null;
								 for(int i=0;i<results.size();i++){
									 b = results.get(i);
									 if(i%2==0){
										 tabclass="lineO";
									 }else{
										 tabclass="lineE"; 
									 }
								%>
									<tr class="<%=tabclass%>">
										<td class ="td_text_center"  width="4%">
											<input type="text" name="productCode" id="productCode" value ="<%=b.getProductCode()%>" size="3"
										       
											    class="disableText"  tabindex="<%=tabIndex%>" autoComplete="off" readonly   
											  /> 
											 <input type="hidden" name="lineId" id="lineId" value ="<%=b.getLineId()%>"/>
										</td>
										<td class="td_text" width="8%">
									       <input type="text" name="barcode" id="barcode" value ="<%=b.getBarcode()%>" size="11" 
											    class="disableText"  tabindex="<%=tabIndex%>" autoComplete="off" readonly  
											  /> 
										</td>
										<td class="td_text"  width="8%">
									       <input type="text" name="productName" id="productName" value ="<%=b.getProductName()%>" size="13" readonly class="disableText"/>	
										</td>
										<td class="td_text_center" width="5%">   
									       <input type="text" name="productPackSize" id="productPackSize" value ="<%=b.getProductPackSize()%>" size="4" readonly class="disableNumber"/>	
										</td>
										<td class="td_text_number" width="4%">
										 <input type="text" name="productAge" id="productAge" value ="<%=b.getProductAge()%>" size="2" readonly class="disableText"/>	  
										</td>
										<td class="td_text_center"  width="5%">
										 <input type="text" name="retailPriceBF" id="retailPriceBF" value ="<%=b.getRetailPriceBF()%>" 
										 size="3" readonly class="disableNumber"/>	  
										</td>
										<td class="td_text_center"  width="4%">
										 <%tabIndex++; %>
										 <input type="text" name="promotionPrice" tabindex="<%=tabIndex %>" id="promotionPrice" 
										 value ="<%=b.getPromotionPrice()%>" size="3"  class="enableNumber"
										 onkeydown="return isNum(this,event);" autocomplete="off"/>	  
										</td>
										<td class="td_text_center"  width="3%">
										 <%tabIndex++; %>
										 <input type="text" name="legQty" tabindex="<%=tabIndex %>" id="legQty" 
										 value ="<%=b.getLegQty()%>" size="1"  class="enableNumber" 
										 onkeydown="return isNum0to9andpoint(this,event);" autocomplete="off"/>	  
										</td>
										<td class="td_text_center"  width="4%">
										 <%tabIndex++; %>
										 <input type="text" name="inStoreQty" tabindex="<%=tabIndex %>" id="inStoreQty" 
										 value ="<%=b.getInStoreQty()%>" size="1"  class="enableNumber"
										 onkeydown="return isNum0to9andpoint(this,event);" autocomplete="off"/>	  
										</td>
										<td class="td_text_center"  width="4%">
										 <%tabIndex++; %>
										  <input type="text" name="backendQty" tabindex="<%=tabIndex %>" id="backendQty" 
										  value ="<%=b.getBackendQty()%>" size="1" class="enableNumber"
										  onkeydown="return isNum0to9andpoint(this,event);" autocomplete="off"/>	  
										</td>
										<td class="td_text_center"  width="5%">
										 <%tabIndex++; %>
										  <select name="uom" tabindex="<%=tabIndex %>" id="uom">
											 <% for(int n=0;n<uomList.size();n++){ 
												 uom = uomList.get(n);
												 if(b.getUom().equalsIgnoreCase(uom[0])){
											        out.println("<option selected value='"+uom[0]+"'>"+uom[1]+"</option>");
												 }else{
													out.println("<option value='"+uom[0]+"'>"+uom[1]+"</option>"); 
												 }
											 }
											 %>
											</select>
										</td>
										<!-- ********* 1******************************* -->
										<td class="td_number" width="4%">
										    <%tabIndex++; %>
											<input type="text"
											tabindex="<%=tabIndex %>"
											value="<%=b.getFrontendQty1()%>" name="frontendQty1" size="1"
											onkeydown="return isNum0to9andpoint(this,event);"
											class="enableNumber" autocomplete="off"/>
										</td>
										<td class="td_number"  width="5%">
										    <%tabIndex++; %>
											<select name="uom1" tabindex="<%=tabIndex %>" id="uom1">
											 <% for(int n=0;n<uomList.size();n++){ 
												 uom = uomList.get(n);
												 if(b.getUom1().equalsIgnoreCase(uom[0])){
											        out.println("<option selected value='"+uom[0]+"'>"+uom[1]+"</option>");
												 }else{
													out.println("<option value='"+uom[0]+"'>"+uom[1]+"</option>"); 
												 }
											 }
											 %>
											</select>
										</td>
										<td class="td_text_center" width="6%">
										   <%tabIndex++; %>
										   <input type='text' name='expireDate1' size='7' 
										   value='<%=b.getExpireDate1()%>'
										   id="expireDate1_<%=b.getNo() %>"  readonly >
									       <font color="red"></font>
										</td>
										<!-- ********* 2******************************* -->
										<td class="td_number" width="4%">
										    <%tabIndex++; %>
											<input type="text"
											tabindex="<%=tabIndex %>"
											value="<%=b.getFrontendQty2()%>" name="frontendQty2" size="1"
											onkeydown="return isNum0to9andpoint(this,event);"
											class="enableNumber" autocomplete="off"/>
										</td>
										<td class="td_number" width="5%" >
										    <%tabIndex++; %>
											<select name="uom2" tabindex="<%=tabIndex %>" id="uom2">
											 <% for(int n=0;n<uomList.size();n++){ 
												 uom = uomList.get(n);
												 if(b.getUom2().equalsIgnoreCase(uom[0])){
											        out.println("<option selected value='"+uom[0]+"'>"+uom[1]+"</option>");
												 }else{
													out.println("<option value='"+uom[0]+"'>"+uom[1]+"</option>"); 
												 }
											 }
											 %>
											</select>
										</td>
										<td class="td_text_center" width="6%">
										   <%tabIndex++; %>
										   <input type='text' name='expireDate2' size='7'
										   value='<%=b.getExpireDate2()%>' id="expireDate2_<%=b.getNo() %>"  readonly>
									       <font color="red"></font>
										</td>
										<!-- ********* 3******************************* -->
										<td class="td_number" width="3%">
										    <%tabIndex++; %>
											<input type="text"
											tabindex="<%=tabIndex %>"
											value="<%=b.getFrontendQty3()%>" name="frontendQty3" size="1"
											onkeydown="return isNum0to9andpoint(this,event);"
											class="enableNumber" autocomplete="off"/>
										</td>
										<td class="td_number" width="4%" >
										    <%tabIndex++; %>
											<select name="uom3" tabindex="<%=tabIndex %>" id="uom3">
											 <% for(int n=0;n<uomList.size();n++){ 
												 uom = uomList.get(n);
												 if(b.getUom3().equalsIgnoreCase(uom[0])){
											        out.println("<option selected value='"+uom[0]+"'>"+uom[1]+"</option>");
												 }else{
													out.println("<option value='"+uom[0]+"'>"+uom[1]+"</option>"); 
												 }
											 }
											 %>
											</select>
										</td>
										<td class="td_text_center" width="6%">
										   <%tabIndex++; %>
										   <input type='text' name='expireDate3' size='7'
										   value='<%=b.getExpireDate3()%>' id="expireDate3_<%=b.getNo() %>" readonly>
									       <font color="red"></font>
										</td>
									</tr>
								<%} }//for %>
								  </tbody>
								</table>
							   </div>
								<!--  Results -->
								</td>
							</tr>
				
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								 <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
								   <c:if test="${stockMCForm.bean.canEdit ==true}">
									<a href="#" onclick="return save('${pageContext.request.contextPath}');">
									  <input type="button" value="�ѹ�֡��¡��" class="newPosBtnLong">
									</a>	
								   </c:if>	
								 
								  &nbsp;
								   <a href="#" onclick="clearForm('${pageContext.request.contextPath}');">
									  <input type="button" value="Clear"  class="newPosBtnLong">
								    </a>
								    <%} %>
								   <a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									  <input type="button" value="�Դ˹�Ҩ�"  class="newPosBtnLong">
								    </a>
								 </td>
							</tr>
						</table>
						
						<!-- Hidden Field -->
						 <html:hidden property="bean.id"/>
						 <html:hidden property="bean.lineIdDeletes" styleId="lineIdDeletes"/>
						 <input type="hidden" name="pageName" value="<%=pageName %>"/>
					     <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
						 <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
						<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
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
<script>
	//hide row tableHead
	//hideTrHead();
</script>
<%}catch(Exception e){
  e.printStackTrace();
}
	%>

