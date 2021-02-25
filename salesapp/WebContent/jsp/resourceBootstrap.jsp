<%@page import="com.pens.util.ControlCode"%>
<%@page import="com.pens.util.SIdUtils"%>
<%if(ControlCode.canExecuteMethod("template", "sb_admin")){ %>
    <!-- Template sb_admin -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css?v=<%=SIdUtils.getInstance().getIdSession()%>">
	<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
	<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap_sb_admin.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap/fonts/bootstrap_font_sb_admin.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<%}else{%>
    <!-- Template sb_admin_2 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css?v=<%=SIdUtils.getInstance().getIdSession()%>">
	<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
	<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap_sb_admin.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap/fonts/bootstrap_font_sb_admin.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<%}%>