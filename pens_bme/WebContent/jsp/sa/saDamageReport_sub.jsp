<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.SAReportBean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<jsp:useBean id="saReportForm" class="com.isecinc.pens.web.sa.SAReportForm" scope="session" />
<%

 List<SAReportBean> itemList = null;
 if(session.getAttribute("SA_DAMAGE_REPORT_LIST") != null ){
    itemList = (List)session.getAttribute("SA_DAMAGE_REPORT_LIST");
  }
  
  if(itemList != null && itemList.size() >0){
%>

<c:if test="${saReportForm.bean.summaryType == 'Summary'}">
	<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
	       <tr>
	            <th >Employee ID</th><!-- 0 -->
	            <th >Name</th><!-- 1 -->
			    <th >Surname</th><!-- 1 -->
				<th >Region</th><!-- 8 -->
				<th >Group Store</th><!-- 8 -->
				<th >Branch</th><!-- 9 -->
				<th >ยอดจ่ายค่าเฝ้าตู้ให้ SA</th><!-- 9 -->
				<th >ยอดหักค่าความเสียหาย</th><!-- 9 -->
				<th >ยอดชำระแล้ว ณ วันที่ run report</th><!-- 9 -->
				<th >ยอดค้างชำระแล้ว ณ วันที่ run report</th><!-- 9 -->
		   </tr>
		<% 
		String tabclass ="lineE";
		double totalRewardAmt = 0;
		double totalDamage = 0;
		double totalPayAmt = 0;
		double totalUnPayAmt =0;
		for(int n=0;n<itemList.size();n++){
			SAReportBean mc = (SAReportBean)itemList.get(n);
			totalRewardAmt += Utils.convertStrToDouble(mc.getRewardAmt());
			totalDamage +=Utils.convertStrToDouble(mc.getTotalDamage());
			totalPayAmt +=Utils.convertStrToDouble(mc.getTotalPayment());
			totalUnPayAmt +=Utils.convertStrToDouble(mc.getTotalDelayPayment());
			
			if(n%2==0){ 
				tabclass="lineO";
			}
			%>
				<tr class="<%=tabclass%>" style='mso-number-format:"@"'> 
					<td class="td_text" width="6%" style='mso-number-format:"@"'><%=mc.getEmpId()%></td><!-- 1 -->
					<td class="td_text" width="15%"><%=mc.getName()%></td><!-- 3 -->
				    <td class="td_text" width="15%"><%=mc.getSurname()%></td><!-- 3 -->
				    <td class="td_text" width="8%"><%=mc.getRegionDesc()%></td><!-- 8 -->
					<td class="td_text" width="8%"><%=mc.getGroupStore()%></td><!-- 8 -->
					<td class="td_text" width="8%"><%=mc.getBranch()%></td><!-- 9 -->
                    <td class="td_text_right" width="7%"><%=mc.getRewardAmt()%></td>
				    <td class="td_text_right" width="7%"><%=mc.getTotalDamage()%></td> 
					<td class="td_text_right" width="7%"><%=mc.getTotalPayment()%></td>
					<td class="td_text_right" width="7%"><%=mc.getTotalDelayPayment()%></td>
				</tr>
		<%} %>
		 <tr class="hilight_text"> 
			<td class="td_text" width="6%"></td><!-- 1 -->
			<td class="td_text" width="8%"></td><!-- 7 -->
			<td class="td_text" width="15%"></td><!-- 3 -->
		    <td class="td_text" width="15%"></td><!-- 3 -->
			<td class="td_text" width="8%"></td><!-- 8 -->
		    <td class="td_text_right" width="8%"><b>ยอดรวม</b></td><!-- 9 -->
		    <td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalRewardAmt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalDamage,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalPayAmt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalUnPayAmt,Utils.format_current_2_disgit) %></b></td>
		</tr>

   </table>
  </c:if>
   <c:if test="${saReportForm.bean.summaryType == 'Detail'}">
   <table id="tblProduct" align="center" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
	       <tr>
	            <th rowspan="2">Employee ID</th><!-- 0 -->
	            <th rowspan="2" >Name</th><!-- 1 -->
			    <th rowspan="2" >Surname</th><!-- 1 -->
				<th rowspan="2" >Region</th><!-- 8 -->
				<th rowspan="2" >Group Store</th><!-- 8 -->
				<th rowspan="2" >Branch</th><!-- 9 -->
				<th rowspan="2" >Invoice No</th><!-- 9 -->
				<th rowspan="2" >วันทีเข้าตรวจนับ</th><!-- 9 -->
				<th rowspan="2" >ระยะเวลา</th><!-- 9 -->
				<th rowspan="2" >จำนวนเดือน</th><!-- 9 -->
				<th rowspan="2" >รวมค่าเฝ้าตู้</th><!-- 9 -->
				<th rowspan="2" >รวมความเสียหาย</th><!-- 9 -->
				<th rowspan="2" >ค่าเฝ้าตู้หักความเสียหาย</th><!-- 9 -->
				<th colspan="4">การหักค่าความเสียหาย</th><!-- 9 -->
		   </tr>
		    <tr>
				<th>1.หัก เงินสด</th><!-- 9 -->
				<th>2.หัก ค่าเฝ้าตู้</th><!-- 9 -->
				<th>3.หัก เงินเดือน</th><!-- 9 -->
				<th>4.หัก Surety bond</th><!-- 9 -->
		   </tr>
		<% 
		String tabclass ="lineE";
		boolean printEmpId= true;
		boolean genSummary = false;
		
		double totalRewardAmt = 0;
		double totalDamage = 0;
		double totalNetDamage = 0;
		double totalPayType1Amt =0;
		double totalPayType2Amt =0;
		double totalPayType3Amt =0;
		double totalPayType4Amt =0;
		
		double totalRewardAmtSub = 0;
		double totalDamageSub = 0;
		double totalNetDamageSub = 0;
		double totalPayType1AmtSub =0;
		double totalPayType2AmtSub =0;
		double totalPayType3AmtSub =0;
		double totalPayType4AmtSub =0;
		
		SAReportBean nextBean = null;
		String empIdTemp = "";
		for(int n=0;n<itemList.size();n++){
			SAReportBean mc = (SAReportBean)itemList.get(n);
			
			totalRewardAmt += Utils.convertStrToDouble(mc.getRewardAmt());
			totalDamage +=Utils.convertStrToDouble(mc.getTotalDamage());
			totalNetDamage +=Utils.convertStrToDouble(mc.getNetDamageAmt());
			totalPayType1Amt +=Utils.convertStrToDouble(mc.getPayType1Amt());
			totalPayType2Amt +=Utils.convertStrToDouble(mc.getPayType2Amt());
			totalPayType3Amt +=Utils.convertStrToDouble(mc.getPayType3Amt());
			totalPayType4Amt +=Utils.convertStrToDouble(mc.getPayType4Amt());
			
			totalRewardAmtSub += Utils.convertStrToDouble(mc.getRewardAmt());
			totalDamageSub +=Utils.convertStrToDouble(mc.getTotalDamage());
			totalNetDamageSub +=Utils.convertStrToDouble(mc.getNetDamageAmt());
			totalPayType1AmtSub +=Utils.convertStrToDouble(mc.getPayType1Amt());
			totalPayType2AmtSub +=Utils.convertStrToDouble(mc.getPayType2Amt());
			totalPayType3AmtSub +=Utils.convertStrToDouble(mc.getPayType3Amt());
			totalPayType4AmtSub +=Utils.convertStrToDouble(mc.getPayType4Amt());
			
			printEmpId= false;
			genSummary = false;
			tabclass ="lineE";
			if(n%2==0)
				tabclass="lineO";
			
			
			if(!mc.getEmpId().equalsIgnoreCase(empIdTemp)){
				 printEmpId = true;
			 }else{
				 printEmpId = false;
			 } 
			if(n < itemList.size()-1){
				nextBean = (SAReportBean)itemList.get(n+1);
				//logger.debug("empId["+s.getEmpId()+"]:nextEmpId["+nextBean.getEmpId()+"]");
				
				if(!mc.getEmpId().equalsIgnoreCase(nextBean.getEmpId())){
					genSummary = true;
				}
			}else{
				genSummary = true;
				
			}
			empIdTemp = mc.getEmpId();
			%>
				<tr class="<%=tabclass%>"> 
				    <%if(printEmpId==true){ %>
						<td class="td_text" width="6%" style='mso-number-format:"@"'><%=mc.getEmpId()%></td><!-- 1 -->
						<td class="td_text" width="10%"><%=mc.getName()%></td><!-- 2 -->
					    <td class="td_text" width="10%"><%=mc.getSurname()%></td><!-- 3 -->
					    <td class="td_text" width="5%"><%=mc.getRegionDesc()%></td><!-- 4 -->
						<td class="td_text" width="5%"><%=mc.getGroupStore()%></td><!-- 5 -->
						<td class="td_text" width="6%"><%=mc.getBranch()%></td><!-- 6 -->
					<% }else{ %>
					    <td class="td_text" width="6%"></td><!-- 1 -->
						<td class="td_text" width="10%"></td><!-- 2 -->
					    <td class="td_text" width="10%"></td><!-- 3 -->
					    <td class="td_text" width="5%"></td><!-- 4 -->
						<td class="td_text" width="5%"></td><!-- 5 -->
						<td class="td_text" width="6%"></td><!-- 6 -->
					<%} %>
					<td class="td_text" width="6%"><%=mc.getInvoiceNo()%></td><!-- 7 -->
					<td class="td_text" width="6%"><%=mc.getCountStockDate()%></td><!-- 8 -->
					<td class="td_text" width="8%"><%=mc.getRewardMonth()%></td><!-- 9 -->
					<td class="td_text_center" width="5%"><%=mc.getRewardMonthCount()%></td><!-- 10-->
                    <td class="td_text_right" width="7%"><%=mc.getRewardAmt()%></td>
				    <td class="td_text_right" width="7%"><%=mc.getTotalDamage()%></td> 
					<td class="td_text_right" width="7%"><%=mc.getNetDamageAmt()%></td>
					<td class="td_text_right" width="6%"><%=mc.getPayType1Amt()%></td>
					<td class="td_text_right" width="6%"><%=mc.getPayType2Amt()%></td>
					<td class="td_text_right" width="6%"><%=mc.getPayType3Amt()%></td>
					<td class="td_text_right" width="6%"><%=mc.getPayType4Amt()%></td>
				</tr>
				<%if(genSummary){ %>
				 <tr>
			
					<td class="td_text_right" width="5%" colspan="10" align="right"><b>Sub Total</b></td><!-- 10-->
                    <td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalRewardAmtSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalDamageSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalNetDamageSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType1AmtSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType2AmtSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType3AmtSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType4AmtSub,Utils.format_current_2_disgit) %></b></td>
				 </tr>
				<%
				
					//Reset Summary Sub Total
					 totalRewardAmtSub = 0;
					 totalDamageSub = 0;
					 totalNetDamageSub = 0;
					 totalPayType1AmtSub =0;
					 totalPayType2AmtSub =0;
					 totalPayType3AmtSub =0;
					 totalPayType4AmtSub =0;
				} %>
				
		<%} %>
		 <tr class="hilight_text"> 
		    <td class="td_text_right" width="5%" colspan="10" align="right"><b>Grand Total</b></td><!-- 10 -->
		    <td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalRewardAmt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalDamage,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="7%"><b><%=Utils.decimalFormat(totalNetDamage,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType1Amt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType2Amt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType3Amt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType4Amt,Utils.format_current_2_disgit) %></b></td>
		</tr>
   </table>
   </c:if>
   
   <c:if test="${saReportForm.bean.summaryType == 'Detail2'}">
   <table id="tblProduct" align="center" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
	       <tr>
	            <th rowspan="2">Employee ID</th><!-- 0 -->
	            <th rowspan="2" >Name</th><!-- 1 -->
			    <th rowspan="2" >Surname</th><!-- 1 -->
				<th rowspan="2" >Region</th><!-- 8 -->
				<th rowspan="2" >Group Store</th><!-- 8 -->
				<th rowspan="2" >Branch</th><!-- 9 -->
				<th rowspan="2" >Invoice No</th><!-- 9 -->
				<th rowspan="2" >วันทีเข้าตรวจนับ</th><!-- 9 -->
				<th rowspan="2" >ระยะเวลา</th><!-- 9 -->
				<th rowspan="2" >จำนวนเดือน</th><!-- 9 -->
				<th rowspan="2" >รวมค่าเฝ้าตู้</th><!-- 9 -->
				<th rowspan="2" >รวมความเสียหาย</th><!-- 9 -->
				<th rowspan="2" >ค่าเฝ้าตู้หักความเสียหาย</th><!-- 9 -->
				<th colspan="6">การหักค่าความเสียหาย</th><!-- 9 -->
		   </tr>
		    <tr>
				<th>1.หัก เงินสด</th><!-- 9 -->
				<th>2.หัก ค่าเฝ้าตู้</th><!-- 9 -->
				<th>4.หัก Surety bond</th><!-- 9 -->
				<th>3.หัก เงินเดือน</th><!-- 9 -->
				<th>แสดงวันที่หักเงินเดือน</th>
				<th>ยอดเงินเดือนที่หัก</th>
		   </tr>
		<% 
		String tabclass ="lineE";
		boolean printEmpId= true;
		boolean genSummary = false;
		
		double totalRewardAmt = 0;
		double totalDamage = 0;
		double totalNetDamage = 0;
		double totalPayType1Amt =0;
		double totalPayType2Amt =0;
		double totalPayType3Amt =0;
		double totalPayType4Amt =0;
		
		double totalRewardAmtSub = 0;
		double totalDamageSub = 0;
		double totalNetDamageSub = 0;
		double totalPayType1AmtSub =0;
		double totalPayType2AmtSub =0;
		double totalPayType3AmtSub =0;
		double totalPayType4AmtSub =0;
		
		SAReportBean nextBean = null;
		String empIdTemp = "";
		Map<String,String> dupInvoiceMap = new HashMap<String,String>();
		
		for(int n=0;n<itemList.size();n++){
			SAReportBean mc = (SAReportBean)itemList.get(n);
			
		
			
			printEmpId= false;
			genSummary = false;
			tabclass ="lineE";
			if(n%2==0)
				tabclass="lineO";
			
			
			if(!mc.getEmpId().equalsIgnoreCase(empIdTemp)){
				 printEmpId = true;
			 }else{
				 printEmpId = false;
			 } 
			if(n < itemList.size()-1){
				nextBean = (SAReportBean)itemList.get(n+1);
				//logger.debug("empId["+s.getEmpId()+"]:nextEmpId["+nextBean.getEmpId()+"]");
				
				if(!mc.getEmpId().equalsIgnoreCase(nextBean.getEmpId())){
					genSummary = true;
				}
			}else{
				genSummary = true;
				
			}
			empIdTemp = mc.getEmpId();
			%>
				<tr class="<%=tabclass%>"> 
				    <%if(printEmpId==true){ %>
						<td class="td_text" width="5%" style='mso-number-format:"@"'><%=mc.getEmpId()%></td><!-- 1 -->
						<td class="td_text" width="9%"><%=mc.getName()%></td><!-- 2 -->
					    <td class="td_text" width="9%"><%=mc.getSurname()%></td><!-- 3 -->
					    <td class="td_text" width="5%"><%=mc.getRegionDesc()%></td><!-- 4 -->
						<td class="td_text" width="4%"><%=mc.getGroupStore()%></td><!-- 5 -->
						<td class="td_text" width="6%"><%=mc.getBranch()%></td><!-- 6 -->
					<% }else{ %>
					    <td class="td_text" width="5%"></td><!-- 1 -->
						<td class="td_text" width="9%"></td><!-- 2 -->
					    <td class="td_text" width="9%"></td><!-- 3 -->
					    <td class="td_text" width="5%"></td><!-- 4 -->
						<td class="td_text" width="5%"></td><!-- 5 -->
						<td class="td_text" width="6%"></td><!-- 6 -->
					<%} %>
				<%if(dupInvoiceMap.get(mc.getInvoiceNo()) == null){ 
				   //Sum Only show invoice
				   	totalRewardAmt += Utils.convertStrToDouble(mc.getRewardAmt());
					totalDamage +=Utils.convertStrToDouble(mc.getTotalDamage());
					totalNetDamage +=Utils.convertStrToDouble(mc.getNetDamageAmt());
					totalPayType1Amt +=Utils.convertStrToDouble(mc.getPayType1Amt());
					totalPayType2Amt +=Utils.convertStrToDouble(mc.getPayType2Amt());
					totalPayType3Amt +=Utils.convertStrToDouble(mc.getPayType3Amt());
					totalPayType4Amt +=Utils.convertStrToDouble(mc.getPayType4Amt());
					
					totalRewardAmtSub += Utils.convertStrToDouble(mc.getRewardAmt());
					totalDamageSub +=Utils.convertStrToDouble(mc.getTotalDamage());
					totalNetDamageSub +=Utils.convertStrToDouble(mc.getNetDamageAmt());
					totalPayType1AmtSub +=Utils.convertStrToDouble(mc.getPayType1Amt());
					totalPayType2AmtSub +=Utils.convertStrToDouble(mc.getPayType2Amt());
					totalPayType3AmtSub +=Utils.convertStrToDouble(mc.getPayType3Amt());
					totalPayType4AmtSub +=Utils.convertStrToDouble(mc.getPayType4Amt());
					
					//Put mc.getInvoiceNo() to check dup 
					dupInvoiceMap.put(mc.getInvoiceNo(),mc.getInvoiceNo());
				%>
					<td class="td_text" width="6%" nowrap><%=mc.getInvoiceNo()%></td><!-- 7 -->
					<td class="td_text" width="6%"><%=mc.getCountStockDate()%></td><!-- 8 -->
					<td class="td_text" width="8%" nowrap><%=mc.getRewardMonth()%></td><!-- 9 -->
					<td class="td_text_center" width="5%"><%=mc.getRewardMonthCount()%></td><!-- 10-->
                    <td class="td_text_right" width="6%"><%=mc.getRewardAmt()%></td>
				    <td class="td_text_right" width="6%"><%=mc.getTotalDamage()%></td> 
					<td class="td_text_right" width="6%"><%=mc.getNetDamageAmt()%></td>
					<td class="td_text_right" width="6%"><%=mc.getPayType1Amt()%></td>
					<td class="td_text_right" width="6%"><%=mc.getPayType2Amt()%></td>
					<td class="td_text_right" width="6%"><%=mc.getPayType4Amt()%></td><!--Surety Bound-->
					<td class="td_text_right" width="6%"><%=mc.getPayType3Amt()%></td><!-- ค่าเฝเ้าตู้ -->
				<%}else{ %>
				    <td class="td_text" width="6%"></td><!-- 7 -->
					<td class="td_text" width="6%"></td><!-- 8 -->
					<td class="td_text" width="8%" nowrap></td><!-- 9 -->
					<td class="td_text_center" width="5%"></td><!-- 10-->
                    <td class="td_text_right" width="6%"></td>
				    <td class="td_text_right" width="6%"></td> 
					<td class="td_text_right" width="6%"></td>
					<td class="td_text_right" width="6%"></td>
					<td class="td_text_right" width="6%"></td>
					<td class="td_text_right" width="6%"></td><!--Surety Bound-->
					<td class="td_text_right" width="6%"></td><!-- ค่าเฝเ้าตู้ -->
				<%} %>
					<td class="td_text_right" width="6%"><%=mc.getPayDate()%></td>
					<td class="td_text_right" width="6%"><%=mc.getPayAmt()%></td>
					
				</tr>
				<%if(genSummary){ %>
				 <tr>
			
					<td class="td_text_right" width="5%" colspan="10" align="right"><b>Sub Total</b></td><!-- 10-->
                    <td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalRewardAmtSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalDamageSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalNetDamageSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType1AmtSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType2AmtSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType4AmtSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType3AmtSub,Utils.format_current_2_disgit) %></b></td>
					<td class="td_text_right" width="6%"></td>
					<td class="td_text_right" width="6%"></td>
				 </tr>
				<%
					//Reset Summary Sub Total
					 totalRewardAmtSub = 0;
					 totalDamageSub = 0;
					 totalNetDamageSub = 0;
					 totalPayType1AmtSub =0;
					 totalPayType2AmtSub =0;
					 totalPayType3AmtSub =0;
					 totalPayType4AmtSub =0;
				} %>
				
		<%} %>
		 <tr class="hilight_text"> 
		    <td class="td_text_right" width="5%" colspan="10" align="right"><b>Grand Total</b></td><!-- 10 -->
		    <td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalRewardAmt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalDamage,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalNetDamage,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType1Amt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType2Amt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType4Amt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"><b><%=Utils.decimalFormat(totalPayType3Amt,Utils.format_current_2_disgit) %></b></td>
			<td class="td_text_right" width="6%"></td>
			<td class="td_text_right" width="6%"></td>
		</tr>
   </table>
   </c:if>
<%} %>
