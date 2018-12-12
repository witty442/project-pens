<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%
String role = ((User)session.getAttribute("user")).getType();
User user = (User)session.getAttribute("user");
/** No of menu **/
int no = 0;
int subNo = 0;
%>

<ul id="nav">
	<li><a  href="javascript: void(0)" class="parent"><bean:message key="HomeMenu" bundle="sysprop"/></a>
	<% no=0; %>
		<ul>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/profile.do?id=<%=((User)session.getAttribute("user")).getId() %>';"><span><%no++;out.print(no);%>.<bean:message key="Profile" bundle="sysprop"/></span></a>
			</li>
			<%if(role.equalsIgnoreCase(User.ADMIN)){ %>
           	<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/user.do';"><span><%no++;out.print(no);%>.<bean:message key="User" bundle="sysprop"/></span></a>
			</li>
			<%} %>
            <%if(role.equalsIgnoreCase(User.ADMIN)||role.equalsIgnoreCase(User.DD)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/sysconfig.do';"><span><%no++;out.print(no);%>.<bean:message key="SystemConf" bundle="sysprop"/></span></a>
            </li>
            <%} %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/docseq.do';"><span><%no++;out.print(no);%>.<bean:message key="DocumentSeq" bundle="sysprop"/></span></a>
            </li>
            <%if(!role.equalsIgnoreCase(User.ADMIN)&&!role.equalsIgnoreCase(User.DD)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salestargetnew.do';"><span><%no++;out.print(no);%>.<bean:message key="SalesTarget" bundle="sysprop"/></span></a>
            </li>
            <%} %>
            <%if(role.equalsIgnoreCase(User.VAN)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/inventory.do';"><span><%no++;out.print(no);%>.<bean:message key="InventoryOnhand" bundle="sysprop"/></span></a>
            </li>
           
            <%} %>
            <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/tripAction.do?do=prepare&type=admin';"><span><%no++;out.print(no);%>.<bean:message key="Trip" bundle="sysprop"/></span></a>
            </li>
            <%}%>
            <%if(!role.equalsIgnoreCase(User.DD) && !role.equalsIgnoreCase(User.ADMIN)) {%>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/tripAction.do?do=prepare&type=user';"><span><%no++;out.print(no);%>.<bean:message key="Trip" bundle="sysprop"/></span></a>
            </li>
            <%} %>
            <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
           	<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/administer/customerSeqUpdate.jsp';"><span><%no++;out.print(no);%>.<bean:message key="CustomerSequenceUpdate" bundle="sysprop"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';"><span><%no++;out.print(no);%>.<bean:message key="ManageOrderReceipt" bundle="sysprop"/></span></a>
			</li>
			<%} %>
			<!-- WIT EDIT  -->
			<%if(role.equalsIgnoreCase(User.VAN) || role.equalsIgnoreCase(User.TT) || role.equalsIgnoreCase(User.DD)){ %>
	           	<li>
					<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';"><span><%no++;out.print(no);%>.<bean:message key="ManageOrderReceipt" bundle="sysprop"/></span></a>
				</li>
			<%} %>
		
			<li>
			<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/pdReceipt.do?do=prepare';"><span><%no++;out.print(no);%>.<bean:message key="PD" bundle="sysprop"/></span></a>
			</li>

			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/softwareUpdater/SalesAppUpdater.jsp';"><span><%no++;out.print(no);%>.ปรับปรุงโปรแกรม SalesApp</span></a>
			</li>
			<%if( !role.equalsIgnoreCase(User.ADMIN)){ %>
           	<%-- <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/clearInvoiceAction.do?do=prepare&action=new';"><span><%no++;out.print(no);%>.<bean:message key="ClearInvoice" bundle="sysprop"/></span></a>
			</li> --%>
			<%} %>
		</ul>
	</li>
	<!-- WIT Edit :04/08/2554 -->
	<%if(role.equalsIgnoreCase(User.ADMIN)){ no=0;%>
       <li>
          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/customer.do';"><span><bean:message key="Customer" bundle="sysprop"/></span>
          </a>
       </li>
     <%} %>
            
    <%if(!role.equalsIgnoreCase(User.ADMIN)){ no=0;%>
    <li><a  href="javascript: void(0)" class="parent"><bean:message key="MasterData" bundle="sysprop"/></a>
        <ul>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/product.do';"><span><%no++;out.print(no);%>.<bean:message key="Product" bundle="sysprop"/></span></a>
            </li>
           	<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/pricelist.do';"><span><%no++;out.print(no);%>.<bean:message key="PriceList" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/modifier.do';"><span><%no++;out.print(no);%>.<bean:message key="Promotion" bundle="sysprop"/></span></a>
            </li>
             <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/product/productC4.jsp';"><span><%no++;out.print(no);%>.<bean:message key="ProductC4" bundle="sysprop"/></span></a>
            </li>
            <%} %>
        </ul>
    </li>
    <%} %>
    <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
      <%if(!role.equalsIgnoreCase(User.DD)){ no=0;%>
       <li><div onclick="window.location='${pageContext.request.contextPath}/jsp/customer.do';"><bean:message key="Customer" bundle="sysprop"/></div></li>
      <%} %>
    <%} %>
    
    <%if(!role.equalsIgnoreCase(User.ADMIN)){ no=0;%>
    <li><a  href="javascript: void(0)" class="parent"><bean:message key="Reports" bundle="sysprop"/></a>
    	<ul>
    		<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/orderThreeMonthsReport.do';"><span><%no++;out.print(no);%>.<bean:message key="OrderHistoryReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesHistoryReport.do';"><span><%no++;out.print(no);%>.<bean:message key="InterfaceHistoryReport" bundle="sysprop"/></span></a>
            </li>
            <%if(!role.equalsIgnoreCase(User.DD)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/transactionSummaryReport.do';"><span><%no++;out.print(no);%>.<bean:message key="TransactionSummaryReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoiceDetailReport.do';"><span><%no++;out.print(no);%>.<bean:message key="InvoiceDetailReport" bundle="sysprop"/></span></a>
            </li>
            <%} %>

    		<%if(role.equalsIgnoreCase(User.VAN)){ %>
    		<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/performanceReport.do';"><span><%no++;out.print(no);%>.<bean:message key="PerformanceReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentNewReport.do';"><span><%no++;out.print(no);%>.<bean:message key="ReceiptReport" bundle="sysprop"/></span></a>
            </li>
             <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/cancelDetailReport.do';"><span><%no++;out.print(no);%>.<bean:message key="CancelDetailReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderReport.do';"><span><%no++;out.print(no);%>.<bean:message key="MoveOrderReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentAllReport.do';"><span><%no++;out.print(no);%>.<bean:message key="InvoicePaymentAllReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/detailedSalesReport.do';"><span><%no++;out.print(no);%>.<bean:message key="DetailedSalesReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/chequeReport.do';"><span><%no++;out.print(no);%>.<bean:message key="ChequeReport" bundle="sysprop"/></span></a>
            </li>
             <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentReport.do';"><span><%no++;out.print(no);%>.<bean:message key="ReceiptReport" bundle="sysprop"/>(เวอร์ชั่นเก่า)</span></a>
            </li>
            
        	<%} %>
        	<%if(role.equalsIgnoreCase(User.TT)){ %>
	        	<li>
	          	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentAllReport.do';"><span><%no++;out.print(no);%>.<bean:message key="InvoicePaymentAllReport" bundle="sysprop"/></span></a>
	         	</li>
        	<%} %>
        	<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetSummary.do';"><span><%no++;out.print(no);%>.<bean:message key="SalesTargetSummary" bundle="sysprop"/></span></a>
            </li>
            <%if(role.equalsIgnoreCase(User.VAN)){ %>
	            <li>
	            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/creditControlReport.do';"><span><%no++;out.print(no);%>.<bean:message key="CreditControlReport" bundle="sysprop"/></span></a>
	            </li>
	             <li>
	            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/creditPaidReport.do?do=prepareReport&action=new';"><span><%no++;out.print(no);%>.<bean:message key="CreditPaidReport" bundle="sysprop"/></span></a>
	            </li>
	             <li>
	            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/creditNoPaidReport.do?do=prepareReport&action=new';"><span><%no++;out.print(no);%>.<bean:message key="CreditNoPaidReport" bundle="sysprop"/></span></a>
	            </li>
           <%} %>
           <%if(role.equalsIgnoreCase(User.TT)){ %>
                <li>
	            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/stockAction.do?do=stockReport&action=new';"><span><%no++;out.print(no);%>.<bean:message key="StockCreditReport" bundle="sysprop"/></span></a>
	         	</li>
           <%} %>
        </ul>
    </li>
    <%} %>
   
    <li class="parent"><a  href="javascript: void(0)" class="parent"><bean:message key="Interfaces" bundle="sysprop"/></a>
   		<%no=0; %>
   		<ul>
           	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare';"><span><%no++;out.print(no);%>.<bean:message key="Interfaces" bundle="sysprop"/></span></a>
           	</li>
          	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/monitorInterfacesAction.do?do=prepare';"><span><%no++;out.print(no);%>.<bean:message key="MonitorInterfaces" bundle="sysprop"/></span></a>
           	</li>
           	<%if(role.equalsIgnoreCase(User.ADMIN)){%>
             	<li>
           		  <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manualUpdateAddressAction.do?do=prepare';"><span><%no++;out.print(no);%>.<bean:message key="ManualUpdateAddress" bundle="sysprop"/></span></a>
             	</li>
           	<%} %>
       </ul>
   </li>
   <%if(role.equalsIgnoreCase(User.ADMIN)){ no=0;%>
    <li><a  href="javascript: void(0)" class="parent"><bean:message key="Interim" bundle="sysprop"/></a>
    	<ul>
    		<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/autoCreateReceipt.do?do=prepare';"><span><%no++;out.print(no);%>.<bean:message key="Interim.AutoCreateReceipt" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/autoCreateReceipt.do?do=preparePD';"><span><%no++;out.print(no);%>.<bean:message key="Interim.PDReceiptAuto" bundle="sysprop"/></span></a>
            </li>
         </ul>
      </li>
     <%} %>
     
     <li><a  href="javascript: void(0)" class="parent">ค้นหาข้อมูล</a><%no=0; %>
	     <ul>
	    		<li>
	            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new';"><span><%no++;out.print(no);%>.รายงานการขาย By Item</span></a>
	            </li>
	     </ul>
     </li>

    <li><a  href="javascript: void(0)" class="parent">รายการทั่วไป</a><%no=0; %>
    	<ul>
    	   <%if(role.equalsIgnoreCase(User.TT)){ %>
    		<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/requestPromotionAction.do?do=prepare';"><span><%no++;out.print(no);%>.บันทึกใบอนุมัติจัดรายการร้านค้า</span></a>
            </li>
            <%} %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/stockAction.do?do=prepareCustomer&action=new';"><span><%no++;out.print(no);%>.<bean:message key="Stock" bundle="sysprop"/> </span></a>
            </li>
         </ul>
      </li>
    
	 <%if(role.equalsIgnoreCase(User.VAN)){ no=0;%>
	     <li><a  href="javascript: void(0)" class="parent">รายการ เบิก/คืน</a>
		     <ul>
		    	<li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderAction.do?do=prepare&action=new&moveOrderType=MoveOrderRequisition';"><span><%no++;out.print(no);%>.รายการใบเบิกสินค้า</span></a>
		        </li>
		        <li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderAction.do?do=prepare&action=new&moveOrderType=MoveOrderReturn';"><span><%no++;out.print(no);%>.รายการใบคืนสินค้า</span></a>
		        </li>
		         <li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderSummaryAction.do?do=prepare&action=new';"><span><%no++;out.print(no);%>.รายงานสรุป ใบเบิก/คืน  </span></a>
		        </li>
		         <li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/billPlanAction.do?do=prepare&action=new';"><span><%no++;out.print(no);%>.รับบิลที (Bill T)  </span></a>
		        </li>
		         <li>
		          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/requisitionProductAction.do?do=prepare&action=new';"><span><%no++;out.print(no);%>.<bean:message key="RequisitionProduct" bundle="sysprop"/> </span></a>
		        </li>
		     </ul>
	     </li>
	 <%} %>
</ul>

  