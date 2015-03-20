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
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/menu.css" type="text/css">
<script language = "javascript" src = "${pageContext.request.contextPath}/js/jquery.js"></script>
<script language = "javascript" src = "${pageContext.request.contextPath}/js/menu.js"></script>
<script language="javascript">
//test_click=false;
//hide_sub_menu('all');
</script>
<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" id="mainmenu">
<tr><td align="left" valign="middle">
<div id="menu">
    <ul class="menu">
        <li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp';"><span><bean:message key="HomeMenu" bundle="sysprop"/></span></a>
            <div><ul>
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
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/salestarget.do';"><span><bean:message key="SalesTarget" bundle="sysprop"/></span></a>
                </li>
                <%} %>
                <%if(role.equalsIgnoreCase(User.VAN)){ %>
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/inventory.do';"><span><bean:message key="InventoryOnhand" bundle="sysprop"/></span></a>
                </li>
                <!-- 
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/inventoryTransaction.do';"><span>Inventory Movement</span></a>
                </li>
                 -->
                <%} %>
                <%if(!role.equalsIgnoreCase(User.DD)) {%>
                <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/tripAction.do?do=prepare&type=user';"><span><bean:message key="Trip" bundle="sysprop"/></span></a>
                </li>
                <%}%>
                <%if(role.equalsIgnoreCase(User.ADMIN)){ %>
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/tripAction.do?do=prepare&type=admin';"><span><bean:message key="Trip" bundle="sysprop"/></span></a>
                </li>
                <%}%>
                <%} %>
            </ul></div>
        </li>
        <%if(!role.equalsIgnoreCase(User.ADMIN)){ %>
        <li><a href="#" class="parent"><span><bean:message key="MasterData" bundle="sysprop"/></span></a>
            <div><ul>
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
            </ul></div>
        </li>
        <%if(!role.equalsIgnoreCase(User.DD)){ %>
        <li><a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/customer.do';"><span><bean:message key="Customer" bundle="sysprop"/></span></a></li>
        <%} %>
        <%if(role.equalsIgnoreCase(User.DD)){ %>
        <li><a href="#"><span><bean:message key="Member" bundle="sysprop"/></span></a>
        	<div><ul>
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/member.do';"><span><bean:message key="MemberInfo" bundle="sysprop"/></span></a>
                </li>
               	<li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/memberrenew.do';"><span><bean:message key="Renew" bundle="sysprop"/></span></a>
                </li>
            </ul></div>
        </li>
        <%} %>
        <li>
        	<a href="#" class="parent"><span><bean:message key="Reports" bundle="sysprop"/></span></a>
        	<div><ul>
        	<%if(role.equalsIgnoreCase(User.DD)){ %>
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
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiptTempReport.do';"><span><bean:message key="ReceiptTemporary" bundle="sysprop"/></span></a>
                </li>
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/orderThreeMonthsReport.do';"><span>Order 3 Months Report</span></a>
                </li>        
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/remittanceReport.do';"><span><bean:message key="Remittance" bundle="sysprop"/></span></a>
                </li>
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiveBenecolReport.do';"><span><bean:message key="ReceiveBenecol" bundle="sysprop"/></span></a>
                </li>
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/receiveBagReport.do';"><span><bean:message key="ReceiveBag" bundle="sysprop"/></span></a>
                </li>
        	<%} %>
        	<%if(role.equalsIgnoreCase(User.VAN)){ %>
        		<li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/performanceReport.do';"><span><bean:message key="PerformanceReport" bundle="sysprop"/></span></a>
                </li>
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/invoicePaymentReport.do';"><span><bean:message key="ReceiptReport" bundle="sysprop"/></span></a>
                </li>
            <%} %>
            </ul></div>
        </li>
        <li class="parent">
        	<a href="#"><span>โอนข้อมูล</span></a>
        	<div><ul>
                <li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare';"><span>Interfaces</span></a>
                </li>
               	<li>
                	<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare';"><span>Monitor Interfaces</span></a>
                </li>
            </ul></div>
        </li>
        <%} %>
    </ul>
</div>
</td>
</tr>
</table>
<div id="copyright" style="display: none;">Copyright &copy; 2010 <a href="http://apycom.com/">Apycom jQuery Menus</a></div>