<%@page import="com.isecinc.pens.web.shop.ShopAction"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.pens.util.EnvProperties"%>
<%@page import="java.net.InetAddress"%>
<%@page import="com.pens.util.Constants"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%
  int no = 0;
  int subNo = 0;		
  EnvProperties env = EnvProperties.getInstance();
  User user = (User)session.getAttribute("user");
 // System.out.println("Role:"+user.getRole().getKey());
  
  String userName =user.getUserName();
  String password=user.getPassword();
  
  String webPath = request.getContextPath();
  String contextPathProd = request.getContextPath();
  String contextPathDD = request.getContextPath();

  String ipProd = env.getProperty("ftp.ip.server");
  String hostProd = "http://"+ipProd+":8080";
  
  String ipDD = env.getProperty("host.dd.server");
  String hostDD = "http://"+ipDD+":8081";
  
  String currentIP =InetAddress.getLocalHost().getHostAddress();
  //System.out.println("Current IP:"+currentIP);

  //case Server Test contextPath = pens_bme_test
  if("192.168.38.186".equals(ipProd)){ //For Test
	  if("192.168.202.8".equals(currentIP)){ //On Witty
	      contextPathProd ="/pens_bme";
	  }else{
		  contextPathProd ="/pens_bme_test"; //ON UAT SERVER
	  }
  }
  //For test on Witty host
  if("192.168.202.8".equals(ipDD)){ 
	  contextPathDD ="/pens_bme";
	  hostDD = "http://"+ipDD+":8080";
  }
  //For BME test host
  if("192.168.202.7".equals(ipDD) && webPath.equalsIgnoreCase("/pens_bme_test")){ 
	  contextPathDD ="/pens_bme_test";
	  hostDD = "http://"+ipDD+":8081";
  }
  
  //System.out.println("contextPathProd:"+contextPathProd);
  //System.out.println("contextPathDD:"+contextPathDD);

  //System.out.println("webPath:"+webPath);
  //System.out.println("currentIP:"+currentIP);
%>
<script>
 <!--For Test Only -->
 <%if(webPath.equalsIgnoreCase("/pens_bme_test") 
	|| "192.168.202.8".equalsIgnoreCase(currentIP)){ %>
	 function link(isProd,url){
		 linkModelTest(false,url);
	 }
 <%}else {%>
	 function link(isProd,url){
		 linkModel(isProd,url);
	 }
 <%}%>
 
 <!--Production -->
 function linkModel(isProd,url){
	  var newUrl ;
	  <% 
	    // Link to Isec Server  DDServer To IsecServer
	    //input  : /jsp/mcAction.do?do=prepareMCStaff&action=new
	    //output : login.do?do=loginCrossServer&pathRedirect=payAction|prepare2|new
	    	
	   // System.out.println("LocalAddress:"+request.getLocalAddr()+","+InetAddress.getLocalHost().getHostAddress());
	    if(InetAddress.getLocalHost().getHostAddress().equalsIgnoreCase(ipDD)){
	    	//System.out.println("InetAddress.getLocalHost():"+InetAddress.getLocalHost().getHostAddress().indexOf(ipDD));
		  %>
		  if(isProd==true){
		     //var pageAction = url.substring(url.indexOf("jsp")+4,url.indexOf(".do"));
	  	     //var doAction = url.substring(url.indexOf("do=")+3,url.indexOf("&"));
	  	    // var param = url.substring(url.indexOf("&")+1,url.length);
	  	     
	  	     var pathRedirect = url.substring(url.indexOf("jsp")-1,url.length);
	  	         pathRedirect = ReplaceAll (pathRedirect , '&', '$');
	  	     //alert(pathRedirect);
	  	    
	  	     //192.168.202.8 (witty dev) or pens_bme_ddserver no redirect to (198.162.37.185:8080(product bme))
	  	     <% if("192.168.202.8".equals(currentIP) || webPath.equalsIgnoreCase("/pens_bme_ddserver")){ //On Witty%>
	  	    	newUrl = url;
	  	     <%}else{%>
	  	        newUrl = "<%=hostProd%><%=contextPathProd%>/login.do?do=loginCrossServer&pathRedirect="+pathRedirect;
		     <%}%>
	  	     url = newUrl+"&userName=<%=userName%>&password=<%=EncyptUtils.base64encode(password)%>";
	  	     //alert("isProd:"+isProd+":"+url);
		     window.location = encodeURI(url);
		 
		  }else{
			 //alert(url);
	         window.location = encodeURI(url);
		  }
		<%}else{ %>
		    //Goto HostDD
		     // /jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=_GEN_ORDER_EXCEL'
		  
		    if(isProd==false){
			    // var pageAction = url.substring(url.indexOf("jsp")+4,url.indexOf(".do"));
		  	     //var doAction = url.substring(url.indexOf("do=")+3,url.indexOf("&"));
		  	     //var param = url.substring(url.indexOf("&")+1,url.length);
		  	     
		  	      var pathRedirect = url.substring(url.indexOf("jsp")-1,url.length);
	  	              pathRedirect = ReplaceAll (pathRedirect , '&', '$');
	  	         //alert(pathRedirect);
	  	         
		  	     var newUrl = "<%=hostDD%><%=contextPathDD%>/login.do?do=loginCrossServer&pathRedirect="+pathRedirect;
		  	     
		  	     url = newUrl+"&userName=<%=userName%>&password=<%=EncyptUtils.base64encode(password)%>";
		  	     
		  	     //alert("isProd:"+isProd+":"+url);
			     window.location = encodeURI(url);
		    }else{
		    	 //alert(url);
		    	 window.location = encodeURI(url);
		    }
		<%}%>
   }
 
  function linkModelTest(isProd,url){
	//alert(url);
	window.location = encodeURI(url);	
  }
</script>
<ul id="nav">
<!-- ---------------------------------------------------------------------------------------------------------------------------------------- -->
<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE,User.PICK}) ){ no=0;%>
	<li><a  href="javascript: void(0)" class="parent"><span>Stock Onhand B'me </span></a>
		<ul>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
			    
			    <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=master&action=new');"><%no++;out.print(no);%>.<span><bean:message bundle="sysprop" key="ImportBMEMaster"/></span></a>
				</li>
				
				 <li><a class="parent"><span><%no++;out.print(no);%>.Load Text file on-hand from Wacoal</span></a>
				 <%subNo=0; %>
			       <ul>
				    	<li>
						  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=onhandLotus&action=new');"><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
						  <span><bean:message bundle="sysprop" key="ImportBMEFromWacoal"/></span></a>
					    </li>
					    <li>
					      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=onhandFriday');"><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
					      <span><bean:message bundle="sysprop" key="ImportBMEFridayFromWacoal"/></span></a>
				       </li>
				       <li>
					      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=onhandOShopping');"><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
					      <span><bean:message bundle="sysprop" key="ImportBMEOShopping"/></span></a>
				        </li>
				        <!--  despricate -->
				         <%-- <li>
					      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=onhand7Catalog');"><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
					      <span><bean:message bundle="sysprop" key="ImportBME7Catalog"/></span></a>
				        </li> --%>
				        <!-- Use table 7-catalog=pens shop -->
				        <li>
					      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=onhandPensShop');"><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
					      <span><bean:message bundle="sysprop" key="ImportBMEPensShop"/></span></a>
				        </li>
				        <li>
					      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=onhandTVDirect');"><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
					      <span><bean:message bundle="sysprop" key="ImportBMETVDirect"/></span></a>
				        </li>
			       </ul>
			   </li>
	            <li><a class="parent"><span><%no++;out.print(no);%>.Load File Sales Transaction</span></a>
			       <ul>
		               <li>
				          <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.IMPORT_XLS_SALESOUT_LOTUS%>');">
				          <span>3.1.<bean:message key="<%=BatchTaskConstants.IMPORT_XLS_SALESOUT_LOTUS%>" bundle="sysprop"/></span></a>
				       </li> 
		               
		               <!-- OLD FORMAT IMPORT -->
		                <li>
						   <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=bigc&action=new');">3.2 
						   <span><bean:message bundle="sysprop" key="ImportBMEFromBigC"/></span></a>
						</li> 
						 
						 <!-- King -->
					    <li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=king&action=new');">3.3 
							<span><bean:message bundle="sysprop" key="ImportBMEFromKing"/></span></a>
						</li> 
						
						 <li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=tops&action=new');">3.4 
							<span><bean:message bundle="sysprop" key="ImportBMEFromTops"/></span></a>
						</li>
						<!-- NEW FORMAT IMPORT BIGC 11/03/2562-->
		                  <li>
					          <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.IMPORT_SALES_OUT_BIGC_FROM_TEXT%>');">
					          3.5 <span><bean:message key="<%=BatchTaskConstants.IMPORT_SALES_OUT_BIGC_FROM_TEXT%>" bundle="sysprop"/></span></a>
					      </li> 
			       </ul>
			    </li>
	           
				<li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mttAction.do?do=prepare2&action=new');"><%no++;out.print(no);%>.
					<span><bean:message bundle="sysprop" key="mtt"/></span></a>
				</li>
				
				<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
					<li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=physical&action=new');">
						<%no++;out.print(no);%>.<span><bean:message bundle="sysprop" key="ImportBMEPhysical"/></span>
						</a>
					</li>
					<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
						<li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=return_wacoal&action=new');"><%no++;out.print(no);%>.
							<span><bean:message bundle="sysprop" key="ImportReturnWacoal"/></span></a>
						</li>
					<%} %>
				<%} %>
				<li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/masterAction.do?do=prepare&action=new&page=master');"><%no++;out.print(no);%>.
					<span><bean:message bundle="sysprop" key="MaintainMaster"/></span></a>
				</li>
				<%-- <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=ftp_file_scan_barcode');">11.<span><bean:message bundle="sysprop" key="ImportScanBarcode"/></span></a>
				</li> --%>
				
				 <li>
	          		<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_BMESCAN%>');">
	          		<span><%no++;out.print(no);%>.<bean:message key="ImportBarcodeScan" bundle="sysprop"/></span></a>
	          	</li>
         <%} %>
         
         <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
	          	 <li>
	          		<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/genCNAction.do?do=prepare');">
	          		<span><%no++;out.print(no);%>.<bean:message key="genCN" bundle="sysprop"/></span>
	          		</a>
	          	</li>
         <%} %>
          		
		   <li><a class="parent"><span><%no++;out.print(no);%>. Load ข้อมูลการตรวจนับสต๊อก</span></a>
		   <%subNo=0; %>
		       <ul>
		           <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=LoadStockInitLotus');">
				       <%out.print(no);%>.<%subNo++;out.print(subNo);%> <span>
				        <bean:message bundle="sysprop" key="LoadStockInitLotus"/></span>
				        </a>
			       </li>
			       <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=LoadStockInitBigC');">
				       <%out.print(no);%>.<%subNo++;out.print(subNo);%> 
				       <span><bean:message bundle="sysprop" key="LoadStockInitBigC"/></span>
				       </a>
			      </li>
			      <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=LoadStockInitMTT');">
				       <%out.print(no);%>.<%subNo++;out.print(subNo);%> 
				       <span><bean:message bundle="sysprop" key="LoadStockInitMTT"/></span>
				       </a>
			      </li>
		       </ul>
		  </li>
		    <li>
	          <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.IMPORT_SWITCHITEM_TO_ORL_FROM_EXCEL%>');">
	          <span><%no++;out.print(no);%>.<bean:message key="<%=BatchTaskConstants.IMPORT_SWITCHITEM_TO_ORL_FROM_EXCEL%>" bundle="sysprop"/></span></a>
	        </li> 
		</ul>
	</li>
<%} %>

<!-- Wacoal Menu-->
<%if ( Utils.userInRole(user,new String[]{User.SALE,User.ADMIN}) ){ no=0;%>
	<li><a  href="javascript: void(0)" class="parent"><span>Wacoal </span></a>
		<ul> 
		    <li>
	           <a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_WACOAL_STOCK%>');"><span><%no++;out.print(no);%>.<bean:message key="ImportWacoalStock" bundle="sysprop"/></span></a>
	        </li>
			<li>
	           <a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN%>');"><span><%no++;out.print(no);%>.<bean:message key="ImportWacoalSaleInReturn" bundle="sysprop"/></span></a>
	        </li>
	        <li>
	           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_SALEOUT_WACOAL%>');"><span><%no++;out.print(no);%>.<bean:message key="ImportSaleOutWacoal" bundle="sysprop"/></span></a>
	        </li> 
	        <li>
			   <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=ReportStockWacoalLotus');"><span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="ReportStockWacoalLotus"/></span></a>
			</li>
			 
		</ul>
	</li>
<%} %>

<!-- Shop Menu-->
<% if ( Utils.userInRole(user,new String[]{User.SALE,User.ADMIN,User.PICK}) ){ no=0;%>
    <li><a  href="javascript: void(0)" class="parent"><span>SHOP</span></a>
		<ul> 
		    <li><a class="parent"><span><%no++;out.print(no);%>.MAYA</span></a>
			   <%subNo=0; %>
			     <ul>
				    <li>
			           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_MAYA_SALEOUT%>');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%><bean:message key="MayaSaleOut" bundle="sysprop"/></span></a>
			        </li>
					 <li>
			           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_MAYA_STOCK_ONHAND%>');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message key="MayaStockOnhand" bundle="sysprop"/></span></a>
			        </li> 
			        <li>
			           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_SHOP_BILL_DETAIL%>');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message key="ShopBillDetail" bundle="sysprop"/></span></a>
			        </li>
	            </ul>
	         </li>
	         <li><a class="parent"><span><%no++;out.print(no);%>.TERMINAL</span></a>
				  <%subNo=0; %>
				  <ul>
				    <li>
			           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_TM_SALEOUT%>');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%><bean:message key="TMSaleOut" bundle="sysprop"/></span></a>
			        </li>
					 <li>
			           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_TM_STOCK_ONHAND%>');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message key="TMStockOnhand" bundle="sysprop"/></span></a>
			        </li> 
		          </ul>
	         </li>
	           <li><a class="parent"><span><%no++;out.print(no);%>.I'm CHINA</span></a>
				  <%subNo=0; %>
				  <ul>
				    <li>
			           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_CH_SALEOUT%>');">
			           <span><%out.print(no);%>.<%subNo++;out.print(subNo);%><bean:message key="CHINASaleOut" bundle="sysprop"/></span></a>
			        </li>
					 <li>
			           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_CH_STOCK_ONHAND%>');">
			           <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message key="CHINAStockOnhand" bundle="sysprop"/></span></a>
			        </li> 
		          </ul>
	         </li> 
	         <li>
	           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_SHOP_PROM%>');"><span><%no++;out.print(no);%>.<bean:message key="ShopPromotion" bundle="sysprop"/></span></a>
	        </li> 
		</ul>
	</li>   
<%} %>

<!-- -------------------------------------------------------------------Report ------------------------------------------------------------------------------------------------------- -->
 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK,User.SALE}) ){ no=0;%>
    <li><a  href="javascript: void(0)"  class="parent"><span>Report</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhand');"><span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="SummaryBMEFromWacoal"/></span></a>
			</li>
			
			<li><a class="parent"><span><%no++;out.print(no);%>.รายงาน Lotus</span></a>
			   <%subNo=0; %>
			    <ul>
			         <!-- OLD -->
				     <%-- <li>
					  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandLotus');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="SummaryBMEOnhandLotus"/></span></a>
					</li>  --%> 
					<!-- NEW -->
					<li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reportAllAction.do?do=prepare&action=new&pageName=reportOnhandLotus');">
							<span><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
							<bean:message bundle="sysprop" key="reportOnhandLotus"/></span>
						</a>
					</li>   
					 <li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=lotus');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%>  <bean:message bundle="sysprop" key="SummaryBMEFromLotus"/></span></a>
					</li>
					 <li>
				      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=sizeColorLotus');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="SummaryBMESizeColorLotus"/></span></a>
			       </li>  
					<li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandLotusPeriod');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="SummaryBMEOnhandLotusPeriod"/></span></a>
					</li> 
					<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
					  	<%-- <li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=monthEndLotus');"><span>2.5 <bean:message bundle="sysprop" key="SummaryBMEMonthEndLotus"/></span></a>
						</li>  --%>
						
						<li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reportAllAction.do?do=prepare&action=new&pageName=reportEndDateLotus');">
							<span><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
							<bean:message bundle="sysprop" key="reportEndDateLotus"/></span>
							</a>
						</li>  
					<%}else{ %>
						<li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=reportEndDateLotus');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="reportEndDateLotus"/></span></a>
						</li>
					<%} %>
			    </ul>
			</li>
			 <li><a class="parent" ><span><%no++;out.print(no);%>.รายงาน Tops</span></a>
			 <%subNo=0; %>
			    <ul>
			        <li>
					  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandTops');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%>  <bean:message bundle="sysprop" key="SummaryBMEOnhandTops"/></span></a>
					</li> 
					<li>
				      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=tops');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="SummaryBMEFromTops"/></span></a>
			      </li>
			   </ul>	    
		    </li> 
	    	 <li><a class="parent" ><span><%no++;out.print(no);%>.รายงาน BigC</span></a>
	    	 <%subNo=0; %>
			    <ul>
			      <li>
				    <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandBigC');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%>  <bean:message bundle="sysprop" key="SummaryBMEOnhandBigC"/></span></a>
			        </li> 
					<li>
				     <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=BigC');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%>  <bean:message bundle="sysprop" key="SummaryBMEFromBigC"/></span></a>
			       </li>
			       <li>
				     <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=BigC_TEMP');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%>  <bean:message bundle="sysprop" key="SummaryBMEFromBigCTemp"/></span></a>
			       </li>
			        <li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reportAllAction.do?do=prepare&action=new&pageName=reportSizeColorBigC');">
						  <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
						  <bean:message bundle="sysprop" key="SummaryBMESizeColorBigC"/></span>
						</a>
					</li> 
					
			        <li> 
				      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandBigCSP');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%>  <bean:message bundle="sysprop" key="SummaryBMEOnhandBigCSP"/></span></a>
			        </li> 
			         <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandBigCOracle');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%>  <bean:message bundle="sysprop" key="SummaryBMEOnhandBigCAtOracle"/></span></a>
			        </li>
			   </ul>	    
		    </li> 
		
			<li><a class="parent" ><span><%no++;out.print(no);%>.รายงาน Dutyfree</span></a>
	    	 <%subNo=0; %>
			    <ul>
			         <li>
				      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandAsOfKing');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%>  <bean:message bundle="sysprop" key="SummaryBMEOnhandKing"/></span></a>
			        </li>  
					<li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=king');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="SummaryBMEFromKing"/></span></a>
			        </li>
			          <li>
				      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=sizeColorKing');"><span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="SummaryBMESizeColorKing"/></span></a>
			        </li>  
			   </ul>	    
		    </li> 

			<%-- <li>
				<a href="#" onclick="window.location='<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=physical';"><span><bean:message bundle="sysprop" key="SummaryBMEFromPhysical"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=diff_stock';"><span><bean:message bundle="sysprop" key="SummaryBMEDiffStock"/></span></a>
			</li> --%>
		
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reportAction.do?do=prepare&action=new');"><span>TEMP.<bean:message bundle="sysprop" key="Report"/></span></a>
			</li> 
			 <li><a class="parent" ><span><%no++;out.print(no);%>.รายงาน Robinson</span></a>
			 <%subNo=0; %>
			    <ul>
			       <li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reportAllAction.do?do=prepare&action=new&pageName=reportOnhandAsOfRobinson');">
						  <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> 
						  <bean:message bundle="sysprop" key="onhandAsOf_Robinson"/></span>
						</a>
					</li> 
					
			       <li>
				     <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=openBillRobinsonReport');">
				     <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="openBillRobinsonReport"/></span></a>
			       </li>
			   </ul>	    
		    </li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mttAction.do?do=prepareReport&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="mttReport"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandMTT');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="SummaryBMEOnhandMTT"/></span></a>
			</li>  
			
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandMTTDetail');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="SummaryBMEOnhandMTTDetail"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/sumGroupCodeAction.do?do=prepare&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="SummaryBMEByGroupCode"/></span></a>
			</li>  
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mttAction.do?do=prepareScanReport&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="SummaryBMEScanReport"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepareReportOrder&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reportOrderBME"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=bmeTrans');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="SummaryBMETransaction"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/pickReportAction.do?do=prepareReport&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="PickReport"/></span></a>
			</li>
		</ul>
	</li>
<%} %>
<!-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- -->
 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE,User.MERC}) ){ no=0;%>
	 <li><a  href="javascript: void(0)" class="parent"><span>Order</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepare&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="Order"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderFridayAction.do?do=prepare&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="OrderFriday"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAllAction.do?do=prepare&action=new&pageName=OShopping');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="OrderOShopping"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAllAction.do?do=prepare&action=new&pageName=7Catalog');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="Order7Catalog"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAllAction.do?do=prepare&action=new&pageName=TVDIRECT');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="OrderTVDirect"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepareView&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="OrderInquiry"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepareHistory&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="OrderInquiryHistory"/></span></a>
			</li>
			<li>
	          <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.IMPORT_ORDER_FROM_EXCEL%>');">
	          <span><%no++;out.print(no);%>.<bean:message key="<%=BatchTaskConstants.IMPORT_ORDER_FROM_EXCEL%>" bundle="sysprop"/></span></a>
	        </li>
	        <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MERC}) ){ %>
		         <li>
		           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/importAllAction.do?do=prepare&action=new&pageName=ImportMasterOrderREP');">
		           <span><%no++;out.print(no);%>.<bean:message key="ImportMasterOrderREP" bundle="sysprop"/></span></a>
		         </li> 
		     
		         <li>
		          <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/autoOrderAction.do?do=prepareSearch&action=new');">
		          <span><%no++;out.print(no);%>.<bean:message key="AutoOrder" bundle="sysprop"/></span></a>
		        </li>  
		    <%} %>
		</ul>
	</li>
<%} %>

 
<!-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- -->
 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HISHER}) ){ no=0;%>
	 <li><a  href="javascript: void(0)" class="parent" ><span>Interfaces</span></a>
		<ul>
		     <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_GEN_ITEM_MASTER_HISHER%>');">
	          	<span><%no++;out.print(no);%>.<bean:message key="GenerateItemMasterHisHer" bundle="sysprop"/></span></a>
	         </li>
			 <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_GEN_HISHER%>');">
	          	<span><%no++;out.print(no);%>.<bean:message key="InterfaceHisHer" bundle="sysprop"/></span></a>
	         </li>
	         <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_GEN_ORDER_EXCEL%>');">
	          	<span><%no++;out.print(no);%>.<bean:message key="GenOrderExcel" bundle="sysprop"/></span></a>
	         </li>
	          <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_BILL_ICC%>');">
	          	<span><%no++;out.print(no);%>.<bean:message key="ImportBillICC" bundle="sysprop"/></span></a>
	         </li>
	         <li>
	          	<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/saveInvoiceAction.do?do=prepare2&action=new');">
	          	<span><%no++;out.print(no);%>.<bean:message key="SaveInvoice" bundle="sysprop"/></span></a>
	         </li>
	           <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_EXPORT_BILL_ICC%>');">
	          	<span><%no++;out.print(no);%>.<bean:message key="ExportBillICC" bundle="sysprop"/></span></a>
	         </li>
	           
		</ul>
	</li>
<%} %>
 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK,User.PICKADMIN,User.SALE}) ){ no=0;%>
	 <li><a  href="javascript: void(0)" class="parent"><span>Transaction</span></a>
		<ul>
			
			<li><a class="parent" ><span><%no++;out.print(no);%>.B'me Inventory Adjustment</span></a>
			 <%subNo=0; %>
			    <ul>
			      <li>
					 <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAllAction.do?do=prepare&action=new&pageName=ImportFileSwitchItemAdjustStock');">
					 <span><%subNo++;out.print(no+"."+subNo);%>.<bean:message bundle="sysprop" key="ImportFileSwitchItemAdjustStock"/></span></a>
				   </li>   
		           <li>
				     <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/adjustStockAction.do?do=prepare2&action=new');">
				     <span><%subNo++;out.print(no+"."+subNo);%>.<bean:message bundle="sysprop" key="adjustStock"/></span></a>
			       </li>
			   </ul>	    
		    </li> 
		    
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/adjustStockSAAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="adjustStockSA"/></span></a>
			</li>
			 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN}) ){%>
				<li>
				   <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=reconcile');">
				   <%no++;out.print(no);%>.<span><bean:message bundle="sysprop" key="reconcile"/></span></a>
			   </li>
		   <%} %>
		    <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
		    <li>
	           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_POS%>');">
	           <span><%no++;out.print(no);%>.<bean:message key="ImportPos" bundle="sysprop"/></span></a>
	        </li>
	         <li>
	            <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.IMPORT_ORDER_AUTOSUB_FROM_WACOAL%>');">
	            <span><%no++;out.print(no);%>.<bean:message key="<%=BatchTaskConstants.IMPORT_ORDER_AUTOSUB_FROM_WACOAL%>" bundle="sysprop"/>
	            </span></a>
	        </li>
	       
	       <%} %>
	        <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN}) ){%>
		       <li>
		          <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/autoCNAction.do?do=prepare2&action=new');">
		          <span><%no++;out.print(no);%>.<bean:message key="AutoCN" bundle="sysprop"/>
		          </span></a>
		        </li>
		    <%} %>
		     <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN,User.HISHER}) ){%>
		         <li>
		           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/autoCNHISHERAction.do?do=prepare2&action=new');">
		             <span><%no++;out.print(no);%>.<bean:message key="AutoCNHISHER" bundle="sysprop"/>
		          </span></a>
		        </li>
	       <%} %>
	         <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
	             <li>
		          <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/autoSubInAction.do?do=prepare2&action=new');">
		          <span><%no++;out.print(no);%>.<bean:message key="AutoSubIn" bundle="sysprop"/>
		          </span></a>
		        </li>
		    <%} %>
		    <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){ %>
	              <li>
	                <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.IMPORT_ORDER_TO_ORACLE_FROM_EXCEL%>');">
	                 <span><%no++;out.print(no);%>.<bean:message key="<%=BatchTaskConstants.IMPORT_ORDER_TO_ORACLE_FROM_EXCEL%>" bundle="sysprop"/></span>
	                </a>
	             </li> 
		    <%} %>
		     <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN,User.SALE}) ){%>
	             <li>
		          <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/autoSubB2BAction.do?do=prepare2&action=new');">
		          <span><%no++;out.print(no);%>.<bean:message key="AutoSubB2B" bundle="sysprop"/>
		          </span></a>
		        </li> 
		    <%} %>
		    <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){ %>
	          <%--    <li>
		           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/manualStockAction.do?do=prepareSearch&action=new');">
		           <span><%no++;out.print(no);%>.<bean:message key="ManualStock" bundle="sysprop"/>
		           </span></a>
		        </li>  --%>
		    <%} %>
		     <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN,User.SALE}) ){%>
	             <li>
		           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/autoCNHHTempAction.do?do=prepare2&action=new');">
		             <span><%no++;out.print(no);%>.<bean:message key="AutoCNHHTemp" bundle="sysprop"/>
		             </span>
		           </a>
		        </li> 
		     <%} %>
		     <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
		         <li>
	               <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/importAllAction.do?do=prepare&action=new&pageName=ImportExcelPICG899ToG07');">
	                <span><%no++;out.print(no);%>.<bean:message key="ImportExcelPICG899ToG07" bundle="sysprop"/></span></a>
	            </li> 
		    <%} %>
		</ul>
	</li>
<%} %>

 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE,User.PICK}) ){ no=0;%>
	<li><a  href="javascript: void(0)" class="parent" ><span>Pick</span></a>
		<ul>
	     <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/jobAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>. <bean:message bundle="sysprop" key="job"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/barcodeAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>. <bean:message bundle="sysprop" key="scanBarcode"/></span></a>
			</li>
			 <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';">
			 <span><%no++;out.print(no);%>.Transfer Stock </span></a>
			 <%subNo=0; %>
			       <ul>
						<li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/moveWarehouseAction.do?do=prepare&action=new');">
							<span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="moveWarehouse"/></span></a>
						</li>
						 <li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/moveStockWarehouseAction.do?do=prepareSearch&action=new');">
							<span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="moveStockWarehouse"/></span></a>
						</li>  
			      </ul>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqFinishAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reqFinish"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/confFinishAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="confFinish"/></span></a>
			</li> 
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/stockQueryAction.do?do=prepare&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="stockQuery"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/stockFinishGoodQueryAction.do?do=prepare&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="stockFinishGoodQuery"/></span></a>
			</li>
			 <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span><%no++;out.print(no);%>.Request เบิกสินค้าจาก PD</span></a>
			    <%subNo=0; %>
			    <ul>
			        <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W2');">
				       <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="reqPickStockW2"/></span></a>
			       </li>
			        <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W3');">
				       <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="reqPickStockW3"/></span></a>
			       </li> 
			        <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W4');">
				       <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="reqPickStockW4"/></span></a>
			        </li>
			         <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W5');">
				       <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="reqPickStockW5"/></span></a>
			        </li>
			        <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W6');">
				       <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="reqPickStockW6"/></span></a>
			        </li>
			         <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W7');">
				       <span><%out.print(no);%>.<%subNo++;out.print(subNo);%> <bean:message bundle="sysprop" key="reqPickStockW7"/></span></a>
			        </li> 
			    </ul>
			 </li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/scanCheckAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="scanCheck"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/confPickStockAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="confPickStock"/></span></a>
			</li> 
			 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN}) ){%>
				 <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/confPickStockAllAction.do?do=prepare2&action=new');">
					<span><%out.print(no);%>.1 <bean:message bundle="sysprop" key="confPickStockAll"/></span></a>
				</li> 
			<%} %>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqReturnAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reqReturnWacoal"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/confirmReturnAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="confirmReturnWacoal"/></span></a>
			</li>
			
			<li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span><%no++;out.print(no);%>.Pick Report</span></a>
			    <%subNo=0; %>
			    <ul>
			        <li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/checkStockReportAction.do?do=prepare&pageName=summary&warehouse=W2&action=new');">
						 <span><%out.print(no);%>.<%subNo++;out.print(subNo);%>.<bean:message bundle="sysprop" key="CheckStockReportW2Summary"/></span></a>
					</li>
					 <li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/checkStockReportAction.do?do=prepare&pageName=detail&warehouse=W2&action=new');">
						 <span><%out.print(no);%>.<%subNo++;out.print(subNo);%>.<bean:message bundle="sysprop" key="CheckStockReportW2Detail"/></span></a>
					</li> 
			    </ul>
			 </li>
			 <!-- **** Despricate *** -->
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/pickStockAction.do?do=prepare2&action=new');">
				<span><u><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="pickStock"/>(temp)</u></span></a>
			</li> 
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/pickStockGroupAction.do?do=prepare2&action=new&page=req');">
				<span><u><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="pickStockGroup"/>(temp)</u></span></a>
			</li> 
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/pickStockGroupAction.do?do=prepare2&action=new&page=complete');">
				<span><u><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="pickStockGroupComplete"/>(temp)</u></span></a>
			</li>
			 <!-- **** Despricate *** -->

		</ul>
	<%}else if ( Utils.userInRole(user,new String[]{User.SALE}) ){ no=0;%>
	        <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/stockQueryAction.do?do=prepare&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="stockQuery"/></span></a>
			</li>
		    <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/stockFinishGoodQueryAction.do?do=prepare&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="stockFinishGoodQuery"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W2');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reqPickStockW2"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W3');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reqPickStockW3"/></span></a>
			 </li> 
			<%--  <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/pickStockGroupAction.do?do=prepare2&action=new&page=req');">
				<span>4.<bean:message bundle="sysprop" key="pickStockGroup"/></span></a>
			</li>  --%>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W4');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reqPickStockW4"/></span></a>
			</li>
			  <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W5');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reqPickStockW5"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W6');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reqPickStockW6"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W7');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reqPickStockW7"/></span></a>
			</li>
	    </ul>  
	<%} %>
	</li>
  <%} %>
 			
 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MC,User.MT_SALE,User.HR,User.MCQUERY}) ){ no=0;%>
  	<li><a  href="javascript: void(0)" class="parent" ><span>MC/PC/SA</span></a>
		<ul>
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HR}) ){%>
			    <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mcEmpAction.do?do=prepare2&action=new');">
					<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="mcEmp"/></span></a>
				</li>
			<%} %> 
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MT_SALE,User.MC}) ){%>
				<li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mcAction.do?do=prepare2&action=new');">
					<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="mc"/></span></a>
				</li>   
			<%} %>
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MC}) ){%>
				<li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mcAction.do?do=prepareMCStaff&action=new');">
					  <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="mcStaff"/></span>
					</a>
				</li>   
			<%} %>
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MC,User.MCQUERY}) ){%>
				<li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mcTimeAction.do?do=prepareSearch&action=new');">
					  <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="mcTime"/></span>
					</a>
				</li>   
			<%} %>
			
		</ul>
	</li> 
<%} %>

 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM,User.SALE}) ){ no=0;%>
  	<li><a  href="javascript: void(0)" class="parent"><span>งาน SA</span></a>
		<ul>
		    <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/saEmpAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="saEmp"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/saDamageAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="saDamage"/></span></a>
			</li> 
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/saTranAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="saTran"/></span></a>
			</li>  
			<li><a href="#" class="parent" onclick=";"><span><%no++;out.print(no);%>.รายงานที่เกี่ยวข้อง</span></a>
			<%subNo=0; %>
		       <ul>
			    	<li>
					  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/saReportAction.do?do=prepare&page=saStatementReport&action=new');">
					  <%out.print(no);%>.<%subNo++;out.print(subNo);%> <span><bean:message bundle="sysprop" key="saStatementReport"/></span></a>
				    </li>
				     <li>
				      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/saReportAction.do?do=prepare&page=saOrisoftReport&action=new');">
				      <%out.print(no);%>.<%subNo++;out.print(subNo);%>  <span><bean:message bundle="sysprop" key="saOrisoftReport"/></span></a>
			       </li>
			       <li>
				      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/saReportAction.do?do=prepare&page=saDeptReport&action=new');">
				      <%out.print(no);%>.<%subNo++;out.print(subNo);%>  <span><bean:message bundle="sysprop" key="saDeptReport"/></span></a>
			       </li>
			         <li>
				      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/saReportAction.do?do=prepare&page=saDamageReport&action=new');">
				      <%out.print(no);%>.<%subNo++;out.print(subNo);%>  <span><bean:message bundle="sysprop" key="saDamageReport"/></span></a>
			       </li>
		      </ul>
		     </li>
		     <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/saDamageAction.do?do=prepareNoDamageSearch&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="saNoDamage"/></span></a>
			</li>  
		</ul>
	</li> 
<%} %>

<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.REDDOC,User.REDEDIT,User.IT}) ){ no=0;%>
  	<li><a  href="javascript: void(0)" class="parent"><span>Document Menu</span></a>
		<ul>
			<li>
			  <a href="#" onclick="javascript:link(false,'<%=contextPathProd%>/jsp/payAction.do?do=prepare2&action=new');">
			  <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="pay"/></span></a>
			</li> 
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.WHITEDOC}) ){%>
				<li>
				  <a href="#" onclick="javascript:link(false,'<%=contextPathProd%>/jsp/payWhiteAction.do?do=prepare2&action=new');">
				  <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="payWhite"/></span></a>
				</li>
			<%} %>
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.YELLOWDOC}) ){%>
				 <li>
				  <a href="#" onclick="javascript:link(false,'<%=contextPathProd%>/jsp/payYellowAction.do?do=prepare2&action=new');">
				  <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="PayYellow"/></span></a>
				</li> 
			<%} %>
			<%if ( Utils.userInRole(user,new String[]{User.IT,User.ADMIN}) ){%>
				<li>
				  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=filePosBME');">
				  <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="filePosBME"/></span></a>
				</li> 
				<li>
				  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/genVanScriptAction.do?do=prepare&action=new');">
				  <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="genVanScript"/></span></a>
				</li> 
				 <li>
				  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/itManageAction.do?do=prepareHead&action=new&pageName=ITStock');">
				  <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="ITStock"/></span></a>
				</li> 
				<li>
				  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/generalAction.do?do=prepare&action=new&pageName=GenBarcode');">
				  <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="GenBarcode"/></span></a>
				</li> 
			<%} %>
		</ul>
	</li> 
<%} %>

<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK,User.SALE,User.NISSINTEAM,User.PENSTEAM,User.NISSINVIEW}) ){ no=0;%>
  	<li><a  href="javascript: void(0)" class="parent"><span>Other</span></a>
		<ul>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/rtAction.do?do=prepare2&action=new&page=sale');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="rt"/></span></a>
			</li>   
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/rtAction.do?do=prepare2&action=new&page=pic');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="picRT"/></span></a>
			</li>   
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.NISSINTEAM,User.NISSINVIEW}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/nsAction.do?do=prepare2&action=new&page=nissin');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="ns"/></span></a>
			</li>   
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PENSTEAM,User.NISSINVIEW}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/nsAction.do?do=prepare2&action=new&page=pens');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="nsPens"/></span></a>
			</li>   
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK,User.SALE}) ){%>
		    <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/toolManageAction.do?do=prepareSearch&action=new&pageName=ToolManageOut');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="ToolManageOut"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/toolManageAction.do?do=prepareSearch&action=new&pageName=ToolManageIn');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="ToolManageIn"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/toolManageAction.do?do=prepareSearch&action=new&pageName=ToolManageReport');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="ToolManageReport"/></span></a>
			</li>   
		<%} %>
		</ul>
	</li> 
<%} %>

<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HISHER,User.SALE}) ){ no=0;%>
  	<li><a href="javascript: void(0)" class="parent"><span>Master Data</span></a>
		<ul>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HISHER}) ){ %>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/priceListMasterAction.do?do=prepare&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="PriceListMaster"/></span></a>
			</li>   
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){ %>
			  <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/lockItemOrderAction.do?do=prepare2&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="lockItemOrder"/></span></a>
			</li>  
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
			  <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/styleMappingLotusMasterAction.do?do=prepare&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="StyleMappingLotusMaster"/></span></a>
			</li>  
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
		     <li>
	            <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.IMPORT_BARCODE_MASTER_FROM_EXCEL%>');">
	            <span><%no++;out.print(no);%>.<bean:message key="<%=BatchTaskConstants.IMPORT_BARCODE_MASTER_FROM_EXCEL%>" bundle="sysprop"/></span></a>
	        </li> 
	     <%} %>
	     <%if ("canapos".equalsIgnoreCase(user.getUserName())){%>
		     <li>
	            <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/jojoAction.do?do=prepare&pageAction=new');">
	            <span><%no++;out.print(no);%>.Jojo Report</span></a>
	        </li> 
	     <%} %>
	     <%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
	         <li>
	           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.IMPORT_ITEMBARCODE_CROSSREF_TO_ORL_FR_EXCEL%>');">
	           <span><%no++;out.print(no);%>.<bean:message key="<%=BatchTaskConstants.IMPORT_ITEMBARCODE_CROSSREF_TO_ORL_FR_EXCEL%>" bundle="sysprop"/></span></a>
	        </li>
	      <%} %>
	       <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
	         <li>
	           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.IMPORT_ITEM_GP_ROBIN_FROM_EXCEL%>');">
	           <span><%no++;out.print(no);%>.<bean:message key="<%=BatchTaskConstants.IMPORT_ITEM_GP_ROBIN_FROM_EXCEL%>" bundle="sysprop"/></span></a>
	        </li>
	      <%} %>
	     
		</ul>
	</li> 
<%} %>

<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
  	<%--   <li><a  href="javascript: void(0)" class="parent"><span>Scheduler</span></a>
		<ul>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
			   <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/schedule.do?do=prepare&action=new');"><span>1.<bean:message bundle="sysprop" key="scheduler"/></span></a>
			</li>  
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/searchTask.do?do=prepare&action=new');"><span>2.<bean:message bundle="sysprop" key="searchTask"/></span></a>
			</li>
		<%} %>
		</ul>
	</li>   --%>
<%} %>

</ul>
   