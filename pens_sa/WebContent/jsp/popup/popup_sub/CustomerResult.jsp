<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%
String selectone = Utils.isNull(request.getParameter("selectone"));
%>
<%if("false".equalsIgnoreCase(selectone)) {%>
	<display:table style="width:100%;" id="item" name="sessionScope.DATA_LIST" 
	    defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
	    <display:column  style="text-align:center;" title="เลือกข้อมูล"  sortable="false" class="chk">
			<input type ="checkbox" name="chCheck" id="chCheck" onclick="saveSelectedInPage(${item.no})"  />
			<input type ="hidden" name="code_temp" value="<bean:write name="item" property="code"/>" />
			<input type ="hidden" name="desc" value="<bean:write name="item" property="desc"/>" />
		 </display:column>
	    											    
	    <display:column  title="Customer Code" property="code"  sortable="false" class="code"/>
	    <display:column  title="Customer Name" property="desc" sortable="false" class="desc"/>								
	</display:table>	
<%}else{ %>
    <display:table style="width:100%;" id="item" name="sessionScope.DATA_LIST" 
	    defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
	    <display:column  style="text-align:center;" title="เลือกข้อมูล"  sortable="false" class="chk">
			<input type ="radio" name="chCheck" id="chCheck" onclick="saveSelectedInPage(${item.no})"  />
			<input type ="hidden" name="code_temp" value="<bean:write name="item" property="code"/>" />
			<input type ="hidden" name="desc" value="<bean:write name="item" property="desc"/>" />
		 </display:column>
	    											    
	    <display:column  title="Customer Code" property="code"  sortable="false" class="code"/>
	    <display:column  title="Customer Name" property="desc" sortable="false" class="desc"/>								
	</display:table>
<%}%>