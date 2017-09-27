<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.inf.helper.DBConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%>
<%@page import="com.isecinc.pens.web.sales.bean.Basket"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>
<%
Connection conn = null;
List<References> productCatL = null;
int pageId  = 0;
int no_of_column = 0;
int no_of_rows =  0;
int no_of_total_display = 0;
int totalRecord =0;
int totalPage = 0;
Basket basket = null;
try{
	    conn = DBConnection.getInstance().getConnection();
		User user = ((User)session.getAttribute("user"));
		String pageId_param = request.getParameter("pageId");
		
		String custId = request.getParameter("custId");
		boolean isCustHaveProductSpecial = new MCustomer().isCustHaveProductSpecial(conn, custId);
		System.out.println("isCustHaveProductSpecial:"+isCustHaveProductSpecial);
		
		basket = (Basket)session.getAttribute(custId);
		if(basket == null ){
			basket = new Basket();	
		}
		
		if(StringUtils.isEmpty(pageId_param))
			pageId_param = "0";
		
		pageId = Integer.valueOf(pageId_param);
		
		MProductCategory mProductCat = new MProductCategory();
		 productCatL = mProductCat.lookUpBrandList(conn,pageId,user,isCustHaveProductSpecial);
		
		 no_of_column = MProductCategory.NO_OF_DISPLAY_COLUMNS;
		 no_of_rows =  MProductCategory.NO_OF_DISPLAY_ROWS;
		 no_of_total_display = no_of_column * no_of_rows;
		
		 totalRecord = mProductCat.lookUpBrandList(conn,user,isCustHaveProductSpecial).size();
		
		// totalPage = totalRecord/(no_of_column * no_of_rows);
		   totalPage = Utils.calcTotalPage(totalRecord, (no_of_column * no_of_rows));
}catch(Exception e){
	e.printStackTrace();
}finally{ 
	if(conn != null){
		conn.close();
	}
}

/// Find Total Page
//System.out.println("totalRecord[]"+totalRecord+"]totalPage["+totalPage+"]");

if(productCatL != null && productCatL.size() > 0){
%>
<table>
<tr>
<td class="paging" colspan="<%=no_of_column%>">หน้าที่
<%  int id = 1 ;
	for(int no = 0 ; no < totalPage;no++ ) {  
		int id_param = id-1;
		//System.out.println("id:"+id);
		String className = "currPageBtn";
		if(id_param == pageId)
			className = "currPageActiveBtn";
%>
<%-- <a href="#" onclick="loadProductCat(<%=id_param%>)">&nbsp;<span class="<%=className%>"><%=id%></span></a> --%>
<input type="button" name="xx" value="<%=id%>" onclick="loadProductCat(<%=id_param%>)" class="<%=className%>"/>
<%       //  no = no + no_of_total_display;
           id++;

	}//for
%> 
</td>
</tr>
<% 	int idx = 0; 
	for(References reference : productCatL ) {
		boolean newRow = (idx%no_of_column == 0);
		boolean closeRow = (idx%no_of_column == (no_of_column-1));
		String productCatKey = reference.getKey();
		String productCatCode = reference.getCode();
		String productCatName = reference.getName();
		idx++;

		String style = (basket.hasItemBrandInBasket(productCatCode))?"style=\"background-color:#FFFF99;\"":"";
%>
	<% if (newRow) { %><tr><% } %>
	<td id="<%=productCatCode%>" class="catalog" <%=style%> width="20%">
	  <table border='0'>
	  <tr><td>
	      <a href="">
		    <img src="${pageContext.request.contextPath}/images/pc<%=productCatCode%>.jpg" height="60" width="120" onerror="imgError(this);" 
						onclick="loadProducts(<%=productCatCode%>)"/>
		   </a> 
	   </td></tr>
	   <tr><td>
		   <p class="brandName"><%=productCatCode+"-"+productCatName%></p>
	   </td></tr>
	  </table>
	</td>
	<% if (closeRow) { %></tr><% } %>
<% } %>
	<tr></tr>
</table>
<% } else { %>
<span id="msg" class="errormsg">
No Record Found!
</span>
<% } %>
<script>
function imgError(source){
    source.src = '${pageContext.request.contextPath}/images/img_not_found.jpg';
    source.onerror = "";
    return true;
}
</script>
