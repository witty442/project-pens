<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>Insert title here</title>

<script type="text/javascript" src="/pens_help/js/jquery-1.10.0.js"></script> 
<script type="text/javascript" src="/pens_help/js/jquery.stickytable.js"></script> 
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery.stickytable.css?" type="text/css" />
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
</style>
</head>
<body>
Original Page:/pens_sa/SalesTargetPDSearch.jsp
 <textarea rows="15" cols="200">
        .sticky-table {
	    max-width: < %(String)session.getAttribute("screenWidth")_%>px;
	    max-height: 70vh;
	    overflow: auto;
	    border-top: 1px solid #ddd;
	    border-bottom: 1px solid #ddd;
	    padding: 0 !important;
	    transition: width 2s; 
	}
	  <div class="sticky-table sticky-ltr-cells">
		    <table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="2" 
				   class="table table-striped" width="100%">
				<thead>
			       <tr  class="sticky-header">
					<th>รหัส<br/>สินค้า</th>
					<th>บาร์โค้ด</th>
					<th>รายละเอียดสินค้า</th>
					<th>บรรจุ</th>
				  </tr>
				</thead>
				<tbody>
				 <tr><td></td></tr>
				</tbody>
				</table>
		</div>
   </textarea>
          <div class="sticky-table sticky-ltr-cells">
		    <table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="2" 
				   class="table table-striped" width="100%">
				<thead>
			       <tr  class="sticky-header">
					<th>รหัส<br/>สินค้า</th>
					<th>บาร์โค้ด</th>
					<th>รายละเอียดสินค้า</th>
					<th>บรรจุ</th>
				  </tr>
				</thead>
			<tbody>
			  <%for (int i=1;i<20;i++){ %>
			    <tr class='lineE'>
					<td>xxx</td>
					<td>xxx</td>
					<td>xxx</td>
					<td>xxx</td>
				  </tr>
			<%} %>
			</tbody>
		   </table>
		</div>
</body>
</html>