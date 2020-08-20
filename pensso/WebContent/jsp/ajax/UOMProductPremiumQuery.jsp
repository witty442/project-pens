
<%@page import="com.isecinc.pens.bean.Product"%><%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.UOM"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.DecimalFormat"%>
<%
String pID = (String)request.getParameter("pId");
String pricelistID = (String)request.getParameter("plId");

Connection conn = null;
Statement stmt = null;
ResultSet rst = null;
String uom1 = "";
String uom2 = "";
double priceUom1 = 0;
double priceUom2 = 0;
List<UOM> uoms = new ArrayList<UOM>();
DecimalFormat formatter = new DecimalFormat("###0.00000");
Product product = null;
try{
	if(pID != null && pID.length()>0 ){
		
		String sql ="\n SELECT um.UOM_ID, 0 as PRICE " +
					"\n FROM m_uom_conversion um " +
					"\n WHERE um.PRODUCT_ID = " + pID + 
	                "\n AND (um.disable_date is null or date_format(um.disable_date,'%Y%m%d') >= date_format(current_timestamp,'%Y%m%d')) ";
		
		System.out.println("UOMproductPremiumQuery sql:"+sql);
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		stmt = conn.createStatement();
		rst = stmt.executeQuery(sql);
		product = new MProduct().find(pID);
		
		while(rst.next()){
			if(product.getUom().getId().equals(rst.getString("UOM_ID"))){
				uom1 = rst.getString("UOM_ID");
				priceUom1 = rst.getDouble("PRICE");
			}else{
				uom2 = rst.getString("UOM_ID");
				priceUom2 = rst.getDouble("PRICE");
			}
		}
		
	}//if
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{
		rst.close();
	}catch(Exception e2){}
	try{
		stmt.close();
	}catch(Exception e2){}
	try{
		conn.close();
	}catch(Exception e2){}
}

%>

<%=uom1+"|"+uom2+"|"+formatter.format(priceUom1)+"|"+formatter.format(priceUom2)%>