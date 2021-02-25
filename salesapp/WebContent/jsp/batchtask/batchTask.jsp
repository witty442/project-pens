<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.web.batchtask.BatchTaskListBean"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.web.batchtask.BatchTaskInfo"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.MonitorBean"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="batchTaskForm" class="com.isecinc.pens.web.batchtask.BatchTaskForm" scope="session" />
<%
/*clear session form other page no clear in BatchTask (use session for display results ) */
//SessionUtils.clearSessionUnusedForm(request, "batchTaskForm");

try{
User user = (User) session.getAttribute("user");
String pageName = Utils.isNull(request.getParameter("pageName"));
String initBatchAction = Utils.isNull(request.getParameter("initBatchAction"));
String batchAction = Utils.isNull(request.getParameter("batchAction"));

System.out.println("pageName:"+pageName);
System.out.println("action:"+request.getAttribute("action"));
System.out.println("initBatchAction:"+initBatchAction);
System.out.println("batchAction:"+batchAction);

//String buttonName = "";
BatchTaskInfo taskInfo = batchTaskForm.getTaskInfo(); 
String buttonName = "";
List<BatchTaskInfo> paramList = null;
if(taskInfo != null){
	//paramMap = taskInfo.getParamMap();//old code
	
	paramList = taskInfo.getParamList();
	buttonName = taskInfo.getButtonName();
	
	System.out.println("Desc:"+taskInfo.getDescription());
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
 
<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<!-- Include Progress Bar  -->
<jsp:include page="../progressBar.jsp" flush="true"/>
<!-- /Include Progress Bar  -->	

<!-- OLD CODE -->
<%out.println(taskInfo.getValidateScript()); %>
<!-- NEW CODE GR FROM FILE -->

<Script>
    function loadme(){
    	/** Case onload from popup Page summited Batch**/
      	 <%if( "initBatchFromPageByPopup".equalsIgnoreCase(initBatchAction)){%>
      	     submitBatchFromPageByPopup(document.getElementById("path").value);
      	 <%}%>
      	 
      	 /** Case onload Task Success and search to display Case from popup Page summited Batch */
      	 /** refresh main page searchBatch and data BatchTaskList from session **/
      	 <%if( "searchTaskFinishFromPopupPage".equals(request.getAttribute("action"))){%>
	         window.opener.searchBatch('<%=request.getContextPath()%>');
	         window.close();
	     <%}%>
	     
	     /** Case onload from popup Page summited Batch**/
      	 <%if( "initBatchFromPageByPopupNoWait".equalsIgnoreCase(initBatchAction)){%>
      	     submitBatchFromPageByPopupNoWait(document.getElementById("path").value);
      	 <%}%>
      	 
      	 /** start check batchTask by Ajax **/
       	<%if( "submited".equals(request.getAttribute("action"))){%>
       	   startCheckBatch();
       	<%}%>
       	
       	//load calendar parameter date
       	<%if( paramList != null && paramList.size() >0){
			 for(int n=0;n<paramList.size();n++){
		        BatchTaskInfo task = paramList.get(n);   
		        if(task.getParamType().equalsIgnoreCase("DATE")){%> 
		           new Epoch('epoch_popup', 'th', document.getElementById('<%=task.getParamName()%>'));
		 <%   }//if
		     }//while
		   }//if 
		 %>
    }
    /** Start Batch**/
	function submitBatch(path) {
		var confirmText = "�׹�ѹ Process ";
		if(validate()){
		  if(confirm(confirmText)){
			document.batchTaskForm.action = path + "/jsp/batchTaskAction.do?do=runBatch&action=submited";
			document.batchTaskForm.submit();
			return true;
		  }
		}
		return false; 
	}
	
	/** Start Batch from Page By Popup Page**/
	function submitBatchFromPageByPopup(path) {
		//alert("path:"+path+",runBatchFromPopup");
		document.batchTaskForm.action = path + "/jsp/batchTaskAction.do?do=runBatchFromPageByPopup&action=submited";
		document.batchTaskForm.submit();
		return true;
	}
	function disableF5(e) {
		if (e.which == 116) e.preventDefault(); 
	}
	//To re-enable f5
	$(document).unbind("keydown", disableF5);
	
	//clear cach
	$.ajaxSetup({cache: false});
	

	function search(path, type) {
		var param = "";
		 <%if( "initBatchFromPageByPopup".equalsIgnoreCase(batchAction)){%>
		    param +="&batchAction=initBatchFromPageByPopup";
		 <%}%>
		 <%if( "initBatchFromPageByPopupNoWait".equalsIgnoreCase(batchAction)){%>
		    param +="&batchAction=initBatchFromPageByPopupNoWait";
		 <%}%>
		 //alert(param);
		document.batchTaskForm.action = path + "/jsp/batchTaskAction.do?do=search"+param;
		document.batchTaskForm.submit();
		return true;
	}
	function clearForm(path, type) {
		document.batchTaskForm.action = path + "/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName="+document.getElementById("pageName").value;
		document.batchTaskForm.submit();
		return true;
	}
</Script>

	<!-- ProgressBar -->
	<% if( "submited".equals(request.getAttribute("action"))){%>
	   <script type="text/javascript" language="javascript">
	   
	    //To disable f5
	    $(document).bind("keydown", disableF5);
	 
	    //init progress bar
	    startProgressBar();
	   
	   var useTimeMillisecs = 0;
	   var startTime = new Date();

	   /** Onload Window    */
	   var startDate = new Date();
	   function startCheckBatch(){
    	   var status = document.getElementsByName("monitorBean.status")[0]; 
            
    	 // alert(status.value);
    	   if (status.value != "1" && status.value != "-1"){
    		   window.setTimeout("checkStatusProcess();", 800);
    	   }
	    } 

	    /** Check Status From monitor_id BY AJax */
	    function checkStatusProcess(){
	    	$(function(){
	    		var getData = $.ajax({
	    			url: "${pageContext.request.contextPath}/jsp/batchtask/getStatusBatchTaskAjax.jsp",
	    			data : "id=<%=request.getAttribute("id")%>&transaction_count=<%=request.getAttribute("transaction_count")%>&imp=<%=request.getAttribute("import")%>",
	    			success: function(getData){
	    				var returnString = jQuery.trim(getData);
	    				document.getElementsByName("monitorBean.status")[0].value = returnString;
	    			}
	    		}).responseText;   
	    	});
	    	setTimeout("checkStatus()",3000);
	    }
	    
        /** Check Status Recursive **/
	    function checkStatus(){
	    	  var status =  document.getElementsByName("monitorBean.status")[0].value;
	    	 // alert(status);
	    	   if(status == '1'){ //Finish Task
	    		   //Calc Time thred use
	    		   try{
		    		   var endDate = new Date();
		    		   var dif = endDate.getTime() - startDate.getTime();
	
		    		   var Seconds_from_T1_to_T2 = dif / 1000;
		    		   //var Seconds_Between_Dates = Math.abs(Seconds_from_T1_to_T2);
		    		   
		    		   document.getElementsByName("monitorBean.timeInUse")[0].value = Seconds_from_T1_to_T2;//Seconds_Between_Dates; 
		    		   
		    		  // alert( document.getElementsByName("monitorBean.timeInUse")[0].value);
	    		   }catch(e){}
	    		   
	    		   /** Task Success ***/
	    		   updateProgress(status);
                   
	    		   //search display
	    		   search('<%=request.getContextPath()%>', 'admin');
                   
	    	   }else { //Task Running
	    		   /** Task Not Success  and Re Check Status**/
		    	   updateProgress(status);
	               window.setTimeout("checkStatusProcess();", 1200);
	           }
	    }
	    </script>
	    <!-- PROGRESS BAR -->
	<% } %>
</head>

<body class="sb-nav-fixed">

	   <!-- Include Header Mobile  -->
       <jsp:include page="../header.jsp"  flush="true"/>
       <!-- /Include Header Mobile -->
       
	    	<!-- PROGRAM HEADER -->
	    	<!-- Case submit from page by popu no display Name Batch-->
	    	 <%if( !"initBatchFromPageByPopup".equalsIgnoreCase(initBatchAction)
	    		&& !"initBatchFromPageByPopup".equalsIgnoreCase(batchAction)
	    		&& !"initBatchFromPageByPopupNoWait".equalsIgnoreCase(initBatchAction)
	    		&& !"initBatchFromPageByPopupNoWait".equalsIgnoreCase(batchAction)
	    	   ){%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="<%=pageName%>"/>
				</jsp:include>
			<%} %>
	      	<!-- TABLE BODY -->
	    
						<!-- BODY -->
						<html:form action="/jsp/batchTaskAction"  enctype="multipart/form-data">
			
						<jsp:include page="../error.jsp"/>
						
						<!-- Generate Parameter By Task Config -->
					     <table align="center" border="0" cellpadding="3" cellspacing="10" width="100%">
							<%
							if( paramList != null && paramList.size() >0){
								for(int p=0;p<paramList.size();p++){
								     BatchTaskInfo task = paramList.get(p);
							%> 
								<tr>
								<%if(task.getParamType().equalsIgnoreCase("DATE")|| task.getParamType().equalsIgnoreCase("STRING") ){ %>
									<td width ="40%" align="right">
									    <%=task.getParamLabel() %>
									    <%if(task.getParamValid().equalsIgnoreCase("VALID")){%>
									       <font color="red">*</font>
									    <%} %>
									</td>
									<td width ="60%" align="left">
									    <input type="text" name="<%=task.getParamName() %>" id="<%=task.getParamName() %>" 
									     value="<%=task.getParamValue() %>"/>
									</td>
								<%}else if(task.getParamType().indexOf("LIST") != -1){ %>
									<td width ="40%" align="right">
									    <%=task.getParamLabel() %>
									    <%if(task.getParamValid().equalsIgnoreCase("VALID")){%>
									       <font color="red">*</font>
									    <%} %>
									</td>
									<td width ="60%" align="left">
									<%
									String keySessionName = task.getParamType();
									System.out.println("keySessionName:"+keySessionName);
									 if(session.getAttribute(keySessionName) != null){
										 List<BatchTaskListBean> listBox = (List<BatchTaskListBean>)session.getAttribute(keySessionName);
									%>
									    <select name="<%=task.getParamName() %>" id="<%=task.getParamName() %>">
									    
										    <%for(int i=0;i<listBox.size();i++){ 
										    	BatchTaskListBean data = listBox.get(i);
										    	
										    	if(task.getParamValue().equalsIgnoreCase(data.getValue())){
										    %>
									               <option value="<%=data.getValue() %>" selected><%=data.getDisp() %></option>
									        <%  }else{ %>
									               <option value="<%=data.getValue() %>"><%=data.getDisp() %></option>
									        <%  }//if %>
									       <%}//for %>
									 </select>
									<% }//if
									 %>
									</td>
								<%}else if(task.getParamType().equalsIgnoreCase("FROMFILE")){ %>
									<td width ="40%" align="right">
									   <%=task.getParamLabel() %>
									    <%if(task.getParamValid().equalsIgnoreCase("VALID")){%>
									       <font color="red">*</font>
									    <%} %>
									</td>
									<td width ="60%" align="left">
									   <input type="file" name="<%=task.getParamName() %>" 
									     id="<%=task.getParamName() %>"/>
									</td>
								<%} %>
								</tr>
						<% 
						   }//for
						}//if
						%>
						</table>

                        <!-- Generate BUTTON By Task Config-->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						    <tr>
								<td align="center" width ="100%">&nbsp;</td>
							</tr>
							<tr>
								<td align="center" width ="100%">
								      <input type="button" name ="process" value="<%=buttonName%>" class="newPosBtnLong" onClick="javascript:submitBatch('${pageContext.request.contextPath}')">
								      <input type="button" value="��Ǩ�ͺʶҹ� ����ش" class="newPosBtnLong" style="width: 200px;" onClick="javascript:search('${pageContext.request.contextPath}','admin')">
								      <input type="button" value="Clear" class="newPosBtnLong" style="width: 100px;" onClick="javascript:clearForm('${pageContext.request.contextPath}','admin')">
								</td>
							</tr>
							<tr>
								<td align="center" width ="100%">&nbsp;</td>
							</tr> 
							 <tr>
								<td align="center" width ="100%">
								 <%if(user.getUserName().equalsIgnoreCase("admin")){ %>
									  <table border="0"  width ="50%" cellpadding="3" cellspacing="1" bgcolor="#000000">
									    <tr bgcolor="#FFFFFF">
									      <td width="10%" align="center"><b>Process Name</b></td>
									      <td width="40%" align="center"><b>Process Description</b></td>
									    </tr>
									    <tr bgcolor="#FFFFFF">
									       <td> <%=pageName %></td>
									       <td> <%=Utils.isNull(taskInfo.getDescription()) %></td>
									    </tr>
										 <tr bgcolor="#FFFFFF">
										     <td> Table Ralate</td>
										     <td> <%=Utils.isNull(taskInfo.getDevInfo()) %></td>
										  </tr>
									  </table>
									<%}else{ %>
									   <table border="0" width ="50%" cellpadding="3" cellspacing="1" bgcolor="#000000">
									    <tr  bgcolor="#FFFFFF">
									      <td width="10%" align="center"><b>Process Name</b></td>
									      <td width="40%" align="center"><b>Process Description</b></td>
									    </tr>
									    <tr  bgcolor="#FFFFFF">
									       <td> <%=pageName %></td>
									       <td> <%=Utils.isNull(taskInfo.getDescription()) %></td>
									    </tr>
									  </table>
									<%} %>
								</td>
							</tr>
							 <tr>
								<td align="center" width ="100%">
								<%if(user.getUserName().equalsIgnoreCase("admin")){ %>
								<!--   <b><a href="javascript:showDivInfo()">Process Info</a></b> -->
								<%} %>
								</td>
							</tr>
						</table>   
						<div id="divInfo" style="display: none" align="center">
					     
						</div>
                        <!-- Batch Task Result--> 
					    <jsp:include page="batchTaskResult.jsp"></jsp:include>
						<br><br>
						
						<html:hidden property="monitorBean.status"  styleClass="disableText"/>
						<input type="hidden" name="pageName"  id="pageName" value="<%=pageName%>"/>
					    <input type="hidden" name="path"  id="path" value="${pageContext.request.contextPath}"/>
					    <input type="hidden" name="batchAction"  id="batchAction" value="<%=initBatchAction%>"/>
				</html:form>
				<!-- BODY -->   
					
	<!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->	
</body>
<script>
function showDivInfo(){
	document.getElementById("divInfo").style.display = "block";
}
</script>
<%
}catch(Exception e){
	e.printStackTrace();
} %>

</html>
