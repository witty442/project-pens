
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="com.isecinc.pens.dao.SummaryDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.bean.Master"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%

String custCode = Utils.isNull(request.getParameter("custCode"));
String mcArea = Utils.isNull(request.getParameter("mcArea"));
String mcRoute = Utils.isNull(request.getParameter("mcRoute"));
String staffType = Utils.isNull(request.getParameter("staffType"));
String outputText = "";
try{

	if( !"".equals(Utils.isNull(custCode)) ){
		//condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
		PopupForm cri = new PopupForm();
		cri.setCodeSearch(custCode);
		
		List<PopupForm> ret = MCDAO.searchStaffList(cri,"equals",mcArea,mcRoute,staffType);
		if(ret != null &&  ret.size() >0){
			PopupForm p = ret.get(0);
			outputText = p.getDesc()+"|"+p.getStoreNo()+"|"+p.getSubInv();
		}else{
		   outputText ="";
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>