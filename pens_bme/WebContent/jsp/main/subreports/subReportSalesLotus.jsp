<%@page import="com.isecinc.pens.bean.TransactionSummary"%>
<%@page import="com.pens.util.*"%>
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
     <c:if test="${summaryForm.page == 'lotus'}">
     <c:if test="${summaryForm.resultsTrans != null}">

		<br/>
			<div id ="scroll" >
				<display:table style="width:120%;"  id="item" name="sessionScope.summaryForm.resultsTrans" defaultsort="0" defaultorder="descending"  class="resultDisp"
					    requestURI="#" sort="list" pagesize="50">	
					    
					    <display:column  title="Sales Date" property="salesDate"  sortable="false"  class="td_text" style="width:5%"/>
					    <display:column  title="Pens Cust Code" property="pensCustCode"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="Pens Cust Desc" property="pensCustDesc"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="Store No" property="storeNo"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="Store Name " property="storeName"  sortable="false"  class="td_text" style="width:5%"/>			
					    <display:column  title="STYLE NO" property="styleNo"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="DESCRIPTION" property="description"  sortable="false"  class="td_text" style="width:5%"/>	
					    
					    <display:column  title="QTY" property="qty"  sortable="false"  class="td_text" style="width:5%"/>
					    <display:column  title="Pens Group" property="pensGroup"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="Pens Group Type" property="pensGroupType"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="Sales Year" property="salesYear"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="Sales Month" property="salesMonth"  sortable="false"  class="td_text" style="width:5%"/>			
					    <display:column  title="file Name" property="fileName"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="Vendor" property="vendor"  sortable="false"  class="td_text" style="width:5%"/>
					    <display:column  title="Name" property="name"  sortable="false" style="width:10%;white-space:nowrap" class="name" />	
					    <display:column  title="AP Type" property="apType"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="LEASE VENDOR TYPE" property="leaseVendorType"  sortable="false" class="td_text" style="width:5%"/>			
					    <display:column  title="COL" property="col"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="Size Type" property="sizeType"  sortable="false"  class="td_text" style="width:5%"/>	
					    			
					    <display:column  title="SIZE" property="sizes"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="GROSS SALES" property="grossSales"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="RETURN AMT" property="returnAmt"  sortable="false"  class="td_text" style="width:5%"/>			
					    <display:column  title="NET SALES INCL VAT" property="netSalesInclVat"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="VAT AMT" property="vatAmt"  sortable="false"  class="td_text" style="width:5%"/>		
	
					    <display:column  title="NET SALES EXC VAT" property="netSalesExcVat"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="GP AMOUNT" property="gpAmount"  sortable="false"  class="td_text" style="width:5%"/>			
					    <display:column  title="VAT ON GP AMOUNT" property="vatOnGpAmount"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="GP AMOUNT INCL VAT" property="gpAmountInclVat"  sortable="false"  class="td_text" style="width:5%"/>		
	
					    <display:column  title="AP AMOUNT" property="apAmount"  sortable="false" class="td_text" style="width:5%"/>	
					    <display:column  title="TOTAL VAT AMT" property="totalVatAmt"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="AP AMOUNT INCL VAT" property="apAmountInclVat"  sortable="false"  class="td_text" style="width:5%"/>			
					    <display:column  title="Create date" property="createDate"  sortable="false"  class="td_text" style="width:5%"/>	
					    <display:column  title="Create by" property="createUser"  sortable="false"  class="td_text" style="width:5%"/>	<!-- 33 -->
					    
					    <%if(currentPage.equals(totalPage)){ %>
					    <display:footer>
					      <tr>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%">&nbsp;</td>
						      <td width="5%"><b>Total</b></td>
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
						    </tr>
					    </display:footer>
                        <%} %>
					</display:table>
			 <%if(currentPage.equals(totalPage)){ %>
				<%-- <table width="120%" class="resultDisp">		
				    <tr>
				      <td width="5%">&nbsp;</td>
				      <td width="5%">&nbsp;</td>
				      <td width="5%">&nbsp;</td>
				      <td width="5%">&nbsp;</td>
				      <td width="5%">&nbsp;</td>
				      <td width="5%">&nbsp;</td>
				      <td width="5%">Total</td>
				      <td class="td_number_bold" width="10%"><bean:write name="summaryTrans" property="qty"/> </td>
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
				    </tr>
				</table> --%>
			 <%} %>
			 </div>
		</c:if>
			
 </c:if>
  <%if(currentPage.equals(totalPage)){ %>
	<script>
	  document.getElementById("totalQty").innerHTML = '<%=totalQty%>';
	</script>
<%} %>
</body>
</html>