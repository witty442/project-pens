<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.Constants"%>

<jsp:useBean id="userForm" class="com.isecinc.pens.web.user.UserForm" scope="session" />
<jsp:useBean id="manualUpdateAddressForm" class="com.isecinc.pens.web.manual.ManualUpdateAddressForm" scope="request" />
<%
String role = ((User)session.getAttribute("user")).getType();
User user = (User)session.getAttribute("user");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablednd_0_5.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript" language="javascript">
function loadMe(){
	addTextRemove();
}

function addTextRemove(){
  //.\n,เขต ,xxx  
  //alert("[xx"+document.getElementsByName("textRemove")[0].value+"xx]");
  
  if(document.getElementsByName("textRemove")[0].value ==''){
    document.getElementsByName("textRemove")[0].value = "[อ.] ,[เขต] ,[จ.]";
  }
}
function addText(){
	  
	document.getElementsByName("textRemove")[0].value = document.getElementsByName("textRemove")[0].value +",["+document.getElementsByName("textStr")[0].value+"]";
}
function search(path, type) {
	document.manualUpdateAddressForm.action = path + "/jsp/manualUpdateAddressAction.do?do=search&rf=N&";
	document.manualUpdateAddressForm.submit();
	return true;
}

function clearForm(path, type) {
	document.manualUpdateAddressForm.action = path + "/jsp/manualUpdateAddressAction.do?do=prepare&type="+type;
	document.manualUpdateAddressForm.submit();
	return true;
}

function save(path,type){
	document.manualUpdateAddressForm.action = path + "/jsp/manualUpdateAddressAction.do?do=save";
	document.manualUpdateAddressForm.submit();
	return true;
}

</script>

<!-- Move for new index. -->
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe(); MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="ManualUpdateAddress"/>
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
						<html:form action="/jsp/manualUpdateAddressAction">
			            <jsp:include page="../error.jsp"/>	
			            
			                <!-- ******************Criteria ****************-->
			                <table  border="0" align="center" cellpadding="3" cellspacing="0" class="body">
						           <tr>
									  <td align="right" valign="bottom">
									     <input type="text" name="textStr" size="4" /> 
									      <input type="button" value=" เพิ่ม " style="width:50px;" 
										     onClick="javascript:addText('${pageContext.request.contextPath}')" />
									  </td>
									  <td width="60%" align="left" nowrap valign="bottom"> 
									     <input type="text" name="textRemove" size="30" value="<%=com.isecinc.pens.inf.helper.Utils.removeStringEnter(request.getParameter("textRemove")) %>"/>
									  </td>
								   </tr>
								    <tr>
									  <td align="left" valign="bottom" colspan="2">
									    Text ที่ต้องการ Replace เพื่อใช้เอาชื่อไปค้นหา ID ใน Master District,Province เช่น  
									  </td>
								   </tr>
								   <tr>
									  <td align="left" valign="bottom" colspan="2">
									   1) "เขตลำลูกกา" --> "ลำลูกกา" 
									  </td>
								   </tr>
								    <tr>
									  <td align="left" valign="bottom" colspan="2">
									   2) "อ.บ้านเพ" -->"บ้านเพ" 
									  </td>
								   </tr>
						    </table>
						    <!-- ******************Criteria ****************-->
							<br>
							<!-- BUTTON -->
							<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
								<tr>
									<td align="right" nowrap>
									     <input type="button" value=" Search " class="newPosBtn" style="width: 150px;" 
										     onClick="javascript:search('${pageContext.request.contextPath}','admin')" />
										 <input type="button" value=" Update Address " class="newPosBtn" style="width: 150px;" 
										     onClick="javascript:save('${pageContext.request.contextPath}','admin')" />
									</td>
									<td width="60%">&nbsp;</td>
								</tr>
							</table>
							
							<!-- RESULT -->
					         <c:if test="${manualUpdateAddressForm.results != null}">
								<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
								&nbsp;<span class="searchResult">${manualUpdateAddressForm.size}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
								
									<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" width="40%">
										<tr>
							                <th> ลำดับที่.</th>
											<th> Address ID</th>
											<th> Province ID</th>
											<th> ชื่อจังหวัด </th>
											<th> ชื่อจังหวัดที่ใกล้เคียง (ข้อมูลจาก Master)</th>
											<th> District ID</th>
											<th> ชื่ออำเภอ </th>
											<th> ชื่ออำเภอที่ใกล้เคียง (ข้อมูลจาก Master)</th>
										</tr>
										
										<c:forEach var="results" items="${manualUpdateAddressForm.results}" varStatus="rows">
										<c:choose>
											<c:when test="${rows.index %2 == 0}">
												<c:set var="tabclass" value="lineO"/>
											</c:when>
											<c:otherwise>
												<c:set var="tabclass" value="lineE"/>
											</c:otherwise>
										</c:choose>
										
										<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}">
								                <td width="3%"> <c:out value='${rows.index+1}'/></td>
												<td width="3%"> ${results.addressId} <input type="hidden" name="addressIds" value="${results.addressId}"/></td>
												<td width="7%" class="${results.provinceStyle}"> ${results.provinceId}</td>
												<td width="7%" class="${results.provinceStyle}"> ${results.provinceName}</td>
												<td width="7%"> ${results.masterProvinceName}</td>
												<td width="7%" class="${results.districtStyle}"> ${results.districtId}</td>
												<td width="7%" class="${results.districtStyle}"> ${results.districtName}</td>
												<td width="7%"> ${results.masterDistrictName}</td>
												
										</tr>
										</c:forEach>
									</table>
					           </c:if>
			                <!-- RESULT -->

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