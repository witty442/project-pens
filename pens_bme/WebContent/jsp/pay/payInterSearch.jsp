<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.PayBean"%>
<%@page import="java.util.Calendar"%>
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
<jsp:useBean id="payInterForm" class="com.isecinc.pens.web.pay.PayInterForm" scope="session" />
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
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('dateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('dateTo'));
}

function clearForm(path){
	var form = document.payInterForm;
	form.action = path + "/jsp/payInterAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.payInterForm;
	if( $('#mcArea').val()==""){
		alert("กรุณาระบุ เขตพื้นที่");
		return false;
	}
	/* if( $('#staffType').val()==""){
		alert("กรุณาระบุ ประเภท");
		return false;
	} */
	
	form.action = path + "/jsp/payInterAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.payInterForm;
	var path = document.getElementById("path").value ;
	form.action = path + "/jsp/payInterAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function newDoc(path){
	 var form = document.payInterForm;
	var param ="";
	form.action = path + "/jsp/payInterAction.do?do=prepare&mode=add"+param;
	form.submit();
	return true; 
}

function openEdit(path,docNo){
	 var form = document.payInterForm;
	var param ="&docNo="+docNo;
	form.action = path + "/jsp/payInterAction.do?do=prepare&mode=edit"+param;
	form.submit();
	return true; 
}

function openCopy(path,docNo){
	 var form = document.payInterForm;
	var param ="&docNo="+docNo;
	form.action = path + "/jsp/payInterAction.do?do=prepare&mode=copy"+param;
	form.submit();
	return true; 
}

function printReport(path,docNo){
   var url = path+"/jsp/popup/printPayInterPopup.jsp?report_name=PayInReport&docNo="+docNo;
   //, "Print2", "width=800,height=400,location=No,resizable=No");
	PopupCenter(url,'Printer',800,350);
}

</script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="payInter"/>
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
						<html:form action="/jsp/payInterAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td> จากวันที่<font color="red"></font></td>
									<td>		
										 <html:text property="bean.dateFrom" styleClass="" styleId="dateFrom"></html:text>
										 ถึงวันที่
										 <html:text property="bean.dateTo" styleClass="" styleId="dateTo"></html:text>
									</td>
								</tr>
								<tr>
                                    <td> ผู้บันทึก<font color="red"></font></td>
									<td>		
										<html:text property="bean.createUser" styleClass="disableText" readonly="true" size="50"></html:text>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:newDoc('${pageContext.request.contextPath}')">
										  <input type="button" value="    เพิ่มรายการใหม่      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${payInterForm.resultsSearch != null}">
                  	<% 
					   int totalPage = payInterForm.getTotalPage();
					   int totalRecord = payInterForm.getTotalRecord();
					   int currPage =  payInterForm.getCurrPage();
					   int startRec = payInterForm.getStartRec();
					   int endRec = payInterForm.getEndRec();
					   int pageSize = payInterForm.getPageSize();
					   int no = Utils.calcStartNoInPage(currPage, pageSize);
					%>
					<%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
					
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearch">
						       <tr>
						            <th >เลขที่เอกสาร</th>
									<th >วันที่ทำรายการ</th>
									<th >จ่าย</th>
									<th >ฝ่าย</th>
									<th >แผนก</th>
									<th >แก้ไข</th>
									<th >พิมพ์</th>
									<th >Copy</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<PayBean> resultList = payInterForm.getResultsSearch();
							
							for(int n=0;n<resultList.size();n++){
								PayBean mc = (PayBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="10%"><%=mc.getDocNo() %></td>
										<td class="td_text_center" width="10%"><%=mc.getDocDate()%></td>
										<td class="td_text" width="25%"><%=mc.getPayToName()%></td>
									    <td class="td_text" width="15%"><%=mc.getDeptId() %>:<%=mc.getDeptName()%></td>
									    <td class="td_text" width="15%"><%=mc.getSectionId() %>:<%=mc.getSectionName()%></td>
				
										<td class="td_text_center" width="10%">
											 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getDocNo()%>')">
											             แก้ไข
											 </a>
										</td>
										<td class="td_text_center" width="10%">
											 <a href="javascript:printReport('${pageContext.request.contextPath}','<%=mc.getDocNo()%>')">
											             พิมพ์
											 </a>
										</td>
										<td class="td_text_center" width="10%">
											 <a href="javascript:openCopy('${pageContext.request.contextPath}','<%=mc.getDocNo()%>')">
											     Copy
											 </a>
										</td>
									</tr>
							<%} %>
							 
					</table>
				</c:if>
				
		<!-- ************************Result ***************************************************-->	
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