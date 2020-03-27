<%@page import="com.isecinc.pens.web.shop.ShopBean"%>
<%@page import="com.isecinc.pens.web.shop.ShopAction"%>
<%@page import="com.pens.util.SIdUtils"%>
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
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
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
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));
	String pageName = Utils.isNull(request.getParameter("pageName"));
%>

<script type="text/javascript">
function loadMe(){
	//new Epoch('epoch_popup', 'th', document.getElementById('asOfDate'));
	 $('#asOfDate').calendarsPicker({calendar: $.calendars.instance('thai','th')});
}

function openPopup(path,pageName,fieldName,multipleCheck){
	var form = document.shopForm;
	var param = "&pageName="+pageName;
	    param +="&fieldName="+fieldName;
        param +="&groupStore="+form.custGroup.value;
        param +="&groupStoreName=MAYA&hideAll=true";
        
	url = path + "/jsp/popupSearchAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName,fieldName){
	var form = document.shopForm;
	document.getElementById(fieldName).value = code;
} 
function search(path){
	var form = document.shopForm;
   var startDate = form.asOfDate;
   
   if(asOfDate.value =="" ){
	   asOfDate.focus();
	   alert("กรุณากรอกข้อมูล วันที่ขาย (As Of Date)");
	   return false;
   }
   form.action = path + "/jsp/shopAction.do?do=search&action=newsearch&pageName=<%=request.getParameter("pageName")%>";
   form.submit();
   return true;
}
function gotoPage(currPage){
   var form = document.shopForm;
   var path = form.path.value;
   form.action = path + "/jsp/shopAction.do?do=search&pageName=<%=request.getParameter("pageName")%>&currPage="+currPage;
   form.submit();
   return true;
}
function exportExcel(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=export&pageName=<%=request.getParameter("pageName")%>";
	form.submit();
	return true;
}
function clearForm(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=request.getParameter("pageName")%>";
	form.submit();
	return true;
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
			<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="<%=pageName %>"/>
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
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
					 <!-- ************* CRITERIA ********************************************************* -->
					<table  border="0" cellpadding="3" cellspacing="0" width="50%">
					<tr>
						<td align="right" width="10%"> Shop</td>
						<td align="left" width="6%"><html:text property="bean.custGroup" styleId="custGroup" styleClass="disableText" readonly="true" size="15"/></td>
						<td align="right" width="6%"> </td>
						<td align="left" width="10%"></td>
					  </tr>
					 <tr>
						<td align="right" width="10%"> วันที่ขาย (As Of Date)<font color="red">*</font></td>
						<td align="left" width="6%"><html:text property="bean.asOfDate" styleId="asOfDate" readonly="false" size="15" styleClass="\" autoComplete=\"off"/></td>
						<td align="right" width="6%"> </td>
						<td align="left" width="10%"></td>
					  </tr>
					   <tr>
						<td align="right" width="10%"> Group</td>
						<td align="left" width="6%">
						<html:text property="bean.groupCodeFrom" styleId="groupCodeFrom" size="15" maxlength="6"/>
						 <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_GroupCode','groupCodeFrom','false')"/>   
						</td>
						<td align="right" width="6%"> </td>
						<td align="left" width="10%">   
						</td>
					  </tr>
					  <tr>
						<td align="right" width="10%"> From Style</td>
						<td align="left" width="6%"><html:text property="bean.styleFrom" styleId="styleFrom"size="15"/>
						 <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_Style','styleFrom','false')"/>   
						</td>
						<td align="right" width="6%">To Style</td>
						<td align="left" width="10%"><html:text property="bean.styleTo" styleId="styleTo" size="15"/>
						<input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_Style','styleTo','false')"/> 
						</td>
					  </tr>
					  <tr>
						<td align="right" width="10%"> From Pens Item</td>
						<td align="left" width="6%"><html:text property="bean.pensItemFrom" styleId="pensItemFrom" size="15"/>
						<input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_PensItem','pensItemFrom','false')"/> 
						</td>
						<td align="right" width="6%">To Pens Item</td>
						<td align="left" width="10%"><html:text property="bean.pensItemTo" styleId="pensItemTo"  size="15"/>
						<input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_PensItem','pensItemTo','false')"/> 
						</td>
					  </tr>
					</table>
				     <!-- ************* CRITERIA ********************************************************* -->
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="80%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtn"> 
								</a>&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtn">
								</a>&nbsp;
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtn">
								</a>
							</td>
							 <td align="right" width="20%">
							</td>
						</tr>
					</table>
			     </div>

				   <!-- ****** RESULT ***************************************************************** -->
				
					<c:if test="${shopForm.results != null}">
					  <br/>
				    <% 
					   int totalPage = shopForm.getTotalPage();
					   int totalRecord = shopForm.getTotalRecord();
					   int currPage =  shopForm.getCurrPage();
					   int startRec = shopForm.getStartRec();
					   int endRec = shopForm.getEndRec();
					   int pageSize = shopForm.getPageSize();
					   int no = Utils.calcStartNoInPage(currPage, pageSize);
					%>
					<%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
					
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearch">
						       <tr>
						            <th >Group</th>
									<th >Pens Item</th>
									<th >Matarial Master</th>
									<th >Barcode</th>
									<th >Initial Stock</th>
									<th >Trans In Qty</th>
									<th >Sale Out Qty</th>
									<th >Return Qty</th>
									<th >Adjust Qty</th>
									<th >Stock Short Qty</th>
									<th >Onhand Qty</th>
							   </tr>
							<% 
							System.out.println("currPage:"+currPage+",startRec:"+startRec+",endRec:"+endRec);
							
							String tabclass ="lineE";
							List<ShopBean> resultList = shopForm.getResults();
							
							for(int n=startRec;n < endRec;n++){
								ShopBean mc = (ShopBean)resultList.get(n);
								tabclass ="lineE";
								if(n%2==0){
									tabclass="lineO";
								}
								%>
								<tr class="<%=tabclass%>">
									<td class="td_text_center" width="8%"><%=mc.getGroupCode() %></td>
									<td class="td_text_center" width="8%"><%=mc.getPensItem()%></td>
									<td class="td_text" width="8%"><%=mc.getStyle()%></td>
								    <td class="td_text" width="8%"><%=mc.getBarcode() %></td>
								    <td class="td_number" width="7%"><%=mc.getInitSaleQty() %></td>
									<td class="td_number" width="7%"><%=mc.getTransInQty() %></td>
									<td class="td_number" width="7%"><%=mc.getSaleOutQty() %></td>
									<td class="td_number" width="7%"><%=mc.getSaleReturnQty() %></td>
									<td class="td_number" width="7%"><%=mc.getAdjustQty() %></td>
									<td class="td_number" width="7%"><%=mc.getStockShortQty() %></td>
									<td class="td_number" width="7%"><%=mc.getOnhandQty() %></td>
								</tr>
							<%} %>
					  </table>
				  </c:if>
                    <!-- ****** RESULT ***************************************************************** -->

					<!-- hidden field -->
					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
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
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
<script>
   loadCalendar();
</script>
</body>
</html>