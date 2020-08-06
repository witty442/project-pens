<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%
String regionAll = Utils.isNull(request.getParameter("regionAll"));
       regionAll = new String(regionAll.getBytes("ISO8859_1"), "UTF-8");
String provinceAll = Utils.isNull(request.getParameter("provinceAll"));
provinceAll = new String(provinceAll.getBytes("ISO8859_1"), "UTF-8");
String amphurAll = Utils.isNull(request.getParameter("amphurAll"));
       amphurAll = new String(amphurAll.getBytes("ISO8859_1"), "UTF-8");
    
System.out.println("setRegionSession :regionAll="+regionAll);
System.out.println("setProvinceSession :provinceAll="+provinceAll);
System.out.println("setAmphurSession :amphurAll="+amphurAll);

session.setAttribute("REGION_ALL", regionAll);
session.setAttribute("PROVINCE_ALL", provinceAll);
session.setAttribute("AMPHUR_ALL", amphurAll);
%>