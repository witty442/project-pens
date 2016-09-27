
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%
String barcodeArr = Utils.isNull((String) request.getParameter("barcodeArr"));
String pensItemArr = Utils.isNull((String) request.getParameter("pensItemArr"));
Map<String,String> itemMap = new HashMap<String,String>();
String keyMap = "";
int i = 0;
System.out.println("barcodeArr:"+barcodeArr);
System.out.println("pensItemArr:"+pensItemArr);
try{
	String barcodeArray[] = barcodeArr.split("\\,");
	String pensItemArray[] = pensItemArr.split("\\,");
	
	for(i=0;i<barcodeArray.length;i++){
		keyMap = barcodeArray[i]+pensItemArray[i];
		
		System.out.println("keyMap["+i+"]:"+keyMap);
		//Set distcount count Qty by Barcode+pensItem
		if(session.getAttribute("ITEM_MAP") != null && !"".equals(keyMap)){
			itemMap = (Map)session.getAttribute("ITEM_MAP");
			if(itemMap.get(keyMap) != null){
				int totalQtyByBarcodePensItem = Utils.convertStrToInt(itemMap.get(keyMap)) -1;//discount totalQty
				itemMap.put(keyMap, String.valueOf(totalQtyByBarcodePensItem));
				
				System.out.println("discount pensItem["+pensItemArray[i]+"]remainQty["+totalQtyByBarcodePensItem+"]");
			}
		
			session.setAttribute("ITEM_MAP",itemMap);
		}
	}//for 
	
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>