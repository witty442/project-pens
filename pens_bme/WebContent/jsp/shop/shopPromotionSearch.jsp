<%@page import="com.isecinc.pens.web.shop.ShopBean"%>
<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
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
<%
String pageName = Utils.isNull(request.getParameter("pageName"));
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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
}
function clearForm(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=prepare&pageAction=new";
	form.submit();
	return true;
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
/* function exportToExcel(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=exportToExcel";
	form.submit();
	return true;
} */
function back(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}
function openEdit(path,promoId,mode){
	var form = document.shopForm;
	var param  ="&promoId="+promoId;
	    param +="&mode="+mode;
	form.action = path + "/jsp/shopAction.do?do=searchDetail"+param;
	form.submit();
	return true;
}
function openAdd(path){
	var form = document.shopForm;
	var param ="&mode=add";
	form.action = path + "/jsp/shopAction.do?do=searchDetail"+param;
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
				<jsp:param name="function" value="ShopPromotion"/>
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
						<jsp:include page="../error.jsp"/>
						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td> ชื่อ Promotion หลัก</td>
									<td >
						               <html:text property="bean.promoName" styleId="promoName" size="40" styleClass="\" autoComplete=\"off"/>
									</td>
								</tr>	
								<tr>
                                    <td align="right"> เริ่มต้น</td>
									<td>
									s
						               <html:text property="bean.startDate" styleId="startDate" size="15" readonly="false" styleClass="\" autoComplete=\"off"/>
						                                          สิ้นสุด
						                <html:text property="bean.endDate" styleId="endDate" size="15" readonly="false" styleClass="\" autoComplete=\"off"/>
									</td>
								</tr>	
						   </table>
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:openAdd('${pageContext.request.contextPath}')">
										  <input type="button" value="  เพิ่มรายการใหม่    " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>	
										<%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
										  <input type="button" value="   Export   " class="newPosBtnLong">
										</a>	 --%>				
									</td>
								</tr>
							</table>
					  </div>
					  
				<c:if test="${shopForm.results != null}">
                   <% 
					  /*  int totalPage = shopForm.getTotalPage();
					   int totalRecord = shopForm.getTotalRecord();
					   int currPage =  shopForm.getCurrPage();
					   int startRec = shopForm.getStartRec();
					   int endRec = shopForm.getEndRec(); */
					%>
					 <jsp:include page="../pageing.jsp">
				       <jsp:param name="totalPage" value="<%=shopForm.getTotalPage() %>"/>
				       <jsp:param name="totalRecord" value="<%=shopForm.getTotalRecord() %>"/>
				       <jsp:param name="currPage" value="<%=shopForm.getCurrPage() %>"/>
				       <jsp:param name="startRec" value="<%=shopForm.getStartRec() %>"/>
				       <jsp:param name="endRec" value="<%=shopForm.getEndRec() %>"/>
			         </jsp:include>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
					        <tr>
								<th >ชื่อ Promotion หลัก</th>
								<th >Start Date</th>
								<th >End Date</th>
								<th >แก้ไข </th>
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
										<td class="td_text" width="10%"><%=mc.getPromoName()%></td>
										<td class="td_text_center" width="5%"><%=mc.getStartDate()%></td>
									    <td class="td_text_center" width="5%"><%=mc.getEndDate()%></td>
										<td class="td_text_center" width="10%">
										<%if(mc.isCanEdit()){ %>
											 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getPromoId()%>','edit')">
											             แก้ไข
											 </a>
										<%}else{ %>
										    <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getPromoId()%>','view')">
											     VIEW
											 </a>
										<%} %>
										</td>
									</tr>
							<%}
							}catch(Exception e){
								e.printStackTrace();
							}
							%>
					</table>
				</c:if>
				
		          <!-- ************************Result ***************************************************-->	
					
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
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