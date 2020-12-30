<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String customerCode = Utils.isNull(request.getParameter("customerCode"));
System.out.println("shipToAddressListBoxAjax.customerCode:"+customerCode);
List<Address> addressShipToList = null;
try{
	if( !Utils.isNull(customerCode).equals("")){
		addressShipToList = new MAddress().findAddressShipToList("",customerCode);
	}

%>

<%
 if(addressShipToList != null && addressShipToList.size() >0){
   for(Address u : addressShipToList){ 
     String addressText = u.getId()+"-"+u.getLine1()+"-"+u.getLine2()
    		 +"-"+u.getLine3()+"-"+u.getLine4()+"-"+u.getProvince().getName();
%>
    <option value="<%=u.getId()%>"><%=addressText%></option>
<%
    }//for
  }//if

}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>