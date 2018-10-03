<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.bean.SalesTargetNew"%>
<%@page import="com.isecinc.pens.model.MSalesTargetNew"%>
<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="com.isecinc.core.bean.Messages"%>
<%
User user = (User) session.getAttribute("user");
String dateFrom = (String)request.getParameter("datefrom");
String dateTo = (String)request.getParameter("dateto");

SalesTargetNew[] stn = null;

String error = "";
double totalTargetAmount = 0;;
try{
	String whereCause = "  and user_id = "+user.getId();
	
	if(dateFrom.length()>0 && dateTo.length()==0){
		whereCause+=" and target_to >= '"+DateToolsUtil.convertToTimeStamp(dateFrom)+"' ";
	}else
	if(dateFrom.length() == 0 && dateTo.length() > 0){
		whereCause+=" and target_from <= '"+DateToolsUtil.convertToTimeStamp(dateTo)+"' ";
	}else
	if(dateFrom.length() > 0 && dateTo.length() > 0){
		whereCause+=" and ((target_from <= '"+DateToolsUtil.convertToTimeStamp(dateFrom)+"' ";
		whereCause+=" and target_from >= '"+DateToolsUtil.convertToTimeStamp(dateTo)+"') ";
		
		whereCause+=" or (target_to <= '"+DateToolsUtil.convertToTimeStamp(dateFrom)+"' ";
		whereCause+=" and target_to >= '"+DateToolsUtil.convertToTimeStamp(dateTo)+"') ";
		
		whereCause+=" or (target_from >= '"+DateToolsUtil.convertToTimeStamp(dateFrom)+"' ";
		whereCause+=" and target_to <= '"+DateToolsUtil.convertToTimeStamp(dateTo)+"') ";
		
		whereCause+=" or (target_from <= '"+DateToolsUtil.convertToTimeStamp(dateFrom)+"' ";
		whereCause+=" and target_to >= '"+DateToolsUtil.convertToTimeStamp(dateTo)+"') ";
		
		whereCause+=")";
	}
			
	whereCause+=" order by product_id ";		
	stn =  new MSalesTargetNew().search(whereCause);
	if(stn == null)error = InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc();
	if(stn!=null){
		//for check sales amount
		new MOrderLine().compareSalesTarget(stn,null,null);
	}
}catch(Exception e){
	e.printStackTrace();
	error = e.toString();
}finally{
	
}

int i=0;
%>

<%if(stn != null){%>

<%@page import="java.text.DecimalFormat"%>
<%@page import="util.DateToolsUtil"%>
<%@page import="com.isecinc.pens.model.MOrderLine"%><div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
	<bean:message key="RecordsFound" bundle="sysprop" />&nbsp;<span class="searchResult"><%=stn.length%></span>&nbsp;
	<bean:message key="Records" bundle="sysprop" />
</div>
<!-- ORDER -->
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop"/></th>
		<th class="name"><bean:message key="Date" bundle="sysele"/></th>
		<th class="name"><bean:message key="Product" bundle="sysele"/></th>
		<th class="code"><bean:message key="Product.UOM"  bundle="sysele"/></th>
		<th class="costprice">จำนวนเป้า</th>
		<th class="costprice">เป้ายอดเงิน</th>
		<th class="costprice">จำนวนขาย</th>
		<th class="costprice">จำนวนแถม</th>
		<!-- 
		<th class="code">ยอดขายรวม</th>
		<th class="code">คิดเป็น %</th>
		 -->
	</tr>	
	<%i=1; %>
	<%for(SalesTargetNew o : stn){ 
		totalTargetAmount += o.getTargetAmount();
	%>
	<tr class="lineO">
		<td align="center" width="48px;"><%=i++ %></td>
		<td align="center" width="212px;"><%=o.getTargetFrom() %> - <%=o.getTargetTo() %></td>
		<td align="left" width="213px;"><%=o.getProduct().getCode()%> <%=o.getProduct().getName() %></td>
		<td align="center" width="130px;"><%=o.getUom().getCode()%></td>
		<td align="right" width="96px;"><%=new DecimalFormat("#,##0").format(o.getTargetQty())%></td>
		<td align="right"><%=new DecimalFormat("#,##0.00").format(o.getTargetAmount())%></td>
		<td align="right"><%=o.getBaseQty() %>/<%=o.getSubQty() %></td>
		<td align="right"><%=o.getBasePromo() %>/<%=o.getSubPromo() %></td>
		<!-- 
		<td align="right"><%=new DecimalFormat("#,##0.00").format(o.getSoldAmount())%></td>
		<td align="right"><%=new DecimalFormat("#,##0.00").format(o.getPercentCompare())%></td>
		 -->
	</tr>
	<%} %>
	<tr class="lineE">
		<td align="right" colspan="5">ยอดรวม</td>
		<td align="right" class="name"><%=new DecimalFormat("#,##0.00").format(totalTargetAmount)%></td>
		<td align="right"></td>
		<td align="right"></td>
	</tr>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td class="footer">&nbsp;</td>
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
