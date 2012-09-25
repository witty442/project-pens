<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%
String code = ConvertNullUtil.convertToString(request.getParameter("code"));
//System.out.println(code);
String whereCause="";
if(code.length()>0) whereCause+="  and code like '"+code+"%' ";
List<MemberImport> mis = new ArrayList<MemberImport>();
mis = new MemberImportProcess().getMemberImport(whereCause);
if(mis!=null)
	pageContext.setAttribute("mis",mis,PageContext.PAGE_SCOPE);
%>
<%@page import="com.isecinc.pens.dataimports.bean.MemberImport"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.process.dataimports.MemberImportProcess"%>
<%@page import="util.ConvertNullUtil"%>
<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="com.isecinc.core.bean.Messages"%>
<c:if test="${mis != null}">
<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
&nbsp;<span class="searchResult"><%=mis.size() %></span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
<table id="tblResult" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop"/></th>
		<th class="code"><bean:message key="Member.Code" bundle="sysele"/></th>
		<th class="name"><bean:message key="Member.Name" bundle="sysele"/>-<bean:message key="Member.Surname" bundle="sysele"/></th>
		<th>orders</th>
	</tr>	
	<c:forEach var="results" items="${mis}" varStatus="rows">
	<c:choose>
		<c:when test="${rows.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>
	<tr class="<c:out value='${tabclass}'/>">
		<td width="5px;"><c:out value='${rows.index+1}'/></td>
		<td align="left">${results.member.code}</td>
		<td align="left">${results.member.name}</td>
		<td align="left">
		<c:if test="${results.orderId != null}">
			<c:forEach var="ords" items="${results.orderId}" varStatus="rows2">
			<a href="#" onclick="javascript:viewOrder('${ords.id}');">
			${ords.orderNo} ÂÍ´ÃÇÁ&nbsp;<fmt:formatNumber pattern="#,##0.00" value="${ords.netAmount}"/></a>
			<c:if test="${ords.payment=='N'}">
				<span id="order_${ords.id}">
				&nbsp;ÃÑºªÓÃÐ
				<a href="#" onclick="javascript:autoReceipt('${ords.id}','${ords.customerId}','<fmt:formatNumber pattern="#,##0.00" value="${ords.netAmount}"/>');">
				<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif" align="absmiddle"></a>
				</span>
			</c:if>
			<br>
			</c:forEach>
		</c:if>
		</td>
	</tr>
	</c:forEach>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td align="left" class="footer">&nbsp;</td>
	</tr>
</table>
</c:if>						