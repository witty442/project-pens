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
<%@page import="com.isecinc.pens.report.salesanalyst.SAInitial"%>

<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "salesAnalystReportForm");
User user = (User)session.getAttribute("user"); 

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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/salesAnalyst.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

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
				<jsp:param name="function" value="SalesAnalysis"/>
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
						<html:form action="/jsp/salesAnalystReportAction">
						<html:hidden property="salesBean.returnString"/>
						<input type="hidden" name="roleTabText" id="roleTabText"/>
			            <jsp:include page="../error.jsp"/>	
                            <!-- Criteria -->
                        
                            <table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
			                  <tr><td>
                               <%out.println(Utils.isNull(session.getAttribute("USER_ROLE_INFO"))); %>
                              </td></tr>
                            </table>
                            <table width="80%" border="0" align="center" cellpadding="1" cellspacing="1">
			                  <tr><td>
		                            <fieldset>
		                             <legend><b>รูปแบบการค้นหาที่ใช้ประจำ</b></legend>
					                <table width="80%" border="0" align="left" cellpadding="1" cellspacing="1">
					                  <tr>
					                      <td width="15%" align="left" nowrap>
		                                       <html:select property="salesBean.profileId" styleId="profileId" onchange="changeProfile('${pageContext.request.contextPath}','')" styleClass="txt_style" >
											         <html:options collection="profileList" property="key" labelProperty="name"/>
									            </html:select>
		                                  </td>
		                                  <td width="65%" align="left" nowrap>     
		                                    <input type="button" value="บันทึกรูปแบบการค้นหา" class="btnSmallLong" style="width: 185px;" 
											onClick="javascript:saveProfile('${pageContext.request.contextPath}','admin')" /> 
										 	<%if(user.getUserName().equalsIgnoreCase("pornsawas")
										 		|| user.getUserName().equalsIgnoreCase("sansern")) {%>
											 	&nbsp;&nbsp;
												<input type="button" value="แก้ไขชื่อ/เพิ่ม" class="btnSmallLong" style="width: 120px;" 
												onClick="javascript:editProfile('${pageContext.request.contextPath}','admin')" /> 
										    <%} %>
										 </td>
		                              </tr>
		                            </table>
		                           </fieldset>
                                </td> </tr>
                            </table>
                            
                          <table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
						    <tr><td align="left">
						     <fieldset>
                                 <table width="80%" border="0" align="left" cellpadding="3" cellspacing="1">
										<tr><td align="left">
											<font size="2"><b><u>ข้อมูล ณ วันที่ &nbsp;:&nbsp;<%=(String)session.getAttribute("maxOrderedDate")%>
											&nbsp;&nbsp;เวลา&nbsp;:<%=(String)session.getAttribute("maxOrderedTime")%>
											</u></b></font>
										</td>
										</tr>
										<tr>
										  <td align="right">
										    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
											<tr class="txt_style" >
										  	<td width="13%" align="left" nowrap><b>รอบเวลา </b>
										  	      &nbsp;&nbsp;
										  	    <html:select property="salesBean.typeSearch" onchange="chkSearch()" styleClass="txt_style">
										         <html:options collection="typeSearchList" property="key" labelProperty="name"/>
									           </html:select>
									           </td>
										     <td width="6%" align="right"><b>วันที่</b></td>
										     <td width="27%" align="left" nowrap>
										      <html:text property="salesBean.day" readonly="true" styleId="day" size="12"> </html:text>
										      &nbsp;&nbsp;-&nbsp;&nbsp;
										      <html:text property="salesBean.dayTo" readonly="true" styleId="dayTo" size="12"></html:text>
	                                        </td> 
											<td width="2%" align="right"><b>ปี</b></td>
											<td width="6%" align="left">
											  <html:select property="salesBean.year" styleId="yearList" onchange="chkYear()" styleClass="txt_style">
										         <html:options collection="yearList" property="key" labelProperty="name"/>
								              </html:select>
											</td>
										    <td width="8%" align="right" nowrap><b>จัดกลุ่มตาม</b></td>
										    <td width="20%" align="left">
										        <html:select property="salesBean.groupBy" styleClass="txt_style">
											         <html:options collection="groupByList" property="key" labelProperty="name"/>
									            </html:select>
									            
									            <input type="hidden" name="maxOrderedDate" value=<%=(String)session.getAttribute("maxOrderedDate")%> id="maxOrderedDate"/>
	                                            <input type="hidden" name="maxOrderedTime" value=<%=(String)session.getAttribute("maxOrderedTime")%> id="maxOrderedTime"/>
	                                        </td>
										    </tr>
											</table>
										</td></tr>
									  </table>
									 </fieldset>
								   </td></tr>
									
									<tr><td colspan="8" align="left">
									     <fieldset>
									       <legend>เดือน</legend>
										    <table width="100%" border="0">
										    <c:forEach var="item" items="${yearList}" >
	                                         <tr id="${item.key}" class="txt_style" >
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}01</html:multibox>&nbsp;ม.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}02</html:multibox>&nbsp;ก.พ.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}03</html:multibox>&nbsp;มี.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}04</html:multibox>&nbsp;เม.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}05</html:multibox>&nbsp;พ.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}06</html:multibox>&nbsp;มิ.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}07</html:multibox>&nbsp;ก.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}08</html:multibox>&nbsp;ส.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}09</html:multibox>&nbsp;ก.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}10</html:multibox>&nbsp;ต.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}11</html:multibox>&nbsp;พ.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}12</html:multibox>&nbsp;ธ.ค.</td>
	                                         </tr>
	                                         </c:forEach>
                                        </table>
                                        </fieldset>
                                      </td>
							       </tr>
							       <tr>
									  <td colspan="8" align="left">
									       <fieldset>
									       <legend>ไตรมาส</legend>
										    <table width="100%" border="0">
										     <c:forEach var="item" items="${yearList}" >
		                                         <tr id="${item.key}_Q" class="txt_style" >
		                                          <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}1</html:multibox>&nbsp;ไตรมาส 1</td>
		                                          <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}2</html:multibox>&nbsp;ไตรมาส 2</td>
		                                          <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}3</html:multibox>&nbsp;ไตรมาส 3</td>
		                                          <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}4</html:multibox>&nbsp;ไตรมาส 4</td>
		                                         </tr>
		                                     </c:forEach>
                                           </table>
                                        </fieldset>
                                      </td>
							        </tr>
							       <tr>
									  <td colspan="8" align="left">
									    <fieldset>
									       <legend>ปี</legend>
										    <table width="80%" border="0">
	                                         <tr class="txt_style" >
	                                         <%if(yearListASC != null){ 
	                                              for(int i=0;i<yearListASC.size();i++){
	                                            	  com.isecinc.core.bean.References ref=(com.isecinc.core.bean.References)yearListASC.get(i);
	                                              
		                                         %>
		                                          <td width="5%"><html:multibox  property="salesBean.chkYear"><%=ref.getKey()%></html:multibox>&nbsp;<%=ref.getName() %></td>
	                                         <%}} %>
	                                         </tr>
                                        </table>
                                       </fieldset>
                                      </td>
							     </tr>
									
				          </table>
  
						<table width="80%" align="center" border="0" cellpadding="1" cellspacing="1" >
						 
                          <tr>
                            <td>
	                            <fieldset id="condition-frame">
	                            <legend>เงื่อนไขในการเลือกข้อมูล</legend>
		                            <table width="100%"  border="0" cellpadding="1" cellspacing="1" >
		                              <tr nowarp="nowarp" class="txt_style" >
		                                <td align="left" width="20%"><b>ขอบเขตข้อมูล</b> </td>
		                                <td align="center" width="5%">=</td>
		                                <td align="left" width="75%"><b>ข้อมูลเงื่อนไข</b></td>
		                              </tr>
		                              <tr nowarp="nowarp" class="txt_style" >
		                                <td align="left">
		                                    <html:select property="salesBean.condName1" onchange="clearText(1);" styleId="condName1">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								        </td>
		                                <td align="center" >
		                                   =
		                                </td>
		                                <td align="left">
											 <html:text property="salesBean.condCode1" styleId="condCode1" style="width:100px;" onkeyup="loadValue(event, 1,true);"
											  onchange="set_display_value1(event, 1);" styleClass="\" autoComplete=\"off"/>&nbsp;   
											 <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','1');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" width="20px" height="20px"/>
									        </a>
									        &nbsp;
									         <a href="javascript:clearText('1');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png" width="20px" height="20px"/>
									        </a>                                        
									         <html:text property="salesBean.condValueDisp1" readonly ="true" styleId="condValueDisp1" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue1" styleId="condValue1"></html:hidden>
								        </td>
		                              </tr>
		                             <tr nowarp="nowarp class="txt_style" >
		                                <td align="left">
		                                    <html:select property="salesBean.condName2" onchange="clearText(2);" styleId="condName2" >
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								       </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left">
											 <html:text property="salesBean.condCode2" styleId="condCode2" style="width:100px;" onkeyup="loadValue(event, 2,true);" 
											 onchange="set_display_value2(event, 2);" styleClass="\" autoComplete=\"off"/>&nbsp;
											 <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','2');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"  width="20px" height="20px"/>
									        </a>
									        &nbsp;
									         <a href="javascript:clearText('2');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"  width="20px" height="20px"/>
									        </a>
                                             <html:text property="salesBean.condValueDisp2" readonly="true" styleId="condValueDisp2" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue2"  styleId="condValue2"></html:hidden>
									        
								       </td>
		                              </tr>
		                             <tr nowarp="nowarp" class="txt_style" >
		                                <td align="left">
		                                    <html:select property="salesBean.condName3" onchange="clearText(3);" styleId="condName3">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								       </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left">
											 <html:text property="salesBean.condCode3" styleId="condCode3" style="width:100px;" onkeyup="loadValue(event, 3,true);" 
											 onchange="set_display_value3(event, 3);" styleClass="\" autoComplete=\"off"/>&nbsp;
											  <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','3');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"  width="20px" height="20px"/>
									        </a>
									        &nbsp;
									         <a href="javascript:clearText('3');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"  width="20px" height="20px"/>
									        </a>
                  							 <html:text property="salesBean.condValueDisp3" styleId="condValueDisp3" readonly="true" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue3"  styleId="condValue3"></html:hidden>
									       
								       </td>
		                              </tr>
		                              <tr nowarp="nowarp" class="txt_style" >
		                                <td align="left">
		                                    <html:select property="salesBean.condName4" onchange="clearText(4);" styleId="condName4">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								        </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left" >
                                             <html:text property="salesBean.condCode4" styleId="condCode4" style="width:100px;" onkeyup="loadValue(event, 4,true);"
                                              onchange="set_display_value4(event, 4);" styleClass="\" autoComplete=\"off"/>&nbsp;
											  <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','4');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"  width="20px" height="20px"/>
									        </a>
									        &nbsp;
									         <a href="javascript:clearText('4');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png" width="20px" height="20px"/>
									        </a>
                  							 <html:text property="salesBean.condValueDisp4" styleId="condValueDisp4" readonly="true" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue4"  styleId="condValue4"></html:hidden>							 
								        </td>
		                              </tr>
		                              
		                              <tr>
		                                <%-- <td align="left" width="20%"></td>
		                                <td align="center" width="5%"></td>
		                                <td align="left" width="75%"><html:radio property="salesBean.includePos" value="Y" />รวม Pos
		                                &nbsp;<html:radio property="salesBean.includePos" value="N"/>Offtake</td> --%>
		                              </tr>
		                              
	                              </table>
	                           </fieldset>
                            </td>
                            <td>
	                            <fieldset id="display-frame">
	                            <legend>การเลือกแสดงข้อมูล</legend>
			                             <table width="100%"  border="0" cellpadding="1" cellspacing="1">
			                              <tr>
			                              	<td colspan="3">
			                              		<html:select property="salesBean.summaryType" styleId="summaryType">
											    	<html:options collection="summaryTypeList" property="key" labelProperty="name"/>
									         	</html:select>
			                              	</td>
			                              </tr>
			                              <tr class="txt_style" >
			                                <td align="left"><b>ประเภทข้อมูล</b></td>
			                                <td align="left"><b>หน่วย</b></td>
			                                <td align="left"><b>เปรียบเทียบ</b></td>
			                              </tr>
			                              <tr class="txt_style" >
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp1">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit1">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.compareDisp1">
											        <html:options collection="compareColumnList" property="key" labelProperty="name"/>
									          </html:select>
										    </td>
			                              </tr>
			                              <tr class="txt_style" >
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp2">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit2">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                 
										    </td>
			                              </tr>
			                            <tr class="txt_style" >
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp3">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit3">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.compareDisp2">
											        <html:options collection="compareColumnList" property="key" labelProperty="name"/>
									          </html:select>
										    </td>
			                              </tr>
			                             <tr class="txt_style" >
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp4">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit4">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                  
										    </td>
			                              </tr>
		                              </table>
			                    </fieldset>        
                            </td>
                          </tr>
                        </table>	
                     </fieldset>					
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
								<%out.print(Utils.isNull(session.getAttribute("RESULT_SA"))); %>
							</div>
						    <!-- RESULT -->
						    
						    <!-- Hidden field -->
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