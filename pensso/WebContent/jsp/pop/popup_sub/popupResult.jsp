<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.web.popup.PopupHelper"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="session" />
<%
String selectone = Utils.isNull(request.getParameter("selectone"));
User user = (User)session.getAttribute("user");
String pageName = popupForm.getPageName();
String headName ="";
String codeSearchTxtName = "";
String descSearchTxtName = "";
String[] headTextArr = PopupHelper.genHeadTextPopup(pageName);//Gen By PageName Search
headName = headTextArr[0];
codeSearchTxtName = headTextArr[1];
descSearchTxtName = headTextArr[2];
List<PopupBean> dataList = (List) session.getAttribute("DATA_LIST");
%>
<%
	 int pageSize = 20;
	 int totalPage = Utils.calcTotalPage(dataList.size(), pageSize);
	 int totalRecord = dataList.size();
	 int currPage = 1;
	 if( !Utils.isNull(request.getParameter("currPage")).equals("")){
		 currPage = Utils.convertStrToInt(request.getParameter("currPage"));
	 }
	 int startRec = ((currPage-1)*pageSize);
	 int endRec = (currPage * pageSize);
     if(endRec > totalRecord){
	   endRec = totalRecord;
     }
	 int no = Utils.calcStartNoInPage(currPage, pageSize);
	 
	 System.out.println("currPage:"+currPage);
	 System.out.println("startRec:"+startRec);
	 System.out.println("endRec:"+endRec);
	%>
	 
	<%=PageingGenerate.genPageing(user, totalPage, totalRecord, currPage, startRec, endRec, no) %>

<!-- NEW VERSION -->
<div class="container">
   <!-- Head Table -->
   <div class="row mb-1">
     <div class="col-2 themed-grid-col">เลือกข้อมูล</div>
     <div class="col-5 themed-grid-col"><%=codeSearchTxtName %></div>
     <div class="col-5 themed-grid-col"><%=descSearchTxtName %></div>
  </div>
<% 
	 for(int i=startRec;i<endRec;i++){
		 PopupBean item = dataList.get(i);
	%>
	   <div class="row mb-1">
	     <div class="col-2 themed-grid-col-detail" align="center">
	       <%if("false".equalsIgnoreCase(selectone)) {%>
	          <input type ="checkbox" name="chCheck" id="chCheck" onclick="saveSelectedInPage(<%=item.getNo()%>)"  />
		   <%}else{ %>
		      <input type ="radio" name="chCheck" onclick="saveSelectedInPage(<%=item.getNo()%>)"  />
		   <%} %>
			<input type ="hidden" name="code_temp" value="<%=item.getCode()%>" />
			<input type ="hidden" name="desc" value="<%=item.getDesc() %>" />
			<input type ="hidden" name="desc2" value="<%=item.getDesc2() %>" />
			<input type ="hidden" name="desc3" value="<%=item.getDesc3() %>" />
	     </div>
	     <div class="col-5 themed-grid-col-detail"><%=item.getCode()%></div>
	     <div class="col-5 themed-grid-col-detail"><%=item.getDesc() %></div>
	   </div>
	
	<%}//for %>
</div>