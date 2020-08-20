<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.model.MTransport"%>
<%@page import="com.isecinc.pens.bean.Transport"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="session" />

<%
//Search
User user = (User) request.getSession().getAttribute("user");
List<PopupBean> subBrandList = (List) session.getAttribute("INIT_SUBBRAND_LIST");
pageContext.setAttribute("subBrandList", subBrandList, PageContext.PAGE_SCOPE);
List<PopupBean> results = popupForm.getBean().getDataList();
System.out.println("subBrand:"+popupForm.getBean().getSubBrand());
%>
<html>
<head>
<title>Search Transport</title>
<!-- For fix Head and Column Table -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript">
function searchPopup(path) {
    document.popupForm.action = path + "/jsp/popupAction.do?do=search";
    document.popupForm.submit();
   return true;
}

function selectChk(){
	
	var productCodeChk = null;
	var productCodeAll = '';
	<%
	 if(results != null && results.size() >0){
	  for(int i=0;i<results.size();i++){
		  PopupBean t = results.get(i);
     %>
        productCodeChk = document.getElementById("product_code_<%=t.getSubBrand()%>_<%=i%>");
        
	    if(productCodeChk.checked){
	    	productCodeAll +=productCodeChk.value+",";
	    }
     <%}
	 }//for%>
    window.opener.setMainValue('<%=Utils.isNull(request.getParameter("page"))%>',productCodeAll);
	window.close();
}
function setProductChkBySubBrand(subBrandChk){
	var productCodeChk = null;
	<%
	  if(results != null && results.size() >0){
	   for(int i=0;i<results.size();i++){
		  PopupBean t = results.get(i);
    %>
	    //alert(subBrandChk.value);
	    if(subBrandChk.value =='<%=t.getSubBrand()%>'){
	    	productCodeChk = document.getElementById("product_code_<%=t.getSubBrand()%>_<%=i%>");
	    	productCodeChk.checked = subBrandChk.checked;
	    }
    <%}}%>
}

</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/popupAction">
<html:hidden property="page" value="SUBBRAND_STOCK"></html:hidden>
<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px" class="txt1">
		<td width="15%" >&nbsp;</td>
		<td width="90%" ><font size="2"><b>ค้นหาสินค้าจาก Sub Brand</b></font></td>
	</tr>
	 <tr height="21px" class="txt1">
		<td width="15%" >Sub Brand</td>
		<td width="90%" >
		 <html:select property="bean.subBrand">
		      <html:option value=""></html:option>
			<html:options collection="subBrandList" property="code" labelProperty="desc"/>
		 </html:select>
		 <input type="button" name="search" value="ค้นข้อมูล" onclick="searchPopup('${pageContext.request.contextPath}')" 
		 class="newPosBtnLong"/>
		</td>
	</tr>
	<tr height="21px" class="txt1">
		<td colspan="2" ></td>
	</tr>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<input type="button" name="OK" value="ยืนยันเลือกข้อมูล" onclick="selectChk()" class="newPosBtnLong"/>
			<input type="button" name="CLOSE" value="ปิดหน้าจอนี้" onclick="javascript:window.close();" class="newPosBtnLong"/>
		</td>
	</tr>
</table>
<!-- RESULT -->
<%

if(results != null && results.size() >0){ %>
<div style='height:600px;width:890px;'>
<table id='myTable' class='table table-condensed table-striped' border='0' cellpadding="3" cellspacing="1" > 
   <thead> 
    <tr>
		<th ></th>
		<th >Sub Brand</th>
		<th ></th>
		<th >Product</th>
		<th >ProductName</th>
	</tr>
	</thead>
	<%
	int c = -1;
	String className="";
	Map<String,String> subbrandMap = new HashMap<String,String>();
	  for(int i=0;i<results.size();i++){
		  PopupBean t = results.get(i);
		  if(i%2==0){
			  className="lineE";
		  }else{
			  className="lineO"; 
		  }
	%>
	<tbody>
	<tr class="<%=className%>">
	    <%
		  if(subbrandMap.get(t.getSubBrand()) ==null){
	    %>
	         <td><input type="checkbox" name="subbrand" value="<%=t.getSubBrand()%>" onchange="setProductChkBySubBrand(this)"></td>
	         <td><%=t.getSubBrand()%>-<%=t.getSubBrandDesc()%></td>
	    <% } else { %>
	         <td></td>
	         <td></td>
	    <%}%>
	
	    <%
			c++;
	    %>
	       <td>
	           <input type="checkbox" id="product_code_<%=t.getSubBrand() %>_<%=c %>" 
	           value="<%=t.getProductCode()%>">
	       </td>
	       <td><%=t.getProductCode()%></td>
	       <td><%=t.getProductName()%></td>
	</tr>
	<%
	   subbrandMap.put(t.getSubBrand(), t.getSubBrand());
	}//for%>
	</tbody>
</table>
</div>

<script>
	//load jquery
	$(function() {
		//Load fix column and Head
		$('#myTable').stickyTable({overflowy: true});
	});
</script>
<%} %>
<!-- RESULT -->

</html:form>
</body>
</html>