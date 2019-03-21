<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.web.prodshow.ProdShowForm"%>
<%@page import="com.isecinc.pens.web.prodshow.ProdShowBean"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<jsp:useBean id="prodShowForm" class="com.isecinc.pens.web.prodshow.ProdShowForm" scope="session" />
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<c:if test="${prodShowForm.resultsSearch != null}">
        <% 
		   int totalPage = prodShowForm.getTotalPage();
		   int totalRecord = prodShowForm.getTotalRecord();
		   int currPage =  prodShowForm.getCurrPage();
		   int startRec = prodShowForm.getStartRec();
		   int endRec = prodShowForm.getEndRec();
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
						<th >รหัสค้า</th>
						<th >ชื่อร้านค้า</th>
						<th >เลขที่ตั้งกอง</th>
						<th >วันที่ทำรายการ</th>
						<th> แบรนด์</th>
						<th> รูปที่ 1</th>
						<th> รูปที่ 2</th>
						<th >รูปที่ 3</th>
						<th >แก้ไข</th>						
				   </tr>
				<% 
				String tabclass ="";
				List<ProdShowBean> resultList = prodShowForm.getResultsSearch();
				for(int n=0;n<resultList.size();n++){
					ProdShowBean item = (ProdShowBean)resultList.get(n);
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
							<td class="td_text" width="11%">
							   <%=item.getSalesrepCode() %>-<%=item.getSalesrepName() %>
							</td>
							<td class="td_text_center" width="6%"><%=item.getCustomerCode() %></td>
							<td class="td_text" width="12%"> <%=item.getCustomerName() %></td>
							<td class="td_text_center" width="9%">
								 <%=item.getOrderNo() %>
							</td>
							<td class="td_text_center" width="5%">
								 <%=item.getShowDate() %>
							</td>
							<td class="td_text" width="11%">
								 <%=item.getBrand()+"-"+item.getBrandName()%>
							</td>
							<td class="td_text" width="10%">
							    <a href="javascript:openImage('${pageContext.request.contextPath}', '<%=item.getPic1()%>')">
								  <%=item.getPic1() %>
								</a>
							</td>
							<td class="td_text" width="10%">
								<a href="javascript:openImage('${pageContext.request.contextPath}', '<%=item.getPic2()%>')">
								  <%=item.getPic2() %>
								</a>
							</td>
							<td class="td_text" width="10%">
								 <a href="javascript:openImage('${pageContext.request.contextPath}', '<%=item.getPic3()%>')">
								  <%=item.getPic3() %>
								</a>
							</td>
							<td class="td_text_center" width="4%">
								  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=item.getOrderNo()%>','view')">
								      View
								  </a>
							
							  <% no++;%>
							</td>
						</tr>
				<%}//for %>
		</table>	
</c:if>		
