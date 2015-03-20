<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="org.hibernate.cfg.Configuration"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%User user = (User)session.getAttribute("user");
  System.out.println("Role:"+user.getRole().getKey());
  
  String userName =user.getUserName();
  String password=user.getPassword();
  
  String webPath = request.getContextPath();
  String contextPath = request.getContextPath();
  
  //Check Test or production
 /*  Configuration hibernateConfig = new Configuration();
  hibernateConfig.configure();
  String url = hibernateConfig.getProperty("connection.url");
  if("jdbc:oracle:thin:@//192.168.37.186:1529/TEST".equals(url)){
	  //webPath ="pens_bme";
	  //contextPath = webPath;
  }else{
	 // contextPath = webPath;
  } */
 // System.out.println("contextPath:"+contextPath);
  
%>
<script>
//a href="#" onclick="window.location='http://dd-server:8081/<%=contextPath%>/login.do?do=loginCrossServer&pathRedirect=payAction|prepare2|new";
 
 function linkToDD(url){
	  url += "&userName=<%=userName%>&password=<%=password%>";
	  window.location = url;
  }
 
 //
  function link(url){
	  <% 
	    if(request.getLocalAddr().equals("192.168.202.244")){
	    	// Link to Isec Server  DDServer To IsecServer
	    	//input  : /jsp/mcAction.do?do=prepareMCStaff&action=new
	    	//output : login.do?do=loginCrossServer&pathRedirect=payAction|prepare2|new
	  %>
	     var pageAction = url.substring(url.indexOf("jsp")+4,url.indexOf(".do"));
  	     var doAction = url.substring(url.indexOf("do=")+3,url.indexOf("&"));
  	     var action = url.substring(url.indexOf("action")+7,url.length);
  	     var newUrl = "http://192.168.37.185:8080/<%=contextPath%>/login.do?do=loginCrossServer&pathRedirect="+pageAction+"|"+doAction+"|"+action;
	     
  	     url = newUrl+"&userName=<%=userName%>&password=<%=password%>";
	     window.location = url;
	 
	  <%}else{//Link normal
       %>
	     //alert(url);
         window.location = url;
	  <% } %>
  }
  

</script>

<ul id="nav">
<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
	<li><a href="#" class="parent" onclick="window.location='<%=contextPath%>/jsp/mainpage.jsp';"><span>Stock Onhand B'me </span></a>
		<ul>
		    <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/importAction.do?do=prepare&page=master&action=new');">1.<span><bean:message bundle="sysprop" key="ImportBMEMaster"/></span></a>
			</li>
           	<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/importAction.do?do=prepare&page=onhand&action=new');">2.<span><bean:message bundle="sysprop" key="ImportBMEFromWacoal"/></span></a>
			</li>
		    <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/importAction.do?do=prepare&page=lotus&action=new');">3.<span><bean:message bundle="sysprop" key="ImportBMEFromLotus"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/importAction.do?do=prepare&page=bigc&action=new');">4.<span><bean:message bundle="sysprop" key="ImportBMEFromBigC"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/importAction.do?do=prepare&page=tops&action=new');">5.<span><bean:message bundle="sysprop" key="ImportBMEFromTops"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/mttAction.do?do=prepare2&action=new');">6.<span><bean:message bundle="sysprop" key="mtt"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/importAction.do?do=prepare&page=physical&action=new');">7.<span><bean:message bundle="sysprop" key="ImportBMEPhysical"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/importAction.do?do=prepare&page=return_wacoal&action=new');">8.<span><bean:message bundle="sysprop" key="ImportReturnWacoal"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/masterAction.do?do=prepare&action=new&page=master');">9.<span><bean:message bundle="sysprop" key="MaintainMaster"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/importAction.do?do=prepare&action=new&page=onhandFriday');">10.<span><bean:message bundle="sysprop" key="ImportBMEFridayFromWacoal"/></span></a>
			</li>
		</ul>
	</li>
<%} %>

 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK,User.SALE}) ){%>
    <li><a href="#" class="parent" onclick="window.location='<%=contextPath%>/jsp/mainpage.jsp';"><span>Report</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=onhand');"><span>1.<bean:message bundle="sysprop" key="SummaryBMEFromWacoal"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandLotus');"><span>2.<bean:message bundle="sysprop" key="SummaryBMEOnhandLotus"/></span></a>
			</li>  
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=lotus');"><span>3.<bean:message bundle="sysprop" key="SummaryBMEFromLotus"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=BigC');"><span>4.<bean:message bundle="sysprop" key="SummaryBMEFromBigC"/></span></a>
			</li>
					 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=tops');"><span>5.<bean:message bundle="sysprop" key="SummaryBMEFromTops"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandBigC');"><span>6.<bean:message bundle="sysprop" key="SummaryBMEOnhandBigC"/></span></a>
			</li>  
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandLotusPeriod');"><span>7.<bean:message bundle="sysprop" key="SummaryBMEOnhandLotusPeriod"/></span></a>
			</li> 
			<%-- <li>
				<a href="#" onclick="window.location='<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=physical';"><span><bean:message bundle="sysprop" key="SummaryBMEFromPhysical"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=diff_stock';"><span><bean:message bundle="sysprop" key="SummaryBMEDiffStock"/></span></a>
			</li> --%>
			<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
				<li>
					<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/reportAction.do?do=prepare&action=new');"><span>8.<bean:message bundle="sysprop" key="Report"/></span></a>
				</li>
			<%} %>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/mttAction.do?do=prepareReport&action=new');"><span>9.<bean:message bundle="sysprop" key="mttReport"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/summaryAction.do?do=prepare&action=new&page=onhandMTT');"><span>10.<bean:message bundle="sysprop" key="SummaryBMEOnhandMTT"/></span></a>
			</li>  
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/sumGroupCodeAction.do?do=prepare&action=new');"><span>11.<bean:message bundle="sysprop" key="SummaryBMEByGroupCode"/></span></a>
			</li>  
		</ul>
	</li>
<%} %>

 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
 
	 <li><a href="#" class="parent" onclick="window.location='<%=contextPath%>/jsp/mainpage.jsp';"><span>Order</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/orderAction.do?do=prepare&action=new');"><span><bean:message bundle="sysprop" key="Order"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/orderAction.do?do=prepareView&action=new');"><span><bean:message bundle="sysprop" key="OrderInquiry"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/orderAction.do?do=prepareHistory&action=new');"><span><bean:message bundle="sysprop" key="OrderInquiryHistory"/></span></a>
			</li>
		</ul>
	</li>
<%} %>

 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
	 <li><a href="#" class="parent" onclick="window.location='<%=contextPath%>/jsp/mainpage.jsp';"><span>Transaction</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/adjustStockAction.do?do=prepare2&action=new');"><span>1.<bean:message bundle="sysprop" key="adjustStock"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/adjustStockSAAction.do?do=prepare2&action=new');"><span>2.<bean:message bundle="sysprop" key="adjustStockSA"/></span></a>
			</li>
		</ul>
	</li>
<%} %>

 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
	<li><a href="#" class="parent" onclick="window.location='<%=contextPath%>/jsp/mainpage.jsp';"><span>Pick</span></a>
		<ul>
	     <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICK}) ){%>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/jobAction.do?do=prepare2&action=new');"><span>1.<bean:message bundle="sysprop" key="job"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/barcodeAction.do?do=prepare2&action=new');"><span>2.<bean:message bundle="sysprop" key="scanBarcode"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/moveWarehouseAction.do?do=prepare&action=new');"><span>3.<bean:message bundle="sysprop" key="moveWarehouse"/></span></a>
			</li>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/reqFinishAction.do?do=prepare2&action=new');"><span>4.<bean:message bundle="sysprop" key="reqFinish"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/confFinishAction.do?do=prepare2&action=new');"><span>5.<bean:message bundle="sysprop" key="confFinish"/></span></a>
			</li> 
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/stockQueryAction.do?do=prepare&action=new');"><span>6.<bean:message bundle="sysprop" key="stockQuery"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/stockFinishGoodQueryAction.do?do=prepare&action=new');"><span>7.<bean:message bundle="sysprop" key="stockFinishGoodQuery"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/reqPickStockAction.do?do=prepare2&action=new');"><span>8.<bean:message bundle="sysprop" key="reqPickStock"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/confPickStockAction.do?do=prepare2&action=new');"><span>9.<bean:message bundle="sysprop" key="confPickStock"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/reqReturnWacoalAction.do?do=prepare2&action=new');"><span>10.<bean:message bundle="sysprop" key="reqReturnWacoal"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/confirmReturnWacoalAction.do?do=prepare2&action=new');"><span>11.<bean:message bundle="sysprop" key="confirmReturnWacoal"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/pickStockAction.do?do=prepare2&action=new');"><span>12.<bean:message bundle="sysprop" key="pickStock"/></span></a>
			</li> 
		</ul>
	<%}else if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
	    <ul>
		    <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/stockFinishGoodQueryAction.do?do=prepare&action=new');"><span>1.<bean:message bundle="sysprop" key="stockFinishGoodQuery"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/reqPickStockAction.do?do=prepare2&action=new');"><span>2.<bean:message bundle="sysprop" key="reqPickStock"/></span></a>
			</li> 
	    </ul>  
	<%} %>
	</li>
  <%} %>
 			
 <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MC,User.MT_SALE}) ){%>
  	<li><a href="#" class="parent" onclick="window.location='<%=contextPath%>/jsp/mainpage.jsp';"><span>MC/PC</span></a>
		<ul>
			<li>
				<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/mcAction.do?do=prepare2&action=new');"><span>1.<bean:message bundle="sysprop" key="mc"/></span></a>
			</li>   
				<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.MC}) ){%>
				<li>
					<a href="#" onclick="javascript:link('<%=contextPath%>/jsp/mcAction.do?do=prepareMCStaff&action=new');">
					  <span>2.<bean:message bundle="sysprop" key="mcStaff"/></span>
					</a>
				</li>   
			<%} %>
			
		</ul>
	</li> 
<%} %>

<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.REDDOC}) ){%>
	
  	<li><a href="#" class="parent" onclick="window.location='<%=contextPath%>/jsp/mainpage.jsp';"><span>Document Menu</span></a>
		<ul>
			<li>
			    <%
			    System.out.println("LocalhostAddre:"+request.getLocalAddr());
			    System.out.println("LocalName:"+request.getLocalName());
			    if(request.getLocalAddr().equals("192.168.202.244")){
			    %>
			       <a href="#" onclick="window.location='<%=webPath%>/jsp/payAction.do?do=prepare2&action=new'"><span>1.<bean:message bundle="sysprop" key="pay"/></span></a>
			   <%
			    }else if(request.getLocalName().equals("0.0.0.0")){
			    %>
			       <a href="#" onclick="window.location='<%=webPath%>/jsp/payAction.do?do=prepare2&action=new'"><span>1.<bean:message bundle="sysprop" key="pay"/></span></a>
			 
			   <%}else{ 
				 //For test at localhost
			    if(request.getLocalAddr().equals("0.0.0.0")){
			    	contextPath ="/pens_bme_test";
			    }
			   %>
			   
			       <a href="#" onclick="javascrupt:linkToDD('http://dd-server:8081<%=contextPath%>/login.do?do=loginCrossServer&pathRedirect=payAction|prepare2|new');">
			         <span>1.<bean:message bundle="sysprop" key="pay"/></span>
			       </a>
  
			    <%}%>
			</li> 
		</ul>
	</li> 
<%} %>
</ul>
   