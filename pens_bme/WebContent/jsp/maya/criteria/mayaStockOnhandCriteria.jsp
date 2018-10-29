<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<table  border="0" cellpadding="3" cellspacing="0" width="50%">
<tr>
	<td align="right" width="10%">Maya Shop</td>
	<td align="left" width="6%"><html:text property="bean.custGroup" styleId="custGroup" styleClass="disableText" readonly="true" size="10"/></td>
	<td align="right" width="6%"> </td>
	<td align="left" width="10%"></td>
  </tr>
 <tr>
	<td align="right" width="10%"> วันที่ขาย ()As Of Date)<font color="red">*</font></td>
	<td align="left" width="6%"><html:text property="bean.asOfDate" styleId="asOfDate" readonly="true" size="10"/></td>
	<td align="right" width="6%"> </td>
	<td align="left" width="10%"></td>
  </tr>
   <tr>
	<td align="right" width="10%"> From Group</td>
	<td align="left" width="6%"><html:text property="bean.groupCodeFrom" styleId="groupCodeFrom" readonly="true" size="10"/></td>
	<td align="right" width="6%"> To Group</td>
	<td align="left" width="10%"><html:text property="bean.groupCodeTo" styleId="groupCodeTo" readonly="true" size="10"/></td>
  </tr>
  <tr>
	<td align="right" width="10%"> From Style</td>
	<td align="left" width="6%"><html:text property="bean.styleFrom" styleId="styleFrom" readonly="true" size="10"/></td>
	<td align="right" width="6%">To Style</td>
	<td align="left" width="10%"><html:text property="bean.styleTo" styleId="styleTo" readonly="true" size="10"/></td>
  </tr>
  <tr>
	<td align="right" width="10%"> From Pens Item</td>
	<td align="left" width="6%"><html:text property="bean.pensItemFrom" styleId="pensItemFrom" readonly="true" size="10"/></td>
	<td align="right" width="6%">To Pens Item</td>
	<td align="left" width="10%"><html:text property="bean.pensItemTo" styleId="pensItemTo" readonly="true" size="10"/></td>
  </tr>
</table>