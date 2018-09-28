<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.bean.MCBean"%>
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
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
<jsp:useBean id="mcTimeForm" class="com.isecinc.pens.web.mc.MCTimeForm" scope="session" />

<%
 User user = (User) request.getSession().getAttribute("user");

 /** For diable save or disableText **/
 String textStyle = "timeStyle";
 String textStyle2 = "normalText";
 boolean readonly = false;
 
 if(Utils.userInRole(user,new String[]{User.MCQUERY}) ){
	 readonly = true;
	 textStyle = "timeStyleDisable";
	 textStyle2 = "disableText";
 }

if(session.getAttribute("dayoffReasonList") == null){
	List<PopupForm> reasonLeaveList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	reasonLeaveList.add(ref);
	reasonLeaveList.addAll(MCDAO.searchMCRefList(new PopupForm(),"","DayoffReason"));
		
	session.setAttribute("dayoffReasonList",reasonLeaveList); 
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
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

.day {
  width: 14%;
}
.holiday {
  width: 14%;
  background-color: #F78181;
}
.timeStyle {
	border: 1px solid;
	border-color: #BEBEBE;
	color: #000000;
	text-align: center;
}
.timeStyleDisable {
    background-color: #DCDCDC;
	border: 1px solid;
	border-color: #BEBEBE;
	color: #000000;
	text-align: center;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
  sumTotalTime();
}

function exportExcel(path){
	var form = document.mcTimeForm;
	 var param ="&empRefId="+form.empRefId.value;
     param +="&staffYear="+form.staffYear.value;
     param +="&staffMonth="+form.staffMonth.value;
     param +="&totalTimeAll="+form.totalTimeAll.value;
     
	form.action = path + "/jsp/mcTimeAction.do?do=exportExcel"+param;
	form.submit();
	return true;
}

function back(path){
	var form = document.mcTimeForm;
	form.action = path + "/jsp/mcTimeAction.do?do=prepareSearch&action=back";
	form.submit();
	return true;
}

function search(path){
	var form = document.mcTimeForm;
	if( $('#mcArea').val()==""){
		alert("กรุณาระบุ เขตพื้นที่");
		return false;
	}
	/* if( $('#staffType').val()==""){
		alert("กรุณาระบุ ประเภท");
		return false;
	} */
	
	form.action = path + "/jsp/mcTimeAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function save(path){
	var form = document.mcTimeForm;
	if(validTime()==false){
		return false;
	}
	form.action = path + "/jsp/mcTimeAction.do?do=save";
	form.submit();
	return true;
}

function isNum(obj){
	  if(obj.value != ""){
		var newNum = parseFloat(obj.value);
		if(isNaN(newNum)){
			//alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
			obj.value = "";
			obj.focus();
			return false;
		}else{return true;}
	   }
	  return true;
}
	
function setTimeMask(index,obj){
	var totalTime = document.getElementsByName("totalTime")[index];
	var startTime = document.getElementsByName("startTime")[index];
	var endTime = document.getElementsByName("endTime")[index];
	
	var t = obj.value;
	 t = t.replace(new RegExp(':', 'g'), '');
	// alert(t);
	if(t != ''){
		if(t.length==4){
			obj.value = t.substr(0,2)+":"+t.substr(2,2);
			
			validateTime(index,obj);
		}else {
			alert("กรอก รูปแบบเวลาผิด  กรุณากรอกใหม่");
			obj.focus();
			obj.value = "";
			obj.style.backgroundColor = '#fba';
			
			//Clear TotalTime
			totalTime.value ='';
			sumTotalTime();
		}
	}else{
		//alert("startTime["+startTime.value+"]endTime["+endTime.value+"]");
		if(startTime.value == '' || endTime.value  == ''){
		  //Clear TotalTime
		  totalTime.value ='';
		  sumTotalTime();
		}
	}	
}

function validateTime(index,obj){
	var t = obj.value;
	if(t.length==5){
		if(!validateTimeModel(obj)){
			alert("กรอก รูปแบบเวลาผิด  กรุณากรอกใหม่");
			obj.value = "";
		}
	}
	var totalTime = document.getElementsByName("totalTime")[index];
	var startTime = document.getElementsByName("startTime")[index];
	var endTime = document.getElementsByName("endTime")[index];
	//alert("startTime:"+startTime.value+",endTime:"+endTime.value);
	
	if(startTime.value != '' && endTime.value  != ''){
		var validDiffTime = validateDiffTime(index,startTime.value,endTime.value);
		
		if(validDiffTime){
		   calcTotalTime(index,startTime.value,endTime.value);
		   sumTotalTime();
		   endTime.style.backgroundColor = 'white';
		}else{
			alert("เวลาออก ต้องมากกว่าเวลาเข้า");
			endTime.focus();
			endTime.value ="";
			endTime.style.backgroundColor = '#fba';
			totalTime.value = "";
			
			sumTotalTime();
		}
	}
}

function calcTotalTime(index,startTime,endTime){
	var totalTime = document.getElementsByName("totalTime")[index];
  
     //lets calculate the difference. But values consist of four digits.
     var startHH = parseInt(startTime.substr(0,2));
     var startMM = parseInt(startTime.substr(3,2));
     var endHH = parseInt(endTime.substr(0,2));
     var endMM = parseInt(endTime.substr(3,2));
     
     var diffHH = endHH - startHH;
     
     var diffMM = 0;
     if(endMM < startMM){
    	 diffMM = (endMM+60) - startMM;
    	 diffHH = diffHH-1;
     }else{
        diffMM = endMM - startMM;
     }
     
     diffHH = Math.abs(diffHH);
     diffMM = Math.abs(diffMM);
     
     diffHH = diffHH < 10 ? "0" + diffHH : diffHH;
     diffMM = diffMM < 10 ? "0" + diffMM : diffMM;

     totalTime.value = diffHH + ":" + diffMM; 
}

function calcTotalTime_V1(index,startTime,endTime){
	var totalTime = document.getElementsByName("totalTime")[index];
  
     //lets calculate the difference. But values consist of four digits.
     var time1Seconds = toSeconds(startTime.substr(0,2), startTime.substr(3,2));
     var time2Seconds = toSeconds(endTime.substr(0,2), endTime.substr(3,2));

     if (!time1Seconds || !time2Seconds){
         //input is not correct.
         return false;
     }

     var difference = time1Seconds - time2Seconds;
     //alert("time2Seconds:"+time2Seconds+"<>time1Seconds:"+time1Seconds);
     if (difference < 0) {
         difference = Math.abs(difference);
     }
     var hours = parseInt(difference/3600); 
     hours = hours < 10 ? "0" + hours : hours;
     
     var minutes =  parseInt((difference/3600) % 1 *60);
     alert("diff["+difference+"]mod["+parseInt((difference/3600) % 1 *60)+"]");
     
     minutes = minutes < 10 ? "0" + minutes : minutes;

     totalTime.value = hours + ":" + minutes; 
}

function validateDiffTime(index,startTime,endTime){
	var totalTime = document.getElementsByName("totalTime")[index];
  
     //lets calculate the difference. But values consist of four digits.
     var time1Seconds = toSeconds(startTime.substr(0,2), startTime.substr(3,2));
     var time2Seconds = toSeconds(endTime.substr(0,2), endTime.substr(3,2));

     if (!time1Seconds || !time2Seconds){
         //input is not correct.
         return false;
     }

     var difference = time2Seconds - time1Seconds;
     //alert("difference:"+difference);
     
     if (difference <= 0) {
    	 return false;
     }
   return true;
}

function toSeconds(hours, minutes){
    var seconds = 0;
    if ( (hours >= 0 && hours < 24) && (minutes >= 0 && minutes < 60)){
        seconds += (parseInt(hours)*3600) + (parseInt(minutes)*60);
        return seconds
    }else{
        return false;
    }
}

function validTime(){
	var startTime = document.getElementsByName("startTime");
	var endTime = document.getElementsByName("endTime");
	var totalTime = document.getElementsByName("totalTime");
	var valid = true;
    var validInput = true;
    var validDiffTime = true;
	for(var i=0;i<totalTime.length;i++){
		
		/* if(startTime[i].value == '' || endTime[i].value  == ''){
			validInput = false;
			if(startTime[i].value == ''){
				startTime[i].style.backgroundColor = '#fba';
			}else{
				startTime[i].style.backgroundColor = 'white';
			}
			
			if(endTime[i].value == ''){
				endTime[i].style.backgroundColor = '#fba';
			}else{
				endTime[i].style.backgroundColor = 'white';
			}
		} */
		
		if(validInput ==true){
			if(startTime[i].value != '' && endTime[i].value  != ''){
				var validDiffTime = validateDiffTime(i,startTime[i].value,endTime[i].value);
				
				if(validDiffTime){
				   calcTotalTime(i,startTime[i].value,endTime[i].value);
				   endTime[i].style.backgroundColor = 'white';
				}else{
					totalTime[i].value = "";
					validDiffTime = false;
					endTime[i].style.backgroundColor = '#fba';
				}
			}
		}
	}
	
	sumTotalTime();
	
	if(!validInput){
	   alert("กรุณากรอกข้อมูลให้ครบถ้วน");
	}
	if(!validDiffTime){
       alert("เวลาออก ต้องมากกว่าเวลาเข้า");
	}
	if(validInput == false || validDiffTime == false){
		valid = false;
	}
	return valid;
}

function sumTotalTime(){
	var totalTimeAll = document.getElementsByName("totalTimeAll")[0];
	var totalTime = document.getElementsByName("totalTime");
	//alert(totalTime.length);
	var totalHH  = 0;
	var totalMM  = 0;
	for(var i=0;i<totalTime.length;i++){
		if( totalTime[i].value != ''){
			var HH = totalTime[i].value.substr(0,2);
			var MM = totalTime[i].value.substr(3,2);
			
			//alert("HH["+HH+"]MM["+MM+"]");
			
			totalHH += parseInt(HH);//HH ->seconds
			totalMM += parseInt(MM);//MM ->seconds
		}//if
	}//for
	
    //convert to hh:mm
     //alert("totalMM["+totalMM+"]:%["+parseInt(totalMM % 60)+"]");
	 var minutes =  parseInt(totalMM % 60);
     minutes = minutes < 10 ? "0" + minutes : minutes;
     
     var hours = totalHH +parseInt(totalMM/60);  
     hours = hours < 10 ? "0" + hours : hours;

    // alert(hours+":"+minutes);
     
     totalTimeAll.value = hours + ":" + minutes;
}

function hoursToSeconds(hours){
    var seconds = 0;
    if ( (hours >= 0 && hours < 24) ){
        seconds += (parseInt(hours)*3600);
        return seconds;
    }else{
        return false;
    }
}

function minuteToSeconds(minutes){
    var seconds = 0;
    if ( (minutes >= 0 && minutes < 60)){
        seconds +=  (parseInt(minutes)*60);
        return seconds;
    }else{
        return false;
    }
}

//HH:mm
function validateTimeModel(inputField) {
    //var isValid = /^([0-1]?[0-9]|2[0-4]):([0-5][0-9])(:[0-5][0-9])?$/.test(inputField.value);
    var timeStr = inputField.value;
    
   // alert(timeStr.substr(0,2)+","+timeStr.substr(3,2));
    
    var isValid = (timeStr.search(/^\d{2}:\d{2}$/) != -1) &&
                  (timeStr.substr(0,2) >= 0 && timeStr.substr(0,2) <= 24) &&
                  (timeStr.substr(3,2) >= 0 && timeStr.substr(3,2) <= 59) ;
    
    //alert(isValid);
    
    if (isValid) {
        inputField.style.backgroundColor = 'white';
    } else {
        inputField.style.backgroundColor = '#fba';
    }

    return isValid;
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
				<jsp:param name="function" value="mcTime"/>
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
						<html:form action="/jsp/mcTimeAction">
						<jsp:include page="../error.jsp"/>
						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td align="right"> รหัสพนักงาน <font color="red"></font></td>
									<td>		
										 <html:text property="bean.empId" styleId="empId" size="10" styleClass="disableText" readonly="true"/>
										 <html:hidden property="bean.empRefId" styleId="empRefId" />
										 <html:hidden property="bean.staffYear" styleId="staffYear" />
										 <html:hidden property="bean.staffMonth" styleId="staffMonth" />
									</td>
								   <td align="right"> ชื่อ-สกุล<font color="red"></font></td>
									<td>		
										 <html:text property="bean.fullName" styleId="fullName" size="40" styleClass="disableText" readonly="true"/>
									</td>
									 <td align="right">ประจำปี/เดือน<font color="red"></font></td>
									 <td>		
										<input type="text" name="disp"  size="40" value="${mcTimeForm.bean.staffYear}/${mcTimeForm.bean.staffMonth}" readonly class="disableText"/>
									</td>
								</tr>
									<tr>
                                    <td align="right">ประเภท<font color="red"></font></td>
									<td>		
										 <html:text property="bean.empTypeDesc" styleId="empTypeDesc" size="10" styleClass="disableText" readonly="true"/>
									</td>
								    <td align="right"> ภาค<font color="red"></font></td>
									<td>		
										  <html:text property="bean.regionDesc" styleId="regionDesc" size="40" styleClass="disableText" readonly="true"/>
									</td>
									<td align="right">Route เส้นทาง/สายงาน<font color="red"></font></td>
									 <td>		
										<input type="text" name="disp"  size="40" value="${mcTimeForm.bean.empRouteName}" readonly class="disableText"/>
									</td>
								</tr>
						   </table>
						
					  </div>

            <c:if test="${mcTimeForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="80%">
						       <tr>
						            <th >วันที่</th>
						            <th >เวลาเข้า</th>
									<th >เวลาออก</th>
									<th >รวมเวลา (ชั่วโมง:นาที)</th>
									<th >สาเหตุการลา</th>
									<th >หมายเหตุ</th>

							   </tr>
							<% 
							String tabclass ="lineE";
							List<MCBean> resultList = mcTimeForm.getResults();
							int tabeIndex = 0;
							for(int n=0;n<resultList.size();n++){
								MCBean mc = (MCBean)resultList.get(n);
								if(mc.isHoliday()){
									tabclass="holiday";
								}else{
									tabclass="lineE";
								}
								tabeIndex++;
								%>
								<tr class="<%=tabclass%>">
								 	<td class="td_text_center" width="10%">
								 	   <input type="text" name="staffDate" value="<%=mc.getStaffDate()%>" class="disableText" readonly size="8" tabindex="-1"/>
								 	</td>
									<td class="td_text" width="10%" nowrap><font color="red"></font>
									   <input type="text" name="startTime" <%=readonly?"readonly":"" %> class ="<%=textStyle%>" maxlength="5" onblur="setTimeMask(<%=n%>,this)" value="<%=mc.getStartTime()%>" size="10" tabindex="<%=tabeIndex%>" />
									</td>
									<td class="td_text" width="10%" nowrap><font color="red"></font>
									     <input type="text" name="endTime" <%=readonly?"readonly":"" %> class ="<%=textStyle%>" maxlength="5" onblur="setTimeMask(<%=n%>,this)" value="<%=mc.getEndTime()%>"  size="10" tabindex="<%=tabeIndex%>" >
									</td>
								    <td class="td_text" width="10%">
								        <input type="text" name="totalTime" class ="timeStyleDisable"  value="<%=mc.getTotalTime()%>"  size="15"  tabindex="-1" class="disableText" readonly/>
								    </td> 
								    <td class="td_text_center" width="20%">
								         <select name="reasonLeave"  tabindex="<%=tabeIndex%>" <%=readonly?"disabled":"" %>>
								          <% 
								          if(session.getAttribute("dayoffReasonList") != null){
								        	  List<PopupForm> reasonLeaveList = (List)session.getAttribute("dayoffReasonList");
									          String selected ="";
									          for(int i=0;i<reasonLeaveList.size();i++){ 
									        	 selected ="";
									             PopupForm item = reasonLeaveList.get(i);
									             if(item.getCode().equalsIgnoreCase(mc.getReasonLeave())){
									            	 selected = "selected"; 
									             }
									           %>
									            <option value="<%=item.getCode()%>" <%=selected %>><%=item.getDesc() %></option>
									          <% } }%>
								        </select>
								        
								    </td>
									<td class="td_text" width="20%"> 
									  <input type="text" name="note"  <%=readonly?"readonly":"" %> class ="<%=textStyle2%>" value="<%=mc.getNote()%>"  tabindex="<%=tabeIndex%>" size="40"/>
									</td>
								
								</tr>
							<%} %>
							 <tr class="<%=tabclass%>">
							   <td  align="right" colspan="3">
							           <b>  รวมเวลา =></b>
							   </td>
							   <td align="center">
							     <input type="text" name="totalTimeAll" tabindex="-1" size="15" class="timeStyleDisable"/>
							   </td>
							    <td colspan="2"></td>
							 </tr>
					</table>
				</c:if>
				
		       <!-- ************************Result ***************************************************-->
					<div align="center">
					 <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								   <%if( !readonly){ %>
									<a href="javascript:save('${pageContext.request.contextPath}')">
									  <input type="button" value="   บันทึก      " class="newPosBtnLong"> 
									</a>
									<%} %>
									<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="   ExportToExcel   " class="newPosBtnLong">
									</a>
									
									<a href="javascript:back('${pageContext.request.contextPath}')">
									  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
									</a>						
								</td>
							</tr>
					</table>
					</div>		
					
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