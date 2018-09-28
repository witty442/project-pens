<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.bean.MCBean"%>
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
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
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="mcForm" class="com.isecinc.pens.web.mc.MCForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/mtt.css" type="text/css" />
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

.headHoliday {
  text-align:center;
  background-color: #F78181;
  font-weight: bold;
}
.head {
  text-align:center;
  font-weight: bold;
  background-color: #D3D3D3;
}

.calendar1 {
  width: 14%;
}
.calendarHoliday {
  width: 14%;
  background-color: #F78181;
}

.textCalendar {
	background-color: #eee;
	font-size: 15px;
	font-weight: bold;
}
.textCalendarHoliday {
	background-color: #F78181;
	font-size: 15px;
	font-weight: bold;
}

textarea {
     resize:none;
     width: 100%;
     -webkit-box-sizing: border-box; /* Safari/Chrome, other WebKit */
     -moz-box-sizing: border-box;    /* Firefox, other Gecko */
     box-sizing: border-box;         /* Opera/IE 8+ */
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
}
function clearForm(path){
	var form = document.mcForm;
	form.action = path + "/jsp/mcAction.do?do=clearSearchMC";
	form.submit();
	return true;
}
function back(path){
	var form = document.mcForm;
	form.action = path + "/jsp/mcAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.mcForm;
	
	form.action = path + "/jsp/mcAction.do?do=save";
	form.submit();
	return true;
}
function copyDataFromLastMonth(path){
	var form = document.mcForm;
	if(confirm("ยืนยันการ Copy ข้อมูลจากเดือนก่อนหน้านี้")){
		form.action = path + "/jsp/mcAction.do?do=copyDataFromLastMonth";
		form.submit();
		return true;
	}
	return false;
}

</script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerMC.jsp"/></td>
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
				<jsp:param name="function" value="mc"/>
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
						<html:form action="/jsp/mcAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td >เจ้าหน้าที่  Staff
									</td>
									<td align="left"> 
									  <html:text property="bean.empId" styleId="empId" size="10" readonly="true" styleClass="disableText"/>
									  <input type="text" readonly class="disableText" size="40" value="${mcForm.bean.name} ${mcForm.bean.surName}" />
									  
									  <html:hidden property="bean.name" styleId="name"  />
									  <html:hidden property="bean.surName" styleId="surName" />
									   เบอร์โทรศัพท์  #1:<html:text property="bean.mobile1" styleId="mobile1" size="15" readonly="true" styleClass="disableText"/>
									   เบอร์โทรศัพท์  #2:<html:text property="bean.mobile2" styleId="mobile2" size="15" readonly="true" styleClass="disableText"/>
									ประเภท <html:text property="bean.empTypeDesc" styleId="empTypeDes" size="8"  readonly="true" styleClass="disableText"/>
									 <html:hidden property="bean.empType" styleId="empType" />
									</td>
								</tr>
								<tr>
                                    <td>เขตพื้นที่ </td>
									<td>	
										 <html:hidden property="bean.mcArea" styleId="mcArea" />
										  <html:text property="bean.mcAreaDesc" styleId="mcAreaDesc" size="20"  readonly="true" styleClass="disableText"/>
									 Route 
									 <html:hidden property="bean.mcRoute" styleId="mcRoute" /> -
									 <html:text property="bean.mcRouteDesc" styleId="mcRouteDesc" size="100" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td>เดือน </td>
									<td>		
										 <html:hidden property="bean.monthTrip" styleId="monthTrip" />
										 <html:text property="bean.monthTripDesc" styleId="monthTripDesc" size="20" readonly="true" styleClass="disableText"/>
										  Remark 
									     <html:text property="bean.remark" styleId="remark" size="59" styleClass="normalText"/>
									</td>
								</tr>
						   </table>
					  </div>
				  <table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="1" width="100%">
				       <tr>
				            <td class="head">จันทร์</td><!-- 2 -->
							<td class="head">อังคาร</td><!-- 3 -->
							<td class="head">พุธ</td><!-- 4 -->
							<td class="head">พฤหัสบดี</td><!-- 5 -->
							<td class="head">ศุกร์</td><!-- 6 -->
							<td class="headHoliday">เสาร์</td><!-- 7 -->
							<td class="headHoliday">อาทิตย์</td>	<!-- 1 -->
					   </tr> 
					   
					   <%
					   
						int maxDay = mcForm.getBean().getMaxDay();
					    int startDayOfMonth  = mcForm.getBean().getStartDayOfMonth();
					    Map<String,String> daysMap = mcForm.getBean().getDaysMap();
					    int row = 0;
					    int i=0;
					    
					    //System.out.println("maxDay:"+maxDay);
					    //System.out.println("startDayOfMonth:"+startDayOfMonth);
					    
					    int rows = (maxDay/7)+1;
					    if(startDayOfMonth==Calendar.SUNDAY && mcForm.getBean().getMaxDay() > 28){
					    	rows = rows+1;
					    }
					    
						for(row=1;row<=rows;row++) {
			                if(row==1){
							%>
							 <tr> 
							   <%
							    boolean found= false;
							    for(int w=1;w<=7;w++){ 
							    //System.out.println("w["+w+"],i:"+i);
							    
							    %>
							      <% if(startDayOfMonth==Calendar.MONDAY && w==1){ 
							    	   i++; String key = (String.valueOf(i).length()==1?"0"+i:String.valueOf(i))+mcForm.getBean().getMonthTrip();
							    	   found = true;
							      %>
							          <td class="calendar1" align="right">
							             <span class="textCalendar"><%=i %></span>
							             <input type="hidden" value="<%=w %>" name="week_<%=key%>"/>
							             <br>
							             <textarea rows="5" cols="10" name="<%=key%>"><%=Utils.isNull(daysMap.get(key))%> </textarea>
							           </td>
							      <% }else if(startDayOfMonth==Calendar.TUESDAY &&  w==2){
							    	  i++; String key = (String.valueOf(i).length()==1?"0"+i:String.valueOf(i))+mcForm.getBean().getMonthTrip();
							    	  found = true;
							    	  %>
							           <td class="calendar1" align="right">
							              <input type="hidden" value="<%=w %>" name="week_<%=key%>"/>
							             <span class="textCalendar"><%=i %></span>
							             <br>
							             <textarea rows="5" cols="10" name="<%=key%>"> <%=Utils.isNull(daysMap.get(key))%></textarea>
							           </td>
							      <% }else if(startDayOfMonth==Calendar.WEDNESDAY &&  w==3){
							    	  i++; String key = (String.valueOf(i).length()==1?"0"+i:String.valueOf(i))+mcForm.getBean().getMonthTrip();
							    	  found = true;
							    	  %>
							          <td class="calendar1" align="right">
							              <input type="hidden" value="<%=w %>" name="week_<%=key%>"/>
							             <span class="textCalendar"><%=i %></span>
							             <br>
							             <textarea rows="5" cols="10" name="<%=key%>"> <%=Utils.isNull(daysMap.get(key))%> </textarea>
							           </td>
							      <% }else if(startDayOfMonth==Calendar.THURSDAY &&  w==4){ 
							    	  i++; String key = (String.valueOf(i).length()==1?"0"+i:String.valueOf(i))+mcForm.getBean().getMonthTrip();
							    	  found = true;
							      %>
							           <td class="calendar1" align="right">
							             <input type="hidden" value="<%=w %>" name="week_<%=key%>"/>
							             <span class="textCalendar"><%=i %></span>
							             <br>
							             <textarea rows="5" cols="10" name="<%=key%>"><%=Utils.isNull(daysMap.get(key))%></textarea>
							           </td>
							      <% }else if(startDayOfMonth==Calendar.FRIDAY &&  w==5){ 
							    	  i++; String key = (String.valueOf(i).length()==1?"0"+i:String.valueOf(i))+mcForm.getBean().getMonthTrip();
							    	  found = true;
							      %>
							           <td class="calendar1" align="right">
							              <input type="hidden" value="<%=w %>" name="week_<%=key%>"/>
							             <span class="textCalendar"><%=i %></span>
							             <br>
							             <textarea rows="5" cols="10" name="<%=key%>"><%=Utils.isNull(daysMap.get(key))%></textarea>
							           </td>
							      <% }else if(startDayOfMonth==Calendar.SATURDAY &&  w==6){ 
							    	  i++;String key = (String.valueOf(i).length()==1?"0"+i:String.valueOf(i))+mcForm.getBean().getMonthTrip();
							    	  found = true;
							      %>
							           <td class="calendarHoliday" align="right">
							               <input type="hidden" value="<%=w %>" name="week_<%=key%>"/>
							              <span class="textCalendarHoliday"><%=i %></span>
							             <br>
							             <textarea rows="5" cols="10" name="<%=key%>"><%=Utils.isNull(daysMap.get(key))%></textarea>
							           </td>
							      <% }else if(startDayOfMonth==Calendar.SUNDAY &&  w==7){ 
							    	  i++; String key = (String.valueOf(i).length()==1?"0"+i:String.valueOf(i))+mcForm.getBean().getMonthTrip();
							    	  found = true;
							       %>
							          <td class="calendarHoliday" align="right">
							              <input type="hidden" value="<%=w %>" name="week_<%=key%>"/>
							             <span class="textCalendarHoliday"><%=i %></span>
							             <br>
							             <textarea rows="5" cols="10" name="<%=key%>"><%=Utils.isNull(daysMap.get(key))%> </textarea>
							           </td>
							      <%}else{ 
							    	  if(found){
							    		  i++;
							    	  }
							      %>
							           
							           <%if(i > 0){
							        	   String key = (String.valueOf(i).length()==1?"0"+i:String.valueOf(i))+mcForm.getBean().getMonthTrip();
							        	   String tdClass= "calendar1";
							        	   String textClass= "textCalendar";
							        	   if(w==6 || w==7){
							        		   tdClass= "calendarHoliday";
							        		   textClass= "textCalendarHoliday";
							        	   }
							        	   %>
							            <td class="<%=tdClass%>" align="right">
							                <input type="hidden" value="<%=w %>" name="week_<%=key%>"/>
							                <span class="<%=textClass%>"><%=i %></span>
							                <br>
							                <textarea rows="5" cols="10" name="<%=key%>"><%=Utils.isNull(daysMap.get(key))%></textarea>
							           </td>
							           <%}else{ %>
							             <td class="calendar1" align="right">
							             <br>
							             
							           </td>
							           <%} %>
							      <%}//if %>
							      
							    <%}//for %>
							 </tr> 
					     <%}else{ %>
					     
					        <tr> 
							   <%for(int w=1;w<=7;w++){ 
							        i++; String key = (String.valueOf(i).length()==1?"0"+i:String.valueOf(i))+mcForm.getBean().getMonthTrip();
							        //System.out.println("monthTrip:"+mcForm.getBean().getMonthTrip());
							        String tdClass= "calendar1";
							        String textClass= "textCalendar";
						        	   if(w==6 || w==7){
						        		   tdClass= "calendarHoliday";
						        		   textClass= "textCalendarHoliday";
						        	   }
								    if(i<=maxDay){
								    %>
								       <td class="<%=tdClass %>" align="right">
								           <input type="hidden" value="<%=w %>" name="week_<%=key%>"/>
							               <span class="<%=textClass%>"><%=i %></span>
							                <br>
							                <textarea rows="5" cols="10" name="<%=key%>"><%=Utils.isNull(daysMap.get(key))%></textarea>
							           </td>
								    <%}else{ %>
								       <td class="calendar1" align="right">
							               <br>
							           </td>
								  <%} 
							    }//for %>
							 </tr> 
					     
						<%
					     }//if
						}//for %>
									
				  </table>
	
				
				<!-- ************************Result ***************************************************-->	
				<div align="center">
					 <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
								    <%if ( !Utils.userInRole(user,new String[]{User.MT_SALE,User.MCQUERY}) ){%>         		  
										<a href="javascript:copyDataFromLastMonth('${pageContext.request.contextPath}')">
										  <input type="button" value="Copy Data From Last Month" class="newPosBtnLong"> 
										</a>
									<%} %>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								    <%//if( !User.MT_SALE.equalsIgnoreCase(user.getRole().getKey())){  %>  
								    <%if ( !Utils.userInRole(user,new String[]{User.MT_SALE,User.MCQUERY}) ){%>       		  
										<a href="javascript:save('${pageContext.request.contextPath}')">
										  <input type="button" value="   บันทึก      " class="newPosBtnLong"> 
										</a>
									 <%} %>
									<a href="javascript:back('${pageContext.request.contextPath}')">
									  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
									</a>						
								</td>
							</tr>
					</table>
					</div>		
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