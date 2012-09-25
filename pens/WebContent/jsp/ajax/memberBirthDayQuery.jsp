<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.Messages"%>
<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="util.DateToolsUtil"%>
<%@page import="com.isecinc.pens.bean.Member"%>
<%@page import="com.isecinc.pens.model.MMember"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="util.ConvertNullUtil"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	User user = (User) session.getAttribute("user");
	String dateFrom = (String) request.getParameter("dateFrom");
	String dateTo = (String) request.getParameter("dateTo");
	Member[] members = null;
	String whereCause = "";
	String error = "";
	
	try{
		// Set where cause.
		if(!dateFrom.equals("")){
			whereCause += " AND DATE_FORMAT(BIRTHDAY,'%m%d') >= DATE_FORMAT('" + DateToolsUtil.convertToTimeStamp(dateFrom) + "','%m%d') ";	
		}
		if(!dateTo.equals("")){
			whereCause += " AND DATE_FORMAT(BIRTHDAY,'%m%d') <= DATE_FORMAT('" + DateToolsUtil.convertToTimeStamp(dateTo) + "','%m%d') ";	
		}
		whereCause += " AND CUSTOMER_TYPE = 'DD' ";
		whereCause += " AND BIRTHDAY IS NOT NULL ";
		whereCause += " ORDER BY BIRTHDAY ";
		
		members = new MMember().search(whereCause);	
		if(members==null){
			members = new Member[0];
		}
		if(members.length == 0){
			error = InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc();
		}
	}catch(Exception e){
		e.printStackTrace();
		error = e.toString();
	}

int i=0;
%>
<%if(members.length > 0){%>
<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
	<bean:message key="RecordsFound" bundle="sysprop" />&nbsp;
	<span class="searchResult"><%=members.length%></span>&nbsp;
	<bean:message key="Records" bundle="sysprop" />
</div>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop" /></th>
		<th class="code"><bean:message key="Other.Birthday" bundle="sysele" /></th>
		<th class="code"><bean:message key="Member.Code" bundle="sysele" /></th>
		<th class="name"><bean:message key="Member.Name" bundle="sysele"/>-<bean:message key="Member.Surname" bundle="sysele"/></th>
		<th class="code"><bean:message key="SysConf.MemberType" bundle="sysele" /></th>
		<th class="status"><bean:message key="Other.MemberStatus" bundle="sysele"/></th>
	</tr>	
<%i=1; %>
<%for(Member o : members){ %>
<tr class="lineO">
	<td align="center" width="52px;"><%=i++ %></td>
	<td align="center" width="142px;"><%=o.getBirthDay()%></td>
	<td align="center" width="143px;"><%=o.getCode()%></td>
	<td align="left" width="233px;"><%=o.getName() + " " + o.getName2()%></td>
	<td align="center" width="143px;"><%=ConvertNullUtil.convertToString(o.getMemberTypeLabel()) %></td>
	<td align="center"><%=ConvertNullUtil.convertToString(o.getMemberLevelLabel()) %></td>
</tr>
<%} %>
	<tr>
		<td class="footer" colspan="6">&nbsp;</td>
	</tr>	
</table>
<script language="javascript">
	$('#msg').html('');
</script>
<%}else{ %>
<script language="javascript">
	$('#msg').html('<%=error%>');
</script>
<%} %>