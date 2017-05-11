<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.scheduler.utils.CronExpressionUtil"%>
<%@page import="com.isecinc.pens.scheduler.manager.SchedulerConstant"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

 <%
   String programId = Utils.isNull(request.getParameter("programId"));
   String paramQtr = Utils.isNull(request.getParameter("PARAM_REGEN"));
   
   if( !"".equals(paramQtr)){
    String[] paramArr = paramQtr.split("\\,");
    
     %>
	        
<script type="text/javascript">

function submitRegen(path){
	//alert('checkDate1');
	var param = "&programId=<%=programId%>";
	<%
	for(int i=0;i<paramArr.length;i++){
	   String[] param = paramArr[i].split("\\|");
	   String paramName = param[0];
	 %>
	     param +="&<%=paramName%>="+ document.getElementsByName("<%=paramName%>")[0].value;
	 <%}%>
	 
     alert(param);
  // document.scheduleForm.action = path + "/jsp/schedule.do?do=deleteBatch";
  // document.scheduleForm.submit();   
     window.location.href = path + "/jsp/schedule.do?do=regen"+param;
	 
}
</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">

       <!-- Param Regen -->
      
	                  
		   <table width="100%" border="0" cellspacing="1" cellpadding="3">
			<tr>
				<td  colspan="3" align="center"><b>Parameter Regen <%=programId %></b></td>
			</tr>
			<%if(paramArr.length >0){ 
			 for(int i=0;i<paramArr.length;i++){
			   String[] param = paramArr[i].split("\\|");
			   String paramName = param[0];
			   String paramType = param[1];
			%>
				<tr align="center">
					<td align="right"><%=paramName %><font color="red">*</font></td>
					<td align="left"><input type="text" name="<%=paramName%>" id="<%=paramName%>" /></td>
				</tr>
				
		<%        }//for
			}//if
		%>
				 
		<script>
		<%
		for(int i=0;i<paramArr.length;i++){
		   String[] param = paramArr[i].split("\\|");
		   String paramName = param[0];
		   String paramType = param[1];
		   if("DATE".equalsIgnoreCase(paramType)){
			out.println("new Epoch('epoch_popup', 'th', document.getElementById('"+paramName+"'))");
			}//if
		  }//for
		%>
		</script>
	
	</table>
    <hr>
    <div align="center">
    	<html:button property="button" value="Submit Regen Job" styleClass="newPosBtn" onclick="submitRegen('${pageContext.request.contextPath}');"/>
	</div>
	
<%
	}else{
	
	String regenSuccess =  Utils.isNull(request.getAttribute("regenSuccess"));
	System.out.println("regenSuccess:"+regenSuccess);
	if( "".equals(regenSuccess)){
 %>
	  <div align="center"> ‰¡Ëæ∫ Parameter Regen</div>
 <%}else{ %>
     <div align="center">  Regenerat Success </div>
     <script>
       window.opener.submitSearch('${pageContext.request.contextPath}');
       setTimeout("window.close()",2000);
     
     </script>
     
<% } }%>

</body>
</html>