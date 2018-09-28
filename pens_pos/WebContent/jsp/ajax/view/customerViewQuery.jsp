<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	User user = null;
	String cName = request.getParameter("cName");
	String isMain = request.getParameter("main");
	String uId = null;
	String id = request.getParameter("id");
	if(request.getParameter("uId")!=null){
		uId = request.getParameter("uId");
		user = new MUser().find(uId);
	}
	
	String name1 = "";
	String name2 = "";
	String[] fullname = null;
	String whereCause = "";
	Customer[] results = null;

	try {
		cName = new String(cName.getBytes("ISO8859_1"), "UTF-8");
		fullname = cName.split(" ");
		if (fullname[0] != null && !fullname[0].equals("")) {
			whereCause += " AND NAME LIKE '%" + fullname[0] + "%'";
		}
		if (fullname.length > 1 && fullname[1] != null && !fullname[1].equals("")) {
			whereCause += " AND NAME2 LIKE '%" + fullname[1] + "%'"; 
		}
		whereCause += " AND CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "'";
		if(!isMain.equals("Y")){
			if(!user.getCustomerType().getKey().equalsIgnoreCase(Customer.DIREC_DELIVERY)){
				whereCause += " AND USER_ID = " + user.getId();
				
			}	
		}
		if(id != null && !id.equals(""))
			whereCause += " AND CUSTOMER_ID <> " + id;
			
		whereCause += " AND ISACTIVE = 'Y' ORDER BY CUSTOMER_ID ";
		
		results = new MCustomer().search(whereCause);

	} catch (Exception e) {
		e.printStackTrace();
	}
%>


<%@page import="com.isecinc.pens.model.MUser"%><table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<c:forEach var="results" items="<%=results %>" varStatus="rows">
	<c:choose>
		<c:when test="${rows.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>	
	<tr class="<c:out value='${tabclass}'/>" onclick="selectCustomer('${results.code}','${results.name}','${results.name2}');" style="cursor: pointer; cursor: hand;">
		<td width="10%"><c:out value='${rows.index+1}'/></td>
		<td align="left">${results.code}&nbsp;&nbsp;${results.name}</td>
	</tr>
	</c:forEach>
</table>
