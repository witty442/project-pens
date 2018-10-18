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
int no = 0;
int subNo = 0;
%>

<ul id="nav">
	<li><a  href="javascript: void(0)" class="parent"><%no=0; %><font size="3"><bean:message key="HomeMenu" bundle="sysprop"/></font></a>
		<ul>
			<li> 
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/profile.do?id=<%=((User)session.getAttribute("user")).getId() %>';">
				<span>
				   <font size="2"><%no++;out.print(no);%>.<bean:message key="Profile" bundle="sysprop"/></font>
				</span></a>
			   
			</li>
			<%if(role.equalsIgnoreCase(User.ADMIN)){ %>
           	<li> 
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/user.do';">
				<span><font size="2"><%no++;out.print(no);%>.<bean:message key="User" bundle="sysprop"/></font></span></a>
			</li>
			<%} %>
            <li> 
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/docseq.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="DocumentSeq" bundle="sysprop"/></font></span></a>
            </li>
           <li> 
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manageOrderReceipt.do?do=prepare';">
				<span><font size="2"><%no++;out.print(no);%>.<bean:message key="ManageOrderReceipt" bundle="sysprop"/> </font></span></a>
			  
			</li>
          <%--   <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salestargetnew.do';"><span><bean:message key="SalesTarget" bundle="sysprop"/></span></a>
            </li> --%>
            
		</ul>
	</li>

    <%if(!role.equalsIgnoreCase(User.ADMIN)){ no=0;%>
    <li><a  href="javascript: void(0)" class="parent"><font size="3"><bean:message key="MasterData" bundle="sysprop"/></font></a>
        <ul>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/product.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="Product" bundle="sysprop"/></font></span></a>
            </li>
           	<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/pricelist.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="PriceList" bundle="sysprop"/></font></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/modifier.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="Promotion" bundle="sysprop"/></font></span></a>
            </li>
             <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/product/productC4.jsp';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="ProductC4" bundle="sysprop"/></font></span></a>
            </li>
            <%} %>
        </ul>
    </li>
    <%} %>
    <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
       <%if(!role.equalsIgnoreCase(User.DD)){ %>
         <li>
            <div 
            onclick="window.location='${pageContext.request.contextPath}/jsp/saleOrderAction.do?do=prepare&action=add'">
              <font size="3"><bean:message key="SalesOrder" bundle="sysprop"/></font>
            </div>
         </li>
         <li>
              <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/saleOrderAction.do?do=search&action=new'">
                 <font size="3"><bean:message key="SalesOrderSearch" bundle="sysprop"/></font>
             </a>
         </li>
      <%} %>
    <%} %>

    <%if(!role.equalsIgnoreCase(User.ADMIN)){ no=0;%>
    <li><a  href="javascript: void(0)" class="parent"><font size="3"><bean:message key="Reports" bundle="sysprop"/></font></a>
    	<ul>
            <li> 
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoiceDetailReport.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="InvoiceDetailReport" bundle="sysprop"/></font></span></a>
            </li>
    		 <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/performanceReport.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="PerformanceReport" bundle="sysprop"/></font></span></a>
            </li> 
            <li> 
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentNewReport.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="ReceiptReport" bundle="sysprop"/></font></span></a>
            </li>
             <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/cancelDetailReport.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="CancelDetailReport" bundle="sysprop"/></font></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentAllReport.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="InvoicePaymentAllReport" bundle="sysprop"/></font></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/detailedSalesReport.do';">
            	<span><font size="2"><%no++;out.print(no);%>.<bean:message key="DetailedSalesReport" bundle="sysprop"/></font></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new';">
            	<span><font size="2"><%no++;out.print(no);%>.รายงานการขาย By Item</font></span></a>
            </li>
            
        	<%-- <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetSummary.do';"><span><bean:message key="SalesTargetSummary" bundle="sysprop"/></span></a>
            </li> --%>
            
        </ul>
    </li>
    <%} %>
   
   <%no=0; %>
    <li class="parent"><a  href="javascript: void(0)" class="parent"><font size="3"><bean:message key="Interfaces" bundle="sysprop"/></font></a>
   		<ul>
           	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare';">
           		<span><font size="2"><%no++;out.print(no);%>.<bean:message key="Interfaces" bundle="sysprop"/></font></span></a>
           	</li>
          	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/monitorInterfacesAction.do?do=prepare';">
           		<span><font size="2"><%no++;out.print(no);%>.<bean:message key="MonitorInterfaces" bundle="sysprop"/></font></span></a>
           	</li>
       </ul>
    </li>
   
</ul>

  