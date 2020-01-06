<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<center>
<span id="msg" class="errormsg">
<logic:present name="Message" scope="request">
	${Message}
</logic:present>
</span>
<br/>
<span id="msg2" class="errormsg">
<logic:present name="Message2" scope="request">
	${Message2}
</logic:present>
</span>
</center>

