<%@page import="com.isecinc.pens.web.stockinv.StockInvBean"%>
<%@page import="util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockInvForm" class="com.isecinc.pens.web.stockinv.StockInvForm" scope="session" />
<%
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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
}
function clearForm(path){
	var form = document.stockInvForm;
	form.action = path + "/jsp/stockInvAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.stockInvForm;
	/* if( $('#transactionDate').val()==""){
		alert("กรุณากรอก ข้อมูลค้นหาอย่างน้อย 1 รายการ");
		return false;
	} */
	
	form.action = path + "/jsp/stockInvAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.stockInvForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/stockInvAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}

function openEdit(path,headerId,mode,transType){
	var form = document.stockInvForm;
	//alert(mode);
	form.action = path + "/jsp/stockInvAction.do?do=prepareInitStock&mode="+mode+"&headerId="+headerId+"&transType="+transType;
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
				<jsp:param name="function" value="StockInv"/>
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
						<html:form action="/jsp/stockInvAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Transaction Date</td>
									<td>					
										 <html:text property="bean.transactionDate" styleId="transactionDate" size="20"/>
									</td>
									<td> Transaction Type:					
										<html:select property="bean.transType">
										   <html:option value=""></html:option>
										   <html:option value="IN">เข้าคลัง</html:option>
										   <html:option value="OUT">ออกจากคลัง</html:option>
										</html:select>
									</td>
									<td></td>
								</tr>
									
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:openEdit('${pageContext.request.contextPath}','','add','IN')">
										  <input type="button" value="เพิ่มรายการใหม่(เข้าคลัง)" class="newPosBtnLong">
										</a>
											<a href="javascript:openEdit('${pageContext.request.contextPath}','','add','OUT')">
										  <input type="button" value="เพิ่มรายการใหม่(ออกจากคลัง)" class="newPosBtnLong">
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${stockInvForm.resultsSearch != null}">
                  	<% 
					   int totalPage = stockInvForm.getTotalPage();
					   int totalRecord = stockInvForm.getTotalRecord();
					   int currPage =  stockInvForm.getCurrPage();
					   int startRec = stockInvForm.getStartRec();
					   int endRec = stockInvForm.getEndRec();
					   int no = stockInvForm.getStartRec();
					%>
					<%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >No</th>
									<th >Transaction ID</th>
									<th >Transaction Date</th>
									<th >Transaction Type</th>
									<th >รหัสสินค้า</th>
									<th >ชื่อสินค้า</th>
									<th >หน่วยนับ</th>
									<!-- <th >ราคา</th> -->
									<th >จำนวน</th>
									<th >Status</th>
									<th >Action</th>						
							   </tr>
								<% 
								String tabclass ="";
								List<StockInvBean> resultList = stockInvForm.getResultsSearch();
								for(int n=0;n<resultList.size();n++){
								
									StockInvBean mc = (StockInvBean)resultList.get(n);
									if(n%2==0){ 
									   tabclass="lineO";
									}else{
									   tabclass ="lineE";
									}
								%>
								
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="3%"><%=no %></td>
										<td class="td_text_center" width="5%"><%=mc.getHeaderId() %></td>
										<td class="td_text_center" width="5%"><%=mc.getTransactionDate() %></td>
										<td class="td_text_center" width="5%"><%=mc.getTransTypeDesc() %></td>
										<td class="td_text_center" width="5%"><%=mc.getProductCode() %></td>
										<td class="td_text" width="13%"><%=mc.getProductName()%></td>
										<td class="td_text_right" width="5%">
										    <%=mc.getUom() %>
										</td>
										<%-- <td class="td_text_right" width="5%">
										    <%=mc.getPrice()%>
										</td> --%>
										<td class="td_text_right" width="5%">
										    <%=mc.getQty1() %>/ <%=mc.getQty2() %>
										</td>
										<td class="td_text_center" width="5%">
										   <%=mc.getStatus() %>
										</td>
										<td class="td_text_center" width="5%">
										 <% if(mc.isCanEdit()==false){ %>
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=mc.getHeaderId() %>','view','')">
											         <font size="2"><b>ดู </b></font>
											  </a>
										 <%}else if(mc.isCanEdit()==true){ %>
										      <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=mc.getHeaderId() %>','edit','')">
											        <font size="2"><b> แก้ไข</b></font>
											   </a>
										 <%} %>
										</td>
									</tr>
								<% 	no++;}//for %>
					</table>
					
				</c:if>
					<!-- ************************Result ***************************************************-->
					
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
					<input type="hidden" name="path" id="path" value ="${pageContext.request.contextPath}"/>
					
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