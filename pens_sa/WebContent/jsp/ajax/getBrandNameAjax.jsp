
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String brandId = Utils.isNull(request.getParameter("brandId"));
String outputText = "";
try{
	System.out.println("custCode:"+brandId);
	if( !"".equals(Utils.isNull(brandId)) ){
		if( "ALL".equalsIgnoreCase(Utils.isNull(brandId)) ){
			 outputText = "ALL Branch";
		}else{
			//condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
			PopupBean c = new PopupBean();
			c.setCodeSearch(brandId);
			
		    List<PopupBean> dataList = SalesTargetUtils.searchBrand(c, "equals");
		    if(dataList != null && dataList.size()>0){
			   outputText = Utils.isNull(dataList.get(0).getBrandName());
			  // System.out.println("outputText:"+outputText);
		    }
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>