<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.web.shop.ShopBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<jsp:useBean id="shopForm" class="com.isecinc.pens.web.shop.ShopForm" scope="session" />

<script type="text/javascript">
function search(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=search&action=newsearch&pageName=<%=request.getParameter("pageName")%>";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=search&pageName=<%=request.getParameter("pageName")%>&currPage="+currPage;
    form.submit();
    return true;
}
</script>

<c:if test="${shopForm.results != null}">
<%if(shopForm.getBean().getReportType().equalsIgnoreCase("Detail")){ %>	
	 <jsp:include page="../../pageing.jsp">
       <jsp:param name="totalPage" value="<%=shopForm.getTotalPage() %>"/>
       <jsp:param name="totalRecord" value="<%=shopForm.getTotalRecord() %>"/>
       <jsp:param name="currPage" value="<%=shopForm.getCurrPage() %>"/>
       <jsp:param name="startRec" value="<%=shopForm.getStartRec() %>"/>
       <jsp:param name="endRec" value="<%=shopForm.getEndRec() %>"/>
        </jsp:include>
		<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
	        <tr>
	            <th rowspan="2">Promotion หลัก</th>
				<th rowspan="2">Promotion</th>
				<th rowspan="2">จากวันที่</th>
				<th rowspan="2">ถึงวันที่</th>
				<th rowspan="2">สีไซร์</th>
				<th rowspan="2">Pens Item</th>
				<th rowspan="2">ยอดขายชิ้น</th>
				<th rowspan="2">ขายปลีก</th>
				<th rowspan="2" >ขายส่ง</th>
				<th colspan="2">ส่วนลดลูกค้า</th>
				<th rowspan="2">ยอดขาย หักส่วนลดลูกค้า</th>
				<th rowspan="2">ไทยวาโก้ เก็บชดเชย</th>
				<th rowspan="2">PENS เก็บค่าบริการขาย 6%</th>
			 </tr>
			  <tr>
			    <th>%</th>
			    <th>AMT</th>
			  </tr>
			<% 
			String tabclass ="lineE";
			List<ShopBean> resultList = shopForm.getResults();
			for(int n=0;n<resultList.size();n++){
				ShopBean mc = (ShopBean)resultList.get(n);
				if(n%2==0){
					tabclass="lineO";
				}
				%>
					<tr class="<%=tabclass%>">
						<td class="td_text_center" width="10%"><%=mc.getPromoName()%></td>
						<td class="td_text_center" width="10%"><%=mc.getSubPromoName()%></td>
						<td class="td_text_center" width="5%"><%=mc.getStartDate()%></td>
					    <td class="td_text_center" width="5%"><%=mc.getEndDate()%></td>
						<td class="td_text_center" width="7%"><%=mc.getStyle()%></td>
						<td class="td_text_center" width="7%"><%=mc.getPensItem()%></td>
						<td class="td_text_center" width="5%"><%=mc.getQty()%></td>
						<td class="td_text_center" width="5%"><%=mc.getRetailSellAmt()%></td>
						<td class="td_text_center" width="5%"><%=mc.getWholeSellAmt()%></td>
						<td class="td_text_center" width="8%"><%=mc.getDiscountPercent()%></td>
						<td class="td_text_center" width="8%"><%=mc.getDiscountAmt()%></td>
						<td class="td_text_center" width="8%"><%=mc.getSellAfDisc()%></td>
						<td class="td_text_center" width="8%"><%=mc.getWacoalAmt()%></td>
						<td class="td_text_center" width="8%"><%=mc.getPensAmt()%></td>
					</tr>
			<%}%>
	</table>
	<%}else{ %>
	
	 <jsp:include page="../../pageing.jsp">
       <jsp:param name="totalPage" value="<%=shopForm.getTotalPage() %>"/>
       <jsp:param name="totalRecord" value="<%=shopForm.getTotalRecord() %>"/>
       <jsp:param name="currPage" value="<%=shopForm.getCurrPage() %>"/>
       <jsp:param name="startRec" value="<%=shopForm.getStartRec() %>"/>
       <jsp:param name="endRec" value="<%=shopForm.getEndRec() %>"/>
        </jsp:include>
		<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
	        <tr>
				<th rowspan="2">ชื่อโปรโมชั่น</th>
				<th rowspan="2">ยอดขายชิ้น</th>
				<th rowspan="2">ขายปลีก</th>
				<th rowspan="2" >ขายส่ง</th>
				<th colspan="2">ส่วนลดลูกค้า</th>
				<th rowspan="2">ยอดขาย หักส่วนลดลูกค้า</th>
				<th rowspan="2">ไทยวาโก้ เก็บชดเชย</th>
				<th rowspan="2">PENS เก็บค่าบริการขาย 6%</th>
			 </tr>
			  <tr>
			    <th>%</th>
			    <th>AMT</th>
			  </tr>
			<% 
			try{
			String tabclass ="lineE";
			List<ShopBean> resultList = shopForm.getResults();
			for(int n=0;n<resultList.size();n++){
				ShopBean mc = (ShopBean)resultList.get(n);
				if(n%2==0){
					tabclass="lineO";
				}
				%>
					<tr class="<%=tabclass%>">
						<td class="td_text_center" width="10%"><%=mc.getPromoName()%></td>
						<td class="td_text_center" width="7%"><%=mc.getQty()%></td>
						<td class="td_text_center" width="8%"><%=mc.getRetailSellAmt()%></td>
						<td class="td_text_center" width="8%"><%=mc.getWholeSellAmt()%></td>
						<td class="td_text_center" width="8%"><%=mc.getDiscountPercent()%></td>
						<td class="td_text_center" width="8%"><%=mc.getDiscountAmt()%></td>
						<td class="td_text_center" width="8%"><%=mc.getSellAfDisc()%></td>
						<td class="td_text_center" width="8%"><%=mc.getWacoalAmt()%></td>
						<td class="td_text_center" width="8%"><%=mc.getPensAmt()%></td>
					</tr>
			<%}
			}catch(Exception e){
				e.printStackTrace();
			}
			%>
	</table>
<%} %>
</c:if>
				
		      