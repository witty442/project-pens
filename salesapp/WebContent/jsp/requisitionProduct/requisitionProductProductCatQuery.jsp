<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.web.requisitionProduct.RequisitionProductBasket"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%>
<%@page import="com.isecinc.pens.web.moveorder.MoveOrderBasket"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>
<%
User user = ((User)session.getAttribute("user"));
String pageId_param = request.getParameter("pageId");

String custId = request.getParameter("custId");
RequisitionProductBasket basket = (RequisitionProductBasket)session.getAttribute(custId);
if(basket == null ){
	basket = new RequisitionProductBasket();	
}

if(StringUtils.isEmpty(pageId_param))
	pageId_param = "0";

int pageId = Integer.valueOf(pageId_param);

MProductCategory mProductCat = new MProductCategory();
List<References> productCatL = mProductCat.lookUpBrandListCaseRequisitionProduct(pageId,user);

int no_of_column = MProductCategory.NO_OF_DISPLAY_COLUMNS;
int no_of_rows =  MProductCategory.NO_OF_DISPLAY_ROWS;
int no_of_total_display = no_of_column * no_of_rows;

int totalRecord = mProductCat.lookUpBrandList(user).size();

int  totalPage = Utils.calcTotalPage(totalRecord, (no_of_column * no_of_rows));

/// Find Total Page
System.out.println("totalRecord[]"+totalRecord+"]totalPage["+totalPage+"]");

if(productCatL != null && productCatL.size() > 0){
%>
<table>
<tr>
<td class="paging" colspan="<%=no_of_column%>">หน้าที่
<%  int id = 1 ;
	for(int no = 0 ; no < totalPage;no++ ) {  
		int id_param = id-1;
		System.out.println("id:"+id);
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
		
		//System.out.println("productCatKey:"+productCatKey);
		//System.out.println("productCatCode:"+productCatCode);
		//System.out.println("productCatName:"+productCatName);

		String style = (basket.hasItemBrandInBasket(productCatCode))?"style=\"background-color:#FFFF99;\"":"";
%>
	<% if (newRow) { %><tr><% } %>
	<td id="<%=productCatCode%>" class="catalog" <%=style%> width="20%">
	  <table border='0'>
	  <tr><td>
		  <img src="${pageContext.request.contextPath}/images/pc<%=productCatCode%>.jpg" height="60" width="120" onerror="imgError(this);" 
						onclick="loadProducts(<%=productCatCode%>)"/>
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
