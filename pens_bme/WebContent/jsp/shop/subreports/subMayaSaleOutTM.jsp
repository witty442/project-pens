
<%@page import="com.isecinc.pens.web.shop.ShopBean"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<jsp:useBean id="shopForm" class="com.isecinc.pens.web.shop.ShopForm" scope="session" />
<%
User user = (User)session.getAttribute("user");
  //get d-xxx-d parameter d-49489-p=16
  String queryStr= request.getQueryString();
if(queryStr.indexOf("d-") != -1){
	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
	System.out.println("queryStr:"+queryStr);
}

  String currentPage = Utils.isNull(request.getParameter(queryStr)).equals("")?"1":Utils.isNull(request.getParameter(queryStr));
  String totalPage = "";

  List<ShopBean> dataList = shopForm.getResults();
  if(dataList != null && dataList.size() >0){
    totalPage = String.valueOf((dataList.size()/ 50)+1);
  } 
  System.out.println("currentPage:"+currentPage+",totalPage:"+totalPage);
%>
	<c:if test="${shopForm.results != null}">
	    <br/>
		<display:table style="width:100%;" id="item" name="sessionScope.shopForm.results" defaultsort="0"  defaultorder="descending" class="resultDisp"
		    requestURI="#" sort="list" pagesize="50">	
		    <display:column  title="Sales Date" property="orderDate" sortable="false" class="td_text_center" style="width:8%"/>
		    <display:column  title="Order No" property="orderNo"  sortable="false" class="td_text_center" style="width:8%"/>
		    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="td_text" style="width:6%"/>
		    <display:column  title="Barcode" property="barcode"  sortable="false" class="td_text_center" style="width:10%"/>	
		    <display:column  title="Style" property="style"  sortable="false" class="td_text_center" style="width:8%"/>	
		    <display:column  title="Qty" property="qty"  sortable="false" class="td_number" style="width:8%"/>
		    <display:column  title="Free Item" property="freeItem"  sortable="false" class="td_text_center" style="width:5%"/>	
		    <%if( !user.getRole().getKey().equalsIgnoreCase(User.WACOAL)) {%>
		    <display:column  title="Unit Price" property="unitPrice"  sortable="false" class="td_number" style="width:7%"/>		
		    <display:column  title="Line Amount" property="lineAmount"  sortable="false" class="td_number" style="width:7%"/>
		    <display:column  title="Discount " property="discount"  sortable="false" class="td_number" style="width:7%"/>	
		    <display:column  title="Vat Amount " property="vatAmount"  sortable="false" class="td_number" style="width:7%"/>	
		    <%} %>
		    <display:column  title="Total Line Amount(In. Vat) " property="totalAmount"  sortable="false" class="td_number" style="width:10%"/>	
		    <display:column  title="Total Line Amount(Ex. Vat) " property="totalAmountExVat"  sortable="false" class="td_number" style="width:10%"/>	
		     <%if(currentPage.equalsIgnoreCase(totalPage)){ %>
		    <display:footer>
		       <tr class="text_blod">
		          <td colspan="5" align="right"><b>รวม</b></td>
		          <td class="td_number"><bean:write name="summary" property="qty"/></td>
		          <td ></td>
		          <%if( !user.getRole().getKey().equalsIgnoreCase(User.WACOAL)) {%>
			          <td ></td>
			          <td class="td_number"><bean:write name="summary" property="lineAmount"/></td>
			          <td class="td_number"><bean:write name="summary" property="discount"/></td>
			          <td class="td_number"><bean:write name="summary" property="vatAmount"/></td>
		          <%} %>
		          <td class="td_number"><bean:write name="summary" property="totalAmount"/></td>
		          <td class="td_number"><bean:write name="summary" property="totalAmountExVat"/></td>
		      </tr> 
		    </display:footer>	
		    <%} %>
		</display:table>
   </c:if>
