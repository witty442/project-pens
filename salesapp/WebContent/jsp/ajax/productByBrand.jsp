<%@page import="java.sql.Connection"%>
<%@page import="com.pens.util.DBConnection"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.web.sales.bean.ProductCatalog"%>
<%@page import="com.isecinc.pens.web.sales.bean.Basket"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%
Connection conn = null;
try{
conn = DBConnection.getInstance().getConnection();
User user = ((User)session.getAttribute("user"));
String custId = request.getParameter("custId");
Basket basket = (Basket)session.getAttribute(custId);
//System.out.println("basket session:"+basket);
boolean isCustHaveProductSpecial = new MCustomer().isCustHaveProductSpecial(conn, custId);
	
if(basket == null ){
	basket = new Basket();	
}

String categoryCode = "";
String orderDate = "";
String priceListId = "";

DecimalFormat formatter = new DecimalFormat("###,###,##0.00");

if(request.getParameter("brandCode")!=null)
	categoryCode = request.getParameter("brandCode");

if(request.getParameter("orderDate")!=null)
	orderDate = request.getParameter("orderDate");

if(request.getParameter("pricelistId")!=null)
	priceListId = request.getParameter("pricelistId");

String whereCause = "";
List<ProductCatalog> catalogs = null;

try{	
	if(categoryCode != null && categoryCode.length()>0){
		catalogs = new MProduct().getProductCatalogByBrand(categoryCode,orderDate,priceListId,user,isCustHaveProductSpecial);
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<input id="categoryCode" name="categoryCode" type="hidden" value="<%=categoryCode%>" />
<table id="productList" width="100%" class="table">
<thead class="thead-dark">
<tr>
<th width="6%">T</th>
<th width="10%">รหัสสินค้า</th>
<th width="30%">ชื่อสินค้า</th>
<th width="8%">หน่วยนับ</th>
<th width="16%">ราคาต่อหน่วย</th>
<th width="18%">จำนวน</th>
<th width="12%">รวมทั้งสิ้น</th>
</tr>
</thead>
<tbody>
<%
int rowNo = 0;
String lineClass = "";
for(ProductCatalog catalog:catalogs) {
	String disable ="";
	ProductCatalog item = basket.getBasketItem(catalog);
	String qty1 = "";
	String qty2 = "";
	String lineAmt = "";
	String lineAmtText = "";
	
	if(item != null){
		qty1 = ""+item.getQty1();
		qty2 = ""+item.getQty2();
		lineAmt =""+item.getLineNetAmt();
		lineAmtText = formatter.format(item.getLineNetAmt());
	}
	
	if(StringUtils.isEmpty(catalog.getUom2()))
		disable = "disabled";
	
	if(rowNo %2 == 0){
		lineClass  ="pop_lineO";
	}else{
		lineClass  ="pop_lineE";
	}

	//System.out.println("item:"+item+",qty1:"+qty1+",qty2:"+qty2);
%>

<tr class="<%=lineClass%>">
<td align="center" valign="middle">
  <%if("Y".equalsIgnoreCase(catalog.getTarget())){%>
    <img border=0 src="${pageContext.request.contextPath}/icons/fav.jpg" width="24" height="24">
  <%} %>
 </td>
<td><b><%=catalog.getProductCode()%></b></td>
<td><%=catalog.getProductNameDisplay()%></td>
<td align="left"><%=catalog.getUom1()%> &frasl; <%=catalog.getUom2()%></td>
<td align="right"><%=formatter.format(catalog.getPrice1())%> &frasl; <%=formatter.format(catalog.getPrice2())%>
</td>
<td>
	<input name="qty1" type="text" class="qtyInput" height="50" 
	onkeydown="return inputNum2(event,this);" 
	onblur="isNumeric(this);linePrice('<%=rowNo%>','<%=catalog.getPrice1()%>','<%=catalog.getPrice2()%>')" 
	value="<%=qty1%>" />
	&nbsp;&frasl;&nbsp;
	<input name="qty2" type="text" class="qtyInput" height="50" 
	<%=disable%> onkeydown="return inputNum2(event,this);" 
	onblur="isNumeric(this);linePrice('<%=rowNo%>','<%=catalog.getPrice1()%>','<%=catalog.getPrice2()%>')" 
	value="<%=qty2%>" />
</td>
<td class="number"><span name="totalLineAmtT"><%=lineAmtText%></span><input name="totalLineAmt" type="hidden" value="<%=lineAmt%>" />
<input name="price1" type="hidden" value="<%=catalog.getPrice1()%>" />
	<input name="price2" type="hidden" value="<%=catalog.getPrice2()%>" />
	<input name="uom1" type="hidden" value="<%=catalog.getUom1()%>" />
	<input name="uom2" type="hidden" value="<%=catalog.getUom2()%>" />
	<input name="productName" type="hidden" value="<%=catalog.getProductNameDisplay()%>" />
	<input name="productCode" type="hidden" value="<%=catalog.getProductCode()%>" />
	<input name="productId" type="hidden" value="<%=catalog.getProductId()%>" />
	<input name="taxable" type="hidden" value="<%=catalog.getTaxable()%>" />
</td>
</tr>
<% 
rowNo++;
} %>
</tbody>
</table>
<script>

</script>
<%}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn != null){
		conn.close();
	}
}

%>
