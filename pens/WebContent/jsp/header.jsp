<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="util.AppversionVerify"%>
<%
String[] msg1 = new String[2];
if(request.getSession().getAttribute("appVersionCheckMsg") != null){
	msg1 =  (String[])request.getSession().getAttribute("appVersionCheckMsg");
}else{
	System.out.println("Header.jsp AppVerify ");
	msg1 = AppversionVerify.getIns().checkAppVersion(request) ;
} 
%>  
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr height="137px;" >
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/header_left.png" border="0"/>
		</td>
        <td width="341px;" background="${pageContext.request.contextPath}/images2/header01.png">&nbsp;
            <div class="userlabel">
        	   ${user.code} : ${user.name} / ${user.role.name}
        	</div>
        	<div class="itlabel">
        	   ITSUPPORT:087-8016837,083-3061296
        	</div>
        </td>
        <td background="${pageContext.request.contextPath}/images2/header_bg.png">&nbsp;
          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/softwareUpdater/SalesAppUpdater.jsp';">
			  <font size="3" color="red"><%=msg1[0]%></font> 
		  </a>
         </td>
        <td width="523px;" background="${pageContext.request.contextPath}/images2/header02.png" align="right" valign="top">
        
        	<a href="${pageContext.request.contextPath}/login.do?do=logoff" onmouseout="MM_swapImgRestore()" 
        	  onmouseover="MM_swapImage('Image14','','${pageContext.request.contextPath}/images2/button_logout2.png',1)">
        	  <img src="${pageContext.request.contextPath}/images2/button_logout1.png" name="Image14" width="46" height="46" border="0" id="Image14" />
        	</a>
        </td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/header_right.png" border="0"/></td>
	</tr>
</table>
