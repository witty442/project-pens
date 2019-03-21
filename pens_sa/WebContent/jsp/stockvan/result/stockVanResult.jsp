
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}
System.out.println("screenWidth:"+screenWidth);
%>
<style type="text/css">
 #scroll {
<%if(!"0".equals(screenWidth)){%>
    width:<%=screenWidth%>px; 

	border:1px solid #000;
	overflow:auto;
	white-space:nowrap;
	box-shadow:0 0 25px #000;
<%}%>
</style>

<%
  if(request.getAttribute("RESULT_DATA") != null){
	  out.print("<div id ='scroll'>");
	  out.print(((StringBuffer)request.getAttribute("RESULT_DATA")).toString());
	  out.print("</div>");
  }
%>

	