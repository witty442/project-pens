<%@page import="com.isecinc.pens.web.buds.BudsAllForm"%>
<%@page import="com.isecinc.core.model.I_PO"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.ConfPickingBean"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="budsAllForm" class="com.isecinc.pens.web.buds.BudsAllForm" scope="session" />
<%
ConfPickingBean bean = ((BudsAllForm)session.getAttribute("budsAllForm")).getBean().getConfPickingBean();
System.out.println("ConfPickingBean:"+bean);
String role = ((User)session.getAttribute("user")).getType();
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
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight")); 
	String hideAll = "true"; 
	String pageName = !Utils.isNull(request.getParameter("pageName")).equals("")?Utils.isNull(request.getParameter("pageName")):budsAllForm.getPageName();
	String subPageName = !Utils.isNull(request.getParameter("subPageName")).equals("")?Utils.isNull(request.getParameter("subPageName")):budsAllForm.getSubPageName();

%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- For fix Head and Column Table -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
}
function search(path){
	var form = document.budsAllForm;
	form.action = path + "/jsp/budsAllAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function openEdit(mode,pickingNo){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value ;
	//set Move page to Detail
	<%if( !"BudsConfPicking".equalsIgnoreCase(subPageName)){%>
	   document.getElementById("pageName").value = "ConfPicking";
	   document.getElementById("subPageName").value = "ConfPicking";
	<%}else{%>
	   document.getElementById("pageName").value = "ConfPicking";
	   document.getElementById("subPageName").value = "BudsConfPicking";
	<%}%>
	
	form.action = path + "/jsp/budsAllAction.do?do=viewDetail&mode="+mode+"&pickingNo="+pickingNo;
	form.submit();
	return true;
}
function exportExcel(path){
	var form = document.budsAllForm;
	
	form.action = path + "/jsp/budsAllAction.do?do=export";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.payForm;
	var path = document.getElementById("path").value ;
	form.action = path + "/jsp/payAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}
function clearForm(path){
	var form = document.budsAllForm;
	form.action = path + "/jsp/budsAllAction.do?do=prepareSearchHead&action=new";
	form.submit();
	return true;
}
function openPopupPage(pageName){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value;
    var param = "&pageName="+pageName+"&selectone=true&hideAll=true";
	var url = path + "/jsp/popupAction.do?do=prepareAll&action=new"+param;
	
	PopupCenterFullHeight(url,"",700);
}

function setDataPopupValue(code,desc,pageName){
	if("PICKING_NO"==pageName){
	   document.getElementById("pickingNo").value = code;
	}else if("PICKING_NO_PRINT"==pageName){
	   document.getElementById("pickingNo").value = code;
	}else if("TRANSPORT"==page){
	   document.getElementById("amphurCri").value = code;
	}
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
				<jsp:param name="function" value="<%=subPageName%>"/>
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
						<html:form action="/jsp/budsAllAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
				    	<!-- ***** Criteria ******* -->
				    	<table align="center" border="0" cellpadding="3" cellspacing="0" >
					        <tr>
				                <td> Picking No</td>
								<td>
								    <html:text property="bean.confPickingBean.pickingNo" 
								    styleClass="\" autoComplete=\"off" styleId="pickingNo"></html:text>
								    <%if("ConfPicking".equalsIgnoreCase(subPageName)){ %>
									    <input type="button" name="btTransport" value="..." 
									    onclick="openPopupPage('PICKING_NO')" class="newPosBtnLong"/>
								    <%}else if("BudsConfPicking".equalsIgnoreCase(subPageName)){ %>
								       <input type="button" name="btTransport" value="..." 
									    onclick="openPopupPage('PICKING_NO_PRINT')" class="newPosBtnLong"/>
								    <%} %>
								     &nbsp;Transaction Date 
								    <html:text property="bean.confPickingBean.transactionDate" styleId="transactionDate" size="10" readonly="true" styleClass=""/> 
								  
							    </td>
							 </tr>
						</table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="100%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="  ค้นหา  " class="newPosBtnLong">
								</a>&nbsp;
								<%if("ConfPicking".equalsIgnoreCase(subPageName)){ %>
									<a href="javascript:openEdit('add','')">
									  <input type="button" value="เพิ่มรายการใหม่ " class="newPosBtnLong">
									</a>
								<%} %>
								&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value=" Clear  " class="newPosBtnLong">
								</a>&nbsp;
								
							</td>
							 <td align="right" width="40%" nowrap></td>
						</tr>
					</table>
			  </div>
			
			     <!-- ****** RESULT *************************************************** -->
			    <c:if test="${budsAllForm.bean.confPickingBean.itemsList != null}">
                  	<% 
					   int totalPage = budsAllForm.getTotalPage();
					   int totalRecord = budsAllForm.getTotalRecord();
					   int currPage =  budsAllForm.getCurrPage();
					   int startRec = budsAllForm.getStartRec();
					   int endRec = budsAllForm.getEndRec();
					   int pageSize = budsAllForm.getPageSize();
					   int no = Utils.calcStartNoInPage(currPage, pageSize);
					%>
					
					<%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
					
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearch">
						       <tr>
						            <th >Picking No</th>
									<th >Tranasction Date</th>
									<th > สายขนส่ง </th>
									<th >Amount (Exc.vat)</th>
									<th >Vat Amount</th>
									<th >Amount (inc.vat)</th>
									<th >สถานะ</th>
									<%if( !"BudsConfPicking".equalsIgnoreCase(subPageName)){%>
									  <th >แก้ไข/View</th>
									<%}else{%>
									  <th >Gen Invoice/View</th>
									<%} %>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<ConfPickingBean> resultList = budsAllForm.getBean().getConfPickingBean().getItemsList();
							
							for(int n=0;n<resultList.size();n++){
								ConfPickingBean mc = (ConfPickingBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="5%"><%=mc.getPickingNo() %></td>
										<td class="td_text_center" width="10%"><%=mc.getTransactionDate()%></td>
										<td class="td_text_center" width="10%"><%=mc.getRegionCri()%></td>
										<td class="td_text_right" width="10%"><%=mc.getTotalAmount()%></td>
									 	<td class="td_text_right" width="10%"><%=mc.getVatAmount()%></td>
									    <td class="td_text_right" width="10%"><%=mc.getNetAmount()%></td>
									    <td class="td_text_center" width="5%"><%=mc.getStatus()%></td>
										<td class="td_text_center" width="10%"><font size="2">
											<%if( !"BudsConfPicking".equalsIgnoreCase(subPageName) && !mc.getStatus().equals(I_PO.STATUS_LOADING)){ %>
												 <a href="javascript:openEdit('edit','<%=mc.getPickingNo()%>')">
												             แก้ไข
												 </a>
											<%}else{ %>
											    <%if(mc.getStatus().equals(I_PO.STATUS_LOADING)){%>
												    <a href="javascript:openEdit('view','<%=mc.getPickingNo()%>')">
													      View
													 </a>
												<%}else{ %>
												    <a href="javascript:openEdit('view','<%=mc.getPickingNo()%>')">
													      Gen Invoice
													 </a>
												<%} %>
											<%} %>
										</font>
										</td>
										
									</tr>
							<%} %>
							 
					</table>
				</c:if>
			     <!-- ***************************************************************** -->
					<!-- hidden field -->
					<input type="hidden" name="pageName" id="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="subPageName" id="subPageName" value="<%=request.getParameter("subPageName") %>"/>
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
</body>
</html>

 <!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->