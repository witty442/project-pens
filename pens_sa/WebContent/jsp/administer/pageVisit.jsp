<%@page import="com.pens.util.PageVisit"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.DBConnection"%>
<%@page import="com.isecinc.pens.bean.PageVisitBean"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.web.stock.StockCreditExpireReport"%>
<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String action = Utils.isNull(request.getParameter("action"));
String pageName = Utils.isNull(request.getParameter("pageName"));
String period = Utils.isNull(request.getParameter("period"));
String startDate = Utils.isNull(request.getParameter("startDate"));
String endDate = Utils.isNull(request.getParameter("endDate"));

List<String> pageNameList = new ArrayList<String>();
List<PopupBean> periodList = null;
PageVisitBean pageVisitBean = null;
Connection conn = null;
StringBuffer h = null;
try{
	System.out.println("action:"+action);
	
	if(action.equalsIgnoreCase("search")){
		conn =DBConnection.getInstance().getConnectionApps();
		//convert criteria
		pageVisitBean = new PageVisitBean();
		pageVisitBean.setPageName(pageName);
		
		if( !"".equals(startDate)){
			System.out.println("period:"+period);
			pageVisitBean.setPeriod(period);
			pageVisitBean.setStartDate(startDate);
			pageVisitBean = PageVisit.convertCriteria(pageVisitBean); 
		}
		h = PageVisit.search(conn,request, pageVisitBean, false);
	}else{
		 session.setAttribute("PAGE_NAME_LIST" ,null);
		 
		//Get PageName List
		if(session.getAttribute("PAGE_NAME_LIST") ==null){
			conn =DBConnection.getInstance().getConnectionApps();
			
		    session.setAttribute("PAGE_NAME_LIST",PageVisit.initPageNameList(request));
		    
		   //init periodList
	        session.setAttribute("PERIOD_LIST",PageVisit.initPeriod(conn));
		}
	}//if
	
	if(session.getAttribute("PAGE_NAME_LIST") !=null){
		pageNameList =  (List)session.getAttribute("PAGE_NAME_LIST");
	}
	if(session.getAttribute("PERIOD_LIST") !=null){
		periodList =  (List)session.getAttribute("PERIOD_LIST");
	}
	

%>
<html>
<head>
<title>PageVisit</title>

<script>
function search(path){
	var form = document.pageVisitForm;
	form.submit();
	return true; 
}
function setPeriodDate(periodDesc){
	var form = document.pageVisitForm;
	//alert(periodDesc);
	//form.period.value = periodDesc.value.split("|")[0];
	form.startDate.value = periodDesc.value.split("|")[1];
	form.endDate.value = periodDesc.value.split("|")[2]; 
}

</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<form name="pageVisitForm" method="post" action="pageVisit.jsp?action=search">
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" class="body"  width="100%">
	<tr>
		<td align="right" width="50%">
		 PageName:
		</td>
		<td width="50%">
		  <select id="pageName" name="pageName">
		  <option></option>
		  <%
   			for(int i =0;i<pageNameList.size();i++){
   				if(pageName.equalsIgnoreCase(pageNameList.get(i))){
   				   out.println("<option value='"+pageNameList.get(i)+"' selected>"+pageNameList.get(i)+"</option>");
   				}else{
   				   out.println("<option value='"+pageNameList.get(i)+"'>"+pageNameList.get(i)+"</option>");
   				}
   			}
          %>
		  </select>
		 
		</td>
	</tr>
	<tr>
		<td align="right" width="50%">
		  Period:
		</td>
		<td width="50%">
			 <select id="period" name="period"  onchange="setPeriodDate(this)">
			  <option></option>
			  <%
	   			for(int i =0;i<periodList.size();i++){
	   				PopupBean p = periodList.get(i);
	   			//	System.out.println("period[]"+period+"]["++"]")
	   				if(period.equalsIgnoreCase(p.getValue())){
	   				   out.println("<option value='"+p.getValue()+"' selected>"+p.getKeyName()+"</option>");
	   				}else{
	   				   out.println("<option value='"+p.getValue()+"'>"+p.getKeyName()+"</option>");
	   				}
	   			}
	          %>
			  </select>
		    <input type="text" name="startDate" id="startDate" size='8' readonly value="<%=startDate%>"/>-
		    <input type="text" name="endDate" id="endDate" size='8' readonly value="<%=endDate%>"/>
		</td>
	</tr>
	<tr>
		<td align="center"  colspan="2">
			<input type="button" value="ค้นหาข้อมูล" class="newPosBtnLong" onclick="search('${pageContext.request.contextPath}')">
		<input type="button" value="ปิดหน้านี้" class="newPosBtnLong" onclick="closeForm('${pageContext.request.contextPath}')">
		</td>
	</tr>
</table>

<!-- Result -->
<%if(h!=null){out.println(h.toString());} %>

</form>
<%}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn != null){
		conn.close();
		conn= null;
	}
}
%>
</body>
</html>