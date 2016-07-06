<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@page import="java.net.InetAddress"%>
<%@page import="com.isecinc.pens.inf.helper.Constants"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<%
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
  System.out.println("Current IP:"+currentIP);

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
  
 // System.out.println("contextPathProd:"+contextPathProd);
 // System.out.println("contextPathDD:"+contextPathDD);

%>
<script>

  function link(isProd,url){
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
	  	    
	  	     <% if("192.168.202.8".equals(currentIP)){ //On Witty%>
	  	    	newUrl = url;
	  	     <%}else{%>
	  	        newUrl = "<%=hostProd%><%=contextPathProd%>/login.do?do=loginCrossServer&pathRedirect="+pathRedirect;
		     <%}%>
	  	     url = newUrl+"&userName=<%=userName%>&password=<%=password%>";
	  	    // alert("isProd:"+isProd+":"+url);
		     window.location = url;
		 
		  }else{
			  //alert(url);
	         window.location = url;
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
		  	     
		  	     url = newUrl+"&userName=<%=userName%>&password=<%=password%>";
		  	     
		  	    // alert("isProd:"+isProd+":"+url);
			     window.location = url;
		    }else{
		    	 window.location = url;
		    }
		<%}%>
		
  }
</script>

<ul id="nav">
<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE,User.PICK}) ){%>
	<li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>Stock Onhand B'me </span></a>
		<ul>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
			    
			    <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=master&action=new');">1.<span><bean:message bundle="sysprop" key="ImportBMEMaster"/></span></a>
				</li>
				
				 <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>2.Load Text file on-hand from Wacoal</span></a>
			       <ul>
				    	<li>
						  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=onhand&action=new');">2.1 <span><bean:message bundle="sysprop" key="ImportBMEFromWacoal"/></span></a>
					    </li>
					    <li>
					      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=onhandFriday');">2.2 <span><bean:message bundle="sysprop" key="ImportBMEFridayFromWacoal"/></span></a>
				       </li>
				       <li>
					      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=onhandOShopping');">2.3 <span><bean:message bundle="sysprop" key="ImportBMEOShopping"/></span></a>
				        </li>
				        <li>
					      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=onhand7Catalog');">2.4 <span><bean:message bundle="sysprop" key="ImportBME7Catalog"/></span></a>
				        </li>
			       </ul>
			   </li>
	           
			     <%--   OLD
			     <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=lotus&action=new');">3.<span><bean:message bundle="sysprop" key="ImportBMEFromLotus"/></span></a>
				</li> --%>
				<!--  NEW CODE --> 
				<li>
	          	  <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_TRANSACTION_LOTUS%>');"><span>1.<bean:message key="ImportBMEFromLotus" bundle="sysprop"/></span></a>
	            </li>
	         
				 <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=bigc&action=new');">4.<span><bean:message bundle="sysprop" key="ImportBMEFromBigC"/></span></a>
				</li>
				<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
					 <li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=king&action=new');">5.<span><bean:message bundle="sysprop" key="ImportBMEFromKing"/></span></a>
					</li>
				<%} %>
				 <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mttAction.do?do=prepare2&action=new');">6.<span><bean:message bundle="sysprop" key="mtt"/></span></a>
				</li>
				<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
					<li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=physical&action=new');">7.<span><bean:message bundle="sysprop" key="ImportBMEPhysical"/></span></a>
					</li>
					<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
						<li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=return_wacoal&action=new');">8.<span><bean:message bundle="sysprop" key="ImportReturnWacoal"/></span></a>
						</li>
					<%} %>
				<%} %>
				<li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/masterAction.do?do=prepare&action=new&page=master');">9.<span><bean:message bundle="sysprop" key="MaintainMaster"/></span></a>
				</li>
				
				
				<%-- <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=ftp_file_scan_barcode');">11.<span><bean:message bundle="sysprop" key="ImportScanBarcode"/></span></a>
				</li> --%>
				
				 <li>
	          		<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_BMESCAN%>');"><span>10.<bean:message key="ImportBarcodeScan" bundle="sysprop"/></span></a>
	          	</li>
          	<%} %>
          	<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
	          	 <li>
	          		<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/genCNAction.do?do=prepare');"><span>11.<bean:message key="genCN" bundle="sysprop"/></span></a>
	          	</li>
          	<%} %>
          		
			   <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>12. Load ข้อมูลการตรวจนับสต๊อก</span></a>
			       <ul>
			           <li>
					       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=LoadStockInitLotus');">12.1 <span><bean:message bundle="sysprop" key="LoadStockInitLotus"/></span></a>
				       </li>
				       <li>
					       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=LoadStockInitBigC');">12.2 <span><bean:message bundle="sysprop" key="LoadStockInitBigC"/></span></a>
				      </li>
				      <li>
					       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=LoadStockInitMTT');">12.3 <span><bean:message bundle="sysprop" key="LoadStockInitMTT"/></span></a>
				      </li>
			       </ul>
			  </li>
		</ul>
	</li>
<%} %>

 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK,User.SALE}) ){%>
    <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>Report</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhand');"><span>1.<bean:message bundle="sysprop" key="SummaryBMEFromWacoal"/></span></a>
			</li>
			
			<li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>2.รายงาน Lotus</span></a>
			    <ul>
				    <li>
					  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandLotus');"><span>2.1 <bean:message bundle="sysprop" key="SummaryBMEOnhandLotus"/></span></a>
					</li>  
					 <li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=lotus');"><span>2.2 <bean:message bundle="sysprop" key="SummaryBMEFromLotus"/></span></a>
					</li>
					 <li>
				      <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=sizeColorLotus');"><span>2.3 <bean:message bundle="sysprop" key="SummaryBMESizeColorLotus"/></span></a>
			       </li>  
					<li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandLotusPeriod');"><span>2.4 <bean:message bundle="sysprop" key="SummaryBMEOnhandLotusPeriod"/></span></a>
					</li> 
					<li>
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=monthEndLotus');"><span>2.5 <bean:message bundle="sysprop" key="SummaryBMEMonthEndLotus"/></span></a>
					</li>
			    </ul>
			</li>
			    
	    	 <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>4.รายงาน BigC</span></a>
			    <ul>
					<li>
				     <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=BigC');"><span>4.1 <bean:message bundle="sysprop" key="SummaryBMEFromBigC"/></span></a>
			       </li>
			       <li>
				    <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandBigC');"><span>4.2 <bean:message bundle="sysprop" key="SummaryBMEOnhandBigC"/></span></a>
			        </li> 
			          <li>
				    <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=sizeColorBigC');"><span>4.3 <bean:message bundle="sysprop" key="SummaryBMESizeColorBigC"/></span></a>
			        </li> 
			  
			   </ul>	    
		    </li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=king');"><span>5.<bean:message bundle="sysprop" key="SummaryBMEFromKing"/></span></a>
			</li>

			<%-- <li>
				<a href="#" onclick="window.location='<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=physical';"><span><bean:message bundle="sysprop" key="SummaryBMEFromPhysical"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=diff_stock';"><span><bean:message bundle="sysprop" key="SummaryBMEDiffStock"/></span></a>
			</li> --%>
		
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reportAction.do?do=prepare&action=new');"><span>6.<bean:message bundle="sysprop" key="Report"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mttAction.do?do=prepareReport&action=new');"><span>7.<bean:message bundle="sysprop" key="mttReport"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandMTT');"><span>8.<bean:message bundle="sysprop" key="SummaryBMEOnhandMTT"/></span></a>
			</li>  
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandMTTDetail');"><span>9.<bean:message bundle="sysprop" key="SummaryBMEOnhandMTTDetail"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/sumGroupCodeAction.do?do=prepare&action=new');"><span>10.<bean:message bundle="sysprop" key="SummaryBMEByGroupCode"/></span></a>
			</li>  
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mttAction.do?do=prepareScanReport&action=new');"><span>11.<bean:message bundle="sysprop" key="SummaryBMEScanReport"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepareReportOrder&action=new');"><span>12.<bean:message bundle="sysprop" key="reportOrderBME"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=bmeTrans');"><span>13.<bean:message bundle="sysprop" key="SummaryBMETransaction"/></span></a>
			</li>
			
		</ul>
	</li>
<%} %>


 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
 
	 <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>Order</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepare&action=new');"><span><bean:message bundle="sysprop" key="Order"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderFridayAction.do?do=prepare&action=new');"><span><bean:message bundle="sysprop" key="OrderFriday"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAllAction.do?do=prepare&action=new&pageName=OShopping');"><span><bean:message bundle="sysprop" key="OrderOShopping"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAllAction.do?do=prepare&action=new&pageName=7Catalog');"><span><bean:message bundle="sysprop" key="Order7Catalog"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepareView&action=new');"><span><bean:message bundle="sysprop" key="OrderInquiry"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepareHistory&action=new');"><span><bean:message bundle="sysprop" key="OrderInquiryHistory"/></span></a>
			</li>
			
		</ul>
	</li>
<%} %>
 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HISHER}) ){%>
	 <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>Interfaces</span></a>
		<ul>
		     <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_GEN_ITEM_MASTER_HISHER%>');"><span>1.<bean:message key="GenerateItemMasterHisHer" bundle="sysprop"/></span></a>
	         </li>
			 <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_GEN_HISHER%>');"><span>2.<bean:message key="InterfaceHisHer" bundle="sysprop"/></span></a>
	         </li>
	         <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_GEN_ORDER_EXCEL%>');"><span>3.<bean:message key="GenOrderExcel" bundle="sysprop"/></span></a>
	         </li>
	          <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_BILL_ICC%>');"><span>4.<bean:message key="ImportBillICC" bundle="sysprop"/></span></a>
	         </li>
	         <li>
	          	<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/saveInvoiceAction.do?do=prepare2&action=new');"><span>5.<bean:message key="SaveInvoice" bundle="sysprop"/></span></a>
	         </li>
	           <li>
	          	<a href="#" onclick="javascript:link(false,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_EXPORT_BILL_ICC%>');"><span>6.<bean:message key="ExportBillICC" bundle="sysprop"/></span></a>
	         </li>
	           
		</ul>
	</li>
<%} %>
 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
	 <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>Transaction</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/adjustStockAction.do?do=prepare2&action=new');"><span>1.<bean:message bundle="sysprop" key="adjustStock"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/adjustStockSAAction.do?do=prepare2&action=new');"><span>2.<bean:message bundle="sysprop" key="adjustStockSA"/></span></a>
			</li>
		</ul>
	</li>
<%} %>

 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE,User.PICK}) ){%>
	<li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>Pick</span></a>
		<ul>
	     <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/jobAction.do?do=prepare2&action=new');"><span>1. <bean:message bundle="sysprop" key="job"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/barcodeAction.do?do=prepare2&action=new');"><span>2. <bean:message bundle="sysprop" key="scanBarcode"/></span></a>
			</li>
			 <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>3.Transfer Stock </span></a>
			       <ul>
						<li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/moveWarehouseAction.do?do=prepare&action=new');"><span>3.1 <bean:message bundle="sysprop" key="moveWarehouse"/></span></a>
						</li>
						 <li>
							<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/moveStockWarehouseAction.do?do=prepare&action=new');"><span>3.2 <bean:message bundle="sysprop" key="moveStockWarehouse"/></span></a>
						</li>  
			      </ul>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqFinishAction.do?do=prepare2&action=new');"><span>4.<bean:message bundle="sysprop" key="reqFinish"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/confFinishAction.do?do=prepare2&action=new');"><span>5.<bean:message bundle="sysprop" key="confFinish"/></span></a>
			</li> 
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/stockQueryAction.do?do=prepare&action=new');"><span>6.<bean:message bundle="sysprop" key="stockQuery"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/stockFinishGoodQueryAction.do?do=prepare&action=new');"><span>7.<bean:message bundle="sysprop" key="stockFinishGoodQuery"/></span></a>
			</li>
			

			 <li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>8.Request เบิกสินค้าจาก PD</span></a>
			    <ul>
			        <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W2');"><span>8.1 <bean:message bundle="sysprop" key="reqPickStockW2"/></span></a>
			       </li>
			        <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W3');"><span>8.2 <bean:message bundle="sysprop" key="reqPickStockW3"/></span></a>
			       </li> 
			        <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W4');"><span>8.3 <bean:message bundle="sysprop" key="reqPickStockW4"/></span></a>
			        </li>
			         <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W5');"><span>8.4 <bean:message bundle="sysprop" key="reqPickStockW5"/></span></a>
			        </li>
			    </ul>
			 </li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/scanCheckAction.do?do=prepare2&action=new');"><span>9.<bean:message bundle="sysprop" key="scanCheck"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/confPickStockAction.do?do=prepare2&action=new');"><span>10.<bean:message bundle="sysprop" key="confPickStock"/></span></a>
			</li> 
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqReturnAction.do?do=prepare2&action=new');"><span>11.<bean:message bundle="sysprop" key="reqReturnWacoal"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/confirmReturnAction.do?do=prepare2&action=new');"><span>12.<bean:message bundle="sysprop" key="confirmReturnWacoal"/></span></a>
			</li>
			
			 <!-- Deprecate -->
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/pickStockAction.do?do=prepare2&action=new');"><span><u>_12.<bean:message bundle="sysprop" key="pickStock"/></u></span></a>
			</li> 
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/pickStockGroupAction.do?do=prepare2&action=new&page=req');"><span><u>13.<bean:message bundle="sysprop" key="pickStockGroup"/></u></span></a>
			</li> 
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/pickStockGroupAction.do?do=prepare2&action=new&page=complete');"><span><u>14.<bean:message bundle="sysprop" key="pickStockGroupComplete"/></u></span></a>
			</li>
			
		</ul>
	<%}else if ( Utils.userInRole(user,new String[]{User.SALE}) ){%>
	        <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/stockQueryAction.do?do=prepare&action=new');"><span>1.<bean:message bundle="sysprop" key="stockQuery"/></span></a>
			</li>
		    <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/stockFinishGoodQueryAction.do?do=prepare&action=new');"><span>2.<bean:message bundle="sysprop" key="stockFinishGoodQuery"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W2');"><span>3.<bean:message bundle="sysprop" key="reqPickStockW2"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W3');"><span>4.<bean:message bundle="sysprop" key="reqPickStockW3"/></span></a>
			 </li> 
			<%--  <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/pickStockGroupAction.do?do=prepare2&action=new&page=req');"><span>4.<bean:message bundle="sysprop" key="pickStockGroup"/></span></a>
			</li>  --%>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/reqPickStockAction.do?do=prepare2&action=new&wareHouse=W4');"><span>5.<bean:message bundle="sysprop" key="reqPickStockW4"/></span></a>
			</li>
	    </ul>  
	<%} %>
	</li>
  <%} %>
 			
 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MC,User.MT_SALE,User.HR,User.MCQUERY}) ){%>
  	<li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>MC/PC/SA</span></a>
		<ul>
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HR}) ){%>
			    <li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mcEmpAction.do?do=prepare2&action=new');"><span>1.<bean:message bundle="sysprop" key="mcEmp"/></span></a>
				</li>
			<%} %> 
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MT_SALE,User.MC}) ){%>
				<li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mcAction.do?do=prepare2&action=new');"><span>2.<bean:message bundle="sysprop" key="mc"/></span></a>
				</li>   
			<%} %>
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MC}) ){%>
				<li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mcAction.do?do=prepareMCStaff&action=new');">
					  <span>3.<bean:message bundle="sysprop" key="mcStaff"/></span>
					</a>
				</li>   
			<%} %>
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MC,User.MCQUERY}) ){%>
				<li>
					<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mcTimeAction.do?do=prepareSearch&action=new');">
					  <span>4.<bean:message bundle="sysprop" key="mcTime"/></span>
					</a>
				</li>   
			<%} %>
			
		</ul>
	</li> 
<%} %>

<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.REDDOC,User.REDEDIT}) ){%>
	
  	<li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>Document Menu</span></a>
		<ul>
			<li>
			  <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/payAction.do?do=prepare2&action=new');"><span>1.<bean:message bundle="sysprop" key="pay"/></span></a>
			  
			</li> 
		</ul>
	</li> 
<%} %>
<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK,User.SALE,User.NISSINTEAM,User.PENSTEAM}) ){%>
  	<li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>Other</span></a>
		<ul>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/rtAction.do?do=prepare2&action=new&page=sale');"><span>1.<bean:message bundle="sysprop" key="rt"/></span></a>
			</li>   
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/rtAction.do?do=prepare2&action=new&page=pic');"><span>1.<bean:message bundle="sysprop" key="picRT"/></span></a>
			</li>   
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.NISSINTEAM}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/nsAction.do?do=prepare2&action=new&page=nissin');"><span>1.<bean:message bundle="sysprop" key="ns"/></span></a>
			</li>   
		<%} %>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PENSTEAM}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/nsAction.do?do=prepare2&action=new&page=pens');"><span>1.<bean:message bundle="sysprop" key="nsPens"/></span></a>
			</li>   
		<%} %>
		</ul>
	</li> 
<%} %>

<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HISHER}) ){%>
  	<li><a href="#" class="parent" onclick="window.location='<%=contextPathProd%>/jsp/mainpage.jsp';"><span>Master Data</span></a>
		<ul>
		<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HISHER}) ){%>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/priceListMasterAction.do?do=prepare&action=new');"><span>1.<bean:message bundle="sysprop" key="PriceListMaster"/></span></a>
			</li>   
		<%} %>
		</ul>
	</li> 
<%} %>

</ul>
   