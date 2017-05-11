<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<style type="text/css">
input[type=checkbox]
{
  /* Double-sized Checkboxes */
  -ms-transform: scale(2); /* IE */
  -moz-transform: scale(2); /* FF */
  -webkit-transform: scale(2); /* Safari and Chrome */
  -o-transform: scale(2); /* Opera */
  padding: 10px;
}

input[type=radio]
{
  /* Double-sized Checkboxes */
  -ms-transform: scale(2); /* IE */
  -moz-transform: scale(2); /* FF */
  -webkit-transform: scale(2); /* Safari and Chrome */
  -o-transform: scale(2); /* Opera */
  padding: 10px;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="request" />
<%
	String mcArea = Utils.isNull(request.getParameter("mcArea"));
    String mcAreaDesc = "";
    
    //Get Region Desc
    if( !Utils.isNull(mcArea).equals("")){
	    PopupForm pCri = new PopupForm();
	    pCri.setCodeSearch(mcArea);
	    List dList = MCDAO.searchMCRefList(pCri,"equals","MCarea");
	    mcAreaDesc = dList != null?((PopupForm)dList.get(0)).getDesc():"";	
    }
    
    String mcRoute = Utils.isNull(request.getParameter("mcRoute"));
    String staffType = Utils.isNull(request.getParameter("staffType"));
    String active = Utils.isNull(request.getParameter("active"));
    String currentPage = request.getParameter("d-1552-p")==null?"1":request.getParameter("d-1552-p");
    
    /** Store Select MutilCode in each Page **/
    String codes = Utils.isNull(session.getAttribute("codes"));
    String descs = Utils.isNull(session.getAttribute("descs"));
    
    System.out.println("codes:"+codes);
%>
<script type="text/javascript">

function searchPopup(path, type) {
    document.popupForm.action = path + "/jsp/searchCustomerPopupAction.do?do=searchMC&operation=equals";
    document.popupForm.submit();
   return true;
}


function selectMultiple(){
	var chRadio = document.getElementsByName("chRadio");

	var retCode = document.getElementsByName("code");
	var retDesc = document.getElementsByName("desc");
	
	var empType = document.getElementsByName("empType");
	var mobile1 = document.getElementsByName("mobile1");
	var mobile2 = document.getElementsByName("mobile2");
	var empRefId = document.getElementsByName("empRefId");
	var empTypeDesc = document.getElementsByName("empTypeDesc");
	var region = document.getElementsByName("region");
	
	for(var i=0;i<chRadio.length;i++){
		//alert(chRadio[i].checked);
        if(chRadio[i].checked){
          // alert(i+":"+retCode[i].value);
           window.opener.setStoreMainValue(retCode[i].value,retDesc[i].value,empType[i].value,mobile1[i].value,mobile2[i].value,empRefId[i].value,empTypeDesc[i].value,region[i].value);
           window.close();
           break;
        }
	}

}

</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/searchCustomerPopupAction">
<input type="hidden" name="mcArea" value="<%=mcArea %>"/>
<input type="hidden" name="mcRoute" value="<%=mcRoute %>"/>
<input type="hidden" name="staffType" value="<%=staffType %>"/>
<input type="hidden" name="active" value="<%=active %>"/>
<input type="hidden" name="currentPage"  value ="<%=currentPage%>" />
<input type="hidden" name="codes" value ="<%=codes%>" />
<input type="hidden" name="descs" value ="<%=descs%>" />

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px" class="txt1">
		<td width="15%" >&nbsp;</td>
		<td width="90%" ><b>ค้นหาข้อมูล  MC Staff (<%=mcAreaDesc%>)</b></td>
	</tr>
	<tr height="21px" class="txt1">
		<td width="15%" ><b>Employee ID</b>  </td>
		<td width="90%" ><html:text property="codeSearch"  size="30" style="height:20px"/>
		<input type="button" name="search" value="Search" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
	<tr height="21px" class="txt1">
		<td ><b>ชื่อ</b></td>
		<td ><html:text property="descSearch"  size="60" style="height:20px"/></td>
	</tr>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<input type="button" name="ok" value="  OK  " onclick="selectMultiple()" style="width:60px;"/>
			<input type="button" name="close" value=" Close " onclick="javascript:window.close();" style="width:60px;"/>
			<!-- <input type ="checkbox" name="chCheckAll" id="chCheckAll" onclick="selectAll();"  /> เลือกทั้งหมด -->
		</td>
	</tr>
</table>
<!-- RESULT -->
<display:table style="width:100%;" id="item" name="requestScope.CUSTOMER_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
    	
    <display:column  style="text-align:center;width:20%" title="เลือกข้อมูล" sortable="false" class="chk">
		<input type ="radio" name="chRadio" />
		<input type ="hidden" name="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="desc" value="<bean:write name="item" property="desc"/>" />
		
	    <input type ="hidden" name="empRefId" value="${item.mcEmpBean.empRefId}" />
		<input type ="hidden" name="empType" value="${item.mcEmpBean.empType}" />
		<input type ="hidden" name="region" value="${item.mcEmpBean.region}" />
		<input type ="hidden" name="empTypeDesc" value="${item.mcEmpBean.empTypeDesc}" />
		<input type ="hidden" name="mobile1" value="<bean:write name="item" property="mcEmpBean.mobile1"/>" />
		<input type ="hidden" name="mobile2" value="<bean:write name="item" property="mcEmpBean.mobile2"/>" /> 
		
	 </display:column>
    <display:column  title="ID" property="mcEmpBean.empRefId"   sortable="false" class="code"/>			    
    <display:column  title="Employee ID" property="code"   sortable="false" class="code"/>
    <display:column  title="รายละเอียด" property="desc"  sortable="false" class="desc"/>								
</display:table>	
<!-- RESULT -->



</html:form>
</body>
</html>