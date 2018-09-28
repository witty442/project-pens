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
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

 <jsp:useBean id="summaryForm" class="com.isecinc.pens.web.summary.SummaryForm" scope="session" /> 
<%
  //get d-xxx-d parameter d-49489-p=16
  String queryStr= request.getQueryString();
if(queryStr.indexOf("d-") != -1){
	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
	System.out.println("queryStr:"+queryStr);
}

  String currentPage = Utils.isNull(request.getParameter(queryStr)).equals("")?"1":Utils.isNull(request.getParameter(queryStr));
  String totalPage = "";
  
  List<OnhandSummary> dataList = summaryForm.getResults();
  if(dataList != null && dataList.size() >0){
    totalPage = String.valueOf(Utils.calcTotalPage(dataList.size(), 50));
    //String.valueOf((dataList.size()/ 50)+1);
  } 
  System.out.println("currentPage:"+currentPage);
  System.out.println("totalPage:"+totalPage);
%>
<html>
<head>
</head>
     <c:if test="${summaryForm.page == 'onhandBigC'}">
		<c:if test="${summaryForm.results != null}">
		    <br/>
			<display:table style="width:100%;" id="item" name="sessionScope.summaryForm.results" defaultsort="0"  defaultorder="descending" class="resultDisp"
			    requestURI="#" sort="list" pagesize="50">	
			    
			    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="td_text_center" style="width:8%"/>
			    <display:column  title="Sub Inv" property="subInv"  sortable="false" class="td_text_center" style="width:8%"/>	
			    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="false" class="td_text" style="width:10%"/>	
			    <display:column  title="Group" property="group"  sortable="false" class="td_text_center" style="width:8%"/>	
			    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="td_text_center" style="width:6%"/>	
			    <display:column  title="Initial Qty" property="initSaleQty"  sortable="false" class="td_number" style="width:8%"/>
			    <display:column  title="Transfer In Qty" property="transInQty"  sortable="false" class="td_number" style="width:8%"/>	
			    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="td_number" style="width:8%"/>
			    <display:column  title="SaleOut+StockShort Qty" property="saleOutQty"  sortable="false" class="td_number" style="width:8%"/>	
			    <display:column  title="Adjust" property="adjustQty"  sortable="false" class="td_number" style="width:8%"/>	
			  <%--   <display:column  title="Stock short" property="stockShortQty"  sortable="false" class="td_number" style="width:8%"/>	 --%>
			    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="td_number" style="width:8%"/>
			     
			     <%if(currentPage.equalsIgnoreCase(totalPage)){ %>
			    <display:footer>
			      <tr class="text_blod">
			          <td colspan="5" align="right"><b>รวม</b></td>
			          <td class="td_number"><bean:write name="summary" property="initSaleQty"/></td>
			          <td class="td_number"><bean:write name="summary" property="transInQty"/></td>
			          <td class="td_number"><bean:write name="summary" property="saleReturnQty"/></td>
			          <td class="td_number"><bean:write name="summary" property="saleOutQty"/></td>
			          <td class="td_number"><bean:write name="summary" property="adjustQty"/></td>
			        <%--   <td class="td_number"><bean:write name="summary" property="stockShortQty"/></td> --%>
			          <td class="td_number"><bean:write name="summary" property="onhandQty"/></td>
			      </tr>
			    </display:footer>	
			    <%} %>
			</display:table>
			</c:if>
	   </c:if>
</body>
</html>