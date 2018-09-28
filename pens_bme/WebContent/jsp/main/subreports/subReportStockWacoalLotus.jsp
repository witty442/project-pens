<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.OnhandSummary"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
 <jsp:useBean id="summaryForm" class="com.isecinc.pens.web.summary.SummaryForm" scope="session" /> 
<%
  //get d-xxx-d parameter d-49489-p=16
  String queryStr= request.getQueryString();
if(queryStr.indexOf("d-") != -1){
	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
	System.out.println("queryStr:"+queryStr);
}

  String currentPage = Utils.isNull(request.getParameter(queryStr));
  String totalPage = "";
  System.out.println("currentPage:"+currentPage);
  List<OnhandSummary> dataList = summaryForm.getResults();
  if(dataList != null && dataList.size() >0){
    totalPage = String.valueOf((dataList.size()/ 50)+1);
  } 
%>
	<c:if test="${summaryForm.results != null}">
	    <br/>
		<display:table style="width:100%;" id="item" name="sessionScope.summaryForm.results" defaultsort="0"  defaultorder="descending" class="resultDisp"
		    requestURI="#" sort="list" pagesize="50">	
		    <display:column  title="Branch ID" property="storeCode"  sortable="false" class="td_text_center" style="width:10%"/>
		    <display:column  title="Branch Name" property="storeName"  sortable="false" class="td_text" style="width:15%"/>
		    <display:column  title="วันที่เช็คสต๊อกล่าสุด" property="initDate"  sortable="false" class="td_text_center" style="width:8%"/>
		    <display:column  title="Group" property="group"  sortable="false" class="td_text_center" style="width:10%"/>	
		    <display:column  title="Initial Stock" property="initSaleQty"  sortable="false" class="td_number" style="width:8%"/>	
		    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="td_number" style="width:8%"/>
		    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="td_number" style="width:8%"/>		
		    <display:column  title="Return Qty" property="saleReturnQty"  sortable="false" class="td_number" style="width:8%"/>
		    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="td_number" style="width:8%"/>	
		 
		     <%if(currentPage.equalsIgnoreCase(totalPage)){ %>
		    <display:footer>
		      <tr class="text_blod">
		          <td colspan="4" align="right"><b>รวม</b></td>
		          <td class="td_number"><bean:write name="summary" property="initSaleQty"/></td>
		          <td class="td_number"><bean:write name="summary" property="saleInQty"/></td>
		          <td class="td_number"><bean:write name="summary" property="saleOutQty"/></td>
		          <td class="td_number"><bean:write name="summary" property="saleReturnQty"/></td>
		          <td class="td_number"><bean:write name="summary" property="onhandQty"/></td>
		      </tr>
		    </display:footer>	
		    <%} %>
		</display:table>
   </c:if>
