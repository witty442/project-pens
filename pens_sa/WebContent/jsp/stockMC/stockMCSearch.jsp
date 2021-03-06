<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCAction"%>
<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockMCForm" class="com.isecinc.pens.web.stockmc.StockMCForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "stockMCForm");
User user = (User)session.getAttribute("user");
String pageName = Utils.isNull(request.getParameter("pageName")); 

String codes = Utils.isNull(session.getAttribute("stock_mc_codes"));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/stockMC.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<script type="text/javascript">

/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}

window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	new Epoch('epoch_popup','th',document.getElementById('stockDate'));
}
function clearForm(path){
	var form = document.stockMCForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockMCAction.do?do=prepareSearch&action=back&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.stockMCForm;
/* 	if( form.startDate.value ==""&& form.endDate.value==""){
		alert("��س��к��ѹ���");
		form.startDate.focus();
		return false;
	} */
	
	form.action = path + "/jsp/stockMCAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function openEdit(path,action,id){
	var form = document.stockMCForm;
    var param = "&id="+id;
        param +="&action="+action;
        param +="&pageName="+document.getElementsByName("pageName")[0].value;
        
	form.action = path + "/jsp/stockMCAction.do?do=viewDetail"+param;
	form.submit();
	return true;
	
	//var url = path + "/jsp/stockMCAction.do?do=viewDetail"+param;
    //PopupCenterFull(url,"View")
}

function exportToExcel(path){
	var form = document.stockMCForm;
	var codes = document.getElementsByName("codes")[0].value;
	if(codes == ''){
		alert("��س��к� ��¡�÷���ͧ��� Export ��͹");
		return false;
	}
	form.action = path + "/jsp/stockMCAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.stockMCForm;
	form.action =  "${pageContext.request.contextPath}/jsp/stockMCAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}
/* set Chechbox Page **/
function saveSelectedInPage(no){
	//alert(no);
	var chk = document.getElementsByName("chCheck");
	var currentPage = document.getElementsByName("currentPage")[0].value;
	var code = document.getElementsByName("code_temp");
	var retCode = '';
	var codesAllNew = "";

	//alert("no:"+no);
	if(no > <%=StockMCAction.pageSize%>){
	   no = no - ( (currentPage-1) *<%=StockMCAction.pageSize%>);
	}
	//alert("no["+no+"]checked:"+chk[no].checked);
    if(chk[no].checked){
    	//Add 
        retCode = code[no].value;
		
		//alert("code["+no+"]="+retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		
	    var found = chekCodeDupInCodesAll(retCode);
	    if(found == false){
	  	   codesAllNew += retCode +",";
	    }
	    document.getElementsByName("codes")[0].value =  codesAllNew;
	    //alert(no+":found["+found+"] codes["+document.getElementsByName("codes")[0].value+"]");
    }else{
    	//remove
    	retCode = code[no].value;
		
		//alert(retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		
	    removeUnSelected(retCode);
    }
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/stockMC/ajax/saveValueSelected.jsp",
			data : "codes=" + encodeURIComponent(document.getElementsByName("codes")[0].value),
			//async: false,
			cache: true,
			success: function(){
			}
		}).responseText;
	});
}
function chekCodeDupInCodesAll(codeCheck){
	var codesAll = document.getElementsByName("codes")[0].value;
	var codesAllArray = codesAll.split(",");
	var found = false;;
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] == codeCheck){
   			found = true;
   			break;
   		}//if
	}//for
	return found;
}
function removeUnSelected(codeCheck){
	var codesAll = document.getElementsByName("codes")[0].value;
	var codesAllArray = codesAll.split(",");
	var codesAllNew  = "";
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] != codeCheck){
   		   codesAllNew += codesAllArray[i] +",";
   		}//if
	}//for
	codesAllNew = codesAllNew.substring(0,codesAllNew.length-1);
	document.getElementsByName("codes")[0].value =  codesAllNew;
}

</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">
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
				<jsp:param name="function" value="StockMC"/>
			</jsp:include>
			<!-- Hidden Field -->
		 <%--    <html:hidden property="pageName" value="<%=pageName %>"/> --%>
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
						<html:form action="/jsp/stockMCAction">
						<jsp:include page="../error.jsp"/>
						<div align="center">
						   	<!--  Criteria -->
						   <table align="center" border="0" cellpadding="3" cellspacing="0" >
					       <tr>
				                <td>�ѹ����Ǩ�Ѻʵ�͡<font color="red"></font></td>
								<td>		
			                        <html:text property="bean.stockDate" styleId="stockDate" size="15" readonly="true" styleClass=""/>					    
								</td>
								<td> </td>
								<td> </td>
							</tr>
							<tr>
				                <td align="right"> ��ҧ  </td>
								<td colspan="2">
								  <html:text property="bean.customerCode" styleId="customerCode" 
								    size="10"  styleClass="\" autoComplete=\"off"
									onkeypress="getCustNameKeypress(event,this)"/>
								    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC')"/>   
								   <html:text property="bean.customerName" styleId="customerName" size="40" readonly="true" styleClass="disableText"/>
								</td>
							</tr>	
					   </table>
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									<a href="javascript:search('${pageContext.request.contextPath}')">
									  <input type="button" value="   ����     " class="newPosBtnLong"> 
									</a>
									<%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="  Export   " class="newPosBtnLong"> 
									</a> --%>
									<%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
										<a href="javascript:openEdit('${pageContext.request.contextPath}','add','')">
										  <input type="button" value=" ������¡������  " class="newPosBtnLong"> 
										</a>
									
									<%}%>
									<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="Export To Excel" class="newPosBtnLong">
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>						
								</td>
							</tr>
						</table>
					    
					 	    <!-- ************************Result *************-->
					 	    <c:if test="${stockMCForm.results != null}">
					 	     <% 
							   int totalPage = stockMCForm.getTotalPage();
							   int totalRecord = stockMCForm.getTotalRecord();
							   int currPage =  stockMCForm.getCurrPage();
							   int startRec = stockMCForm.getStartRec();
							   int endRec = stockMCForm.getEndRec();
							   int no = startRec;
							%>
						        <%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
									
									<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="1" class="tableSearch">
									       <tr>
									            <th >Selected</th>
												<th >No</th>
												<th >�ѹ����Ǩ�Ѻʵ�͡</th>
												<th >��ҧ</th>
												<th >������ҧ</th>
												<th >�Ң�</th>
												<th >���ѹ�֡��Ǩ��ʵ�͡</th>
												<th >��ѡ�ҹ PC</th>
												<th >Action</th>						
										   </tr>
										<% 
										String tabclass ="";
										List<StockMCBean> resultList = stockMCForm.getResults();
										for(int n=0;n<resultList.size();n++){
											StockMCBean item = (StockMCBean)resultList.get(n);
											if(n%2==0){ 
												tabclass="lineO";
											}else{
												tabclass ="lineE";
											}
											%>
												<tr class="<%=tabclass%>">
												    <td class="td_text_center" width="2%">
												     <input type ="checkbox" name="chCheck" id="chCheck" onclick="saveSelectedInPage(<%=n%>)"  />
												      <input type ="hidden" name="code_temp" value="<%=item.getId() %>" />
													</td>
													<td class="td_text_center" width="2%"><%=no %></td>
													<td class="td_text_center" width="5%">
													   <%=item.getStockDate() %>
													</td>
													<td class="td_text" width="5%">
													   <%=item.getCustomerCode() %>
													</td>
													<td class="td_text" width="10%">
													  <%=item.getCustomerName()%>
													</td>
													<td class="td_text" width="7%"> <%=item.getStoreName()%></td>
													<td class="td_text_center" width="5%">
														 <%=item.getCreateUser() %>
													</td>
													<td class="td_text_center" width="10%">
														 <%=item.getMcName() %>
													</td>
													<td class="td_text_center" width="4%">
														 <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
															  <a href="javascript:openEdit('${pageContext.request.contextPath}','edit', '<%=item.getId()%>')">
															      ���
															  </a> 
															
															   
														  <%}else{ %>
														      <a href="javascript:openEdit('${pageContext.request.contextPath}','view', '<%=item.getId()%>')">
															      VIEW
															  </a>
														  <%} %>
													  <% no++;%>
													</td>
												</tr>
										<%}//for %>
								  </table>	
						    </c:if>		
					 	</div>
					 	
					 	<!-- INPUT HIDDEN -->
					 	<input type="hidden" name="pageName" value="<%=pageName %>"/>
					 	<input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
					 	<input type="hidden" name="currentPage"  value ="<%=stockMCForm.getCurrPage()%>" />
                        <input type="hidden" name="codes" value ="<%=codes%>" />
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