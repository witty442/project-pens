<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stockonhand.StockOnhandBean"%>
<%@page import="com.isecinc.pens.web.stockonhand.StockOnhandForm"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "stockOnhandForm");
StockOnhandBean bean = ((StockOnhandForm)session.getAttribute("stockOnhandForm")).getBean();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.stockOnhandForm;
}
function clearForm(path){
	var form = document.stockOnhandForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockOnhandAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.stockOnhandForm;
	
	form.action = path + "/jsp/stockOnhandAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.stockOnhandForm;
	form.action = path + "/jsp/stockOnhandAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}

</script>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
     <tr>
		<td align="right"> Transaction Date
		</td>
		  <td> 
		   <html:text property="bean.transDate" styleId="transDate" styleClass="disableText" readonly="true"></html:text>
		</td>
	</tr>	
  </table>
   <table  border="0" cellpadding="3" cellspacing="0" >
		<tr>
			<td align="left">
				<a href="javascript:search('${pageContext.request.contextPath}')">
				  <input type="button" value="   ค้นหา     " class="newPosBtnLong"> 
				</a>
				<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
				  <input type="button" value="  Export   " class="newPosBtnLong"> 
				</a>
				<a href="javascript:clearForm('${pageContext.request.contextPath}')">
				  <input type="button" value="   Clear   " class="newPosBtnLong">
				</a>						
			</td>
		</tr>
	</table>