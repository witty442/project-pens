<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="adminConsoleForm" class="com.isecinc.pens.web.adminconsole.AdminConsoleForm" scope="request" />
<%

%>
<html>
<head>
<title>Admin Console</title>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />

<style type="text/css">
.tab_style {
  color:green; /* �բͧ����ѡ�� */
  background-color:white; /* �վ�鹢ͧ���� */
  font:18pt Tahoma; /* Ẻ�ѡ�� */
  padding:3px; /* ��ͧ��ҧ�����ҧ��ͺ�Ѻ����ѡ�� */
}
.tab_selected_style {
  color:white; /* �բͧ����ѡ�� */
  background-color:black; /* �վ�鹢ͧ���� */
  font:18pt Tahoma; /* Ẻ�ѡ�� */
  padding:3px; /* ��ͧ��ҧ�����ҧ��ͺ�Ѻ����ѡ�� */
}

.button2_style {
  font:14pt Tahoma; /* Ẻ�ѡ�� */
  padding:3px; /* ��ͧ��ҧ�����ҧ��ͺ�Ѻ����ѡ�� */
}

.h1_style {
  color:blue; /* �բͧ����ѡ�� */
  background-color:white; /* �վ�鹢ͧ���� */
  font:14pt Tahoma; /* Ẻ�ѡ�� */
  padding:3px; /* ��ͧ��ҧ�����ҧ��ͺ�Ѻ����ѡ�� */
}
</style>

<script>
window.onload  = function(){
	//alert("onload:"+document.getElementsByName("currentTab")[0].value);
	switchTabOnload(document.getElementsByName("currentTab")[0].value);
}

function switchTabOnload(currentTab){
	//alert("onload:"+currentTab);

	if(currentTab =='tab_config_info' || currentTab ==''){
		//alert('tab_config_info');
		document.getElementById("div_config_info").style.visibility = 'visible';
		document.getElementById("div_query").style.visibility = 'hidden';
		document.getElementById("div_execute").style.visibility = 'hidden';
		document.getElementById("div_backupdb").style.visibility = 'hidden';
		document.getElementById("div_cleardb").style.visibility = "hidden";
		document.getElementById("div_clearcust_dup").style.visibility = "hidden";
		document.getElementById("div_add_db").style.visibility = "hidden";
		
		document.getElementById("id_config_info").className = "tab_selected_style";
	}
	if(currentTab =='tab_execute'){
		//alert('tab_execute');
		document.getElementById("div_config_info").style.visibility = 'hidden';
		document.getElementById("div_query").style.visibility = 'hidden';
		document.getElementById("div_execute").style.visibility = 'visible';
		document.getElementById("div_backupdb").style.visibility = 'hidden';
		document.getElementById("div_cleardb").style.visibility = "hidden";
		document.getElementById("div_clearcust_dup").style.visibility = "hidden";
		document.getElementById("div_add_db").style.visibility = "hidden";
		
		document.getElementById("id_execute").className = "tab_selected_style";
		
	}
	if(currentTab =='tab_query'){
		//alert('tab_query');
		document.getElementById("div_config_info").style.visibility = 'hidden';
		document.getElementById("div_query").style.visibility = 'visible';
		document.getElementById("div_execute").style.visibility = 'hidden';
		document.getElementById("div_backupdb").style.visibility = 'hidden';
		document.getElementById("div_cleardb").style.visibility = "hidden";
		document.getElementById("div_clearcust_dup").style.visibility = "hidden";
		document.getElementById("div_add_db").style.visibility = "hidden";
		
		document.getElementById("id_query").className = "tab_selected_style";
	}
	if(currentTab =='tab_backupdb'){
		//alert('tab_backupdb');
		document.getElementById("div_config_info").style.visibility = 'hidden';
		document.getElementById("div_query").style.visibility = 'hidden';
		document.getElementById("div_execute").style.visibility = 'hidden';
		document.getElementById("div_cleardb").style.visibility = "hidden";
		document.getElementById("div_clearcust_dup").style.visibility = "hidden";
		document.getElementById("div_add_db").style.visibility = "hidden";
		
		document.getElementById("div_backupdb").style.visibility = 'visible';
		document.getElementById("id_backupdb").className = "tab_selected_style";
	}
	
	if(currentTab =='tab_cleardb'){
		//alert('tab_backupdb');
		document.getElementById("div_config_info").style.visibility = 'hidden';
		document.getElementById("div_query").style.visibility = 'hidden';
		document.getElementById("div_execute").style.visibility = 'hidden';
		document.getElementById("div_backupdb").style.visibility = 'hidden';
		document.getElementById("div_clearcust_dup").style.visibility = "hidden";
		document.getElementById("div_add_db").style.visibility = "hidden";
		
		document.getElementById("div_cleardb").style.visibility = "visible";
		document.getElementById("id_cleardb").className = "tab_selected_style";
	}
	if(currentTab =='tab_clearcust_dup'){
		//alert(currentTab);
		document.getElementById("div_config_info").style.visibility = 'hidden';
		document.getElementById("div_query").style.visibility = 'hidden';
		document.getElementById("div_execute").style.visibility = 'hidden';
		document.getElementById("div_backupdb").style.visibility = 'hidden';
		document.getElementById("div_cleardb").style.visibility = "hidden";
		document.getElementById("div_add_db").style.visibility = "hidden";
		
		document.getElementById("div_clearcust_dup").style.visibility = "visible";
		document.getElementById("id_clearcust_dup").className = "tab_selected_style";
	}
	if(currentTab =='tab_add_db'){
		//alert('tab_backupdb');
		document.getElementById("div_config_info").style.visibility = 'hidden';
		document.getElementById("div_query").style.visibility = 'hidden';
		document.getElementById("div_execute").style.visibility = 'hidden';
		document.getElementById("div_cleardb").style.visibility = "hidden";
		document.getElementById("div_clearcust_dup").style.visibility = "hidden";
		document.getElementById("div_backupdb").style.visibility = 'hidden';
		
		document.getElementById("div_add_db").style.visibility = "visible";
		document.getElementById("id_add_db").className = "tab_selected_style";
	}
}

function switchTab(path,currentTab){
	document.getElementById("div_msg").style.visibility = "visible";
	
	//alert(currentTab);
	document.getElementsByName("currentTab")[0].value = currentTab;
	var queryStr ="&curentTab="+currentTab;
	
	document.adminConsoleForm.action = path + "/jsp/adminConsole.do?do=process"+queryStr;
    document.adminConsoleForm.submit();
}

function submitBT(path){
	var currentTab  = document.getElementsByName("currentTab")[0].value;
	var queryStr ="&curentTab="+currentTab;
	
	if(currentTab =='tab_config_info'){
		queryStr +="&action=tab_config_info";
	}
	if(currentTab =='tab_execute'){
		queryStr +="&action=tab_execute";
		//queryStr +="&eSQL="+document.getElementsByName("eSQL")[0].value;
	}
	if(currentTab =='tab_query'){
		queryStr +="&action=tab_query";
		//queryStr +="&q1="+document.getElementsByName("q1")[0].value;
		//queryStr +="&q2="+document.getElementsByName("q2")[0].value;
	}
	if(currentTab =='tab_backupdb'){
		queryStr +="&action=tab_backupdb";
	}
	
	if(currentTab =='tab_cleardb'){
		queryStr +="&action=tab_cleardb";
	}
	if(currentTab =='tab_clearcust_dup'){
		queryStr +="&action=tab_clearcust_dup";
	}
	if(currentTab =='tab_add_db'){
		queryStr +="&action=tab_add_db";
	}
	//alert("quertStr:"+queryStr);
	
	document.adminConsoleForm.action = path + "/jsp/adminConsole.do?do=process"+queryStr;
    document.adminConsoleForm.submit();
}
function addSlqToeSQL(sqlUtils){
	document.getElementById("eSQL").value = sqlUtils.value;
}
</script>

</head>
<%
	String currentTab = Utils.isNull(request.getParameter("currentTab"));
	if("".equals(currentTab)){
		currentTab = Utils.isNull(request.getAttribute("currentTab"));
	}
%>
<body>

<html:form action="/jsp/adminConsole">
    <INPUT TYPE="hidden" name ="currentTab" VALUE="<%=currentTab%>"> 
    
    <INPUT TYPE="button" class="tab_style" id ="id_config_info" name ="tab_config_info" VALUE="Configuration Info" onclick ="switchTab('<%=request.getContextPath()%>','tab_config_info')" />
	<INPUT TYPE="button" class="tab_style" id ="id_query" name ="tab_query" VALUE="Query DB" onclick ="switchTab('<%=request.getContextPath()%>','tab_query')"> 
	<INPUT TYPE="button" class="tab_style" id ="id_execute" name ="tab_execute"  VALUE="Execute DB" onclick ="switchTab('<%=request.getContextPath()%>','tab_execute')">
	<INPUT TYPE="button" class="tab_style" id ="id_backupdb" name ="tab_backupdb"  VALUE="BackUp DB" onclick ="switchTab('<%=request.getContextPath()%>','tab_backupdb')">
	<INPUT TYPE="button" class="tab_style" id ="id_cleardb" name ="tab_cleardb"  VALUE="Clear DB" onclick ="switchTab('<%=request.getContextPath()%>','tab_cleardb')">
	<INPUT TYPE="button" class="tab_style" id ="id_clearcust_dup" name ="tab_clearcust_dup"  VALUE="Clear Dup Address" onclick ="switchTab('<%=request.getContextPath()%>','tab_clearcust_dup')">
	<INPUT TYPE="button" class="tab_style" id ="id_add_db" name ="tab_add_db"  VALUE="Add New DB" onclick ="switchTab('<%=request.getContextPath()%>','tab_add_db')">
	
    <div id="div_msg" style="display:none"> 
		    <br/><br/>
		  <font size="3" color="red">��س����ѡ���� ���ѧ����¡������................ </font>  
    </div>

	  <div id="div_config_info"  style="position: absolute; left: 5px; top: 60px;width:100%;align:left;" >
		  <BR><span class="h1_style">Configuration Tab </span><br> 
		       <html:textarea property="configInfo" style=" width :100%;" rows="14"/>
		     <br><br>
		   <INPUT TYPE="button" class="button2_style" name ="B_TEST_INFO" VALUE="Submit Test Connection" onclick="submitBT('<%=request.getContextPath()%>');"><br><br>
		   
		   <span class="text_style">Result Test Connection </span><br>
		   <html:textarea property="configInfoTest" style=" width :100%;" rows="16"/>
	  </div>
	  
	  
	   <div id="div_query"  style="position: absolute; left: 5px; top: 60px;width:100%;align:left;" >
	      <BR>
		   <span class="h1_style"> Query Tab </span> :
	       <INPUT TYPE="button" class="button2_style" name ="B_QUERY" VALUE="Submit Query DB" onclick="submitBT('<%=request.getContextPath()%>');">
	        <BR><br>
		    Please enter your text SQL 1:<BR>
		 
		   <html:textarea property="q1" style=" width :100%;" rows="8"/>
		  <BR><BR>
		  //-- Result SQL 1--
		  <BR>${adminConsoleForm.resultQ1}
		  <br>
		  Please enter your text SQL 2:
		  <BR>
		  <html:textarea property="q2" style=" width :100%;" rows="8"/>
		  <BR>
		  //-- Result SQL 2--
		  <BR>${adminConsoleForm.resultQ2}
	  </div>
	  
	  
	   <div id="div_execute" style="position: absolute; left: 5px; top: 60px;width:100%;align:left;" >
	      <BR>
		     <span class="h1_style">Execute Tab </span> :
	         <INPUT TYPE="button" class="button2_style" name ="B_EXECUTE"  VALUE="Submit Execute DB" onclick="submitBT('<%=request.getContextPath()%>');">
		     &nbsp; &nbsp;<b>���͡ SQl ��������:</b>
		     <select id="sqlUtils" onchange="addSlqToeSQL(this)">
			  <option value=""></option>
			  <option value="delete from monitor; delete from monitor_item;delete from monitor_item_detail;">ClearMonitorImportExport</option>
			  <option value="delete from t_temp_import_trans;">ClearTempImport</option>
			  <option value="delete from c_control_run_script_db;">ClearControlRunScriptDB</option>
			</select>
		  <BR>
		    Please enter your text SQL To Execute:
		  <BR>
		   <html:textarea property="eSQL" styleId="eSQL" style=" width :100%;" rows="12"/>
		  <BR>
		  <BR>
		   -- Result Execute SQL --
		  <BR>
		   <html:textarea property="eOutput" style=" width :100%;" rows="14"/>
		  <br>
	  </div>
	  
	  <div id="div_backupdb" style="position: absolute; left: 5px; top: 60px;width:100%;align:left;" >
		  <BR>
		  <span class="h1_style"> Backup DB Tab </span> :
		   <INPUT class="button2_style" TYPE="button" name ="B_BackUpDB"  VALUE="Submit Backup DB" onclick="submitBT('<%=request.getContextPath()%>');">
		  <BR>
		   <html:textarea property="resultBKDB" style=" width :100%;" rows="40"/>
		  <br>
	  </div>
	  
	   <div id="div_cleardb" style="position: absolute; left: 5px; top: 60px;width:100%;align:left;" >
		  <BR>
		  <span class="h1_style"> Clear DB Tab </span> :
		   <INPUT class="button2_style" TYPE="button" name ="B_ClearDB"  VALUE="Submit Clear DB" onclick="submitBT('<%=request.getContextPath()%>');">
		  <BR>
		   <html:textarea property="resultClearDB" style=" width :100%;" rows="40"/>
		  <br>
	  </div>
	  <div id="div_clearcust_dup" style="position: absolute; left: 5px; top: 60px;width:100%;align:left;" >
		  <BR>
		  <span class="h1_style"> Clear Duplicate Address </span> :
		   <INPUT class="button2_style" TYPE="button" name ="B_Clearcust_dup"  VALUE="Submit Clear Duplicate Address" onclick="submitBT('<%=request.getContextPath()%>');">
		  <BR>
		   <html:textarea property="resultClearCustDup" style=" width :100%;" rows="40"/>
		  <br>
	  </div>
      <div id="div_add_db" style="position: absolute; left: 5px; top: 60px;width:100%;align:left;" >
		  <BR>
		  <span class="h1_style">Add New Script DB </span> :
		   <INPUT class="button2_style" TYPE="button" name ="B_AddNewDB"  
		   VALUE="Submit" onclick="submitBT('<%=request.getContextPath()%>');">
		  <BR>
		   <html:textarea property="resultAddDB" style=" width :100%;" rows="40"/>
		  <br>
	  </div>
</html:form>
</body>
</html>