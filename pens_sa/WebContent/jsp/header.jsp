<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr height="137px;">
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/header_left.png" border="0"/>
		</td>
         <td width="300px;" background="${pageContext.request.contextPath}/images2/header01.png" valign='bottom'>&nbsp;
            <div class="userlabel">
                <img src="${pageContext.request.contextPath}/images2/pens_logo_small.png" border="0"/>
                <br/><br/>
        	    ${user.name} / ${user.userGroupName}<br/>
        	</div> 
        <td background="${pageContext.request.contextPath}/images2/header_bg.png">&nbsp;
         </td>
        <td width="723px;" background="${pageContext.request.contextPath}/images2/header02.png" align="right" valign="top">
        
        	<a href="${pageContext.request.contextPath}/?logoff=T" onmouseout="MM_swapImgRestore()" 
        	  onmouseover="MM_swapImage('Image14','','${pageContext.request.contextPath}/images2/button_logout2.png',1)">
        	  <img src="${pageContext.request.contextPath}/images2/button_logout1.png" name="Image14" width="46" height="46" border="0" id="Image14" />
        	</a>
        </td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/header_right.png" border="0"/></td>
	</tr>
</table>
