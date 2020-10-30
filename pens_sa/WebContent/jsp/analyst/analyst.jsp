<%@page import="com.isecinc.pens.web.report.analyst.AReportForm"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.*"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "aReportForm");
User user = (User)session.getAttribute("user"); 

String reportName = Utils.isNull(request.getParameter("reportName"));
if("".equals(reportName)){
	AReportForm aReportForm= (AReportForm)session.getAttribute("aReportForm");
	reportName = aReportForm.getReportName();
}

String typeSearch = Utils.isNull(request.getAttribute("DATA"));
java.util.List yearList = null;
if(session.getAttribute("yearList") != null) 
	yearList = (java.util.List)session.getAttribute("yearList");

java.util.List yearListASC = null;
if(session.getAttribute("yearListASC") != null) 
	yearListASC = (java.util.List)session.getAttribute("yearListASC");

String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}
System.out.println("screenWidth:"+screenWidth);

%>
<html>
<head>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="cache-control" content="no-store" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" /> 
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
select#summaryType{width:150px;}
fieldset#condition-frame{height:186px}
fieldset#display-frame{height:186px}

fieldset {
    font-family: sans-serif;
    border: 1px solid #1F497D;
  /*   background: #ddd; */
    border-radius: 5px;
    padding: 10px;
}
fieldset legend {
    background: #3F5C93; /* #1F497D; */
    color: #fff;
    padding: 5px 10px ;
    font-size: 14px;
    border-radius: 2px;
    box-shadow: 0 0 0 1px #ddd;
    margin-left: 20px;
}
.txt_style {
	font-family: "Lucida Grande", Tahoma, Arial, Verdana, sans-serif;
	font-size: 14px;
	font-weight: normal;
	text-decoration: none;
}

input[type=checkbox]
{
  /* Double-sized Checkboxes */
  -ms-transform: scale(2); /* IE */
  -moz-transform: scale(2); /* FF */
  -webkit-transform: scale(2); /* Safari and Chrome */
  -o-transform: scale(2); /* Opera */
  padding: 2px;
}
.text{
	mso-number-format:"\@";
}
 #scroll {
<%if(!"0".equals(screenWidth)){%>
    width:<%=screenWidth%>px; 
    background:#A3CBE0;
	border:1px solid #000;
	overflow:auto;
	white-space:nowrap;
	box-shadow:0 0 25px #000;
<%}%>
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablednd_0_5.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/date.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablesorter.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/analyst.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" language="javascript">

function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('day'));
	new Epoch('epoch_popup','th',document.getElementById('dayTo'));
	chkSearch();
	chkYear();
}

function chkSearch(){
   var typeSearch = document.getElementsByName("salesBean.typeSearch")[0];
   //alert(typeSearch.value);
	disabledObj(document.getElementsByName("salesBean.day")[0] ,false);
	disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,false);
	
	var quarterList = document.getElementsByName("salesBean.chkQuarter");
	for(i=0;i<quarterList.length;i++){
	   disabledObj(document.getElementsByName("salesBean.chkQuarter")[i],false);
	}
	
	var monthList = document.getElementsByName("salesBean.chkMonth");
	for(i=0;i<monthList.length;i++){
       disabledObj(document.getElementsByName("salesBean.chkMonth")[i],false);
    }
	for(i=0;i < <%=yearList!=null?yearList.size():0 %>; i++){
	   disabledObj(document.getElementsByName("salesBean.chkYear")[i],false);
	}
	   
   if(typeSearch.value == 'DAY'){
	   for(i=0;i<monthList.length;i++){
	      disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
	   }
	   for(i=0;i< <%=yearList!=null?yearList.size():0%>; i++){
		  disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
	   }
	   for(i=0;i<quarterList.length;i++){
		   disabledObj(document.getElementsByName("salesBean.chkQuarter")[i],true);
		}
    }else  if(typeSearch.value == 'MONTH'){
       disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
       disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,true);
       for(i=0;i < <%=yearList!=null?yearList.size():0%>; i++){
    	   disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
    	}
       for(i=0;i<quarterList.length;i++){
    	   disabledObj(document.getElementsByName("salesBean.chkQuarter")[i],true);
       }
    }else  if(typeSearch.value == 'QUARTER'){
   	   disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
       disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,true);
   	   for(i=0;i<monthList.length;i++){
	      disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
	   }
	   	for(i=0;i < <%=yearList!=null?yearList.size():0%>; i++){
	 	   disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
	 	}
    }else  if(typeSearch.value == 'YEAR'){
    	disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
    	disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,true);
    	for(i=0;i<monthList.length;i++){
 	       disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
 	    }
    	for(i=0;i<quarterList.length;i++){
    	   disabledObj(document.getElementsByName("salesBean.chkQuarter")[i],true);
        }
    }
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
				<jsp:param name="function" value="<%=reportName %>"/>
				<jsp:param name="code" value=""/>
			</jsp:include>
			<!-- For Export To Excel display -->
			<iframe id="txtArea1" style="display:none"></iframe>
			
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
						<html:form action="/jsp/aReportAction">
						<html:hidden property="salesBean.returnString"/>
						<input type="hidden" name="roleTabText" id="roleTabText"/>
			            <jsp:include page="../error.jsp"/>	
			            
                            <!-- Criteria -->
                             <jsp:include page="../analyst/sub/criteria.jsp"/>
					
                            <!-- Criteria -->
                            
							<!-- BUTTON -->
							<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
								<tr>
									<td align="right">
									     <input type="button" value=" Search " class="newPosBtnLong" style="width: 120px;"
										     onClick="javascript:search('${pageContext.request.contextPath}','admin')" />
										
										 <button type="button" id="btnExport" onclick="fnExcelReport();" class="newPosBtnLong" style="width:120px;" > Export</button>  
										 <input type="button" value=" Clear " class="newPosBtnLong" style="width: 120px;" 
										     onClick="javascript:clearForm('${pageContext.request.contextPath}','admin')" />	 
										  
										  <%if(user.getUserName().equalsIgnoreCase("admin")) {%>
										    <input type="button" value=" GetSQL " class="newPosBtnLong" style="width: 120px;" 
										      onClick="javascript:getSQL('${pageContext.request.contextPath}','admin')" />	 
										  <%} %>
										  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										    <input type="button" value=" Export(ข้อมูลจำนวนมาก) " class="newPosBtnLong" style="width: 220px;" 
										     onClick="javascript:exportData('${pageContext.request.contextPath}','admin')" />
										     
										<%--  <input type="button" value=" GET SQL " class="newPosBtn" style="width: 120px;" 
										     onClick="javascript:getSQL('${pageContext.request.contextPath}','admin')" />
										  --%>    
									
									</td>
								</tr>
							</table>
							<!-- BUTTON -->
							
							<!-- RESULT -->
							<div id ="scroll">
								<%out.print(Utils.isNull(session.getAttribute("aReportForm_RESULT"))); %>
							</div>
						    <!-- RESULT -->
						    
						    <!-- Hidden field -->
						    <input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
						    <input type="hidden" name="reportName" id="reportName" value ="<%=reportName %>"/>
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