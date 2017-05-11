
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.dao.MoveStockWarehoseDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String mat = Utils.isNull(request.getParameter("mat"));
String warehouse = Utils.isNull(request.getParameter("warehouse"));
String pensItem = Utils.isNull(request.getParameter("pensItem"));

System.out.println("pensItem:"+pensItem);

//System.out.println(refId);
List<PopupForm> pensItemList = new ArrayList<PopupForm>();
try{
	if(mat != null && mat.length()>0 && warehouse != null && warehouse.length() >0){
		pensItemList = MoveStockWarehoseDAO.getPensItemListStockFinish(warehouse,mat);
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>
<option value=""></option>
<%
String selected = "";
for(PopupForm u : pensItemList){ 
	if( !"".equals(pensItem)){
		pensItem = pensItem.split("\\|")[0];
		System.out.println("pensItem Split:"+pensItem);
		if(u.getPensItem().equalsIgnoreCase(pensItem) ){
		  selected = "selected"; 
		}else{
		  selected = "";
		}
	}
%>
<option value="<%=u.getPensItem()+"|"+u.getQty()%>" <%=selected %>><%=u.getPensItem()%></option>
<%}%>