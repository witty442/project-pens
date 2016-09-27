<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.SAReportBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
 if(session.getAttribute("SA_STATEMENT_REPORT_LIST") != null){
   List<SAReportBean> itemList = (List)session.getAttribute("SA_STATEMENT_REPORT_LIST");

%>

		<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
		       <tr>
		            <th >รหัสพนักงาน</th><!-- 0 -->
		            <th >ชื่อ-สกุล</th><!-- 1 -->
					<th >Region</th><!-- 7 -->
					<th >Group Store</th><!-- 8 -->
					<th >Branch</th><!-- 9 -->
					<th >รวมยอดค้างชำระ</th><!-- 9 -->
					<th >พิมพ์รายตัว</th><!-- 9 -->
			   </tr>
			<% 
			String tabclass ="lineE";
			for(int n=0;n<itemList.size();n++){
				SAReportBean mc = (SAReportBean)itemList.get(n);
				if(n%2==0){ 
					tabclass="lineO";
				}
				%>
					<tr class="<%=tabclass%>"> 
						<td class="td_text" width="6%"><%=mc.getEmpId()%></td><!-- 1 -->
						<td class="td_text" width="15%"><%=mc.getFullName()%></td><!-- 3 -->
						<td class="td_text" width="8%"><%=mc.getRegionDesc()%></td><!-- 7 -->
						<td class="td_text" width="8%"><%=mc.getGroupStore()%></td><!-- 8 -->
						<td class="td_text" width="7%"><%=mc.getBranch()%></td><!-- 9 -->
						<td class="td_text_right" width="7%"><%=mc.getTotalDamage()%></td><!-- 9 -->
						 <td class="td_text_center" width="10%"> 
							 <a href="javascript:exportToExcel('${pageContext.request.contextPath}','<%=mc.getEmpId()%>')">พิมพ์</a><!-- 0 -->
						</td>
					</tr>
			<%} %>
			 
	</table>
<%} %>
