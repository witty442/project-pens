<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.web.stockonhand.PreOrderNissinProcess"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
String action =Utils.isNull(request.getParameter("action"));

List<PopupBean> dataList = new PreOrderNissinProcess().searchTransDateNissinPreOrderList();
if(dataList != null && dataList.size() >0){
%>
<div align="center">
<br/>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
      <tr>
		<td align="right" colspan="2"><font size="2"><b>ค้นหาข้อมูล Transaction Date </b></font></td>
	</tr>
	 <tr>
		<td align="right" colspan="2"></td>
	</tr>
     <tr>
		<td align="right"> Transaction Date
		</td>
		  <td> 
		  <select name="transDate" id="transDate">
		  <%for(int i=0;i<dataList.size();i++){ 
		     PopupBean item = dataList.get(i);
		    %>
		      <option value="<%=item.getValue()%>"><%=item.getKeyName()%></option>
		  <%} %>
		  </select>
		</td>
	</tr>
</table>
<%}else{
  out.println("ไม่พบข้อมูล");

}%>
<br/>
 <table  border="0" cellpadding="3" cellspacing="0" >
	<tr>
		<td align="left">
		<a href="javascript:selectData()">
		  <input type="button" value="   OK   " class="newPosBtnLong"> 
		</a>
		&nbsp;
		<a href="javascript:window.close()">
		  <input type="button" value="ปิดหน้าจอนี้ " class="newPosBtnLong"> 
		</a>	
      </td>
  </tr>
</div>
<script>
  function selectData(){
	  var transDate = document.getElementById("transDate");
	  window.opener.setDataPopupValue(transDate.value);
	  window.close();
  }
</script>