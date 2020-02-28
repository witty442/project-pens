<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.web.prodshow.ProdShowForm"%>
<%@page import="com.isecinc.pens.web.prodshow.ProdShowBean"%>
<%@page import="com.pens.util.Utils"%>
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
		<%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
		<div align="center">
			<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="1" class="tableSearch">
			       <tr>
						<th >No</th>
						<th >�Ҥ��â��</th>
						<th >��ѡ�ҹ���</th>
						<th >���ʤ��</th>
						<th >������ҹ���</th>
						<th >�Ţ����駡ͧ</th>
						<th >�ѹ������¡��</th>
						<th> �ù��</th>
						<th> �ٻ��� 1</th>
						<th> �ٻ��� 2</th>
						<th >�ٻ��� 3</th>
						<th >���</th>						
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
	</div>	
</c:if>		
