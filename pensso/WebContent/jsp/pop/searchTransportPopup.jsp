<%@page import="com.isecinc.pens.web.buds.page.ConfPickingDAO"%>
<%@page import="com.isecinc.pens.bean.ConfPickingBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.model.MTransport"%>
<%@page import="com.isecinc.pens.bean.Transport"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
//Search
String pickingNo = Utils.isNull(request.getParameter("pickingNo"));
User user = (User) request.getSession().getAttribute("user");
PopupForm popupFormCri = new PopupForm();
List<Transport> results = null;
List<Transport> regionList = null;
List<Transport> provinceList = null;
Transport tModel = MTransport.searchTransport(popupFormCri,user);
if(tModel != null){
	results = tModel.getItemsList();
	regionList = tModel.getRegionList();
	provinceList = tModel.getProvinceList();
}
//Case PickingNo is not null get Prev Criteria to display
//GET Old criteria
ConfPickingBean p =  ConfPickingDAO.searchPickingTrans(pickingNo);

//Region
Map<String,String> regionAllMap = new HashMap<String,String>();
String regionAll = "";
if(session.getAttribute("REGION_ALL") != null){
	regionAll = (String)session.getAttribute("REGION_ALL");
}else{
   if( !Utils.isNull(pickingNo).equals("")){
       regionAll = p!=null?p.getRegionCri():"";
   }
}
if( !Utils.isNull(regionAll).equals("")){
	String[] regionAllArr = regionAll.split("\\,");
	for(int i=0;i<regionAllArr.length;i++){
		System.out.println("region["+regionAllArr[i]+"]");
		regionAllMap.put(regionAllArr[i], regionAllArr[i]);
	}
}
//provinceMapAll
Map<String,String> provinceAllMap = new HashMap<String,String>();
String provinceAll = "";
if(session.getAttribute("PROVINCE_ALL") != null){
	provinceAll = (String)session.getAttribute("PROVINCE_ALL");
}else{
   if( !Utils.isNull(pickingNo).equals("")){
      provinceAll = p!=null?p.getProvinceCri():"";
   }
}
if( !Utils.isNull(provinceAll).equals("")){
	String[] provinceAllArr = provinceAll.split("\\,");
	for(int i=0;i<provinceAllArr.length;i++){
		System.out.println("province["+provinceAllArr[i]+"]");
		provinceAllMap.put(provinceAllArr[i], provinceAllArr[i]);
	}
}

Map<String,String> amphurAllMap = new HashMap<String,String>();
String amphurAll = "";
if(session.getAttribute("AMPHUR_ALL") != null){
   amphurAll = (String)session.getAttribute("AMPHUR_ALL");
}else{
   if( !Utils.isNull(pickingNo).equals("")){
      amphurAll = p!=null?p.getAmphur():"";
   }
}
if( !Utils.isNull(amphurAll).equals("")){
	String[] amphurAllArr = amphurAll.split("\\,");
	for(int i=0;i<amphurAllArr.length;i++){
		amphurAllMap.put(amphurAllArr[i], amphurAllArr[i]);
	}
}
%>
<html>
<head>
<title>Search Transport</title>
<!-- For fix Head and Column Table -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<%-- <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
 --%>
 <%if(user.isMobile()){ %>
    <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_mobile_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
 <%}else{%>
    <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/popup_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
    <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
 <% }%>
 
<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="request" />

<script type="text/javascript">
function searchPopup(path, type) {
    document.popupForm.action = path + "/jsp/popupAction.do?do=search";
    document.popupForm.submit();
   return true;
}

function selectChk(){
	var regionChk = null;
	var regionAll = '';
	var provinceChk = null;
	var provinceAll = '';
	var amphurChk = null;
	var amphurAll = '';
	
	//get RegionChk
	regionChk = document.getElementsByName("region");
	for(var i=0;i<regionChk.length;i++){
		if(regionChk[i].checked){
			regionAll +=regionChk[i].value+",";
		}
	}
	//Get ProvinceChk
	//region|province
	var provinceChkAll = document.getElementsByName("province");
	for(var i=0;i<provinceChkAll.length;i++){
	     if( provinceChkAll[i].checked){
	    	 var provinceChkAllArr= provinceChkAll[i].value.split("|");
	    	 provinceAll += provinceChkAllArr[1]+",";
	     }
    }
    
    //Get AmphurChk  region|province|amphur
    var amphurChkAll = document.getElementsByName("amphur");
	for(var i=0;i<amphurChkAll.length;i++){
		var amphurChkAllArr = amphurChkAll[i].value.split("|");
	    if(amphurChkAll[i].checked && amphurChkAllArr[2] != ''){
	    	amphurAll +=amphurChkAllArr[2]+",";
	    }
    }//for
    
    /** set amphurAll to session **/
    var param ="regionAll="+regionAll;
        param +="&provinceAll="+provinceAll;
        param +="&amphurAll="+amphurAll;
    var getData = $.ajax({
		url: "${pageContext.request.contextPath}/jsp/ajax/setPickingCriSessionAjax.jsp",
		data : encodeURI(param),
		async: false,
		cache: false,
		success: function(getData){
		  returnString = jQuery.trim(getData);
		}
	}).responseText;
    
    window.opener.setMainValue('<%=Utils.isNull(request.getParameter("page"))%>',regionAll,provinceAll,amphurAll);
	window.close();
}

function setProvinceChkByRegion(regionChk){
	var provinceChk = null;
	var amphurChk = null;
	var provinceChkAll = document.getElementsByName("province");
	for(var i=0;i<provinceChkAll.length;i++){
		var provinceArrChk = provinceChkAll[i].value.split("|");//region|province
		//alert("regionChk["+regionChk.value+"]provinceArrChk[0]["+provinceArrChk[0]+"]");
	    if(provinceArrChk[0] == regionChk.value){
	       provinceChkAll[i].checked = regionChk.checked;
	      
	       setAmphurChkByProvince(regionChk,provinceChkAll[i]);
	    }
	}//for
}
function setAmphurChkByProvince(regionChk,provinceChk){
	var amphurChkAll = document.getElementsByName("amphur");
	for(var i=0;i<amphurChkAll.length;i++){
	
		var provinceArrChk = provinceChk.value.split("|");	//region|provinceId
		var amphurChkAllArr = amphurChkAll[i].value.split("|");//region|provinceId|amphur
		//alert("regionChk["+regionChk.value+"]amphurChkAllArr[0]["+amphurChkAllArr[0]+"]amphurChkAllArr[1]["+amphurChkAllArr[1]+"]provinceArrChk[1]["+provinceArrChk[1]+"]");
	    if( regionChk.value==amphurChkAllArr[0] && amphurChkAllArr[1]==provinceArrChk[1]){
	    	amphurChkAll[i].checked = provinceChk.checked;
	    }
    }//for
}
function setProvinceChk(provinceChk){
	setAmphurChkByProvince(provinceChk);
}

</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/popupAction">
<html:hidden property="page" value="TRANSPORT"></html:hidden>
<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px" class="txt1">
		<td width="15%" >&nbsp;</td>
		<td width="90%" ><b>ระบุสายขนส่ง/จังหวัดที่ต้องการจัดสินค้า</b></td>
	</tr>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<input type="button" name="OK" value="ยืนยันเลือกข้อมูล" onclick="selectChk()"  class="newPosBtnLong"/>
			<input type="button" name="CLOSE" value="ปิดหน้าจอนี้" onclick="javascript:window.close();"  class="newPosBtnLong"/>
		</td>
	</tr>
</table>
<!-- RESULT -->
<%if(results != null && results.size() >0){ %>
<div style='align:cenetr;height:600px;width:900px;'>
<table id='myTable' class='table table-condensed table-striped' border='1'> 
   <thead> 
    <tr>
		<th >.</th>
		<th >สายขนส่ง</th>
		<th ></th>
		<th >จังหวัด</th>
		<th >อำเภอ/เขต</th>
	</tr>
	</thead>
	<%
	int provinceC = -1;
	String regionChecked = "";
	String provinceChecked = "";
	Map<String,String> regionMap = new HashMap<String,String>();
	Map<String,String> provinceMap = new HashMap<String,String>();
	  for(int i=0;i<results.size();i++){
		  Transport t = results.get(i);
	%>
	<tbody>
	<tr>
	    <%
		  if(regionMap.get(t.getRegion()) ==null){
			  regionChecked = regionAllMap.get(t.getRegion()) != null?"checked":"";
	    %>
	         <td><input type="checkbox" name="region" value="<%=t.getRegion()%>" 
	         onchange="setProvinceChkByRegion(this)" <%=regionChecked%>></td>
	         <td><%=t.getRegion()%></td>
	    <% } else { %>
	         <td></td>
	         <td></td>
	    <%}%>
	
	    <%
		  if(provinceMap.get(t.getRegion()+t.getProvince()) ==null){
			  provinceC++;
			  provinceChecked = provinceAllMap.get(t.getProvince()) != null?"checked":"";
	    %>
	        <td>
	           <input type="checkbox" name ="province" id="province_<%=provinceC %>" 
	           value="<%=t.getRegion()%>|<%=t.getProvince() %>" 
	           onchange="setProvinceChk(this)" <%=provinceChecked %> />
	         </td>
	         <td><%=t.getProvince()%></td>
	      <% } else { %>
	         <td></td>
	         <td></td>
	    <%}%>
	  <td>
	  <input type="checkbox" name ="amphur" id="amphur_<%=i %>" value="<%=t.getRegion()%>|<%=t.getProvince()%>|<%=t.getAmphur()%>"
	  <%=(amphurAllMap.get(t.getAmphur())!=null?"checked":"") %> >
	  <%=t.getAmphur() %></td>
	</tr>
	<%
	   regionMap.put(t.getRegion(), t.getRegion());
	   provinceMap.put(t.getRegion()+t.getProvince(), t.getRegion()+t.getProvince());
	}//for%>
	</tbody>
</table>
</div>

<script>
	//load jquery
	$(function() {
		//Load fix column and Head
		$('#myTable').stickyTable({overflowy: true});
	});
</script>
<%} %>
<!-- RESULT -->

</html:form>
</body>
</html>