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

	 $('#startDate').calendarsPicker({calendar: $.calendars.instance('thai','th')});
	 $('#endDate').calendarsPicker({calendar: $.calendars.instance('thai','th')});
}
function search(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=search&action=newsearch&pageName=<%=request.getParameter("pageName")%>";
	form.submit();
	return true;
}

function gotoPage(path,currPage){
	var form = document.shopForm;
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
					<table  border="0" cellpadding="3" cellspacing="0" class="body" width="50%">
				   <tr>
					<td align="right" width="10%"> �ҡ �ѹ�����</td>
					<td align="left" width="20%">
					<html:text property="bean.startDate" styleId="startDate" readonly="false" size="10" styleClass="\" autoComplete=\"off"/>
					&nbsp;&nbsp;�֧ �ѹ�����&nbsp;&nbsp;
				    <html:text property="bean.endDate" styleId="endDate" readonly="false" size="10" styleClass="\" autoComplete=\"off"/>
					</td>
				  </tr>
				   <tr>
					<td align="right" width="10%"> Group Code</td>
					<td align="left" width="20%">
					<html:text property="bean.groupCode" styleId="groupCode" maxlength="6"  size="10" styleClass="\" autoComplete=\"off"/>
					</td>
				  </tr>
				   <tr>
					<td align="right" width="10%"> �ٻẺ</td>
					<td align="left" width="20%">
					 <html:select property="bean.reportType">
					   <html:option value="DETAIL">Detail</html:option>
					   <html:option value="SUMMARY">Summary</html:option>
					 </html:select>
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
								  <input type="button" value="����" class="newPosBtn"> 
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
				  <%if(shopForm.getResults() != null && shopForm.getResults().size() >0) {%>
					<%if(shopForm.getBean().getReportType().equalsIgnoreCase("Detail")){ %>	
						 <jsp:include page="../../pageing.jsp">
					       <jsp:param name="totalPage" value="<%=shopForm.getTotalPage() %>"/>
					       <jsp:param name="totalRecord" value="<%=shopForm.getTotalRecord() %>"/>
					       <jsp:param name="currPage" value="<%=shopForm.getCurrPage() %>"/>
					       <jsp:param name="startRec" value="<%=shopForm.getStartRec() %>"/>
					       <jsp:param name="endRec" value="<%=shopForm.getEndRec() %>"/>
					        </jsp:include>
							<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						        <tr>
						            <th rowspan="2">Promotion ��ѡ</th>
									<th rowspan="2">Promotion</th>
									<th rowspan="2">�ҡ�ѹ���</th>
									<th rowspan="2">�֧�ѹ���</th>
									<th rowspan="2">�����</th>
									<th rowspan="2">Pens Item</th>
									<th rowspan="2">�ʹ��ª��</th>
									<th rowspan="2">��»�ա</th>
									<th rowspan="2" >�����</th>
									<th colspan="2">��ǹŴ�١���</th>
									<th rowspan="2">�ʹ��� �ѡ��ǹŴ�١���</th>
									<th rowspan="2">������ �纪���</th>
									<th rowspan="2">PENS �纤�Һ�ԡ�â�� 6%</th>
								 </tr>
								  <tr>
								    <th>%</th>
								    <th>AMT</th>
								  </tr>
								<% 
								String tabclass ="lineE";
								List<ShopBean> resultList = shopForm.getResults();
								for(int n=0;n<resultList.size();n++){
									ShopBean mc = (ShopBean)resultList.get(n);
									if(n%2==0){
										tabclass="lineO";
									}
									%>
										<tr class="<%=tabclass%>">
											<td class="td_text_center" width="10%"><%=mc.getPromoName()%></td>
											<td class="td_text_center" width="10%"><%=mc.getSubPromoName()%></td>
											<td class="td_text_center" width="5%"><%=mc.getStartDate()%></td>
										    <td class="td_text_center" width="5%"><%=mc.getEndDate()%></td>
											<td class="td_text_center" width="7%"><%=mc.getStyle()%></td>
											<td class="td_text_center" width="7%"><%=mc.getPensItem()%></td>
											<td class="td_text_center" width="5%"><%=mc.getQty()%></td>
											<td class="td_text_center" width="5%"><%=mc.getRetailSellAmt()%></td>
											<td class="td_text_center" width="5%"><%=mc.getWholeSellAmt()%></td>
											<td class="td_text_center" width="8%"><%=mc.getDiscountPercent()%></td>
											<td class="td_text_center" width="8%"><%=mc.getDiscountAmt()%></td>
											<td class="td_text_center" width="8%"><%=mc.getSellAfDisc()%></td>
											<td class="td_text_center" width="8%"><%=mc.getWacoalAmt()%></td>
											<td class="td_text_center" width="8%"><%=mc.getPensAmt()%></td>
										</tr>
								<%}%>
								<!-- Summary -->
								<%
								if(shopForm.getCurrPage()==shopForm.getTotalPage()){
								ShopBean mc = shopForm.getSummary();
								%>
								<tr class="hilight_text">
											<td class="td_text_center" width="10%"></td>
											<td class="td_text_center" width="10%"></td>
											<td class="td_text_center" width="5%"></td>
										    <td class="td_text_center" width="5%"></td>
											<td class="td_text_center" width="7%"></td>
											<td class="td_text_center" width="7%">���</td>
											<td class="td_text_center" width="5%"><%=mc.getQty()%></td>
											<td class="td_text_center" width="5%"></td>
											<td class="td_text_center" width="5%"></td>
											<td class="td_text_center" width="8%"></td>
											<td class="td_text_center" width="8%"></td>
											<td class="td_text_center" width="8%"></td>
											<td class="td_text_center" width="8%"></td>
											<td class="td_text_center" width="8%"></td>
										</tr>
							  <%} %>
						</table>
						<%}else{ %>
						
						 <jsp:include page="../../pageing.jsp">
					       <jsp:param name="totalPage" value="<%=shopForm.getTotalPage() %>"/>
					       <jsp:param name="totalRecord" value="<%=shopForm.getTotalRecord() %>"/>
					       <jsp:param name="currPage" value="<%=shopForm.getCurrPage() %>"/>
					       <jsp:param name="startRec" value="<%=shopForm.getStartRec() %>"/>
					       <jsp:param name="endRec" value="<%=shopForm.getEndRec() %>"/>
					        </jsp:include>
							<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						        <tr>
									<th rowspan="2">�����������</th>
									<th rowspan="2">�ʹ��ª��</th>
									<th rowspan="2">��»�ա</th>
									<th rowspan="2" >�����</th>
									<th colspan="2">��ǹŴ�١���</th>
									<th rowspan="2">�ʹ��� �ѡ��ǹŴ�١���</th>
									<th rowspan="2">������ �纪���</th>
									<th rowspan="2">PENS �纤�Һ�ԡ�â�� 6%</th>
								 </tr>
								  <tr>
								    <th>%</th>
								    <th>AMT</th>
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
											<td class="td_text_center" width="10%"><%=mc.getPromoName()%></td>
											<td class="td_text_center" width="7%"><%=mc.getQty()%></td>
											<td class="td_text_center" width="8%"><%=mc.getRetailSellAmt()%></td>
											<td class="td_text_center" width="8%"><%=mc.getWholeSellAmt()%></td>
											<td class="td_text_center" width="8%"><%=mc.getDiscountPercent()%></td>
											<td class="td_text_center" width="8%"><%=mc.getDiscountAmt()%></td>
											<td class="td_text_center" width="8%"><%=mc.getSellAfDisc()%></td>
											<td class="td_text_center" width="8%"><%=mc.getWacoalAmt()%></td>
											<td class="td_text_center" width="8%"><%=mc.getPensAmt()%></td>
										</tr>
								<%}
								}catch(Exception e){
									e.printStackTrace();
								}
								%>
								<!-- Summary -->
								<%
								if(shopForm.getCurrPage()==shopForm.getTotalPage()){
								ShopBean mc = shopForm.getSummary();
								%>
								<tr class="hilight_text">
								    <td class="td_text_center" width="10%">���</td>
									<td class="td_text_center" width="7%"><%=mc.getQty()%></td>
									<td class="td_text_center" width="8%"></td>
									<td class="td_text_center" width="8%"></td>
									<td class="td_text_center" width="8%"></td>
									<td class="td_text_center" width="8%"></td>
									<td class="td_text_center" width="8%"></td>
									<td class="td_text_center" width="8%"></td>
									<td class="td_text_center" width="8%"></td>
								</tr>
								<%} %>
						</table>
					  <%} %>
				   <%} %>
                    <!-- ****** RESULT ***************************************************************** -->

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