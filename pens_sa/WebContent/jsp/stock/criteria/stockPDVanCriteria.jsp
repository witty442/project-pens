<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.web.stock.StockForm"%>
<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="com.pens.util.*"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
StockBean bean = ((StockForm)session.getAttribute("stockForm")).getBean();
%>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.stockForm;
	//alert("onload");
	
	<%if(session.getAttribute("GEN_PDF_SUCCESS") != null){%>
		// alert(path);
		form.action = "${pageContext.request.contextPath}/jsp/stockAction.do?do=loadPDFReport";
		form.submit();
		return true;
	<%}%>
}

function clearForm(path){
	var form = document.stockForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function searchReport(path){
	var form = document.stockForm;
	//alert(form.startDate.value);
	if( $('#period').val()==""){
		alert("กรุณาระบุ เดือน");
		return false;
	} 

	form.action = path + "/jsp/stockAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}

function exportReport(path,e){
	var form = document.stockForm;
	 if( $('#period').val()==""){
		alert("กรุณาระบุ เดือน");
		return false;
	} 
	 
	//To disable f5
	$(document).bind("keydown", disableF5);
	 
	//document.getElementById("progress").style.display = "block";
	//document.getElementById("control_btn").style.display = "none";
	
	/**Control Save Lock Screen **/
    startControlSaveLockScreen();
	
	// alert(path);
	form.action = path + "/jsp/stockAction.do?do=exportReport&action=newsearch";
	form.submit();
	return true;
}

function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
}
//To re-enable f5
$(document).unbind("keydown", disableF5);

</script>

  <table align="center" border="0" cellpadding="3" cellspacing="0" >
       <tr><td colspan="1" nowrap> 
		   <span id="div_month">
			        เดือน <font color="red">*</font>
			     <html:select property="bean.period" styleId="period">
					<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
			    </html:select>
		    </span>
		</td></tr>
		<tr><td colspan="1" nowrap> 
		   <span id="div_month">
			        ปิดรอบของ PD <font color="red">*</font>
			     <html:select property="bean.pdCode" styleId="pdCode">
					<html:options collection="PD_LIST" property="pdCode" labelProperty="pdDesc"/>
			    </html:select>
		    </span>
		</td></tr>
   </table>
	   <br/>
   <div id="control_btn" title=" "  style="display:block">
   <table  border="0" cellpadding="3" cellspacing="0" >
		<tr>
			<td align="left">
				 <a href="javascript:exportReport('${pageContext.request.contextPath}')">
				  <input type="button" value="  พิมพ์  " class="newPosBtnLong"> 
				</a>
				
				<%-- <a href="javascript:downloadDocument('${pageContext.request.contextPath}')">
				  <input type="button" value="  พิมพ์(ajax)  " class="newPosBtnLong"> 
				</a> --%>
				&nbsp;
				<a href="javascript:clearForm('${pageContext.request.contextPath}')">
				  <input type="button" value="   Clear   " class="newPosBtnLong">
				</a>
				&nbsp;
				<a href="javascript:window.close()">
				  <input type="button" value=" ปิดหน้าจอนี้  " class="newPosBtnLong">
				</a>			
			</td>
		</tr>
	</table>
	</div>

