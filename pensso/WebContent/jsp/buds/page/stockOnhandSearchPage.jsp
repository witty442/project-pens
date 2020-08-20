<%@page import="com.isecinc.pens.bean.StockOnhandBean"%>
<%@page import="com.isecinc.pens.web.buds.BudsAllForm"%>
<%@page import="com.isecinc.core.model.I_PO"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="budsAllForm" class="com.isecinc.pens.web.buds.BudsAllForm" scope="session" />
<%
StockOnhandBean bean = ((BudsAllForm)session.getAttribute("budsAllForm")).getBean().getStockOnhandBean();
System.out.println("StockOnhandBean:"+bean);
String role = ((User)session.getAttribute("user")).getType();
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
	String hideAll = "true"; 
	String pageName = !Utils.isNull(request.getParameter("pageName")).equals("")?Utils.isNull(request.getParameter("pageName")):budsAllForm.getPageName();
	String subPageName = !Utils.isNull(request.getParameter("subPageName")).equals("")?Utils.isNull(request.getParameter("subPageName")):budsAllForm.getSubPageName();

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
	new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
}
function search(){
	var form = document.budsAllForm;
	var path =document.getElementById("path").value 
	form.action = path + "/jsp/budsAllAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}
function printReport(pickingNo){
	var form = document.budsAllForm;
	var path =document.getElementById("path").value 
	form.action = path + "/jsp/budsAllAction.do?do=printReport&reportName=controlPickingReport&pickingNo="+pickingNo;
	form.submit();
	return true;
}
function exportExcel(){
	var form = document.budsAllForm;
	var path =document.getElementById("path").value 
	form.action = path + "/jsp/budsAllAction.do?do=export&reportName=StockOnhandReport";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.payForm;
	var path = document.getElementById("path").value ;
	form.action = path + "/jsp/payAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}
function clearForm(path){
	var form = document.budsAllForm;
	form.action = path + "/jsp/budsAllAction.do?do=prepareSearchHead&action=new";
	form.submit();
	return true;
}
function openPopupPage(page){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value;
    var param = "&pageName="+page+"&hideAll=true&selectone=false";
    if(page=="BRAND"){
    	param +="&brand=381,382,383";//only buds item
    }else  if(page=="SUB_BRAND"){
    	param +="&brand=381,382,383";//only buds item
    	if(document.getElementById("brand").value !=''){
    		param +="&brand="+document.getElementById("brand").value;
    	}
    }else  if(page=="PRODUCT"){
    	param +="&brand=381,382,383";//only buds item
    	if(document.getElementById("brand").value !=''){
    		param +="&brand="+document.getElementById("brand").value;
    	}
    	if(document.getElementById("subBrand").value !=''){
    		param +="&subBrand="+document.getElementById("subBrand").value;
    	}
    }
	var url = path + "/jsp/popupAction.do?do=prepareAll&action=new"+param;
	
	PopupCenterFullHeight(url,"",700);
}

function setDataPopupValue(code,desc,page){
	//alert(page+",code="+code+",desc="+desc);
	if("BRAND"==page){
	   document.getElementById("brand").value = code;
	   document.getElementById("brandName").value = desc;
	}else if("SUB_BRAND"==page){
	   document.getElementById("subBrand").value = code;
	   document.getElementById("subBrandName").value = desc;
	}else if("PRODUCT_INFO"==page){
	   document.getElementById("productCode").value = code;
	   document.getElementById("productName").value = desc;
	}
} 

function getAutoOnblur(e,obj,pageName){
	var form = document.budsAllForm;
	if(obj.value ==''){
		if("BRAND" == pageName){
			form.brandName.value = '';
		}else if("SUB_BRAND"==pageName){
			form.subBrandName.value = '';
		}else if("PRODUCT_INFO"==pageName){
			form.productName.value = '';
		}
	}else{
		getAutoDetail(obj,pageName);
	}
}
function getAutoKeypress(e,obj,pageName){
	var form = document.budsAllForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("BRAND" == pageName){
				form.brandName.value = '';
			}else if("SUB_BRAND"==pageName){
				form.subBrandName.value = '';
			}else if("PRODUCT_INFO"==pageName){
				form.productName.value = '';
			}
		}else{
			getAutoDetail(obj,pageName);
		}
	}
}

function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.budsAllForm;
	
	//prepare parameter
	var param = "pageName="+pageName;
	if("BRAND" == pageName){
		param +="&brand="+obj.value;
	}else if("SUB_BRAND"==pageName){
    	if(document.getElementById("brand").value !=''){
    		param +="&brand="+document.getElementById("brand").value;
    	}
		param +="&subBrand="+obj.value;
	}else if("PRODUCT_INFO"==pageName){
    	if(document.getElementById("brand").value !=''){
    		param +="&brand="+document.getElementById("brand").value;
    	}
    	if(document.getElementById("subBrand").value !=''){
    		param +="&subBrand="+document.getElementById("subBrand").value;
    	}
		param +="&productCode="+obj.value;
	}
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getAutoKeypressAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if("BRAND" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.brand.value = retArr[1];
			form.brandName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.brand.focus();
			form.brand.value = '';
			form.brandName.value = "";
		}
	}else if("SUB_BRAND" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.subBrand.value = retArr[1];
			form.subBrandName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.subBrand.focus();
			form.subBrand.value = '';
			form.subBrandName.value = "";
		}
	}else if("PRODUCT_INFO" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.productCode.value = retArr[1];
			form.productName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.productCode.focus();
			form.productCode.value = '';
			form.productName.value = "";
		}
	}
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
				<jsp:param name="function" value="<%=subPageName%>"/>
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
						<html:form action="/jsp/budsAllAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
				    	<!-- ***** Criteria ******* -->
				    	<table align="center" border="0" cellpadding="3" cellspacing="0" >
					        <tr>
				                <td> Brand</td>
								<td>
								    <html:text property="bean.stockOnhandBean.brand" 
								    styleClass="\" autoComplete=\"off" styleId="brand"
								    onkeypress="getAutoKeypress(event,this,'BRAND')"
					                onblur="getAutoOnblur(event,this,'BRAND')"></html:text>
								    <input type="button" name="btBrand" value="..." 
								    onclick="openPopupPage('BRAND')" class="newPosBtnLong"/>
							    </td>
							    <td>
							     <html:text property="bean.stockOnhandBean.brandName" styleId="brandName" 
							     styleClass="disableText" readonly="true" size="30"/>
							    </td>
							 </tr>
							 <tr>
				                <td> SubBrand</td>
								<td>
								    <html:text property="bean.stockOnhandBean.subBrand" 
								    styleClass="\" autoComplete=\"off" styleId="subBrand"
								    onkeypress="getAutoKeypress(event,this,'SUB_BRAND')"
					                onblur="getAutoOnblur(event,this,'SUB_BRAND')">
								    </html:text>
								    <input type="button" name="btBrand" value="..." 
								    onclick="openPopupPage('SUB_BRAND')" class="newPosBtnLong"/>
							    </td>
							    <td>
							     <html:text property="bean.stockOnhandBean.subBrandName" styleId="subBrandName" 
							     styleClass="disableText" readonly="true" size="30"/>
							    </td>
							 </tr>
							 <tr>
				                <td> SKU</td>
								<td>
								    <html:text property="bean.stockOnhandBean.productCode" 
								    styleClass="\" autoComplete=\"off" styleId="productCode"
								    onkeypress="getAutoKeypress(event,this,'PRODUCT_INFO')"
					                onblur="getAutoOnblur(event,this,'PRODUCT_INFO')">
								    </html:text>
								    <input type="button" name="btBrand" value="..." 
								    onclick="openPopupPage('PRODUCT_INFO')" class="newPosBtnLong"/>
							    </td>
							    <td>
							     <html:text property="bean.stockOnhandBean.productName" styleId="productName" 
							     styleClass="disableText" readonly="true" size="30"/>
							    </td>
							 </tr>
						</table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="100%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="  ค้นหา  " class="newPosBtnLong">
								</a>&nbsp;
								<a href="javascript:exportExcel()">
									<input type="button" value=" Export " class="newPosBtnLong">
								</a>
								&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value=" Clear  " class="newPosBtnLong">
								</a>&nbsp;
								
							</td>
							 <td align="right" width="40%" nowrap></td>
						</tr>
					</table>
			  </div>
			
			   <!-- ****** RESULT *************************************************** -->
			     <%
			       if(request.getAttribute("budsAllForm_RESULTS") != null ){
			    	%>
			    	 <div style="height:300px;width:<%=Utils.convertStrToInt(screenWidth)-50%>px;">
			    	<%
			    	    out.println(((StringBuffer)request.getAttribute("budsAllForm_RESULTS")).toString());
			    	%>
			    	 </div>
			    	 
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
					<input type="hidden" name="pageName" id="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="subPageName" id="subPageName" value="<%=request.getParameter("subPageName") %>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
					
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

 <!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->