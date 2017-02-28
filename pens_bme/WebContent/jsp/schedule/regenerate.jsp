<%@ page language="java" contentType="text/html; charset=Windows-874"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="th.co.gosoft.storeacct.bp.web.common.WebConstants"%>

<html>
<head>
<title>Regenerate Task Form</title>
<meta http-equiv="Content-Type" content="text/html; charset=Windows-874">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/css/global.css">
<script language="JavaScript"
	src="<%=request.getContextPath()%>/javascripts/lw_layers.js"
	type="text/javascript"></script>
<script language="JavaScript"
	src="<%=request.getContextPath()%>/javascripts/lw_menu.js"
	type="text/javascript"></script>
<script language="JavaScript"
	src="<%=request.getContextPath()%>/javascripts/popcalendar.js"
	type="text/javascript"></script>
<script language="javascript">
	function ccc(){		
		document.regenForm.action = '<%=request.getContextPath()%>/countTask.do';		
	}
	function aaa(){		
		document.regenForm.action = '<%=request.getContextPath()%>/viewRegenForm.do';		
	}
</script>
</head>
<body>
<html:form method="post" action="regenerateMore.do">	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10">			
			</td>
		</tr>
	</table>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td align="center" valign="top">
			<table width="780" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" valign="top"><!-- Job Detail area -->
					<table width="100%" border="0" cellspacing="0" cellpadding="0"
						bgcolor="#E8E8E8">
						<tr>
							<td width="34%" height="24" align="left" valign="top">
							<table width="23" height="24" border="0" cellspacing="0"
								cellpadding="0"
								background="<%=request.getContextPath()%>/img/topLeftConner.gif">
								<tr>
									<td></td>
								</tr>
							</table>							</td>
							<td width="1%"></td>
							<td width="65%" align="right" valign="top">
							<table width="23" height="24" border="0" cellspacing="0"
								cellpadding="0"
								background="<%=request.getContextPath()%>/img/topRightConner.gif">
								<tr>
									<td></td>
								</tr>
							</table>							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td><strong>Regenerate (Batch Only) </strong></td>
						</tr>
						<tr>
							<td height="10"></td>
							<td></td>
							<td></td>
						</tr>
						<tr>
						  <td align="right">Create Date From</td>
							<td>&nbsp;</td>
							<td><html:text name="regenForm" property="fromDate" style="width: 80" readonly="true"></html:text>
                                <script language="javascript">
						if (!document.layers) {
								document.write("<span style=\"cursor: hand\"><img style=\"vertical-align:text-bottom\" src=\"img/calendar.gif\" 	onclick='popUpCalendar(this, regenForm.fromDate, \"dd/mm/yyyy\")' alt='Calendar'></span>");
						}						
					          </script>
                                <a href="#" onClick="javascript: regenForm.fromDate.value='';">Clear</a> To&nbsp;
							  <html:text name="regenForm" property="toDate" style="width: 80" readonly="true"></html:text>
                                <script language="javascript">
						if (!document.layers) {
								document.write("<span style=\"cursor: hand\"><img style=\"vertical-align:text-bottom\" src=\"img/calendar.gif\" 	onclick='popUpCalendar(this, regenForm.toDate, \"dd/mm/yyyy\")' alt='Calendar'></span>");
						}						
					          </script>
                              <a href="#" onClick="javascript: regenForm.toDate.value='';">Clear</a> </td>
						</tr>
						<tr>
						  <td align="right">Program ID </td>
					      <td>&nbsp;</td>
					      <td><html:text name="regenForm" property="programId" style="width: 80"></html:text></td>
					  </tr>
						<tr>
						  <td align="right" valign="top">Module</td>
						  <td>&nbsp;</td>
					      <td><html:select name="regenForm" property="module" multiple="true" size="4">
                              <html:option value="CR">CR</html:option>
                              <html:option value="RL">RL</html:option>
                              <html:option value="SA">SA</html:option>
							  <html:option value="CO">CO</html:option>
                          </html:select></td>
					  </tr>
						<tr>
						  <td align="right" valign="top">Status</td>
						  <td>&nbsp;</td>
					      <td><html:select name="regenForm" property="status" multiple="true" size="4">
						  	  <html:option value="Completed">Completed</html:option>
                              <html:option value="Scheduled">Scheduled</html:option>
                              <html:option value="Running">Running</html:option>
                              <html:option value="Failed">Failed</html:option>
                          </html:select></td>
					  </tr>
					  <tr>
						  <td align="right" valign="top">&nbsp;</td>
					      <td>&nbsp;</td>
					      <td>&nbsp;</td>
					  </tr>
						<tr>
						  <td align="right" valign="top">&nbsp;</td>
					      <td>&nbsp;</td>
					      <td><html:submit property="resetForm" value="Reset Form" onclick="aaa();"/>&nbsp;&nbsp;<html:submit property="countTask" value="Count Task" onclick="ccc();" />&nbsp;&nbsp;<html:submit property="regenerate" value="Regenerate" /></td>
					  </tr>
						<tr>
						  <td align="right" valign="top" height="10"></td>
					      <td></td>
					      <td></td>
					  </tr>
						<tr>
						  <td align="right" valign="top">&nbsp;</td>
					      <td>&nbsp;</td>
					      <td><div style="color:#006600; font-size:16px;">
				<logic:present name="regenForm" property="message">
					 <bean:write name="regenForm" property="message"/>
				</logic:present>
				<logic:present name="regenForm" property="totalTask">					
					Total <bean:write name="regenForm" property="totalTask"/> task(s)
				</logic:present>
			</div></td>
					  </tr>						
						<tr>
							<td height="24" align="left" valign="bottom">
							<table width="23" height="24" border="0" cellspacing="0"
								cellpadding="0"
								background="<%=request.getContextPath()%>/img/bottomLeftConner.gif">
								<tr>
									<td></td>
								</tr>
							</table>							</td>
							<td></td>
							<td align="right" valign="bottom">
							<table width="23" height="24" border="0" cellspacing="0"
								cellpadding="0"
								background="<%=request.getContextPath()%>/img/bottomRightConner.gif">
								<tr>
									<td></td>
								</tr>
							</table>							</td>
						</tr>
					</table>
					<br>
					<!-- Parameters Area -->  <!-- Scheduling Area --></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>	
</html:form>
</body>	
</html>
