
<%@page import="com.isecinc.pens.dao.SummaryDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.bean.Master"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%

String custCode = Utils.isNull(request.getParameter("custCode"));
String storeGroup = Utils.isNull(request.getParameter("storeGroup"));
String storeType = Utils.isNull(request.getParameter("storeType"));
String outputText = "";
try{

	System.out.println("custCode:"+custCode+",storeGroup:"+storeGroup+",storeType:"+storeType);
	
	if( !"".equals(Utils.isNull(custCode)) ){
		//condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
		PopupForm cri = new PopupForm();
		cri.setCodeSearch(custCode);
		
		List<PopupForm> ret = SummaryDAO.searchCustomerMaster(cri,storeType,storeGroup,"equals");
		if(ret != null &&  ret.size() >0){
			PopupForm p = ret.get(0);
			outputText = p.getDesc()+"|"+p.getStoreNo()+"|"+p.getSubInv();
			System.out.println("customerName:"+p.getDesc());
		}else{
		   outputText ="";
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>