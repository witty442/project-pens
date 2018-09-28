<%@ page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@page import="com.isecinc.pens.bean.User"%>
<% 
User user = (User) session.getAttribute("user");
List<References> qtyDels = InitialReferences.getReferenes().get(InitialReferences.QTY_DELIVER);
pageContext.setAttribute("qtyDels",qtyDels,PageContext.PAGE_SCOPE);

List<References> roundDels = InitialReferences.getReferenes().get(InitialReferences.ROUND_DELIVER);
pageContext.setAttribute("roundDels",roundDels,PageContext.PAGE_SCOPE);

List<References> memberTypes = InitialReferences.getReferenes().get(InitialReferences.MEMBER_TYPE);
pageContext.setAttribute("memberTypes",memberTypes,PageContext.PAGE_SCOPE);
%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
	<tr>
		<td align="right" width="50%"><bean:message key="SysConf.MemberAgingPeriod"  bundle="sysele"/>
			<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
				<font color="red">*</font>
			<%}else{ %>
				&nbsp;&nbsp;
			<%} %>
		</td>
		<td align="left">
			<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
			<html:text property="alertPeriod" size="5" style="text-align: right;" onkeydown="return inputNum(event);"/>
			<%}else{ %>
			<html:text property="alertPeriod" readonly="true" styleClass="disableText" size="5" style="text-align: right;"/>
			<%} %>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="SysConf.QtyDelivery" bundle="sysele"/>
			<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
				<font color="red">*</font>
			<%}else{ %>
				&nbsp;&nbsp;
			<%} %>
		</td>
		<td align="left">
			<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
			<html:select property="qtyDeliver">
				<html:options collection="qtyDels" property="key" labelProperty="name"/>
			</html:select>
			<%}else{ %>
			<html:select property="qtyDeliver" disabled="true"  styleClass="disableText">
				<html:options collection="qtyDels" property="key" labelProperty="name"/>
			</html:select>
			<%} %>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="SysConf.RoundDelievery" bundle="sysele"/>
			<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
				<font color="red">*</font>
			<%}else{ %>
				&nbsp;&nbsp;
			<%} %>
		</td>
		<td align="left">
			<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
			<html:select property="roundDeliver">
				<html:options collection="roundDels" property="key" labelProperty="name"/>
			</html:select>
			<%}else{ %>
			<html:select property="roundDeliver" disabled="true"  styleClass="disableText">
				<html:options collection="roundDels" property="key" labelProperty="name"/>
			</html:select>
			<%} %>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Condition.MemberType" bundle="sysele"/>
			<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
				<font color="red">*</font>
			<%}else{ %>
				&nbsp;&nbsp;
			<%} %>
		</td>
		<td align="left">
			<%if(user.getType().equalsIgnoreCase(User.ADMIN)){ %>
			<html:select property="memberType">
				<html:options collection="memberTypes" property="key" labelProperty="name"/>
			</html:select>
			<%}else{ %>
			<html:select property="memberType" disabled="true"  styleClass="disableText">
				<html:options collection="memberTypes" property="key" labelProperty="name"/>
			</html:select>
			<%} %>
		</td>
	</tr>
</table>	
