<%@page import="com.isecinc.pens.web.sales.OrderAction"%>
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

<ul id="nav">
	<li><a  href="javascript: void(0)" class="parent"><bean:message key="HomeMenu" bundle="sysprop"/></a>
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
           <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';"><span><bean:message key="ManageOrderReceipt" bundle="sysprop"/></span></a>
			</li>
          <%--   <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salestargetnew.do';"><span><bean:message key="SalesTarget" bundle="sysprop"/></span></a>
            </li> --%>
            
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
    <li><a  href="javascript: void(0)" class="parent"><bean:message key="MasterData" bundle="sysprop"/></a>
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
         <li><div 
            onclick="window.location='${pageContext.request.contextPath}/jsp/saleOrderAction.do?do=prepare&action=add'">
           <bean:message key="SalesOrder" bundle="sysprop"/>
         </div>
         </li>
         
         <li>
               <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/saleOrderAction.do?do=search&action=new'">
                    <bean:message key="SalesOrderSearch" bundle="sysprop"/>
               </a>
          </li>
      <%} %>
    <%} %>

    <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
    <li><a  href="javascript: void(0)" class="parent"><bean:message key="Reports" bundle="sysprop"/></a>
    	<ul>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoiceDetailReport.do';"><span><bean:message key="InvoiceDetailReport" bundle="sysprop"/></span></a>
            </li>
    		<%-- <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/performanceReport.do';"><span><bean:message key="PerformanceReport" bundle="sysprop"/></span></a>
            </li> --%>
            <li> 
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentNewReport.do';"><span><bean:message key="ReceiptReport" bundle="sysprop"/></span></a>
            </li>
             <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/cancelDetailReport.do';"><span><bean:message key="CancelDetailReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentAllReport.do';"><span><bean:message key="InvoicePaymentAllReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/detailedSalesReport.do';"><span><bean:message key="DetailedSalesReport" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new';"><span>��§ҹ��â�� By Item</span></a>
            </li>
            
        	<%-- <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetSummary.do';"><span><bean:message key="SalesTargetSummary" bundle="sysprop"/></span></a>
            </li> --%>
            
        </ul>
    </li>
    <%} %>
   
    <li class="parent"><a  href="javascript: void(0)" class="parent"><bean:message key="Interfaces" bundle="sysprop"/></a>
   		<ul>
           	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare';"><span><bean:message key="Interfaces" bundle="sysprop"/></span></a>
           	</li>
          	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/monitorInterfacesAction.do?do=prepare';"><span><bean:message key="MonitorInterfaces" bundle="sysprop"/></span></a>
           	</li>
           <%-- 	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manualUpdateAddressAction.do?do=prepare';"><span><bean:message key="ManualUpdateAddress" bundle="sysprop"/></span></a>
           	</li> --%>
       </ul>
    </li>
   
</ul>

  