<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.Member"%>
<%@page import="com.isecinc.pens.model.MMember"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String mId = request.getParameter("mId");
	String whereCause = "";
	Member[] results = null;

	try {
		mId = new String(mId.getBytes("ISO8859_1"), "UTF-8");
		
		if(mId != null && !mId.equals("")){
			whereCause += " AND RECOMMENDED_ID = " + mId;
		}
		whereCause += " AND ISACTIVE = 'Y' ORDER BY CUSTOMER_ID ";
		results = new MMember().search(whereCause);

	} catch (Exception e) {
		e.printStackTrace();
	}
%>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order" align="center"><bean:message key="No" bundle="sysprop"/></th>
		<th class="code" align="center"><bean:message key="Member.Code" bundle="sysele"/></th>
		<th class="name" align="center"><bean:message key="Member.Name" bundle="sysele"/>-<bean:message key="Member.Surname" bundle="sysele"/></th>
		<th class="code" align="center"><bean:message key="SysConf.MemberType" bundle="sysele"/></th>
		<th class="code" align="center"><bean:message key="Other.MemberStatus" bundle="sysele"/></th>
		<th class="code" align="center"><bean:message key="Status" bundle="sysele"/></th>
		<th class="code" align="center"><bean:message key="Profile.ApplyDate" bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="<%=results %>" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO"/>
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE"/>
			</c:otherwise>
		</c:choose>	
		<tr class="<c:out value='${tabclass}'/>">
			<td align="center"><c:out value='${rows.index+1}'/></td>
			<td align="left">${results.code}</td>
			<td align="left">${results.name}&nbsp;&nbsp;${results.name2}</td>
			<td align="center">${results.memberTypeLabel}</td>
			<td align="center">${results.memberLevelLabel}</td>
			<td align="center">${results.activeLabel}</td>
			<td align="center">${results.registerDate}</td>
		</tr>
	</c:forEach>
</table>
