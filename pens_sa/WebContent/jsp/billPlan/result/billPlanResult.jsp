
<%@page import="com.isecinc.pens.web.billplan.BillPlanBean"%>
<%@page import="java.util.List"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="billPlanForm" class="com.isecinc.pens.web.billplan.BillPlanForm" scope="session" />
<style>
.lineError_2{
  background: #FFA07A;
}
.lineE_2 {
	background-color: #e7eefa;
}
.lineO_2 {
	background-color: #e7eefa;
}
.lineWarning{
  background: #F9E79F;
}
</style>


	<c:if test="${billPlanForm.resultsSearch != null}">
        <% 
		   int totalPage = billPlanForm.getTotalPage();
		   int totalRecord = billPlanForm.getTotalRecord();
		   int currPage =  billPlanForm.getCurrPage();
		   int startRec = billPlanForm.getStartRec();
		   int endRec = billPlanForm.getEndRec();
		   int no = startRec;
		%>
		<div align="left">
		   <span class="pagebanner">รายการทั้งหมด  <%=totalRecord %> รายการ, แสดงรายการที่  <%=startRec %> ถึง  <%=endRec %>.</span>
		   <span class="pagelinks">
			หน้าที่ 
			    <% 
				 for(int r=0;r<totalPage;r++){
					 if(currPage ==(r+1)){
				 %>
 				   <strong><%=(r+1) %></strong>
				 <%}else{ %>
				    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  
				       title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
			 <% }} %>				
			</span>
		</div>
			<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="1" class="tableSearch">
			       <tr>
						<th >ภาคการดูแล</th>
						<th >รหัสพนักงานขาบ</th>
						<th >ชื่อพนักงาน</th>
						<th >เลขที่ Bill-T</th>
						<th >วันที่ Bill-T</th> 
						<% if("detail".equalsIgnoreCase(billPlanForm.getBean().getDispType())){ %>
						<th> รหัสสินค้า</th>
						<th >ชื่อสินค้า</th>
						<%} %>
						<th >จำนวนที่ Plan ไป</th>
				   </tr>
				<% 
				String tabclass ="";
				List<BillPlanBean> resultList = billPlanForm.getResultsSearch();
				for(int n=0;n<resultList.size();n++){
					BillPlanBean item = (BillPlanBean)resultList.get(n);
					if(n%2==0){ 
						tabclass="lineO_2";
					}else{
						tabclass ="lineE_2";
					}
				
					%>
						<tr class="<%=tabclass%>">
							<td class="td_text_center" width="10%"><%=item.getSalesZone() %></td>
							<td class="td_text_center" width="5%"><%=item.getSalesrepCode() %></td>
							<td class="td_text" width="10%" nowrap><%=item.getSalesrepName() %></td>
							<td class="td_text_center" width="5%"><%=item.getBillTNo() %></td>
							<td class="td_text_center" width="5%"> <%=item.getBillTDate() %></td>
							<% if("detail".equalsIgnoreCase(billPlanForm.getBean().getDispType())){ %>
							  <td class="td_text" width="7%"> <%=item.getItem() %></td>
							  <td class="td_text" width="15%"><%=item.getItemName()%></td>
							<%} %>
							<td class="td_number" width="5%"> <%=item.getPlanQty() %></td>
						</tr>
				<%      no++;
				   }//for %>
		</table>	
</c:if>		
