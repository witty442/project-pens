<%@page import="java.math.BigDecimal"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%>
<%@page import="java.util.Date"%>
<%@page import="com.isecinc.pens.dao.ProdShowDAO"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="com.isecinc.pens.model.MOrder"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.inf.helper.DBConnection"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.web.prodshow.ProdShowForm"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.ProdShowBean"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
Connection conn = null;
ProdShowBean  prodShowBean = null;
User user = (User) request.getSession().getAttribute("user");

String action = Utils.isNull(request.getParameter("action"));
String orderNo = Utils.isNull(request.getParameter("orderNo"));
String customerCode = Utils.isNull(request.getParameter("customerCode"));
String fromPage = Utils.isNull(request.getParameter("fromPage"));

System.out.println("action:"+action);
System.out.println("orderNo:"+orderNo);
System.out.println("customerCode:"+customerCode);
System.out.println("fromPage:"+fromPage);
try{
	 conn  = DBConnection.getInstance().getConnection();
	 
	//Case By Credit Sales  OrderNo ->gen by running
	 prodShowBean = new ProdShowBean();
	 prodShowBean.setOrderNo("");
	 prodShowBean.setOrderId(0);
	 
	 //get Customer Detail
	 Customer cus = new MCustomer().findByWhereCond(conn, " where code ='"+customerCode+"'");
	 if(cus != null){
		 prodShowBean.setCustomerCode(cus.getCode());
		 prodShowBean.setCustomerName(cus.getName());
	 }
	 //get ProdShow Data 
	 List<ProdShowBean> prodShowList = ProdShowDAO.searchProdShowList(conn,customerCode);
		
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>PENS Sales Application</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
function loadMe(){
	//Add Row blank case edit or add 

}
function manageProdShowTT(path,customerCode,orderNo){
	var param  = "&customerCode="+customerCode;
	    param += "&orderNo="+orderNo;
	    param += "&fromPage=customerSearch";
	document.prodShowForm.action = path + "/jsp/prodshow/prodShow.jsp?action=new"+param;
	document.prodShowForm.submit();
	return true;
}

function prepare(path,type,id){
	document.prodShowForm.action = path + "/jsp/customer/customerSearch.jsp";
	document.prodShowForm.submit();
	return true;
}
</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="ProdShow"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
		            
						<!-- BODY -->
						<form name="prodShowForm" action="/jsp/prodShowServlet" enctype="multipart/form-data" method = "POST">
						<jsp:include page="../error.jsp"/>
                        <!-- head -->
                          <div align="center">
						    <div align="center">
						     <font size="2" color="blue"> </font>
						    </div>
					 	    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						      <tr>	 
						        <td align="center" colspan="4">
						         <font size="3" ><b> ข้อมูลกองโชว์</b></font>
						        </td>
							   </tr>
								<tr>
                                    <td align="right">   รหัสร้านค้า<font color="red"></font>	</td>
									<td>	    
									  <input type="text" name="customerCode" Id="customerCode" size="15" 
									  readonly Class="disableText"  value="<%=prodShowBean.getCustomerCode()%>"/>
									</td>
									<td>		 
									   <input type="text" name="customerName" Id="customerName" size="40" 
									   readonly Class="disableText"  value="<%=prodShowBean.getCustomerName()%>"/>
									</td>
								</tr>
								<tr>	 
						          <td align="center" colspan="4">&nbsp;</td>
							   </tr>
								<tr>
									<td colspan="3" align="center">	
									   <a href="#" onclick="manageProdShowTT('${pageContext.request.contextPath}','<%=customerCode%>','');">
							              <input type="button" name="add" value="เพิ่มรายการใหม่"  class="newPosBtnLong"/>
							           </a> 
							           &nbsp;&nbsp;
							           <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','<%=prodShowBean.getOrderId()%>');">
			                              <input type="button" name="backBtn" value="  ปิดหน้าจอ  "  class="newPosBtnLong"/>
			                           </a>
									</td>
								</tr>
						   </table> 
                    <!-- table data -->
                    <%	if(prodShowList != null && prodShowList.size() >0){ %>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th >รหัสร้านค้า</th> 
						            <th >ชื่อร้านค้า</th>
						            <th >Document No</th>
									<th >แก้ไข / ดู</th>
							   </tr>
							<% 
							String selected = "";
							String tabclass ="lineE";
								System.out.println("prodShowList size:"+prodShowList.size());
							    for(int rowId=0;rowId<prodShowList.size();rowId++){
								  ProdShowBean mc = (ProdShowBean)prodShowList.get(rowId);
								  if(rowId%2==0){ 
								  	tabclass="lineO";
								  }
								%> 
								<tr class="<%=tabclass%>"> 
								    <td class="td_text_center" width="10%">
								       <%=mc.getCustomerCode() %>
								    </td>
									 <td class="td_text_center" width="25%">
								       <%=mc.getCustomerName() %>
								    </td>
								     <td class="td_text_center" width="10%">
								       <%=mc.getOrderNo() %>
								    </td>
								     <td class="td_text_center" width="10%">
								       <a href="#" onclick="manageProdShowTT('${pageContext.request.contextPath}','<%=mc.getCustomerCode()%>','<%=mc.getOrderNo()%>');">
							               <b> แก้ไข/ดู</b>
							           </a>  
								    </td>
								</tr>
							<%}  %> 
					</table>
					<% } %> 
		        <!-- ************************Result ***************************************************-->
               </div>
               
					<!-- hidden field -->
					<input type="hidden" name="fromPage" value="<%=fromPage%>"/>
					</form>
					<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
   <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
<%
}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn !=null){
		conn.close();conn=null;
	}
}
%>
</html>