<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.SAReportBean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<jsp:useBean id="saReportForm" class="com.isecinc.pens.web.sa.SAReportForm" scope="session" />
<%
 if(session.getAttribute("SA_DEPT_REPORT_LIST") != null){
   List<SAReportBean> itemList = (List)session.getAttribute("SA_DEPT_REPORT_LIST");
%>

	<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
	       <tr>
	            <th >รหัสพนักงาน</th><!-- 0 -->
	            <th >รหัสลูกหนี้ Oracle</th><!-- 7 -->
	            <th >Name</th><!-- 1 -->
			    <th >Surname</th><!-- 1 -->
				<th >Group Store</th><!-- 8 -->
				<th >Branch</th><!-- 9 -->
				<c:if test="${saReportForm.bean.summaryType == 'Summary'}">
					<th >ยอดสะสม ค่าเฝ้าตู้ Bme</th><!-- 9 -->
					<th >ยอดสะสม ค่าเฝ้าตู้ Wacoal</th><!-- 9 -->
				</c:if>
				<c:if test="${saReportForm.bean.summaryType == 'Detail'}">
				   <th >Invoice No</th><!-- 9 -->
				</c:if>
				<th >Invoice Amt</th><!-- 9 -->
				<th >บันทึกค่าความเสียหาย</th><!-- 9 -->
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
					<td class="td_text" width="8%"><%=mc.getOracleRefId()%></td><!-- 7 -->
					<td class="td_text" width="15%"><%=mc.getName()%></td><!-- 3 -->
				    <td class="td_text" width="15%"><%=mc.getSurname()%></td><!-- 3 -->
					<td class="td_text" width="8%"><%=mc.getGroupStore()%></td><!-- 8 -->
					<td class="td_text" width="8%"><%=mc.getBranch()%></td><!-- 9 -->
					
					<c:if test="${saReportForm.bean.summaryType == 'Detail'}">
				        <td class="td_text" width="7%"><%=mc.getInvoiceNo()%></td><!-- 9 -->
				    </c:if>
				     
				     <c:if test="${saReportForm.bean.summaryType == 'Summary'}">
					     <td class="td_text_right" width="7%"><%=mc.getTotalRewardBme()%> 
							<input type="hidden" id="totalRewardBmeTemp" name="totalRewardBmeTemp" value="<%=mc.getTotalRewardBme()%>" />
						</td><!-- 9 -->
						<td class="td_text_right" width="7%"><%=mc.getTotalRewardWacoal()%> 
							<input type="hidden" id="totalRewardWacoalTemp" name="totalRewardWacoalTemp" value="<%=mc.getTotalRewardWacoal()%>" />
						</td>
					 </c:if>
					 
				    <td class="td_text_right" width="7%"><%=mc.getTotalInvoiceAmt()%>
				      <input type="hidden" name="totalInvoiceAmtTemp" value="<%=mc.getTotalInvoiceAmt() %>"/>
				    </td>
					<td class="td_text_right" width="7%"><%=mc.getTotalDamage()%>
					 <input type="hidden" name="totalDamageTemp" value="<%=mc.getTotalDamage() %>"/>
					</td>
					
				</tr>
		<%} %>
		 <tr class="hilight_text"> 
			<td class="td_text" width="6%"></td><!-- 1 -->
			<td class="td_text" width="8%"></td><!-- 7 -->
			<td class="td_text" width="15%"></td><!-- 3 -->
		    <td class="td_text" width="15%"></td><!-- 3 -->
			<td class="td_text" width="8%"></td><!-- 8 -->
			
			<c:if test="${saReportForm.bean.summaryType == 'Summary'}">
			   <td class="td_text_right" width="8%"><b>ยอดรวม</b></td><!-- 9 -->
			   <td class="td_text_right" width="7%"><b><span id="totalRewardBme"></span></b></td>
		       <td class="td_text_right" width="7%"><b><span id="totalRewardWacoal"></span></b></td>
			</c:if>
			<c:if test="${saReportForm.bean.summaryType == 'Detail'}">
		        <td class="td_text" width="7%"></td><!-- 9 -->
		        <td class="td_text_right" width="8%"><b>ยอดรวม</b></td><!-- 9 -->
		     </c:if>
		 
		    <td class="td_text_right" width="7%"><b><span id="totalInvoiceAmt"></span></b></td>
			<td class="td_text_right" width="7%"><b><span id="totalDamage"></span></b></td>
		</tr>

   </table>
 <script>
 sumTotal();
 function sumTotal(){
	   <%if(saReportForm.getBean().getSummaryType().equalsIgnoreCase("summary")){ %>
		    var totalRewardBmeTemp = document.getElementsByName("totalRewardBmeTemp");
			var totalRewardWacoalTemp = document.getElementsByName("totalRewardWacoalTemp");
			var totalRewardBme = 0;
			var totalRewardWacoal = 0;
		<%}%>
		
	    var totalInvoiceAmtTemp = document.getElementsByName("totalInvoiceAmtTemp");
		var totalDamageTemp = document.getElementsByName("totalDamageTemp");
		
		var totalInvoiceAmt = 0;
		var totalDamage = 0;
		
		for(var i=0;i<totalDamageTemp.length;i++){
			<%if(saReportForm.getBean().getSummaryType().equalsIgnoreCase("summary")){ %>
			   totalRewardBme += parseFloat(totalRewardBmeTemp[i].value.replace(/\,/g,''));
			   totalRewardWacoal += parseFloat(totalRewardWacoalTemp[i].value.replace(/\,/g,''));
			<%} %>
			totalInvoiceAmt += parseFloat(totalInvoiceAmtTemp[i].value.replace(/\,/g,''));
			totalDamage += parseFloat(totalDamageTemp[i].value.replace(/\,/g,''));
		}
		<%if(saReportForm.getBean().getSummaryType().equalsIgnoreCase("summary")){ %>
		   document.getElementById("totalRewardBme").innerHTML =addCommas(Number(toFixed(totalRewardBme,2)).toFixed(2));
		   document.getElementById("totalRewardWacoal").innerHTML =addCommas(Number(toFixed(totalRewardWacoal,2)).toFixed(2));
		<%} %>
		document.getElementById("totalInvoiceAmt").innerHTML =addCommas(Number(toFixed(totalInvoiceAmt,2)).toFixed(2));
		document.getElementById("totalDamage").innerHTML =addCommas(Number(toFixed(totalDamage,2)).toFixed(2));
	}
 </script>
<%} %>
