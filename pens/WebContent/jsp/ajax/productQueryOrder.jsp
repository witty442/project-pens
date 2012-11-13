<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.Product"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%
String pCode = (String)request.getParameter("pCode");
String pBrand = request.getParameter("pBrand");
String pID = "";
String pName = "";
String desc="";
String code="";
String whereCause = "";
Product[] results = null;
User user = ((User)session.getAttribute("user"));
try{
	
	if(pCode != null && pCode.length() > 0){
		whereCause += " AND CODE = '" + pCode + "' AND ISACTIVE ='Y' ";
		whereCause += " AND CODE NOT IN( SELECT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+user.getRole().getKey()+"') ";
		
		 if (pBrand != null && pBrand.trim().length() > 0) {
			pBrand = new String(pBrand.getBytes("ISO8859_1"), "UTF-8");
			whereCause += "\n AND PRODUCT_CATEGORY_ID IN( ";
			whereCause += "\n  select product_category_id from m_product_category  WHERE name LIKE '%" + pBrand + "%'";
			whereCause += "\n )";
		}
		
		results = new MProduct().search(whereCause);
		if(results!=null){
			for(Product p : results){
				pID = String.valueOf(p.getId());
				pName = p.getName();
				desc = ""; //p.getDescription();
				code = p.getCode();
			}	
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=pID%>||<%=pName%>||<%=desc%>||<%=code%>