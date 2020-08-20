
<%@page import="com.pens.util.ControlCode"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.inf.helper.DBConnection"%>
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
boolean checkStockStep = false;
try{
	if(ControlCode.canExecuteMethod("Stock", "checkStock")){
		checkStockStep = true;
	}
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
<table id="productList" width="100%">
<thead>
<tr>
<th width="5%">T</th>
<th width="8%">รหัสสินค้า</th>
<th width="25%">ชื่อสินค้า</th>
<th width="8%">หน่วยนับ</th>
<th width="8%">บรรจุ</th>
<th width="10%">ราคาต่อหน่วย</th>
<%if(checkStockStep) {%>
<th width="8%">สต๊อกคงเหลือ</th>
<%} %>
<th width="18%">จำนวน</th>
<th width="15%">รวมทั้งสิ้น</th>
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
	<td align="center"><b><%=catalog.getProductCode()%></b></td>
	<td><%=catalog.getProductNameDisplay()%></td>
	<td align="center"><%=catalog.getUom1()%> &frasl; <%=catalog.getUom2()%></td>
	<td align="center"><%=catalog.getUom2ConvRate()%>&frasl; <%=catalog.getUom1ConvRate()%></td>
	<td align="right">
	    <%=formatter.format(catalog.getPrice1())%> &frasl; <%=formatter.format(catalog.getPrice2())%>
	</td>
	<%if(checkStockStep) {%>
	 <td align="right">
	    <%=catalog.getStockOnhandQty1()%> &frasl; <%=catalog.getStockOnhandQty2()%>
	</td> 
	<%} %>
<td align="right">
	<input name="qty1" type="text" class="qtyInput" height="50" 
	onkeydown="return inputNum2(event,this);" 
	onblur="isNumeric(this);linePrice('<%=rowNo%>','<%=catalog.getPrice1()%>','<%=catalog.getPrice2()%>')" 
	value="<%=qty1%>" autoComplete="off"/>
	&nbsp;&frasl;&nbsp;
	<input name="qty2" type="text" class="qtyInput" height="50" 
	<%=disable%> onkeydown="return inputNum2(event,this);" 
	onblur="isNumeric(this);linePrice('<%=rowNo%>','<%=catalog.getPrice1()%>','<%=catalog.getPrice2()%>')" 
	value="<%=qty2%>" autoComplete="off"/>
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
		
		<!-- ConvRate: -->
		<input name="uom1ConvRate" type="hidden" size="2"  value="<%=catalog.getUom1ConvRate()%>"/>:
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
<script>
function linePrice(rowNo,price1,price2){
	var qty1 = document.getElementsByName("qty1")[rowNo].value;
	var qty2 = document.getElementsByName("qty2")[rowNo].value;
	 
	var totalAmtText = document.getElementsByName("totalLineAmtT")[rowNo];
	var totalAmt = document.getElementsByName("totalLineAmt")[rowNo];

	if(qty1 == '' && qty2 == ''){
		totalAmt.value = "";	
		totalAmtText.innerHTML = "";
		return ;
	}
	
	if(qty1 == null)
		qty1 = 0;

	if(qty2 == null)
		qty2 = 0;

	var amt1 = (qty1 * price1);
	var amt2 = (qty2 * price2);

	var amt = amt1 + amt2;
	totalAmt.value = (amt.toFixed(5));	
	totalAmtText.innerHTML = addCommas(amt.toFixed(2));
	
	var uom1ConvRate = document.getElementsByName("uom1ConvRate")[rowNo];
	var uom2ConvRate = document.getElementsByName("uom2ConvRate")[rowNo]; 
	//validate key half ctn
	var allQty = convetTxtObjToFloat(uom2ConvRate)*convetTxtObjToFloat(uom1ConvRate);
	var halfQty = allQty/2;
	if(qty2 != ''){
		if(qty2%halfQty != 0){
			alert("คีย์ได้ ครึ่งหีบ หรือเต็มหีบ เท่านั้น ");
			document.getElementsByName("qty2")[rowNo].value ="";
			document.getElementsByName("qty2")[rowNo].focus();
		}//if
	}//if
	
	//Step validate Stock onhand
	<%if(checkStockStep) {%>
		//compare Stock Onhand
		var stockOnhandQty = document.getElementsByName("stockOnhandQty")[rowNo];//1.8
		var inputPriQty =document.getElementsByName("inputPriQty")[rowNo];
	
		//convert qty2 to PriQty
		var inputPriQtyTemp = document.getElementsByName("qty1")[rowNo];//set for sum +subQty
		var subQty = 0;
		 if(convetTxtObjToFloat(uom2ConvRate) > 0){
	    	subQty = qty2 / (convetTxtObjToFloat(uom1ConvRate)/convetTxtObjToFloat(uom2ConvRate)) ;
	    	//alert("subQty:"+subQty+"");
	     }
		subQty +="";
		//alert("subQty:"+subQty);
		 if(subQty.indexOf(".") != -1){
			inputPriQtyTemp  = convetTxtObjToFloat(inputPriQtyTemp)+ parseFloat(subQty.substr(0,subQty.indexOf("."))) ;
			inputPriQtyTemp += subQty.substr(subQty.indexOf("."),subQty.length);//1.8
		}else{
			inputPriQtyTemp =convetTxtObjToFloat(inputPriQtyTemp)+ parseFloat(subQty);
		} 
		//alert("inputPriQtyTemp:"+inputPriQtyTemp);
		 inputPriQty.value = inputPriQtyTemp; 
		
		if(convetTxtObjToFloat(inputPriQty) > convetTxtObjToFloat(stockOnhandQty)){
			alert("ไม่สามารถ ระบุ Order เกินยอด Stock Onhand ได้ ");
			document.getElementsByName("qty1")[rowNo].value ="";
			document.getElementsByName("qty2")[rowNo].value ="";
			document.getElementsByName("qty2")[rowNo].focus();
			return false;
		} 
		//alert("stockOnhandQty["+stockOnhandQty.value+"]inputPriQty["+inputPriQty+"]");
	<%}%>
}
</script>
<%}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn != null){
		conn.close();
	}
}

%>
