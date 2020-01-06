<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="importForm" class="com.isecinc.pens.web.imports.ImportForm" scope="session" />

<%
boolean showResult = false;
boolean showResult2 = false;
if(importForm.getSummaryErrorList() != null || importForm.getSummarySuccessList() != null){
	showResult = true;
}
if(importForm.getSummaryBoxErrorList() != null || importForm.getSummaryBoxSuccessList() != null){
	showResult2 = true;
}
%>

<%if(showResult) {%>
 <fieldset>
 <legend>Result Import Txt File Onhand From Wacoal</legend>
<%} %>
 <c:if test="${importForm.summaryWacoalListErrorSize > 0}">
 
		<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
		<tr>
			<th colspan="9"  align="left"> <font color="red">จำนวนรายการที่ไม่สามารถ import ได้  : ${importForm.summaryWacoalListErrorSize} รายการ </font> </th>
		</tr>
		<tr>
		    <th width="5%">Row</th>
			<th width="8%">materialMaster</th>
			<th width="8%">barcode </th>
			<th width="10%">onhandQty </th>
			<th width="10%">wholePriceBF </th>
			<th width="10%">retailPriceBF </th>
			<th width="10%">item </th>
			<th width="15%">itemDesc </th>
			<th width="24%">Message </th>
		</tr>
		<c:forEach var="results" items="${importForm.summaryErrorList}" varStatus="rows">
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
				<td align="left">${results.onhandSummary.materialMaster}</td>
				<td align="left">${results.onhandSummary.barcode}</td>
				<td align="left">${results.onhandSummary.onhandQty}</td>
				<td align="left">${results.onhandSummary.wholePriceBF}</td>
				<td align="left">${results.onhandSummary.retailPriceBF}</td>
				<td align="left">${results.onhandSummary.item}</td>
				<td align="left">${results.onhandSummary.itemDesc}</td>
				<td align="left">
				  <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
				    ${rows2.index}:${results2.message}<br/>
				 </c:forEach>
				</td>
			</tr>
		</c:forEach>
		</table>
	</c:if>
	
	<c:if test="${importForm.summaryWacoalListSuccessSize > 0}">
		<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
		<tr>
			<th colspan="9"  align="left">จำนวนรายการที่สามารถ import ได้  : ${importForm.summaryWacoalListSuccessSize} รายการ  </th>
		</tr>
		<tr>
		    <th width="5%">Row</th>
			<th width="8%">materialMaster</th>
			<th width="8%">barcode </th>
			<th width="10%">onhandQty </th>
			<th width="10%">wholePriceBF </th>
			<th width="10%">retailPriceBF </th>
			<th width="10%">item </th>
			<th width="15%">itemDesc </th>
			<th width="24%">Message </th>
		</tr>
		<c:forEach var="results" items="${importForm.summarySuccessList}" varStatus="rows">
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
				<td align="left">${results.onhandSummary.materialMaster}</td>
				<td align="left">${results.onhandSummary.barcode}</td>
				<td align="left">${results.onhandSummary.onhandQty}</td>
				<td align="left">${results.onhandSummary.wholePriceBF}</td>
				<td align="left">${results.onhandSummary.retailPriceBF}</td>
				<td align="left">${results.onhandSummary.item}</td>
				<td align="left">${results.onhandSummary.itemDesc}</td>
				<td align="left">
				 <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
				    ${rows2.index}:${results2.message}<br/>
				 </c:forEach>
				</td>
			</tr>
		</c:forEach>
		</table>
	</c:if>
<%if(showResult) {%>
</fieldset>
<%} %>

<!-- ********* Box Result *************************** -->

<%if(showResult2) {%>
 <fieldset>
 <legend>Result Import Txt File สรุปจำนวนกล่อง From Wacoal</legend>
<%} %>
 <c:if test="${importForm.summaryWacoalBoxListErrorSize > 0}">
 
		<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
		<tr>
			<th colspan="11"  align="left"> <font color="red">จำนวนรายการที่ไม่สามารถ import ได้  : ${importForm.summaryWacoalBoxListErrorSize} รายการ </font> </th>
		</tr>
		<tr>
		    <th width="5%">Row</th>
			<th width="8%">Store Code</th>
			<th width="10%">Store Name </th>
			<th width="6%">Lot No </th>
			<th width="6%">Lot Date </th>
			<th width="6%">Total Box </th>
			<th width="8%">Forwarder </th>
			<th width="10%">Province </th>
			<th width="7%">RefId </th>
			<th width="6%">Send Date </th>
			<th width="15%">Message </th>
		</tr>
		<c:forEach var="results" items="${importForm.summaryBoxErrorList}" varStatus="rows">
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
				<td align="left">${results.onhandSummary.storeCode}</td>
				<td align="left">${results.onhandSummary.storeName}</td>
				<td align="left">${results.onhandSummary.lotNo}</td>
				<td align="left">${results.onhandSummary.lotDate}</td>
				<td align="left">${results.onhandSummary.totalBox}</td>
				<td align="left">${results.onhandSummary.forwarder}</td>
				<td align="left">${results.onhandSummary.province}</td>
				<td align="left">${results.onhandSummary.refId}</td>
				<td align="left">${results.onhandSummary.sendDate}</td>
				<td align="left">
				  <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
				    ${rows2.index}:${results2.message}<br/>
				 </c:forEach>
				</td>
			</tr>
		</c:forEach>
		</table>
	</c:if>
	
	<c:if test="${importForm.summaryWacoalBoxListSuccessSize > 0}">
		<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
		<tr>
			<th colspan="11"  align="left">จำนวนรายการที่สามารถ import ได้  : ${importForm.summaryWacoalBoxListSuccessSize} รายการ  </th>
		</tr>
		<tr>
		    <th width="5%">Row</th>
			<th width="8%">Store Code</th>
			<th width="10%">Store Name </th>
			<th width="6%">Lot No </th>
			<th width="6%">Lot Date </th>
			<th width="6%">Total Box </th>
			<th width="8%">Forwarder </th>
			<th width="10%">Province </th>
			<th width="7%">RefId </th>
			<th width="6%">Send Date </th>
			<th width="15%">Message </th>
		</tr>
		<c:forEach var="results" items="${importForm.summaryBoxSuccessList}" varStatus="rows">
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
				<td align="left">${results.onhandSummary.storeCode}</td>
				<td align="left">${results.onhandSummary.storeName}</td>
				<td align="left">${results.onhandSummary.lotNo}</td>
				<td align="left">${results.onhandSummary.lotDate}</td>
				<td align="left">${results.onhandSummary.totalBox}</td>
				<td align="left">${results.onhandSummary.forwarder}</td>
				<td align="left">${results.onhandSummary.province}</td>
				<td align="left">${results.onhandSummary.refId}</td>
				<td align="left">${results.onhandSummary.sendDate}</td>
				<td align="left">
				  <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
				    ${rows2.index}:${results2.message}<br/>
				 </c:forEach>
				</td>
			</tr>
		</c:forEach>
		</table>
	</c:if>
<%if(showResult2) {%>
</fieldset>
<%} %>

