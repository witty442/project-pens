<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%>
<%@page import="com.isecinc.pens.web.moveorder.MoveOrderBasket"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>
<%
String pageId_param = request.getParameter("pageId");

String custId = request.getParameter("custId");
MoveOrderBasket basket = (MoveOrderBasket)session.getAttribute(custId);
if(basket == null ){
	basket = new MoveOrderBasket();	
}

if(StringUtils.isEmpty(pageId_param))
	pageId_param = "0";

int pageId = Integer.valueOf(pageId_param);

MProductCategory mProductCat = new MProductCategory();
List<References> productCatL = mProductCat.lookUpBrandListCaseMoveOrder(pageId);

int no_of_column = MProductCategory.NO_OF_DISPLAY_COLUMNS;
int no_of_rows =  MProductCategory.NO_OF_DISPLAY_ROWS;
int no_of_total_display = no_of_column * no_of_rows;

int totalBrand = mProductCat.lookUpBrandList().size();
/// Find Total Page

if(productCatL != null && productCatL.size() > 0){
%>
<table>
<tr>
<td class="paging" colspan="<%=no_of_column%>">˹�ҷ��
<%  int id = 1 ;
	for(int no = 0 ; no < totalBrand; ) {  
		int id_param = id-1;
		String className = "pageLink";
		if(id_param == pageId)
			className = "currPage";
%>
<a href="#" onclick="loadProductCat(<%=id_param%>)"><span class="<%=className%>"><%=id%></span></a>
<%  no = no + no_of_total_display;id++; } %> 
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
	<td id="<%=productCatCode%>" class="catalog" <%=style%> >
		<img src="${pageContext.request.contextPath}/images/pc<%=productCatCode%>.jpg" height="68" width="68" onerror="imgError(this);" 
						onclick="loadProducts('<%=productCatCode%>');"/>
	<br><p class="brandName"><%=productCatCode+"-"+productCatName%></p></td>
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
