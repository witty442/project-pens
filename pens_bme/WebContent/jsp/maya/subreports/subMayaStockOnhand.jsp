<%@page import="com.isecinc.pens.web.maya.MayaBean"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
 <jsp:useBean id="mayaForm" class="com.isecinc.pens.web.maya.MayaForm" scope="session" /> 
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
  List<MayaBean> dataList = mayaForm.getResults();
  if(dataList != null && dataList.size() >0){
    totalPage = String.valueOf((dataList.size()/ 50)+1);
  } 
%>
	<c:if test="${mayaForm.results != null}">
	    <br/>
		<display:table style="width:100%;" id="item" name="sessionScope.mayaForm.results" defaultsort="0"  defaultorder="descending" class="resultDisp"
		    requestURI="#" sort="list" pagesize="50">	
		    <display:column  title="Sales Date" property="orderDate" sortable="false" class="td_text_center" style="width:8%"/>
		    <display:column  title="Order No" property="orderNo"  sortable="false" class="td_text_center" style="width:8%"/>
		    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="td_text" style="width:6%"/>
		    <display:column  title="Barcode" property="barcode"  sortable="false" class="td_text_center" style="width:10%"/>	
		    <display:column  title="Style" property="style"  sortable="false" class="td_text_center" style="width:8%"/>	
		    <display:column  title="Qty" property="qty"  sortable="false" class="td_number" style="width:8%"/>
		    <display:column  title="Free Item" property="freeItem"  sortable="false" class="td_text_center" style="width:5%"/>	
		    <display:column  title="Unit Price" property="unitPrice"  sortable="false" class="td_number" style="width:8%"/>		
		    <display:column  title="Line Amount" property="lineAmount"  sortable="false" class="td_number" style="width:8%"/>
		    <display:column  title="Discount " property="discount"  sortable="false" class="td_number" style="width:8%"/>	
		    <display:column  title="Vat Amount " property="vatAmount"  sortable="false" class="td_number" style="width:8%"/>	
		    <display:column  title="Total Line Amount " property="totalAmount"  sortable="false" class="td_number" style="width:8%"/>	
		     <%if(currentPage.equalsIgnoreCase(totalPage)){ %>
		    <display:footer>
		      <%-- <tr class="text_blod">
		          <td colspan="4" align="right"><b>รวม</b></td>
		          <td class="td_number"><bean:write name="summary" property="initSaleQty"/></td>
		          <td class="td_number"><bean:write name="summary" property="saleInQty"/></td>
		          <td class="td_number"><bean:write name="summary" property="saleOutQty"/></td>
		          <td class="td_number"><bean:write name="summary" property="saleReturnQty"/></td>
		          <td class="td_number"><bean:write name="summary" property="onhandQty"/></td>
		      </tr> --%>
		    </display:footer>	
		    <%} %>
		</display:table>
   </c:if>
