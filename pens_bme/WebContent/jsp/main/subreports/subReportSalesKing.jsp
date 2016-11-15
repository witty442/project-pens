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
     <c:if test="${summaryForm.page == 'king'}">
     <c:if test="${summaryForm.resultsTrans != null}">

		<br/>
			<div id ="scroll" >
				<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.resultsTrans" defaultsort="0" defaultorder="descending"  class="resultDisp"
					    requestURI="#" sort="list" pagesize="50">	
					    
					          <display:column  title="Sales Date" property="salesDate"  sortable="false" class="king_salesDate" style="width:20%;"/>
							    <display:column  title="Cust Group" property="custGroup"  sortable="false" class="king_custGroup" style="width:10%;"/>
							    <display:column  title="Cust No"  property="storeNo"  sortable="false" class="king_storeNo"/>	
							    <display:column  title="Cust Name"  property="storeName"  sortable="false" class="king_storeName"/>	
							    <display:column  title="Code" property="kingCode" sortable="false" class="king_kingCode" style="width:20%;"/>	
							    <display:column  title="Description" property="kingDescription"  sortable="false" class="kingDescription" style="width:10%;"/>	
							    <display:column  title="Reference" property="kingReference"  sortable="false" class="kingReference"/>	
                                <display:column  title="Unit Price" property="kingUnitPrice"  sortable="false" class="kingUnitPrice"/>	
                                <display:column  title="Unit Cost" property="kingUnitCost"  sortable="false" class="kingUnitCost"/>	
                                <display:column  title="QTY" property="qty"  sortable="false" class="king_qty"/>
                                <display:column  title="Amount" property="kingAmount"  sortable="false" class="kingAmount"/>	
                                <display:column  title="Cost Amount" property="kingCostAmt"  sortable="false" class="kingCostAmt"/>	
							    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="king_pensItem"/>	
							    <display:column  title="Group Code" property="groupCode"  sortable="false" class="king_groupCode"/>	
							    <display:column  title="file Name" property="fileName"  sortable="false" class="king_fileName"/>				
							    <display:column  title="Create date" property="createDate"  sortable="false" class="king_createDate"/>	
							    <display:column  title="Create by" property="createUser"  sortable="false" class="king_createUser"/>		
							    
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