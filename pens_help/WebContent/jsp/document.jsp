<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.pens.util.DBConnectionApps"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.Locale"%>
<%
request.setCharacterEncoding("TIS-620");
response.setCharacterEncoding("TIS-620");

String sessionId = Utils.isNull(request.getParameter("sessionId"));
System.out.println("session Id:"+sessionId );

if( !Utils.isNull(sessionId).equals("")){
 String module = Utils.isNull(request.getParameter("module"));
 String programName = Utils.isNull(request.getParameter("programName"));
 String description = Utils.isNull(request.getParameter("description"));
 String action = Utils.isNull(request.getParameter("action"));
 
 if("save".equalsIgnoreCase(action)){
	 Connection conn = null;
	 PreparedStatement ps = null;
	 StringBuffer sql = new StringBuffer("");
	 try{
		 conn = DBConnectionApps.getInstance().getConnection();
		 sql.append("update pensbi.dev_doc set description = ?, update_date = ? where module =? and program_name =? ");
		 ps = conn.prepareStatement(sql.toString());
		 ps.setString(1, description);
		 ps.setDate(2, new java.sql.Date(new Date().getTime()));
		 ps.setString(3, module);
		 ps.setString(4, programName);
		 
		 if(ps.executeUpdate()==0){
			 sql = new StringBuffer("");
			 sql.append("insert into pensbi.dev_doc(module,program_name,description,update_date)values(?,?,?,?)");
			 ps = conn.prepareStatement(sql.toString());
			 ps.setString(1, module);
			 ps.setString(2, programName);
			 ps.setString(3, description);
			 ps.setDate(4, new java.sql.Date(new Date().getTime()));
			 ps.executeUpdate();
		 }
	 }catch(Exception e){
		 e.printStackTrace();
	 }finally{
		 conn.close();
		 ps.close();
	 }
 }else{
	 //view 
	 Connection conn = null;
	 PreparedStatement ps = null;
	 ResultSet rs = null;
	 StringBuffer sql = new StringBuffer("");
	 try{
		 conn = DBConnectionApps.getInstance().getConnection();
		 sql.append("select * from pensbi.dev_doc  where module =? and program_name =? ");
		 ps = conn.prepareStatement(sql.toString());
		 ps.setString(1, module);
		 ps.setString(2, programName);
		 
		 rs = ps.executeQuery();
		 if(rs.next()){
			module = Utils.isNull(rs.getString("module"));
			programName = Utils.isNull(rs.getString("program_name"));
			description = Utils.isNull(rs.getString("description"));
		 }
	 }catch(Exception e){
		 e.printStackTrace();
	 }finally{
		 conn.close();
		 ps.close();
	 }
 }
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<script>
 function submitForm(){
	 document.docForm.action = '/pens_help/jsp/document.jsp?action=save';
	 document.docForm.submit();
 }
</script>
</head>
<body>
<form name="docForm" method="post">
  <input type="hidden" name="sessionId" value="<%=sessionId%>" />
  <input type="hidden" name="module" value="<%=module%>" />
  <input type="hidden" name="programName" value="<%=programName%>" />

	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
		<tr>
	 		<td colspan="2" align="center"><font size="4">App Version:28/01/2564<b></b></font> </td>
		</tr>
	 	<tr>
	 		<td colspan="2" align="center"><font size="3"><b>Module : <u><%=module %></u></b></font> </td>
		</tr>
		<tr>
	 		<td colspan="2" align="center"><font size="3"><b> ProgramName : <u><%=programName %></u></b></font> </td>
	 	
		</tr>
		<tr>
	 		<td colspan="2" align="center"><font size="3"><b> Document Description </b></font></td>
		</tr>
		<tr>
	 		<td colspan="2" align="center"> 
	 		<textarea rows="30" cols="180" name="description"><%=description %></textarea>
	 		</td>
		</tr>
	</table> 
	<br/>
	<div align="center">
	  <input type="button" name="st" value ="บันทึกข้อมูล" onclick="submitForm()"/>
	  <input type="button" name="close" value ="ปิดหน้าจอนี้" onclick="window.close()"/>
	</div>
</form>
</body>
<%} %>
