<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.web.stockonhand.StockOnhandAction"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.stickytable.js"></script> 
<jsp:useBean id="stockOnhandForm" class="com.isecinc.pens.web.stockonhand.StockOnhandForm" scope="session" />
<style>
 .sticky-table {
    max-width: <%=(String)session.getAttribute("screenWidth")%>px;
    max-height: 70vh;
    overflow: auto;
    border-top: 1px solid #ddd;
    border-bottom: 1px solid #ddd;
    padding: 0 !important;
    transition: width 2s; 
}
.sticky-table, .sticky-table * {
    -webkit-transition: all 0s;
    -moz-transition: all 0s;
    -o-transition: all 0s;
    transition: all 0s;
}
.sticky-table table {
    margin-bottom: 0;
    width: 100%;
    max-width: 100%;
    border-spacing: 0;
    padding: 0 !important;
    border-collapse: collapse;
    
	text-align: center;
	COLOR: #00000;
}

.sticky-table table tr.sticky-header th, .sticky-table table tr.sticky-header td,
.sticky-table table tr.sticky-footer th, .sticky-table table tr.sticky-footer td {
    background-color: #fff;
    border-top: 0;
    position: relative;
    position: -webkit-sticky;
    position: -ms-sticky;
    position: sticky;
  /*   outline: 1px solid #ddd; */
    z-index: 5;
    
	text-align: center;
	height: 30px;
	COLOR: #00000;
}
.sticky-table table tr.sticky-header th, .sticky-table table tr.sticky-header td {
    top: 0;
}
.sticky-table table tr.sticky-footer th, .sticky-table table tr.sticky-footer td {
    bottom: 0;
}
.sticky-table table td.sticky-cell, .sticky-table table th.sticky-cell,
.sticky-table table td.sticky-cell-opposite, .sticky-table table th.sticky-cell-opposite {
    background-color: #fff;
    outline: 1px solid #ddd;
    position: relative;
    position: -webkit-sticky;
    position: -ms-sticky;
    position: sticky;
    z-index: 10;
}
.sticky-table.sticky-ltr-cells table td.sticky-cell, .sticky-table.sticky-ltr-cells table th.sticky-cell,
.sticky-table.sticky-rtl-cells table td.sticky-cell-opposite, .sticky-table.sticky-rtl-cells table th.sticky-cell-opposite {
    left: 0
}
.sticky-table.sticky-rtl-cells table td.sticky-cell, .sticky-table.sticky-rtl-cells table th.sticky-cell,
.sticky-table.sticky-ltr-cells table td.sticky-cell-opposite, .sticky-table.sticky-ltr-cells table th.sticky-cell-opposite {
    right: 0
}
.sticky-table table tr.sticky-header td.sticky-cell, .sticky-table table tr.sticky-header th.sticky-cell,
.sticky-table table tr.sticky-header td.sticky-cell-opposite, .sticky-table table tr.sticky-header th.sticky-cell-opposite,
.sticky-table table tr.sticky-footer td.sticky-cell, .sticky-table table tr.sticky-footer th.sticky-cell,
.sticky-table table tr.sticky-footer td.sticky-cell-opposite, .sticky-table table tr.sticky-footer th.sticky-cell-opposite {
    z-index: 15;
}
</style> 
<%
  String pageName = Utils.isNull(request.getParameter("pageName"));
  System.out.println("screen status:"+stockOnhandForm.getBean().getStatus());
  if(request.getAttribute("stockOnhandForm_RESULT") != null){
	  out.print(((StringBuffer)request.getAttribute("stockOnhandForm_RESULT")).toString());
%>
 	<!-- Button -->
 	<div id="btn_action" align="center">
		  <%if( !stockOnhandForm.getBean().getStatus().equalsIgnoreCase("FINISH")){ %>
				<a href="javascript:save('saveDB')">
				  <input type="button" value="�ѹ�֡������" class="newPosBtnLong" id="save"> 
				</a>
				<a href="javascript:save('saveFinishPreOrder')">
				  <input type="button" value=" Finish " class="newPosBtnLong"> 
				</a>	
		  <%} %>			
 	</div>
 <% }%>
 	
<!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreenNoJquery.jsp"/> 
		

	