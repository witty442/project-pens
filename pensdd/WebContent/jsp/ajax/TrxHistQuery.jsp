<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.bean.TrxHistory"%>
<%@page import="com.isecinc.pens.model.MTrxHistory"%>
<%
String module = (String)request.getParameter("module");
String recordId = (String)request.getParameter("recordId");

//System.out.println(module);
//System.out.println(recordId);

List<TrxHistory> trxs = new ArrayList<TrxHistory>();

try{
String whereCause = " AND TRX_MODULE = '"+module+"' ";
whereCause+="  AND RECORD_ID = "+recordId;
whereCause+=" ORDER BY TRX_HIST_ID DESC "; 
trxs = new MTrxHistory().lookUp(whereCause);
}catch(Exception e){
	e.printStackTrace();
}finally{
	
}
%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%><table align="left" border="0" cellpadding="3" cellspacing="1" class="result">
<%int i=0; %>
<%String className=""; %>
<%for(TrxHistory h : trxs){ %>
<%if(i%2==0){className="lineO";}else{className="lineE";} %>
<tr class="<%=className %>">
	<td align="left">
		<%=(i+1)+"." %>
		<%=h.getUser().getCode() %>&nbsp;
		<%=h.getUser().getName() %>
		<%if(h.getTrxType().equalsIgnoreCase(TrxHistory.TYPE_INSERT)){ %><bean:message key="TrxHist_I" bundle="sysprop"/><%} %>
		<%if(h.getTrxType().equalsIgnoreCase(TrxHistory.TYPE_UPDATE)){ %><bean:message key="TrxHist_U" bundle="sysprop"/><%} %>
		<%if(h.getTrxType().equalsIgnoreCase(TrxHistory.TYPE_DELETE)){ %><bean:message key="TrxHist_D" bundle="sysprop"/><%} %>
		<bean:message key="TrxHistDate" bundle="sysprop"/>
		<%=h.getTrxDate() %>
	</td>
</tr>
<%i++; %>
<%} %>
</table>