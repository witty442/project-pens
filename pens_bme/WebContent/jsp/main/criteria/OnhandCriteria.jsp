<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String storeType = "";
String hideAll = "";
%>
 <table  border="0" cellpadding="3" cellspacing="0" class="body" width="65%">
   <tr>
	<td align="left" width="30%">Location <font color="red">*</font>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		 <html:select property="onhandSummary.location" styleId="location">
		     <html:option value=""></html:option>
			<html:option value="StockStore">Stock ห้าง</html:option>
			<html:option value="StockFriday">Stock Friday</html:option>
			<html:option value="StockOShopping">Stock O-Shopping</html:option>
			<html:option value="Stock7Catalog">Stock 7-Catalog</html:option>
			<html:option value="StockTVDirect">Stock TV-Direct</html:option>
	     </html:select>
	</td>
	<td align="left" width="40%">       
	</td>
  </tr> 
     <tr>
	<td align="left" width="30%">
	     จาก รหัสสินค้า&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;<html:text property="onhandSummary.itemCodeFrom" styleId="itemCodeFrom"/>
	</td>
	<td align="left" width="40%">
	          ถึง รหัสสินค้า&nbsp;&nbsp; <html:text property="onhandSummary.itemCodeTo" styleId="itemCodeTo"/>
	</td>
</tr>
<tr>
	<td align="left" width="30%">
	     Pens Item From&nbsp;&nbsp; <html:text property="onhandSummary.pensItemFrom" styleId="pensItemFrom"/>
	     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from')"/>
	</td>
	<td align="left" width="40%">
	     Pens Item To&nbsp;&nbsp; <html:text property="onhandSummary.pensItemTo" styleId="pensItemTo"/>
	     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to')"/>    
	</td>
</tr>
<tr>
	<td align="left" width="30%">Group &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    <html:text property="onhandSummary.group" styleId="group" size="25"/>
	    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}')"/>
	     <html:hidden property="onhandSummary.groupDesc" styleId="groupDesc" />
	  </td>
	<td align="left" width="40%">สถานะ
	   &nbsp;
	   <html:select property="onhandSummary.status">
		<html:option value="SUCCESS">SUCCESS</html:option>
		<html:option value="ERROR">ERROR</html:option>
     </html:select>
     Display Zero Stock <html:checkbox property="onhandSummary.dispZeroStock" />
		</td>
	</tr>
</table>