<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%>
<%@page import="java.util.Date"%>
<%@page import="com.isecinc.pens.dao.ProdShowDAO"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.bean.Customer"%>
<%@page import="com.isecinc.pens.model.MOrder"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.inf.helper.DBConnection"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.web.prodshow.ProdShowForm"%>
<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.bean.ProdShowBean"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
Connection conn = null;
ProdShowBean  prodShowBean = null;
List<References> brandList = null;
User user = (User) request.getSession().getAttribute("user");

String action = Utils.isNull(request.getParameter("action"));
String orderNo = Utils.isNull(request.getParameter("orderNo"));

System.out.println("action:"+action);
System.out.println("orderNo:"+orderNo);

//Get NaxFileSizeUpload Config
String maxFileSizeUpload = "";
References refFileSize = InitialReferences.getReferenesByOne(InitialReferences.ProdShowFileSize, InitialReferences.ProdShowFileSize);
if( refFileSize != null){
   maxFileSizeUpload =refFileSize.getKey();
   System.out.println("maxFileSizeUpload(kb) :"+maxFileSizeUpload);
}
try{
	
if("new".equalsIgnoreCase(action)){
	 session.setAttribute("Message", "");
	 conn  = DBConnection.getInstance().getConnection();
	 
	 Order order = new MOrder().findByWhereCond(conn,"where order_no='"+orderNo+"'");
	 if(order!= null){
		 prodShowBean = new ProdShowBean();
		 prodShowBean.setOrderNo(orderNo);
		 prodShowBean.setOrderId(order.getId());
		 
		 //get Customer Detail
		 Customer cus = new MCustomer().findByWhereCond(conn, " where customer_id ="+order.getCustomerId());
		 if(cus != null){
			 prodShowBean.setCustomerCode(cus.getCode());
			 prodShowBean.setCustomerName(cus.getName());
		 }
		 //get ProdShow Data
		ProdShowBean prodShowBeanDB = ProdShowDAO.searchProdShow(conn,prodShowBean.getOrderNo(), true);
		 if(prodShowBeanDB !=null && !prodShowBeanDB.getDocDate().equals("")){
			 System.out.println("ProdShowDB orderNo:"+prodShowBeanDB.getOrderNo()+",Exist");
			 //set from db to show
			 prodShowBean.setDocDate(prodShowBeanDB.getDocDate());
			 prodShowBean.setRemark(prodShowBeanDB.getRemark());
			 prodShowBean.setExport(prodShowBeanDB.getExport());
			 //set Item
			 prodShowBean.setItems(prodShowBeanDB.getItems());
			 System.out.println("Items Size:"+prodShowBean.getItems().size());
		    if("Y".equalsIgnoreCase(prodShowBeanDB.getExport())){
		    	prodShowBean.setMode("view");
		    	prodShowBean.setCanSave(false);
		    }else{
		    	prodShowBean.setCanSave(true);
		    	prodShowBean.setMode("edit");
		    }
		 }else{
			 prodShowBean.setCanSave(true);
			 prodShowBean.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 prodShowBean.setMode("add"); 
		 }
	 }
		
	//List All Brand
	brandList = new MProductCategory().lookUpBrandAllListNew(user);
	List<References> brandAllList = new ArrayList<References>();
	References ref = new References("", "", "");
	brandAllList.add(ref);
	brandAllList.addAll(brandList);
	request.getSession().setAttribute("brandList", brandAllList); 
	
}else{
	System.out.println("session prodShowBean:"+session.getAttribute("prodShowBean"));
    prodShowBean  = (ProdShowBean)session.getAttribute("prodShowBean");
    System.out.println(" prodShowBean:"+session.getAttribute("prodShowBean"));
}

brandList = (List<References>)request.getSession().getAttribute("brandList");
//System.out.println("bean:"+prodShowBean.getOrderNo());

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>PENS Sales Application</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
function loadMe(){
	//Add Row blank case edit or add 
	<%if(prodShowBean.getMode().equalsIgnoreCase("add")
		|| prodShowBean.getMode().equalsIgnoreCase("edit")){%>
	  
	   addRowAction();
	<%}%>
}
function saveAction(path){
	var form = document.prodShowForm;
	//Validate row data is fillup one row
	if( !validateTableData()){return false;}
	
	if(confirm("ยืนยันบันทึกข้อมูล")){
	  form.action = path + "/jsp/prodShowServlet";
	  form.submit();
	  return true;
	}
	return false;
}
function validateTableData(){
	var r = true;
	var foundOneRow = false;
	var brand = document.getElementsByName("brand");
	var statusRow = document.getElementsByName("statusRow");
	
	var pic1 = document.getElementsByName("pic1");
	var status_pic1 = document.getElementsByName("status_pic1");
	var db_pic1 = document.getElementsByName("status_pic1");
	
	for(var i=0;i<brand.length;i++){
		if(brand[i].value != "" && statusRow[i].value != "CANCEL"){
			foundOneRow = true;
			//alert(pic1[i].value);
			var status_pic1 = $("#status_pic1_"+i);
			var db_pic1 = $("#db_pic1_"+i);
			if(pic1[i].value ==""){
				if(status_pic1.val() =="" && db_pic1.val() !=""){
					// fooun image in db and status_pic1 <> 'CLEAR'
				}else{
			      pic1[i].className = "lineError";
			      r = false;
				}
			}else{
				pic1[i].className = "enableText";
			}
		}
	}
	if(foundOneRow == false){
		alert("กรุณากรอกข้อมูลอย่างน้อย 1 รายการ");
		brand[0].className = "lineError";
		return false;
	}else{
		brand[0].className = "normalText";
	}
	if(r==false){
		alert("กรุณากรอกข้อมูลให้ถุกต้อง");
		return false;
	}
	return true;
}
function prepare(path,type,id){
	if(id!=null){
		document.prodShowForm.action = path + "/jsp/saleOrderAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.prodShowForm.action = path + "/jsp/saleOrderAction.do?do=prepare"+"&action="+type;
	}
	document.prodShowForm.submit();
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
	    "<td class='td_text_center' width='5%'> "+
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
	    "<td class='td_text' width='20%'> "+
	    "  <input type='input' name='brandName' id='brandName' size='25' readonly class='disableText'/> "+
	    "</td>"+
	    "<td class='td_text' width='20%' nowrap valign='bottom'> "+
	    "  <input type='hidden' name='status_pic1_"+rowId+"' id='status_pic1_"+rowId+"'/>"+
	    "  <input type='hidden' name='db_pic1_"+rowId+"' id='db_pic1_"+rowId+"' value=''/> "+
	    "  <img id='image_pic1_"+rowId+"' width='50' height='30'/> "+
	    "  <input type='file' name='pic1' id='pic1' style='width:150px' accept='image/*' onchange='validateFile(this,"+rowId+",1)'/> "+
	    "  <input type='button' value='Clear' name='del_image_pic1_"+rowId+"'"+
	    "   onclick='delImage(0,1,"+rowId+")' class='newPosBtnLong'/> "+
	    "</td>"+
	    "<td class='td_text' width='20%' nowrap valign='bottom'> "+
	    "  <input type='hidden' name='status_pic2_"+rowId+"' id='status_pic2_"+rowId+"'/>"+
	    "  <input type='hidden' name='db_pic2_"+rowId+"' id='db_pic2_"+rowId+"' value=''/> "+
	    "  <img id='image_pic2_"+rowId+"'  width='50' height='30'/> "+
	    "  <input type='file' name='pic2' id='pic2' style='width:150px' accept='image/*' onchange='validateFile(this,"+rowId+",2)'/> "+
	    "  <input type='button' value='Clear' name='del_image_pic2_"+rowId+"'"+
	    "   onclick='delImage(0,2,"+rowId+")' class='newPosBtnLong'/> "+
	    "</td>"+
	    "<td class='td_text' width='20%' nowrap valign='bottom'> "+
	    "  <input type='hidden' name='status_pic3_"+rowId+"' id='status_pic3_"+rowId+"'/>"+
	    "  <input type='hidden' name='db_pic3_"+rowId+"' id='db_pic3_"+rowId+"' value=''/> "+
	    "  <img id='image_pic3_"+rowId+"' width='50' height='30'/> "+
	    "  <input type='file' name='pic3' id='pic3' style='width:150px' accept='image/*' onchange='validateFile(this,"+rowId+",3)'/> "+
	    "  <input type='button' value='Clear' name='del_image_pic3_"+rowId+"'"+
	    "   onclick='delImage(0,3,"+rowId+")' class='newPosBtnLong'/> "+
	   
	    "</td>"+
	    "</tr>";

    $('#tblProduct').append(rowData);
    //set focus default
    var brand = document.getElementsByName("brand");
    brand[rowId].focus();
}
/** Validate Display image after upload **/
function validateFile(input,rowId,picNo) {
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
      if(fileSize > <%=maxFileSizeUpload%>){
    	  alert("ไม่สามารถอัพโหลดรูปภาพขนาดเกิน  <%=maxFileSizeUpload%> KB ได้ กรุณาเลือกรูปใหม่");
    	  input.value = "";
    	  return false;
      }else{
    	 //display image
    	  showImage(input,rowId,picNo);
      }
  }
}
function bytesToSize(bytes) {
	var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
   if (bytes == 0) return '0 Byte';
   var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
   return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
};

function showImage(input,rowId,picNo) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        var image = $("#image_pic" + picNo+"_"+rowId);
        //alert("#image_pic" + picNo+"_"+rowId +":"+imageBlah);
        
        reader.onload = function (e) {
        	image
                .attr('src', e.target.result)
                .width(50)
                .height(30);
        };
        reader.readAsDataURL(input.files[0]);
        
        //set statusImage to =''
        var imageStatus = $("#status_pic" + picNo+"_"+rowId);
        imageStatus.val("");
    }
}
function delImage(fileNameDB,picNo,rowId) {
	var imagePicStatus = $("#status_pic" + picNo+"_"+rowId);
	var imagePic = $("#image_pic" + picNo+"_"+rowId);
	
   //Case insert file new clear onscreen
    imagePic.attr('src', '');
   
    //clear imageShow and set status=CANCEL
    //Case Found DB mark status =CLEAR
    if(fileNameDB != "0"){
    	imagePicStatus.val("CLEAR");
    	//alert("delImage status_pic" + picNo+"_"+rowId+":"+imagePicStatus.val());
    }
}

function setBrandName(brandObj,rowId){
	if(checkDupItem(brandObj.value,rowId) == true){
		var brandName = document.getElementsByName("brandName");
		alert("ข้อมูล แบรนด์ ["+brandObj.value +"] ซ้ำ");
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
						<form name="prodShowForm" action="/jsp/prodShowServlet" enctype="multipart/form-data" method = "POST">
						<jsp:include page="../error.jsp"/>
                        <!-- head -->
                          <div align="center">
                          <% String modeMsg = "";
                          System.out.println("Message:"+request.getAttribute("Message"));
                          String message  = Utils.isNull(session.getAttribute("Message"));
						        /* if(prodShowBean.getMode().equalsIgnoreCase("add")){
						        	modeMsg = "เพิ่ม";
						        }else if(prodShowBean.getMode().equalsIgnoreCase("edit")){
						        	modeMsg ="แก้ไข";
						        }else{
						        	modeMsg ="แสดง";
						        } */
						    %>
						    <div align="center">
						     <font size="2" color="blue"> <%=message %></font>
						    </div>
					 	    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						      <tr>	 
						        <td align="center" colspan="4">
						         
						         <font size="3" ><b>
						          <%=modeMsg %>ข้อมูลกองโชว์</b></font>
						        </td>
							   </tr>
						       <tr>
                                    <td align="right">  วันที่ทำรายการกองโชว์	<font color="red"></font></td>
									<td>						   
									    <input type="text" name="docDate" Id="docDate" size="15" readonly 
									    Class="disableText" value="<%=prodShowBean.getDocDate()%>"/>
									</td>
									<td>	
									   เลขที่เอกสาร <font color="red"></font>&nbsp;
									<input type="text" name="orderNo" Id="orderNo" size="15" readonly 
									Class="disableText"  value="<%=prodShowBean.getOrderNo()%>"/>
									<input type="hidden" name="orderId" value ="<%=prodShowBean.getOrderId()%>"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">   รหัสร้านค้า<font color="red"></font>	</td>
									<td>	    
									  <input type="text" name="customerCode" Id="customerCode" size="15" 
									  readonly Class="disableText"  value="<%=prodShowBean.getCustomerCode()%>"/>
									</td>
									<td>		 
									   <input type="text" name="customerName" Id="customerName" size="40" 
									   readonly Class="disableText"  value="<%=prodShowBean.getCustomerName()%>"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">หมายเหตุ<font color="red"></font></td>
									<td colspan="2">	
									 <!--   <html:text property="bean.remark" styleId="remark" size="60" /> -->
									    <input type="text" name="remark" Id="remark" size="60"/>
									</td>
								</tr>
						   </table> 
				
                    <!-- table data -->
                  
                      <%if(prodShowBean.isCanSave()) { %>
	                        <table id="tblProductDel" align="center" border="0" cellpadding="3" cellspacing="2"  width="100%">
	                         <tr><td >
	                             <input type="button" name="add" value="  เพิ่มข้อมูล  " onclick="addRowAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
	                             <input type="button" name="delete" value="  ลบข้อมูล  " onclick="removeRowAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
	                           </td></tr>
	                        </table>
                       <%} %>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
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
							List<ProdShowBean> resultItemList = prodShowBean!=null? prodShowBean.getItems():null; 
							
							// Check disble text by view or verify
							boolean readonly = true;
							if(prodShowBean.isCanSave()){
								readonly = false;
							}
							
							if(resultItemList != null && resultItemList.size() >0){
								System.out.println("resultItemList size:"+resultItemList.size());
							for(int rowId=0;rowId<resultItemList.size();rowId++){
								ProdShowBean mc = (ProdShowBean)resultItemList.get(rowId);
								if(rowId%2==0){ 
									tabclass="lineO";
								}
								%> 
								<tr class="<%=tabclass%>"> 
								    <td class="td_text_center" width="5%" valign="bottom">
								       <input type="checkbox" name="lineChk" id="lineChk" value="<%=mc.getId()%>" >
								       <input type="hidden" name="statusRow" id="statusRow" value="" />
								       <input type="hidden" name="id" id="id" value="<%=mc.getId()%>" />
								       <input type="hidden" name="rowId" id="rowId" value="<%=rowId%>" />
								    </td>
									<td class="td_text_center" width="15%"  valign="bottom"> 
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
									 <td class="td_text" width="20%" valign="bottom">
								        <input type="text" name="brandName" id="brandName" size="25" value="<%=mc.getBrandName()%>" readonly class="disableText" />
                                    </td>
								    <td class="td_text" width="20%" valign="bottom"  nowrap>
								         <input type="hidden" name="status_pic1_<%=rowId %>" id="status_pic1_<%=rowId %>"/>
								         <input type="hidden" name="db_pic1_<%=rowId %>" id="db_pic1_<%=rowId %>" 
								                value="<%=mc.getInputFileNameDBPic1()%>"/> 
								         <%if( !Utils.isNull(mc.getInputFileNameDBPic1()).equals("")){ %>
									         <img id="image_pic1_<%=rowId%>" width="50" height="30"
									          src="${pageContext.request.contextPath}/photoServlet?pageName=prodShow&fileName=<%=mc.getInputFileNameDBPic1()%>" />
									     <%}else{ %>
									         <img id='image_pic1_<%=rowId%>' width="50" height="30"/>
									     <%} %>
									     <input type='file' name='pic1' id='pic1' style="width:150px" accept='image/*'
								           onchange='validateFile(this,<%=rowId%>,1)'/> 
								         <input type="button" value="Clear" name="del_image_pic1_<%=rowId%>"  class="newPosBtnLong"
								           onclick="delImage('<%=mc.getInputFileNameDBPic1()%>',1,<%=rowId%>)"/>
                                    </td>
                                    
                                    <td class="td_text" width="20%" valign="bottom"  nowrap>
								          <input type="hidden" name="status_pic2_<%=rowId %>" id="status_pic2_<%=rowId %>"/>
								          <input type="hidden" name="db_pic2_<%=rowId %>" id="db_pic2_<%=rowId %>" 
								                 value="<%=mc.getInputFileNameDBPic2()%>"/>
								        <%if( !Utils.isNull(mc.getInputFileNameDBPic2()).equals("")){ %>
									         <img id="image_pic2_<%=rowId%>" width="50" height="30" 
									          src="${pageContext.request.contextPath}/photoServlet?pageName=prodShow&fileName=<%=mc.getInputFileNameDBPic2()%>"/>
									     <%}else{%>
									         <img id='image_pic2_<%=rowId%>' width="50" height="30"/> 
									     <%} %>
									    <input type='file' name='pic2' id='pic2'  style="width:150px" accept='image/*'
								           onchange='validateFile(this,<%=rowId%>,2)'/> 
								        <input type="button" value="Clear" name="del_image_pic2_<%=rowId%>" class="newPosBtnLong"
								           onclick="delImage('<%=mc.getInputFileNameDBPic1()%>',2,<%=rowId%>)"/>
                                    </td>
                                    
                                    <td class="td_text" width="20%" valign="bottom" nowrap>
								         <input type="hidden" name="status_pic3_<%=rowId %>" id="status_pic3_<%=rowId %>"/>
								         <input type="hidden" name="db_pic3_<%=rowId%>" id="db_pic3_<%=rowId%>" 
								                value="<%=mc.getInputFileNameDBPic3()%>"/>
								        <%if( !Utils.isNull(mc.getInputFileNameDBPic3()).equals("")){ %>
									         <img id="image_pic3_<%=rowId%>" width="50" height="30"
									          src="${pageContext.request.contextPath}/photoServlet?pageName=prodShow&fileName=<%=mc.getInputFileNameDBPic3()%>"/>
									     <%}else{%>
									        <img id='image_pic3_<%=rowId%>' width="50" height="30"/> 
									      <%} %>
									    <input type='file' name='pic3' id='pic3'  style="width:150px"  accept='image/*'
								          onchange='validateFile(this,<%=rowId%>,3)'/>
								         <input type="button" value="Clear" name="del_image_pic3_<%=rowId%>" class="newPosBtnLong"
								          onclick="delImage('<%=mc.getInputFileNameDBPic3()%>',3,<%=rowId%>)"/>
                                    </td>
								</tr>
							<%} } %> 
					</table>
		        <!-- ************************Result ***************************************************-->
	            <table id="tblProductBtn" align="center" border="0" cellpadding="3" cellspacing="2"  width="65%">
                         <tr><td align="center">
                            <%if(prodShowBean.isCanSave()) { %>
                                <input type="button" name="saveBtn" value=" บันทึกข้อมูล  " onclick="saveAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>     
                            <%} %>
                            &nbsp;&nbsp;
                            <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','<%=prodShowBean.getOrderId()%>');">
                              <input type="button" name="backBtn" value="  ปิดหน้าจอ  "  class="newPosBtnLong"/>
                            </a>
                           </td></tr>
                   </table>
               </div>
               
					<!-- hidden field -->
					</form>
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
<%
}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn !=null){
		conn.close();conn=null;
	}
}
%>
</html>