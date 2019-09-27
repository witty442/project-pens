<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%-- <jsp:useBean id="summaryForm" class="com.isecinc.pens.web.summary.SummaryForm" scope="session" />
 --%>
<html>
    <c:if test="${summaryForm.results != null}">
	<br/>
		<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.results" defaultsort="0" defaultorder="descending" class="resultDisp"
		    requestURI="#" sort="list" pagesize="50">	
		    
		    <display:column  title="รหัสร้านค้า(Bme)" property="storeCode"  sortable="false" style="width:10%;"/>
		    <display:column  title="CustNo(Oracle)" property="custNo"  sortable="false" />
		    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="false"/>
		    <display:column  title="Group" property="group"  sortable="false"/>	
		    <display:column  title="PensItem" property="pensItem"  sortable="false" />
		    <display:column  title="Material Master" property="materialMaster"  sortable="false" />
		    <display:column  title="Barcode" property="barcode"  sortable="false" />
		    <display:column  title="Initial Stock" property="initSaleQty"  sortable="false" />	
		    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" />	
		    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false"/>	
		    <display:column  title="Adjust Qty" property="adjustQty"  sortable="false"/>	
		    <display:column  title="Return Qty" property="saleReturnQty"  sortable="false" />
		    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" />		    				
		</display:table>
      </c:if>
</body>
</html>