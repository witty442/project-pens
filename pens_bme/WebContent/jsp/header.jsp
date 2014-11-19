<%@page import="com.isecinc.pens.web.managepath.ManagePath"%>
<%@page import="com.isecinc.pens.bean.User"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr height="137px;">
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/header_left.png" border="0"/>
		</td>
        <td width="523px;" background="${pageContext.request.contextPath}/images2/header01.png">&nbsp;
            <div class="userlabel">
        	   ${user.code} : ${user.name} / ${user.role.name}
        	</div>
        	
        </td>
        <td background="${pageContext.request.contextPath}/images2/header_bg.png">&nbsp; </td>
        <td width="523px;" background="${pageContext.request.contextPath}/images2/header02.png" align="right" valign="top">
        	
        	<a href="${pageContext.request.contextPath}/login.do?do=logoff" onmouseout="MM_swapImgRestore()" 
        	  onmouseover="MM_swapImage('Image14','','${pageContext.request.contextPath}/images2/button_logout2.png',1)">
        	  <img src="${pageContext.request.contextPath}/images2/button_logout1.png" name="Image14" width="46" height="46" border="0" id="Image14" />
        	</a>
        </td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/header_right.png" border="0"/></td>
	</tr>
</table>
<%
if(session.getAttribute("user")!=null){ 
		//Insert Path
		User user = (User)session.getAttribute("user");
		String path = request.getServletPath();
		//System.out.println("path 1:"+request.getContextPath());
		//System.out.println("path 2:"+request.getPathInfo());
		//System.out.println("path 3:"+request.getRequestURI());
		//System.out.println("path 4:"+request.getServletPath());
		
		//ManagePath.savePath(user, path);
} %>