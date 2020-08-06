<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.DBConnectionApps"%>
<%@page import="com.isecinc.pens.model.MSubBrand"%>
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
MSubBrand mSubBrand= new MSubBrand();
try{
    conn = DBConnectionApps.getInstance().getConnection();
	User user = ((User)session.getAttribute("user"));
	String pageId_param = request.getParameter("pageId");
	String custId = request.getParameter("custId");
	boolean isCustHaveProductSpecial = new MCustomer().isCustHaveProductSpecial(conn, custId);
	System.out.println("isCustHaveProductSpecial:"+isCustHaveProductSpecial);
	
	basket = (Basket)session.getAttribute(custId);
	if(basket == null ){
		basket = new Basket();	
	}
	
	if(StringUtils.isEmpty(pageId_param) || Integer.valueOf(pageId_param) ==0){
		pageId_param = "1";
	}
	pageId = Integer.valueOf(pageId_param);
	System.out.println("pageId:"+pageId);
	
	//Get ProductSubBrand By PageId
	productCatL = mSubBrand.lookUpSubBrandListByPage(conn,pageId,user,isCustHaveProductSpecial);
	
	no_of_column = MProductCategory.NO_OF_DISPLAY_COLUMNS;
	no_of_rows =  MProductCategory.NO_OF_DISPLAY_ROWS;
	no_of_total_display = no_of_column * no_of_rows;
	
	totalRecord = mSubBrand.lookUpSubBrandTotalRec(conn,user,isCustHaveProductSpecial);
	
	totalPage = Utils.calcTotalPage(totalRecord, (no_of_column * no_of_rows));
	   
	//List All Brand
	List<References> brandAllList = mSubBrand.lookUpSubBrandAllList(user);
	
	System.out.println("productCatL["+productCatL.size()+"]brandAllList["+brandAllList.size()+"]");
    /// Find Total Page
    System.out.println("totalRecord["+totalRecord+"]totalPage["+totalPage+"]");

if(productCatL != null && productCatL.size() > 0){
%>
<table>
<tr>
<td class="paging" colspan="<%=no_of_column%>">หน้าที่
<%  int id = 1 ;
	for(int no = 0 ; no < totalPage;no++ ) {  
		int id_param = id;
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
&nbsp;&nbsp;&nbsp;&nbsp;
<font size="3">
	Sub Brand List :<select onchange="loadProductsByBrand(this)" id="brandSelect">
	 <option value="">กรุณาเลือก Sub Brand</option>
	  <%if(brandAllList != null && brandAllList.size() >0) { 
	     for(int r=0;r<brandAllList.size();r++){
	    	 References brandRef = brandAllList.get(r);
	    	// System.out.println("brandCode:"+brandRef.getCode());
	  %>
	     <option value="<%=brandRef.getCode()%>"><%=brandRef.getCode()+"-"+brandRef.getName()%></option>
	  <%} }%>
	</select>
</font>
</td>
</tr>
<% 	int idx = 0; 
	for(References reference : productCatL ) {
		boolean newRow = (idx%no_of_column == 0);
		boolean closeRow = (idx%no_of_column == (no_of_column-1));
		String productSubBrandKey = reference.getKey();
		String productSubBrandCode = reference.getCode();
		String productSubBrandName = reference.getName();
		idx++;

		String style = (basket.hasItemBrandInBasket(productSubBrandCode))?"style=\"background-color:#FFFF99;\"":"";
%>
	<% if (newRow) { %><tr><% } %>
	<td id="<%=productSubBrandCode%>" class="catalog" <%=style%> width="20%">
	  <table border='0'>
	  <tr><td>
	      <a href="">
		    <img src="${pageContext.request.contextPath}/images/pc<%=productSubBrandCode%>.jpg" height="60" width="120" onerror="imgError(this);" 
						onclick="loadProducts(<%=productSubBrandCode%>)"/>
		   </a> 
	   </td></tr>
	   <tr><td>
		   <p class="brandName"><%=productSubBrandCode+"-"+productSubBrandName%></p>
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
<%
   } 

}catch(Exception e){
	e.printStackTrace();
}finally{ 
	if(conn != null){
		conn.close();
	}
}

%>
<script>
function imgError(source){
    source.src = '${pageContext.request.contextPath}/images/img_not_found.jpg';
    source.onerror = "";
    return true;
}
</script>
