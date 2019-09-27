<%@page import="com.isecinc.pens.dao.JobDAO"%>
<%@page import="com.isecinc.pens.web.pick.MoveStockWarehouseAction"%>
<%@page import="com.isecinc.pens.bean.MoveStockWarehouseBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.Date"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="moveStockWarehouseForm" class="com.isecinc.pens.web.pick.MoveStockWarehouseForm" scope="session" />

<%
String dateSession = DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH);
int pageNumber = 1;
int start = 0;
int end = 0;

String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));

System.out.println("screenWidth:"+screenWidth);
System.out.println("screenHeight:"+screenHeight);

User user = (User) request.getSession().getAttribute("user");
String role = user.getRole().getKey();

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
#scroll {
    width:<%=screenWidth%>px;
    height:<%=screenHeight%>px;
    background:#A3CBE0;
	border:1px solid #000;
	overflow:auto;
	white-space:nowrap;
	box-shadow:0 0 25px #000;
	}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('dateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('dateTo'));
}

function clearForm(path){
	var form = document.moveStockWarehouseForm;
	form.action = path + "/jsp/moveStockWarehouseAction.do?do=prepareSearch&action=new";
	form.submit();
	return true;
}

function search(path){
	var form = document.moveStockWarehouseForm;
	
	form.action = path + "/jsp/moveStockWarehouseAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}

function newDoc(path){
	 var form = document.moveStockWarehouseForm;
	var param ="";
	form.action = path + "/jsp/moveStockWarehouseAction.do?do=prepare&action=new"+param;
	form.submit();
	return true; 
}

function gotoPage(path,pageNumber){
	var form = document.moveStockWarehouseForm;
	form.action = path + "/jsp/moveStockWarehouseAction.do?do=searchHead&pageNumber="+pageNumber;
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
			<jsp:param name="function" value="moveStockWarehouse"/>
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
						<html:form action="/jsp/moveStockWarehouseAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td align="right"> จากวันที่ทำรายการ&nbsp; </td>
                                    <td>
                                        <html:text property="bean.dateFrom" styleClass="" styleId="dateFrom"></html:text>
                                    </td>
									<td>&nbsp;ถึงวันที่ทำรายการ&nbsp;
										  <font color="red"></font>
                                        <html:text property="bean.dateTo" styleClass="" styleId="dateTo"></html:text>
									</td>
								</tr>
								<tr>
                                    <td align="right"> From Warehouse&nbsp; </td>
                                    <td>
                                          <html:select property="bean.warehouseFrom" styleId="warehouseFrom" >
											<html:options collection="wareHouseList" property="key" labelProperty="name"/>
									    </html:select>
                                    </td>
									<td>&nbsp;To Warehouse&nbsp;
										  <font color="red"></font>
                                         <html:select property="bean.warehouseTo" styleId="warehouseTo" >
											<html:options collection="wareHouseList" property="key" labelProperty="name"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td align="right"> รุ่น&nbsp; </td>
                                    <td>
                                        <html:text property="bean.groupCodeSearch" styleClass="" styleId="groupCodeSearch"></html:text>
                                    </td>
									<td>&nbsp;รุ่นสินค้า (สีไซร์)&nbsp;
										  <font color="red"></font>
                                        <html:text property="bean.materialMaster" styleClass="" styleId="materialMaster"></html:text>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									  
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:newDoc('${pageContext.request.contextPath}')">
										  <input type="button" value="    เพิ่มรายการใหม่      " class="newPosBtnLong"> 
										</a>
											
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${moveStockWarehouseForm.resultsSearch != null}">
                  <% 
					
						String tabclass ="lineE";
						List<MoveStockWarehouseBean> resultList = moveStockWarehouseForm.getResultsSearch();
						
						//calc Page number
						String action = Utils.isNull(request.getParameter("action"));
						int totalPage = moveStockWarehouseForm.getTotalPage();
						int totalRow = moveStockWarehouseForm.getTotalRow();
						int pageSize = MoveStockWarehouseAction.pageSize;
	                    if( !"newsearch".equalsIgnoreCase(action)){
						     pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
	                    }
	                    //System.out.println("Screen pageNumber:"+pageNumber);
	                    start = ((pageNumber-1)*pageSize)+1;
						end = (pageNumber * pageSize);
						if(end > totalRow){
						   end = totalRow;
						}
				  %>
					   <div align="left">
						   <span class="pagebanner">รายการทั้งหมด  <%=totalRow %> รายการ, แสดงรายการที่  <%=(start) %> ถึง  <%=end %>.</span>
						   <span class="pagelinks">
							หน้าที่ 
							 <% 
								 for(int r=0;r<totalPage;r++){
									 if(pageNumber ==(r+1)){
								 %>
				 				   <font size="3"><b><%=(r+1) %></b></font>
								 <%}else{ %>
								    <font size="2" ><b>
								      <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
								      </b>
								    </font>
							 <% }} %>				
							</span>
					  </div>
            
					  <table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
					       <tr>
					            <th >วันที่ทำรายการ</th>
					            <th >FROM W/H</th>
					            <th >TO W/H</th>
								<th >รุ่นสินค้า (สีไซร์)</th>
								<th >จำนวนที่โอน</th>
						   </tr>
							
					    <% 
					     int no = 1;
					     List<MoveStockWarehouseBean> resultSearchList = moveStockWarehouseForm.getResultsSearch();
						 for(int n=0; n <resultSearchList.size() ; n++){
								MoveStockWarehouseBean mc = (MoveStockWarehouseBean)resultList.get(n);
								if(n%2==0){
									tabclass="lineO";
								}
								no = n+1;
					     %>
									<tr class="<%=tabclass%>">
									    <td class="td_text_center" width="10%" ><%=mc.getCreateDate() %></td>
									    <td class="td_text_center" width="10%" ><%=mc.getWarehouseFrom() %></td>
									    <td class="td_text_center" width="10%" ><%=mc.getWarehouseTo() %></td>
									    <td class="td_text_center" width="10%" ><%=mc.getMaterialMaster()%></td>
									    <td class="td_number" width="10%" ><%=mc.getTransferQty()%></td>
									</tr>
							<%}//for %>
							
							<%if(totalPage==pageNumber){ 
								//MoveStockWarehouseBean s =  moveStockWarehouseForm.getBean().getSummary();
							%>
							<!-- <tr class='hilight_text'> -->
								<%-- 	<td class="td_text_right"  colspan="15" align="right">Total</td>
									<td class="td_text_center" width="4%"><%=s.getCupQty()%></td>
									<td class="td_text_center" width="4%"><%=s.getCupNQty()%></td>
									<td class="td_text_center" width="4%"><%=s.getPacQty()%></td>
									<td class="td_text_center" width="4%"><%=s.getPacNQty()%></td>
									<td class="td_text_center" width="4%"><%=s.getPoohQty()%></td>
									<td class="td_text_center" width="4%"><%=s.getPoohNQty()%></td>
									<td class="td_text" width="15%" nowrap></td> 
									<td class="td_text" width="5%" nowarp></td>  --%>
						<!-- 	</tr> -->
							 <%} %>
					</table>
					<!-- </div> -->
				</c:if>
				
		<!-- ************************Result ***************************************************-->	
					<!-- hidden field -->
					<input type="hidden" name="page" value="<%=request.getParameter("page") %>"/>
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