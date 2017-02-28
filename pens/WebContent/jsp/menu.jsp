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
	<li><div class="parent"><span><bean:message key="Reports" bundle="sysprop"/></span></div>
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
	<li><div class="parent"><bean:message key="HomeMenu" bundle="sysprop"/></div>
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
            <%if(role.equalsIgnoreCase(User.VAN)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/inventory.do';"><span><bean:message key="InventoryOnhand" bundle="sysprop"/></span></a>
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
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';"><span><bean:message key="ManageOrderReceipt" bundle="sysprop"/></span></a>
			</li>
			<%} %>
			<!-- WIT EDIT  -->
			<%if(role.equalsIgnoreCase(User.VAN) || role.equalsIgnoreCase(User.TT) || role.equalsIgnoreCase(User.DD)){ %>
	           	<li>
					<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';"><span><bean:message key="ManageOrderReceipt" bundle="sysprop"/></span></a>
				</li>
			<%} %>
			<% if(user.isPDPaid()) {%>
				<li>
					<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/pdReceipt.do?do=prepare';"><span><bean:message key="PD" bundle="sysprop"/></span></a>
				</li>
			<% } %>
			
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/softwareUpdater/SalesAppUpdater.jsp';"><span>��Ѻ��ا����� SalesApp</span></a>
			</li>
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
    <li><div class="parent"><bean:message key="MasterData" bundle="sysprop"/></div>
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
    <li><div onclick="window.location='${pageContext.request.contextPath}/jsp/customer.do';"><bean:message key="Customer" bundle="sysprop"/></div></li>
    <%} %>
    <%} %>
    
    <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
    <li><div class="parent"><bean:message key="Reports" bundle="sysprop"/></div>
    	<ul>
    		<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/orderThreeMonthsReport.do';"><span><bean:message key="OrderHistoryReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesHistoryReport.do';"><span><bean:message key="InterfaceHistoryReport" bundle="sysprop"/></span></a>
            </li>
            <%if(!role.equalsIgnoreCase(User.DD)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/transactionSummaryReport.do';"><span><bean:message key="TransactionSummaryReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoiceDetailReport.do';"><span><bean:message key="InvoiceDetailReport" bundle="sysprop"/></span></a>
            </li>
            <%} %>

    		<%if(role.equalsIgnoreCase(User.VAN)){ %>
    		<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/performanceReport.do';"><span><bean:message key="PerformanceReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentNewReport.do';"><span><bean:message key="ReceiptReport" bundle="sysprop"/></span></a>
            </li>
             <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/cancelDetailReport.do';"><span><bean:message key="CancelDetailReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderReport.do';"><span><bean:message key="MoveOrderReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentAllReport.do';"><span><bean:message key="InvoicePaymentAllReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/detailedSalesReport.do';"><span><bean:message key="DetailedSalesReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/chequeReport.do';"><span><bean:message key="ChequeReport" bundle="sysprop"/></span></a>
            </li>
             <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentReport.do';"><span><bean:message key="ReceiptReport" bundle="sysprop"/>(����������)</span></a>
            </li>
            
        	<%} %>
        	<%if(role.equalsIgnoreCase(User.TT)){ %>
	        	<li>
	          	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentAllReport.do';"><span><bean:message key="InvoicePaymentAllReport" bundle="sysprop"/></span></a>
	         	</li>
        	<%} %>
        	<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetSummary.do';"><span><bean:message key="SalesTargetSummary" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/creditControlReport.do';"><span><bean:message key="CreditControlReport" bundle="sysprop"/></span></a>
            </li>
           
        </ul>
    </li>
    <%} %>
   
    <li class="parent"><div class="parent"><bean:message key="Interfaces" bundle="sysprop"/></div>
   		<ul>
           	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare';"><span><bean:message key="Interfaces" bundle="sysprop"/></span></a>
           	</li>
          	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/monitorInterfacesAction.do?do=prepare';"><span><bean:message key="MonitorInterfaces" bundle="sysprop"/></span></a>
           	</li>
           	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manualUpdateAddressAction.do?do=prepare';"><span><bean:message key="ManualUpdateAddress" bundle="sysprop"/></span></a>
           	</li>
       </ul>
   </li>
   <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
    <li><div class="parent"><bean:message key="Interim" bundle="sysprop"/></div>
    	<ul>
    		<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/autoCreateReceipt.do?do=prepare';"><span><bean:message key="Interim.AutoCreateReceipt" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/autoCreateReceipt.do?do=preparePD';"><span><bean:message key="Interim.PDReceiptAuto" bundle="sysprop"/></span></a>
            </li>
         </ul>
      </li>
     <%} %>
     
     <li><div class="parent">���Ң�����</div>
	     <ul>
	    		<li>
	            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new';"><span>��§ҹ��â�� By Item</span></a>
	            </li>
	     </ul>
     </li>

    <li><div class="parent">��¡�÷����</div>
    	<ul>
    	   <%if(role.equalsIgnoreCase(User.TT)){ %>
    		<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/requestPromotionAction.do?do=prepare';"><span>�ѹ�֡�͹��ѵԨѴ��¡����ҹ���</span></a>
            </li>
            <%} %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/stockAction.do?do=prepareCustomer&action=new';"><span><bean:message key="Stock" bundle="sysprop"/> </span></a>
            </li>
         </ul>
      </li>
    
	 <%if(role.equalsIgnoreCase(User.VAN)){ %>
	     <li><div class="parent">��¡�� �ԡ/�׹</div>
		     <ul>
		    	<li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderAction.do?do=prepare&action=new&moveOrderType=MoveOrderRequisition';"><span>��¡����ԡ�Թ���</span></a>
		        </li>
		        <li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderAction.do?do=prepare&action=new&moveOrderType=MoveOrderReturn';"><span>��¡��㺤׹�Թ���</span></a>
		        </li>
		         <li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderSummaryAction.do?do=prepare&action=new';"><span>��§ҹ��ػ ��ԡ/�׹  </span></a>
		        </li>
		         <li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/billPlanAction.do?do=prepare&action=new';"><span>�Ѻ��ŷ� (Bill T)  </span></a>
		        </li>
		         <li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/requisitionProductAction.do?do=prepare&action=new';"><span><bean:message key="RequisitionProduct" bundle="sysprop"/> </span></a>
		        </li>
		     </ul>
	     </li>
	 <%} %>
</ul>
<%}%>


	
  
  