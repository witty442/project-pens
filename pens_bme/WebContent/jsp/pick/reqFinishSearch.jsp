<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.ReqFinish"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.dao.ReqReturnWacoalDAO"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="reqFinishForm" class="com.isecinc.pens.web.pick.ReqFinishForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css"></style>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('requestDate'));
}
function clearForm(path){
	var form = document.reqFinishForm;
	form.action = path + "/jsp/reqFinishAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.reqFinishForm;
	form.action = path + "/jsp/reqFinishAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.reqFinishForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/reqFinishAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,requestDate,requestNo,mode){
	var form = document.reqFinishForm;
	form.action = path + "/jsp/reqFinishAction.do?do=prepare&requestDate="+requestDate+"&requestNo="+requestNo+"&mode="+mode;
	form.submit();
	return true;
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
				<jsp:param name="function" value="reqFinish"/>
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
						<html:form action="/jsp/reqFinishAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Request Date</td>
									<td>					
										 <html:text property="bean.requestDate" styleId="requestDate" size="20"/>
									</td>
									<td> 
									    Request No <html:text property="bean.requestNo" styleId="requestNo" size="20" styleClass="\" autoComplete=\"off"/>
									</td>
									<td>					
										
									</td>
								</tr>
								 <tr>
                                    <td>	ʶҹ�   </td>
									<td align="left">					
										 <html:select property="bean.status">
											<html:options collection="statusReqReturnW2List" property="key" labelProperty="name"/>
									    </html:select>
									</td>
									<td > 
									     Warehouse
									      <html:select property="bean.wareHouse" styleId="wareHouse" >
											<html:options collection="wareHouseList2" property="key" labelProperty="name"/>
									    </html:select>
									</td>
									<td align="left">	
									    
									</td>
								</tr>
								<tr>
                                    <td> �����˵�</td>
									<td colspan="3">
						               <html:text property="bean.remark" styleId="remark" size="80"  styleClass="\" autoComplete=\"off"/>
									</td>
								</tr>	
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ����      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:openEdit('${pageContext.request.contextPath}','','','add')">
										  <input type="button" value="   ������¡������   " class="newPosBtnLong">
										</a>	
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${reqFinishForm.resultsSearch != null}">
                  	<% 
					   int totalPage = reqFinishForm.getTotalPage();
					   int totalRecord = reqFinishForm.getTotalRecord();
					   int currPage =  reqFinishForm.getCurrPage();
					   int startRec = reqFinishForm.getStartRec();
					   int endRec = reqFinishForm.getEndRec();
					   int pageSize = reqFinishForm.getPageSize();
					   int no = Utils.calcStartNoInPage(currPage, pageSize);
					%>
					<%=PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no) %>
					
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >No</th>
									<th >Request Date</th>
									<th >Request No</th>
									<th >Status</th>
									<th >Remark</th>
									<th >Action</th>						
							   </tr>
							<% 
							//int no = (currPage-1)*pageSize;
							String tabclass ="lineE";
							List<ReqFinish> resultList = reqFinishForm.getResultsSearch();
							for(int n=0;n<resultList.size();n++){
								ReqFinish mc = (ReqFinish)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								%>
									<tr class="<%=tabclass%>">
										<td class="td_text_center" width="5%"><%=no%></td>
										<td class="td_text_center" width="10%"><%=mc.getRequestDate() %></td>
										<td class="td_text_center" width="10%"><%=mc.getRequestNo()%></td>
										<td class="td_text_center" width="10%"><%=mc.getStatusDesc()%></td>
										<td class="td_text" width="15%"><%=mc.getRemark() %></td>
										<td class="td_text_center" width="10%">
										 <%if(mc.isCanEdit()==false) {%>
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=mc.getRequestDate()%>','<%=mc.getRequestNo()%>','view')">
											       <font size="2">  �� </font> 
											  </a>
										  <%} %>
										   <%if(mc.isCanEdit()==true){ %>
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '<%=mc.getRequestDate()%>','<%=mc.getRequestNo()%>','edit')">
											       <font size="2">    ���</font>
											  </a>
										 <% } %>
										</td>
									</tr>
							
							 <% no++;
							 } %>
					</table>
				</c:if>
					<!-- ************************Result ***************************************************-->
					
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
					<input type="hidden" name="path" id="path" value ="${pageContext.request.contextPath}"/>
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