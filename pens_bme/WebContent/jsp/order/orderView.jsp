<%@page import="com.isecinc.pens.web.order.OrderAction"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
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


<jsp:useBean id="orderForm" class="com.isecinc.pens.web.order.OrderForm" scope="session" />

<%
ImportDAO importDAO = new ImportDAO();
if(session.getAttribute("storeTypeList") == null){
   List<References> storeTypeList = importDAO.getStoreTypeList();
   session.setAttribute("storeTypeList",storeTypeList);
}

if(session.getAttribute("billTypeList") == null){
  List<References> billTypeList = importDAO.getBillTypeList();
  session.setAttribute("billTypeList",billTypeList);
}


if(session.getAttribute("regionList") == null){
   List<References> regionList = importDAO.getRegionList();
   session.setAttribute("regionList",regionList);
}

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
	 new Epoch('epoch_popup', 'th', document.getElementById('orderDate'));
	 <%if(request.getAttribute("Message") != null){ %>
		 alert("<%=Utils.isNull(request.getAttribute("Message"))%>");
	 <%} %>
}

function search(path){
	var form = document.orderForm;
	var orderDate =$('#orderDate').val();
	if(orderDate ==""){
		alert("กรุณากรอกวันที่ Order");
		return false;
	}
	form.action = path + "/jsp/orderAction.do?do=searchView&action=newsearch";
	form.submit();
	return true;
}


function gotoPage(path,pageNumber){
	var form = document.orderForm;
	
	//if(confirm("ข้อมูลในหน้านี้ จะถูกบันทึก กรุณากดปุ่มเพื่อยืนยันการบันทึก และไปหน้าถัดไป ")){
		var orderDate =$('#orderDate').val();
		if(orderDate ==""){
			alert("กรุณากรอกวันที่ Order");
			return false;
		}
		
		form.action = path + "/jsp/orderAction.do?do=searchView&pageNumber="+pageNumber;
		form.submit();
		return true;
	//}
	//return false;
}

function exportSummaryAllToExcel(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=exportSummaryAllToExcel";
	form.submit();
	return true;
}
function exportToTextAll(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=exportToTextAllView";
	form.submit();
	return true;
}

function exportToExcel(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function exportSummaryToExcel(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=exportSummaryToExcelView";
	form.submit();
	return true;
}

function exportDetailToExcel(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=exportDetailToExcelView";
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=prepareView&action=new";
	form.submit();
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
				<jsp:param name="function" value="OrderInquiry"/>
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
						<html:form action="/jsp/orderAction">
						<jsp:include page="../error.jsp"/>
						<% if(session.getAttribute("results") != null){ %> 
						    <div align="left">
						     <table align="left" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
									<td >
									     ห้าง &nbsp;&nbsp; 
									     <html:select property="order.storeType">
											<html:options collection="storeTypeList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
									<td>
									     ประเภทร้านค้า&nbsp;&nbsp; 
									     <html:select property="order.billType">
											<html:options collection="billTypeList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
								</tr>
								<tr>
									<td >
									     ภาค &nbsp;&nbsp; 
									     <html:select property="order.region">
											<html:options collection="regionList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
									<td> วันที่ Order &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									  <html:text property="order.orderDate" styleId="orderDate" size="20" />
									  </td>
								</tr>
								<tr>
									<td>
									    Group &nbsp;&nbsp; 
									    <html:text property="order.groupCode"  size="20" />
									</td>
									<td>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									  
									  </td>
								</tr>
						   </table>
						      <br/><br/><br/><br/>
						   <table  align="left" border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>						
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
						  </table>
							
					     </div>
					     <br/>
					     <br/>
					     <br/> 
						<%}else{ %>
						   <div align="center">
						  
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
									<td >
									     ห้าง &nbsp;&nbsp; 
									     <html:select property="order.storeType">
											<html:options collection="storeTypeList" property="key" labelProperty="name"/>
									     </html:select>
									     
									</td>
									<td >
									     ประเภทร้านค้า&nbsp;&nbsp; 
									     <html:select property="order.billType">
											<html:options collection="billTypeList" property="key" labelProperty="name"/>
									     </html:select>
									     
									</td>
								</tr>
								<tr>
									<td >
									     ภาค &nbsp;&nbsp; 
									     <html:select property="order.region">
											<html:options collection="regionList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
									<td> วันที่ Order &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									  <html:text property="order.orderDate" styleId="orderDate" size="20"/>
									  </td>
								</tr>
								<tr>
									<td>
									    Group &nbsp;&nbsp; 
									    <html:text property="order.groupCode"  size="20" />
									</td>
									<td>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									  
									  </td>
								</tr>
						   </table>
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
						  
					  </div>
						<%} %>
						
					<% if(storeList != null && storeList.size() > 0 && session.getAttribute("results") != null){ %>
					<!-- BUTTON ACTION-->
					<table align="left" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
							
								<%-- <a href="javascript:exportToTextAll('${pageContext.request.contextPath}')">
								     <input type="button" value="Export To Text ทุกห้าง" class="newPosBtnLong">
								</a>	 --%>	
								<a href="javascript:exportSummaryToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export Summary To Excel" class="newPosBtnLong">
								</a>
								<a href="javascript:exportDetailToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export Detail To Excel" class="newPosBtnLong">
								</a>
									<a href="javascript:exportSummaryAllToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export สรุปยอดรวมทุกห้าง" class="newPosBtnLong">
								</a>
								<%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export To Excel" class="newPosBtnLong">
								</a> --%>						
							</td>
						</tr>
					</table>
				   <% } %>
				   
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
					                  <th width="3%">Group</th>
					                  <th width="4%">Size/ Color</th>
					                  <th width="3%">Item</th>
					                  <th width="5%">Onhand Qty</th>
					                  <th width="3%">ราคาขาย ปลีกก่อน VAT</th>
					                 
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
						Map<String,String> itemErrorMap = new HashMap<String, String>();
						if(session.getAttribute("itemErrorMap") != null){
							itemErrorMap = (Map)session.getAttribute("itemErrorMap");
						}
						
						int tabindex = 0;
						int no= start;
						String titleDisp ="";
						for(int i=0;i<orderItemList.size();i++){
						   Order o = (Order) orderItemList.get(i);
						   titleDisp = o.getItemDesc()+" Onhand("+o.getOnhandQty()+")";
						  
						   String classStyle = itemErrorMap.get(o.getItem())!=null?itemErrorMap.get(o.getItem()):"lineO";
						   
						   if(i%15==0 && i > 0){
						%>
						     <tr>
					                  <th width="2%">&nbsp;</th>
					                  <th width="3%">&nbsp;</th>
					                  <th width="4%">&nbsp;</th>
					                  <th width="3%">&nbsp;</th>
					                  <th width="5%">&nbsp;</th>
					                  <th width="3%">&nbsp;</th>
					                 
					               <%
					                if(storeList != null && storeList.size()>0){ 
					               	  for(int k=0;k<storeList.size();k++){
					                        StoreBean s = (StoreBean)storeList.get(k);
					                %>
					                         <th  width="5%"><%=s.getStoreDisp() %></th>
					                <%
					                       }//for 2
					                   }//if 
					               
					                %>
					            </tr>    
						<%} %>
						<tr class="<%=classStyle%>">
						       <td><%=no%></td>
						       <td><input type="text" name="groupCode" value="<%=o.getGroupCode()%>" readonly size="5" class="disableText"></td>
						       <td><input type="text" name="itemDesc" value="<%=o.getItemDisp()%>" readonly size="4" class="disableText"></td>
						       <td><input type="text" name="item" value="<%=o.getItem()%>" readonly size="6" class="disableText"></td>
						       <td><input type="text" name="onhandQty_<%=i%>" id="onhandQty_<%=i%>"  readonly value="<%=o.getOnhandQty()%>" size="3" class="disableText"></td>	
						       <td><input type="text" name="retailPriceBF" value="<%=o.getRetailPriceBF()%>" readonly size="5" class="disableText">
						       
						       <input type="hidden" name="wholePriceBF" value="<%=o.getWholePriceBF()%>" readonly size="5" class="disableText">
						       <input type="hidden" name="barcode" value="<%=o.getBarcode()%>" readonly size="12" class="disableText">
						       <!-- Hidden For get value -->
						       <input type="hidden" name="onhandQty"  value="<%=o.getOnhandQty()%>">
						      
						       </td>
						       <!--  For By Store -->
						        <%if(o.getStoreItemList() != null && o.getStoreItemList().size()>0){ 
						        	for(int c=0;c<o.getStoreItemList().size();c++){
						              StoreBean storeItem = (StoreBean)o.getStoreItemList().get(c);
						             
						              tabindex++;
						              //disp
						              StoreBean storeDisp = (StoreBean)storeList.get(c);
						              
						         %>
						              <td>  
						                 <input type="text"  readonly class="disableText" name="qty_<%=c%>_<%=i%>" id="qty_<%=c%>_<%=i%>" 
						                        value="<%=Utils.isNull(storeItem.getQty())%>" tabindex="<%=tabindex%>" size="3" 
						                        onkeypress="chkQtyKeypress(this,event,'<%=c%>','<%=i%>')"
						                        onchange="validateQty(this,'<%=c%>','<%=i%>')"
						                        title="<%=titleDisp %>"
						                        />
						                         <input type="hidden" name="orderNo_<%=c%>_<%=i%>"  value="<%=Utils.isNull(storeItem.getOrderNo())%>">
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

					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
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