
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>

<%
String countDate = (String)request.getParameter("countDate");
String custCode = (String) request.getParameter("custCode");
String fileName = (String) request.getParameter("fileName");
String outputText = "0";
// return 
// 0 :no dup
// -1:dupFileName
// -2:dupFileName dup countDate custCode
try{
	System.out.println("countDate:"+countDate);
	System.out.println("custCode:"+custCode);
	System.out.println("fileName:"+fileName);
	
	if( !"".equals(Utils.isNull(countDate)) && !"".equals(Utils.isNull(custCode)) && !"".equals(Utils.isNull(custCode)) ){
		//condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
		ImportDAO importDAO = new ImportDAO();
		boolean dupFileName = importDAO.importPhyFileNameIsDuplicate(fileName);
		if(dupFileName == true){
			outputText = "-1";
	    }else if(dupFileName==false){
			boolean dupWarn = importDAO.importPhyFileNameIsDuplicate(countDate, custCode);
			if(dupWarn){
			   outputText ="-2";
			   System.out.println("outputText:"+outputText);
			}else{
			   outputText ="0";
			}
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>