<%@page import="util.UserUtils"%>
<%@page import="com.isecinc.pens.web.stock.StockConstants"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.report.salesanalyst.SAConstants"%>
<%
   /* for menu Count */
   int no = 0;
   int subNo = 0;	
	User user = (User)session.getAttribute("user");
    String role = user.getRoleSalesTarget(); 
%>
<ul id="nav">
<%if(user.getUserGroupId()==SAConstants.USER_GROUP_ID_ADMIN){%>
	<li><a href="javascript: void(0)" class="parent" ><span><bean:message key="HomeMenu" bundle="sysprop"/></span></a>
		<ul>
           	<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/userAction.do?do=init&action=new';"><span><bean:message key="User" bundle="sysprop"/></span></a>
			</li>
			<%--  <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/groupRoleAction.do?do=prepare&action=new';"><span><bean:message key="Group" bundle="sysprop"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/roleAction.do?do=prepare&action=new';"><span><bean:message key="Role" bundle="sysprop"/></span></a>
			</li>  --%> 
			
		</ul>
	</li>
<%} %>
	<li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesAnalystReportAction.do?do=prepare&action=new';"><span><bean:message key="SalesAnalysis" bundle="sysprop"/></span></a>
		<ul>
			<%-- <li>
	          <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesAnalystReportAction.do?do=prepare&action=new';"><span><bean:message key="SalesAnalysis" bundle="sysprop"/></span></a>
	       </li> --%>
		</ul>
	</li> 
	
	<%if ( UserUtils.userInRoleSpider(user,new String[]{User.ADMIN,User.SPIDER}) ){ %>
	<li><a href="javascript: void(0)" class="parent"><span>PENS Spider</span></a>
		<ul>
			 <li>
	          <a href="#" class="parent" 
	           onclick="window.location='${pageContext.request.contextPath}/jsp/locationAction.do?do=prepare&pageName=spider&action=new';">
	          <span>1.<bean:message key="Location" bundle="sysprop"/></span></a>
	       </li> 
	         <li>
	          <a href="#" class="parent" 
	           onclick="window.location='${pageContext.request.contextPath}/jsp/locationAction.do?do=prepare&pageName=monitorSpider&action=new';">
	          <span>2.<bean:message key="MonitorSpider" bundle="sysprop"/></span></a>
	       </li>  
	        <li>
	          <a href="#" class="parent" 
	           onclick="window.location='${pageContext.request.contextPath}/jsp/locationAction.do?do=prepare&pageName=trip&action=new';">
	          <span>3.<bean:message key="Trip" bundle="sysprop"/></span></a>
	       </li>  
		</ul>
	</li> 
	<%}%>
	
<%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MT_SALES,User.DD_SALES,User.MKT,User.MTMGR}) ){
	no=0;
%>
	<li><a href="javascript: void(0)" class="parent"><span>Sale Target</span></a>
		<ul>
	        <li><a class="parent"><span>1.เป้าหมายของ MT</span></a>
			       <ul>
			       <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MKT}) ){ %>
					     <li>
		                    <a href="#" class="parent" 
		                    onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_MKT%>&action=new';">
		                      <span>1.1 <bean:message key="MKT_SalesTarget" bundle="sysprop"/></span>
		                    </a>
		                 </li>
	                 <%}%>
	                  <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.DD_SALES,User.MT_SALES}) ){ %>
		                  <li>
		                    <a href="#" class="parent" 
		                    onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_SALES%>&action=new';">
		                    <span>1.2 <bean:message key="MT_SalesTarget" bundle="sysprop"/></span>
		                    </a>
		                 </li> 
	                 <%}%>
	                  <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MTMGR}) ){ %>
		                <li>
		                    <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_MTMGR%>&action=new';">
		                      <span>1.3 <bean:message key="MTMGR_SalesTarget" bundle="sysprop"/></span>
		                    </a>
		                 </li>
	                 <%} %>
			       </ul>
			   </li>
			   
			   <li>
					<a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_REPORT_SALES_TARGET%>&action=new';"> 
		                <span>2.<bean:message key="ReportSalesTarget" bundle="sysprop"/></span>
		            </a> 
	           </li> 
	            <li>
					 <%--  <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL%>&action=new';">
		                  <span>3 <bean:message key="ReportSalesTargetAll" bundle="sysprop"/></span>
		             </a>  --%>
	           </li> 
		 </ul>
	</li>
<%} %>
<%if ( UserUtils.userInRoleCreditStock(user,new String[]{User.ADMIN, User.STOCKCR}) ){ 
	no=0;
%>
	<li><a href="javascript: void(0)" class="parent" ><span>Credit Sales</span></a>
		<ul>
	        <li>
               <a href="#" class="parent" 
               onclick="window.location='${pageContext.request.contextPath}/jsp/stockAction.do?do=prepareSearch&pageName=<%=StockConstants.PAGE_CREDIT%>&action=new';">
               <span><%no++;out.print(no);%>.<bean:message key="ReportStockCredit" bundle="sysprop"/></span>
               </a>
             </li> 
              <li>
               <a href="#" class="parent" 
               onclick="window.location='${pageContext.request.contextPath}/jsp/promotionAction.do?do=prepareSearch&action=new';">
               <span><%no++;out.print(no);%>.<bean:message key="Promotion" bundle="sysprop"/></span>
               </a>
             </li> 
		</ul>
	</li>  
<%}%>
<%if ( UserUtils.userInRoleProdShow(user,new String[]{User.ADMIN, User.PRODSHOW}) 
		|| UserUtils.userInRoleVanDoc(user,new String[]{User.ADMIN, User.VANDOC})){ 
	  no=0;
%>
	<li><a href="javascript: void(0)" class="parent" ><span>Van Sales</span></a>
		<ul>
		 <%if ( UserUtils.userInRoleProdShow(user,new String[]{User.ADMIN, User.PRODSHOW}) ){ %>
	        <li>
               <a href="#" class="parent" 
               onclick="window.location='${pageContext.request.contextPath}/jsp/prodShowAction.do?do=prepareSearch&action=new';">
               <span><%no++;out.print(no);%>.<bean:message key="ProdShow" bundle="sysprop"/></span>
               </a>
             </li>
          <%} %> 
           <%if ( UserUtils.userInRoleVanDoc(user,new String[]{User.ADMIN, User.VANDOC}) ){ %>
               <li>
                <a href="#" class="parent" 
                   onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderAction.do?do=prepareSearch&action=new';">
                 <span><%no++;out.print(no);%>.<bean:message key="MoveOrder" bundle="sysprop"/></span>
                </a>
              </li>   
            <%} %>
		</ul>
	</li>  
<%}%>

<li>
   <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/administer/changePassword.jsp';"><span>เปลี่ยนรหัสผ่าน</span></a>	
</li>
	
</ul>
   