<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.web.stockonhand.StockOnhandAction"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script> 
<jsp:useBean id="stockOnhandForm" class="com.isecinc.pens.web.stockonhand.StockOnhandForm" scope="session" />

<script type="text/javascript">
	//'.tbl-content' consumed little space for vertical scrollbar, scrollbar width depend on browser/os/platfrom. Here calculate the scollbar width .
	$(window).on("load resize ", function() {
	  var scrollWidth = $('.tbl-content').width() - $('.tbl-content table').width();
	  $('.tbl-header').css({'padding-right':scrollWidth});
	}).resize();
</script>

<style>
 
.table_fix{
  width:100%;
  table-layout: fixed;
  background: -webkit-linear-gradient(left, #25c481, #25b7c4);
  background: linear-gradient(to right, #25c481, #25b7c4);
  font-family: 'Roboto', sans-serif;
}
.tbl-header{
  background-color: rgba(255,255,255,0.3);
 }
.tbl-content{
  height:300px;
  overflow-x:auto;
  margin-top: 0px;
  border: 1px solid rgba(255,255,255,0.3);
}
TABLE.table_fix th{
  padding: 20px 15px;
  text-align: center;
  height: 30px;
}

TABLE.table_fix td{
  padding: 15px;
  text-align: left;
  vertical-align:middle;
  font-weight: 300;
  font-size: 13px;
  color: #fff;
  border-bottom: solid 1px rgba(255,255,255,0.1);
}

/* follow me template */
.made-with-love {
  margin-top: 40px;
  padding: 10px;
  clear: left;
  text-align: center;
  font-size: 10px;
  font-family: arial;
  color: #fff;
}
.made-with-love i {
  font-style: normal;
  color: #F50057;
  font-size: 14px;
  position: relative;
  top: 2px;
}
.made-with-love a {
  color: #fff;
  text-decoration: none;
}
.made-with-love a:hover {
  text-decoration: underline;
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
				  <input type="button" value="บันทึกข้อมูล" class="newPosBtnLong" id="save"> 
				</a>
				<a href="javascript:save('saveFinishPreOrder')">
				  <input type="button" value=" Finish " class="newPosBtnLong"> 
				</a>	
		  <%} %>			
 	</div>
 <% }%>
 	
<!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreenNoJquery.jsp"/> 
		

	