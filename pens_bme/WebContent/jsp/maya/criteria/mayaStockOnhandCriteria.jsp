<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="mayaForm" class="com.isecinc.pens.web.maya.MayaForm" scope="session" />

<table  border="0" cellpadding="3" cellspacing="0" width="50%">
   <tr>
	<td align="right" width="10%"> From Group</td>
	<td align="left" width="6%"><html:text property="bean.groupCodeFrom" styleId="groupCodeFrom" readonly="true" size="10"/></td>
	<td align="right" width="6%"> ถึง วันที่ขาย</td>
	<td align="left" width="10%"><html:text property="bean.groupCodeTo" styleId="groupCodeTo" readonly="true" size="10"/></td>
  </tr>
  <tr>
	<td align="right" width="10%"> From Group</td>
	<td align="left" width="6%"><html:text property="bean.groupCodeFrom" styleId="groupCodeFrom" readonly="true" size="10"/></td>
	<td align="right" width="6%"> ถึง วันที่ขาย</td>
	<td align="left" width="10%"><html:text property="bean.groupCodeTo" styleId="groupCodeTo" readonly="true" size="10"/></td>
  </tr>
</table>