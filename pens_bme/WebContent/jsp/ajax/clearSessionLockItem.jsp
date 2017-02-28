<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
System.out.println("clearSessionLockItem");

String groupCode = Utils.isNull(request.getParameter("groupCode"));
String groupStore = Utils.isNull(request.getParameter("groupStore"));
System.out.println("groupCode:"+groupCode);
System.out.println("groupStore:"+groupStore);

if(session.getAttribute("StoreCodeToMap") != null){
  Map<String,String> storeCodeMap= (Map)session.getAttribute("StoreCodeToMap");
  String key = groupCode+"_"+groupStore;
  storeCodeMap.put(key, "");
  session.setAttribute("StoreCodeToMap", storeCodeMap);
}
%>