<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="receiptForm" class="com.isecinc.pens.web.receipt.ReceiptForm" scope="request"/>
<script type="text/javascript">
function openBillLine(path){
	var lineId = document.getElementsByName('bill.orderLineId');
	var ids='';
	for(i=0;i<lineId.length;i++){
		ids+=','+lineId[i].value;
	}
	if(ids.length>0)ids=ids.substring(1, ids.length);
	window.open(path + '/jsp/pop/orderLinePopup.jsp?memberId=<%=receiptForm.getReceipt().getCustomerId()%>&selected='+ids,"Bill","width=939,height=400,location=No,resizable=No");
}

function addBillLine(path, arrayVal) {

	var jQtable = $('#tblBill');

	for (i = 0; i < arrayVal.length; i++) {
		jQtable.each(function() {
			var $table = $(this);
			// Number of td's in the last table row
				var n = $('tr', this).length;
				// n++;
				// alert(n);
				var className = "lineO";
				if (n % 2 == 0)
					className = "lineE";
				arrayVal[i].rows = n;

				var tds = '<tr class=' + className + '>';
				tds += '<td align="center"></td>';
				tds += '<td align="center"></td>';
				tds += '<td align="center"></td>';
				tds += '<td align="left"></td>';
				tds += '<td align="right"></td>';
				tds += '</tr>';
				if ($('tbody', this).length > 0) {
					$('tbody', this).append(tds);
				} else {
					$(this).append(tds);
				}
			});
	}
	setValueToBillLine(path, arrayVal);
}

/** Set Value to Bill */
function setValueToBillLine(path, arrayVal) {

	var tbl = document.getElementById('tblBill');
	var checkBoxLabel;
	var iconLabel;
	var inputLabel;
	for (i = 0; i < arrayVal.length; i++) {

		checkBoxLabel = '<input type="checkbox" name="billids" value="0"/>';

		inputLabel="";
		inputLabel+="<input type='hidden' name='bill.id' value='0'>";
		inputLabel+="<input type='hidden' name='bill.orderId' value='"+arrayVal[i].orderId+"'>";
		inputLabel+="<input type='hidden' name='bill.orderNo' value='"+arrayVal[i].orderNo+"'>";
		inputLabel+="<input type='hidden' name='bill.orderLineId' value='"+arrayVal[i].lineId+"'>";
		inputLabel+="<input type='hidden' name='bill.netAmt' value='"+arrayVal[i].lineAmount+"'>";
		inputLabel+="<input type='hidden' name='bill.description' value='"+arrayVal[i].lineDesc+"'>";
		
		tbl.rows[arrayVal[i].rows].cells[0].innerHTML = arrayVal[i].rows;
		tbl.rows[arrayVal[i].rows].cells[1].innerHTML = checkBoxLabel;
		tbl.rows[arrayVal[i].rows].cells[2].innerHTML = arrayVal[i].orderNo+inputLabel;
		tbl.rows[arrayVal[i].rows].cells[3].innerHTML = arrayVal[i].lineDesc;
		tbl.rows[arrayVal[i].rows].cells[4].innerHTML = addCommas(Number(arrayVal[i].lineAmount).toFixed(2));
	
	}

	calculateAll();
	
	return true;
}

</script>
<div align="left">&nbsp;&nbsp;<input type="button" value="เพิ่มรายการ" onclick="openBillLine('${pageContext.request.contextPath}', 0);"/></div>
<table id="tblBill" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
		<th class="checkBox">
			<input type="checkbox" name="chkAll" onclick="checkSelect(this,document.getElementsByName('billids'));" />
		</th>
		<th class="name"><bean:message key="Order.No"  bundle="sysele"/></th>
		<th><bean:message key="Description" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="TotalAmount"  bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="${receiptForm.lines}" varStatus="rows1">
	<c:choose>
		<c:when test="${rows1.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>
	<tr class="${tabclass}">
		<td align="center">${results.lineNo}</td>
		<td align="center"><input type="checkbox" name="billids" value="${results.id}"/></td>
		<td align="center">
			${results.order.orderNo}
			<input type="hidden" name='bill.id' value='${results.id}'>
			<input type='hidden' name='bill.orderId' value='${results.order.id}' >
			<input type='hidden' name='bill.orderNo' value='${results.order.orderNo}' >
			<input type='hidden' name='bill.orderLineId' value='${results.orderLine.id}'>
			<input type='hidden' name='bill.netAmt' value='${results.invoiceAmount}'>
			<input type='hidden' name='bill.description' value='${results.description}'>
		</td>
		<td align="left">${results.description}</td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.invoiceAmount}"/></td>
	</tr>
	</c:forEach>							
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td align="left" colspan="8" class="footer">&nbsp;
			<a href="javascript:deleteBill('${pageContext.request.contextPath}');"> 
			<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif"> ลบรายการ</a>
		</td>
	</tr>
</table>
								