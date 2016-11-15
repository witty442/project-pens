<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<jsp:useBean id="importForm" class="com.isecinc.pens.web.imports.ImportForm" scope="session" />
<%
%>
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/table_style.css" />
<style type="text/css">

</style>
<script>
 
</script>
</head>

		<c:if test="${errorList != null}">
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result_error" width="70%">
			<tr>
				<th width="5%">Row In Excel</th>
				<th width="10%">Barcode</th>
				<th width="15%">Message </th>
			</tr>
			<c:forEach var="results" items="${errorList}" varStatus="rows">
				<c:choose>
					<c:when test="${rows.index %2 == 0}">
						<c:set var="tabclass" value="lineO" />
					</c:when>
					<c:otherwise>
						<c:set var="tabclass" value="lineE" />
					</c:otherwise>
				</c:choose>
				<tr class="<c:out value='${tabclass}'/>">
					<td>${results.row}</td>
					<td align="left">${results.barcode}</td>
					<td align="left">${results.message}</td>
				</tr>
			</c:forEach>
			</table>
		</c:if>
		
			<c:if test="${reconcileList != null}">
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width="80%">
			    <tr>
			    <th width="8%">Group Code</th>
				<th width="8%">Begining </th>
				<th width="8%">Sale In</th>
				<th width="8%">Sale Return</th>
				<th width="8%">Sale Out</th>
				<th width="8%">Adjust  </th>
				<th width="8%">Stock Short</th>
				<th width="8%">Onhand</th>
				<th width="8%">ตรวจนับจริง </th>
				<th width="8%">ส่วนต่าง</th>
				<th width="8%">22%</th>
				<th width="8%">รวมเงิน</th>
			    </tr>
				 <c:forEach var="results" items="${reconcileList}" varStatus="rows">
					<c:choose>
						<c:when test="${rows.index %2 == 0}">
							<c:set var="tabclass" value="lineO" />
						</c:when>
						<c:otherwise>
							<c:set var="tabclass" value="lineE" />
						</c:otherwise>
					</c:choose>
					<tr class="<c:out value='${tabclass}'/>">
						<td><input type="text" class="disableText" readonly name="groupCode" value="${results.group}" size="10"></input></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="beginingQty" value="${results.beginingQty}"  size="8"></input></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="saleInQty"  value="${results.saleInQty}" size="8"></input></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="saleReturnQty" value="${results.saleReturnQty}" size="8"></input></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="saleOutQty"  value="${results.saleOutQty}" size="8"></input></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="adjustQty" value="${results.adjustQty}" size="8"></input></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="stockShortQty" value="${results.stockShortQty}" size="8"></input></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="onhandQty" value="${results.onhandQty}" size="8"></input></td>
						<td align="left"><input type="text" class="enableNumber"  name="scanQty" value="${results.scanQty}" onblur="isNum(this);calcDiff(${rows.index})" size="8"/></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="diffQty" value="" size="8"/></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="retailPriceBf" value="${results.retailPriceBF}" size="8"/></td>
						<td align="left"><input type="text" class="disableNumber" readonly name="diffAmt" value="" size="13"/></td>
					</tr>
				</c:forEach>
				    <tr class="lineSummary">
						<td align="right"><b>Total</b></td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="beginingQtyT" name="beginingQtyT"  value="0" size="6"/></td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="saleInQtyT"  name="saleInQtyT"  value="" size="6"/></td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="saleReturnQtyT" name="saleReturnQtyT" value="" size="6"/></td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="saleOutQtyT" name="saleOutQtyT"   value="" size="6"/></td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="adjustQtyT" name="adjustQtyT" value="" size="6"/></td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="stockShortQtyT" name="stockShortQtyT" value="" size="6"/></td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="onhandQtyT" name="onhandQtyT" value="" size="6"/></td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="scanQtyT" name="scanQtyT"  value="" size="6"/></td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="diffQtyT" name="diffQtyT" value="" size="6"/></td>
						<td align="left">&nbsp;</td>
						<td align="left"><input type="text" class="disableNumberBold" readonly id="diffAmtT" name="diffAmtT" value="" size="10"/></td>
					</tr>
				</table>
			</c:if>
			
<c:if test="${reconcileList != null}">
<script>
  sumTotal();
  
  function fixNum(myNumber){
	  return parseFloat(Math.round(myNumber * 100) / 100).toFixed(2);
  } 
  function parseF(obj){
		 // alert(obj.value);
	 try{
	  if(obj.value==''){
		  return 0;
	  }
	  return parseFloat(obj.value.replace(/\,/g,''));
	 }catch(err) {
		 alert(err.message);
	 }
  }
  
  function calcDiff(d){
	  //alert(d+":"+document.getElementsByName("scanQty")[0].value);
	  var onhandQty = document.getElementsByName("onhandQty")[d]; 
	  var scanQty = document.getElementsByName("scanQty")[d]; 
	  var diffQty = document.getElementsByName("diffQty")[d]; 
	  var retailPriceBf = document.getElementsByName("retailPriceBf")[d]; 
	  var diffAmt = document.getElementsByName("diffAmt")[d]; 
	  
	  diffQty.value = parseF(onhandQty) - parseF(scanQty) ;
	  
	  diffAmt.value = (parseF(diffQty) * parseF(retailPriceBf)).toFixed(2);
	  toCurreny(diffAmt);
	  
	//  alert(onhandQty);
	  sumTotalDiff();
  }
 
  
  function sumTotalDiff(){
	  var scanQty = document.getElementsByName("scanQty"); 
	  var diffQty = document.getElementsByName("diffQty"); 
	  var diffAmt = document.getElementsByName("diffAmt"); 
	  
	  var scanQtyT = document.getElementById("scanQtyT"); 
	  var diffQtyT = document.getElementById("diffQtyT");
	  var diffAmtT = document.getElementById("diffAmtT");
	  
	  var scanQtyC = 0;
	  var diffQtyC = 0;
	  var diffAmtC = 0;
	 // alert(scanQty.length);
	   for(var r=0;r<=scanQty.length;r++) {
		  if(scanQty[r] != null){
		     scanQtyC += parseF(scanQty[r]);
		  }
		  if(diffQty[r] != null){
		     diffQtyC += parseF(diffQty[r]);
		  }
		  if(diffAmt[r] != null){
			  diffAmtC += parseF(diffAmt[r]);
		 }
	  }
	   
	  // alert(scanQtyT.value);
	  scanQtyT.value = scanQtyC ; 
	  diffQtyT.value = diffQtyC ; 
	  diffAmtT.value =  diffAmtC.toFixed(2);
	  toCurreny(diffAmtT);
  }
  
  function sumTotal(){
	  var beginingQty = document.getElementsByName("beginingQty"); 
	  var saleInQty = document.getElementsByName("saleInQty"); 
	  var saleReturnQty = document.getElementsByName("saleReturnQty"); 
	  var saleOutQty = document.getElementsByName("saleOutQty"); 
	  var adjustQty = document.getElementsByName("adjustQty"); 
	  var stockShortQty = document.getElementsByName("stockShortQty"); 
	  var onhandQty = document.getElementsByName("onhandQty"); 
	  var scanQty = document.getElementsByName("scanQty"); 
	  var diffQty = document.getElementsByName("diffQty"); 
	  var retailPriceBf = document.getElementsByName("retailPriceBf"); 
	  var diffAmt = document.getElementsByName("diffAmt"); 
	  
	  var beginingQtyT = document.getElementById("beginingQtyT"); 
	  var saleInQtyT = document.getElementById("saleInQtyT"); 
	  var saleReturnQtyT = document.getElementById("saleReturnQtyT"); 
	  var saleOutQtyT = document.getElementById("saleOutQtyT"); 
	  var adjustQtyT = document.getElementById("adjustQtyT"); 
	  var stockShortQtyT = document.getElementById("stockShortQtyT"); 
	  var onhandQtyT = document.getElementById("onhandQtyT"); 
	  var scanQtyT = document.getElementById("scanQtyT"); 
	  var diffQtyT = document.getElementById("diffQtyT");
	  var diffAmtT = document.getElementById("diffAmtT");
	  
	  var beginingQtyC = 0; 
	  var saleInQtyC =  0; 
	  var saleReturnQtyC =  0; 
	  var saleOutQtyC =  0; 
	  var adjustQtyC =  0; 
	  var stockShortQtyC =  0; 
	  var onhandQtyC =  0;  
	  var scanQtyC =  0; 
	  var diffQtyC =  0; 
	  var diffAmtC =  0; 
	  
	  var onhandQtyTemp = 0;
	  var scanQtyTemp = 0;
	  for(var r=0;r<=beginingQty.length;r++){
		  if(beginingQty[r] != null)
		    beginingQtyC += parseF(beginingQty[r]);
		  if(saleInQty[r] != null)
		    saleInQtyC += parseF(saleInQty[r]);
		  if(saleReturnQty[r] != null)
		    saleReturnQtyC += parseF(saleReturnQty[r]);
		  if(saleOutQty[r] != null)
		    saleOutQtyC += parseF(saleOutQty[r]);
		  if(adjustQty[r] != null)
		    adjustQtyC += parseF(adjustQty[r]);
		  if(stockShortQty[r] != null)
		    stockShortQtyC += parseF(stockShortQty[r]);
		  if(onhandQty[r] != null){
		    onhandQtyC += parseF(onhandQty[r]);
		    onhandQtyTemp = parseF(onhandQty[r]);
		  }
		  
		  if(scanQty[r] != null){
			 scanQtyC += parseF(scanQty[r]);
			 scanQtyTemp = parseF(scanQty[r])
		  }
			  
		  if(diffQty[r] != null){
			 diffQty[r].value = onhandQtyTemp - scanQtyTemp ;
			 diffQtyC += parseF(diffQty[r]);
			 
			 //calc diff Amt
			 diffAmt[r].value =  parseF(diffQty[r]) *  parseF(retailPriceBf[r]);
			 diffAmt[r].value = parseFloat(diffAmt[r].value ).toFixed(2);
			 
			 toCurreny(diffAmt[r]);
			 
			 diffAmtC += parseF(diffAmt[r]);
		  }
	  }
	  
	  //alert(beginingQtyT);
	   beginingQtyT.value =beginingQtyC ;
	   saleInQtyT.value =saleInQtyC ;
	   saleReturnQtyT.value =saleReturnQtyC ;
	   saleOutQtyT.value =saleOutQtyC ; 
	   adjustQtyT.value =adjustQtyC ; 
	   stockShortQtyT.value =stockShortQtyC ;
	   onhandQtyT.value =onhandQtyC ;  
	   scanQtyT.value =scanQtyC ; 
	   diffQtyT.value =diffQtyC ; 
	   diffAmtT.value = diffAmtC.toFixed(2); toCurreny(diffAmtT);
  }
</script>
</c:if>
        
</body>
</html>