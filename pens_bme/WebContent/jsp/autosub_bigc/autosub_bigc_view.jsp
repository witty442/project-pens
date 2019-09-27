<%@page import="com.isecinc.pens.web.autosub.AutoSubBigCBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="autoSubBigCForm" class="com.isecinc.pens.web.autosub.AutoSubBigCForm" scope="session" /> 
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

<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
}
function backAction(path){
	var form = document.autoSubBigCForm;
	form.action = path + "/jsp/autoSubBigCAction.do?do=search2&action=back";
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
				<jsp:param name="function" value="AutoSubTransBigC"/>
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
					<html:form action="/jsp/autoSubBigCAction">
					<jsp:include page="../error.jsp"/>

					<div align="center">
					    <table align="center" border="0" cellpadding="3" cellspacing="0" >
							<tr>
                                   <td align="right">  RTN NO	</td>
								<td>	
								     <html:text property="bean.rtnNo" styleId="rtnNo" size="15"   styleClass="disableText" readonly="true"/>
								</td>
								<td>		
								   รหัสร้านค้า<font color="red"></font>
                                      <html:text property="bean.storeCode" styleId="storeCode" size="15"   styleClass="disableText" readonly="true"/>   
								  <html:text property="bean.storeName" styleId="storeName" size="30"  styleClass="disableText" readonly="true"/>
								</td>
							</tr>
					   </table>		
					  </div>

            <c:if test="${autoSubBigCForm.resultsSearch != null}">
                   
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th >RMA Order</th><!-- 0 -->
						            <th >CN NO</th><!-- 1 -->
									<th >CN Date</th><!-- 3 -->
									<th >Ref.Inv</th><!-- 3 -->
									<th >Seq</th><!-- 4 -->
									<th >Item</th><!-- 5 -->
									<th >Item Desc</th><!-- 6 -->
									<th >Qty</th><!-- 7 -->
									<th >List Price</th><!-- 7 -->
									<th >Total Amount</th><!-- 7 -->
							   </tr>
							<% 
							String tabclass ="lineE";
							List<AutoSubBigCBean> resultList = autoSubBigCForm.getResultsSearch();
							for(int n=0;n<resultList.size();n++){
								AutoSubBigCBean mc = (AutoSubBigCBean)resultList.get(n);
								if(n%2==0){ 
								  tabclass="lineO";
								}
								%>
								<tr class="<%=tabclass%>"> 
									<td class="td_text_center" width="6%"><%=mc.getRmaOrder()%></td><!-- 1 -->
									<td class="td_text_center" width="6%"><%=mc.getCnNo()%></td><!-- 3 -->
								    <td class="td_text_center" width="6%"><%=mc.getCnDate()%></td><!-- 4 -->
								    <td class="td_text_center" width="6%"><%=mc.getRefInv()%></td><!-- 4 -->
								    <td class="td_text_center" width="6%"><%=mc.getSeq()%></td><!-- 5 -->
									<td class="td_text_center" width="6%"><%=mc.getPensItem()%></td><!-- 6 -->
									<td class="td_text" width="6%"><%=mc.getItemName()%></td><!-- 7 -->
									<td class="td_number" width="6%"><%=mc.getQty()%></td><!-- 8 -->
									<td class="td_number" width="6%"><%=mc.getUnitPrice()%></td><!-- 8 -->
									<td class="td_number" width="6%"><%=mc.getAmount()%></td><!-- 8 -->
								</tr>
							<%} %> 
					</table>
				</c:if>
		   <!-- ************************Result ***************************************************-->
	              <table id="tblProductBtn" align="center" border="0" cellpadding="3" cellspacing="2"  width="65%">
                        <tr><td align="center">
                            <input type="button" name="delete" value="  ปิดหน้าจอ  " onclick="backAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                        </td></tr>
                   </table>
                   
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