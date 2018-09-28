<%@page import="com.isecinc.pens.web.moveorder.MoveOrderBean"%>
<%@page import="java.util.List"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<jsp:useBean id="moveOrderForm" class="com.isecinc.pens.web.moveorder.MoveOrderForm" scope="session" />
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<c:if test="${moveOrderForm.resultsSearch != null}">
        <% 
		   int totalPage = moveOrderForm.getTotalPage();
		   int totalRecord = moveOrderForm.getTotalRecord();
		   int currPage =  moveOrderForm.getCurrPage();
		   int startRec = moveOrderForm.getStartRec();
		   int endRec = moveOrderForm.getEndRec();
		   int no = startRec;
		%>
		<div align="left">
		   <span class="pagebanner">��¡�÷�����  <%=totalRecord %> ��¡��, �ʴ���¡�÷��  <%=startRec %> �֧  <%=endRec %>.</span>
		   <span class="pagelinks">
			˹�ҷ�� 
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
						<th rowspan='2'>No</th>
						<th rowspan='2'>������ �͡���</th>
						<th rowspan='2'>�Ţ��� �͡���</th>
						<th rowspan='2'>�ѹ��� �͡���</th> 
						<th rowspan='2'>���� ��ѡ�ҹ���</th>
						<th rowspan='2'>���� ��ѡ�ҹ���</th>
						<th rowspan='2'> PD</th>
						<th rowspan='2'> ���� PD</th>
						<th rowspan='2'> ����պ</th>
						<th rowspan='2'>������</th>
						<th rowspan='2'>��Ť���Թ��� ����͡���</th>			
						<th rowspan='2'>�ӹǹ �ѹ�Թ�ҧ �ͧ�͡���</th>
						<th rowspan='2'>�ѹ��� �Ѻ�͡��� �ҡ�Ţ�</th>		
						<th rowspan='2'>�˵ؼ�</th>	
						<th rowspan='2'>�����˵�</th>
						<th colspan="2">����͡��è�ԧ</th>
				   </tr>
				   <tr>
				      <th >����պ</th>
				      <th >������</th>
				   </tr>
				<% 
				String tabclass ="";
				List<MoveOrderBean> resultList = moveOrderForm.getResultsSearch();
				for(int n=0;n<resultList.size();n++){
					MoveOrderBean item = (MoveOrderBean)resultList.get(n);
					if(n%2==0){ 
						tabclass="lineO_2";
					}else{
						tabclass ="lineE_2";
					}
					//set background No receive
					if(Utils.isNull(item.getStatusDate()).equals("")){
						if(item.getMoveDay() > moveOrderForm.getBean().getMoveDay()){
						   tabclass = "lineError_2";
						}
					}else{
						if(item.getMoveDay() > moveOrderForm.getBean().getMoveDay()){
						   tabclass = "lineWarning";
						}
					}
					%>
						<tr class="<%=tabclass%>">
							<td class="td_text_center" width="2%"><%=no %></td>
							<td class="td_text_center" width="4%"><%=item.getDocType() %></td>
							<td class="td_text" width="8%" nowrap><%=item.getRequestNumber() %></td>
							<td class="td_text_center" width="4%"><%=item.getRequestDate() %></td>
							<td class="td_text_center" width="4%"> <%=item.getSalesrepCode() %></td>
							<td class="td_text" width="8%"> <%=item.getSalesrepName() %></td>
							<td class="td_text_center" width="3%"><%=item.getPdCode()%></td>
							<td class="td_text" width="10%"> <%=item.getPdName() %></td>
							<td class="td_number" width="3%"><%=item.getCtnQty() %></td>
							<td class="td_number" width="3%"><%=item.getPcsQty() %></td>
							<td class="td_number" width="6%"><%=item.getAmount() %></td>
							<td class="td_number" width="5%"><%=item.getMoveDay() %></td>
							<td class="td_text" width="5%"><%=item.getStatusDate()%></td>
							<td class="td_text" width="9%"><%=item.getReason()%></td>
							<td class="td_text" width="9%"><%=item.getRemark()%></td>
							<td class="td_number" width="5%"><%=item.getRealCtnQty()%></td>
						    <td class="td_number" width="5%"><%=item.getRealPcsQty()%></td>
						</tr>
				<%      no++;
				   }//for %>
		</table>	
</c:if>		
