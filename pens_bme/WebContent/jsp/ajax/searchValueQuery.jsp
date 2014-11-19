<%@page import="com.isecinc.pens.web.report.ReportProcess"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.web.popup.DisplayBean"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>

<%
String condCode = (String)request.getParameter("condCode");
String condType = (String) request.getParameter("condType");
String outputText = "";
ReportProcess process = new ReportProcess();
List<DisplayBean> lstData = null;

boolean isMultiCode = condCode.indexOf(",") > -1;
String value = "";
String name = "";

try{
	System.out.println("condCode:"+condCode);
	System.out.println("condType:"+condType);
	
	if(condCode != null && !condCode.equals("")){
		condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
		lstData = ReportProcess.getInstance().getConditionValueList(condType, condCode, "");
		if(lstData != null && lstData.size() > 0){
			if(isMultiCode){
				for(int i = 0; i < lstData.size() ; i++){
					if(i != 0){
						value += ",";
						name  += ",";
					}
					value +=lstData.get(i).getCode();
					name +=lstData.get(i).getName();
				}
				
				outputText = value+"|"+name;
			}
			else{
				outputText = lstData.get(0).getCode() +"|"+ lstData.get(0).getName();
			}
			System.out.println("outputText:"+outputText);
		}else{
			outputText ="|";
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>