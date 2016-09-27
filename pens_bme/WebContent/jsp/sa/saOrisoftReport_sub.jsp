<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.SAReportBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
 if(session.getAttribute("SA_ORISOFT_REPORT_LIST") != null){
   List<SAReportBean> itemList = (List)session.getAttribute("SA_ORISOFT_REPORT_LIST");
%>
		<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
		       <tr>
		            <th >Employee ID</th><!-- 0 -->
		            <th >Name</th><!-- 1 -->
		            <th >SurName</th><!-- 1 -->
		            <th >Group Store</th><!-- 8 -->
		            <th >Branch</th><!-- 9 -->
					<th >as Of Month</th><!-- 7 -->
					<th >Surety Bond จากเงินเดือน</th><!-- 9 -->
					<th >ค่าเฝ้าตู้</th><!-- 9 -->
					<th >ค่าเสียหายหักจากเงินเดือน</th><!-- 9 -->
					<th >หัก Surety Bond ของบริษัท</th><!-- 9 -->
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
						<td class="td_text" width="15%"><%=mc.getName()%></td><!-- 3 -->
					    <td class="td_text" width="15%"><%=mc.getSurname()%></td><!-- 3 -->
						<td class="td_text" width="8%"><%=mc.getGroupStore()%></td><!-- 8 -->
						<td class="td_text" width="7%"><%=mc.getBranch()%></td><!-- 9 -->
						<td class="td_text_center" width="7%"><%=mc.getAsOfMonth()%></td><!-- 9 -->
						<td class="td_text_right" width="7%"><%=mc.getSuretyBondAmt()%></td><!-- 9 -->
						<td class="td_text_right" width="7%"><%=mc.getRewardAmt()%></td><!-- 9 -->
						<td class="td_text_right" width="7%"><%=mc.getNetDamageAmt()%></td><!-- 9 --> 
						<td class="td_text_right" width="7%"><%=mc.getNetSuretyBondAmt()%></td><!-- 9 -->
					</tr>
			<%} %>
			 
	</table>
<%} %>
