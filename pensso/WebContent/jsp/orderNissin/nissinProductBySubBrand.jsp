<%@page import="com.isecinc.pens.web.ordernissin.OrderNissinBasket"%>
<%@page import="com.pens.util.ControlCode"%>
<%@page import="com.pens.util.DBConnectionApps"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.inf.helper.DBConnection"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.web.sales.bean.ProductCatalog"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%
Connection conn = null;
boolean checkStockStep = false;
try{

//conn = DBConnectionApps.getInstance().getConnection();
User user = ((User)session.getAttribute("user"));
String custId = request.getParameter("custId");
OrderNissinBasket basket = (OrderNissinBasket)session.getAttribute(custId);
//System.out.println("basket session:"+basket);
	
if(basket == null ){
	basket = new OrderNissinBasket();	
}

String subBrandCode = "";
String orderDate = "";
String priceListId = "";

DecimalFormat formatter = new DecimalFormat("###,###,##0.00");

if(request.getParameter("subBrandCode")!=null)
	subBrandCode = request.getParameter("subBrandCode");

if(request.getParameter("orderDate")!=null)
	orderDate = request.getParameter("orderDate");

if(request.getParameter("pricelistId")!=null)
	priceListId = request.getParameter("pricelistId");

String whereCause = "";
List<ProductCatalog> catalogs = null;

try{	
	if(subBrandCode != null && subBrandCode.length()>0){
		catalogs = new MProduct().getProductCatalogBySubBrandNissin(subBrandCode,orderDate,priceListId,user);
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<input id="subBrandCode" name="subBrandCode" type="hidden" value="<%=subBrandCode%>" />
<table id="productList" width="100%">
<thead>
<tr>
<th width="5%">T</th>
<th width="8%">รหัสสินค้า</th>
<th width="25%">ชื่อสินค้า</th>
<th width="8%">หน่วยนับ</th>
<th width="18%">จำนวน</th>
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
	<td align="center" valign="middle" width="5%">
	  <%if("Y".equalsIgnoreCase(catalog.getTarget())){%>
	    <img border=0 src="${pageContext.request.contextPath}/icons/fav.jpg" width="24" height="24">
	  <%} %>
	 </td>
	<td align="center"  width="10%"><b><%=catalog.getProductCode()%></b></td>
	<td  width="20%"><%=catalog.getProductNameDisplay()%></td> 
	<td align="center"  width="10%"><%=catalog.getUom1Display()%> &frasl; <%=catalog.getUom2Display()%></td>

	
<td align="center"  width="30%">
	<input name="qty1" type="text" class="qtyInput" height="50" 
	onkeydown="return inputNum2(event,this);" 
	onblur="isNumeric(this);" size="10"
	value="<%=qty1%>" autoComplete="off"/>
	&nbsp;&frasl;&nbsp;
	<input name="qty2" type="text" class="qtyInput" height="50" 
	<%=disable%> onkeydown="return inputNum2(event,this);" 
	onblur="isNumeric(this);"  size="10"
	value="<%=qty2%>" autoComplete="off"/>
	
	<!-- Hidden -->
	<input name="totalLineAmt" type="hidden" value="<%=lineAmt%>" />
    <input name="price1" type="hidden" value="<%=catalog.getPrice1()%>" />
	<input name="price2" type="hidden" value="<%=catalog.getPrice2()%>" />
	<input name="uom1" type="hidden" value="<%=catalog.getUom1Display()%>" />
	<input name="uom2" type="hidden" value="<%=catalog.getUom2Display()%>" />
	<input name="productName" type="hidden" value="<%=catalog.getProductNameDisplay()%>" />
	<input name="productCode" type="hidden" value="<%=catalog.getProductCode()%>" />
	<input name="productId" type="hidden" value="<%=catalog.getProductId()%>" />
	<input name="taxable" type="hidden" value="<%=catalog.getTaxable()%>" />
	<input name="checkInputHalf" type="hidden" value="<%=catalog.getCheckInputHalf()%>" />
	
	<!-- ConvRate: -->
	<input name="uom1ConvRate" type="hidden" size="2"  value="<%=catalog.getUom1ConvRate()%>"/>
	<input name="uom2ConvRate" type="hidden" size="2"  value="<%=catalog.getUom2ConvRate()%>"/>
	
	<!-- inputPriQty -->
	<input name="inputPriQty" type="hidden" size="2" value=""/> 
	<!-- stockQty -->
	<input name="stockOnhandQty" type="hidden" size="2" value="<%=catalog.getStockOnhandQty()%>"/> 
</td>

</tr>
<% 
rowNo++;
} %>
</tbody>
</table>

<%}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn != null){
		conn.close();
	}
}

%>
