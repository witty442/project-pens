<%@ page language="java" contentType="text/html; charset=windows-874"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html>

<head>
<title>Log</title>
<meta http-equiv="Content-Type" content="text/html; charset=windows-874">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/global.css">
<script language="javascript">
	function downloadLog(taskId){
		var url = "<%=request.getContextPath()%>/downloadLog.do?taskId="+taskId;
		window.open(url,'hiddenFrame','status = 0, height =0, width =0, resizable=0');		
	}
</script>
</head>
<body>
<table width="100%"  border="0" cellspacing="0" cellpadding="10">
  <tr>
    <td height="10"></td>
  </tr>
  <tr>
    <td align="center"><strong>Log of Task Code: <bean:write name="taskCode"/></strong></td>
  </tr>
  <tr>
    <td height="10"></td>
  </tr>
  <tr>
    <td height="2" bgcolor="#E8E8E8"></td>
  </tr>
  <tr>
    <td height="10"></td>
  </tr>
  <tr>
    <td align="left" valign="top">
    	<logic:present name="logDetail">
    		<pre><bean:write name="logDetail"/></pre>
    	</logic:present>
    	<logic:notPresent name="logDetail">
			<table height="50" width="100%"><tr><td align="center" valign="top">Log file not found.</td></tr></table>
    	</logic:notPresent>
	</td>
  </tr>
  <tr>
    <td>			
			<iframe id="hiddenFrame" name="hiddenFrame" frameborder= "0" height="0" width="0"> </iframe>
			<a href="#" onClick="window.close();">Close</a>&nbsp;|
			<a href="javascript:downloadLog(<bean:write name="taskCode"/>);">Download File</a>
	</td>
  </tr>
  <tr>
    <td height="15">			
			
	</td>
  </tr>
</table>
</body>
</html>
