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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
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
	  	    
	  	     <% if("192.168.202.8".equals(currentIP)){ //On Witty%>
	  	    	newUrl = url;
	  	     <%}else{%>
	  	        newUrl = "<%=hostProd%><%=contextPathProd%>/login.do?do=loginCrossServer&pathRedirect="+pathRedirect;
		     <%}%>
	  	     url = newUrl+"&userName=<%=userName%>&password=<%=EncyptUtils.base64encode(password)%>";
	  	    // alert("isProd:"+isProd+":"+url);
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
		  	     
		  	    // alert("isProd:"+isProd+":"+url);
			     window.location = encodeURI(url);
		    }else{
		    	 window.location = encodeURI(url);
		    }
		<%}%>
   }
 
  function linkModelTest(isProd,url){
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
		          	       <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=<%=Constants.TYPE_IMPORT_TRANSACTION_LOTUS%>');">
		          	       <span>3.1 <bean:message key="ImportBMEFromLotus" bundle="sysprop"/></span></a>
		               </li>
		               <!-- OLD FORMAT IMPORT -->
		                <li>
						   <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=bigc&action=new');">3.2 
						   <span><bean:message bundle="sysprop" key="ImportBMEFromBigC"/></span></a>
						</li> 
						 
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
						<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&page=physical&action=new');"><%no++;out.print(no);%>.<span><bean:message bundle="sysprop" key="ImportBMEPhysical"/></span></a>
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
	          		<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/genCNAction.do?do=prepare');"><span><%no++;out.print(no);%>.<bean:message key="genCN" bundle="sysprop"/></span></a>
	          	</li>
         <%} %>
          		
		   <li><a class="parent"><span><%no++;out.print(no);%>. Load ข้อมูลการตรวจนับสต๊อก</span></a>
		   <%subNo=0; %>
		       <ul>
		           <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=LoadStockInitLotus');"><%out.print(no);%>.<%subNo++;out.print(subNo);%> <span><bean:message bundle="sysprop" key="LoadStockInitLotus"/></span></a>
			       </li>
			       <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=LoadStockInitBigC');"><%out.print(no);%>.<%subNo++;out.print(subNo);%> <span><bean:message bundle="sysprop" key="LoadStockInitBigC"/></span></a>
			      </li>
			      <li>
				       <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/importAction.do?do=prepare&action=new&page=LoadStockInitMTT');"><%out.print(no);%>.<%subNo++;out.print(subNo);%> <span><bean:message bundle="sysprop" key="LoadStockInitMTT"/></span></a>
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


</ul>
   