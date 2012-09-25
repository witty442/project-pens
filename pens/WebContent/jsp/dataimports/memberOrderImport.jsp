<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
String[] memberIds = (String[])request.getAttribute("memberIds");
List<Member> members = new ArrayList<Member>();
if(memberIds!=null){
	for(String id:memberIds){
		members.add(new MMember().find(id));
	}
}
pageContext.setAttribute("members",members,PageContext.PAGE_SCOPE);
%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Member"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MMember"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>" /></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" language="javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('orderDate'));
}
function showMember(path, id){
	var txt = document.getElementsByName('memberIds');
	id="";
	for(i=0;i<txt.length;i++){
		id+=","+txt[i].value;
	}
	if(id.length>0) id=id.substring(1,id.length);
	if(id.length==0)id=0;
	window.open(path + "/jsp/pop/view/memberViewPopup.jsp?id="+id, "Member List", "width=500,height=350,location=No,resizable=No");
}
function setMember(code, name){
	loadMember(null,code);
}
function loadMember(e,code){
	var member=code;
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/memberQuery.jsp",
				data : "mCode=" + code,
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);	
					member+='||'+getData;
				}
			}).responseText;
		});
	}
	addMemberToTable(member);
}

function addMemberToTable(member){
	var jQtable = $('#tblMember');
	var rows=0;
    jQtable.each(function(){
        var $table = $(this);
        // Number of td's in the last table row
        var n = $('tr', this).length;
        var className="lineO";
        if(n%2==0)
        	className="lineE";
    	rows=n;
        var tds = '<tr class='+className+'>';
        tds += '<td align="left"></td>';
        tds += '<td align="center" width="5px;"></td>';
        tds += '</tr>';
        if($('tbody', this).length > 0){
            $('tbody', this).append(tds);
        }else {
            $(this).append(tds);
        }
    });
    setValueToMember('${pageContext.request.contextPath}',member,rows);
}

function setValueToMember(path, member,rows){
	var tbl = document.getElementById('tblMember');
	var memberId = trim(member.split("||")[1]);
    var memberLabel = trim(member.split("||")[0])+' '+trim(member.split("||")[2]);
    var inputLabel = "<input type='hidden' name='memberIds' value='"+eval(memberId)+"'>";
    var iconLabel = '<a href="#" onclick="deleteMember('+(eval(memberId))+');">';
    iconLabel+="<img border=0 src='"+path+"/icons/user_inactive.gif' align='absmiddle'></a>";
	tbl.rows[rows].cells[0].innerHTML = memberLabel + inputLabel;
	tbl.rows[rows].cells[1].innerHTML = iconLabel;
	return true;
}

function deleteMember(id){
	var tbl = document.getElementById('tblMember');
	var txt = document.getElementsByName('memberIds');
	var drow;
	for(i=txt.length-1;i>=0;i--){
		if(id==txt[i].value)
		{
			drow = tbl.rows[i];
			$(drow).remove();
		}
	}
}
function deleteMemberAll(){
	var tbl = document.getElementById('tblMember');
	var drow;
	for(i=tbl.rows.length-1;i>=0;i--){
		drow = tbl.rows[i];
		$(drow).remove();
	}
}

function importOrder(path){
	var tbl = document.getElementById('tblMember');
	//validate order Date
	if(Trim(document.getElementById('orderDate').value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementById('orderDate').focus();
		return false;
	}
	if(document.getElementById('importFile').value=='' && tbl.rows.length==0){
		alert('กรุณาระบุข้อมูลสมาชิกด้วยการนำเข้าไฟล์หรือเลือกสมาชิกที่ต้องการนำเข้า');
		return false;
	}
	document.memberOrderImportForm.action = path + "/jsp/memberOrderImportAction.do?do=importMemberOrder";
	document.memberOrderImportForm.submit();
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
				<jsp:param name="function" value="MemberOrderImport" />
				<jsp:param name="code" value=""/>
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
						<html:form action="/jsp/memberOrderImportAction" enctype="multipart/form-data">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="40%"></td>
							</tr>
							<tr>
								<td align="right">วันที่ในรายการสั่งซื้อ<font color="red">*</font></td>
								<td align="left" colspan="2"><html:text property="orderDate" styleId="orderDate" maxlength="10" size="15" readonly="true"/></td>
							</tr>
							<tr>
								<td align="right">นำเข้าจากไฟล์&nbsp;&nbsp;</td>
								<td align="left" colspan="2"><html:file styleId="importFile" property="importFile"/></td>
							</tr>
							<tr>
								<td align="right" valign="top">นำเข้าจากรหัสสมาชิก&nbsp;&nbsp;</td>
								<td align="left" width="305px;">
									<fieldset>
									<div style="border: 1px; height: 200px;width: 300px; overflow: auto;">
									<table id="tblMember" align="center" border="0" cellpadding="0" cellspacing="1" width="100%" class="result">
									<c:forEach var="results" items="${members}" varStatus="rows">
										<c:choose>
											<c:when test="${rows.index %2 == 0}">
												<c:set var="tabclass" value="lineO"/>
											</c:when>
											<c:otherwise>
												<c:set var="tabclass" value="lineE"/>
											</c:otherwise>
										</c:choose>
										<tr class="<c:out value='${tabclass}'/>">
											<td align="left">
												${results.code}&nbsp;${results.name}
												<input type="hidden" name='memberIds' value='${results.id}'>
											</td>
											<td align="center" width="5px;">
												<a href="#" onclick="deleteMember('${results.id}');">
												<img border=0 src='${pageContext.request.contextPath}/icons/user_inactive.gif' align='absmiddle'></a>
											</td>
										</tr>
									</c:forEach>
									</table>
									</div>
									</fieldset>
									<a href="#" onclick="deleteMemberAll();">
									<img src="${pageContext.request.contextPath}/icons/user_inactive.gif" align="absmiddle"/>&nbsp;&nbsp;ลบทั้งหมด</a>
								</td>
								<td align="left" valign="top">
									<input type="button" value="เลือกสมาชิก" onclick="javascript:showMember('${pageContext.request.contextPath}',0)">
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="#" onclick="importOrder('${pageContext.request.contextPath}');">
									<input type="button" value="นำเข้า" class="newPosBtn"></a>
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<input type="button" value="ยกเลิก" class="newNegBtn"></a>
								</td>
							</tr>
						</table>
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