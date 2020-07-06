<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.isecinc.pens.web.reportall.bean.EffectiveSKUBean"%>
<%@page import="com.isecinc.pens.web.reportall.ReportAllForm"%>
<%@page import="com.isecinc.pens.web.reportall.ReportAllBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="reportAllForm" class="com.isecinc.pens.web.reportall.ReportAllForm" scope="session" />
<%
EffectiveSKUBean bean = ((ReportAllForm)session.getAttribute("reportAllForm")).getBean().getEffectiveSKUBean();
List<PopupBean> monthList = (List)session.getAttribute("MONTH_LIST");
System.out.println("bean:"+bean);
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
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));
	String pageName = Utils.isNull(request.getParameter("pageName"));
	String hideAll = "true";
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- For fix Head and Column Table -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript">

function loadMe(){
	var form = document.reportAllForm;
}

function search(path){
	var form = document.reportAllForm;
	 if( isCheckYearMonthOne()){ 
		 alert("กรุณากรอกระบุ ช่วงข้อมูลขายเดือน อย่างน้อย 1 เดือน");
		 return false;
	 }  
	form.action = path + "/jsp/reportAllAction.do?do=search";
	form.submit();
	return true;
}
function exportExcel(path){
	var form = document.reportAllForm;
	 if( isCheckYearMonthOne()){ 
		 alert("กรุณากรอกระบุ ช่วงข้อมูลขายเดือน อย่างน้อย 1 เดือน");
		 return false;
	 }  
	form.action = path + "/jsp/reportAllAction.do?do=export";
	form.submit();
	return true;
}
function clearForm(path){
	var form = document.reportAllForm;
	form.action = path + "/jsp/reportAllAction.do?do=prepare&action=new";
	form.submit();
	return true;
}
function isCheckYearMonthOne(){
	var ret = false;
	var form = document.reportAllForm;
	var yearMonthChk = form.yearMonthChk;
	for(var i=0;i<yearMonthChk.length;i++){
		//alert(yearMonthChk[i].checked);
		if(yearMonthChk[i].checked){
			break;
			//alert(i+":"+yearMonthChk[i].checked);
			ret= true;
		}
	}
	return ret;
}
function openPopup(path,pageName){
	var form = document.reportAllForm;
	var param = "";
	if("CustomerMaster" == pageName){
		param = "&pageName="+pageName+"&hideAll=true&selectone=true";
	}else if("Brand" == pageName){
		param = "&pageName="+pageName+"&hideAll=true&selectone=false";
	}

	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.reportAllForm;
	if("CustomerMaster" == pageName){
		form.customerCode.value = code;
		//form.storeName.value = desc;
	}else if("Brand" == pageName){
		form.brand.value = code;
		//form.brandName.value = desc;
	}
} 

function loadSalesrepCodeList(){
	 var cboDistrict = document.getElementsByName('bean.effectiveSKUBean.salesrepCode')[0];
	//var param  ="salesChannelNo=" + document.getElementsByName('bean.effectiveSKUBean.salesChannelNo')[0].value;
	 var param ="&custCatNo="+ document.getElementsByName('bean.effectiveSKUBean.custCatNo')[0].value;
	    param +="&salesZone="+ document.getElementsByName('bean.effectiveSKUBean.salesZone')[0].value;
	    param +="&salesrepCode="+ document.getElementsByName('bean.effectiveSKUBean.salesrepCode')[0].value;//selected
	    param +="&roleNameChk=ROLE_CR_STOCK&pageName=StockReturn";
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/genSalesrepCodeAllListBoxAjax.jsp",
			data : param,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	}); 
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
			<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="<%=pageName %>"/>
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
						<html:form action="/jsp/reportAllAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
				    	<!-- ***** Criteria ******* -->
				    	<table align="center" border="0" cellpadding="3" cellspacing="0" >
					        <tr>
				                <td> ประเภทขาย </td>
								<td colspan="2">
								 <html:select property="bean.effectiveSKUBean.custCatNo" styleId="custCatNo">
										<html:options collection="CUST_CAT_LIST" property="custCatNo" labelProperty="custCatDesc"/>
								    </html:select>
							    </td>
							 </tr>
							  <tr>
				                <td> ช่วงข้อมูลขายเดือน </td>
								<td colspan="2">
								   <%
								   String yearMonthCheck = reportAllForm.getBean().getEffectiveSKUBean().getYearMonthChk();
								   String yearMonthChkChecked = "";
								   for(int i = 0;i<monthList.size();i++){ 
								     PopupBean item = monthList.get(i);
								     yearMonthChkChecked = "";
								     if( !Utils.isNull(yearMonthCheck).equals("") && Utils.isNull(yearMonthCheck).indexOf(item.getValue()) != -1){
								    	 yearMonthChkChecked = "checked";
								     }
								    %>
								     <input type="checkbox" name="yearMonthChk" <%=yearMonthChkChecked %> value="<%=item.getValue() %>"/>&nbsp;<%=item.getKeyName() %>&nbsp;
								   <%} %>
							    </td>
							 </tr>
							  <tr>
				                <td>  ภาคตามสายดูแล </td>
								<td colspan="2">
								  <html:select property="bean.effectiveSKUBean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList()">
										<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
								    </html:select>
								     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
								       พนักงานขาย 
								      <html:select property="bean.effectiveSKUBean.salesrepCode" styleId="salesrepCode" >
										<html:options collection="SALESREP_LIST" property="salesrepCode" labelProperty="salesrepCode"/>
								    </html:select>
							 </td>
							 </tr>
							<tr>
			                 <td> ร้านค้า<font color="red"></font></td>
							 <td>			
							     <html:text property="bean.effectiveSKUBean.customerCode" styleId="customerCode" size="35"
							     styleClass="\" autoComplete=\"off"
							     onkeypress="getAutoKeypress(event,this,'Customer')"
                                 onblur="getAutoOnblur(event,this,'Customer')"/> 
							     <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerMaster')"/>   
							    
							     แบรนด์<font color="red"></font>
							   <html:text property="bean.effectiveSKUBean.brand" styleId="brand" size="35"
							     styleClass="\" autoComplete=\"off"
							     onkeypress="getAutoKeypress(event,this,'Brand')"
                                 onblur="getAutoOnblur(event,this,'Brand')"/> 
							     <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Brand')"/>   
							 </td>
						   </tr>
						</table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="60%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtnLong"> 
								</a>&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtnLong">
								</a>&nbsp;
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtnLong">
								</a>
							</td>
							 <td align="right" width="40%" nowrap></td>
						</tr>
					</table>
			  </div>
			
			     <!-- ****** RESULT *************************************************** -->
			     <%
			       if(request.getAttribute("reportAllForm_RESULTS") != null ){
			    	 out.println(((StringBuffer)request.getAttribute("reportAllForm_RESULTS")).toString());
			    	 %>
			    	 <script>
						//load jquery
						$(function() {
							//Load fix column and Head
							$('#tblProduct').stickyTable({overflowy: true});
						});
					 </script>
			    <%} %>
			     <!-- ***************************************************************** -->
					<!-- hidden field -->
					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
					
				    <script>
					    <%if( !Utils.isNull(bean.getSalesrepCode()).equals("")){ %>
				         document.getElementsByName('bean.effectiveSKUBean.salesrepCode')[0].value='<%=Utils.isNull(bean.getSalesrepCode())%>';
				       <%}%>
				      <%--  <%if( !Utils.isNull(bean.getCustCatNo()).equals("")){ %>
				         document.getElementsByName('bean.effectiveSKUBean.custCatNo')[0].value='<%=Utils.isNull(bean.getCustCatNo())%>';
				       <%}%>
				       <%if( !Utils.isNull(bean.getSalesZone()).equals("")){ %>
				         document.getElementsByName('bean.effectiveSKUBean.salesZone')[0].value='<%=Utils.isNull(bean.getSalesZone())%>';
				       <%}%> --%>
					   loadSalesrepCodeList();
					   
					</script>
					
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
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>