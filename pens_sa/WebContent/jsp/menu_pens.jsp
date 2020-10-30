<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.isecinc.pens.web.stock.StockConstants"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.pens.util.Utils"%>
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
<%if ( UserUtils.userInRole("",user,new String[]{User.ADMIN}) ){ %>
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
			<li>
	         <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/administer/changePassword.jsp';"><span>เปลี่ยนรหัสผ่าน</span></a>	
	       </li>
	        <li>
	            <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/aReportAction.do?do=prepare&action=new&reportName=SalesAnalyst';">
	              <span><bean:message key="SalesAnalysis" bundle="sysprop"/>(New Version) </span>
	            </a>
	         </li>
		</ul>
	</li>
<%} %>
<%if ( UserUtils.userInRole("ROLE_SA",user,new String[]{User.ADMIN,User.SA}) ){ %>
	<li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesAnalystReportAction.do?do=prepare&action=new&reportName=SalesAnalyst';">
	      <span><bean:message key="SalesAnalysis" bundle="sysprop"/>
	      </span>
	    </a>
		<ul>
	       <%--  <li>
	          <a href="#" class="parent"  onclick="window.location='${pageContext.request.contextPath}/jsp/aReportAction.do?do=prepare&action=new&reportName=SalesAnalyst';">
	          <span><bean:message key="SalesAnalyst" bundle="sysprop"/>(NEW)</span></a>
	       </li>  --%>
		</ul>
	</li> 
<%} %>

<%if ( UserUtils.userInRole("ROLE_SA",user,new String[]{User.ADMIN,User.PROJECTC}) ){ %>
	<li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/aReportAction.do?do=prepare&action=new&reportName=ProjectCAnalyst';">
	    <span><bean:message key="ProjectCAnalyst" bundle="sysprop"/></span></a>
	</li> 
<%} %>

<%if ( UserUtils.userInRole("ROLE_STOCK_VAN",user,new String[]{User.ADMIN,User.STOCKVAN}) 
	|| UserUtils.userInRole("ROLE_ONHAND",user,new String[]{User.ADMIN,User.ONHAND ,User.COVERAGE}) 
		){ %>
	<li><a href="javascript: void(0)" class="parent"><span>Inventory</span></a>
		<ul>
		<%if ( UserUtils.userInRole("ROLE_STOCK_VAN",user,new String[]{User.ADMIN,User.STOCKVAN})) {%>
			<li>
               <a href="#" class="parent" 
                 onclick="window.location='${pageContext.request.contextPath}/jsp/stockOnhandAction.do?do=prepareSearch&action=new&pageName=StockVan';">
                 <span><%no++;out.print(no);%>.<bean:message key="StockVan" bundle="sysprop"/></span>
               </a>
             </li>
          <%} %>
          <%if ( UserUtils.userInRole("ROLE_ONHAND",user,new String[]{User.ADMIN,User.ONHAND})) {%>
             <li>
               <a href="#" class="parent" 
                 onclick="window.location='${pageContext.request.contextPath}/jsp/stockOnhandAction.do?do=prepareSearch&action=new&pageName=StockOnhand';">
                 <span><%no++;out.print(no);%>.<bean:message key="StockOnhand" bundle="sysprop"/></span>
               </a>
             </li> 
           <%} %>
            <%if ( UserUtils.userInRole("ROLE_COVERAGE",user,new String[]{User.ADMIN,User.COVERAGE})) {%>
              <li>
                <a href="#" class="parent" 
                 onclick="window.location='${pageContext.request.contextPath}/jsp/stockOnhandAction.do?do=prepareSearch&action=new&pageName=StockCoverage';">
                 <span><%no++;out.print(no);%>.<bean:message key="StockCoverage" bundle="sysprop"/></span>
                </a> 
              </li> 
           <%} %>
		</ul>
	</li> 
<%}%>
	
<%if ( UserUtils.userInRole("ROLE_SPIDER",user,new String[]{User.ADMIN,User.SPIDER}) ){ %>
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
	
<%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MT_SALES,User.DD_SALES
		,User.MKT,User.MTMGR,User.TTSUPER,User.TTMGR
		,User.PD}) ){
	no=0;
%>
	<li><a href="javascript: void(0)" class="parent"><span>Sale Target</span></a>
		<ul>
		 <!-- ****************************************************************************** -->
		 <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MT_SALES,User.DD_SALES,User.MKT,User.MTMGR}) ){ %>
	         <li><a class="parent"><span><% subNo=0;no++;out.print(no); %>.เป้าหมายของ MT</span></a>
			       <ul>
			       <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MKT}) ){ %>
					     <li>
		                    <a href="#" class="parent" 
		                    onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_MKT%>&action=new';">
		                      <span><%subNo++;out.print(no+"."+subNo+" "); %><bean:message key="MKT_SalesTarget" bundle="sysprop"/></span>
		                    </a>
		                 </li>
	                 <%}%>
	                  <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.DD_SALES,User.MT_SALES}) ){ %>
		                  <li>
		                    <a href="#" class="parent" 
		                    onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_MTSALES%>&action=new';">
		                    <span><%subNo++;out.print(no+"."+subNo+" "); %> <bean:message key="MT_SalesTarget" bundle="sysprop"/></span>
		                    </a>
		                 </li> 
	                 <%}%>
	                  <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MTMGR}) ){ %>
		                <li>
		                    <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_MTMGR%>&action=new';">
		                      <span><%subNo++;out.print(no+"."+subNo+" "); %><bean:message key="MTMGR_SalesTarget" bundle="sysprop"/></span>
		                    </a>
		                 </li>
	                 <%} %>
					    <li>
							<a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_REPORT_SALES_TARGET%>&action=new';"> 
				                <span><%subNo++;out.print(no+"."+subNo+" "); %><bean:message key="ReportSalesTargetMT" bundle="sysprop"/></span>
				            </a> 
			           </li>  
			            <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.ADMIN}) ){ %>
			                 <li>
			                    <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_MTADMIN%>&action=new';">
			                      <span><%subNo++;out.print(no+"."+subNo+" "); %><bean:message key="MTADMIN_SalesTarget" bundle="sysprop"/></span>
			                    </a>
			                 </li>  
	                    <%} %>
			       </ul>
	             </li>
	       <%} %>
	            
		 <!-- ********************** TT **************************************** -->
		  <%if (UserUtils.userInRoleSalesTarget(user,new String[]{User.MKT,User.TTMGR,User.TTSUPER}) ){ %>
	         <li><a class="parent"><span><% subNo=0;no++;out.print(no); %>.เป้าหมายของ TT</span></a>
			       <ul>
			       <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MKT}) ){ %>
					     <li>
		                    <a href="#" class="parent" 
		                    onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_MKT_TT%>&action=new';">
		                      <span><%subNo++;out.print(no+"."+subNo+" "); %><bean:message key="MKT_SalesTarget_TT" bundle="sysprop"/></span>
		                    </a>
		                 </li>
	                 <%}%>
	                  <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.TTSUPER}) ){ %>
		                  <li>
		                    <a href="#" class="parent" 
		                    onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_TTSUPER%>&action=new';">
		                    <span><%subNo++;out.print(no+"."+subNo+" "); %> <bean:message key="TTSUPER_SalesTarget" bundle="sysprop"/></span>
		                    </a>
		                 </li> 
	                 <%}%>
	                  <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.TTMGR}) ){ %>
		                  <li>
		                    <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_TTMGR%>&action=new';">
		                      <span><%subNo++;out.print(no+"."+subNo+" "); %><bean:message key="TTMGR_SalesTarget" bundle="sysprop"/></span>
		                    </a>
		                 </li> 
	                 <%} %>
					   <li>
							<a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&subPageName=TT&pageName=<%=SalesTargetConstants.PAGE_REPORT_SALES_TARGET%>&action=new';"> 
				                <span><%subNo++;out.print(no+"."+subNo+" "); %><bean:message key="ReportSalesTargetTT" bundle="sysprop"/></span>
				            </a> 
			           </li>  
			             <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.ADMIN}) ){ %>
			                 <li>
			                    <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_TTADMIN%>&action=new';">
			                      <span><%subNo++;out.print(no+"."+subNo+" "); %><bean:message key="TTADMIN_SalesTarget" bundle="sysprop"/></span>
			                    </a>
			                 </li>  
	                    <%} %>
			       </ul>
	             </li>
	          <%} %>
	          <!-- ****************************************************************************** -->
	            <li>
					 <%--  <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL%>&action=new';">
		                  <span>3 <bean:message key="ReportSalesTargetAll" bundle="sysprop"/></span>
		             </a>  --%>
	           </li> 
	           
	         <%if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.PD}) ){ %>
                  <li>
                    <a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/salesTargetAction.do?do=prepareSearch&pageName=<%=SalesTargetConstants.PAGE_SALES_TARGET_PD%>&action=new';">
                      <span><%no++;out.print(no+"."); %><bean:message key="<%=SalesTargetConstants.PAGE_SALES_TARGET_PD%>" bundle="sysprop"/></span>
                    </a>
                 </li>  
             <%} %>
		 </ul>
	</li>
<%} %>
<%if (   UserUtils.userInRole("ROLE_CR_STOCK",user,new String[]{User.ADMIN, User.STOCKCR,User.STOCKCRSALE,User.PROJECTC}) 
	  || UserUtils.userInRole("ROLE_PRODSHOW",user,new String[]{User.ADMIN, User.PRODSHOW})) { 
	no=0;
%>
	<li><a href="javascript: void(0)" class="parent" ><span>Credit Sales</span></a>
		<ul>
		       <%if ( UserUtils.userInRole("ROLE_CR_STOCK",user,new String[]{User.ADMIN, User.STOCKCR,User.STOCKCRSALE})){ %>
		          <li>
		               <a href="#" class="parent" 
		               onclick="window.location='${pageContext.request.contextPath}/jsp/stockAction.do?do=prepareSearch&pageName=<%=StockConstants.PAGE_STOCK_CREDIT%>&action=new';">
		               <span><%no++;out.print(no);%>.<bean:message key="ReportStockCredit" bundle="sysprop"/></span>
		               </a>
	              </li> 
	          <%} %>
	         <%if ( UserUtils.userInRole("ROLE_CR_STOCK",user,new String[]{User.ADMIN,User.STOCKCR,User.STOCKCRSALE})){ %>
                  <li>
	               <a href="#" class="parent" 
	               onclick="window.location='${pageContext.request.contextPath}/jsp/stockAction.do?do=prepareSearch&pageName=<%=StockConstants.PAGE_STOCK_CR_EXPIRE%>&action=new';">
	               <span><%no++;out.print(no);%>.<bean:message key="<%=StockConstants.PAGE_STOCK_CR_EXPIRE%>" bundle="sysprop"/></span>
	               </a>
	             </li>  
             <%} %>
             <%if ( UserUtils.userInRole("ROLE_PRODSHOW",user,new String[]{User.ADMIN, User.PRODSHOW}) ){ %>
		        <li>
	               <a href="#" class="parent" 
	               onclick="window.location='${pageContext.request.contextPath}/jsp/prodShowAction.do?do=prepareSearch&action=new&pageName=CREDIT';">
	               <span><%no++;out.print(no);%>.<bean:message key="ProdShow" bundle="sysprop"/></span>
	               </a>
	             </li>
            <%} %> 
              <%if ( UserUtils.userInRole("ROLE_CR_STOCK",user,new String[]{User.ADMIN, User.STOCKCR,User.STOCKCRSALE})){ %>
	              <li>
		              <a href="#" class="parent" 
		              onclick="window.location='${pageContext.request.contextPath}/jsp/promotionAction.do?do=prepareSearch&action=new';">
		              <span><%no++;out.print(no);%>.<bean:message key="Promotion" bundle="sysprop"/></span>
		              </a>
	             </li> 
             <%} %>
             <%if ( UserUtils.userInRole("ROLE_CR_STOCK",user,new String[]{User.ADMIN,User.STOCKCR,User.STOCKCRSALE})){ %>
                  <li>
	               <a href="#" class="parent" 
	               onclick="window.location='${pageContext.request.contextPath}/jsp/reportAllAction.do?do=prepare&pageName=StockReturn&action=new';">
	               <span><%no++;out.print(no);%>.<bean:message key="StockReturn" bundle="sysprop"/></span>
	               </a>
	             </li> 
	          <%} %> 
             <%if ( UserUtils.userInRole("ROLE_CR_STOCK",user,new String[]{User.ADMIN,User.STOCKCR,User.STOCKCRSALE})){ %>
                 <li>
	               <a href="#" class="parent" 
	               onclick="window.location='${pageContext.request.contextPath}/jsp/stockAction.do?do=prepareSearch&pageName=<%=StockConstants.PAGE_STOCK_CALLC_CREDIT%>&action=new';">
	               <span><%no++;out.print(no);%>.<bean:message key="ReportStockCallCardCredit" bundle="sysprop"/></span>
	               </a>
	             </li> 
             <%} %>
             <%if ( UserUtils.userInRole("ROLE_CR_STOCK",user,new String[]{User.ADMIN,User.STOCKCR})){ %>
                  <li>
	               <a href="#" class="parent" 
	                 onclick="window.location='${pageContext.request.contextPath}/jsp/reportAllAction.do?do=prepare&pageName=EffectiveSKUReport&action=new';">
	                 <span><%no++;out.print(no);%>.<bean:message key="EffectiveSKUReport" bundle="sysprop"/></span>
	               </a>
	             </li>  
             <%} %>
             <%if ( UserUtils.userInRole("ROLE_CR_STOCK",user,new String[]{User.ADMIN,User.PROJECTC})){ %>
                  <li>
	               <a href="#" class="parent" 
	               onclick="window.location='${pageContext.request.contextPath}/jsp/projectCAction.do?do=prepareSearch&action=new';">
	               <span><%no++;out.print(no);%>.<bean:message key="ProjectC" bundle="sysprop"/></span>
	               </a>
	             </li>  
	          <%} %>
	         <%if ( UserUtils.userInRole("ROLE_CR_STOCK",user,new String[]{User.ADMIN,User.PROJECTC})){ %>
	             <li>
	               <a href="#" class="parent" 
	               onclick="window.location='${pageContext.request.contextPath}/jsp/reportAllAction.do?do=prepare&action=new&pageName=ProjectCReport';">
	               <span><%no++;out.print(no);%>.<bean:message key="ProjectCReport" bundle="sysprop"/></span>
	               </a>
	             </li>  
	             
             <%} %>
		</ul>
	</li>  
<%}%>
<%if (   UserUtils.userInRole("ROLE_PRODSHOW",user,new String[]{User.ADMIN, User.PRODSHOW}) 
	  || UserUtils.userInRole("ROLE_VANDOC",user,new String[]{User.ADMIN, User.VANDOC})){ 
	  no=0;
%>
	<li><a href="javascript: void(0)" class="parent" ><span>Van Sales</span></a>
		<ul>
		 <%if ( UserUtils.userInRole("ROLE_PRODSHOW",user,new String[]{User.ADMIN, User.PRODSHOW}) ){ %>
	        <li>
               <a href="#" class="parent" 
               onclick="window.location='${pageContext.request.contextPath}/jsp/prodShowAction.do?do=prepareSearch&action=new&pageName=VAN';">
               <span><%no++;out.print(no);%>.<bean:message key="ProdShow" bundle="sysprop"/></span>
               </a>
             </li>
          <%} %> 
           <%if ( UserUtils.userInRole("ROLE_VANDOC",user,new String[]{User.ADMIN, User.VANDOC}) ){ %>
               <li>
                <a href="#" class="parent" 
                   onclick="window.location='${pageContext.request.contextPath}/jsp/moveOrderAction.do?do=prepareSearch&action=new';">
                 <span><%no++;out.print(no);%>.<bean:message key="MoveOrder" bundle="sysprop"/></span>
                </a>
              </li>   
            <%} %>
             <%if ( UserUtils.userInRole("ROLE_VANDOC",user,new String[]{User.ADMIN, User.VANDOC}) ){ %>
               <li>
                 <a href="#" class="parent" 
                   onclick="window.location='${pageContext.request.contextPath}/jsp/billPlanAction.do?do=prepareSearch&action=new';">
                 <span><%no++;out.print(no);%>.<bean:message key="BILLT_INCOMPLETE" bundle="sysprop"/></span>
                </a>  
              </li>   
            <%} %>
             <%if ( UserUtils.userInRole("ROLE_VANDOC",user,new String[]{User.ADMIN, User.VANDOC}) ){ %>
               <li>
                <a href="#" class="parent" 
                 onclick="window.location='${pageContext.request.contextPath}/jsp/vanAction.do?do=prepareSearch&action=new&pageName=OrderVanVO';">
                 <span><%no++;out.print(no);%>.<bean:message key="OrderVanVO" bundle="sysprop"/></span>
                </a> 
              </li>  
           <%} %>
           <%if ( UserUtils.userInRole("ROLE_VANDOC",user,new String[]{User.ADMIN, User.VANDOC}) ){ %>
               <li>
	               <a href="#" class="parent" 
	               onclick="window.location='${pageContext.request.contextPath}/jsp/reportAllAction.do?do=prepare&pageName=BoxNoNissinReport&action=new';">
	               <span><%no++;out.print(no);%>.<bean:message key="BoxNoNissinReport" bundle="sysprop"/></span>
	               </a>  
	            </li>  
	       <%} %>
		</ul>
	</li>  
<%}%>
<%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY,User.MT_QUERY}) ){ 
	  no=0;
%>
	<li><a href="javascript: void(0)" class="parent" ><span>MC</span></a>
		<ul>
		 <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY,User.MT_QUERY}) ){ %>
	        <li>
               <a href="#" class="parent"  
               onclick="window.location='${pageContext.request.contextPath}/jsp/stockMCAction.do?do=prepareSearch&action=new&pageName=STOCKMC';">
               <span><%no++;out.print(no);%>.<bean:message key="StockMC" bundle="sysprop"/></span>
               </a>
             </li> 
          <%} %> 
          <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN,User.MT_QUERY}) ){ %>
	        <li>
               <a href="#" class="parent"  
               onclick="window.location='${pageContext.request.contextPath}/jsp/stockMCAction.do?do=prepareSearch&action=new&pageName=MasterItemStockMC';">
               <span><%no++;out.print(no);%>.<bean:message key="MasterItemStockMC" bundle="sysprop"/></span>
               </a>
             </li> 
              <%-- <li>
               <a href="#" class="parent"  
               onclick="window.location='${pageContext.request.contextPath}/jsp/stockMCAction.do?do=prepareSearch&action=new&pageName=STOCKMCQuery';">
               <span><%no++;out.print(no);%>.<bean:message key="StockMCQuery" bundle="sysprop"/></span>
               </a>
             </li>  --%>
          <%} %> 
		</ul>
	</li>  
<%}%>
<!-- ************************************************************************** -->
<%if ( UserUtils.userInRole("ROLE_B2B",user,new String[]{User.ADMIN, User.B2B_MAKRO}) ){ 
	  no=0;
%>
    <li><a href="javascript: void(0)" class="parent" ><span>B2B</span></a>
		<ul>
          <%if (UserUtils.userInRole("ROLE_B2B",user,new String[]{User.ADMIN,User.B2B_MAKRO}) ){ %>
               <li>
               <a href="#" class="parent"  
               onclick="window.location='${pageContext.request.contextPath}/jsp/b2bAction.do?do=prepareSearch&action=new&pageName=B2BMakro';">
               <span><%no++;out.print(no);%>.<bean:message key="B2BMakro" bundle="sysprop"/></span>
               </a>
             </li>  
             <li>
               <a href="#" class="parent"  
               onclick="window.location='${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=ImportB2BSalesItemMakroFromExcel';">
               <span><%no++;out.print(no);%>.<bean:message key="ImportB2BSalesItemMakroFromExcel" bundle="sysprop"/></span>
               </a>
             </li>  
               <li>
               <a href="#" class="parent"  
               onclick="window.location='${pageContext.request.contextPath}/jsp/b2bAction.do?do=prepareSearch&action=new&pageName=QueryB2BMakroSalesByItem';">
               <span><%no++;out.print(no);%>.<bean:message key="QueryB2BMakroSalesByItem" bundle="sysprop"/></span>
               </a>
             </li>  
		   <%} %>
	 	</ul>
	</li>      
<%}%>
<!-- ************************************************************************** -->
<%if ( UserUtils.userInRole("ROLE_MKT",user,new String[]{User.ADMIN, User.PREORDER}) ){ 
	  no=0;
%>
    <li><a href="javascript: void(0)" class="parent" ><span>Marketing</span></a>
	  <ul>
	 <%if ( UserUtils.userInRole("ROLE_MKT",user,new String[]{User.ADMIN,User.PREORDER})) {%>
	      <li>
	        <a href="#" class="parent" 
	         onclick="window.location='${pageContext.request.contextPath}/jsp/stockOnhandAction.do?do=prepareSearch&action=new&pageName=PreOrderNissin';">
	         <span><%no++;out.print(no);%>.<bean:message key="PreOrderNissin" bundle="sysprop"/></span>
	        </a> 
	      </li> 
	   <%} %>
	 </ul>
	</li>      
<%}%>

<%if ( !UserUtils.userInRole("",user,new String[]{User.ADMIN}) ){ %>
	<li>
	    <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/administer/changePassword.jsp';"><span>เปลี่ยนรหัสผ่าน</span></a>	
	</li>
<%} %>
</ul>
   