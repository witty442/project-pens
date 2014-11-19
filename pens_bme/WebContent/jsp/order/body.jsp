<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

 <table align="center" border="1" cellpadding="3" cellspacing="0" class="body">
	                     
	<!-- ITEM -->
<% if(session.getAttribute("results") != null){
	List<Order> orderItemList =(List<Order>) session.getAttribute("results");
	for(int i=0;i<orderItemList.size();i++){
	 Order o = (Order) orderItemList.get(i);
	 List<StoreBean> storeList = o.getStoreItemList();
	%>
	<tr>
	       <td><input type="text" name="group" value="<%=o.getGroupCode()%>" size="6"></td>
	       <td><input type="text" name="item" value="<%=o.getItem()%>" size="7"></td>
	       <td><input type="text" name="itemDesc" value="<%=o.getItemDesc()%>" size="15"></td>
	       <td><input type="text" name="onhandQty" value="<%=o.getOnhandQty()%>" size="5"></td>
	       <td><input type="text" name="wholePriceBF" value="<%=o.getWholePriceBF()%>" size="10"></td>
	       <td><input type="text" name="retailPriceBF" value="<%=o.getRetailPriceBF()%>" size="10"></td>
	       <td><input type="text" name="barcode" value="<%=o.getBarcode()%>" size="12"></td>
	        <%if(storeList != null && storeList.size()>0){ 
	        	for(int k=0;k<storeList.size();k++){
	              StoreBean s = (StoreBean)storeList.get(k);
	         %>
	              <td><input type="text" name="qty_<%=s.getStoreCode()%>" value="" size="3"></td>
	         <%
	             }//for 2
	           }//if  
	         %>
	     </tr>
	
	<%  		  
	   } //for 1
	}//if 1
	%>
</table>