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
	<td align="right"  nowrap>จาก วันที่ขาย (As Of)<font color="red">*</font> 
	&nbsp;&nbsp;
	<html:text property="onhandSummary.salesDate" styleId="salesDate" readonly="true"/>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td align="left" width="50%" nowrap></td>
 </tr>
     <tr>
    	 <td align="right"  nowrap>Brahch ID<font color="red">*</font>
		  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		   <html:text property="onhandSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="20" onkeypress="getBranchNameKeypress(event,this,'pensCustNameFrom')"/>-
		    <input type="button" name="x1" value="..." onclick="openPopupBranchAll('${pageContext.request.contextPath}','from','<%=storeType%>','<%=hideAll%>')"/>
		</td>
		<td align="left" width="30%"  nowrap> 
		  <html:text property="onhandSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="50"/>
		</td>
   </tr>
</table>
