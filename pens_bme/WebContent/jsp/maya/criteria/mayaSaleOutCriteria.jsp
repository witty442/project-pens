<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<table  border="0" cellpadding="3" cellspacing="0" class="body" width="50%">
   <tr>
	<td align="right" width="10%"> จาก วันที่ขาย</td>
	<td align="left" width="20%">
	<html:text property="bean.startDate" styleId="startDate" readonly="true" size="10"/>
	&nbsp;&nbsp;ถึง วันที่ขาย&nbsp;&nbsp;
    <html:text property="bean.endDate" styleId="endDate" readonly="true" size="10"/>
	</td>
  </tr>
</table>