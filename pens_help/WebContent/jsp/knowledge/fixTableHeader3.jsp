<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en"><head>
<title>Pure CSS Scrollable Table with Fixed Header</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="language" content="en-us" />
<!-- <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script> -->

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script>

<script type="text/javascript">

//'.tbl-content' consumed little space for vertical scrollbar, scrollbar width depend on browser/os/platfrom. Here calculate the scollbar width .
$(window).on("load resize ", function() {
  var scrollWidth = $('.tbl-content').width() - $('.tbl-content table').width();
  $('.tbl-header').css({'padding-right':scrollWidth});
}).resize();

</script>
<style type="text/css">

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
  text-align: left;
  font-weight: 500;
  font-size: 12px;
  color: #fff;
  text-transform: uppercase;
}
TABLE.table_fix td{
  padding: 15px;
  text-align: left;
  vertical-align:middle;
  font-weight: 300;
  font-size: 12px;
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


/* for custom scrollbar for webkit browser*/

/* ::-webkit-scrollbar {
    width: 6px;
} 
::-webkit-scrollbar-track {
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3); 
} 
::-webkit-scrollbar-thumb {
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3); 
} */

</style>
</head>
<body>

  <!--for demo wrap-->
  <div class="tbl-header">
    <table cellpadding="0" cellspacing="0" border="1" class='table_fix'>
      <thead>
        <tr>
          <th width="5%">Code</th>
          <th width="20%">Company</th>
          <th width="5%">Price</th>
          <th width="5%">Change</th>
          <th width="5%">Change %</th>
          <th colspan="2" width="10%">test colspan</th>
        </tr>
         <tr>
          <th width="5%">Code</th>
          <th width="20%">Company</th>
          <th width="5%">Price</th>
          <th width="5%">Change</th>
          <th width="5%">Change %</th>
          <th width="5%">test colspan 1</th>
          <th width="5%">test colspan 2</th>
        </tr>
      </thead>
    </table>
  </div>
  <div class="tbl-content">
    <table cellpadding="0" cellspacing="0" border="1" class='table_fix'>
      <tbody>
       <% for(int i=0;i<10;i++){ %>
        <tr>
          <td width="5%">AAC</td>
          <td width="20%">AUSTRALIAN COMPANY </td>
          <td width="5%">$1.38</td>
          <td width="5%">+2.01</td>
          <td width="5%">-0.36%</td>
          <td width="5%">111</td>
          <td width="5%">222</td>
        </tr>
       <%} %>
      </tbody>
    </table>
  </div>

</body></html>