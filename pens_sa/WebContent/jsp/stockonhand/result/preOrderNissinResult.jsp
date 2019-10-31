<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.web.stockonhand.StockOnhandAction"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<jsp:useBean id="stockOnhandForm" class="com.isecinc.pens.web.stockonhand.StockOnhandForm" scope="session" />
<%
  String pageName = Utils.isNull(request.getParameter("pageName"));
  System.out.println("screen status:"+stockOnhandForm.getBean().getStatus());
  if(request.getAttribute("stockOnhandForm_RESULT") != null){
	  out.print(((StringBuffer)request.getAttribute("stockOnhandForm_RESULT")).toString());
%>
 	<!-- Button -->
 	 <%if(StockOnhandAction.PAGE_PREODER_NISSIN.equalsIgnoreCase(pageName)) {%>  
		 <table  border="0" cellpadding="3" cellspacing="0" align="center">
			<tr>
				<td align="left">
				   <%if( !stockOnhandForm.getBean().getStatus().equalsIgnoreCase("FINISH")){ %>
						<a href="javascript:save('saveDB')">
						  <input type="button" value="บันทึกข้อมูล" class="newPosBtnLong"> 
						</a>
						<a href="javascript:save('saveFinishPreOrder')">
						  <input type="button" value=" Finish " class="newPosBtnLong"> 
						</a>	
				  <%} %>			
				</td>
			</tr>
		</table>
 	 <%} %>
 <% }%>

	