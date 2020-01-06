<%@page import="com.isecinc.pens.web.projectc.ProjectCBean"%>
<%@page import="com.isecinc.pens.web.reportall.page.ProjectCReportAction"%>
<%@page import="com.isecinc.pens.web.projectc.ProjectCImageBean"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String checkDate =Utils.isNull(request.getParameter("checkDate"));
String storeCode =Utils.isNull(request.getParameter("storeCode"));
ProjectCBean cri = new ProjectCBean();
cri.setStoreCode(storeCode);
cri.setCheckDate(checkDate);
List<ProjectCImageBean> imageList = new ProjectCReportAction().searchCheckStockImageList(cri);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>แสดงรูปภาพ Project-C</title>
<style>
img.one {
  height: auto;
  width: auto;
}
</style>
</head>
<body>
<a href="<%=request.getContextPath() %>/jsp/reportAll/page/projectCImageListORI.jsp?storeCode=<%=storeCode%>&checkDate=<%=checkDate%>">
รูปภาพต้นฉบับ(Original)
</a>
<table border="0" align="center">
<%if(imageList != null && imageList.size() >0){
for(int i=0;i<imageList.size();i++){ 
   ProjectCImageBean imageBean = imageList.get(i);
%>
	<tr>
	<td nowrap width="10%">รูปที่ <%=imageBean.getImageId() %></td>
	  <td width="80%"> 
	  <img id="imageDB" 
	   src="${pageContext.request.contextPath}/photoServlet?pageName=ProjectC&fileName=<%=imageBean.getImageName()%>"
	    border="0" width="300" height="200"/>
	  </td>
	</tr>
<%} }%>
</table>

</body>
</html>