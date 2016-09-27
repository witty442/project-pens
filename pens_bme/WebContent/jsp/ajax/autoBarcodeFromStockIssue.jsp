
<%@page import="com.isecinc.pens.inf.helper.DBConnection"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.bean.Barcode"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%
String barcode = Utils.isNull((String) request.getParameter("barcode"));
String issueReqNo = Utils.isNull((String) request.getParameter("issueReqNo"));
String matCode = Utils.isNull((String) request.getParameter("matCode"));
String warehouse = Utils.isNull((String) request.getParameter("warehouse"));
String boxNo = Utils.isNull((String) request.getParameter("boxNo"));
String outputText = "";
Map<String,String> itemMap = new HashMap<String,String>();
String keyMap = "";
String pensItem = "";
Connection conn = null;
int totalQtyByBarcodeAndPensItem = 0;
try{
	//System.out.println("itemCode:"+itemCode);
	if( !issueReqNo.equals("") && (!"".equals(Utils.isNull(barcode)) || !"".equals(Utils.isNull(matCode)) ) ){
		
		//get Conbection
		conn = DBConnection.getInstance().getConnection(); 
		
		//Get ITEM_MAP For compare Qty by barcode+pensItem
		if(session.getAttribute("ITEM_MAP") != null){
		   itemMap = (Map)session.getAttribute("ITEM_MAP");
		   System.out.println("ItemMap:"+itemMap);
		}
		
		//Get pensItem by Barcode order by pens_item asc and have count qty <> 0
		 Barcode bResult = GeneralDAO.getPensItemByBarcodeModelStockIssueIsQtyNotZero(conn, barcode, matCode, issueReqNo, warehouse, boxNo,itemMap);
		 if(bResult != null){
			 pensItem = bResult.getPensItem();
			 totalQtyByBarcodeAndPensItem = bResult.getQty();
		 } 
         System.out.println("*********No remian Qty <> 0 pensItem:"+pensItem+",remain Qty:"+totalQtyByBarcodeAndPensItem);
		
		
		keyMap = barcode+pensItem;
		System.out.println("keyMap:"+keyMap);
		
		//Set count Qty by Barcode+pensItem
		if(session.getAttribute("ITEM_MAP") != null){
			itemMap = (Map)session.getAttribute("ITEM_MAP");
			if(itemMap.get(keyMap) != null){
				int totalQtyByBarcodePensItem = Utils.convertStrToInt(itemMap.get(keyMap)) +1;//count totalQty
				itemMap.put(keyMap, String.valueOf(totalQtyByBarcodePensItem));
			}else{
				itemMap.put(keyMap, "1");
			}
			session.setAttribute("ITEM_MAP",itemMap);
		}else{
			itemMap.put(keyMap, "1");
			session.setAttribute("ITEM_MAP",itemMap);
		}
		 
		Barcode b = GeneralDAO.searchProductByBarcodeFromStockIssue(conn,request,barcode,matCode,issueReqNo,warehouse,boxNo,pensItem);  
		
		if(b != null ){
		    outputText = b.getBarcode()+"|"+b.getMaterialMaster()+"|"+b.getGroupCode()+"|"+b.getPensItem()+"|"+b.getQty();
		}else{
		    outputText =""; 
		} 
		
		//System.out.println("returnText["+outputText+"]");
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn !=null){
		conn.close();
	}
}
%>
<%=outputText %>