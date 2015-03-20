<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%
String role = ((User)session.getAttribute("user")).getType();
User user = (User)session.getAttribute("user");
%>
<%if(role.equalsIgnoreCase(User.NB)){ %>
<ul id="nav">
	<li><a href="#" class="parent"><span><bean:message key="Reports" bundle="sysprop"/></span></a>
    	<ul>
			<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentReport.do';"><span><bean:message key="HeartiBenecolShipment" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentTempReport.do';"><span><bean:message key="HeartiBenecolShipmentTemp" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentBenecolReport.do';"><span><bean:message key="ShipmentBenecolToMember" bundle="sysprop"/></span></a>
            </li>
           	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/reports/printStickerReport.jsp';"><span><bean:message key="PrintStickerReport" bundle="sysprop"/></span></a>
           	</li>
        </ul>
    </li>
</ul>
<%}else {%>
<ul id="nav">
	<li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp';"><span><bean:message key="HomeMenu" bundle="sysprop"/></span></a>
		<ul>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/profile.do?id=<%=((User)session.getAttribute("user")).getId() %>';"><span><bean:message key="Profile" bundle="sysprop"/></span></a>
			</li>
			<%if(role.equalsIgnoreCase(User.ADMIN)){ %>
           	<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/user.do';"><span><bean:message key="User" bundle="sysprop"/></span></a>
			</li>
			<%} %>
            <%if(role.equalsIgnoreCase(User.ADMIN)||role.equalsIgnoreCase(User.DD)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/sysconfig.do';"><span><bean:message key="SystemConf" bundle="sysprop"/></span></a>
            </li>
            <%} %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/docseq.do';"><span><bean:message key="DocumentSeq" bundle="sysprop"/></span></a>
            </li>
            <%if(!role.equalsIgnoreCase(User.ADMIN)&&!role.equalsIgnoreCase(User.DD)){ %>
            <li>
            	<!-- <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salestarget.do';"><span><bean:message key="SalesTarget" bundle="sysprop"/></span></a> -->
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salestargetnew.do';"><span><bean:message key="SalesTarget" bundle="sysprop"/></span></a>
            </li>
            <%} %>
            
            <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/tripAction.do?do=prepare&type=admin';"><span><bean:message key="Trip" bundle="sysprop"/></span></a>
            </li>
            <%}%>
            <%if(!role.equalsIgnoreCase(User.DD) && !role.equalsIgnoreCase(User.ADMIN)) {%>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/tripAction.do?do=prepare&type=user';"><span><bean:message key="Trip" bundle="sysprop"/></span></a>
            </li>
            <%} %>
            <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
           	<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/administer/customerSeqUpdate.jsp';"><span><bean:message key="CustomerSequenceUpdate" bundle="sysprop"/></span></a>
			</li>
			<%} %>
			<!-- WIT EDIT  -->
		</ul>
	</li>
	<!-- WIT Edit :04/08/2554 -->
	<%if(role.equalsIgnoreCase(User.ADMIN)){ %>
       <li>
          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/customer.do';"><span><bean:message key="Customer" bundle="sysprop"/></span>
          </a>
       </li>
     <%} %>
            
    <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
    <li><a href="#" class="parent"><span><bean:message key="MasterData" bundle="sysprop"/></span></a>
        <ul>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/product.do';"><span><bean:message key="Product" bundle="sysprop"/></span></a>
            </li>
           	<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/pricelist.do';"><span><bean:message key="PriceList" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/modifier.do';"><span><bean:message key="Promotion" bundle="sysprop"/></span></a>
            </li>
             <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/product/productC4.jsp';"><span><bean:message key="ProductC4" bundle="sysprop"/></span></a>
            </li>
            <%} %>
        </ul>
    </li>
    <%} %>
    <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
    <%if(!role.equalsIgnoreCase(User.DD)){ %>
    <li><a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/customer.do';"><span><bean:message key="Customer" bundle="sysprop"/></span></a></li>
    <%} %>
    <%} %>
    <%if(role.equalsIgnoreCase(User.DD)){ %>
    <li><a href="#"><span><bean:message key="Member" bundle="sysprop"/></span></a>
    		<ul>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/member.do';"><span><bean:message key="MemberInfo" bundle="sysprop"/></span></a>
            </li>
           	<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/memberrenew.do';"><span><bean:message key="Renew" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/member/memberBirthDaySearch.jsp';"><span><bean:message key="Member.BirthDay" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/memberimport.do';"><span><bean:message key="MemberImport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/memberOrderImport.do';"><span><bean:message key="MemberOrderImport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/dataimports/memberImportSearch.jsp';"><span><bean:message key="MemberImportSearch" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/memberOrder.do';"><span><bean:message key="MemberOrderAdjust" bundle="sysprop"/></span></a>
            </li>
        </ul>
    </li>
    <%} %>
    <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
    <li><a href="#" class="parent"><span><bean:message key="Reports" bundle="sysprop"/></span></a>
    	<ul>
    		<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/orderThreeMonthsReport.do';"><span><bean:message key="OrderHistoryReport" bundle="sysprop"/></span></a>
            </li>
          <%--   <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesHistoryReport.do';"><span><bean:message key="InterfaceHistoryReport" bundle="sysprop"/></span></a>
            </li> --%>
        
    		<%if(role.equalsIgnoreCase(User.DD)){ %>
			<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentReport.do';"><span><bean:message key="HeartiBenecolShipment" bundle="sysprop"/></span></a>
            </li>
           <%--  <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentTempReport.do';"><span><bean:message key="HeartiBenecolShipmentTemp" bundle="sysprop"/></span></a>
            </li> --%>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentBenecolReport.do';"><span><bean:message key="ShipmentBenecolToMember" bundle="sysprop"/></span></a>
            </li>
           <%--  <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiptTempReport.do';"><span><bean:message key="ReceiptTemporary" bundle="sysprop"/></span></a>
            </li> --%>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/remittanceReport.do';"><span><bean:message key="Remittance" bundle="sysprop"/></span></a>
            </li>
            <%-- <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiveBenecolReport.do';"><span><bean:message key="ReceiveBenecol" bundle="sysprop"/></span></a>
            </li> --%>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiveBagReport.do';"><span><bean:message key="ReceiveBag" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/benecolSummaryReport.do';"><span><bean:message key="BenecolSummaryReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/callBeforeReport.do';"><span><bean:message key="CallBeforeSendReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/taxInvoiceDDReport.do';"><span><bean:message key="TaxInvoiceDDReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/memberReport.do?do=prepare';"><span><bean:message key="MemberReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentDetailReport.do?do=clearForm';"><span><bean:message key="ShipmentDetailReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salesVATReport.do?do=clearForm';"><span><bean:message key="SalesVATReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiptSummaryReport.do?do=clearForm';"><span><bean:message key="ReceiptSummaryReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentForm.do';"><span><bean:message key="ShipmentForm" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/taxShipment.do';"><span><bean:message key="TaxShipment" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/formReceipt.do';"><span><bean:message key="FormReceipt" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/taxReceipt.do';"><span><bean:message key="TaxReceipt" bundle="sysprop"/></span></a>
            </li>
    		<%} %>
        	<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetSummary.do';"><span><bean:message key="SalesTargetSummary" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiptPlanReport.do?do=prepare';"><span><bean:message key="ReceiptPlanReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiptPlanCompareReport.do?do=prepare';"><span><bean:message key="ReceiptPlanCompareReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentSummaryReport.do?do=prepare';"><span><bean:message key="ShipmentSummaryReport" bundle="sysprop"/></span></a>
            </li>
        </ul>
    </li>
    <%} %>
    <%if(role.equalsIgnoreCase(User.DD)){ %>
    <li><a href="#" class="parent"><span>พิมพ์แบบฟอร์ม  </span></a>
	    	<ul>
    	      <li><a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/reports/printLabelReport.jsp';"><span>พิมพ์ Label จดหมาย</span></a></li>
    	  
    	      <li><a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/reports/printStickerReport.jsp';"><span>พิมพ์สติ๊กเกอร์</span></a></li>
    	    </ul>
    </li>
    <%} %>
 
		<li><a href="#" class="parent"><span>จัดการข้อมูล</span></a>
	    	<ul>
	    		<li>
	            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentAction.do?do=prepare';"><span><bean:message key="ShipmentConfirmation" bundle="sysprop"/></span></a>
	            </li>
	            <li>
	            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/shipmentAction.do?do=cancel';"><span><bean:message key="CancelShipmentConfirmation" bundle="sysprop"/></span></a>
	            </li>
	            <li>
	            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiptAllAction.do?do=prepare';"><span><bean:message key="ReceiptConfirmation" bundle="sysprop"/></span></a>
	            </li>
	            <li>
					<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';"><span><bean:message key="ManageReceipt" bundle="sysprop"/></span></a>
				</li>
	        </ul>
    </li>


</ul>
<%}%>

  
  