
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>

<jsp:useBean id="summaryForm" class="com.isecinc.pens.web.summary.SummaryForm" scope="session" />
<html>
<head>
</head>
 <body>
                    <c:if test="${summaryForm.onhandSummaryResults != null}">
				        <table align="Left" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="left">
									<b>Data As Of Date :  ${summaryForm.onhandSummary.asOfDate} </b>
								</td>
							</tr>
							<tr>
								<td align="left">
									<b>File Name :${summaryForm.onhandSummary.fileName}</b>
								</td>
							</tr>
						</table>
						<br/>
						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.onhandSummaryResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="Group" property="group"  sortable="false" class="group"/>
							    <display:column  title="Item" property="item"  sortable="false" class="item"/>
							    <display:column  title="Description" property="itemDesc"  sortable="false" class="itemDesc"/>	
							    <display:column  title="On-Hand" property="onhandQty"  sortable="false" class="onhandQty"/>	
							    <display:column  title="ราคาขายส่งก่อน VAT" property="wholePriceBF"  sortable="false" class="wholePriceBF"/>	
							    <display:column  title="ราคาขายปลีกก่อน VAT " property="retailPriceBF"  sortable="false" class="retailPriceBF"/>			
							    <display:column  title="Barcode" property="barcode"  sortable="false" class="barcode"/>	
							    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="pensItem"/>
							    <display:column  title=" Material Master" property="materialMaster"  sortable="false" class="materialMaster"/>	
							    <display:column  title=" Status" property="status"  sortable="false" class="status"/>	
							    <display:column  title=" Message" property="message"  sortable="false" class="message"/>			
							</display:table>
                    </c:if>       
                     <c:if test="${summaryForm.onhandSummaryLotusResults != null}">

						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.onhandSummaryLotusResults" defaultsort="0"  defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="lotus_storeCode"/>
							    <display:column  title="PensItem" property="pensItem"  sortable="false" class="lotus_pensItem"/>
							    <display:column  title="Group" property="group"  sortable="false" class="lotus_group"/>	
							    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="lotus_saleInQty"/>	
							    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="lotus_saleReturnQty"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="lotus_saleOutQty"/>	
							    <display:column  title="Adjust" property="adjustQty"  sortable="false" class="lotus_adjustQty"/>	
							    <display:column  title="Stock short" property="stockShortQty"  sortable="false" class="lotus_stockShortQty"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="lotus_onhandQty"/>	
							    				
							</display:table>
                    </c:if>
                    
                     <c:if test="${summaryForm.onhandSummaryBmeTransResults != null}">

						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.onhandSummaryBmeTransResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="lotus_storeCode"/>
							    <display:column  title="PensItem" property="pensItem"  sortable="false" class="lotus_pensItem"/>
							    <display:column  title="Group" property="group"  sortable="false" class="lotus_group"/>	
							    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="lotus_saleInQty"/>	
							    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="lotus_saleReturnQty"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="lotus_saleOutQty"/>	
							    <display:column  title="Adjust" property="adjustQty"  sortable="false" class="lotus_adjustQty"/>	
							    <display:column  title="Stock short" property="stockShortQty"  sortable="false" class="lotus_stockShortQty"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="lotus_onhandQty"/>	
							    				
							</display:table>
                    </c:if>
   
                      <c:if test="${summaryForm.onhandSummaryMTTDetailResults != null}">

						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.onhandSummaryMTTDetailResults" defaultsort="0" defaultorder="descending" class="resultDisp"
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
							    <display:column  title="Return Qty" property="saleReturnQty"  sortable="false" />
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" />		    				
							</display:table>
                    </c:if>
                    
                      <c:if test="${summaryForm.onhandSummarySizeColorBigCResults != null}">

						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.onhandSummarySizeColorBigCResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า(Bme)" property="storeCode"  sortable="false"/>
							    <display:column  title="SubInv" property="subInv"  sortable="false" />
							    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="false"/>
							    <display:column  title="Group" property="group"  sortable="false"/>	
							    <display:column  title="PensItem" property="pensItem"  sortable="false" />
							    <display:column  title="Material Master" property="materialMaster"  sortable="false" />
							    <display:column  title="Barcode" property="barcode"  sortable="false" />
							    <display:column  title="Initial Stock" property="initSaleQty"  sortable="false" />	
							    <display:column  title="Trans In Qty" property="transInQty"  sortable="false" />	
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false"/>	
							    <display:column  title="Return Qty" property="saleReturnQty"  sortable="false" />
							    <display:column  title="Adjust Qty" property="adjustSaleQty"  sortable="false" />
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" />	
							    				
							</display:table>
                    </c:if>

                     <c:if test="${summaryForm.onhandBigCResults != null}">

						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.onhandBigCResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="bigc_storeCode"/>
							    <display:column  title="Sub Inv" property="subInv"  sortable="false" class="bigc_subInv"/>
							    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="false" class="bigc_storeName"/>
							    <display:column  title="Group" property="group"  sortable="false" class="bigc_group"/>	
							    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="bigc_pensItem"/>	
							    <display:column  title="Transfer In Qty" property="transInQty"  sortable="false" class="bigc_transInQty"/>	
							    <display:column  title="Return Qty" property="saleReturnQty"  sortable="false" class="bigc_saleReturnQty"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="bigc_saleOutQty"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="bigc_onhandQty"/>	
							    				
							</display:table>
                    </c:if>
                    
                    <c:if test="${summaryForm.onhandSummaryLotusPeriodResults != null}">

						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.onhandSummaryLotusPeriodResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="lotus_storeCode"/>
							    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="false" class="lotus_storeName"/>
							    <display:column  title="Group" property="group"  sortable="false" class="lotus_group"/>	
							    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="lotus_saleInQty"/>	
							    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="lotus_saleReturnQty"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="lotus_saleOutQty"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="lotus_onhandQty"/>	
							    				
							</display:table>
                    </c:if>
                    
    
                     <c:if test="${summaryForm.physicalSummaryResults != null}">
						<br/>
						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.physicalSummaryResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="30">	
							    
							    <display:column  title="Item" property="item"  sortable="false" class="phy_item"/>
							    <display:column  title="Barcode" property="barcode"  sortable="false" class="phy_barcode"/>	
							    <display:column  title="Cust Code" property="pensCustCode"  sortable="false" class="phy_custCode"/>
							    <display:column  title="Cust Name" property="pensCustName"  sortable="false" class="phy_custName"/>		
							    <display:column  title="Count Date" property="countDate"  sortable="false" class="phy_countDate"/>	
							    <display:column  title="File Name " property="fileName"  sortable="false" class="phy_fileName"/>			
							    <display:column  title="Create Date" property="createDate"  sortable="false" class="phy_createDate"/>	
					
							</display:table>
                    </c:if>
                    
                      <c:if test="${summaryForm.diffStockSummaryLists != null}">
						<br/>
						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.diffStockSummaryLists" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="custCode"  sortable="false" class="d_custCode"/>
							    <display:column  title="Item" property="item"  sortable="false" class="d_item"/>
							    <display:column  title="Description" property="description"  sortable="false" class="d_description"/>
							    <display:column  title="Order Consign" property="orderConsign"  sortable="false" class="d_orderConsign"/>		
							    <display:column  title="Order From Lotus" property="orderFromLotus"  sortable="false" class="d_orderFromLotus"/>	
							    <display:column  title="Data From Physical " property="dataFromPhysical"  sortable="false" class="d_dataFromPhysical"/>			
							    <display:column  title="Adjust" property="adjust"  sortable="false" class="d_adjust"/>	
					            <display:column  title="Diff" property="diff"  sortable="false" class="d_diff"/>	
							</display:table>
                    </c:if>
                    
                     <c:if test="${summaryForm.summaryByGroupCodeResults != null}">
						<br/>
						<br/>
							<display:table style="width:100%;"  id="item" name="sessionScope.summaryForm.summaryByGroupCodeResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="custCode"  sortable="false" class="d_custCode"/>
							    <display:column  title="Item" property="item"  sortable="false" class="d_item"/>
							    <display:column  title="Description" property="description"  sortable="false" class="d_description"/>
							    <display:column  title="Order Consign" property="orderConsign"  sortable="false" class="d_orderConsign"/>		
							    <display:column  title="Order From Lotus" property="orderFromLotus"  sortable="false" class="d_orderFromLotus"/>	
							    <display:column  title="Data From Physical " property="dataFromPhysical"  sortable="false" class="d_dataFromPhysical"/>			
							    <display:column  title="Adjust" property="adjust"  sortable="false" class="d_adjust"/>	
					            <display:column  title="Diff" property="diff"  sortable="false" class="d_diff"/>	
							</display:table>
                    </c:if>
                    
</body>
</html>