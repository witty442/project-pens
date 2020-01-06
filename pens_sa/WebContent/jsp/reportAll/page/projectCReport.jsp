<%@page import="com.isecinc.pens.web.projectc.ProjectCBean"%>
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
ProjectCBean bean = ((ReportAllForm)session.getAttribute("reportAllForm")).getBean().getProjectCBean();
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<script type="text/javascript">

function loadMe(){
	var form = document.reportAllForm;
	
	//setDateMonth
	<%if( "month".equals(Utils.isNull(bean.getTypeSearch()))|| "".equals(Utils.isNull(bean.getTypeSearch()))) { %>
	    setPeriodDate(form.periodDesc);
	    document.getElementById("div_month").style.visibility  = "visible";
	    document.getElementById("div_day").style.visibility  = "hidden";
	    form.startDate.className ="disableText";
	    form.endDate.className ="disableText";
	    form.startDate.readOnly =true;
	    form.endDate.readOnly =true;
	<%}else{%>
	    document.getElementById("div_month").style.visibility  = "hidden";
	    document.getElementById("div_day").style.visibility  = "visible";
		form.startDate.className ="enableText";
		form.endDate.className ="enableText";
		form.startDate.readOnly =false;
		form.endDate.readOnly =false;
		new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
		new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
	<%}%>
	
	// Load Amphur By Province
	<%if( !Utils.isNull(reportAllForm.getBean().getProjectCBean().getProvince()).equals("")){%>
	   document.getElementsByName('bean.projectCBean.province')[0].value = "<%=Utils.isNull(reportAllForm.getBean().getProjectCBean().getProvince())%>";
	   loadDistrict('${pageContext.request.contextPath}');
	   document.getElementsByName('bean.projectCBean.amphor')[0].value = "<%=Utils.isNull(reportAllForm.getBean().getProjectCBean().getAmphor())%>";
	<%} %>
}
function setTypeSerch(typeSerch){
	var form = document.reportAllForm;
	//alert(periodDesc);
	var periodDesc = form.periodDesc;
	if(typeSerch.value =='month'){
	   form.period.value = periodDesc.value.split("|")[0];
	   form.startDate.value = periodDesc.value.split("|")[1];
	   form.endDate.value = periodDesc.value.split("|")[2]; 
	   
	   //disable startDate endDate
	   document.getElementById("div_month").style.visibility  = "visible";
	   document.getElementById("div_day").style.visibility  = "hidden";
	   form.startDate.className ="disableText";
	   form.endDate.className ="disableText";
	   form.startDate.readOnly =true;
	   form.endDate.readOnly =true;
	}else{
		form.period.value = "";
		form.startDate.value = "";
		form.endDate.value = "";
		
		document.getElementById("div_month").style.visibility  = "hidden";
		document.getElementById("div_day").style.visibility  = "visible";
		form.startDate.className ="enableText";
		form.endDate.className ="enableText";
		form.startDate.readOnly =true;
		form.endDate.readOnly =true;
		new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
		new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
	}
}

function setPeriodDate(periodDesc){
	var form = document.reportAllForm;
	//alert(periodDesc);
	form.period.value = periodDesc.value.split("|")[0];
	form.startDate.value = periodDesc.value.split("|")[1];
	form.endDate.value = periodDesc.value.split("|")[2]; 
}
function search(path){
	var form = document.reportAllForm;
	   /* var asOfDateFrom = form.salesDate.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
	   if(asOfDateFrom ==""){ 
		   alert("กรุณากรอกข้อมูลวันที่ As Of");
		   return false;
	   }
	    if(pensCustCodeFrom ==""){ 
		   alert("กรุณากรอกข้อมูลรหัสร้านค้า");
		   return false;
	   }  */
	 
	form.action = path + "/jsp/reportAllAction.do?do=search";
	form.submit();
	return true;
}
function exportExcel(path){
	var form = document.reportAllForm;
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
function openImageList(checkDate,storeCode){
	var form = document.reportAllForm;
	var param = "checkDate="+checkDate+"&storeCode="+storeCode;
    var path = document.getElementById("path").value;
	var url = path + "/jsp/reportAll/page/projectCImageList.jsp?"+param;
	PopupCenterFull(url,"List Image");
}

function openPopup(path,pageName){
	var form = document.reportAllForm;
	var param = "";
	if("CustomerProjectC" == pageName){
		param = "&pageName="+pageName+"&hideAll=true&selectone=true";
	}else if("BranchProjectC" == pageName){
		param = "&pageName="+pageName+"&hideAll=true&selectone=true";
		if(form.storeCode.value==""){
			alert("กรุณาระบุร้านค้า");
			form.storeCode.focus();
			return "";
		}
		param  +="&storeCode="+form.storeCode.value;
	}else if("Brand" == pageName){
		param = "&pageName="+pageName+"&hideAll=true&selectone=false";
	}

	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.reportAllForm;
	if("CustomerProjectC" == pageName){
		form.storeCode.value = code;
		form.storeName.value = desc;
	}else if("BranchProjectC" == pageName){
		form.branchId.value = code;
		form.branchName.value = desc;
	}else if("Brand" == pageName){
		form.brand.value = code;
		form.brandName.value = desc;
	}
} 
/** get autoKeypress Ajax **/
function getAutoOnblur(e,obj,pageName){
	var form = document.reportAllForm;
	if(obj.value ==''){
		if("CustomerProjectC" == pageName){
			form.storeCode.value = '';
			form.storeName.value = "";
		}else if("BranchProjectC" == pageName){
			form.branchId.value = '';
			form.branchName.value = "";
		}else if("Brand" == pageName){
			form.brand.value = '';
			form.brandName.value = "";
		}
	}else{
		getAutoDetail(obj,pageName);
	}
}
function getAutoKeypress(e,obj,pageName){
	var form = document.reportAllForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("SalesrepSales" == pageName){
				form.storeCode.value = '';
				form.storeName.value = "";
			}else if("BranchProjectC" == pageName){
				form.branchId.value = '';
				form.branchName.value = "";
			}else if("Brand" == pageName){
				form.brand.value = '';
				form.brandName.value = "";
			}
		}else{
			getAutoDetail(obj,pageName);
		}
	}
}
function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.reportAllForm;
	var path = form.path.value;
	
	//prepare parameter
	var param = "";
	if("CustomerProjectC"==pageName){
		param   ="pageName="+pageName;
		param  +="&storeCode="+obj.value;
	}else if("BranchProjectC"==pageName){
		if(form.storeCode.value==""){
			alert("กรุณาระบุร้านค้า");
			form.storeCode.focus();
			return "";
		}
		param   ="pageName="+pageName;
		param  +="&storeCode="+form.storeCode.value;
		param  +="&branchId="+obj.value;
	}else if("Brand"==pageName){
		param   ="pageName="+pageName;
		param  +="&brand="+obj.value;
	}
	var getData = $.ajax({
			url: path+"/jsp/ajax/getAutoKeypressAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	if("CustomerProjectC" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.storeCode.value = retArr[1];
			form.storeName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.storeCode.focus();
			form.storeCode.value = '';
			form.storeName.value = "";
		}
	}else if("BranchProjectC" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.branchId.value = retArr[1];
			form.branchName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.branchId.focus();
			form.branchId.value = '';
			form.branchName.value = "";
		}
		
	}else if("Brand" == pageName){
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
		
	}
}
function loadDistrict(path){
	var cboDistrict = document.getElementsByName('bean.projectCBean.amphor')[0];
	$(function(){
		var getData = $.ajax({
			url: path+"/jsp/reportAll/ajax/getAmphorAjax.jsp",
			data : "provinceName=" + encodeURI(document.getElementsByName('bean.projectCBean.province')[0].value),
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
				                <td> เลือกรอบเวลา <font color="red">*</font></td>
								<td>					
									 <html:select property="bean.projectCBean.typeSearch" styleId="typeSearch" onchange="setTypeSerch(this)">
										<html:option value="month">เดือน</html:option>
										<html:option value="day">วัน</html:option>
								    </html:select>
								
								   <span id="div_month">
									        เดือน <font color="red">*</font>
									     <html:select property="bean.projectCBean.periodDesc" styleId="periodDesc" onchange="setPeriodDate(this)">
											<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
									    </html:select>
								    </span>
								     <html:hidden property="bean.projectCBean.period" styleId="period"/>
									 <span id="div_day">วันที่ &nbsp;</span>
								    <html:text property="bean.projectCBean.startDate" styleId="startDate" size="20" readonly="true" styleClass="disableText"/>
								        -
									<html:text property="bean.projectCBean.endDate" styleId="endDate" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
			                 <td> ร้านค้า<font color="red"></font></td>
							 <td>			
							     <html:text property="bean.projectCBean.storeCode" styleId="storeCode" size="15"
							     styleClass="\" autoComplete=\"off"
							     onkeypress="getAutoKeypress(event,this,'CustomerProjectC')"
                                 onblur="getAutoOnblur(event,this,'CustomerProjectC')"/> 
							     <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerProjectC')"/>   
							      <html:text property="bean.projectCBean.storeName" styleId="storeName" size="35"/>   
							     
							     &nbsp;&nbsp; 
							      สาขา
							      <html:text property="bean.projectCBean.branchId" styleId="branchId" size="15"
							     styleClass="\" autoComplete=\"off"
							     onkeypress="getAutoKeypress(event,this,'BranchProjectC')"
                                 onblur="getAutoOnblur(event,this,'BranchProjectC')"/> 
							     <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','BranchProjectC')"/>   
							      <html:text property="bean.projectCBean.branchName" styleId="branchName" size="35"/>   
							      
							 </td>
						   </tr>
						   <tr>
			                 <td> จังหวัด<font color="red"></font></td>
							 <td>			
							     <html:select property="bean.projectCBean.province" styleId="province" styleClass="txt_style" onchange="loadDistrict('${pageContext.request.contextPath}');">
								     <html:options collection="PROVINCE_LIST" property="province" labelProperty="provinceName"/>
							     </html:select>
							     &nbsp;&nbsp; 
							      อำเภอ
							      <html:select property="bean.projectCBean.amphor" styleId="amphor" >
						                <%-- <html:options collection="DISTRICT_LIST" property="district" labelProperty="districtName"/> --%>
				                   </html:select>
				                   
				                 &nbsp;&nbsp;
				                                    แบรนด์
				                <html:text property="bean.projectCBean.brand" styleId="brand" size="15"
							     styleClass="\" autoComplete=\"off"
							     onkeypress="getAutoKeypress(event,this,'Brand')"
                                 onblur="getAutoOnblur(event,this,'Brand')"/> 
							     <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Brand')"/>   
							      <html:text property="bean.projectCBean.brandName" styleId="brandName" size="35"/> 
							 </td>
						   </tr>
							<tr>
			                <td> รูปแบบการแสดงผล  <font color="red"></font></td>
							<td colspan="2">
							    <html:select property="bean.projectCBean.reportType" styleId="reportType">
									<html:options collection="REPORT_TYPE_LIST" property="reportValue" labelProperty="reportType"/>
							    </html:select> 
							    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							  
							     <html:select property="bean.projectCBean.condType" styleId="condType">
									<html:options collection="COND_TYPE_LIST" property="value" labelProperty="keyName"/>
							    </html:select> 
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
			
		     <!-- ****** RESULT ***************************************************************** -->
		     <%
		      if(bean!= null  && bean.getDataStrBuffer() != null ){
		    	 out.println(bean.getDataStrBuffer().toString());
		      }
		     %>
		     <!-- ******************************************************************************* -->
					<!-- hidden field -->
					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
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