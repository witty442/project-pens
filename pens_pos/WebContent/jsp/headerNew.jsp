<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="util.AppversionVerify"%>
<%
String[] msg1 = new String[2];
/* if(request.getSession().getAttribute("appVersionCheckMsg") != null){
	msg1 =  (String[])request.getSession().getAttribute("appVersionCheckMsg");
}else{
	System.out.println("Header.jsp AppVerify ");
	msg1 = AppversionVerify.getApp().checkAppVersion(request) ;
}  */
%>  
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr height="65px;" >
		<td width="27px;" valign="bottom">
		  <img src="${pageContext.request.contextPath}/images2/header_left.png" border="0" height="57px" width="30px"/>
		</td>
        <td width="341px;" valign="bottom" background="${pageContext.request.contextPath}/images2/header_bg.png">
            <div class="userlabel">
        	   ${user.code} : ${user.name} / ${user.role.name} &nbsp;ITSUPPORT:087-8016837,083-3061296
        	</div>
        </td>
        <td background="${pageContext.request.contextPath}/images2/header_bg.png">&nbsp;
          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/softwareUpdater/SalesAppUpdater.jsp';">
			  <font size="3" color="red"><%=Utils.isNull(msg1[0])%></font> 
		  </a>
         </td>
        <td width="523px;" align="right" valign="top" background="${pageContext.request.contextPath}/images2/header_bg.png">
        
        	<a href="${pageContext.request.contextPath}/login.do?do=logoff" onmouseout="MM_swapImgRestore()" 
        	  onmouseover="MM_swapImage('Image14','','${pageContext.request.contextPath}/images2/button_logout2.png',1)">
        	  <img src="${pageContext.request.contextPath}/images2/button_logout1.png" name="Image14" width="46" height="46" border="0" id="Image14" />
        	</a>
        </td>
        <td width="31px;" valign="bottom">
          <img src="${pageContext.request.contextPath}/images2/header_right.png" border="0"  height="57px" width="30px"/>
        </td>
	</tr>
</table>
