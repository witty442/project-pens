<%@page import="com.isecinc.pens.web.shop.ShopAction"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@page import="java.net.InetAddress"%>
<%@page import="com.isecinc.pens.inf.helper.Constants"%>
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

<!-- Stock Onhand menu -->
<%if ( Utils.userInRole(user,new String[]{User.WACOAL}) ){ no=0;%>
	<li><a  href="javascript: void(0)" class="parent"><span>Stock Onhand B'me </span></a>
		<ul>
		     <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mttAction.do?do=prepare2&action=new');"><%no++;out.print(no);%>.<span><bean:message bundle="sysprop" key="mtt"/></span></a>
			</li>
		</ul>
	</li>
<%} %>

<!-- Shop Menu-->
<% if ( Utils.userInRole(user,new String[]{User.WACOAL}) ){ no=0;%>
    <li><a  href="javascript: void(0)" class="parent"><span>MAYA</span></a>
		<ul> 
		    <li>
	           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_MAYA_SALEOUT%>');"><span><%no++;out.print(no);%>.<bean:message key="MayaSaleOut" bundle="sysprop"/></span></a>
	        </li>
			 <li>
	           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_MAYA_STOCK_ONHAND%>');"><span><%no++;out.print(no);%>.<bean:message key="MayaStockOnhand" bundle="sysprop"/></span></a>
	        </li> 
	         <li>
	           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_SHOP_PROM%>');"><span><%no++;out.print(no);%>.<bean:message key="ShopPromotion" bundle="sysprop"/></span></a>
	        </li>  
	        <li>
	           <a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=ShopAction.P_SHOP_BILL_DETAIL%>');"><span><%no++;out.print(no);%>.<bean:message key="ShopBillDetail" bundle="sysprop"/></span></a>
	        </li>
		</ul>
	</li>   
<%} %>

<!-- Report Menu -->
<%if ( Utils.userInRole(user,new String[]{User.WACOAL}) ){ no=0;%>
    <li><a  href="javascript: void(0)" class="parent"><span>Report</span></a>
		<ul>
		    <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=onhand');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="SummaryBMEFromWacoal"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/mttAction.do?do=prepareReport&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="mttReport"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=lotus');">
				<span><%no++;out.print(no);%>. <bean:message bundle="sysprop" key="SummaryBMEFromLotus"/></span></a>
			 </li>
			 <li>
				   <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=BigC');">
				   <span><%no++;out.print(no);%>. <bean:message bundle="sysprop" key="SummaryBMEFromBigC"/></span></a>
			 </li>
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=king');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="SummaryBMEFromKing"/></span></a>
			</li>
			 <li>
				 <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=sizeColorLotus');">
				 <span><%no++;out.print(no);%>. <bean:message bundle="sysprop" key="SummaryBMESizeColorLotus"/></span></a>
			 </li>  
			 <li>
				 <a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/summaryAction.do?do=prepare&action=new&page=sizeColorBigC');">
				 <span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="SummaryBMESizeColorBigC"/></span></a>
			 </li> 
			 <li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepareReportOrder&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="reportOrderBME"/></span></a>
			</li>
		</ul>
	</li>
<%} %>

<!-- Order Inquery Menu -->
<%if ( Utils.userInRole(user,new String[]{User.WACOAL}) ){ no=0;%>
	 <li><a  href="javascript: void(0)" class="parent" ><span>Order</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepareView&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="OrderInquiry"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link(true,'<%=contextPathProd%>/jsp/orderAction.do?do=prepareHistory&action=new');">
				<span><%no++;out.print(no);%>.<bean:message bundle="sysprop" key="OrderInquiryHistory"/></span></a>
			</li>
		</ul>
	</li>
<%} %>


</ul>
   