<%@page import="com.pens.util.EnvProperties"%>
<%@page import="com.isecinc.pens.web.stock.StockConstants"%>
<%@page import="com.pens.util.EncyptUtils"%>
<%@page import="java.net.InetAddress"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%
String role = "VAN";
User user = (User)session.getAttribute("user");
/** No of menu **/
int no = 0;
int subNo = 0;
	
  EnvProperties env = EnvProperties.getInstance();

 // System.out.println("Role:"+user.getRole().getKey());
  
  String userName =user.getUserName();
  String password=user.getPassword();
  
  String contextPathServerCross = "/pensclient";
  String ipServerCross = "localhost";
  String hostServerCross = "http://"+ipServerCross+":8080";
  

  String currentIP =InetAddress.getLocalHost().getHostAddress();
  //System.out.println("Current IP:"+currentIP);

  //case Server Test contextPath = pens_sa_test
	if("192.168.202.8".equals(currentIP)){ //On Witty dev
		contextPathServerCross ="/pens";
	     hostServerCross = "http://localhost:8080";
	 }else{
		 if("production".equalsIgnoreCase(env.getProperty("productType"))){
			 contextPathServerCross ="/pensclient"; //ON PRODUCTION SERVER
			 hostServerCross = "http://"+ipServerCross+":8080";
		 }else{
			 contextPathServerCross ="/pensclient"; //ON UAT SERVER at DD_SERVER
		     hostServerCross = "http://localhost:8080";
		 }
	 }
  
  System.out.println("hostServerCross:"+hostServerCross);
  System.out.println("currentIP:"+currentIP);
%>
<script>
function link(isCrossServer,url){
	linkModel(isCrossServer,url);
 }
 
 <!--Production -->
 function linkModel(isCrossServer,url){
	  var newUrl ;
		  //Goto HostProd
		  // /jsp/interfacesAction.do?do=prepare&pageAction=new&pageName=_GEN_ORDER_EXCEL'
		//alert("isCrossServer:"+isCrossServer);
	    if(isCrossServer==false){
		    // var pageAction = url.substring(url.indexOf("jsp")+4,url.indexOf(".do"));
	  	     //var doAction = url.substring(url.indexOf("do=")+3,url.indexOf("&"));
	  	     //var param = url.substring(url.indexOf("&")+1,url.length);
	  	     
	  	      var pathRedirect = url.substring(url.indexOf("jsp")-1,url.length);
  	              pathRedirect = ReplaceAll (pathRedirect , '&', '$');
  	         //alert(pathRedirect);
  	         
	  	     var newUrl = "<%=hostServerCross%><%=contextPathServerCross%>/login.do?do=loginCrossServer&pathRedirect="+pathRedirect;
	  	     
	  	     url = newUrl+"&userName=<%=userName%>&password=<%=EncyptUtils.base64encode(password)%>";
	  	     
	  	    // alert("isCrossServer:"+isCrossServer+":"+url);
		     window.location = encodeURI(url);
	    }else{
	    	 //alert("isCrossServer:"+isCrossServer+":"+url);
	    	 window.location = encodeURI(url);
	    }
   }

</script>

<ul id="nav">
    <li><a  href="javascript: void(0)" class="parent">รายงานใบปิดรอบ Van Sales/PD</a><%no=0; %>
	     <ul>
            <li>
            	<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/stockAction.do?do=prepareSearch&pageName=<%=StockConstants.PAGE_STOCK_CLOSE_VAN %>&action=new&popup=false');"><span><%no++;out.print(no);%>.รายงานใบปิดรอบ Van Sales</span></a>
            </li>
            <li>
            	<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/stockAction.do?do=prepareSearch&pageName=<%=StockConstants.PAGE_STOCK_CLOSEPD_VAN %>&action=new&popup=false');"><span><%no++;out.print(no);%>.รายงานใบปิดรอบ PD</span></a>
            </li>
	     </ul>
     </li>
      <li><a  href="javascript:javascript:link(true,'${pageContext.request.contextPath}/jsp/boxNoAction.do?do=prepare&action=new');"><span>ใบปะหน้ากล่อง ของเสีย(Nissin)<%no=0; %></span>
      </li>
</ul>

  