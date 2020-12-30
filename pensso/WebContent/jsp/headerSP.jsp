
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr height="137px;" >
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/header_left.png" border="0"/>
		</td>
         <td width="354px;" background="${pageContext.request.contextPath}/images2/header01SP.png">&nbsp;
           <div class="userlabel">
                <img src="${pageContext.request.contextPath}/images2/pens_logo_small.png" border="0"/>
                <br/><br/>
        	   ${user.code} : ${user.name} / ${user.role.name}
        	</div> 
        </td>
        <td background="${pageContext.request.contextPath}/images2/header_bg.png">&nbsp;
          <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/softwareUpdater/SalesAppUpdater.jsp';">
			  <font size="3" color="red"><%//msg1[0]%></font> 
		  </a>
         </td>
        <td width="723px;" background="${pageContext.request.contextPath}/images2/header02SP.png" align="right" valign="top">
        
        	<a href="${pageContext.request.contextPath}/login.do?do=logoff" onmouseout="MM_swapImgRestore()" 
        	  onmouseover="MM_swapImage('Image14','','${pageContext.request.contextPath}/images2/button_logout2.png',1)">
        	  <img src="${pageContext.request.contextPath}/images2/button_logout1.png" name="Image14" width="46" height="46" border="0" id="Image14" />
        	</a>
        </td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/header_right.png" border="0"/></td>
	</tr>
</table>
