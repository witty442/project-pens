
<%@page import="com.isecinc.pens.dao.SADamageDAO"%>
<%@page import="com.isecinc.pens.bean.SADamageBean"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String invRefWal = Utils.isNull(request.getParameter("invRefWal"));
String typeInvoice = Utils.isNull(request.getParameter("typeInvoice"));
System.out.println("invRefWal:"+invRefWal);
System.out.println("typeInvoice:"+typeInvoice);
String outputText = "";
try{
	//System.out.println("code:"+code);
	if( !"".equals(Utils.isNull(invRefWal)) ){
		//validate found in SA_DAMAGE_HEAD
		String invRefWalDB =SADamageDAO.getInvRefwalInDamageHead(invRefWal);
		if( !Utils.isNull(invRefWalDB).equals("")){
			outputText ="ERROR_FOUND_IN_DB";
		}else{
			SADamageBean bean = SADamageDAO.getInvRefwalFromSaleAnalysis(invRefWal);
			//Validate Type Invoice 
			//เลือก B'me  เช็ค invoice ต้องขึ้นต้นด้วย 1
            // เลือก Wacoal  เช็ค invoice ต้องขึ้นต้นด้วย M
			if(bean != null ){
			    outputText  =  bean.getOracleRefId()+"|"+bean.getOracleRefName()+"|"+bean.getTotalDamage();
			    outputText += "|"+Utils.isNull(bean.getEmpId())+"|"+Utils.isNull(bean.getName())+"|"+Utils.isNull(bean.getSurname())+"|"+Utils.isNull(bean.getBranch())+"|"+Utils.isNull(bean.getGroupStore());
			}else{
			    outputText ="";
			}
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>