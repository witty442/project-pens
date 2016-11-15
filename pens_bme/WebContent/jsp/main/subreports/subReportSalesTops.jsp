<%@page import="com.isecinc.pens.bean.TransactionSummary"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.bean.OnhandSummary"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>

<jsp:useBean id="summaryForm" class="com.isecinc.pens.web.summary.SummaryForm" scope="session" />
<%
String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));

String totalPage = "";
String currentPage = "";
if(summaryForm.getResultsTrans() != null){
	 totalPage = String.valueOf(Utils.calcTotalPage(summaryForm.getResultsTrans().size(),50));
	 
	String queryStr= request.getQueryString();
	if(queryStr.indexOf("d-") != -1){
		queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
		System.out.println("queryStr:"+queryStr);
		currentPage = request.getParameter(queryStr);
	}
}
String totalQty = "";
if(session.getAttribute("summaryTrans") != null){
	TransactionSummary s = (TransactionSummary)session.getAttribute("summaryTrans");
	if(s != null){
		totalQty = s.getQty();
	}
}
/** Case ToatlPage ==1 set currentPage=1 ***/
if(totalPage.equals("1")){
	currentPage ="1";
}
System.out.println("totalPage:"+totalPage);
System.out.println("currentPage:"+currentPage);
%>
<html>
<head>
<style>
#scroll {
    width:<%=screenWidth%>px;

    background:#A3CBE0;
	border:1px solid #000;
	overflow:auto;
	white-space:nowrap;
	box-shadow:0 0 25px #000;
	}
</style>
</head>
     <c:if test="${summaryForm.page == 'Tops'}">
     <c:if test="${summaryForm.resultsTrans != null}">

		<br/>
			<div id ="scroll" >
				<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.resultsTrans" defaultsort="0" defaultorder="descending"  class="resultDisp"
					    requestURI="#" sort="list" pagesize="50">	
					    
					           <display:column  title="Sales Date" property="salesDate"  sortable="false" class="tops_salesDate" style="width:10%;"/>
							    <display:column  title="Pens Cust Code"  property="pensCustCode" sortable="false" class="tops_pensCustCode" style="width:10%;"/>	
							    <display:column  title="Pens Cust Desc" property="pensCustDesc" sortable="false" class="tops_pensCustDesc" style="width:20%;"/>	
							    <display:column  title="Pens Group" property="pensGroup"  sortable="false" class="tops_pensGroup" style="width:10%;"/>	
							    <display:column  title="Pens Group Type" property="pensGroupType"  sortable="false" class="tops_pensGroupType"/>	
                                <display:column  title="Pens Item" property="pensItem"  sortable="false" class="tops_pensItem"/>	
							    <display:column  title="QTY" property="qty"  sortable="false" class="tops_qty"/>
							    <display:column  title="Item" property="item"  sortable="false" class="tops_item"/>	
							    <display:column  title="Item Desc" property="itemDesc"  sortable="false" class="tops_itemDesc"/>	
							    <display:column  title="Branch Name" property="branchName"  sortable="false" class="tops_branchName"/>	
							    <display:column  title="Group No" property="groupNo"  sortable="false" class="tops_groupNo"/>
							    <display:column  title="Group Name" property="groupName"  sortable="false" class="tops_groupName"/>
							    <display:column  title="DEPT" property="dept"  sortable="false" class="tops_dept"/>
							    <display:column  title="Dept Name" property="deptName"  sortable="false" class="tops_deptName"/>
							    <display:column  title="Unit Cost" property="unitCost"  sortable="false" class="tops_unitCost"/>
							    <display:column  title="Retail Price" property="retailPrice"  sortable="false" class="tops_retailPrice"/>
							    <display:column  title="GP PERCENT" property="gpPercent"  sortable="false" class="tops_gpPercent"/>		
							    <display:column  title="NET SALES INCL VAT" property="netSalesInclVat"  sortable="false" class="tops_netSalesInclVat"/>
							    <display:column  title="NET SALES EXC VAT" property="netSalesExcVat"  sortable="false" class="tops_netSalesExcVat"/>
							    <display:column  title="GP AMOUNT" property="gpAmount"  sortable="false" class="tops_gpAmount"/>		
							    <display:column  title="GROSS SALES" property="grossSales"  sortable="false" class="tops_grossSales"/>	
							   	<display:column  title="Discount" property="discount"  sortable="false" class="tops_discount"/>
							   	<display:column  title="CUS RETURN" property="cusReturn"  sortable="false" class="tops_cusReturn"/>
							   	<display:column  title="DISCOUNT CUS RETURN" property="discountCusReturn"  sortable="false" class="tops_discountCusReturn"/>
							   	<display:column  title="NET CUS RETURN" property="netCusReturn"  sortable="false" class="tops_netCusReturn"/>
							   	<display:column  title="COGS" property="cogs"  sortable="false" class="tops_cogs"/>
		
							    <display:column  title="Sales Year" property="salesYear"  sortable="false" class="tops_salesYear"/>	
							    <display:column  title="Sales Month" property="salesMonth"  sortable="false" class="tops_salesMonth"/>			
							    <display:column  title="file Name" property="fileName"  sortable="false" class="tops_fileName"/>				
							    <display:column  title="Create date" property="createDate"  sortable="false" class="tops_createDate"/>	
							    <display:column  title="Create by" property="createUser"  sortable="false" class="tops_createUser"/>	
							    
					    <%if(currentPage.equals(totalPage)){ %>
					    <display:footer>
					      <!-- <tr>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">Total</td>
						      <td class="td_number_bold" width="10%"><span id="totalQty"></span> </td>
						      <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
							  <td width="10%">&nbsp;</td>
						    </tr> -->
					    </display:footer>
                        <%} %>
					</display:table>
			
			 </div>
		</c:if>
			
 </c:if>
  <%if(currentPage.equals(totalPage)){ %>
	<script>
	 // document.getElementById("totalQty").innerHTML = '<%=totalQty%>';
	</script>
<%} %>
</body>
</html>