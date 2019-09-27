<%@page import="com.isecinc.pens.report.salesanalyst.DisplayBean"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.sql.Connection"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.report.salesanalyst.SAInitial"%>

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="searchValuePopupForm" class="com.isecinc.pens.web.popup.SearchValuePopupForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>ค้นหาข้อมูล</title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/popup_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script> --%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<%
 boolean isMultiSelect = false;
 String load = Utils.isNull(request.getParameter("load"));

 String currCondNo = Utils.isNull(request.getParameter("currCondNo"));
 String currCondTypeValue = Utils.isNull(request.getParameter("currCondTypeValue"));
 String currCondNameText = Utils.isNull((String)SAInitial.getInstance().GROUP_BY_MAP.get(request.getParameter("currCondTypeValue")));
 String searchType = Utils.isNull(request.getParameter("searchType"));
 String currentPage = Utils.isNull(request.getParameter("currentPage"));

/*  System.out.println("currCondNo:"+currCondNo);
System.out.println("currCondTypeValue:"+currCondTypeValue);
System.out.println("currCondNameText:"+currCondNameText);
System.out.println("searchType:"+searchType);
System.out.println("currentPage:"+Utils.isNull(currentPage)); 
System.out.println("desc1:"+request.getParameter("salesBean.desc")); */

String code = session.getAttribute("code_session")!=null?(String)session.getAttribute("code_session"):"";
String desc = session.getAttribute("desc_session")!=null?(String)session.getAttribute("desc_session"):"";

 if(SAInitial.MULTI_SELECTION_LIST.contains(currCondTypeValue)){
	 isMultiSelect = true;
 } 
 //System.out.println("isMultiSelect:"+isMultiSelect);
 
 pageContext.setAttribute("isMultiSelect", isMultiSelect, PageContext.PAGE_SCOPE);
 
 /** Store Select MutilCode in each Page **/
 String codes = Utils.isNull(session.getAttribute("codes"));
 String keys = Utils.isNull(session.getAttribute("keys"));
 String descs = Utils.isNull(session.getAttribute("descs"));
 
 //System.out.println("codes:"+codes);
 
 String condType1 = Utils.isNull(request.getParameter("condType1"));
 String condType2 = Utils.isNull(request.getParameter("condType2"));
 String condType3 = Utils.isNull(request.getParameter("condType3"));
 String condType4 = Utils.isNull(request.getParameter("condType4"));

 //System.out.println("condType1:"+condType1);
 //System.out.println("condType1:"+Utils.isNull(request.getParameter("condType1")));
%>
<script type="text/javascript">

function searchPopup(path, type) {

    var condType1 = document.getElementById("condType1").value;
	var condCode1 = document.getElementById("condCode1").value ;
	
	var condType2 = document.getElementById("condType2").value;
	var condCode2 = document.getElementById("condCode2").value;
	
	var condType3 = document.getElementById("condType3").value;
	var condCode3  = document.getElementById("condCode3").value;
	
    var currCondTypeValue = document.getElementsByName("currCondTypeValue")[0].value;
    var currCondNo = document.getElementsByName("currCondNo")[0].value;
    
    var param = "&currCondNo="+currCondNo+"&currCondType="+currCondTypeValue;
        param += "&condType1="+condType1+"&condCode1="+condCode1;
        param += "&condType2="+condType2+"&condCode2="+condCode2;
        param += "&condType3="+condType3+"&condCode3="+condCode3;
        
       // alert(param);
        
    document.searchValuePopupForm.action = path + "/jsp/searchValuePopupAction.do?do=search&action=newsearch"+param+"&searchType="<%=searchType%>;
    document.searchValuePopupForm.submit();
   return true;
}
function gotoPage(path, currPage) {
	document.searchValuePopupForm.action = path + "/jsp/searchValuePopupAction.do?do=search&currentPage="+currPage;
	document.searchValuePopupForm.submit();
}

function selectData(){
	<%if(isMultiSelect){%>
	  selectMultiple();
	<%}else{%>
	  selectOneRadio();
	<%}%>
}
function selectOneByDoubleClick(no){
	var chRadio = document.getElementsByName("chRadio");
	var code = document.getElementsByName("code");
	var key = document.getElementsByName("key");
	var desc = document.getElementsByName("desc");

	var currCondTypeValue = document.getElementsByName("currCondTypeValue")[0].value;
    var currCondNo = document.getElementsByName("currCondNo")[0].value;
	
    window.opener.setMainValue(code[no].value,key[no].value,desc[no].value ,currCondNo );
    //alert("no["+no+"] code="+code[no].value+":key="+key[no].value+":currCondNo:"+currCondNo);
    var selectCode= code[no].value;
    document.getElementsByName("selectcode").value=selectCode;
  
    /** Set condition Parent to session  Cond1 only**/
    setConditionParent(currCondNo,selectCode,currCondTypeValue);
    
	window.close();
}
function selectOneRadio(){
	var chRadio = document.getElementsByName("chRadio");
	var code = document.getElementsByName("code");
	var key = document.getElementsByName("key");
	var desc = document.getElementsByName("desc");

	var currCondTypeValue = document.getElementsByName("currCondTypeValue")[0].value;
    var currCondNo = document.getElementsByName("currCondNo")[0].value;
	
	var selectCode = "";
	for(i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
            window.opener.setMainValue(code[i].value,key[i].value,desc[i].value ,currCondNo );
            //alert("tutiya code="+code[i].value+":key="+key[i].value+":currCondNo:"+currCondNo);
            selectCode= code[i].value;
            document.getElementsByName("selectcode").value=selectCode;
          
            /** Set condition Parent to session  Cond1 only**/
            setConditionParent(currCondNo,selectCode,currCondTypeValue);
            
        	window.close();
            break;
        }
	}
}

/** Set condition Parent to session  Cond1 only**/
function setConditionParent(condNo,condCode,condType){
	$.ajax({
        type: "POST",
        url: "${pageContext.request.contextPath}/jsp/salesAnalyst/searchpost.jsp",		
      	data : "condNo="+condNo+"&condCode="+condCode+"&condType="+condType,
      	async: false,
        success: function(msg){
          //alert( "Data Saved: " + msg );
        }
        });
}

function selectMultiple(){
	var chk = document.getElementsByName("chCheck");

	var currCondTypeValue = document.getElementsByName("currCondTypeValue")[0].value;
    var currCondNo = document.getElementsByName("currCondNo")[0].value;
	
	var idx = 2;
	//Add Select Muti in each page 
	
	var retCode = document.getElementsByName("codes")[0].value;
	var retKey = document.getElementsByName("keys")[0].value;
	var retDesc = document.getElementsByName("descs")[0].value;
	
	retCode = retCode.substring(0,retCode.length-1);
	retKey = retKey.substring(0,retKey.length-1);
	retDesc = retDesc.substring(0,retDesc.length-1);
	
	//alert(retCode);
	//alert("idx:"+idx);
	if(idx ==1){
		//alert(currCondNo+","+retCode+":"+retKey+":"+retDesc);
		window.opener.setMainValue(retCode,retKey,retDesc ,currCondNo);
	}else{
		//alert(currCondNo+":"+retCode+":"+retKey+":"+retDesc);
		window.opener.setMultiCode(retKey);
		window.opener.setMultiKey(retCode);
		window.opener.setMultiValueDisp(retDesc);
		
		window.opener.setValueCondition(currCondNo);
	}
	
	 /** Set condition Parent to session  Cond1 only**/
    setConditionParent(currCondNo,retCode,currCondTypeValue);
	
	window.close();
}

function saveSelectedInPage(no){
	//alert(no);
	var chk = document.getElementsByName("chCheck");
	var code = document.getElementsByName("code");
	var key = document.getElementsByName("key");
	var desc = document.getElementsByName("desc");
	
	var currentPage = document.getElementsByName("currentPage")[0].value;
	
	var retCode = '';
	var retKey = '';
	var retDesc = '';
	
	var codesAllNew = "";
	var keysAllNew = "";
	var descsAllNew = "";

	/* if(no >= 21){
	   no = no - ( (currentPage-1) *20);
	} */
    //alert(no);
	
    if(chk[no].checked){
    	//Add 
        retCode = code[no].value;
		retKey = key[no].value;
		retDesc = desc[no].value;
		
		//alert(retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		keysAllNew = document.getElementsByName("keys")[0].value;
		descsAllNew = document.getElementsByName("descs")[0].value;
		
	    var found = chekCodeDupInCodesAll(retCode);

	    if(found == false){
	  	   codesAllNew += retCode +",";
	  	   keysAllNew  += retKey  +",";
	  	   descsAllNew += retDesc +",";
	    }
	    document.getElementsByName("codes")[0].value =  codesAllNew;
	    document.getElementsByName("keys")[0].value =  keysAllNew;
	    document.getElementsByName("descs")[0].value =  descsAllNew;
	    
    }else{
    	//remove
    	retCode = code[no].value;
		retKey = key[no].value;
		retDesc = desc[no].value;
		
		//alert(retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		keysAllNew = document.getElementsByName("keys")[0].value;
		descsAllNew = document.getElementsByName("descs")[0].value;
		
	    removeUnSelected(retCode);
    }
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/setValueSelected.jsp",
			data : "codes=" + encodeURIComponent(document.getElementsByName("codes")[0].value) +
			       "&keys=" + encodeURIComponent(document.getElementsByName("keys")[0].value) +
			       "&descs=" + encodeURIComponent(document.getElementsByName("descs")[0].value),
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
	var keysAll = document.getElementsByName("keys")[0].value;
	var descsAll = document.getElementsByName("descs")[0].value;
	
	var codesAllArray = codesAll.split(",");
	var keysAllArray = keysAll.split(",");
	var descsAllArray = descsAll.split(",");
	
	var codesAllNew  = "";
	var keysAllNew  = "";
	var descsAllNew  = "";
	
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] != codeCheck){
   		   codesAllNew += codesAllArray[i] +",";
	  	   keysAllNew  += keysAllArray[i]  +",";
	  	   descsAllNew += descsAllArray[i] +",";
   		}//if
	}//for
	
	codesAllNew = codesAllNew.substring(0,codesAllNew.length-1);
	keysAllNew = keysAllNew.substring(0,keysAllNew.length-1);
	descsAllNew = descsAllNew.substring(0,descsAllNew.length-1);
	
	document.getElementsByName("codes")[0].value =  codesAllNew;
	document.getElementsByName("keys")[0].value =  keysAllNew;
	document.getElementsByName("descs")[0].value =  descsAllNew;
}

function setChkInPage(){
	var chk = document.getElementsByName("chCheck");
	var code = document.getElementsByName("code");
	
	var codes = document.getElementsByName("codes")[0].value;
	var codesChk = codes.split(",");
	//alert(codesChk);
	
	for(var i=0;i<chk.length;i++){
		for(var c=0;c<codesChk.length;c++){
			if(code[i].value == codesChk[c]){
				chk[i].checked = true;
				break;
			}
		}
	}	
}

window.onload = function(){
	setChkInPage();
	//loadDataFromMain();
	loadNav();
	//load Critiria thai
	loadCritiria();
}
function loadCritiria(){
	<%if( !Utils.isNull(code).equals("")){ %>
	   document.getElementsByName("salesBean.code")[0].value ="<%=Utils.isNull(code)%>";
	<%}%>
	<%if( !Utils.isNull(desc).equals("")){ %>
	   document.getElementsByName("salesBean.desc")[0].value ="<%=Utils.isNull(desc)%>";
	<%}%>
}
function loadNav(){
	var nav1 = document.getElementById("nav1");
	var nav2 = document.getElementById("nav2");
	var nav3 = document.getElementById("nav3");
	var nav4 = document.getElementById("nav4");
	
	var currCondNo = document.getElementById("currCondNo");
	//alert(<%=condType1%>);
	if(currCondNo.value =='1'){
	    nav1.innerHTML = "<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue))%>";
	}else if(currCondNo.value =='2'){
		nav1.innerHTML = "<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType1))%>["+document.getElementById("condValueDisp1").value+"]";
		nav2.innerHTML = " >><%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue))%>";
		
	}else if(currCondNo.value =='3'){
		nav1.innerHTML= "<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType1))%>["+document.getElementById("condValueDisp1").value+"]";
		nav2.innerHTML= ">><%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType2))%>["+document.getElementById("condValueDisp2").value+"]";
		nav3.innerHTML= ">><%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue))%>";
	
	}else if(currCondNo.value =='4'){
		nav1.innerHTML = "<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType1))%>["+document.getElementById("condValueDisp1").value+"]";
		nav2.innerHTML= " >><%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType2))%>["+document.getElementById("condValueDisp2").value+"]";
		nav3.innerHTML= " >><%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType3))%>["+document.getElementById("condValueDisp3").value+"]";
		nav4.innerHTML= " >><%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue))%>";
	}
}

function loadDataFromMain(){
    //alert(window.opener.$("#condName1").val());
	//alert(window.opener.$("#condCode1").val());
	//alert(document.getElementById("condValueDisp1").value);
	
	<% if("1".equals(load)){ %>
		document.getElementById("condType1").value = window.opener.$("#condName1").val();
		document.getElementById("condCode1").value = window.opener.$("#condCode1").val();
		document.getElementById("condValueDisp1").value = window.opener.$("#condValue1").val();
		
		document.getElementById("condType2").value = window.opener.$("#condName2").val();
		document.getElementById("condCode2").value = window.opener.$("#condCode2").val();
		document.getElementById("condValueDisp2").value = window.opener.$("#condValue2").val();
		
		document.getElementById("condType3").value = window.opener.$("#condName3").val();
		document.getElementById("condCode3").value = window.opener.$("#condCode3").val();
		document.getElementById("condValueDisp3").value = window.opener.$("#condValue3").val(); 
   <%}%>
}
</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/searchValuePopupAction">
<input type="hidden" name="currCondNo" id="currCondNo" value ="<%=currCondNo%>" />
<input type="hidden" name="currCondTypeValue" id="currCondTypeValue" value ="<%=currCondTypeValue%>" />
<input type="hidden" name="currCondNameText"  id="currCondNameText"  value ="<%=currCondNameText%>" />
<input type="hidden" name="searchType" id="searchType" value ="<%=searchType%>" />
<input type="hidden" name="selectcode" id="selectcode" value ="" />

<html:hidden property="filterBean.condType1" styleId="condType1" />
<html:hidden property="filterBean.condCode1" styleId="condCode1" />
<html:hidden property="filterBean.condValueDisp1" styleId="condValueDisp1" />

<html:hidden property="filterBean.condType2" styleId="condType2" />
<html:hidden property="filterBean.condCode2" styleId="condCode2" />
<html:hidden property="filterBean.condValueDisp2" styleId="condValueDisp2" />

<html:hidden property="filterBean.condType3" styleId="condType3" />
<html:hidden property="filterBean.condCode3" styleId="condCode3" />
<html:hidden property="filterBean.condValueDisp3" styleId="condValueDisp3" />

<input type="hidden" name="codes" size="50" value ="<%=codes%>" />
<input type="hidden" name="keys" size="50" value ="<%=keys%>" />
<input type="hidden" name="descs" size="50" value ="<%=descs%>" />

<input type="hidden" name="currentPage" size="50" value ="<%=currentPage%>" />

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%"  class="tableHead">
      
	 <tr>
		<th class="headTitle" colspan="2" align="center">ค้นหาข้อมูล :<%=currCondNameText %></th>
	</tr>
	<tr >
		<td class="headSubTitle" colspan="2" align="left" height="40px">
		   <span id="nav1" class="headSubTitle" ></span><span id="nav2"class="headSubTitle" >
		   </span><span id="nav3" class="headSubTitle" ></span><span id="nav4" class="headSubTitle" ></span>
		
		</td>
	</tr>
	<%-- <tr height="21px">
		<td class="h1" colspan="2"><b>${searchValuePopupForm.navigation}</b></td>
	</tr> --%> 
	<tr height="21px">
		<td width="15%" align="right"><b>รหัส&nbsp;</b></td>
		<td width="90%" align="left">
		<html:text property="salesBean.code"  size="30" style="height:20px" styleClass="\" autoComplete=\"off" />
		</td>
	</tr>
	<tr height="21px">
		<td width="15%" align="right" nowrap><b>รายละเอียด &nbsp;</b></td>
		<td width="90%" align="left"><html:text property="salesBean.desc"  size="40" style="height:20px" styleClass="\" autoComplete=\"off"/> 
		<input type="button" name="search" class="newPosBtnLong"  value="ค้นหาข้อมูล" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" bgcolor="#F1F1F1">
	<tr>
		<td align="left">
			<input type="button" name="ok" value=" OK " class="newPosBtnLong"  onclick="selectData()" style="width:80px;"/>
			<input type="button" name="close" value="Close" class="newPosBtnLong"  onclick="javascript:window.close();" style="width:80px;"/>
		</td>
	</tr>
</table>
<div align="center">
 <b><font color="red" size="1"><%=Utils.isNull(request.getAttribute("Message")) %></font></b>
</div>
<!-- RESULT -->
<%-- <display:table style="width:100%;" id="item" name="sessionScope.VALUE_LIST"  defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
 --%>   
<%if(session.getAttribute("VALUE_LIST") != null){
	
	  List<DisplayBean> valueList = (List<DisplayBean> )session.getAttribute("VALUE_LIST");
	  int pageSize =20;
	  int totalPage = 0;
	  int totalRecord = 0;
	  int currPage =  0;
	  int startRec = 0;
	  int endRec = 0;
	  int no = 0;
	  
	  if(valueList != null && valueList.size() >0){
			//calc Paging from List
			System.out.println("currentPage:"+Utils.isNull(request.getParameter("currentPage")));
			//default currPage = 1
			currPage = Utils.convertStrToInt(Utils.isNull(request.getParameter("currentPage")));
			currPage = currPage==0?1:currPage;
			
			//get Total Record
			totalRecord = valueList.size();
			//calc TotalPage
			totalPage = Utils.calcTotalPage(totalRecord, pageSize);
			//calc startRec endRec
			startRec = ((currPage-1)*pageSize);
			endRec = (currPage * pageSize);
		    if(endRec > totalRecord){
			   endRec = totalRecord;
		    }//if
		    
		    System.out.println("startRec:"+startRec+" ,endRec:"+endRec);
		}//if
	%>
	<div align="left">
	   <span class="pagebanner">รายการทั้งหมด  <%=totalRecord %> รายการ, แสดงรายการที่  <%=(startRec+1) %> ถึง  <%=endRec %>.</span>
	   <span class="pagelinks">
		หน้าที่ 
		    <% 
			 for(int r=0;r<totalPage;r++){
				 if(currPage ==(r+1)){
			 %>
			   <strong><%=(r+1) %></strong>
			 <%}else{ %>
			    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  
			       title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
		 <% }} %>				
		</span>
	</div>
	<table id="tblProduct" align="center" border="1" cellpadding="3" cellspacing="1" class="tableSearch">
	       <tr>
	            <th>Selected</th>
				<th> รหัส</th>
				<th>รายละเอียด</th>	
		   </tr>
		<% 
		String tabclass ="";
		for(int n = startRec;n < endRec;n++){
			DisplayBean item = (DisplayBean)valueList.get(n);
			if(n%2==0){ 
				tabclass="linePopupO";
			}else{
				tabclass ="linePopupE";
			}
		%>
		<tr class='<%=tabclass%>'  ondblclick="selectOneByDoubleClick(<%=no %>)">
		  <td width="5%" class='td_text_center'>
		    <%if(isMultiSelect){ %>
				<input type ="checkbox" name="chCheck" onclick="saveSelectedInPage(<%=no%>)"  />
			<%}else{ %>
				<input type ="radio" name="chRadio" />
			<%} %>
			<input type ="hidden" name="code" value="<%=item.getCode() %>" />
			<input type ="hidden" name="key" value="<%=item.getKey() %>" />
			<input type ="hidden" name="desc" value="<%=item.getName() %>" />
		  </td>
		   <td width="10%" class='td_text_center'><%=item.getKey() %></td>
		   <td width="35%" class='td_text'><%=item.getName() %></td>
		</tr>
		<% no++;} %>
	</table>
<%} %>
<!-- RESULT -->

	<Script>
	loadDataFromMain();
	</Script>
		
</html:form>
</body>
</html>