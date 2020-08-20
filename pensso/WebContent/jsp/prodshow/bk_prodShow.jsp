<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.ProdShowBean"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="prodShowForm" class="com.isecinc.pens.web.prodshow.ProdShowForm" scope="session" /> 

<%
User user = (User) request.getSession().getAttribute("user");
List<References> brandList = (List<References>)request.getSession().getAttribute("brandList");

System.out.println("bean:"+prodShowForm.getBean().getOrderNo());
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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
function loadMe(){
	//Add Row blank case edit or add 
	<%if(prodShowForm.getBean().getMode().equalsIgnoreCase("add")
		|| prodShowForm.getBean().getMode().equalsIgnoreCase("edit")){%>
	   addRowAction();
	   
	   new Epoch('epoch_popup', 'th', document.getElementById('docDate'));
	<%}%>
}
function saveAction(path){
	var form = document.prodShowForm;
	//Validate row data is fillup one row
	if( !validateTableData()){return false;}
	
	if(confirm("ยืนยันบันทึกข้อมูล")){
	  form.action = path + "/jsp/uploadServlet";
	  form.submit();
	  return true;
	}
	return false;
}
function validateTableData(){
	var r = true;
	var foundOneRow = false;
	var brand = document.getElementsByName("brand");
	var pic1 = document.getElementsByName("pic1");
	for(var i=0;i<brand.length;i++){
		if(brand[i].value != ""){
			foundOneRow = true;
			//alert(pic1[i].value);
			if(pic1[i].value ==""){
			   pic1[i].className = "lineError";
			   r = false;
			}else{
				pic1[i].className = "enableText";
			}
		}
	}
	if(foundOneRow == false){
		alert("กรุณากรอกข้อมูลอย่างน้อย 1 รายการ");
		return false;
	}
	if(r==false){
		alert("กรุณากรอกข้อมูลให้ถุกต้อง");
		return false;
	}
	return true;
}

function backAction(path){
	var form = document.toolManageForm;
	//form.action = path + "/jsp/prodShowAction.do?do=searchHead&action=back";
	//form.submit();
	return true;
}

function addRowAction(){
	var rows = $('#tblProduct tr').length-1;
	var rowId = rows;
	//alert("rowId["+rowId+"]");
	var tabclass='lineE';
	if(rowId%2==0){ 
		tabclass="lineO";
	}
	var rowData ="<tr class='"+tabclass+"'>"+
	    "<td class='td_text_center' width='10%'> "+
	     " <input type='checkbox' name='lineChk'>"+
	     " <input type='hidden' name='statusRow' id='statusRow' /> "+
	     " <input type='hidden' name='id' id='id' value='0' /> "+
	     " <input type='hidden' name='rowId' id='rowId' value='"+rowId+"' /> "+
	    "</td>"+
	    "<td class='td_text_center' width='15%'> "+
	    "<select name='brand' id='brand' onchange='setBrandName(this,"+rowId+")'> "+
	     <%for(int i=0;i<brandList.size();i++){  
	    	 References ref = brandList.get(i);
	     %>
	    "   <option value='<%=ref.getCode()%>'><%=ref.getCode()+"-"+ref.getName()%></option> "+
	     <%}%>
	    " </select>"+
	    "</td>"+
	    "<td class='td_text' width='15%'> "+
	    "  <input type='input' name='brandName' id='brandName' size='20' readonly class='disableText'/> "+
	    "</td>"+
	    "<td class='td_text' width='10%'> "+
	    "  <input type='file' name='pic1' id='pic1' size='20' accept='image/*' onchange='validateFile(this)'/> "+
	    "</td>"+
	    "<td class='td_text' width='10%'> "+
	    "  <input type='file' name='pic2' id='pic2' size='20' accept='image/*' onchange='validateFile(this)'/> "+
	    "</td>"+
	    "<td class='td_text' width='10%'> "+
	    "  <input type='file' name='pic3' id='pic3' size='20' accept='image/*' onchange='validateFile(this)'/> "+
	    "</td>"+
	    "</tr>";

    $('#tblProduct').append(rowData);
    //set focus default
    var brand = document.getElementsByName("brand");
    brand[rowId].focus();
}
/** Display image after upload **/
function validateFile(input) {
  var file;
  var fileSize =0;
  //validate file type
   var extension = '';
    if(input.value != '' && input.value.indexOf('.') > 0){
       extension = input.value.substring(input.value.lastIndexOf('.') + 1).toLowerCase();
    }
    if(input.value != '' && (extension == 'png' 
    || extension == 'jpeg') || extension == 'jpg' || extension == 'bmp'  ){
    }else{
       alert('กรุณาเลือกไฟล์รูปภาพเท่านั้น นามสกุล  png ,jpeg ,jpg ,bmp เท่านั้น');
       input.value = "";
       return false;
    }
  
  //validate fileSize
  if (input.files && input.files[0]) {
	  var bytes = input.files[0].size;
	  if (bytes == 0) return '0 Byte';
	  //convert bytes to kb
	  fileSize =  Math.round(bytes / Math.pow(1024, 1), 2) ;
     // alert(fileSize);
      if(fileSize > 200){
    	  alert("ไม่สามารถอัพโหลดรูปภาพขนาดเกิน 200 KB ได้ กรุณาเลือกรูปใหม่");
    	  input.value = "";
    	  return false;
      }else{
    	  //upload file to temp
    	  
      }
  }
}
function bytesToSize(bytes) {
	var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
   if (bytes == 0) return '0 Byte';
   var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
   return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
};
function setImageVisible(id, visible) {
	var img = document.getElementById(id);
	img.style.visibility = (visible ? 'visible' : 'hidden');
}

function setBrandName(brandObj,rowId){
	if(checkDupItem(brandObj.value,rowId) == true){
		var brandName = document.getElementsByName("brandName");
		alert("ข้อมูล แบรนด์ ["+itemObj.value +"] ซ้ำ");
		brandObj.value = "";
		brandName[rowId].value = '';
		return false;
	}else{
		 var brandName = document.getElementsByName("brandName");
		var brandTemp = brandObj.options[brandObj.selectedIndex].text;
		var brandTempArr = brandTemp.split("-");
		brandName[rowId].value = brandTempArr[1];
		return true; 
	}
}

 function removeRowAction(path){
	if(confirm("ยืนยันลบข้อมูล")){
		var tbl = document.getElementById('tblProduct');
		var chk = document.getElementsByName("lineChk");
		//alert(chk.length);
		var drow;
		var bcheck=false;
		for(var i=chk.length-1;i>=0;i--){
			if(chk[i].checked){
				//alert(chk[i].checked);
				drow = tbl.rows[i+1];
				//$(drow).remove();
				bcheck=true;
				//hide row table
				removeRowByIndex(path,drow,i);
			}
		}
		if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}else{sumTotal();}
	}
} 
 function removeRowByIndex(path,drow,index){
    //alert(drow);
	//todo play with type	
	drow.style.display ='none';
	//set  staus = CANCEL 
	//alert(index);
	document.getElementsByName("statusRow")[index].value ='CANCEL';
}
 
 function checkDupItem(brandNew,curRowId){
	var brand = document.getElementsByName("brand");
	var statusRow = document.getElementsByName("statusRow");
	for(var i=0;i<brand.length;i++){
		if(statusRow[i].value !='CANCEL' && i != curRowId && brandNew==brand[i].value){
			return true;
		}
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
				<jsp:param name="function" value="ProdShow"/>
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
						<html:form action="/jsp/prodShowAction" enctype="multipart/form-data">
						<jsp:include page="../error.jsp"/>
                        <!-- head -->
                          <div align="center">
                          <% String modeMsg = "";
						        if(prodShowForm.getBean().getMode().equalsIgnoreCase("add")){
						        	modeMsg = "เพิ่ม";
						        }else if(prodShowForm.getBean().getMode().equalsIgnoreCase("edit")){
						        	modeMsg ="แก้ไข";
						        }else{
						        	modeMsg ="แสดง";
						        }
						    %>
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						      <tr>	 
						        <td align="center" colspan="4"><font size="3" color="blue"><b>
						          <%=modeMsg %>ข้อมูลกองโชว์</b></font>
						        </td>
							   </tr>
						       <tr>
                                    <td align="right">  วันที่ทำรายการกองโชว์	<font color="red"></font></td>
									<td>	
									    <html:text property="bean.docDate" styleId="docDate" size="15" readonly="true" styleClass="disableText"/>	
									</td>
									<td>	
									   เลขที่เอกสาร <font color="red"></font>&nbsp;
                                      <html:text property="bean.orderNo" styleId="orderNo" size="15" readonly="true" styleClass="disableText" />	                                             
									</td>
								</tr>
								<tr>
                                    <td align="right">   รหัสร้านค้า<font color="red"></font>	</td>
									<td>	
                                       <html:text property="bean.customerCode" styleId="customerCode" size="15" readonly="true" styleClass="disableText"/>   
									</td>
									<td>
									  <html:text property="bean.customerName" styleId="customerName" size="40"  styleClass="disableText" readonly="true"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">หมายเหตุ<font color="red"></font></td>
									<td colspan="2">	
									   <html:text property="bean.remark" styleId="remark" size="60" />
									</td>
								</tr>
						   </table>
				
                    <!-- table data -->
                  
                      <%if(prodShowForm.getBean().isCanSave()) { %>
	                        <table id="tblProductDel" align="center" border="0" cellpadding="3" cellspacing="2"  width="80%">
	                         <tr><td >
	                             <input type="button" name="add" value="  เพิ่มข้อมูล  " onclick="addRowAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
	                             <input type="button" name="delete" value="  ลบข้อมูล  " onclick="removeRowAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
	                           </td></tr>
	                        </table>
                       <%} %>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="80%">
						       <tr>
						            <th >Select</th> 
						            <th >แบรนด์</th>
						            <th >ชื่อแบรนด์</th>
									<th >รูป 1</th>
									<th >รูป 2</th>
									<th >รูป 3</th>
							   </tr>
							<% 
							String selected = "";
							String tabclass ="lineE";
							List<ProdShowBean> resultList = prodShowForm.getBean().getItems(); 
							// Check disble text by view or verify
							boolean readonly = true;
							if(prodShowForm.getBean().isCanSave()){
								readonly = false;
							}
							
							if(resultList != null && resultList.size() >0){
							for(int n=0;n<resultList.size();n++){
								ProdShowBean mc = (ProdShowBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								%> 
								<tr class="<%=tabclass%>"> 
								    <td class="td_text_center" width="10%">
								       <input type="checkbox" name="lineChk" id="lineChk" value="<%=mc.getId()%>" >
								       <input type="hidden" name="statusRow" id="statusRow" value="" />
								       <input type="hidden" name="id" id="id" value="<%=mc.getId()%>" />
								       <input type="hidden" name="rowId" id="rowId" value="<%=n%>" />
								    </td>
									<td class="td_text_center" width="15%"> 
								       <select name='brand' id='brand' onchange='setBrandName(this,"+rowId+")'> 
									     <%for(int i=0;i<brandList.size();i++){
									    	selected =mc.getBrand().equals(brandList.get(i).getCode())?"selected":"";
									      %>
									       <option value='<%=brandList.get(i).getCode()%>' <%=selected%>>
									          <%=brandList.get(i).getCode()+"-"+brandList.get(i).getName()%>
									       </option> 
									     <%}%>
									   </select>
									</td>
									 <td class="td_number" width="15%">
								        <input type="text" name="brandName" id="brandName" value="<%=mc.getBrandName()%>" readonly class="disableText" />
                                    </td>
								    <td class="td_number" width="10%">
								       
                                    </td>
                                    <td class="td_number" width="10%">
								       
                                    </td>
                                    <td class="td_number" width="10%">
								       
                                    </td>
								</tr>
							<%} } %> 
					</table>
					
		        <!-- ************************Result ***************************************************-->
	            <table id="tblProductBtn" align="center" border="0" cellpadding="3" cellspacing="2"  width="65%">
                         <tr><td align="center">
                            <%if(prodShowForm.getBean().isCanSave()) { %>
                                <input type="button" name="saveBtn" value=" บันทึกข้อมูล  " onclick="saveAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>     
                            <%} %>
                            &nbsp;&nbsp;
                            <input type="button" name="backBtn" value="  ปิดหน้าจอ  " onclick="backAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                           </td></tr>
                   </table>
               </div>
               
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