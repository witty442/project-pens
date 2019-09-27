<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.web.promotion.PromotionBean"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.Utils"%>
<jsp:useBean id="promotionForm" class="com.isecinc.pens.web.promotion.PromotionForm" scope="session" />
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<c:if test="${promotionForm.resultsSearch != null}">
        <% 
		   int totalPage = promotionForm.getTotalPage();
		   int totalRecord = promotionForm.getTotalRecord();
		   int currPage =  promotionForm.getCurrPage();
		   int startRec = promotionForm.getStartRec();
		   int endRec = promotionForm.getEndRec();
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
						<th >No</th>
						<th >ภาคการขาย</th>
						<th >พนักงานขาย</th>
						<th >วันที่ทำรายการ</th>
						<th >เลขที่เอกสาร</th>
						<th >รหัสร้านค้า</th>
						<th >ชื่อร้านค้า</th>
						<th> แบรนด์</th>
						<th> ประเภท</th>
						<th >แก้ไข</th>						
				   </tr>
				<% 
				String tabclass ="";
				List<PromotionBean> resultList = promotionForm.getResultsSearch();
				for(int n=0;n<resultList.size();n++){
					PromotionBean item = (PromotionBean)resultList.get(n);
					if(n%2==0){ 
						tabclass="lineO";
					}else{
						tabclass ="lineE";
					}
					%>
						<tr class="<%=tabclass%>">
							<td class="td_text_center" width="2%"><%=no %></td>
							<td class="td_text_center" width="5%">
							   <%=item.getSalesChannelName() %>
							</td>
							<td class="td_text" width="10%">
							   <%=item.getSalesrepCode() %>-<%=item.getSalesrepName() %>
							</td>
							<td class="td_text_center" width="5%">
								 <%=item.getRequestDate() %>
							</td>
							<td class="td_text_center" width="7%">
								 <%=item.getRequestNo() %>
							</td>
							<td class="td_text_center" width="6%"><%=item.getCustomerCode() %></td>
							<td class="td_text" width="10%"> <%=item.getCustomerName() %></td>
							
							<td class="td_text" width="10%">
								 <%=item.getBrand()+"-"+item.getBrandName()%>
							</td>
							<td class="td_text_center" width="5%">
								 <%=item.getProductTypeDesc()%> 
							</td>
							<td class="td_text_center" width="4%">
								  <a href="javascript:popupPrint('${pageContext.request.contextPath}', '<%=item.getRequestNo()%>','view')">
								      View
								  </a>
							
							  <% no++;%>
							</td>
						</tr>
				<%}//for %>
		</table>	
</c:if>		
