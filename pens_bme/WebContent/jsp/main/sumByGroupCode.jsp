<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.order.OrderAction"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:useBean id="sumGroupCodeForm" class="com.isecinc.pens.web.summary.groupCode.SummaryForm" scope="session" />

<%
ImportDAO importDAO = new ImportDAO();
//if(session.getAttribute("storeTypeList") == null){
   List<PopupForm> storeTypeList = GeneralDAO.searchCustGroupInSaleOutPage(new PopupForm());
   session.setAttribute("storeTypeList",storeTypeList);
//}

List<StoreBean> storeList = null;
if(session.getAttribute("storeList") != null){
	storeList = (List<StoreBean>)session.getAttribute("storeList");
}
int start = 0;
int end = 0;
int pageNumber = 1;
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}

span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 //new Epoch('epoch_popup', 'th', document.getElementById('orderDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
	    
	 <%if(request.getAttribute("Message") != null){ %>
		// alert("<%=Utils.isNull(request.getAttribute("Message"))%>");
	 <%} %>
}

function search(path){
	var form = document.sumGroupCodeForm;
	var groupCode =$('#groupCode').val();
	var storeType =document.getElementById("storeType").value;
	//alert(storeType);
	if(storeType ==""){
	    alert("กรุณากรอก กลุ่มร้านค้า");
		return false;
	}
	if(groupCode ==""){
		alert("กรุณากรอก groupCode");
		return false;
	}
	form.action = path + "/jsp/sumGroupCodeAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function gotoPage(path,pageNumber){
	var form = document.sumGroupCodeForm;
	
	//if(confirm("ข้อมูลในหน้านี้ จะถูกบันทึก กรุณากดปุ่มเพื่อยืนยันการบันทึก และไปหน้าถัดไป ")){
	var groupCode =$('#groupCode').val();
	var storeType = document.getElementById("storeType").value;
	if(storeType ==""){
	    alert("กรุณากรอก กลุ่มร้านค้า");
		return false;
	}
	
	if(groupCode ==""){
	    alert("กรุณากรอก groupCode");
		return false;
	}
	
	form.action = path + "/jsp/sumGroupCodeAction.do?do=search&pageNumber="+pageNumber;
	form.submit();
	return true;
	
}

function exportToExcel(path){
	var form = document.sumGroupCodeForm;
	form.action = path + "/jsp/sumGroupCodeAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.sumGroupCodeForm;
	form.action = path + "/jsp/sumGroupCodeAction.do?do=clear";
	form.submit();
	return true;
}

function openPopupGroup(path){
    var param = "";
	url = path + "/jsp/searchGroupPopupAction.do?do=prepare&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}
function setGroupMainValue(code,desc,types){
	var form = document.sumGroupCodeForm;
	form.groupCode.value = code;
	//form.groupDesc.value = desc;
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
				<jsp:param name="function" value="SummaryBMEByGroupCode"/>
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
						<html:form action="/jsp/sumGroupCodeAction">
						<jsp:include page="../error.jsp"/>
						
						    <div align="center">
						     <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
									<td >
									จาก วันที่ขาย&nbsp;&nbsp;&nbsp; <html:text property="order.salesDateFrom" styleId="salesDateFrom" readonly="true"/>
									ถึง วันที่ขาย&nbsp;&nbsp;&nbsp; <html:text property="order.salesDateTo" styleId="salesDateTo"/>
									</td>
								</tr>
								<tr>
									<td>กลุ่มร้านค้า <font color="red" >*</font>&nbsp;&nbsp;&nbsp;&nbsp;
									   <html:select property="order.storeType" styleId="storeType">
										  <html:options collection="storeTypeList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
									<td>GroupCode<font color="red" >*</font>&nbsp;&nbsp;&nbsp;
									    <html:text property="order.groupCode" styleId="groupCode" />
									    &nbsp;
									    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}')"/>
									     <html:hidden property="order.groupCodeDesc" styleId="groupCodeDesc" />     
									  </td>
									
								</tr>
						   </table>
						      
						   <table  align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>	
										<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
								           <input type="button" value=" Export " class="newPosBtnLong">
								        </a>					
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
						  </table>
							
					     </div>

				   
					<!-- ************************Result ***************************************************-->
                    <br/>&nbsp;
					<!-- HEAD -->
			<% if(storeList != null && storeList.size() > 0 && session.getAttribute("results") != null){  %>
					<!-- Page -->
					<% if(session.getAttribute("totalPage") != null){ 
					
					   int totalPage = ((Integer)session.getAttribute("totalPage")).intValue();
					   int totalRow = ((Integer)session.getAttribute("totalRow")).intValue();
					   int pageSize = OrderAction.pageSize;
					   if(Utils.isNull(request.getAttribute("action")).equalsIgnoreCase("newsearch")){
						  pageNumber = 1;
					   }else{
					      pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
					   }
					   
					   start = ((pageNumber-1)*pageSize)+1;
					   end = (pageNumber * pageSize);
					   if(end > totalRow){
						   end = totalRow;
					   }
					   %>
					   
					<div align="left">
					   <span class="pagebanner">รายการทั้งหมด  <%=totalRow %> รายการ, แสดงรายการที่  <%=start %> ถึง  <%=end %>.</span>
					   
					   <span class="pagelinks">
						หน้าที่ 
						 <% 
							 for(int r=0;r<totalPage;r++){
								 if(pageNumber ==(r+1)){
							 %>
			 				   <strong><%=(r+1) %></strong>
							 <%}else{ %>
							 
							    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
							    
						 <% }} %>				
						</span>
					</div>
					<%} %>
					<table align="center" border="1" cellpadding="3" cellspacing="0" class="result">
					                <tbody>
					                  <th width="2%">No</th>
					                  <th width="3%">Group Code</th>
					                  <th width="4%">Size</th>
					                  <th width="3%">Color</th>
					                 
					               <%
					                if(storeList != null && storeList.size()>0){ 
					               	  for(int k=0;k<storeList.size();k++){
					                        StoreBean s = (StoreBean)storeList.get(k);
					                %>
					                         <th width="5%"><%=s.getStoreDisp() %></th>
					                <%
					                       }//for 2
					                   }//if 
					               
					                %>
					            </tbody>    
					        
					        
					 <!-- HEAD -->
					<!-- Content -->
					<% if(session.getAttribute("results") != null){
						List<Order> orderItemList =(List<Order>) session.getAttribute("results");
						
						
						int tabindex = 0;
						int no= start;
						String titleDisp ="";
						for(int i=0;i<orderItemList.size();i++){
						   Order o = (Order) orderItemList.get(i);
						   titleDisp = o.getItemDesc()+" Onhand("+o.getOnhandQty()+")";
						  
						   String classStyle = "lineE";
		             %>
						<tr class="<%=classStyle%>">
						       <td><%=no%></td>
						       <td><span><%=o.getGroupCode()%></span></td>
						       <td><span><%=o.getSize()%></span></td>
						       <td><span><%=o.getColor()%></span></td>
						       
						       <!--  For By Store -->
						        <%if(o.getStoreItemList() != null && o.getStoreItemList().size()>0){ 
						        	for(int c=0;c<o.getStoreItemList().size();c++){
						              StoreBean storeItem = (StoreBean)o.getStoreItemList().get(c);
						             
						              tabindex++;
						              //disp
						              //StoreBean storeDisp = (StoreBean)storeList.get(c);
						              
						         %>
						              <td>  
						               <span><%=storeItem.getQty() %></span>
						             </td>
						         <%
						             }//for 2
						           }//if  
						         %>
						        <!--  For By Store -->
						     </tr>
						
						<%  
						 no++;
						   } //for 1
						%>
						</table>
						<%
						}//if 1
						
			}//if StoreList != null	
						%>
					
                    <!-- ************************Result ***************************************************-->

					<!-- hidden field -->
					<input type="hidden" name="maxColumns" id="maxColumns" value="<%=storeList!=null?storeList.size():0%>"/>
					<input type="hidden" name="pageNumber" id="pageNumber" value="<%=pageNumber%>"/>
					</html:form>
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
</html>