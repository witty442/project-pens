<%@page import="com.isecinc.pens.report.salesanalyst.SAGenerate"%>
<%@page import="com.isecinc.pens.web.report.salesanalyst.SAReportForm"%>
<%@page import="com.isecinc.pens.report.salesanalyst.SABean"%>
<%@page import="com.isecinc.pens.web.report.analyst.process.AInitial"%>
<%@page import="com.isecinc.pens.web.report.analyst.AReportForm"%>
<%@page import="com.isecinc.pens.web.report.analyst.bean.ABean"%>
<%@page import="com.isecinc.pens.web.report.analyst.process.AGenerate"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MRole"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.*" %>
<%
/** Gen Header  Report Excel  **/
String columnCount = request.getParameter("columnCount");
String condDisp1 = Utils.isNull(request.getParameter("condDisp1"));
String condDisp2 = Utils.isNull(request.getParameter("condDisp2"));
String condDisp3 = Utils.isNull(request.getParameter("condDisp3"));
String condDisp4 = Utils.isNull(request.getParameter("condDisp4"));
String condDisp5 = Utils.isNull(request.getParameter("condDisp5"));
String reportName = Utils.isNull(request.getParameter("reportName"));
String action = Utils.isNull(request.getParameter("action"));//=new (new version)

String headerHtml  =  "";
User user = (User)session.getAttribute("user");
AInitial aInit = new AInitial().getAInit(request);
try{
	System.out.println("aBean Session:"+session.getAttribute("aReportForm"));
	System.out.println("reportName:"+reportName +",action:"+action);
	
	if(action.equalsIgnoreCase("new") && "ProjectCAnalyst".equalsIgnoreCase(reportName)){;
	    ABean salesBean = ((AReportForm)session.getAttribute("aReportForm")).getSalesBean();
	    headerHtml = new AGenerate(aInit).genHeaderReportExportExcel(reportName, user,salesBean,columnCount,condDisp1,condDisp2,condDisp3,condDisp4,condDisp5).toString();
	}else if(action.equalsIgnoreCase("new") && "SalesAnalyst".equalsIgnoreCase(reportName)){
		ABean salesBean = ((AReportForm)session.getAttribute("aReportForm")).getSalesBean();
		headerHtml = new AGenerate(aInit).genHeaderReportExportExcel(reportName, user,salesBean,columnCount,condDisp1,condDisp2,condDisp3,condDisp4,condDisp5).toString();	
	}else{
		//old version
		SABean salesBean = ((SAReportForm)session.getAttribute("salesAnalystReportForm")).getSalesBean();
		headerHtml = SAGenerate.genHeaderReportExportExcel(user,salesBean,columnCount,condDisp1,condDisp2,condDisp3,condDisp4,condDisp5).toString();
	}
	//System.out.println("result:\n"+headerHtml);
	
}catch(Exception e){
	headerHtml = e.getMessage();
}finally{
	
}
%>
<%=headerHtml%>