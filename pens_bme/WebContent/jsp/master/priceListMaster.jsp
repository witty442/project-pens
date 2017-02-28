<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
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
<jsp:useBean id="priceListMasterForm" class="com.isecinc.pens.web.pricelist.PriceListMasterForm" scope="session" />
<%
if(request.getAttribute("custGroupList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	ref.setCodeSearch(PickConstants.STORE_TYPE_HISHER_CODE);
	billTypeList.addAll(GeneralDAO.searchCustGroup(ref));
	
	request.setAttribute("custGroupList",billTypeList);
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/pick_job.css" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">

function loadMe(){
}
function clearForm(path){
	var form = document.priceListMasterForm;
	form.action = path + "/jsp/priceListMasterAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.priceListMasterForm;
	form.action = path + "/jsp/priceListMasterAction.do?do=prepare&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.priceListMasterForm;
	var groupCode =$('#groupCode').val();
	var pensItem =$('#pensItem').val();
	var wholePriceBF =$('#wholePriceBF').val();
	var retailPriceBF =$('#retailPriceBF').val();
	var productType =$('#productType').val();
	
	if(productType ==""){
		alert("กรุณากรอก  กลุ่มสินค้า");
		return false;
	}
	if(groupCode ==""){
		alert("กรุณากรอก   group Code");
		return false;
	}else{
		if(groupCode.length !=6){
			alert("กรุณากรอก  group Code ให้ครบ 6 ตำแหน่ง");
			return false;
		}
	}
	if(pensItem ==""){
		alert("กรุณากรอก  pensItem");
		return false;
	}
	
	if(wholePriceBF ==""){
		alert("กรุณากรอก wholePriceBF");
		return false;
	}
	if(retailPriceBF ==""){
		alert("กรุณากรอก retailPriceBF");
		return false;
	}

	if(confirm("ยันยันการบันทึกข้อมูล")){
		form.action = path + "/jsp/priceListMasterAction.do?do=save";
		form.submit();
		return true;
	}
	return false;
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
				<jsp:param name="function" value="PriceListMaster"/>
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
						<html:form action="/jsp/priceListMasterAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
							     <tr>
							     <%if(priceListMasterForm.getMode().equalsIgnoreCase("add")){ %>
	                                   <td colspan="1" align="center"> <b>เพิ่มรายการใหม่</b> </td>		
	                             <%}else{ %>
	                                   <td colspan="1" align="center"> <b>แก้ไขรายการ </b> </td>	
	                             <%} %>
								</tr>
					            <tr>
								    <td>กลุ่มสินค้า<font color="red">*</font></td>
								    <td>
								       <html:select property="bean.productType" styleId="productType">
											<html:options collection="productTypeList" property="key" labelProperty="name"/>
									    </html:select>
								    </td>
						        </tr>
								<tr>
                                    <td> กลุ่มร้านค้า <font color="red">*</font></td>
									<td>			
										 <html:select property="bean.custGroup" styleId="custGroup">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								
								<tr>
                                    <td> Group Code<font color="red">*</font> </td>
									<td >
						               <html:text property="bean.groupCode" styleId="groupCode" size="20"/>
									</td>
								</tr>	
								<tr>
                                    <td> Pens Item<font color="red">*</font></td>
									<td >
						               <html:text property="bean.pensItem" styleId="pensItem" size="20"/>
									</td>
								</tr>	
								<tr>
                                    <td> ราคาส่ง His&Her (-Vat)<font color="red">*</font></td>
									<td >
						               <html:text property="bean.wholePriceBF" styleId="wholePriceBF" size="20" onblur="isNum2Digit(this);" styleClass="enableNumber" />
									</td>
								</tr>	
								<tr>
                                    <td> ราคาปลีก (-Vat)<font color="red">*</font> </td>
									<td >
						               <html:text property="bean.retailPriceBF" styleId="retailPriceBF" size="20" onblur="isNum2Digit(this);" styleClass="enableNumber" />
									</td>
								</tr>	
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
										<a href="javascript:save('${pageContext.request.contextPath}')">
										  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>	
										
										<a href="javascript:back('${pageContext.request.contextPath}')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>	
										
									</td>
								</tr>
							</table>
					  </div>
					<!-- ************************Result ***************************************************-->
					
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
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