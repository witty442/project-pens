<%@page import="com.isecinc.pens.bean.TransferBean"%>
<%@page import="util.SessionGen"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="transferForm" class="com.isecinc.pens.web.transfer.TransferForm" scope="session" />
<%
 List<References> transferBankVanList = (List<References>)session.getAttribute("TRANSFER_BANK_VAN");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript">

function loadMe(path){
	addRow(path)
    sumTotal();
}
function backsearch(path) {
	document.transferForm.action = path + "/jsp/transferAction.do?do=prepareSearch"+"&action=back";
	document.transferForm.submit();
}
function clearForm(path) {
	document.transferForm.action = path + "/jsp/transferAction.do?do=prepare"+"&action=new";//clearForm
	document.transferForm.submit();
}
function save(path,moveOrderType){
	if(checkTableCanSave()){
		/** lock screen save **/
		startControlSaveLockScreen();
		
		document.transferForm.action = path + "/jsp/transferAction.do?do=save";
		document.transferForm.submit();
		return true;
	}
	return false;
}

function checkTableCanSave(){
	var transferType = document.getElementsByName("transferType");
	var transferBank = document.getElementsByName("transferBank");
	var transferTime = document.getElementsByName("transferTime");
	var transferDate= document.getElementsByName("transferDate");
	
	var amount = document.getElementsByName("amount");
	var chequeNo = document.getElementsByName("chequeNo");
	var chequeDate= document.getElementsByName("chequeDate");
	
	var status = document.getElementsByName("keyData");
	var r = true;
	var rowSelectOne = false;
	
	for(var i=0;i<transferType.length;i++){
	 // alert(transferType[i].value+":"+status[i].value);
	 
	  if(transferType[i].value =='' && status[i].value !='DELETE'){
		  //alert("check");
		  if(transferType[i].value =='' && transferBank[i].value =='' 
				  && transferTime[i].value =='' && transferDate[i].value ==''){
			  if(rowSelectOne==false){
				  alert("��س��кآ����� ���ҧ���� 1 ��");
				  transferType[i].focus();
				  transferType[i].className ='errorNumber';
				  transferBank[i].className ='errorNumber';
				  transferTime[i].className ='errorNumber';
				  transferDate[i].className ='errorNumber';
				  r = false;
			  }
		   }
	   }else if(transferType[i].value !='' && status[i].value !='DELETE'){
		      transferType[i].className ='enableNumber';
		      transferBank[i].className ='enableNumber';
		      transferTime[i].className ='enableNumber';
		      transferDate[i].className ='enableNumber';
			  
			  rowSelectOne = true;
			  
			  if(transferBank[i].value ==''){
				  transferBank[i].className ='errorNumber';
				  r = false;
			  }else{
				  transferBank[i].className ='normalText';
			  }
			  if(transferTime[i].value ==''){
				  transferTime[i].className ='errorNumber';
				  r = false;
			  }else{
				  transferTime[i].className ='normalText';
				  if( !validateInputTime(transferTime[i])){
					  r = false; 
				  }
			  }
			  if(transferDate[i].value ==''){
				  transferDate[i].className ='errorNumber';
				  r = false;
			  }else{
				  transferDate[i].className ='normalText';
			  }
			  if(amount[i].value ==''){
				  amount[i].className ='errorNumber';
				  r = false;
			  }else{
				  amount[i].className ='normalText';
			  }
			  
			  //case cheque 
			  if(transferType[i].value =='CH'){
				  if(chequeNo[i].value ==''){
					  chequeNo[i].className ='errorNumber';
					  r = false;
				  }else{
					  chequeNo[i].className ='normalText';
				  }
				  if(chequeDate[i].value ==''){
					  chequeDate[i].className ='errorNumber';
					  r = false;
				  }else{
					  chequeDate[i].className ='normalText';
				  }
			  }
	   }//if
	}//for
	
	if(r==false){
		alert("��س��кآ����� ���ú��ǹ");
	 }
	
	return r;
}

function popCalendar(thisObj, thisEvent) {
	new Epoch('epoch_popup', 'th', thisObj);
}
function inputTimeNew(e, objText){
	var keynum = e.keyCode;
	//alert(keynum);
	//backspace (8) ,delete(46) no action
	if(keynum == 8 || keynum == 46){
		return true;
	}
 	if(inputNum2(e)){
		if((objText.value.length==0 && keynum!=8) || keynum==37 || keynum==39){
			if(keynum > 50){return false;}	
		}
		if((objText.value.length==2 && keynum!=8) || keynum==37 || keynum==39){
			var hour = objText.value.substring(0, 2);
			if(eval(hour) > 23){
				objText.value = objText.value.substring(0, objText.value.length-1);
				return false;
			}
		}
		if(objText.value.length==2 && objText.value.indexOf(":") == -1){
			objText.value += ':';
		}
		if((objText.value.length==5 && keynum!=8) || keynum==37 || keynum==39){
			var minute = objText.value.substring(3,5);
			if(eval(minute) > 59){
				objText.value = objText.value.substring(0, objText.value.length-1);
				return false;
			}
		}
		
		return true;
	}else{
		return false;
	}
}
function inputTimeM(e, objText){
	inputTimeNew(e,objText);
	
	/* if( !inputNum2(e,objText) ){
		alert("�кآ����������繵���Ţ��ҹ��");
		objText.value="";
		//alert(objText.value);
		return false;
	}else{
		return inputTimeNew(e,objText);
	} */
}
function validateInputTime(inputField) {
	var isValid = /^([0-1]?[0-9]|2[0-3]):([0-5][0-9])(:[0-5][0-9])?$/.test(inputField.value);
	
	if (!isValid) {
    	alert("��͡������ �ٻẺ�������١��ͧ ������ҧ 08:30 ,23:59 ");
        inputField.style.backgroundColor = '#fba';
        inputField.focus();
    
    }else{
    	inputField.style.backgroundColor = 'white';
    	
    }
    //alert(isValid);
    return isValid;
}

function addRow(){
	// $('#myTable tr').length;
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
	     " <input type='hidden' name='keyData' id='keyData' /> "+
	     " <input type='hidden' name='rowId' id='rowId' value='"+rowId+"' /> "+
	     " <input type='hidden' name='row_can_edit' id='row_can_edit' value='true' /> "+
	     " <input type='hidden' name='lineId' id='lineId' value='' /> "+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "<select name='transferType' id='transferType'>"+
	      "<option value=''></option>"+
	      "<option value='CS'>�Թʴ</option>"+
	      "<option value='CH'>��</option>"+
	   "</select>"+
	    "</td>"+
	    "<td class='td_text' width='35%'> "+
	    "<select name='transferBank' id='transferBank' style='width: 300px;'>"+
	    "<option value=''></option>"+
	    <%if(transferBankVanList != null && transferBankVanList.size()>0){
	  	  for(int i=0;i<transferBankVanList.size();i++){
	  		  References ref = transferBankVanList.get(i);
	  	  %>
	         "<option value='<%=ref.getKey()%>'><%=ref.getName() %></option>"+
	     <%}} %>
	    "</select> "+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='text' name='transferDate' size='10' readonly id='transferDate' onmouseover='popCalendar(this,this)' value='' size='10' class='' /> "+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='text' name='transferTime' size='6' onkeypress='inputTimeM(event, this);' onkeyup='inputTimeM(event, this);' onchange='validateInputTime(this);' "+
	    "   autocomplete='off' id='transferTime'  value='' size='10' class='' /> "+
	    "</td>"+
	    "<td class='td_number' width='10%'> "+
	    "  <input type='text' name='amount' size='15'  autocomplete='off'  id='amount'  onblur='checkFloat(this)' value='' size='17' class='enableNumber' /> "+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='text' name='chequeNo' size='20' autocomplete='off' id='chequeNo' value='' size='20' onkeydown='return isNum0to9andpoint(this,event);'' /> "+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='text' name='chequeDate' size='10' readonly id='chequeDate'  onmouseover='popCalendar(this,this)'  value='' size='20' class='' /> "+
	    "</td>"+
	    "</tr>";

    $('#tblProduct').append(rowData);
    
    //set focus default transferType
    var transferType = document.getElementsByName("transferType");
    transferType[rowId].focus();
}

function sumTotal(){
	var chk = document.getElementsByName("lineChk");
	var amount = document.getElementsByName("amount");
	var keyData = document.getElementsByName("keyData");
    var totalAmount = 0;
	for(var i=0;i<chk.length;i++){
		if(keyData[i].value !='CANCEL'){
			totalAmount +=convetTxtObjToFloat(amount[i]);
		}
	}
	
	document.getElementsByName("totalAmount")[0].value = totalAmount;
	toCurreny(document.getElementsByName("totalAmount")[0]);

} 
 function removeRow(path){
	if(confirm("�׹�ѹź������")){
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
		if(!bcheck){alert('���͡���������ҧ���� 1 ��¡��');return false;}else{sumTotal();}
	}
} 
 function removeRowByIndex(path,drow,index){
    //alert(drow);
	//todo play with type	
	drow.style.display ='none';
	//set  staus = CANCEL 
	//alert(index);
	document.getElementsByName("keyData")[index].value ='CANCEL';
	
	sumTotal();
}
 function checkFloat(obj){
	 if(obj.value != ""){
		 if(isNum(obj)){
			 sumTotal();
			 return true
		 }
		 return false;
	 }
 }

</script>
</head>
<body class="sb-nav-fixed" onload="loadMe()">
     <!-- Include Header Mobile  -->
     <jsp:include page="../header.jsp"  flush="true"/>
     <!-- /Include Header Mobile -->
     
   	<!-- PROGRAM HEADER -->
     	<jsp:include page="../program.jsp">
		<jsp:param name="function" value="Transfer"/>
		<jsp:param name="code" value=""/>
	</jsp:include>
	      	
	<!-- BODY -->
	<html:form action="/jsp/transferAction">
	<jsp:include page="../error.jsp"/>
						
			<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
				<tr>
					<td colspan="4" align="center">
				        <font color="black" size="5"> <b> �ѹ�֡����͹�Թ</b> </font>
				    </td>
				</tr>
				<tr>
					<td colspan="4" align="center">
					<div align="left" style="margin-left:13px;">
					   <%//if( !"view".equalsIgnoreCase(request.getParameter("action"))){ %>
					      <div align="left">
					          <input type="button" class="newPosBtn" value="������¡��" onclick="addRow('${pageContext.request.contextPath}');"/>
					           <input type="button" class="newPosBtn" value="ź��¡��" onclick="removeRow('${pageContext.request.contextPath}');"/>	
				           </div>
				        <%//} %>
					     <%-- </c:if> --%>
					     <div align="center">
					     <font color="black" size="3">�ѹ���ѹ�֡������ :&nbsp;
					       <html:text property="bean.createDate" size="10" readonly="true" styleId="createDate"  styleClass="disableText"/>
					     </font>
					     </div>
					</div>
			
			<!--  Results  -->
		     <div class="table-responsive">
              <table class="table table-bordered table-striped table-light"
                   id="tblProduct" width="100%" cellspacing="0">
                 <thead class="thead-dark">
				<tr>
				    <th>
				     <!--   <input type="checkbox" name="chkAll"
						onclick="checkSelect(this,document.getElementsByName('lineids'));" /> -->
					</th>
					<th>����������͹</th>
					<th>��Ҥ�âͧ PENS</th>
					<th>�ѹ����͹</th>
					<th>���ҷ���͹ <br/>(�ٻẺ���� 24 �� . 08:30)</th>
					<th>�ʹ�Թ����͹</th>
					<th>�Ţ�����</th>
					<th>�ѹ���˹����</th>
				</tr>
				</thead>
				<% 	
				List<TransferBean> resultList = transferForm.getLines();
				String keyData = "";
				String tabclass ="";
				String rowDisablestyle = "";
				String rowDisbleCommand = "";
				if(resultList != null && resultList.size() >0){
				    for(int n=0;n<resultList.size();n++){
				       TransferBean mc = (TransferBean)resultList.get(n);
					   if(n%2==0){ 
						 tabclass="lineO";
					   }
					   keyData = ""+mc.getLineId();
					   //defalut
					   rowDisablestyle = "";
					   rowDisbleCommand = "";
					   //check can edit row
					   if( !mc.isCanEdit()){
						   rowDisablestyle = "disableText";
						   rowDisbleCommand = "readonly";
					   }
					   
                          %>
					<tr class="<%=tabclass%>">
					   <% if( mc.isCanEdit()){%>
						    <td class = "td_text_center" width="5%">
							  <input type="checkbox" name="lineChk" id="lineChk" value="<%=mc.getKeyData() %>" <%=rowDisbleCommand %> >
					          <input type="hidden" name="keyData" id="keyData" value="<%=mc.getKeyData() %>" />
					          <input type="hidden" name="rowId" id="rowId" value="<%=n%>" />
					          <input type="hidden" name="row_can_edit" id="row_can_edit" value="<%=mc.isCanEdit()%>" />
					          <input type="hidden" name="lineId" id="lineId" value="<%=mc.getLineId()%>" />
							</td>
							 <td class = "td_text_center" width="10%">
							   <select name="transferType" id="transferType">
							      <option value=""></option>
							      <option value="CS" <%=Utils.isNull(mc.getTransferType()).equals("CS")?"selected":"" %>>�Թʴ</option>
							      <option value="CH" <%=Utils.isNull(mc.getTransferType()).equals("CH")?"selected":"" %>>��</option>
							   </select>
							   
							 </td> 
							 <td class = "td_text" width="35%">
								 <select name="transferBank" id="transferBank"  <%=rowDisbleCommand %> style="width: 300px;">
								      <option value=""></option>
								      <%
								      String selected = "";
								      if(transferBankVanList != null && transferBankVanList.size()>0){
								    	  for(int i=0;i<transferBankVanList.size();i++){
								    		  References ref = transferBankVanList.get(i);
								    		  selected = ref.getKey().equals(Utils.isNull(mc.getTransferBank()))?"selected":"";
								    	  %>
								           <option value="<%=ref.getKey()%>" <%=selected%>><%=ref.getName() %></option>
								      <%}} %>
								   </select>
							 </td> 
							 <td class ="td_text_center" width="10%">
							    <input type="text" name="transferDate" size= "10"  onmouseover='popCalendar(this,this)' 
							    readonly="true" id="transferDate" value="<%=Utils.isNull(mc.getTransferDate())%>"  <%=rowDisbleCommand %>  />
							 </td>
							 <td class ="td_text_center" width="10%">
							    <input type="text" name="transferTime" size= "6"  onkeypress='inputTimeM(event, this);' onkeyup='inputTimeM(event, this);'
							     onchange="validateInputTime(this)"
							     autocomplete="off" id="transferTime" value="<%=Utils.isNull(mc.getTransferTime())%>"  <%=rowDisbleCommand %> />
							 </td>
							 <td class ="td_number" width="10%">
							    <input type="text" name="amount" size= "15"  onblur="checkFloat(this)" class="enableNumber"
							    autocomplete="off" id="amount" value="<%=Utils.isNull(mc.getAmount()) %>"  <%=rowDisbleCommand %> />
							 </td>

							<td class ="td_text_center" width="10%">
							    <input type="text" name="chequeNo" size= "20"  autocomplete="off" id="chequeNo" 
							    value="<%=Utils.isNull(mc.getChequeNo()) %>"  <%=rowDisbleCommand %> onkeydown="return isNum0to9andpoint(this,event);" />
							 </td>
							 <td class ="td_text_center" width="10%">
							    <input type="text" name="chequeDate" size= "10"  onmouseover='popCalendar(this,this)' 
							     readonly id="chequeDate" value="<%=Utils.isNull(mc.getChequeDate()) %>"  <%=rowDisbleCommand %> />
							 </td>
						 
						<%}else{ %>
						  <td class = "td_text_center" width="5%">
							  <label class="checkbox"><input type="checkbox" name="lineChk" id="lineChk" value="<%=mc.getKeyData() %>" style="opacity:0; position:absolute; left:99px;"></label>
							  
					          <input type="hidden" name="keyData" id="keyData" value="<%=mc.getKeyData() %>" />
					          <input type="hidden" name="rowId" id="rowId" value="<%=n%>" />
					          <input type="hidden" name="row_can_edit" id="row_can_edit" value="<%=mc.isCanEdit()%>" />
					          <input type="hidden" name="lineId" id="rowId" value="<%=mc.getLineId()%>" />
							</td>
						     <td class = "td_text_center" width="10%">
							      <input type="text" name="transferTypeLabel"  class="disableText" size= "5" value="<%=Utils.isNull(mc.getTransferTypeLabel()) %>" /> 
		  					      <input type="hidden" name="transferType"  value="<%=Utils.isNull(mc.getTransferType()) %>"/> 
							 </td> 
							 <td class = "td_text" width="35%">
								 <input type="text" name="transferBankLabel" readonly class="disableText" size= "40" value="<%=Utils.isNull(mc.getTransferBankLabel()) %>" /> 
		  					     <input type="hidden" name="transferBank"  value="<%=Utils.isNull(mc.getTransferBank()) %>"/> 
							 </td> 
							 <td class ="td_text_center" width="10%">
							    <input type="text" name="transferDate" size= "10" class="disableText"
							    readonly id="transferDate" value="<%=Utils.isNull(mc.getTransferDate())%>" />
							 </td>
							 <td class ="td_text_center" width="10%">
							    <input type="text" name="transferTime" size= "6"  class="disableText" readonly 
							    id="transferTime" value="<%=Utils.isNull(mc.getTransferTime())%>"  />
							 </td>
							 <td class ="td_number" width="10%">
							    <input type="text" name="amount" size= "15" readonly class="disableNumber"  id="amount" value="<%=Utils.isNull(mc.getAmount()) %>"  <%=rowDisbleCommand %> />
							 </td>
							<td class ="td_text_center" width="10%">
							    <input type="text" name="chequeNo" size= "20"  readonly  class="disableText" id="chequeNo" 
							    value="<%=Utils.isNull(mc.getChequeNo()) %>"  />
							 </td>
							 <td class ="td_text_center" width="10%">
							    <input type="text" name="chequeDate" size= "10"  readonly  class="disableText" 
							      id="chequeDate" value="<%=Utils.isNull(mc.getChequeDate()) %>" />
							 </td>
						<%} %>
					</tr>
				     <%} }%>
					</table>
					</div>
					<!--  Results -->
					<!-- Summary -->
					<table id="tblProductSummary" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
					  <tr>
					   <td colspan="5" align="right" width="65%"><b>�ʹ���</b></td>
					   <td width="10%" class="td_number">
					    <input type="text" name="totalAmount" size="12" readonly id="totalAmount" class="disableNumberBold"/>
					    </td>
					   <td colspan="2"  width="20%"></td>
					  </tr>
					</table>
			
			<br>
			<!-- BUTTON -->
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="center">
					   <c:if test="${transferForm.bean.canEdit =='true'}">
						<a href="#" onclick="return save('${pageContext.request.contextPath}');">
						  <input type="button" value="�ѹ�֡��¡��" class="newPosBtnLong">
						</a>	
					   </c:if>	
						<a href="#" onclick="clearForm('${pageContext.request.contextPath}');">
							<input type="button" value=" Clear "  class="newPosBtnLong">
					    </a>
						  
					</td>
				</tr>
			</table>
			
			<!-- Hidden Field -->
			
			<html:hidden property="deletedId"/>
			<html:hidden property="lineNoDeleteArray"/>
			<!-- Hidden -->
               <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
			  
			<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
		</html:form>
		<!-- BODY -->
			
     <!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->			
</body>
</html>
 <!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->

