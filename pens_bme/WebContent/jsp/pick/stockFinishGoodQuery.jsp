<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
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
<jsp:useBean id="stockFinishGoodQueryForm" class="com.isecinc.pens.web.pick.StockFinishGoodQueryForm" scope="session" />

<%
List<References> billTypeList =null;
References ref = null;

if(session.getAttribute("statusList") == null){
	billTypeList = new ArrayList();
	billTypeList.addAll(PickConstants.getRequestStatusW2ListInPageQueryFinishGood());
	session.setAttribute("statusList",billTypeList);
}

 if(session.getAttribute("summaryTypeList") == null){
	billTypeList = new ArrayList();
	ref = new References("SummaryByPensItem","Summary ตาม PensItem");
	billTypeList.add(ref);
	ref = new References("Detail","Detail");
	billTypeList.add(ref);
	session.setAttribute("summaryTypeList",billTypeList);
} 
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/pick_stockPickQuery.css" type="text/css" />
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
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	// new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
}
function clearForm(path){
	var form = document.stockFinishGoodQueryForm;
	form.action = path + "/jsp/stockFinishGoodQueryAction.do?do=clear";
	form.submit();
	return true;
}
function search(path){
	var form = document.stockFinishGoodQueryForm;
	form.action = path + "/jsp/stockFinishGoodQueryAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}
function exportExcel(path){
	var form = document.stockFinishGoodQueryForm;
	form.action = path + "/jsp/stockFinishGoodQueryAction.do?do=exportExcel";
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
				<jsp:param name="function" value="stockFinishGoodQuery"/>
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
						<html:form action="/jsp/stockFinishGoodQueryAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Warehouse</td>
									<td colspan="3">	
										<html:radio property="bean.wareHouse" value="W2" onclick="swithWareHouse()">W2-<%=PickConstants.getWareHouseDesc("W2") %></html:radio>
										<html:radio property="bean.wareHouse" value="W3" onclick="swithWareHouse()">W3-<%=PickConstants.getWareHouseDesc("W3") %></html:radio>
										<html:radio property="bean.wareHouse" value="W4" onclick="swithWareHouse()">W4-<%=PickConstants.getWareHouseDesc("W4") %></html:radio>
										<html:radio property="bean.wareHouse" value="W5" onclick="swithWareHouse()">W5-<%=PickConstants.getWareHouseDesc("W5") %></html:radio>
									</td>
								</tr>
						       <tr>
                                    <td> PensItem From</td>
									<td>					
										 <html:text property="bean.pensItemFrom" styleId="pensItemFrom" size="20"/>
									</td>
									<td> 
									    PensItem To       
									</td>
									<td>					
										<html:text property="bean.pensItemTo" styleId="pensItemTo" size="20"/>
									</td>
								</tr>
								 <tr>
                                    <td> Group Code From</td>
									<td>					
										 <html:text property="bean.groupCodeFrom" styleId="GroupCodeFrom" size="20"/>
									</td>
									<td> 
									    Group Code To     
									</td>
									<td>					
										 <html:text property="bean.groupCodeTo" styleId="GroupCodeTo" size="20"/>									
									</td>
								</tr>
								<tr>
                                    <td> สถานะ </td>
									<td colspan="3">					
										 <html:select property="bean.status">
											<html:options collection="statusList" property="key" labelProperty="name"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> แสดงตาม  </td>
									<td colspan="3">					
										 <html:select property="bean.summaryType">
											<html:options collection="summaryTypeList" property="key" labelProperty="name"/>
									    </html:select>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
										  <input type="button" value=" Export To Excel " class="newPosBtnLong">
										</a>	
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${stockFinishGoodQueryForm.results != null}">
                  	
                  	 <c:if test="${stockFinishGoodQueryForm.bean.summaryType =='Detail'}">
							<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
							       <tr>
							            <th >Wacoal Mat.</th>
										<th >Group Code.</th>
										<th >Pens Item</th>
										<th >Barcode</th>
										<th >จำนวน</th>		
										<th >Status</th>	
								   </tr>
								  <c:forEach var="results" items="${stockFinishGoodQueryForm.results}" varStatus="rows">
									<c:choose>
										<c:when test="${rows.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									
										<tr class="<c:out value='${tabclass}'/>">
										    <td class="search_materialMaster">
											   ${results.materialMaster}
											</td>
											<td class="search_groupCode">${results.groupCode}</td>
											<td class="search_pensItem">
												${results.pensItem}
											</td>
											
											<td class="search_barcode">
											    ${results.barcode}
											</td>
											<td class="search_status">
											  ${results.onhandQty}
											</td>
											<td class="search_status">
											  ${results.statusDesc}
											</td>
											
										</tr>
								</c:forEach>
								 <tr>
									<td  colspan="4" align="right"><b>Total QTY</b></td>	
									<td class="search3_groupCode"><b>${stockFinishGoodQueryForm.bean.totalQty}</b></td>		
									<td></td>			
								 </tr>
						</table>
					</c:if>

					<c:if test="${stockFinishGoodQueryForm.bean.summaryType =='SummaryByPensItem'}">
							<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch3">
							       <tr>
										<th >Pens Item</th>
										<th >Group Code</th>	
										<th >จำนวน</th>
										<th >Status</th>					
								   </tr>
								  <c:forEach var="results" items="${stockFinishGoodQueryForm.results}" varStatus="rows">
									<c:choose>
										<c:when test="${rows.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									
										<tr class="<c:out value='${tabclass}'/>">
											<td class="search3_pensItem">${results.pensItem}</td>
											<td class="search3_groupCode">
											  ${results.groupCode}
											</td>
											<td class="search3_qty">
											  ${results.onhandQty}
											</td>
											<td class="search_status">
											  ${results.statusDesc}
											</td>
										</tr>
								</c:forEach>
								 <tr>
										<td ></td>
										<td class="search3_pensItem"><b>Total QTY</b></td>	
										<td class="search3_groupCode"><b>${stockFinishGoodQueryForm.bean.totalQty}</b></td>					
								   </tr>
						</table>
					</c:if>
				</c:if>
					<!-- ************************Result ***************************************************-->
					
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
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