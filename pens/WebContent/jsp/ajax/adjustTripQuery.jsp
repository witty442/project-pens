
<%@page import="com.isecinc.core.bean.Messages"%>
<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="com.isecinc.pens.model.MTrip"%><%@ page language="java"
	contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String message = "";
	try {
		boolean result = new MTrip().adjustTrip();
		if (result) {
			message = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
		} else {
			message = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();
		}
	} catch (Exception e) {
		message = e.toString();
	}
%>
<%=message%>