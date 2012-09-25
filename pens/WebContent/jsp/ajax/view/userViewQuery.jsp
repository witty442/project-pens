<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String uName = request.getParameter("uName");
	String whereCause = "";
	User[] results = null;

	try {
		uName = new String(uName.getBytes("ISO8859_1"), "UTF-8");
		if (uName != null && !uName.equals("")) {
			whereCause += " AND NAME LIKE '%" + uName + "%'";
		}
		// A-neak.t 27/12/2010
		whereCause += " AND ROLE <> 'AD' ";
		
		whereCause += " AND ISACTIVE = 'Y' ORDER BY USER_ID ";
		
		results = new MUser().search(whereCause);

	} catch (Exception e) {
		e.printStackTrace();
	}
%>

<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<c:forEach var="results" items="<%=results %>" varStatus="rows">
	<c:choose>
		<c:when test="${rows.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>	
	<tr class="<c:out value='${tabclass}'/>" onclick="selectUser('${results.code}','${results.name}');" style="cursor: pointer; cursor: hand;">
		<td width="10%"><c:out value='${rows.index+1}'/></td>
		<td align="left">${results.code}&nbsp;&nbsp;${results.name}</td>
	</tr>
	</c:forEach>
</table>
