<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.web.itmanage.ITManageBean"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="itManageForm" class="com.isecinc.pens.web.itmanage.ITManageForm" scope="session" />

 <c:if test="${itManageForm.resultsSearch != null}">
             	<% 
   int totalPage = itManageForm.getTotalPage();
   int totalRecord = itManageForm.getTotalRecord();
   int currPage =  itManageForm.getCurrPage();
   int startRec = itManageForm.getStartRec();
   int endRec = itManageForm.getEndRec();
   int no = Utils.calcStartNoInPage(currPage, itManageForm.getPageSize());
%>
   <%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
   
	<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearch">
       <tr>
            <th >รหัสพนักงานขาย</th>
			<th >ชื่อ-นามสกุล</th>
			<th >เบิกคืน</th>
			<th >วันที่ทำรายการ</th>
			<th >แก้ไข /ดู </th>
			<th >Copy</th>
	   </tr>
		<% 
		String tabclass ="lineE";
		List<ITManageBean> resultList = itManageForm.getResultsSearch();
		
		for(int n=0;n<resultList.size();n++){
			ITManageBean mc = (ITManageBean)resultList.get(n);
			if(n%2==0){
				tabclass="lineO";
			}
			%>
			<tr class="<%=tabclass%>">
				<td class="td_text_center" width="10%"><%=mc.getSalesrepCode() %></td>
				<td class="td_text" width="15%"><%=mc.getSalesrepFullName()%></td>
				<td class="td_text_center" width="10%"><%=mc.getDocType().equalsIgnoreCase("Return")?"คืน":"เบิก"%>
				</td>
			    <td class="td_text_center" width="10%"><%=mc.getDocDate() %></td>
			    
				<td class="td_text_center" width="10%">
					 <a href="javascript:openEdit('${pageContext.request.contextPath}','<%=mc.getId()%>')">
					    <b> แก้ไข /ดู </b>
					 </a>
				</td>
				<td class="td_text_center" width="10%">
					 <a href="javascript:openCopy('${pageContext.request.contextPath}','<%=mc.getId()%>')">
					    <b> Copy</b>
					 </a>
				</td>
			</tr>
		<%} %>
		 
</table>
</c:if>
