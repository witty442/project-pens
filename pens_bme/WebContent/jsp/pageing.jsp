<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.pens.util.Utils"%>
<% 
	   int totalPage = Utils.convertStrToInt(request.getParameter("totalPage"));
	   int totalRecord = Utils.convertStrToInt(request.getParameter("totalRecord"));
	   int currPage =  Utils.convertStrToInt(request.getParameter("currPage"));
	   int startRec = Utils.convertStrToInt(request.getParameter("startRec"));
	   int endRec = Utils.convertStrToInt(request.getParameter("endRec"));
	%>
	<div align="left">
	   <span class="pagebanner">รายการทั้งหมด  <%=totalRecord %> รายการ, แสดงรายการที่  <%=startRec %> ถึง  <%=endRec %>.</span>
	   <span class="pagelinks">
		หน้าที่ 
		 <% 
			 for(int r=0;r<totalPage;r++){
				 if(currPage ==(r+1)){
			 %>
				   <strong><%=(r+1) %></strong>,
			 <%}else{ %>
			    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  
			       title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>, 
		 <% }} %>				
		</span>
	</div>
					