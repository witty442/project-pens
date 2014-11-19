<%@page import="com.isecinc.pens.bean.ReqPickStock"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>

<jsp:useBean id="addItemPickStockForm" class="com.isecinc.pens.web.pick.AddItemPickStockForm" scope="session" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/req_pick_stock.css" type="text/css" />

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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
 <%if(session.getAttribute("resultItems") != null) {%>
    sumQty();
  <%}%>
  
  <%if(session.getAttribute("saved") !=null){%>
       if(document.getElementsByName("bean.totalQty")[0].value =='0'){
    	   sumQty = "";
       }else{
    	   sumQty = document.getElementsByName("bean.totalQty")[0].value;
       }
       window.opener.setQtyMain(document.getElementsByName("bean.rowIndex")[0].value,sumQty);
 	   window.close();
 <% }%>
}

function clearForm(path){
	var form = document.addItemPickStockForm;
	form.action = path + "/jsp/addItemPickStockAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.addItemPickStockForm;
	form.action = path + "/jsp/addItemPickStockAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.addItemPickStockForm;
	form.action = path + "/jsp/addItemPickStockAction.do?do=exportExcel";
	form.submit();
	return true;
}

function save(path){
	 
	var form = document.addItemPickStockForm;
	form.action = path + "/jsp/addItemPickStockAction.do?do=save";
	form.submit();
	 return true;
}

function isNum(obj){
  if(obj.value != ""){
	var newNum = parseInt(obj.value);
	if(isNaN(newNum)){
		alert('����͡��੾�е���Ţ��ҹ��');
		obj.value = "";
		obj.focus();
		return false;
	}else{return true;}
   }
  return true;
}

function chkQtyKeypress(obj,e,row){
	//alert(obj.value);
	if(e != null && e.keyCode == 13){
		return validateQty(obj,row);
	}
}

function validateQty(obj,row){
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	
	var r = isNum(obj);
	var onhandQtyObj = document.getElementsByName("onhandQty");
	//alert(onhandQtyObj[row].value);
	if(r){
		//validate Onhand Qty
		var onhandQty = parseInt(onhandQtyObj[row].value);
		var currQty = parseInt(obj.value);
		if(currQty > onhandQty){
			alert("�ӹǹ��� QTY("+currQty+") ���ҡ���  Onhand QTY("+onhandQty+")");
			//rows[row+1].className ="lineError";
			obj.value = "";
			obj.focus();
			return false;
		}
		
		sumQty();
	}
	return true;
}

function sumQty(){
	var qtyObj = document.getElementsByName("qty");
	var totalQtyNotInCurPage = 0;
	if(document.getElementsByName("totalQtyNotInCurPage")[0].value !=""){
		totalQtyNotInCurPage = parseInt(document.getElementsByName("totalQtyNotInCurPage")[0].value);
	}
	
	//alert(totalQtyNotCurPage);
	var sumCurPageQty = 0;
	for(var i=0;i<qtyObj.length;i++){
		if(qtyObj[i].value != '')
			sumCurPageQty = sumCurPageQty + parseInt(qtyObj[i].value);
	}
	//cur Page
	document.getElementsByName("curPageQty")[0].value = sumCurPageQty;
	//total All show
	//alert(totalQtyNotInCurPage +","+ sumCurPageQty);
	
	document.getElementsByName("bean.totalQty")[0].value = totalQtyNotInCurPage + sumCurPageQty ;
	
}
</script>
</head>		
   <body onload="loadMe()">         
			<!-- BODY -->
			<html:form action="/jsp/addItemPickStockAction">
			<jsp:include page="../error.jsp"/>
			
               <html:hidden property="bean.rowIndex"/>
                
			   <div align="center">
				    <table align="center" border="0" cellpadding="3" cellspacing="0" >
				        <tr>
                            <td colspan="3" align="center"><font size="3"><b>�ѹ�֡��������� Item</b></font></td>
					   </tr>
                             <tr>
                                  <td>GroupCode</td>
                                   <td>
                                     <html:text property="bean.groupCode" styleId="groupCode" size="20" readonly="true" styleClass="disableText"/>
                                   </td>
							<td align="right">Pens Item </td>
							<td align="left">
							 <html:text property="bean.pensItem" styleId="pensItem" size="20" readonly="true" styleClass="disableText"/>	  
							</td>
						</tr>
					</table>
		      </div>

		  <%if(session.getAttribute("resultItems") != null) {%>
				<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
				     <tr>
						<th >MaterialMaster</th>
						<th >Barcode</th>
						<th >Onhand-Qty</th>
						<th >Qty �����ԡ</th>
					  </tr>
					<%
					List<ReqPickStock> resultList =(List<ReqPickStock>) session.getAttribute("resultItems");
					int tabindex = 0;
					String titleDisp ="";
					for(int i=0;i<resultList.size();i++){
					   ReqPickStock o = (ReqPickStock) resultList.get(i);
					  // System.out.println("getLineItemStyle:"+o.getLineItemStyle());
					   
					   String classStyle = (i%2==0)?"lineO":"lineE";
					   if( !Utils.isNull(o.getLineItemStyle()).equals("")){
						   classStyle = Utils.isNull(o.getLineItemStyle());
					   }
					%>
					      <tr class="<%=classStyle%>" id="<%=o.getLineItemId()%>"> 
					            
								<td class="data_materialMaster"><%=o.getMaterialMaster() %></td>
								<td class="data_barcode"><%=o.getBarcode() %></td>
								<td class="data_onhandQty">
								    <input tabindex="-1" type="hidden" name="onhandQty" value ="<%=o.getOnhandQty()%>"/>
								    <%=o.getOnhandQty()%>
								</td>
								<td class="data_qty">
									  <input tabindex="1" type="text" name="qty" value ="<%=Utils.isNull(o.getQty()) %>" size="20"  class="enableNumber"
									   onkeypress="chkQtyKeypress(this,event,<%=i%>)"
					                   onchange="validateQty(this,<%=i%>)"
									  />
								</td>
						</tr>
					<%} %>
			</table>
			
			<div align="right">
				<table  border="0" cellpadding="3" cellspacing="0" >
					<tr>
						<td align="right">	 <span class="pagelinks">��������� :
						<html:text property="bean.totalQty" styleId="totalQty" size="20" styleClass="disableNumber"/>
						<br/>
						<input type="hidden" name="totalQtyNotInCurPage" id="totalQtyNotInCurPage" value=""/>
						<input type="hidden" name = "curPageQty" id="curPageQty"/>
						</span>			
						</td>
					</tr>
				</table>
			</div>	
		<%} %>		

			<!-- BUTTON ACTION-->
			<div align="center">
				<table  border="0" cellpadding="3" cellspacing="0" >
						<tr>
							<td align="left">
								<a href="javascript:save('${pageContext.request.contextPath}')">
								  <input type="button" value="  OK  " class="newPosBtnLong"> 
								</a>
							<a href="javascript:window.close()">
							  <input type="button" value="   �Դ˹�Ҩ�   " class="newPosBtnLong">
							</a>	
													
							</td>
						</tr>
				</table>
			</div>

			<!-- ************************Result ***************************************************-->
			</html:form>
			<!-- BODY -->
					
</body>
</html>