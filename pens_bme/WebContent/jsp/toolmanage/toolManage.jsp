<%@page import="com.isecinc.pens.web.toolmanage.ToolManageBean"%>
<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="toolManageForm" class="com.isecinc.pens.web.toolmanage.ToolManageForm" scope="session" /> 

<% 
User user = (User) request.getSession().getAttribute("user");
List<ToolManageBean> itemList = (List<ToolManageBean>)request.getSession().getAttribute("itemList");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" /><link rel="StyleSheet" href="${pageContext.request.contextPath}/css/field_size.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>

<script type="text/javascript">
function loadMe(){
	//Add Row blank case edit or add
	<%if(toolManageForm.getBean().getMode().equalsIgnoreCase("add")
		|| toolManageForm.getBean().getMode().equalsIgnoreCase("edit")){%>
	   addRowAction();
	   
	   new Epoch('epoch_popup', 'th', document.getElementById('docDate'));
	<%}%>
}

function saveAction(path){
	var form = document.toolManageForm;
	if($('#docDate').val()==""){
		alert("กรูณาระบุวันที่");
		$('#docDate').focus();
		return false;
	}
	if($('#custGroup').val()==""){
		alert("กรูณาระบุกลุ่มร้านค้า");
		$('#custGroup').focus();
		return false;
	}
	if($('#storeCode').val()==""){
		alert("กรูณาระบุสาขา");
		$('#storeCode').focus();
		return false;
	}
	//Validate row data is fillup one row
	if( !validateTableData()){return false;}
	
	if(confirm("ยืนยันบันทึกข้อมูล")){
	  form.action = path + "/jsp/toolManageAction.do?do=save";
	  form.submit();
	  return true;
	}
	return false;
}

function validateTableData(){
	var r = true;
	var foundOneRow = false;
	var item = document.getElementsByName("item");
	var qty = document.getElementsByName("qty");
	for(var i=0;i<item.length;i++){
		if(item[i].value != ""){
			foundOneRow = true;
			//alert(qty[i].value);
			if(qty[i].value ==""){
			   qty[i].className = "lineError";
			   r = false;
			}else{
			   qty[i].className = "enableNumber";
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

function postAction(path){
	var form = document.toolManageForm;
	if(confirm("ยืนยัน Post")){
	  form.action = path + "/jsp/toolManageAction.do?do=postAction";
	  form.submit();
	  return true;
	}
	return false;
}
function clearAction(path){
	var form = document.toolManageForm;
	form.action = path + "/jsp/toolManageAction.do?do=prepare&action=add";
	form.submit();
	return true;
}
function backAction(path){
	var form = document.toolManageForm;
	form.action = path + "/jsp/toolManageAction.do?do=searchHead&action=back";
	form.submit();
	return true;
}

function addRowAction(){
	// $('#myTable tr').length;
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
	    //"  <input type='text' name='item' id='item' value='' size='10' "+
	    //"    onkeypress = 'getProductKeypress(event,this,"+rowId+")'" +
	    // "  /> "+
	    "<select name='item' id='item' onchange='setItemName(this,"+rowId+")'> "+
	     <%for(int i=0;i<itemList.size();i++){     
	     %>
	    "   <option value='<%=itemList.get(i).getItem()%>'><%=itemList.get(i).getItem()+"-"+itemList.get(i).getItemName()%></option> "+
	     <%}%>
	    " </select>"+
	    "</td>"+
	    "<td class='td_text' width='35%'> "+
	    "  <input type='text' name='itemName' id='itemName'  value='' size='60' readonly class='disableText'/> "+
	    "</td>"+
	    "<td class='td_number' width='20%'> "+
	    "  <input type='text' name='qty' id='qty'  value='' size='14' class='enableNumber' onblur='isNum(this);sumTotalInRow("+rowId+")'/> "+
	    "</td>"+
	    "</tr>";

    $('#tblProduct').append(rowData);
    //set focus default
    //var item = document.getElementsByName("item");
    //item[rowId].focus();
}
function setItemName(itemObj,rowId){
	if(checkDupItem(itemObj.value,rowId) == true){
		var itemName = document.getElementsByName("itemName");
		var qty = document.getElementsByName("qty");
		
		alert("ข้อมูล รหัส Item ["+itemObj.value +"] ซ้ำ");
		itemObj.value = "";
		itemName[rowId].value = '';
		qty[rowId].value = '';
		return false;
	}else{
		var itemName = document.getElementsByName("itemName");
		var itemTemp = itemObj.options[itemObj.selectedIndex].text;
		var itemTempArr = itemTemp.split("-");
		itemName[rowId].value = itemTempArr[1];
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
 
 function checkDupItem(itemNew,curRowId){
	var item = document.getElementsByName("item");
	var statusRow = document.getElementsByName("statusRow");
	for(var i=0;i<item.length;i++){
		if(statusRow[i].value !='CANCEL' && i != curRowId && itemNew==item[i].value){
			return true;
		}
	}
	return false;
 } 
 
 function getProductKeypress(e,itemObj,rowId){
		//materialMaster groupCode pensItem wholePriceBF retailPriceBF
		//alert(barcode.value);
		
		var item = document.getElementsByName("item");
		var itemName = document.getElementsByName("itemName");
		var qty = document.getElementsByName("qty");
		
		if(e != null && e.keyCode == 13){
			if(itemObj.value ==''){
				itemName[rowId].value = '';
				qty[rowId].value = '';
			}else{
				//check dup in page
				if(checkDupItem(itemObj.value,rowId) == true){
					alert("ข้อมูล รหัส Item ["+itemObj.value +"] ซ้ำ");
					itemObj.value = "";
					itemName[rowId].value = '';
					qty[rowId].value = '';
					return false;
				}
				
				var found = getProductModel(itemObj,rowId);
				if(found){
					//Set Prev row readonly 
					item[rowId].className ="disableText";
					item[rowId].readOnly = true;
					qty[rowId].focus();
				}
			}
		}
	}

	function getProductModel(itemObj,rowId){
		var found = false;
		var item = document.getElementsByName("item");
		var itemName = document.getElementsByName("itemName");
		
		var returnString = "";
		var form = document.barcodeForm;
		var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/toolmanage/getItemToolManageAjax.jsp",
				data : "item=" + itemObj.value,
				async: false,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		   // alert("x:"+returnString);
			if(returnString==''){
				alert("ไม่พบข้อมูลสินค้า  "+itemObj.value);
				itemObj.focus();
				item[rowId].value = '';
				itemName[rowId].value = '';
			}else{
				itemName[rowId].value = returnString;
				found = true;
			}
		return found;
}
	function openPopup(path,pageName,multipleCheck){
		var form = document.toolManageForm;
		var param = "&pageName="+pageName;
		if("StoreCodeBME" == pageName){
	        param +="&groupStore="+form.custGroup.value;
	        param +="&multipleCheck="+multipleCheck;
		}
		url = path + "/jsp/popupSearchAction.do?do=prepare&action=new"+param;
		PopupCenterFullHeight(url,"",600);
	}
	function setDataPopupValue(code,desc,pageName){
		var form = document.toolManageForm;
		if("StoreCodeBME" == pageName){
			form.storeCode.value = code;
			form.storeName.value = desc;
		}
	} 
	function getCustNameKeypress(path,e,storeCode,fieldName){
		var form = document.toolManageForm;
		var storeType = form.custGroup.value;
		if(e != null && e.keyCode == 13){
			if(form.custGroup.value ==''){
				alert("กรณาระบุ กลุ่มร้านค้า");
				form.storeCode.value = '';
				form.storeName.value = '';
				form.custGroup.focus();
				return false;
			}
			if(form.custGroup.value =='' && storeCode.value ==''){
				if("storeName" == fieldName){
					form.storeName.value = '';
				}	
			}else{
			   getCustName(path,storeCode,fieldName,storeType);
			}
		}
	}
	function getCustName(path,storeCode,fieldName,storeType){
		var returnString = "";
		var form = document.toolManageForm;
		var getData = $.ajax({
				url: path+"/jsp/ajax/getCustNameAjax.jsp",
				data : "custCode=" + storeCode.value+"&storeType="+storeType,
				async: false,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		
		if("storeName" == fieldName){
			if(returnString != ''){
			   form.storeName.value = returnString;
			}else{
				storeCode.value ='';
				storeCode.focus();
				alert("ไม่พบข้อมูล");
			}
		}
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
	      	 <%if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageOut")){ %>
		        <jsp:include page="../program.jsp">
					<jsp:param name="function" value="ToolManageOut"/>
				</jsp:include>
	        <%} else if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageIn")){ %>
		        <jsp:include page="../program.jsp">
					<jsp:param name="function" value="ToolManageIn"/>
				</jsp:include>
	        <%} else if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageReport")){ %>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="ToolManageReport"/>
				</jsp:include>
		    <%} %>
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
						<html:form action="/jsp/toolManageAction">
						<jsp:include page="../error.jsp"/>
                        <!-- head -->
                          <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						      <tr>	 
						        <% String modeMsg = "";
						        if(toolManageForm.getBean().getMode().equalsIgnoreCase("add")){
						        	modeMsg = "เพิ่ม";
						        }else if(toolManageForm.getBean().getMode().equalsIgnoreCase("edit")){
						        	modeMsg ="แก้ไข";
						        }else{
						        	modeMsg ="แสดง";
						        }
						        if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageOut")){ %>
                                    <td align="center" colspan="4"><font size="3" color="red"><b>
                                      <%=modeMsg %>รายการเบิกอุปกรณ์และของพรีเมี่ยม</b></font>
                                    </td>
							     <%}else if(toolManageForm.getBean().getPageName().equalsIgnoreCase("ToolManageIn")){ %>
							        <td align="center" colspan="4"><font size="3" color="blue"><b>
							          <%=modeMsg %>รายการคืนอุปกรณ์และของพรีเมี่ยม</b></font>
							        </td>
							     <%} %>
							   </tr>
						       <tr>
                                    <td align="right">  วันที่	<font color="red">*</font></td>
									<td>	
									    <html:text property="bean.docDate" styleId="docDate" size="15" readonly="true"/>	
									</td>
									<td>&nbsp;&nbsp;		
									   เลขที่เอกสาร <font color="red"></font>
                                      <html:text property="bean.docNo" styleId="docNo" size="15" readonly="true" styleClass="disableText" />	
                                      &nbsp;&nbsp;
                                                                                              อ้างอิง Authorize Return no
                                        <html:text property="bean.refRtn" styleId="refRtn" size="15" />	                                               
									</td>
								</tr>
								<tr>
                                    <td align="right">  กลุ่มร้านค้า<font color="red">*</font>	</td>
									<td>	
									    <html:select property="bean.custGroup" styleId="custGroup">
											<html:options collection="custGroupList" property="pensValue" labelProperty="pensDesc"/>
									    </html:select>	
									</td>
									<td>&nbsp;&nbsp;		
									   รหัสร้านค้า<font color="red">*</font>
                                       <html:text property="bean.storeCode" styleId="storeCode" size="15"  onkeypress="getCustNameKeypress('${pageContext.request.contextPath}',event,this,'storeName')"/>
								       <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','StoreCodeBME','true')"/>   
									  <html:text property="bean.storeName" styleId="storeName" size="30"  styleClass="disableText" readonly="true"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">หมายเหตุ<font color="red"></font></td>
									<td colspan="2">	
									   <html:text property="bean.remark" styleId="remark" size="60" />
										 &nbsp;&nbsp;
									          สถานะ &nbsp;&nbsp;
									   <html:text property="bean.status" styleId="status" size="15"  styleClass="disableText" readonly="true"/>
									</td>
								</tr>
						   </table>
				
                    <!-- table data -->
                  
                      <%if(toolManageForm.getBean().isCanSave()) { %>
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
						            <th >รหัสอุปกรณ์</th>
						            <th >ชื่ออุปกรณ์</th>
									<th >จำนวน</th>
							   </tr>
							<% 
							String selected = "";
							String tabclass ="lineE";
							List<ToolManageBean> resultList = toolManageForm.getBean().getItems();
							// Check disble text by view or verify
							boolean readonly = true;
							if(toolManageForm.getBean().isCanSave()){
								readonly = false;
							}
							
							if(resultList != null && resultList.size() >0){
							for(int n=0;n<resultList.size();n++){
								ToolManageBean mc = (ToolManageBean)resultList.get(n);
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
								       <select name='item' id='item' onchange='setItemName(this,"+rowId+")'> 
									     <%for(int i=0;i<itemList.size();i++){
									    	selected =mc.getItem().equals(itemList.get(i).getItem())?"selected":"";
									      %>
									       <option value='<%=itemList.get(i).getItem()%>' <%=selected%>>
									          <%=itemList.get(i).getItem()+"-"+itemList.get(i).getItemName()%>
									       </option> 
									     <%}%>
									   </select>
									</td>
									<td class="td_text" width="35%">
									    <input type="text" name="itemName" id="itemName"  value="<%=mc.getItemName() %>" size="60" readonly class='disableText'/>
									</td>
								    <td class="td_number" width="20%">
								        <input type="text" name="qty" id="qty"  value="<%=mc.getQty() %>" size="14" 
								         <%if(readonly){ %>
								             readonly class="disableNumber" 
								         <%}else{ %> 
								             class="enableNumber" 
								             onblur="isNum(this);"
								          <%} %>
								          />
                                    </td>
								</tr>
							<%} } %> 
					</table>
					
		        <!-- ************************Result ***************************************************-->
	            <table id="tblProductBtn" align="center" border="0" cellpadding="3" cellspacing="2"  width="65%">
                         <tr><td align="center">
                            <%if(toolManageForm.getBean().isCanSave()) { %>
                                <input type="button" name="saveBtn" value=" บันทึกข้อมูล  " onclick="saveAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                                &nbsp;&nbsp;
                               <input type="button" name="backBtn" value="   CLEAR   " onclick="clearAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                            <%} %>
                            &nbsp;&nbsp;
                            <input type="button" name="backBtn" value="  ปิดหน้าจอ  " onclick="backAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                           
                            <%if(toolManageForm.getBean().isCanSave()) { %>
                              &nbsp;&nbsp;
                                <input type="button" name="posyBtn" value="   POST   " onclick="postAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                            <% }%>
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