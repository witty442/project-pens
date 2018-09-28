<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
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
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<%
  String action = request.getParameter("action");
  String referenceCode = Utils.isNull(request.getParameter("referenceCode"));
  String interfaceValue = Utils.isNull(request.getParameter("interfaceValue"));
  String interfaceDesc = new String(Utils.isNull(request.getParameter("interfaceDesc")).getBytes("ISO8859_1"), "UTF-8");
  String pensValue = new String(Utils.isNull(request.getParameter("pensValue")).getBytes("ISO8859_1"), "UTF-8");
  String pensDesc = new String(Utils.isNull(request.getParameter("pensDesc")).getBytes("ISO8859_1"), "UTF-8");
  String pensDesc2 = new String(Utils.isNull(request.getParameter("pensDesc2")).getBytes("ISO8859_1"), "UTF-8");
  String pensDesc3 = new String(Utils.isNull(request.getParameter("pensDesc3")).getBytes("ISO8859_1"), "UTF-8");
  String sequence = new String(Utils.isNull(request.getParameter("sequence")).getBytes("ISO8859_1"), "UTF-8");
  String status = new String(Utils.isNull(request.getParameter("status")).getBytes("ISO8859_1"), "UTF-8");
  
  User user = (User)session.getAttribute("user");
  String createUser = user.getUserName();
  
  //System.out.println("action:"+action);
  //System.out.println("referenceCodeList:"+session.getAttribute("referenceCodeList"));
%>
<script type="text/javascript">

function clearMaster(){
	var form = document.form1;
	form.interfaceValue.value="";
	form.interfaceDesc.value="";
	form.pensValue.value="";
	form.pensDesc.value="";
	form.pensDesc2.value="";
	form.pensDesc3.value="";
	form.sequence.value="";
	form.status.value="";
}

function saveMaster(action) {
	var returnString = "";
	var form = document.form1;
	var  referenceCode = $("#referenceCode option:selected").text();
	var interfaceValue = form.interfaceValue;
	var interfaceDesc = form.interfaceDesc;
	var pensValue = form.pensValue;
	var pensDesc = form.pensDesc;
	var pensDesc2 = form.pensDesc2;
	var pensDesc3 = form.pensDesc3;
	var sequence = form.sequence;
	var status = form.status;
	var createUser = '<%=createUser%>';
	
	
	if(interfaceValue.value ==""){
		//alert(" กรุณากรอก interface value ");
		//interfaceValue.focus();
		//return false;
	}
	
	/* if(interfaceDesc.value ==""){
		alert(" กรุณากรอก interfaceDesc ");
		interfaceDesc.focus();
		return false;
	} */
	if(pensValue.value ==""){
		//alert(" กรุณากรอก pensValue ");
		//pensValue.focus();
		//return false;
	}
	/* if(pensDesc.value ==""){
		alert(" กรุณากรอก pensDesc ");
		pensDesc.focus();
		return false;
	} */
	/* if(pensDesc2.value ==""){
		alert(" กรุณากรอก pensDesc2 ");
		pensDesc2.focus();
		return false;
	} */
	
	var param ="referenceCode="+referenceCode;
	    param +="&interfaceValue="+interfaceValue.value;
	    param +="&interfaceDesc="+interfaceDesc.value;
	    param +="&pensValue="+pensValue.value;
	    param +="&pensDesc="+pensDesc.value;
	    param +="&pensDesc2="+pensDesc2.value;
	    param +="&pensDesc3="+pensDesc3.value;
	    param +="&createUser="+createUser;
	    param +="&sequence="+sequence.value;
	    param +="&status="+status.value;
	    param +="&action="+action;
	
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/saveMasterAjax.jsp",
			data : encodeURI(param),
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	
	alert(returnString);
	document.getElementById("msg").innerHTML = returnString;
	clearMaster();
   return true;
}

</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" >

<form action="" method="post" name="form1">
	<input type="hidden" name="action" value="<%=action %>"/>
	<center><span id="msg" class="txt1"> </span></center>
	<table align="center" border="0" cellpadding="0" cellspacing="1"  width="100%" class="result" >
	    <tr height="21px" >
			<td width="30%" align="left" ><b>Reference Code</b> </td>
			<td width="70%" align="left">
			
			   <select id="referenceCode" name="referenceCode" <%="edit".equals(action)?"disabled":""%>>
			      <%if(session.getAttribute("referenceCodeList") !=null){
			    	  List<References> refList = (List<References>)session.getAttribute("referenceCodeList");
			    	  if(refList != null && refList.size()>0){
			    	  for(int i=0;i<refList.size();i++){
			    		  References ref =(References)refList.get(i);
			    		  String ss = referenceCode.equalsIgnoreCase(ref.getKey())?"selected":"";
			       %>
				      <option <%=ss %> value="<%=ref.getKey()%>"><%=ref.getKey() %></option>
				  <% }}}%>
				</select>
				
			</td>
		</tr>
		<tr height="21px" >
			<td width="30%" align="left" ><b>Interface Value</b>  </td>
			<td width="70%" align="left"><input type="text" name="interfaceValue"  size="40" value="<%=interfaceValue %>"/>
			</td>
		</tr>
		<tr height="21px" >
			<td width="30%" align="left" ><b>Interface Desc</b>  </td>
			<td width="70%" align="left"><textarea  name="interfaceDesc" rows="2" cols="40"><%=interfaceDesc %></textarea>
			</td>
		</tr>
		<tr height="21px">
			<td width="30%" align="left" ><b>Pens Value</b>  </td>
			<td width="70%" align="left"><input type="text" name="pensValue" size="40" value="<%=pensValue %>"/>
			</td>
		</tr>
		<tr height="21px" >
			<td width="30%" align="left" ><b>Pens Desc</b>  </td>
			<td width="70%" align="left"><textarea  name="pensDesc" rows="2" cols="40"><%=pensDesc %></textarea>
			</td>
		</tr>
		<tr height="21px" >
			<td width="30%" align="left" ><b>Pens Desc2</b>  </td>
			<td width="70%" align="left"><textarea  name="pensDesc2" rows="2" cols="40"><%=pensDesc2 %></textarea>
			</td>
		</tr>
		<tr height="21px" >
			<td width="30%" align="left" ><b>Pens Desc3</b>  </td>
			<td width="70%" align="left"><textarea  name="pensDesc3" rows="2" cols="40"><%=pensDesc3 %></textarea>
			</td>
		</tr>
		<tr height="21px" >
			<td width="30%" align="left" ><b>Sequence</b>  </td>
			<td width="70%" align="left"><input type="text" name="sequence" size="40" value="<%=sequence %>"/>
			</td>
		</tr>
		<tr height="21px" >
			<td width="30%" align="left" ><b>Status e.g('' or 'Active' ,'Inactive')</b>  </td>
			<td width="70%" align="left"><input type="text" name="status" size="40" value="<%=status %>"/>
			</td>
		</tr>
	</table>
<br/><br/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<%if("edit".equals(action)) {%>
			   <input type="button" name="edit" value="บันทึกแก้ไข" onclick="saveMaster('edit')" style="width:80px;"/>
			   <input type="button" name="delete" value="ลบข้อมูล" onclick="saveMaster('delete')" style="width:60px;"/>
			<%}else{ %>
			   <input type="button" name="save" value="บันทึก" onclick="saveMaster('add')" style="width:60px;"/>
		    <%} %>
			<input type="button" name="clear" value="Clear" onclick="clearMaster()" style="width:60px;"/>
			<input type="button" name="close" value="Close" onclick="javascript:window.close();" style="width:60px;"/>
		</td>
	</tr>
</table>

</form>
</body>
</html>