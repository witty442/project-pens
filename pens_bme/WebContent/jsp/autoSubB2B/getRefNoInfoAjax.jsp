
<%@page import="com.isecinc.pens.dao.AutoSubB2BDAO"%>
<%@page import="com.isecinc.pens.web.autosubb2b.AutoSubB2BBean"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%

String refNo = Utils.isNull(request.getParameter("refNo"));
String refType = Utils.isNull(request.getParameter("refType"));
AutoSubB2BBean head = null,cri = null;

String outputText = "";
try{
	System.out.println("refNo:"+refNo+",refType:"+refType);
	
	if( !"".equals(Utils.isNull(refNo)) ){
		cri = new AutoSubB2BBean();
		cri.setRefNo(refNo);
		if(refType.equalsIgnoreCase("WACOAL")){
			head = AutoSubB2BDAO.getStoreDetailByWacoal(cri);
		}else{
			head = AutoSubB2BDAO.getStoreDetailByPic(cri);
		}
	}
	if(head != null){
		outputText =head.getFromStoreCode()+"|"+head.getFromStoreName()+"|"+head.getFromSubInv()+"|"+head.getTotalQty();
		outputText += "|"+head.getTotalBox();
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>