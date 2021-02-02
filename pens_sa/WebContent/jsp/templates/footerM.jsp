
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.EnvProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<hr>
<div>
	Application Version
	<a href ="javascript:PopupCenterFull('<%=request.getContextPath()%>/jsp/adminConsole/adminConsole.jsp','')" title="Configuration">
	  <font color="red"><b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
	</a>
	 Type:[<b><%=EnvProperties.getInstance().getProperty("product.type")%></b>]
</div>
  
			 
		