<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%>
<%@page import="com.isecinc.pens.web.moveorder.MoveOrderBasket"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>
<%
try{
User user = ((User)session.getAttribute("user"));
String searchType = request.getParameter("searchType");
String pageId_param = request.getParameter("pageId");
String brand = request.getParameter("brand");

System.out.println("searchType:"+searchType);
System.out.println("brand:"+brand);
System.out.println("pageId_param:"+pageId_param);

String custId = request.getParameter("custId"); 
MoveOrderBasket basket = (MoveOrderBasket)session.getAttribute(custId);
if(basket == null ){
	basket = new MoveOrderBasket();	
}

if(StringUtils.isEmpty(pageId_param))
	pageId_param = "0";

int pageId = Integer.valueOf(pageId_param);

MProductCategory mProductCat = new MProductCategory();
List<References> productCatL = null;

if(searchType.equalsIgnoreCase("byBrand")){
	productCatL = mProductCat.lookUpBrandListCaseMoveOrderByBrand(user,brand); 
}else{
	productCatL = mProductCat.lookUpBrandListCaseMoveOrder(pageId,user); 
}

int no_of_column = MProductCategory.NO_OF_DISPLAY_COLUMNS;
int no_of_rows =  MProductCategory.NO_OF_DISPLAY_ROWS;
int no_of_total_display = no_of_column * no_of_rows;
int totalRecord = 0;

List<References> brandList = mProductCat.lookUpBrandListNew(user);
totalRecord = brandList != null?brandList.size():0;

//List All Brand
List<References> brandAllList = mProductCat.lookUpBrandAllListNew(user);

int totalPage = Utils.calcTotalPage(totalRecord, (no_of_column * no_of_rows));
//Case Error calc page set default totalPage = 7
if(totalPage <=1){
   totalPage = 7;
}

if(productCatL != null && productCatL.size() > 0){
%>
<table>
<tr>
<td class="paging" colspan="<%=no_of_column%>">&nbsp;&nbsp;&nbsp; หน้าที่
<%  int id = 1 ;
	for(int no = 0 ; no < totalPage;no++ ) {  
		int id_param = id-1;
		String className = "currPageBtn";
		if(id_param == pageId)
			className = "currPageActiveBtn";
%>
&nbsp;&nbsp;&nbsp;<input type="button" name="xx" value="<%=id%>" onclick="loadProductCat(<%=id_param%>)" class="<%=className%>"/>
<%       //  no = no + no_of_total_display;
       id++;
	}//for
%> 
&nbsp;&nbsp;&nbsp;&nbsp;
<font size="3">
	Brand List :<select onchange="loadProductsByBrand(this)" id="brandSelect">
	 <option value="">กรุณาเลือก Brand</option>
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
						onclick="loadProducts('<%=productCatCode%>');"/>
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
No Brand Record Found!
</span>
<% } %>


<script>
function imgError(source){
    source.src = '${pageContext.request.contextPath}/images/img_not_found.jpg';
    source.onerror = "";
    return true;
}
</script>

<%}catch(Exception e) {
	e.printStackTrace();
}

%>
