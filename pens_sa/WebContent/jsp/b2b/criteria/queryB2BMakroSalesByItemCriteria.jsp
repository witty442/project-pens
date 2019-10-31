<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.isecinc.pens.web.stock.StockForm"%>
<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="com.pens.util.*"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
User user = (User) session.getAttribute("user");

%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.stickytable.js"></script> 
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery.stickytable.css?" type="text/css" />
<style>
.sticky-table {
    max-width: <%=(String)session.getAttribute("screenWidth")%>px;
    max-height: 75vh;
    overflow: auto;
    border-top: 1px solid #ddd;
    border-bottom: 1px solid #ddd;
    padding: 0 !important;
    transition: width 2s; 
}
</style>
<script type="text/javascript">
window.onload = function(){
	loadMe();
	
	/** load Excel from temp to StreamOutput **/
	<%if(request.getAttribute("LOAD_EXCEL") != null){%>
	  loadExcel('<%=(String)request.getAttribute("LOAD_EXCEL") %>');
	<%}%>
}

function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.b2bForm;
}

function clearForm(){
	 var path = document.getElementById("path").value;
	var form = document.b2bForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/b2bAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function search(){
	var path = document.getElementById("path").value;
	var form = document.b2bForm;
	form.action = path + "/jsp/b2bAction.do?do=searchHead&action=newsearch";
	form.submit();
	return false;
}
function exportExcel(){
	/**  Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	var path = document.getElementById("path").value;
	var form = document.b2bForm;
	form.action = path + "/jsp/b2bAction.do?do=exportExcel";
	form.submit();
	return false;
}
function loadExcel(fileName){
	var path = document.getElementById("path").value;
	var pageName = document.getElementById("pageName").value;
	var form = document.b2bForm;
	form.action = path + "/jsp/b2bAction.do?do=loadExcel&pageName="+pageName+"&fileName="+fileName;
	form.submit();
	return false;
}
</script>
<table align="center" border="0" cellpadding="3" cellspacing="0" >    
      <tr>
            <td> ภาค<font color="red"></font></td>
		<td>					
			 <html:select property="bean.region" styleId="region" >
				<html:options collection="REGION_LIST" property="value" labelProperty="keyName"/>
		    </html:select>
		</td>
	</tr>
 </table>
 <table  border="0" cellpadding="3" cellspacing="0" >
		<tr>
			<td align="left">
				<a href="javascript:search()">
				  <input type="button" value=" ค้นหา " class="newPosBtnLong"> 
				</a>&nbsp;
				 <a href="javascript:exportExcel()">
				  <input type="button" value="  Export  " class="newPosBtnLong"> 
				</a>
				&nbsp;
				<a href="javascript:clearForm()">
				  <input type="button" value="   Clear   " class="newPosBtnLong">
				</a>			
			</td>
		</tr>
	</table>
	

 <%if(user.getUserName().equalsIgnoreCase("admin")){ %>
      <br/>
	  <table border="0"  width ="100%" cellpadding="3" cellspacing="1" bgcolor="#000000">
	    <tr bgcolor="#FFFFFF">
	      <td width="10%" ><b>Process Name</b></td>
	      <td width="40%" ><b>Process Description</b></td>
	    </tr>
	    <tr bgcolor="#FFFFFF">
	       <td>Query B2B Makro Sales By Item And Export</td>
	       <td>Table:  PENSBI.XXPENS_BI_MST_B2B_STORE ,PENSBI.XXPENS_BI_B2B_SALES_ITEM_TEMP  </td>
	    </tr>
	  </table>
	<%}else{ %>
	   <table border="0" width ="50%" cellpadding="3" cellspacing="1" bgcolor="#000000">
	    <tr  bgcolor="#FFFFFF">
	      <td width="10%"><b>Process Name</b></td>
	      <td width="40%"><b>Process Description</b></td>
	    </tr>
	    <tr bgcolor="#FFFFFF">
	       <td>Query B2B Makro Sales By Item and Export  </td>
	       <td></td>
	    </tr>
	  </table>
	<%} %>
