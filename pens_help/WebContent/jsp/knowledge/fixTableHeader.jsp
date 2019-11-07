<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>Insert title here</title>

<script type="text/javascript" src="/pens_help/js/jquery-1.10.0.js"></script> 
<script type="text/javascript" src="/pens_help/js/jquery.stickytable.js"></script> 
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
    
    background-color: #03A4B6;
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
    
    background-color: #03A4B6;
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