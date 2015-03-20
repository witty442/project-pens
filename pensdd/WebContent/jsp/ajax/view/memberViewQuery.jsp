<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.Member"%>
<%@page import="com.isecinc.pens.model.MMember"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String mName = request.getParameter("mName");
	String mId = request.getParameter("mId");
	String whereCause = "";
	Member[] results = null;

	try {
		
		User user = (User)session.getAttribute("user");
		mName = new String(mName.getBytes("ISO8859_1"), "UTF-8");
		
		if (mName != null && !mName.equals("")) {
			whereCause += " AND NAME LIKE '%" + mName + "%'";
		}
		whereCause += " AND CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "'";
		whereCause += " AND ISACTIVE = 'Y' AND CUSTOMER_ID NOT IN (" + mId + ") ORDER BY CUSTOMER_ID ";
		results = new MMember().search(whereCause);

	} catch (Exception e) {
		e.printStackTrace();
	}
%>


<%@page import="com.isecinc.pens.bean.User"%><table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<c:forEach var="results" items="<%=results %>" varStatus="rows">
	<c:choose>
		<c:when test="${rows.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>	
	<tr class="<c:out value='${tabclass}'/>" onclick="selectMember('${results.code}','${results.name}');" style="cursor: pointer; cursor: hand;">
		<td width="10%"><c:out value='${rows.index+1}'/></td>
		<td align="left">${results.code}&nbsp;&nbsp;${results.name}&nbsp;&nbsp;${results.name2}</td>
	</tr>
	</c:forEach>
</table>
