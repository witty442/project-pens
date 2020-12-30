<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.pens.util.EncyptUtils"%>
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
 // System.out.println("Role:"+user.getRole().getKey());
%>
<ul id="nav">
 <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN}) ){ %>
      <li><a href="javascript: void(0)" class="parent">Admin</a><%no=0; %>
          <ul>
		  	  <li> 
		  	    <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/schedule.do?do=prepare&action=new';">
		  	    <span><%no++; out.print(no); %>&nbsp;<bean:message bundle="sysprop" key="scheduler"/></span></a>
			  </li>
			  <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/searchTask.do?do=prepare&action=new';">
				<span><%no++; out.print(no); %>&nbsp;<bean:message bundle="sysprop" key="searchTask"/></span></a>
			 </li>
	     </ul>
     </li>     
  <%} %>
  
 <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.TT}) ){no=0; %>
	<li><a  href="javascript: void(0)" class="parent"><bean:message key="HomeMenu" bundle="sysprop"/></a>
	<% no=0; %>
		<ul>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/profile.do?id=<%=((User)session.getAttribute("user")).getId() %>';"><span><%no++;out.print(no);%>.<bean:message key="Profile" bundle="sysprop"/></span></a>
			</li>
            <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.TT}) ){ %>
				<li>
					<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';"><span><%no++;out.print(no);%>.<bean:message key="ManageOrderReceipt" bundle="sysprop"/></span></a>
				</li>
			<%} %>
		</ul>
	</li>
  <%} %>

   <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.TT}) ){no=0; %>
       <li>
          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/customer.do';"><span><bean:message key="Customer" bundle="sysprop"/></span>
          </a>
       </li>
         <li>
              <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/saleOrderAction.do?do=search&action=new'">
                                       ค้นหาข้อมูลขาย
             </a>
         </li>
   <%} %>
            
    <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.TT}) ){ no=0; %>
    <li><a  href="javascript: void(0)" class="parent"><bean:message key="Reports" bundle="sysprop"/></a>
    	<ul>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/transactionSummaryReport.do';"><span><%no++;out.print(no);%>.<bean:message key="TransactionSummaryReport" bundle="sysprop"/></span></a>
            </li>
          
        	<li>
          	   <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentAllReport.do';"><span><%no++;out.print(no);%>.<bean:message key="InvoicePaymentAllReport" bundle="sysprop"/></span></a>
         	</li>
        	
        	<li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetSummary.do';"><span><%no++;out.print(no);%>.<bean:message key="SalesTargetSummary" bundle="sysprop"/></span></a>
            </li>
             <li>
           	     <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/budsAllAction.do?do=prepareSearchHead&action=new&pageName=InvoiceReport&subPageName=InvoiceReport';">
           	     <span><%no++;out.print(no);%>.<bean:message key="InvoiceReport" bundle="sysprop"/></span></a>
             </li>  
        </ul>
    </li>
    <%} %>
    
	 <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.TT}) ){ no=0; %>
        <li>
           <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.EXPORT_RECEIPT%>';">
		     <span><bean:message key="<%=BatchTaskConstants.EXPORT_RECEIPT%>" bundle="sysprop"/></span>
		  </a>
	   </li> 
    <% } %>
    
     <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.MT}) ){ %>
      <li><a  href="javascript: void(0)" class="parent">EDI</a><%no=0; %>
          <ul>
              <li>
           	    <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/budsAllAction.do?do=prepareSearchHead&action=new&pageName=OrderEDISearch&subPageName=OrderEDISearch';">
           	    <span><%no++;out.print(no); %>.<bean:message key="OrderEDISearch" bundle="sysprop"/></span></a>
              </li> 
              <%--  <li>
           	     <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/budsAllAction.do?do=viewDetail&action=new&pageName=OrderEDIDetailManual&subPageName=OrderEDIDetailManual';">
           	     <span><%no++;out.print(no); %>.<bean:message key="OrderEDIDetailManual" bundle="sysprop"/></span></a>
              </li>  --%>
           </ul>  
        </li>
    <% } %>
    
	 <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.BUD_ADMIN,User.STOCK,User.TT}) ){ %>
		   <li><a  href="javascript: void(0)" class="parent">Stock สินค้าของ PENS</a><%no=0; %>
	        <ul>
	           <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.STOCK}) ){ %>
		    		<li>
		            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/stockInvAction.do?do=prepareSearch&action=new';">
		            	<span><%no++;out.print(no);%>.<bean:message key="StockInv" bundle="sysprop"/></span></a>
		            </li>
	             <%} %>
	              <li>
            	   <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/budsAllAction.do?do=prepareSearchHead&action=new&pageName=StockOnhandSearch&subPageName=StockOnhand';">
            	   <span><%no++;out.print(no);%>.<bean:message key="StockOnhand" bundle="sysprop"/></span></a>
                </li>  
		     </ul>
	     </li> 
     <%} %>
     
   <%if ( UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.BUD_ADMIN}) ){ %>
	    <li><a href="javascript: void(0)" class="parent">Picking</a><%no=0; %>
        <ul>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/budsAllAction.do?do=prepareSearchHead&action=new&pageName=ConfPickingSearch&subPageName=ConfPicking';">
            	<span><%no++;out.print(no);%>.<bean:message key="ConfPickingSearch" bundle="sysprop"/></span></a>
            </li>
            <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/budsAllAction.do?do=prepareSearchHead&action=new&pageName=ConfPickingSearch&subPageName=BudsConfPicking';">
            	<span><%no++;out.print(no);%>.<bean:message key="BudsConfPicking" bundle="sysprop"/></span></a>
            </li>
             <li>
            	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/budsAllAction.do?do=prepareSearchHead&action=new&pageName=ControlPickingSearch&subPageName=ControlPicking';">
            	<span><%no++;out.print(no);%>.<bean:message key="ControlPicking" bundle="sysprop"/></span></a>
            </li> 
	     </ul>
     </li>  
   <%} %>
   
<!-- **************************** MENU NISSIN *********************************************** -->
  <jsp:include page="menu_nis.jsp"/>
<!-- **************************** /MENU NISSIN ********************************************** -->
   
</ul>

  